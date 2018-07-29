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

        String testXml = "<Element attr1=\"12\" attr2=\"xy\"/>";
        String controlXml = "<Element attr1=\"12\" attr2=\"xy\"/>";

        assertThat(testXml).and(controlXml).areIdentical();
    }

    @Test
    public void testAreIdentical_withDifferentAttributesOrder_shouldPass() {

        String testXml = "<Element attr2=\"xy\" attr1=\"12\"/>";
        String controlXml = "<Element attr1=\"12\" attr2=\"xy\"/>";

        assertThat(testXml).and(controlXml).areIdentical();
    }

    @Test
    public void testAreSimilar_shouldFailed() {

        thrown.expectAssertionError("Expected node type 'CDATA Section' but was 'Text'");

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

        assertThat(testXml).and(controlXml).areIdentical();
    }

    @Test
    public void testAreIdentical_shouldFailed_withReadableMessage() {

        thrown.expectAssertionError("Expected attribute value 'xy' but was 'xyz'");
        thrown.expectAssertionError("at /Element[1]/@attr2");
        thrown.expectAssertionError("attr2=\"xyz\"");

        String testXml = "<Element attr2=\"xyz\" attr1=\"12\"/>";
        String controlXml = "<Element attr1=\"12\" attr2=\"xy\"/>";

        assertThat(testXml).and(controlXml).areIdentical();
    }

    @Test
    public void testAreIdentical_shouldFailed_withElementOrderMessage() {

        thrown.expectAssertionError("Expected child nodelist sequence '0' but was '1'");
        thrown.expectAssertionError("comparing <b...> at /a[1]/b[1] to <b...> at /a[1]/b[1]");

        String testXml = "<a><c/><b/></a>";
        String controlXml = "<a><b/><c/></a>";

        assertThat(testXml).and(controlXml)
                .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText))
                .areIdentical();
    }

    @Test
    public void testAreIdentical_withWhitespaces_shouldFailed() {

        thrown.expectAssertionError("Expected child nodelist length '1' but was '3'");
        thrown.expectAssertionError(String.format("expected:<<a>[<b/>]</a>> but was:<<a>[%n <b/>%n]</a>>"));

        String testXml = String.format("<a>%n <b/>%n</a>");
        String controlXml = "<a><b/></a>";

        assertThat(testXml).and(controlXml).areIdentical();
    }

    @Test
    public void testAreIdentical_withIgnoreWhitespacees_shouldPass() {

        String testXml = String.format("<a>%n <b/>%n</a>");
        String controlXml = "<a><b/></a>";

        assertThat(testXml).and(controlXml)
                .ignoreWhitespace()
                .areIdentical();
    }

    @Test
    public void testAreIdentical_withIgnoreWhitespaceAndTextValue_shouldPass() {

        String testXml = String.format("<a>%nx <b/>%n</a>");
        String controlXml = "<a>x<b/></a>";

        assertThat(testXml).and(controlXml)
                .ignoreWhitespace()
                .areIdentical();
    }

    @Test
    public void testAreIdentical_withIgnoreElementContentWhitespace_shouldFailed() {

        thrown.expectAssertionError(String.format("Expected text value 'x' but was '%nx '"));

        String testXml = String.format("<a>%nx <b/>%n</a>");
        String controlXml = "<a>x<b/></a>";

        assertThat(testXml).and(controlXml)
                .ignoreElementContentWhitespace()
                .areIdentical();
    }

    @Test
    public void testAreIdentical_withIgnoreComments_shouldPass() {

        String testXml = "<a><!-- test --></a>";
        String controlXml = "<a></a>";

        assertThat(testXml).and(controlXml)
                .ignoreComments()
                .areIdentical();
    }

    @Test
    public void testAreIdentical_withNormalizeWhitespace_shouldPass() {

        String testXml = String.format("<a>%n  <b>%n  Test%n  Node%n  </b>%n</a>");
        String controlXml = "<a><b>Test Node</b></a>";

        assertThat(testXml).and(controlXml)
                .normalizeWhitespace()
                .areIdentical();
    }

    @Test
    public void testAreIdentical_withNormalizeWhitespace_shouldFailed() {

        thrown.expectAssertionError(String.format("Expected text value 'TestNode' but was 'Test Node'"));

        String testXml = String.format("<a>%n  <b>%n  Test%n  Node%n  </b>%n</a>");
        String controlXml = "<a><b>TestNode</b></a>";

        assertThat(testXml).and(controlXml)
                .normalizeWhitespace()
                .areIdentical();
    }
}
