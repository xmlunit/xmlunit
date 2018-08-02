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
package org.xmlunit.assertj;

import org.junit.Rule;
import org.junit.Test;
import org.xmlunit.diff.Comparison;
import org.xmlunit.diff.ComparisonListener;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.ElementSelectors;

import static org.xmlunit.assertj.ExpectedException.none;
import static org.xmlunit.assertj.XmlAssert.assertThat;

public class CompareAssertAreNotIdenticalTest {

    @Rule
    public ExpectedException thrown = none();

    @Test
    public void testAreNotIdentical_withSameAttributesOrder_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <control instance> and <test instance> to be not identical");

        String testXml = "<Element attr1=\"12\" attr2=\"xy\"/>";
        String controlXml = "<Element attr1=\"12\" attr2=\"xy\"/>";

        assertThat(testXml).and(controlXml).areNotIdentical();
    }

    @Test
    public void testAreNotIdentical_withDifferentAttributesOrder_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <control instance> and <test instance> to be not identical");

        String testXml = "<Element attr2=\"xy\" attr1=\"12\"/>";
        String controlXml = "<Element attr1=\"12\" attr2=\"xy\"/>";

        assertThat(testXml).and(controlXml).areNotIdentical();
    }

    @Test
    public void testAreNotIdentical_withCDataInsteadText_shouldPass() {

        String testXml = "<!DOCTYPE a>" +
                "<a xmlns:xyz=\"https://www.xmlunit.com/xyz\">" +
                "   <b>text</b>" +
                "   <c>" +
                "      <d/>" +
                "      <xyz:e/>" +
                "   </c>" +
                "</a>";

        String controlXml = "" +
                "<a xmlns:vwy=\"https://www.xmlunit.com/xyz\">" +
                "   <b><![CDATA[text]]></b>" +
                "   <c>" +
                "      <d/>" +
                "      <vwy:e/>" +
                "   </c>" +
                "</a>";

        assertThat(testXml).and(controlXml).areNotIdentical();
    }

    @Test
    public void testAreNotIdentical_withAttributeDifferentValues_shouldPass() {

        String testXml = "<Element attr1=\"12\" attr2=\"xyz\"/>";
        String controlXml = "<Element attr1=\"12\" attr2=\"xy\"/>";

        assertThat(testXml).and(controlXml).areNotIdentical();
    }

    @Test
    public void testAreNotIdentical_withDifferentElementOrder_shouldPass() {

        String testXml = "<a><c/><b/></a>";
        String controlXml = "<a><b/><c/></a>";

        assertThat(testXml).and(controlXml)
                .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText))
                .areNotIdentical();
    }

    @Test
    public void testAreNotIdentical_withWhitespaces_shouldPass() {

        String testXml = String.format("<a>%n <b/>%n</a>");
        String controlXml = "<a><b/></a>";

        assertThat(testXml).and(controlXml).areNotIdentical();
    }

    @Test
    public void testAreNotIdentical_withIgnoreWhitespaces_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <control instance> and <test instance> to be not identical");

        String testXml = String.format("<a>%n <b/>%n</a>");
        String controlXml = "<a><b/></a>";

        assertThat(testXml).and(controlXml)
                .ignoreWhitespace()
                .areNotIdentical();
    }

    @Test
    public void testAreNotIdentical_withIgnoreWhitespaceAndTextValue_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <control instance> and <test instance> to be not identical");

        String testXml = String.format("<a>%nX <b/>%n</a>");
        String controlXml = "<a>X<b/></a>";

        assertThat(testXml).and(controlXml)
                .ignoreWhitespace()
                .areNotIdentical();
    }

    @Test
    public void testAreNotIdentical_withIgnoreElementContentWhitespace_shouldPass() {

        String testXml = String.format("<a>%nx <b/>%n</a>");
        String controlXml = "<a>x<b/></a>";

        assertThat(testXml).and(controlXml)
                .ignoreElementContentWhitespace()
                .areNotIdentical();
    }

    @Test
    public void testAreNotIdentical_withIgnoreComments_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <control instance> and <test instance> to be not identical");

        String testXml = "<a><!-- test --></a>";
        String controlXml = "<a></a>";

        assertThat(testXml).and(controlXml)
                .ignoreComments()
                .areNotIdentical();
    }

    @Test
    public void testAreNotIdentical_withNormalizeWhitespace_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <control instance> and <test instance> to be not identical");

        String testXml = String.format("<a>%n  <b>%n  Test%n  Node%n  </b>%n</a>");
        String controlXml = "<a><b>Test Node</b></a>";

        assertThat(testXml).and(controlXml)
                .normalizeWhitespace()
                .areNotIdentical();
    }

    @Test
    public void testAreNotIdentical_withNormalizeWhitespace_shouldPass() {

        String testXml = String.format("<a>%n  <b>%n  Test%n  Node%n  </b>%n</a>");
        String controlXml = "<a><b>TestNode</b></a>";

        assertThat(testXml).and(controlXml)
                .normalizeWhitespace()
                .areNotIdentical();
    }

    @Test
    public void testAreNotIdentical_withInvalidTestXml_shouldFailed() {

        thrown.expectAssertionErrorPattern(".*Expecting code not to raise a throwable but caught.*Caught exception during comparison.*");

        String testXml = "abc";
        String controlXml = "<a><b/><c/></a>";

        assertThat(testXml).and(controlXml).areNotIdentical();
    }

    @Test
    public void testAreNotIdentical_withInvalidControlXml_shouldFailed() {

        thrown.expectAssertionErrorPattern(".*Expecting code not to raise a throwable but caught.*Caught exception during comparison.*");

        String testXml = "<a><b/><c/></a>";
        String controlXml = "abc";

        assertThat(testXml).and(controlXml).areNotIdentical();
    }

    @Test
    public void testIsSimilarTo_withDifferenceListener_shouldCollectChanges() {

        DifferenceComparisonListener differenceListener = new DifferenceComparisonListener();

        String testXml = "<Element attr1=\"12\" attr2=\"xyz\"/>";
        String controlXml = "<Element attr1=\"12\" attr2=\"xy\"/>";

        assertThat(testXml).and(controlXml)
                .withDifferenceListeners(differenceListener)
                .areNotIdentical();

        assertThat(differenceListener.difference).isEqualTo(1);
    }

    private final class DifferenceComparisonListener implements ComparisonListener {

        private int difference;

        @Override
        public void comparisonPerformed(Comparison comparison, ComparisonResult outcome) {
            switch (outcome) {
                case DIFFERENT:
                    difference++;
                    break;
            }
        }
    }
}
