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

import javax.xml.transform.Source;

/**
 * The Diff-Object is the result of two comparisons.
 * @see org.xmlunit.builder.DiffBuilder
 */
public class Diff {

    private static final ComparisonFormatter DEFAULT_FORMATTER =
        new DefaultComparisonFormatter();

    private final Iterable<Difference> differences;

    private final Source controlSource;

    private final Source testSource;

    private final ComparisonFormatter formatter;

    public Diff(Source controlSource, Source testSource, Iterable<Difference> differences) {
        this(controlSource, testSource, DEFAULT_FORMATTER, differences);
    }

    public Diff(Source controlSource, Source testSource,
                ComparisonFormatter formatter, Iterable<Difference> differences) {
        this.controlSource = controlSource;
        this.testSource = testSource;
        this.formatter = formatter;
        this.differences = differences;
        for (Difference d : differences) {
            d.setComparisonFormatter(formatter);
        }
    }

    /**
     * @return true if there was at least one difference.
     */
    public boolean hasDifferences() {
        return differences.iterator().hasNext();
    }

    /**
     * @return all differences found before the comparison process stopped.
     */
    public Iterable<Difference> getDifferences() {
        return differences;
    }


    public Source getControlSource() {
        return controlSource;
    }


    public Source getTestSource() {
        return testSource;
    }

    @Override
    public String toString() {
        return toString(formatter);
    }

    public String toString(ComparisonFormatter formatter) {
        if (!hasDifferences()) {
            return "[identical]";
        }
        return getDifferences().iterator().next().getComparison().toString(formatter);
    }

}
