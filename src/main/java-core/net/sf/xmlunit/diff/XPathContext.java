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
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import net.sf.xmlunit.util.Nodes;
import org.w3c.dom.Node;

public class XPathContext {
    private final Deque<Level> path = new LinkedList<Level>();
    private final Map<String, String> uri2Prefix;

    public XPathContext() {
        this(null);
    }

    public XPathContext(Map<String, String> uri2Prefix) {
        if (uri2Prefix == null) {
            this.uri2Prefix = Collections.emptyMap();
        } else {
            this.uri2Prefix = Collections.unmodifiableMap(uri2Prefix);
        }
        path.addLast(new Level(""));
    }

    public void navigateToChild(int index) {
        path.addLast(path.getLast().children.get(index));
    }

    public void navigateToAttribute(QName attribute) {
        path.addLast(path.getLast().attributes.get(attribute));
    }

    public void navigateToParent() {
        path.removeLast();
    }

    public void registerAttributes(Iterable<? extends QName> attributes) {
        Level current = path.getLast();
        for (QName attribute : attributes) {
            current.attributes.put(attribute,
                                   new Level("@" + getName(attribute)));
        }
    }

    public void registerChildren(Iterable<? extends NodeInfo> children) {
        Level current = path.getLast();
        int comments, pis, texts;
        comments = pis = texts = 0;
        Map<String, Integer> elements = new HashMap<String, Integer>();
        for (NodeInfo child : children) {
            Level l = null;
            switch (child.getType()) {
            case Node.COMMENT_NODE:
                l = new Level("comment()[" + (++comments) + "]");
                break;
            case Node.PROCESSING_INSTRUCTION_NODE:
                l = new Level("processing-instruction()[" + (++pis) + "]");
                break;
            case Node.CDATA_SECTION_NODE:
            case Node.TEXT_NODE:
                l = new Level("text()[" + (++texts) + "]");
                break;
            case Node.ELEMENT_NODE:
                String name = getName(child.getName());
                Integer old = elements.get(name);
                int index = old == null ? 0 : old.intValue();
                l = new Level(name + "[" + (++index) + "]");
                elements.put(name, Integer.valueOf(index));
                break;
            default:
                throw new IllegalArgumentException("unknown node type " +
                                                   child.getType());
            }
            current.children.add(l);
        }
    }

    public String getXPath() {
        StringBuilder sb = new StringBuilder();
        for (Level l : path) {
            sb.append("/").append(l.expression);
        }
        return sb.toString().replace("//", "/");
    }

    private String getName(QName name) {
        String ns = name.getNamespaceURI();
        String p = null;
        if (ns != null) {
            p = uri2Prefix.get(ns);
        }
        return (p == null ? "" : p + ":") + name.getLocalPart();
    }

    private static class Level {
        private final String expression;
        private List<Level> children = new ArrayList<Level>();
        private Map<QName, Level> attributes = new HashMap<QName, Level>();
        private Level(String expression) {
            this.expression = expression;
        }
    }

    public static interface NodeInfo {
        QName getName();
        short getType();
    }

    public static final class DOMNodeInfo implements NodeInfo {
        private QName name;
        private short type;
        public DOMNodeInfo(Node n) {
            name = Nodes.getQName(n);
            type = n.getNodeType();
        }
        public QName getName() { return name; }
        public short getType() { return type; }
    }
}
