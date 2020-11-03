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
package org.xmlunit.assertj3;

import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.xmlunit.assertj3.ExpectedException.none;
import static org.xmlunit.assertj3.XmlAssert.assertThat;

public class XmlAssertDoesNotHaveXPathTest {

    @Rule
    public ExpectedException thrown = none();

    @Test
    public void testDoesNotHaveXPath_withNotExistingXPath_shouldPass() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<fruits>" +
                "   <fruit name=\"apple\"/>" +
                "   <fruit name=\"orange\"/>" +
                "   <fruit name=\"banana\"/>" +
                "</fruits>";

        assertThat(xml).doesNotHaveXPath("//entry/id");
    }

    @Test
    public void testDoesNotHaveXPath_withExistingXPath_shouldFailed() {
        thrown.expectAssertionError("Expecting empty but was");

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<fruits>" +
                "   <fruit name=\"apple\"/>" +
                "   <fruit name=\"orange\"/>" +
                "   <fruit name=\"banana\"/>" +
                "</fruits>";

        assertThat(xml).doesNotHaveXPath("/fruits/fruit");
    }

    @Test
    public void testDoesNotHaveXPath_withAttribute_shouldPass() {
        String xml = "<a><b attr=\"abc\"></b></a>";

        assertThat(xml).doesNotHaveXPath("//a/b[@attr=\"xyz\"]");
    }

    @Test
    public void testDoesNotHaveXPath_withAttribute_shouldFailed() {
        thrown.expectAssertionError("Expecting empty but was");

        String xml = "<a><b attr=\"abc\"></b></a>";

        assertThat(xml).doesNotHaveXPath("//a/b[@attr=\"abc\"]");
    }

    @Test
    public void testDoesNotHaveXPath_withNamespacesContext_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed xmlns=\"http://www.w3.org/2005/Atom\">" +
                "   <title>title</title>" +
                "   <entry>" +
                "       <title>title1</title>" +
                "       <id>id1</id>" +
                "   </entry>" +
                "</feed>";

        Map<String, String> prefix2Uri = new HashMap<String, String>();
        prefix2Uri.put("atom", "http://www.w3.org/2005/Atom");

        assertThat(xml)
                .withNamespaceContext(prefix2Uri)
                .doesNotHaveXPath("//atom:feed/atom:entry/atom:description");
    }


    @Test
    public void testDoesNotHaveXPath_withInvalidXML_shouldFailed() {

        thrown.expectAssertionError("Expecting code not to raise a throwable but caught");

        String xml = "<b>not empty</a>";

        assertThat(xml).doesNotHaveXPath("//atom:feed/atom:entry/atom:id");
    }
}
