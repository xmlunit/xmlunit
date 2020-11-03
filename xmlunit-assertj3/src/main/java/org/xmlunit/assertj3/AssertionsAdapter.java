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
package org.xmlunit.assertj3;

import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractCharSequenceAssert;
import org.assertj.core.api.AbstractDoubleAssert;
import org.assertj.core.api.AbstractIntegerAssert;
import org.assertj.core.api.AbstractObjectArrayAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.BooleanAssert;
import org.assertj.core.api.DoubleAssert;
import org.assertj.core.api.IntegerAssert;
import org.assertj.core.api.ObjectArrayAssert;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.api.StringAssert;

/**
 * Class that is proxy for AssertJ assertions used by org.xmlunit.assertj.*Assert classes.
 *
 * <p>XMLUnit AssertJ tends to break binary compatibility of the
 * assertThat methods from time to time so we shield ourselves from
 * these changes a bit.</p>
 *
 * @see <a href="https://github.com/xmlunit/xmlunit/issues/135">GitHub issue #135</a>
 * @since XMLUnit 2.8.1
 */
final class AssertionsAdapter {

    private AssertionsAdapter() {
    }

    static <T> AbstractObjectAssert<?, T> assertThat(T actual) {
        return new ObjectAssert<>(actual);
    }

    static <T> AbstractObjectArrayAssert<?, T> assertThat(T[] actual) {
        return new ObjectArrayAssert<>(actual);
    }

    static AbstractCharSequenceAssert<?, String> assertThat(String actual) {
        return new StringAssert(actual);
    }

    static AbstractIntegerAssert<?> assertThat(int actual) {
        return new IntegerAssert(actual);
    }

    static AbstractDoubleAssert<?> assertThat(double actual) {
        return new DoubleAssert(actual);
    }

    static AbstractBooleanAssert<?> assertThat(boolean actual) {
        return new BooleanAssert(actual);
    }
}
