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
import net.sf.xmlunit.builder.Input;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
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

    private Map.Entry<Document, Node> stripWsSetup() {
        final Document toTest = Convert.toDocument(Input.fromMemory(
            "<root>\n"
            + "<!-- trim me -->\n"
            + "<child attr=' trim me ' attr2='not me'>\n"
            + " trim me \n"
            + "</child><![CDATA[ trim me ]]>\n"
            + "<?target  trim me ?>\n"
            + "<![CDATA[          ]]>\n"
            + "</root>").build());
        final Node stripped = Nodes.stripWhitespace(toTest);
        return new Map.Entry<Document, Node>() {
            public Document getKey() {
                return toTest;
            }
            public Node getValue() {
                return stripped;
            }
            public Node setValue(Node n) {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Test public void stripWhitespaceWorks() {
        Map.Entry<Document, Node> s = stripWsSetup();
        assertTrue(s.getValue() instanceof Document);
        NodeList top = s.getValue().getChildNodes();
        assertEquals(1, top.getLength());
        assertTrue(top.item(0) instanceof Element);
        assertEquals("root", top.item(0).getNodeName());
        NodeList rootsChildren = top.item(0).getChildNodes();
        assertEquals(4, rootsChildren.getLength());
        assertTrue("should be comment, is " + rootsChildren.item(0).getClass(),
                   rootsChildren.item(0) instanceof Comment);
        assertEquals("trim me", ((Comment) rootsChildren.item(0)).getData());
        assertTrue("should be element, is " + rootsChildren.item(1).getClass(),
                   rootsChildren.item(1) instanceof Element);
        assertEquals("child", rootsChildren.item(1).getNodeName());
        assertTrue("should be cdata, is " + rootsChildren.item(2).getClass(),
                   rootsChildren.item(2) instanceof CDATASection);
        assertEquals("trim me",
                     ((CDATASection) rootsChildren.item(2)).getData());
        assertTrue("should be PI, is " + rootsChildren.item(3).getClass(),
                   rootsChildren.item(3) instanceof ProcessingInstruction);
        assertEquals("trim me",
                     ((ProcessingInstruction) rootsChildren.item(3)).getData());
        Node child = rootsChildren.item(1);
        NodeList grandChildren = child.getChildNodes();
        assertEquals(1, grandChildren.getLength());
        assertTrue("should be text, is " + grandChildren.item(0).getClass(),
                   grandChildren.item(0) instanceof Text);
        assertEquals("trim me", ((Text) grandChildren.item(0)).getData());
        NamedNodeMap attrs = child.getAttributes();
        assertEquals(2, attrs.getLength());
        Attr a = (Attr) attrs.getNamedItem("attr");
        assertEquals("trim me", a.getValue());
        Attr a2 = (Attr) attrs.getNamedItem("attr2");
        assertEquals("not me", a2.getValue());
    }

    @Test public void stripWhitespaceDoesntAlterOriginal() {
        Map.Entry<Document, Node> s = stripWsSetup();
        NodeList top = s.getKey().getChildNodes();
        assertEquals(1, top.getLength());
        assertTrue(top.item(0) instanceof Element);
        assertEquals("root", top.item(0).getNodeName());
        NodeList rootsChildren = top.item(0).getChildNodes();
        assertEquals(10, rootsChildren.getLength());
        assertNewlineTextNode(rootsChildren.item(0));
        assertTrue("should be comment, is " + rootsChildren.item(1).getClass(),
                   rootsChildren.item(1) instanceof Comment);
        assertEquals(" trim me ", ((Comment) rootsChildren.item(1)).getData());
        assertNewlineTextNode(rootsChildren.item(2));
        assertTrue("should be element, is " + rootsChildren.item(3).getClass(),
                   rootsChildren.item(3) instanceof Element);
        assertEquals("child", rootsChildren.item(3).getNodeName());
        assertTrue("should be cdata, is " + rootsChildren.item(4).getClass(),
                   rootsChildren.item(4) instanceof CDATASection);
        assertEquals(" trim me ",
                     ((CDATASection) rootsChildren.item(4)).getData());
        assertNewlineTextNode(rootsChildren.item(5));
        assertTrue("should be PI, is " + rootsChildren.item(6).getClass(),
                   rootsChildren.item(6) instanceof ProcessingInstruction);
        assertEquals("trim me ",
                     ((ProcessingInstruction) rootsChildren.item(6)).getData());
        assertNewlineTextNode(rootsChildren.item(7));
        assertTrue("should be cdata, is " + rootsChildren.item(8).getClass(),
                   rootsChildren.item(8) instanceof CDATASection);
        assertEquals("          ",
                     ((CDATASection) rootsChildren.item(8)).getData());
        assertNewlineTextNode(rootsChildren.item(9));
        Node child = rootsChildren.item(3);
        NodeList grandChildren = child.getChildNodes();
        assertEquals(1, grandChildren.getLength());
        assertTrue("should be text, is " + grandChildren.item(0).getClass(),
                   grandChildren.item(0) instanceof Text);
        assertEquals("\n trim me \n", ((Text) grandChildren.item(0)).getData());
        NamedNodeMap attrs = child.getAttributes();
        assertEquals(2, attrs.getLength());
        Attr a = (Attr) attrs.getNamedItem("attr");
        assertEquals(" trim me ", a.getValue());
        Attr a2 = (Attr) attrs.getNamedItem("attr2");
        assertEquals("not me", a2.getValue());
    }

    private static void assertNewlineTextNode(Node n) {
        assertTrue("should be text, is " + n.getClass(),
                   n instanceof Text);
        assertEquals("\n", ((Text) n).getData());
    }
}
