package org.xmlunit.assertj;

import org.assertj.core.error.BasicErrorMessageFactory;
import org.assertj.core.error.ErrorMessageFactory;
import org.w3c.dom.Node;

class ShouldHaveAttribute extends BasicErrorMessageFactory {

    static ErrorMessageFactory shouldHaveAttribute(Node node, String attributeName) {
        return new ShouldHaveAttribute(node, attributeName, null);
    }

    static ErrorMessageFactory shouldHaveAttributeWithValue(Node node, String attributeName, String attributeValue) {
        return new ShouldHaveAttribute(node, attributeName, attributeValue);
    }

    private ShouldHaveAttribute(Node node, String attributeName, String attributeValue) {
        super("\nExpecting:\n <%s>\nto have attribute:\n <%s>" +
                        (attributeValue != null ? "\n with value:\n <%s>" : ""),
                node, attributeName, attributeValue);
    }
}
