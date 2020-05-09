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
import org.xmlunit.XMLUnitException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.*;

import javax.xml.namespace.QName;
import java.util.Iterator;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

/**
 * Not testing the {@link PlaceholderDifferenceEvaluator} class directly, but testing it via the {@link DiffBuilder} and {@link Diff}. <br><br>
 */
public class PlaceholderDifferenceEvaluatorTest {
    @Test
    public void regression_NoPlaceholder_Equal() throws Exception {
        String control = "<elem1><elem11>123</elem11></elem1>";
        String test = "<elem1><elem11>123</elem11></elem1>";
        Diff diff = DiffBuilder.compare(control).withTest(test)
                .withDifferenceEvaluator(new PlaceholderDifferenceEvaluator()).build();

        assertFalse(diff.hasDifferences());
    }

    @Test
    public void regression_NoPlaceholder_Different() throws Exception {
        String control = "<elem1><elem11>123</elem11></elem1>";
        String test = "<elem1><elem11>abc</elem11></elem1>";
        Diff diff = DiffBuilder.compare(control).withTest(test)
                .withDifferenceEvaluator(new PlaceholderDifferenceEvaluator()).build();

        assertTrue(diff.hasDifferences());
        int count = 0;
        Iterator it = diff.getDifferences().iterator();
        while (it.hasNext()) {
            count++;
            Difference difference = (Difference) it.next();
            assertEquals(ComparisonResult.DIFFERENT, difference.getResult());
        }
        assertEquals(1, count);
    }

    @Test
    public void regression_NoPlaceholder_Different_EmptyExpectedElement() throws Exception {
        String control = "<elem1><elem11/></elem1>";
        String test = "<elem1><elem11>abc</elem11></elem1>";
        Diff diff = DiffBuilder.compare(control).withTest(test)
                .withDifferenceEvaluator(new PlaceholderDifferenceEvaluator()).build();

        assertTrue(diff.hasDifferences());
        int count = 0;
        Iterator it = diff.getDifferences().iterator();
        while (it.hasNext()) {
            count++;
            Difference difference = (Difference) it.next();
            assertEquals(ComparisonResult.DIFFERENT, difference.getResult());

            Comparison comparison = difference.getComparison();
            if (count == 1) {
                String xpath = "/elem1[1]/elem11[1]";
                assertEquals(ComparisonType.CHILD_NODELIST_LENGTH, comparison.getType());
                assertEquals(xpath, comparison.getControlDetails().getXPath());
                assertEquals(0, comparison.getControlDetails().getValue());
                assertEquals(xpath, comparison.getTestDetails().getXPath());
                assertEquals(1, comparison.getTestDetails().getValue());
            } else {
                assertEquals(ComparisonType.CHILD_LOOKUP, comparison.getType());
                assertEquals(null, comparison.getControlDetails().getXPath());
                assertEquals(null, comparison.getControlDetails().getValue());
                assertEquals("/elem1[1]/elem11[1]/text()[1]", comparison.getTestDetails().getXPath());
                assertEquals(QName.valueOf("#text"), comparison.getTestDetails().getValue());
            }
        }
        assertEquals(2, count);
    }

    @Test
    public void hasIgnorePlaceholder_Equal_NoWhitespaceInPlaceholder() throws Exception {
        String control = "<elem1><elem11>${xmlunit.ignore}</elem11></elem1>";
        String test = "<elem1><elem11>abc</elem11></elem1>";
        Diff diff = DiffBuilder.compare(control).withTest(test)
                .withDifferenceEvaluator(new PlaceholderDifferenceEvaluator()).build();

        assertFalse(diff.hasDifferences());
    }

    @Test
    public void hasIgnorePlaceholder_Equal_NoWhitespaceInPlaceholder_CDATA_Control() throws Exception {
        String control = "<elem1><elem11><![CDATA[${xmlunit.ignore}]]></elem11></elem1>";
        String test = "<elem1><elem11>abc</elem11></elem1>";
        Diff diff = DiffBuilder.compare(control).withTest(test)
            .withDifferenceEvaluator(DifferenceEvaluators.chain(
                DifferenceEvaluators.Default, new PlaceholderDifferenceEvaluator()))
            .build();

        assertFalse(diff.toString(), diff.hasDifferences());
    }

    @Test
    public void hasIgnorePlaceholder_Equal_NoWhitespaceInPlaceholder_CDATA_TEST() throws Exception {
        String control = "<elem1><elem11>${xmlunit.ignore}</elem11></elem1>";
        String test = "<elem1><elem11><![CDATA[abc]]></elem11></elem1>";
        Diff diff = DiffBuilder.compare(control).withTest(test)
            .withDifferenceEvaluator(DifferenceEvaluators.chain(
                DifferenceEvaluators.Default, new PlaceholderDifferenceEvaluator()))
            .build();

        assertFalse(diff.hasDifferences());
    }

    @Test
    public void hasIgnorePlaceholder_CustomDelimiters_Equal_NoWhitespaceInPlaceholder() throws Exception {
        String control = "<elem1><elem11>#{xmlunit.ignore}</elem11></elem1>";
        String test = "<elem1><elem11>abc</elem11></elem1>";
        Diff diff = DiffBuilder.compare(control).withTest(test)
                .withDifferenceEvaluator(new PlaceholderDifferenceEvaluator("#\\{", null)).build();

        assertFalse(diff.hasDifferences());
    }

    @Test
    public void hasIgnorePlaceholder_Equal_StartAndEndWhitespacesInPlaceholder() throws Exception {
        String control = "<elem1><elem11>${  xmlunit.ignore  }</elem11></elem1>";
        String test = "<elem1><elem11>abc</elem11></elem1>";
        Diff diff = DiffBuilder.compare(control).withTest(test)
                .withDifferenceEvaluator(new PlaceholderDifferenceEvaluator()).build();

        assertFalse(diff.hasDifferences());
    }

    @Test
    public void hasIgnorePlaceholder_Equal_EmptyActualElement() throws Exception {
        String control = "<elem1><elem11>${xmlunit.ignore}</elem11></elem1>";
        String test = "<elem1><elem11/></elem1>";
        Diff diff = DiffBuilder.compare(control).withTest(test)
                .withDifferenceEvaluator(new PlaceholderDifferenceEvaluator()).build();

        assertFalse(diff.hasDifferences());
    }

    @Test
    public void hasIgnorePlaceholder_Exception_ExclusivelyOccupy() throws Exception {
        String control = "<elem1><elem11> ${xmlunit.ignore}abc</elem11></elem1>";
        String test = "<elem1><elem11>abc</elem11></elem1>";
        DiffBuilder diffBuilder = DiffBuilder.compare(control).withTest(test)
                .withDifferenceEvaluator(new PlaceholderDifferenceEvaluator());

        try {
            diffBuilder.build();
            fail();
        } catch (XMLUnitException e) {
            assertEquals("The placeholder must exclusively occupy the text node.", e.getCause().getMessage());
        }
    }

    @Test
    public void regression_NoPlaceholder_Attributes_Equal() throws Exception {
        String control = "<elem1 attr='123'/>";
        String test = "<elem1 attr='123'/>";
        Diff diff = DiffBuilder.compare(control).withTest(test)
                .withDifferenceEvaluator(new PlaceholderDifferenceEvaluator()).build();

        assertFalse(diff.hasDifferences());
    }

    @Test
    public void regression_NoPlaceholder_Attributes_Different() throws Exception {
        String control = "<elem1 attr='123'/>";
        String test = "<elem1 attr='abc'/>";
        Diff diff = DiffBuilder.compare(control).withTest(test)
                .withDifferenceEvaluator(new PlaceholderDifferenceEvaluator()).build();

        assertTrue(diff.hasDifferences());
        int count = 0;
        Iterator it = diff.getDifferences().iterator();
        while (it.hasNext()) {
            count++;
            Difference difference = (Difference) it.next();
            assertEquals(ComparisonResult.DIFFERENT, difference.getResult());
        }
        assertEquals(1, count);
    }

    @Test
    public void regression_NoPlaceholder_Missing_Attribute() throws Exception {
        String control = "<elem1/>";
        String test = "<elem1 attr='abc'/>";
        Diff diff = DiffBuilder.compare(control).withTest(test)
                .withDifferenceEvaluator(new PlaceholderDifferenceEvaluator()).build();

        assertTrue(diff.hasDifferences());
        int count = 0;
        Iterator it = diff.getDifferences().iterator();
        while (it.hasNext()) {
            count++;
            Difference difference = (Difference) it.next();
            assertEquals(ComparisonResult.DIFFERENT, difference.getResult());

            Comparison comparison = difference.getComparison();
            if (count == 1) {
                String xpath = "/elem1[1]";
                assertEquals(ComparisonType.ELEMENT_NUM_ATTRIBUTES, comparison.getType());
                assertEquals(xpath, comparison.getControlDetails().getXPath());
                assertEquals(0, comparison.getControlDetails().getValue());
                assertEquals(xpath, comparison.getTestDetails().getXPath());
                assertEquals(1, comparison.getTestDetails().getValue());
            } else {
                assertEquals(ComparisonType.ATTR_NAME_LOOKUP, comparison.getType());
                assertEquals("/elem1[1]", comparison.getControlDetails().getXPath());
                assertEquals(null, comparison.getControlDetails().getValue());
                assertEquals("/elem1[1]/@attr", comparison.getTestDetails().getXPath());
            }
        }
        assertEquals(2, count);
    }

    @Test
    public void hasIgnorePlaceholder_Attribute_Equal_NoWhitespaceInPlaceholder() throws Exception {
        String control = "<elem1 attr='${xmlunit.ignore}'/>";
        String test = "<elem1 attr='abc'/>";
        Diff diff = DiffBuilder.compare(control).withTest(test)
                .withDifferenceEvaluator(new PlaceholderDifferenceEvaluator()).build();

        assertFalse(diff.hasDifferences());
    }

    @Test
    public void hasIgnorePlaceholder_CustomDelimiters_Attribute_Equal_NoWhitespaceInPlaceholder() throws Exception {
        String control = "<elem1 attr='#{xmlunit.ignore}'/>";
        String test = "<elem1 attr='abc'/>";
        Diff diff = DiffBuilder.compare(control).withTest(test)
                .withDifferenceEvaluator(new PlaceholderDifferenceEvaluator("#\\{", null)).build();

        assertFalse(diff.hasDifferences());
    }

    @Test
    public void hasIgnorePlaceholder_Attribute_Equal_StartAndEndWhitespacesInPlaceholder() throws Exception {
        String control = "<elem1 attr='${  xmlunit.ignore  }'/>";
        String test = "<elem1 attr='abc'/>";
        Diff diff = DiffBuilder.compare(control).withTest(test)
                .withDifferenceEvaluator(new PlaceholderDifferenceEvaluator()).build();

        assertFalse(diff.hasDifferences());
    }

    @Test
    public void hasIgnorePlaceholder_Attribute_Equal_MissingActualAttribute() throws Exception {
        String control = "<elem1 attr='${xmlunit.ignore}'/>";
        String test = "<elem1/>";
        Diff diff = DiffBuilder.compare(control).withTest(test)
                .withDifferenceEvaluator(new PlaceholderDifferenceEvaluator()).build();

        assertFalse(diff.hasDifferences());
    }

    @Test
    public void missingAttributeWithMoreThanOneAttribute() throws Exception {
        String control = "<elem1 attr='${xmlunit.ignore}' a='b'/>";
        String test = "<elem1 a='b'/>";
        Diff diff = DiffBuilder.compare(control).withTest(test)
                .withDifferenceEvaluator(new PlaceholderDifferenceEvaluator()).build();

        assertFalse(diff.hasDifferences());
    }

    @Test
    public void missingAttributeWithMoreThanOneIgnore() throws Exception {
        String control = "<elem1 attr='${xmlunit.ignore}' a='${xmlunit.ignore}'/>";
        String test = "<elem1/>";
        Diff diff = DiffBuilder.compare(control).withTest(test)
                .withDifferenceEvaluator(new PlaceholderDifferenceEvaluator()).build();

        assertFalse(diff.hasDifferences());
    }

    @Test
    public void missingAttributeWithMissingControlAttribute() throws Exception {
        String control = "<elem1 attr='${xmlunit.ignore}' a='${xmlunit.ignore}'/>";
        String test = "<elem1 b='a'/>";
        Diff diff = DiffBuilder.compare(control).withTest(test)
                .withDifferenceEvaluator(new PlaceholderDifferenceEvaluator()).build();

        assertTrue(diff.hasDifferences());
    }

    @Test
    public void hasIgnorePlaceholder_Attribute_Exception_ExclusivelyOccupy() throws Exception {
        String control = "<elem1 attr='${xmlunit.ignore}abc'/>";
        String test = "<elem1 attr='abc'/>";
        DiffBuilder diffBuilder = DiffBuilder.compare(control).withTest(test)
                .withDifferenceEvaluator(new PlaceholderDifferenceEvaluator());

        try {
            diffBuilder.build();
            fail();
        } catch (XMLUnitException e) {
            assertEquals("The placeholder must exclusively occupy the text node.", e.getCause().getMessage());
        }
    }

    @Test
    public void hasIsNumberPlaceholder_Attribute_NotNumber() {
        String control = "<elem1 attr='${xmlunit.isNumber}'/>";
        String test = "<elem1 attr='abc'/>";
        Diff diff = DiffBuilder.compare(control).withTest(test)
            .withDifferenceEvaluator(new PlaceholderDifferenceEvaluator()).build();

        assertTrue(diff.hasDifferences());
    }

    @Test
    public void hasIsNumberPlaceholder_Attribute_IsNumber() {
        String control = "<elem1 attr='${xmlunit.isNumber}'/>";
        String test = "<elem1 attr='123'/>";
        Diff diff = DiffBuilder.compare(control).withTest(test)
            .withDifferenceEvaluator(new PlaceholderDifferenceEvaluator()).build();

        assertFalse(diff.hasDifferences());
    }

    @Test
    public void hasIsNumberPlaceholder_Element_NotNumber() {
        String control = "<elem1>${xmlunit.isNumber}</elem1>";
        String test = "<elem1>abc</elem1>";
        Diff diff = DiffBuilder.compare(control).withTest(test)
            .withDifferenceEvaluator(new PlaceholderDifferenceEvaluator()).build();

        assertTrue(diff.hasDifferences());
    }

    @Test
    public void hasIsNumberPlaceholder_Element_IsNumber() {
        String control = "<elem1>${xmlunit.isNumber}</elem1>";
        String test = "<elem1>123</elem1>";
        Diff diff = DiffBuilder.compare(control).withTest(test)
            .withDifferenceEvaluator(new PlaceholderDifferenceEvaluator()).build();

        assertFalse(diff.hasDifferences());
    }

    @Test
    public void hasMatchesRegexPlaceholder_Attribute_Matches() {
        String control = "<elem1 attr='${xmlunit.matchesRegex(^\\d+$)}'>qwert</elem1>";
        String test = "<elem1 attr='023'>qwert</elem1>";
        Diff diff = PlaceholderSupport.withPlaceholderSupport(DiffBuilder.compare(control).withTest(test)).build();

        assertFalse(diff.hasDifferences());
    }

    @Test
    public void hasMatchesRegexPlaceholder_Attribute_NotMatches() {
        String control = "<elem1 attr='${xmlunit.matchesRegex(^\\d+$)}'>qwert</elem1>";
        String test = "<elem1 attr='023asd'>qwert</elem1>";
        Diff diff = PlaceholderSupport.withPlaceholderSupport(DiffBuilder.compare(control).withTest(test)).build();

        assertTrue(diff.hasDifferences());
    }

    @Test
    public void hasMatchesRegexPlaceholder_Element_Matches() {
        String control = "<elem1>${xmlunit.matchesRegex(^\\d+$)}</elem1>";
        String test = "<elem1>023</elem1>";
        Diff diff = PlaceholderSupport.withPlaceholderSupport(DiffBuilder.compare(control).withTest(test)).build();

        assertFalse(diff.hasDifferences());
    }

    @Test
    public void hasMatchesRegexPlaceholder_Element_NotMatches() {
        String control = "<elem1>${xmlunit.matchesRegex(^\\d+$)}</elem1>";
        String test = "<elem1>23abc</elem1>";
        Diff diff = PlaceholderSupport.withPlaceholderSupport(DiffBuilder.compare(control).withTest(test)).build();

        assertTrue(diff.hasDifferences());
    }

    @Test
    public void hasMatchesRegexPlaceholder_Element_Exception_MalformedRegex() {
        String control = "<elem1>${xmlunit.matchesRegex[^(\\d+$]}</elem1>";
        String test = "<elem1>23abc</elem1>";
        DiffBuilder diffBuilder = DiffBuilder.compare(control).withTest(test)
                .withDifferenceEvaluator(new PlaceholderDifferenceEvaluator(null, null, Pattern.quote("["), Pattern.quote("]"), null));

        try {
            diffBuilder.build();
            fail();
        } catch (XMLUnitException e) {
            assertThat(e.getCause().getMessage(), containsString("Unclosed group near index"));
        }
    }

    @Test
    public void hasMalformedPlaceholder_Attribute() {
        String control = "<elem1 attr='${xmlunit.,}'>qwert</elem1>";
        String test = "<elem1 attr='023'>qwert</elem1>";
        Diff diff = PlaceholderSupport.withPlaceholderSupport(DiffBuilder.compare(control).withTest(test)).build();

        assertTrue(diff.hasDifferences());
    }

    @Test
    public void isDateTimePlaceholder_Attribute_NotDateTime() {
        String control = "<elem1 attr='${xmlunit.isDateTime}'/>";
        String test = "<elem1 attr='abc'/>";
        Diff diff = PlaceholderSupport.withPlaceholderSupport(DiffBuilder.compare(control).withTest(test)).build();

        assertTrue(diff.hasDifferences());
    }

    @Test
    public void isDateTimePlaceholder_Attribute_IsDateTime() {
        String control = "<elem1 attr='${xmlunit.isDateTime}'/>";
        String test = "<elem1 attr='2020-01-01 15:00:00Z'/>";
        Diff diff = PlaceholderSupport.withPlaceholderSupport(DiffBuilder.compare(control).withTest(test)).build();

        assertFalse(diff.hasDifferences());
    }

    @Test
    public void isDateTimePlaceholder_Attribute_IsDateTime_CustomFormat() {
        String control = "<elem1 attr='${xmlunit.isDateTime(dd.MM.yyyy)}'/>";
        String test = "<elem1 attr='05.09.2020'/>";
        Diff diff = DiffBuilder.compare(control).withTest(test)
            .withDifferenceEvaluator(new PlaceholderDifferenceEvaluator()).build();

        assertFalse(diff.hasDifferences());
    }

    @Test
    public void isDateTimePlaceholder_Element_NotDateTime() {
        String control = "<elem1>${xmlunit.isDateTime}</elem1>";
        String test = "<elem1>abc</elem1>";
        Diff diff = PlaceholderSupport.withPlaceholderSupport(DiffBuilder.compare(control).withTest(test)).build();

        assertTrue(diff.hasDifferences());
    }

    @Test
    public void isDateTimePlaceholder_Element_IsDateTime() {
        String control = "<elem1>${xmlunit.isDateTime}</elem1>";
        String test = "<elem1>2020-01-01 15:00:00Z</elem1>";
        Diff diff = PlaceholderSupport.withPlaceholderSupport(DiffBuilder.compare(control).withTest(test)).build();

        assertFalse(diff.hasDifferences());
    }

    @Test
    public void isDateTimePlaceholder_Element_IsDateTime_CustomFormat() {
        String control = "<elem1>${xmlunit.isDateTime(dd.MM.yyyy)}</elem1>";
        String test = "<elem1>05.09.2020</elem1>";
        Diff diff = DiffBuilder.compare(control).withTest(test)
            .withDifferenceEvaluator(new PlaceholderDifferenceEvaluator()).build();

        assertFalse(diff.hasDifferences());
    }

}
