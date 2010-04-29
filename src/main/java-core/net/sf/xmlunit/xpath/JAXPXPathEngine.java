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

import java.util.Map;
import javax.xml.transform.Source;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import net.sf.xmlunit.exceptions.ConfigurationException;
import net.sf.xmlunit.exceptions.XMLUnitException;
import net.sf.xmlunit.util.Convert;
import net.sf.xmlunit.util.IterableNodeList;
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
            return new IterableNodeList(
                (NodeList) xpath.evaluate(xPath, Convert.toInputSource(s),
                                          XPathConstants.NODESET)
                                        );
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
        xpath.setNamespaceContext(Convert.toNamespaceContext(prefix2Uri));
    }

}
