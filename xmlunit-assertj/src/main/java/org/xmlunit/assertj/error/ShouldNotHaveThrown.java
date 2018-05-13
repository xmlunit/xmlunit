package org.xmlunit.assertj.error;

import org.assertj.core.error.BasicErrorMessageFactory;
import org.assertj.core.error.ErrorMessageFactory;

import static org.assertj.core.util.Throwables.getStackTrace;

public class ShouldNotHaveThrown extends BasicErrorMessageFactory {

    public static ErrorMessageFactory shouldNotHaveThrown(Throwable throwable) {
        return new ShouldNotHaveThrown(throwable);
    }

    private ShouldNotHaveThrown(Throwable throwable) {
        super("%nExpecting code not to raise a throwable but caught%n  <%s>", getStackTrace(throwable));
    }
}
