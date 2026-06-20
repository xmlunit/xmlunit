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
package org.xmlunit.xpath;

import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.xmlunit.ConfigurationException;
import org.xmlunit.XMLUnitException;
import org.xmlunit.util.Convert;
import org.xmlunit.util.DocumentBuilderFactoryConfigurer;
import org.xmlunit.util.IterableNodeList;
import org.xmlunit.util.XPathFactoryConfigurer;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Simplified access to JAXP's XPath API.
 */
public class JAXPXPathEngine implements XPathEngine {
    private final XPath xpath;
    private final DocumentBuilderFactory dbf;

    /**
     * Create an XPathEngine that uses a custom XPathFactory and a custom
     * DocumentBuilderFactory for parsing {@link Source}s that are not already DOM nodes.
     * @param fac the XPathFactory to use
     * @param dbf the DocumentBuilderFactory to use
     * @since XMLUnit 2.12.1
     */
    public JAXPXPathEngine(XPathFactory fac, DocumentBuilderFactory dbf) {
        try {
            xpath = fac.newXPath();
        } catch (Exception e) {
            throw new ConfigurationException(e);
        }
        this.dbf = dbf;
    }

    /**
     * Create an XPathEngine that uses a custom XPathFactory and a DocumentBuilderFactory hardened with
     * {@link DocumentBuilderFactoryConfigurer#Default}.
     * @param fac the factory to use
     */
    public JAXPXPathEngine(XPathFactory fac) {
        this(fac, DocumentBuilderFactoryConfigurer.Default.configure(DocumentBuilderFactory.newInstance()));
    }

    /**
     * Create an XPathEngine that uses JAXP's default XPathFactory with {@link XPathFactoryConfigurer#Default} applied
     * under the covers and a custom DocumentBuilderFactory for parsing {@link Source}s that are not already DOM nodes.
     * @param dbf the DocumentBuilderFactory to use
     * @since XMLUnit 2.12.1
     */
    public JAXPXPathEngine(DocumentBuilderFactory dbf) {
        this(XPathFactoryConfigurer.Default.configure(XPathFactory.newInstance()), dbf);
    }

    /**
     * Create an XPathEngine that uses JAXP's default XPathFactory with {@link XPathFactoryConfigurer#Default} applied
     * under the covers and a DocumentBuilderFactory hardened with {@link DocumentBuilderFactoryConfigurer#Default}.
     */
    public JAXPXPathEngine() {
        this(XPathFactoryConfigurer.Default.configure(XPathFactory.newInstance()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<Node> selectNodes(String xPath, Source s) {
        return selectNodes(xPath, Convert.toNode(s, dbf));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String evaluate(String xPath, Source s) {
        return evaluate(xPath, Convert.toNode(s, dbf));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<Node> selectNodes(String xPath, Node n) {
        try {
            return new IterableNodeList(
                (NodeList) xpath.evaluate(xPath, n, XPathConstants.NODESET));
        } catch (XPathExpressionException ex) {
            throw new XMLUnitException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String evaluate(String xPath, Node n) {
        try {
            return xpath.evaluate(xPath, n);
        } catch (XPathExpressionException ex) {
            throw new XMLUnitException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNamespaceContext(Map<String, String> prefix2Uri) {
        xpath.setNamespaceContext(Convert.toNamespaceContext(prefix2Uri));
    }

}
