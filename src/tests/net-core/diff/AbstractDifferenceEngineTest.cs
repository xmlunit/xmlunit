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
using NUnit.Framework;

namespace net.sf.xmlunit.diff {

    public abstract class AbstractDifferenceEngineTest {

        protected abstract AbstractDifferenceEngine DifferenceEngine {
            get;
        }

        private ComparisonResult outcome = ComparisonResult.CRITICAL;
        private ComparisonResult ResultGrabber(Comparison comparison,
                                               ComparisonResult outcome) {
            this.outcome = outcome;
            return outcome;
        }

        [Test]
        public void CompareTwoNulls() {
            AbstractDifferenceEngine d = DifferenceEngine;
            d.DifferenceEvaluator = ResultGrabber;
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d.Compare(new Comparison(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                                     null, null, null,
                                                     null, null, null)));
            Assert.AreEqual(ComparisonResult.EQUAL, outcome);
        }

        [Test]
        public void CompareControlNullTestNonNull() {
            AbstractDifferenceEngine d = DifferenceEngine;
            d.DifferenceEvaluator = ResultGrabber;
            Assert.AreEqual(ComparisonResult.DIFFERENT,
                         d.Compare(new Comparison(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                                  null, null, null,
                                                  null, null, "")));
            Assert.AreEqual(ComparisonResult.DIFFERENT, outcome);
        }

        [Test]
        public void CompareControlNonNullTestNull() {
            AbstractDifferenceEngine d = DifferenceEngine;
            d.DifferenceEvaluator = ResultGrabber;
            Assert.AreEqual(ComparisonResult.DIFFERENT,
                            d.Compare(new Comparison(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                                     null, null, "",
                                                     null, null, null)));
            Assert.AreEqual(ComparisonResult.DIFFERENT, outcome);
        }

        [Test]
        public void CompareTwoDifferentNonNulls() {
            AbstractDifferenceEngine d = DifferenceEngine;
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
            AbstractDifferenceEngine d = DifferenceEngine;
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
            AbstractDifferenceEngine d = DifferenceEngine;
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
            AbstractDifferenceEngine d = DifferenceEngine;
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

    }
}
