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

namespace net.sf.xmlunit.diff {

    /// <summary>
    /// Details of a single comparison XMLUnit has performed.
    /// </summary>
    public class Comparison {

        /// <summary>
        /// The details of a target (usually a representation of an
        /// XML node) that took part in the comparison.
        /// </summary>
        public sealed class Detail {
            private readonly object target;
            private readonly string xpath;
            private readonly object value;

            internal Detail(object t, string x, object v) {
                target = t;
                xpath = x;
                value = v;
            }

            /// <summary>
            /// The actual target.
            /// </summary>
            public object Target { get { return target; } }
            /// <summary>
            /// XPath leading to the target.
            /// </summary>
            public string XPath { get { return xpath; } }
            /// <summary>
            /// The value for comparison found at the current target.
            /// </summary>
            public object Value { get { return value; } }
        }

        private readonly Detail control, test;
        private readonly ComparisonType type;

        public Comparison(ComparisonType t, object controlTarget,
                          string controlXPath, object controlValue,
                          object testTarget, string testXPath,
                          object testValue) {
            type = t;
            control = new Detail(controlTarget, controlXPath, controlValue);
            test = new Detail(testTarget, testXPath, testValue);
        }

        /// <summary>
        /// The kind of comparison performed.
        /// </summary>
        public ComparisonType Type {
            get {
                return type;
            }
        }

        /// <summary>
        /// Details of the control target.
        /// </summary>
        public Detail ControlDetails {
            get {
                return control;
            }
        }

        /// <summary>
        /// Details of the test target.
        /// </summary>
        public Detail TestDetails {
            get {
                return test;
            }
        }

    }
}
