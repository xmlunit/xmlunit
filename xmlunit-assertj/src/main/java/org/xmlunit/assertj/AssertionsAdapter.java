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
package org.xmlunit.assertj;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractCharSequenceAssert;
import org.assertj.core.api.AbstractDoubleAssert;
import org.assertj.core.api.AbstractIntegerAssert;
import org.assertj.core.api.AbstractObjectArrayAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.BooleanAssert;
import org.assertj.core.api.DoubleAssert;
import org.assertj.core.api.IntegerAssert;
import org.assertj.core.api.ObjectArrayAssert;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.api.StringAssert;
import org.assertj.core.api.WritableAssertionInfo;

/**
 * Class that is proxy for AssertJ assertions used by org.xmlunit.assertj.*Assert classes.
 * <p>
 * XMLUnit AssertJ module is compatibility with Java 7, so it use use AssertJ 2.x version.
 * In AssertJ 3.9.1 was introduced binary incompatibility,
 * so using latest AssertJ version may cause {@link java.lang.NoSuchMethodError}.
 * Using AssertionsAdapter allows to provide custom implementation of <i>assertThat</i> methods in such cases.
 *
 * @see <a href="https://github.com/xmlunit/xmlunit/issues/135">GitHub issude #135</a>
 * @since XMLUnit 2.6.2
 */
final class AssertionsAdapter {

    private AssertionsAdapter() {
    }

    static <T> AbstractObjectAssert<?, T> assertThat(T actual) {
        return new ObjectAssert<>(actual);
    }

    static <T> AbstractObjectAssert<?, T> assertThat(T actual, AssertionInfo info) {
        return  withAssertInfo(assertThat(actual), info);
    }

    static <T> AbstractObjectArrayAssert<?, T> assertThat(T[] actual) {
        return new ObjectArrayAssert<>(actual);
    }

    static <T> AbstractObjectArrayAssert<?, T> assertThat(T[] actual, AssertionInfo info) {
        return  withAssertInfo(assertThat(actual), info);
    }

    static AbstractCharSequenceAssert<?, String> assertThat(String actual) {
        return new StringAssert(actual);
    }

    static AbstractCharSequenceAssert<?, String> assertThat(String actual, AssertionInfo info) {
        return  withAssertInfo(assertThat(actual), info);
    }

    static AbstractIntegerAssert<?> assertThat(int actual) {
        return new IntegerAssert(actual);
    }

    static AbstractIntegerAssert<?> assertThat(int actual, AssertionInfo info) {
        return  withAssertInfo(assertThat(actual), info);
    }

    static AbstractDoubleAssert<?> assertThat(double actual) {
        return new DoubleAssert(actual);
    }

    static AbstractDoubleAssert<?> assertThat(double actual, AssertionInfo info) {
        return  withAssertInfo(assertThat(actual), info);
    }

    static AbstractBooleanAssert<?> assertThat(boolean actual) {
        return new BooleanAssert(actual);
    }

    static AbstractBooleanAssert<?> assertThat(boolean actual, AssertionInfo info) {
        return  withAssertInfo(assertThat(actual), info);
    }

    static <T extends AbstractAssert<?, ?>> T withAssertInfo(T assertion, AssertionInfo info) {
        WritableAssertionInfo destInfo = assertion.getWritableAssertionInfo();
        destInfo.overridingErrorMessage(info.overridingErrorMessage());
        destInfo.description(info.description());
        destInfo.useRepresentation(info.representation());

        return assertion;
    }
}
