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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.xmlunit.ConfigurationException;
import org.xmlunit.XMLUnitException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Conversion methods.
 */
public final class Convert {
    private Convert() { }

    /**
     * Creates a SAX InputSource from a TraX Source.
     *
     * <p>May use an XSLT identity transformation if SAXSource cannot
     * convert it directly.</p>
     *
     * @param s the source to convert
     * @return the created InputSource
     */
    public static InputSource toInputSource(Source s) {
        return toInputSource(s, null);
    }

    /**
     * Creates a SAX InputSource from a TraX Source.
     *
     * <p>May use an XSLT identity transformation if SAXSource cannot
     * convert it directly.</p>
     *
     * @param s the source to convert
     * @param fac the TransformerFactory to use, will use the default
     * factory if the value is null.
     * @return the created InputSource
     */
    public static InputSource toInputSource(Source s, TransformerFactory fac) {
        try {
            InputSource is = SAXSource.sourceToInputSource(s);
            if (is == null) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                StreamResult r = new StreamResult(bos);
                if (fac == null) {
                    fac = TransformerFactoryConfigurer.NoExternalAccess.configure(TransformerFactory.newInstance());
                }
                Transformer t = fac.newTransformer();
                t.transform(s, r);
                s = new StreamSource(new ByteArrayInputStream(bos
                                                              .toByteArray()));
                is = SAXSource.sourceToInputSource(s);
            }
            return is;
        } catch (javax.xml.transform.TransformerConfigurationException e) {
            throw new ConfigurationException(e);
        } catch (javax.xml.transform.TransformerException e) {
            throw new XMLUnitException(e);
        }
    }

    /**
     * Creates a DOM Document from a TraX Source.
     *
     * <p>If the source is a {@link DOMSource} holding a Document
     * Node, this one will be returned.  Otherwise {@link
     * #toInputSource} and a namespace aware DocumentBuilder (created
     * by the default DocumentBuilderFactory) will be used to read the
     * source.  This may involve an XSLT identity transform in
     * toInputSource.</p>
     *
     * @param s the source to convert
     * @return the created Document
     */
    public static Document toDocument(Source s) {
        Document d = tryExtractDocFromDOMSource(s);
        return d != null ? d
            : toDocument(s, DocumentBuilderFactoryConfigurer.Default.configure(DocumentBuilderFactory.newInstance()));
    }

    /**
     * Creates a DOM Document from a TraX Source.
     *
     * <p>If the source is a {@link DOMSource} holding a Document
     * Node, this one will be returned.  Otherwise {@link
     * #toInputSource} and a namespace aware DocumentBuilder (created
     * by given DocumentBuilderFactory) will be used to read the
     * source.  This may involve an XSLT identity transform in
     * toInputSource.</p>
     *
     * @param s the source to convert
     * @param factory factory to use
     * @return the created Document
     */
    public static Document toDocument(Source s,
                                      DocumentBuilderFactory factory) {
        Document d = tryExtractDocFromDOMSource(s);
        if (d == null) {
            InputSource is = toInputSource(s);
            DocumentBuilder b = null;

            // yes, there is a race condition but it is so unlikely to
            // happen that I currently don't care enough
            boolean oldNsAware = factory.isNamespaceAware();
            try {
                if (!oldNsAware) {
                    factory.setNamespaceAware(true);
                }
                b = factory.newDocumentBuilder();
            } catch (javax.xml.parsers.ParserConfigurationException e) {
                throw new ConfigurationException(e);
            } finally {
                if (!oldNsAware) {
                    factory.setNamespaceAware(false);
                }
            }

            try {
                d = b.parse(is);
            } catch (org.xml.sax.SAXException e) {
                throw new XMLUnitException(e);
            } catch (java.io.IOException e) {
                throw new XMLUnitException(e);
            }
        }
        return d;
    }

    private static Document tryExtractDocFromDOMSource(Source s) {
        Node n = tryExtractNodeFromDOMSource(s);
        if (n != null && n instanceof Document) {
            return (Document)n;
        }
        return null;
    }

    /**
     * Creates a DOM Node from a TraX Source.
     *
     * <p>If the source is a {@link DOMSource} its Node will be
     * returned, otherwise this delegates to {@link #toDocument}.</p>
     *
     * @param s the source to convert
     * @return the created Node
     */
    public static Node toNode(Source s) {
        Node n = tryExtractNodeFromDOMSource(s);
        return n != null ? n
            : toDocument(s, DocumentBuilderFactoryConfigurer.Default.configure(DocumentBuilderFactory.newInstance()));
    }

    /**
     * Creates a DOM Node from a TraX Source.
     *
     * <p>If the source is a {@link DOMSource} its Node will be
     * returned, otherwise this delegates to {@link #toDocument}.</p>
     *
     * @param s the source to convert
     * @param factory factory to use
     * @return the created Node
     */
    public static Node toNode(Source s,
                              DocumentBuilderFactory factory) {
        Node n = tryExtractNodeFromDOMSource(s);
        return n != null ? n : toDocument(s, factory);
    }

    private static Node tryExtractNodeFromDOMSource(Source s) {
        if (s instanceof DOMSource) {
            @SuppressWarnings("unchecked") DOMSource ds = (DOMSource) s;
            return ds.getNode();
        }
        return null;
    }

    /**
     * Creates a JAXP NamespaceContext from a Map prefix =&gt; Namespace URI.
     * @param prefix2URI maps from prefix to namespace URI.
     * @return the created NamespaceContext
     */
    public static NamespaceContext
        toNamespaceContext(Map<String, String> prefix2URI) {
        final Map<String, String> copy =
            new LinkedHashMap<String, String>(prefix2URI);
        return new NamespaceContext() {
            @Override
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
                String uri = copy.get(prefix);
                return uri != null ? uri : XMLConstants.NULL_NS_URI;
            }

            @Override
            public String getPrefix(String uri) {
                Iterator i = getPrefixes(uri);
                return i.hasNext() ? (String) i.next() : null;
            }

            @Override
            public Iterator getPrefixes(String uri) {
                if (uri == null) {
                    throw new IllegalArgumentException("uri must not be null");
                }
                Collection<String> c = new LinkedHashSet<String>();
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
                    for (Map.Entry<String, String> entry : copy.entrySet()) {
                        if (uri.equals(entry.getValue())) {
                            c.add(entry.getKey());
                        }
                    }
                }
                return c.iterator();
            }
        };
    }
}
