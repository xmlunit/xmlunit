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
import org.xmlunit.TestResources;

import java.io.File;

import static org.xmlunit.assertj.ExpectedException.none;
import static org.xmlunit.assertj.XmlAssert.assertThat;

public class CompareAssertAreNotSimilarTest {

    @Rule
    public ExpectedException thrown = none();

    @Test
    public void testAreNotSimilar_shouldPass() {

        String testXml = "<a><c/><b/></a>";
        String controlXml = "<a><b/><c/></a>";

        assertThat(testXml).and(controlXml).areNotSimilar();
    }

    @Test
    public void testAreNotSimilar_fromFiles_shouldPass() {

        File testXml = new File(TestResources.ANIMAL_FILE);
        File controlXml = new File(TestResources.TEST_RESOURCE_DIR + "test2.xml");

        assertThat(testXml).and(controlXml).areNotSimilar();
    }

    @Test
    public void testAreNotSimilar_withIdenticalXmls_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <control instance> and <test instance> to be not similar");

        String testXml = "<Element attr1=\"12\" attr2=\"xy\"/>";
        String controlXml = "<Element attr1=\"12\" attr2=\"xy\"/>";

        assertThat(testXml).and(controlXml).areNotSimilar();
    }

    @Test
    public void testAreNotSimilar_withSimilarXmls_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <control instance> and <test instance> to be not similar");

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

        assertThat(testXml).and(controlXml).areNotSimilar();
    }

    @Test
    public void testAreNotSimilar_withSimilarXmlsIgnoreChildNodesOrder_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <control instance> and <test instance> to be not similar");

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
                .areNotSimilar();
    }

    @Test
    public void testAreNotSimilar_withIgnoreComments_shouldFailed() {

        thrown.expectAssertionError("Expecting:%n <control instance> and <test instance> to be not similar");

        String testXml = "<a><!-- test --></a>";
        String controlXml = "<a></a>";

        assertThat(testXml).and(controlXml)
                .ignoreComments()
                .areNotSimilar();
    }

    @Test
    public void testAreNotSimilar_withInvalidTestXml_shouldFailed() {

        thrown.expectAssertionErrorPattern(".*Expecting code not to raise a throwable but caught.*Caught exception during comparison.*");

        String testXml = "abc";
        String controlXml = "<a><b/><c/></a>";

        assertThat(testXml).and(controlXml).areNotSimilar();
    }

    @Test
    public void testAreNotSimilar_withInvalidControlXml_shouldFailed() {

        thrown.expectAssertionErrorPattern(".*Expecting code not to raise a throwable but caught.*Caught exception during comparison.*");

        String testXml = "<a><b/><c/></a>";
        String controlXml = "abc";

        assertThat(testXml).and(controlXml).areNotSimilar();
    }
}
