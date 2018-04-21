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
package org.xmlunit.diff;

import java.util.Arrays;
import java.util.EnumSet;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Evaluators used for the base cases.
 */
public final class DifferenceEvaluators {

    private static final Short CDATA = Node.CDATA_SECTION_NODE;
    private static final Short TEXT = Node.TEXT_NODE;

    /**
     * Difference evaluator that just echos the result passed in.
     */
    public static final DifferenceEvaluator Accept =
        new DifferenceEvaluator() {
            @Override
            public ComparisonResult evaluate(Comparison comparison,
                                             ComparisonResult outcome) {
                return outcome;
            }
        };

    /**
     * The "standard" difference evaluator which decides which
     * differences make two XML documents really different and which
     * still leave them similar.
     */
    public static final DifferenceEvaluator Default =
        new DifferenceEvaluator() {
            @Override
            public ComparisonResult evaluate(Comparison comparison,
                                             ComparisonResult outcome) {
                if (outcome == ComparisonResult.DIFFERENT) {
                    switch (comparison.getType()) {
                    case NODE_TYPE:
                        Short control = (Short) comparison
                            .getControlDetails().getValue();
                        Short test = (Short) comparison
                            .getTestDetails().getValue();
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
                    default:
                        break;
                    }
                }
                return outcome;
            }
        };

    private DifferenceEvaluators() {}

    /**
     * Combines multiple DifferenceEvaluators so that the first one
     * that changes the outcome wins.
     */
    public static DifferenceEvaluator
        first(final DifferenceEvaluator... evaluators) {
        return new DifferenceEvaluator() {
            @Override
            public ComparisonResult evaluate(Comparison comparison,
                                             ComparisonResult orig) {
                for (DifferenceEvaluator ev : evaluators) {
                    ComparisonResult evaluated = ev.evaluate(comparison, orig);
                    if (evaluated != orig) {
                        return evaluated;
                    }
                }
                return orig;
            }
        };
    }

    /**
     * Combines multiple DifferenceEvaluators so that the result of the
     * first Evaluator will be passed to the next Evaluator.
     */
    public static DifferenceEvaluator
        chain(final DifferenceEvaluator... evaluators) {
        return new DifferenceEvaluator() {
            @Override
            public ComparisonResult evaluate(Comparison comparison, ComparisonResult orig) {
                ComparisonResult finalResult = orig;
                for (DifferenceEvaluator ev : evaluators) {
                    ComparisonResult evaluated = ev.evaluate(comparison, finalResult);
                    finalResult = evaluated;
                }
                return finalResult;
            }
        };
    }

    /**
     * Creates a DifferenceEvaluator that returns a EQUAL result for
     * differences found in one of the given ComparisonTypes.
     *
     * @since XMLUnit 2.1.0
     */
    public static DifferenceEvaluator downgradeDifferencesToEqual(ComparisonType... types) {
        return recordDifferencesAs(ComparisonResult.EQUAL, types);
    }

    /**
     * Creates a DifferenceEvaluator that returns a SIMILAR result for
     * differences (Comparisons that are not EQUAL) found in one of
     * the given ComparisonTypes.
     *
     * @since XMLUnit 2.1.0
     */
    public static DifferenceEvaluator downgradeDifferencesToSimilar(ComparisonType... types) {
        return recordDifferencesAs(ComparisonResult.SIMILAR, types);
    }

    /**
     * Creates a DifferenceEvaluator that returns a DIFFERENT result
     * for differences (Comparisons that are not EQUAL) found in one
     * of the given ComparisonTypes.
     *
     * @since XMLUnit 2.1.0
     */
    public static DifferenceEvaluator upgradeDifferencesToDifferent(ComparisonType... types) {
        return recordDifferencesAs(ComparisonResult.DIFFERENT, types);
    }

    /**
     * Ignore any differences that are part of the <a
     * href="https://www.w3.org/TR/2008/REC-xml-20081126/#sec-prolog-dtd">XML
     * prolog</a>.
     *
     * <p>Here "ignore" means return {@code ComparisonResult.EQUAL}.</p>
     *
     * @since XMLUnit 2.1.0
     */
    public static DifferenceEvaluator ignorePrologDifferences() {
        return new DifferenceEvaluator() {
            @Override
            public ComparisonResult evaluate(Comparison comparison, ComparisonResult orig) {
                return belongsToProlog(comparison, true)
                    || isSequenceOfRootElement(comparison)
                    ? ComparisonResult.EQUAL : orig;
            }
        };
    }

    /**
     * Ignore any differences except differences inside the doctype
     * declaration that are part of the <a
     * href="https://www.w3.org/TR/2008/REC-xml-20081126/#sec-prolog-dtd">XML
     * prolog</a>.
     *
     * <p>Here "ignore" means return {@code ComparisonResult.EQUAL}.</p>
     *
     * <p>This is one of the building blocks for mimicing the behavior
     * of XMLUnit for Java 1.x. In order to get the same behavior you
     * need:</p>
     *
     * <pre>
     * chain(Default, // so CDATA and Text are the same
     *       ignorePrologDifferencesExceptDoctype()) // so most of the prolog is ignored
     * </pre>
     *
     * <p>In general different doctype declarations will be ignored
     * because of {@link NodeFilters.Default}, so if you want to
     * detect these differences you need to pick a different {@code
     * NodeFilter}.</p>
     *
     * @since XMLUnit 2.1.0
     */
    public static DifferenceEvaluator ignorePrologDifferencesExceptDoctype() {
        return new DifferenceEvaluator() {
            @Override
            public ComparisonResult evaluate(Comparison comparison, ComparisonResult orig) {
                return belongsToProlog(comparison, false)
                    || isSequenceOfRootElement(comparison)
                    ? ComparisonResult.EQUAL : orig;
            }
        };
    }

    private static DifferenceEvaluator recordDifferencesAs(final ComparisonResult outcome,
                                                           ComparisonType... types) {
        final EnumSet<ComparisonType> comparisonTypes =
            EnumSet.copyOf(Arrays.asList(types));
        return new DifferenceEvaluator() {
            @Override
            public ComparisonResult evaluate(Comparison comparison, ComparisonResult orig) {
                return orig != ComparisonResult.EQUAL
                    && comparisonTypes.contains(comparison.getType())
                    ? outcome : orig;
            }
        };
    }

    private static boolean belongsToProlog(Comparison comparison,
                                           boolean ignoreDoctypeDeclarationAsWell) {
        if (comparison.getType().isDoctypeComparison()) {
            return ignoreDoctypeDeclarationAsWell;
        }
        return belongsToProlog(comparison.getControlDetails().getTarget(),
                               ignoreDoctypeDeclarationAsWell)
            || belongsToProlog(comparison.getTestDetails().getTarget(),
                               ignoreDoctypeDeclarationAsWell);
    }

    private static boolean belongsToProlog(Node n,
                                           boolean ignoreDoctypeDeclarationAsWell) {
        if (n == null || n instanceof Element) {
            return false;
        }
        if (!ignoreDoctypeDeclarationAsWell && n instanceof DocumentType) {
            return false;
        }
        if (n instanceof Document) {
            return true;
        }
        return belongsToProlog(n.getParentNode(), ignoreDoctypeDeclarationAsWell);
    }

    private static boolean isSequenceOfRootElement(Comparison comparison) {
        return comparison.getType() == ComparisonType.CHILD_NODELIST_SEQUENCE
            && comparison.getControlDetails().getTarget() instanceof Element
            && comparison.getControlDetails().getTarget().getParentNode() instanceof Document;
    }

}
