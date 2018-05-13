package org.xmlunit.assertj;

import org.junit.Rule;
import org.junit.Test;

import static org.xmlunit.assertj.ExpectedException.none;
import static org.xmlunit.assertj.XmlAssert.assertThat;

public class SingleNodeAssertHasNotAttributeTest {

    @Rule
    public ExpectedException thrown = none();

    @Test
    public void testHasNotAttribute_forNodeWithoutAttribute_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr=\"value\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/title")
                .first()
                .hasNotAttribute("attr");
    }

    @Test
    public void testHasNotAttribute_withValue_forNodeWithoutAttribute_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr=\"value\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/title")
                .first()
                .hasNotAttribute("attr", "value");
    }

    @Test
    public void testHasNotAttribute_forNodeWithAttribute_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry abc=\"value\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/title")
                .first()
                .hasNotAttribute("attr");
    }

    @Test
    public void testHasNotAttribute_withValue_forNodeWithAttribute_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr=\"abc\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/title")
                .first()
                .hasNotAttribute("attr", "value");
    }

    @Test
    public void testHasNotAttribute_forNodeWithMultipleAttributes_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry abc=\"value1\" xyz=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .first()
                .hasNotAttribute("attr1")
                .hasNotAttribute("attr2");
    }

    @Test
    public void testHasNotAttribute_withValue_forNodeWithMultipleAttributes_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr1=\"abc\" attr2=\"xyz\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .first()
                .hasNotAttribute("attr1", "value1")
                .hasNotAttribute("attr2", "value2");
    }

    @Test
    public void testHasNotAttribute_withMultipleMatchingNodes_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <entry attr1=\"xyz\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr2=\"abc\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr5=\"value3\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr4=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .first()
                .hasNotAttribute("attr1", "value1");

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .last()
                .hasNotAttribute("attr4", "value4");

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .element(2)
                .hasNotAttribute("attr3", "value3");
    }

    @Test
    public void testHasNotAttribute_withAnyValue_shouldFailed() {

        thrown.expectAssertionError("Expecting:\n <entry>\nnot to have attribute:\n <attr>");

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr=\"value\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .first()
                .hasNotAttribute("attr");
    }

    @Test
    public void testHasNotAttribute_withValue_shouldFailed() {

        thrown.expectAssertionError("Expecting:\n <entry>\nnot to have attribute:\n <attr>\nwith value:\n <value>");

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr=\"value\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .first()
                .hasNotAttribute("attr", "value");
    }

    @Test
    public void testHasNotAttribute_withMultipleAttributes_shouldFailed() {

        thrown.expectAssertionError("Expecting:\n <entry>\nnot to have attribute:\n <attr2>\nwith value:\n <value2>");

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr1=\"abc\" attr2=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .first()
                .hasNotAttribute("attr1", "value1")
                .hasNotAttribute("attr2", "value2");
    }

    @Test
    public void testHasNotAttribute_forFirstNode_shouldFailed() {

        thrown.expectAssertionError("Expecting:\n <entry>\nnot to have attribute:\n <attr1>\nwith value:\n <value1>");

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
                .nodesByXPath("/feed/entry")
                .first()
                .hasNotAttribute("attr1", "value1");
    }

    @Test
    public void testHasNotAttribute_forLastNode_shouldFailed() {

        thrown.expectAssertionError("Expecting:\n <entry>\nnot to have attribute:\n <attr2>\nwith value:\n <value2>");

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
                .nodesByXPath("/feed/entry")
                .last()
                .hasNotAttribute("attr2", "value2");
    }
}
