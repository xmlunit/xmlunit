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
package org.xmlunit.placeholder;

import org.w3c.dom.Node;
import org.xmlunit.diff.Comparison;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.ComparisonType;
import org.xmlunit.diff.DifferenceEvaluator;

/**
 * This class is used to add placeholder feature to XML comparison. To use it, just add it with DiffBuilder like below <br><br>
 * <code>Diff diff = DiffBuilder.compare(control).withTest(test).withDifferenceEvaluator(new PlaceholderDifferenceEvaluator()).build();</code><br><br>
 * Supported scenarios are demonstrated in the unit tests (PlaceholderDifferenceEvaluatorTest).<br><br>
 * Default delimiters for placeholder are <code>${</code> and <code>}</code>. To use custom delimiters (in regular expression), create instance with the <code>PlaceholderDifferenceEvaluator(String placeholderOpeningDelimiterRegex, String placeholderClosingDelimiterRegex)</code> constructor. <br><br>
 * This class is <b>experimental/unstable</b>, hence the API or supported scenarios could change in future versions.<br><br>
 * @since 2.5.1
 */
public class PlaceholderDifferenceEvaluator implements DifferenceEvaluator {
    private static final String PLACEHOLDER_OPENING_DELIMITER_REGEX_DEFAULT = "\\$\\{";
    private static final String PLACEHOLDER_CLOSING_DELIMITER_REGEX_DEFAULT = "}";
    private static final String PLACEHOLDER_NAME_REGEX_IGNORE = "xmlunit\\.ignore";
    private static final String WHITESPACES_REGEX = "[\\s]*";
    private String placeholderRegexIgnore;

    public PlaceholderDifferenceEvaluator() {
        this.placeholderRegexIgnore = constructIgnorePlaceholder(
                PLACEHOLDER_OPENING_DELIMITER_REGEX_DEFAULT, PLACEHOLDER_CLOSING_DELIMITER_REGEX_DEFAULT);
    }

    /**
     * Null, empty or whitespaces string argument is omitted. Otherwise, argument is trimmed.
     * @param placeholderOpeningDelimiterRegex
     * @param placeholderClosingDelimiterRegex
     */
    public PlaceholderDifferenceEvaluator(String placeholderOpeningDelimiterRegex, String placeholderClosingDelimiterRegex) {
        String openingDelimiterRegex = PLACEHOLDER_OPENING_DELIMITER_REGEX_DEFAULT;
        String closingDelimiterRegex = PLACEHOLDER_CLOSING_DELIMITER_REGEX_DEFAULT;
        if (placeholderOpeningDelimiterRegex != null && !placeholderOpeningDelimiterRegex.matches(WHITESPACES_REGEX)) {
            openingDelimiterRegex = placeholderOpeningDelimiterRegex.trim();
        }
        if (placeholderClosingDelimiterRegex != null && !placeholderClosingDelimiterRegex.matches(WHITESPACES_REGEX)) {
            closingDelimiterRegex = placeholderClosingDelimiterRegex.trim();
        }

        this.placeholderRegexIgnore = constructIgnorePlaceholder(openingDelimiterRegex, closingDelimiterRegex);
    }

    private String constructIgnorePlaceholder(String openingDelimiterRegex, String closingDelimiterRegex) {
        return openingDelimiterRegex + WHITESPACES_REGEX + PLACEHOLDER_NAME_REGEX_IGNORE + WHITESPACES_REGEX +
                closingDelimiterRegex;
    }

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
        String textWithIgnorePlaceholderRemoved = text.replaceFirst(placeholderRegexIgnore, "");
        if (textWithIgnorePlaceholderRemoved.length() == 0) {    //  the ignore placeholder is all that in the text
            return true;
        } else if (textWithIgnorePlaceholderRemoved.length() != text.length()) {    //  there is other content than the ignore placeholder in the text
            throw new RuntimeException("The 'ignore' placeholder must exclusively occupy the text node.");
        } else {                         //  there is no ignore placeholder in the text
            return false;
        }
    }
}
