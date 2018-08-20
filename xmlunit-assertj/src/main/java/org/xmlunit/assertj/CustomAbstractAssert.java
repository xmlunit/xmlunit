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
import org.assertj.core.error.AssertionErrorFactory;
import org.assertj.core.internal.Failures;

import java.util.List;

import static org.assertj.core.util.Lists.newArrayList;

/**
 * AbstractAssert allow only to throw errors extending ErrorMessageFactory.
 * CustomAbstractAssert allow to throw errors that based on AssertionErrorFactory.
 *
 * @see AbstractAssert#throwAssertionError(org.assertj.core.error.ErrorMessageFactory)
 * @see org.xmlunit.assertj.error.ComparisonFailureErrorFactory
 * @since XMLUnit 2.6.1
 */
abstract class CustomAbstractAssert<SELF extends CustomAbstractAssert<SELF, ACTUAL>, ACTUAL> extends AbstractAssert<SELF, ACTUAL> {

    private static final String ORG_XMLUNIT_ASSERTJ_ERROR = "org.xmlunit.assertj.error";

    CustomAbstractAssert(ACTUAL actual, Class<?> selfType) {
        super(actual, selfType);
    }

    void throwAssertionError(AssertionErrorFactory assertionErrorFactory) {
        AssertionError assertionError = assertionErrorFactory.newAssertionError(info.description(), info.representation());
        Failures.instance().removeAssertJRelatedElementsFromStackTraceIfNeeded(assertionError);
        removeCustomAssertRelatedElementsFromStackTraceIfNeeded(assertionError);
        throw assertionError;
    }

    private void removeCustomAssertRelatedElementsFromStackTraceIfNeeded(AssertionError assertionError) {

        if (!Failures.instance().isRemoveAssertJRelatedElementsFromStackTrace()) return;

        List<StackTraceElement> filtered = newArrayList(assertionError.getStackTrace());
        for (StackTraceElement element : assertionError.getStackTrace()) {
            if (isElementOfCustomAssert(element)) {
                filtered.remove(element);
            }
        }
        StackTraceElement[] newStackTrace = filtered.toArray(new StackTraceElement[0]);
        assertionError.setStackTrace(newStackTrace);
    }

    private boolean isElementOfCustomAssert(StackTraceElement stackTraceElement) {

        Class<?> currentAssertClass = getClass();
        while (currentAssertClass != AbstractAssert.class) {
            if (stackTraceElement.getClassName().equals(currentAssertClass.getName())) {
                return true;
            }
            if (stackTraceElement.getClassName().contains(ORG_XMLUNIT_ASSERTJ_ERROR)) {
                return true;
            }
            currentAssertClass = currentAssertClass.getSuperclass();
        }
        return false;
    }
}
