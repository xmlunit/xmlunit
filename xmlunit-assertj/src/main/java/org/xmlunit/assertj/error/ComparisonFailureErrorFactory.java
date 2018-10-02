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
package org.xmlunit.assertj.error;

import org.assertj.core.description.Description;
import org.assertj.core.error.AssertionErrorFactory;
import org.assertj.core.internal.Failures;
import org.assertj.core.presentation.Representation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @since XMLUnit 2.6.1
 */
abstract class ComparisonFailureErrorFactory implements AssertionErrorFactory {

    private static Constructor<?> comparisonFailureConstructor;

    private static final String EXPECTED_BUT_WAS_MESSAGE = "%nExpecting:%n <%s>%nto be equal to:%n <%s>%nbut was not.";

    abstract String getMessage();

    abstract String getExpected();

    abstract String getActual();

    /**
     * {@inheritDoc}
     * Create <pre>org.junit.ComparisonFailure</pre> if possible.
     */
    @Override
    public AssertionError newAssertionError(Description d, Representation representation) {
        AssertionError assertionError = getComparisonFailureInstance();
        if (assertionError != null) {
            return assertionError;
        }

        String message = String.format(EXPECTED_BUT_WAS_MESSAGE, getActual(), getExpected());
        return Failures.instance().failure(message);
    }

    private AssertionError getComparisonFailureInstance() {
        Constructor<?> constructor = getComparisonFailureConstructor();
        if (constructor != null) {
            try {
                Object o = constructor.newInstance(getMessage(), getExpected(), getActual());
                if (o instanceof AssertionError) return (AssertionError) o;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
            }
        }
        return null;
    }

    private static Constructor<?> getComparisonFailureConstructor() {
        if (comparisonFailureConstructor == null) {
            try {
                Class<?> targetType = Class.forName("org.junit.ComparisonFailure");
                comparisonFailureConstructor = targetType.getConstructor(String.class, String.class, String.class);
            } catch (ClassNotFoundException | NoSuchMethodException ignored) {
            }
        }

        return comparisonFailureConstructor;
    }
}
