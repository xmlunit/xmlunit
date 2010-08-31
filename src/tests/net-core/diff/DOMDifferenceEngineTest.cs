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
            private readonly bool withXPath;
            private readonly string controlXPath;
            private readonly string testXPath;
            internal DiffExpecter(ComparisonType type) : this(type, 1) { }

            internal DiffExpecter(ComparisonType type, int expected)
                : this(type, expected, false, null, null) { }

            internal DiffExpecter(ComparisonType type, string controlXPath,
                                  string testXPath)
                : this(type, 1, true, controlXPath, testXPath) { }
            
            private DiffExpecter(ComparisonType type, int expected,
                                 bool withXPath, string controlXPath,
                                 string testXPath) {
                this.type = type;
                this.expectedInvocations = expected;
                this.withXPath = withXPath;
                this.controlXPath = controlXPath;
                this.testXPath = testXPath;
            }
            public void ComparisonPerformed(Comparison comparison,
                                            ComparisonResult outcome) {
                Assert.Greater(expectedInvocations, invoked);
                invoked++;
                Assert.AreEqual(type, comparison.Type);
                Assert.AreEqual(ComparisonResult.CRITICAL, outcome);
                if (withXPath) {
                    Assert.AreEqual(controlXPath,
                                    comparison.ControlDetails.XPath,
                                    "Control XPath");
                    Assert.AreEqual(testXPath,
                                    comparison.TestDetails.XPath,
                                    "Test XPath");
                }
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
                                           new XPathContext(),
                                           doc.CreateComment("x"),
                                           new XPathContext()));
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
                                           new XPathContext(),
                                           doc.CreateElement("x"),
                                           new XPathContext()));
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
                                           new XPathContext(),
                                           doc.CreateElement("y", "z"),
                                           new XPathContext()));
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
                                           new XPathContext(),
                                           doc.CreateElement("z:y", "x"),
                                           new XPathContext()));
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
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.CompareNodes(e1, new XPathContext(),
                                           e2, new XPathContext()));
            e1.AppendChild(doc.CreateElement("x"));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(e1, new XPathContext(),
                                           e2, new XPathContext()));
            Assert.AreEqual(1, ex.invoked);
            e2.AppendChild(doc.CreateElement("x"));
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.CompareNodes(e1, new XPathContext(),
                                           e2, new XPathContext()));
            e2.AppendChild(doc.CreateElement("x"));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(e1, new XPathContext(),
                                           e2, new XPathContext()));
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
                            comparison.ControlDetails.Target is XmlCharacterData
                            &&
                            comparison.TestDetails.Target is XmlCharacterData)) {
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
                            d.CompareNodes(fooComment, new XPathContext(),
                                           fooComment, new XPathContext()));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(fooComment, new XPathContext(),
                                           barComment, new XPathContext()));
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.CompareNodes(fooText, new XPathContext(),
                                           fooText, new XPathContext()));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(fooText, new XPathContext(),
                                           barText, new XPathContext()));
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.CompareNodes(fooCDataSection, new XPathContext(),
                                           fooCDataSection, new XPathContext()));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(fooCDataSection, new XPathContext(),
                                           barCDataSection, new XPathContext()));

            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.CompareNodes(fooComment, new XPathContext(),
                                           fooText, new XPathContext()));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(fooComment, new XPathContext(),
                                           barText, new XPathContext()));
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.CompareNodes(fooComment, new XPathContext(),
                                           fooCDataSection, new XPathContext()));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(fooComment, new XPathContext(),
                                           barCDataSection, new XPathContext()));
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.CompareNodes(fooText, new XPathContext(),
                                           fooComment, new XPathContext()));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(fooText, new XPathContext(),
                                           barComment, new XPathContext()));
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.CompareNodes(fooText, new XPathContext(),
                                           fooCDataSection, new XPathContext()));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(fooText, new XPathContext(),
                                           barCDataSection, new XPathContext()));
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.CompareNodes(fooCDataSection, new XPathContext(),
                                           fooText, new XPathContext()));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(fooCDataSection, new XPathContext(),
                                           barText, new XPathContext()));
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.CompareNodes(fooCDataSection, new XPathContext(),
                                           fooComment, new XPathContext()));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(fooCDataSection, new XPathContext(),
                                           barComment, new XPathContext()));
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
                            d.CompareNodes(foo1, new XPathContext(),
                                           foo1, new XPathContext()));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(foo1, new XPathContext(),
                                           bar1, new XPathContext()));
            Assert.AreEqual(1, ex.invoked);

            d = new DOMDifferenceEngine();
            ex = new DiffExpecter(ComparisonType.PROCESSING_INSTRUCTION_DATA);
            d.DifferenceListener += ex.ComparisonPerformed;
            d.DifferenceEvaluator =
                DifferenceEvaluators.DefaultStopWhenDifferent;
            XmlProcessingInstruction foo2 = doc.CreateProcessingInstruction("foo",
                                                                            "2");
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.CompareNodes(foo1, new XPathContext(),
                                           foo1, new XPathContext()));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(foo1, new XPathContext(),
                                           foo2, new XPathContext()));
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
                            d.CompareNodes(d1, new XPathContext(),
                                           d2, new XPathContext()));
            Assert.AreEqual(1, ex.invoked);
#endif

#if false // .NET doesn't like XML 1.1 anyway
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
                            d.CompareNodes(d1, new XPathContext(),
                                           d2, new XPathContext()));
            Assert.AreEqual(1, ex.invoked);
#endif

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
                            d.CompareNodes(d1, new XPathContext(),
                                           d2, new XPathContext()));
            Assert.AreEqual(1, ex.invoked);

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
                            d.CompareNodes(d1, new XPathContext(),
                                           d2, new XPathContext()));
            Assert.AreEqual(1, ex.invoked);
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
                            d.CompareNodes(dt1, new XPathContext(),
                                           dt2, new XPathContext()));
            Assert.AreEqual(1, ex.invoked);

            d = new DOMDifferenceEngine();
            ex = new DiffExpecter(ComparisonType.DOCTYPE_PUBLIC_ID);
            d.DifferenceListener += ex.ComparisonPerformed;
            d.DifferenceEvaluator =
                DifferenceEvaluators.DefaultStopWhenDifferent;
            dt2 = doc.CreateDocumentType("name", "pub2",
                                         TestResources.BOOK_DTD, null);
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(dt1, new XPathContext(),
                                           dt2, new XPathContext()));
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
                            d.CompareNodes(dt1, new XPathContext(),
                                           dt2, new XPathContext()));
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
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.CompareNodes(e1, new XPathContext(),
                                           e2, new XPathContext()));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(e1, new XPathContext(),
                                           e3, new XPathContext()));
            Assert.AreEqual(1, ex.invoked);

            d = new DOMDifferenceEngine();
            ex = new DiffExpecter(ComparisonType.ELEMENT_NUM_ATTRIBUTES);
            d.DifferenceListener += ex.ComparisonPerformed;
            d.DifferenceEvaluator =
                DifferenceEvaluators.DefaultStopWhenDifferent;
            e1.SetAttribute("attr1", "value1");
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(e1, new XPathContext(),
                                           e2, new XPathContext()));
            Assert.AreEqual(1, ex.invoked);

            d = new DOMDifferenceEngine();
            ex = new DiffExpecter(ComparisonType.ATTR_NAME_LOOKUP,
                                  "/@attr1", "/");
            d.DifferenceListener += ex.ComparisonPerformed;
            d.DifferenceEvaluator =
                DifferenceEvaluators.DefaultStopWhenDifferent;
            e2.SetAttribute("attr1", "urn:xmlunit:test", "value1");
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(e1, new XPathContext(),
                                           e2, new XPathContext()));
            Assert.AreEqual(1, ex.invoked);

            d = new DOMDifferenceEngine();
            d.DifferenceListener += delegate(Comparison comp,
                                             ComparisonResult r) {
                Assert.Fail("unexpected Comparison of type " + comp.Type
                            + " with outcome " + r + " and values '"
                            + comp.ControlDetails.Value
                            + "' and '"
                            + comp.TestDetails.Value + "'"
                            + " on '" + comp.ControlDetails.Target + "'");
            };
            d.DifferenceEvaluator =
                DifferenceEvaluators.DefaultStopWhenDifferent;
            e1.SetAttribute("attr1", "urn:xmlunit:test", "value1");
            e2.SetAttribute("attr1", null, "value1");
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.CompareNodes(e1, new XPathContext(),
                                           e2, new XPathContext()));
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
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(a1, new XPathContext(),
                                           a2, new XPathContext()));
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
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.CompareNodes(a1, new XPathContext(),
                                           a2, new XPathContext()));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(a1, new XPathContext(),
                                           a3, new XPathContext()));
            Assert.AreEqual(1, ex.invoked);
        }

        [Test]
        public void NaiveRecursion() {
            XmlElement e1 = doc.CreateElement("foo");
            XmlElement e2 = doc.CreateElement("foo");
            XmlElement c1 = doc.CreateElement("bar");
            e1.AppendChild(c1);
            DOMDifferenceEngine d = new DOMDifferenceEngine();
            DiffExpecter ex = new DiffExpecter(ComparisonType.CHILD_LOOKUP,
                                               "/bar[1]", null);
            d.DifferenceListener += ex.ComparisonPerformed;
            DifferenceEvaluator ev = delegate(Comparison comparison,
                                              ComparisonResult outcome) {
                if (comparison.Type == ComparisonType.CHILD_NODELIST_LENGTH) {
                    return ComparisonResult.EQUAL;
                }
                return DifferenceEvaluators.DefaultStopWhenDifferent(comparison,
                                                                     outcome);
            };
            d.DifferenceEvaluator = ev;
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(e1, new XPathContext(),
                                           e2, new XPathContext()));
            Assert.AreEqual(1, ex.invoked);

            // symmetric?
            d = new DOMDifferenceEngine();
            ex = new DiffExpecter(ComparisonType.CHILD_LOOKUP,
                                  null, "/bar[1]");
            d.DifferenceListener += ex.ComparisonPerformed;
            d.DifferenceEvaluator = ev;
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(e2, new XPathContext(),
                                           e1, new XPathContext()));
            Assert.AreEqual(1, ex.invoked);

            XmlElement c2 = doc.CreateElement("bar");
            e2.AppendChild(c2);
            d = new DOMDifferenceEngine();
            ex = new DiffExpecter(ComparisonType.CHILD_LOOKUP);
            d.DifferenceListener += ex.ComparisonPerformed;
            d.DifferenceEvaluator = ev;
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.CompareNodes(e1, new XPathContext(),
                                           e2, new XPathContext()));
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.CompareNodes(e2, new XPathContext(),
                                           e1, new XPathContext()));
            Assert.AreEqual(0, ex.invoked);
        }

        [Test] 
        public void TextAndCDataMatchRecursively() {
            XmlElement e1 = doc.CreateElement("foo");
            XmlElement e2 = doc.CreateElement("foo");
            XmlText fooText = doc.CreateTextNode("foo");
            e1.AppendChild(fooText);
            XmlCDataSection fooCDATASection = doc.CreateCDataSection("foo");
            e2.AppendChild(fooCDATASection);
            DOMDifferenceEngine d = new DOMDifferenceEngine();
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.CompareNodes(e1, new XPathContext(),
                                           e2, new XPathContext()));
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.CompareNodes(e2, new XPathContext(),
                                           e1, new XPathContext()));
        }

        [Test]
        public void RecursionUsesElementSelector() {
            XmlElement e1 = doc.CreateElement("foo");
            XmlElement e2 = doc.CreateElement("foo");
            XmlElement e3 = doc.CreateElement("bar");
            e1.AppendChild(e3);
            XmlElement e4 = doc.CreateElement("baz");
            e2.AppendChild(e4);
            DOMDifferenceEngine d = new DOMDifferenceEngine();
            DiffExpecter ex = new DiffExpecter(ComparisonType.ELEMENT_TAG_NAME,
                                               "/bar[1]", "/baz[1]");
            d.DifferenceListener += ex.ComparisonPerformed;
            d.DifferenceEvaluator =
                DifferenceEvaluators.DefaultStopWhenDifferent;
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(e1, new XPathContext(),
                                           e2, new XPathContext()));
            Assert.AreEqual(1, ex.invoked);

            d = new DOMDifferenceEngine();
            d.ElementSelector = ElementSelectors.ByName;
            ex = new DiffExpecter(ComparisonType.CHILD_LOOKUP);
            d.DifferenceListener += ex.ComparisonPerformed;
            d.DifferenceEvaluator =
                DifferenceEvaluators.DefaultStopWhenDifferent;
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(e1, new XPathContext(),
                                           e2, new XPathContext()));
            Assert.AreEqual(1, ex.invoked);
        }

        [Test]
        public void SchemaLocationDifferences() {
            XmlElement e1 = doc.CreateElement("foo");
            XmlElement e2 = doc.CreateElement("foo");
            e1.SetAttribute("schemaLocation",
                            "http://www.w3.org/2001/XMLSchema-instance",
                            "somewhere");
            e2.SetAttribute("schemaLocation",
                            "http://www.w3.org/2001/XMLSchema-instance",
                            "somewhere else");

            DOMDifferenceEngine d = new DOMDifferenceEngine();
            DiffExpecter ex = new DiffExpecter(ComparisonType.SCHEMA_LOCATION);
            d.DifferenceListener += ex.ComparisonPerformed;
            d.DifferenceEvaluator = delegate(Comparison comparison,
                                             ComparisonResult outcome) {
                if (comparison.Type == ComparisonType.SCHEMA_LOCATION) {
                    Assert.AreEqual(ComparisonResult.DIFFERENT, outcome);
                    return ComparisonResult.CRITICAL;
                }
                Assert.AreEqual(ComparisonResult.EQUAL, outcome);
                return ComparisonResult.EQUAL;
            };
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(e1, new XPathContext(),
                                           e2, new XPathContext()));
            Assert.AreEqual(1, ex.invoked);

            e1 = doc.CreateElement("foo");
            e2 = doc.CreateElement("foo");
            e1.SetAttribute("noNamespaceSchemaLocation",
                            "http://www.w3.org/2001/XMLSchema-instance",
                            "somewhere");
            e2.SetAttribute("noNamespaceSchemaLocation",
                            "http://www.w3.org/2001/XMLSchema-instance",
                            "somewhere else");
            d = new DOMDifferenceEngine();
            ex = new DiffExpecter(ComparisonType.NO_NAMESPACE_SCHEMA_LOCATION);
            d.DifferenceListener += ex.ComparisonPerformed;
            d.DifferenceEvaluator = delegate(Comparison comparison,
                                             ComparisonResult outcome) {
                if (comparison.Type == ComparisonType.NO_NAMESPACE_SCHEMA_LOCATION) {
                    Assert.AreEqual(ComparisonResult.DIFFERENT, outcome);
                    return ComparisonResult.CRITICAL;
                }
                Assert.AreEqual(ComparisonResult.EQUAL, outcome);
                return ComparisonResult.EQUAL;
            };
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(e1, new XPathContext(),
                                           e2, new XPathContext()));
            Assert.AreEqual(1, ex.invoked);
        }

        [Test]
        public void CompareElementsNS() {
            DOMDifferenceEngine d = new DOMDifferenceEngine();
            DiffExpecter ex = new DiffExpecter(ComparisonType.ELEMENT_TAG_NAME);
            d.DifferenceListener += ex.ComparisonPerformed;
            DifferenceEvaluator ev = delegate(Comparison comparison,
                                              ComparisonResult outcome) {
                if (comparison.Type == ComparisonType.NAMESPACE_PREFIX) {
                    return ComparisonResult.EQUAL;
                }
                return DifferenceEvaluators.DefaultStopWhenDifferent(comparison,
                                                                     outcome);
            };
            d.DifferenceEvaluator = ev;

            XmlElement e1 = doc.CreateElement("p1", "foo", "urn:xmlunit:test");
            XmlElement e2 = doc.CreateElement("p1", "foo", "urn:xmlunit:test");
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.CompareNodes(e1, new XPathContext(),
                                           e2, new XPathContext()));
            Assert.AreEqual(0, ex.invoked);
        }
    }
}
