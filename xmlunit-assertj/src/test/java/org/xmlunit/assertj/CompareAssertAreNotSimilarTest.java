package org.xmlunit.assertj;

import org.junit.Rule;
import org.junit.Test;

import java.io.File;

import static org.xmlunit.assertj.ExpectedException.none;
import static org.xmlunit.assertj.XmlAssert.assertThat;

public class CompareAssertAreNotSimilarTest {

    @Rule
    public ExpectedException thrown = none();

    @Test
    public void testAreNotSimilar_shouldPass() {

        String testXml = "<a><c/><b/></a>";
        String controlXml = "<a><b/><c/></a>";

        assertThat(testXml).and(controlXml).areNotSimilar();
    }

    @Test
    public void testAreNotSimilar_fromFiles_shouldPass() {

        File testXml = new File("../test-resources/test1.xml");
        File controlXml = new File("../test-resources/test2.xml");

        assertThat(testXml).and(controlXml).areNotSimilar();
    }

    @Test
    public void testAreNotSimilar_withIdenticalXmls_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <control instance> and <test instance> to be not similar");

        String testXml = "<Element attr1=\"12\" attr2=\"xy\"/>";
        String controlXml = "<Element attr1=\"12\" attr2=\"xy\"/>";

        assertThat(testXml).and(controlXml).areNotSimilar();
    }

    @Test
    public void testAreNotSimilar_withSimilarXmls_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <control instance> and <test instance> to be not similar");

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

        assertThat(testXml).and(controlXml).areNotSimilar();
    }

    @Test
    public void testAreNotSimilar_withSimilarXmlsIgnoreChildNodesOrders_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <control instance> and <test instance> to be not similar");

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
                .areNotSimilar();
    }

    @Test
    public void testAreNotSimilar_withIgnoreComments_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <control instance> and <test instance> to be not similar");

        String testXml = "<a><!-- test --></a>";
        String controlXml = "<a></a>";

        assertThat(testXml).and(controlXml)
                .ignoreComments()
                .areNotSimilar();
    }

    @Test
    public void testAreNotSimilar_withInvalidTestXml_shouldFailed() {

        thrown.expectAssertionErrorPattern(".*Expecting code not to raise a throwable but caught.*Caught exception during comparison.*");

        String testXml = "abc";
        String controlXml = "<a><b/><c/></a>";

        assertThat(testXml).and(controlXml).areNotSimilar();
    }

    @Test
    public void testAreNotSimilar_withInvalidControlXml_shouldFailed() {

        thrown.expectAssertionErrorPattern(".*Expecting code not to raise a throwable but caught.*Caught exception during comparison.*");

        String testXml = "<a><b/><c/></a>";
        String controlXml = "abc";

        assertThat(testXml).and(controlXml).areNotSimilar();
    }
}
