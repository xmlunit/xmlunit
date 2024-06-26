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
import javax.xml.transform.Source;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.xmlunit.ConfigurationException;
import org.xmlunit.XMLUnitException;
import org.xmlunit.util.Convert;
import org.xmlunit.util.IterableNodeList;
import org.xmlunit.util.XPathFactoryConfigurer;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Simplified access to JAXP's XPath API.
 */
public class JAXPXPathEngine implements XPathEngine {
    private final XPath xpath;

    /**
     * Create an XPathEngine that uses a custom XPathFactory.
     * @param fac the factory to use
     */
    public JAXPXPathEngine(XPathFactory fac) {
        try {
            xpath = fac.newXPath();
        } catch (Exception e) {
            throw new ConfigurationException(e);
        }
    }

    /**
     * Create an XPathEngine that uses JAXP's default XPathFactory with {@link XPathFactoryConfigurer#Default} applied
     * under the covers.
     */
    public JAXPXPathEngine() {
        this(XPathFactoryConfigurer.Default.configure(XPathFactory.newInstance()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<Node> selectNodes(String xPath, Source s) {
        try {
            return new IterableNodeList(
                (NodeList) xpath.evaluate(xPath, Convert.toInputSource(s),
                                          XPathConstants.NODESET)
                                        );
        } catch (XPathExpressionException ex) {
            throw new XMLUnitException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String evaluate(String xPath, Source s) {
        try {
            return xpath.evaluate(xPath, Convert.toInputSource(s));
        } catch (XPathExpressionException ex) {
            throw new XMLUnitException(ex);
        }
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
