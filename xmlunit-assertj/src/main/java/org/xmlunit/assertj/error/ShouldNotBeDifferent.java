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

import org.xmlunit.diff.Comparison;
import org.xmlunit.diff.ComparisonFormatter;
import org.xmlunit.diff.DefaultComparisonFormatter;

/**
 * @since XMLUnit 2.6.1
 */
public class ShouldNotBeDifferent extends ComparisonFailureErrorFactory {

    private static final ComparisonFormatter comparisonFormatter = new DefaultComparisonFormatter();

    private final String reason;
    private final String controlString;
    private final String testString;

    private ShouldNotBeDifferent(String reason, String controlString, String testString) {
        this.reason = reason;
        this.controlString = controlString;
        this.testString = testString;
    }

    @Override
    String getMessage() {
        return reason;
    }

    @Override
    String getExpected() {
        return controlString;
    }

    @Override
    String getActual() {
        return testString;
    }

    public static ShouldNotBeDifferent shouldNotBeDifferent(String systemId, Comparison comparison, boolean formatXml) {

        return new ShouldNotBeDifferent(createReasonPrefix(systemId, comparison),
                comparisonFormatter.getDetails(comparison.getControlDetails(), comparison.getType(), formatXml),
                comparisonFormatter.getDetails(comparison.getTestDetails(), comparison.getType(), formatXml));
    }

    private static String createReasonPrefix(String systemId, Comparison difference) {
        String description = comparisonFormatter.getDescription(difference);
        String reason;
        if (systemId == null) {
            reason = description;
        } else {
            reason = String.format("In Source '%s' %s", systemId, description);
        }
        return reason;
    }
}
