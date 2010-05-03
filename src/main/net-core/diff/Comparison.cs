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

using System.Xml;

namespace net.sf.xmlunit.diff {

    /// <summary>
    /// Details of a single comparison XMLUnit has performed.
    /// </summary>
    public class Comparison {

        /// <summary>
        /// The details of a Node that took part in the comparision.
        /// </summary>
        public sealed class Detail {
            private readonly XmlNode node;
            private readonly string xpath;
            private readonly object value;

            internal Detail(XmlNode n, string x, object v) {
                node = n;
                xpath = x;
                value = v;
            }

            /// <summary>
            /// The actual Node.
            /// </summary>
            public XmlNode Node { get { return node; } }
            /// <summary>
            /// XPath leading to the Node.
            /// </summary>
            public string XPath { get { return xpath; } }
            /// <summary>
            /// The value for comparision found at the current node.
            /// </summary>
            public object Value { get { return value; } }
        }

        private readonly Detail control, test;
        private readonly ComparisonType type;

        public Comparison(ComparisonType t, XmlNode controlNode,
                          string controlXPath, object controlValue,
                          XmlNode testNode, string testXPath,
                          object testValue) {
            type = t;
            control = new Detail(controlNode, controlXPath, controlValue);
            test = new Detail(testNode, testXPath, testValue);
        }

        /// <summary>
        /// The kind of comparision performed.
        /// </summary>
        public ComparisonType Type {
            get {
                return type;
            }
        }

        /// <summary>
        /// Details of the control node.
        /// </summary>
        public Detail ControlNodeDetails {
            get {
                return control;
            }
        }

        /// <summary>
        /// Details of the test node.
        /// </summary>
        public Detail TestNodeDetails {
            get {
                return test;
            }
        }

    }
}
