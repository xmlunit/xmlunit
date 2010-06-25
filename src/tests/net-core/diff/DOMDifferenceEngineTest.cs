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

namespace net.sf.xmlunit.diff {

    [TestFixture]
    public class DOMDifferenceEngineTest {

        private ComparisonResult outcome = ComparisonResult.CRITICAL;
        private ComparisonResult ResultGrabber(Comparison comparison,
                                               ComparisonResult outcome) {
            this.outcome = outcome;
            return outcome;
        }

        [Test]
        public void CompareTwoNulls() {
            DOMDifferenceEngine d = new DOMDifferenceEngine();
            d.DifferenceEvaluator = ResultGrabber;
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.Compare(new Comparison(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                                     null, null, null,
                                                     null, null, null)));
            Assert.AreEqual(ComparisonResult.EQUAL, outcome);
        }

        [Test]
        public void CompareControlNullTestNonNull() {
            DOMDifferenceEngine d = new DOMDifferenceEngine();
            d.DifferenceEvaluator = ResultGrabber;
            Assert.AreEqual(ComparisonResult.DIFFERENT,
                         d.Compare(new Comparison(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                                  null, null, null,
                                                  null, null, "")));
            Assert.AreEqual(ComparisonResult.DIFFERENT, outcome);
        }

        [Test]
        public void CompareControlNonNullTestNull() {
            DOMDifferenceEngine d = new DOMDifferenceEngine();
            d.DifferenceEvaluator = ResultGrabber;
            Assert.AreEqual(ComparisonResult.DIFFERENT,
                            d.Compare(new Comparison(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                                     null, null, "",
                                                     null, null, null)));
            Assert.AreEqual(ComparisonResult.DIFFERENT, outcome);
        }

        [Test]
        public void CompareTwoDifferentNonNulls() {
            DOMDifferenceEngine d = new DOMDifferenceEngine();
            d.DifferenceEvaluator = ResultGrabber;
            Assert.AreEqual(ComparisonResult.DIFFERENT,
                            d.Compare(new Comparison(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                                     null, null,
                                                     Convert.ToInt16("1"),
                                                     null, null,
                                                     Convert.ToInt16("2"))));
            Assert.AreEqual(ComparisonResult.DIFFERENT, outcome);
        }

        [Test]
        public void CompareTwoEqualNonNulls() {
            DOMDifferenceEngine d = new DOMDifferenceEngine();
            d.DifferenceEvaluator = ResultGrabber;
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.Compare(new Comparison(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                                     null, null,
                                                     Convert.ToInt16("2"),
                                                     null, null,
                                                     Convert.ToInt16("2"))));
            Assert.AreEqual(ComparisonResult.EQUAL, outcome);
        }

        [Test]
        public void CompareNotifiesListener() {
            DOMDifferenceEngine d = new DOMDifferenceEngine();
            int invocations = 0;
            d.ComparisonListener += delegate(Comparison comp,
                                             ComparisonResult r) {
                invocations++;
                Assert.AreEqual(ComparisonResult.EQUAL, r);
            };
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.Compare(new Comparison(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                                     null, null,
                                                     Convert.ToInt16("2"),
                                                     null, null,
                                                     Convert.ToInt16("2"))));
            Assert.AreEqual(1, invocations);
        }

        [Test]
        public void CompareUsesResultOfEvaluator() {
            DOMDifferenceEngine d = new DOMDifferenceEngine();
            int invocations = 0;
            d.ComparisonListener += delegate(Comparison comp,
                                             ComparisonResult r) {
                invocations++;
                Assert.AreEqual(ComparisonResult.SIMILAR, r);
            };
            d.DifferenceEvaluator = delegate(Comparison comparison,
                                             ComparisonResult outcome) {
                return ComparisonResult.SIMILAR;
            };
            Assert.AreEqual(ComparisonResult.SIMILAR,
                            d.Compare(new Comparison(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                                     null, null,
                                                     Convert.ToInt16("2"),
                                                     null, null,
                                                     Convert.ToInt16("2"))));
            Assert.AreEqual(1, invocations);
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

        [Test] public void compareProcessingInstructions() {
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

    }
}
