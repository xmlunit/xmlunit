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
package org.xmlunit.bugreports;

import static org.junit.Assert.assertThat;
import static org.xmlunit.builder.Input.fromString;
import static org.xmlunit.diff.ElementSelectors.byName;
import static org.xmlunit.diff.ElementSelectors.byXPath;
import static org.xmlunit.diff.ElementSelectors.selectorForElementNamed;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.ElementSelector;
import org.xmlunit.diff.ElementSelectors;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @see "http://stackoverflow.com/questions/33975471/compare-subnodes-in-xmlunit-2-expected-child-node2-but-was-null"
 */
public class SelectElementsByNameOfChildTest {

    private String test1 = "<root>\n"
        + "    <child>\n"
        + "        <node1/>\n"
        + "    </child>\n"
        + "    <child>\n"
        + "        <node2/>\n"
        + "    </child>\n"
        + "</root>";
    private String test2 = "<root>\n"
        + "    <child>\n"
        + "        <node2/>\n"
        + "    </child>\n"
        + "    <child>\n"
        + "        <node1/>\n"
        + "    </child>\n"
        + "</root>";

    @Test
    public void tryToSelectMatchingChildNodesUsingXPath() throws Exception {
        ElementSelector childSelector = selectorForElementNamed("child", byXPath("./*[1]", byName));
        assertThat(fromString(test1),
                   isSimilarTo(fromString(test2))
                   .withNodeMatcher(new DefaultNodeMatcher(childSelector, byName)));
    }

    @Test
    @Ignore("doesn't work with XMLUnit > 2.0.0-alpha-02")
    public void tryToSelectMatchingChildNodesUsingXPathAlpha02() throws Exception {
        ElementSelector childSelector = selectorForElementNamed("child", byXPath("./child/*[1]", byName));
        assertThat(fromString(test1),
                   isSimilarTo(fromString(test2))
                   .withNodeMatcher(new DefaultNodeMatcher(childSelector, byName)));
    }

    @Test
    public void tryToSelectMatchingChildNodesUsingCustomElementSelector() throws Exception {
        ElementSelector childSelector = selectorForElementNamed("child", new FirstChildElementNameSelector());
        assertThat(fromString(test1),
                   isSimilarTo(fromString(test2))
                   .withNodeMatcher(new DefaultNodeMatcher(childSelector, byName)));
    }

    public static class FirstChildElementNameSelector implements ElementSelector {
        @Override
        public boolean canBeCompared(Element controlElement,
                                     Element testElement) {
            return byName.canBeCompared(firstChildElement(controlElement),
                                        firstChildElement(testElement));
        }

        private Element firstChildElement(Element e) {
            NodeList nl = e.getChildNodes();
            int len = nl.getLength();
            for (int i = 0; i < len; i++) {
                if (nl.item(i) instanceof Element) {
                    return (Element) nl.item(i);
                }
            }
            return null;
        }
    }
}
