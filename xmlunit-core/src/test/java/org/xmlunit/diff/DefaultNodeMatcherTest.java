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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.junit.Before;
import org.junit.Test;
import org.xmlunit.util.Linqy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class DefaultNodeMatcherTest {

    private Document doc;

    @Before
    public void createDoc() throws Exception {
        doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .newDocument();
    }

    @Test
    public void elementSelectorsAreQueriedInSequence() {
        Element control1 = doc.createElement("a");
        control1.appendChild(doc.createTextNode("foo"));
        Element control2 = doc.createElement("a");
        control2.appendChild(doc.createTextNode("bar"));

        Element test1 = doc.createElement("a");
        test1.appendChild(doc.createTextNode("baz"));
        Element test2 = doc.createElement("a");
        test2.appendChild(doc.createTextNode("foo"));

        DefaultNodeMatcher m =
            new DefaultNodeMatcher(ElementSelectors.byNameAndText, ElementSelectors.byName);
        List<Map.Entry<Node, Node>> result =
            Linqy.asList(m.match(Arrays.<Node>asList(control1, control2),
                Arrays.<Node>asList(test1, test2)));
        assertEquals(2, result.size());

        // byNameAndText
        assertSame(control1, result.get(0).getKey());
        assertSame(test2, result.get(0).getValue());

        // byName
        assertSame(control2, result.get(1).getKey());
        assertSame(test1, result.get(1).getValue());
    }

    @Test
    // https://github.com/xmlunit/xmlunit/issues/197
    public void elementSelectorsAreQueriedInSequenceWithConditionalSelector() {
        Element control1 = doc.createElement("a");
        control1.appendChild(doc.createTextNode("foo"));
        Element control2 = doc.createElement("a");
        control2.appendChild(doc.createTextNode("bar"));

        Element test1 = doc.createElement("a");
        test1.appendChild(doc.createTextNode("baz"));
        Element test2 = doc.createElement("a");
        test2.appendChild(doc.createTextNode("foo"));

        DefaultNodeMatcher m =
            new DefaultNodeMatcher(ElementSelectors.selectorForElementNamed("a", ElementSelectors.byNameAndText),
                ElementSelectors.byName);
        List<Map.Entry<Node, Node>> result =
            Linqy.asList(m.match(Arrays.<Node>asList(control1, control2),
                Arrays.<Node>asList(test1, test2)));
        assertEquals(2, result.size());

        // byNameAndText
        assertSame(control1, result.get(0).getKey());
        assertSame(test2, result.get(0).getValue());

        // byName
        assertSame(control2, result.get(1).getKey());
        assertSame(test1, result.get(1).getValue());
    }
}
