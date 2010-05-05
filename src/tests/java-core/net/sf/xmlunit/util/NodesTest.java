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
package net.sf.xmlunit.util;

import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import static org.junit.Assert.*;

public class NodesTest {

    private static final String FOO = "foo";
    private static final String BAR = "bar";
    private static final String SOME_URI = "urn:some:uri";

    private Document doc;

    @Before public void createDoc() throws Exception {
        doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .newDocument();
    }

    @Test public void qNameOfElementWithNoNs() {
        Element e = doc.createElement(FOO);
        QName q = Nodes.getQName(e);
        assertEquals(FOO, q.getLocalPart());
        assertEquals(XMLConstants.NULL_NS_URI, q.getNamespaceURI());
        assertEquals(XMLConstants.DEFAULT_NS_PREFIX, q.getPrefix());
        assertEquals(new QName(FOO), q);
    }

    @Test public void qNameOfElementWithNsNoPrefix() {
        Element e = doc.createElementNS(SOME_URI, FOO);
        QName q = Nodes.getQName(e);
        assertEquals(FOO, q.getLocalPart());
        assertEquals(SOME_URI, q.getNamespaceURI());
        assertEquals(XMLConstants.DEFAULT_NS_PREFIX, q.getPrefix());
        assertEquals(new QName(SOME_URI, FOO), q);
    }

    @Test public void qNameOfElementWithNsAndPrefix() {
        Element e = doc.createElementNS(SOME_URI, FOO);
        e.setPrefix(BAR);
        QName q = Nodes.getQName(e);
        assertEquals(FOO, q.getLocalPart());
        assertEquals(SOME_URI, q.getNamespaceURI());
        assertEquals(BAR, q.getPrefix());
        assertEquals(new QName(SOME_URI, FOO), q);
        assertEquals(new QName(SOME_URI, FOO, BAR), q);
    }

    @Test public void mergeNoTexts() {
        Element e = doc.createElement(FOO);
        assertEquals("", Nodes.getMergedNestedText(e));
    }

    @Test public void mergeSingleTextNode() {
        Element e = doc.createElement(FOO);
        Text t = doc.createTextNode(BAR);
        e.appendChild(t);
        assertEquals(BAR, Nodes.getMergedNestedText(e));
    }

    @Test public void mergeSingleCDATASection() {
        Element e = doc.createElement(FOO);
        CDATASection t = doc.createCDATASection(BAR);
        e.appendChild(t);
        assertEquals(BAR, Nodes.getMergedNestedText(e));
    }

    @Test public void mergeIgnoresTextOfChildren() {
        Element e = doc.createElement(FOO);
        Element c = doc.createElement("child");
        Text t = doc.createTextNode(BAR);
        e.appendChild(c);
        c.appendChild(t);
        assertEquals("", Nodes.getMergedNestedText(e));
    }

    @Test public void mergeIgnoresComments() {
        Element e = doc.createElement(FOO);
        Comment c = doc.createComment(BAR);
        e.appendChild(c);
        assertEquals("", Nodes.getMergedNestedText(e));
    }

    @Test public void mergeMultipleChildren() {
        Element e = doc.createElement(FOO);
        CDATASection c = doc.createCDATASection(BAR);
        e.appendChild(c);
        e.appendChild(doc.createElement("child"));
        Text t = doc.createTextNode(BAR);
        e.appendChild(t);
        assertEquals(BAR + BAR, Nodes.getMergedNestedText(e));
    }

    @Test public void attributeMapNoAttributes() {
        Element e = doc.createElement(FOO);
        Map<QName, String> m = Nodes.getAttributes(e);
        assertEquals(0, m.size());
    }

    @Test public void attributeMapNoNS() {
        Element e = doc.createElement(FOO);
        e.setAttribute(FOO, BAR);
        Map<QName, String> m = Nodes.getAttributes(e);
        assertEquals(1, m.size());
        assertEquals(BAR, m.get(new QName(FOO)));
    }

    @Test public void attributeMapwithNS() {
        Element e = doc.createElement(FOO);
        e.setAttributeNS(SOME_URI, FOO, BAR);
        Map<QName, String> m = Nodes.getAttributes(e);
        assertEquals(1, m.size());
        assertEquals(BAR, m.get(new QName(SOME_URI, FOO)));
        assertEquals(BAR, m.get(new QName(SOME_URI, FOO, BAR)));
    }
}
