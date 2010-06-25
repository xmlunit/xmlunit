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

using System;
using System.Xml;

namespace net.sf.xmlunit.diff {

    /// <summary>
    /// Difference engine based on DOM.
    /// </summary>
    public sealed class DOMDifferenceEngine : IDifferenceEngine {
        public event ComparisonListener ComparisonListener;
        public event ComparisonListener MatchListener;
        public event ComparisonListener DifferenceListener;

        private ElementSelector elementSelector = ElementSelectors.Default;
        public ElementSelector ElementSelector {
            set {
                if (value == null) {
                    throw new ArgumentNullException("element selector");
                }
                elementSelector = value;
            }
        }

        private DifferenceEvaluator diffEvaluator = DifferenceEvaluators.Default;
        public DifferenceEvaluator DifferenceEvaluator {
            set {
                if (value == null) {
                    throw new ArgumentNullException("difference evaluator");
                }
                diffEvaluator = value;
            }
        }

        public void Compare(ISource control, ISource test) {
            if (control == null) {
                throw new ArgumentNullException("control");
            }
            if (test == null) {
                throw new ArgumentNullException("test");
            }

            CompareNodes(net.sf.xmlunit.util.Convert.ToNode(control),
                         net.sf.xmlunit.util.Convert.ToNode(test));
        }

        /// <summary>
        /// Recursively compares two XML nodes.
        /// </summary>
        /// <remarks>
        /// Performs comparisons common to all node types, the performs
        /// the node type specific comparisons and finally recures into
        /// the node's child lists.
        ///
        /// Stops as soon as any comparison returns ComparisonResult.CRITICAL.
        /// </remarks>
        internal ComparisonResult CompareNodes(XmlNode control, XmlNode test) {
            ComparisonResult lastResult =
                Compare(new Comparison(ComparisonType.NODE_TYPE, control,
                                       null, control.NodeType,
                                       test, null, test.NodeType));
            if (lastResult == ComparisonResult.CRITICAL) {
                return lastResult;
            }
            lastResult =
                Compare(new Comparison(ComparisonType.NAMESPACE_URI, control,
                                       null, control.NamespaceURI,
                                       test, null, test.NamespaceURI));
            if (lastResult == ComparisonResult.CRITICAL) {
                return lastResult;
            }
            lastResult =
                Compare(new Comparison(ComparisonType.NAMESPACE_PREFIX, control,
                                       null, control.Prefix,
                                       test, null, test.Prefix));
            if (lastResult == ComparisonResult.CRITICAL) {
                return lastResult;
            }
            XmlNodeList controlChildren = control.ChildNodes;
            XmlNodeList testChildren = test.ChildNodes;
            lastResult =
                Compare(new Comparison(ComparisonType.CHILD_NODELIST_LENGTH,
                                       control, null, controlChildren.Count,
                                       test, null, testChildren.Count));
            if (lastResult == ComparisonResult.CRITICAL) {
                return lastResult;
            }
            lastResult = NodeTypeSpecificComparison(control, test);
            if (lastResult == ComparisonResult.CRITICAL) {
                return lastResult;
            }
            return CompareNodeLists(controlChildren, testChildren);
        }

        /// <summary>
        /// Dispatches to the node type specific comparison if one is
        /// defined for the given combination of nodes.
        /// </summary>
        internal ComparisonResult NodeTypeSpecificComparison(XmlNode control,
                                                             XmlNode test) {
            switch (control.NodeType) {
            case XmlNodeType.CDATA:
            case XmlNodeType.Comment:
            case XmlNodeType.Text:
                if (test is XmlCharacterData) {
                    return CompareCharacterData((XmlCharacterData) control,
                                                (XmlCharacterData) test);
                }
                break;
            case XmlNodeType.Document:
#if false
                if (test instanceof Document) {
                    return compareDocuments((Document) control,
                                            (Document) test);
                }
#endif
                break;
            case XmlNodeType.Element:
#if false
                if (test instanceof Element) {
                    return compareElements((Element) control,
                                           (Element) test);
                }
#endif
                break;
            case XmlNodeType.ProcessingInstruction:
                if (test is XmlProcessingInstruction) {
                    return
                        CompareProcessingInstructions((XmlProcessingInstruction) control,
                                                      (XmlProcessingInstruction) test);
                }
                break;
            }
            return ComparisonResult.EQUAL;
        }

        /// <summary>
        /// Compares textual content.
        /// </summary>
        private ComparisonResult CompareCharacterData(XmlCharacterData control,
                                                      XmlCharacterData test) {
            return Compare(new Comparison(ComparisonType.TEXT_VALUE, control,
                                          null, control.Data,
                                          test, null, test.Data));
        }

        private ComparisonResult
            CompareProcessingInstructions(XmlProcessingInstruction control,
                                          XmlProcessingInstruction test) {
            ComparisonResult r = 
                Compare(new Comparison(ComparisonType.PROCESSING_INSTRUCTION_TARGET,
                                       control, null, control.Target,
                                       test, null, test.Target));
            if (r == ComparisonResult.CRITICAL) {
                return r;
            }
            return Compare(new Comparison(ComparisonType.PROCESSING_INSTRUCTION_DATA,
                                          control, null, control.Data,
                                          test, null, test.Data));
        }

        ComparisonResult CompareNodeLists(XmlNodeList control,
                                          XmlNodeList test) {
            return ComparisonResult.EQUAL;
        }

        /// <summary>
        /// Compares the detail values for object equality, lets the
        /// difference evaluator evaluate the result, notifies all
        /// listeners and returns the outcome.
        /// </summary>
        internal ComparisonResult Compare(Comparison comp) {
            object controlValue = comp.ControlNodeDetails.Value;
            object testValue = comp.TestNodeDetails.Value;
            bool equal = controlValue == null
                ? testValue == null : controlValue.Equals(testValue);
            ComparisonResult initial =
                equal ? ComparisonResult.EQUAL : ComparisonResult.DIFFERENT;
            ComparisonResult altered = diffEvaluator(comp, initial);
            FireComparisonPerformed(comp, altered);
            return altered;
        }

        private void FireComparisonPerformed(Comparison comp,
                                             ComparisonResult outcome) {
            if (ComparisonListener != null) {
                ComparisonListener(comp, outcome);
            }
            if (outcome == ComparisonResult.EQUAL && MatchListener != null) {
                MatchListener(comp, outcome);
            } else if (outcome != ComparisonResult.EQUAL
                       && DifferenceListener != null) {
                DifferenceListener(comp, outcome);
            }
        }
    }
}
