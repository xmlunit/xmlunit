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
package org.xmlunit.diff;

/**
 * May decide to up- or downgrade the severity of a difference.
 */
public interface DifferenceEvaluator {
    /**
     * May alter the outcome of a comparison.
     *
     * @param comparison the comparison
     * @param outcome the current outcome of the comparison
     * @return the new result of the comparison.
     */
    ComparisonResult evaluate(Comparison comparison, ComparisonResult outcome);
}
