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

    public void addComparisonListener(ComparisonListener l) {
        if (l == null) {
            throw new IllegalArgumentException("listener must not be null");
        }
        listeners.addComparisonListener(l);
    }

    public void addMatchListener(ComparisonListener l) {
        if (l == null) {
            throw new IllegalArgumentException("listener must not be null");
        }
        listeners.addMatchListener(l);
    }

    public void addDifferenceListener(ComparisonListener l) {
        if (l == null) {
            throw new IllegalArgumentException("listener must not be null");
        }
        listeners.addDifferenceListener(l);
    }

    public void setNodeMatcher(NodeMatcher n) {
        if (n == null) {
            throw new IllegalArgumentException("node matcher must"
                                               + " not be null");
        }
        nodeMatcher = n;
    }

    public NodeMatcher getNodeMatcher() {
        return nodeMatcher;
    }

    public void setDifferenceEvaluator(DifferenceEvaluator e) {
        if (e == null) {
            throw new IllegalArgumentException("difference evaluator must"
                                               + " not be null");
        }
        diffEvaluator = e;
    }

    public DifferenceEvaluator getDifferenceEvaluator() {
        return diffEvaluator;
    }

    public void setNamespaceContext(Map<String, String> uri2Prefix) {
        this.uri2Prefix = Collections.unmodifiableMap(uri2Prefix);
    }

    /**
     * Compares the detail values for object equality, lets the
     * difference evaluator evaluate the result, notifies all
     * listeners and returns the outcome.
     */
    protected final ComparisonResult compare(Comparison comp) {
        ComparisonResult altered = compareDontFire(comp);
        listeners.fireComparisonPerformed(comp, altered);
        return altered;
    }

    /**
     * Compares the detail values for object equality, lets the
     * difference evaluator evaluate the result
     */
    protected final ComparisonResult compareDontFire(Comparison comp) {
        Object controlValue = comp.getControlDetails().getValue();
        Object testValue = comp.getTestDetails().getValue();
        boolean equal = controlValue == null
            ? testValue == null : controlValue.equals(testValue);
        ComparisonResult initial =
            equal ? ComparisonResult.EQUAL : ComparisonResult.DIFFERENT;
        return getDifferenceEvaluator().evaluate(comp, initial);
    }

    protected static String getXPath(XPathContext ctx) {
        return ctx == null ? null : ctx.getXPath();
    }
}
