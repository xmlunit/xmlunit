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

        String xml1 = "<a><c/><b/></a>";
        String xml2 = "<a><b/><c/></a>";

        assertThat(xml1).and(xml2).areDifferent();
    }

    @Test
    public void testAreDifferent_fromFiles_shouldPass() {

        File xml1 = new File("../test-resources/test1.xml");
        File xml2 = new File("../test-resources/test2.xml");

        assertThat(xml1).and(xml2).areDifferent();
    }

    @Test
    public void testAreDifferent_withIdenticalXmls_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <control instance> and <test instance> to be different");

        String xml1 = "<Element attr1=\"12\" attr2=\"xy\"/>";
        String xml2 = "<Element attr1=\"12\" attr2=\"xy\"/>";

        assertThat(xml1).and(xml2).areDifferent();
    }

    @Test
    public void testAreDifferent_withSimilarXmls_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <control instance> and <test instance> to be different");

        String xml1 = "<!DOCTYPE a>" +
                "<a xmlns:xyz=\"https://www.xmlunit.com/xyz\">" +
                "   <b>text</b>" +
                "   <c>" +
                "      <d/>" +
                "      <xyz:e/>" +
                "   </c>" +
                "</a>";

        String xml2 = "" +
                "<a xmlns:vwy=\"https://www.xmlunit.com/xyz\">" +
                "   <b><![CDATA[text]]></b>" +
                "   <c>" +
                "      <d/>" +
                "      <vwy:e/>" +
                "   </c>" +
                "</a>";

        assertThat(xml1).and(xml2).areDifferent();
    }

    @Test
    public void testAreDifferent_withSimilarXmlsIgnoreChildNodesOrders_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <control instance> and <test instance> to be different");

        String xml1 = "<!DOCTYPE a>" +
                "<a>" +
                "   <c><d/><e/></c>" +
                "   <b>text</b>" +
                "</a>";

        String xml2 = "" +
                "<a>" +
                "   <b><![CDATA[text]]></b>" +
                "   <c><e/><d/></c>" +
                "</a>";

        assertThat(xml1).and(xml2)
                .ignoreChildNodesOrder()
                .areDifferent();
    }

    @Test
    public void testAreDifferent_withIgnoreComments_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <control instance> and <test instance> to be different");

        String xml1 = "<a><!-- test --></a>";
        String xml2 = "<a></a>";

        assertThat(xml1).and(xml2)
                .ignoreComments()
                .areDifferent();
    }

}
