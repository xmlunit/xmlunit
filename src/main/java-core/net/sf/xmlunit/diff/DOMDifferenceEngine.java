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

package net.sf.xmlunit.diff;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import net.sf.xmlunit.exceptions.XMLUnitException;
import net.sf.xmlunit.util.Convert;
import net.sf.xmlunit.util.IterableNodeList;
import net.sf.xmlunit.util.Linqy;
import net.sf.xmlunit.util.Nodes;
import net.sf.xmlunit.util.Predicate;
import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;

/**
 * Difference engine based on DOM.
 */
public final class DOMDifferenceEngine extends AbstractDifferenceEngine {

    public void compare(Source control, Source test) {
        if (control == null) {
            throw new IllegalArgumentException("control must not be null");
        }
        if (test == null) {
            throw new IllegalArgumentException("test must not be null");
        }
        try {
            compareNodes(Convert.toNode(control), new XPathContext(),
                         Convert.toNode(test), new XPathContext());
        } catch (Exception ex) {
            throw new XMLUnitException("Caught exception during comparison",
                                       ex);
        }
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
    ComparisonResult compareNodes(Node control, XPathContext controlContext,
                                  Node test, XPathContext testContext) {
        ComparisonResult lastResult =
            compare(new Comparison(ComparisonType.NODE_TYPE,
                                   control, getXPath(controlContext),
                                   control.getNodeType(),
                                   test, getXPath(testContext),
                                   test.getNodeType()));
        if (lastResult == ComparisonResult.CRITICAL) {
            return lastResult;
        }

        lastResult =
            compare(new Comparison(ComparisonType.NAMESPACE_URI,
                                   control, getXPath(controlContext),
                                   control.getNamespaceURI(),
                                   test, getXPath(testContext),
                                   test.getNamespaceURI()));

        if (lastResult == ComparisonResult.CRITICAL) {
            return lastResult;
        }

        lastResult =
            compare(new Comparison(ComparisonType.NAMESPACE_PREFIX,
                                   control, getXPath(controlContext),
                                   control.getPrefix(),
                                   test, getXPath(testContext),
                                   test.getPrefix()));
        if (lastResult == ComparisonResult.CRITICAL) {
            return lastResult;
        }


        Iterable<Node> controlChildren =
            Linqy.filter(new IterableNodeList(control.getChildNodes()),
                         INTERESTING_NODES);
        Iterable<Node> testChildren =
            Linqy.filter(new IterableNodeList(test.getChildNodes()),
                         INTERESTING_NODES);
        if (control.getNodeType() != Node.ATTRIBUTE_NODE) {
            lastResult =
                compare(new Comparison(ComparisonType.CHILD_NODELIST_LENGTH,
                                       control, getXPath(controlContext),
                                       Linqy.count(controlChildren),
                                       test, getXPath(testContext),
                                       Linqy.count(testChildren)));
            if (lastResult == ComparisonResult.CRITICAL) {
                return lastResult;
            }
        }

        lastResult = nodeTypeSpecificComparison(control, controlContext,
                                                test, testContext);
        if (lastResult == ComparisonResult.CRITICAL) {
            return lastResult;
        }

        if (control.getNodeType() != Node.ATTRIBUTE_NODE) {
            controlContext
                .setChildren(Linqy.map(controlChildren, TO_NODE_INFO));
            testContext
                .setChildren(Linqy.map(testChildren, TO_NODE_INFO));

            lastResult = compareNodeLists(controlChildren, controlContext,
                                          testChildren, testContext);
            if (lastResult == ComparisonResult.CRITICAL) {
                return lastResult;
            }
        }
        return lastResult;
    }

    /**
     * Dispatches to the node type specific comparison if one is
     * defined for the given combination of nodes.
     */
    private ComparisonResult
        nodeTypeSpecificComparison(Node control,
                                   XPathContext controlContext,
                                   Node test, XPathContext testContext) {
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
        }
        return ComparisonResult.EQUAL;
    }

    /**
     * Compares textual content.
     */
    private ComparisonResult compareCharacterData(CharacterData control,
                                                  XPathContext controlContext,
                                                  CharacterData test,
                                                  XPathContext testContext) {
        return compare(new Comparison(ComparisonType.TEXT_VALUE, control,
                                      getXPath(controlContext),
                                      control.getData(),
                                      test, getXPath(testContext),
                                      test.getData()));
    }

    /**
     * Compares document node, doctype and XML declaration properties
     */
    private ComparisonResult compareDocuments(Document control,
                                              XPathContext controlContext,
                                              Document test,
                                              XPathContext testContext) {
        DocumentType controlDt = control.getDoctype();
        DocumentType testDt = test.getDoctype();

        ComparisonResult lastResult =
            compare(new Comparison(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                   control, getXPath(controlContext),
                                   Boolean.valueOf(controlDt != null),
                                   test, getXPath(testContext),
                                   Boolean.valueOf(testDt != null)));
        if (lastResult == ComparisonResult.CRITICAL) {
            return lastResult;
        }

        if (controlDt != null && testDt != null) {
            lastResult = compareNodes(controlDt, controlContext,
                                      testDt, testContext);
            if (lastResult == ComparisonResult.CRITICAL) {
                return lastResult;
            }
        }

        lastResult =
            compare(new Comparison(ComparisonType.XML_VERSION,
                                   control, getXPath(controlContext),
                                   control.getXmlVersion(),
                                   test, getXPath(testContext),
                                   test.getXmlVersion()));
        if (lastResult == ComparisonResult.CRITICAL) {
            return lastResult;
        }


        lastResult =
            compare(new Comparison(ComparisonType.XML_STANDALONE,
                                   control, getXPath(controlContext),
                                   control.getXmlStandalone(),
                                   test, getXPath(testContext),
                                   test.getXmlStandalone()));
        if (lastResult == ComparisonResult.CRITICAL) {
            return lastResult;
        }

        return compare(new Comparison(ComparisonType.XML_ENCODING,
                                      control, getXPath(controlContext),
                                      control.getXmlEncoding(),
                                      test, getXPath(testContext),
                                      test.getXmlEncoding()));
    }

    /**
     * Compares properties of the doctype declaration.
     */
    private ComparisonResult compareDocTypes(DocumentType control,
                                             XPathContext controlContext,
                                             DocumentType test,
                                             XPathContext testContext) {
        ComparisonResult lastResult =
            compare(new Comparison(ComparisonType.DOCTYPE_NAME,
                                   control, getXPath(controlContext),
                                   control.getName(),
                                   test, getXPath(testContext),
                                   test.getName()));
        if (lastResult == ComparisonResult.CRITICAL) {
            return lastResult;
        }

        lastResult =
            compare(new Comparison(ComparisonType.DOCTYPE_PUBLIC_ID,
                                   control, getXPath(controlContext),
                                   control.getPublicId(),
                                   test, getXPath(testContext),
                                   test.getPublicId()));
        if (lastResult == ComparisonResult.CRITICAL) {
            return lastResult;
        }

        return compare(new Comparison(ComparisonType.DOCTYPE_SYSTEM_ID,
                                      control, null, control.getSystemId(),
                                      test, null, test.getSystemId()));
    }

    /**
     * Compares elements node properties, in particular the element's
     * name and its attributes.
     */
    private ComparisonResult compareElements(Element control,
                                             XPathContext controlContext,
                                             Element test,
                                             XPathContext testContext) {
        ComparisonResult lastResult =
            compare(new Comparison(ComparisonType.ELEMENT_TAG_NAME,
                                   control, getXPath(controlContext),
                                   Nodes.getQName(control).getLocalPart(),
                                   test, getXPath(testContext),
                                   Nodes.getQName(test).getLocalPart()));
        if (lastResult == ComparisonResult.CRITICAL) {
            return lastResult;
        }

        Attributes controlAttributes = splitAttributes(control.getAttributes());
        controlContext
            .addAttributes(Linqy.map(controlAttributes.remainingAttributes,
                                     QNAME_MAPPER));
        Attributes testAttributes = splitAttributes(test.getAttributes());
        testContext
            .addAttributes(Linqy.map(testAttributes.remainingAttributes,
                                     QNAME_MAPPER));
        Set<Attr> foundTestAttributes = new HashSet<Attr>();

        lastResult =
            compare(new Comparison(ComparisonType.ELEMENT_NUM_ATTRIBUTES,
                                   control, getXPath(controlContext),
                                   controlAttributes.remainingAttributes.size(),
                                   test, getXPath(testContext),
                                   testAttributes.remainingAttributes.size()));
        if (lastResult == ComparisonResult.CRITICAL) {
            return lastResult;
        }

        for (Attr controlAttr : controlAttributes.remainingAttributes) {
            final Attr testAttr =
                findMatchingAttr(testAttributes.remainingAttributes,
                                 controlAttr);

            controlContext.navigateToAttribute(Nodes.getQName(controlAttr));
            try {
                lastResult =
                    compare(new Comparison(ComparisonType.ATTR_NAME_LOOKUP,
                                           control, getXPath(controlContext),
                                           Boolean.TRUE,
                                           test, getXPath(testContext),
                                           Boolean.valueOf(testAttr != null)));
                if (lastResult == ComparisonResult.CRITICAL) {
                    return lastResult;
                }

                if (testAttr != null) {
                    testContext.navigateToAttribute(Nodes.getQName(testAttr));
                    try {
                        lastResult = compareNodes(controlAttr, controlContext,
                                                  testAttr, testContext);
                        if (lastResult == ComparisonResult.CRITICAL) {
                            return lastResult;
                        }

                        foundTestAttributes.add(testAttr);
                    } finally {
                        testContext.navigateToParent();
                    }
                }
            } finally {
                controlContext.navigateToParent();
            }
        }

        for (Attr testAttr : testAttributes.remainingAttributes) {
            testContext.navigateToAttribute(Nodes.getQName(testAttr));
            try {
                lastResult =
                    compare(new Comparison(ComparisonType.ATTR_NAME_LOOKUP,
                                           control, getXPath(controlContext),
                                           Boolean.valueOf(foundTestAttributes.contains(testAttr)),
                                           test, getXPath(testContext),
                                           Boolean.TRUE));
                if (lastResult == ComparisonResult.CRITICAL) {
                    return lastResult;
                }
            } finally {
                testContext.navigateToParent();
            }
        }

        lastResult =
            compare(new Comparison(ComparisonType.SCHEMA_LOCATION,
                                   control, getXPath(controlContext),
                                   controlAttributes.schemaLocation != null
                                   ? controlAttributes.schemaLocation.getValue()
                                   : null,
                                   test, getXPath(testContext),
                                   testAttributes.schemaLocation != null
                                   ? testAttributes.schemaLocation.getValue()
                                   : null));
        if (lastResult == ComparisonResult.CRITICAL) {
            return lastResult;
        }

        return
            compare(new Comparison(ComparisonType.NO_NAMESPACE_SCHEMA_LOCATION,
                                   control, getXPath(controlContext),
                                   controlAttributes.noNamespaceSchemaLocation != null ?
                                   controlAttributes.noNamespaceSchemaLocation.getValue()
                                   : null,
                                   test, getXPath(testContext),
                                   testAttributes.noNamespaceSchemaLocation != null
                                   ? testAttributes.noNamespaceSchemaLocation.getValue()
                                   : null));
    }

    /**
     * Compares properties of a processing instruction.
     */
    private ComparisonResult
        compareProcessingInstructions(ProcessingInstruction control,
                                      XPathContext controlContext,
                                      ProcessingInstruction test,
                                      XPathContext testContext) {
        ComparisonResult lastResult =
            compare(new Comparison(ComparisonType.PROCESSING_INSTRUCTION_TARGET,
                                   control, getXPath(controlContext),
                                   control.getTarget(),
                                   test, getXPath(testContext),
                                   test.getTarget()));
        if (lastResult == ComparisonResult.CRITICAL) {
            return lastResult;
        }

        return compare(new Comparison(ComparisonType.PROCESSING_INSTRUCTION_DATA,
                                      control, getXPath(controlContext),
                                      control.getData(),
                                      test, getXPath(testContext),
                                      test.getData()));
    }

    /**
     * Matches nodes of two node lists and invokes compareNode on each pair.
     *
     * <p>Also performs CHILD_LOOKUP comparisons for each node that
     * couldn't be matched to one of the "other" list.</p>
     */
    private ComparisonResult compareNodeLists(Iterable<Node> controlSeq,
                                              XPathContext controlContext,
                                              Iterable<Node> testSeq,
                                              XPathContext testContext) {
        // if there are no children on either Node, the result is equal
        ComparisonResult lastResult = ComparisonResult.EQUAL;

        Iterable<Map.Entry<Node, Node>> matches =
            getNodeMatcher().match(controlSeq, testSeq);
        List<Node> controlList = Linqy.asList(controlSeq);
        List<Node> testList = Linqy.asList(testSeq);
        Set<Node> seen = new HashSet<Node>();
        for (Map.Entry<Node, Node> pair : matches) {
            Node control = pair.getKey();
            seen.add(control);
            Node test = pair.getValue();
            seen.add(test);
            int controlIndex = controlList.indexOf(control);
            int testIndex = testList.indexOf(test);

            controlContext.navigateToChild(controlIndex);
            testContext.navigateToChild(testIndex);
            try {
                lastResult =
                    compare(new Comparison(ComparisonType.CHILD_NODELIST_SEQUENCE,
                                           control, getXPath(controlContext),
                                           Integer.valueOf(controlIndex),
                                           test, getXPath(testContext),
                                           Integer.valueOf(testIndex)));
                if (lastResult == ComparisonResult.CRITICAL) {
                    return lastResult;
                }

                lastResult = compareNodes(control, controlContext,
                                          test, testContext);
                if (lastResult == ComparisonResult.CRITICAL) {
                    return lastResult;
                }
            } finally {
                testContext.navigateToParent();
                controlContext.navigateToParent();
            }
        }

        final int controlSize = controlList.size();
        for (int i = 0; i < controlSize; i++) {
            if (!seen.contains(controlList.get(i))) {
                controlContext.navigateToChild(i);
                try {
                    lastResult =
                        compare(new Comparison(ComparisonType.CHILD_LOOKUP,
                                               controlList.get(i),
                                               getXPath(controlContext),
                                               controlList.get(i),
                                               null, null, null));
                    if (lastResult == ComparisonResult.CRITICAL) {
                        return lastResult;
                    }
                } finally {
                    controlContext.navigateToParent();
                }
            }
        }

        final int testSize = testList.size();
        for (int i = 0; i < testSize; i++) {
            if (!seen.contains(testList.get(i))) {
                testContext.navigateToChild(i);
                try {
                    lastResult =
                        compare(new Comparison(ComparisonType.CHILD_LOOKUP,
                                               null, null, null,
                                               testList.get(i),
                                               getXPath(testContext),
                                               testList.get(i)));
                    if (lastResult == ComparisonResult.CRITICAL) {
                        return lastResult;
                    }
                } finally {
                    testContext.navigateToParent();
                }
            }
        }
        return lastResult;
    }

    /**
     * Compares properties of an attribute.
     */
    private ComparisonResult compareAttributes(Attr control,
                                               XPathContext controlContext,
                                               Attr test,
                                               XPathContext testContext) {
        ComparisonResult lastResult =
            compare(new Comparison(ComparisonType.ATTR_VALUE_EXPLICITLY_SPECIFIED,
                                   control, getXPath(controlContext),
                                   control.getSpecified(),
                                   test, getXPath(testContext),
                                   test.getSpecified()));
        if (lastResult == ComparisonResult.CRITICAL) {
            return lastResult;
        }

        return compare(new Comparison(ComparisonType.ATTR_VALUE,
                                      control, getXPath(controlContext),
                                      control.getValue(),
                                      test, getXPath(testContext),
                                      test.getValue()));
    }

    /**
     * Separates XML namespace related attributes from "normal" attributes.xb
     */
    private static Attributes splitAttributes(final NamedNodeMap map) {
        Attr sLoc = (Attr) map.getNamedItemNS(XMLConstants
                                              .W3C_XML_SCHEMA_INSTANCE_NS_URI,
                                              "schemaLocation");
        Attr nNsLoc = (Attr) map.getNamedItemNS(XMLConstants
                                                .W3C_XML_SCHEMA_INSTANCE_NS_URI,
                                                "noNamespaceSchemaLocation");
        List<Attr> rest = new LinkedList<Attr>();
        final int len = map.getLength();
        for (int i = 0; i < len; i++) {
            Attr a = (Attr) map.item(i);
            if (!XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(a.getNamespaceURI())
                &&
                !XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI
                .equals(a.getNamespaceURI())) {
                rest.add(a);
            }
        }
        return new Attributes(sLoc, nNsLoc, rest);
    }

    private static class Attributes {
        private final Attr schemaLocation;
        private final Attr noNamespaceSchemaLocation;
        private final List<Attr> remainingAttributes;
        private Attributes(Attr schemaLocation, Attr noNamespaceSchemaLocation,
                           List<Attr> remainingAttributes) {
            this.schemaLocation = schemaLocation;
            this.noNamespaceSchemaLocation = noNamespaceSchemaLocation;
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

    /**
     * Maps Nodes to their QNames.
     */
    private static final Linqy.Mapper<Node, QName> QNAME_MAPPER =
        new Linqy.Mapper<Node, QName>() {
        public QName map(Node n) { return Nodes.getQName(n); }
    };

    /**
     * Maps Nodes to their NodeInfo equivalent.
     */
    private static final Linqy.Mapper<Node, XPathContext.NodeInfo> TO_NODE_INFO =
        new Linqy.Mapper<Node, XPathContext.NodeInfo>() {
        public XPathContext.NodeInfo map(Node n) {
            return new XPathContext.DOMNodeInfo(n);
        }
    };

    /**
     * Suppresses document-type nodes.
     */
    private static final Predicate<Node> INTERESTING_NODES =
        new Predicate<Node>() {
        public boolean matches(Node n) {
            return n.getNodeType() != Node.DOCUMENT_TYPE_NODE;
        }
    };

}
