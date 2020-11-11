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

import org.assertj.core.api.AbstractAssert;

/**
 * Ensures XMLUnit's stack trace lines get removed from AssertionError's stack traces if those of AssertJ are removed.
 * @since XMLUnit 2.8.1
 */
abstract class CustomAbstractAssert<SELF extends CustomAbstractAssert<SELF, ACTUAL>, ACTUAL> extends AbstractAssert<SELF, ACTUAL> {

    private static final String ORG_XMLUNIT_ASSERTJ_ERROR = "org.xmlunit.assertj3.error";

    CustomAbstractAssert(ACTUAL actual, Class<?> selfType) {
        super(actual, selfType);
    }

    @Override
    protected boolean isElementOfCustomAssert(final StackTraceElement stackTraceElement) {
        return stackTraceElement.getClassName().contains(ORG_XMLUNIT_ASSERTJ_ERROR)
            || super.isElementOfCustomAssert(stackTraceElement);
    }

}
