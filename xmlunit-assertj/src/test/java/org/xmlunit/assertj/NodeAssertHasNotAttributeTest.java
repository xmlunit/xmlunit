package org.xmlunit.assertj;

import org.junit.Rule;
import org.junit.Test;

import static org.xmlunit.assertj.ExpectedException.none;
import static org.xmlunit.assertj.XmlAssert.assertThat;

public class NodeAssertHasNotAttributeTest {

    @Rule
    public ExpectedException thrown = none();

    @Test
    public void testHasAttribute_withAnyValue_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr=\"value\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .hasXPath("/feed/entry")
                .first()
                .hasAttribute("attr");
    }

    @Test
    public void testHasAttribute_withValue_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr=\"value\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .hasXPath("/feed/entry")
                .first()
                .hasAttribute("attr", "value");
    }

    @Test
    public void testHasAttribute_withMultipleAttributes_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr1=\"value1\" attr2=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .hasXPath("/feed/entry")
                .first()
                .hasAttribute("attr1", "value1")
                .hasAttribute("attr2", "value2");
    }

    @Test
    public void testHasAttribute_withMultipleMatchingNodes_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <entry attr1=\"value1\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr2=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .hasXPath("/feed/entry")
                .first()
                .hasAttribute("attr1", "value1");

        assertThat(xml)
                .hasXPath("/feed/entry")
                .last()
                .hasAttribute("attr2", "value2");
    }
    @Test
    public void testHasAttribute_withAnyValue_shouldFailed() {

        thrown.expectAssertionError("Expecting:\n <title>\nto have attribute:\n <attr>");

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr=\"value\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .hasXPath("/feed/title")
                .first()
                .hasAttribute("attr");
    }

    @Test
    public void testHasAttribute_withValue_shouldFailed() {

        thrown.expectAssertionError("Expecting:\n <entry>\nto have attribute:\n <attr>\n with value:\n <value>");

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .hasXPath("/feed/entry")
                .first()
                .hasAttribute("attr", "value");
    }

    @Test
    public void testHasAttribute_withMultipleAttributes_shouldFailed() {

        thrown.expectAssertionError("Expecting:\n <entry>\nto have attribute:\n <attr2>\n with value:\n <value2>");

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr1=\"value1\" attr3=\"value3\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .hasXPath("/feed/entry")
                .first()
                .hasAttribute("attr1", "value1")
                .hasAttribute("attr2", "value2");
    }

    @Test
    public void testHasAttribute_forFirstNode_shouldFailed() {

        thrown.expectAssertionError("Expecting:\n <entry>\nto have attribute:\n <attr1>\n with value:\n <value1>");

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <entry attr2=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr1=\"value1\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .hasXPath("/feed/entry")
                .first()
                .hasAttribute("attr1", "value1");
    }

    @Test
    public void testHasAttribute_forLastNode_shouldFailed() {

        thrown.expectAssertionError("Expecting:\n <entry>\nto have attribute:\n <attr2>\n with value:\n <value2>");
        
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <entry attr2=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr1=\"value1\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .hasXPath("/feed/entry")
                .last()
                .hasAttribute("attr2", "value2");
    }
}
