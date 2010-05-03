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

import javax.xml.transform.Source;

/**
 * XMLUnit's difference engine.
 */
public interface IDifferenceEngine {
    /**
     * Registers a listener that is notified of each comparison.
     */
    void addComparisonListener(ComparisonListener l);

    /**
     * Registers a listener that is notified of each comparison with
     * outcome {@link ComparisonResult#EQUAL}.
     */
    void addMatchListener(ComparisonListener l);

    /**
     * Registers a listener that is notified of each comparison with
     * outcome other than {@link ComparisonResult#EQUAL}.
     */
    void addDifferenceListener(ComparisonListener l);

    /**
     * Sets the strategy for selecting elements to compare.
     */
    void setElementSelector(ElementSelector s);

    /**
     * Determines whether the comparison should stop after given
     * difference has been found.
     */
    void setDifferenceEvaluator(DifferenceEvaluator e);

    /**
     * Compares two pieces of XML and invokes the registered listeners.
     */
    void compare(Source control, Source test);
}
