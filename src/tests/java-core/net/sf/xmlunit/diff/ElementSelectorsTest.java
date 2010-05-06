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
package net.sf.xmlunit.diff;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import static org.junit.Assert.*;

public class ElementSelectorsTest {
    private static final String FOO = "foo";
    private static final String BAR = "bar";
    private static final String SOME_URI = "urn:some:uri";

    private Document doc;

    @Before public void createDoc() throws Exception {
        doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .newDocument();
    }

    private void pureElementNameComparisons(ElementSelector s) {
        Element control = doc.createElement(FOO);
        Element equal = doc.createElement(FOO);
        Element different = doc.createElement(BAR);
        Element controlNS = doc.createElementNS(SOME_URI, FOO);
        controlNS.setPrefix(BAR);

        assertFalse(s.canBeCompared(null, null));
        assertFalse(s.canBeCompared(null, control));
        assertFalse(s.canBeCompared(control, null));
        assertTrue(s.canBeCompared(control, equal));
        assertFalse(s.canBeCompared(control, different));
        assertFalse(s.canBeCompared(control, controlNS));
        assertTrue(s.canBeCompared(doc.createElementNS(SOME_URI, FOO),
                                   controlNS));
    }

    @Test public void byName() {
        pureElementNameComparisons(ElementSelectors.byName);
    }

    @Test public void byNameAndText_NamePart() {
        pureElementNameComparisons(ElementSelectors.byNameAndText);
    }

    private void byNameAndText_SingleLevel(ElementSelector s) {
        Element control = doc.createElement(FOO);
        control.appendChild(doc.createTextNode(BAR));
        Element equal = doc.createElement(FOO);
        equal.appendChild(doc.createTextNode(BAR));
        Element equalC = doc.createElement(FOO);
        equalC.appendChild(doc.createCDATASection(BAR));
        Element noText = doc.createElement(FOO);
        Element differentText = doc.createElement(FOO);
        differentText.appendChild(doc.createTextNode(BAR));
        differentText.appendChild(doc.createTextNode(BAR));

        assertTrue(s.canBeCompared(control, equal));
        assertTrue(s.canBeCompared(control, equalC));
        assertFalse(s.canBeCompared(control, noText));
        assertFalse(s.canBeCompared(control, differentText));
    }

    @Test public void byNameAndText() {
        byNameAndText_SingleLevel(ElementSelectors.byNameAndText);
    }

    @Test public void byNameAndTextRec_NamePart() {
        pureElementNameComparisons(ElementSelectors.byNameAndTextRec);
    }

    @Test public void byNameAndTextRec_Single() {
        byNameAndText_SingleLevel(ElementSelectors.byNameAndTextRec);
    }

    @Test public void byNameAndTextRec() {
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

        ElementSelector s = ElementSelectors.byNameAndTextRec;
        assertTrue(s.canBeCompared(control, equal));
        assertTrue(s.canBeCompared(control, equalC));
        assertFalse(s.canBeCompared(control, noText));
        assertFalse(s.canBeCompared(control, differentLevel));
        assertFalse(s.canBeCompared(control, differentElement));
        assertFalse(s.canBeCompared(control, differentText));
    }

    @Test public void byNameAndAllAttributes_NamePart() {
        pureElementNameComparisons(ElementSelectors.byNameAndAllAttributes);
    }

    @Test public void byNameAndAllAttributes() {
        Element control = doc.createElement(FOO);
        control.setAttribute(BAR, BAR);
        Element equal = doc.createElement(FOO);
        equal.setAttribute(BAR, BAR);
        Element noAttributes = doc.createElement(FOO);
        Element differentValue = doc.createElement(FOO);
        differentValue.setAttribute(BAR, FOO);
        Element differentName = doc.createElement(FOO);
        differentName.setAttribute(FOO, FOO);
        Element differentNS = doc.createElement(FOO);
        differentNS.setAttributeNS(SOME_URI, BAR, BAR);

        assertTrue(ElementSelectors.byNameAndAllAttributes
                   .canBeCompared(control, equal));
        assertFalse(ElementSelectors.byNameAndAllAttributes
                   .canBeCompared(control, noAttributes));
        assertFalse(ElementSelectors.byNameAndAllAttributes
                    .canBeCompared(noAttributes, control));
        assertFalse(ElementSelectors.byNameAndAllAttributes
                   .canBeCompared(control, differentValue));
        assertFalse(ElementSelectors.byNameAndAllAttributes
                   .canBeCompared(control, differentName));
        assertFalse(ElementSelectors.byNameAndAllAttributes
                   .canBeCompared(control, differentNS));
    }

    @Test public void byNameAndAttributes_NamePart() {
        pureElementNameComparisons(ElementSelectors
                                   .byNameAndAttributes(new String[] {}));
        pureElementNameComparisons(ElementSelectors
                                   .byNameAndAttributes(new QName[] {}));
        pureElementNameComparisons(ElementSelectors.byNameAndAttributes(BAR));
        pureElementNameComparisons(ElementSelectors
                                   .byNameAndAttributes(new QName(SOME_URI,
                                                                  BAR)));
    }

    @Test public void byNameAndAttributes_String() {
        Element control = doc.createElement(FOO);
        control.setAttribute(BAR, BAR);
        Element equal = doc.createElement(FOO);
        equal.setAttribute(BAR, BAR);
        Element noAttributes = doc.createElement(FOO);
        Element differentValue = doc.createElement(FOO);
        differentValue.setAttribute(BAR, FOO);
        Element differentName = doc.createElement(FOO);
        differentName.setAttribute(FOO, FOO);
        Element differentNS = doc.createElement(FOO);
        differentNS.setAttributeNS(SOME_URI, BAR, BAR);

        assertTrue(ElementSelectors.byNameAndAttributes(BAR)
                   .canBeCompared(control, equal));
        assertFalse(ElementSelectors.byNameAndAttributes(BAR)
                   .canBeCompared(control, noAttributes));
        assertTrue(ElementSelectors.byNameAndAttributes(FOO)
                   .canBeCompared(control, noAttributes));
        assertTrue(ElementSelectors.byNameAndAttributes(new String[] {})
                   .canBeCompared(control, noAttributes));
        assertFalse(ElementSelectors.byNameAndAttributes(BAR)
                    .canBeCompared(noAttributes, control));
        assertFalse(ElementSelectors.byNameAndAttributes(BAR)
                   .canBeCompared(control, differentValue));
        assertFalse(ElementSelectors.byNameAndAttributes(BAR)
                   .canBeCompared(control, differentName));
        assertFalse(ElementSelectors.byNameAndAttributes(BAR)
                   .canBeCompared(control, differentNS));
    }

    @Test public void byNameAndAttributes_QName() {
        Element control = doc.createElement(FOO);
        control.setAttribute(BAR, BAR);
        Element equal = doc.createElement(FOO);
        equal.setAttribute(BAR, BAR);
        Element noAttributes = doc.createElement(FOO);
        Element differentValue = doc.createElement(FOO);
        differentValue.setAttribute(BAR, FOO);
        Element differentName = doc.createElement(FOO);
        differentName.setAttribute(FOO, FOO);
        Element differentNS = doc.createElement(FOO);
        differentNS.setAttributeNS(SOME_URI, BAR, BAR);

        assertTrue(ElementSelectors.byNameAndAttributes(new QName(BAR))
                   .canBeCompared(control, equal));
        assertFalse(ElementSelectors.byNameAndAttributes(new QName(BAR))
                   .canBeCompared(control, noAttributes));
        assertTrue(ElementSelectors.byNameAndAttributes(new QName(FOO))
                   .canBeCompared(control, noAttributes));
        assertTrue(ElementSelectors.byNameAndAttributes(new QName[] {})
                   .canBeCompared(control, noAttributes));
        assertFalse(ElementSelectors.byNameAndAttributes(new QName(BAR))
                    .canBeCompared(noAttributes, control));
        assertFalse(ElementSelectors.byNameAndAttributes(new QName(BAR))
                   .canBeCompared(control, differentValue));
        assertFalse(ElementSelectors.byNameAndAttributes(new QName(BAR))
                   .canBeCompared(control, differentName));
        assertFalse(ElementSelectors.byNameAndAttributes(new QName(BAR))
                   .canBeCompared(control, differentNS));
    }

    @Test public void byNameAndAttributesControlNS_NamePart() {
        pureElementNameComparisons(ElementSelectors
                                   .byNameAndAttributesControlNS());
        pureElementNameComparisons(ElementSelectors
                                   .byNameAndAttributesControlNS(BAR));
    }

    @Test public void byNameAndAttributesControlNS() {
        Element control = doc.createElement(FOO);
        control.setAttributeNS(SOME_URI, BAR, BAR);
        Element equal = doc.createElement(FOO);
        equal.setAttributeNS(SOME_URI, BAR, BAR);
        Element noAttributes = doc.createElement(FOO);
        Element differentValue = doc.createElement(FOO);
        differentValue.setAttributeNS(SOME_URI, BAR, FOO);
        Element differentName = doc.createElement(FOO);
        differentName.setAttributeNS(SOME_URI, FOO, FOO);
        Element differentNS = doc.createElement(FOO);
        differentNS.setAttributeNS(SOME_URI + "2", BAR, BAR);
        Element noNS = doc.createElement(FOO);
        noNS.setAttribute(BAR, BAR);

        assertTrue(ElementSelectors.byNameAndAttributesControlNS(BAR)
                   .canBeCompared(control, equal));
        assertFalse(ElementSelectors.byNameAndAttributesControlNS(BAR)
                   .canBeCompared(control, noAttributes));
        assertTrue(ElementSelectors.byNameAndAttributesControlNS(FOO)
                   .canBeCompared(control, noAttributes));
        assertTrue(ElementSelectors.byNameAndAttributesControlNS(new String[] {})
                   .canBeCompared(control, noAttributes));
        assertTrue(ElementSelectors.byNameAndAttributesControlNS(BAR)
                   .canBeCompared(noAttributes, control));
        assertFalse(ElementSelectors.byNameAndAttributesControlNS(BAR)
                    .canBeCompared(noAttributes, noNS));
        assertFalse(ElementSelectors.byNameAndAttributesControlNS(BAR)
                   .canBeCompared(control, differentValue));
        assertFalse(ElementSelectors.byNameAndAttributesControlNS(BAR)
                   .canBeCompared(control, differentName));
        assertFalse(ElementSelectors.byNameAndAttributesControlNS(BAR)
                   .canBeCompared(control, differentNS));
        assertFalse(ElementSelectors.byNameAndAttributesControlNS(BAR)
                   .canBeCompared(control, noNS));
    }

}
