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

import java.util.AbstractMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * {@link ElementSelector} that allows two elements to be compared if
 * their name (including namespace URI, if any) and textual content is
 * the same and the same is true for all child elements recursively.
 *
 * <p>This {@code ElementSelector} helps with structures nested more
 * deeply but may need to be combined inside a {@link
 * ElementSelectors#conditionalSelector conditionalSelector} in order
 * to be useful for the document as a whole.</p>
 */
public class ByNameAndTextRecSelector implements ElementSelector {
    @Override
    public boolean canBeCompared(Element controlElement,
                                 Element testElement) {
        if (!ElementSelectors.byNameAndText.canBeCompared(controlElement,
                                                          testElement)) {
            return false;
        }
        NodeList controlChildren = controlElement.getChildNodes();
        NodeList testChildren = testElement.getChildNodes();
        final int controlLen = controlChildren.getLength();
        final int testLen = testChildren.getLength();
        int controlIndex, testIndex;
        for (controlIndex = testIndex = 0;
             controlIndex < controlLen && testIndex < testLen;
             ) {
            // find next non-text child nodes
            Map.Entry<Integer, Node> control = findNonText(controlChildren,
                                                           controlIndex,
                                                           controlLen);
            controlIndex = control.getKey();
            Node c = control.getValue();
            if (isText(c)) {
                break;
            }
            Map.Entry<Integer, Node> test = findNonText(testChildren,
                                                        testIndex,
                                                        testLen);
            testIndex = test.getKey();
            Node t = test.getValue();
            if (isText(t)) {
                break;
            }

            // different types of children make elements
            // non-comparable
            if (c.getNodeType() != t.getNodeType()) {
                return false;
            }
            // recurse for child elements
            if (c instanceof Element
                && !canBeCompared((Element) c, (Element) t)) {
                return false;
            }
            controlIndex++;
            testIndex++;
        }

        // child lists exhausted?
        if (controlIndex < controlLen) {
            Map.Entry<Integer, Node> p = findNonText(controlChildren,
                                                     controlIndex,
                                                     controlLen);
            controlIndex = p.getKey();
            // some non-Text children remained
            if (controlIndex < controlLen) {
                return false;
            }
        }
        if (testIndex < testLen) {
            Map.Entry<Integer, Node> p = findNonText(testChildren,
                                                     testIndex,
                                                     testLen);
            testIndex = p.getKey();
            // some non-Text children remained
            if (testIndex < testLen) {
                return false;
            }
        }
        return true;
    }

    private static Map.Entry<Integer, Node> findNonText(NodeList nl, int current, int len) {
        Node n = nl.item(current);
        while (isText(n) && ++current < len) {
            n = nl.item(current);
        }
        return new AbstractMap.SimpleImmutableEntry<Integer, Node>(current, n);
    }

    private static boolean isText(Node n) {
        return n instanceof Text;
    }

}

