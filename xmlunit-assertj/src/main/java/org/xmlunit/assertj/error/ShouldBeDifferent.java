package org.xmlunit.assertj.error;

import org.assertj.core.error.BasicErrorMessageFactory;

public class ShouldBeDifferent extends BasicErrorMessageFactory {

    public static ShouldBeDifferent shouldBeDifferent(String controlSystemId, String testSystemId) {

        return new ShouldBeDifferent(controlSystemId != null ? controlSystemId : "control instance",
                testSystemId != null ? testSystemId : "test instance");
    }

    private ShouldBeDifferent(String controlSystemId, String testSystemId) {
        super("%nExpecting:%n <%s> and <%s> to be different", unquotedString(controlSystemId), unquotedString(testSystemId));
    }
}
