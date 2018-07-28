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
package org.xmlunit.assertj;

import org.assertj.core.api.Assertions;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.DifferenceEngineConfigurer;
import org.xmlunit.diff.Comparison;
import org.xmlunit.diff.ComparisonController;
import org.xmlunit.diff.ComparisonControllers;
import org.xmlunit.diff.ComparisonFormatter;
import org.xmlunit.diff.ComparisonListener;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.DifferenceEvaluator;
import org.xmlunit.diff.ElementSelectors;
import org.xmlunit.diff.NodeMatcher;
import org.xmlunit.util.Predicate;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Map;

import static org.xmlunit.assertj.error.ShouldBeDifferent.shouldBeDifferent;
import static org.xmlunit.assertj.error.ShouldNotBeDifferent.shouldNotBeDifferent;

/**
 * Assertion methods for XMLs comparision.
 *
 * <p><b>Simple Example</b></p>
 *
 * <pre>
 * import static org.xmlunit.assertj.XmlAssert.assertThat;
 *
 * final String control = &quot;&lt;a&gt;&lt;b attr=\&quot;abc\&quot;&gt;&lt;/b&gt;&lt;/a&gt;&quot;;
 * final String test = &quot;&lt;a&gt;&lt;b attr=\&quot;xyz\&quot;&gt;&lt;/b&gt;&lt;/a&gt;&quot;;
 *
 * assertThat(test).and(control).areIdentical();
 * </pre>
 *
 * @since XMLUnit 2.6.1
 */
public class CompareAssert extends CustomAbstractAssert<CompareAssert, Object> implements DifferenceEngineConfigurer<CompareAssert> {

    private final DiffBuilder diffBuilder;
    private ComparisonController customComparisonController;
    private boolean formatXml;

    private CompareAssert(Object actual, DiffBuilder diffBuilder) {
        super(actual, CompareAssert.class);
        this.diffBuilder = diffBuilder;
    }

    static CompareAssert create(Object actual, Object control, Map<String, String> prefix2Uri, DocumentBuilderFactory dbf) {

        Assertions.assertThat(control).isNotNull();

        DiffBuilder diffBuilder = DiffBuilder.compare(control)
                .withTest(actual)
                .withNamespaceContext(prefix2Uri)
                .withDocumentBuilderFactory(dbf);

        return new CompareAssert(actual, diffBuilder);
    }

    /**
     * {@inheritDoc}
     *
     * @see DiffBuilder#withNodeMatcher(NodeMatcher)
     */
    @Override
    public CompareAssert withNodeMatcher(NodeMatcher nodeMatcher) {
        diffBuilder.withNodeMatcher(nodeMatcher);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see DiffBuilder#withDifferenceEvaluator(DifferenceEvaluator)
     */
    @Override
    public CompareAssert withDifferenceEvaluator(DifferenceEvaluator differenceEvaluator) {
        diffBuilder.withDifferenceEvaluator(differenceEvaluator);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see DiffBuilder#withComparisonController(ComparisonController)
     */
    @Override
    public CompareAssert withComparisonController(ComparisonController comparisonController) {
        customComparisonController = comparisonController;
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see DiffBuilder#withComparisonListeners(ComparisonListener...)
     */
    @Override
    public CompareAssert withComparisonListeners(ComparisonListener... comparisonListeners) {
        diffBuilder.withComparisonListeners(comparisonListeners);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see DiffBuilder#withDifferenceListeners(ComparisonListener...)
     */
    @Override
    public CompareAssert withDifferenceListeners(ComparisonListener... comparisonListeners) {
        diffBuilder.withDifferenceListeners(comparisonListeners);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see DiffBuilder#withNamespaceContext(Map<String, String>)
     */
    @Override
    public CompareAssert withNamespaceContext(Map<String, String> prefix2Uri) {
        diffBuilder.withNamespaceContext(prefix2Uri);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see DiffBuilder#withAttributeFilter(Predicate<Attr>)
     */
    @Override
    public CompareAssert withAttributeFilter(Predicate<Attr> attributeFilter) {
        diffBuilder.withAttributeFilter(attributeFilter);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see DiffBuilder#withNodeFilter(Predicate<Node>)
     */
    @Override
    public CompareAssert withNodeFilter(Predicate<Node> nodeFilter) {
        diffBuilder.withNodeFilter(nodeFilter);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see DiffBuilder#withComparisonFormatter(ComparisonFormatter)
     */
    @Override
    public CompareAssert withComparisonFormatter(ComparisonFormatter formatter) {
        diffBuilder.withComparisonFormatter(formatter);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see DiffBuilder#withDocumentBuilderFactory(DocumentBuilderFactory)
     */
    public CompareAssert withDocumentBuilderFactory(DocumentBuilderFactory f) {
        diffBuilder.withDocumentBuilderFactory(f);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see DiffBuilder#ignoreWhitespace()
     */
    public CompareAssert ignoreWhitespace() {
        formatXml = true;
        diffBuilder.ignoreWhitespace();
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see DiffBuilder#normalizeWhitespace()
     */
    public CompareAssert normalizeWhitespace() {
        formatXml = true;
        diffBuilder.normalizeWhitespace();
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see DiffBuilder#ignoreElementContentWhitespace()
     */
    public CompareAssert ignoreElementContentWhitespace() {
        diffBuilder.ignoreElementContentWhitespace();
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see DiffBuilder#ignoreComments()
     */
    public CompareAssert ignoreComments() {
        diffBuilder.ignoreComments();
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see DiffBuilder#ignoreCommentsUsingXSLTVersion(String)
     */
    public CompareAssert ignoreCommentsUsingXSLTVersion(String xsltVersion) {
        diffBuilder.ignoreCommentsUsingXSLTVersion(xsltVersion);
        return this;
    }

    /**
     * Equivalent for <pre>withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText))</pre>
     *
     * @see DiffBuilder#withNodeMatcher(NodeMatcher)
     */
    public CompareAssert ignoreChildNodesOrder() {
        diffBuilder.withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText));
        return this;
    }

    /**
     * Check if actual and control XMLs are identical.
     * If custom comparison controller wasn't defined then {@link ComparisonControllers#StopWhenSimilar} is used.
     *
     * @see DiffBuilder#checkForIdentical()
     */
    public CompareAssert areIdentical() {
        diffBuilder.checkForIdentical();
        compare(ComparisonResult.EQUAL);
        return this;
    }

    /**
     * Check if actual and control XMLs are similar.
     * If custom comparison controller wasn't defined then {@link ComparisonControllers#StopWhenDifferent} is used.
     *
     * @see DiffBuilder#checkForSimilar()
     */
    public CompareAssert areSimilar() {
        diffBuilder.checkForSimilar();
        compare(ComparisonResult.SIMILAR);
        return this;
    }

    /**
     * Check if actual and control XMLs are different.
     * Similar XMLs aren't different. It means that if <pre>areSimilar</pre> pass then <pre>areDifferent</pre> failed.
     *
     * If custom comparison controller wasn't defined then {@link ComparisonControllers#StopWhenSimilar} is used.
     *
     * @see DiffBuilder#checkForSimilar()
     */
    public CompareAssert areDifferent() {
        diffBuilder.checkForSimilar();
        compare(ComparisonResult.DIFFERENT);
        return this;
    }

    private void compare(ComparisonResult compareFor) {

        if (customComparisonController != null) {
            diffBuilder.withComparisonController(customComparisonController);
        } else if (ComparisonResult.EQUAL == compareFor) {
            diffBuilder.withComparisonController(ComparisonControllers.StopWhenSimilar);
        } else if (ComparisonResult.SIMILAR == compareFor) {
            diffBuilder.withComparisonController(ComparisonControllers.StopWhenDifferent);
        } else {
            diffBuilder.withComparisonController(ComparisonControllers.StopWhenSimilar);
        }

        Diff diff = diffBuilder.build();

        if (!diff.hasDifferences() && ComparisonResult.DIFFERENT == compareFor) {

            String controlSystemId = diff.getControlSource().getSystemId();
            String testSystemId = diff.getTestSource().getSystemId();
            throwAssertionError(shouldBeDifferent(controlSystemId, testSystemId));

        } else if (diff.hasDifferences() && ComparisonResult.DIFFERENT != compareFor) {

            String systemId = diff.getControlSource().getSystemId();
            Comparison firstDifferenceComparison = diff.getDifferences().iterator().next().getComparison();
            throwAssertionError(shouldNotBeDifferent(systemId, firstDifferenceComparison, formatXml));
        }
    }
}
