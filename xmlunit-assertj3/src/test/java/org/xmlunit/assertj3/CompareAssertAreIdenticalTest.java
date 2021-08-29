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
package org.xmlunit.assertj3;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xmlunit.diff.Comparison;
import org.xmlunit.diff.ComparisonFormatter;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.ComparisonType;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.DifferenceEvaluator;
import org.xmlunit.diff.DifferenceEvaluators;
import org.xmlunit.diff.ElementSelectors;
import org.xmlunit.util.Predicate;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static org.xmlunit.assertj3.ExpectedException.none;
import static org.xmlunit.assertj3.XmlAssert.assertThat;
import static org.xmlunit.diff.DifferenceEvaluators.chain;

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
    public void testAreIdentical_shouldFail() {

        thrown.expectAssertionError("Expecting:%n <control instance> and <test instance> to be identical");
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
    public void testAreIdentical_shouldUseCustomFailMessage() {

        thrown.expectAssertionError("Alarm alarm!");

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

        assertThat(testXml)
            .withFailMessage("Alarm alarm!")
            .and(controlXml).areIdentical();
    }

    @Test
    public void testAreIdentical_shouldFailed_withReadableMessage() {

        thrown.expectAssertionError("Expecting:%n <control instance> and <test instance> to be identical");
        thrown.expectAssertionError("Expected attribute value 'xy' but was 'xyz'");
        thrown.expectAssertionError("at /Element[1]/@attr2");
        thrown.expectAssertionError("attr2=\"xyz\"");

        String testXml = "<Element attr2=\"xyz\" attr1=\"12\"/>";
        String controlXml = "<Element attr1=\"12\" attr2=\"xy\"/>";

        assertThat(testXml).and(controlXml).areIdentical();
    }

    @Test
    public void testAreIdentical_shouldFailed_withElementOrderMessage() {

        thrown.expectAssertionError("Expecting:%n <control instance> and <test instance> to be identical");
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

        thrown.expectAssertionError("Expecting:%n <control instance> and <test instance> to be identical");
        thrown.expectAssertionError("Expected child nodelist length '1' but was '3'");
        thrown.expectAssertionError("expected:<<a><b/></a>> but was:<<a>%n <b/>%n</a>>");

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

        String testXml = String.format("<a>%nX <b/>%n</a>");
        String controlXml = "<a>X<b/></a>";

        assertThat(testXml).and(controlXml)
                .ignoreWhitespace()
                .areIdentical();
    }

    @Test
    public void testAreIdentical_withIgnoreElementContentWhitespace_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <control instance> and <test instance> to be identical");
        thrown.expectAssertionError("Expected text value 'x' but was '%nx '");

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
    public void testAreIdentical_withIgnoreComments_1_0_shouldPass() {

        String testXml = "<a><!-- test --></a>";
        String controlXml = "<a></a>";

        assertThat(testXml).and(controlXml)
                .ignoreCommentsUsingXSLTVersion("1.0")
                .areIdentical();
    }

    @Test
    public void testAreIdentical_withIgnoreComments_2_0_shouldPass() {

        String testXml = "<a><!-- test --></a>";
        String controlXml = "<a></a>";

        assertThat(testXml).and(controlXml)
                .ignoreCommentsUsingXSLTVersion("2.0")
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

        thrown.expectAssertionError("Expecting:%n <control instance> and <test instance> to be identical");
        thrown.expectAssertionError("Expected text value 'TestNode' but was 'Test Node'");

        String testXml = String.format("<a>%n  <b>%n  Test%n  Node%n  </b>%n</a>");
        String controlXml = "<a><b>TestNode</b></a>";

        assertThat(testXml).and(controlXml)
                .normalizeWhitespace()
                .areIdentical();
    }

    @Test
    public void testAreIdentical_withInvalidTestXml_shouldFailed() {

        thrown.expectAssertionErrorPattern(".*Expecting code not to raise a throwable but caught.*Caught exception during comparison.*");

        String testXml = "abc";
        String controlXml = "<a><b/><c/></a>";

        assertThat(testXml).and(controlXml).areIdentical();
    }

    @Test
    public void testAreIdentical_withInvalidControlXml_shouldFailed() {

        thrown.expectAssertionErrorPattern(".*Expecting code not to raise a throwable but caught.*Caught exception during comparison.*");

        String testXml = "<a><b/><c/></a>";
        String controlXml = "abc";

        assertThat(testXml).and(controlXml).areIdentical();
    }

    @Test
    public void testAreIdentical_withDifferenceEvaluator_shouldPass() {

        String testXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry>" +
                "       <title>title1</title>" +
                "       <id>id1</id>" +
                "   </entry>" +
                "</feed>";

        String controlXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry>" +
                "       <description>description</description>" +
                "       <uuid>uuid1</uuid>" +
                "   </entry>" +
                "</feed>";


        assertThat(testXml).and(controlXml)
                .withDifferenceEvaluator(
                        chain(DifferenceEvaluators.Default,
                                new IgnoreNodeEvaluator("entry")))
                .areIdentical();
    }

    @Test
    public void testAreIdentical_withAttributeFilter_shouldPass() {


        String testXml = "<Element attr2=\"xyz\" attr1=\"12\"/>";
        String controlXml = "<Element attr1=\"12\" attr2=\"xy\"/>";

        assertThat(testXml).and(controlXml)
                .withAttributeFilter(new AttributeFilter("attr2"))
                .areIdentical();
    }

    @Test
    public void testAreIdentical_withNodeFilter_shouldPass() {

        String testXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry>" +
                "       <title>title1</title>" +
                "       <id>id1</id>" +
                "   </entry>" +
                "</feed>";

        String controlXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry>" +
                "       <description>description</description>" +
                "       <uuid>uuid1</uuid>" +
                "   </entry>" +
                "</feed>";


        assertThat(testXml).and(controlXml)
                .withNodeFilter(new NodeFilter("entry"))
                .areIdentical();
    }

    @Test
    public void testAreIdentical_withComparisonFormatter_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <control instance> and <test instance> to be identical");
        thrown.expectAssertionError("foo");
        thrown.expectAssertionErrorPattern(".*bar.*bar.*");

        String testXml = "<a><c/><b/></a>";
        String controlXml = "<a><b/><c/></a>";

        assertThat(testXml).and(controlXml)
                .withComparisonFormatter(new DummyFormatter())
                .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText))
                .areIdentical();
    }

    @Test
    public void usesDocumentBuilderFactorySpecifiedOnXmlAssert() throws Exception {
        thrown.expectAssertionError("Expecting code not to raise a throwable but caught");
        thrown.expectAssertionError("org.xmlunit.XMLUnitException: Caught exception during comparison");
        thrown.expectAssertionError("java.io.IOException");

        DocumentBuilderFactory dFac = Mockito.mock(DocumentBuilderFactory.class);
        DocumentBuilder b = Mockito.mock(DocumentBuilder.class);
        Mockito.when(dFac.newDocumentBuilder()).thenReturn(b);
        Mockito.doThrow(new IOException())
                .when(b).parse(Mockito.any(InputSource.class));

        String control = "<a><b></b><c/></a>";

        try {
            assertThat(control)
                    .withDocumentBuilderFactory(dFac)
                    .and(control)
                    .areIdentical();
        } finally {
            Mockito.verify(b).parse(Mockito.any(InputSource.class));
        }
    }

    @Test
    public void usesDocumentBuilderFactorySpecifiedCompareXmlAssert() throws Exception {
        thrown.expectAssertionError("Expecting code not to raise a throwable but caught");
        thrown.expectAssertionError("org.xmlunit.XMLUnitException: Caught exception during comparison");
        thrown.expectAssertionError("java.io.IOException");

        DocumentBuilderFactory dFac = Mockito.mock(DocumentBuilderFactory.class);
        DocumentBuilder b = Mockito.mock(DocumentBuilder.class);
        Mockito.when(dFac.newDocumentBuilder()).thenReturn(b);
        Mockito.doThrow(new IOException())
                .when(b).parse(Mockito.any(InputSource.class));

        String control = "<a><b></b><c/></a>";

        try {
            assertThat(control)
                    .and(control)
                    .withDocumentBuilderFactory(dFac)
                    .areIdentical();
        } finally {
            Mockito.verify(b).parse(Mockito.any(InputSource.class));
        }
    }

    @Test
    public void testAreIdentical_withIgnoreChildNodesOrder_shouldPass() {

        String testXml = "<a>" +
                "   <c><d/><e/></c>" +
                "   <b>text</b>" +
                "</a>";

        String controlXml = "<a>" +
                "   <b>text</b>" +
                "   <c><e/><d/></c>" +
                "</a>";

        assertThat(testXml).and(controlXml)
                .ignoreChildNodesOrder()
                .areIdentical();
    }

    private final class IgnoreNodeEvaluator implements DifferenceEvaluator {

        private final String nodeName;

        public IgnoreNodeEvaluator(String nodeName) {
            this.nodeName = nodeName;
        }

        @Override
        public ComparisonResult evaluate(Comparison comparison, ComparisonResult outcome) {
            Node controlNode = comparison.getControlDetails().getTarget();

            do {
                if (controlNode instanceof Element) {
                    Element elem = (Element) controlNode;
                    if (elem.getTagName().equals(nodeName)) {
                        return ComparisonResult.EQUAL;
                    }
                }
                controlNode = controlNode.getParentNode();
            } while (controlNode != null);

            return outcome;
        }
    }

    private final class AttributeFilter implements Predicate<Attr> {

        private final String attrName;

        private AttributeFilter(String attrName) {
            this.attrName = attrName;
        }

        @Override
        public boolean test(Attr toTest) {
            return !attrName.equalsIgnoreCase(toTest.getName());
        }
    }

    private final class NodeFilter implements Predicate<Node> {

        private final String nodeName;

        private NodeFilter(String nodeName) {
            this.nodeName = nodeName;
        }

        @Override
        public boolean test(Node toTest) {
            if (toTest instanceof Element) {
                return !nodeName.equalsIgnoreCase(((Element) toTest).getTagName());
            }
            return true;
        }
    }

    private static final class DummyFormatter implements ComparisonFormatter {
        @Override
        public String getDescription(Comparison difference) {
            return "foo";
        }

        @Override
        public String getDetails(Comparison.Detail details, ComparisonType type,
                                 boolean formatXml) {
            return "bar";
        }
    }
}
