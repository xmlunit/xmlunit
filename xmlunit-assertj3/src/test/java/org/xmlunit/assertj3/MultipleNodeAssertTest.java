package org.xmlunit.assertj3;

import org.junit.Rule;
import org.junit.Test;

import static java.lang.String.format;
import static org.xmlunit.assertj3.ExpectedException.none;
import static org.xmlunit.assertj3.XmlAssert.assertThat;

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
    public void testContainsAnyNodeHavingXPath_shouldFail() {

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
    public void testContainsAnyNodeHavingXPath_shouldUseCustomFailMessage() {

        thrown.expectAssertionError("Alarm alarm!");

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
                .withFailMessage("Alarm alarm!")
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
    public void testContainsAllNodesHavingXPath_shouldFail() {

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
    public void testContainsAllNodesHavingXPath_shouldUseCustomFailMessage() {

        thrown.expectAssertionError("Alarm alarm!");

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
                .withFailMessage("Alarm alarm!")
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

    @Test
    public void testExtractingAttribute_shouldFail() {

        thrown.expectAssertionError("[XPath \"/feed/entry\" evaluated to node set] ");

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
                .containsExactly("value1", "value2", "value3", "value4");
    }

    @Test
    public void testExtractingAttribute_shouldUseCustomErrorMessage() {

        thrown.expectAssertionError("Alarm alarm!");

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr1=\"value1\" />" +
                "   <entry attr1=\"value2\"/>" +
                "   <entry />" +
                "   <entry attr1=\"value4\" />" +
                "</feed>";

        assertThat(xml)
                .withFailMessage("Alarm alarm!")
                .nodesByXPath("/feed/entry")
                .extractingAttribute("attr1")
                .containsExactly("value1", "value2", "value3", "value4");
    }

    @Test
    public void testExtractingTextSingleNode_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>My Simple Feed</title>" +
                "   <entry attr1=\"value1\" />" +
                "   <entry attr1=\"value2\"/>" +
                "   <entry />" +
                "   <entry attr1=\"value4\" />" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/title")
                .extractingText()
                .containsExactly("My Simple Feed");
    }

    @Test
    public void testExtractingTextSingleNodeWithWhiteSpace_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>" +
                "           My Simple Feed" +
                "   </title>" +
                "   <entry attr1=\"value1\" />" +
                "   <entry attr1=\"value2\"/>" +
                "   <entry />" +
                "   <entry attr1=\"value4\" />" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/title")
                .extractingText()
                .containsExactly("My Simple Feed");
    }

    @Test
    public void testExtractingTextMultipleNodes_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<document>" +
                "   <h2> A header</h2>" +
                " <h2>Just another header</h2>" +
                "</document>";

        assertThat(xml)
                .nodesByXPath("/document/h2")
                .extractingText()
                .containsExactly("A header", "Just another header");
    }

    @Test
    public void testExtractingTextMultipleNodesOneEmpty_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<document>" +
                "   <h2> A header</h2>" +
                " <h2/>" +
                "</document>";

        assertThat(xml)
                .nodesByXPath("/document/h2")
                .extractingText()
                .containsExactly("A header", "");
    }

    @Test
    public void testExtractingTextMultipleNodes_shouldFail() {

        thrown.expectAssertionError("[XPath \"/document/h2\" evaluated to node set] ");

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<document>" +
                "   <h2> Matching header</h2>" +
                " <h2>Not matching</h2>" +
                "</document>";

        assertThat(xml)
                .nodesByXPath("/document/h2")
                .extractingText()
                .containsExactly("Matching header", "Header not matching");
    }

    @Test
    public void testExtractingTextMultipleNodes_shouldUseCustomFailMessage() {

        thrown.expectAssertionError("Alarm alarm!");

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<document>" +
                "   <h2> Matching header</h2>" +
                " <h2>Not matching</h2>" +
                "</document>";

        assertThat(xml)
                .withFailMessage("Alarm alarm!")
                .nodesByXPath("/document/h2")
                .extractingText()
                .containsExactly("Matching header", "Header not matching");
    }

}
