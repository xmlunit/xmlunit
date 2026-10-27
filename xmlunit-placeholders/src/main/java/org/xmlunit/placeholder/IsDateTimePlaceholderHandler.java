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

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.xmlunit.diff.ComparisonResult;

/**
 * Handler for the {@code isDateTime} placeholder keyword.
 * @since 2.7.0
 */
public class IsDateTimePlaceholderHandler implements PlaceholderHandler {
    private static final String PLACEHOLDER_NAME = "isDateTime";

    private static final List<String> ISO_PATTERNS = Collections.unmodifiableList(Arrays.asList(
        "yyyy-MM-dd",
        "yyyy-MM-dd'T'HH:mm",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        "yyyy-MM-dd'T'HH:mm:ssZ",
        "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
        "yyyy-MM-dd'T'HH:mm:ssXXX",
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
        "yyyy-MM-dd HH:mm",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd HH:mm:ss.SSS",
        "yyyy-MM-dd HH:mm:ssZ",
        "yyyy-MM-dd HH:mm:ss.SSSZ",
        "yyyy-MM-dd HH:mm:ssXXX",
        "yyyy-MM-dd HH:mm:ss.SSSXXX"
     ));

    // SimpleDateFormat reads any number of digits following the
    // decimal point as milliseconds, so fractional seconds with more
    // than three digits (java.time.Instant#toString creates up to
    // nine) are truncated to milliseconds before parsing
    private static final Pattern EXTRA_FRACTION_DIGITS = Pattern.compile("(\\.[0-9]{3})[0-9]+");

    @Override
    public String getKeyword() {
        return PLACEHOLDER_NAME;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The first optional argument is the date/time pattern, the
     * second optional argument is the locale used to parse it, given
     * as a BCP 47 language tag (for example {@code de} or {@code
     * fr-FR}). When no locale is given {@link Locale#US} is used.</p>
     *
     * <p>The whole text, ignoring leading and trailing whitespace,
     * must match the pattern. Text following a date or values that
     * are out of range for their field (like a month of 13) make the
     * comparison fail.</p>
     */
    @Override
    public ComparisonResult evaluate(final String testText, final String... args) {
        if (args != null && args.length >= 1) {
            final Locale locale = args.length >= 2 && args[1] != null && !"".equals(args[1])
                ? Locale.forLanguageTag(args[1])
                : Locale.US;
            return canParse(new SimpleDateFormat(args[0], locale), testText)
                ? ComparisonResult.EQUAL
                : ComparisonResult.DIFFERENT;
        }
        return canParse(testText)
            ? ComparisonResult.EQUAL
            : ComparisonResult.DIFFERENT;
    }

    private boolean canParse(final String testText) {
        if (testText == null || "".equals(testText)) {
            return false;
        }
        for (final DateFormat fmt : getLocaleFormats()) {
            if (canParse(fmt, testText)) {
                return true;
            }
        }
        final String isoText = EXTRA_FRACTION_DIGITS.matcher(testText).replaceFirst("$1");
        for (final String pattern : ISO_PATTERNS) {
            if (canParse(new SimpleDateFormat(pattern, Locale.US), isoText)) {
                return true;
            }
        }
        return false;
    }

    private static List<DateFormat> getLocaleFormats() {
        final DateFormat shortDate = DateFormat.getDateInstance(DateFormat.SHORT);
        final List<DateFormat> formats = new ArrayList<DateFormat>();
        formats.add(shortDate);
        formats.add(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT));
        if (shortDate instanceof SimpleDateFormat) {
            // short date followed by a 24 hour time like "01/01/2020 15:00"
            final String datePattern = ((SimpleDateFormat) shortDate).toPattern();
            formats.add(new SimpleDateFormat(datePattern + " HH:mm"));
            formats.add(new SimpleDateFormat(datePattern + " HH:mm:ss"));
        }
        return formats;
    }

    private boolean canParse(final DateFormat fmt, final String testText) {
        if (testText == null) {
            return false;
        }
        final String text = testText.trim();
        // DateFormat#parse(String) stops at the first character it
        // cannot parse and silently ignores the rest of the text, and
        // the lenient default rolls out of range values over into the
        // next field, so "2020-01-01 not a date" and "2020-02-30"
        // would both be accepted otherwise
        fmt.setLenient(false);
        final ParsePosition pos = new ParsePosition(0);
        return fmt.parse(text, pos) != null && pos.getIndex() == text.length();
    }
}
