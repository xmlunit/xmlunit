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
package org.xmlunit.xpath;

import java.io.File;
import java.io.StringBufferInputStream;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import org.xmlunit.TestResources;
import org.xmlunit.XMLUnitException;
import org.xmlunit.builder.Input;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import static org.junit.Assert.*;

public abstract class AbstractXPathEngineTest {

    protected abstract XPathEngine getEngine();

    private Source source;
    private DocumentBuilder db;
    private Element sourceRootElement;

    @Before public void readSource() throws Exception {
        source = Input.fromFile(TestResources.BLAME_FILE).build();
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setNamespaceAware(true);
        db = f.newDocumentBuilder();
        sourceRootElement = db.parse(new File(TestResources.BLAME_FILE)).getDocumentElement();
    }

    @Test public void selectNodesWithNoMatches() {
        Iterable<Node> i = getEngine().selectNodes("foo", source);
        assertNotNull(i);
        assertFalse(i.iterator().hasNext());
    }

    @Test public void selectNodesWithSingleMatch() {
        Iterable<Node> i = getEngine().selectNodes("//ul", source);
        assertNotNull(i);
        Iterator<Node> it = i.iterator();
        assertTrue(it.hasNext());
        assertEquals("ul", it.next().getNodeName());
        assertFalse(it.hasNext());
    }

    @Test public void selectNodesWithMultipleMatchs() {
        Iterable<Node> i = getEngine().selectNodes("//li", source);
        assertNotNull(i);
        int count = 0;
        for (Iterator<Node> it = i.iterator(); it.hasNext(); ) {
            count++;
            assertEquals("li", it.next().getNodeName());
        }
        assertEquals(4, count);
    }

    @Test(expected=XMLUnitException.class)
        public void selectNodesWithInvalidXPath() {
        getEngine().selectNodes("//li[", source);
    }

    @Test public void evaluateWithNoMatches() {
        assertEquals("", getEngine().evaluate("foo", source));
    }

    @Test public void evaluateWithSingleMatch() {
        assertEquals("Don't blame it on the...",
                     getEngine().evaluate("//title", source));
    }

    @Test public void evaluateWithSingleMatchTextSelector() {
        assertEquals("Don't blame it on the...",
                     getEngine().evaluate("//title/text()", source));
    }

    @Test public void evaluateWithMultipleMatches() {
        assertEquals("sunshine",
                     getEngine().evaluate("//li", source));
    }

    @Test(expected=XMLUnitException.class)
    public void evaluateWithInvalidXPath() {
        getEngine().evaluate("//li[", source);
    }

    @Test public void selectNodesWithNS() {
        XPathEngine e = getEngine();
        source = Input.fromString("<n:d xmlns:n='urn:test:1'><n:e/></n:d>")
            .build();
        HashMap<String, String> m = new HashMap<String, String>();
        m.put("x", "urn:test:1");
        e.setNamespaceContext(m);
        Iterable<Node> it = e.selectNodes("/x:d/x:e", source);
        assertTrue(it.iterator().hasNext());
    }

    @Test public void selectNodesWithDefaultNS() {
        XPathEngine e = getEngine();
        source = Input.fromString("<d xmlns='urn:test:1'><e/></d>")
            .build();
        HashMap<String, String> m = new HashMap<String, String>();
        m.put("x", "urn:test:1");
        e.setNamespaceContext(m);
        Iterable<Node> it = e.selectNodes("/x:d/x:e", source);
        assertTrue(it.iterator().hasNext());
    }

    @Test public void selectNodesWithDefaultNSEmptyPrefix() {
        XPathEngine e = getEngine();
        source = Input.fromString("<d xmlns='urn:test:1'><e/></d>")
            .build();
        HashMap<String, String> m = new HashMap<String, String>();
        m.put("", "urn:test:1");
        e.setNamespaceContext(m);
        Iterable<Node> it = e.selectNodes("/:d/:e", source);
        assertTrue(it.iterator().hasNext());
    }

    @Test public void selectNodesWithNoMatches_NodeVersion() {
        Iterable<Node> i = getEngine().selectNodes("foo", sourceRootElement);
        assertNotNull(i);
        assertFalse(i.iterator().hasNext());
    }

    @Test public void selectNodesWithSingleMatch_NodeVersion() {
        Iterable<Node> i = getEngine().selectNodes("//ul", sourceRootElement);
        assertNotNull(i);
        Iterator<Node> it = i.iterator();
        assertTrue(it.hasNext());
        assertEquals("ul", it.next().getNodeName());
        assertFalse(it.hasNext());
    }

    @Test public void selectNodesWithMultipleMatchs_NodeVersion() {
        Iterable<Node> i = getEngine().selectNodes("//li", sourceRootElement);
        assertNotNull(i);
        int count = 0;
        for (Iterator<Node> it = i.iterator(); it.hasNext(); ) {
            count++;
            assertEquals("li", it.next().getNodeName());
        }
        assertEquals(4, count);
    }

    @Test(expected=XMLUnitException.class)
        public void selectNodesWithInvalidXPath_NodeVersion() {
        getEngine().selectNodes("//li[", sourceRootElement);
    }

    @Test public void evaluateWithNoMatches_NodeVersion() {
        assertEquals("", getEngine().evaluate("foo", sourceRootElement));
    }

    @Test public void evaluateWithSingleMatch_NodeVersion() {
        assertEquals("Don't blame it on the...",
                     getEngine().evaluate("//title", sourceRootElement));
    }

    @Test public void evaluateWithSingleMatchTextSelector_NodeVersion() {
        assertEquals("Don't blame it on the...",
                     getEngine().evaluate("//title/text()", sourceRootElement));
    }

    @Test public void evaluateWithMultipleMatches_NodeVersion() {
        assertEquals("sunshine",
                     getEngine().evaluate("//li", sourceRootElement));
    }

    @Test(expected=XMLUnitException.class)
    public void evaluateWithInvalidXPath_NodeVersion() {
        getEngine().evaluate("//li[", sourceRootElement);
    }

    @Test
    public void selectNodesWithNS_NodeVersion() throws Exception {
        XPathEngine e = getEngine();
        sourceRootElement = db
            .parse(new StringBufferInputStream("<n:d xmlns:n='urn:test:1'><n:e/></n:d>"))
            .getDocumentElement();
        HashMap<String, String> m = new HashMap<String, String>();
        m.put("x", "urn:test:1");
        e.setNamespaceContext(m);
        Iterable<Node> it = e.selectNodes("/x:d/x:e", sourceRootElement);
        assertTrue(it.iterator().hasNext());
    }

    @Test
    public void selectNodesWithDefaultNS_NodeVersion() throws Exception {
        XPathEngine e = getEngine();
        sourceRootElement = db
            .parse(new StringBufferInputStream("<d xmlns='urn:test:1'><e/></d>"))
            .getDocumentElement();
        HashMap<String, String> m = new HashMap<String, String>();
        m.put("x", "urn:test:1");
        e.setNamespaceContext(m);
        Iterable<Node> it = e.selectNodes("/x:d/x:e", sourceRootElement);
        assertTrue(it.iterator().hasNext());
    }

    @Test
    public void selectNodesWithDefaultNSEmptyPrefix_NodeVersion() throws Exception {
        XPathEngine e = getEngine();
        sourceRootElement = db
            .parse(new StringBufferInputStream("<d xmlns='urn:test:1'><e/></d>"))
            .getDocumentElement();
        HashMap<String, String> m = new HashMap<String, String>();
        m.put("", "urn:test:1");
        e.setNamespaceContext(m);
        Iterable<Node> it = e.selectNodes("/:d/:e", sourceRootElement);
        assertTrue(it.iterator().hasNext());
    }
}
