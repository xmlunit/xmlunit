package org.xmlunit.assertj.error;

import org.assertj.core.error.BasicErrorMessageFactory;
import org.assertj.core.error.ErrorMessageFactory;

public class ShouldNotHaveAttribute extends BasicErrorMessageFactory {

    public static ErrorMessageFactory shouldNotHaveAttribute(String nodeName, String attributeName) {
        return new ShouldNotHaveAttribute(nodeName, attributeName);
    }

    public static ErrorMessageFactory shouldNotHaveAttributeWithValue(String nodeName, String attributeName, String attributeValue) {
        return new ShouldNotHaveAttribute(nodeName, attributeName, attributeValue);
    }

    private ShouldNotHaveAttribute(String nodeName, String attributeName) {
        super("%nExpecting:%n <%s>%nnot to have attribute:%n <%s>",
                unquotedString(nodeName),
                unquotedString(attributeName));
    }

    private ShouldNotHaveAttribute(String nodeName, String attributeName, String attributeValue) {
        super("%nExpecting:%n <%s>%nnot to have attribute:%n <%s>%nwith value:%n <%s>",
                unquotedString(nodeName),
                unquotedString(attributeName),
                unquotedString(attributeValue));
    }
}
