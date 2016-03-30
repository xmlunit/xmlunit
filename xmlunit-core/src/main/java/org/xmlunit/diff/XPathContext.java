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
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.xmlunit.util.Linqy;
import org.xmlunit.util.Nodes;

import org.w3c.dom.Node;

/**
 * Helper class that keeps track of the XPath of matched nodes during
 * comparison.
 */
public class XPathContext implements Cloneable {
    private Deque<Level> path = new LinkedList<Level>();
    private final Map<String, String> uri2Prefix;

    private static final String COMMENT = "comment()";
    private static final String PI = "processing-instruction()";
    private static final String TEXT = "text()";
    private static final String OPEN = "[";
    private static final String CLOSE = "]";
    private static final String SEP = "/";
    private static final String ATTR = "@";
    private static final String EMPTY = "";

    /**
     * Starts with an empty context.
     */
    public XPathContext() {
        this(null, null);
    }

    /**
     * Starts with the context of a root node.
     */
    public XPathContext(Node root) {
        this(null, root);
    }

    /**
     * Starts with an empty context and a given namespace mapping.
     * @param prefix2uri maps from prefix to namespace URI.
     */
    public XPathContext(Map<String, String> prefix2uri) {
        this(prefix2uri, null);
    }

    /**
     * Starts with the context of an optional root node and an
     * optional namespace mapping.
     * @param prefix2uri maps from prefix to namespace URI.
     * @param root optional root node that determines the initial XPath
     */
    public XPathContext(Map<String, String> prefix2uri, Node root) {
        if (prefix2uri == null) {
            this.uri2Prefix = Collections.emptyMap();
        } else {
            this.uri2Prefix = Collections.unmodifiableMap(invert(prefix2uri));
        }
        path.addLast(new Level(EMPTY));
        if (root != null) {
            setChildren(Linqy.singleton(new DOMNodeInfo(root)));
            navigateToChild(0);
        }
    }

    /**
     * Moves from the current node to the given child node.
     */
    public void navigateToChild(int index) {
        path.addLast(path.getLast().children.get(index));
    }

    /**
     * Moves from the current node to the given attribute.
     */
    public void navigateToAttribute(QName attribute) {
        path.addLast(path.getLast().attributes.get(attribute));
    }

    /**
     * Moves back to the parent.
     */
    public void navigateToParent() {
        path.removeLast();
    }

    /**
     * Adds knowledge about the current node's attributes.
     */
    public void addAttributes(Iterable<? extends QName> attributes) {
        Level current = path.getLast();
        for (QName attribute : attributes) {
            current.attributes.put(attribute,
                                   new Level(ATTR + getName(attribute)));
        }
    }

    /**
     * Adds knowledge about a single attribute of the current node.
     */
    public void addAttribute(QName attribute) {
        Level current = path.getLast();
        current.attributes.put(attribute,
                               new Level(ATTR + getName(attribute)));
    }

    /**
     * Adds knowledge about the current node's children replacing
     * existing knowledge.
     */
    public void setChildren(Iterable<? extends NodeInfo> children) {
        Level current = path.getLast();
        current.children.clear();
        appendChildren(children);
    }

    /**
     * Adds knowledge about the current node's children appending to
     * the knowledge already present.
     */
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
            Level l;
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

    /**
     * Stringifies the XPath of the current node.
     */
    public String getXPath() {
        return getXPath(path.descendingIterator());
    }

    /**
     * Stringifies the XPath of the current node's parent.
     */
    public String getParentXPath() {
        Iterator<Level> levelIterator = path.descendingIterator();
        if (levelIterator.hasNext()) {
            levelIterator.next();
        }
        return getXPath(levelIterator);
    }

    /**
     * Creates a deep copy of this XPathContext.
     */
    @Override
    public XPathContext clone() {
        try {
            XPathContext c = (XPathContext) super.clone();
            c.path = new LinkedList<Level>();
            for (Level l: path) {
                c.path.addLast(l.clone());
            }
            return c;
        } catch (CloneNotSupportedException e) {
            // impossible
            throw new RuntimeException("XPathContext cannot be cloned?", e);
        }
    }

    private String getXPath(Iterator<Level> dIterator) {
        if (!dIterator.hasNext()) {
            return EMPTY;
        }
        Level l = dIterator.next();
        if (null == l.xpath) {
            String previous = getXPath(dIterator);
            if (!SEP.equals(previous)) {
                previous += SEP;
            }
            l.xpath = previous + l.expression;
        }
        return l.xpath;
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
        int index = old == null ? 1 : old.intValue() + 1;
        map.put(name, Integer.valueOf(index));
        return index;
    }

    private static class Level implements Cloneable {
        private final String expression;
        private List<Level> children = new ArrayList<Level>();
        private Map<QName, Level> attributes = new HashMap<QName, Level>();
        private String xpath;
        private Level(String expression) {
            this.expression = expression;
        }
        @Override
        public Level clone() {
            try {
                Level l = (Level) super.clone();
                l.children = new ArrayList<Level>(children.size());
                for (Level c : children) {
                    l.children.add(c.clone());
                }
                l.attributes = new HashMap<QName, Level>(attributes.size());
                for (Map.Entry<QName, Level> e : attributes.entrySet()) {
                    l.attributes.put(e.getKey(), e.getValue().clone());
                }
                return l;
            } catch (CloneNotSupportedException e) {
                // impossible
                throw new RuntimeException("Level cannot be cloned?", e);
            }
        }
    }

    /**
     * Representation of a node used by {@link XPathContext}.
     */
    public interface NodeInfo {
        QName getName();
        short getType();
    }

    /**
     * DOM based implementation of {@link NodeInfo}.
     */
    public static final class DOMNodeInfo implements NodeInfo {
        private final QName name;
        private final short type;
        public DOMNodeInfo(Node n) {
            name = Nodes.getQName(n);
            type = n.getNodeType();
        }
        @Override
        public QName getName() { return name; }
        @Override
        public short getType() { return type; }
    }

    private static Map<String, String> invert(Map<String, String> m) {
        Map<String, String> inverted = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : m.entrySet()) {
            inverted.put(entry.getValue(), entry.getKey());
        }
        return inverted;
    }
}
