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
import org.xmlunit.diff.ComparisonType;
import org.xmlunit.diff.DefaultComparisonFormatter;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.DifferenceEvaluator;
import org.xmlunit.diff.DifferenceEvaluators;
import org.xmlunit.diff.ElementSelectors;
import org.xmlunit.diff.NodeMatcher;
import org.xmlunit.util.Predicate;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Map;

import static org.xmlunit.assertj.error.ShouldBeNotSimilar.shouldBeNotIdentical;
import static org.xmlunit.assertj.error.ShouldBeNotSimilar.shouldBeNotSimilar;
import static org.xmlunit.assertj.error.ShouldBeSimilar.shouldBeIdentical;
import static org.xmlunit.assertj.error.ShouldBeSimilar.shouldBeSimilar;
import static org.xmlunit.assertj.error.ShouldNotHaveThrown.shouldNotHaveThrown;
import static org.xmlunit.diff.DifferenceEvaluators.Default;
import static org.xmlunit.diff.DifferenceEvaluators.chain;

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
 * assertThat(test).and(control).areNotSimilar();
 * </pre>
 *
 * @since XMLUnit 2.6.1
 */
public class CompareAssert extends CustomAbstractAssert<CompareAssert, Object> implements DifferenceEngineConfigurer<CompareAssert> {

    private enum ComparisonContext {
        IDENTICAL, NOT_IDENTICAL, SIMILAR, NOT_SIMILAR
    }

    private static final String EXPECTING_NOT_NULL = "Expecting control not to be null";
    private static DifferenceEvaluator IgnoreNodeListSequence =
            DifferenceEvaluators.downgradeDifferencesToEqual(ComparisonType.CHILD_NODELIST_SEQUENCE);

    private final DiffBuilder diffBuilder;
    private ComparisonController customComparisonController;
    private boolean formatXml;
    private ComparisonFormatter formatter = new DefaultComparisonFormatter();

    private CompareAssert(Object actual, DiffBuilder diffBuilder) {
        super(actual, CompareAssert.class);
        this.diffBuilder = diffBuilder;
    }

    static CompareAssert create(Object actual, Object control, Map<String, String> prefix2Uri, DocumentBuilderFactory dbf) {

        Assertions.assertThat(control)
                .as(EXPECTING_NOT_NULL)
                .isNotNull();

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
     * @see DiffBuilder#withNamespaceContext(Map)
     */
    @Override
    public CompareAssert withNamespaceContext(Map<String, String> prefix2Uri) {
        diffBuilder.withNamespaceContext(prefix2Uri);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see DiffBuilder#withAttributeFilter(Predicate)
     */
    @Override
    public CompareAssert withAttributeFilter(Predicate<Attr> attributeFilter) {
        diffBuilder.withAttributeFilter(attributeFilter);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see DiffBuilder#withNodeFilter(Predicate)
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
        this.formatter = formatter;
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
     * Equivalent for
     * <pre>
     *     .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText))
     *     .withDifferenceEvaluator(
     *          chain(
     *              Default,
     *              DifferenceEvaluators.downgradeDifferencesToEqual(ComparisonType.CHILD_NODELIST_SEQUENCE)));
     * </pre>
     *
     * @see DiffBuilder#withNodeMatcher(NodeMatcher)
     */
    public CompareAssert ignoreChildNodesOrder() {
        diffBuilder.withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText));
        diffBuilder.withDifferenceEvaluator(chain(Default, IgnoreNodeListSequence));
        return this;
    }

    /**
     * Check if actual and control XMLs are identical.
     * If custom comparison controller wasn't defined then {@link ComparisonControllers#StopWhenSimilar} is used.
     *
     * @throws AssertionError if the test value is invalid
     * @throws AssertionError if the control value is invalid
     * @see DiffBuilder#checkForIdentical()
     */
    public CompareAssert areIdentical() {
        diffBuilder.checkForIdentical();
        compare(ComparisonContext.IDENTICAL);
        return this;
    }

    /**
     * Check if actual and control XMLs are not identical.
     * If custom comparison controller wasn't defined then {@link ComparisonControllers#StopWhenSimilar} is used.
     *
     * @throws AssertionError if the test value is invalid
     * @throws AssertionError if the control value is invalid
     * @see DiffBuilder#checkForSimilar()
     */
    public CompareAssert areNotIdentical() {
        diffBuilder.checkForIdentical();
        compare(ComparisonContext.NOT_IDENTICAL);
        return this;
    }

    /**
     * Check if actual and control XMLs are similar.
     * If custom comparison controller wasn't defined then {@link ComparisonControllers#StopWhenDifferent} is used.
     *
     * @throws AssertionError if the test value is invalid
     * @throws AssertionError if the control value is invalid
     * @see DiffBuilder#checkForSimilar()
     */
    public CompareAssert areSimilar() {
        diffBuilder.checkForSimilar();
        compare(ComparisonContext.SIMILAR);
        return this;
    }

    /**
     * Check if actual and control XMLs are not similar.
     * If custom comparison controller wasn't defined then {@link ComparisonControllers#StopWhenDifferent} is used.
     *
     * @throws AssertionError if the test value is invalid
     * @throws AssertionError if the control value is invalid
     * @see DiffBuilder#checkForSimilar()
     */
    public CompareAssert areNotSimilar() {
        diffBuilder.checkForSimilar();
        compare(ComparisonContext.NOT_SIMILAR);
        return this;
    }

    private void compare(ComparisonContext context) {

        if (customComparisonController != null) {
            diffBuilder.withComparisonController(customComparisonController);
        } else if (ComparisonContext.IDENTICAL == context
                || ComparisonContext.NOT_IDENTICAL == context) {
            diffBuilder.withComparisonController(ComparisonControllers.StopWhenSimilar);
        } else if (ComparisonContext.SIMILAR == context
                || ComparisonContext.NOT_SIMILAR == context) {
            diffBuilder.withComparisonController(ComparisonControllers.StopWhenDifferent);
        }

        Diff diff;

        try {

            diff = diffBuilder.build();
        } catch (Exception e) {
            throwAssertionError(shouldNotHaveThrown(e));
            return; //fix compile issue
        }

        String controlSystemId = diff.getControlSource().getSystemId();
        String testSystemId = diff.getTestSource().getSystemId();

        if (diff.hasDifferences()) {
            Comparison firstDifferenceComparison = diff.getDifferences().iterator().next().getComparison();
            if (ComparisonContext.IDENTICAL == context) {
                throwAssertionError(shouldBeIdentical(controlSystemId, testSystemId, firstDifferenceComparison, formatter, formatXml));
            } else if (ComparisonContext.SIMILAR == context) {
                throwAssertionError(shouldBeSimilar(controlSystemId, testSystemId, firstDifferenceComparison, formatter, formatXml));
            }
        } else {

            if (ComparisonContext.NOT_IDENTICAL == context) {
                throwAssertionError(shouldBeNotIdentical(controlSystemId, testSystemId));
            } else if (ComparisonContext.NOT_SIMILAR == context) {
                throwAssertionError(shouldBeNotSimilar(controlSystemId, testSystemId));
            }
        }
    }
}
