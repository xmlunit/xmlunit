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

import org.junit.Rule;
import org.junit.Test;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static org.xmlunit.assertj.ExpectedException.none;
import static org.xmlunit.assertj.XmlAssert.assertThat;

public class XmlAssertHasXPathTest {
    @Rule
    public ExpectedException thrown = none();

    @Test
    public void testHasXPath_onElementType_withSingleMatching_shouldPass() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry>" +
                "       <title>title1</title>" +
                "       <id>id1</id>" +
                "   </entry>" +
                "</feed>";

        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Element rootElement = documentBuilder.parse(new InputSource(new StringReader(xml))).getDocumentElement();

        assertThat(rootElement).hasXPath("entry/id");
        assertThat(rootElement).hasXPath("/feed/title");
    }

    @Test
    public void testHasXPath_onStringType_withSingleMatching_shouldPass() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry>" +
                "       <title>title1</title>" +
                "       <id>id1</id>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml).hasXPath("//entry/id");
        assertThat(xml).hasXPath("/feed/entry/id");
    }

    @Test
    public void testHasXPath_withNotExistingXPath_shouldFailed() {
        thrown.expectAssertionError("Expecting actual not to be empty");

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry>" +
                "       <title>title1</title>" +
                "       <id>id1</id>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml).hasXPath("//entry/description");
    }

    @Test
    public void testHasXPath_withMultipleMatching_shouldPass() {
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

        assertThat(xml).hasXPath("//entry/id");
        assertThat(xml).hasXPath("/feed/entry/title");
    }

    @Test
    public void testHasXPath_withAttribute_shouldPass() {
        String xml = "<a><b attr=\"abc\"></b></a>";

        assertThat(xml).hasXPath("//a/b[@attr=\"abc\"]");
    }

    @Test
    public void testHasXPath_withAttribute_shouldFailed() {
        thrown.expectAssertionError("Expecting actual not to be empty");

        String xml = "<a><b attr=\"abc\"></b></a>";

        assertThat(xml).hasXPath("//a/b[@attr=\"abcde\"]");
    }

    @Test
    public void testHasXPath_withNamespacesContext_shouldPass() {

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
                .hasXPath("//atom:feed/atom:entry/atom:id");

        assertThat(xml)
                .withNamespaceContext(prefix2Uri)
                .hasXPath("//atom:feed/atom:entry/atom:title");
    }

    @Test
    public void testHasXPath_withDocumentBuilderFactory_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <entry>" +
                "       <title>title1</title>" +
                "       <id>id1</id>" +
                "   </entry>" +
                "</feed>";

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        assertThat(xml)
                .withDocumentBuilderFactory(dbf)
                .hasXPath("//entry/title");
    }

    @Test
    public void testHasXPath_withInvalidNamespacesContext_shouldFailed() {

        thrown.expectAssertionError("Expecting actual not to be empty");

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed xmlns=\"http://www.w3.org/2005/Atom\">" +
                "   <title>title</title>" +
                "   <entry>" +
                "       <title>title1</title>" +
                "       <id>id1</id>" +
                "   </entry>" +
                "</feed>";

        Map<String, String> prefix2Uri = new HashMap<String, String>();
        prefix2Uri.put("atom", "http://www.w3.org/2005/ATOM");

        assertThat(xml)
                .withNamespaceContext(prefix2Uri)
                .hasXPath("//atom:feed/atom:entry/atom:id");
    }

    @Test
    public void testHasXPath_withInvalidXML_shouldFailed() {

        thrown.expectAssertionError("Expecting code not to raise a throwable but caught");

        String xml = "<b>not empty</a>";

        assertThat(xml).hasXPath("//atom:feed/atom:entry/atom:id");
    }


}
