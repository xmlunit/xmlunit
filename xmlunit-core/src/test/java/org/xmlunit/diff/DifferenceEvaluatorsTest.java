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

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
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

}
