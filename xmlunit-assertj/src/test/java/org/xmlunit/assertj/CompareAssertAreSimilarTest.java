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
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xmlunit.TestResources;
import org.xmlunit.diff.Comparison;
import org.xmlunit.diff.ComparisonController;
import org.xmlunit.diff.ComparisonListener;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.ComparisonType;
import org.xmlunit.diff.Difference;
import org.xmlunit.diff.DifferenceEvaluator;
import org.xmlunit.diff.DifferenceEvaluators;

import java.io.File;

import static org.xmlunit.assertj.ExpectedException.none;
import static org.xmlunit.assertj.XmlAssert.assertThat;
import static org.xmlunit.diff.ComparisonType.ATTR_VALUE;
import static org.xmlunit.diff.ComparisonType.ELEMENT_TAG_NAME;
import static org.xmlunit.diff.DifferenceEvaluators.chain;

public class CompareAssertAreSimilarTest {

    @Rule
    public ExpectedException thrown = none();

    @Test
    public void testAreSimilar_shouldPass() {

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

        assertThat(testXml).and(controlXml).areSimilar();
    }

    @Test
    public void testAreSimilar_withIgnoreChildNodesOrder_shouldPass() {

        String testXml = "<!DOCTYPE a>" +
                "<a>" +
                "   <c><d/><e/></c>" +
                "   <b>text</b>" +
                "</a>";

        String controlXml = "" +
                "<a>" +
                "   <b><![CDATA[text]]></b>" +
                "   <c><e/><d/></c>" +
                "</a>";

        assertThat(testXml).and(controlXml)
                .ignoreChildNodesOrder()
                .areSimilar();
    }

    @Test
    public void testAreSimilar_fromFiles_shouldFailed() {

        thrown.expectAssertionErrorPattern(".*Expecting:.*<(?:.*test2\\.xml)> and <(?:.*test1\\.xml)> to be similar.*");
        thrown.expectAssertionError("Expected processing instruction data 'href=\"animal.xsl\" type=\"text/xsl\"' but was 'type=\"text/xsl\" href=\"animal.xsl\"");

        File testXml = new File(TestResources.ANIMAL_FILE);
        File controlXml = new File(TestResources.TEST_RESOURCE_DIR + "test2.xml");

        assertThat(testXml).and(controlXml).areSimilar();
    }

    @Test
    public void testAreSimilar_withInvalidTestXml_shouldFailed() {

        thrown.expectAssertionErrorPattern(".*Expecting code not to raise a throwable but caught.*Caught exception during comparison.*");

        String testXml = "abc";
        String controlXml = "<a><b/><c/></a>";

        assertThat(testXml).and(controlXml).areSimilar();
    }

    @Test
    public void testAreSimilar_withInvalidControlXml_shouldFailed() {

        thrown.expectAssertionErrorPattern(".*Expecting code not to raise a throwable but caught.*Caught exception during comparison.*");

        String testXml = "<a><b/><c/></a>";
        String controlXml = "abc";

        assertThat(testXml).and(controlXml).areSimilar();
    }

    @Test
    public void testAreSimilar_withDifferenceEvaluator_shouldPass() {

        String testXml = "<a><b attr=\"abc\"></b></a>";
        String controlXml = "<a><b attr=\"xyz\"></b></a>";

        assertThat(testXml).and(controlXml)
                .withDifferenceEvaluator(
                        chain(DifferenceEvaluators.Default,
                                new IgnoreAttributeValueEvaluator("attr")))
                .areSimilar();
    }

    @Test
    public void testAreSimilar_withComparisonController_shouldPass() {

        String testXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry>" +
                "       <title>title1</title>" +
                "       <id>id1</id>" +
                "   </entry>" +
                "</feed>";

        String controlXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <invalidEntry>" +
                "       <description>description</description>" +
                "       <uuid>uuid1</uuid>" +
                "   </invalidEntry>" +
                "</feed>";

        assertThat(testXml).and(controlXml)
                .withDifferenceEvaluator(new IgnoreEntryEvaluator())
                .withComparisonController(new StopAfterNode("invalidEntry"))
                .areSimilar();
    }

    @Test
    public void testAreSimilar_withComparisonListeners_shouldPass() {

        SimilarityComparisonListener comparisonListener = new SimilarityComparisonListener();
        String controlXml = "<a><b>Test Value</b><c><![CDATA[ABC]]></c></a>";
        String testXml = "<a><b><![CDATA[Test Value]]></b><c>ABC</c></a>";

        assertThat(testXml).and(controlXml)
                .withComparisonListeners(comparisonListener)
                .areSimilar();

        assertThat(comparisonListener.similars).isEqualTo(2);
    }


    private final class IgnoreAttributeValueEvaluator implements DifferenceEvaluator {

        private final String attributeName;

        public IgnoreAttributeValueEvaluator(String attributeName) {
            this.attributeName = attributeName;
        }

        @Override
        public ComparisonResult evaluate(Comparison comparison, ComparisonResult outcome) {
            final Node controlNode = comparison.getControlDetails().getTarget();
            if (comparison.getType() == ATTR_VALUE && controlNode instanceof Attr) {
                Attr attr = (Attr) controlNode;
                if (attr.getName().equals(attributeName)) {
                    return ComparisonResult.SIMILAR;
                }
            }
            return outcome;
        }
    }

    private final class IgnoreEntryEvaluator implements DifferenceEvaluator {

        @Override
        public ComparisonResult evaluate(Comparison comparison, ComparisonResult outcome) {
            final Node controlNode = comparison.getControlDetails().getTarget();
            if (controlNode instanceof Element) {
                Element elem = (Element) controlNode;
                if (elem.getTagName().toLowerCase().contains("entry")) {
                    return ComparisonResult.SIMILAR;
                }
            }
            return outcome;
        }
    }

    private final class StopAfterNode implements ComparisonController {

        private final String nodeName;

        private StopAfterNode(String nodeName) {
            this.nodeName = nodeName;
        }

        @Override
        public boolean stopDiffing(Difference difference) {
            ComparisonType type = difference.getComparison().getType();
            if (type == ELEMENT_TAG_NAME) {
                String valueControl = (String) difference.getComparison().getControlDetails().getValue();
                String valueTest = (String) difference.getComparison().getTestDetails().getValue();

                return nodeName.equals(valueTest) || nodeName.equals(valueControl);
            }
            return false;
        }
    }

    private final class SimilarityComparisonListener implements ComparisonListener {

        private int similars;

        @Override
        public void comparisonPerformed(Comparison comparison, ComparisonResult outcome) {
            switch (outcome) {
                case SIMILAR:
                    similars++;
                    break;
            }
        }
    }
}
