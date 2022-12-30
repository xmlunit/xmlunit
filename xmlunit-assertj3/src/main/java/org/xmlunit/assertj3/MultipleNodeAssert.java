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
package org.xmlunit.assertj3;

import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.FactoryBasedNavigableIterableAssert;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.description.Description;
import org.w3c.dom.Node;
import org.xmlunit.xpath.XPathEngine;

import java.util.ArrayList;
import java.util.List;

import static org.xmlunit.assertj3.AssertionsAdapter.withAssertInfo;
import static org.xmlunit.assertj3.error.ElementsShouldSatisfy.UnsatisfiedRequirement;
import static org.xmlunit.assertj3.error.ElementsShouldSatisfy.elementsShouldSatisfy;
import static org.xmlunit.assertj3.error.ShouldAnyNodeHaveXPath.shouldAnyNodeHaveXPath;

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
 *
 * @since XMLUnit 2.8.1
 */
public class MultipleNodeAssert extends FactoryBasedNavigableIterableAssert<MultipleNodeAssert, Iterable<Node>, Node, SingleNodeAssert> {

    interface SingleNodeAssertConsumer {
        void accept(SingleNodeAssert t);
    }

    private MultipleNodeAssert(Iterable<Node> nodes, XPathEngine engine) {
        super(nodes, MultipleNodeAssert.class, new NodeAssertFactory(engine));
    }

    static MultipleNodeAssert create(Object xmlSource, String xPath, XmlAssertConfig config) {

        AssertionsAdapter.assertThat(xPath, config.info).isNotBlank();

        final XPathEngine engine = XPathEngineFactory.create(config);
        Node root = NodeUtils.parseSource(xmlSource, config);
        Iterable<Node> nodes = engine.selectNodes(xPath, root);

        MultipleNodeAssert multipleNodeAssert =
            withAssertInfo(new MultipleNodeAssert(nodes, engine), config.info);
        if (!multipleNodeAssert.info.hasDescription()) {
            multipleNodeAssert.info.description("XPath \"%s\" evaluated to node set", xPath);
        }

        return multipleNodeAssert;
    }

    /**
     * Equivalent for {@link #isNotEmpty()}.
     * @return this
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
     * @param attributeName name of the expected attribute
     * @return this
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
     * @param attributeName name of the expected attribute
     * @param attributeValue expected attribute value
     * @return this
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
     * @param attributeName name of the expected attribute
     * @return this
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
     * @param attributeName name of the expected attribute
     * @param attributeValue expected attribute value
     * @return this
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

    /**
     * Verifies that any of actual nodes has given {@code xPath}.
     *
     * @throws AssertionError if the actual nodes iterable is {@code null}.
     * @throws AssertionError if all nodes don't have xpath.
     * @since XMLUnit 2.6.4
     * @return this
     * @param xPath XPath expression to test
     */
    public MultipleNodeAssert containsAnyNodeHavingXPath(String xPath) {

        isNotNull();

        int index = 0;
        for (Node node : actual) {
            final SingleNodeAssert singleNodeAssert = toAssert(node, navigationDescription("check node at index " + index));
            if (!singleNodeAssert.isNodeSetEmpty(xPath)) {
                return this;
            }
            index++;
        }

        throwAssertionError(shouldAnyNodeHaveXPath(xPath));
        return null; //fix compile issue
    }

    /**
     * Verifies that all of actual nodes have given {@code xPath}.
     *
     * @throws AssertionError if the actual nodes iterable is {@code null}.
     * @throws AssertionError if some node doesn't have xpath.
     * @since XMLUnit 2.6.4
     * @return this
     * @param xPath XPath expression to test
     */
    public MultipleNodeAssert containsAllNodesHavingXPath(final String xPath) {
        isNotNull();

        allSatisfy(new SingleNodeAssertConsumer() {
            @Override
            public void accept(SingleNodeAssert singleNodeAssert) {
                singleNodeAssert.hasXPath(xPath);
            }
        });

        return this;
    }

    /**
     * Extracting values of given node's attribute.
     * If a node doesn't have the attribute then {@code null} value is return.
     *
     * @throws AssertionError if the actual nodes iterable is {@code null}.
     * @since XMLUnit 2.6.4
     * @param attribute name of the attribute
     * @return list assert of attribute values
     */
    public AbstractListAssert<?, List<? extends String>, String, ObjectAssert<String>> extractingAttribute(String attribute) {
        isNotNull();

        List<String> values = new ArrayList<>();

        for (Node node : actual) {
            values.add(NodeUtils.attributeValue(node, attribute));
        }

        String extractedDescription = String.format("Extracted attribute: %s", attribute);
        String description = Description.mostRelevantDescription(this.info.description(), extractedDescription);

        return asListAssert(values).as(description);
    }

    /**
     * Extracting text content of given nodes.
     * If a node doesn't have the text then an empty string is returned.
     *
     * @throws AssertionError if the actual nodes iterable is {@code null}.
     * @since XMLUnit 2.8.0
     * @return list assert of text content
     */
    public AbstractListAssert<?, List<? extends String>, String, ObjectAssert<String>> extractingText() {
        isNotNull();

        List<String> values = new ArrayList<>();

        for (Node node : actual) {
            String textContent = node.getTextContent();
            if (textContent != null) {
                textContent = textContent.trim();
            }
            values.add(textContent);
        }

        String extractedDescription = "Extracted text content";
        String description = Description.mostRelevantDescription(this.info.description(), extractedDescription);

        return asListAssert(values).as(description);
    }


    private void allSatisfy(SingleNodeAssertConsumer consumer) {
        int index = 0;
        final List<UnsatisfiedRequirement> errors = new ArrayList<>();
        for (Node node : actual) {
            final SingleNodeAssert singleNodeAssert = toAssert(node, navigationDescription("check node at index " + index));
            try {
                consumer.accept(singleNodeAssert);
            } catch (AssertionError ae) {
                errors.add(new UnsatisfiedRequirement(node, ae.getMessage()));
            }
            index++;
        }
        if (!errors.isEmpty()) {
            throwAssertionError(elementsShouldSatisfy(actual, errors, info));
        }
    }

    private AbstractListAssert<?, List<? extends String>, String, ObjectAssert<String>>
        asListAssert(final List<String> values) {
        AbstractListAssert<?, List<? extends String>, String, ObjectAssert<String>> a =
            newListAssertInstance(values);
        return withAssertInfo(a, info);
    }
}
