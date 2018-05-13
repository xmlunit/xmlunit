package org.xmlunit.assertj;

import org.junit.Rule;
import org.junit.Test;

import static org.xmlunit.assertj.ExpectedException.none;
import static org.xmlunit.assertj.XmlAssert.assertThat;

public class MultipleNodeAssertHaveNotAttributeTest {

    @Rule
    public ExpectedException thrown = none();

    @Test
    public void testHaveNotAttribute_withAnyValue_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr1=\"value\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr2=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .haveNotAttribute("attr");
    }

    @Test
    public void testHaveNotAttribute_withValue_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr1=\"value\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr=\"abc\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .haveNotAttribute("attr", "value");
    }

    @Test
    public void testHaveNotAttribute_withMultipleAttributes_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry abc=\"value1\" aaa=\"def\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <title>title</title>" +
                "   <entry xyz=\"value4\" bbb=\"aaa\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .haveNotAttribute("attr1")
                .haveNotAttribute("attr2");
    }

    @Test
    public void testHaveNotAttribute_withMultipleAttributeWithValues_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr1=\"abc\" xyz=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry def=\"value2\" attr1=\"ghi\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .haveNotAttribute("attr1", "value1")
                .haveNotAttribute("attr2", "value2");
    }

    @Test
    public void testHaveNotAttribute_withAnyValue_shouldFailed() {

        thrown.expectAssertionError("check node at index 1");
        thrown.expectAssertionError("Expecting:\n <entry>\nnot to have attribute:\n <attr>");

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry abc=\"value\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .hasXPath("/feed/entry")
                .haveNotAttribute("attr");
    }

    @Test
    public void testHaveNotAttribute_withValue_shouldFailed() {

        thrown.expectAssertionError("check node at index 1");
        thrown.expectAssertionError("Expecting:\n <entry>\nnot to have attribute:\n <attr>\nwith value:\n <value>");

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr=\"value\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .hasXPath("/feed/entry")
                .haveNotAttribute("attr", "value");
    }

    @Test
    public void testHaveNotAttribute_withMultipleAttributes_shouldFailed() {

        thrown.expectAssertionError("check node at index 2");
        thrown.expectAssertionError("Expecting:\n <entry>\nnot to have attribute:\n <attr1>");

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry abc=\"value1\" def=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <title>title</title>" +
                "   <entry>" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr1=\"value5\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .haveNotAttribute("attr1")
                .haveNotAttribute("attr2");
    }

    @Test
    public void testHaveNotAttribute_withMultipleAttributeWithValues_shouldFailed() {

        thrown.expectAssertionError("check node at index 2");
        thrown.expectAssertionError("Expecting:\n <entry>\nnot to have attribute:\n <attr2>\nwith value:\n <value2>");

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr1=\"value2\" attr2=\"value1\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry abc=\"value1\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr1=\"VALUE1\" attr2=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr1=\"value2\" attr2=\"value1\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .haveNotAttribute("attr1", "value1")
                .haveNotAttribute("attr2", "value2");
    }
}
