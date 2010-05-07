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

using System.Xml;

namespace net.sf.xmlunit.diff {

    /// <summary>
    /// Evaluators used for the base cases.
    /// </summary>
    public sealed class DifferenceEvaluators {
        private DifferenceEvaluators() { }

        /// <summary>
        /// The "standard" difference evaluator which decides which
        /// differences make two XML documents really different and which
        /// still leave them similar.
        /// </summary>
        public static ComparisonResult Default(Comparison comparison,
                                               ComparisonResult outcome) {
            if (outcome == ComparisonResult.DIFFERENT) {
                switch (comparison.Type) {
                case ComparisonType.NODE_TYPE:
                    XmlNodeType control =
                        (XmlNodeType) comparison.ControlNodeDetails.Value;
                    XmlNodeType test =
                        (XmlNodeType) comparison.TestNodeDetails.Value;
                    if ((control == XmlNodeType.Text && test == XmlNodeType.CDATA)
                        ||
                        (control == XmlNodeType.CDATA && test == XmlNodeType.Text)
                        ) {
                        outcome = ComparisonResult.SIMILAR;
                    }
                    break;
                case ComparisonType.HAS_DOCTYPE_DECLARATION:
                case ComparisonType.DOCTYPE_SYSTEM_ID:
                case ComparisonType.SCHEMA_LOCATION:
                case ComparisonType.NO_NAMESPACE_SCHEMA_LOCATION:
                case ComparisonType.NAMESPACE_PREFIX:
                case ComparisonType.ATTR_VALUE_EXPLICITLY_SPECIFIED:
                case ComparisonType.CHILD_NODELIST_SEQUENCE:
                    outcome = ComparisonResult.SIMILAR;
                    break;
                }
            }
            return outcome;
        }

        private static readonly DifferenceEvaluator defaultStopWhenDifferent
            = StopWhenDifferent(Default);

        /// <summary>
        /// Makes the comparison stop as soon as the first "real"
        /// difference is encountered, uses the {@link #Default default}
        /// evaluator to decide which differences leave the documents
        /// simlar.
        /// </summary>
        public static DifferenceEvaluator DefaultStopWhenDifferent {
            get {
                return defaultStopWhenDifferent;
            }
        }

        /// <summary>
        /// Makes the comparison stop as soon as the first "real"
        /// difference is encountered.
        /// </summary>
        /// <param name="nestedEvaluator">provides the initial
        /// decision whether a difference is "real" or still leaves
        /// the documents in a similar state.</param>
        public static DifferenceEvaluator
            StopWhenDifferent(DifferenceEvaluator nestedEvaluator) {
            return delegate(Comparison comparison, ComparisonResult outcome) {
                ComparisonResult r = nestedEvaluator(comparison, outcome);
                return r == ComparisonResult.DIFFERENT
                    ? ComparisonResult.CRITICAL : r;
            };
        }
    }
}