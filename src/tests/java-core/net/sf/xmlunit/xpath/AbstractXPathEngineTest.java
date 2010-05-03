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
package net.sf.xmlunit.xpath;

import java.util.HashMap;
import java.util.Iterator;
import javax.xml.transform.Source;
import net.sf.xmlunit.TestResources;
import net.sf.xmlunit.builder.Input;
import net.sf.xmlunit.exceptions.XMLUnitException;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;

import static org.junit.Assert.*;

public abstract class AbstractXPathEngineTest {

    protected abstract IXPathEngine getEngine();

    private Source source;

    @Before public void readSource() throws Exception {
        source = Input.fromFile(TestResources.BLAME_FILE).build();
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
        IXPathEngine e = getEngine();
        source = Input.fromMemory("<n:d xmlns:n='urn:test:1'><n:e/></n:d>")
            .build();
        HashMap<String, String> m = new HashMap<String, String>();
        m.put("x", "urn:test:1");
        e.setNamespaceContext(m);
        Iterable<Node> it = e.selectNodes("/x:d/x:e", source);
        assertTrue(it.iterator().hasNext());
    }

    @Test public void selectNodesWithDefaultNS() {
        IXPathEngine e = getEngine();
        source = Input.fromMemory("<d xmlns='urn:test:1'><e/></d>")
            .build();
        HashMap<String, String> m = new HashMap<String, String>();
        m.put("x", "urn:test:1");
        e.setNamespaceContext(m);
        Iterable<Node> it = e.selectNodes("/x:d/x:e", source);
        assertTrue(it.iterator().hasNext());
    }

    @Test public void selectNodesWithDefaultNSEmptyPrefix() {
        IXPathEngine e = getEngine();
        source = Input.fromMemory("<d xmlns='urn:test:1'><e/></d>")
            .build();
        HashMap<String, String> m = new HashMap<String, String>();
        m.put("", "urn:test:1");
        e.setNamespaceContext(m);
        Iterable<Node> it = e.selectNodes("/:d/:e", source);
        assertTrue(it.iterator().hasNext());
    }

    // doesn't match
    public void selectNodesWithDefaultNSNoPrefix() {
        IXPathEngine e = getEngine();
        source = Input.fromMemory("<d xmlns='urn:test:1'><e/></d>")
            .build();
        HashMap<String, String> m = new HashMap<String, String>();
        m.put("", "urn:test:1");
        e.setNamespaceContext(m);
        Iterable<Node> it = e.selectNodes("/d/e", source);
        assertTrue(it.iterator().hasNext());
    }
}
