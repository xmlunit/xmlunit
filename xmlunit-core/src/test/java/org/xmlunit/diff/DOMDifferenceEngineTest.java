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

import java.util.Arrays;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import org.xmlunit.NullNode;
import org.xmlunit.TestResources;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.util.Convert;
import org.xmlunit.util.Linqy;
import org.xmlunit.util.Predicate;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import static org.junit.Assert.*;

public class DOMDifferenceEngineTest extends AbstractDifferenceEngineTest {

    @Override protected AbstractDifferenceEngine getDifferenceEngine() {
        return new DOMDifferenceEngine();
    }

    private static class DiffExpecter implements ComparisonListener {
        private int invoked = 0;
        private final int expectedInvocations;
        private final ComparisonType type;
        private final boolean withXPath;
        private final String controlXPath;
        private final String testXPath;
        private boolean withParentXPath;
        private String controlParentXPath;
        private String testParentXPath;
        private DiffExpecter(ComparisonType type) {
            this(type, 1);
        }
        private DiffExpecter(ComparisonType type, int expected) {
            this(type, expected, false, null, null);
        }
        private DiffExpecter(ComparisonType type, String controlXPath,
                             String testXPath) {
            this(type, 1, true, controlXPath, testXPath);
        }
        private DiffExpecter(ComparisonType type, int expected,
                             boolean withXPath, String controlXPath,
                             String testXPath) {
            this.type = type;
            this.expectedInvocations = expected;
            this.withXPath = withXPath;
            this.controlXPath = controlXPath;
            this.testXPath = testXPath;
            withParentXPath = withXPath;
            controlParentXPath = getParentXPath(controlXPath);
            testParentXPath = getParentXPath(testXPath);
        }

        public DiffExpecter withParentXPath(String controlParentXPath, String testParentXPath) {
            withParentXPath = true;
            this.controlParentXPath = controlParentXPath;
            this.testParentXPath = testParentXPath;
            return this;
        }

        @Override
        public void comparisonPerformed(Comparison comparison,
                                        ComparisonResult outcome) {
            assertTrue(invoked + " should be less than " + expectedInvocations,
                       invoked < expectedInvocations);
            invoked++;
            assertEquals(type, comparison.getType());
            assertEquals(ComparisonResult.DIFFERENT, outcome);
            if (withXPath) {
                assertEquals("Control XPath", controlXPath,
                             comparison.getControlDetails().getXPath());
                assertEquals("Test XPath", testXPath,
                             comparison.getTestDetails().getXPath());
            }
            if (withParentXPath) {
                assertEquals("Control Parent XPath", controlParentXPath,
                      comparison.getControlDetails().getParentXPath());
                assertEquals("Test Parent XPath", testParentXPath,
                      comparison.getTestDetails().getParentXPath());
            }
        }

        private String getParentXPath(String xPath) {
            if (xPath == null) {
                return null;
            }
            if (xPath.equals("/") || xPath.equals("")) {
                return "";
            }
            int i = xPath.lastIndexOf('/');
            if (i == xPath.indexOf('/')) {
                return "/";
            }
            return i >= 0 ? xPath.substring(0, i) : xPath;
        }
    }

    private Document doc;

    @Before public void createDoc() throws Exception {
        doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .newDocument();
    }

    @Test public void diffExpecterParentXPath() {
        DiffExpecter ex = new DiffExpecter(ComparisonType.ATTR_NAME_LOOKUP);
        assertEquals("/bla/blubb", ex.getParentXPath("/bla/blubb/x[1]"));
        assertEquals("/bla/blubb", ex.getParentXPath("/bla/blubb/@attr"));
        assertEquals("/", ex.getParentXPath("/bla[1]"));
        assertEquals("/", ex.getParentXPath("/@attr"));
        assertEquals("", ex.getParentXPath("/"));
        assertEquals("", ex.getParentXPath(""));
        assertEquals(null, ex.getParentXPath(null));
    }

    @Test public void compareXPathOfDifferentRootElements() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.ELEMENT_TAG_NAME,
                                           "/x[1]", "/y[1]");
        d.addDifferenceListener(ex);
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);
        d.compare(new DOMSource(doc.createElement("x")),
                  new DOMSource(doc.createElement("y")));
        assertEquals(1, ex.invoked);
    }

    @Test public void compareNodesOfDifferentType() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.NODE_TYPE);
        d.addDifferenceListener(ex);
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(doc.createElement("x"), new XPathContext(),
                                    doc.createComment("x"), new XPathContext()));
        assertEquals(1, ex.invoked);
    }

    @Test public void compareNodesWithoutNS() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.NODE_TYPE, 0);
        d.addDifferenceListener(ex);
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);
        assertEquals(wrap(ComparisonResult.EQUAL),
                     d.compareNodes(doc.createElement("x"), new XPathContext(),
                                    doc.createElement("x"), new XPathContext()));
        assertEquals(0, ex.invoked);
    }

    @Test public void compareNodesDifferentNS() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.NAMESPACE_URI);
        d.addDifferenceListener(ex);
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(doc.createElementNS("x", "y"),
                                    new XPathContext(),
                                    doc.createElementNS("z", "y"),
                                    new XPathContext()));
        assertEquals(1, ex.invoked);
    }

    @Test public void compareNodesDifferentPrefix() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.NAMESPACE_PREFIX);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(new DifferenceEvaluator() {
                public ComparisonResult evaluate(Comparison comparison,
                                                 ComparisonResult outcome) {
                    if (comparison.getType()
                        == ComparisonType.NAMESPACE_PREFIX) {
                        assertEquals(ComparisonResult.DIFFERENT, outcome);
                        return ComparisonResult.DIFFERENT;
                    }
                    assertEquals(ComparisonResult.EQUAL, outcome);
                    return ComparisonResult.EQUAL;
                }
            });
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(doc.createElementNS("x", "x:y"),
                                    new XPathContext(),
                                    doc.createElementNS("x", "z:y"),
                                    new XPathContext()));
        assertEquals(1, ex.invoked);
    }

    @Test public void compareNodesDifferentNumberOfChildren() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex =
            new DiffExpecter(ComparisonType.CHILD_NODELIST_LENGTH, 2);
        d.addDifferenceListener(ex);
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);
        Element e1 = doc.createElement("x");
        Element e2 = doc.createElement("x");
        assertEquals(wrap(ComparisonResult.EQUAL),
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
        e1.appendChild(doc.createElement("x"));
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
        assertEquals(1, ex.invoked);
        e2.appendChild(doc.createElement("x"));
        assertEquals(wrap(ComparisonResult.EQUAL),
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
        e2.appendChild(doc.createElement("x"));
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
        assertEquals(2, ex.invoked);
    }

    @Test public void compareCharacterData() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.TEXT_VALUE, 9);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(new DifferenceEvaluator() {
                public ComparisonResult evaluate(Comparison comparison,
                                                 ComparisonResult outcome) {
                    if (comparison.getType() == ComparisonType.NODE_TYPE) {
                        if (outcome == ComparisonResult.EQUAL
                            || (
                                comparison.getControlDetails()
                                .getTarget() instanceof CharacterData
                                &&
                                comparison.getTestDetails()
                                .getTarget() instanceof CharacterData
                                )) {
                            return ComparisonResult.EQUAL;
                        }
                    }
                    return outcome;
                }
            });
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);

        Comment fooComment = doc.createComment("foo");
        Comment barComment = doc.createComment("bar");
        Text fooText = doc.createTextNode("foo");
        Text barText = doc.createTextNode("bar");
        CDATASection fooCDATASection = doc.createCDATASection("foo");
        CDATASection barCDATASection = doc.createCDATASection("bar");

        assertEquals(wrap(ComparisonResult.EQUAL),
                     d.compareNodes(fooComment, new XPathContext(),
                                    fooComment, new XPathContext()));
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(fooComment, new XPathContext(),
                                    barComment, new XPathContext()));
        assertEquals(wrap(ComparisonResult.EQUAL),
                     d.compareNodes(fooText, new XPathContext(),
                                    fooText, new XPathContext()));
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(fooText, new XPathContext(),
                                    barText, new XPathContext()));
        assertEquals(wrap(ComparisonResult.EQUAL),
                     d.compareNodes(fooCDATASection, new XPathContext(),
                                    fooCDATASection, new XPathContext()));
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(fooCDATASection, new XPathContext(),
                                    barCDATASection, new XPathContext()));

        assertEquals(wrap(ComparisonResult.EQUAL),
                     d.compareNodes(fooComment, new XPathContext(),
                                    fooText, new XPathContext()));
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(fooComment, new XPathContext(),
                                    barText, new XPathContext()));
        assertEquals(wrap(ComparisonResult.EQUAL),
                     d.compareNodes(fooComment, new XPathContext(),
                                    fooCDATASection, new XPathContext()));
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(fooComment, new XPathContext(),
                                    barCDATASection, new XPathContext()));
        assertEquals(wrap(ComparisonResult.EQUAL),
                     d.compareNodes(fooText, new XPathContext(),
                                    fooComment, new XPathContext()));
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(fooText, new XPathContext(),
                                    barComment, new XPathContext()));
        assertEquals(wrap(ComparisonResult.EQUAL),
                     d.compareNodes(fooText, new XPathContext(),
                                    fooCDATASection, new XPathContext()));
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(fooText, new XPathContext(),
                                    barCDATASection, new XPathContext()));
        assertEquals(wrap(ComparisonResult.EQUAL),
                     d.compareNodes(fooCDATASection, new XPathContext(),
                                    fooText, new XPathContext()));
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(fooCDATASection, new XPathContext(),
                                    barText, new XPathContext()));
        assertEquals(wrap(ComparisonResult.EQUAL),
                     d.compareNodes(fooCDATASection, new XPathContext(),
                                    fooComment, new XPathContext()));
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(fooCDATASection, new XPathContext(),
                                    barComment, new XPathContext()));
        assertEquals(9, ex.invoked);
    }

    @Test public void compareProcessingInstructions() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.PROCESSING_INSTRUCTION_TARGET);
        d.addDifferenceListener(ex);
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);

        ProcessingInstruction foo1 = doc.createProcessingInstruction("foo", "1");
        ProcessingInstruction bar1 = doc.createProcessingInstruction("bar", "1");
        assertEquals(wrap(ComparisonResult.EQUAL),
                     d.compareNodes(foo1, new XPathContext(),
                                    foo1, new XPathContext()));
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(foo1, new XPathContext(),
                                    bar1, new XPathContext()));
        assertEquals(1, ex.invoked);

        d = new DOMDifferenceEngine();
        ex = new DiffExpecter(ComparisonType.PROCESSING_INSTRUCTION_DATA);
        d.addDifferenceListener(ex);
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);
        ProcessingInstruction foo2 = doc.createProcessingInstruction("foo", "2");
        assertEquals(wrap(ComparisonResult.EQUAL),
                     d.compareNodes(foo1, new XPathContext(),
                                    foo1, new XPathContext()));
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(foo1, new XPathContext(),
                                    foo2, new XPathContext()));
        assertEquals(1, ex.invoked);
    }

    @Test public void compareDocuments() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.HAS_DOCTYPE_DECLARATION);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(new DifferenceEvaluator() {
                public ComparisonResult evaluate(Comparison comparison,
                                                 ComparisonResult outcome) {
                    if (comparison.getType()
                        == ComparisonType.CHILD_NODELIST_LENGTH) {
                        assertEquals(ComparisonResult.DIFFERENT, outcome);
                        // downgrade so we get to see the HAS_DOCTYPE_DECLARATION
                        // difference
                        return ComparisonResult.EQUAL;
                    }
                    if (comparison.getType()
                        == ComparisonType.HAS_DOCTYPE_DECLARATION) {
                        assertEquals(ComparisonResult.DIFFERENT, outcome);
                        return ComparisonResult.DIFFERENT;
                    }
                    assertEquals("Expected EQUAL for " + comparison.getType()
                                 + " comparison.",
                                 ComparisonResult.EQUAL, outcome);
                    return ComparisonResult.EQUAL;
                }
            });
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);
        d.setNodeFilter(NodeFilters.AcceptAll);
        Document d1 = Convert.toDocument(Input.fromString("<Book/>").build());
        Document d2 =
            Convert.toDocument(Input.fromString("<!DOCTYPE Book PUBLIC "
                    + "\"XMLUNIT/TEST/PUB\" "
                    + "\"" + TestResources.BOOK_DTD
                    + "\">"
                    + "<Book/>")
                               .build());
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(d1, new XPathContext(),
                                    d2, new XPathContext()));
        assertEquals(1, ex.invoked);

        d = new DOMDifferenceEngine();
        ex = new DiffExpecter(ComparisonType.XML_VERSION);
        d.addDifferenceListener(ex);
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);

        d1 = Convert.toDocument(Input.fromString("<?xml version=\"1.0\""
                + " encoding=\"UTF-8\"?>"
                + "<Book/>").build());
        d2 = Convert.toDocument(Input.fromString("<?xml version=\"1.1\""
                + " encoding=\"UTF-8\"?>"
                + "<Book/>").build());
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(d1, new XPathContext(),
                                    d2, new XPathContext()));
        assertEquals(1, ex.invoked);

        d = new DOMDifferenceEngine();
        ex = new DiffExpecter(ComparisonType.XML_STANDALONE);
        d.addDifferenceListener(ex);
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);

        d1 = Convert.toDocument(Input.fromString("<?xml version=\"1.0\""
                + " standalone=\"yes\"?>"
                + "<Book/>").build());
        d2 = Convert.toDocument(Input.fromString("<?xml version=\"1.0\""
                + " standalone=\"no\"?>"
                + "<Book/>").build());
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(d1, new XPathContext(),
                                    d2, new XPathContext()));
        assertEquals(1, ex.invoked);

        d = new DOMDifferenceEngine();
        ex = new DiffExpecter(ComparisonType.XML_ENCODING);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(new DifferenceEvaluator() {
                public ComparisonResult evaluate(Comparison comparison,
                                                 ComparisonResult outcome) {
                    if (comparison.getType()
                        == ComparisonType.XML_ENCODING) {
                        assertEquals(ComparisonResult.DIFFERENT, outcome);
                        return ComparisonResult.DIFFERENT;
                    }
                    assertEquals(ComparisonResult.EQUAL, outcome);
                    return ComparisonResult.EQUAL;
                }
            });
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);

        d1 = Convert.toDocument(Input.fromString("<?xml version=\"1.0\""
                + " encoding=\"UTF-8\"?>"
                + "<Book/>").build());
        d2 = Convert.toDocument(Input.fromString("<?xml version=\"1.0\""
                + " encoding=\"UTF-16\"?>"
                + "<Book/>").build());
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(d1, new XPathContext(),
                                    d2, new XPathContext()));
        assertEquals(1, ex.invoked);
    }

    @Test
    public void nodeFilterAppliesToDocTypes() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.HAS_DOCTYPE_DECLARATION);
        d.addDifferenceListener(ex);
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);
        Document d1 = Convert.toDocument(Input.fromString("<Book/>").build());
        Document d2 =
            Convert.toDocument(Input.fromString("<!DOCTYPE Book PUBLIC "
                    + "\"XMLUNIT/TEST/PUB\" "
                    + "\"" + TestResources.BOOK_DTD
                    + "\">"
                    + "<Book/>")
                               .build());
        assertEquals(wrap(ComparisonResult.EQUAL),
                     d.compareNodes(d1, new XPathContext(),
                                    d2, new XPathContext()));
        assertEquals(0, ex.invoked);
    }

    private static class DocType extends NullNode implements DocumentType {
        private final String name, publicId, systemId;
        private DocType(String name, String publicId, String systemId) {
            this.name = name;
            this.publicId = publicId;
            this.systemId = systemId;
        }
        @Override public short getNodeType() {
            return Node.DOCUMENT_TYPE_NODE;
        }
        public NamedNodeMap getEntities() {
            return null;
        }
        public String getInternalSubset() {
            return null;
        }
        public String getName() {
            return name;
        }
        public NamedNodeMap getNotations() {
            return null;
        }
        public String getPublicId() {
            return publicId;
        }
        public String getSystemId() {
            return systemId;
        }
    }

    @Test public void compareDocTypes() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.DOCTYPE_NAME);
        d.addDifferenceListener(ex);
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);
        DocumentType dt1 = new DocType("name", "pub", "system");
        DocumentType dt2 = new DocType("name2", "pub", "system");
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(dt1, new XPathContext(),
                                    dt2, new XPathContext()));
        assertEquals(1, ex.invoked);

        d = new DOMDifferenceEngine();
        ex = new DiffExpecter(ComparisonType.DOCTYPE_PUBLIC_ID);
        d.addDifferenceListener(ex);
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);
        dt2 = new DocType("name", "pub2", "system");
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(dt1, new XPathContext(),
                                    dt2, new XPathContext()));
        assertEquals(1, ex.invoked);

        d = new DOMDifferenceEngine();
        ex = new DiffExpecter(ComparisonType.DOCTYPE_SYSTEM_ID);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(new DifferenceEvaluator() {
                public ComparisonResult evaluate(Comparison comparison,
                                                 ComparisonResult outcome) {
                    if (comparison.getType()
                        == ComparisonType.DOCTYPE_SYSTEM_ID) {
                        assertEquals(ComparisonResult.DIFFERENT, outcome);
                        return ComparisonResult.DIFFERENT;
                    }
                    assertEquals(ComparisonResult.EQUAL, outcome);
                    return ComparisonResult.EQUAL;
                }
            });
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);
        dt2 = new DocType("name", "pub", "system2");
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(dt1, new XPathContext(),
                                    dt2, new XPathContext()));
        assertEquals(1, ex.invoked);
    }

    @Test public void compareElements() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.ELEMENT_TAG_NAME);
        d.addDifferenceListener(ex);
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);
        Element e1 = doc.createElement("foo");
        Element e2 = doc.createElement("foo");
        Element e3 = doc.createElement("bar");
        assertEquals(wrap(ComparisonResult.EQUAL),
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(e1, new XPathContext(),
                                    e3, new XPathContext()));
        assertEquals(1, ex.invoked);

        d = new DOMDifferenceEngine();
        ex = new DiffExpecter(ComparisonType.ELEMENT_NUM_ATTRIBUTES);
        e1.setAttribute("attr1", "value1");
        d.addDifferenceListener(ex);
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
        assertEquals(1, ex.invoked);

        d = new DOMDifferenceEngine();
        ex = new DiffExpecter(ComparisonType.ATTR_NAME_LOOKUP,
                              "/@attr1", "/");
        e2.setAttributeNS("urn:xmlunit:test", "attr1", "value1");
        d.addDifferenceListener(ex);
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
        assertEquals(1, ex.invoked);


        d = new DOMDifferenceEngine();
        d.addDifferenceListener(new ComparisonListener() {
                public void comparisonPerformed(Comparison comparison,
                                                ComparisonResult outcome) {
                    fail("unexpected Comparison of type " + comparison.getType()
                         + " with outcome " + outcome + " and values '"
                         + comparison.getControlDetails().getValue()
                         + "' and '"
                         + comparison.getTestDetails().getValue() + "'");
                }
            });
        e1.setAttributeNS("urn:xmlunit:test", "attr1", "value1");
        e2.setAttributeNS(null, "attr1", "value1");
        d.addDifferenceListener(ex);
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);
        assertEquals(wrap(ComparisonResult.EQUAL),
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
    }

    @Test public void compareAttributes() {
        Attr a1 = doc.createAttribute("foo");
        Attr a2 = doc.createAttribute("foo");

        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.ATTR_VALUE_EXPLICITLY_SPECIFIED);
        /* Can't reset "explicitly set" state for Documents created via API
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.Accept);
        a2.setValue("");
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(a1, new XPathContext(),
                                    a2, new XPathContext()));
        assertEquals(1, ex.invoked);

        d = new DOMDifferenceEngine();
        */
        ex = new DiffExpecter(ComparisonType.ATTR_VALUE);
        d.addDifferenceListener(ex);
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);
        Attr a3 = doc.createAttribute("foo");
        a1.setValue("foo");
        a2.setValue("foo");
        a3.setValue("bar");
        assertEquals(wrap(ComparisonResult.EQUAL),
                     d.compareNodes(a1, new XPathContext(),
                                    a2, new XPathContext()));
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(a1, new XPathContext(),
                                    a3, new XPathContext()));
        assertEquals(1, ex.invoked);
    }

    @Test public void compareAttributesWithAttributeFilter() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        d.setAttributeFilter(new Predicate<Attr>() {
                @Override
                public boolean test(Attr a) {
                    return "x".equals(a.getName());
                }
            });
        DiffExpecter ex = new DiffExpecter(ComparisonType.ATTR_VALUE);
        d.addDifferenceListener(ex);
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);

        Element e1 = doc.createElement("foo");
        e1.setAttribute("x", "1");
        e1.setAttribute("a", "xxx");
        Element e2 = doc.createElement("foo");
        e2.setAttribute("x", "1");
        e2.setAttribute("b", "xxx");
        e2.setAttribute("c", "xxx");
        Element e3 = doc.createElement("foo");
        e3.setAttribute("x", "3");

        assertEquals(wrap(ComparisonResult.EQUAL),
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(e1, new XPathContext(),
                                    e3, new XPathContext()));
        assertEquals(1, ex.invoked);
    }

    @Test public void compareNodesWithNodeFilter() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        d.setNodeFilter(new Predicate<Node>() {
                @Override
                public boolean test(Node n) {
                    return "x".equals(n.getNodeName())
                        || "foo".equals(n.getNodeName());
                }
            });
        DiffExpecter ex = new DiffExpecter(ComparisonType.CHILD_NODELIST_LENGTH,
                                           "/", "/");
        d.addDifferenceListener(ex);
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);

        Element e1 = doc.createElement("foo");
        e1.appendChild(doc.createElement("x"));
        e1.appendChild(doc.createElement("y"));
        Element e2 = doc.createElement("foo");
        e2.appendChild(doc.createElement("x"));
        e2.appendChild(doc.createElement("y"));
        e2.appendChild(doc.createElement("z"));
        Element e3 = doc.createElement("foo");
        e3.appendChild(doc.createElement("y"));

        assertEquals(wrap(ComparisonResult.EQUAL),
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(e1, new XPathContext(),
                                    e3, new XPathContext()));
        assertEquals(1, ex.invoked);
    }

    @Test public void naiveRecursion() {
        Element e1 = doc.createElement("foo");
        Element e2 = doc.createElement("foo");
        Element c1 = doc.createElement("bar");
        e1.appendChild(c1);
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.CHILD_LOOKUP,
                                           "/bar[1]", null).withParentXPath("/", "/");
        d.addDifferenceListener(ex);
        DifferenceEvaluator ev = new DifferenceEvaluator() {
                public ComparisonResult evaluate(Comparison comparison,
                                                 ComparisonResult outcome) {
                    if (comparison.getType() == ComparisonType.CHILD_NODELIST_LENGTH) {
                        return ComparisonResult.EQUAL;
                    }
                    return outcome;
                }
            };
        d.setDifferenceEvaluator(ev);
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
        assertEquals(1, ex.invoked);

        // symmetric?
        d = new DOMDifferenceEngine();
        ex = new DiffExpecter(ComparisonType.CHILD_LOOKUP, null, "/bar[1]").withParentXPath("/", "/");
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(ev);
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(e2, new XPathContext(),
                                    e1, new XPathContext()));
        assertEquals(1, ex.invoked);

        Element c2 = doc.createElement("bar");
        e2.appendChild(c2);
        d = new DOMDifferenceEngine();
        ex = new DiffExpecter(ComparisonType.CHILD_LOOKUP);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(ev);
        assertEquals(wrap(ComparisonResult.EQUAL),
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
        assertEquals(wrap(ComparisonResult.EQUAL),
                     d.compareNodes(e2, new XPathContext(),
                                    e1, new XPathContext()));
        assertEquals(0, ex.invoked);
    }

    @Test
    /**
     * @see "https://sourceforge.net/p/xmlunit/discussion/73273/thread/92c980ec5b/"
     */
    public void sourceforgeForumThread92c980ec5b() {
        Element gp1 = doc.createElement("grandparent");
        Element p1_0 = doc.createElement("parent");
        p1_0.setAttribute("id", "0");
        gp1.appendChild(p1_0);
        Element p1_1 = doc.createElement("parent");
        p1_1.setAttribute("id", "1");
        gp1.appendChild(p1_1);
        Element c1_1 = doc.createElement("child");
        c1_1.setAttribute("id", "1");
        p1_1.appendChild(c1_1);

        Element gp2 = doc.createElement("grandparent");
        Element p2_1 = doc.createElement("parent");
        p2_1.setAttribute("id", "1");
        gp2.appendChild(p2_1);
        Element c2_1 = doc.createElement("child");
        c2_1.setAttribute("id", "1");
        p2_1.appendChild(c2_1);
        Element c2_2 = doc.createElement("child");
        c2_2.setAttribute("id", "2");
        p2_1.appendChild(c2_2);

        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.CHILD_LOOKUP,
                                           null, "/grandparent[1]/parent[1]/child[2]")
            .withParentXPath("/grandparent[1]/parent[2]", "/grandparent[1]/parent[1]");
        d.addDifferenceListener(ex);
        DifferenceEvaluator ev = new DifferenceEvaluator() {
                public ComparisonResult evaluate(Comparison comparison,
                                                 ComparisonResult outcome) {
                    if (comparison.getType() == ComparisonType.CHILD_NODELIST_LENGTH
                        || comparison.getType() == ComparisonType.CHILD_NODELIST_SEQUENCE) {
                        return ComparisonResult.EQUAL;
                    }
                    if (comparison.getType() == ComparisonType.CHILD_LOOKUP
                        && comparison.getTestDetails().getTarget() == null) {
                        return ComparisonResult.EQUAL;
                    }
                    return outcome;
                }
            };
        d.setDifferenceEvaluator(ev);
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);
        d.setNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndAllAttributes));
        d.setNodeFilter(new Predicate<Node>() {
            @Override
            public boolean test(Node n) {
                return n.getNodeType() != Node.DOCUMENT_TYPE_NODE &&
                    !("parent".equals(n.getNodeName())
                      && "0".equals(n.getAttributes().getNamedItem("id").getNodeValue()));
            }
        });
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(gp1, new XPathContext(gp1),
                                    gp2, new XPathContext(gp2)));
        assertEquals(1, ex.invoked);
    }

    @Test public void textAndCDataMatchRecursively() {
        Element e1 = doc.createElement("foo");
        Element e2 = doc.createElement("foo");
        Text fooText = doc.createTextNode("foo");
        e1.appendChild(fooText);
        CDATASection fooCDATASection = doc.createCDATASection("foo");
        e2.appendChild(fooCDATASection);
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        assertEquals(wrap(ComparisonResult.EQUAL),
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
        assertEquals(wrap(ComparisonResult.EQUAL),
                     d.compareNodes(e2, new XPathContext(),
                                    e1, new XPathContext()));
    }

    @Test public void recursionUsesElementSelector() {
        Element e1 = doc.createElement("foo");
        Element e2 = doc.createElement("foo");
        Element e3 = doc.createElement("bar");
        e1.appendChild(e3);
        Element e4 = doc.createElement("baz");
        e2.appendChild(e4);
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.ELEMENT_TAG_NAME,
                                           "/bar[1]", "/baz[1]");
        d.addDifferenceListener(ex);
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
        assertEquals(1, ex.invoked);

        d = new DOMDifferenceEngine();
        d.setNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byName));
        ex = new DiffExpecter(ComparisonType.CHILD_LOOKUP, "/bar[1]", null).withParentXPath("/", "/");
        d.addDifferenceListener(ex);
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
        assertEquals(1, ex.invoked);
    }

    @Test public void schemaLocationDifferences() {
        Element e1 = doc.createElement("foo");
        Element e2 = doc.createElement("foo");
        e1.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance",
                          "schemaLocation", "somewhere");
        e2.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance",
                          "schemaLocation", "somewhere else");

        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.SCHEMA_LOCATION);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(new DifferenceEvaluator() {
                public ComparisonResult evaluate(Comparison comparison,
                                                 ComparisonResult outcome) {
                    if (comparison.getType() == ComparisonType.SCHEMA_LOCATION) {
                        assertEquals(ComparisonResult.DIFFERENT, outcome);
                        return ComparisonResult.DIFFERENT;
                    }
                    assertEquals(ComparisonResult.EQUAL, outcome);
                    return ComparisonResult.EQUAL;
                }
            });
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
        assertEquals(1, ex.invoked);

        e1 = doc.createElement("foo");
        e2 = doc.createElement("foo");
        e1.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance",
                          "noNamespaceSchemaLocation", "somewhere");
        e2.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance",
                          "noNamespaceSchemaLocation", "somewhere else");
        d = new DOMDifferenceEngine();
        ex = new DiffExpecter(ComparisonType.NO_NAMESPACE_SCHEMA_LOCATION);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(new DifferenceEvaluator() {
                public ComparisonResult evaluate(Comparison comparison,
                                                 ComparisonResult outcome) {
                    if (comparison.getType() == ComparisonType.NO_NAMESPACE_SCHEMA_LOCATION) {
                        assertEquals(ComparisonResult.DIFFERENT, outcome);
                        return ComparisonResult.DIFFERENT;
                    }
                    assertEquals(ComparisonResult.EQUAL, outcome);
                    return ComparisonResult.EQUAL;
                }
            });
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
        assertEquals(1, ex.invoked);
    }

    @Test public void compareElementsNS() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.ELEMENT_TAG_NAME);
        d.addDifferenceListener(ex);
        DifferenceEvaluator ev = new DifferenceEvaluator() {
                public ComparisonResult evaluate(Comparison comparison,
                                                 ComparisonResult outcome) {
                    if (comparison.getType() == ComparisonType.NAMESPACE_PREFIX) {
                        return ComparisonResult.EQUAL;
                    }
                    return outcome;
                }
            };
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);
        d.setDifferenceEvaluator(ev);
        Element e1 = doc.createElementNS("urn:xmlunit:test", "foo");
        e1.setPrefix("p1");
        Element e2 = doc.createElementNS("urn:xmlunit:test", "foo");
        e2.setPrefix("p2");
        assertEquals(wrap(ComparisonResult.EQUAL),
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
        assertEquals(0, ex.invoked);
    }

    @Test public void childNodeListSequence() {
        Element e1 = doc.createElement("foo");
        Element e3 = doc.createElement("bar");
        Element e4 = doc.createElement("baz");
        e1.appendChild(e3);
        e1.appendChild(e4);

        Element e2 = doc.createElement("foo");
        Element e5 = doc.createElement("bar");
        Element e6 = doc.createElement("baz");
        e2.appendChild(e6);
        e2.appendChild(e5);

        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.CHILD_NODELIST_SEQUENCE,
                                           "/bar[1]", "/bar[1]");
        d.addDifferenceListener(ex);
        DifferenceEvaluator ev = new DifferenceEvaluator() {
                public ComparisonResult evaluate(Comparison comparison,
                                                 ComparisonResult outcome) {
                    if (outcome != ComparisonResult.EQUAL
                        && comparison.getType() == ComparisonType.CHILD_NODELIST_SEQUENCE) {
                        return ComparisonResult.DIFFERENT;
                    }
                    return outcome;
                }
            };
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);
        d.setDifferenceEvaluator(ev);
        d.setNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byName));

        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
        assertEquals(1, ex.invoked);
    }

    @Test public void xsiTypesWithDifferentPrefixes() {
        Document d1 =
            documentForString("<foo xsi:type='p1:Foo'"
                              + " xmlns:p1='urn:xmlunit:test'"
                              + " xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'"
                              + "/>");
        Document d2 =
            documentForString("<foo xsi:type='p2:Foo'"
                              + " xmlns:p2='urn:xmlunit:test'"
                              + " xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'"
                              + "/>");
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.ATTR_VALUE);
        d.addDifferenceListener(ex);
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);
        assertEquals(wrap(ComparisonResult.EQUAL),
                     d.compareNodes(d1, new XPathContext(),
                                    d2, new XPathContext()));
    }

    @Test public void xsiTypesWithDefaultNamespace() {
        Document d1 =
            documentForString("<a:foo xsi:type='Foo'"
                              + " xmlns='urn:xmlunit:test'"
                              + " xmlns:a='urn:xmlunit:test2'"
                              + " xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'"
                              + "/>");
        Document d2 =
            documentForString("<a:foo xsi:type='p2:Foo'"
                              + " xmlns:p2='urn:xmlunit:test'"
                              + " xmlns:a='urn:xmlunit:test2'"
                              + " xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'"
                              + "/>");
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.ATTR_VALUE);
        d.addDifferenceListener(ex);
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);
        assertEquals(wrap(ComparisonResult.EQUAL),
                     d.compareNodes(d1, new XPathContext(),
                                    d2, new XPathContext()));
    }

    @Test public void xsiTypesWithDifferentLocalNames() {
        Document d1 =
            documentForString("<foo xsi:type='p1:Bar'"
                              + " xmlns:p1='urn:xmlunit:test'"
                              + " xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'"
                              + "/>");
        Document d2 =
            documentForString("<foo xsi:type='p1:Foo'"
                              + " xmlns:p1='urn:xmlunit:test'"
                              + " xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'"
                              + "/>");
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.ATTR_VALUE);
        d.addDifferenceListener(ex);
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(d1, new XPathContext(),
                                    d2, new XPathContext()));
    }

    @Test public void xsiTypesWithDifferentNamespaceURIs() {
        Document d1 =
            documentForString("<foo xsi:type='p1:Foo'"
                              + " xmlns:p1='urn:xmlunit:test'"
                              + " xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'"
                              + "/>");
        Document d2 =
            documentForString("<foo xsi:type='p1:Foo'"
                              + " xmlns:p1='urn:xmlunit:test2'"
                              + " xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'"
                              + "/>");
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.ATTR_VALUE);
        d.addDifferenceListener(ex);
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(d1, new XPathContext(),
                                    d2, new XPathContext()));
    }

    @Test public void xsiTypesWithNamespaceDeclarationOnDifferentLevels() {
        Document d1 =
            documentForString("<bar xmlns:p1='urn:xmlunit:test'>"
                              + "<foo xsi:type='p1:Foo'"
                              + " xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'"
                              + "/></bar>");
        Document d2 =
            documentForString("<bar><foo xsi:type='p1:Foo'"
                              + " xmlns:p1='urn:xmlunit:test'"
                              + " xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'"
                              + "/></bar>");
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.ATTR_VALUE);
        d.addDifferenceListener(ex);
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);
        assertEquals(wrap(ComparisonResult.EQUAL),
                     d.compareNodes(d1, new XPathContext(),
                                    d2, new XPathContext()));
    }

    @Test public void xsiNil() {
        Document d1 =
            documentForString("<foo xsi:nil='true'"
                              + " xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'"
                              + "/>");
        Document d2 =
            documentForString("<foo xsi:nil='false'"
                              + " xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'"
                              + "/>");
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.ATTR_VALUE);
        d.addDifferenceListener(ex);
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(d1, new XPathContext(),
                                    d2, new XPathContext()));
    }

    @Test
    public void shouldDetectCommentInPrelude() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        Document d1 = Convert.toDocument(Input.fromFile(TestResources.TEST_RESOURCE_DIR
                                                        + "BookXsdGenerated.xml")
                                         .build());
        Document d2 = Convert.toDocument(Input.fromFile(TestResources.TEST_RESOURCE_DIR
                                                        + "BookXsdGeneratedWithComment.xml")
                                         .build());
        DiffExpecter ex = new DiffExpecter(ComparisonType.CHILD_NODELIST_LENGTH,
                                           "/", "/");
        d.addDifferenceListener(ex);
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(d1, new XPathContext(),
                                    d2, new XPathContext()));
    }

    @Test
    public void shouldDetectMissingXsiType() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        Document d1 = Convert.toDocument(Input.fromString("<doc xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                                                          + "<effectiveTime xsi:type=\"IVL_TS\"></effectiveTime></doc>")
                                         .build());
        Document d2 = Convert.toDocument(Input.fromString("<doc xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                                                          + "<effectiveTime></effectiveTime></doc>")
                                         .build());

        DiffExpecter ex = new DiffExpecter(ComparisonType.ATTR_NAME_LOOKUP,
                                           "/doc[1]/effectiveTime[1]/@type",
                                           "/doc[1]/effectiveTime[1]");
        d.addDifferenceListener(ex);
        d.setComparisonController(ComparisonControllers.StopWhenDifferent);
        assertEquals(wrapAndStop(ComparisonResult.DIFFERENT),
                     d.compareNodes(d1, new XPathContext(),
                                    d2, new XPathContext()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void cantUseNullDocumentBuilderFactoryInSetter() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        d.setDocumentBuilderFactory(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cantUseNullDocumentBuilderFactoryInConstructor() {
        DOMDifferenceEngine d = new DOMDifferenceEngine(null);
    }

    // https://github.com/xmlunit/xmlunit.net/issues/22
    @Test
    public void elementsWithDifferentPrefixesAreSimilar() {
        Diff diff = DiffBuilder.compare("<Root xmlns:x='http://example.org'><x:Elem/></Root>")
            .withTest("<Root xmlns:y='http://example.org'><y:Elem/></Root>")
            .build();
        assertEquals(1, Linqy.count(diff.getDifferences()));
        assertEquals(ComparisonResult.SIMILAR, diff.getDifferences().iterator().next().getResult());
        assertEquals(ComparisonType.NAMESPACE_PREFIX, diff.getDifferences().iterator().next().getComparison().getType());
    }

    @Test
    public void attributesWithDifferentPrefixesAreSimilar() {
        Diff diff = DiffBuilder.compare("<Root xmlns:x='http://example.org' x:Attr='1'/>")
            .withTest("<Root xmlns:y='http://example.org' y:Attr='1'/>")
            .build();
        assertEquals(1, Linqy.count(diff.getDifferences()));
        assertEquals(ComparisonResult.SIMILAR, diff.getDifferences().iterator().next().getResult());
        assertEquals(ComparisonType.NAMESPACE_PREFIX, diff.getDifferences().iterator().next().getComparison().getType());
    }

    @Test
    public void xPathKnowsAboutNodeFiltersForUnmatchedControlNodes() {
        final Diff diff = DiffBuilder.compare("<Document><Section><Binding /><Binding /><Finding /><Finding /><Finding /><Finding /><Finding /><Finding /><Finding /></Section></Document>")
            .withTest("<Document><Section><Binding /><Binding /><Finding /><Finding /><Finding /><Finding /><Finding /><Finding /></Section></Document>")
            .ignoreWhitespace()
            .withNodeFilter(new Predicate<Node>() {
                @Override
                public boolean test(final Node node) {
                    return Arrays.asList("Document", "Section", "Finding").contains(node.getNodeName());
                }
            })
            .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText))
            .build();
        final List<Difference> differences = Linqy.asList(diff.getDifferences());
        assertEquals(2, differences.size());
        assertEquals(ComparisonType.CHILD_NODELIST_LENGTH, differences.get(0).getComparison().getType());
        assertEquals("/Document[1]/Section[1]", differences.get(0).getComparison().getControlDetails().getXPath());
        assertEquals("/Document[1]/Section[1]", differences.get(0).getComparison().getTestDetails().getXPath());
        assertEquals(ComparisonType.CHILD_LOOKUP, differences.get(1).getComparison().getType());
        assertEquals("/Document[1]/Section[1]/Finding[7]", differences.get(1).getComparison().getControlDetails().getXPath());
        assertNull(differences.get(1).getComparison().getTestDetails().getXPath());
        assertEquals("/Document[1]/Section[1]", differences.get(1).getComparison().getTestDetails().getParentXPath());
    }

    @Test
    public void xPathKnowsAboutNodeFiltersForUnmatchedTestNodes() {
        final Diff diff = DiffBuilder.compare("<Document><Section><Binding /><Binding /><Finding /><Finding /><Finding /><Finding /><Finding /><Finding /></Section></Document>")
            .withTest("<Document><Section><Binding /><Binding /><Finding /><Finding /><Finding /><Finding /><Finding /><Finding /><Finding /></Section></Document>")
            .ignoreWhitespace()
            .withNodeFilter(new Predicate<Node>() {
                @Override
                public boolean test(final Node node) {
                    return Arrays.asList("Document", "Section", "Finding").contains(node.getNodeName());
                }
            })
            .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText))
            .build();
        final List<Difference> differences = Linqy.asList(diff.getDifferences());
        assertEquals(2, differences.size());
        assertEquals(ComparisonType.CHILD_NODELIST_LENGTH, differences.get(0).getComparison().getType());
        assertEquals("/Document[1]/Section[1]", differences.get(0).getComparison().getControlDetails().getXPath());
        assertEquals("/Document[1]/Section[1]", differences.get(0).getComparison().getTestDetails().getXPath());
        assertEquals(ComparisonType.CHILD_LOOKUP, differences.get(1).getComparison().getType());
        assertEquals("/Document[1]/Section[1]/Finding[7]", differences.get(1).getComparison().getTestDetails().getXPath());
        assertNull(differences.get(1).getComparison().getControlDetails().getXPath());
        assertEquals("/Document[1]/Section[1]", differences.get(1).getComparison().getControlDetails().getParentXPath());
    }

    private Document documentForString(String s) {
        return Convert.toDocument(Input.fromString(s).build());
    }
}
