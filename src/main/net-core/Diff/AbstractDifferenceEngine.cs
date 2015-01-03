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

namespace Org.XmlUnit.Diff {

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
                namespaceContext = value == null ? value
                    : new Dictionary<string, string>(value);
            }
            protected get {
                return namespaceContext == null ? namespaceContext
                    : new Dictionary<string, string>(namespaceContext);
            }
        }

        /// <summary>
        /// Compares the detail values for object equality, lets the
        /// difference evaluator evaluate the result, notifies all
        /// listeners and returns the outcome.
        /// </summary>
        protected internal ComparisonResult Compare(Comparison comp) {
            object controlValue = comp.ControlDetails.Value;
            object testValue = comp.TestDetails.Value;
            bool equal = controlValue == null
                ? testValue == null : controlValue.Equals(testValue);
            ComparisonResult initial =
                equal ? ComparisonResult.EQUAL : ComparisonResult.DIFFERENT;
            ComparisonResult altered = DifferenceEvaluator(comp, initial);
            FireComparisonPerformed(comp, altered);
            return altered;
        }

        /// <summary>
        /// Returns a function that compares the detail values for
        /// object equality, lets the difference evaluator evaluate
        /// the result, notifies all listeners and returns the
        /// outcome.
        /// </summary>
        protected internal Func<ComparisonResult> Comparer(Comparison comp) {
            return () => Compare(comp);
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

        /// <summary>
        /// Chain of comparisons where the last comparision performed
        /// determines the final result but the first comparison with
        /// a critical difference stops the chain.
        /// </summary>
        protected class ComparisonChain {
            private ComparisonResult currentResult;
            internal ComparisonChain()
                : this(ComparisonResult.EQUAL) {
            }
            internal ComparisonChain(ComparisonResult firstResult) {
                currentResult = firstResult;
            }
            internal ComparisonChain AndThen(Func<ComparisonResult> next) {
                if (currentResult != ComparisonResult.CRITICAL) {
                    currentResult = next();
                }
                return this;
            }
            internal ComparisonChain AndIfTrueThen(bool evalNext,
                                                   Func<ComparisonResult> next) {
                return evalNext ? AndThen(next) : this;
            }
            internal ComparisonResult FinalResult {
                get {
                    return currentResult;
                }
            }
        }
    }
}
