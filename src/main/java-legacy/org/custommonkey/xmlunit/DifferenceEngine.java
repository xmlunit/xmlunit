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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/**
 * Class that has responsibility for comparing Nodes and notifying a
 * DifferenceListener of any differences or dissimilarities that are found.
 * Knows how to compare namespaces and nested child nodes, but currently
 * only compares nodes of type ELEMENT_NODE, CDATA_SECTION_NODE,
 * COMMENT_NODE, DOCUMENT_TYPE_NODE, PROCESSING_INSTRUCTION_NODE and TEXT_NODE.
 * Nodes of other types (eg ENTITY_NODE) will be skipped.
 * <br />Examples and more at <a href="http://xmlunit.sourceforge.net"/>xmlunit.
 * sourceforge.net</a>
 * @see DifferenceListener#differenceFound(Difference)
 */
public class DifferenceEngine
    implements DifferenceConstants, DifferenceEngineContract {

    private static final String NULL_NODE = "null";
    private static final String NOT_NULL_NODE = "not null";
    private static final String ATTRIBUTE_ABSENT = "[attribute absent]";
    private final ComparisonController controller;
    private MatchTracker matchTracker;
    private final XpathNodeTracker controlTracker;
    private final XpathNodeTracker testTracker;
    
    /**
     * Simple constructor that uses no MatchTracker at all.
     * @param controller the instance used to determine whether a Difference
     * detected by this class should halt further comparison or not
     * @see ComparisonController#haltComparison(Difference)
     */
    public DifferenceEngine(ComparisonController controller) {
        this(controller, null);
    }
        
    /**
     * Simple constructor
     * @param controller the instance used to determine whether a Difference
     * detected by this class should halt further comparison or not
     * @param matchTracker the instance that is notified on each
     * successful match.  May be null.
     * @see ComparisonController#haltComparison(Difference)
     * @see MatchTracker#matchFound(Difference)
     */
    public DifferenceEngine(ComparisonController controller,
                            MatchTracker matchTracker) {
        this.controller = controller;
        this.matchTracker = matchTracker;
        this.controlTracker = new XpathNodeTracker();
        this.testTracker = new XpathNodeTracker();
    }

    /**
     * @param matchTracker the instance that is notified on each
     * successful match.  May be null.
     */
    public void setMatchTracker(MatchTracker matchTracker) {
        this.matchTracker = matchTracker;
    }
        
    /**
     * Entry point for Node comparison testing.
     * @param control Control XML to compare
     * @param test Test XML to compare
     * @param listener Notified of any {@link Difference differences} detected
     * during node comparison testing
     * @param elementQualifier Used to determine which elements qualify for
     * comparison e.g. when a node has repeated child elements that may occur
     * in any sequence and that sequence is not considered important. 
     */
    public void compare(Node control, Node test, DifferenceListener listener, 
                        ElementQualifier elementQualifier) {
        controlTracker.reset();
        testTracker.reset();
        try {
            compare(getNullOrNotNull(control), getNullOrNotNull(test),
                    control, test, listener, NODE_TYPE);
            if (control!=null) {
                compareNode(control, test, listener, elementQualifier);
            }
        } catch (DifferenceFoundException e) {
            // thrown by the protected compare() method to terminate the
            // comparison and unwind the call stack back to here
        }       
    }
        
    private String getNullOrNotNull(Node aNode) {
        return aNode==null ? NULL_NODE : NOT_NULL_NODE;
    }

    /**
     * First point of call: if nodes are comparable it compares node values then
     *  recurses to compare node children.
     * @param control
     * @param test
     * @param listener
     * @param elementQualifier
     * @throws DifferenceFoundException
     */
    protected void compareNode(Node control, Node test,
                               DifferenceListener listener, ElementQualifier elementQualifier) 
        throws DifferenceFoundException {
        boolean comparable = compareNodeBasics(control, test, listener);
        boolean isDocumentNode = false;

        if (comparable) {
            switch (control.getNodeType()) {
            case Node.ELEMENT_NODE:
                compareElement((Element)control, (Element)test, listener);
                break;
            case Node.CDATA_SECTION_NODE:
            case Node.TEXT_NODE:
                compareText((CharacterData) control,
                            (CharacterData) test, listener);
                break;
            case Node.COMMENT_NODE:
                compareComment((Comment)control, (Comment)test, listener);
                break;
            case Node.DOCUMENT_TYPE_NODE:
                compareDocumentType((DocumentType)control,
                                    (DocumentType)test, listener);
                break;
            case Node.PROCESSING_INSTRUCTION_NODE:
                compareProcessingInstruction((ProcessingInstruction)control,
                                             (ProcessingInstruction)test, listener);
                break;
            case Node.DOCUMENT_NODE:
                isDocumentNode = true;
                compareDocument((Document)control, (Document) test, 
                                listener, elementQualifier);
                break;
            default:
                listener.skippedComparison(control, test);
            }
        } 

        compareHasChildNodes(control, test, listener);
        if (isDocumentNode) {
            Element controlElement = ((Document)control).getDocumentElement();
            Element testElement = ((Document)test).getDocumentElement();
            if (controlElement!=null && testElement!=null) {
                compareNode(controlElement, testElement, listener, elementQualifier);
            }
        } else {
            controlTracker.indent();
            testTracker.indent();
            compareNodeChildren(control, test, listener, elementQualifier);
            controlTracker.outdent();
            testTracker.outdent();
        }
    }
    
    /**
     * Compare two Documents for doctype and then element differences
     * @param control
     * @param test
     * @param listener
     * @param elementQualifier
     * @throws DifferenceFoundException
     */
    protected void compareDocument(Document control, Document test, 
                                   DifferenceListener listener, ElementQualifier elementQualifier) 
        throws DifferenceFoundException {
        DocumentType controlDoctype = control.getDoctype();
        DocumentType testDoctype = test.getDoctype();
        compare(getNullOrNotNull(controlDoctype), 
                getNullOrNotNull(testDoctype), 
                controlDoctype, testDoctype, listener, 
                HAS_DOCTYPE_DECLARATION);
        if (controlDoctype!=null && testDoctype!=null) {
            compareNode(controlDoctype, testDoctype, listener, elementQualifier);
        }
    }

    /**
     * Compares node type and node namespace characteristics: basically
     * determines if nodes are comparable further
     * @param control
     * @param test
     * @param listener
     * @return true if the nodes are comparable further, false otherwise
     * @throws DifferenceFoundException
     */
    protected boolean compareNodeBasics(Node control, Node test,
                                        DifferenceListener listener) throws DifferenceFoundException {
        controlTracker.visited(control);
        testTracker.visited(test);

        Short controlType = new Short(control.getNodeType());
        Short testType = new Short(test.getNodeType());

        boolean textAndCDATA = comparingTextAndCDATA(control.getNodeType(),
                                                     test.getNodeType());
        if (!textAndCDATA) {
            compare(controlType, testType, control, test, listener,
                    NODE_TYPE);
        }
        compare(control.getNamespaceURI(), test.getNamespaceURI(),
                control, test, listener, NAMESPACE_URI);
        compare(control.getPrefix(), test.getPrefix(),
                control, test, listener, NAMESPACE_PREFIX);
            
        return textAndCDATA || controlType.equals(testType);
    }

    private boolean comparingTextAndCDATA(short controlType, short testType) {
        return XMLUnit.getIgnoreDiffBetweenTextAndCDATA() &&
            (controlType == Node.TEXT_NODE
             && testType == Node.CDATA_SECTION_NODE
             ||
             testType == Node.TEXT_NODE
             && controlType == Node.CDATA_SECTION_NODE);
    }

    /**
     * Compare the number of children, and if the same, compare the actual
     *  children via their NodeLists.
     * @param control
     * @param test
     * @param listener
     * @throws DifferenceFoundException
     */
    protected void compareHasChildNodes(Node control, Node test,
                                        DifferenceListener listener) throws DifferenceFoundException {
        Boolean controlHasChildren = hasChildNodes(control);
        Boolean testHasChildren = hasChildNodes(test);
        compare(controlHasChildren, testHasChildren, control, test,
                listener, HAS_CHILD_NODES);
    }

    /**
     * Tests whether a Node has children, taking ignoreComments
     * setting into account.
     */
    private Boolean hasChildNodes(Node n) {
        boolean flag = n.hasChildNodes();
        if (flag && XMLUnit.getIgnoreComments()) {
            List nl = nodeList2List(n.getChildNodes());
            flag = !nl.isEmpty();
        }
        return flag ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * Returns the NodeList's Nodes as List, taking ignoreComments
     * into account.
     */
    static List nodeList2List(NodeList nl) {
        int len = nl.getLength();
        ArrayList l = new ArrayList(len);
        for (int i = 0; i < len; i++) {
            Node n = nl.item(i);
            if (!XMLUnit.getIgnoreComments() || !(n instanceof Comment)) {
                l.add(n);
            }
        }
        return l;
    }

    /**
     * Compare the number of children, and if the same, compare the actual
     *  children via their NodeLists.
     * @param control
     * @param test
     * @param listener
     * @param elementQualifier
     * @throws DifferenceFoundException
     */
    protected void compareNodeChildren(Node control, Node test,
                                       DifferenceListener listener, ElementQualifier elementQualifier) 
        throws DifferenceFoundException {
        if (control.hasChildNodes() && test.hasChildNodes()) {
            List controlChildren = nodeList2List(control.getChildNodes());
            List testChildren = nodeList2List(test.getChildNodes());

            Integer controlLength = new Integer(controlChildren.size());
            Integer testLength = new Integer(testChildren.size());
            compare(controlLength, testLength, control, test, listener,
                    CHILD_NODELIST_LENGTH);
            compareNodeList(controlChildren, testChildren,
                            controlLength.intValue(), listener, elementQualifier);
        }
    }

    /**
     * Compare the contents of two node list one by one, assuming that order
     * of children is NOT important: matching begins at same position in test
     * list as control list.
     * @param control
     * @param test
     * @param numNodes convenience parameter because the calling method should
     *  know the value already
     * @param listener
     * @param elementQualifier used to determine which of the child elements in
     * the test NodeList should be compared to the current child element in the
     * control NodeList.
     * @throws DifferenceFoundException
     * @deprecated Use the version with List arguments instead
     */
    protected void compareNodeList(final NodeList control, final NodeList test,
                                   final int numNodes,
                                   final DifferenceListener listener,
                                   final ElementQualifier elementQualifier) 
        throws DifferenceFoundException {
        compareNodeList(nodeList2List(control), nodeList2List(test),
                        numNodes, listener, elementQualifier);
    }

    /**
     * Compare the contents of two node list one by one, assuming that order
     * of children is NOT important: matching begins at same position in test
     * list as control list.
     * @param control
     * @param test
     * @param numNodes convenience parameter because the calling method should
     *  know the value already
     * @param listener
     * @param elementQualifier used to determine which of the child elements in
     * the test NodeList should be compared to the current child element in the
     * control NodeList.
     * @throws DifferenceFoundException
     */
    protected void compareNodeList(final List controlChildren,
                                   final List testChildren,
                                   final int numNodes,
                                   final DifferenceListener listener,
                                   final ElementQualifier elementQualifier) 
        throws DifferenceFoundException {

        int j = 0;
        final int lastTestNode = testChildren.size() - 1;
        testTracker.preloadChildList(testChildren);

        HashMap/*<Node, Node>*/ matchingNodes = new HashMap();
        HashMap/*<Node, Integer>*/ matchingNodeIndexes = new HashMap();

        List/*<Node>*/ unmatchedTestNodes = new ArrayList(testChildren);

        // first pass to find the matching nodes in control and test docs
        for (int i=0; i < numNodes; ++i) {
            Node nextControl = (Node) controlChildren.get(i);
            boolean matchOnElement = nextControl instanceof Element;
            short findNodeType = nextControl.getNodeType();
            int startAt = ( i > lastTestNode ? lastTestNode : i);
            j = startAt;
            
            boolean matchFound = false;

            /*
             * XMLUnit 1.2 and earlier don't check whether the
             * "matched" test node has already been matched to a
             * different control node and will happily match the same
             * test node to each and every control node, if necessary.
             *
             * I (Stefan) feel this is wrong but can't change it
             * without breaking backwards compatibility
             * (testXpathLocation12 in test_DifferenceEngine which
             * predates XMLUnit 1.0 fails, so at one point it has been
             * the expected and intended behaviour).
             *
             * As a side effect it may leave test nodes inside the
             * unmatched list, see
             * https://sourceforge.net/tracker/?func=detail&aid=2807167&group_id=23187&atid=377768
             *
             * To overcome the later problem the code will now prefer
             * test nodes that haven't already been matched to any
             * other node and falls back to the first
             * (multiply-)matched node if none could be found.  Yes,
             * this is strange.
             */
            int fallbackMatch = -1;

            while (!matchFound) {
                Node t = (Node) testChildren.get(j);
                if (findNodeType == t.getNodeType()
                    || comparingTextAndCDATA(findNodeType, t.getNodeType())) {
                    matchFound = !matchOnElement
                        || elementQualifier == null
                        || elementQualifier
                        .qualifyForComparison((Element) nextControl,
                                              (Element) t);
                }
                if (matchFound && !unmatchedTestNodes.contains(t)) {
                    /*
                     * test node already matched to a different
                     * control node, try the other test nodes first
                     * but keep this as "fallback" (unless there
                     * already is a fallback)
                     */
                    if (fallbackMatch < 0) {
                        fallbackMatch = j;
                    }
                    matchFound = false;
                }
                if (!matchFound) {
                    ++j;
                    if (j > lastTestNode) {
                        j = 0;
                    }
                    if (j == startAt) {
                        // been through all children
                        break;
                    }
                }
            }
            if (!matchFound && XMLUnit.getCompareUnmatched()
                && fallbackMatch >= 0) {
                matchFound = true;
                j = fallbackMatch;
            }
            if (matchFound) {
                matchingNodes.put(nextControl, testChildren.get(j));
                matchingNodeIndexes.put(nextControl, new Integer(j));
                unmatchedTestNodes.remove(testChildren.get(j));
            }
        }

        // next, do the actual comparision on those that matched - or
        // match them against the first test nodes that didn't match
        // any other control nodes
        for (int i=0; i < numNodes; ++i) {
            Node nextControl = (Node) controlChildren.get(i);
            Node nextTest = (Node) matchingNodes.get(nextControl);
            Integer testIndex = (Integer) matchingNodeIndexes.get(nextControl);
            if (nextTest == null && XMLUnit.getCompareUnmatched()
                && !unmatchedTestNodes.isEmpty()) {
                nextTest = (Node) unmatchedTestNodes.get(0);
                testIndex = new Integer(testChildren.indexOf(nextTest));
                unmatchedTestNodes.remove(0);
            }
            if (nextTest != null) {
                compareNode(nextControl, nextTest, listener, elementQualifier);
                compare(new Integer(i), testIndex,
                        nextControl, nextTest, listener,
                        CHILD_NODELIST_SEQUENCE);
            } else {
                missingNode(nextControl, null, listener);
            }
        }

        // now handle remaining unmatched test nodes
        for (Iterator iter = unmatchedTestNodes.iterator(); iter.hasNext();) {
            missingNode(null, (Node) iter.next(), listener);
        }
    }

    private void missingNode(Node control, Node test,
                             DifferenceListener listener)
        throws DifferenceFoundException {
        if (control != null) {
            controlTracker.visited(control);
            compare(control.getNodeName(), null, control, null,
                    listener, CHILD_NODE_NOT_FOUND, controlTracker, null);
        } else {
            testTracker.visited(test);
            compare(null, test.getNodeName(), null, test, listener,
                    CHILD_NODE_NOT_FOUND, null, testTracker);
        }
    }

    /**
     * @param aNode
     * @return true if the node has a namespace
     */
    private boolean isNamespaced(Node aNode) {
        String namespace = aNode.getNamespaceURI();
        return namespace != null && namespace.length() > 0;
    }

    /**
     * Compare 2 elements and their attributes
     * @param control
     * @param test
     * @param listener
     * @throws DifferenceFoundException
     */
    protected void compareElement(Element control, Element test,
                                  DifferenceListener listener) throws DifferenceFoundException {
        compare(getUnNamespacedNodeName(control), getUnNamespacedNodeName(test), 
                control, test, listener, ELEMENT_TAG_NAME);

        NamedNodeMap controlAttr = control.getAttributes();
        Integer controlNonXmlnsAttrLength =
            getNonSpecialAttrLength(controlAttr);
        NamedNodeMap testAttr = test.getAttributes();
        Integer testNonXmlnsAttrLength = getNonSpecialAttrLength(testAttr);
        compare(controlNonXmlnsAttrLength, testNonXmlnsAttrLength,
                control, test, listener, ELEMENT_NUM_ATTRIBUTES);

        compareElementAttributes(control, test, controlAttr, testAttr,
                                 listener);
    }

    /**
     * The number of attributes not related to namespace declarations
     * and/or Schema location.
     */
    private Integer getNonSpecialAttrLength(NamedNodeMap attributes) {
        int length = 0, maxLength = attributes.getLength();
        for (int i = 0; i < maxLength; ++i) {
            Attr a = (Attr) attributes.item(i);
            if (!isXMLNSAttribute(a)
                && !isRecognizedXMLSchemaInstanceAttribute(a)) {
                ++length;
            }
        }
        return new Integer(length);
    }

    void compareElementAttributes(Element control, Element test,
                                  NamedNodeMap controlAttr,
                                  NamedNodeMap testAttr,
                                  DifferenceListener listener)
        throws DifferenceFoundException {
        ArrayList unmatchedTestAttrs = new ArrayList();
        for (int i=0; i < testAttr.getLength(); ++i) {
            Attr nextAttr = (Attr) testAttr.item(i);
            if (!isXMLNSAttribute(nextAttr)) {
                unmatchedTestAttrs.add(nextAttr);
            }
        }
        
        for (int i=0; i < controlAttr.getLength(); ++i) {
            Attr nextAttr = (Attr) controlAttr.item(i);
            if (isXMLNSAttribute(nextAttr)) {
                // xml namespacing is handled in compareNodeBasics
            } else {
                boolean isNamespacedAttr = isNamespaced(nextAttr);
                String attrName = getUnNamespacedNodeName(nextAttr, isNamespacedAttr);
                Attr compareTo = null;
                
                if (isNamespacedAttr) {
                    compareTo = (Attr) testAttr.getNamedItemNS(
                                                               nextAttr.getNamespaceURI(), attrName);
                } else {
                    compareTo = (Attr) testAttr.getNamedItem(attrName);
                }

                if (compareTo != null) {
                    unmatchedTestAttrs.remove(compareTo);
                }

                if (isRecognizedXMLSchemaInstanceAttribute(nextAttr)) {
                    compareRecognizedXMLSchemaInstanceAttribute(nextAttr,
                                                                compareTo,
                                                                listener);

                } else if (compareTo != null) {
                    compareAttribute(nextAttr, compareTo, listener);

                    if (!XMLUnit.getIgnoreAttributeOrder()) {
                        Attr attributeItem = (Attr) testAttr.item(i);
                        String testAttrName = ATTRIBUTE_ABSENT;
                        if (attributeItem != null) {
                            testAttrName =
                                getUnNamespacedNodeName(attributeItem);
                        }
                        compare(attrName, testAttrName,
                                nextAttr, compareTo, listener, ATTR_SEQUENCE);
                    }
                } else {
                    controlTracker.clearTrackedAttribute();
                    testTracker.clearTrackedAttribute();
                    compare(attrName, null, control, test, listener,
                            ATTR_NAME_NOT_FOUND);
                }
            }
        }

        for (Iterator iter = unmatchedTestAttrs.iterator(); iter.hasNext(); ) {
            Attr nextAttr = (Attr) iter.next();
            if (isRecognizedXMLSchemaInstanceAttribute(nextAttr)) {
                compareRecognizedXMLSchemaInstanceAttribute(null, nextAttr,
                                                            listener);
            } else {
                controlTracker.clearTrackedAttribute();
                testTracker.clearTrackedAttribute();
                compare(null,
                        getUnNamespacedNodeName(nextAttr,
                                                isNamespaced(nextAttr)),
                        control, test, listener, ATTR_NAME_NOT_FOUND);
            }
        }

        controlTracker.clearTrackedAttribute();
        testTracker.clearTrackedAttribute();
    }
    
    private String getUnNamespacedNodeName(Node aNode) {
        return getUnNamespacedNodeName(aNode, isNamespaced(aNode));
    }
    
    private String getUnNamespacedNodeName(Node aNode, boolean isNamespacedNode) {
        if (isNamespacedNode) {
            return aNode.getLocalName();
        }
        return aNode.getNodeName();
    }


    /**
     * @param attribute
     * @return true if the attribute represents a namespace declaration
     */
    private boolean isXMLNSAttribute(Attr attribute) {
        return XMLConstants.XMLNS_PREFIX.equals(attribute.getPrefix()) ||
            XMLConstants.XMLNS_PREFIX.equals(attribute.getName());
    }

    /**
     * @param attr
     * @return true if the attribute is an XML Schema Instance
     * namespace attribute XMLUnit treats in a special way.
     */
    private boolean isRecognizedXMLSchemaInstanceAttribute(Attr attr) {
        return XMLConstants
            .W3C_XML_SCHEMA_INSTANCE_NS_URI.equals(attr.getNamespaceURI())
            && (XMLConstants
                .W3C_XML_SCHEMA_INSTANCE_SCHEMA_LOCATION_ATTR
                .equals(attr.getLocalName())
                || XMLConstants
                .W3C_XML_SCHEMA_INSTANCE_NO_NAMESPACE_SCHEMA_LOCATION_ATTR
                .equals(attr.getLocalName()));
    }

    /**
     * Compare two attributes
     * @param control
     * @param test
     * @param listener
     * @throws DifferenceFoundException
     */
    protected void compareRecognizedXMLSchemaInstanceAttribute(Attr control,
                                                               Attr test,
                                                               DifferenceListener listener)
        throws DifferenceFoundException {
        Attr nonNullNode = control != null ? control : test;
        Difference d = 
            XMLConstants.W3C_XML_SCHEMA_INSTANCE_SCHEMA_LOCATION_ATTR
            .equals(nonNullNode.getLocalName())
            ? SCHEMA_LOCATION : NO_NAMESPACE_SCHEMA_LOCATION;

        if (control != null) {
            controlTracker.visited(control);
        }
        if (test != null) {
            testTracker.visited(test);
        }
        
        compare(control != null ? control.getValue() : ATTRIBUTE_ABSENT,
                test != null ? test.getValue() : ATTRIBUTE_ABSENT,
                control, test, listener, d);
    }

    /**
     * Compare two attributes
     * @param control
     * @param test
     * @param listener
     * @throws DifferenceFoundException
     */
    protected void compareAttribute(Attr control, Attr test,
                                    DifferenceListener listener) throws DifferenceFoundException {
        controlTracker.visited(control);
        testTracker.visited(test);
        
        compare(control.getPrefix(), test.getPrefix(), control, test, 
                listener, NAMESPACE_PREFIX);
                
        compare(control.getValue(), test.getValue(), control, test,
                listener, ATTR_VALUE);

        compare(control.getSpecified() ? Boolean.TRUE : Boolean.FALSE,
                test.getSpecified() ? Boolean.TRUE : Boolean.FALSE,
                control, test, listener, ATTR_VALUE_EXPLICITLY_SPECIFIED);
    }

    /**
     * Compare two CDATA sections - unused, kept for backwards compatibility
     * @param control
     * @param test
     * @param listener
     * @throws DifferenceFoundException
     */
    protected void compareCDataSection(CDATASection control, CDATASection test,
                                       DifferenceListener listener) throws DifferenceFoundException {
        compareText(control, test, listener);
    }

    /**
     * Compare two comments
     * @param control
     * @param test
     * @param listener
     * @throws DifferenceFoundException
     */
    protected void compareComment(Comment control, Comment test,
                                  DifferenceListener listener) throws DifferenceFoundException {
        if (!XMLUnit.getIgnoreComments()) {
            compareCharacterData(control, test, listener, COMMENT_VALUE);
        }
    }

    /**
     * Compare two DocumentType nodes
     * @param control
     * @param test
     * @param listener
     * @throws DifferenceFoundException
     */
    protected void compareDocumentType(DocumentType control, DocumentType test,
                                       DifferenceListener listener) throws DifferenceFoundException {
        compare(control.getName(), test.getName(), control, test, listener,
                DOCTYPE_NAME);
        compare(control.getPublicId(), test.getPublicId(), control, test, listener,
                DOCTYPE_PUBLIC_ID);

        compare(control.getSystemId(), test.getSystemId(),
                control, test, listener, DOCTYPE_SYSTEM_ID);
    }

    /**
     * Compare two processing instructions
     * @param control
     * @param test
     * @param listener
     * @throws DifferenceFoundException
     */
    protected void compareProcessingInstruction(ProcessingInstruction control,
                                                ProcessingInstruction test, DifferenceListener listener)
        throws DifferenceFoundException {
        compare(control.getTarget(), test.getTarget(), control, test, listener,
                PROCESSING_INSTRUCTION_TARGET);
        compare(control.getData(), test.getData(), control, test, listener,
                PROCESSING_INSTRUCTION_DATA);
    }

    /**
     * Compare text - unused, kept for backwards compatibility
     * @param control
     * @param test
     * @param listener
     * @throws DifferenceFoundException
     */
    protected void compareText(Text control, Text test,
                               DifferenceListener listener)
        throws DifferenceFoundException {
        compareText((CharacterData) control, (CharacterData) test, listener);
    }

    /**
     * Compare text
     * @param control
     * @param test
     * @param listener
     * @throws DifferenceFoundException
     */
    protected void compareText(CharacterData control, CharacterData test,
                               DifferenceListener listener)
        throws DifferenceFoundException {
        compareCharacterData(control, test, listener,
                             control instanceof CDATASection ? CDATA_VALUE : TEXT_VALUE);
    }

    /**
     * Character comparison method used by comments, text and CDATA sections
     * @param control
     * @param test
     * @param listener
     * @param differenceType
     * @throws DifferenceFoundException
     */
    private void compareCharacterData(CharacterData control, CharacterData test,
                                      DifferenceListener listener, Difference difference)
        throws DifferenceFoundException {
        compare(control.getData(), test.getData(), control, test, listener,
                difference);
    }

    /**
     * If the expected and actual values are unequal then inform the listener of
     *  a difference and throw a DifferenceFoundException.
     * @param expected
     * @param actual
     * @param control
     * @param test
     * @param listener
     * @param differenceType
     * @throws DifferenceFoundException
     */
    protected void compare(Object expected, Object actual,
                           Node control, Node test, DifferenceListener listener, Difference difference)
        throws DifferenceFoundException {
        compare(expected, actual, control, test, listener, difference,
                controlTracker, testTracker);
    }

    /**
     * If the expected and actual values are unequal then inform the listener of
     *  a difference and throw a DifferenceFoundException.
     * @param expected
     * @param actual
     * @param control
     * @param test
     * @param listener
     * @param differenceType
     * @throws DifferenceFoundException
     */
    protected void compare(Object expected, Object actual,
                           Node control, Node test, DifferenceListener listener,
                           Difference difference, XpathNodeTracker controlLoc,
                           XpathNodeTracker testLoc)
        throws DifferenceFoundException {
        NodeDetail controlDetail = new NodeDetail(String.valueOf(expected),
                                                  control,
                                                  controlLoc == null ? null
                                                  : controlLoc.toXpathString());
        NodeDetail testDetail = new NodeDetail(String.valueOf(actual),
                                               test,
                                               testLoc == null ? null
                                               : testLoc.toXpathString());
        Difference differenceInstance = new Difference(difference, 
                                                       controlDetail, testDetail);
        if (unequal(expected, actual)) {
            listener.differenceFound(differenceInstance);
            if (controller.haltComparison(differenceInstance)) {
                throw flowControlException;
            }
        } else if (matchTracker != null) {
            matchTracker.matchFound(differenceInstance);
        }
    }

    /**
     * Test two possibly null values for inequality
     * @param expected
     * @param actual
     * @return TRUE if the values are neither both null, nor equals() equal
     */
    private boolean unequal(Object expected, Object actual) {
        return (expected==null ? actual!=null : unequalNotNull(expected, actual));
    }

    /**
     * Test two non-null values for inequality
     * @param expected
     * @param actual
     * @return TRUE if the values are not equals() equal (taking whitespace
     *  into account if necessary)
     */
    private boolean unequalNotNull(Object expected, Object actual) {
        if ((XMLUnit.getIgnoreWhitespace() || XMLUnit.getNormalizeWhitespace())
            && expected instanceof String && actual instanceof String) {
            String expectedString = ((String) expected).trim();
            String actualString = ((String) actual).trim();
            if (XMLUnit.getNormalizeWhitespace()) {
                expectedString = normalizeWhitespace(expectedString);
                actualString = normalizeWhitespace(actualString);
            }
            return !expectedString.equals(actualString);
        }
        return !(expected.equals(actual));
    }

    /**
     * Replace all whitespace characters with SPACE and collapse
     * consecutive whitespace chars to a single SPACE.
     */
    final static String normalizeWhitespace(String orig) {
        StringBuffer sb = new StringBuffer();
        boolean lastCharWasWhitespace = false;
        boolean changed = false;
        char[] characters = orig.toCharArray();
        for (int i = 0; i < characters.length; i++) {
            if (Character.isWhitespace(characters[i])) {
                if (lastCharWasWhitespace) {
                    // suppress character
                    changed = true;
                } else {
                    sb.append(' ');
                    changed |= characters[i] != ' ';
                    lastCharWasWhitespace = true;
                }
            } else {
                sb.append(characters[i]);
                lastCharWasWhitespace = false;
            }
        }
        return changed ? sb.toString() : orig;
    }

    /**
     * Marker exception thrown by the protected compare() method and passed
     * upwards through the call stack to the public compare() method.
     */
    protected static final class DifferenceFoundException extends Exception {
        private DifferenceFoundException() {
            super("This exception is used to control flow");
        }
    }

    /**
     * Exception instance used internally to control flow
     * when a difference is found
     */
    private static final DifferenceFoundException flowControlException =
        new DifferenceFoundException();
}
