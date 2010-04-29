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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.Source;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import net.sf.xmlunit.exceptions.ConfigurationException;
import net.sf.xmlunit.exceptions.XMLUnitException;
import net.sf.xmlunit.util.Convert;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Simplified access to JAXP's XPath API.
 */
public class JAXPXPathEngine {
    private final XPath xpath;

    public JAXPXPathEngine(XPathFactory fac) {
        try {
            xpath = fac.newXPath();
        } catch (Exception e) {
            throw new ConfigurationException(e);
        }
    }

    /**
     * Create an XPathEngine that uses JAXP's default XPathFactory
     * under the covers.
     */
    public JAXPXPathEngine() {
        this(XPathFactory.newInstance());
    }

    /**
     * Returns a potentially empty collection of Nodes matching an
     * XPath expression.
     */
    public Iterable<Node> selectNodes(String xPath, Source s) {
        try {
            NodeList nl = (NodeList) xpath.evaluate(xPath,
                                                    Convert.toInputSource(s),
                                                    XPathConstants.NODESET);
            return new IterableNodeList(nl);
        } catch (XPathExpressionException ex) {
            throw new XMLUnitException(ex);
        }
    }

    /**
     * Evaluates an XPath expression and stringifies the result.
     */
    public String evaluate(String xPath, Source s) {
        try {
            return xpath.evaluate(xPath, Convert.toInputSource(s));
        } catch (XPathExpressionException ex) {
            throw new XMLUnitException(ex);
        }
    }

    /**
     * Establish a namespace context.
     *
     * @param prefix2Uri maps from prefix to namespace URI.
     */
    public void setNamespaceContext(Map<String, String> prefix2Uri) {
        xpath.setNamespaceContext(new NC(prefix2Uri));
    }

    private static class NC implements NamespaceContext {
        private final Map<String, String> prefix2Uri;

        private NC(Map<String, String> prefix2Uri) {
            this.prefix2Uri = prefix2Uri;
        }

        public String getNamespaceURI(String prefix) {
            if (prefix == null) {
                throw new IllegalArgumentException("prefix must not be null");
            }
            if (XMLConstants.XML_NS_PREFIX.equals(prefix)) {
                return XMLConstants.XML_NS_URI;
            }
            if (XMLConstants.XMLNS_ATTRIBUTE.equals(prefix)) {
                return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
            }
            String uri = prefix2Uri.get(prefix);
            return uri != null ? uri : XMLConstants.NULL_NS_URI;
        }

        public String getPrefix(String uri) {
            Iterator i = getPrefixes(uri);
            return i.hasNext() ? (String) i.next() : null;
        }

        public Iterator getPrefixes(String uri) {
            if (uri == null) {
                throw new IllegalArgumentException("uri must not be null");
            }
            Collection<String> c = new HashSet<String>();
            boolean done = false;
            if (XMLConstants.XML_NS_URI.equals(uri)) {
                c.add(XMLConstants.XML_NS_PREFIX);
                done = true;
            }
            if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(uri)) {
                c.add(XMLConstants.XMLNS_ATTRIBUTE);
                done = true;
            }
            if (!done) {
                for (Map.Entry<String, String> entry : prefix2Uri.entrySet()) {
                    if (uri.equals(entry.getValue())) {
                        c.add(entry.getKey());
                    }
                }
            }
            return c.iterator();
        }
    }

    private static class IterableNodeList implements Iterable<Node> {
        private final NodeList nl;
        private final int length;
        private int current = 0;
        private IterableNodeList(NodeList nl) {
            this.nl = nl;
            length = nl.getLength();
        }
        public Iterator<Node> iterator() {
            return new Iterator<Node>() {
                public void remove() {
                    throw new UnsupportedOperationException();
                }
                public Node next() {
                    return nl.item(current++);
                }
                public boolean hasNext() {
                    return current < length;
                }
            };
        }
    }

}
