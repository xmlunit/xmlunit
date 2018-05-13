package org.xmlunit.assertj;

import org.junit.Rule;
import org.junit.Test;

import static java.lang.String.format;
import static org.xmlunit.assertj.ExpectedException.none;
import static org.xmlunit.assertj.XmlAssert.assertThat;

public class SingleNodeAssertHasAttributeTest {

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
                .nodesByXPath("/feed/entry")
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
                .nodesByXPath("/feed/entry")
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
                .nodesByXPath("/feed/entry")
                .first()
                .hasAttribute("attr1")
                .hasAttribute("attr2");
    }

    @Test
    public void testHasAttribute_withMultipleAttributeWithValues_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr1=\"value1\" attr2=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
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
                "   <entry attr3=\"value3\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr4=\"value4\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .first()
                .hasAttribute("attr1", "value1");

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .last()
                .hasAttribute("attr4", "value4");

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .element(2)
                .hasAttribute("attr3", "value3");
    }
    @Test
    public void testHasAttribute_withAnyValue_shouldFailed() {

        thrown.expectAssertionError(format("Expecting:%n <title>%nto have attribute:%n <attr>"));

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
                .hasAttribute("attr");
    }

    @Test
    public void testHasAttribute_withValue_shouldFailed() {

        thrown.expectAssertionError(format("Expecting:%n <entry>%nto have attribute:%n <attr>%nwith value:%n <value>"));

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .first()
                .hasAttribute("attr", "value");
    }

    @Test
    public void testHasAttribute_withMultipleAttributes_shouldFailed() {

        thrown.expectAssertionError(format("Expecting:%n <entry>%nto have attribute:%n <attr2>%nwith value:%n <value2>"));

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr1=\"value1\" attr3=\"value3\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .first()
                .hasAttribute("attr1", "value1")
                .hasAttribute("attr2", "value2");
    }

    @Test
    public void testHasAttribute_forFirstNode_shouldFailed() {

        thrown.expectAssertionError(format("Expecting:%n <entry>%nto have attribute:%n <attr1>%nwith value:%n <value1>"));

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
                .nodesByXPath("/feed/entry")
                .first()
                .hasAttribute("attr1", "value1");
    }

    @Test
    public void testHasAttribute_forLastNode_shouldFailed() {

        thrown.expectAssertionError(format("Expecting:%n <entry>%nto have attribute:%n <attr2>%nwith value:%n <value2>"));

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
                .nodesByXPath("/feed/entry")
                .last()
                .hasAttribute("attr2", "value2");
    }
}
