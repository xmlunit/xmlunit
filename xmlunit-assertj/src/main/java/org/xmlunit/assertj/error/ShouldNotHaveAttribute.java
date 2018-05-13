package org.xmlunit.assertj.error;

import org.assertj.core.error.BasicErrorMessageFactory;
import org.assertj.core.error.ErrorMessageFactory;

public class ShouldNotHaveAttribute extends BasicErrorMessageFactory {

    public static ErrorMessageFactory shouldNotHaveAttribute(String nodeName, String attributeName) {
        return new ShouldNotHaveAttribute(nodeName, attributeName, null);
    }

    public static ErrorMessageFactory shouldNotHaveAttributeWithValue(String nodeName, String attributeName, String attributeValue) {
        return new ShouldNotHaveAttribute(nodeName, attributeName, attributeValue);
    }

    private ShouldNotHaveAttribute(String nodeName, String attributeName, String attributeValue) {
        super("\nExpecting:\n <%s>\nnot to have attribute:\n <%s>" +
                        (attributeValue != null ? "\nwith value:\n <%s>" : ""),
                unquotedString(nodeName),
                unquotedString(attributeName),
                (attributeValue != null ? unquotedString(attributeValue) : null));
    }
}
