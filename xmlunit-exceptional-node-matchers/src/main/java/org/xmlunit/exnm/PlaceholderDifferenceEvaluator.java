package org.xmlunit.exnm;

import org.w3c.dom.Node;
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
        Comparison.Detail controlDetails = comparison.getControlDetails();
        Node controlTarget = controlDetails.getTarget();
        Comparison.Detail testDetails = comparison.getTestDetails();

        if (comparison.getType() == ComparisonType.TEXT_VALUE) {
            String controlTextValue = (String) controlDetails.getValue();
            return evaluateConsideringIgnorePlaceholder(controlTextValue, outcome);
        } else if (comparison.getType() == ComparisonType.CHILD_NODELIST_LENGTH &&
                Integer.valueOf(1).equals(controlDetails.getValue()) &&
                Integer.valueOf(0).equals(testDetails.getValue()) &&
                controlTarget.getFirstChild().getNodeType() == Node.TEXT_NODE) {
            String controlNodeChildValue = controlTarget.getFirstChild().getNodeValue();
            return evaluateConsideringIgnorePlaceholder(controlNodeChildValue, outcome);
        } else if (comparison.getType() == ComparisonType.CHILD_LOOKUP && controlTarget != null &&
                controlTarget.getNodeType() == Node.TEXT_NODE) {
            String controlNodeValue = controlTarget.getNodeValue();
            return evaluateConsideringIgnorePlaceholder(controlNodeValue, outcome);
        } else {
            return outcome;
        }
    }

    private ComparisonResult evaluateConsideringIgnorePlaceholder(String controlText, ComparisonResult outcome) {
        if (isIgnorePlaceholder(controlText)) {
            return ComparisonResult.EQUAL;
        } else {
            return outcome;
        }
    }

    private boolean isIgnorePlaceholder(String text) {
        String textWithIgnorePlaceholderRemoved = text.replaceFirst(PLACEHOLDER_REGEX_IGNORE, "");
        if (textWithIgnorePlaceholderRemoved.length() == 0) {    //  the ignore placeholder is all that in the text
            return true;
        } else if (textWithIgnorePlaceholderRemoved.length() != text.length()) {    //  there is other content than the ignore placeholder in the text
            throw new RuntimeException("${xmlunit.ignore} must exclusively occupy the text node.");
        } else {                         //  there is no ignore placeholder in the text
            return false;
        }
    }
}
