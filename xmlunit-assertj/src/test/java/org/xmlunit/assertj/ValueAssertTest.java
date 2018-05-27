package org.xmlunit.assertj;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xmlunit.XMLUnitException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static org.xmlunit.assertj.ExpectedException.none;
import static org.xmlunit.assertj.XmlAssert.assertThat;

public class ValueAssertTest {
    @Rule
    public ExpectedException thrown = none();

    @Test
    public void testIsEqualTo_withCountExpression_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<fruits>" +
                "<fruit name=\"apple\"/>" +
                "<fruit name=\"orange\"/>" +
                "<fruit name=\"banana\"/>" +
                "</fruits>";
        assertThat(xml).valueByXPath("count(//fruits/fruit)").isEqualTo(3);
        assertThat(xml).valueByXPath("count(//fruits/fruit[@name=\"orange\"])").isEqualTo(1);
        assertThat(xml).valueByXPath("count(//fruits/fruit[@name=\"apricot\"])").isEqualTo(0);
    }

    @Test
    public void testAsInt_withCountExpression_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<fruits>" +
                "<fruit name=\"apple\"/>" +
                "<fruit name=\"orange\"/>" +
                "<fruit name=\"banana\"/>" +
                "</fruits>";
        assertThat(xml).valueByXPath("count(//fruits/fruit)").asInt().isEqualTo(3);
        assertThat(xml).valueByXPath("count(//fruits/fruit[@name=\"orange\"])").asInt().isEqualTo(1);
        assertThat(xml).valueByXPath("count(//fruits/fruit[@name=\"apricot\"])").asInt().isEqualTo(0);
    }

    @Test
    public void testAsInt_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <apple>%nto be convertible to%n <int>");

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<fruits>" +
                "<fruit name=\"apple\"/>" +
                "<fruit name=\"orange\"/>" +
                "<fruit name=\"banana\"/>" +
                "</fruits>";

        assertThat(xml).valueByXPath("//fruits/fruit/@name").asInt();
    }


    @Test
    public void testIsEqualTo_withAttributeValueExpression_shouldPass() {

        String xml = "<a><b attr=\"abc\"></b></a>";

        assertThat(xml).valueByXPath("//a/b/@attr").isEqualTo("abc");
    }

    @Test
    public void testIsEqualTo_withAttributeValueExpression_shouldFailed() {

        thrown.expectAssertionError("expected:<\"[something]\"> but was:<\"[abc]\">");

        String xml = "<a><b attr=\"abc\"></b></a>";

        assertThat(xml).valueByXPath("//a/b/@attr").isEqualTo("something");
    }

    @Test
    public void testIsEqualTo_withAttributeValueExpression_fromElementClass_shouldPass() throws Exception {

        String xml = "<a><b attr=\"abc\"></b></a>";

        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setNamespaceAware(true);
        DocumentBuilder db = f.newDocumentBuilder();
        Element xmlRootElement = db.parse(new InputSource(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)))).getDocumentElement();

        assertThat(xmlRootElement).valueByXPath("//a/b/@attr").isEqualTo("abc");
    }

    @Test
    public void testIsEqualTo_withNamespaceContext_shouldPass() {

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

        HashMap<String, String> prefix2Uri = new HashMap<>();
        prefix2Uri.put("atom", "http://www.w3.org/2005/Atom");

        assertThat(xml).withNamespaceContext(prefix2Uri)
                .valueByXPath("count(//atom:feed/atom:entry)").isEqualTo("2");
        assertThat(xml).withNamespaceContext(prefix2Uri)
                .valueByXPath("//atom:feed/atom:entry/atom:title/text()").isEqualTo("Google");
        assertThat(xml).withNamespaceContext(prefix2Uri)
                .valueByXPath("//atom:feed/atom:entry[2]/atom:title/text()").isEqualTo("Bing");
    }

    @Test
    public void testValueByXpath_withInvalidXml_shouldFailed() {
        thrown.expectAssertionErrorPattern(".*Expecting code not to raise a throwable but caught.*Content is not allowed in prolog.*");

        assertThat("not empty").valueByXPath("count(//atom:feed/atom:entry)").isEmpty();
    }
}
