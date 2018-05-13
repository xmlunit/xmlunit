package org.xmlunit.assertj;

import org.junit.Rule;
import org.junit.Test;

import static java.lang.String.format;
import static org.xmlunit.assertj.ExpectedException.none;
import static org.xmlunit.assertj.XmlAssert.assertThat;

public class MultipleNodeAssertHaveAttributeTest {

    @Rule
    public ExpectedException thrown = none();

    @Test
    public void testHaveAttribute_withAnyValue_shouldPass() {

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
                .haveAttribute("attr");
    }

    @Test
    public void testHaveAttribute_withValue_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr=\"value\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr=\"value\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .haveAttribute("attr", "value");
    }

    @Test
    public void testHaveAttribute_withMultipleAttributes_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr1=\"value1\" attr2=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <title>title</title>" +
                "   <entry attr2=\"value4\" attr1=\"value3\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .haveAttribute("attr1")
                .haveAttribute("attr2");
    }

    @Test
    public void testHaveAttribute_withMultipleAttributeWithValues_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr1=\"value1\" attr2=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr2=\"value2\" attr1=\"value1\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .haveAttribute("attr1", "value1")
                .haveAttribute("attr2", "value2");
    }

    @Test
    public void testHaveAttribute_withAnyValue_shouldFailed() {

        thrown.expectAssertionError("check node at index 1");
        thrown.expectAssertionError(format("Expecting:%n <entry>%nto have attribute:%n <attr>"));

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
                .haveAttribute("attr");
    }

    @Test
    public void testHaveAttribute_withValue_shouldFailed() {

        thrown.expectAssertionError("check node at index 1");
        thrown.expectAssertionError(format("Expecting:%n <entry>%nto have attribute:%n <attr>%nwith value:%n <value>"));

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
                .hasXPath("/feed/entry")
                .haveAttribute("attr", "value");
    }

    @Test
    public void testHaveAttribute_withMultipleAttributes_shouldFailed() {

        thrown.expectAssertionError("check node at index 2");
        thrown.expectAssertionError(format("Expecting:%n <entry>%nto have attribute:%n <attr2>"));

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr1=\"value1\" attr2=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <title>title</title>" +
                "   <entry attr2=\"value4\" attr1=\"value3\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr1=\"value5\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .haveAttribute("attr1")
                .haveAttribute("attr2");
    }

    @Test
    public void testHaveAttribute_withMultipleAttributeWithValues_shouldFailed() {

        thrown.expectAssertionError("check node at index 2");
        thrown.expectAssertionError(format("Expecting:%n <entry>%nto have attribute:%n <attr1>%nwith value:%n <value1>"));

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr1=\"value1\" attr2=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr1=\"value1\" attr2=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr1=\"VALUE1\" attr2=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr1=\"value1\" attr2=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .haveAttribute("attr1", "value1")
                .haveAttribute("attr2", "value2");
    }

    @Test
    public void testHaveAttribute_forEmptyNodeSet_shouldPass() {


        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr1=\"value1\" attr2=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr1=\"value1\" attr2=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/abc")
                .haveAttribute("attr1")
                .haveAttribute("attr2", "value2");
    }
}
