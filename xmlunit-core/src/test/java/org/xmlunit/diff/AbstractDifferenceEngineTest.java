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
import java.util.AbstractMap;
import java.util.Map;

import static org.junit.Assert.*;

public abstract class AbstractDifferenceEngineTest {

    protected abstract AbstractDifferenceEngine getDifferenceEngine();

    private static class ResultGrabber implements DifferenceEvaluator {
        private ComparisonResult outcome = ComparisonResult.SIMILAR;
        public ComparisonResult evaluate(Comparison comparison,
                                         ComparisonResult outcome) {
            this.outcome = outcome;
            return outcome;
        }
    }

    @Test public void compareTwoNulls() {
        ResultGrabber g = new ResultGrabber();
        AbstractDifferenceEngine d = getDifferenceEngine();
        d.setDifferenceEvaluator(g);
        assertEquals(wrap(ComparisonResult.EQUAL),
                     d.compare(new Comparison(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                              null, null, null,
                                              null, null, null)));
        assertEquals(ComparisonResult.EQUAL, g.outcome);
    }

    @Test public void compareControlNullTestNonNull() {
        ResultGrabber g = new ResultGrabber();
        AbstractDifferenceEngine d = getDifferenceEngine();
        d.setDifferenceEvaluator(g);
        assertEquals(wrap(ComparisonResult.DIFFERENT),
                     d.compare(new Comparison(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                              null, null, null,
                                              null, null, "")));
        assertEquals(ComparisonResult.DIFFERENT, g.outcome);
    }

    @Test public void compareControlNonNullTestNull() {
        ResultGrabber g = new ResultGrabber();
        AbstractDifferenceEngine d = getDifferenceEngine();
        d.setDifferenceEvaluator(g);
        assertEquals(wrap(ComparisonResult.DIFFERENT),
                     d.compare(new Comparison(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                              null, null, "",
                                              null, null, null)));
        assertEquals(ComparisonResult.DIFFERENT, g.outcome);
    }

    @Test public void compareTwoDifferentNonNulls() {
        ResultGrabber g = new ResultGrabber();
        AbstractDifferenceEngine d = getDifferenceEngine();
        d.setDifferenceEvaluator(g);
        assertEquals(wrap(ComparisonResult.DIFFERENT),
                     d.compare(new Comparison(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                              null, null, new Short("1"),
                                              null, null, new Short("2"))));
        assertEquals(ComparisonResult.DIFFERENT, g.outcome);
    }

    @Test public void compareTwoEqualNonNulls() {
        ResultGrabber g = new ResultGrabber();
        AbstractDifferenceEngine d = getDifferenceEngine();
        d.setDifferenceEvaluator(g);
        assertEquals(wrap(ComparisonResult.EQUAL),
                     d.compare(new Comparison(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                              null, null, new Short("2"),
                                              null, null, new Short("2"))));
        assertEquals(ComparisonResult.EQUAL, g.outcome);
    }

    @Test public void compareNotifiesListener() {
        AbstractDifferenceEngine d = getDifferenceEngine();
        ComparisonListenerSupportTest.Listener l =
            new ComparisonListenerSupportTest.Listener(ComparisonResult.EQUAL);
        d.addComparisonListener(l);
        assertEquals(wrap(ComparisonResult.EQUAL),
                     d.compare(new Comparison(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                              null, null, new Short("2"),
                                              null, null, new Short("2"))));
        assertEquals(1, l.getInvocations());
    }

    @Test public void compareUsesResultOfEvaluator() {
        AbstractDifferenceEngine d = getDifferenceEngine();
        ComparisonListenerSupportTest.Listener l =
            new ComparisonListenerSupportTest.Listener(ComparisonResult.SIMILAR);
        d.addComparisonListener(l);
        d.setDifferenceEvaluator(new DifferenceEvaluator() {
                public ComparisonResult evaluate(Comparison comparison,
                                                 ComparisonResult outcome) {
                    return ComparisonResult.SIMILAR;
                }
            });
        assertEquals(wrap(ComparisonResult.SIMILAR),
                     d.compare(new Comparison(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                              null, null, new Short("2"),
                                              null, null, new Short("2"))));
        assertEquals(1, l.getInvocations());
    }

    @Test public void compareUsesResultOfController() {
        AbstractDifferenceEngine d = getDifferenceEngine();
        ComparisonListenerSupportTest.Listener l =
            new ComparisonListenerSupportTest.Listener(ComparisonResult.SIMILAR);
        d.addComparisonListener(l);
        d.setComparisonController(new ComparisonController() {
                @Override
                public boolean stopDiffing(Difference ignored) {
                    return true;
                }
            });
        assertEquals(wrapAndStop(ComparisonResult.SIMILAR),
                     d.compare(new Comparison(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                              null, null, new Short("1"),
                                              null, null, new Short("2"))));
        assertEquals(1, l.getInvocations());
    }

    protected static Map.Entry<ComparisonResult, Boolean> wrap(ComparisonResult c) {
        return new AbstractMap.SimpleImmutableEntry(c, false);
    }

    protected static Map.Entry<ComparisonResult, Boolean> wrapAndStop(ComparisonResult c) {
        return new AbstractMap.SimpleImmutableEntry(c, true);
    }
}
