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
using System.Collections.Generic;

namespace net.sf.xmlunit.diff {

    /// <summary>
    /// Useful base-implementation of some parts of the
    /// IDifferenceEngine interface.
    /// </summary>
    public abstract class AbstractDifferenceEngine : IDifferenceEngine {
        public event ComparisonListener ComparisonListener;
        public event ComparisonListener MatchListener;
        public event ComparisonListener DifferenceListener;

        private INodeMatcher nodeMatcher = new DefaultNodeMatcher();
        public virtual INodeMatcher NodeMatcher {
            set {
                if (value == null) {
                    throw new ArgumentNullException("node matcher");
                }
                nodeMatcher = value;
            }
            get {
                return nodeMatcher;
            }
        }

        private DifferenceEvaluator diffEvaluator = DifferenceEvaluators.Default;
        public virtual DifferenceEvaluator DifferenceEvaluator {
            set {
                if (value == null) {
                    throw new ArgumentNullException("difference evaluator");
                }
                diffEvaluator = value;
            }
            get {
                return diffEvaluator;
            }
        }

        public abstract void Compare(ISource control, ISource test);

        private IDictionary<string, string> namespaceContext;

        public IDictionary<string, string> NamespaceContext {
            set {
                namespaceContext = value;
            }
        }

        /// <summary>
        /// Compares the detail values for object equality, lets the
        /// difference evaluator evaluate the result, notifies all
        /// listeners and returns the outcome.
        /// </summary>
        protected internal ComparisonResult Compare(Comparison comp) {
            ComparisonResult altered = CompareDontFire(comp);
            FireComparisonPerformed(comp, altered);
            return altered;
        }

        /// <summary>
        /// Compares the detail values for object equality, lets the
        /// difference evaluator evaluate the result.
        /// </summary>
        protected internal ComparisonResult CompareDontFire(Comparison comp) {
            object controlValue = comp.ControlDetails.Value;
            object testValue = comp.TestDetails.Value;
            bool equal = controlValue == null
                ? testValue == null : controlValue.Equals(testValue);
            ComparisonResult initial =
                equal ? ComparisonResult.EQUAL : ComparisonResult.DIFFERENT;
            return DifferenceEvaluator(comp, initial);
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

        protected static string GetXPath(XPathContext ctx) {
            return ctx == null ? null : ctx.XPath;
        }
    }
}
