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
import org.xmlunit.builder.DiffBuilder;

import static org.junit.Assert.*;
import static org.xmlunit.diff.ElementSelectorsTest.*;

public class ByNameAndTextRecSelectorTest {

    private Document doc;

    @Before
    public void createDoc() throws Exception {
        doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .newDocument();
    }

    @Test
    public void byNameAndTextRec_NamePart() {
        pureElementNameComparisons(new ByNameAndTextRecSelector(), doc);
    }

    @Test
    public void byNameAndTextRec_Single() {
        byNameAndText_SingleLevel(new ByNameAndTextRecSelector(), doc);
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

        ElementSelector s = new ByNameAndTextRecSelector();
        assertTrue(s.canBeCompared(control, equal));
        assertTrue(s.canBeCompared(control, equalC));
        assertFalse(s.canBeCompared(control, noText));
        assertFalse(s.canBeCompared(control, differentLevel));
        assertFalse(s.canBeCompared(control, differentElement));
        assertFalse(s.canBeCompared(control, differentText));
    }

    @Test
    public void byNameAndTextRec_Multilevel() throws Exception {
        Document control = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder().newDocument();
        {
            Element root = control.createElement("root");
            control.appendChild(root);

            Element controlSub = control.createElement("sub");
            root.appendChild(controlSub);
            Element controlSubSubValue = control.createElement("value");
            controlSub.appendChild(controlSubSubValue);
            controlSubSubValue.appendChild(control.createTextNode("1"));
            controlSubSubValue = control.createElement("value");
            controlSub.appendChild(controlSubSubValue);
            controlSubSubValue.appendChild(control.createTextNode("2"));

            controlSub = control.createElement("sub");
            root.appendChild(controlSub);
            controlSubSubValue = control.createElement("value");
            controlSub.appendChild(controlSubSubValue);
            controlSubSubValue.appendChild(control.createTextNode("3"));
            controlSubSubValue = control.createElement("value");
            controlSub.appendChild(controlSubSubValue);
            controlSubSubValue.appendChild(control.createTextNode("4"));
        }
        Document test = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder().newDocument();
        {
            Element root = test.createElement("root");
            test.appendChild(root);
			
            Element testSub = test.createElement("sub");
            root.appendChild(testSub);
            Element testSubValue = test.createElement("value");
            testSub.appendChild(testSubValue);
            testSubValue.appendChild(test.createTextNode("1"));
            testSubValue = test.createElement("value");
            testSub.appendChild(testSubValue);
            testSubValue.appendChild(test.createTextNode("2"));

            testSub = test.createElement("sub");
            root.appendChild(testSub);
            testSubValue = test.createElement("value");
            testSub.appendChild(testSubValue);
            testSubValue.appendChild(test.createTextNode("4"));
            testSubValue = test.createElement("value");
            testSub.appendChild(testSubValue);
            testSubValue.appendChild(test.createTextNode("3"));
        }

        DiffBuilder builder = DiffBuilder.compare(control)
            .withTest(test).checkForSimilar()
            .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.or(new ByNameAndTextRecSelector(),
                                                                        ElementSelectors.byName)));
        Diff d = builder.build();
        assertTrue(d.toString(new DefaultComparisonFormatter()), d.hasDifferences());

        builder = DiffBuilder.compare(control)
            .withTest(test).checkForSimilar()
            .withNodeMatcher(new DefaultNodeMatcher(new ByNameAndTextRecSelector(),
                                                    ElementSelectors.byName));
        d = builder.build();
        assertFalse(d.toString(new DefaultComparisonFormatter()), d.hasDifferences());
    }
}
