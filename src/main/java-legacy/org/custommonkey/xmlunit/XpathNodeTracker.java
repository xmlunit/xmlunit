/*
******************************************************************
Copyright (c) 2001-2010, Jeff Martin, Tim Bacon
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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

import net.sf.xmlunit.diff.XPathContext;
import net.sf.xmlunit.util.IterableNodeList;
import net.sf.xmlunit.util.Linqy;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Tracks Nodes visited by the DifferenceEngine and converts that
 * information into an Xpath-String to supply to the NodeDetail of a
 * Difference instance.
 *
 * <p>The tracker has the concept of a level which corresponds to the
 * depth of the tree it has visited.  The {@link #indent indent}
 * method creates a new level, making the tracker walk prepare to walk
 * down the tree, the {@link #outdent outdent} method moves one level
 * up and any information about the previous level is discarded.</p>
 *
 * <p>At each level there may be a current Node - the last one for
 * which {@link #visitedNode visitedNode} or {@link #visited visited}
 * has been called - and maybe a current attribute at this node - the
 * last one for which {@link #visitedAttribute visitedAttribute} or
 * {@link #visited visited} has been called.  Attributes are assumed
 * to be at the same level as the nodes they belong to.</p>

 * @see NodeDetail#getXpathLocation()
 * @see Difference#getControlNodeDetail
 * @see Difference#getTestNodeDetail
 */
public class XpathNodeTracker implements XMLConstants {
    private XPathContext ctx;
    private final LinkedList<TrackingEntry> levels =
        new LinkedList<TrackingEntry>();

    /**
     * Simple constructor
     */ 
    public XpathNodeTracker() {
        ctx = new XPathContext();
        newLevel();
    }

    /**
     * Clear state data.
     * Call if required to reuse an existing instance.
     */
    public void reset() {
        ctx = new XPathContext();
        levels.clear();
        indent();
    }

    /**
     * Call before examining child nodes one level of indentation into DOM
     *
     * <p>Any subsequent call to {@link #visited visited} is assumed
     * to belong to nodes one level deeper into the tree than the
     * nodes visited before calling {@link #indent indent}.</p>
     *
     * <p>As a side effect, the current attribute - if any - is
     * reset.</p>
     */
    public void indent() {
        newLevel();
    }

    private void newLevel() {
        clearTrackedAttribute();
        levels.add(new TrackingEntry());
    }

    /**
     * Call after processing attributes of an element and turning to
     * compare the child nodes.
     */
    public void clearTrackedAttribute() {
        if (levels.size() > 0) {
            levels.getLast().clearTrackedAttribute();
        }
    }

    /**
     * Call after examining child nodes, ie before returning back one
     * level of indentation from DOM.
     *
     * <p>Any subsequent call to {@link #visited visited} is assumed
     * to belong to nodes one level closer to the root of the tree
     * than the nodes visited before calling {@link #outdent
     * outdent}.</p>
     */ 
    public void outdent() {
        if (levels.size() < 2) {
            reset();
        } else {
            levels.getLast().reset();
            levels.removeLast();
        }
    }

    /**
     * Call when visiting a node whose xpath location needs tracking
     *
     * <p>Delegates to {@link #visitedAttribute visitedAttribute} for
     * attribute nodes, {@link #visitedNode visitedNode} for elements,
     * texts, CDATA sections, comments or processing instructions and
     * ignores any other type of node.</p>
     *
     * @param node the Node being visited - must not be null.
     */
    public void visited(Node node) {
        switch(node.getNodeType()) {
        case Node.ATTRIBUTE_NODE:
            visitedAttribute(getNodeName(node));
            break;
        case Node.ELEMENT_NODE:
            visitedNode(node, getNodeName(node));
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

    /**
     * Invoked by {@link #visited visited} when visited is an element,
     * text, CDATA section, comment or processing instruction.
     *
     * @param visited the visited node - Unit tests call this with
     * null values, so it is not safe to assume it will never be null.
     * It will never be null when the {@link #visited visited} method
     * delegates here.
     * @param value the local name of the element or an XPath
     * identifier matching the type of node.
     */
    protected void visitedNode(Node visited, String value) {
        levels.getLast().trackNode(visited, value);
    }

    /**
     * Invoked by {@link #visited visited} when visiting an attribute node.
     *
     * @param visited the local name of the attribute.
     */
    protected void visitedAttribute(String visited) {
        levels.getLast().trackAttribute(new QName(visited));
    }

    /**
     * Preload the items in a NodeList by visiting each in turn
     * Required for pieces of test XML whose node children can be visited
     * out of sequence by a DifferenceEngine comparison
     *
     * <p>Makes the nodes of this list known as nodes that are
     * visitable at the current level and makes the last child node
     * the current node as a side effect.</p>
     *
     * @param nodeList the items to preload
     */
    public void preloadNodeList(NodeList nodeList) {
        preloadChildren(new IterableNodeList(nodeList));
    }

    /**
     * Preload the items in a List by visiting each in turn
     * Required for pieces of test XML whose node children can be visited
     * out of sequence by a DifferenceEngine comparison
     *
     * <p>Makes the nodes of this list known as nodes that are
     * visitable at the current level and makes the last child node
     * the current node as a side effect.</p>
     *
     * @param nodeList the items to preload
     */
    public void preloadChildList(List nodeList) {
        Iterable<Node> nodes = Linqy.cast(nodeList);
        preloadChildren(nodes);
    }

    /**
     * @return the last visited node as an xpath-location String
     */ 
    public String toXpathString() {
        return ctx.getXPath();
    }

    /**
     * extracts the local name of a node.
     */
    private static String getNodeName(Node n) {
        String nodeName = n.getLocalName();
        if (nodeName == null || nodeName.length() == 0) {
            nodeName = n.getNodeName();
        }
        return nodeName;
    }

    /**
     * Preload the nodes by visiting each in turn.
     * Required for pieces of test XML whose node children can be visited
     * out of sequence by a DifferenceEngine comparison
     *
     * <p>Makes the nodes of this list known as nodes that are
     * visitable at the current level and makes the last child node
     * the current node as a side effect.</p>
     *
     * @param nodeList the items to preload
     */
    private void preloadChildren(Iterable<Node> nodeList) {
        levels.getLast().trackNodesAsWellAsValues(true);
        for (Node n : nodeList) {
            visited(n);
        }
        levels.getLast().trackNodesAsWellAsValues(false);
    }

    /**
     * Holds node tracking details - one instance is used for each
     * level of indentation in a DOM
     *
     * Provides reference between a String-ified Node value and the
     * xpath index of that value
     */
    private final class TrackingEntry {
        // Is the XPathContext looking at an attribute?
        private boolean atAttribute = false;

        // Has the XPathContext walked down a child node?
        private boolean atChild = false;

        // the next index usable for a child node
        private int nextIndex = 0;

        // may be used if children of this level have been preloaded,
        // maps the Node instance to index of the Node in the parent's
        // child list
        private Map<Node, Integer> nodeReferenceMap = null;

        // node references are tracked while preloading child nodes
        private boolean trackNodeReferences = false;

        /**
         * Keep a reference to the current visited (non-attribute) node
         * @param visited the non-attribute node visited
         * @param value the String-ified value of the non-attribute node visited
         */
        private void trackNode(final Node visited, final String value) {
            if (trackNodeReferences && visited != null) {
                nodeReferenceMap.put(visited, nextIndex++);
            }

            int currentIndex = 0;
            if (nodeReferenceMap == null || visited == null ||
                !nodeReferenceMap.containsKey(visited)) {
                currentIndex = nextIndex++;
            } else {
                currentIndex = nodeReferenceMap.get(visited).intValue();
            }
            reset();

            if (trackNodeReferences || nodeReferenceMap == null) {
                XPathContext.NodeInfo i = null;
                if (visited != null) {
                    i = new XPathContext.DOMNodeInfo(visited);
                } else {
                    // visited is only ever null when invoked from the
                    // unit tests and in this case it is always
                    // expected to be an element node
                    i = new XPathContext.NodeInfo() {
                            public short getType() { return Node.ELEMENT_NODE; }
                            public QName getName() { return new QName(value); }
                        };
                }
                ctx.appendChildren(Linqy.singleton(i));
            }

            if (!trackNodeReferences) {
                ctx.navigateToChild(currentIndex);
                atChild = true;
            }
        }

        /**
         * Keep a reference to the visited attribute at the current visited node
         * @param value the attribute visited
         */
        private void trackAttribute(QName visited) {
            if (atAttribute) {
                ctx.navigateToParent();
            }
            ctx.addAttributes(Linqy.singleton(visited));
            ctx.navigateToAttribute(visited);
            atAttribute = true;
        }

        /**
         * Clear any reference to the current visited attribute
         */
        void clearTrackedAttribute() {
            if (atAttribute) {
                ctx.navigateToParent();
                atAttribute = false;
            }
        }

        private void reset() {
            clearTrackedAttribute();
            if (atChild) {
                ctx.navigateToParent();
                atChild = false;
            }
        }

        /**
         * whether the indices of subsequently tracked nodes should be tracked.
         */
        void trackNodesAsWellAsValues(boolean yesNo) {
            this.trackNodeReferences = yesNo;
            if (yesNo) {
                nodeReferenceMap = new HashMap<Node, Integer>();
            }
        }
    }
}
