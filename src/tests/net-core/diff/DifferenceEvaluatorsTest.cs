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

using NUnit.Framework;

namespace net.sf.xmlunit.diff {

    [TestFixture]
    public class DifferenceEvaluatorsTest {

        internal class Evaluator {
            internal bool Called = false;
            private readonly ComparisonResult ret;
            internal Evaluator(ComparisonResult ret) {
                this.ret = ret;
            }
            public ComparisonResult Evaluate(Comparison comparison,
                                             ComparisonResult orig) {
                Called = true;
                return ret;
            }
        }

        [Test]
        public void EmptyFirstJustWorks() {
            DifferenceEvaluator d = DifferenceEvaluators.First();
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d(null, ComparisonResult.CRITICAL));
        }

        [Test]
        public void FirstChangeWinsInFirst() {
            Evaluator e1 = new Evaluator(ComparisonResult.CRITICAL);
            Evaluator e2 = new Evaluator(ComparisonResult.EQUAL);
            DifferenceEvaluator d = DifferenceEvaluators.First(e1.Evaluate,
                                                               e2.Evaluate);
            Assert.AreEqual(ComparisonResult.CRITICAL,
                            d(null, ComparisonResult.DIFFERENT));
            Assert.IsTrue(e1.Called);
            Assert.IsFalse(e2.Called);
            e1.Called = false;
            Assert.AreEqual(ComparisonResult.EQUAL,
                            d(null, ComparisonResult.CRITICAL));
            Assert.IsTrue(e1.Called);
            Assert.IsTrue(e2.Called);
        }
    }
}
