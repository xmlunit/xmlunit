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
package net.sf.xmlunit.diff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import javax.xml.namespace.QName;
import org.junit.Test;
import org.w3c.dom.Node;

import static org.junit.Assert.*;

public class XPathContextTest {
    @Test public void empty() {
        assertEquals("/", new XPathContext().getXPath());
    }

    @Test public void oneLevelOfElements() {
        ArrayList<Element> l = new ArrayList<Element>();
        l.add(new Element("foo"));
        l.add(new Element("foo"));
        l.add(new Element("bar"));
        l.add(new Element("foo"));
        XPathContext ctx = new XPathContext();
        ctx.registerChildren(l);
        ctx.navigateToChild(0);
        assertEquals("/foo[1]", ctx.getXPath());
        ctx.navigateToParent();
        ctx.navigateToChild(1);
        assertEquals("/foo[2]", ctx.getXPath());
        ctx.navigateToParent();
        ctx.navigateToChild(2);
        assertEquals("/bar[1]", ctx.getXPath());
        ctx.navigateToParent();
        ctx.navigateToChild(3);
        assertEquals("/foo[3]", ctx.getXPath());
    }

    @Test public void twoLevelsOfElements() {
        ArrayList<Element> l = new ArrayList<Element>();
        l.add(new Element("foo"));
        l.add(new Element("foo"));
        l.add(new Element("bar"));
        l.add(new Element("foo"));
        XPathContext ctx = new XPathContext();
        ctx.registerChildren(l);
        ctx.navigateToChild(0);
        assertEquals("/foo[1]", ctx.getXPath());
        ctx.registerChildren(l);
        ctx.navigateToChild(3);
        assertEquals("/foo[1]/foo[3]", ctx.getXPath());
        ctx.navigateToParent();
        assertEquals("/foo[1]", ctx.getXPath());
        ctx.navigateToParent();
        ctx.navigateToChild(2);
        assertEquals("/bar[1]", ctx.getXPath());
    }

    @Test public void attributes() {
        XPathContext ctx = new XPathContext();
        ctx.registerChildren(Collections.singletonList(new Element("foo")));
        ctx.navigateToChild(0);
        ArrayList<QName> l = new ArrayList<QName>();
        l.add(new QName("bar"));
        ctx.registerAttributes(l);
        ctx.navigateToAttribute(new QName("bar"));
        assertEquals("/foo[1]/@bar", ctx.getXPath());
    }

    @Test public void mixed() {
        ArrayList<XPathContext.NodeInfo> l = new ArrayList<XPathContext.NodeInfo>();
        l.add(new Text());
        l.add(new Comment());
        l.add(new CDATA());
        l.add(new PI());
        l.add(new CDATA());
        l.add(new Comment());
        l.add(new PI());
        l.add(new Text());
        XPathContext ctx = new XPathContext();
        ctx.registerChildren(l);
        ctx.navigateToChild(0);
        assertEquals("/text()[1]", ctx.getXPath());
        ctx.navigateToParent();
        ctx.navigateToChild(1);
        assertEquals("/comment()[1]", ctx.getXPath());
        ctx.navigateToParent();
        ctx.navigateToChild(2);
        assertEquals("/text()[2]", ctx.getXPath());
        ctx.navigateToParent();
        ctx.navigateToChild(3);
        assertEquals("/processing-instruction()[1]", ctx.getXPath());
        ctx.navigateToParent();
        ctx.navigateToChild(4);
        assertEquals("/text()[3]", ctx.getXPath());
        ctx.navigateToParent();
        ctx.navigateToChild(5);
        assertEquals("/comment()[2]", ctx.getXPath());
        ctx.navigateToParent();
        ctx.navigateToChild(6);
        assertEquals("/processing-instruction()[2]", ctx.getXPath());
        ctx.navigateToParent();
        ctx.navigateToChild(7);
        assertEquals("/text()[4]", ctx.getXPath());
    }

    @Test public void elementsAndNs() {
        ArrayList<Element> l = new ArrayList<Element>();
        l.add(new Element("foo", "urn:foo:foo"));
        l.add(new Element("foo"));
        l.add(new Element("foo", "urn:foo:bar"));
        HashMap<String, String> m = new HashMap<String, String>();
        m.put("urn:foo:bar", "bar");
        XPathContext ctx = new XPathContext(m);
        ctx.registerChildren(l);
        ctx.navigateToChild(0);
        assertEquals("/foo[1]", ctx.getXPath());
        ctx.navigateToParent();
        ctx.navigateToChild(1);
        assertEquals("/foo[2]", ctx.getXPath());
        ctx.navigateToParent();
        ctx.navigateToChild(2);
        assertEquals("/bar:foo[1]", ctx.getXPath());
    }

    @Test public void attributesAndNs() {
        HashMap<String, String> m = new HashMap<String, String>();
        m.put("urn:foo:bar", "bar");
        XPathContext ctx = new XPathContext(m);
        ctx.registerChildren(Collections.singletonList(new Element("foo",
                                                                   "urn:foo:bar"))
                             );
        ctx.navigateToChild(0);
        ArrayList<QName> l = new ArrayList<QName>();
        l.add(new QName("baz"));
        l.add(new QName("urn:foo:bar", "baz"));
        ctx.registerAttributes(l);
        ctx.navigateToAttribute(new QName("baz"));
        assertEquals("/bar:foo[1]/@baz", ctx.getXPath());
        ctx.navigateToParent();
        ctx.navigateToAttribute(new QName("urn:foo:bar", "baz"));
        assertEquals("/bar:foo[1]/@bar:baz", ctx.getXPath());
        ctx.navigateToParent();
    }

    private static class Element implements XPathContext.NodeInfo {
        private final QName name;
        private Element(String name) {
            this.name = new QName(name);
        }
        private Element(String name, String ns) {
            this.name = new QName(ns, name);
        }
        public QName getName() { return name; }
        public short getType() { return Node.ELEMENT_NODE; }
    }

    private static abstract class NonElement implements XPathContext.NodeInfo {
        public QName getName() { return null; }
    }
    private static class Text extends NonElement {
        public short getType() { return Node.TEXT_NODE; }
    }
    private static class Comment extends NonElement {
        public short getType() { return Node.COMMENT_NODE; }
    }
    private static class PI extends NonElement {
        public short getType() { return Node.PROCESSING_INSTRUCTION_NODE; }
    }
    private static class CDATA extends NonElement {
        public short getType() { return Node.CDATA_SECTION_NODE; }
    }
}