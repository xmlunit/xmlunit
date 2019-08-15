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
package org.xmlunit.assertj;

import static org.xmlunit.assertj.error.ShouldHaveAttribute.shouldHaveAttribute;
import static org.xmlunit.assertj.error.ShouldHaveAttribute.shouldHaveAttributeWithValue;
import static org.xmlunit.assertj.error.ShouldHaveXPath.shouldHaveXPath;
import static org.xmlunit.assertj.error.ShouldNotHaveAttribute.shouldNotHaveAttribute;
import static org.xmlunit.assertj.error.ShouldNotHaveAttribute.shouldNotHaveAttributeWithValue;

import java.util.Map;

import javax.xml.namespace.QName;

import org.assertj.core.api.AbstractAssert;
import org.w3c.dom.Node;
import org.xmlunit.util.Nodes;
import org.xmlunit.xpath.JAXPXPathEngine;

/**
 * Assertion methods for {@link Node}.
 *
 * <p><b>Simple Example</b></p>
 *
 * <pre>
 * import static org.xmlunit.assertj.XmlAssert.assertThat;
 *
 * final String xml = &quot;&lt;a&gt;&lt;b attr=\&quot;abc\&quot;&gt;&lt;/b&gt;&lt;/a&gt;&quot;;
 *
 * assertThat(xml).nodesByXPath("//a/b").first().hasAttribute("attr", "abc").
 * </pre>
 *
 * @since XMLUnit 2.6.1
 */
public class SingleNodeAssert extends AbstractAssert<SingleNodeAssert, Node> {

    private final JAXPXPathEngine engine;

    SingleNodeAssert(Node node, JAXPXPathEngine engine) {
        super(node, SingleNodeAssert.class);
        this.engine = engine;
    }

    /**
     * Verifies that the actual node has attribute with given name.
     *
     * @throws AssertionError if the actual node is {@code null}.
     * @throws AssertionError if node has not attribute with given name.
     */
    public SingleNodeAssert hasAttribute(String attributeName) {
        isNotNull();

        final Map.Entry<QName, String> entry = attributeForName(attributeName);
        if (entry == null) {
            throwAssertionError(shouldHaveAttribute(actual.getNodeName(), attributeName));
        }
        return this;
    }

    /**
     * Verifies that the actual node has attribute with given name and value.
     *
     * @throws AssertionError if the actual node is {@code null}.
     * @throws AssertionError if node has not attribute with given name and value.
     */
    public SingleNodeAssert hasAttribute(String attributeName, String attributeValue) {
        isNotNull();

        final Map.Entry<QName, String> attribute = attributeForName(attributeName);
        if (attribute == null || !attribute.getValue().equals(attributeValue)) {
            throwAssertionError(shouldHaveAttributeWithValue(actual.getNodeName(), attributeName, attributeValue));
        }

        return this;
    }

    /**
     * Verifies that the actual node has not attribute with given name.
     *
     * @throws AssertionError if the actual node is {@code null}.
     * @throws AssertionError if node has attribute with given name.
     */
    public SingleNodeAssert doesNotHaveAttribute(String attributeName) {
        isNotNull();

        final Map.Entry<QName, String> entry = attributeForName(attributeName);
        if (entry != null) {
            throwAssertionError(shouldNotHaveAttribute(actual.getNodeName(), attributeName));
        }
        return this;
    }

    /**
     * Verifies that the actual node has not attribute with given name and value.
     *
     * @throws AssertionError if the actual node is {@code null}.
     * @throws AssertionError if node has attribute with given name and value.
     */
    public SingleNodeAssert doesNotHaveAttribute(String attributeName, String attributeValue) {
        isNotNull();

        final Map.Entry<QName, String> attribute = attributeForName(attributeName);
        if (attribute != null && attribute.getValue().equals(attributeValue)) {
            throwAssertionError(shouldNotHaveAttributeWithValue(actual.getNodeName(), attributeName, attributeValue));
        }

        return this;
    }

    /**
     * Verifies that the actual node or any child node matches given {@code xPath}.
     * The actual node is the root for {@code xPath}.
     *
     * @throws AssertionError if the actual node is {@code null}.
     * @throws AssertionError if node has attribute with given name and value.
     *
     * @since XMLUnit 2.6.4
     */
    public SingleNodeAssert hasXPath(String xPath) {
        isNotNull();

        if (isNodeSetEmpty(xPath)) {
            throwAssertionError(shouldHaveXPath(actual.getNodeName(), xPath));
        }

        return this;
    }

    boolean isNodeSetEmpty(String xPath) {
        return !engine.selectNodes(xPath, actual).iterator().hasNext();
    }

    private Map.Entry<QName, String> attributeForName(String attributeName) {

        Map<QName, String> attributes = Nodes.getAttributes(actual);

        for (Map.Entry<QName, String> entry : attributes.entrySet()) {
            final QName qName = entry.getKey();
            if (matchQName(qName, attributeName)) {
                return entry;
            }
        }

        return null;
    }

    private static boolean matchQName(QName qName, String name) {

        return qName.toString().equals(name)
                || (qName.getPrefix() + ":" + qName.getLocalPart()).equals(name)
                || qName.getLocalPart().equals(name);
    }
}
