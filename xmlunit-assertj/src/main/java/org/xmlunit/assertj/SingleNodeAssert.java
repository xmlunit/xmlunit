package org.xmlunit.assertj;

import org.assertj.core.api.AbstractAssert;
import org.w3c.dom.Node;
import org.xmlunit.util.Nodes;

import javax.xml.namespace.QName;
import java.util.Map;

import static org.xmlunit.assertj.error.ShouldHaveAttribute.shouldHaveAttribute;
import static org.xmlunit.assertj.error.ShouldHaveAttribute.shouldHaveAttributeWithValue;
import static org.xmlunit.assertj.error.ShouldNotHaveAttribute.shouldNotHaveAttribute;
import static org.xmlunit.assertj.error.ShouldNotHaveAttribute.shouldNotHaveAttributeWithValue;

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
 */
public class SingleNodeAssert extends AbstractAssert<SingleNodeAssert, Node> {

    SingleNodeAssert(Node node) {
        super(node, SingleNodeAssert.class);
    }

    /**
     * Verifies that node has attribute with given name.
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
     * Verifies that node has attribute with given name and value.
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
     * Verifies that node has not attribute with given name.
     * 
     * @throws AssertionError if the actual node is {@code null}.
     * @throws AssertionError if node has attribute with given name.
     */
    public SingleNodeAssert hasNotAttribute(String attributeName) {
        isNotNull();
        
        final Map.Entry<QName, String> entry = attributeForName(attributeName);
        if (entry != null) {
            throwAssertionError(shouldNotHaveAttribute(actual.getNodeName(), attributeName));
        }
        return this;
    }

    /**
     * Verifies that node has not attribute with given name and value.
     * 
     * @throws AssertionError if the actual node is {@code null}.
     * @throws AssertionError if node has attribute with given name and value.
     */
    public SingleNodeAssert hasNotAttribute(String attributeName, String attributeValue) {
        isNotNull();

        final Map.Entry<QName, String> attribute = attributeForName(attributeName);
        if (attribute != null && attribute.getValue().equals(attributeValue)) {
            throwAssertionError(shouldNotHaveAttributeWithValue(actual.getNodeName(), attributeName, attributeValue));
        }

        return this;
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
