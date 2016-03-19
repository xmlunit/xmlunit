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

import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.Source;

import org.junit.Test;
import org.xmlunit.builder.Input;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

public class DifferenceEvaluatorsTest {

    private static class Evaluator implements DifferenceEvaluator {
        private boolean called = false;
        private final ComparisonResult ret;
        private ComparisonResult orig;
        private Evaluator(ComparisonResult ret) {
            this.ret = ret;
        }
        @Override
        public ComparisonResult evaluate(Comparison comparison,
                                         ComparisonResult orig) {
            called = true;
            this.orig = orig;
            return ret;
        }
    }

    @Test public void emptyFirstJustWorks() {
        DifferenceEvaluator d = DifferenceEvaluators.first();
        assertEquals(ComparisonResult.DIFFERENT,
                     d.evaluate(null, ComparisonResult.DIFFERENT));
    }

    @Test public void firstChangeWinsInFirst() {
        Evaluator e1 = new Evaluator(ComparisonResult.DIFFERENT);
        Evaluator e2 = new Evaluator(ComparisonResult.EQUAL);
        DifferenceEvaluator d = DifferenceEvaluators.first(e1, e2);
        assertEquals(ComparisonResult.DIFFERENT,
                     d.evaluate(null, ComparisonResult.SIMILAR));
        assertTrue(e1.called);
        assertFalse(e2.called);
        e1.called = false;
        assertEquals(ComparisonResult.EQUAL,
                     d.evaluate(null, ComparisonResult.DIFFERENT));
        assertTrue(e1.called);
        assertTrue(e2.called);
    }

    @Test public void allEvaluatorsAreCalledInSequence() {
        Evaluator e1 = new Evaluator(ComparisonResult.SIMILAR);
        Evaluator e2 = new Evaluator(ComparisonResult.EQUAL);
        DifferenceEvaluator d = DifferenceEvaluators.chain(e1, e2);

        assertEquals(ComparisonResult.EQUAL, d.evaluate(null, ComparisonResult.DIFFERENT));

        assertTrue(e1.called);
        assertThat(e1.orig, is(ComparisonResult.DIFFERENT)); // passed initial ComparisonResult
        assertTrue(e2.called);
        assertThat(e2.orig, is(ComparisonResult.SIMILAR)); // passed ComparisonResult from e1
    }

    @Test
    public void downgradeDifferencesToEqualDowngradesMatchingTypes() {
        DifferenceEvaluator d = DifferenceEvaluators
            .downgradeDifferencesToEqual(ComparisonType.XML_VERSION,
                                         ComparisonType.XML_STANDALONE);
        assertEquals(ComparisonResult.EQUAL,
                     d.evaluate(new Comparison(ComparisonType.XML_VERSION,
                                               null, null, null, null,
                                               null, null, null, null),
                                ComparisonResult.SIMILAR));
    }

    @Test
    public void downgradeDifferencesToEqualLeavesUnknownTypesAlone() {
        DifferenceEvaluator d = DifferenceEvaluators
            .downgradeDifferencesToEqual(ComparisonType.XML_VERSION,
                                         ComparisonType.XML_STANDALONE);
        assertEquals(ComparisonResult.SIMILAR,
                     d.evaluate(new Comparison(ComparisonType.XML_ENCODING,
                                               null, null, null, null,
                                               null, null, null, null),
                                ComparisonResult.SIMILAR));
    }

    @Test
    public void downgradeDifferencesToSimilarDowngradesMatchingTypes() {
        DifferenceEvaluator d = DifferenceEvaluators
            .downgradeDifferencesToSimilar(ComparisonType.XML_VERSION,
                                           ComparisonType.XML_STANDALONE);
        assertEquals(ComparisonResult.SIMILAR,
                     d.evaluate(new Comparison(ComparisonType.XML_VERSION,
                                               null, null, null, null,
                                               null, null, null, null),
                                ComparisonResult.DIFFERENT));
    }

    @Test
    public void downgradeDifferencesToSimilarLeavesUnknownTypesAlone() {
        DifferenceEvaluator d = DifferenceEvaluators
            .downgradeDifferencesToSimilar(ComparisonType.XML_VERSION,
                                           ComparisonType.XML_STANDALONE);
        assertEquals(ComparisonResult.DIFFERENT,
                     d.evaluate(new Comparison(ComparisonType.XML_ENCODING,
                                               null, null, null, null,
                                               null, null, null, null),
                                ComparisonResult.DIFFERENT));
    }

    @Test
    public void downgradeDifferencesToSimilarLeavesEqualResultsAlone() {
        DifferenceEvaluator d = DifferenceEvaluators
            .downgradeDifferencesToSimilar(ComparisonType.XML_VERSION,
                                           ComparisonType.XML_STANDALONE);
        assertEquals(ComparisonResult.EQUAL,
                     d.evaluate(new Comparison(ComparisonType.XML_VERSION,
                                               null, null, null, null,
                                               null, null, null, null),
                                ComparisonResult.EQUAL));
    }

    @Test
    public void upgradeDifferencesToDifferentUpgradesMatchingTypes() {
        DifferenceEvaluator d = DifferenceEvaluators
            .upgradeDifferencesToDifferent(ComparisonType.XML_VERSION,
                                           ComparisonType.XML_STANDALONE);
        assertEquals(ComparisonResult.DIFFERENT,
                     d.evaluate(new Comparison(ComparisonType.XML_VERSION,
                                               null, null, null, null,
                                               null, null, null, null),
                                ComparisonResult.SIMILAR));
    }

    @Test
    public void upgradeDifferencesToDifferentLeavesUnknownTypesAlone() {
        DifferenceEvaluator d = DifferenceEvaluators
            .upgradeDifferencesToDifferent(ComparisonType.XML_VERSION,
                                           ComparisonType.XML_STANDALONE);
        assertEquals(ComparisonResult.SIMILAR,
                     d.evaluate(new Comparison(ComparisonType.XML_ENCODING,
                                               null, null, null, null,
                                               null, null, null, null),
                                ComparisonResult.SIMILAR));
    }

    @Test
    public void upgradeDifferencesToDifferentLeavesEqualResultsAlone() {
        DifferenceEvaluator d = DifferenceEvaluators
            .upgradeDifferencesToDifferent(ComparisonType.XML_VERSION,
                                           ComparisonType.XML_STANDALONE);
        assertEquals(ComparisonResult.EQUAL,
                     d.evaluate(new Comparison(ComparisonType.XML_VERSION,
                                               null, null, null, null,
                                               null, null, null, null),
                                ComparisonResult.EQUAL));
    }

    @Test
    public void ignorePrologIgnoresAdditionalContentInProlog() {
        List<Comparison> differences =
            compare("<?xml version = \"1.0\" encoding = \"UTF-8\"?>"
                    + "<!-- some comment -->"
                    + "<?foo some PI ?>\n"
                    + "<bar/>",
                    "<bar/>");
        assertThat(differences, hasSize(0));
    }

    @Test
    public void ignorePrologIgnoresXMLDeclarationDifferences() {
        List<Comparison> differences =
            compare(
                    "<?xml version = \"1.0\" encoding = \"UTF-8\"?>"
                    + "<!-- some comment -->"
                    + "<?foo some PI ?>\n"
                    + "<bar/>",
                    "<?xml version = \"1.0\" encoding = \"UTF-8\"?>"
                    + "<!-- some comment -->"
                    + "<?foo some PI ?>\n"
                    + "<bar/>");
        assertThat(differences, hasSize(0));
    }

    @Test
    public void ignorePrologIgnoresPrologCommentDifferences() {
        List<Comparison> differences =
            compare("<?xml version = \"1.0\" encoding = \"UTF-8\"?>"
                    + "<!-- some comment -->"
                    + "<?foo some PI ?>\n"
                    + "<bar/>",
                    "<?xml version = \"1.0\" encoding = \"UTF-8\"?>"
                    + "<?foo some PI ?>\n"
                    + "<!-- some other comment -->"
                    + "<bar/>");
        assertThat(differences, hasSize(0));
    }

    @Test
    public void ignorePrologIgnoresPrologProcessingInstructionDifferences() {
        List<Comparison> differences =
            compare("<?xml version = \"1.0\" encoding = \"UTF-8\"?>"
                    + "<!-- some comment -->"
                    + "<?foo some PI ?>\n"
                    + "<bar/>",
                    "<?xml version = \"1.0\" encoding = \"UTF-8\"?>"
                    + "<!-- some comment -->"
                    + "<?foo some other PI ?>\n"
                    + "<bar/>");
        assertThat(differences, hasSize(0));
    }

    @Test
    public void ignorePrologIgnoresPrologWhitespaceDifferences() {
        List<Comparison> differences =
            compare("<?xml version = \"1.0\" encoding = \"UTF-8\"?>"
                    + "<!-- some comment -->"
                    + "<?foo some PI ?>\n"
                    + "<bar/>",
                    "<?xml version = \"1.0\" encoding = \"UTF-8\"?>"
                    + "<!-- some comment --> "
                    + "<?foo some PI ?>"
                    + "<bar/>");
        assertThat(differences, hasSize(0));
    }

    @Test
    public void ignorePrologIgnoresDoesntIgnoreElementName() {
        List<Comparison> differences =
            compare("<?xml version = \"1.0\" encoding = \"UTF-8\"?>"
                    + "<!-- some comment -->"
                    + "<?foo some PI ?>\n"
                    + "<foo/>",
                    "<?xml version = \"1.0\" encoding = \"UTF-8\"?>"
                    + "<!-- some comment -->"
                    + "<?foo some PI ?>\n"
                    + "<bar/>");
        assertThat(differences, not(hasSize(0)));
    }

    @Test
    public void ignorePrologDoesntIgnoreCommentsOutsideOfProlog() {
        List<Comparison> differences =
            compare("<?xml version = \"1.0\" encoding = \"UTF-8\"?>"
                    + "<foo>"
                    + "<!-- some comment -->"
                    + "</foo>",
                    "<?xml version = \"1.0\" encoding = \"UTF-8\"?>"
                    + "<foo>"
                    + "<!-- some other comment -->"
                    + "</foo>");
        assertThat(differences, not(hasSize(0)));
    }

    @Test
    public void ignorePrologDoesntIgnorePIsOutsideOfProlog() {
        List<Comparison> differences =
            compare("<?xml version = \"1.0\" encoding = \"UTF-8\"?>"
                    + "<foo>"
                    + "<?foo some PI ?>\n"
                    + "</foo>",
                    "<?xml version = \"1.0\" encoding = \"UTF-8\"?>"
                    + "<foo>"
                    + "<?foo some other PI ?>\n"
                    + "</foo>");
        assertThat(differences, not(hasSize(0)));
    }

    @Test
    public void ignorePrologDoesntIgnoreWhitespaceOutsideOfProlog() {
        List<Comparison> differences =
            compare("<?xml version = \"1.0\" encoding = \"UTF-8\"?>"
                    + "<foo>"
                    + "\n"
                    + "</foo>",
                    "<?xml version = \"1.0\" encoding = \"UTF-8\"?>"
                    + "<foo>"
                    + "</foo>");
        assertThat(differences, not(hasSize(0)));
    }

    @Test
    public void ignorePrologIgnoresPresenceOfDoctype() {
        List<Comparison> differences =
            compare("<!DOCTYPE test ["
                    + "<!ELEMENT bar EMPTY>"
                    + "]>"
                    + "<bar/>",
                    "<bar/>");
        assertThat(differences, hasSize(0));
    }

    @Test
    public void ignorePrologIgnoresNameOfDoctype() {
        List<Comparison> differences =
            compare("<!DOCTYPE foo ["
                    + "<!ELEMENT bar EMPTY>"
                    + "]>"
                    + "<bar/>",
                    "<!DOCTYPE test ["
                    + "<!ELEMENT bar EMPTY>"
                    + "]>"
                    + "<bar/>");
        assertThat(differences, hasSize(0));
    }

    @Test
    public void ignorePrologExceptDoctypeIgnoresAdditionalContentInProlog() {
        List<Comparison> differences =
            compare("<?xml version = \"1.0\" encoding = \"UTF-8\"?>"
                    + "<!-- some comment -->"
                    + "<?foo some PI ?>\n"
                    + "<bar/>",
                    "<bar/>");
        assertThat(differences, hasSize(0));
    }

    @Test
    public void ignorePrologExceptDoctypeIgnoresXMLDeclarationDifferences() {
        List<Comparison> differences =
            compare(
                    "<?xml version = \"1.0\" encoding = \"UTF-8\"?>"
                    + "<!-- some comment -->"
                    + "<?foo some PI ?>\n"
                    + "<bar/>",
                    "<?xml version = \"1.0\" encoding = \"UTF-8\"?>"
                    + "<!-- some comment -->"
                    + "<?foo some PI ?>\n"
                    + "<bar/>",
                    false);
        assertThat(differences, hasSize(0));
    }

    @Test
    public void ignorePrologExceptDoctypeIgnoresPrologCommentDifferences() {
        List<Comparison> differences =
            compare("<?xml version = \"1.0\" encoding = \"UTF-8\"?>"
                    + "<!-- some comment -->"
                    + "<?foo some PI ?>\n"
                    + "<bar/>",
                    "<?xml version = \"1.0\" encoding = \"UTF-8\"?>"
                    + "<?foo some PI ?>\n"
                    + "<!-- some other comment -->"
                    + "<bar/>",
                    false);
        assertThat(differences, hasSize(0));
    }

    @Test
    public void ignorePrologExceptDoctypeIgnoresPrologProcessingInstructionDifferences() {
        List<Comparison> differences =
            compare("<?xml version = \"1.0\" encoding = \"UTF-8\"?>"
                    + "<!-- some comment -->"
                    + "<?foo some PI ?>\n"
                    + "<bar/>",
                    "<?xml version = \"1.0\" encoding = \"UTF-8\"?>"
                    + "<!-- some comment -->"
                    + "<?foo some other PI ?>\n"
                    + "<bar/>",
                    false);
        assertThat(differences, hasSize(0));
    }

    @Test
    public void ignorePrologExceptDoctypeIgnoresPrologWhitespaceDifferences() {
        List<Comparison> differences =
            compare("<?xml version = \"1.0\" encoding = \"UTF-8\"?>"
                    + "<!-- some comment -->"
                    + "<?foo some PI ?>\n"
                    + "<bar/>",
                    "<?xml version = \"1.0\" encoding = \"UTF-8\"?>"
                    + "<!-- some comment --> "
                    + "<?foo some PI ?>"
                    + "<bar/>",
                    false);
        assertThat(differences, hasSize(0));
    }

    @Test
    public void ignorePrologExceptDoctypeIgnoresDoesntIgnoreElementName() {
        List<Comparison> differences =
            compare("<?xml version = \"1.0\" encoding = \"UTF-8\"?>"
                    + "<!-- some comment -->"
                    + "<?foo some PI ?>\n"
                    + "<foo/>",
                    "<?xml version = \"1.0\" encoding = \"UTF-8\"?>"
                    + "<!-- some comment -->"
                    + "<?foo some PI ?>\n"
                    + "<bar/>",
                    false);
        assertThat(differences, not(hasSize(0)));
    }

    @Test
    public void ignorePrologExceptDoctypeDoesntIgnoreCommentsOutsideOfProlog() {
        List<Comparison> differences =
            compare("<?xml version = \"1.0\" encoding = \"UTF-8\"?>"
                    + "<foo>"
                    + "<!-- some comment -->"
                    + "</foo>",
                    "<?xml version = \"1.0\" encoding = \"UTF-8\"?>"
                    + "<foo>"
                    + "<!-- some other comment -->"
                    + "</foo>",
                    false);
        assertThat(differences, not(hasSize(0)));
    }

    @Test
    public void ignorePrologExceptDoctypeDoesntIgnorePIsOutsideOfProlog() {
        List<Comparison> differences =
            compare("<?xml version = \"1.0\" encoding = \"UTF-8\"?>"
                    + "<foo>"
                    + "<?foo some PI ?>\n"
                    + "</foo>",
                    "<?xml version = \"1.0\" encoding = \"UTF-8\"?>"
                    + "<foo>"
                    + "<?foo some other PI ?>\n"
                    + "</foo>",
                    false);
        assertThat(differences, not(hasSize(0)));
    }

    @Test
    public void ignorePrologExceptDoctypeDoesntIgnoreWhitespaceOutsideOfProlog() {
        List<Comparison> differences =
            compare("<?xml version = \"1.0\" encoding = \"UTF-8\"?>"
                    + "<foo>"
                    + "\n"
                    + "</foo>",
                    "<?xml version = \"1.0\" encoding = \"UTF-8\"?>"
                    + "<foo>"
                    + "</foo>",
                    false);
        assertThat(differences, not(hasSize(0)));
    }

    @Test
    public void ignorePrologExceptDoctypeDoesntIgnorePresenceOfDoctype() {
        List<Comparison> differences =
            compare("<!DOCTYPE test ["
                    + "<!ELEMENT bar EMPTY>"
                    + "]>"
                    + "<bar/>",
                    "<bar/>",
                    false);
        assertThat(differences, not(hasSize(0)));
    }

    @Test
    public void ignorePrologExceptDoctypeDoesntIgnoreNameOfDoctype() {
        List<Comparison> differences =
            compare("<!DOCTYPE foo ["
                    + "<!ELEMENT bar EMPTY>"
                    + "]>"
                    + "<bar/>",
                    "<!DOCTYPE test ["
                    + "<!ELEMENT bar EMPTY>"
                    + "]>"
                    + "<bar/>",
                    false);
        assertThat(differences, not(hasSize(0)));
    }

    private List<Comparison> compare(String controlXml, String testXml) {
        return compare(controlXml, testXml, true);
    }

    private List<Comparison> compare(String controlXml, String testXml,
                                     boolean ignoreDoctypeDeclarationAsWell) {
        Source control = Input.from(controlXml) .build();
        Source test = Input.from(testXml) .build();
        DOMDifferenceEngine e = new DOMDifferenceEngine();
        if (ignoreDoctypeDeclarationAsWell) {
            e.setDifferenceEvaluator(DifferenceEvaluators.ignorePrologDifferences());
        } else {
            e.setDifferenceEvaluator(DifferenceEvaluators.ignorePrologDifferencesExceptDoctype());
        }
        final List<Comparison> differences = new ArrayList<Comparison>();
        e.addDifferenceListener(new ComparisonListener() {
                @Override
                public void comparisonPerformed(Comparison comparison,
                                                ComparisonResult outcome) {
                    differences.add(comparison);
                }
            });
        e.compare(control, test);
        return differences;
    }
}
