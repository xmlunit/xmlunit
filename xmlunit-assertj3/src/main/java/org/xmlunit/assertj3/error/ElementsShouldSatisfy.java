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
package org.xmlunit.assertj3.error;

import java.util.List;

import org.assertj.core.api.AssertionInfo;
import org.assertj.core.error.BasicErrorMessageFactory;
import org.assertj.core.error.ErrorMessageFactory;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

/**
 * Copied subset of {@link org.assertj.core.error.ElementsShouldSatisfy} as AssertJ has moved the nested
 * UnsatisfiedRequirement class to a top level class, breaking backwards compatibility.
 *
 * @since XMLUnit 2.8.3
 */
public class ElementsShouldSatisfy extends BasicErrorMessageFactory {

    /**
     * Creates ErrorMessageFactory.
     * @param actual the actual value
     * @param elementsNotSatisfyingRestrictions elements that don't satisfy assertion
     * @param info assertion information
     * @return ErrorMessageFactory
     */
    public static ErrorMessageFactory elementsShouldSatisfy(Object actual,
        List<UnsatisfiedRequirement> elementsNotSatisfyingRestrictions, AssertionInfo info) {
        return new ElementsShouldSatisfy(actual, elementsNotSatisfyingRestrictions, info);
    }

    private ElementsShouldSatisfy(Object actual,
        List<UnsatisfiedRequirement> elementsNotSatisfyingRestrictions, AssertionInfo info) {
        super(format("%n" +
                     "Expecting all elements of:%n" +
                     "  %s%n" +
                     "to satisfy given requirements, but these elements did not:%n%n"
                     + describeErrors(elementsNotSatisfyingRestrictions, info),
                     actual));
    }

    private static String describeErrors(List<UnsatisfiedRequirement> elementsNotSatisfyingRequirements, AssertionInfo info) {
        return escapePercent(elementsNotSatisfyingRequirements.stream()
            .map(ur -> ur.describe(info))
            .collect(joining(format("%n%n"))));
    }

    private static String escapePercent(String s) {
        return s.replace("%", "%%");
    }

    /**
     * Holds the element not satisfing a requirement and the error message.
     */
    public static class UnsatisfiedRequirement {
        private final Object elementNotSatisfyingRequirements;
        private final String errorMessage;

        /**
         * @param elementNotSatisfyingRequirements the element not satsfying the requirement
         * @param errorMessage the error message
         */
        public UnsatisfiedRequirement(Object elementNotSatisfyingRequirements, String errorMessage) {
            this.elementNotSatisfyingRequirements = elementNotSatisfyingRequirements;
            this.errorMessage = errorMessage;
        }

        /**
         * @param info assertion info
         * @return the formatted description
         */
        public String describe(AssertionInfo info) {
            return format("%s%nerror: %s", info.representation().toStringOf(elementNotSatisfyingRequirements), errorMessage);
        }

        @Override
        public String toString() {
            return format("%s %s", elementNotSatisfyingRequirements, errorMessage);
        }
    }
}
