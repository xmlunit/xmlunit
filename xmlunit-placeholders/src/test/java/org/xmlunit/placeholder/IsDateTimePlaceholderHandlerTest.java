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
    public void shouldParseExplicitPattern() {
        assertThat(placeholderHandler.evaluate("31 01 2020 12:34", "dd MM yyyy HH:mm"),
                   equalTo(ComparisonResult.EQUAL));
        assertThat(placeholderHandler.evaluate("abc", "dd MM yyyy HH:mm"),
                   equalTo(ComparisonResult.DIFFERENT));
    }
}
