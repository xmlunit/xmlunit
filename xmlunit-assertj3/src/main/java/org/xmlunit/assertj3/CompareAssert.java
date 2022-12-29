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
package org.xmlunit.assertj3;

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

import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;

import static org.xmlunit.assertj3.AssertionsAdapter.withAssertInfo;
import static org.xmlunit.assertj3.error.ShouldBeNotSimilar.shouldBeNotIdentical;
import static org.xmlunit.assertj3.error.ShouldBeNotSimilar.shouldBeNotSimilar;
import static org.xmlunit.assertj3.error.ShouldNotHaveThrown.shouldNotHaveThrown;
import static org.xmlunit.diff.DifferenceEvaluators.Default;
import static org.xmlunit.diff.DifferenceEvaluators.chain;

/**
 * Assertion methods for XMLs comparison.
 *
 * <p><b>Simple Example</b></p>
 *
 * <pre>
 * import static org.xmlunit.assertj.XmlAssert.assertThat;
 *
 * final String control = &quot;&lt;a&gt;&lt;b attr=\&quot;abc\&quot;&gt;&lt;/b&gt;&lt;/a&gt;&quot;;
 * final String test = &quot;&lt;a&gt;&lt;b attr=\&quot;xyz\&quot;NodeAssertFactory&gt;&lt;/b&gt;&lt;/a&gt;&quot;;
 *
 * assertThat(test).and(control).areIdentical();
 * assertThat(test).and(control).areNotSimilar();
 * </pre>
 *
 * @since XMLUnit 2.8.1
 */
public class CompareAssert extends CustomAbstractAssert<CompareAssert, Object> implements DifferenceEngineConfigurer<CompareAssert> {

    private enum ComparisonContext {
        IDENTICAL, NOT_IDENTICAL, SIMILAR, NOT_SIMILAR
    }

    private static final String EXPECTING_NOT_NULL = "Expecting control not to be null";
    private static final DifferenceEvaluator IgnoreNodeListSequence =
            DifferenceEvaluators.downgradeDifferencesToEqual(ComparisonType.CHILD_NODELIST_SEQUENCE);

    private final DiffBuilder diffBuilder;
    private ComparisonController customComparisonController;
    private boolean formatXml;
    private ComparisonFormatter formatter = new DefaultComparisonFormatter();

    private CompareAssert(Object actual, DiffBuilder diffBuilder) {
        super(actual, CompareAssert.class);
        this.diffBuilder = diffBuilder;
    }

    static CompareAssert create(Object actual, Object control, XmlAssertConfig config) {

        AssertionsAdapter.assertThat(control, config.info)
                .as(EXPECTING_NOT_NULL)
                .isNotNull();

        DiffBuilder diffBuilder = DiffBuilder.compare(control)
                .withTest(actual)
                .withNamespaceContext(config.prefix2Uri)
                .withDocumentBuilderFactory(config.dbf);

        return withAssertInfo(new CompareAssert(actual, diffBuilder), config.info);
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
     * Sets the {@link DocumentBuilderFactory} to use when creating a {@link org.w3c.dom.Document} from the {@link
     * Source}s to compare.
     *
     * @see DiffBuilder#withDocumentBuilderFactory(DocumentBuilderFactory)
     * @param f the DocumentBuilderFactory to use
     * @return this
     */
    public CompareAssert withDocumentBuilderFactory(DocumentBuilderFactory f) {
        diffBuilder.withDocumentBuilderFactory(f);
        return this;
    }

    /**
     * Ignore whitespace by removing all empty text nodes and trimming the non-empty ones.
     *
     * @see DiffBuilder#ignoreWhitespace()
     * @return this
     */
    public CompareAssert ignoreWhitespace() {
        formatXml = true;
        diffBuilder.ignoreWhitespace();
        return this;
    }

    /**
     * Normalize Text-Elements by removing all empty text nodes and normalizing the non-empty ones.
     *
     * @see DiffBuilder#normalizeWhitespace()
     * @return this
     */
    public CompareAssert normalizeWhitespace() {
        formatXml = true;
        diffBuilder.normalizeWhitespace();
        return this;
    }

    /**
     * Ignore element content whitespace by removing all text nodes solely consisting of whitespace.
     *
     * @see DiffBuilder#ignoreElementContentWhitespace()
     * @return this
     */
    public CompareAssert ignoreElementContentWhitespace() {
        diffBuilder.ignoreElementContentWhitespace();
        return this;
    }

    /**
     * Will remove all comment-Tags "&lt;!-- Comment --&gt;" from test- and control-XML before comparing.
     *
     * @see DiffBuilder#ignoreComments()
     * @return this
     */
    public CompareAssert ignoreComments() {
        diffBuilder.ignoreComments();
        return this;
    }

    /**
     * Will remove all comment-Tags "&lt;!-- Comment --&gt;" from test- and control-XML before comparing.
     *
     * @see DiffBuilder#ignoreCommentsUsingXSLTVersion(String)
     * @param xsltVersion use this version for the stylesheet
     * @return this
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
     * @return this
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
     * @return this
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
     * @return this
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
     * @return this
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
     * @return this
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
                failComparison("identical", controlSystemId, testSystemId, firstDifferenceComparison, formatter, formatXml);
            } else if (ComparisonContext.SIMILAR == context) {
                failComparison("similar", controlSystemId, testSystemId, firstDifferenceComparison, formatter, formatXml);
            }
        } else {
            if (ComparisonContext.NOT_IDENTICAL == context) {
                throwAssertionError(shouldBeNotIdentical(controlSystemId, testSystemId));
            } else if (ComparisonContext.NOT_SIMILAR == context) {
                throwAssertionError(shouldBeNotSimilar(controlSystemId, testSystemId));
            }
        }
    }


    private static final String COMPARISON_FAILURE_PATTERN = "%nExpecting:%n <%s> and <%s> to be %s%n%s%nexpected:<%s> but was:<%s>>";

    private void failComparison(final String type, final String controlSystemId,
                                final String testSystemId, final Comparison difference,
                                final ComparisonFormatter formatter, final boolean formatXml) {
        final String controlId = controlSystemId != null ? controlSystemId : "control instance";
        final String testId = testSystemId != null ? testSystemId : "test instance";
        final String description = formatter.getDescription(difference);

        final String expected = formatter.getDetails(difference.getControlDetails(),
            difference.getType(), formatXml);
        final String actual = formatter.getDetails(difference.getTestDetails(),
            difference.getType(), formatXml);

        final String msg = String.format(COMPARISON_FAILURE_PATTERN, controlId, testId, type,
            description, expected, actual)
                .replace("%", "%%"); // any remaining '%' signs should be escaped because assertj tries to format this as well.

        failWithActualExpectedAndMessage(actual, expected, msg);
    }

}
