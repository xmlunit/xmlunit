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

public class JAXPXPathEngineTest {

    private Source source;

    @Before public void readSource() throws Exception {
        source = Input.fromFile(TestResources.BLAME_FILE).build();
    }

    @Test public void selectNodesWithNoMatches() {
        Iterable<Node> i = new JAXPXPathEngine().selectNodes("foo", source);
        assertNotNull(i);
        assertFalse(i.iterator().hasNext());
    }

    @Test public void selectNodesWithSingleMatch() {
        Iterable<Node> i = new JAXPXPathEngine().selectNodes("//ul", source);
        assertNotNull(i);
        Iterator<Node> it = i.iterator();
        assertTrue(it.hasNext());
        assertEquals("ul", it.next().getNodeName());
        assertFalse(it.hasNext());
    }

    @Test public void selectNodesWithMultipleMatchs() {
        Iterable<Node> i = new JAXPXPathEngine().selectNodes("//li", source);
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
        new JAXPXPathEngine().selectNodes("//li[", source);
    }

    @Test public void evaluateWithNoMatches() {
        assertEquals("", new JAXPXPathEngine().evaluate("foo", source));
    }

    @Test public void evaluateWithSingleMatch() {
        assertEquals("Don't blame it on the...",
                     new JAXPXPathEngine().evaluate("//title", source));
    }

    @Test public void evaluateWithMultipleMatchs() {
        assertEquals("sunshine",
                     new JAXPXPathEngine().evaluate("//li", source));
    }

    @Test(expected=XMLUnitException.class)
    public void evaluateWithInvalidXPath() {
        new JAXPXPathEngine().evaluate("//li[", source);
    }

    @Test public void selectNodesWithNS() {
        JAXPXPathEngine e = new JAXPXPathEngine();
        source = Input.fromMemory("<n:d xmlns:n='urn:test:1'><n:e/></n:d>")
            .build();
        HashMap<String, String> m = new HashMap<String, String>();
        m.put("x", "urn:test:1");
        e.setNamespaceContext(m);
        Iterable<Node> it = e.selectNodes("/x:d/x:e", source);
        assertTrue(it.iterator().hasNext());
    }

    @Test public void selectNodesWithDefaultNS() {
        JAXPXPathEngine e = new JAXPXPathEngine();
        source = Input.fromMemory("<d xmlns='urn:test:1'><e/></d>")
            .build();
        HashMap<String, String> m = new HashMap<String, String>();
        m.put("x", "urn:test:1");
        e.setNamespaceContext(m);
        Iterable<Node> it = e.selectNodes("/x:d/x:e", source);
        assertTrue(it.iterator().hasNext());
    }
}
