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

import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.xmlunit.diff.ComparisonController;
import org.xmlunit.diff.ComparisonFormatter;
import org.xmlunit.diff.ComparisonListener;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.DifferenceEvaluator;
import org.xmlunit.diff.DifferenceEvaluators;
import org.xmlunit.diff.NodeMatcher;
import org.xmlunit.util.Predicate;

/**
 * Subset of the configuration options available for a {@link DifferenceEngine}.
 * @since 2.6.0
 */
public interface DifferenceEngineConfigurer<D extends DifferenceEngineConfigurer<D>> {

    /**
     * Sets the strategy for selecting nodes to compare.
     * <p>
     * Example with {@link org.xmlunit.diff.DefaultNodeMatcher}:
     * <pre>
     * .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText))
     * </pre>
     *
     * @see org.xmlunit.diff.DifferenceEngine#setNodeMatcher(NodeMatcher)
     */
    D withNodeMatcher(NodeMatcher nodeMatcher);

    /**
     * Provide your own custom {@link DifferenceEvaluator} implementation.
     * <p>This overwrites the Default DifferenceEvaluator.</p>
     *
     * <p>If you want use your custom DifferenceEvaluator in
     * combination with the default or another DifferenceEvaluator you
     * should use {@link
     * DifferenceEvaluators#chain(DifferenceEvaluator...)}  or {@link
     * DifferenceEvaluators#first(DifferenceEvaluator...)} to combine
     * them:</p>
     *
     * <pre>
     *         .withDifferenceEvaluator(
     *             DifferenceEvaluators.chain(
     *                 DifferenceEvaluators.Default,
     *                 new MyCustomDifferenceEvaluator()))
     *         ....
     * </pre>
     */
    D withDifferenceEvaluator(DifferenceEvaluator differenceEvaluator);

    /**
     * Replace the {@link ComparisonControllers#Default} with your own ComparisonController.
     * <p>
     * Example use:
     * <pre>
     *      .withComparisonController(ComparisonControllers.StopWhenDifferent)
     * </pre>
     */
    D withComparisonController(ComparisonController comparisonController);

    /**
     * Registers listeners that are notified of each comparison.
     *
     * @see org.xmlunit.diff.DifferenceEngine#addComparisonListener(ComparisonListener)
     */
    D withComparisonListeners(ComparisonListener... comparisonListeners);

    /**
     * Registers listeners that are notified of each comparison with
     * outcome other than {@link ComparisonResult#EQUAL}.
     *
     * @see org.xmlunit.diff.DifferenceEngine#addDifferenceListener(ComparisonListener)
     */
    D withDifferenceListeners(ComparisonListener... comparisonListeners);

    /**
     * Establish a namespace context that will be used in {@link
     * org.xmlunit.diff.Comparison.Detail#getXPath Comparison.Detail#getXPath}.
     *
     * <p>Without a namespace context (or with an empty context) the
     * XPath expressions will only use local names for elements and
     * attributes.</p>
     *
     * @param prefix2Uri mapping between prefix and namespace URI
     */
    D withNamespaceContext(Map<String, String> prefix2Uri);

    /**
     * Registers a filter for attributes.
     *
     * <p>Only attributes for which the predicate returns true are
     * part of the comparison.  By default all attributes are
     * considered.</p>
     *
     * <p>The "special" namespace, namespace-location and
     * schema-instance-type attributes can not be ignored this way.
     * If you want to suppress comparison of them you'll need to
     * implement {@link DifferenceEvaluator}.</p>
     */
    D withAttributeFilter(Predicate<Attr> attributeFilter);

    /**
     * Registers a filter for nodes.
     *
     * <p>Only nodes for which the predicate returns true are part of
     * the comparison.  By default nodes that are not document types
     * are considered.</p>
     */
    D withNodeFilter(Predicate<Node> nodeFilter);

    /**
     * Sets a non-default formatter for the differences found.
     */
    D withComparisonFormatter(ComparisonFormatter formatter);
}
