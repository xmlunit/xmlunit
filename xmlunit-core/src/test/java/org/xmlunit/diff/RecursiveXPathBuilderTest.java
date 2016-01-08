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
package org.xmlunit.diff;

import java.util.Collections;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RecursiveXPathBuilderTest {
    private Document doc;
    private RecursiveXPathBuilder builder = new RecursiveXPathBuilder();

    @Before
    public void initialize() throws Exception {
        doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .newDocument();
    }

    @Test
    public void soleElement() {
        assertEquals("/foo[1]",
                     builder.apply(doc.createElement("foo")).getXPath());
    }

    @Test
    public void rootElement() {
        Element e = doc.createElement("foo");
        doc.appendChild(e);
        assertEquals("/foo[1]",
                     builder.apply(e).getXPath());
    }

    @Test
    public void deeperStructure() {
        Element e = doc.createElement("foo");
        doc.appendChild(e);
        Element e2 = doc.createElement("foo");
        e.appendChild(e2);
        e2.appendChild(doc.createElement("foo"));
        Element e3 = doc.createElement("foo");
        e2.appendChild(e3);
        e2.appendChild(doc.createComment("foo"));
        Comment c = doc.createComment("foo");
        e2.appendChild(c);
        assertEquals("/foo[1]/foo[1]/foo[2]",
                     builder.apply(e3).getXPath());
        assertEquals("/foo[1]/foo[1]/comment()[2]",
                     builder.apply(c).getXPath());
    }

    @Test
    public void attribute() {
        Element e = doc.createElement("foo");
        e.setAttribute("foo", "bar");
        e.setAttribute("baz", "xyzzy");
        assertEquals("/foo[1]/@foo",
                     builder.apply(e.getAttributeNode("foo")).getXPath());
    }

    @Test
    public void namespaceButNoMap() {
        Element e = doc.createElementNS("http://www.xmlunit.org/test", "foo");
        e.setAttributeNS("http://www.xmlunit.org/test", "foo", "bar");
        e.setAttributeNS("http://www.xmlunit.org/test", "baz", "xyzzy");
        assertEquals("/foo[1]/@foo",
                     builder.apply(e.getAttributeNode("foo")).getXPath());
    }

    @Test
    public void namespaceWithMap() {
        Element e = doc.createElementNS("http://www.xmlunit.org/test", "foo");
        e.setAttributeNS("http://www.xmlunit.org/test", "foo", "bar");
        e.setAttributeNS("http://www.xmlunit.org/test", "baz", "xyzzy");
        Map<String, String> p2u = Collections.singletonMap("x", "http://www.xmlunit.org/test");
        builder.setNamespaceContext(p2u);
        assertEquals("/x:foo[1]/@x:foo",
                     builder.apply(e.getAttributeNode("foo")).getXPath());
    }
}
