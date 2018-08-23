/*
  This file is licensed to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package org.xmlunit.matchers;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.math.BigDecimal;

/**
 * This Hamcrest {@link Matcher} is base Matcher to verify
 * whether examined string value is convertible to the specified type
 * and whether converted value corresponds to the given value valueMatcher.
 * Examined string value can be evaluation of an XPath expression.
 * <p>
 * Currently {@link BigDecimal}, {@link Double}, {@link Integer} and {@link Boolean} types are supported.
 *
 * <p><b>Simple examples</b></p>
 *
 * <pre>
 *     assertThat("3.0", asDouble(greaterThanOrEqualTo(2.0)));
 *     assertThat("1.0e1", asBigDecimal(equalTo(BigDecimal.TEN)));
 *     assertThat("3", asInt(lessThan(4)));
 *     assertThat("false", asBoolean(equalTo(false)));
 *     assertThat("True", asBoolean(equalTo(true)));
 * </pre>
 *
 * <p><b>Examples with XPath evaluation</b></p>
 *
 * <pre>
 *     String xml = "<fruits>" +
 *             "<fruit name=\"apple\"/>" +
 *             "<fruit name=\"orange\"/>" +
 *             "<fruit name=\"banana\"/>" +
 *             "<fruit name=\"pear\" fresh=\"false\"/>" +
 *             "</fruits>";
 *
 *     assertThat(xml, hasXPath("count(//fruits/fruit)", asDouble(equalTo(4.0))));
 *     assertThat(xml, hasXPath("count(//fruits/fruit)", asBigDecimal(greaterThan(BigDecimal.ONE))));
 *     assertThat(xml, hasXPath("count(//fruits/fruit)", asInt(lessThan(5))));
 *     assertThat(xml, hasXPath("//fruits/fruit[@name=\"pear\"]/@fresh", asBoolean(equalTo(false))));
 * </pre>
 *
 * @param <T> target type
 * @since XMLUnit 2.6.2
 */
public abstract class TypeMatcher<T> extends TypeSafeMatcher<String> {

    private T value;
    private Exception exception;

    private final Class<T> clazz;
    private final Matcher<? extends T> valueMatcher;

    public TypeMatcher(Class<T> clazz, Matcher<? extends T> valueMatcher) {
        super(String.class);
        this.clazz = clazz;
        this.valueMatcher = valueMatcher;
    }

    @Override
    protected boolean matchesSafely(String item) {
        try {
            value = nullSafeConvert(item);
            return valueMatcher.matches(value);
        } catch (Exception e) {
            exception = e;
            return false;
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("string converted to ")
                .appendText(clazz.getName())
                .appendText(" ")
                .appendDescriptionOf(valueMatcher);
    }

    @Override
    protected void describeMismatchSafely(String item, Description mismatchDescription) {
        if (exception != null) {
            mismatchDescription.appendText("failed with ")
                    .appendText(exception.toString());
        } else {
            mismatchDescription.appendText("was ")
                    .appendValue(value);
        }
    }

    private T nullSafeConvert(String item) {
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

    /**
     * Creates a matcher that matches when the examined string is convertible to {@link BigDecimal}
     * and converted value satisfies the specified <code>valueMatcher</code>.
     *
     * <p>For example:</p>
     * <pre>
     *     assertThat("1.0e1", asBigDecimal(equalTo(BigDecimal.TEN)));
     *     assertThat(xml, hasXPath("count(//fruits/fruit)", asBigDecimal(greaterThan(BigDecimal.ONE))));
     * </pre>
     *
     * @param valueMatcher valueMatcher for the converted value
     * @return the BigDecimal matcher
     */
    @Factory
    public static TypeMatcher<BigDecimal> asBigDecimal(Matcher<? extends BigDecimal> valueMatcher) {
        return new BigDecimalTypeMatcher(valueMatcher);
    }

    /**
     * Creates a matcher that matches when the examined string is convertible to {@link Double}
     * and converted value satisfies the specified <code>valueMatcher</code>.
     *
     * <p>For example:</p>
     * <pre>
     *     assertThat("3.0", asDouble(greaterThanOrEqualTo(2.0)));
     *     assertThat(xml, hasXPath("count(//fruits/fruit)", asDouble(equalTo(3.0))));
     * </pre>
     *
     * @param valueMatcher valueMatcher for the converted value
     * @return the Double matcher
     */
    @Factory
    public static TypeMatcher<Double> asDouble(Matcher<? extends Double> valueMatcher) {
        return new DoubleTypeMatcher(valueMatcher);
    }

    /**
     * Creates a matcher that matches when the examined string is convertible to {@link Integer}
     * and converted value satisfies the specified <code>valueMatcher</code>.
     *
     * <p>For example:</p>
     * <pre>
     *     assertThat("3", asInt(lessThan(4)));
     *     assertThat(xml, hasXPath("count(//fruits/fruit)", asInt(lessThan(4))));
     * </pre>
     *
     * @param valueMatcher valueMatcher for the converted value
     * @return the Integer matcher
     */
    @Factory
    public static TypeMatcher<Integer> asInt(Matcher<? extends Integer> valueMatcher) {
        return new IntegerTypeMatcher(valueMatcher);
    }

    /**
     * Creates a matcher that matches when the examined string is convertible to {@link Boolean}
     * and converted value satisfies the specified <code>valueMatcher</code>.
     *
     * <p>For example:</p>
     * <pre>
     *     assertThat("false", asBoolean(equalTo(false)));
     *     assertThat(xml, hasXPath("//fruits/fruit[@name=\"apple\"]/@fresh", asBoolean(equalTo(true))));
     * </pre>
     *
     * @param valueMatcher valueMatcher for the converted value
     * @return the Boolean matcher
     */
    @Factory
    public static TypeMatcher<Boolean> asBoolean(Matcher<? extends Boolean> valueMatcher) {
        return new BooleanTypeMatcher(valueMatcher);
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

    private static class BooleanTypeMatcher extends TypeMatcher<Boolean> {

        private BooleanTypeMatcher(Matcher<? extends Boolean> matcher) {
            super(Boolean.class, matcher);
        }

        @Override
        protected Boolean convert(String item) {
            item = item.toLowerCase();
            if (item.equals("true")) {
                return true;
            }
            if (item.equals("false")) {
                return false;
            }
            throw new IllegalArgumentException("\"" + item + "\" is not a boolean value");
        }
    }
}
