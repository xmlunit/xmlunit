package org.xmlunit.assertj;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;


public class ExpectedException implements TestRule {
    private final org.junit.rules.ExpectedException delegate = org.junit.rules.ExpectedException.none();

    public static ExpectedException none() {
        return new ExpectedException();
    }

    private ExpectedException() {}

    @Override
    public Statement apply(Statement base, Description description) {
        return delegate.apply(base, description);
    }

    public void expectAssertionError(String message) {
        expect(AssertionError.class, message);
    }

    void expect(Class<? extends Throwable> type, String message) {
        delegate.expect(type);
        delegate.expectMessage(message);
    }
}
