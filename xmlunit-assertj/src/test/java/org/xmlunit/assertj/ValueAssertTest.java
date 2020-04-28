/*
  This file is licensed to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package org.xmlunit.assertj;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xmlunit.assertj.util.SetEnglishLocaleRule;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathFactory;

import static org.xmlunit.assertj.ExpectedException.none;
import static org.xmlunit.assertj.XmlAssert.assertThat;

public class ValueAssertTest {
    @Rule
    public ExpectedException thrown = none();

    @ClassRule
    public static SetEnglishLocaleRule locale = new SetEnglishLocaleRule();

    @Test
    public void testAsInt_shouldPass() {

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
    public void testAsDouble_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<fruits>" +
                "<fruit name=\"apple\" weight=\"66.6\"/>" +
                "</fruits>";

        assertThat(xml).valueByXPath("//fruits/fruit/@weight").asDouble().isEqualTo(66.6);
    }

    @Test
    public void testAsDouble_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <apple>%nto be convertible to%n <double>");

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<fruits>" +
                "<fruit name=\"apple\" weight=\"66.6\"/>" +
                "</fruits>";

        assertThat(xml).valueByXPath("//fruits/fruit/@name").asDouble();
    }

    @Test
    public void testAsBoolean_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<fruits>" +
                "<fruit name=\"apple\" fresh=\"true\"/>" +
                "<fruit name=\"orange\" fresh=\"false\"/>" +
                "<fruit name=\"banana\" fresh=\"True\"/>" +
                "<fruit name=\"pear\" fresh=\"False\"/>" +
                "</fruits>";

        assertThat(xml).valueByXPath("//fruits/fruit[@name=\"apple\"]/@fresh").asBoolean().isTrue();
        assertThat(xml).valueByXPath("//fruits/fruit[@name=\"orange\"]/@fresh").asBoolean().isFalse();
        assertThat(xml).valueByXPath("//fruits/fruit[@name=\"banana\"]/@fresh").asBoolean().isTrue();
        assertThat(xml).valueByXPath("//fruits/fruit[@name=\"pear\"]/@fresh").asBoolean().isFalse();
    }

    @Test
    public void testAsBoolean_withNumberAsArgument_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <2>%nto be convertible to%n <boolean>");

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<fruits>" +
                "<fruit name=\"apple\" fresh=\"2\"/>" +
                "</fruits>";

        assertThat(xml).valueByXPath("//fruits/fruit[@name=\"apple\"]/@fresh").asBoolean();
    }

    @Test
    public void testAsBoolean_withZeroAsArgument_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <0>%nto be convertible to%n <boolean>");

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<fruits>" +
                "<fruit name=\"apple\" fresh=\"0\"/>" +
                "</fruits>";

        assertThat(xml).valueByXPath("//fruits/fruit[@name=\"apple\"]/@fresh").asBoolean();
    }

    @Test
    public void testAsBoolean_withOneAsArgument_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <1>%nto be convertible to%n <boolean>");

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<fruits>" +
                "<fruit name=\"apple\" fresh=\"1\"/>" +
                "</fruits>";

        assertThat(xml).valueByXPath("//fruits/fruit[@name=\"apple\"]/@fresh").asBoolean();
    }

    @Test
    public void testAsBoolean_withRandomStringAsArgument_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <asdfasd>%nto be convertible to%n <boolean>");

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<fruits>" +
                "<fruit name=\"apple\" fresh=\"asdfasd\"/>" +
                "</fruits>";

        assertThat(xml).valueByXPath("//fruits/fruit[@name=\"apple\"]/@fresh").asBoolean();
    }

    @Test
    public void testAsXml_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<a><b>" +
                "<![CDATA[<c><d attr=\"xyz\"></d></c>]]>" +
                "</b></a>";

        assertThat(xml).valueByXPath("//a/b/text()")
                .isEqualTo("<c><d attr=\"xyz\"></d></c>")
                .asXml().hasXPath("/c/d");
    }

    @Test
    public void testAsXml_shouldFailed() {

        thrown.expectAssertionError("The markup in the document following the root element must be well-formed");

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<a><b>" +
                "<![CDATA[<d attr=\"xyz\"></d><c></c>]]>" +
                "</b></a>";

        assertThat(xml).valueByXPath("//a/b/text()")
                .isEqualTo("<d attr=\"xyz\"></d><c></c>")
                .asXml().hasXPath("/c");
    }

    @Test
    public void testAsXml_withWrappingNodeName_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<a><b>" +
                "<![CDATA[<d attr=\"xyz\"></d><c></c>]]>" +
                "</b></a>";

        final XmlAssert xmlAssert = assertThat(xml).valueByXPath("//a/b/text()")
                .isEqualTo("<d attr=\"xyz\"></d><c></c>")
                .asXml("x");

        xmlAssert.hasXPath("/x/d");
        xmlAssert.hasXPath("/x/c");
        xmlAssert.doesNotHaveXPath("/d");
        xmlAssert.doesNotHaveXPath("/c");
    }

    @Test
    public void testIsEqualTo_withInt_shouldPass() {

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
    public void testIsEqualTo_withDouble_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<fruits>" +
                "<fruit name=\"apple\" weight=\"23.3\"/>" +
                "<fruit name=\"orange\" weight=\"0.0\"/>" +
                "<fruit name=\"banana\" weight=\"7\"/>" +
                "</fruits>";
        assertThat(xml).valueByXPath("//fruits/fruit[@name=\"apple\"]/@weight").isEqualTo(23.3);
        assertThat(xml).valueByXPath("//fruits/fruit[@name=\"orange\"]/@weight").isEqualTo(0.0);
        assertThat(xml).valueByXPath("//fruits/fruit[@name=\"banana\"]/@weight").isEqualTo(7.0);
    }

    @Test
    public void testIsEqualTo_withBoolean_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<fruits>" +
                "<fruit name=\"apple\" fresh=\"true\"/>" +
                "<fruit name=\"orange\" fresh=\"false\"/>" +
                "<fruit name=\"banana\" fresh=\"True\"/>" +
                "<fruit name=\"pear\" fresh=\"False\"/>" +
                "</fruits>";

        assertThat(xml).valueByXPath("//fruits/fruit[@name=\"apple\"]/@fresh").isEqualTo(true);
        assertThat(xml).valueByXPath("//fruits/fruit[@name=\"orange\"]/@fresh").isEqualTo(false);
        assertThat(xml).valueByXPath("//fruits/fruit[@name=\"banana\"]/@fresh").isEqualTo(true);
        assertThat(xml).valueByXPath("//fruits/fruit[@name=\"pear\"]/@fresh").isEqualTo(false);
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
    public void usesXPathEngine() {
        XPathFactory xFac = Mockito.mock(XPathFactory.class);
        Mockito.when(xFac.newXPath()).thenReturn(XPathFactory.newInstance().newXPath());
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<fruits>" +
                "<fruit name=\"apple\" weight=\"66.6\"/>" +
                "</fruits>";
        assertThat(xml)
                .withXPathFactory(xFac)
                .valueByXPath("//fruits/fruit/@weight").asDouble().isEqualTo(66.6);
        Mockito.verify(xFac).newXPath();
    }
}
