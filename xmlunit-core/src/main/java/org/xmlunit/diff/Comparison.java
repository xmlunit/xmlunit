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

import org.w3c.dom.Node;

import static org.xmlunit.diff.AbstractDifferenceEngine.getParentXPath;
import static org.xmlunit.diff.AbstractDifferenceEngine.getXPath;

/**
 * Details of a single comparison XMLUnit has performed.
 */
public class Comparison {

    /**
     * The details of a target (usually some representation of an XML
     * Node) that took part in the comparison.
     */
    public static class Detail {
        private final Node target;
        private final String xpath;
        private final Object value;
        private final String parentXPath;

        private Detail(Node n, String x, Object v, String parentX) {
            target = n;
            xpath = x;
            value = v;
            parentXPath = parentX;
        }

        /**
         * The actual target.
         * @return the actual target
         */
        public Node getTarget() { return target; }
        /**
         * XPath leading to the target.
         * @return XPath leading to the target
         */
        public String getXPath() { return xpath; }
        /**
         * The value for comparison found at the current target.
         * @return the value for comparison found at the current target
         */
        public Object getValue() { return value; }

        /**
         * XPath leading to the target's parent.
         * @return XPath leading to the target's parent
         */
        public String getParentXPath() {
            return parentXPath;
        }
    }

    private final Detail control, test;
    private final ComparisonType type;

    /**
     * Creates a new comparison.
     *
     * @param t the type of comparison
     * @param controlTarget the control node the comparison applies to
     * @param controlXPath xpath of the control node the comparison applies to
     * @param controlValue value from the control node used during comparison
     * @param controlParentXPath xpath of the control node' parent
     * @param testTarget the test node the comparison applies to
     * @param testXPath xpath of the test node the comparison applies to
     * @param testValue value from the test node used during comparison
     * @param testParentXPath xpath of the test node' parent
     */
    public Comparison(ComparisonType t,
                      Node controlTarget, String controlXPath, Object controlValue, String controlParentXPath,
                      Node testTarget, String testXPath, Object testValue, String testParentXPath) {
        type = t;
        control = new Detail(controlTarget, controlXPath, controlValue, controlParentXPath);
        test = new Detail(testTarget, testXPath, testValue, testParentXPath);
    }

    /**
     * Creates a new comparison.
     *
     * @param t the type of comparison
     * @param controlContext xpathContext object contains the current and parent XPath
     * @param controlTarget the control node the comparison applies to
     * @param controlValue value from the control node used during comparison
     * @param testContext xpathContext object contains the current and parent XPath
     * @param testTarget the test node the comparison applies to
     * @param testValue value from the test node used during comparison
     * @since XMLUnit 2.10.1
     */
    public Comparison(ComparisonType t,
                      XPathContext controlContext, Node controlTarget, Object controlValue,
                      XPathContext testContext, Node testTarget,Object testValue) {
        this (t,
              controlTarget, getXPath(controlContext), controlValue, getParentXPath(controlContext),
              testTarget, getXPath(testContext), testValue, getParentXPath(testContext));
    }

    /**
     * The kind of comparison performed.
     * @return the type of comparison
     */
    public ComparisonType getType() {
        return type;
    }

    /**
     * Details of the control target.
     * @return details of the control target
     */
    public Detail getControlDetails() {
        return control;
    }

    /**
     * Details of the test target.
     * @return details of the test target
     */
    public Detail getTestDetails() {
        return test;
    }

    /**
     * Returns a string representation of this comparison using the
     * given {@link ComparisonFormatter}
     * @param formatter the ComparisonFormatter to use
     * @return a string representation of this comparison
     */
    public String toString(ComparisonFormatter formatter) {
        return formatter.getDescription(this);
    }

    /**
     * Returns a string representation of this comparison using {@link DefaultComparisonFormatter}
     * @return a string representation of this comparison
     */
    @Override
    public String toString(){
        return toString(new DefaultComparisonFormatter());
    }
}
