package org.xmlunit.matchers;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.EvaluateXPathMatcher.hasXPath;

public class EvaluateXPathMatcherTest {

    @Test
    public void testXPathCountInXmlString() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<fruits>" +
                    "<fruit name=\"apple\"/>" +
                    "<fruit name=\"orange\"/>" +
                    "<fruit name=\"banana\"/>" +
                "</fruits>";
        assertThat(xml, hasXPath("count(//fruits/fruit)", equalTo("3")));
        assertThat(xml, hasXPath("//fruits/fruit/@name", equalTo("apple")));
        assertThat(xml, hasXPath("count(//fruits/fruit[@name=\"orange\"])", equalTo("1")));
        assertThat(xml, hasXPath("count(//fruits/fruit[@name=\"apricot\"])", equalTo("0")));
    }

    @Test
    public void testXPathAttributeValueMatchingInXmlString() throws Exception {
        String xml = "<a><b attr=\"abc\"></b></a>";
        assertThat(xml, hasXPath("//a/b/@attr", equalTo("abc")));
        assertThat(xml, hasXPath("count(//a/b/c)", equalTo("0")));


        try {
            assertThat(xml, hasXPath("//a/b/@attr", equalTo("something")));
            Assert.fail("Should throw AssertionError");
        } catch(AssertionError e) {
            assertThat(e.getMessage(), containsString("was \"abc\""));
        }

    }

    @Test
    public void testXPathAttributeValueMatchingInXmlElement() throws Exception {
        String xml = "<a><b attr=\"abc\"></b></a>";
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setNamespaceAware(true);
        DocumentBuilder db = f.newDocumentBuilder();
        Element xmlRootElement = db.parse(
                new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8")))).getDocumentElement();
        assertThat(xmlRootElement, hasXPath("//a/b/@attr", equalTo("abc")));
    }

    @Test
    public void testXPathEvaluationWithNamespaceContext() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed xmlns=\"http://www.w3.org/2005/Atom\">" +
                "   <title>Search Engine Feed</title>" +
                "   <link href=\"https://en.wikipedia.org/wiki/Web_search_engine\"/>" +
                "   <entry>" +
                "       <title>Google</title>" +
                "       <id>goog</id>" +
                "   </entry>" +
                "   <entry>" +
                "       <title>Bing</title>" +
                "       <id>msft</id>" +
                "   </entry>" +
                "</feed>";

        HashMap<String, String> prefix2Uri = new HashMap<String, String>();
        prefix2Uri.put("atom", "http://www.w3.org/2005/Atom");

        assertThat(xml, hasXPath("count(//atom:feed/atom:entry)", equalTo("2")).withNamespaceContext(prefix2Uri));
        assertThat(xml, hasXPath("//atom:feed/atom:entry/atom:title/text()",
                equalTo("Google")).withNamespaceContext(prefix2Uri));
        assertThat(xml, hasXPath("//atom:feed/atom:entry[2]/atom:title/text()",
                equalTo("Bing")).withNamespaceContext(prefix2Uri));
    }

    /**
     * Really only tests there is no NPE.
     * @see "https://github.com/xmlunit/xmlunit/issues/81"
     */
    @Test(expected = AssertionError.class)
    public void canBeCombinedWithFailingMatcher() {
        assertThat("not empty", both(isEmptyString())
                   .and(hasXPath("count(//atom:feed/atom:entry)", equalTo("2"))));
    }

    @Test
    public void canBeCombinedWithPassingMatcher() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<fruits>" +
                    "<fruit name=\"apple\"/>" +
                    "<fruit name=\"orange\"/>" +
                    "<fruit name=\"banana\"/>" +
                "</fruits>";
        assertThat(xml, both(not(isEmptyString()))
                   .and(hasXPath("count(//fruits/fruit)", equalTo("3"))));
    }
}
