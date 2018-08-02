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
public class ShouldBeSimilar extends ComparisonFailureErrorFactory {

    private static final ComparisonFormatter comparisonFormatter = new DefaultComparisonFormatter();

    private final String reason;
    private final String controlString;
    private final String testString;

    private ShouldBeSimilar(String reason, String controlString, String testString) {
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

    public static ShouldBeSimilar shouldBeIdentical(String controlSystemId, String testSystemId, Comparison comparison, boolean formatXml) {

        return new ShouldBeSimilar(createReasonPrefix(controlSystemId, testSystemId, "identical", comparison),
                comparisonFormatter.getDetails(comparison.getControlDetails(), comparison.getType(), formatXml),
                comparisonFormatter.getDetails(comparison.getTestDetails(), comparison.getType(), formatXml));
    }

    public static ShouldBeSimilar shouldBeSimilar(String controlSystemId, String testSystemId, Comparison comparison, boolean formatXml) {

        return new ShouldBeSimilar(createReasonPrefix(controlSystemId, testSystemId, "similar", comparison),
                comparisonFormatter.getDetails(comparison.getControlDetails(), comparison.getType(), formatXml),
                comparisonFormatter.getDetails(comparison.getTestDetails(), comparison.getType(), formatXml));
    }

    private static String createReasonPrefix(String controlSystemId, String testSystemId, String type, Comparison difference) {

        controlSystemId = controlSystemId != null ? controlSystemId : "control instance";
        testSystemId = testSystemId != null ? testSystemId : "test instance";

        String description = comparisonFormatter.getDescription(difference);
        return String.format("%nExpecting:%n <%s> and <%s> to be %s%n%s", controlSystemId, testSystemId, type, description);
    }
}
