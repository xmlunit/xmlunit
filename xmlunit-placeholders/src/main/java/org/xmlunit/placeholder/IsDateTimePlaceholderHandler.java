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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.xmlunit.diff.ComparisonResult;

/**
 * Handler for the {@code isDateTime} placeholder keyword.
 * @since 2.7.0
 */
public class IsDateTimePlaceholderHandler implements PlaceholderHandler {
    private static final String PLACEHOLDER_NAME = "isDateTime";

    private static final List<String> ISO_PATTERNS = Collections.unmodifiableList(Arrays.asList(
        "yyyy-MM-dd",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        "yyyy-MM-dd'T'HH:mm:ssZ",
        "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
        "yyyy-MM-dd'T'HH:mm:ssXXX",
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd HH:mm:ss.SSS",
        "yyyy-MM-dd HH:mm:ssZ",
        "yyyy-MM-dd HH:mm:ss.SSSZ",
        "yyyy-MM-dd HH:mm:ssXXX",
        "yyyy-MM-dd HH:mm:ss.SSSXXX"
     ));

    @Override
    public String getKeyword() {
        return PLACEHOLDER_NAME;
    }

    @Override
    public ComparisonResult evaluate(final String testText, final String... args) {
        if (args != null && args.length == 1) {
            return canParse(new SimpleDateFormat(args[0]), testText)
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
        if (canParse(DateFormat.getDateInstance(DateFormat.SHORT), testText) ||
            canParse(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT), testText)) {
            return true;
        }
        for (final String pattern : ISO_PATTERNS) {
            if (canParse(new SimpleDateFormat(pattern), testText)) {
                return true;
            }
        }
        return false;
    }

    private boolean canParse(final DateFormat fmt, final String testText) {
        try {
            return null != fmt.parse(testText);
        } catch (ParseException ex) {
            return false;
        }
    }
}
