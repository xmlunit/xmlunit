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

using System.Collections.Generic;

namespace net.sf.xmlunit.diff {

    /// <summary>
    /// XMLUnit's difference engine.
    /// </summary>
    public interface IDifferenceEngine {
        /// <summary>
        /// Is notified of each comparison.
        /// </summary>
        event ComparisonListener ComparisonListener;

        /// <summary>
        /// Is notified of each comparison with outcome {@link
        /// ComparisonResult#EQUAL}.
        /// </summary>
        event ComparisonListener MatchListener;

        /// <summary>
        /// Is notified of each comparison with
        /// outcome other than {@link ComparisonResult#EQUAL}.
        /// </summary>
        event ComparisonListener DifferenceListener;

        /// <summary>
        /// Sets the strategy for selecting nodes to compare.
        /// </summary>
        INodeMatcher NodeMatcher { set; }

        /// <summary>
        /// Determines whether the comparison should stop after given
        /// difference has been found.
        /// </summary>
        DifferenceEvaluator DifferenceEvaluator { set; }

        /// <summary>
        /// Establish a namespace context mapping from URI to prefix
        /// that will be used in Comparison.Detail.XPath.
        /// </summary>
        /// <remarks>
        /// Without a namespace context (or with an empty context) the
        /// XPath expressions will only use local names for elements and
        /// attributes.
        /// </remarks>
        IDictionary<string, string> NamespaceContext { set; }

        /// <summary>
        /// Compares two pieces of XML and invokes the registered listeners.
        /// </summary>
        void Compare(ISource control, ISource test);
    }
}