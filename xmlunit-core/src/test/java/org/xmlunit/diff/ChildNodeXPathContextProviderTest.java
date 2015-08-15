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

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.junit.Before;
import org.junit.Test;
import org.xmlunit.util.Linqy;

import static org.junit.Assert.*;

public class ChildNodeXPathContextProviderTest {

    private Document doc;
    private XPathContext ctx;
    private List<Node> elements;

    @Before
    public void init() throws Exception {
        doc =
            DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .newDocument();
        elements = new ArrayList<Node>();
        elements.add(doc.createElement("foo"));
        elements.add(doc.createElement("foo"));
        elements.add(doc.createElement("bar"));
        elements.add(doc.createElement("foo"));
        ctx = new XPathContext();
        ctx.setChildren(Linqy.map(elements, ElementSelectors.TO_NODE_INFO));
    }
    
    @Test
    public void shouldReturnACopyOfOriginalXPathContext() {
        ChildNodeXPathContextProvider p = new ChildNodeXPathContextProvider(ctx, elements);
        XPathContext provided = p.apply(elements.get(0));
        assertNotSame(ctx, provided);
    }

    @Test
    public void shouldFindCorrectChildIndex() {
        ChildNodeXPathContextProvider p = new ChildNodeXPathContextProvider(ctx, elements);
        XPathContext provided = p.apply(elements.get(1));
        assertEquals("/foo[2]", provided.getXPath());
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldThrowIfNodeIsNotInInitialList() throws Exception {
        ChildNodeXPathContextProvider p = new ChildNodeXPathContextProvider(ctx, elements);
        XPathContext provided = p.apply(doc.createElement("foo"));
    }
}
