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

/**
 * Details of a single comparison XMLUnit has performed.
 */
public class Comparison {

    /**
     * The details of a target (usually some representation of an XML
     * Node) that took part in the comparison.
     */
    public static class Detail {
        private final Object target;
        private final String xpath;
        private final Object value;

        private Detail(Object n, String x, Object v) {
            target = n;
            xpath = x;
            value = v;
        }

        /**
         * The actual target.
         */
        public Object getTarget() { return target; }
        /**
         * XPath leading to the target.
         */
        public String getXPath() { return xpath; }
        /**
         * The value for comparison found at the current target.
         */
        public Object getValue() { return value; }
    }

    private final Detail control, test;
    private final ComparisonType type;

    public Comparison(ComparisonType t, Object controlTarget,
                      String controlXPath, Object controlValue,
                      Object testTarget, String testXPath, Object testValue) {
        type = t;
        control = new Detail(controlTarget, controlXPath, controlValue);
        test = new Detail(testTarget, testXPath, testValue);
    }

    /**
     * The kind of comparison performed.
     */
    public ComparisonType getType() {
        return type;
    }

    /**
     * Details of the control target.
     */
    public Detail getControlDetails() {
        return control;
    }

    /**
     * Details of the test target.
     */
    public Detail getTestDetails() {
        return test;
    }

}
