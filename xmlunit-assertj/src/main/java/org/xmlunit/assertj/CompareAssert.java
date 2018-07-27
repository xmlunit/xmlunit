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


    @Override
    public CompareAssert withNodeMatcher(NodeMatcher nodeMatcher) {
        diffBuilder.withNodeMatcher(nodeMatcher);
        return this;
    }

    @Override
    public CompareAssert withDifferenceEvaluator(DifferenceEvaluator differenceEvaluator) {
        diffBuilder.withDifferenceEvaluator(differenceEvaluator);
        return this;
    }

    @Override
    public CompareAssert withComparisonController(ComparisonController comparisonController) {
        customComparisonController = comparisonController;
        return this;
    }

    @Override
    public CompareAssert withComparisonListeners(ComparisonListener... comparisonListeners) {
        diffBuilder.withComparisonListeners(comparisonListeners);
        return this;
    }

    @Override
    public CompareAssert withDifferenceListeners(ComparisonListener... comparisonListeners) {
        diffBuilder.withDifferenceListeners(comparisonListeners);
        return this;
    }

    @Override
    public CompareAssert withNamespaceContext(Map<String, String> prefix2Uri) {
        diffBuilder.withNamespaceContext(prefix2Uri);
        return this;
    }

    @Override
    public CompareAssert withAttributeFilter(Predicate<Attr> attributeFilter) {
        diffBuilder.withAttributeFilter(attributeFilter);
        return this;
    }

    @Override
    public CompareAssert withNodeFilter(Predicate<Node> nodeFilter) {
        diffBuilder.withNodeFilter(nodeFilter);
        return this;
    }

    @Override
    public CompareAssert withComparisonFormatter(ComparisonFormatter formatter) {
        diffBuilder.withComparisonFormatter(formatter);
        return this;
    }

    public CompareAssert withDocumentBuilderFactory(DocumentBuilderFactory f) {
        diffBuilder.withDocumentBuilderFactory(f);
        return this;
    }

    public CompareAssert ignoreWhitespace() {
        formatXml = true;
        diffBuilder.ignoreWhitespace();
        return this;
    }

    public CompareAssert normalizeWhitespace() {
        formatXml = true;
        diffBuilder.normalizeWhitespace();
        return this;
    }

    public CompareAssert ignoreElementContentWhitespace() {
        diffBuilder.ignoreElementContentWhitespace();
        return this;
    }

    public CompareAssert ignoreComments() {
        diffBuilder.ignoreComments();
        return this;
    }

    public CompareAssert ignoreCommentsUsingXSLTVersion(String xsltVersion) {
        diffBuilder.ignoreCommentsUsingXSLTVersion(xsltVersion);
        return this;
    }

    /**
     * Equivalent for <pre>withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText))</pre>
     */
    public CompareAssert ignoreChildNodesOrder() {
        diffBuilder.withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText));
        return this;
    }

    public CompareAssert areIdentical() {
        diffBuilder.checkForIdentical();
        compare(ComparisonResult.EQUAL);
        return this;
    }

    public CompareAssert areSimilar() {
        diffBuilder.checkForSimilar();
        compare(ComparisonResult.SIMILAR);
        return this;
    }

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
