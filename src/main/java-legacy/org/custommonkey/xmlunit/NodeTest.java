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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Encapsulation of the Node-by-Node testing of a DOM Document
 * Uses a nodetype-specific <code>NodeFilter</code> to pass the DOM Nodes
 * to a NodeTester instance that performs the acual Node validation.
 * <br />Examples and more at <a href="http://xmlunit.sourceforge.net"/>xmlunit.
 * sourceforge.net</a>
 * @see NodeTester
 */
public class NodeTest {
    private final DocumentTraversal documentTraversal;
    private final Node rootNode;

    /**
     * Construct a NodeTest for the DOM built using the String and JAXP
     */
    public NodeTest(String xmlString)
        throws SAXException, IOException {
        this(new StringReader(xmlString));
    }

    /**
     * Construct a NodeTest for the DOM built using the Reader and JAXP
     */
    public NodeTest(Reader reader) throws SAXException,
                                          IOException {
        this(XMLUnit.buildDocument(XMLUnit.newControlParser(), reader));
    }

    /**
     * Construct a NodeTest for the DOM built using the InputSource.
     */
    public NodeTest(InputSource src) throws SAXException,
                                            IOException {
        this(XMLUnit.buildDocument(XMLUnit.newControlParser(), src));
    }

    /**
     * Construct a NodeTest for the specified Document
     * @exception IllegalArgumentException if the Document does not support the DOM
     * DocumentTraversal interface (most DOM implementations should provide this
     * support)
     */
    public NodeTest(Document document) {
        this(getDocumentTraversal(document),
             document.getDocumentElement());
    }

    /**
     * Try to cast a Document into a DocumentTraversal
     * @param document
     * @return DocumentTraversal interface if the DOM implementation supports it
     */
    private static DocumentTraversal getDocumentTraversal(Document document) {
        try {
            return (DocumentTraversal) document;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("DOM Traversal not supported by "
                                               + document.getImplementation().getClass().getName()
                                               + ". To use this class you will need to switch to a DOM implementation that supports Traversal.");
        }
    }

    /**
     * Construct a NodeTest using the specified DocumentTraversal, starting at
     * the specified root node
     */
    public NodeTest(DocumentTraversal documentTraversal, Node rootNode) {
        this.documentTraversal = documentTraversal;
        this.rootNode = rootNode;
    }

    /**
     * Does this NodeTest pass using the specified NodeTester instance?
     * @param tester
     * @param singleNodeType note <code>Node.ATTRIBUTE_NODE</code> is not
     *  exposed by the DocumentTraversal node iterator unless the root node
     *  is itself an attribute - so a NodeTester that needs to test attributes
     *  should obtain those attributes from <code>Node.ELEMENT_NODE</code>
     *  nodes
     * @exception NodeTestException if test fails
     */
    public void performTest(NodeTester tester, short singleNodeType)
        throws NodeTestException {
        performTest(tester, new short[] {singleNodeType});
    }

    /**
     * Does this NodeTest pass using the specified NodeTester instance?
     * @param tester
     * @param nodeTypes note <code>Node.ATTRIBUTE_NODE</code> is not
     *  exposed by the DocumentTraversal node iterator unless the root node
     *  is itself an attribute - so a NodeTester that needs to test attributes
     *  should obtain those attributes from <code>Node.ELEMENT_NODE</code>
     *  nodes instead
     * @exception NodeTestException if test fails
     */
    public void performTest(NodeTester tester, short[] nodeTypes)
        throws NodeTestException {
        NodeIterator iter = documentTraversal.createNodeIterator(rootNode,
                                                                 NodeFilter.SHOW_ALL, new NodeTypeNodeFilter(nodeTypes), true);

        for (Node nextNode = iter.nextNode(); nextNode != null;
             nextNode = iter.nextNode()) {
            tester.testNode(nextNode, this);
        }
        tester.noMoreNodes(this);
    }

    /**
     * Node type specific Node Filter: accepts Nodes of those types specified
     * in constructor, rejects all others
     */
    private static class NodeTypeNodeFilter implements NodeFilter {
        private final short[] nodeTypes;

        /**
         * Construct filter for specific node types
         * @param nodeTypes note <code>Node.ATTRIBUTE_NODE</code> is not
         *  exposed by the DocumentTraversal node iterator unless the root node
         *  is itself an attribute - so a NodeTester that needs to test attributes
         *  should obtain those attributes from <code>Node.ELEMENT_NODE</code>
         *  nodes
         */
        public NodeTypeNodeFilter(short[] nodeTypes) {
            this.nodeTypes = nodeTypes;
        }

        /**
         * NodeFilter method.
         * @param aNode
         * @return
         */
        public short acceptNode(Node aNode) {
            if (acceptNodeType(aNode.getNodeType())) {
                return NodeFilter.FILTER_ACCEPT;
            }
            return NodeFilter.FILTER_REJECT;
        }

        /**
         * Does this instance accept nodes with the node type value
         * @param shortVal
         * @return
         */
        private boolean acceptNodeType(short shortVal) {
            for (int i=0; i < nodeTypes.length; ++i) {
                if (nodeTypes[i] == shortVal) {
                    return true;
                }
            }
            return false;
        }
    }
}
