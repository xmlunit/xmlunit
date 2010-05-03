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

import org.w3c.dom.Node;

/**
 * Details of a single comparison XMLUnit has performed.
 */
public class Comparison {

    /**
     * The details of a Node that took part in the comparision.
     */
    public static class Detail {
        private final Node node;
        private final String xpath;
        private final Object value;

        private Detail(Node n, String x, Object v) {
            node = n;
            xpath = x;
            value = v;
        }

        /**
         * The actual Node.
         */
        public Node getNode() { return node; }
        /**
         * XPath leading to the Node.
         */
        public String getXPath() { return xpath; }
        /**
         * The value for comparision found at the current node.
         */
        public Object getValue() { return value; }
    }

    private final Detail control, test;
    private final ComparisonType type;

    public Comparison(ComparisonType t, Node controlNode,
                      String controlXPath, Object controlValue,
                      Node testNode, String testXPath, Object testValue) {
        type = t;
        control = new Detail(controlNode, controlXPath, controlValue);
        test = new Detail(testNode, testXPath, testValue);
    }

    /**
     * The kind of comparision performed.
     */
    public ComparisonType getType() {
        return type;
    }

    /**
     * Details of the control node.
     */
    public Detail getControlNodeDetails() {
        return control;
    }

    /**
     * Details of the test node.
     */
    public Detail getTestNodeDetails() {
        return test;
    }

}
