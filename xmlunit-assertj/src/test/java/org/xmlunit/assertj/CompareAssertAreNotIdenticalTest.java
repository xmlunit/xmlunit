package org.xmlunit.assertj;

import org.junit.Rule;
import org.junit.Test;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.ElementSelectors;

import static org.xmlunit.assertj.ExpectedException.none;
import static org.xmlunit.assertj.XmlAssert.assertThat;

public class CompareAssertAreNotIdenticalTest {

    @Rule
    public ExpectedException thrown = none();

    @Test
    public void testAreNotIdentical_withSameAttributesOrder_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <control instance> and <test instance> to be not identical");

        String testXml = "<Element attr1=\"12\" attr2=\"xy\"/>";
        String controlXml = "<Element attr1=\"12\" attr2=\"xy\"/>";

        assertThat(testXml).and(controlXml).areNotIdentical();
    }

    @Test
    public void testAreNotIdentical_withDifferentAttributesOrder_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <control instance> and <test instance> to be not identical");

        String testXml = "<Element attr2=\"xy\" attr1=\"12\"/>";
        String controlXml = "<Element attr1=\"12\" attr2=\"xy\"/>";

        assertThat(testXml).and(controlXml).areNotIdentical();
    }

    @Test
    public void testAreNotIdentical_withCDataInsteadText_shouldPass() {

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

        assertThat(testXml).and(controlXml).areNotIdentical();
    }

    @Test
    public void testAreNotIdentical_withAttributeDifferentValues_shouldPass2() {

        String testXml = "<Element attr1=\"12\" attr2=\"xyz\"/>";
        String controlXml = "<Element attr1=\"12\" attr2=\"xy\"/>";

        assertThat(testXml).and(controlXml).areNotIdentical();
    }

    @Test
    public void testAreNotIdentical_withDifferentElementOrder_shouldPass() {

        String testXml = "<a><c/><b/></a>";
        String controlXml = "<a><b/><c/></a>";

        assertThat(testXml).and(controlXml)
                .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText))
                .areNotIdentical();
    }

    @Test
    public void testAreNotIdentical_withWhitespaces_shouldPass() {

        String testXml = String.format("<a>%n <b/>%n</a>");
        String controlXml = "<a><b/></a>";

        assertThat(testXml).and(controlXml).areNotIdentical();
    }

    @Test
    public void testAreNotIdentical_withIgnoreWhitespaces_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <control instance> and <test instance> to be not identical");

        String testXml = String.format("<a>%n <b/>%n</a>");
        String controlXml = "<a><b/></a>";

        assertThat(testXml).and(controlXml)
                .ignoreWhitespace()
                .areNotIdentical();
    }

    @Test
    public void testAreNotIdentical_withIgnoreWhitespaceAndTextValue_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <control instance> and <test instance> to be not identical");

        String testXml = String.format("<a>%nX <b/>%n</a>");
        String controlXml = "<a>X<b/></a>";

        assertThat(testXml).and(controlXml)
                .ignoreWhitespace()
                .areNotIdentical();
    }

    @Test
    public void testAreNotIdentical_withIgnoreElementContentWhitespace_shouldPass() {

        String testXml = String.format("<a>%nx <b/>%n</a>");
        String controlXml = "<a>x<b/></a>";

        assertThat(testXml).and(controlXml)
                .ignoreElementContentWhitespace()
                .areNotIdentical();
    }

    @Test
    public void testAreNotIdentical_withIgnoreComments_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <control instance> and <test instance> to be not identical");

        String testXml = "<a><!-- test --></a>";
        String controlXml = "<a></a>";

        assertThat(testXml).and(controlXml)
                .ignoreComments()
                .areNotIdentical();
    }

    @Test
    public void testAreNotIdentical_withNormalizeWhitespace_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <control instance> and <test instance> to be not identical");

        String testXml = String.format("<a>%n  <b>%n  Test%n  Node%n  </b>%n</a>");
        String controlXml = "<a><b>Test Node</b></a>";

        assertThat(testXml).and(controlXml)
                .normalizeWhitespace()
                .areNotIdentical();
    }

    @Test
    public void testAreNotIdentical_withNormalizeWhitespace_shouldPass() {

        String testXml = String.format("<a>%n  <b>%n  Test%n  Node%n  </b>%n</a>");
        String controlXml = "<a><b>TestNode</b></a>";

        assertThat(testXml).and(controlXml)
                .normalizeWhitespace()
                .areNotIdentical();
    }

    @Test
    public void testAreNotIdentical_withInvalidTestXml_shouldFailed() {

        thrown.expectAssertionErrorPattern(".*Expecting code not to raise a throwable but caught.*Caught exception during comparison.*");

        String testXml = "abc";
        String controlXml = "<a><b/><c/></a>";

        assertThat(testXml).and(controlXml).areNotIdentical();
    }

    @Test
    public void testAreNotIdentical_withInvalidControlXml_shouldFailed() {

        thrown.expectAssertionErrorPattern(".*Expecting code not to raise a throwable but caught.*Caught exception during comparison.*");

        String testXml = "<a><b/><c/></a>";
        String controlXml = "abc";

        assertThat(testXml).and(controlXml).areNotIdentical();
    }
}
