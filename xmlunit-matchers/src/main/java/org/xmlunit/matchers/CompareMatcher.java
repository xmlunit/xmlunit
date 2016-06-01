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

package org.xmlunit.matchers;

import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Comparison;
import org.xmlunit.diff.ComparisonControllers;
import org.xmlunit.diff.ComparisonFormatter;
import org.xmlunit.diff.ComparisonListener;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.DefaultComparisonFormatter;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.DifferenceEvaluator;
import org.xmlunit.diff.ElementSelector;
import org.xmlunit.diff.NodeMatcher;
import org.xmlunit.util.Predicate;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

/**
 * This Hamcrest {@link Matcher} compares two XML sources with each others.
 * <p>
 * The Test-Object and Control-Object can be all types of input supported by {@link Input#from(Object)}.
 * <p>
 * <b>Simple Example</b><br>
 * This example will throw an AssertionError: "Expected attribute value 'abc' but was 'xyz'".
 * 
 * <pre>
 * final String control = &quot;&lt;a&gt;&lt;b attr=\&quot;abc\&quot;&gt;&lt;/b&gt;&lt;/a&gt;&quot;;
 * final String test = &quot;&lt;a&gt;&lt;b attr=\&quot;xyz\&quot;&gt;&lt;/b&gt;&lt;/a&gt;&quot;;
 * 
 * assertThat(test, CompareMatcher.isIdenticalTo(control));
 * </pre>
 * <p>
 * <b>Complex Example</b><br>
 * In some cases you may have a static factory method for your project which wraps all project-specific configurations
 * like customized {@link ElementSelector} or {@link DifferenceEvaluator}.
 * 
 * <pre>
 * 
 * public static CompareMatcher isMyProjSimilarTo(final File file) {
 *     return CompareMatcher.isSimilarTo(file)
 *         .throwComparisonFailure()
 *         .normalizeWhitespace()
 *         .ignoreComments()
 *         .withNodeMatcher(new DefaultNodeMatcher(new MyElementSelector()))
 *         .withDifferenceEvaluator(DifferenceEvaluators.chain(
 *             DifferenceEvaluators.Default, new MyDifferenceEvaluator()));
 * }
 * </pre>
 * 
 * And then somewhere in your Tests:
 * 
 * <pre>
 * assertThat(test, isMyProjSimilarTo(controlFile));
 * </pre>
 */
public final class CompareMatcher extends BaseMatcher<Object> {

    private static final Logger LOGGER = Logger.getLogger(CompareMatcher.class.getName());

    private final DiffBuilder diffBuilder;

    private boolean throwComparisonFailure;
    
    private ComparisonResult checkFor;

    private Diff diffResult;

    private boolean formatXml;

    private static final ComparisonFormatter DEFAULT_FORMATTER = new DefaultComparisonFormatter();

    private ComparisonFormatter comparisonFormatter = DEFAULT_FORMATTER;

    private static Constructor<?> comparisonFailureConstructor;

    private CompareMatcher(Object control) {
        super();
        diffBuilder = DiffBuilder.compare(control);
    }

    /**
     * Create a {@link CompareMatcher} which compares the test-Object with the given control Object for identity.
     * <p>
     * As input all types are supported which are supported by {@link Input#from(Object)}.
     */
    @Factory
    public static CompareMatcher isIdenticalTo(final Object control) {
        return new CompareMatcher(control).checkForIdentical();
    }

    /**
     * Create a {@link CompareMatcher} which compares the test-Object with the given control Object for similarity.
     * <p>
     * Example for Similar: The XML node "&lt;a&gt;Text&lt;/a&gt;" and "&lt;a&gt;&lt;![CDATA[Text]]&gt;&lt;/a&gt;" are
     * similar and the Test will not fail.
     * <p>
     * The rating, if a node is similar, will be done by the {@link DifferenceEvaluators#Default}.
     * See {@link DiffBuilder#withDifferenceEvaluator(DifferenceEvaluator)}
     * <p>
     * As input all types are supported which are supported by {@link Input#from(Object)}.
     */
    @Factory
    public static CompareMatcher isSimilarTo(final Object control) {
        return new CompareMatcher(control).checkForSimilar();
    }

    private CompareMatcher checkForSimilar() {
        diffBuilder.checkForSimilar();
        checkFor = ComparisonResult.SIMILAR;
        return this;
    }

    private CompareMatcher checkForIdentical() {
        diffBuilder.checkForIdentical();
        checkFor = ComparisonResult.EQUAL;
        return this;
    }

    /**
     * @see DiffBuilder#ignoreWhitespace()
     */
    public CompareMatcher ignoreWhitespace() {
        formatXml = true;
        diffBuilder.ignoreWhitespace();
        return this;
    }

    /**
     * @see DiffBuilder#normalizeWhitespace()
     */
    public CompareMatcher normalizeWhitespace() {
        formatXml = true;
        diffBuilder.normalizeWhitespace();
        return this;
    }

    /**
     * @see DiffBuilder#ignoreComments()
     */
    public CompareMatcher ignoreComments() {
        diffBuilder.ignoreComments();
        return this;
    }

    /**
     * @see DiffBuilder#withNodeMatcher(NodeMatcher)
     */
    public CompareMatcher withNodeMatcher(NodeMatcher nodeMatcher) {
        diffBuilder.withNodeMatcher(nodeMatcher);
        return this;
    }

    /**
     * @see DiffBuilder#withDifferenceEvaluator(DifferenceEvaluator)
     */
    public CompareMatcher withDifferenceEvaluator(DifferenceEvaluator differenceEvaluator) {
        diffBuilder.withDifferenceEvaluator(differenceEvaluator);
        return this;
    }

    /**
     * @see DiffBuilder#withComparisonListeners(ComparisonListener...)
     */
    public CompareMatcher withComparisonListeners(ComparisonListener... comparisonListeners) {
        diffBuilder.withComparisonListeners(comparisonListeners);
        return this;
    }

    /**
     * @see DiffBuilder#withDifferenceListeners(ComparisonListener...)
     */
    public CompareMatcher withDifferenceListeners(ComparisonListener... comparisonListeners) {
        diffBuilder.withDifferenceListeners(comparisonListeners);
        return this;
    }

    /**
     * @see DiffBuilder#withNamespaceContext(Map)
     *
     * @since XMLUnit 2.1.0
     */
    public CompareMatcher withNamespaceContext(Map<String, String> prefix2Uri) {
        diffBuilder.withNamespaceContext(prefix2Uri);
        return this;
    }

    /**
     * @see DiffBuilder#withAttributeFilter
     */
    public CompareMatcher withAttributeFilter(Predicate<Attr> attributeFilter) {
        diffBuilder.withAttributeFilter(attributeFilter);
        return this;
    }

    /**
     * @see DiffBuilder#withNodeFilter
     */
    public CompareMatcher withNodeFilter(Predicate<Node> nodeFilter) {
        diffBuilder.withNodeFilter(nodeFilter);
        return this;
    }

    /**
     * Instead of Matcher returning <code>false</code> a {@link org.junit.ComparisonFailure} will be thrown.
     * <p>
     * The advantage over the standard Matcher behavior is, that the ComparisonFailure can provide the effected
     * Control-Node and Test-Node in separate Properties.<br>
     * Eclipse, NetBeans and IntelliJ can provide a nice DIFF-View for the two values.<br>
     * ComparisonFailure is also used in {@link org.junit.Assert#assertEquals(Object, Object)} if both values are
     * {@link String}s.
     * <p>
     * The only disadvantage is, that you can't combine the {@link CompareMatcher} with other Matchers
     * (like {@link org.hamcrest.CoreMatchers#not(Object)}) anymore. The following code will NOT WORK properly:
     * <code>assertThat(test, not(isSimilarTo(control).throwComparisonFailure()))</code> 
     */
    public CompareMatcher throwComparisonFailure() {
        throwComparisonFailure = true;
        return this;
    }

    /**
     * Use a custom Formatter for the Error Messages. The defaultFormatter is {@link DefaultComparisonFormatter}.
     */
    public CompareMatcher withComparisonFormatter(ComparisonFormatter comparisonFormatter) {
        this.comparisonFormatter = comparisonFormatter;
        return this;
    }

    /**
     * @see DiffBuilder#withDocumentBuilderFactory
     * @since XMLUnit 2.2.0
     */
    public CompareMatcher withDocumentBuilderFactory(DocumentBuilderFactory f) {
        diffBuilder.withDocumentBuilderFactory(f);
        return this;
    }

    @Override
    public boolean matches(Object item) {

        if (checkFor == ComparisonResult.EQUAL) {
            diffBuilder.withComparisonController(ComparisonControllers.StopWhenSimilar);
        } else if (checkFor == ComparisonResult.SIMILAR) {
            diffBuilder.withComparisonController(ComparisonControllers.StopWhenDifferent);
        }

        diffResult = diffBuilder.withTest(item).build();

        if (!diffResult.hasDifferences()) {
            return true;
        }

        if (throwComparisonFailure) {
            AssertionError assertionError = createComparisonFailure();
            if (assertionError != null)
                throw assertionError;
        }

        return false;
    }

    /**
     * @return an instants of {@link org.junit.ComparisonFailure} or <code>null</code> if the class is not available.
     */
    private AssertionError createComparisonFailure() {

        final Comparison difference = firstComparison();
        final String reason = createReasonPrefix(diffResult.getControlSource().getSystemId(), difference);
        final String controlString = comparisonFormatter.getDetails(difference.getControlDetails(), difference
            .getType(), formatXml);
        final String testString = comparisonFormatter.getDetails(difference.getTestDetails(), difference.getType(),
            formatXml);

        return createComparisonFailure(reason, controlString, testString);
    }

    /**
     * Calls the Constructor {@link org.junit.ComparisonFailure#ComparisonFailure(String, String, String)} with
     * reflections and return <code>null</code> if the {@link org.junit.ComparisonFailure} class is not available.
     */
    private AssertionError createComparisonFailure(final String reason, final String controlString,
            final String testString) {
        try {
            if (comparisonFailureConstructor == null) {
                final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                Class<?> comparisonFailureClass = classLoader.loadClass("org.junit.ComparisonFailure");
                comparisonFailureConstructor = comparisonFailureClass.getConstructor(String.class, String.class,
                    String.class);
            }
            return (AssertionError) comparisonFailureConstructor.newInstance(reason, controlString, testString);
        } catch (Exception e) {
            // ClassNotFoundException, NoSuchMethodException, InstantiationException,
            // IllegalAccessException, InvocationTargetException
            LOGGER.info("Either add junit to your classpath or do not call '.throwComparisonFailure()'. " + e);
        }

        return null;
    }

    @Override
    public void describeTo(Description description) {
        if (diffResult == null) {
            description.appendText(" is ")
                .appendText(checkFor == ComparisonResult.EQUAL ? "equal" : "similar")
                .appendText(" to the control document");
            return;
        }
        final Comparison difference = firstComparison();
        final String reason = createReasonPrefix(diffResult.getControlSource().getSystemId(), difference);
        final String testString = comparisonFormatter.getDetails(difference.getControlDetails(), difference.getType(),
            formatXml);

        description.appendText(String.format("%s:\n%s", reason, testString));
    }

    private String createReasonPrefix(final String systemId, final Comparison difference) {
        final String description = comparisonFormatter.getDescription(difference);
        final String reason;
        if (systemId == null) {
            reason = description;
        } else {
            reason = String.format("In Source '%s' %s", systemId, description);
        }
        return reason;
    }

    @Override
    public void describeMismatch(final Object item, final Description description) {
        final Comparison difference = firstComparison();
        final String controlString = comparisonFormatter.getDetails(difference.getTestDetails(), difference.getType(),
            formatXml);

        description.appendText(String.format("result was: \n%s", controlString));
    }

    private Comparison firstComparison() {
        return diffResult.getDifferences().iterator().next().getComparison();
    }
}
