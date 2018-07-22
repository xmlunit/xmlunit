package org.xmlunit.assertj;

import org.junit.Rule;
import org.junit.Test;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.ElementSelectors;

import static org.xmlunit.assertj.ExpectedException.none;
import static org.xmlunit.assertj.XmlAssert.assertThat;

public class CompareAssertAreIdenticalTest {

    @Rule
    public ExpectedException thrown = none();

    @Test
    public void testAreIdentical_withSameAttributesOrder_shouldPass() {

        String xml1 = "<Element attr1=\"12\" attr2=\"xy\"/>";
        String xml2 = "<Element attr1=\"12\" attr2=\"xy\"/>";

        assertThat(xml1).and(xml2).areIdentical();
    }

    @Test
    public void testAreIdentical_withDifferentAttributesOrder_shouldPass() {

        String xml1 = "<Element attr2=\"xy\" attr1=\"12\"/>";
        String xml2 = "<Element attr1=\"12\" attr2=\"xy\"/>";

        assertThat(xml1).and(xml2).areIdentical();
    }

    @Test
    public void testAreSimilar_shouldFailed() {

        thrown.expectAssertionError("Expected node type 'CDATA Section' but was 'Text'");

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

        assertThat(xml1).and(xml2).areIdentical();
    }

    @Test
    public void testAreIdentical_shouldFailed_withReadableMessage() {

        thrown.expectAssertionError("Expected attribute value 'xy' but was 'xyz'");
        thrown.expectAssertionError("at /Element[1]/@attr2");
        thrown.expectAssertionError("attr2=\"xyz\"");

        String xml1 = "<Element attr2=\"xyz\" attr1=\"12\"/>";
        String xml2 = "<Element attr1=\"12\" attr2=\"xy\"/>";

        assertThat(xml1).and(xml2).areIdentical();
    }

    @Test
    public void testAreIdentical_shouldFailed_withElementOrderMessage() {

        thrown.expectAssertionError("Expected child nodelist sequence '0' but was '1'");
        thrown.expectAssertionError("comparing <b...> at /a[1]/b[1] to <b...> at /a[1]/b[1]");

        String xml1 = "<a><c/><b/></a>";
        String xml2 = "<a><b/><c/></a>";

        assertThat(xml1).and(xml2)
                .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText))
                .areIdentical();
    }

    @Test
    public void testAreIdentical_withWhitespaces_shouldFailed() {

        thrown.expectAssertionError("Expected child nodelist length '1' but was '3'");
        thrown.expectAssertionError(String.format("expected:<<a>[<b/>]</a>> but was:<<a>[%n <b/>%n]</a>>"));

        String xml1 = String.format("<a>%n <b/>%n</a>");
        String xml2 = "<a><b/></a>";

        assertThat(xml1).and(xml2).areIdentical();
    }

    @Test
    public void testAreIdentical_withIgnoreWhitespacees_shouldPass() {

        String xml1 = String.format("<a>%n <b/>%n</a>");
        String xml2 = "<a><b/></a>";

        assertThat(xml1).and(xml2)
                .ignoreWhitespace()
                .areIdentical();
    }

    @Test
    public void testAreIdentical_withIgnoreWhitespaceAndTextValue_shouldPass() {

        String xml1 = String.format("<a>%nx <b/>%n</a>");
        String xml2 = "<a>x<b/></a>";

        assertThat(xml1).and(xml2)
                .ignoreWhitespace()
                .areIdentical();
    }

    @Test
    public void testAreIdentical_withIgnoreElementContentWhitespace_shouldFailed() {

        thrown.expectAssertionError(String.format("Expected text value 'x' but was '%nx '"));

        String xml1 = String.format("<a>%nx <b/>%n</a>");
        String xml2 = "<a>x<b/></a>";

        assertThat(xml1).and(xml2)
                .ignoreElementContentWhitespace()
                .areIdentical();
    }

    @Test
    public void testAreIdentical_withIgnoreComments_shouldPass() {

        String xml1 = "<a><!-- test --></a>";
        String xml2 = "<a></a>";

        assertThat(xml1).and(xml2)
                .ignoreComments()
                .areIdentical();
    }

    @Test
    public void testAreIdentical_withNormalizeWhitespace_shouldPass() {

        String xml1 = String.format("<a>%n  <b>%n  Test%n  Node%n  </b>%n</a>");
        String xml2 = "<a><b>Test Node</b></a>";

        assertThat(xml1).and(xml2)
                .normalizeWhitespace()
                .areIdentical();
    }

    @Test
    public void testAreIdentical_withNormalizeWhitespace_shouldFailed() {

        thrown.expectAssertionError(String.format("Expected text value 'TestNode' but was 'Test Node'"));

        String xml1 = String.format("<a>%n  <b>%n  Test%n  Node%n  </b>%n</a>");
        String xml2 = "<a><b>TestNode</b></a>";

        assertThat(xml1).and(xml2)
                .normalizeWhitespace()
                .areIdentical();
    }
}
