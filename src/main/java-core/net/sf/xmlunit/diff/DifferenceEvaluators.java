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

import org.w3c.dom.Node;

/**
 * Evaluators used for the base cases.
 */
public final class DifferenceEvaluators {
    private DifferenceEvaluators() { }

    private static final Short CDATA = Node.TEXT_NODE;
    private static final Short TEXT = Node.CDATA_SECTION_NODE;

    /**
     * The "standard" difference evaluator which decides which
     * differences make two XML documents really different and which
     * still leave them similar.
     */
    public static final DifferenceEvaluator Default =
        new DifferenceEvaluator() {
            public ComparisonResult evaluate(Comparison comparison,
                                             ComparisonResult outcome) {
                if (outcome == ComparisonResult.DIFFERENT) {
                    switch (comparison.getType()) {
                    case NODE_TYPE:
                        Short control = (Short) comparison
                            .getControlNodeDetails().getValue();
                        Short test = (Short) comparison
                            .getTestNodeDetails().getValue();
                        if ((control.equals(TEXT) && test.equals(CDATA))
                            ||
                            (control.equals(CDATA) && test.equals(TEXT))) {
                            outcome = ComparisonResult.SIMILAR;
                        }
                        break;
                    case HAS_DOCTYPE_DECLARATION:
                    case DOCTYPE_SYSTEM_ID:
                    case SCHEMA_LOCATION:
                    case NO_NAMESPACE_SCHEMA_LOCATION:
                    case NAMESPACE_PREFIX:
                    case ATTR_VALUE_EXPLICITLY_SPECIFIED:
                    case CHILD_NODELIST_SEQUENCE:
                    case XML_ENCODING:
                        outcome = ComparisonResult.SIMILAR;
                        break;
                    }
                }
                return outcome;
            }
        };

    /**
     * Makes the comparison stop as soon as the first "real"
     * difference is encountered, uses the {@link #Default default}
     * evaluator to decide which differences leave the documents
     * simlar.
     */
    public static final DifferenceEvaluator DefaultStopWhenDifferent
        = stopWhenDifferent(Default);

    /**
     * Makes the comparison stop as soon as the first "real"
     * difference is encountered.
     * @param nestedEvaluator provides the initial decision whether a
     * difference is "real" or still leaves the documents in a similar
     * state.
     */
    public static DifferenceEvaluator
        stopWhenDifferent(final DifferenceEvaluator nestedEvaluator) {
        return new DifferenceEvaluator() {
            public ComparisonResult evaluate(Comparison comparison,
                                             ComparisonResult outcome) {
                ComparisonResult r = nestedEvaluator.evaluate(comparison,
                                                              outcome);
                return r == ComparisonResult.DIFFERENT
                    ? ComparisonResult.CRITICAL : r;
            }
        };
    }
}
