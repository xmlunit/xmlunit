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

import org.junit.Test;
import org.xmlunit.diff.ComparisonResult;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class MatchesRegexPlaceholderHandlerTest {

    private PlaceholderHandler placeholderHandler = new MatchesRegexPlaceholderHandler();

    @Test
    public void shouldGetKeyword() {
        String expected = "matchesRegex";
        String keyword = placeholderHandler.getKeyword();

        assertThat(keyword, equalTo(expected));
    }

    @Test
    public void shouldEvaluateGivenSimpleRegex() {
        String testTest = "1234";
        String regex = "^\\d+$";
        ComparisonResult comparisonResult = placeholderHandler.evaluate(testTest, regex);

        assertThat(comparisonResult, equalTo(ComparisonResult.EQUAL));
    }

    @Test
    public void shouldNotEvaluateGivenNull() {
        String testTest = null;
        String regex = "^\\d+$";
        ComparisonResult comparisonResult = placeholderHandler.evaluate(testTest, regex);

        assertThat(comparisonResult, equalTo(ComparisonResult.DIFFERENT));
    }

    @Test
    public void shouldNotEvaluateGivenEmptyString() {
        String testTest = "";
        String regex = "^\\d+$";
        ComparisonResult comparisonResult = placeholderHandler.evaluate(testTest, regex);

        assertThat(comparisonResult, equalTo(ComparisonResult.DIFFERENT));
    }

    @Test
    public void shouldNotEvaluateStringDoesNotMatchRegex() {
        String testTest = "not parsable as a number even though it contains 123 numbers";
        String regex = "^\\d+$";
        ComparisonResult comparisonResult = placeholderHandler.evaluate(testTest, regex);

        assertThat(comparisonResult, equalTo(ComparisonResult.DIFFERENT));
    }

    @Test
    public void shouldNotEvaluateWithNullRegex() {
        String testTest = "a string";
        String regex = null;
        ComparisonResult comparisonResult = placeholderHandler.evaluate(testTest, regex);

        assertThat(comparisonResult, equalTo(ComparisonResult.DIFFERENT));
    }

    @Test
    public void shouldNotEvaluateWithEmptyRegex() {
        String testTest = "a string";
        String regex = "";
        ComparisonResult comparisonResult = placeholderHandler.evaluate(testTest, regex);

        assertThat(comparisonResult, equalTo(ComparisonResult.DIFFERENT));
    }

}
