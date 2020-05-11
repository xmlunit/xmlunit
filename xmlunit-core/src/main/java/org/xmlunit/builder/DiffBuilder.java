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

import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.xmlunit.diff.Comparison;
import org.xmlunit.diff.ComparisonController;
import org.xmlunit.diff.ComparisonControllers;
import org.xmlunit.diff.ComparisonFormatter;
import org.xmlunit.diff.ComparisonListener;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.DOMDifferenceEngine;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.Difference;
import org.xmlunit.diff.DifferenceEvaluator;
import org.xmlunit.diff.DifferenceEvaluators;
import org.xmlunit.diff.NodeMatcher;
import org.xmlunit.input.CommentLessSource;
import org.xmlunit.input.ElementContentWhitespaceStrippedSource;
import org.xmlunit.input.WhitespaceNormalizedSource;
import org.xmlunit.input.WhitespaceStrippedSource;
import org.xmlunit.util.Predicate;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

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
 * Diff myDiff = DiffBuilder.compare(Input.fromString(controlXml)).withTest(Input.fromString(testXml))
 *     .checkForSimilar()
 *     .ignoreWhitespace()
 *     .build();
 * assertFalse(&quot;XML similar &quot; + myDiff.toString(), myDiff.hasDifferences());
 * </pre>
 */
public class DiffBuilder implements DifferenceEngineConfigurer<DiffBuilder> {

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
    private List<ComparisonListener> differenceListeners = new ArrayList<ComparisonListener>();

    private ComparisonResult[] comparisonResultsToCheck = CHECK_FOR_IDENTICAL;

    private Map<String, String> namespaceContext;

    private Predicate<Attr> attributeFilter;

    private Predicate<Node> nodeFilter;

    private ComparisonFormatter formatter;

    private boolean ignoreWhitespace;

    private boolean normalizeWhitespace;

    private boolean ignoreECW;

    private boolean ignoreComments;

    private String ignoreCommentVersion = null;

    private DocumentBuilderFactory documentBuilderFactory;

    /**
     * Create a DiffBuilder instance.
     * 
     * @see DiffBuilder
     * @param controlSource the expected reference Result.
     */
    private DiffBuilder(final Source controlSource) {
        this.controlSource = controlSource;
    }

    /**
     * Create a DiffBuilder from all kind of types supported by {@link Input#from(Object)}.
     * 
     * @see DiffBuilder
     * @param control the expected reference document.
     */
    public static DiffBuilder compare(final Object control) {
        final Source controlSource = getSource(control);
        return new DiffBuilder(controlSource);
    }

    /**
     * Set the Test-Source from all kind of types supported by {@link Input#from(Object)}.
     * 
     * @param test the test document which must be compared with the control document.
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
     *
     * <p>If you only want to remove text nodes consisting solely of
     * whitespace (AKA element content whitespace) but leave all other
     * text nodes alone you should use {@link
     * #ignoreElementContentWhitespace} instead.</p>
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
     */
    public DiffBuilder normalizeWhitespace() {
        normalizeWhitespace = true;
        return this;
    }

    /**
     * Ignore element content whitespace by removing all text nodes solely consisting of whitespace.
     * @since XMLUnit 2.6.0
     */
    public DiffBuilder ignoreElementContentWhitespace() {
        ignoreECW = true;
        return this;
    }

    /**
     * Will remove all comment-Tags "&lt;!-- Comment --&gt;" from test- and control-XML before comparing.
     *
     * <p>Comments are ignored by applying an XSLT transformation on
     * the source which may reduce the effect of {@link
     * #withDocumentBuilderFactory}. If you need more control over the
     * transformation build the {@code Source} using a transformation
     * yourself, using {@link CommentLessSource#STYLE}.</p>
     */
    public DiffBuilder ignoreComments() {
        return ignoreCommentsUsingXSLTVersion(null);
    }

    /**
     * Will remove all comment-Tags "&lt;!-- Comment --&gt;" from test- and control-XML before comparing.
     *
     * <p>Comments are ignored by applying an XSLT transformation on
     * the source which may reduce the effect of {@link
     * #withDocumentBuilderFactory}. This uses the {@link
     * CommentLessSource} constructor with two arguments using {@code
     * xsltVersion} as second argument.</p>
     * @param xsltVersion use this version for the stylesheet
     * @since XMLUnit 2.5.0
     */
    public DiffBuilder ignoreCommentsUsingXSLTVersion(String xsltVersion) {
        ignoreComments = true;
        ignoreCommentVersion = xsltVersion;
        return this;
    }

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
    @Override
    public DiffBuilder withNodeMatcher(final NodeMatcher nodeMatcher) {
        this.nodeMatcher = nodeMatcher;
        return this;
    }

    /**
     * Provide your own custom {@link DifferenceEvaluator} implementation.
     * This overwrites the Default DifferenceEvaluator.
     * If you want use your custom DifferenceEvaluator in combination with the default or another DifferenceEvaluator
     * you should use {@link DifferenceEvaluators#chain(DifferenceEvaluator...)}
     * or {@link DifferenceEvaluators#first(DifferenceEvaluator...)} to combine them:
     * <pre>
     * Diff myDiff = DiffBuilder.compare(control).withTest(test)
     *         .withDifferenceEvaluator(
     *             DifferenceEvaluators.chain(
     *                 DifferenceEvaluators.Default,
     *                 new MyCustomDifferenceEvaluator()))
     *         ....
     *         .build();
     * </pre>
     */
    @Override
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
    @Override
    public DiffBuilder withComparisonController(final ComparisonController comparisonController) {
        this.comparisonController = comparisonController;
        return this;
    }

    /**
     * Registers listeners that are notified of each comparison.
     * 
     * @see org.xmlunit.diff.DifferenceEngine#addComparisonListener(ComparisonListener)
     */
    @Override
    public DiffBuilder withComparisonListeners(final ComparisonListener... comparisonListeners) {
        this.comparisonListeners.addAll(Arrays.asList(comparisonListeners));
        return this;
    }

    /**
     * Registers listeners that are notified of each comparison with
     * outcome other than {@link ComparisonResult#EQUAL}.
     * 
     * @see org.xmlunit.diff.DifferenceEngine#addDifferenceListener(ComparisonListener)
     */
    @Override
    public DiffBuilder withDifferenceListeners(final ComparisonListener... comparisonListeners) {
        this.differenceListeners.addAll(Arrays.asList(comparisonListeners));
        return this;
    }

    /**
     * check test source with the control source for similarity.
     * <p>
     * Example for Similar: The XML node "&lt;a&gt;Text&lt;/a&gt;" and "&lt;a&gt;&lt;![CDATA[Text]]&gt;&lt;/a&gt;" are
     * similar and the Test will not fail.
     * <p>
     * The rating, if a node is similar, will be done by the {@link DifferenceEvaluators#Default}.
     * See {@link #withDifferenceEvaluator(DifferenceEvaluator)}
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
    @Override
    public DiffBuilder withNamespaceContext(Map<String, String> prefix2Uri) {
        namespaceContext = prefix2Uri;
        return this;
    }

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
    @Override
    public DiffBuilder withAttributeFilter(Predicate<Attr> attributeFilter) {
        this.attributeFilter = attributeFilter;
        return this;
    }

    /**
     * Registers a filter for nodes.
     *
     * <p>Only nodes for which the predicate returns true are part of
     * the comparison.  By default nodes that are not document types
     * are considered.</p>
     */
    @Override
    public DiffBuilder withNodeFilter(Predicate<Node> nodeFilter) {
        this.nodeFilter = nodeFilter;
        return this;
    }

    /**
     * Sets a non-default formatter for the differences found.
     */
    @Override
    public DiffBuilder withComparisonFormatter(ComparisonFormatter formatter) {
        this.formatter = formatter;
        return this;
    }

    /**
     * Sets the {@link DocumentBuilderFactory} to use when creating a
     * {@link org.w3c.dom.Document} from the {@link Source}s to compare.
     *
     * <p>This is only used if the {@code Source}s used for control
     * and test not already are {@link
     * javax.xml.transform.dom.DOMSource}s.</p>
     *
     * @since XMLUnit 2.2.0
     */
    public DiffBuilder withDocumentBuilderFactory(DocumentBuilderFactory f) {
        documentBuilderFactory = f;
        return this;
    }

    /**
     * Compare the Test-XML {@link #withTest(Object)} with the Control-XML {@link #compare(Object)} and return the
     * collected differences in a {@link Diff} object.
     */
    public Diff build() {

        final DOMDifferenceEngine d = documentBuilderFactory != null
            ? new DOMDifferenceEngine(documentBuilderFactory) : new DOMDifferenceEngine();
        final CollectResultsListener collectResultsListener = new CollectResultsListener(comparisonResultsToCheck);
        d.addDifferenceListener(collectResultsListener);
        if (nodeMatcher != null) {
            d.setNodeMatcher(nodeMatcher);
        }
        d.setDifferenceEvaluator(differenceEvaluator);
        d.setComparisonController(comparisonController);
        for (ComparisonListener comparisonListener : comparisonListeners) {
            d.addComparisonListener(comparisonListener);
        }
        for (ComparisonListener comparisonListener : differenceListeners) {
            d.addDifferenceListener(comparisonListener);
        }
        if (namespaceContext != null) {
            d.setNamespaceContext(namespaceContext);
        }
        if (attributeFilter != null) {
            d.setAttributeFilter(attributeFilter);
        }
        if (nodeFilter != null) {
            d.setNodeFilter(nodeFilter);
        }
        d.compare(wrap(controlSource), wrap(testSource));

        return formatter == null
            ? new Diff(controlSource, testSource, collectResultsListener.getDifferences())
            : new Diff(controlSource, testSource, formatter,
                       collectResultsListener.getDifferences());
    }

    private Source wrap(final Source source) {
        Source newSource = source;
        if (ignoreWhitespace) {
            newSource = documentBuilderFactory != null
                ? new WhitespaceStrippedSource(newSource, documentBuilderFactory)
                : new WhitespaceStrippedSource(newSource);
        }
        if (normalizeWhitespace) {
            newSource = documentBuilderFactory != null
                ? new WhitespaceNormalizedSource(newSource, documentBuilderFactory)
                : new WhitespaceNormalizedSource(newSource);
        }
        if (ignoreComments) {
            newSource = ignoreCommentVersion == null
                ? new CommentLessSource(newSource)
                : new CommentLessSource(newSource, ignoreCommentVersion);
        }
        if (ignoreECW) {
            newSource = documentBuilderFactory != null
                ? new ElementContentWhitespaceStrippedSource(newSource, documentBuilderFactory)
                : new ElementContentWhitespaceStrippedSource(newSource);
        }
        return newSource;
    }

    private static final class CollectResultsListener implements ComparisonListener {

        private final List<Difference> results;
        private final EnumSet<ComparisonResult> comparisonResultsToCheck;

        public CollectResultsListener(final ComparisonResult... comparisonResultsToCheck) {
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
