package org.xmlunit.assertj;

import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.xmlunit.assertj.ExpectedException.none;
import static org.xmlunit.assertj.XmlAssert.assertThat;

public class XmlAssertHasNotXPathTest {

    @Rule
    public ExpectedException thrown = none();

    @Test
    public void testHasNotXPath_withNotExistingXPath_shouldPass() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<fruits>" +
                "   <fruit name=\"apple\"/>" +
                "   <fruit name=\"orange\"/>" +
                "   <fruit name=\"banana\"/>" +
                "</fruits>";

        assertThat(xml).hasNotXPath("//entry/id");
    }

    @Test
    public void testHasNotXPath_withExistingXPath_shouldFailed() {
        thrown.expectAssertionError("Expecting empty but was");

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<fruits>" +
                "   <fruit name=\"apple\"/>" +
                "   <fruit name=\"orange\"/>" +
                "   <fruit name=\"banana\"/>" +
                "</fruits>";

        assertThat(xml).hasNotXPath("/fruits/fruit");
    }

    @Test
    public void testHasNotXPath_withAttribute_shouldPass() {
        String xml = "<a><b attr=\"abc\"></b></a>";

        assertThat(xml).hasNotXPath("//a/b[@attr=\"xyz\"]");
    }

    @Test
    public void testHasNotXPath_withAttribute_shouldFailed() {
        thrown.expectAssertionError("Expecting empty but was");

        String xml = "<a><b attr=\"abc\"></b></a>";

        assertThat(xml).hasNotXPath("//a/b[@attr=\"abc\"]");
    }

    @Test
    public void testHasNotXPath_withNamespacesContext_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed xmlns=\"http://www.w3.org/2005/Atom\">" +
                "   <title>title</title>" +
                "   <entry>" +
                "       <title>title1</title>" +
                "       <id>id1</id>" +
                "   </entry>" +
                "</feed>";

        Map<String, String> prefix2Uri = new HashMap<String, String>();
        prefix2Uri.put("atom", "http://www.w3.org/2005/Atom");

        assertThat(xml)
                .withNamespaceContext(prefix2Uri)
                .hasNotXPath("//atom:feed/atom:entry/atom:description");
    }


    @Test
    public void testHasNotXPath_withInvalidXML_shouldFailed() {

        thrown.expectAssertionError("Expecting code not to raise a throwable but caught");

        String xml = "<b>not empty</a>";

        assertThat(xml).hasNotXPath("//atom:feed/atom:entry/atom:id");
    }
}
