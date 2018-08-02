package org.xmlunit.assertj;

import org.junit.Rule;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.xmlunit.diff.Comparison;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.DifferenceEvaluator;
import org.xmlunit.diff.DifferenceEvaluators;

import java.io.File;

import static org.xmlunit.assertj.ExpectedException.none;
import static org.xmlunit.assertj.XmlAssert.assertThat;
import static org.xmlunit.diff.ComparisonType.ATTR_VALUE;
import static org.xmlunit.diff.DifferenceEvaluators.chain;

public class CompareAssertAreSimilarTest {

    @Rule
    public ExpectedException thrown = none();

    @Test
    public void testAreSimilar_shouldPass() {

        String testXml = "<!DOCTYPE a>" +
                "<a xmlns:xyz=\"https://www.xmlunit.com/xyz\">" +
                "   <b>text</b>" +
                "   <c>" +
                "      <d/>" +
                "      <xyz:e/>" +
                "   </c>" +
                "</a>";

        String controlXml = "" +
                "<a xmlns:vwy=\"https://www.xmlunit.com/xyz\">" +
                "   <b><![CDATA[text]]></b>" +
                "   <c>" +
                "      <d/>" +
                "      <vwy:e/>" +
                "   </c>" +
                "</a>";

        assertThat(testXml).and(controlXml).areSimilar();
    }

    @Test
    public void testAreSimilar_withIgnoreChildNodesOrders_shouldPass() {

        String testXml = "<!DOCTYPE a>" +
                "<a>" +
                "   <c><d/><e/></c>" +
                "   <b>text</b>" +
                "</a>";

        String controlXml = "" +
                "<a>" +
                "   <b><![CDATA[text]]></b>" +
                "   <c><e/><d/></c>" +
                "</a>";

        assertThat(testXml).and(controlXml)
                .ignoreChildNodesOrder()
                .areSimilar();
    }

    @Test
    public void testAreSimilar_fromFiles_shouldFailed() {

        thrown.expectAssertionErrorPattern(".*Expecting:.*<(?:.*test2\\.xml)> and <(?:.*test1\\.xml)> to be similar.*");
        thrown.expectAssertionError("Expected processing instruction data 'href=\"animal.xsl\" type=\"text/xsl\"' but was 'type=\"text/xsl\" href=\"animal.xsl\"");

        File testXml = new File("../test-resources/test1.xml");
        File controlXml = new File("../test-resources/test2.xml");

        assertThat(testXml).and(controlXml).areSimilar();
    }

    @Test
    public void testAreSimilar_withInvalidTestXml_shouldFailed() {

        thrown.expectAssertionErrorPattern(".*Expecting code not to raise a throwable but caught.*Caught exception during comparison.*");

        String testXml = "abc";
        String controlXml = "<a><b/><c/></a>";

        assertThat(testXml).and(controlXml).areSimilar();
    }

    @Test
    public void testAreSimilar_withInvalidControlXml_shouldFailed() {

        thrown.expectAssertionErrorPattern(".*Expecting code not to raise a throwable but caught.*Caught exception during comparison.*");

        String testXml = "<a><b/><c/></a>";
        String controlXml = "abc";

        assertThat(testXml).and(controlXml).areSimilar();
    }

    @Test
    public void testAreSimilar_withDifferenceEvaluator_shouldPass() {

        final String testXml = "<a><b attr=\"abc\"></b></a>";
        final String controlXml = "<a><b attr=\"xyz\"></b></a>";

        assertThat(testXml).and(controlXml)
                .withDifferenceEvaluator(
                        chain(DifferenceEvaluators.Default,
                                new IgnoreAttributeValueEvaluator("attr")))
                .areSimilar();
    }

    private final class IgnoreAttributeValueEvaluator implements DifferenceEvaluator {

        private final String attributeName;

        public IgnoreAttributeValueEvaluator(String attributeName) {
            this.attributeName = attributeName;
        }

        @Override
        public ComparisonResult evaluate(Comparison comparison, ComparisonResult outcome) {
            final Node controlNode = comparison.getControlDetails().getTarget();
            if (comparison.getType() == ATTR_VALUE && controlNode instanceof Attr) {
                Attr attr = (Attr) controlNode;
                if (attr.getName().equals(attributeName)) {
                    return ComparisonResult.SIMILAR;
                }
            }
            return outcome;
        }
    }
}
