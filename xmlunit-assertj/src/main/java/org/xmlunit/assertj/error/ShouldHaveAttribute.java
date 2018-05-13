package org.xmlunit.assertj.error;

import org.assertj.core.error.BasicErrorMessageFactory;
import org.assertj.core.error.ErrorMessageFactory;

public class ShouldHaveAttribute extends BasicErrorMessageFactory {

    public static ErrorMessageFactory shouldHaveAttribute(String nodeName, String attributeName) {
        return new ShouldHaveAttribute(nodeName, attributeName, null);
    }

    public static ErrorMessageFactory shouldHaveAttributeWithValue(String nodeName, String attributeName, String attributeValue) {
        return new ShouldHaveAttribute(nodeName, attributeName, attributeValue);
    }

    private ShouldHaveAttribute(String nodeName, String attributeName, String attributeValue) {
        super("\nExpecting:\n <%s>\nto have attribute:\n <%s>" +
                        (attributeValue != null ? "\nwith value:\n <%s>" : ""),
                unquotedString(nodeName),
                unquotedString(attributeName),
                (attributeValue != null ? unquotedString(attributeValue) : null));
    }
}
