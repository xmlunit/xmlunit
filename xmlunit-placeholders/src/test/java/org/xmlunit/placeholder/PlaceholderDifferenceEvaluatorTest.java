package org.xmlunit.placeholder;

import org.junit.Test;
import org.xmlunit.XMLUnitException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.*;

import javax.xml.namespace.QName;
import java.util.Iterator;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

/**
 * Not testing the {@link PlaceholderDifferenceEvaluator} class directly, but testing it via the {@link DiffBuilder} and {@link Diff}. <br><br>
 * Created by Zheng on 3/10/2017.
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
            assertEquals("${xmlunit.ignore} must exclusively occupy the text node.", e.getCause().getMessage());
        }
    }

}