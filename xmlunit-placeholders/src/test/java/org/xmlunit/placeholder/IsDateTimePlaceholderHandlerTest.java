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

import java.util.Locale;

import org.junit.Test;
import org.xmlunit.diff.ComparisonResult;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class IsDateTimePlaceholderHandlerTest {

    private PlaceholderHandler placeholderHandler = new IsDateTimePlaceholderHandler();

    @Test
    public void shouldGetKeyword() {
        String expected = "isDateTime";
        String keyword = placeholderHandler.getKeyword();

        assertThat(keyword, equalTo(expected));
    }

    @Test
    public void shouldAcceptABunchOfStrings() {
        final Locale l = Locale.getDefault();
        try {
            Locale.setDefault(Locale.US);
            for (final String s : new String[] {
                    "2020-01-01",
                    "01/01/2020",
                    "01/01/2020",
                    "2020-01-01T15:00",
                    "2020-01-01 15:00:00Z",
                    "01/01/2020 15:00",
                }) {
                final ComparisonResult comparisonResult = placeholderHandler.evaluate(s);
                assertThat(comparisonResult, equalTo(ComparisonResult.EQUAL));
            }
        } finally {
            Locale.setDefault(l);
        }
    }

    @Test
    public void shouldRejectNullAndEmpty() {
        assertThat(placeholderHandler.evaluate(null), equalTo(ComparisonResult.DIFFERENT));
        assertThat(placeholderHandler.evaluate(""), equalTo(ComparisonResult.DIFFERENT));
    }

    @Test
    public void shouldRejectExtraContent() {
        assertThat(placeholderHandler.evaluate("This is a test date 2020-01-01"),
                   equalTo(ComparisonResult.DIFFERENT));
    }

    @Test
    public void shouldRejectTrailingContent() {
        for (final String s : new String[] {
                "2020-01-01 not a date",
                "2020-01-01garbage",
                "2020-01-01T15:00:00Z and more",
                "01/01/2020 15:00 not a date",
            }) {
            assertThat(s, placeholderHandler.evaluate(s), equalTo(ComparisonResult.DIFFERENT));
        }
    }

    @Test
    public void shouldRejectOutOfRangeValues() {
        for (final String s : new String[] {
                "2020-02-30",
                "2020-13-01",
                "2020-01-01T25:00:00",
                "2020-01-01 15:61:00Z",
            }) {
            assertThat(s, placeholderHandler.evaluate(s), equalTo(ComparisonResult.DIFFERENT));
        }
    }

    @Test
    public void shouldAcceptSurroundingWhitespace() {
        assertThat(placeholderHandler.evaluate("  2020-01-01T15:00:00Z\n"),
                   equalTo(ComparisonResult.EQUAL));
    }

    @Test
    public void shouldAcceptFractionalSecondsBeyondMilliseconds() {
        assertThat(placeholderHandler.evaluate("2020-01-01T15:00:00.123456789Z"),
                   equalTo(ComparisonResult.EQUAL));
        assertThat(placeholderHandler.evaluate("2020-01-01T15:00:00.123456"),
                   equalTo(ComparisonResult.EQUAL));
    }

    @Test
    public void shouldParseExplicitPattern() {
        assertThat(placeholderHandler.evaluate("31 01 2020 12:34", "dd MM yyyy HH:mm"),
                   equalTo(ComparisonResult.EQUAL));
        assertThat(placeholderHandler.evaluate("abc", "dd MM yyyy HH:mm"),
                   equalTo(ComparisonResult.DIFFERENT));
    }

    @Test
    public void shouldRejectTrailingContentAndOutOfRangeValuesWithExplicitPattern() {
        assertThat(placeholderHandler.evaluate("31 01 2020 12:34 extra", "dd MM yyyy HH:mm"),
                   equalTo(ComparisonResult.DIFFERENT));
        assertThat(placeholderHandler.evaluate("32 01 2020 12:34", "dd MM yyyy HH:mm"),
                   equalTo(ComparisonResult.DIFFERENT));
        assertThat(placeholderHandler.evaluate(null, "dd MM yyyy HH:mm"),
                   equalTo(ComparisonResult.DIFFERENT));
    }

    @Test
    public void shouldParsePatternIndependentOfDefaultLocale() {
        final Locale l = Locale.getDefault();
        try {
            Locale.setDefault(Locale.GERMANY);
            assertThat(placeholderHandler.evaluate("24 June 2023", "dd MMMM yyyy"),
                       equalTo(ComparisonResult.EQUAL));
        } finally {
            Locale.setDefault(l);
        }
    }

    @Test
    public void shouldParsePatternWithExplicitLocale() {
        assertThat(placeholderHandler.evaluate("24 Juni 2023", "dd MMMM yyyy", "de"),
                   equalTo(ComparisonResult.EQUAL));
        assertThat(placeholderHandler.evaluate("24 Juni 2023", "dd MMMM yyyy", "en"),
                   equalTo(ComparisonResult.DIFFERENT));
    }

    @Test
    public void shouldUseUsLocaleWithTwoArgsWhenSecondIsEmpty() {
        // When second argument is empty string, should fall back to Locale.US
        assertThat(placeholderHandler.evaluate("24 June 2023", "dd MMMM yyyy", ""),
                   equalTo(ComparisonResult.EQUAL));
    }
}
