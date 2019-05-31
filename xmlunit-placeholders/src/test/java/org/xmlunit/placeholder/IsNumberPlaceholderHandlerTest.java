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

public class IsNumberPlaceholderHandlerTest {

    private PlaceholderHandler placeholderHandler = new IsNumberPlaceholderHandler();

    @Test
    public void shouldGetKeyword() {
        String expected = "isNumber";
        String keyword = placeholderHandler.getKeyword();

        assertThat(keyword, equalTo(expected));
    }

    @Test
    public void shouldEvaluateGivenSimpleNumber() {
        String testTest = "1234";
        ComparisonResult comparisonResult = placeholderHandler.evaluate(testTest);

        assertThat(comparisonResult, equalTo(ComparisonResult.EQUAL));
    }

    @Test
    public void shouldEvaluateGivenFloatingPointNumber() {
        String testTest = "12.34";
        ComparisonResult comparisonResult = placeholderHandler.evaluate(testTest);

        assertThat(comparisonResult, equalTo(ComparisonResult.EQUAL));
    }

    @Test
    public void shouldEvaluateGivenNegativeNumber() {
        String testTest = "-1234";
        ComparisonResult comparisonResult = placeholderHandler.evaluate(testTest);

        assertThat(comparisonResult, equalTo(ComparisonResult.EQUAL));
    }

    @Test
    public void shouldEvaluateGivenNegativeFloatingPointNumber() {
        String testTest = "-12.34";
        ComparisonResult comparisonResult = placeholderHandler.evaluate(testTest);

        assertThat(comparisonResult, equalTo(ComparisonResult.EQUAL));
    }

    @Test
    public void shouldEvaluateGivenEngineeringNotationFloatingPointNumber() {
        String testTest = "1.7E+3";
        ComparisonResult comparisonResult = placeholderHandler.evaluate(testTest);

        assertThat(comparisonResult, equalTo(ComparisonResult.EQUAL));
    }

    @Test
    public void shouldEvaluateGivenNegativeEngineeringNotationFloatingPointNumber() {
        String testTest = "-1.7E+3";
        ComparisonResult comparisonResult = placeholderHandler.evaluate(testTest);

        assertThat(comparisonResult, equalTo(ComparisonResult.EQUAL));
    }

    @Test
    public void shouldNotEvaluateGivenNull() {
        String testTest = null;
        ComparisonResult comparisonResult = placeholderHandler.evaluate(testTest);

        assertThat(comparisonResult, equalTo(ComparisonResult.DIFFERENT));
    }

    @Test
    public void shouldNotEvaluateGivenEmptyString() {
        String testTest = "";
        ComparisonResult comparisonResult = placeholderHandler.evaluate(testTest);

        assertThat(comparisonResult, equalTo(ComparisonResult.DIFFERENT));
    }

    @Test
    public void shouldNotEvaluateGivenNonNumberString() {
        String testTest = "not parsable as a number even though it contains 123 numbers";
        ComparisonResult comparisonResult = placeholderHandler.evaluate(testTest);

        assertThat(comparisonResult, equalTo(ComparisonResult.DIFFERENT));
    }

}
