/*
  This file is licensed to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

package org.xmlunit.diff;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import org.xmlunit.XMLUnitException;
import org.xmlunit.util.Convert;
import org.xmlunit.util.DocumentBuilderFactoryConfigurer;
import org.xmlunit.util.IterableNodeList;
import org.xmlunit.util.Linqy;
import org.xmlunit.util.Mapper;
import org.xmlunit.util.Nodes;
import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

/**
 * Difference engine based on DOM.
 */
public final class DOMDifferenceEngine extends AbstractDifferenceEngine {

    /**
     * Maps Nodes to their QNames.
     */
    private static final Mapper<Node, QName> QNAME_MAPPER =
        new Mapper<Node, QName>() {
            @Override
            public QName apply(Node n) { return Nodes.getQName(n); }
        };

    private DocumentBuilderFactory documentBuilderFactory;

    /**
     * Creates a new DOMDifferenceEngine using the default {@link DocumentBuilderFactory}.
     */
    public DOMDifferenceEngine() {
        this(DocumentBuilderFactoryConfigurer.Default.configure(DocumentBuilderFactory.newInstance()));
    }

    /**
     * Creates a new DOMDifferenceEngine.
     *
     * <p>The {@link DocumentBuilderFactory} is only used if the
     * {@code Source} passed to {@link #compare} is not already a
     * {@link javax.xml.transform.dom.DOMSource}.</p>
     *
     * @param f {@code DocumentBuilderFactory} to use when creating a
     * {@link Document} from the {@link Source}s to compare.
     *
     * @since XMLUnit 2.7.0
     */
    public DOMDifferenceEngine(final DocumentBuilderFactory f) {
        if (f == null) {
            throw new IllegalArgumentException("factory must not be null");
        }
        documentBuilderFactory = f;
    }

    /**
     * Sets the {@link DocumentBuilderFactory} to use when creating a
     * {@link Document} from the {@link Source}s to compare.
     *
     * <p>This is only used if the {@code Source} passed to {@link #compare}
     * is not already a {@link javax.xml.transform.dom.DOMSource}.</p>
     *
     * @param f {@code DocumentBuilderFactory} to use when creating a
     * {@link Document} from the {@link Source}s to compare.
     *
     * @since XMLUnit 2.2.0
     * @deprecated use the one-arg constructor instead
     */
    public void setDocumentBuilderFactory(DocumentBuilderFactory f) {
        if (f == null) {
            throw new IllegalArgumentException("factory must not be null");
        }
        documentBuilderFactory = f;
    }

    @Override
    public void compare(Source control, Source test) {
        if (control == null) {
            throw new IllegalArgumentException("control must not be null");
        }
        if (test == null) {
            throw new IllegalArgumentException("test must not be null");
        }
        try {
            Node controlNode = Convert.toNode(control, documentBuilderFactory);
            Node testNode = Convert.toNode(test, documentBuilderFactory);
            compareNodes(controlNode, xpathContextFor(controlNode),
                         testNode, xpathContextFor(testNode));
        } catch (Exception ex) {
            throw new XMLUnitException("Caught exception during comparison",
                                       ex);
        }
    }

    private XPathContext xpathContextFor(Node n) {
        return new XPathContext(getNamespaceContext(), n);
    }

    /**
     * Recursively compares two XML nodes.
     *
     * <p>Performs comparisons common to all node types, then performs
     * the node type specific comparisons and finally recurses into
     * the node's child lists.</p>
     *
     * <p>Stops as soon as any comparison returns
     * ComparisonResult.CRITICAL.</p>
     *
     * <p>package private to support tests.</p>
     */
    ComparisonState compareNodes(final Node control, final XPathContext controlContext,
                                 final Node test, final XPathContext testContext) {
        final Iterable<Node> allControlChildren =
            new IterableNodeList(control.getChildNodes());
        final Iterable<Node> controlChildren =
            Linqy.filter(allControlChildren, getNodeFilter());
        final Iterable<Node> allTestChildren =
            new IterableNodeList(test.getChildNodes());
        final Iterable<Node> testChildren =
            Linqy.filter(allTestChildren, getNodeFilter());

        return compare(new Comparison(ComparisonType.NODE_TYPE,
                                      control, getXPath(controlContext),
                                      control.getNodeType(), getParentXPath(controlContext),
                                      test, getXPath(testContext),
                                      test.getNodeType(), getParentXPath(testContext)))
            .andThen(new Comparison(ComparisonType.NAMESPACE_URI,
                                    control, getXPath(controlContext),
                                    control.getNamespaceURI(), getParentXPath(controlContext),
                                    test, getXPath(testContext),
                                    test.getNamespaceURI(), getParentXPath(testContext)))
            .andThen(new Comparison(ComparisonType.NAMESPACE_PREFIX,
                                    control, getXPath(controlContext),
                                    control.getPrefix(), getParentXPath(controlContext),
                                    test, getXPath(testContext),
                                    test.getPrefix(), getParentXPath(testContext)))
            .andIfTrueThen(control.getNodeType() != Node.ATTRIBUTE_NODE,
                           new Comparison(ComparisonType.CHILD_NODELIST_LENGTH,
                                          control, getXPath(controlContext),
                                          Linqy.count(controlChildren), getParentXPath(controlContext),
                                          test, getXPath(testContext),
                                          Linqy.count(testChildren), getParentXPath(testContext)))
            .andThen(new DeferredComparison() {
                    @Override
                    public ComparisonState apply() {
                        return nodeTypeSpecificComparison(control, controlContext,
                                                          test, testContext);
                    }
                })
            // and finally recurse into children
            .andIfTrueThen(control.getNodeType() != Node.ATTRIBUTE_NODE,
                           compareChildren(controlContext,
                                           allControlChildren,
                                           controlChildren,
                                           testContext,
                                           allTestChildren,
                                           testChildren));
    }

    /**
     * Dispatches to the node type specific comparison if one is
     * defined for the given combination of nodes.
     */
    private ComparisonState nodeTypeSpecificComparison(Node control,
                                                       XPathContext controlContext,
                                                       Node test,
                                                       XPathContext testContext) {
        switch (control.getNodeType()) {
        case Node.CDATA_SECTION_NODE:
        case Node.COMMENT_NODE:
        case Node.TEXT_NODE:
            if (test instanceof CharacterData) {
                return compareCharacterData((CharacterData) control,
                                            controlContext,
                                            (CharacterData) test, testContext);
            }
            break;
        case Node.DOCUMENT_NODE:
            if (test instanceof Document) {
                return compareDocuments((Document) control, controlContext,
                                        (Document) test, testContext);
            }
            break;
        case Node.ELEMENT_NODE:
            if (test instanceof Element) {
                return compareElements((Element) control, controlContext,
                                       (Element) test, testContext);
            }
            break;
        case Node.PROCESSING_INSTRUCTION_NODE:
            if (test instanceof ProcessingInstruction) {
                return
                    compareProcessingInstructions((ProcessingInstruction) control,
                                                  controlContext,
                                                  (ProcessingInstruction) test,
                                                  testContext);
            }
            break;
        case Node.DOCUMENT_TYPE_NODE:
            if (test instanceof DocumentType) {
                return compareDocTypes((DocumentType) control, controlContext,
                                       (DocumentType) test, testContext);
            }
            break;
        case Node.ATTRIBUTE_NODE:
            if (test instanceof Attr) {
                return compareAttributes((Attr) control, controlContext,
                                         (Attr) test, testContext);
            }
            break;
        default:
            break;
        }
        return new OngoingComparisonState();
    }

    private DeferredComparison compareChildren(final XPathContext controlContext,
                                               final Iterable<Node> allControlChildren,
                                               final Iterable<Node> controlChildren,
                                               final XPathContext testContext,
                                               final Iterable<Node> allTestChildren,
                                               final Iterable<Node> testChildren) {
        return new DeferredComparison() {
            @Override
            public ComparisonState apply() {
                controlContext
                    .setChildren(Linqy.map(allControlChildren, ElementSelectors.TO_NODE_INFO));
                testContext
                    .setChildren(Linqy.map(allTestChildren, ElementSelectors.TO_NODE_INFO));
                return compareNodeLists(allControlChildren, controlChildren, controlContext,
                                        allTestChildren, testChildren, testContext);
            }
        };
    }

    /**
     * Compares textual content.
     */
    private ComparisonState compareCharacterData(CharacterData control,
                                                 XPathContext controlContext,
                                                 CharacterData test,
                                                 XPathContext testContext) {
        return compare(new Comparison(ComparisonType.TEXT_VALUE, control,
                                      getXPath(controlContext),
                                      control.getData(), getParentXPath(controlContext),
                                      test, getXPath(testContext),
                                      test.getData(), getParentXPath(testContext)));
    }

    /**
     * Compares document node, doctype and XML declaration properties
     */
    private ComparisonState compareDocuments(final Document control,
                                             final XPathContext controlContext,
                                             final Document test,
                                             final XPathContext testContext) {
        final DocumentType controlDt = filterNode(control.getDoctype());
        final DocumentType testDt = filterNode(test.getDoctype());

        return compare(new Comparison(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                      control, getXPath(controlContext),
                                      Boolean.valueOf(controlDt != null), getParentXPath(controlContext),
                                      test, getXPath(testContext),
                                      Boolean.valueOf(testDt != null), getParentXPath(testContext)))
            .andIfTrueThen(controlDt != null && testDt != null,
                           new DeferredComparison() {
                               @Override
                               public ComparisonState apply() {
                                   return compareNodes(controlDt, controlContext,
                                                       testDt, testContext);
                               }
                           })
            .andThen(compareDeclarations(control, controlContext,
                                         test, testContext));
    }

    private <T extends Node> T filterNode(T n) {
        return n != null && getNodeFilter().test(n) ? n : null;
    }

    /**
     * Compares properties of the doctype declaration.
     */
    private ComparisonState
        compareDocTypes(DocumentType control,
                        XPathContext controlContext,
                        DocumentType test,
                        XPathContext testContext) {
        return compare(new Comparison(ComparisonType.DOCTYPE_NAME,
                                      control, getXPath(controlContext),
                                      control.getName(), getParentXPath(controlContext),
                                      test, getXPath(testContext),
                                      test.getName(), getParentXPath(testContext)))
            .andThen(new Comparison(ComparisonType.DOCTYPE_PUBLIC_ID,
                                    control, getXPath(controlContext),
                                    control.getPublicId(), getParentXPath(controlContext),
                                    test, getXPath(testContext),
                                    test.getPublicId(), getParentXPath(testContext)))
            .andThen(new Comparison(ComparisonType.DOCTYPE_SYSTEM_ID,
                                    control, null, control.getSystemId(), null,
                                    test, null, test.getSystemId(), null));
    }

    /**
     * Compares properties of XML declaration.
     */
    private DeferredComparison compareDeclarations(final Document control,
                                                   final XPathContext controlContext,
                                                   final Document test,
                                                   final XPathContext testContext) {
        return new DeferredComparison() {
            @Override
            public ComparisonState apply() {
                return
                    compare(new Comparison(ComparisonType.XML_VERSION,
                                           control, getXPath(controlContext),
                                           control.getXmlVersion(), getParentXPath(controlContext),
                                           test, getXPath(testContext),
                                           test.getXmlVersion(), getParentXPath(testContext)))
                    .andThen(new Comparison(ComparisonType.XML_STANDALONE,
                                            control, getXPath(controlContext),
                                            control.getXmlStandalone(), getParentXPath(controlContext),
                                            test, getXPath(testContext),
                                            test.getXmlStandalone(), getParentXPath(testContext)))
                    .andThen(new Comparison(ComparisonType.XML_ENCODING,
                                            control, getXPath(controlContext),
                                            control.getXmlEncoding(), getParentXPath(controlContext),
                                            test, getXPath(testContext),
                                            test.getXmlEncoding(), getParentXPath(testContext)));
            }
        };
    }

    /**
     * Compares elements node properties, in particular the element's
     * name and its attributes.
     */
    private ComparisonState compareElements(final Element control,
                                            final XPathContext controlContext,
                                            final Element test,
                                            final XPathContext testContext) {
        return
            compare(new Comparison(ComparisonType.ELEMENT_TAG_NAME,
                                   control, getXPath(controlContext),
                                   Nodes.getQName(control).getLocalPart(), getParentXPath(controlContext),
                                   test, getXPath(testContext),
                                   Nodes.getQName(test).getLocalPart(), getParentXPath(testContext)))
            .andThen(new DeferredComparison() {
                    @Override
                    public ComparisonState apply() {
                        return compareElementAttributes(control, controlContext,
                                                        test, testContext);
                    }
                });
    }

    /**
     * Compares element's attributes.
     */
    private ComparisonState compareElementAttributes(final Element control,
                                                     final XPathContext controlContext,
                                                     final Element test,
                                                     final XPathContext testContext) {
        final Attributes controlAttributes = splitAttributes(control.getAttributes());
        controlContext
            .addAttributes(Linqy.map(controlAttributes.remainingAttributes,
                                     QNAME_MAPPER));
        final Attributes testAttributes = splitAttributes(test.getAttributes());
        testContext
            .addAttributes(Linqy.map(testAttributes.remainingAttributes,
                                     QNAME_MAPPER));

        return compare(new Comparison(ComparisonType.ELEMENT_NUM_ATTRIBUTES,
                                      control, getXPath(controlContext),
                                      controlAttributes.remainingAttributes.size(), getParentXPath(controlContext),
                                      test, getXPath(testContext),
                                      testAttributes.remainingAttributes.size(), getParentXPath(testContext)))
            .andThen(new DeferredComparison() {
                    @Override
                    public ComparisonState apply() {
                        return compareXsiType(controlAttributes.type, controlContext,
                                              testAttributes.type, testContext);
                    }
                })
            .andThen(new Comparison(ComparisonType.SCHEMA_LOCATION,
                                    control, getXPath(controlContext),
                                    controlAttributes.schemaLocation != null
                                    ? controlAttributes.schemaLocation.getValue() : null, getParentXPath(controlContext),
                                    test, getXPath(testContext),
                                    testAttributes.schemaLocation != null
                                    ? testAttributes.schemaLocation.getValue() : null, getParentXPath(testContext)))
            .andThen(new Comparison(ComparisonType.NO_NAMESPACE_SCHEMA_LOCATION,
                                    control, getXPath(controlContext),
                                    controlAttributes.noNamespaceSchemaLocation != null ?
                                    controlAttributes.noNamespaceSchemaLocation.getValue()
                                    : null, getParentXPath(controlContext),
                                    test, getXPath(testContext),
                                    testAttributes.noNamespaceSchemaLocation != null
                                    ? testAttributes.noNamespaceSchemaLocation.getValue()
                                    : null, getParentXPath(testContext)))
            .andThen(new NormalAttributeComparer(control, controlContext,
                                                 controlAttributes, test,
                                                 testContext, testAttributes));
    }

    private class NormalAttributeComparer implements DeferredComparison {
        private final Set<Attr> foundTestAttributes = new HashSet<Attr>();
        private final Element control, test;
        private final XPathContext controlContext, testContext;
        private final Attributes controlAttributes, testAttributes;

        private NormalAttributeComparer(Element control,
                                        XPathContext controlContext,
                                        Attributes controlAttributes,
                                        Element test,
                                        XPathContext testContext,
                                        Attributes testAttributes) {
            this.control = control;
            this.controlContext = controlContext;
            this.controlAttributes = controlAttributes;
            this.test = test;
            this.testContext = testContext;
            this.testAttributes = testAttributes;
        }

        @Override
        public ComparisonState apply() {
            ComparisonState chain = new OngoingComparisonState();
            for (final Attr controlAttr : controlAttributes.remainingAttributes) {
                final QName controlAttrName = Nodes.getQName(controlAttr);
                final Attr testAttr =
                    findMatchingAttr(testAttributes.remainingAttributes,
                                     controlAttr);
                final QName testAttrName = testAttr != null
                    ? Nodes.getQName(testAttr) : null;

                controlContext.navigateToAttribute(controlAttrName);
                try {
                    chain = chain.andThen(
                        new Comparison(ComparisonType.ATTR_NAME_LOOKUP,
                                                control, getXPath(controlContext),
                                                controlAttrName, getParentXPath(controlContext),
                                                test, getXPath(testContext),
                                                testAttrName, getParentXPath(testContext)));

                    if (testAttr != null) {
                        testContext.navigateToAttribute(testAttrName);
                        try {
                            chain = chain.andThen(new DeferredComparison() {
                                    @Override
                                    public ComparisonState apply() {
                                        return compareNodes(controlAttr, controlContext,
                                                            testAttr, testContext);
                                    }
                                });
                            foundTestAttributes.add(testAttr);
                        } finally {
                            testContext.navigateToParent();
                        }
                    }
                } finally {
                    controlContext.navigateToParent();
                }
            }
            return chain.andThen(new ControlAttributePresentComparer(control,
                                                                     controlContext,
                                                                     test, testContext,
                                                                     testAttributes,
                                                                     foundTestAttributes));
        }
    }

    private class ControlAttributePresentComparer implements DeferredComparison {

        private final Set<Attr> foundTestAttributes;
        private final Element control, test;
        private final XPathContext controlContext, testContext;
        private final Attributes testAttributes;

        private ControlAttributePresentComparer(Element control,
                                                XPathContext controlContext,
                                                Element test,
                                                XPathContext testContext,
                                                Attributes testAttributes,
                                                Set<Attr> foundTestAttributes) {
            this.control = control;
            this.controlContext = controlContext;
            this.test = test;
            this.testContext = testContext;
            this.testAttributes = testAttributes;
            this.foundTestAttributes = foundTestAttributes;
        }

        @Override
        public ComparisonState apply() {
            ComparisonState chain = new OngoingComparisonState();
            for (Attr testAttr : testAttributes.remainingAttributes) {
                if (!foundTestAttributes.contains(testAttr)) {
                    QName testAttrName = Nodes.getQName(testAttr);
                    testContext.navigateToAttribute(testAttrName);
                    try {
                        chain =
                            chain.andThen(new Comparison(ComparisonType.ATTR_NAME_LOOKUP,
                                                         control,
                                                         getXPath(controlContext),
                                                         null, getParentXPath(controlContext),
                                                         test, getXPath(testContext),
                                                         testAttrName, getParentXPath(testContext)));
                    } finally {
                        testContext.navigateToParent();
                    }
                }
            }
            return chain;
        }

    }

    /**
     * Compares properties of a processing instruction.
     */
    private ComparisonState compareProcessingInstructions(ProcessingInstruction control,
                                                          XPathContext controlContext,
                                                          ProcessingInstruction test,
                                                          XPathContext testContext) {
        return compare(new Comparison(ComparisonType.PROCESSING_INSTRUCTION_TARGET,
                                      control, getXPath(controlContext),
                                      control.getTarget(), getParentXPath(controlContext),
                                      test, getXPath(testContext),
                                      test.getTarget(), getParentXPath(testContext)))
            .andThen(new Comparison(ComparisonType.PROCESSING_INSTRUCTION_DATA,
                                    control, getXPath(controlContext),
                                    control.getData(), getParentXPath(controlContext),
                                    test, getXPath(testContext),
                                    test.getData(), getParentXPath(testContext)));
    }

    /**
     * Matches nodes of two node lists and invokes compareNode on each pair.
     *
     * <p>Also performs CHILD_LOOKUP comparisons for each node that
     * couldn't be matched to one of the "other" list.</p>
     */
    private ComparisonState compareNodeLists(Iterable<Node> allControlChildren,
                                             Iterable<Node> controlSeq,
                                             final XPathContext controlContext,
                                             Iterable<Node> allTestChildren,
                                             Iterable<Node> testSeq,
                                             final XPathContext testContext) {
        ComparisonState chain = new OngoingComparisonState();

        Iterable<Map.Entry<Node, Node>> matches =
            getNodeMatcher().match(controlSeq, testSeq);
        List<Node> controlListForXpath = Linqy.asList(allControlChildren);
        List<Node> testListForXpath = Linqy.asList(allTestChildren);
        List<Node> controlList = Linqy.asList(controlSeq);
        List<Node> testList = Linqy.asList(testSeq);
        Set<Node> seen = new HashSet<Node>();
        for (Map.Entry<Node, Node> pair : matches) {
            final Node control = pair.getKey();
            seen.add(control);
            final Node test = pair.getValue();
            seen.add(test);
            int controlIndexForXpath = controlListForXpath.indexOf(control);
            int testIndexForXpath = testListForXpath.indexOf(test);
            int controlIndex = controlList.indexOf(control);
            int testIndex = testList.indexOf(test);

            controlContext.navigateToChild(controlIndexForXpath);
            testContext.navigateToChild(testIndexForXpath);
            try {
                chain =
                    chain.andThen(new Comparison(ComparisonType.CHILD_NODELIST_SEQUENCE,
                                                 control, getXPath(controlContext),
                                                 Integer.valueOf(controlIndex), getParentXPath(controlContext),
                                                 test, getXPath(testContext),
                                                 Integer.valueOf(testIndex), getParentXPath(testContext)))
                    .andThen(new DeferredComparison() {
                            @Override
                            public ComparisonState apply() {
                                return compareNodes(control, controlContext,
                                                    test, testContext);
                            }
                        });
            } finally {
                testContext.navigateToParent();
                controlContext.navigateToParent();
            }
        }

        return chain.andThen(new UnmatchedControlNodes(controlListForXpath, controlList, controlContext, seen,
                testContext))
            .andThen(new UnmatchedTestNodes(testListForXpath, testList, testContext, seen,
                controlContext));
    }

    private class UnmatchedControlNodes implements DeferredComparison {
        private final List<Node> controlListForXpath;
        private final List<Node> controlList;
        private final XPathContext controlContext;
        private final Set<Node> seen;
        private final XPathContext testContext;

        private UnmatchedControlNodes(List<Node> controlListForXpath, List<Node> controlList, XPathContext controlContext,
                                      Set<Node> seen, XPathContext testContext) {
            this.controlListForXpath = controlListForXpath;
            this.controlList = controlList;
            this.controlContext = controlContext;
            this.seen = seen;
            this.testContext = testContext;
        }

        @Override
        public ComparisonState apply() {
            ComparisonState chain = new OngoingComparisonState();
            final int controlSize = controlList.size();
            for (int i = 0; i < controlSize; i++) {
                if (!seen.contains(controlList.get(i))) {
                    controlContext.navigateToChild(controlListForXpath.indexOf(controlList.get(i)));
                    try {
                        chain =
                            chain.andThen(new Comparison(ComparisonType.CHILD_LOOKUP,
                                                         controlList.get(i),
                                                         getXPath(controlContext),
                                                         Nodes.getQName(controlList.get(i)), getParentXPath(controlContext),
                                                         null, null, null, getXPath(testContext)));
                    } finally {
                        controlContext.navigateToParent();
                    }
                }
            }
            return chain;
        }
    }

    private class UnmatchedTestNodes implements DeferredComparison {
        private final List<Node> testListForXpath;
        private final List<Node> testList;
        private final XPathContext testContext;
        private final Set<Node> seen;
        private final XPathContext controlContext;

        private UnmatchedTestNodes(List<Node> testListForXpath, List<Node> testList, XPathContext testContext,
                                   Set<Node> seen, XPathContext controlContext) {
            this.testListForXpath = testListForXpath;
            this.testList = testList;
            this.testContext = testContext;
            this.seen = seen;
            this.controlContext = controlContext;
        }

        @Override
        public ComparisonState apply() {
            ComparisonState chain = new OngoingComparisonState();
            final int testSize = testList.size();
            for (int i = 0; i < testSize; i++) {
                if (!seen.contains(testList.get(i))) {
                    testContext.navigateToChild(testListForXpath.indexOf(testList.get(i)));
                    try {
                        chain =
                            chain.andThen(new Comparison(ComparisonType.CHILD_LOOKUP,
                                                         null, null, null, getXPath(controlContext),
                                                         testList.get(i),
                                                         getXPath(testContext),
                                                         Nodes.getQName(testList.get(i)), getParentXPath(testContext)));
                    } finally {
                        testContext.navigateToParent();
                    }
                }
            }
            return chain;
        }
    }

    /**
     * Compares xsi:type attribute values
     */
    private ComparisonState compareXsiType(Attr controlAttr,
                                           XPathContext controlContext,
                                           Attr testAttr,
                                           XPathContext testContext) {
        boolean mustChangeControlContext = controlAttr != null;
        boolean mustChangeTestContext = testAttr != null;
        if (!mustChangeControlContext && !mustChangeTestContext) {
            return new OngoingComparisonState();
        }
        boolean attributePresentOnBothSides = mustChangeControlContext
            && mustChangeTestContext;

        try {
            QName controlAttrName = null;
            if (mustChangeControlContext) {
                controlAttrName = Nodes.getQName(controlAttr);
                controlContext.addAttribute(controlAttrName);
                controlContext.navigateToAttribute(controlAttrName);
            }
            QName testAttrName = null;
            if (mustChangeTestContext) {
                testAttrName = Nodes.getQName(testAttr);
                testContext.addAttribute(testAttrName);
                testContext.navigateToAttribute(testAttrName);
            }
            return
                compare(new Comparison(ComparisonType.ATTR_NAME_LOOKUP,
                                       controlAttr, getXPath(controlContext),
                                       controlAttrName, getParentXPath(controlContext),
                                       testAttr, getXPath(testContext),
                                       testAttrName, getParentXPath(testContext)))
                .andIfTrueThen(attributePresentOnBothSides,
                               compareAttributeExplicitness(controlAttr, controlContext,
                                                            testAttr, testContext))
                .andIfTrueThen(attributePresentOnBothSides,
                               new Comparison(ComparisonType.ATTR_VALUE,
                                              controlAttr, getXPath(controlContext),
                                              valueAsQName(controlAttr), getParentXPath(controlContext),
                                              testAttr, getXPath(testContext),
                                              valueAsQName(testAttr), getParentXPath(testContext)));
        } finally {
            if (mustChangeControlContext) {
                controlContext.navigateToParent();
            }
            if (mustChangeTestContext) {
                testContext.navigateToParent();
            }
        }
    }

    /**
     * Compares properties of an attribute.
     */
    private ComparisonState compareAttributes(Attr control,
                                              XPathContext controlContext,
                                              Attr test,
                                              XPathContext testContext) {
        return compareAttributeExplicitness(control, controlContext, test,
                                            testContext).apply()
            .andThen(new Comparison(ComparisonType.ATTR_VALUE,
                                    control, getXPath(controlContext),
                                    control.getValue(), getParentXPath(controlContext),
                                    test, getXPath(testContext),
                                    test.getValue(), getParentXPath(testContext)));
    }

    /**
     * Compares whether two attributes are specified explicitly.
     */
    private DeferredComparison compareAttributeExplicitness(final Attr control,
                                                            final XPathContext controlContext,
                                                            final Attr test,
                                                            final XPathContext testContext) {
        return new DeferredComparison() {
            @Override
            public ComparisonState apply() {
                return compare(new Comparison(ComparisonType.ATTR_VALUE_EXPLICITLY_SPECIFIED,
                                              control, getXPath(controlContext),
                                              control.getSpecified(), getParentXPath(controlContext),
                                              test, getXPath(testContext),
                                              test.getSpecified(), getParentXPath(testContext)));
            }
        };
    }

    /**
     * Separates XML namespace related attributes from "normal" attributes.xb
     */
    private Attributes splitAttributes(final NamedNodeMap map) {
        Attr sLoc = (Attr) map.getNamedItemNS(XMLConstants
                                              .W3C_XML_SCHEMA_INSTANCE_NS_URI,
                                              "schemaLocation");
        Attr nNsLoc = (Attr) map.getNamedItemNS(XMLConstants
                                                .W3C_XML_SCHEMA_INSTANCE_NS_URI,
                                                "noNamespaceSchemaLocation");
        Attr type = (Attr) map.getNamedItemNS(XMLConstants
                                                .W3C_XML_SCHEMA_INSTANCE_NS_URI,
                                                "type");
        List<Attr> rest = new LinkedList<Attr>();
        final int len = map.getLength();
        for (int i = 0; i < len; i++) {
            Attr a = (Attr) map.item(i);
            if (!XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(a.getNamespaceURI())
                && a != sLoc && a != nNsLoc && a != type
                && getAttributeFilter().test(a)) {
                rest.add(a);
            }
        }
        return new Attributes(sLoc, nNsLoc, type, rest);
    }

    private static QName valueAsQName(Attr attribute) {
        if (attribute == null) {
            return null;
        }
        // split QName into prefix and local name
        String[] pieces = attribute.getValue().split(":");
        if (pieces.length < 2) {
            // unprefixed name
            pieces = new String[] { null, pieces[0] };
        } else if (pieces.length > 2) {
            // actually, this is not a valid QName - be lenient
            pieces = new String[] {
                pieces[0],
                attribute.getValue().substring(pieces[0].length() + 1)
            };
        }
        if ("".equals(pieces[0])) {
            pieces[0] = null;
        }
        return new QName(attribute.lookupNamespaceURI(pieces[0]), pieces[1]);
    }

    private static class Attributes {
        private final Attr schemaLocation;
        private final Attr noNamespaceSchemaLocation;
        private final Attr type;
        private final List<Attr> remainingAttributes;
        private Attributes(Attr schemaLocation, Attr noNamespaceSchemaLocation,
                           Attr type, List<Attr> remainingAttributes) {
            this.schemaLocation = schemaLocation;
            this.noNamespaceSchemaLocation = noNamespaceSchemaLocation;
            this.type = type;
            this.remainingAttributes = remainingAttributes;
        }
    }

    /**
     * Find the attribute with the same namespace and local name as a
     * given attribute in a list of attributes.
     */
    private static Attr findMatchingAttr(final List<Attr> attrs,
                                         final Attr attrToMatch) {
        final boolean hasNs = attrToMatch.getNamespaceURI() != null;
        final String nsToMatch = attrToMatch.getNamespaceURI();
        final String nameToMatch = hasNs ? attrToMatch.getLocalName()
            : attrToMatch.getName();
        for (Attr a : attrs) {
            if (((!hasNs && a.getNamespaceURI() == null)
                 ||
                 (hasNs && nsToMatch.equals(a.getNamespaceURI())))
                &&
                ((hasNs && nameToMatch.equals(a.getLocalName()))
                 ||
                 (!hasNs && nameToMatch.equals(a.getName())))
                ) {
                return a;
            }
        }
        return null;
    }

}
