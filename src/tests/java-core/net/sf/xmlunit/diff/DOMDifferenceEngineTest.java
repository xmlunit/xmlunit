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
import org.w3c.dom.CDATASection;
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
            assertTrue(invoked < expectedInvocations);
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
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);

        Comment fooComment = doc.createComment("foo");
        Comment barComment = doc.createComment("bar");
        Text fooText = doc.createTextNode("foo");
        Text barText = doc.createTextNode("bar");
        CDATASection fooCDATASection = doc.createCDATASection("foo");
        CDATASection barCDATASection = doc.createCDATASection("bar");

        assertEquals(ComparisonResult.EQUAL,
                     d.nodeTypeSpecificComparison(fooComment, fooComment));
        assertEquals(ComparisonResult.CRITICAL,
                     d.nodeTypeSpecificComparison(fooComment, barComment));
        assertEquals(ComparisonResult.EQUAL,
                     d.nodeTypeSpecificComparison(fooText, fooText));
        assertEquals(ComparisonResult.CRITICAL,
                     d.nodeTypeSpecificComparison(fooText, barText));
        assertEquals(ComparisonResult.EQUAL,
                     d.nodeTypeSpecificComparison(fooCDATASection, fooCDATASection));
        assertEquals(ComparisonResult.CRITICAL,
                     d.nodeTypeSpecificComparison(fooCDATASection, barCDATASection));
        
        assertEquals(ComparisonResult.EQUAL,
                     d.nodeTypeSpecificComparison(fooComment, fooText));
        assertEquals(ComparisonResult.CRITICAL,
                     d.nodeTypeSpecificComparison(fooComment, barText));
        assertEquals(ComparisonResult.EQUAL,
                     d.nodeTypeSpecificComparison(fooComment, fooCDATASection));
        assertEquals(ComparisonResult.CRITICAL,
                     d.nodeTypeSpecificComparison(fooComment, barCDATASection));
        assertEquals(ComparisonResult.EQUAL,
                     d.nodeTypeSpecificComparison(fooText, fooComment));
        assertEquals(ComparisonResult.CRITICAL,
                     d.nodeTypeSpecificComparison(fooText, barComment));
        assertEquals(ComparisonResult.EQUAL,
                     d.nodeTypeSpecificComparison(fooText, fooCDATASection));
        assertEquals(ComparisonResult.CRITICAL,
                     d.nodeTypeSpecificComparison(fooText, barCDATASection));
        assertEquals(ComparisonResult.EQUAL,
                     d.nodeTypeSpecificComparison(fooCDATASection, fooText));
        assertEquals(ComparisonResult.CRITICAL,
                     d.nodeTypeSpecificComparison(fooCDATASection, barText));
        assertEquals(ComparisonResult.EQUAL,
                     d.nodeTypeSpecificComparison(fooCDATASection, fooComment));
        assertEquals(ComparisonResult.CRITICAL,
                     d.nodeTypeSpecificComparison(fooCDATASection, barComment));
        assertEquals(ComparisonResult.EQUAL,
                     d.nodeTypeSpecificComparison(fooText,
                                                  doc.createElement("bar")));
        assertEquals(9, ex.invoked);
    }

    @Test public void compareProcessingInstructions() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.PROCESSING_INSTRUCTION_TARGET);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);

        ProcessingInstruction foo1 = doc.createProcessingInstruction("foo", "1");
        ProcessingInstruction bar1 = doc.createProcessingInstruction("bar", "1");
        assertEquals(ComparisonResult.EQUAL,
                     d.nodeTypeSpecificComparison(foo1, foo1));
        assertEquals(ComparisonResult.CRITICAL,
                     d.nodeTypeSpecificComparison(foo1, bar1));
        assertEquals(ComparisonResult.EQUAL,
                     d.nodeTypeSpecificComparison(foo1,
                                                  doc.createElement("bar")));
        assertEquals(1, ex.invoked);

        d = new DOMDifferenceEngine();
        ex = new DiffExpecter(ComparisonType.PROCESSING_INSTRUCTION_DATA);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);
        ProcessingInstruction foo2 = doc.createProcessingInstruction("foo", "2");
        assertEquals(ComparisonResult.EQUAL,
                     d.nodeTypeSpecificComparison(foo1, foo1));
        assertEquals(ComparisonResult.CRITICAL,
                     d.nodeTypeSpecificComparison(foo1, foo2));
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
                        == ComparisonType.HAS_DOCTYPE_DECLARATION) {
                        assertEquals(ComparisonResult.DIFFERENT, outcome);
                        return ComparisonResult.CRITICAL;
                    }
                    assertEquals(ComparisonResult.EQUAL, outcome);
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
        assertEquals(ComparisonResult.CRITICAL,
                     d.nodeTypeSpecificComparison(d1, d2));
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
                     d.nodeTypeSpecificComparison(d1, d2));
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
                     d.nodeTypeSpecificComparison(d1, d2));
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
                     d.nodeTypeSpecificComparison(d1, d2));
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
        assertEquals(ComparisonResult.CRITICAL,
                     d.nodeTypeSpecificComparison(dt1, dt2));
        assertEquals(1, ex.invoked);

        d = new DOMDifferenceEngine();
        ex = new DiffExpecter(ComparisonType.DOCTYPE_PUBLIC_ID);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);
        dt2 = new DocType("name", "pub2", "system");
        assertEquals(ComparisonResult.CRITICAL,
                     d.nodeTypeSpecificComparison(dt1, dt2));
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
        assertEquals(ComparisonResult.CRITICAL,
                     d.nodeTypeSpecificComparison(dt1, dt2));
        assertEquals(1, ex.invoked);
    }
}
