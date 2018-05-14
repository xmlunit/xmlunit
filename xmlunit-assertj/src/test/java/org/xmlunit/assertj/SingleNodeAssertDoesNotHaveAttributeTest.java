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

import static java.lang.String.format;
import static org.xmlunit.assertj.ExpectedException.none;
import static org.xmlunit.assertj.XmlAssert.assertThat;

public class SingleNodeAssertDoesNotHaveAttributeTest {

    @Rule
    public ExpectedException thrown = none();

    @Test
    public void testDoesNotHaveAttribute_forNodeWithoutAttribute_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr=\"value\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/title")
                .first()
                .doesNotHaveAttribute("attr");
    }

    @Test
    public void testDoesNotHaveAttribute_withValue_forNodeWithoutAttribute_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr=\"value\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/title")
                .first()
                .doesNotHaveAttribute("attr", "value");
    }

    @Test
    public void testDoesNotHaveAttribute_forNodeWithAttribute_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry abc=\"value\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/title")
                .first()
                .doesNotHaveAttribute("attr");
    }

    @Test
    public void testDoesNotHaveAttribute_withValue_forNodeWithAttribute_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr=\"abc\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/title")
                .first()
                .doesNotHaveAttribute("attr", "value");
    }

    @Test
    public void testDoesNotHaveAttribute_forNodeWithMultipleAttributes_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry abc=\"value1\" xyz=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .first()
                .doesNotHaveAttribute("attr1")
                .doesNotHaveAttribute("attr2");
    }

    @Test
    public void testDoesNotHaveAttribute_withValue_forNodeWithMultipleAttributes_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr1=\"abc\" attr2=\"xyz\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .first()
                .doesNotHaveAttribute("attr1", "value1")
                .doesNotHaveAttribute("attr2", "value2");
    }

    @Test
    public void testDoesNotHaveAttribute_withMultipleMatchingNodes_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <entry attr1=\"xyz\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr2=\"abc\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr5=\"value3\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr4=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .first()
                .doesNotHaveAttribute("attr1", "value1");

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .last()
                .doesNotHaveAttribute("attr4", "value4");

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .element(2)
                .doesNotHaveAttribute("attr3", "value3");
    }

    @Test
    public void testDoesNotHaveAttribute_withAnyValue_shouldFailed() {

        thrown.expectAssertionError(format("Expecting:%n <entry>%nnot to have attribute:%n <attr>"));

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr=\"value\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .first()
                .doesNotHaveAttribute("attr");
    }

    @Test
    public void testDoesNotHaveAttribute_withValue_shouldFailed() {

        thrown.expectAssertionError(format("Expecting:%n <entry>%nnot to have attribute:%n <attr>%nwith value:%n <value>"));

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr=\"value\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .first()
                .doesNotHaveAttribute("attr", "value");
    }

    @Test
    public void testDoesNotHaveAttribute_withMultipleAttributes_shouldFailed() {

        thrown.expectAssertionError(format("Expecting:%n <entry>%nnot to have attribute:%n <attr2>%nwith value:%n <value2>"));

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr1=\"abc\" attr2=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .first()
                .doesNotHaveAttribute("attr1", "value1")
                .doesNotHaveAttribute("attr2", "value2");
    }

    @Test
    public void testDoesNotHaveAttribute_forFirstNode_shouldFailed() {

        thrown.expectAssertionError(format("Expecting:%n <entry>%nnot to have attribute:%n <attr1>%nwith value:%n <value1>"));

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <entry attr1=\"value1\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr2=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .first()
                .doesNotHaveAttribute("attr1", "value1");
    }

    @Test
    public void testDoesNotHaveAttribute_forLastNode_shouldFailed() {

        thrown.expectAssertionError(format("Expecting:%n <entry>%nnot to have attribute:%n <attr2>%nwith value:%n <value2>"));

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <entry attr1=\"value1\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr2=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .last()
                .doesNotHaveAttribute("attr2", "value2");
    }
}
