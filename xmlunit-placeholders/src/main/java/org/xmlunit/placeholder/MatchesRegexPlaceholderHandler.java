package org.xmlunit.placeholder;

import org.xmlunit.XMLUnitException;
import org.xmlunit.diff.ComparisonResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.xmlunit.diff.ComparisonResult.DIFFERENT;
import static org.xmlunit.diff.ComparisonResult.EQUAL;

/**
 * Handler for the {@code matchesRegex()} placeholder keyword.
 */
public class MatchesRegexPlaceholderHandler implements PlaceholderHandler {
    private static final String PLACEHOLDER_NAME = "matchesRegex";

    @Override
    public String getKeyword() {
        return PLACEHOLDER_NAME;
    }

    @Override
    public ComparisonResult evaluate(String testText, String... param) {
        if (param.length > 0 && param[0] != null && !param[0].equals("")) {
            try {
                Pattern pattern = Pattern.compile(param[0].trim());
                if (testText != null && evaluate(testText.trim(), pattern)) {
                    return EQUAL;
                }
            } catch(PatternSyntaxException e) {
                throw new XMLUnitException(e.getMessage(), e);
            }
        }
        return DIFFERENT;
    }

    private boolean evaluate(String testText, Pattern pattern) {
        Matcher matcher = pattern.matcher(testText);
        return matcher.find();
    }
}
