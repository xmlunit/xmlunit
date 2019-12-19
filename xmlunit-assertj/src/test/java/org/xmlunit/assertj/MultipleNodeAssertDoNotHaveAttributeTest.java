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
import org.mockito.Mockito;

import javax.xml.xpath.XPathFactory;

import static java.lang.String.format;
import static org.xmlunit.assertj.ExpectedException.none;
import static org.xmlunit.assertj.XmlAssert.assertThat;

public class MultipleNodeAssertDoNotHaveAttributeTest {

    @Rule
    public ExpectedException thrown = none();

    @Test
    public void testDoNotHaveAttribute_withAnyValue_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr1=\"value\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr2=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .doNotHaveAttribute("attr");
    }

    @Test
    public void testDoNotHaveAttribute_withValue_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr1=\"value\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr=\"abc\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .doNotHaveAttribute("attr", "value");
    }

    @Test
    public void testDoNotHaveAttribute_withMultipleAttributes_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry abc=\"value1\" aaa=\"def\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <title>title</title>" +
                "   <entry xyz=\"value4\" bbb=\"aaa\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .doNotHaveAttribute("attr1")
                .doNotHaveAttribute("attr2");
    }

    @Test
    public void testDoNotHaveAttribute_withMultipleAttributeWithValues_shouldPass() {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr1=\"abc\" xyz=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry def=\"value2\" attr1=\"ghi\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .doNotHaveAttribute("attr1", "value1")
                .doNotHaveAttribute("attr2", "value2");
    }

    @Test
    public void testDoNotHaveAttribute_withAnyValue_shouldFailed() {

        thrown.expectAssertionError("check node at index 1");
        thrown.expectAssertionError(format("Expecting:%n <entry>%nnot to have attribute:%n <attr>"));

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry abc=\"value\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .hasXPath("/feed/entry")
                .doNotHaveAttribute("attr");
    }

    @Test
    public void testDoNotHaveAttribute_withValue_shouldFailed() {

        thrown.expectAssertionError("check node at index 1");
        thrown.expectAssertionError(format("Expecting:%n <entry>%nnot to have attribute:%n <attr>%nwith value:%n <value>"));

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr=\"value\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .hasXPath("/feed/entry")
                .doNotHaveAttribute("attr", "value");
    }

    @Test
    public void testDoNotHaveAttribute_withMultipleAttributes_shouldFailed() {

        thrown.expectAssertionError("check node at index 2");
        thrown.expectAssertionError(format("Expecting:%n <entry>%nnot to have attribute:%n <attr1>"));

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry abc=\"value1\" def=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <title>title</title>" +
                "   <entry>" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr1=\"value5\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .doNotHaveAttribute("attr1")
                .doNotHaveAttribute("attr2");
    }

    @Test
    public void testDoNotHaveAttribute_withMultipleAttributeWithValues_shouldFailed() {

        thrown.expectAssertionError("check node at index 2");
        thrown.expectAssertionError(format("Expecting:%n <entry>%nnot to have attribute:%n <attr2>%nwith value:%n <value2>"));

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr1=\"value2\" attr2=\"value1\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry abc=\"value1\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr1=\"VALUE1\" attr2=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr1=\"value2\" attr2=\"value1\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/entry")
                .doNotHaveAttribute("attr1", "value1")
                .doNotHaveAttribute("attr2", "value2");
    }

    @Test
    public void testNotHaveAttribute_forEmptyNodeSet_shouldPass() {


        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<feed>" +
                "   <title>title</title>" +
                "   <entry attr1=\"value1\" attr2=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "   <entry attr1=\"value1\" attr2=\"value2\">" +
                "       <title>title1</title>" +
                "   </entry>" +
                "</feed>";

        assertThat(xml)
                .nodesByXPath("/feed/abc")
                .doNotHaveAttribute("attr1")
                .doNotHaveAttribute("attr2", "value2");
    }

    @Test
    public void usesXPathEngine() {
        XPathFactory xFac = Mockito.mock(XPathFactory.class);
        Mockito.when(xFac.newXPath()).thenReturn(XPathFactory.newInstance().newXPath());
        assertThat("<foo/>")
                .withXPathFactory(xFac)
                .nodesByXPath("//bar")
                .doNotHaveAttribute("attr1");
        Mockito.verify(xFac).newXPath();
    }
}
