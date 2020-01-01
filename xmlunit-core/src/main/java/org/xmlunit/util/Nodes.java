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
package org.xmlunit.util;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/**
 * Utility algorithms that work on DOM nodes.
 */
public final class Nodes {
    private static final char SPACE = ' ';

    private Nodes() { }

    /**
     * Extracts a Node's name, namespace URI (if any) and prefix as a
     * QName.
     */
    public static QName getQName(Node n) {
        String s = n.getLocalName();
        String p = n.getPrefix();
        return s != null
            ? new QName(n.getNamespaceURI(), s,
                        p != null ? p: XMLConstants.DEFAULT_NS_PREFIX)
            : new QName(n.getNodeName());
    }

    /**
     * Tries to merge all direct Text and CDATA children of the given
     * Node and concatenates their value.
     *
     * @return an empty string if the Node has no Text or CDATA
     * children.
     */
    public static String getMergedNestedText(Node n) {
        StringBuilder sb = new StringBuilder();
        for (Node child : new IterableNodeList(n.getChildNodes())) {
            if (child instanceof Text) {
                String s = child.getNodeValue();
                if (s != null) {
                    sb.append(s);
                }
            }
        }
        return sb.toString();
    }

    /**
     * Obtains an element's attributes as Map.
     */
    public static Map<QName, String> getAttributes(Node n) {
        Map<QName, String> map = new LinkedHashMap<QName, String>();
        NamedNodeMap m = n.getAttributes();
        if (m != null) {
            final int len = m.getLength();
            for (int i = 0; i < len; i++) {
                Attr a = (Attr) m.item(i);
                map.put(getQName(a), a.getValue());
            }
        }
        return map;
    }

    /**
     * Creates a new Node (of the same type as the original node) that
     * is similar to the orginal but doesn't contain any empty text or
     * CDATA nodes and where all textual content including attribute
     * values or comments are trimmed.
     */
    public static Node stripWhitespace(Node original) {
        Node cloned = original.cloneNode(true);
        cloned.normalize();
        handleWsRec(cloned, false);
        return cloned;
    }

    /**
     * Creates a new Node (of the same type as the original node) that
     * is similar to the orginal but doesn't contain any empty text or
     * CDATA nodes and where all textual content including attribute
     * values or comments are trimmed and normalized.
     *
     * <p>"normalized" in this context means all whitespace characters
     * are replaced by space characters and consecutive whitespace
     * characaters are collapsed.</p>
     */
    public static Node normalizeWhitespace(Node original) {
        Node cloned = original.cloneNode(true);
        cloned.normalize();
        handleWsRec(cloned, true);
        return cloned;
    }

    /**
     * Creates a new Node (of the same type as the original node) that
     * is similar to the orginal but doesn't contain any text or CDATA
     * nodes that only consist of whitespace.
     *
     * <p>This doesn't have any effect if applied to a text or CDATA
     * node itself.</p>
     *
     * @since XMLUnit 2.6.0
     */
    public static Node stripElementContentWhitespace(Node original) {
        Node cloned = original.cloneNode(true);
        cloned.normalize();
        stripECW(cloned);
        return cloned;
    }

    /**
     * Trims textual content of this node, removes empty text and
     * CDATA children, recurses into its child nodes.
     * @param normalize whether to normalize whitespace as well
     */
    private static void handleWsRec(Node n, boolean normalize) {
        if (n instanceof CharacterData || n instanceof ProcessingInstruction) {
            String s = n.getNodeValue().trim();
            if (normalize) {
                s = normalize(s);
            }
            n.setNodeValue(s);
        }
        List<Node> toRemove = new LinkedList<Node>();
        for (Node child : new IterableNodeList(n.getChildNodes())) {
            handleWsRec(child, normalize);
            if (!(n instanceof Attr)
                && (child instanceof Text)
                && child.getNodeValue().length() == 0) {
                toRemove.add(child);
            }
        }
        for (Node child : toRemove) {
            n.removeChild(child);
        }
        NamedNodeMap attrs = n.getAttributes();
        if (attrs != null) {
            final int len = attrs.getLength();
            for (int i = 0; i < len; i++) {
                handleWsRec(attrs.item(i), normalize);
            }
        }
    }

    /**
     * Normalize a string.
     *
     * <p>"normalized" in this context means all whitespace characters
     * are replaced by space characters and consecutive whitespace
     * characaters are collapsed.</p>
     */
    static String normalize(String s) {
        StringBuilder sb = new StringBuilder();
        boolean changed = false;
        boolean lastCharWasWS = false;
        final int len = s.length();
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (Character.isWhitespace(c)) {
                if (!lastCharWasWS) {
                    sb.append(SPACE);
                    changed |= c != SPACE;
                } else {
                    changed = true;
                }
                lastCharWasWS = true;
            } else {
                sb.append(c);
                lastCharWasWS = false;
            }
        }
        return changed ? sb.toString() : s;
    }

    private static void stripECW(Node n) {
        List<Node> toRemove = new LinkedList<Node>();
        for (Node child : new IterableNodeList(n.getChildNodes())) {
            stripECW(child);
            if (!(n instanceof Attr)
                && (child instanceof Text)
                && child.getNodeValue().trim().length() == 0) {
                toRemove.add(child);
            }
        }
        for (Node child : toRemove) {
            n.removeChild(child);
        }
    }

}
