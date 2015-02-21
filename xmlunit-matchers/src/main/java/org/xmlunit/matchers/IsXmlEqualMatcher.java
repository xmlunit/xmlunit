package org.xmlunit.matchers;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

public class IsXmlEqualMatcher<T> extends TypeSafeMatcher<T> {
    private final T control;

    public IsXmlEqualMatcher(T control) {
        this.control = control;
    }

    @Override
    protected boolean matchesSafely(T test) {
        Diff diff = DiffBuilder.compare(control).withTest(test).build();
        return !diff.hasDifferences();
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(control);
    }

    @Factory
    public static <T> Matcher xmlEqualTo(T operand) {
        return new IsXmlEqualMatcher<T>(operand);
    }
}
