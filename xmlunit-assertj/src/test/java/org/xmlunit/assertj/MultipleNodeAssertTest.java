package org.xmlunit.assertj;

import org.junit.Rule;
import org.junit.Test;

import static java.lang.String.format;
import static org.xmlunit.assertj.ExpectedException.none;
import static org.xmlunit.assertj.XmlAssert.assertThat;

public class MultipleNodeAssertTest {

    @Rule
    public ExpectedException thrown = none();

    @Test
    public void testContainsAnyNodeHavingXPath_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr=\"value\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .containsAnyNodeHavingXPath("self::node()[@attr='value2']")
                .containsAnyNodeHavingXPath("./title");
    }

    @Test
    public void testContainsAnyNodeHavingXPath_shouldFailed() {

        thrown.expectAssertionError(format("Expecting:%nany node in set have XPath: <self::node()[@attrA]>"));

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr=\"value\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr2=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .hasXPath("/feed/entry")
                .containsAnyNodeHavingXPath("self::node()[@attrA]");
    }

    @Test
    public void testContainsAllNodesHavingXPath_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr=\"value\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .containsAllNodesHavingXPath("self::node()[@attr]")
                .containsAllNodesHavingXPath("./title");
    }

    @Test
    public void testContainsAllNodesHavingXPath_shouldFailed() {

        thrown.expectAssertionError("check node at index 2");
        thrown.expectAssertionError(format("Expecting:%n <entry>%nto have XPath: <./title>"));

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry>" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry>" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry>" +
                "       <description>description</description>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .hasXPath("/feed/entry")
                .containsAllNodesHavingXPath("./title");
    }

    @Test
    public void testExtractingAttribute_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr1=\"value1\" />" +
                "   <entry attr1=\"value2\"/>" +
                "   <entry />" +
                "   <entry attr1=\"value4\" />" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .extractingAttribute("attr1")
                .containsExactly("value1", "value2", null, "value4");
    }
}
