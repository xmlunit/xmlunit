package org.xmlunit.exnm;

import org.xmlunit.diff.Comparison;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.ComparisonType;
import org.xmlunit.diff.DifferenceEvaluator;

/**
 * Created by Zheng on 3/10/2017.
 */
public class PlaceholderDifferenceEvaluator implements DifferenceEvaluator {
    private static final String PLACEHOLDER_REGEX_IGNORE = "\\$\\{[\\s]*xmlunit\\.ignore[\\s]*}";
    public ComparisonResult evaluate(Comparison comparison, ComparisonResult outcome) {
        if (comparison.getType() == ComparisonType.TEXT_VALUE) {
            String controlTextValue = (String) comparison.getControlDetails().getValue();
            String controlTextValueWithIgnorePlaceholderRemoved = controlTextValue.replaceFirst(PLACEHOLDER_REGEX_IGNORE, "");
            if (controlTextValueWithIgnorePlaceholderRemoved.length() == 0) {    //  the ignore placeholder is all that in the control text node
                return ComparisonResult.EQUAL;
            } else if (controlTextValueWithIgnorePlaceholderRemoved.length() != controlTextValue.length()) {    //  there is other content than the ignore placeholder in the control text node
                throw new RuntimeException("${xmlunit.ignore} must exclusively occupy the text node.");
            } else {                         //  there is no ignore placeholder in the control text node
                return outcome;
            }
        } else {
            return outcome;
        }
    }
}
