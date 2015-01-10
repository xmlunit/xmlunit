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

import java.util.Collections;
import java.util.Map;

/**
 * Useful base-implementation of some parts of the DifferenceEngine
 * interface.
 */
public abstract class AbstractDifferenceEngine implements DifferenceEngine {
    private final ComparisonListenerSupport listeners =
        new ComparisonListenerSupport();
    private NodeMatcher nodeMatcher = new DefaultNodeMatcher();
    private DifferenceEvaluator diffEvaluator = DifferenceEvaluators.Default;
    private Map<String, String> uri2Prefix = Collections.emptyMap();

    protected AbstractDifferenceEngine() { }

    @Override
    public void addComparisonListener(ComparisonListener l) {
        if (l == null) {
            throw new IllegalArgumentException("listener must not be null");
        }
        listeners.addComparisonListener(l);
    }

    @Override
    public void addMatchListener(ComparisonListener l) {
        if (l == null) {
            throw new IllegalArgumentException("listener must not be null");
        }
        listeners.addMatchListener(l);
    }

    @Override
    public void addDifferenceListener(ComparisonListener l) {
        if (l == null) {
            throw new IllegalArgumentException("listener must not be null");
        }
        listeners.addDifferenceListener(l);
    }

    @Override
    public void setNodeMatcher(NodeMatcher n) {
        if (n == null) {
            throw new IllegalArgumentException("node matcher must"
                                               + " not be null");
        }
        nodeMatcher = n;
    }

    /**
     * Provides access to the configured NodeMatcher.
     */
    protected NodeMatcher getNodeMatcher() {
        return nodeMatcher;
    }

    @Override
    public void setDifferenceEvaluator(DifferenceEvaluator e) {
        if (e == null) {
            throw new IllegalArgumentException("difference evaluator must"
                                               + " not be null");
        }
        diffEvaluator = e;
    }

    /**
     * Provides access to the configured DifferenceEvaluator.
     */
    protected DifferenceEvaluator getDifferenceEvaluator() {
        return diffEvaluator;
    }

    @Override
    public void setNamespaceContext(Map<String, String> uri2Prefix) {
        this.uri2Prefix = Collections.unmodifiableMap(uri2Prefix);
    }

    /**
     * Provides access to the configured namespace context.
     */
    protected Map<String, String> getNamespaceContext() {
        return uri2Prefix;
    }

    /**
     * Compares the detail values for object equality, lets the
     * difference evaluator evaluate the result, notifies all
     * listeners and returns the outcome.
     */
    protected final ComparisonResult compare(Comparison comp) {
        Object controlValue = comp.getControlDetails().getValue();
        Object testValue = comp.getTestDetails().getValue();
        boolean equal = controlValue == null
            ? testValue == null : controlValue.equals(testValue);
        ComparisonResult initial =
            equal ? ComparisonResult.EQUAL : ComparisonResult.DIFFERENT;
        ComparisonResult altered =
            getDifferenceEvaluator().evaluate(comp, initial);
        listeners.fireComparisonPerformed(comp, altered);
        return altered;
    }

    /**
     * Returns a function that compares the detail values for object
     * equality, lets the difference evaluator evaluate the result,
     * notifies all listeners and returns the outcome.
     */
    protected final DeferredComparison comparer(final Comparison comp) {
        return new DeferredComparison() {
            public ComparisonResult apply() {
                return compare(comp);
            }
        };
    }

    /**
     * Returns a string representation of the given XPathContext.
     */
    protected static String getXPath(XPathContext ctx) {
        return ctx == null ? null : ctx.getXPath();
    }

    /**
     * Encapsulates a comparision that may or may not be performed.
     */
    protected interface DeferredComparison {
        /**
         * Perform the comparison.
         */
        ComparisonResult apply();
    }

    /**
     * Chain of comparisons where the last comparison performed
     * determines the final result but the first comparison with a
     * critical difference stops the chain.
     */
    protected static class ComparisonChain {
        private ComparisonResult currentResult;
        /**
         * Creates a chain without any parts.
         */
        public ComparisonChain() {
            this(ComparisonResult.EQUAL);
        }
        /**
         * Creates a chain with an initial value.
         */
        public ComparisonChain(ComparisonResult firstResult) {
            currentResult = firstResult;
        }
        /**
         * Adds a new part to the chain.
         *
         * <p>If the current result of the chain is already critical
         * the new part will be ognored, otherwise it is evaluated and
         * its outcome is the new result of the chain.</p>
         */
        public ComparisonChain andThen(DeferredComparison next) {
            if (currentResult != ComparisonResult.CRITICAL) {
                currentResult = next.apply();
            }
            return this;
        }
        /**
         * Adds a new part to the chain if the given predicate is true.
         */
        public ComparisonChain andIfTrueThen(boolean evalNext,
                                             DeferredComparison next) {
            return evalNext ? andThen(next) : this;
        }
        /**
         * Returns the current result of the evaluated chain.
         */
        public ComparisonResult getFinalResult() {
            return currentResult;
        }
    }
}
