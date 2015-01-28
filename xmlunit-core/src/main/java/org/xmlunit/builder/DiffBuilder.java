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

package org.xmlunit.builder;

import org.xmlunit.diff.Comparison;
import org.xmlunit.diff.ComparisonController;
import org.xmlunit.diff.ComparisonControllers;
import org.xmlunit.diff.ComparisonListener;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.DOMDifferenceEngine;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.Difference;
import org.xmlunit.diff.DifferenceEvaluator;
import org.xmlunit.diff.DifferenceEvaluators;
import org.xmlunit.diff.NodeMatcher;
import org.xmlunit.input.CommentLessSource;
import org.xmlunit.input.WhitespaceNormalizedSource;
import org.xmlunit.input.WhitespaceStrippedSource;

import javax.xml.transform.Source;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;


/**
 * DiffBuilder to create a {@link Diff} instance.
 * <p>
 * Valid inputs for control and test are all Objects supported by {@link Input#from(Object)}.
 * <p>
 * <b>Example Usage:</b>
 * 
 * <pre>
 * String controlXml = &quot;&lt;a&gt;&lt;b&gt;Test Value&lt;/b&gt;&lt;/a&gt;&quot;;
 * String testXml = &quot;&lt;a&gt;\n &lt;b&gt;\n  Test Value\n &lt;/b&gt;\n&lt;/a&gt;&quot;;
 * Diff myDiff = DiffBuilder.compare(Input.fromMemory(controlXml)).withTest(Input.fromMemory(testXml))
 *     .checkForSimilar()
 *     .ignoreWhitespace()
 *     .build();
 * assertFalse(&quot;XML similar &quot; + myDiff.toString(), myDiff.hasDifferences());
 * </pre>
 */
public class DiffBuilder {

    private static final ComparisonResult[] CHECK_FOR_SIMILAR = new ComparisonResult[] {
        ComparisonResult.DIFFERENT};

    private static final ComparisonResult[] CHECK_FOR_IDENTICAL = new ComparisonResult[] {
        ComparisonResult.SIMILAR, ComparisonResult.DIFFERENT};

    private final Source controlSource;

    private Source testSource;

    private NodeMatcher nodeMatcher;
    
    private ComparisonController comparisonController = ComparisonControllers.Default;

    private DifferenceEvaluator differenceEvaluator = DifferenceEvaluators.Default;

    private List<ComparisonListener> comparisonListeners = new ArrayList<ComparisonListener>();

    private ComparisonResult[] comparisonResultsToCheck = CHECK_FOR_IDENTICAL;

    private boolean ignoreWhitespace;

    private boolean normalizeWhitespace;

    private boolean ignoreComments;

    /**
     * Create a DiffBuilder instance.
     * 
     * @see DiffBuilder
     * @param controlSource the expected reference Result.
     * @param testSource the test result which must be compared with the control source.
     */
    private DiffBuilder(final Source controlSource) {
        super();
        this.controlSource = controlSource;
    }

    /**
     * Create a DiffBuilder from all kind of types supported by {@link Input#from(Object)}.
     * 
     * @see DiffBuilder
     * @param control the expected reference Result.
     */
    public static DiffBuilder compare(final Object control) {
        final Source controlSource = getSource(control);
        return new DiffBuilder(controlSource);
    }

    /**
     * Set the Test-Source from all kind of types supported by {@link Input#from(Object)}.
     * 
     * @param test the test result which must be compared with the control source.
     */
    public DiffBuilder withTest(Object test) {
        testSource = getSource(test);
        return this;
    }

    private static Source getSource(Object object) {
        return Input.from(object).build();
    }

    /**
     * Ignore whitespace by removing all empty text nodes and trimming the non-empty ones.
     */
    public DiffBuilder ignoreWhitespace() {
        ignoreWhitespace = true;
        return this;
    }

    /**
     * Normalize Text-Elements by removing all empty text nodes and normalizing the non-empty ones.
     * <p>
     * "normalized" in this context means all whitespace characters are replaced by space characters and consecutive
     * whitespace characters are collapsed.
     * </p>
     * <p>
     * This flag has no effect if {@link #ignoreWhitespace()} is already activated.
     * </p>
     */
    public DiffBuilder normalizeWhitespace() {
        normalizeWhitespace = true;
        return this;
    }

    public DiffBuilder ignoreComments() {
        ignoreComments = true;
        return this;
    }
    
    public DiffBuilder withNodeMatcher(final NodeMatcher nodeMatcher) {
        this.nodeMatcher = nodeMatcher;
        return this;
    }

    /**
     * Provide your own custom {@link DifferenceEvaluator} implementation.
     * This overwrites the Default DifferenceEvaluator.
     * If you want use your custom DifferenceEvaluator in combination with the default or another DifferenceEvaluator
     * you must use {@link DifferenceEvaluators#sequence(DifferenceEvaluator...)}
     * or {@link DifferenceEvaluators#first(DifferenceEvaluator...)} to combine them:
     * <pre>
     * Diff myDiff = DiffBuilder.compare(control).withTest(test)
     *         .withDifferenceEvaluator(
     *             DifferenceEvaluators.sequence(
     *                 DifferenceEvaluators.Default,
     *                 new MyCustomDifferenceEvaluator()))
     *         ....
     *         .build();
     * </pre>
     */
    public DiffBuilder withDifferenceEvaluator(final DifferenceEvaluator differenceEvaluator) {
        this.differenceEvaluator = differenceEvaluator;
        return this;
    }

    /**
     * Replace the {@link ComparisonControllers#Default} with your own ComparisonController.
     * <p>
     * Example use:
     * <pre>
     * Diff myDiff = DiffBuilder.compare(control).withTest(test)
     *      .withComparisonController(ComparisonControllers.StopWhenDifferent)
     *      .build();
     * </pre>
     */
    public DiffBuilder withComparisonController(final ComparisonController comparisonController) {
        this.comparisonController = comparisonController;
        return this;
    }
    
    public DiffBuilder withComparisonListeners(final ComparisonListener... comparisonListeners) {
        this.comparisonListeners.addAll(Arrays.asList(comparisonListeners));
        return this;
    }

    /**
     * check test source with the control source for similarity.
     * <p>
     * Default is {@link #checkForIdentical()}.
     */
    public DiffBuilder checkForSimilar() {
        comparisonResultsToCheck = CHECK_FOR_SIMILAR;
        return this;
    }

    /**
     * check test source with the control source for identically.
     * <p>
     * This is the Default.
     */
    public DiffBuilder checkForIdentical() {
        comparisonResultsToCheck = CHECK_FOR_IDENTICAL;
        return this;
    }

    public Diff build() {

        final DOMDifferenceEngine d = new DOMDifferenceEngine();
        final CollectResultsListener collectResultsListener = new CollectResultsListener(comparisonResultsToCheck);
        d.addDifferenceListener(collectResultsListener);
        if (nodeMatcher != null) {
            d.setNodeMatcher(nodeMatcher);
        }
        d.setDifferenceEvaluator(differenceEvaluator);
        d.setComparisonController(comparisonController);
        for (ComparisonListener comparisonListener : comparisonListeners) {
            d.addDifferenceListener(comparisonListener);
        }
        d.compare(wrap(controlSource), wrap(testSource));

        return new Diff(controlSource, testSource, collectResultsListener.getDifferences());
    }

    private Source wrap(final Source source) {
        Source newSource = source;
        if (ignoreWhitespace) {
            newSource = new WhitespaceStrippedSource(newSource);
        } else if (normalizeWhitespace) {
            newSource = new WhitespaceNormalizedSource(newSource);
        }
        if (ignoreComments) {
            newSource = new CommentLessSource(newSource);
        }
        return newSource;
    }

    private static final class CollectResultsListener implements ComparisonListener {

        private final List<Difference> results;
        private final EnumSet<ComparisonResult> comparisonResultsToCheck;

        public CollectResultsListener(final ComparisonResult... comparisonResultsToCheck) {
            super();
            results = new ArrayList<Difference>();
            this.comparisonResultsToCheck = EnumSet.copyOf(Arrays.asList(comparisonResultsToCheck));
        }

        @Override
        public void comparisonPerformed(final Comparison comparison, final ComparisonResult outcome) {
            if (comparisonResultsToCheck.contains(outcome)) {
                results.add(new Difference(comparison, outcome));
            }
        }

        public List<Difference> getDifferences() {
            return Collections.unmodifiableList(results);
        }
    }
}
