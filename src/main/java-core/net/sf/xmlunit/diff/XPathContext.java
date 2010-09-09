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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import net.sf.xmlunit.util.Nodes;
import org.w3c.dom.Node;

public class XPathContext {
    // that would be Deque<Level> in Java 6+
    private final LinkedList<Level> path = new LinkedList<Level>();
    private final Map<String, String> uri2Prefix;

    private static final String COMMENT = "comment()";
    private static final String PI = "processing-instruction()";
    private static final String TEXT = "text()";
    private static final String OPEN = "[";
    private static final String CLOSE = "]";
    private static final String SEP = "/";
    private static final String ATTR = "@";
    private static final String EMPTY = "";

    public XPathContext() {
        this(null);
    }

    public XPathContext(Map<String, String> uri2Prefix) {
        if (uri2Prefix == null) {
            this.uri2Prefix = Collections.emptyMap();
        } else {
            this.uri2Prefix = Collections.unmodifiableMap(uri2Prefix);
        }
        path.addLast(new Level(EMPTY));
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

    public void addAttributes(Iterable<? extends QName> attributes) {
        Level current = path.getLast();
        for (QName attribute : attributes) {
            current.attributes.put(attribute,
                                   new Level(ATTR + getName(attribute)));
        }
    }

    public void setChildren(Iterable<? extends NodeInfo> children) {
        Level current = path.getLast();
        current.children.clear();
        appendChildren(children);
    }

    public void appendChildren(Iterable<? extends NodeInfo> children) {
        Level current = path.getLast();
        int comments, pis, texts;
        comments = pis = texts = 0;
        Map<String, Integer> elements = new HashMap<String, Integer>();

        for (Level l : current.children) {
            String childName = l.expression;
            if (childName.startsWith(COMMENT)) {
                comments++;
            } else if (childName.startsWith(PI)) {
                pis++;
            } else if (childName.startsWith(TEXT)) {
                texts++;
            } else {
                childName = childName.substring(0, childName.indexOf(OPEN));
                add1OrIncrement(childName, elements);
            }
        }

        for (NodeInfo child : children) {
            Level l = null;
            switch (child.getType()) {
            case Node.COMMENT_NODE:
                l = new Level(COMMENT + OPEN + (++comments) + CLOSE);
                break;
            case Node.PROCESSING_INSTRUCTION_NODE:
                l = new Level(PI + OPEN + (++pis) + CLOSE);
                break;
            case Node.CDATA_SECTION_NODE:
            case Node.TEXT_NODE:
                l = new Level(TEXT + OPEN + (++texts) + CLOSE);
                break;
            case Node.ELEMENT_NODE:
                String name = getName(child.getName());
                l = new Level(name + OPEN + add1OrIncrement(name, elements)
                              + CLOSE);
                break;
            default:
                // more or less ignore
                // FIXME: is this a good thing?
                l = new Level(EMPTY);
                break;
            }
            current.children.add(l);
        }
    }

    public String getXPath() {
        StringBuilder sb = new StringBuilder();
        for (Level l : path) {
            sb.append(SEP).append(l.expression);
        }
        return sb.toString().replace(SEP + SEP, SEP);
    }

    private String getName(QName name) {
        String ns = name.getNamespaceURI();
        String p = null;
        if (ns != null) {
            p = uri2Prefix.get(ns);
        }
        return (p == null ? EMPTY : p + ":") + name.getLocalPart();
    }

    /**
     * Increments the value name maps to or adds 1 as value if name
     * isn't present inside the map.
     *
     * @return the new mapping for name
     */
    private static int add1OrIncrement(String name, Map<String, Integer> map) {
        Integer old = map.get(name);
        int index = old == null ? 1 : (old.intValue() + 1);
        map.put(name, Integer.valueOf(index));
        return index;
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
