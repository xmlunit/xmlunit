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
 * The possible outcomes of a comparision.
 */
public enum ComparisonResult {
    /**
     * The two nodes are the same for the sake of this comparison.
     */
    EQUAL,
    /**
     * The two nodes are different but similar enough to satisfy a
     * weak equality constraint
     */
    SIMILAR,
    /**
     * The two nodes are different.
     */
    DIFFERENT,
    /**
     * The two nodes are different and comparison should stop
     * immediately.
     *
     * <p>Only used as a return type by {@link DifferenceEvaluator}
     */
    CRITICAL,
}
