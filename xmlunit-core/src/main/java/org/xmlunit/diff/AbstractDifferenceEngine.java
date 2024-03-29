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
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.xmlunit.util.Predicate;

/**
 * Useful base-implementation of some parts of the DifferenceEngine
 * interface.
 */
public abstract class AbstractDifferenceEngine implements DifferenceEngine {
    private static final String NOT_BE_NULL = " not be null";
    private static final String LISTENER_MUST_NOT_BE_NULL = "listener must not be null";
    private final ComparisonListenerSupport listeners =
        new ComparisonListenerSupport();
    private NodeMatcher nodeMatcher = new DefaultNodeMatcher();
    private DifferenceEvaluator diffEvaluator = DifferenceEvaluators.Default;
    private ComparisonController comparisonController = ComparisonControllers.Default;
    private Map<String, String> prefix2uri = Collections.emptyMap();
    private Predicate<Attr> attributeFilter = new Predicate<Attr>() {
            @Override
            public boolean test(Attr a) {
                return true;
            }
        };
    private Predicate<Node> nodeFilter = NodeFilters.Default;

    /**
     * Protected default constructor.
     */
    protected AbstractDifferenceEngine() { }

    @Override
    public void addComparisonListener(ComparisonListener l) {
        if (l == null) {
            throw new IllegalArgumentException(LISTENER_MUST_NOT_BE_NULL);
        }
        listeners.addComparisonListener(l);
    }

    @Override
    public void addMatchListener(ComparisonListener l) {
        if (l == null) {
            throw new IllegalArgumentException(LISTENER_MUST_NOT_BE_NULL);
        }
        listeners.addMatchListener(l);
    }

    @Override
    public void addDifferenceListener(ComparisonListener l) {
        if (l == null) {
            throw new IllegalArgumentException(LISTENER_MUST_NOT_BE_NULL);
        }
        listeners.addDifferenceListener(l);
    }

    @Override
    public void setNodeMatcher(NodeMatcher n) {
        if (n == null) {
            throw new IllegalArgumentException("node matcher must"
                                               + NOT_BE_NULL);
        }
        nodeMatcher = n;
    }

    /**
     * Provides access to the configured NodeMatcher.
     * @return the configured NodeMatcher
     */
    protected NodeMatcher getNodeMatcher() {
        return nodeMatcher;
    }

    @Override
    public void setDifferenceEvaluator(DifferenceEvaluator e) {
        if (e == null) {
            throw new IllegalArgumentException("difference evaluator must"
                                               + NOT_BE_NULL);
        }
        diffEvaluator = e;
    }

    /**
     * Provides access to the configured DifferenceEvaluator.
     * @return the configured DifferenceEvaluator
     */
    protected DifferenceEvaluator getDifferenceEvaluator() {
        return diffEvaluator;
    }

    @Override
    public void setComparisonController(ComparisonController c) {
        if (c == null) {
            throw new IllegalArgumentException("comparison controller must"
                                               + NOT_BE_NULL);
        }
        comparisonController = c;
    }

    /**
     * Provides access to the configured ComparisonController.
     * @return the configured ComparisonController
     */
    protected ComparisonController getComparisonController() {
        return comparisonController;
    }

    @Override
    public void setNamespaceContext(Map<String, String> prefix2uri) {
        this.prefix2uri = Collections.unmodifiableMap(prefix2uri);
    }

    /**
     * Provides access to the configured namespace context.
     * @return the configured namespace context
     */
    protected Map<String, String> getNamespaceContext() {
        return prefix2uri;
    }

    @Override
    public void setAttributeFilter(Predicate<Attr> af) {
        if (af == null) {
            throw new IllegalArgumentException("attribute filter must"
                                               + NOT_BE_NULL);
        }
        this.attributeFilter = af;
    }

    /**
     * Provides access to the configured attribute filter.
     * @return the configured attribute filter
     */
    protected Predicate<Attr> getAttributeFilter() {
        return attributeFilter;
    }

    @Override
    public void setNodeFilter(Predicate<Node> nf) {
        if (nf == null) {
            throw new IllegalArgumentException("node filter must not be null");
        }
        this.nodeFilter = nf;
    }

    /**
     * Provides access to the configured nod filter.
     * @return the configured node filter
     */
    protected Predicate<Node> getNodeFilter() {
        return nodeFilter;
    }

    /**
     * Compares the detail values for object equality, lets the
     * difference evaluator and comparison controller evaluate the
     * result, notifies all listeners and returns the outcome.
     *
     * @param comp the comparison to perform
     *
     * @return the outcome as pair of result and a flag that says
     * "stop the whole comparison process" when true.
     */
    protected final ComparisonState compare(Comparison comp) {
        Object controlValue = comp.getControlDetails().getValue();
        Object testValue = comp.getTestDetails().getValue();
        boolean equal = controlValue == null
            ? testValue == null : controlValue.equals(testValue);
        ComparisonResult initial =
            equal ? ComparisonResult.EQUAL : ComparisonResult.DIFFERENT;
        ComparisonResult altered =
            getDifferenceEvaluator().evaluate(comp, initial);
        listeners.fireComparisonPerformed(comp, altered);
        return altered != ComparisonResult.EQUAL
            && getComparisonController().stopDiffing(new Difference(comp, altered))
            ? new FinishedComparisonState(altered)
            : new OngoingComparisonState(altered);
    }

    /**
     * Returns a string representation of the given XPathContext.
     * @param ctx the XPathContext
     * @return a string representation of the given XPathContext
     */
    protected static String getXPath(XPathContext ctx) {
        return ctx == null ? null : ctx.getXPath();
    }

    /**
     * Returns a string representation of the given XPathContext's parent context.
     * @param ctx the XPathContext
     * @return a string representation of the given XPathContext's parent context.
     */
    protected static String getParentXPath(XPathContext ctx) {
        return ctx == null ? null : ctx.getParentXPath();
    }

    /**
     * Encapsulates a comparison that may or may not be performed.
     */
    protected interface DeferredComparison {
        /**
         * Perform the comparison.
         * @return the comparison outcome
         */
        ComparisonState apply();
    }

    /**
     * Encapsulates the current result and a flag that
     * indicates whether comparison should be stopped.
     */
    protected abstract class ComparisonState {
        private final boolean finished;
        private final ComparisonResult result;

        /**
         * Creates a new ComparisonState.
         * @param finished whether comparison should be stopped
         * @param result the current result
         */
        protected ComparisonState(boolean finished, ComparisonResult result) {
            this.finished = finished;
            this.result = result;
        }

        /**
         * Combines the current state with a different comparison.
         * @param newStateProducer may be invoked to produce the next ConditionState
         * @return this if the comparison should be stopped and the result of invoking newStateProducer otherwise.
         */
        protected ComparisonState andThen(DeferredComparison newStateProducer) {
            return finished ? this : newStateProducer.apply();
        }
        /**
         * Maybe combines the current state with a different comparison.
         * @param predicate whether to combine the comparisons
         * @param newStateProducer may be invoked to produce the next ConditionState
         * @return this if the comparison should be stopped or predicate is false and the result of invoking
         * newStateProducer otherwise.
         */
        protected ComparisonState andIfTrueThen(boolean predicate,
                                                DeferredComparison newStateProducer) {
            return predicate ? andThen(newStateProducer) : this;
        }
        /**
         * Combines the current state with a different comparison.
         * @param comp may be evaluated to produce the next ConditionState
         * @return this if the comparison should be stopped and the result of evaluating comp otherwise.
         */
        protected ComparisonState andThen(final Comparison comp) {
            return andThen(new DeferredComparison() {
                    @Override
                    public ComparisonState apply() {
                        return compare(comp);
                    }
                });
        }
        /**
         * Maybe combines the current state with a different comparison.
         * @param predicate whether to combine the comparisons
         * @param comp may be evaluated to produce the next ConditionState
         * @return this if the comparison should be stopped or predicate is false and the result of evaluating comp
         * otherwise.
         */
        protected ComparisonState andIfTrueThen(boolean predicate,
                                                final Comparison comp) {
            return andIfTrueThen(predicate, new DeferredComparison() {
                    @Override
                    public ComparisonState apply() {
                        return compare(comp);
                    }
                });
        }
        @Override
        public String toString() {
            return getClass().getName() + ": current result is " + result;
        }
        @Override
        public boolean equals(Object other) {
            if (other == null || !getClass().equals(other.getClass())) {
                return false;
            }
            ComparisonState cs = (ComparisonState) other;
            return finished == cs.finished && result == cs.result;
        }
        @Override
        public int hashCode() {
            return (finished ? 7 : 1) * result.hashCode();
        }
    }

    /**
     * A comparison state that indicates the comparison should be stopped.
     */
    protected final class FinishedComparisonState extends ComparisonState {
        /**
         * Creates a FinishedComparisonState.
         * @param result the comparison's outcome.
         */
        protected FinishedComparisonState(ComparisonResult result) {
            super(true, result);
        }
    }

    /**
     * A comparison state that indicates the comparison should perform further steps.
     */
    protected final class OngoingComparisonState extends ComparisonState {
        /**
         * Creates an OngoingComparisonState.
         * @param result the comparison's outcome.
         */
        protected OngoingComparisonState(ComparisonResult result) {
            super(false, result);
        }
        /**
         * Creates an OngoingComparisonState with outcome {@link ComparisonResult#EQUAL}.
         */
        protected OngoingComparisonState() {
            this(ComparisonResult.EQUAL);
        }
    }
}
