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
package org.xmlunit.input;

import static org.junit.Assert.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class NormalizedSourceTest {

    private Document doc;

    @Before
    public void createDoc() throws Exception {
        doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .newDocument();
    }

    @Test
    public void canWrapNullNode() {
        new NormalizedSource((Node) null);
    }

    @Test
    public void canWrapNullDocument() {
        new NormalizedSource((Document) null);
    }

    @Test
    public void canWrapNullSource() {
        new NormalizedSource((Source) null);
    }

    @Test
    public void normalizesNode() {
        Element control = doc.createElement("e");
        control.appendChild(doc.createTextNode("a"));
        control.appendChild(doc.createTextNode(""));
        control.appendChild(doc.createTextNode("b"));
        NormalizedSource s = new NormalizedSource(control);
        assertEquals(1, s.getNode().getChildNodes().getLength());
        assertEquals("ab", s.getNode().getFirstChild().getNodeValue());
    }

    @Test
    public void normalizesNodeAfterSetNode() {
        Element control = doc.createElement("e");
        control.appendChild(doc.createTextNode("a"));
        control.appendChild(doc.createTextNode(""));
        control.appendChild(doc.createTextNode("b"));
        NormalizedSource s = new NormalizedSource();
        s.setNode(control);
        assertEquals(1, s.getNode().getChildNodes().getLength());
        assertEquals("ab", s.getNode().getFirstChild().getNodeValue());
    }

    @Test
    public void normalizesDocument() {
        Element control = doc.createElement("e");
        doc.appendChild(control);
        control.appendChild(doc.createTextNode("a"));
        control.appendChild(doc.createTextNode(""));
        control.appendChild(doc.createTextNode("b"));
        NormalizedSource s = new NormalizedSource(doc);
        assertEquals(1, s.getNode().getChildNodes().getLength());
        assertEquals(1, s.getNode().getFirstChild().getChildNodes().getLength());
        assertEquals("ab", s.getNode().getFirstChild().getFirstChild().getNodeValue());
    }

    @Test
    public void normalizesDOMSource() {
        Element control = doc.createElement("e");
        doc.appendChild(control);
        control.appendChild(doc.createTextNode("a"));
        control.appendChild(doc.createTextNode(""));
        control.appendChild(doc.createTextNode("b"));
        NormalizedSource s = new NormalizedSource(new DOMSource(doc));
        assertEquals(1, s.getNode().getChildNodes().getLength());
        assertEquals(1, s.getNode().getFirstChild().getChildNodes().getLength());
        assertEquals("ab", s.getNode().getFirstChild().getFirstChild().getNodeValue());
    }

    @Test
    public void keepsSystemId() {
        NormalizedSource s = new NormalizedSource(new DOMSource(doc, "foo"));
        assertEquals("foo", s.getSystemId());
    }
}
