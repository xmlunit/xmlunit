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

import static org.xmlunit.assertj.ExpectedException.none;
import static org.xmlunit.assertj.XmlAssert.assertThat;

public class XmlAssertAndTest {

    @Rule
    public ExpectedException thrown = none();

    @Test
    public void testAnd_withNull_shouldFailed() {

        thrown.expectAssertionError("Expecting control not to be null");

        String testXml = "<!DOCTYPE a>" +
                "<a xmlns:xyz=\"https://www.xmlunit.com/xyz\">" +
                "   <b>text</b>" +
                "   <c>" +
                "      <d/>" +
                "      <xyz:e/>" +
                "   </c>" +
                "</a>";


        assertThat(testXml).and(null);
    }

    @Test
    public void testAnd_withWhitespacesOnly_shouldPass() {

        String testXml = "<a><b></b><c/></a>";

        assertThat(testXml).and(" \n \t");
    }
}
