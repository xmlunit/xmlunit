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

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * {@link ElementSelector} that allows two elements to be compared if
 * their name (including namespace URI, if any) and textual content is
 * the same at a certain level of nesting.
 *
 * <p>This means {@link ElementSelectors#byNameAndText} and {@code
 * MultiLevelByNameAndTextSelector(1)} should lead to the same
 * results.</p>
 *
 * <p>Any attribute values are completely ignored.  Only works on
 * elements with exactly one child element at each level.</p>
 *
 * <p>This class mostly exists as an example for custom
 * ElementSelectors and may need to be combined inside a {@link
 * ElementSelectors#conditionalSelector conditionalSelector} in order
 * to be useful for the document as a whole.</p>
 */
public class MultiLevelByNameAndTextSelector implements ElementSelector {

    private final int levels;
    private final boolean ignoreEmptyTexts;

    /**
     * Uses element names and the text nested {@code levels} child
     * elements deeper into the element to compare elements.
     *
     * <p>Does not ignore empty text nodes.
     */
    public MultiLevelByNameAndTextSelector(int levels) {
        this(levels, false);
    }

    /**
     * Uses element names and the text nested {@code levels} child
     * elements deeper into the element to compare elements.
     *
     * @param ignoreEmptyTexts whether whitespace-only textnodes
     * should be ignored.
     */
    public MultiLevelByNameAndTextSelector(int levels, boolean ignoreEmptyTexts) {
        if (levels < 1) {
            throw new IllegalArgumentException("levels must be equal or"
                                               + " greater than one");
        }
        this.levels = levels;
        this.ignoreEmptyTexts = ignoreEmptyTexts;
    }

    @Override
    public boolean canBeCompared(Element controlElement,
                                 Element testElement) {
        Element currentControl = controlElement;
        Element currentTest = testElement;

        // match on element names only for leading levels
        for (int currentLevel = 0; currentLevel <= levels - 2; currentLevel++) {
            if (!ElementSelectors.byName.canBeCompared(currentControl, currentTest)
                || !currentControl.hasChildNodes()
                || !currentTest.hasChildNodes()) {
                return false;
            }
            Node n1 = getFirstEligibleChild(currentControl);
            Node n2 = getFirstEligibleChild(currentTest);
            if (n1.getNodeType() == Node.ELEMENT_NODE
                && n2.getNodeType() == Node.ELEMENT_NODE) {
                currentControl = (Element) n1;
                currentTest = (Element) n2;
            } else {
                return false;
            }
        }

        // finally compare the level containing the text child node
        return ElementSelectors.byNameAndText.canBeCompared(currentControl,
                                                            currentTest);
    }

    private Node getFirstEligibleChild(Node parent) {
        Node n1 = parent.getFirstChild();
        if (ignoreEmptyTexts) {
            while (isText(n1) && n1.getNodeValue().trim().length() == 0) {
                Node n2 = n1.getNextSibling();
                if (n2 == null) {
                    break;
                }
                n1 = n2;
            }
        }
        return n1;
    }

    private static boolean isText(Node n) {
        return n instanceof Text;
    }
}
