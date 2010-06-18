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

import java.util.Arrays;
import java.util.HashSet;
import org.junit.Test;

import static org.junit.Assert.*;

public class ComparisonListenerSupportTest {

    @Test public void dispatchesOnOutcome() {
        ComparisonListenerSupport s = new ComparisonListenerSupport();
        Listener c, m, d;
        s.addComparisonListener(c = new Listener(ComparisonResult.EQUAL,
                                                 ComparisonResult.SIMILAR,
                                                 ComparisonResult.DIFFERENT,
                                                 ComparisonResult.CRITICAL));
        s.addMatchListener(m = new Listener(ComparisonResult.EQUAL));
        s.addDifferenceListener(d = new Listener(ComparisonResult.SIMILAR,
                                                 ComparisonResult.DIFFERENT,
                                                 ComparisonResult.CRITICAL));
        for (ComparisonResult r : new ComparisonResult[] {
                ComparisonResult.EQUAL,
                ComparisonResult.SIMILAR,
                ComparisonResult.DIFFERENT,
                ComparisonResult.CRITICAL
            }) {
            s.fireComparisonPerformed(null, r);
        }

        assertEquals(4, c.invocations);
        assertEquals(1, m.invocations);
        assertEquals(3, d.invocations);
    }

    @Test public void noListenersDontCauseProblems() {
        ComparisonListenerSupport s = new ComparisonListenerSupport();
        s.fireComparisonPerformed(null, ComparisonResult.EQUAL);
    }

    static class Listener implements ComparisonListener {
        private final HashSet<ComparisonResult> acceptable =
            new HashSet<ComparisonResult>();
        private int invocations = 0;

        Listener(ComparisonResult... accept) {
            acceptable.addAll(Arrays.asList(accept));
        }

        public void comparisonPerformed(Comparison comparison,
                                        ComparisonResult outcome) {
            invocations++;
            if (!acceptable.contains(outcome)) {
                fail("unexpected outcome: " + outcome);
            }
        }

        int getInvocations() {
            return invocations;
        }
    }
}
