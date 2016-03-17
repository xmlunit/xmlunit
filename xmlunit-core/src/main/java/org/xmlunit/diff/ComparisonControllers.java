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
 * Controllers used for the base cases.
 */
public final class ComparisonControllers {

    /**
     * Does not stop the comparison at all.
     */
    public static final ComparisonController Default =
        new ComparisonController() {
            @Override
            public boolean stopDiffing(Difference ignored) {
                return false;
            }
        };

    /**
     * Makes the comparison stop as soon as the first "real"
     * difference is encountered.
     */
    public static final ComparisonController StopWhenDifferent = new StopComparisonController(ComparisonResult.DIFFERENT);

    /**
     * Makes the comparison stop as soon as the first
     * difference is encountered even if it is similar.
     */
    public static final ComparisonController StopWhenSimilar = new StopComparisonController(ComparisonResult.SIMILAR);

    private ComparisonControllers() { }

    private static final class StopComparisonController implements ComparisonController {

        final ComparisonResult minimumComparisonResult;
        
        public StopComparisonController(ComparisonResult minimumComparisonResult) {
            this.minimumComparisonResult = minimumComparisonResult;
        }

        @Override
        public boolean stopDiffing(Difference d) {
            return d.getResult().ordinal() >= minimumComparisonResult.ordinal();
        }
    }

}
