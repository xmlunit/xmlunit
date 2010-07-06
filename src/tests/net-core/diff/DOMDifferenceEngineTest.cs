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

        private XmlDocument doc;

        [SetUp]
        public void CreateDoc() {
            doc = new XmlDocument();
        }

        [Test]
        public void CompareNodesOfDifferentType() {
            DOMDifferenceEngine d = new DOMDifferenceEngine();
            int invocations = 0;
            d.DifferenceListener += delegate(Comparison comp,
                                             ComparisonResult r) {
                Assert.AreEqual(0, invocations);
                invocations++;
                Assert.AreEqual(ComparisonType.NODE_TYPE, comp.Type);
                Assert.AreEqual(ComparisonResult.CRITICAL, r);
            };
            d.DifferenceEvaluator =
                DifferenceEvaluators.DefaultStopWhenDifferent;
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(doc.CreateElement("x"),
                                           doc.CreateComment("x")));
            Assert.AreEqual(1, invocations);
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
            int invocations = 0;
            d.DifferenceListener += delegate(Comparison comp,
                                             ComparisonResult r) {
                Assert.AreEqual(0, invocations);
                invocations++;
                Assert.AreEqual(ComparisonType.NAMESPACE_URI, comp.Type);
                Assert.AreEqual(ComparisonResult.CRITICAL, r);
            };
            d.DifferenceEvaluator =
                DifferenceEvaluators.DefaultStopWhenDifferent;
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.CompareNodes(doc.CreateElement("y", "x"),
                                           doc.CreateElement("y", "z")));
            Assert.AreEqual(1, invocations);
        }

        [Test]
        public void CompareNodesDifferentPrefix() {
            DOMDifferenceEngine d = new DOMDifferenceEngine();
            int invocations = 0;
            d.DifferenceListener += delegate(Comparison comp,
                                             ComparisonResult r) {
                Assert.AreEqual(0, invocations);
                invocations++;
                Assert.AreEqual(ComparisonType.NAMESPACE_PREFIX, comp.Type);
                Assert.AreEqual(ComparisonResult.CRITICAL, r);
            };
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
            Assert.AreEqual(1, invocations);
        }

        [Test]
        public void CompareNodesDifferentNumberOfChildren() {
            DOMDifferenceEngine d = new DOMDifferenceEngine();
            int invocations = 0;
            d.DifferenceListener += delegate(Comparison comp,
                                             ComparisonResult r) {
                Assert.Greater(2, invocations);
                invocations++;
                Assert.AreEqual(ComparisonType.CHILD_NODELIST_LENGTH,
                                comp.Type);
                Assert.AreEqual(ComparisonResult.CRITICAL, r);
            };
            d.DifferenceEvaluator =
                DifferenceEvaluators.DefaultStopWhenDifferent;
            XmlElement e1 = doc.CreateElement("x");
            XmlElement e2 = doc.CreateElement("x");
            Assert.AreEqual(ComparisonResult.EQUAL, d.CompareNodes(e1, e2));
            e1.AppendChild(doc.CreateElement("x"));
            Assert.AreEqual(ComparisonResult.CRITICAL, d.CompareNodes(e1, e2));
            Assert.AreEqual(1, invocations);
            e2.AppendChild(doc.CreateElement("x"));
            Assert.AreEqual(ComparisonResult.EQUAL, d.CompareNodes(e1, e2));
            e2.AppendChild(doc.CreateElement("x"));
            Assert.AreEqual(ComparisonResult.CRITICAL, d.CompareNodes(e1, e2));
            Assert.AreEqual(2, invocations);
        }

        [Test]
        public void CompareCharacterData() {
            DOMDifferenceEngine d = new DOMDifferenceEngine();
            int invocations = 0;
            d.DifferenceListener += delegate(Comparison comp,
                                             ComparisonResult r) {
                Assert.Greater(9, invocations);
                invocations++;
                Assert.AreEqual(ComparisonType.TEXT_VALUE,
                                comp.Type);
                Assert.AreEqual(ComparisonResult.CRITICAL, r);
            };
            d.DifferenceEvaluator =
                DifferenceEvaluators.DefaultStopWhenDifferent;

            XmlComment fooComment = doc.CreateComment("foo");
            XmlComment barComment = doc.CreateComment("bar");
            XmlText fooText = doc.CreateTextNode("foo");
            XmlText barText = doc.CreateTextNode("bar");
            XmlCDataSection fooCDataSection = doc.CreateCDataSection("foo");
            XmlCDataSection barCDataSection = doc.CreateCDataSection("bar");

            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.NodeTypeSpecificComparison(fooComment,
                                                         fooComment));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.NodeTypeSpecificComparison(fooComment,
                                                         barComment));
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.NodeTypeSpecificComparison(fooText, fooText));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.NodeTypeSpecificComparison(fooText, barText));
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.NodeTypeSpecificComparison(fooCDataSection,
                                                         fooCDataSection));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.NodeTypeSpecificComparison(fooCDataSection,
                                                         barCDataSection));
        
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.NodeTypeSpecificComparison(fooComment, fooText));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.NodeTypeSpecificComparison(fooComment, barText));
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.NodeTypeSpecificComparison(fooComment,
                                                         fooCDataSection));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.NodeTypeSpecificComparison(fooComment,
                                                         barCDataSection));
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.NodeTypeSpecificComparison(fooText,
                                                         fooComment));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.NodeTypeSpecificComparison(fooText, barComment));
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.NodeTypeSpecificComparison(fooText,
                                                         fooCDataSection));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.NodeTypeSpecificComparison(fooText,
                                                         barCDataSection));
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.NodeTypeSpecificComparison(fooCDataSection,
                                                         fooText));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.NodeTypeSpecificComparison(fooCDataSection,
                                                         barText));
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.NodeTypeSpecificComparison(fooCDataSection,
                                                         fooComment));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.NodeTypeSpecificComparison(fooCDataSection,
                                                         barComment));
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.NodeTypeSpecificComparison(fooText,
                                                         doc.CreateElement("bar")));
            Assert.AreEqual(9, invocations);
        }

        [Test]
        public void CompareProcessingInstructions() {
            DOMDifferenceEngine d = new DOMDifferenceEngine();
            int invocations = 0;
            d.DifferenceListener += delegate(Comparison comp,
                                             ComparisonResult r) {
                Assert.AreEqual(0, invocations);
                invocations++;
                Assert.AreEqual(ComparisonType.PROCESSING_INSTRUCTION_TARGET,
                                comp.Type);
                Assert.AreEqual(ComparisonResult.CRITICAL, r);
            };
            d.DifferenceEvaluator =
                DifferenceEvaluators.DefaultStopWhenDifferent;

            XmlProcessingInstruction foo1 = doc.CreateProcessingInstruction("foo",
                                                                            "1");
            XmlProcessingInstruction bar1 = doc.CreateProcessingInstruction("bar",
                                                                            "1");
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.NodeTypeSpecificComparison(foo1, foo1));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.NodeTypeSpecificComparison(foo1, bar1));
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.NodeTypeSpecificComparison(foo1,
                                                         doc.CreateElement("bar")));
            Assert.AreEqual(1, invocations);

            d = new DOMDifferenceEngine();
            invocations = 0;
            d.DifferenceListener += delegate(Comparison comp,
                                             ComparisonResult r) {
                Assert.AreEqual(0, invocations);
                invocations++;
                Assert.AreEqual(ComparisonType.PROCESSING_INSTRUCTION_DATA,
                                comp.Type);
                Assert.AreEqual(ComparisonResult.CRITICAL, r);
            };
            d.DifferenceEvaluator =
                DifferenceEvaluators.DefaultStopWhenDifferent;
            XmlProcessingInstruction foo2 = doc.CreateProcessingInstruction("foo",
                                                                            "2");
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.NodeTypeSpecificComparison(foo1, foo1));
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.NodeTypeSpecificComparison(foo1, foo2));
            Assert.AreEqual(1, invocations);
        }

        [Test]
        public void CompareDocuments() {
            DOMDifferenceEngine d = new DOMDifferenceEngine();
            int invocations = 0;
            d.DifferenceListener += delegate(Comparison comp,
                                             ComparisonResult r) {
                Assert.AreEqual(0, invocations);
                invocations++;
                Assert.AreEqual(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                comp.Type);
                Assert.AreEqual(ComparisonResult.CRITICAL, r);
            };
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
                            d.NodeTypeSpecificComparison(d1, d2));
            Assert.AreEqual(1, invocations);
#endif

#if false // need a way to figure out the XML_* differences

            // .NET doesn't like XML 1.1 anyway
            invocations = 0;
            d = new DOMDifferenceEngine();
            d.DifferenceListener += delegate(Comparison comp,
                                             ComparisonResult r) {
                Assert.AreEqual(0, invocations);
                invocations++;
                Assert.AreEqual(ComparisonType.XML_VERSION,
                                comp.Type);
                Assert.AreEqual(ComparisonResult.CRITICAL, r);
            };
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
                            d.NodeTypeSpecificComparison(d1, d2));
            Assert.AreEqual(1, invocations);
#endif

#if false // need a way to figure out the XML_* differences
            invocations = 0;
            d = new DOMDifferenceEngine();
            d.DifferenceListener += delegate(Comparison comp,
                                             ComparisonResult r) {
                Assert.AreEqual(0, invocations);
                invocations++;
                Assert.AreEqual(ComparisonType.XML_STANDALONE,
                                comp.Type);
                Assert.AreEqual(ComparisonResult.CRITICAL, r);
            };
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
                            d.NodeTypeSpecificComparison(d1, d2));
            Assert.AreEqual(1, invocations);
#endif

#if false // need a way to figure out the XML_* differences
            invocations = 0;
            d = new DOMDifferenceEngine();
            d.DifferenceListener += delegate(Comparison comp,
                                             ComparisonResult r) {
                Assert.AreEqual(0, invocations);
                invocations++;
                Assert.AreEqual(ComparisonType.XML_ENCODING,
                                comp.Type);
                Assert.AreEqual(ComparisonResult.CRITICAL, r);
            };
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
                            d.NodeTypeSpecificComparison(d1, d2));
            Assert.AreEqual(1, invocations);
#endif
        }

        [Test]
        public void CompareDocTypes() {
            int invocations = 0;
            DOMDifferenceEngine d = new DOMDifferenceEngine();
            d.DifferenceListener += delegate(Comparison comp,
                                             ComparisonResult r) {
                Assert.AreEqual(0, invocations);
                invocations++;
                Assert.AreEqual(ComparisonType.DOCTYPE_NAME,
                                comp.Type);
                Assert.AreEqual(ComparisonResult.CRITICAL, r);
            };
            d.DifferenceEvaluator =
                DifferenceEvaluators.DefaultStopWhenDifferent;

            XmlDocumentType dt1 = doc.CreateDocumentType("name", "pub",
                                                         TestResources.BOOK_DTD,
                                                         null);
            XmlDocumentType dt2 = doc.CreateDocumentType("name2", "pub",
                                                         TestResources.BOOK_DTD,
                                                         null);
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.NodeTypeSpecificComparison(dt1, dt2));
            Assert.AreEqual(1, invocations);

            invocations = 0;
            d = new DOMDifferenceEngine();
            d.DifferenceListener += delegate(Comparison comp,
                                             ComparisonResult r) {
                Assert.AreEqual(0, invocations);
                invocations++;
                Assert.AreEqual(ComparisonType.DOCTYPE_PUBLIC_ID,
                                comp.Type);
                Assert.AreEqual(ComparisonResult.CRITICAL, r);
            };
            d.DifferenceEvaluator =
                DifferenceEvaluators.DefaultStopWhenDifferent;
            dt2 = doc.CreateDocumentType("name", "pub2",
                                         TestResources.BOOK_DTD, null);
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d.NodeTypeSpecificComparison(dt1, dt2));
            Assert.AreEqual(1, invocations);

            invocations = 0;
            d = new DOMDifferenceEngine();
            d.DifferenceListener += delegate(Comparison comp,
                                             ComparisonResult r) {
                Assert.AreEqual(0, invocations);
                invocations++;
                Assert.AreEqual(ComparisonType.DOCTYPE_SYSTEM_ID,
                                comp.Type);
                Assert.AreEqual(ComparisonResult.CRITICAL, r);
            };
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
                            d.NodeTypeSpecificComparison(dt1, dt2));
            Assert.AreEqual(1, invocations);
        }

    }
}
