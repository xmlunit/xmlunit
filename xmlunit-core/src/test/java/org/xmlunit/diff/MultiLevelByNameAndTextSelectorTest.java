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
package org.xmlunit.diff;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.junit.Assert.*;
import static org.xmlunit.diff.ElementSelectorsTest.*;

public class MultiLevelByNameAndTextSelectorTest {

    private Document doc;

    @Before
    public void createDoc() throws Exception {
        doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .newDocument();
    }

    @Test
    public void singleLevel() {
        byNameAndText_SingleLevel(new MultiLevelByNameAndTextSelector(1), doc);
    }

    @Test
    public void byNameAndTextRec() {
        Element control = doc.createElement(FOO);
        Element child = doc.createElement(BAR);
        control.appendChild(child);
        child.appendChild(doc.createTextNode(BAR));
        Element equal = doc.createElement(FOO);
        Element child2 = doc.createElement(BAR);
        equal.appendChild(child2);
        child2.appendChild(doc.createTextNode(BAR));
        Element equalC = doc.createElement(FOO);
        Element child3 = doc.createElement(BAR);
        equalC.appendChild(child3);
        child3.appendChild(doc.createCDATASection(BAR));
        Element noText = doc.createElement(FOO);
        Element differentLevel = doc.createElement(FOO);
        differentLevel.appendChild(doc.createTextNode(BAR));
        Element differentElement = doc.createElement(FOO);
        Element child4 = doc.createElement(FOO);
        differentElement.appendChild(child4);
        child4.appendChild(doc.createTextNode(BAR));
        Element differentText = doc.createElement(FOO);
        Element child5 = doc.createElement(BAR);
        differentText.appendChild(child5);
        child5.appendChild(doc.createTextNode(FOO));

        ElementSelector s = new MultiLevelByNameAndTextSelector(2);
        assertTrue(s.canBeCompared(control, equal));
        assertTrue(s.canBeCompared(control, equalC));
        assertFalse(s.canBeCompared(control, noText));
        assertFalse(s.canBeCompared(control, differentLevel));
        assertFalse(s.canBeCompared(control, differentElement));
        assertFalse(s.canBeCompared(control, differentText));
    }

    @Test
    public void emptyTexts() {
        Element control = doc.createElement(FOO);
        Element child = doc.createElement(BAR);
        control.appendChild(doc.createTextNode(""));
        control.appendChild(child);
        child.appendChild(doc.createTextNode(BAR));
        Element test = doc.createElement(FOO);
        Element child2 = doc.createElement(BAR);
        test.appendChild(child2);
        child2.appendChild(doc.createTextNode(BAR));

        ElementSelector s = new MultiLevelByNameAndTextSelector(2);
        assertFalse(new MultiLevelByNameAndTextSelector(2)
                    .canBeCompared(control, test));
        assertTrue(new MultiLevelByNameAndTextSelector(2, true)
                   .canBeCompared(control, test));
    }

}
