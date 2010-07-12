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
using System;
using System.Xml;
using NUnit.Framework;
using net.sf.xmlunit.builder;

namespace net.sf.xmlunit.diff {

    [TestFixture]
    public class DOMDifferenceEngineTest : AbstractDifferenceEngineTest {

        protected override AbstractDifferenceEngine DifferenceEngine {
            get {
                return new DOMDifferenceEngine();
            }
        }

        private class DiffExpecter {
            internal int invoked = 0;
            private readonly int expectedInvocations;
            private readonly ComparisonType type;
            internal DiffExpecter(ComparisonType type) : this(type, 1) { }
            internal DiffExpecter(ComparisonType type, int expected) {
                this.type = type;
                this.expectedInvocations = expected;
            }
            public void ComparisonPerformed(Comparison comparison,
                                            ComparisonResult outcome) {
                Assert.Greater(expectedInvocations, invoked);
                invoked++;
                Assert.AreEqual(type, comparison.Type);
                Assert.AreEqual(ComparisonResult.CRITICAL, outcome);
            }
        }

        private XmlDocument doc;

        [SetUp]
        public void CreateDoc() {
            doc = new XmlDocument();
        }

        [Test]
        public void CompareNodesOfDifferentType() {
            DOMDifferenceEngine d = new DOMDifferenceEngine();
            DiffExpecter ex = new DiffExpecter(ComparisonType.NODE_TYPE);
            d.DifferenceListener += ex.ComparisonPerformed;
            d.DifferenceEvaluator =
                DifferenceEvaluators.DefaultStopWhenDifferent;
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(doc.CreateElement("x"),
                                           doc.CreateComment("x")));
            Assert.AreEqual(1, ex.invoked);
        }

        [Test]
        public void CompareNodesWithoutNS() {
            DOMDifferenceEngine d = new DOMDifferenceEngine();
            d.DifferenceListener += delegate(Comparison comp,
                                             ComparisonResult r) {
                Assert.Fail("unexpected invocation");
            };
            d.DifferenceEvaluator =
                DifferenceEvaluators.DefaultStopWhenDifferent;
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.CompareNodes(doc.CreateElement("x"),
                                           doc.CreateElement("x")));
        }

        [Test]
        public void CompareNodesDifferentNS() {
            DOMDifferenceEngine d = new DOMDifferenceEngine();
            DiffExpecter ex = new DiffExpecter(ComparisonType.NAMESPACE_URI);
            d.DifferenceListener += ex.ComparisonPerformed;
            d.DifferenceEvaluator =
                DifferenceEvaluators.DefaultStopWhenDifferent;
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(doc.CreateElement("y", "x"),
                                           doc.CreateElement("y", "z")));
            Assert.AreEqual(1, ex.invoked);
        }

        [Test]
        public void CompareNodesDifferentPrefix() {
            DOMDifferenceEngine d = new DOMDifferenceEngine();
            DiffExpecter ex = new DiffExpecter(ComparisonType.NAMESPACE_PREFIX);
            d.DifferenceListener += ex.ComparisonPerformed;
            d.DifferenceEvaluator = delegate(Comparison comparison,
                                             ComparisonResult outcome) {
                if (comparison.Type == ComparisonType.NAMESPACE_PREFIX) {
                    Assert.AreEqual(ComparisonResult.DIFFERENT, outcome);
                    return ComparisonResult.CRITICAL;
                }
                Assert.AreEqual(ComparisonResult.EQUAL, outcome);
                return ComparisonResult.EQUAL;
            };
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(doc.CreateElement("x:y", "x"),
                                           doc.CreateElement("z:y", "x")));
            Assert.AreEqual(1, ex.invoked);
        }

        [Test]
        public void CompareNodesDifferentNumberOfChildren() {
            DOMDifferenceEngine d = new DOMDifferenceEngine();
            DiffExpecter ex =
                new DiffExpecter(ComparisonType.CHILD_NODELIST_LENGTH, 2);
            d.DifferenceListener += ex.ComparisonPerformed;
            d.DifferenceEvaluator =
                DifferenceEvaluators.DefaultStopWhenDifferent;
            XmlElement e1 = doc.CreateElement("x");
            XmlElement e2 = doc.CreateElement("x");
            Assert.AreEqual(ComparisonResult.EQUAL, d.CompareNodes(e1, e2));
            e1.AppendChild(doc.CreateElement("x"));
            Assert.AreEqual(ComparisonResult.CRITICAL, d.CompareNodes(e1, e2));
            Assert.AreEqual(1, ex.invoked);
            e2.AppendChild(doc.CreateElement("x"));
            Assert.AreEqual(ComparisonResult.EQUAL, d.CompareNodes(e1, e2));
            e2.AppendChild(doc.CreateElement("x"));
            Assert.AreEqual(ComparisonResult.CRITICAL, d.CompareNodes(e1, e2));
            Assert.AreEqual(2, ex.invoked);
        }

        [Test]
        public void CompareCharacterData() {
            DOMDifferenceEngine d = new DOMDifferenceEngine();
            DiffExpecter ex = new DiffExpecter(ComparisonType.TEXT_VALUE, 9);
            d.DifferenceListener += ex.ComparisonPerformed;
            d.DifferenceEvaluator = delegate(Comparison comparison,
                                             ComparisonResult outcome) {
                if (comparison.Type == ComparisonType.NODE_TYPE) {
                    if (outcome == ComparisonResult.EQUAL
                        || (
                            comparison.ControlNodeDetails.Node
                            is XmlCharacterData
                            &&
                            comparison.TestNodeDetails.Node is XmlCharacterData
                            )) {
                        return ComparisonResult.EQUAL;
                    }
                }
                return DifferenceEvaluators.DefaultStopWhenDifferent(comparison,
                                                                     outcome);
            };

            XmlComment fooComment = doc.CreateComment("foo");
            XmlComment barComment = doc.CreateComment("bar");
            XmlText fooText = doc.CreateTextNode("foo");
            XmlText barText = doc.CreateTextNode("bar");
            XmlCDataSection fooCDataSection = doc.CreateCDataSection("foo");
            XmlCDataSection barCDataSection = doc.CreateCDataSection("bar");

            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.CompareNodes(fooComment, fooComment));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(fooComment, barComment));
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.CompareNodes(fooText, fooText));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(fooText, barText));
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.CompareNodes(fooCDataSection, fooCDataSection));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(fooCDataSection, barCDataSection));
        
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.CompareNodes(fooComment, fooText));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(fooComment, barText));
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.CompareNodes(fooComment, fooCDataSection));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(fooComment, barCDataSection));
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.CompareNodes(fooText, fooComment));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(fooText, barComment));
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.CompareNodes(fooText, fooCDataSection));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(fooText, barCDataSection));
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.CompareNodes(fooCDataSection, fooText));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(fooCDataSection, barText));
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.CompareNodes(fooCDataSection, fooComment));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(fooCDataSection, barComment));
            Assert.AreEqual(9, ex.invoked);
        }

        [Test]
        public void CompareProcessingInstructions() {
            DOMDifferenceEngine d = new DOMDifferenceEngine();
            DiffExpecter ex =
                new DiffExpecter(ComparisonType.PROCESSING_INSTRUCTION_TARGET);
            d.DifferenceListener += ex.ComparisonPerformed;
            d.DifferenceEvaluator =
                DifferenceEvaluators.DefaultStopWhenDifferent;

            XmlProcessingInstruction foo1 = doc.CreateProcessingInstruction("foo",
                                                                            "1");
            XmlProcessingInstruction bar1 = doc.CreateProcessingInstruction("bar",
                                                                            "1");
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.CompareNodes(foo1, foo1));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(foo1, bar1));
            Assert.AreEqual(1, ex.invoked);

            d = new DOMDifferenceEngine();
            ex = new DiffExpecter(ComparisonType.PROCESSING_INSTRUCTION_DATA);
            d.DifferenceListener += ex.ComparisonPerformed;
            d.DifferenceEvaluator =
                DifferenceEvaluators.DefaultStopWhenDifferent;
            XmlProcessingInstruction foo2 = doc.CreateProcessingInstruction("foo",
                                                                            "2");
            Assert.AreEqual(ComparisonResult.EQUAL, d.CompareNodes(foo1, foo1));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(foo1, foo2));
            Assert.AreEqual(1, ex.invoked);
        }

        [Test]
        public void CompareDocuments() {
            DOMDifferenceEngine d = new DOMDifferenceEngine();
            DiffExpecter ex =
                new DiffExpecter(ComparisonType.HAS_DOCTYPE_DECLARATION);
            d.DifferenceListener += ex.ComparisonPerformed;
            d.DifferenceEvaluator = delegate(Comparison comparison,
                                             ComparisonResult outcome) {
                if (comparison.Type == ComparisonType.HAS_DOCTYPE_DECLARATION) {
                    Assert.AreEqual(ComparisonResult.DIFFERENT, outcome);
                    return ComparisonResult.CRITICAL;
                }
                Assert.AreEqual(ComparisonResult.EQUAL, outcome);
                return ComparisonResult.EQUAL;
            };

            XmlDocument d1, d2;

#if false // ProhibitDtd needs to be handled at a lower level
            d1 = net.sf.xmlunit.util.Convert
                .ToDocument(Input.FromMemory("<Book/>").Build());
            d2 = net.sf.xmlunit.util.Convert
                .ToDocument(Input.FromMemory("<!DOCTYPE Book PUBLIC "
                                             + "\"XMLUNIT/TEST/PUB\" "
                                             + "\"" + TestResources.BOOK_DTD
                                             + "\">"
                                             + "<Book/>")
                            .Build());
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(d1, d2));
            Assert.AreEqual(1, ex.invoked);
#endif

#if false // need a way to figure out the XML_* differences

            // .NET doesn't like XML 1.1 anyway
            d = new DOMDifferenceEngine();
            ex = new DiffExpecter(ComparisonType.XML_VERSION);
            d.DifferenceListener += ex.ComparisonPerformed;
            d.DifferenceEvaluator =
                DifferenceEvaluators.DefaultStopWhenDifferent;

            d1 = net.sf.xmlunit.util.Convert
                .ToDocument(Input.FromMemory("<?xml version=\"1.0\""
                                             + " encoding=\"UTF-8\"?>"
                                             + "<Book/>").Build());
            d2 = net.sf.xmlunit.util.Convert
                .ToDocument(Input.FromMemory("<?xml version=\"1.1\""
                                             + " encoding=\"UTF-8\"?>"
                                             + "<Book/>").Build());
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(d1, d2));
            Assert.AreEqual(1, ex.invoked);
#endif

#if false // need a way to figure out the XML_* differences
            d = new DOMDifferenceEngine();
            ex = new DiffExpecter(ComparisonType.XML_STANDALONE);
            d.DifferenceListener += ex.ComparisonPerformed;
            d.DifferenceEvaluator =
                DifferenceEvaluators.DefaultStopWhenDifferent;

            d1 = net.sf.xmlunit.util.Convert
                .ToDocument(Input.FromMemory("<?xml version=\"1.0\""
                                             + " standalone=\"yes\"?>"
                                             + "<Book/>").Build());
            d2 = net.sf.xmlunit.util.Convert
                .ToDocument(Input.FromMemory("<?xml version=\"1.0\""
                                             + " standalone=\"no\"?>"
                                             + "<Book/>").Build());
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(d1, d2));
            Assert.AreEqual(1, ex.invoked);
#endif

#if false // need a way to figure out the XML_* differences
            d = new DOMDifferenceEngine();
            ex = new DiffExpecter(ComparisonType.XML_ENCODING);
            d.DifferenceListener += ex.ComparisonPerformed;
            d.DifferenceEvaluator = delegate(Comparison comparison,
                                             ComparisonResult outcome) {
                if (comparison.Type == ComparisonType.XML_ENCODING) {
                    Assert.AreEqual(ComparisonResult.DIFFERENT, outcome);
                    return ComparisonResult.CRITICAL;
                }
                Assert.AreEqual(ComparisonResult.EQUAL, outcome);
                return ComparisonResult.EQUAL;
            };

            d1 = net.sf.xmlunit.util.Convert
                .ToDocument(Input.FromMemory("<?xml version=\"1.0\""
                                             + " encoding=\"UTF-8\"?>"
                                             + "<Book/>").Build());
            d2 = net.sf.xmlunit.util.Convert
                .ToDocument(Input.FromMemory("<?xml version=\"1.0\""
                                             + " encoding=\"UTF-16\"?>"
                                             + "<Book/>").Build());
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(d1, d2));
            Assert.AreEqual(1, ex.invoked);
#endif
        }

        [Test]
        public void CompareDocTypes() {
            DOMDifferenceEngine d = new DOMDifferenceEngine();
            DiffExpecter ex = new DiffExpecter(ComparisonType.DOCTYPE_NAME);
            d.DifferenceListener += ex.ComparisonPerformed;
            d.DifferenceEvaluator =
                DifferenceEvaluators.DefaultStopWhenDifferent;

            XmlDocumentType dt1 = doc.CreateDocumentType("name", "pub",
                                                         TestResources.BOOK_DTD,
                                                         null);
            XmlDocumentType dt2 = doc.CreateDocumentType("name2", "pub",
                                                         TestResources.BOOK_DTD,
                                                         null);
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(dt1, dt2));
            Assert.AreEqual(1, ex.invoked);

            d = new DOMDifferenceEngine();
            ex = new DiffExpecter(ComparisonType.DOCTYPE_PUBLIC_ID);
            d.DifferenceListener += ex.ComparisonPerformed;
            d.DifferenceEvaluator =
                DifferenceEvaluators.DefaultStopWhenDifferent;
            dt2 = doc.CreateDocumentType("name", "pub2",
                                         TestResources.BOOK_DTD, null);
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(dt1, dt2));
            Assert.AreEqual(1, ex.invoked);

            d = new DOMDifferenceEngine();
            ex = new DiffExpecter(ComparisonType.DOCTYPE_SYSTEM_ID);
            d.DifferenceListener += ex.ComparisonPerformed;
            d.DifferenceEvaluator = delegate(Comparison comparison,
                                             ComparisonResult outcome) {
                if (comparison.Type == ComparisonType.DOCTYPE_SYSTEM_ID) {
                    Assert.AreEqual(ComparisonResult.DIFFERENT, outcome);
                    return ComparisonResult.CRITICAL;
                }
                Assert.AreEqual(ComparisonResult.EQUAL, outcome);
                return ComparisonResult.EQUAL;
            };
            dt2 = doc.CreateDocumentType("name", "pub",
                                         TestResources.TEST_DTD, null);
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(dt1, dt2));
            Assert.AreEqual(1, ex.invoked);
        }

        [Test]
        public void CompareElements() {
            DOMDifferenceEngine d = new DOMDifferenceEngine();
            DiffExpecter ex = new DiffExpecter(ComparisonType.ELEMENT_TAG_NAME);
            d.DifferenceListener += ex.ComparisonPerformed;
            d.DifferenceEvaluator =
                DifferenceEvaluators.DefaultStopWhenDifferent;

            XmlElement e1 = doc.CreateElement("foo");
            XmlElement e2 = doc.CreateElement("foo");
            XmlElement e3 = doc.CreateElement("bar");
            Assert.AreEqual(ComparisonResult.EQUAL, d.CompareNodes(e1, e2));
            Assert.AreEqual(ComparisonResult.CRITICAL, d.CompareNodes(e1, e3));
            Assert.AreEqual(1, ex.invoked);

            d = new DOMDifferenceEngine();
            ex = new DiffExpecter(ComparisonType.ELEMENT_NUM_ATTRIBUTES);
            d.DifferenceListener += ex.ComparisonPerformed;
            d.DifferenceEvaluator =
                DifferenceEvaluators.DefaultStopWhenDifferent;
            e1.SetAttribute("attr1", "value1");
            Assert.AreEqual(ComparisonResult.CRITICAL, d.CompareNodes(e1, e2));
            Assert.AreEqual(1, ex.invoked);

            d = new DOMDifferenceEngine();
            ex = new DiffExpecter(ComparisonType.ATTR_NAME_LOOKUP);
            d.DifferenceListener += ex.ComparisonPerformed;
            d.DifferenceEvaluator =
                DifferenceEvaluators.DefaultStopWhenDifferent;
            e2.SetAttribute("attr1", "urn:xmlunit:test", "value1");
            Assert.AreEqual(ComparisonResult.CRITICAL, d.CompareNodes(e1, e2));
            Assert.AreEqual(1, ex.invoked);

            d = new DOMDifferenceEngine();
            d.DifferenceListener += delegate(Comparison comp,
                                             ComparisonResult r) {
                Assert.Fail("unexpected Comparison of type " + comp.Type
                            + " with outcome " + r + " and values '"
                            + comp.ControlNodeDetails.Value
                            + "' and '"
                            + comp.TestNodeDetails.Value + "'");
            };
            d.DifferenceEvaluator =
                DifferenceEvaluators.DefaultStopWhenDifferent;
            e1.SetAttribute("attr1", "urn:xmlunit:test", "value1");
            e2.SetAttribute("attr1", null, "value1");
            Assert.AreEqual(ComparisonResult.EQUAL, d.CompareNodes(e1, e2));
        }

        [Test]
        public void CompareAttributes() {
            XmlAttribute a1 = doc.CreateAttribute("foo");
            XmlAttribute a2 = doc.CreateAttribute("foo");

            DOMDifferenceEngine d = new DOMDifferenceEngine();
#if false // Can't reset "explicitly set" state for Documents created via API
            DiffExpecter ex = new DiffExpecter(ComparisonType.ATTR_VALUE_EXPLICITLY_SPECIFIED);
            d.DifferenceListener += ex.ComparisonPerformed;
            d.DifferenceEvaluator = DifferenceEvaluators.Accept;
            a2.Value = string.Empty;
            Assert.AreEqual(ComparisonResult.CRITICAL, d.CompareNodes(a1, a2));
            Assert.AreEqual(1, ex.invoked);
#endif

            d = new DOMDifferenceEngine();
            DiffExpecter ex = new DiffExpecter(ComparisonType.ATTR_VALUE);
            d.DifferenceListener += ex.ComparisonPerformed;
            d.DifferenceEvaluator =
                DifferenceEvaluators.DefaultStopWhenDifferent;
            XmlAttribute a3 = doc.CreateAttribute("foo");
            a1.Value = "foo";
            a2.Value = "foo";
            a3.Value = "bar";
            Assert.AreEqual(ComparisonResult.EQUAL, d.CompareNodes(a1, a2));
            Assert.AreEqual(ComparisonResult.CRITICAL, d.CompareNodes(a1, a3));
            Assert.AreEqual(1, ex.invoked);
        }
    }
}
