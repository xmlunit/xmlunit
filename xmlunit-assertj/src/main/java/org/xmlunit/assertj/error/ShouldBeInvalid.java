package org.xmlunit.assertj.error;

import org.assertj.core.error.BasicErrorMessageFactory;

public class ShouldBeInvalid extends BasicErrorMessageFactory {

    public static ShouldBeInvalid shouldBeInvalid(String systemId) {

        return new ShouldBeInvalid(systemId != null ? systemId : "instance");
    }

    private ShouldBeInvalid(String systemId) {
        super("%nExpecting:%n <%s>%nto be invalid", unquotedString(systemId));
    }
}
