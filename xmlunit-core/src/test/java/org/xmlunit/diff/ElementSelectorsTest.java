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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlunit.util.IsNullPredicate;
import org.xmlunit.util.Predicate;

public class ElementSelectorsTest {
    static final String FOO = "foo";
    static final String BAR = "bar";
    static final String SOME_URI = "urn:some:uri";

    private Document doc;

    @Before
    public void createDoc() throws Exception {
        doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .newDocument();
    }

    private void pureElementNameComparisons(ElementSelector s) {
        pureElementNameComparisons(s, doc);
    }

    static void pureElementNameComparisons(ElementSelector s, Document doc) {
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

    static void byNameAndText_SingleLevel(ElementSelector s, Document doc) {
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
        byNameAndText_SingleLevel(ElementSelectors.byNameAndText, doc);
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

    @Test
    public void not() {
        Element control = doc.createElement(FOO);
        Element equal = doc.createElement(FOO);
        Element different = doc.createElement(BAR);
        assertFalse(ElementSelectors.not(ElementSelectors.byName)
                    .canBeCompared(control, equal));
        assertTrue(ElementSelectors.not(ElementSelectors.byName)
                   .canBeCompared(control, different));
    }

    @Test
    public void or() {
        Element control = doc.createElement(FOO);
        Element test = doc.createElement(BAR);
        assertFalse(ElementSelectors.or(ElementSelectors.byName)
                    .canBeCompared(control, test));
        assertTrue(ElementSelectors.or(ElementSelectors.byName,
                                       ElementSelectors.Default)
                   .canBeCompared(control, test));
    }

    @Test
    public void and() {
        Element control = doc.createElement(FOO);
        control.setAttributeNS(SOME_URI, BAR, BAR);
        Element test = doc.createElement(FOO);
        assertTrue(ElementSelectors.and(ElementSelectors.byName)
                   .canBeCompared(control, test));
        assertTrue(ElementSelectors.and(ElementSelectors.byName,
                                        ElementSelectors.Default)
                   .canBeCompared(control, test));
        assertFalse(ElementSelectors.and(ElementSelectors.byName,
                                         ElementSelectors.Default,
                                         ElementSelectors.byNameAndAllAttributes)
                    .canBeCompared(control, test));
    }

    @Test
    public void xor() {
        Element control = doc.createElement(FOO);
        Element test = doc.createElement(BAR);
        Element test2 = doc.createElement(FOO);
        assertFalse(ElementSelectors.xor(ElementSelectors.byName,
                                         ElementSelectors.byNameAndAllAttributes)
                    .canBeCompared(control, test));
        assertTrue(ElementSelectors.xor(ElementSelectors.byName,
                                        ElementSelectors.Default)
                   .canBeCompared(control, test));
        assertFalse(ElementSelectors.xor(ElementSelectors.byName,
                                         ElementSelectors.Default)
                    .canBeCompared(control, test2));
    }

    @Test
    public void conditionalReturnsFalseIfConditionIsNotMet() {
        Element control = doc.createElement(FOO);
        Element test = doc.createElement(FOO);
        assertFalse(ElementSelectors.conditionalSelector(new Predicate<Object>() {
                @Override
                public boolean test(Object o) {
                    return false;
                }
            }, ElementSelectors.byName)
            .canBeCompared(control, test));
    }

    @Test
    public void conditionalAsksWrappedSelectorIfConditionIsMet() {
        Element control = doc.createElement(FOO);
        Element test = doc.createElement(BAR);
        Element test2 = doc.createElement(FOO);
        assertFalse(ElementSelectors.conditionalSelector(new Predicate<Object>() {
                @Override
                public boolean test(Object o) {
                    return true;
                }
            }, ElementSelectors.byName)
            .canBeCompared(control, test));
        assertTrue(ElementSelectors.conditionalSelector(new Predicate<Object>() {
                @Override
                public boolean test(Object o) {
                    return true;
                }
            }, ElementSelectors.byName)
            .canBeCompared(control, test2));
    }

    @Test
    public void plainStringNamed() {
        Element control = doc.createElement(FOO);
        Element controlNS = doc.createElementNS(SOME_URI, FOO);
        Element test = doc.createElement(FOO);
        Element testNS = doc.createElementNS(SOME_URI, FOO);
        assertFalse(ElementSelectors.selectorForElementNamed(BAR,
                                                             ElementSelectors.byName)
                    .canBeCompared(control, test));
        assertTrue(ElementSelectors.selectorForElementNamed(FOO,
                                                            ElementSelectors.byName)
                   .canBeCompared(control, test));
        assertTrue(ElementSelectors.selectorForElementNamed(FOO,
                                                            ElementSelectors.byName)
                   .canBeCompared(controlNS, testNS));
    }

    @Test
    public void qnameNamed() {
        Element control = doc.createElement(FOO);
        Element controlNS = doc.createElementNS(SOME_URI, FOO);
        Element test = doc.createElement(FOO);
        Element testNS = doc.createElementNS(SOME_URI, FOO);
        assertFalse(ElementSelectors.selectorForElementNamed(new QName(BAR),
                                                             ElementSelectors.byName)
                    .canBeCompared(control, test));
        assertTrue(ElementSelectors.selectorForElementNamed(new QName(FOO),
                                                            ElementSelectors.byName)
                   .canBeCompared(control, test));
        assertTrue(ElementSelectors.selectorForElementNamed(new QName(SOME_URI, FOO,
                                                                      XMLConstants.DEFAULT_NS_PREFIX),
                                                            ElementSelectors.byName)
                   .canBeCompared(controlNS, testNS));
    }

    @Test
    public void xpath() {
        String BAZ = "BAZ";
        String XYZZY1 = "xyzzy1";
        String XYZZY2 = "xyzzy2";

        Element control = doc.createElement(FOO);
        Element bar = doc.createElement(BAR);
        control.appendChild(bar);
        Element baz = doc.createElement(BAZ);
        bar.appendChild(baz);
        baz.appendChild(doc.createTextNode(XYZZY1));
        baz = doc.createElement(BAZ);
        bar.appendChild(baz);
        baz.appendChild(doc.createTextNode(XYZZY2));

        Element test = doc.createElement(FOO);
        bar = doc.createElement(BAR);
        test.appendChild(bar);
        baz = doc.createElement(BAZ);
        bar.appendChild(baz);
        baz.appendChild(doc.createTextNode(XYZZY2));
        baz = doc.createElement(BAZ);
        bar.appendChild(baz);
        baz.appendChild(doc.createTextNode(XYZZY1));

        Element test2 = doc.createElement(FOO);
        bar = doc.createElement(BAR);
        test2.appendChild(bar);
        baz = doc.createElement(BAZ);
        bar.appendChild(baz);
        baz.appendChild(doc.createTextNode(XYZZY2));
        baz = doc.createElement(BAZ);
        bar.appendChild(baz);
        baz.appendChild(doc.createTextNode(XYZZY2));

        assertTrue(ElementSelectors.byXPath(".//BAZ", ElementSelectors.byNameAndText)
                   .canBeCompared(control, test));
        assertFalse(ElementSelectors.byXPath(".//BAZ", ElementSelectors.byNameAndText)
                    .canBeCompared(control, test2));
    }

    @Test
    public void conditionalBuilder() {
        Element control = doc.createElement(FOO);
        Element test = doc.createElement(BAR);

        ElementSelectors.ConditionalSelectorBuilder builder =
            ElementSelectors.conditionalBuilder()
            .whenElementIsNamed(FOO).thenUse(ElementSelectors.byName);

        assertFalse(builder.build().canBeCompared(control, test));

        builder.elseUse(ElementSelectors.Default);
        // not or-ed
        assertFalse(builder.build().canBeCompared(control, test));

        Element control2 = doc.createElement("baz");
        assertTrue(builder.build().canBeCompared(control2, test));
    }

    @Test(expected = IllegalArgumentException.class)
    public void byNameAndAttributesDoesntLikeNullArgumentStringVersion() {
        ElementSelectors.byNameAndAttributes((String[]) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void byNameAndAttributesControlNSDoesntLikeNullArgument() {
        ElementSelectors.byNameAndAttributesControlNS((String[]) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void byNameAndAttributesDoesntLikeNullArgumentQNameVersion() {
        ElementSelectors.byNameAndAttributes((QName[]) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void byNameAndAttributesDoesntLikeNullNameStringVersion() {
        ElementSelectors.byNameAndAttributes(new String[] { null });
    }

    @Test(expected = IllegalArgumentException.class)
    public void byNameAndAttributesControlNSDoesntLikeNullName() {
        ElementSelectors.byNameAndAttributesControlNS(new String[] { null });
    }

    @Test(expected = IllegalArgumentException.class)
    public void byNameAndAttributesDoesntLikeNullNameQNameVersion() {
        ElementSelectors.byNameAndAttributes(new QName[] { null });
    }

    @Test(expected = IllegalArgumentException.class)
    public void notDoesntLikeNullElementSelector() {
        ElementSelectors.not(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void orDoesntLikeNullElementSelectorList() {
        ElementSelectors.or((ElementSelector[]) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void orDoesntLikeNullElementSelector() {
        ElementSelectors.or(new ElementSelector[] { null });
    }

    @Test(expected = IllegalArgumentException.class)
    public void andDoesntLikeNullElementSelectorList() {
        ElementSelectors.and((ElementSelector[]) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void andDoesntLikeNullElementSelector() {
        ElementSelectors.and(new ElementSelector[] { null });
    }

    @Test(expected = IllegalArgumentException.class)
    public void xorDoesntLikeNullElementSelector1() {
        ElementSelectors.xor(null, ElementSelectors.byName);
    }

    @Test(expected = IllegalArgumentException.class)
    public void xorDoesntLikeNullElementSelector2() {
        ElementSelectors.xor(ElementSelectors.byName, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void conditionalSelectorDoesntLikeNullElementSelector() {
        ElementSelectors.conditionalSelector(new IsNullPredicate(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void conditionalSelectorDoesntLikeNullPredicate() {
        ElementSelectors.conditionalSelector(null, ElementSelectors.byName);
    }

    @Test(expected = IllegalArgumentException.class)
    public void selectorForElementNamedDoesntLikeNullElementSelectorStringVersion() {
        ElementSelectors.selectorForElementNamed("foo", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void selectorForElementNamedDoesntLikeNullNameStringVersion() {
        ElementSelectors.selectorForElementNamed((String) null, ElementSelectors.byName);
    }

    @Test(expected = IllegalArgumentException.class)
    public void selectorForElementNamedDoesntLikeNullElementSelectorQNameVersion() {
        ElementSelectors.selectorForElementNamed(new QName("foo"), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void selectorForElementNamedDoesntLikeNullNameQNameVersion() {
        ElementSelectors.selectorForElementNamed((QName) null, ElementSelectors.byName);
    }

    @Test(expected = IllegalStateException.class)
    public void conditionalSelectorBuilderWontAllowThenWithoutWhen() {
        ElementSelectors.ConditionalSelectorBuilderThen t =
            (ElementSelectors.ConditionalSelectorBuilderThen) ElementSelectors.conditionalBuilder();
        t.thenUse(ElementSelectors.byName);
    }

    @Test(expected = IllegalStateException.class)
    public void conditionalSelectorBuilderWontAllowWhensWithoutThens() {
        ElementSelectors.ConditionalSelectorBuilder b =
            ElementSelectors.conditionalBuilder();
        b.when(new IsNullPredicate());
        b.build();
    }

    @Test(expected = IllegalStateException.class)
    public void conditionalSelectorBuilderWontAllowMultipleWhensWithoutInterleavingThens() {
        ElementSelectors.ConditionalSelectorBuilder b =
            ElementSelectors.conditionalBuilder();
        b.when(new IsNullPredicate());
        b.whenElementIsNamed(new QName("foo"));
    }

    @Test(expected = IllegalStateException.class)
    public void conditionalSelectorBuilderWontAllowMultipleDefaults() {
        ElementSelectors.ConditionalSelectorBuilder b =
            ElementSelectors.conditionalBuilder();
        b.elseUse(ElementSelectors.byName);
        b.elseUse(ElementSelectors.byName);
    }
}
