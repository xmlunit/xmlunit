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
        /// Sets the strategy for selecting elements to compare.
        /// </summary>
        ElementSelector ElementSelector { set; }

        /// <summary>
        /// Determines whether the comparison should stop after given
        /// difference has been found.
        /// </summary>
        DifferenceEvaluator DifferenceEvaluator { set; }

        /// <summary>
        /// Compares two pieces of XML and invokes the registered listeners.
        /// </summary>
        void Compare(ISource control, ISource test);
    }
}