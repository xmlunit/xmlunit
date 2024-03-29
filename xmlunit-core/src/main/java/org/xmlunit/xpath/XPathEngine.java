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
import org.w3c.dom.Node;

/**
 * Interface for XMLUnit's XPath abstraction.
 */
public interface XPathEngine {
    /**
     * Returns a potentially empty collection of Nodes matching an
     * XPath expression.
     * @param xPath the XPath expression
     * @param s the XML source to apply the expression to
     * @return matching nodes
     */
    Iterable<Node> selectNodes(String xPath, Source s);
    /**
     * Evaluates an XPath expression and stringifies the result.
     * @param xPath the XPath expression
     * @param s the XML source to apply the expression to
     * @return stringified result
     */
    String evaluate(String xPath, Source s);
    /**
     * Returns a potentially empty collection of Nodes matching an
     * XPath expression.
     * @param xPath the XPath expression
     * @param n the XML source to apply the expression to
     * @return matching nodes
     */
    Iterable<Node> selectNodes(String xPath, Node n);
    /**
     * Evaluates an XPath expression and stringifies the result.
     * @param xPath the XPath expression
     * @param n the XML source to apply the expression to
     * @return stringified result
     */
    String evaluate(String xPath, Node n);
    /**
     * Establish a namespace context.
     *
     * @param prefix2Uri maps from prefix to namespace URI.
     */
    void setNamespaceContext(Map<String, String> prefix2Uri);
}
