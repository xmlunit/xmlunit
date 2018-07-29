package org.xmlunit.assertj;

import org.junit.Rule;
import org.junit.Test;

import java.io.File;

import static org.xmlunit.assertj.ExpectedException.none;
import static org.xmlunit.assertj.XmlAssert.assertThat;

public class CompareAssertAreDifferentTest {

    @Rule
    public ExpectedException thrown = none();

    @Test
    public void testAreDifferent_shouldPass() {

        String testXml = "<a><c/><b/></a>";
        String controlXml = "<a><b/><c/></a>";

        assertThat(testXml).and(controlXml).areDifferent();
    }

    @Test
    public void testAreDifferent_fromFiles_shouldPass() {

        File testXml = new File("../test-resources/test1.xml");
        File controlXml = new File("../test-resources/test2.xml");

        assertThat(testXml).and(controlXml).areDifferent();
    }

    @Test
    public void testAreDifferent_withIdenticalXmls_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <control instance> and <test instance> to be different");

        String testXml = "<Element attr1=\"12\" attr2=\"xy\"/>";
        String controlXml = "<Element attr1=\"12\" attr2=\"xy\"/>";

        assertThat(testXml).and(controlXml).areDifferent();
    }

    @Test
    public void testAreDifferent_withSimilarXmls_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <control instance> and <test instance> to be different");

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

        assertThat(testXml).and(controlXml).areDifferent();
    }

    @Test
    public void testAreDifferent_withSimilarXmlsIgnoreChildNodesOrders_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <control instance> and <test instance> to be different");

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
                .areDifferent();
    }

    @Test
    public void testAreDifferent_withIgnoreComments_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <control instance> and <test instance> to be different");

        String testXml = "<a><!-- test --></a>";
        String controlXml = "<a></a>";

        assertThat(testXml).and(controlXml)
                .ignoreComments()
                .areDifferent();
    }

}
