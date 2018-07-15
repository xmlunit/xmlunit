package org.xmlunit.assertj.error;

import org.xmlunit.diff.Comparison;
import org.xmlunit.diff.ComparisonFormatter;
import org.xmlunit.diff.DefaultComparisonFormatter;

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
