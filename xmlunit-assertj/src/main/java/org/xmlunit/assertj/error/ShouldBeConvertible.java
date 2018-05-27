package org.xmlunit.assertj.error;

import org.assertj.core.error.BasicErrorMessageFactory;

public class ShouldBeConvertible extends BasicErrorMessageFactory {

    public static ShouldBeConvertible shouldBeConvertible(String value, String targetType) {

        return new ShouldBeConvertible(value,targetType);
    }

    private ShouldBeConvertible(String value, String targetType) {
        super("%nExpecting:%n <%s>%nto be convertible to%n <%s>",
                unquotedString(value),
                unquotedString(targetType));
    }
}
