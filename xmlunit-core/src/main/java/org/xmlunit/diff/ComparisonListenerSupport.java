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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Encapsulates support for {@link ComparisonListener}s so it can be
 * reused by different implementations of {@link DifferenceEngine}.
 */
public class ComparisonListenerSupport {
    private final List<ComparisonListener> compListeners =
        new CopyOnWriteArrayList<ComparisonListener>();
    private final List<ComparisonListener> matchListeners =
        new CopyOnWriteArrayList<ComparisonListener>();
    private final List<ComparisonListener> diffListeners =
        new CopyOnWriteArrayList<ComparisonListener>();

    /**
     * Registers a listener that is notified of each comparison.
     * @param l the listener to add
     */
    public void addComparisonListener(ComparisonListener l) {
        compListeners.add(l);
    }

    /**
     * Registers a listener that is notified of each comparison with
     * outcome {@link ComparisonResult#EQUAL}.
     * @param l the listener to add
     */
    public void addMatchListener(ComparisonListener l) {
        matchListeners.add(l);
    }

    /**
     * Registers a listener that is notified of each comparison with
     * outcome other than {@link ComparisonResult#EQUAL}.
     * @param l the listener to add
     */
    public void addDifferenceListener(ComparisonListener l) {
        diffListeners.add(l);
    }

    /**
     * Propagates the result of a comparision to all registered
     * listeners.
     * @param comparison the comparison
     * @param outcome the result of the comparison
     */
    public void fireComparisonPerformed(Comparison comparison,
                                        ComparisonResult outcome) {
        fire(comparison, outcome, compListeners);
        if (outcome == ComparisonResult.EQUAL) {
            fire(comparison, outcome, matchListeners);
        } else {
            fire(comparison, outcome, diffListeners);
        }
    }

    private static void fire(Comparison comparison, ComparisonResult outcome,
                             List<ComparisonListener> listeners) {
        if (!listeners.isEmpty()) {
            for (ComparisonListener l : listeners) {
                l.comparisonPerformed(comparison, outcome);
            }
        }
    }
}
