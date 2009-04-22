/*
******************************************************************
Copyright (c) 2001-2007, Jeff Martin, Tim Bacon
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the following
      disclaimer in the documentation and/or other materials provided
      with the distribution.
    * Neither the name of the xmlunit.sourceforge.net nor the names
      of its contributors may be used to endorse or promote products
      derived from this software without specific prior written
      permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

******************************************************************
*/
package org.custommonkey.xmlunit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Tracks Nodes visited by the DifferenceEngine and 
 * converts that information into an Xpath-String to supply
 * to the NodeDetail of a Difference instance
 * @see NodeDetail#getXpathLocation()
 * @see Difference#getControlNodeDetail
 * @see Difference#getTestNodeDetail
 */
public class XpathNodeTracker implements XMLConstants {
    private final List indentationList = new ArrayList();
    private TrackingEntry currentEntry;

    /**
     * Simple constructor
     */ 
    public XpathNodeTracker() {
        newLevel();
    }
        
    /**
     * Clear state data.
     * Call if required to reuse an existing instance.
     */
    public void reset() {
        indentationList.clear();
        indent();
    }
        
    /**
     * Call before examining child nodes one level of indentation into DOM
     */
    public void indent() {
        if (currentEntry != null) {
            currentEntry.clearTrackedAttribute();
        }
        newLevel();
    }

    private void newLevel() {
        currentEntry = new TrackingEntry();
        indentationList.add(currentEntry);
    }

    /**
     * Call after processing attributes of an element and turining to
     * compare the child nodes.
     */
    public void clearTrackedAttribute() {
        if (currentEntry != null) {
            currentEntry.clearTrackedAttribute();
        }
    }

    /**
     * Call after examining child nodes, ie before returning back one level of indentation from DOM
     */ 
    public void outdent() {
        int last = indentationList.size() - 1;
        indentationList.remove(last);
        --last;
        if (last >= 0) {
            currentEntry = (TrackingEntry) indentationList.get(last);
        }
    }
        
    /**
     * Call when visiting a node whose xpath location needs tracking
     * @param node the Node being visited
     */
    public void visited(Node node) {
        String nodeName; 
        switch(node.getNodeType()) {
        case Node.ATTRIBUTE_NODE:
            nodeName = ((Attr)node).getLocalName();
            if (nodeName == null || nodeName.length() == 0) {
                nodeName = node.getNodeName();
            }
            visitedAttribute(nodeName);
            break;
        case Node.ELEMENT_NODE:
            nodeName = ((Element)node).getLocalName();
            if (nodeName == null || nodeName.length() == 0) {
                nodeName = node.getNodeName();
            }
            visitedNode(node, nodeName);
            break;
        case Node.COMMENT_NODE:
            visitedNode(node, XPATH_COMMENT_IDENTIFIER);
            break;
        case Node.PROCESSING_INSTRUCTION_NODE:
            visitedNode(node, XPATH_PROCESSING_INSTRUCTION_IDENTIFIER);
            break;
        case Node.CDATA_SECTION_NODE:
        case Node.TEXT_NODE:
            visitedNode(node, XPATH_CHARACTER_NODE_IDENTIFIER);
            break;
        default:
            // ignore unhandled node types
            break;
        }
    }
        
    protected void visitedNode(Node visited, String value) {
        currentEntry.trackNode(visited, value);
    }

    protected void visitedAttribute(String visited) {
        currentEntry.trackAttribute(visited);
    }

    /**
     * Preload the items in a NodeList by visiting each in turn
     * Required for pieces of test XML whose node children can be visited
     * out of sequence by a DifferenceEngine comparison
     * @param nodeList the items to preload
     */
    public void preloadNodeList(NodeList nodeList) {
        currentEntry.trackNodesAsWellAsValues(true);
        int length = nodeList.getLength();
        for (int i=0; i < length; ++i) {
            visited(nodeList.item(i));
        }
        currentEntry.trackNodesAsWellAsValues(false);
    }
                
    /**
     * Preload the items in a List by visiting each in turn
     * Required for pieces of test XML whose node children can be visited
     * out of sequence by a DifferenceEngine comparison
     * @param nodeList the items to preload
     */
    public void preloadChildList(List nodeList) {
        currentEntry.trackNodesAsWellAsValues(true);
        int length = nodeList.size();
        for (int i=0; i < length; ++i) {
            visited((Node) nodeList.get(i));
        }
        currentEntry.trackNodesAsWellAsValues(false);
    }
                
    /**
     * @return the last visited node as an xpath-location String
     */ 
    public String toXpathString() {
        StringBuffer buf = new StringBuffer();
        TrackingEntry nextEntry;
        for (Iterator iter = indentationList.iterator(); iter.hasNext(); ) {
            nextEntry = (TrackingEntry) iter.next();
            nextEntry.appendEntryTo(buf);
        }
        return buf.toString();
    }
        
    /**
     * Wrapper class around a mutable <code>int</code> value
     * Avoids creation of many immutable <code>Integer</code> objects
     */
    private static final class Int {
        private int value;
                
        public Int(int startAt) {
            value = startAt;
        }
                
        public void increment() {
            ++value;
        }
                
        public int getValue() {
            return value;
        }
                
        public Integer toInteger() {
            return new Integer(value);
        }
    }
        
    /**
     * Holds node tracking details - one instance is used for each level of indentation in a DOM
     * Provides reference between a String-ified Node value and the xpath index of that value
     */
    private static final class TrackingEntry {
        private final Map valueMap = new HashMap();
        private String currentValue, currentAttribute;

        private Map nodeReferenceMap;
        private boolean trackNodeReferences = false;
        private Integer nodeReferenceLookup = null;
                
        private Int lookup(String value) {
            return (Int) valueMap.get(value);
        }

        /**
         * Keep a reference to the current visited (non-attribute) node
         * @param visited the non-attribute node visited
         * @param value the String-ified value of the non-attribute node visited
         */
        public void trackNode(Node visited, String value) {
            if (nodeReferenceMap == null || trackNodeReferences) {
                Int occurrence = lookup(value);
                if (occurrence == null) {
                    occurrence = new Int(1);
                    valueMap.put(value, occurrence);
                } else {
                    occurrence.increment();
                }
                if (trackNodeReferences) {
                    nodeReferenceMap.put(visited, occurrence.toInteger());
                }
            } else {
                nodeReferenceLookup = (Integer) nodeReferenceMap.get(visited);
            }
            currentValue = value;
            clearTrackedAttribute();
        }
                
        /**
         * Keep a reference to the visited attribute at the current visited node
         * @param value the attribute visited
         */
        public void trackAttribute(String visited) {
            currentAttribute = visited;
        }
                                
        /**
         * Clear any reference to the current visited attribute         
         */
        public void clearTrackedAttribute() {
            currentAttribute = null;
        }
                
        /**
         * Append the details of the current visited node to a StringBuffer
         * @param buf the StringBuffer to append to
         */
        public void appendEntryTo(StringBuffer buf) {
            if (currentValue == null) {
                return;
            }
            buf.append(XPATH_SEPARATOR).append(currentValue);
                        
            int value = nodeReferenceLookup == null
                ? lookup(currentValue).getValue() : nodeReferenceLookup.intValue();
            buf.append(XPATH_NODE_INDEX_START).append(value).append(XPATH_NODE_INDEX_END);
                        
            if (currentAttribute != null) {
                buf.append(XPATH_SEPARATOR).append(XPATH_ATTRIBUTE_IDENTIFIER)
                    .append(currentAttribute);
            }
        }
                
        public void trackNodesAsWellAsValues(boolean yesNo) {
            this.trackNodeReferences = yesNo;
            if (yesNo) {
                nodeReferenceMap = new HashMap();
            }
        }
    }
}
