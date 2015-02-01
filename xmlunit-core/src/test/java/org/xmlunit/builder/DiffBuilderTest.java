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

package org.xmlunit.builder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.xmlunit.util.Linqy.count;

import org.xmlunit.TestResources;
import org.xmlunit.diff.Comparison;
import org.xmlunit.diff.ComparisonControllers;
import org.xmlunit.diff.ComparisonListener;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.ComparisonType;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.Difference;
import org.xmlunit.diff.DifferenceEvaluator;
import org.xmlunit.diff.DifferenceEvaluators;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class DiffBuilderTest {

    @Test
    public void testDiff_withoutIgnoreWhitespaces_shouldFail() {
        // prepare testData
        String controlXml = "<a><b>Test Value</b></a>";
        String testXml = "<a>\n <b>\n  Test Value\n </b>\n</a>";

        // run test
        Diff myDiff = DiffBuilder.compare(Input.fromMemory(controlXml).build())
                      .withTest(Input.fromMemory(testXml).build())
                      .build();

        // validate result
        Assert.assertTrue(myDiff.toString(), myDiff.hasDifferences());

    }

    @Test
    public void testDiff_withIgnoreWhitespaces_shouldSuccess() {
        // prepare testData
        String controlXml = "<a><b>Test Value</b></a>";
        String testXml = "<a>\n <b>\n  Test Value\n </b>\n</a>";

        // run test
        Diff myDiff = DiffBuilder.compare(Input.fromMemory(controlXml).build())
                      .withTest(Input.fromMemory(testXml).build())
                      .ignoreWhitespace()
                      .build();

        // validate result
        Assert.assertFalse("XML similar " + myDiff.toString(), myDiff.hasDifferences());

    }

    @Test
    public void testDiff_withoutNormalizeWhitespaces_shouldFail() {
        // prepare testData
        String controlXml = "<a><b>Test Value</b></a>";
        String testXml = "<a>\n <b>\n  Test Value\n </b>\n</a>";

        // run test
        Diff myDiff = DiffBuilder.compare(Input.fromMemory(controlXml).build())
                      .withTest(Input.fromMemory(testXml).build())
                      .build();

        // validate result
        Assert.assertTrue(myDiff.toString(), myDiff.hasDifferences());

    }

    @Test
    public void testDiff_withNormalizeWhitespaces_shouldSuccess() {
        // prepare testData
        String controlXml = "<a><b>Test Value</b></a>";
        String testXml = "<a>\n <b>\n  Test Value\n </b>\n</a>";

        // run test
        Diff myDiff = DiffBuilder.compare(Input.fromMemory(controlXml).build())
                      .withTest(Input.fromMemory(testXml).build())
                      .normalizeWhitespace()
                      .build();

        // validate result
        Assert.assertFalse("XML similar " + myDiff.toString(), myDiff.hasDifferences());

    }

    @Test
    public void testDiff_withCheckForIdentical_shouldFail() {
        // prepare testData
        String controlXml = "<a>Test Value</a>";
        String testXml = "<a><![CDATA[Test Value]]></a>";

        // run test
        Diff myDiff = DiffBuilder.compare(Input.fromMemory(controlXml).build())
                      .withTest(Input.fromMemory(testXml).build())
                      .checkForIdentical()
                      .build();

        // validate result
        Assert.assertTrue(myDiff.toString(), myDiff.hasDifferences());

    }

    @Test
    public void testDiff_withCheckForSimilar_shouldSuccess() {
        // prepare testData
        String controlXml = "<a>Test Value</a>";
        String testXml = "<a><![CDATA[Test Value]]></a>";

        // run test
        Diff myDiff = DiffBuilder.compare(Input.fromMemory(controlXml).build())
                      .withTest(Input.fromMemory(testXml).build())
                      .checkForSimilar()
                      .build();

        // validate result
        Assert.assertFalse("XML similar " + myDiff.toString(), myDiff.hasDifferences());

    }
    @Test
    public void testDiff_withoutIgnoreComments_shouldFail() {
        // prepare testData
        String controlXml = "<a><b><!-- A comment -->Test Value</b></a>";
        String testXml = "<a><b><!-- An other comment -->Test Value</b></a>";

        // run test
        Diff myDiff = DiffBuilder.compare(Input.fromMemory(controlXml).build())
                    .withTest(Input.fromMemory(testXml).build())
                    .build();

        // validate result
        Assert.assertTrue(myDiff.toString(), myDiff.hasDifferences());

    }

    @Test
    public void testDiff_withIgnoreComments_shouldSuccess() {
        // prepare testData
        String controlXml = "<a><b><!-- A comment -->Test Value</b></a>";
        String testXml = "<a><b><!-- An other comment -->Test Value</b></a>";

        // run test
        Diff myDiff = DiffBuilder.compare(Input.fromMemory(controlXml).build())
                                .withTest(Input.fromMemory(testXml).build())
                                .ignoreComments()
                                .build();

        // validate result
        Assert.assertFalse("XML similar " + myDiff.toString(), myDiff.hasDifferences());

    }

    @Test
    public void testDiff_fromCompinedSourceAndString_shouldSuccess() {
        // prepare testData
        String controlXml = "<a><b>Test Value</b></a>";

        // run test
        Diff myDiff = DiffBuilder.compare(Input.fromMemory(controlXml).build()).withTest(controlXml)
                .build();

        // validate result
        Assert.assertFalse("XML similar " + myDiff.toString(), myDiff.hasDifferences());

    }

    @Test
    public void testDiff_fromBuilder_shouldSuccess() {
        // prepare testData
        String controlXml = "<a><b>Test Value</b></a>";

        // run test
        Diff myDiff = DiffBuilder.compare(Input.fromMemory(controlXml))
                .withTest(Input.fromMemory(controlXml))
                .build();

        // validate result
        Assert.assertFalse("XML similar " + myDiff.toString(), myDiff.hasDifferences());

    }

    @Test
    public void testDiff_fromByteArray_shouldSuccess() {
        // prepare testData
        byte[] controlXml = "<a><b>Test Value</b></a>".getBytes();

        // run test
        Diff myDiff = DiffBuilder.compare(controlXml).withTest(controlXml).build();

        // validate result
        Assert.assertFalse("XML similar " + myDiff.toString(), myDiff.hasDifferences());

    }

    @Test
    public void testDiff_fromFile_shouldSuccess() {
        // prepare testData
        File controlXml = new File(TestResources.ANIMAL_FILE);

        // run test
        Diff myDiff = DiffBuilder.compare(controlXml).withTest(controlXml).build();

        // validate result
        Assert.assertFalse("XML similar " + myDiff.toString(), myDiff.hasDifferences());

    }

    @Test
    public void testDiff_withComparisonListener_shouldCallListener() {
        // prepare testData
        final String control = "<a><b attr=\"abc\"></b></a>";
        final String test = "<a><b attr=\"xyz\"></b></a>";
        final List<Difference> diffs = new ArrayList<Difference>();
        final ComparisonListener comparisonListener = new ComparisonListener() {
            @Override
            public void comparisonPerformed(Comparison comparison, ComparisonResult outcome) {
                diffs.add(new Difference(comparison, outcome));
            }
        };

        // run test
        Diff myDiff = DiffBuilder.compare(control).withTest(test)
                .withComparisonListeners(comparisonListener)
                .build();

        // validate result
        Assert.assertTrue(myDiff.toString(), myDiff.hasDifferences());
        assertThat(diffs.size(), greaterThan(1));
    }

    @Test
    public void testDiff_withDifferenceListener_shouldCallListener() {
        // prepare testData
        final String control = "<a><b attr=\"abc\"></b></a>";
        final String test = "<a><b attr=\"xyz\"></b></a>";
        final List<Difference> diffs = new ArrayList<Difference>();
        final ComparisonListener comparisonListener = new ComparisonListener() {
            @Override
            public void comparisonPerformed(Comparison comparison, ComparisonResult outcome) {
                diffs.add(new Difference(comparison, outcome));
            }
        };

        // run test
        Diff myDiff = DiffBuilder.compare(control).withTest(test)
                .withDifferenceListeners(comparisonListener)
                .build();

        // validate result
        Assert.assertTrue(myDiff.toString(), myDiff.hasDifferences());
        assertThat(diffs.size(), is(1));
        assertThat(diffs.get(0).getResult(), is(ComparisonResult.DIFFERENT));
        assertThat(diffs.get(0).getComparison().getType(), is(ComparisonType.ATTR_VALUE));

    }

    @Test
    public void testDiff_withDifferenceEvaluator_shouldSuccess() {
        // prepare testData
        final String control = "<a><b attr=\"abc\"></b></a>";
        final String test = "<a><b attr=\"xyz\"></b></a>";

        // run test
        Diff myDiff = DiffBuilder.compare(control).withTest(test)
                .withDifferenceEvaluator(new IgnoreAttributeDifferenceEvaluator("attr"))
                .build();

        // validate result
        Assert.assertFalse(myDiff.toString(), myDiff.hasDifferences());

    }

    @Test
    public void testDiff_withDifferenceEvaluator_shouldNotInterfereWithSimilar() {
        // prepare testData
        final String control = "<a><b><![CDATA[abc]]></b></a>";
        final String test = "<a><b>abc</b></a>";

        // run test
        Diff myDiff = DiffBuilder.compare(control).withTest(test)
                .withDifferenceEvaluator(
                    DifferenceEvaluators.chain(DifferenceEvaluators.Default,
                    new IgnoreAttributeDifferenceEvaluator("attr")))
                .checkForSimilar()
                .build();

        // validate result
        Assert.assertFalse(myDiff.toString(), myDiff.hasDifferences());

    }

    @Test
    public void testDiff_withCustomDifferenceEvaluator_shouldNotEvaluateSimilar() {
        // prepare testData
        final String control = "<a><b><![CDATA[abc]]></b></a>";
        final String test = "<a><b>abc</b></a>";

        // run test
        Diff myDiff = DiffBuilder.compare(control).withTest(test)
                .withDifferenceEvaluator(new IgnoreAttributeDifferenceEvaluator("attr"))
                .checkForSimilar()
                .build();

        // validate result
        Assert.assertTrue(myDiff.toString(), myDiff.hasDifferences());
        Assert.assertThat(ComparisonResult.DIFFERENT, is(myDiff.getDifferences().iterator().next().getResult()));

    }

    private final class IgnoreAttributeDifferenceEvaluator implements DifferenceEvaluator {

        private String attributeName;

        public IgnoreAttributeDifferenceEvaluator(String attributeName) {
            this.attributeName = attributeName;
        }

        @Override
        public ComparisonResult evaluate(Comparison comparison, ComparisonResult outcome) {
            final Node controlNode = (Node) comparison.getControlDetails().getTarget();
            if (controlNode instanceof Attr) {
                Attr attr = (Attr) controlNode;
                if (attr.getName().equals(attributeName)) {
                    return ComparisonResult.EQUAL;
                }
            }
            return outcome;
        }
    }

    @Test
    public void testDiff_withDefaultComparisonController_shouldReturnAllDifferences() {
        // prepare testData
        final String control = "<a><b attr1=\"abc\" attr2=\"def\"></b></a>";
        final String test = "<a><b attr1=\"uvw\" attr2=\"xyz\"></b></a>";

        // run test
        Diff myDiff = DiffBuilder.compare(control).withTest(test)
                .build();

        // validate result
        Assert.assertTrue(myDiff.hasDifferences());
        assertThat(count(myDiff.getDifferences()), is(2));
    }


    @Test
    public void testDiff_withStopWhenDifferentComparisonController_shouldReturnOnlyFirstDifference() {
        // prepare testData
        final String control = "<a><b attr1=\"abc\" attr2=\"def\"></b></a>";
        final String test = "<a><b attr1=\"uvw\" attr2=\"xyz\"></b></a>";

        // run test
        Diff myDiff = DiffBuilder.compare(control).withTest(test)
                .withComparisonController(ComparisonControllers.StopWhenDifferent)
                .build();

        // validate result
        Assert.assertTrue(myDiff.hasDifferences());
        assertThat(count(myDiff.getDifferences()), is(1));
    }

}
