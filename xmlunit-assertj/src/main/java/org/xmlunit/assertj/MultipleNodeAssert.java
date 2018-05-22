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

import org.assertj.core.api.Assertions;
import org.assertj.core.api.FactoryBasedNavigableIterableAssert;
import org.w3c.dom.Node;
import org.xmlunit.builder.Input;
import org.xmlunit.util.Convert;
import org.xmlunit.xpath.XPathEngine;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;

/**
 * Assertion methods for {@link Iterable} of {@link Node}.
 *
 * <p><b>Simple Example</b></p>
 *
 * <pre>
 * import static org.xmlunit.assertj.XmlAssert.assertThat;
 *
 * final String xml = &quot;&lt;a&gt;&lt;b attr=\&quot;abc\&quot;&gt;&lt;/b&gt;&lt;/a&gt;&quot;;
 *
 * assertThat(xml).nodesByXPath("//a/b").haveAttribute("attr").
 * </pre>
 * @since XMLUnit 2.6.1
 */
public class MultipleNodeAssert extends FactoryBasedNavigableIterableAssert<MultipleNodeAssert, Iterable<Node>, Node, SingleNodeAssert> {

    interface SingleNodeAssertConsumer {
        void accept(SingleNodeAssert t);
    }

    private MultipleNodeAssert(Iterable<Node> nodes) {
        super(nodes, MultipleNodeAssert.class, new NodeAssertFactory());
    }

    static MultipleNodeAssert create(Object xmlSource, XPathEngine xPathEngine, DocumentBuilderFactory dbf, String xPath) {

        Assertions.assertThat(xPath).isNotBlank();

        Source s = Input.from(xmlSource).build();
        Node root = dbf != null ? Convert.toNode(s, dbf) : Convert.toNode(s);
        Iterable<Node> nodes = xPathEngine.selectNodes(xPath, root);

        return new MultipleNodeAssert(nodes);
    }

    /**
     * Equivalent for {@link #isNotEmpty()}.
     */
    public MultipleNodeAssert exist() {
        return isNotEmpty();
    }

    /**
     * Equivalent for {@link #isEmpty()}.
     */
    public void doNotExist() {
        isEmpty();
    }

    /**
     * Verifies that all the actual nodes have attribute with given name.
     * <p>
     * If the actual nodes iterable is empty, this assertion succeeds as there is no elements to check.
     *
     * @throws AssertionError if the actual nodes iterable is {@code null}.
     * @throws AssertionError if one or more nodes don't have attribute with given name.
     */
    public MultipleNodeAssert haveAttribute(final String attributeName) {
        isNotNull();

        allSatisfy(new SingleNodeAssertConsumer() {
            @Override
            public void accept(SingleNodeAssert singleNodeAssert) {
                singleNodeAssert.hasAttribute(attributeName);
            }
        });

        return this;
    }

    /**
     * Verifies that all the actual nodes have attribute with given name and value.
     * <p>
     * If the actual nodes iterable is empty, this assertion succeeds as there is no elements to check.
     *
     * @throws AssertionError if the actual nodes iterable is {@code null}.
     * @throws AssertionError if one or more nodes don't have attribute with given name and value.
     */
    public MultipleNodeAssert haveAttribute(final String attributeName, final String attributeValue) {
        isNotNull();

        allSatisfy(new SingleNodeAssertConsumer() {
            @Override
            public void accept(SingleNodeAssert singleNodeAssert) {
                singleNodeAssert.hasAttribute(attributeName, attributeValue);
            }
        });

        return this;
    }

    /**
     * Verifies that all the actual nodes don't have attribute with given name.
     * <p>
     * If the actual nodes iterable is empty, this assertion succeeds as there is no elements to check.
     *
     * @throws AssertionError if the actual nodes iterable is {@code null}.
     * @throws AssertionError if any node has attribute with given name.
     */
    public MultipleNodeAssert doNotHaveAttribute(final String attributeName) {
        isNotNull();

        allSatisfy(new SingleNodeAssertConsumer() {
            @Override
            public void accept(SingleNodeAssert singleNodeAssert) {
                singleNodeAssert.doesNotHaveAttribute(attributeName);
            }
        });

        return this;
    }

    /**
     * Verifies that all the actual nodes don't have attribute with given name and value.
     * <p>
     * If the actual nodes iterable is empty, this assertion succeeds as there is no elements to check.
     *
     * @throws AssertionError if the actual nodes iterable is {@code null}.
     * @throws AssertionError if any node has attribute with given name and value.
     */
    public MultipleNodeAssert doNotHaveAttribute(final String attributeName, final String attributeValue) {
        isNotNull();

        allSatisfy(new SingleNodeAssertConsumer() {
            @Override
            public void accept(SingleNodeAssert singleNodeAssert) {
                singleNodeAssert.doesNotHaveAttribute(attributeName, attributeValue);
            }
        });

        return this;
    }

    private void allSatisfy(SingleNodeAssertConsumer consumer) {
        int index = 0;
        for (Node node : actual) {
            final SingleNodeAssert singleNodeAssert = toAssert(node, navigationDescription("check node at index " + index));
            consumer.accept(singleNodeAssert);
            index++;
        }
    }
}
