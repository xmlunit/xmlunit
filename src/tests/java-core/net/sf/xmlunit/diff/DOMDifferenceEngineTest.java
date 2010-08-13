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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import net.sf.xmlunit.NullNode;
import net.sf.xmlunit.TestResources;
import net.sf.xmlunit.builder.Input;
import net.sf.xmlunit.util.Convert;
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
        private DiffExpecter(ComparisonType type) {
            this(type, 1);
        }
        private DiffExpecter(ComparisonType type, int expected) {
            this.type = type;
            this.expectedInvocations = expected;
        }
        public void comparisonPerformed(Comparison comparison,
                                        ComparisonResult outcome) {
            assertTrue(invoked + " should be less than " + expectedInvocations,
                       invoked < expectedInvocations);
            invoked++;
            assertEquals(type, comparison.getType());
            assertEquals(ComparisonResult.CRITICAL, outcome);
        }
    }

    private Document doc;

    @Before public void createDoc() throws Exception {
        doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .newDocument();
    }

    @Test public void compareNodesOfDifferentType() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.NODE_TYPE);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(doc.createElement("x"),
                                    doc.createComment("x")));
        assertEquals(1, ex.invoked);
    }

    @Test public void compareNodesWithoutNS() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.NODE_TYPE, 0);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);
        assertEquals(ComparisonResult.EQUAL,
                     d.compareNodes(doc.createElement("x"),
                                    doc.createElement("x")));
        assertEquals(0, ex.invoked);
    }

    @Test public void compareNodesDifferentNS() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.NAMESPACE_URI);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(doc.createElementNS("x", "y"),
                                    doc.createElementNS("z", "y")));
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
                        return ComparisonResult.CRITICAL;
                    }
                    assertEquals(ComparisonResult.EQUAL, outcome);
                    return ComparisonResult.EQUAL;
                }
            });
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(doc.createElementNS("x", "x:y"),
                                    doc.createElementNS("x", "z:y")));
        assertEquals(1, ex.invoked);
    }

    @Test public void compareNodesDifferentNumberOfChildren() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex =
            new DiffExpecter(ComparisonType.CHILD_NODELIST_LENGTH, 2);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);
        Element e1 = doc.createElement("x");
        Element e2 = doc.createElement("x");
        assertEquals(ComparisonResult.EQUAL, d.compareNodes(e1, e2));
        e1.appendChild(doc.createElement("x"));
        assertEquals(ComparisonResult.CRITICAL, d.compareNodes(e1, e2));
        assertEquals(1, ex.invoked);
        e2.appendChild(doc.createElement("x"));
        assertEquals(ComparisonResult.EQUAL, d.compareNodes(e1, e2));
        e2.appendChild(doc.createElement("x"));
        assertEquals(ComparisonResult.CRITICAL, d.compareNodes(e1, e2));
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
                    return DifferenceEvaluators.DefaultStopWhenDifferent
                        .evaluate(comparison, outcome);
                }
            });

        Comment fooComment = doc.createComment("foo");
        Comment barComment = doc.createComment("bar");
        Text fooText = doc.createTextNode("foo");
        Text barText = doc.createTextNode("bar");
        CDATASection fooCDATASection = doc.createCDATASection("foo");
        CDATASection barCDATASection = doc.createCDATASection("bar");

        assertEquals(ComparisonResult.EQUAL,
                     d.compareNodes(fooComment, fooComment));
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(fooComment, barComment));
        assertEquals(ComparisonResult.EQUAL,
                     d.compareNodes(fooText, fooText));
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(fooText, barText));
        assertEquals(ComparisonResult.EQUAL,
                     d.compareNodes(fooCDATASection, fooCDATASection));
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(fooCDATASection, barCDATASection));

        assertEquals(ComparisonResult.EQUAL,
                     d.compareNodes(fooComment, fooText));
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(fooComment, barText));
        assertEquals(ComparisonResult.EQUAL,
                     d.compareNodes(fooComment, fooCDATASection));
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(fooComment, barCDATASection));
        assertEquals(ComparisonResult.EQUAL,
                     d.compareNodes(fooText, fooComment));
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(fooText, barComment));
        assertEquals(ComparisonResult.EQUAL,
                     d.compareNodes(fooText, fooCDATASection));
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(fooText, barCDATASection));
        assertEquals(ComparisonResult.EQUAL,
                     d.compareNodes(fooCDATASection, fooText));
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(fooCDATASection, barText));
        assertEquals(ComparisonResult.EQUAL,
                     d.compareNodes(fooCDATASection, fooComment));
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(fooCDATASection, barComment));
        assertEquals(9, ex.invoked);
    }

    @Test public void compareProcessingInstructions() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.PROCESSING_INSTRUCTION_TARGET);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);

        ProcessingInstruction foo1 = doc.createProcessingInstruction("foo", "1");
        ProcessingInstruction bar1 = doc.createProcessingInstruction("bar", "1");
        assertEquals(ComparisonResult.EQUAL, d.compareNodes(foo1, foo1));
        assertEquals(ComparisonResult.CRITICAL, d.compareNodes(foo1, bar1));
        assertEquals(1, ex.invoked);

        d = new DOMDifferenceEngine();
        ex = new DiffExpecter(ComparisonType.PROCESSING_INSTRUCTION_DATA);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);
        ProcessingInstruction foo2 = doc.createProcessingInstruction("foo", "2");
        assertEquals(ComparisonResult.EQUAL, d.compareNodes(foo1, foo1));
        assertEquals(ComparisonResult.CRITICAL, d.compareNodes(foo1, foo2));
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
                        return ComparisonResult.EQUAL;
                    }
                    if (comparison.getType()
                        == ComparisonType.HAS_DOCTYPE_DECLARATION) {
                        assertEquals(ComparisonResult.DIFFERENT, outcome);
                        return ComparisonResult.CRITICAL;
                    }
                    assertEquals("Expected EQUAL for " + comparison.getType()
                                 + " comparison.",
                                 ComparisonResult.EQUAL, outcome);
                    return ComparisonResult.EQUAL;
                }
            });
        Document d1 = Convert.toDocument(Input.fromMemory("<Book/>").build());
        Document d2 =
            Convert.toDocument(Input.fromMemory("<!DOCTYPE Book PUBLIC "
                                                + "\"XMLUNIT/TEST/PUB\" "
                                                + "\"" + TestResources.BOOK_DTD
                                                + "\">"
                                                + "<Book/>")
                               .build());
        assertEquals(ComparisonResult.CRITICAL, d.compareNodes(d1, d2));
        assertEquals(1, ex.invoked);

        d = new DOMDifferenceEngine();
        ex = new DiffExpecter(ComparisonType.XML_VERSION);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);

        d1 = Convert.toDocument(Input.fromMemory("<?xml version=\"1.0\""
                                                 + " encoding=\"UTF-8\"?>"
                                                 + "<Book/>").build());
        d2 = Convert.toDocument(Input.fromMemory("<?xml version=\"1.1\""
                                                 + " encoding=\"UTF-8\"?>"
                                                 + "<Book/>").build());
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(d1, d2));
        assertEquals(1, ex.invoked);

        d = new DOMDifferenceEngine();
        ex = new DiffExpecter(ComparisonType.XML_STANDALONE);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);

        d1 = Convert.toDocument(Input.fromMemory("<?xml version=\"1.0\""
                                                 + " standalone=\"yes\"?>"
                                                 + "<Book/>").build());
        d2 = Convert.toDocument(Input.fromMemory("<?xml version=\"1.0\""
                                                 + " standalone=\"no\"?>"
                                                 + "<Book/>").build());
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(d1, d2));
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
                        return ComparisonResult.CRITICAL;
                    }
                    assertEquals(ComparisonResult.EQUAL, outcome);
                    return ComparisonResult.EQUAL;
                }
            });

        d1 = Convert.toDocument(Input.fromMemory("<?xml version=\"1.0\""
                                                 + " encoding=\"UTF-8\"?>"
                                                 + "<Book/>").build());
        d2 = Convert.toDocument(Input.fromMemory("<?xml version=\"1.0\""
                                                 + " encoding=\"UTF-16\"?>"
                                                 + "<Book/>").build());
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(d1, d2));
        assertEquals(1, ex.invoked);
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
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);
        DocumentType dt1 = new DocType("name", "pub", "system");
        DocumentType dt2 = new DocType("name2", "pub", "system");
        assertEquals(ComparisonResult.CRITICAL, d.compareNodes(dt1, dt2));
        assertEquals(1, ex.invoked);

        d = new DOMDifferenceEngine();
        ex = new DiffExpecter(ComparisonType.DOCTYPE_PUBLIC_ID);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);
        dt2 = new DocType("name", "pub2", "system");
        assertEquals(ComparisonResult.CRITICAL, d.compareNodes(dt1, dt2));
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
                        return ComparisonResult.CRITICAL;
                    }
                    assertEquals(ComparisonResult.EQUAL, outcome);
                    return ComparisonResult.EQUAL;
                }
            });
        dt2 = new DocType("name", "pub", "system2");
        assertEquals(ComparisonResult.CRITICAL, d.compareNodes(dt1, dt2));
        assertEquals(1, ex.invoked);
    }

    @Test public void compareElements() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.ELEMENT_TAG_NAME);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);
        Element e1 = doc.createElement("foo");
        Element e2 = doc.createElement("foo");
        Element e3 = doc.createElement("bar");
        assertEquals(ComparisonResult.EQUAL, d.compareNodes(e1, e2));
        assertEquals(ComparisonResult.CRITICAL, d.compareNodes(e1, e3));
        assertEquals(1, ex.invoked);

        d = new DOMDifferenceEngine();
        ex = new DiffExpecter(ComparisonType.ELEMENT_NUM_ATTRIBUTES);
        e1.setAttribute("attr1", "value1");
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);
        assertEquals(ComparisonResult.CRITICAL, d.compareNodes(e1, e2));
        assertEquals(1, ex.invoked);

        d = new DOMDifferenceEngine();
        ex = new DiffExpecter(ComparisonType.ATTR_NAME_LOOKUP);
        e2.setAttributeNS("urn:xmlunit:test", "attr1", "value1");
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);
        assertEquals(ComparisonResult.CRITICAL, d.compareNodes(e1, e2));
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
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);
        assertEquals(ComparisonResult.EQUAL, d.compareNodes(e1, e2));
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
        assertEquals(ComparisonResult.CRITICAL, d.compareNodes(a1, a2));
        assertEquals(1, ex.invoked);

        d = new DOMDifferenceEngine();
        */
        ex = new DiffExpecter(ComparisonType.ATTR_VALUE);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);
        Attr a3 = doc.createAttribute("foo");
        a1.setValue("foo");
        a2.setValue("foo");
        a3.setValue("bar");
        assertEquals(ComparisonResult.EQUAL, d.compareNodes(a1, a2));
        assertEquals(ComparisonResult.CRITICAL, d.compareNodes(a1, a3));
        assertEquals(1, ex.invoked);
    }

    @Test public void naiveRecursion() {
        Element e1 = doc.createElement("foo");
        Element e2 = doc.createElement("foo");
        Element c1 = doc.createElement("bar");
        e1.appendChild(c1);
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.CHILD_LOOKUP);
        d.addDifferenceListener(ex);
        DifferenceEvaluator ev = new DifferenceEvaluator() {
                public ComparisonResult evaluate(Comparison comparison,
                                                 ComparisonResult outcome) {
                    if (comparison.getType() == ComparisonType.CHILD_NODELIST_LENGTH) {
                        return ComparisonResult.EQUAL;
                    }
                    return DifferenceEvaluators.DefaultStopWhenDifferent
                        .evaluate(comparison, outcome);
                }
            };
        d.setDifferenceEvaluator(ev);
        assertEquals(ComparisonResult.CRITICAL, d.compareNodes(e1, e2));
        assertEquals(1, ex.invoked);

        Element c2 = doc.createElement("bar");
        e2.appendChild(c2);
        d = new DOMDifferenceEngine();
        ex = new DiffExpecter(ComparisonType.CHILD_LOOKUP);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(ev);
        assertEquals(ComparisonResult.EQUAL, d.compareNodes(e1, e2));
        assertEquals(0, ex.invoked);
    }
}
