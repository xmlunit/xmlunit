package org.xmlunit.assertj;

import org.assertj.core.api.AbstractAssert;
import org.w3c.dom.Node;
import org.xmlunit.util.Nodes;
import org.xmlunit.xpath.XPathEngine;

import javax.xml.namespace.QName;
import java.util.Map;

import static org.xmlunit.assertj.ShouldHaveAttribute.shouldHaveAttribute;
import static org.xmlunit.assertj.ShouldHaveAttribute.shouldHaveAttributeWithValue;

public class NodeAssert extends AbstractAssert<NodeAssert, Node> {

    private final XPathEngine xPathEngine;

    NodeAssert(Node node, XPathEngine xPathEngine) {
        super(node, NodeAssert.class);
        this.xPathEngine = xPathEngine;
    }

    public NodeAssert hasAttribute(String attributeName) {
        isNotNull();
        final Map.Entry<QName, String> entry = attributeForName(attributeName);
        if(entry == null) {
            throwAssertionError(shouldHaveAttribute(actual.getNodeName(), attributeName));
        }
        return this;
    }

    public NodeAssert hasAttribute(String attributeName, String attributeValue) {
        isNotNull();

        final Map.Entry<QName, String> attribute = attributeForName(attributeName);
        if (attribute == null || !attribute.getValue().equals(attributeValue)) {
            throwAssertionError(shouldHaveAttributeWithValue(actual.getNodeName(), attributeName, attributeValue));
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
