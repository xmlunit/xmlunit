package org.xmlunit.matchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.math.BigDecimal;

public abstract class TypeMatcher<T> extends TypeSafeMatcher<String> {

    private final Class<T> clazz;
    private final Matcher<? extends T> matcher;

    private TypeMatcher(Class<T> clazz, Matcher<? extends T> matcher) {
        super(String.class);
        this.clazz = clazz;
        this.matcher = matcher;
    }

    @Override
    protected boolean matchesSafely(String item) {
        T value = convertSafe(item);
        return matcher.matches(value);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(" a string converted to ")
                .appendText(clazz.getName());
    }

    private T convertSafe(String item) {
        if (item == null) {
            return null;
        }
        item = item.trim();
        if (item.length() < 1) {
            return null;
        }
        return convert(item);
    }

    protected abstract T convert(String item);

    public static TypeMatcher<BigDecimal> asBigDecimal(Matcher<? extends BigDecimal> matcher) {
        return new BigDecimalTypeMatcher(matcher);
    }

    public static TypeMatcher<Double> asDouble(Matcher<? extends Double> matcher) {
        return new DoubleTypeMatcher(matcher);
    }

    public static TypeMatcher<Integer> asInt(Matcher<? extends Integer> matcher) {
        return new IntegerTypeMatcher(matcher);
    }

    private static class BigDecimalTypeMatcher extends TypeMatcher<BigDecimal> {

        private BigDecimalTypeMatcher(Matcher<? extends BigDecimal> matcher) {
            super(BigDecimal.class, matcher);
        }

        @Override
        protected BigDecimal convert(String item) {
            return new BigDecimal(item);
        }
    }

    private static class DoubleTypeMatcher extends TypeMatcher<Double> {

        private DoubleTypeMatcher(Matcher<? extends Double> matcher) {
            super(Double.class, matcher);
        }

        @Override
        protected Double convert(String item) {
            return Double.valueOf(item);
        }
    }

    private static class IntegerTypeMatcher extends TypeMatcher<Integer> {

        private IntegerTypeMatcher(Matcher<? extends Integer> matcher) {
            super(Integer.class, matcher);
        }

        @Override
        protected Integer convert(String item) {
            return Integer.valueOf(item);
        }
    }
}
