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

import org.junit.Test;
import static org.junit.Assert.*;

public class DOMDifferenceEngineTest {

    private static class ResultGrabber implements DifferenceEvaluator {
        private ComparisonResult outcome = ComparisonResult.CRITICAL;
        public ComparisonResult evaluate(Comparison comparison,
                                         ComparisonResult outcome) {
            this.outcome = outcome;
            return outcome;
        }
    }

    @Test public void compareTwoNulls() {
        ResultGrabber g = new ResultGrabber();
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        d.setDifferenceEvaluator(g);
        assertEquals(ComparisonResult.EQUAL,
                     d.compare(new Comparison(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                              null, null, null,
                                              null, null, null)));
        assertEquals(ComparisonResult.EQUAL, g.outcome);
    }

    @Test public void compareControlNullTestNonNull() {
        ResultGrabber g = new ResultGrabber();
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        d.setDifferenceEvaluator(g);
        assertEquals(ComparisonResult.DIFFERENT,
                     d.compare(new Comparison(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                              null, null, null,
                                              null, null, "")));
        assertEquals(ComparisonResult.DIFFERENT, g.outcome);
    }

    @Test public void compareControlNonNullTestNull() {
        ResultGrabber g = new ResultGrabber();
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        d.setDifferenceEvaluator(g);
        assertEquals(ComparisonResult.DIFFERENT,
                     d.compare(new Comparison(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                              null, null, "",
                                              null, null, null)));
        assertEquals(ComparisonResult.DIFFERENT, g.outcome);
    }

    @Test public void compareTwoDifferentNonNulls() {
        ResultGrabber g = new ResultGrabber();
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        d.setDifferenceEvaluator(g);
        assertEquals(ComparisonResult.DIFFERENT,
                     d.compare(new Comparison(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                              null, null, new Short("1"),
                                              null, null, new Short("2"))));
        assertEquals(ComparisonResult.DIFFERENT, g.outcome);
    }

    @Test public void compareTwoEqualNonNulls() {
        ResultGrabber g = new ResultGrabber();
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        d.setDifferenceEvaluator(g);
        assertEquals(ComparisonResult.EQUAL,
                     d.compare(new Comparison(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                              null, null, new Short("2"),
                                              null, null, new Short("2"))));
        assertEquals(ComparisonResult.EQUAL, g.outcome);
    }

    @Test public void compareNotifiesListener() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        ComparisonListenerSupportTest.Listener l =
            new ComparisonListenerSupportTest.Listener(ComparisonResult.EQUAL);
        d.addComparisonListener(l);
        assertEquals(ComparisonResult.EQUAL,
                     d.compare(new Comparison(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                              null, null, new Short("2"),
                                              null, null, new Short("2"))));
        assertEquals(1, l.getInvocations());
    }

    @Test public void compareUsesResultOfEvaluator() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        ComparisonListenerSupportTest.Listener l =
            new ComparisonListenerSupportTest.Listener(ComparisonResult.SIMILAR);
        d.addComparisonListener(l);
        d.setDifferenceEvaluator(new DifferenceEvaluator() {
                public ComparisonResult evaluate(Comparison comparison,
                                                 ComparisonResult outcome) {
                    return ComparisonResult.SIMILAR;
                }
            });
        assertEquals(ComparisonResult.SIMILAR,
                     d.compare(new Comparison(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                              null, null, new Short("2"),
                                              null, null, new Short("2"))));
        assertEquals(1, l.getInvocations());
    }
}
