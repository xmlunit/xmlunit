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

package org.xmlunit.matchers;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.EvaluateXPathMatcher.hasXPath;
import static org.xmlunit.matchers.TypeMatcher.asBigDecimal;
import static org.xmlunit.matchers.TypeMatcher.asBoolean;
import static org.xmlunit.matchers.TypeMatcher.asDouble;
import static org.xmlunit.matchers.TypeMatcher.asInt;

public class TypeMatcherTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testXPathCountAsType() {
        String xml = "<fruits>" +
                "<fruit name=\"apple\"/>" +
                "<fruit name=\"orange\"/>" +
                "<fruit name=\"banana\"/>" +
                "</fruits>";

        assertThat(xml, hasXPath("count(//fruits/fruit)", asDouble(equalTo(3.0))));
        assertThat(xml, hasXPath("count(//fruits/fruit)", asBigDecimal(greaterThan(BigDecimal.ONE))));
        assertThat(xml, hasXPath("count(//fruits/fruit)", asInt(lessThan(4))));
    }

    @Test
    public void createUsefulMessageWhenAsDoubleFailed() {

        thrown.expect(AssertionError.class);
        thrown.expectMessage("XML with XPath //fruits/fruit/@name evaluated to " +
                "string converted to java.lang.Double <3.0>");
        thrown.expectMessage("failed with java.lang.NumberFormatException: For input string: \"apple\"");

        String xml = "<fruits><fruit name=\"apple\"/></fruits>";

        assertThat(xml, hasXPath("//fruits/fruit/@name", asDouble(equalTo(3.0))));
    }

    @Test
    public void createUsefulMessageWhenAsBigDecimalFailed() {

        thrown.expect(AssertionError.class);
        thrown.expectMessage("XML with XPath //fruits/fruit/@name evaluated to " +
                "string converted to java.math.BigDecimal <10>");
        thrown.expectMessage("failed with java.lang.NumberFormatException");

        String xml = "<fruits><fruit name=\"apple\"/></fruits>";

        assertThat(xml, hasXPath("//fruits/fruit/@name", asBigDecimal(equalTo(BigDecimal.TEN))));
    }

    @Test
    public void createUsefulMessageWhenAsIntFailed() {

        thrown.expect(AssertionError.class);
        thrown.expectMessage("XML with XPath //fruits/fruit/@name evaluated to " +
                "string converted to java.lang.Integer <3>");
        thrown.expectMessage("failed with java.lang.NumberFormatException: For input string: \"apple\"");

        String xml = "<fruits><fruit name=\"apple\"/></fruits>";

        assertThat(xml, hasXPath("//fruits/fruit/@name", asInt(equalTo(3))));
    }

    @Test
    public void createUsefulMessageWhenAsBooleanFailed() {

        thrown.expect(AssertionError.class);
        thrown.expectMessage("XML with XPath //fruits/fruit/@name evaluated to " +
                "string converted to java.lang.Boolean <false>");
        thrown.expectMessage("failed with java.lang.IllegalArgumentException: \"apple\" is not a boolean value");

        String xml = "<fruits><fruit name=\"apple\"/></fruits>";

        assertThat(xml, hasXPath("//fruits/fruit/@name", asBoolean(equalTo(false))));
    }

    @Test
    public void createUsefulMessageWhenEqualToFailed() {

        thrown.expect(AssertionError.class);
        thrown.expectMessage("XML with XPath count(//fruits/fruit) evaluated to " +
                "string converted to java.lang.Integer <2>");
        thrown.expectMessage("was <1>");

        String xml = "<fruits><fruit name=\"apple\"/></fruits>";

        assertThat(xml, hasXPath("count(//fruits/fruit)", asInt(equalTo(2))));
    }

    @Test
    public void testAsTypeWithNegation() {

        String xml = "<fruits><fruit name=\"apple\"/></fruits>";

        assertThat(xml, hasXPath("//fruits/fruit/@name", not(asDouble(equalTo(3.0)))));
        assertThat(xml, hasXPath("//fruits/fruit/@name", not(asBigDecimal(equalTo(BigDecimal.TEN)))));
        assertThat(xml, hasXPath("//fruits/fruit/@name", not(asInt(equalTo(3)))));
        assertThat(xml, hasXPath("//fruits/fruit/@name", not(asBoolean(equalTo(false)))));
    }

    @Test
    public void testAsBoolean() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<fruits>" +
                "<fruit name=\"apple\" fresh=\"True\"/>" +
                "<fruit name=\"orange\" fresh=\"false\"/>" +
                "<fruit name=\"banana\" fresh=\"1\"/>" +
                "<fruit name=\"pear\" fresh=\"0\"/>" +
                "</fruits>";

        assertThat(xml, hasXPath("//fruits/fruit[@name=\"apple\"]/@fresh", asBoolean(equalTo(true))));
        assertThat(xml, hasXPath("//fruits/fruit[@name=\"orange\"]/@fresh", asBoolean(equalTo(false))));
        assertThat(xml, hasXPath("//fruits/fruit[@name=\"banana\"]/@fresh", asBoolean(equalTo(true))));
        assertThat(xml, hasXPath("//fruits/fruit[@name=\"pear\"]/@fresh", asBoolean(equalTo(false))));
    }

    @Test
    public void testAsTypeWhenEvaluatedValueIsEmpty() {
        String xml = "<fruits></fruits>";

        assertThat(xml, hasXPath("//fruits/@text", asDouble(nullValue(Double.class))));
        assertThat(xml, hasXPath("//fruits/@text", asBigDecimal(nullValue(BigDecimal.class))));
        assertThat(xml, hasXPath("//fruits/@text", asInt(nullValue(Integer.class))));
        assertThat(xml, hasXPath("//fruits/@text", asBoolean(nullValue(Boolean.class))));
    }

    @Test
    public void testAsTypeWithExplicitTestValues() {

        assertThat("3.0", asDouble(greaterThanOrEqualTo(2.0)));
        assertThat("1.0e1", asBigDecimal(equalTo(BigDecimal.TEN)));
        assertThat("3", asInt(lessThan(4)));
        assertThat("false", asBoolean(equalTo(false)));
        assertThat("1", asBoolean(equalTo(true)));
    }
}
