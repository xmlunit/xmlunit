package org.xmlunit.matchers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.HasXPathMatcher.hasXPath;

public class HasXPathMatcherTest {

    private DocumentBuilder db;

    @Before
    public void setup() throws ParserConfigurationException {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setNamespaceAware(true);
        db = f.newDocumentBuilder();
    }

    @Test
    public void testXPathIsFoundInDocumentWithASingleOccurence() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry>" +
                "       <title>title1</title>" +
                "       <id>id1</id>" +
                "   </entry>" +
                "</feed>";

        Element xmlRootElement = db.parse(
                new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8")))).getDocumentElement();

        assertThat(xmlRootElement, hasXPath("entry/id"));
        assertThat(xmlRootElement, hasXPath("entry/title"));
        assertThat(xmlRootElement, not(hasXPath("entry/description")));
    }

    @Test
    public void testXPathIsFoundInStringWithASingleOccurence() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry>" +
                "       <title>title1</title>" +
                "       <id>id1</id>" +
                "   </entry>" +
                "</feed>";


        assertThat(xml, hasXPath("//feed/entry/id"));
        assertThat(xml, hasXPath("//feed/entry/title"));
        assertThat(xml, not(hasXPath("//feed/entry/description")));
    }

    @Test
    public void testXPathIsFoundInStringWithMultipleOccurences() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry>" +
                "       <title>title1</title>" +
                "       <id>id1</id>" +
                "   </entry>" +
                "   <entry>" +
                "       <title>title2</title>" +
                "       <id>id2</id>" +
                "   </entry>" +
                "</feed>";


        assertThat(xml, hasXPath("//feed/entry/id"));
        assertThat(xml, hasXPath("//feed/entry/title"));
        assertThat(xml, not(hasXPath("//feed/entry/description")));
    }

    @Test
    public void textXPathAttributeIsFound() throws Exception {
        String xml = "<a><b attr=\"abc\"></b></a>";
        assertThat(xml, hasXPath("//a/b/@attr"));

        try {
            assertThat(xml, hasXPath("//a/b[@attr=\"abcd\"]"));
            Assert.fail("Should throw AssertionError");
        }catch(AssertionError e){
            assertThat(e.getMessage(), containsString("XPath returned no results"));
        }
    }

    @Test
    public void testXPathIsFoundInDocumentWithNamespaceContextWithASingleOccurence() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed xmlns=\"http://www.w3.org/2005/Atom\">" +
                "   <title>title</title>" +
                "   <entry>" +
                "       <title>title1</title>" +
                "       <id>id1</id>" +
                "   </entry>" +
                "</feed>";

        HashMap<String, String> prefix2Uri = new HashMap<String, String>();
        prefix2Uri.put("atom", "http://www.w3.org/2005/Atom");
        Element xmlRootElement = db.parse(
                new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8")))).getDocumentElement();

        assertThat(xmlRootElement, hasXPath("//atom:feed/atom:entry/atom:id").withNamespaceContext(prefix2Uri));
        assertThat(xmlRootElement, hasXPath("//atom:feed/atom:entry/atom:title").withNamespaceContext(prefix2Uri));
        assertThat(xmlRootElement,
                not(hasXPath("//atom:feed/atom:entry/atom:description").withNamespaceContext(prefix2Uri)));

    }

    /**
     * Really only tests there is no NPE.
     * @see "https://github.com/xmlunit/xmlunit/issues/81"
     */
    @Test(expected = AssertionError.class)
    public void canBeCombinedWithFailingMatcher() {
        assertThat("not empty", both(isEmptyString())
                   .and(hasXPath("count(//atom:feed/atom:entry")));
    }

    @Test
    public void canBeCombinedWithPassingMatcher() {
        String xml = "<a><b attr=\"abc\"></b></a>";
        assertThat(xml, both(not(isEmptyString()))
                   .and(hasXPath("//a/b/@attr")));
    }
}
