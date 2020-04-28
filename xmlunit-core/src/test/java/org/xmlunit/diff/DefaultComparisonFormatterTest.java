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

package org.xmlunit.diff;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.util.Convert;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class DefaultComparisonFormatterTest {

    private DefaultComparisonFormatter compFormatter = new DefaultComparisonFormatter();

    private static final boolean JAVA_9_PLUS, JAVA_14_PLUS;
    static {
        boolean j9 = false;
        try {
            Class.forName("java.lang.module.ModuleDescriptor");
            j9 = true;
        } catch (ClassNotFoundException e) {
        } catch (Error e) {
        }
        JAVA_9_PLUS = j9;
        boolean j14 = false;
        try {
            Class.forName("java.lang.reflect.RecordComponent");
            j14 = true;
        } catch (ClassNotFoundException e) {
        } catch (Error e) {
        }
        JAVA_14_PLUS = j14;
    }

    @Test
    public void testComparisonType_XML_VERSION() {
        // prepare data
        Diff diff = DiffBuilder.compare("<?xml version=\"1.0\"?><a/>").withTest("<?xml version=\"1.1\"?><a/>").build();
        assertPreRequirements(diff, ComparisonType.XML_VERSION);
        Comparison firstDiff = diff.getDifferences().iterator().next().getComparison();

        // run test
        String description = compFormatter.getDescription(firstDiff);
        String controlDetails =  getDetails(firstDiff.getControlDetails(), firstDiff.getType());
        String testDetails =  getDetails(firstDiff.getTestDetails(), firstDiff.getType());

        // validate result
        Assert.assertEquals("Expected xml version '1.0' but was '1.1' - "
                + "comparing <a...> at / to <?xml version=\"1.1\"?><a...> at /", description);

        assertEquals("<a/>", controlDetails);
        assertEquals("<?xml version=\"1.1\"?>\n<a/>", testDetails);
    }

    @Test
    public void testComparisonType_XML_STANDALONE() {
        // prepare data
        Diff diff = DiffBuilder.compare(
            "<?xml version=\"1.0\" standalone=\"yes\"?><a b=\"x\"><b/></a>")
            .withTest("<?xml version=\"1.0\" standalone=\"no\"?><a b=\"x\"><b/></a>").build();
        assertPreRequirements(diff, ComparisonType.XML_STANDALONE);
        Comparison firstDiff = diff.getDifferences().iterator().next().getComparison();

        // run test
        String description = compFormatter.getDescription(firstDiff);
        String controlDetails =  getDetails(firstDiff.getControlDetails(), firstDiff.getType());
        String testDetails =  getDetails(firstDiff.getTestDetails(), firstDiff.getType());

        // validate result
        Assert.assertEquals("Expected xml standalone 'true' but was 'false' - "
                + "comparing <?xml version=\"1.0\" standalone=\"yes\"?><a...> at / to <a...> at /", description);

        assertEquals("<?xml version=\"1.0\" standalone=\"yes\"?>\n<a b=\"x\">\n  ...", controlDetails);
        assertEquals("<a b=\"x\">\n  ...", testDetails);
    }

    @Test
    public void testComparisonType_XML_ENCODING() {
        // prepare data
        Diff diff = DiffBuilder.compare(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><a/>").withTest(
                "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><a/>").build();
        assertPreRequirements(diff, ComparisonType.XML_ENCODING);
        Comparison firstDiff = diff.getDifferences().iterator().next().getComparison();

        // run test
        String description = compFormatter.getDescription(firstDiff);
        String controlDetails =  getDetails(firstDiff.getControlDetails(), firstDiff.getType());
        String testDetails =  getDetails(firstDiff.getTestDetails(), firstDiff.getType());

        // validate result
        Assert.assertEquals("Expected xml encoding 'UTF-8' but was 'ISO-8859-1' - "
                + "comparing <?xml version=\"1.0\" encoding=\"UTF-8\"?><a...> at / "
                + "to <?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><a...> at /", description);

        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<a/>", controlDetails);
        assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n<a/>", testDetails);
    }

    @Test
    public void testComparisonType_HAS_DOCTYPE_DECLARATION() throws Exception {
        // prepare data
        DocumentBuilderFactory dbf = getDocumentBuilderFactoryWithoutValidation();
        Document controlDoc = Convert.toDocument(Input.fromString("<!DOCTYPE Book><a/>").build(), dbf);

        Diff diff = DiffBuilder.compare(controlDoc).withTest("<a/>")
            .withDifferenceEvaluator(DifferenceEvaluators.downgradeDifferencesToEqual(ComparisonType.CHILD_NODELIST_LENGTH))
            .withNodeFilter(NodeFilters.AcceptAll).build();
        assertPreRequirements(diff, ComparisonType.HAS_DOCTYPE_DECLARATION);
        Comparison firstDiff = diff.getDifferences().iterator().next().getComparison();

        // run test
        String description = compFormatter.getDescription(firstDiff);
        String controlDetails =  getDetails(firstDiff.getControlDetails(), firstDiff.getType());
        String testDetails =  getDetails(firstDiff.getTestDetails(), firstDiff.getType());

        // validate result
        Assert.assertEquals("Expected has doctype declaration 'true' but was 'false' - "
                + "comparing <!DOCTYPE Book><a...> at / to <a...> at /", description);

        assertEquals("<!DOCTYPE Book>\n<a/>", controlDetails);
        assertEquals("<a/>", testDetails);
    }

    @Test
    public void testComparisonType_DOCTYPE_NAME() throws Exception {
        // prepare data
        DocumentBuilderFactory dbf = getDocumentBuilderFactoryWithoutValidation();
        Document controlDoc = Convert.toDocument(Input.fromString("<!DOCTYPE Book ><a/>").build(), dbf);
        Document testDoc = Convert.toDocument(Input.fromString("<!DOCTYPE XY ><a/>").build(), dbf);

        Diff diff = DiffBuilder.compare(controlDoc).withTest(testDoc)
            .withDifferenceEvaluator(DifferenceEvaluators.downgradeDifferencesToEqual(ComparisonType.CHILD_NODELIST_LENGTH))
            .withNodeFilter(NodeFilters.AcceptAll).build();
        assertPreRequirements(diff, ComparisonType.DOCTYPE_NAME);
        Comparison firstDiff = diff.getDifferences().iterator().next().getComparison();

        // run test
        String description = compFormatter.getDescription(firstDiff);
        String controlDetails =  getDetails(firstDiff.getControlDetails(), firstDiff.getType());
        String testDetails =  getDetails(firstDiff.getTestDetails(), firstDiff.getType());

        // validate result
        Assert.assertEquals("Expected doctype name 'Book' but was 'XY' - "
                + "comparing <!DOCTYPE Book><a...> at / to <!DOCTYPE XY><a...> at /", description);

        assertEquals("<!DOCTYPE Book>\n<a/>", controlDetails);
        assertEquals("<!DOCTYPE XY>\n<a/>", testDetails);
    }

    @Test
    public void testComparisonType_DOCTYPE_PUBLIC_ID() throws Exception {
        // prepare data
        DocumentBuilderFactory dbf = getDocumentBuilderFactoryWithoutValidation();
        Document controlDoc = Convert.toDocument(Input.fromString(
                "<!DOCTYPE Book PUBLIC \"XMLUNIT/TEST/PUB\" \"http://example.org/nonsense\"><a/>").build(), dbf);
        Document testDoc = Convert.toDocument(Input.fromString(
                "<!DOCTYPE Book SYSTEM \"http://example.org/nonsense\"><a/>").build(), dbf);

        Diff diff = DiffBuilder.compare(controlDoc).withTest(testDoc)
            .withDifferenceEvaluator(DifferenceEvaluators.downgradeDifferencesToEqual(ComparisonType.CHILD_NODELIST_LENGTH))
            .withNodeFilter(NodeFilters.AcceptAll).build();
        assertPreRequirements(diff, ComparisonType.DOCTYPE_PUBLIC_ID);
        Comparison firstDiff = diff.getDifferences().iterator().next().getComparison();

        // run test
        String description = compFormatter.getDescription(firstDiff);
        String controlDetails =  getDetails(firstDiff.getControlDetails(), firstDiff.getType());
        String testDetails =  getDetails(firstDiff.getTestDetails(), firstDiff.getType());

        // validate result
        Assert.assertEquals("Expected doctype public id 'XMLUNIT/TEST/PUB' but was 'null' - "
                + "comparing <!DOCTYPE Book PUBLIC \"XMLUNIT/TEST/PUB\" \"http://example.org/nonsense\"><a...> at / "
                + "to <!DOCTYPE Book SYSTEM \"http://example.org/nonsense\"><a...> at /", description);

        assertEquals("<!DOCTYPE Book PUBLIC \"XMLUNIT/TEST/PUB\" \"http://example.org/nonsense\">\n<a/>", controlDetails);
        assertEquals("<!DOCTYPE Book SYSTEM \"http://example.org/nonsense\">\n<a/>", testDetails);
    }

    @Test
    public void testComparisonType_DOCTYPE_SYSTEM_ID() throws Exception {
        // prepare data
        DocumentBuilderFactory dbf = getDocumentBuilderFactoryWithoutValidation();
        Document controlDoc = Convert.toDocument(Input.fromString(
                "<!DOCTYPE Book PUBLIC \"XMLUNIT/TEST/PUB\" \"http://example.org/nonsense\"><a/>").build(), dbf);
        Document testDoc = Convert.toDocument(Input.fromString(
                "<!DOCTYPE Book PUBLIC \"XMLUNIT/TEST/PUB\" \"http://example.org/404\"><a/>").build(), dbf);

        Diff diff = DiffBuilder.compare(controlDoc).withTest(testDoc)
            .withDifferenceEvaluator(DifferenceEvaluators.downgradeDifferencesToEqual(ComparisonType.CHILD_NODELIST_LENGTH))
            .withNodeFilter(NodeFilters.AcceptAll).build();
        assertPreRequirements(diff, ComparisonType.DOCTYPE_SYSTEM_ID);
        Comparison firstDiff = diff.getDifferences().iterator().next().getComparison();

        // run test
        String description = compFormatter.getDescription(firstDiff);
        String controlDetails =  getDetails(firstDiff.getControlDetails(), firstDiff.getType());
        String testDetails =  getDetails(firstDiff.getTestDetails(), firstDiff.getType());

        // validate result
        Assert.assertEquals(
            "Expected doctype system id 'http://example.org/nonsense' but was 'http://example.org/404' - "
                    + "comparing <!DOCTYPE Book PUBLIC \"XMLUNIT/TEST/PUB\" \"http://example.org/nonsense\"><a...> "
                    + "to <!DOCTYPE Book PUBLIC \"XMLUNIT/TEST/PUB\" \"http://example.org/404\"><a...>", description);

        assertEquals("<!DOCTYPE Book PUBLIC \"XMLUNIT/TEST/PUB\" \"http://example.org/nonsense\">\n<a/>", controlDetails);
        assertEquals("<!DOCTYPE Book PUBLIC \"XMLUNIT/TEST/PUB\" \"http://example.org/404\">\n<a/>", testDetails);
    }

    @Test
    public void testComparisonType_SCHEMA_LOCATION() {
        // prepare data
        Diff diff = DiffBuilder
                .compare("<a xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                        + "xsi:schemaLocation=\"http://www.publishing.org Book.xsd\"/>")
                        .withTest("<a />").build();
        assertPreRequirements(diff, ComparisonType.SCHEMA_LOCATION);
        Comparison firstDiff = diff.getDifferences().iterator().next().getComparison();

        // run test
        String description = compFormatter.getDescription(firstDiff);
        String controlDetails =  getDetails(firstDiff.getControlDetails(), firstDiff.getType());
        String testDetails =  getDetails(firstDiff.getTestDetails(), firstDiff.getType());

        // validate result
        Assert.assertEquals("Expected schema location 'http://www.publishing.org Book.xsd' but was 'null' - "
                + "comparing <a...> at /a[1] to <a...> at /a[1]", description);

        assertEquals("<a xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                        + "xsi:schemaLocation=\"http://www.publishing.org Book.xsd\"/>", controlDetails);
        assertEquals("<a/>", testDetails);
    }

    @Test
    public void testComparisonType_NO_NAMESPACE_SCHEMA_LOCATION() {
        // prepare data
        Diff diff = DiffBuilder.compare(
                        "<a xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                            + "xsi:noNamespaceSchemaLocation=\"Book.xsd\"/>")
                        .withTest("<a xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                            + "xsi:noNamespaceSchemaLocation=\"Telephone.xsd\"/>")
                        .build();
        assertPreRequirements(diff, ComparisonType.NO_NAMESPACE_SCHEMA_LOCATION);
        Comparison firstDiff = diff.getDifferences().iterator().next().getComparison();

        // run test
        String description = compFormatter.getDescription(firstDiff);
        String controlDetails =  getDetails(firstDiff.getControlDetails(), firstDiff.getType());
        String testDetails =  getDetails(firstDiff.getTestDetails(), firstDiff.getType());

        // validate result
        Assert.assertEquals("Expected no namespace schema location 'Book.xsd' but was 'Telephone.xsd' - "
                + "comparing <a...> at /a[1] to <a...> at /a[1]", description);

        assertEquals("<a xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                            + "xsi:noNamespaceSchemaLocation=\"Book.xsd\"/>", controlDetails);
        assertEquals("<a xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                            + "xsi:noNamespaceSchemaLocation=\"Telephone.xsd\"/>", testDetails);
    }

    @Test
    public void testComparisonType_NODE_TYPE_similar() {
        // prepare data
        Diff diff = DiffBuilder.compare("<a>Text</a>").withTest("<a><![CDATA[Text]]></a>").build();
        assertPreRequirements(diff, ComparisonType.NODE_TYPE);
        Comparison firstDiff = diff.getDifferences().iterator().next().getComparison();

        // run test
        String description = compFormatter.getDescription(firstDiff);
        String controlDetails =  getDetails(firstDiff.getControlDetails(), firstDiff.getType());
        String testDetails =  getDetails(firstDiff.getTestDetails(), firstDiff.getType());

        // validate result
        Assert.assertEquals("Expected node type 'Text' but was 'CDATA Section' - "
                + "comparing <a ...>Text</a> at /a[1]/text()[1] "
                + "to <a ...><![CDATA[Text]]></a> at /a[1]/text()[1]",
                description);

        assertEquals("<a>Text</a>", controlDetails);
        if (JAVA_9_PLUS && !JAVA_14_PLUS) {
            assertEquals("<a>\n  <![CDATA[Text]]>\n</a>", testDetails);
        } else {
            assertEquals("<a><![CDATA[Text]]></a>", testDetails);
        }
    }

    @Test
    public void testComparisonType_NAMESPACE_PREFIX() {
        // prepare data
        Diff diff = DiffBuilder.compare(
            "<ns1:a xmlns:ns1=\"test\">Text</ns1:a>").withTest("<test:a xmlns:test=\"test\">Text</test:a>").build();
        assertPreRequirements(diff, ComparisonType.NAMESPACE_PREFIX);
        Comparison firstDiff = diff.getDifferences().iterator().next().getComparison();

        // run test
        String description = compFormatter.getDescription(firstDiff);
        String controlDetails =  getDetails(firstDiff.getControlDetails(), firstDiff.getType());
        String testDetails =  getDetails(firstDiff.getTestDetails(), firstDiff.getType());

        // validate result
        Assert.assertEquals("Expected namespace prefix 'ns1' but was 'test' - "
                + "comparing <ns1:a...> at /a[1] to <test:a...> at /a[1]", description);

        assertEquals("<ns1:a xmlns:ns1=\"test\">Text</ns1:a>", controlDetails);
        assertEquals("<test:a xmlns:test=\"test\">Text</test:a>", testDetails);
    }

    @Test
    public void testComparisonType_NAMESPACE_URI() {
        // prepare data
        Diff diff = DiffBuilder.compare(
            "<test:a xmlns:test=\"test.org\">Text</test:a>")
            .withTest("<test:a xmlns:test=\"test.net\">Text</test:a>").build();
        assertPreRequirements(diff, ComparisonType.NAMESPACE_URI);
        Comparison firstDiff = diff.getDifferences().iterator().next().getComparison();

        // run test
        String description = compFormatter.getDescription(firstDiff);
        String controlDetails =  getDetails(firstDiff.getControlDetails(), firstDiff.getType());
        String testDetails =  getDetails(firstDiff.getTestDetails(), firstDiff.getType());

        // validate result
        Assert.assertEquals("Expected namespace uri 'test.org' but was 'test.net' - "
                + "comparing <test:a...> at /a[1] to <test:a...> at /a[1]", description);

        assertEquals("<test:a xmlns:test=\"test.org\">Text</test:a>", controlDetails);
        assertEquals("<test:a xmlns:test=\"test.net\">Text</test:a>", testDetails);
    }

    @Test
    public void testComparisonType_TEXT_VALUE() {
        // prepare data
        Diff diff = DiffBuilder.compare("<a>Text one</a>").withTest("<a>Text two</a>").build();
        assertPreRequirements(diff, ComparisonType.TEXT_VALUE);
        Comparison firstDiff = diff.getDifferences().iterator().next().getComparison();

        // run test
        String description = compFormatter.getDescription(firstDiff);
        String controlDetails =  getDetails(firstDiff.getControlDetails(), firstDiff.getType());
        String testDetails =  getDetails(firstDiff.getTestDetails(), firstDiff.getType());

        // validate result
        Assert.assertEquals("Expected text value 'Text one' but was 'Text two' - "
            + "comparing <a ...>Text one</a> at /a[1]/text()[1] "
            + "to <a ...>Text two</a> at /a[1]/text()[1]", description);

        assertEquals("<a>Text one</a>", controlDetails);
        assertEquals("<a>Text two</a>", testDetails);
    }

    @Test
    public void testComparisonType_PROCESSING_INSTRUCTION_TARGET() {
        // prepare data
        Diff diff = DiffBuilder.compare(
            "<?xml-stylesheet type=\"text/xsl\" href=\"animal.xsl\" ?><a>Text one</a>")
            .withTest("<?xml-xy type=\"text/xsl\" href=\"animal.xsl\" ?><a>Text one</a>").build();
        assertPreRequirements(diff, ComparisonType.PROCESSING_INSTRUCTION_TARGET);
        Comparison firstDiff = diff.getDifferences().iterator().next().getComparison();

        // run test
        String description = compFormatter.getDescription(firstDiff);
        String controlDetails =  getDetails(firstDiff.getControlDetails(), firstDiff.getType());
        String testDetails =  getDetails(firstDiff.getTestDetails(), firstDiff.getType());

        // validate result
        Assert.assertEquals("Expected processing instruction target 'xml-stylesheet' but was 'xml-xy' - "
            + "comparing <?xml-stylesheet type=\"text/xsl\" href=\"animal.xsl\" ?> at /processing-instruction()[1] "
            + "to <?xml-xy type=\"text/xsl\" href=\"animal.xsl\" ?> at /processing-instruction()[1]", description);

        assertEquals("<?xml-stylesheet type=\"text/xsl\" href=\"animal.xsl\" ?>", controlDetails);
        assertEquals("<?xml-xy type=\"text/xsl\" href=\"animal.xsl\" ?>", testDetails);
    }

    @Test
    public void testComparisonType_PROCESSING_INSTRUCTION_DATA() {
        // prepare data
        Diff diff = DiffBuilder.compare("<?xml-stylesheet type=\"text/xsl\" href=\"animal.xsl\" ?><a>Text one</a>")
                .withTest("<?xml-stylesheet type=\"text/xsl\" href=\"animal.css\" ?><a>Text one</a>")
                .build();
        assertPreRequirements(diff, ComparisonType.PROCESSING_INSTRUCTION_DATA);
        Comparison firstDiff = diff.getDifferences().iterator().next().getComparison();

        // run test
        String description = compFormatter.getDescription(firstDiff);
        String controlDetails =  getDetails(firstDiff.getControlDetails(), firstDiff.getType());
        String testDetails =  getDetails(firstDiff.getTestDetails(), firstDiff.getType());

        // validate result
        Assert.assertEquals("Expected processing instruction data 'type=\"text/xsl\" href=\"animal.xsl\" ' "
            + "but was 'type=\"text/xsl\" href=\"animal.css\" ' - "
            + "comparing <?xml-stylesheet type=\"text/xsl\" href=\"animal.xsl\" ?> at /processing-instruction()[1] "
            + "to <?xml-stylesheet type=\"text/xsl\" href=\"animal.css\" ?> at /processing-instruction()[1]", description);

        assertEquals("<?xml-stylesheet type=\"text/xsl\" href=\"animal.xsl\" ?>", controlDetails);
        assertEquals("<?xml-stylesheet type=\"text/xsl\" href=\"animal.css\" ?>", testDetails);
    }

    @Test
    public void testComparisonType_ELEMENT_TAG_NAME() {
        // prepare data
        Diff diff = DiffBuilder.compare("<a></a>").withTest("<b></b>").build();
        assertPreRequirements(diff, ComparisonType.ELEMENT_TAG_NAME);
        Comparison firstDiff = diff.getDifferences().iterator().next().getComparison();

        // run test
        String description = compFormatter.getDescription(firstDiff);
        String controlDetails =  getDetails(firstDiff.getControlDetails(), firstDiff.getType());
        String testDetails =  getDetails(firstDiff.getTestDetails(), firstDiff.getType());

        // validate result
        Assert.assertEquals("Expected element tag name 'a' but was 'b' - "
            + "comparing <a...> at /a[1] to <b...> at /b[1]", description);

        assertEquals("<a/>", controlDetails);
        assertEquals("<b/>", testDetails);
    }

    @Test
    public void testComparisonType_ATTR_VALUE_EXPLICITLY_SPECIFIED() {
        // prepare data
        Diff diff = DiffBuilder.compare(
                "<?xml version=\"1.0\" ?>" +
                "<!DOCTYPE root [<!ELEMENT root ANY><!ATTLIST root c CDATA #FIXED \"xxx\">]>" +
                "<root/>")
                .withTest("<?xml version=\"1.0\" ?>" +
                "<!DOCTYPE root [<!ELEMENT root ANY><!ATTLIST root c CDATA #FIXED \"xxx\">]>" +
                "<root c=\"xxx\"/>").build();
        assertPreRequirements(diff, ComparisonType.ATTR_VALUE_EXPLICITLY_SPECIFIED);
        Comparison firstDiff = diff.getDifferences().iterator().next().getComparison();

        // run test
        String description = compFormatter.getDescription(firstDiff);
        String controlDetails =  getDetails(firstDiff.getControlDetails(), firstDiff.getType());
        String testDetails =  getDetails(firstDiff.getTestDetails(), firstDiff.getType());

        // validate result
        Assert.assertEquals("Expected attribute value explicitly specified 'false' but was 'true' - "
            + "comparing <root c=\"xxx\"...> at /root[1]/@c to <root c=\"xxx\"...> at /root[1]/@c", description);

        assertEquals("<root c=\"xxx\"/>", controlDetails);
        assertEquals("<root c=\"xxx\"/>", testDetails);
    }

    @Test
    public void testComparisonType_ELEMENT_NUM_ATTRIBUTES() {
        // prepare data
        Diff diff = DiffBuilder.compare("<a b=\"xxx\"></a>")
                .withTest("<a b=\"xxx\" c=\"xxx\"></a>").build();
        assertPreRequirements(diff, ComparisonType.ELEMENT_NUM_ATTRIBUTES);
        Comparison firstDiff = diff.getDifferences().iterator().next().getComparison();

        // run test
        String description = compFormatter.getDescription(firstDiff);
        String controlDetails =  getDetails(firstDiff.getControlDetails(), firstDiff.getType());
        String testDetails =  getDetails(firstDiff.getTestDetails(), firstDiff.getType());

        // validate result
        Assert.assertEquals("Expected number of attributes '1' but was '2' - "
            + "comparing <a...> at /a[1] to <a...> at /a[1]", description);

        assertEquals("<a b=\"xxx\"/>", controlDetails);
        assertEquals("<a b=\"xxx\" c=\"xxx\"/>", testDetails);
    }

    @Test
    public void testComparisonType_ATTR_VALUE() {
        // prepare data
        Diff diff = DiffBuilder.compare("<a b=\"xxx\"></a>").withTest("<a b=\"yyy\"></a>").build();
        assertPreRequirements(diff, ComparisonType.ATTR_VALUE);
        Comparison firstDiff = diff.getDifferences().iterator().next().getComparison();

        // run test
        String description = compFormatter.getDescription(firstDiff);
        String controlDetails =  getDetails(firstDiff.getControlDetails(), firstDiff.getType());
        String testDetails =  getDetails(firstDiff.getTestDetails(), firstDiff.getType());

        // validate result
        Assert.assertEquals("Expected attribute value 'xxx' but was 'yyy' - "
            + "comparing <a b=\"xxx\"...> at /a[1]/@b to <a b=\"yyy\"...> at /a[1]/@b", description);

        assertEquals("<a b=\"xxx\"/>", controlDetails);
        assertEquals("<a b=\"yyy\"/>", testDetails);
    }

    @Test
    public void testComparisonType_CHILD_NODELIST_LENGTH() {
        // prepare data
        Diff diff = DiffBuilder.compare("<a><b/></a>").withTest("<a><b/><c/></a>").build();
        assertPreRequirements(diff, ComparisonType.CHILD_NODELIST_LENGTH);
        Comparison firstDiff = diff.getDifferences().iterator().next().getComparison();

        // run test
        String description = compFormatter.getDescription(firstDiff);
        String controlDetails =  getDetails(firstDiff.getControlDetails(), firstDiff.getType());
        String testDetails =  getDetails(firstDiff.getTestDetails(), firstDiff.getType());

        // validate result
        Assert.assertEquals("Expected child nodelist length '1' but was '2' - "
            + "comparing <a...> at /a[1] to <a...> at /a[1]", description);

        assertEquals("<a>\n  <b/>\n</a>", controlDetails);
        assertEquals("<a>\n  <b/>\n  <c/>\n</a>", testDetails);
    }


    @Test
    public void testComparisonType_CHILD_NODELIST_SEQUENCE() {
        // prepare data
        Diff diff = DiffBuilder.compare("<a><b>XXX</b><b>YYY</b></a>").withTest("<a><b>YYY</b><b>XXX</b></a>")
            .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText))
            .build();
        assertPreRequirements(diff, ComparisonType.CHILD_NODELIST_SEQUENCE);
        Comparison firstDiff = diff.getDifferences().iterator().next().getComparison();

        // run test
        String description = compFormatter.getDescription(firstDiff);
        String controlDetails =  getDetails(firstDiff.getControlDetails(), firstDiff.getType());
        String testDetails =  getDetails(firstDiff.getTestDetails(), firstDiff.getType());

        // validate result
        Assert.assertEquals("Expected child nodelist sequence '0' but was '1' - "
            + "comparing <b...> at /a[1]/b[1] to <b...> at /a[1]/b[2]", description);

        assertEquals("<a>\n  <b>XXX</b>\n  <b>YYY</b>\n</a>", controlDetails);
        assertEquals("<a>\n  <b>YYY</b>\n  <b>XXX</b>\n</a>", testDetails);
    }

    @Test
    public void testComparisonType_CHILD_LOOKUP() {
        // prepare data
        Diff diff = DiffBuilder.compare("<a>Text</a>").withTest("<a><Element/></a>").build();
        assertPreRequirements(diff, ComparisonType.CHILD_LOOKUP);
        Comparison firstDiff = diff.getDifferences().iterator().next().getComparison();

        // run test
        String description = compFormatter.getDescription(firstDiff);
        String controlDetails =  getDetails(firstDiff.getControlDetails(), firstDiff.getType());
        String testDetails =  getDetails(firstDiff.getTestDetails(), firstDiff.getType());

        // validate result
        Assert.assertEquals("Expected child '#text' but was 'null' - "
            + "comparing <a ...>Text</a> at /a[1]/text()[1] to <NULL>",
                description);

        assertEquals("<a>Text</a>", controlDetails);
        assertEquals("<NULL>", testDetails);
    }

    @Test
    public void testComparisonType_ATTR_NAME_LOOKUP() {
        // prepare data
        Diff diff = DiffBuilder.compare("<a b=\"xxx\"></a>").withTest("<a c=\"yyy\"></a>").build();
        assertPreRequirements(diff, ComparisonType.ATTR_NAME_LOOKUP);
        Comparison firstDiff = diff.getDifferences().iterator().next().getComparison();

        // run test
        String description = compFormatter.getDescription(firstDiff);
        String controlDetails =  getDetails(firstDiff.getControlDetails(), firstDiff.getType());
        String testDetails =  getDetails(firstDiff.getTestDetails(), firstDiff.getType());

        // validate result
        Assert.assertEquals("Expected attribute name '/a[1]/@b' - "
            + "comparing <a...> at /a[1]/@b to <a...> at /a[1]", description);

        assertEquals("<a b=\"xxx\"/>", controlDetails);
        assertEquals("<a c=\"yyy\"/>", testDetails);
    }

    @Test
    public void testComparisonType_Comment() {
        // prepare data
        Diff diff = DiffBuilder.compare("<a><!--XXX--></a>").withTest("<a><!--YYY--></a>").build();
        assertPreRequirements(diff, ComparisonType.TEXT_VALUE);
        Comparison firstDiff = diff.getDifferences().iterator().next().getComparison();

        // run test
        String description = compFormatter.getDescription(firstDiff);
        String controlDetails =  getDetails(firstDiff.getControlDetails(), firstDiff.getType());
        String testDetails =  getDetails(firstDiff.getTestDetails(), firstDiff.getType());

        // validate result
        Assert.assertEquals("Expected text value 'XXX' but was 'YYY' - "
            + "comparing <!--XXX--> at /a[1]/comment()[1] to <!--YYY--> at /a[1]/comment()[1]", description);

        assertEquals("<a>\n  <!--XXX-->\n</a>", controlDetails);
        assertEquals("<a>\n  <!--YYY-->\n</a>", testDetails);
    }

    @Test
    public void testComparisonType_WhitespacesAndUnformattedDetails() {
        // prepare data
        Diff diff = DiffBuilder.compare("<a><b/></a>").withTest("<a>\n  <b/>\n</a>").build();
        assertPreRequirements(diff, ComparisonType.CHILD_NODELIST_LENGTH);
        Comparison firstDiff = diff.getDifferences().iterator().next().getComparison();

        // run test
        String description = compFormatter.getDescription(firstDiff);
        String controlDetails =  getDetails(firstDiff.getControlDetails(), firstDiff.getType());
        String testDetails =  getDetails(firstDiff.getTestDetails(), firstDiff.getType());
        String controlDetailsUnformatted =  compFormatter
                .getDetails(firstDiff.getControlDetails(), firstDiff.getType(), false);
        String testDetailsUnformatted =  compFormatter
                .getDetails(firstDiff.getTestDetails(), firstDiff.getType(), false);

        // validate result
        Assert.assertEquals("Expected child nodelist length '1' but was '3' - "
            + "comparing <a...> at /a[1] to <a...> at /a[1]", description);

        assertEquals("<a>\n  <b/>\n</a>", controlDetails);
        if (JAVA_9_PLUS) {
            assertEquals("<a>\n    \n  <b/>\n  \n</a>", testDetails);
        } else {
            assertEquals("<a>\n  <b/>\n</a>", testDetails);
        }

        assertEquals("<a><b/></a>", controlDetailsUnformatted);
        assertEquals("<a>\n  <b/>\n</a>", testDetailsUnformatted);
    }

    private DocumentBuilderFactory getDocumentBuilderFactoryWithoutValidation() throws ParserConfigurationException {
        // code snippet from http://stackoverflow.com/a/155874/702345
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setNamespaceAware(true);
        dbf.setFeature("http://xml.org/sax/features/namespaces", false);
        dbf.setFeature("http://xml.org/sax/features/validation", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        return dbf;
    }

    /**
     * Assert Equals for two Strings where carriage returns were removed.
     */
    public static void assertEquals(String expected, String actual) {
        Assert.assertEquals(expected, actual.replace("\r", ""));
    }

    private void assertPreRequirements(Diff diff, ComparisonType comparisonType) {
        assertThat(diff.getDifferences().iterator().next(), notNullValue());
        assertThat(diff.getDifferences().iterator().next().getComparison().getType(), is(comparisonType));
    }

    private String getDetails(Comparison.Detail difference, ComparisonType type) {
        return compFormatter.getDetails(difference, type, true);
    }
}
