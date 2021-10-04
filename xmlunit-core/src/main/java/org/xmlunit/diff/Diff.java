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
import java.util.Iterator;

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
     * Returns a string representation of this diff
     * using internal {@link ComparisonFormatter} or
     * {@link DefaultComparisonFormatter} if formatter wasn't set.
     * Each comparison result separated by the end of the line.
     * @return a string representation of this diff
     */
    public String fullDescription() {
        return fullDescription(formatter);
    }

    /**
     * Returns a string representation of this diff
     * using the given {@link ComparisonFormatter}
     * @param formatter the {@link ComparisonFormatter} to use
     * @return a string representation of this diff
     */
    public String fullDescription(ComparisonFormatter formatter) {
        if (!hasDifferences()) {
            return "[identical]";
        }
        Iterator<Difference> diffIterator = getDifferences().iterator();
        StringBuilder result = new StringBuilder()
            .append(diffIterator.next().getComparison().toString(formatter));
        String lineSeparator = System.lineSeparator();
        while (diffIterator.hasNext()) {
            result.append(lineSeparator)
                .append(diffIterator.next().getComparison().toString(formatter));
        }
        return result.toString();
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

    /**
     * Returns a string representation of first found difference in this diff
     * using internal {@link ComparisonFormatter} or
     * {@link DefaultComparisonFormatter} if formatter wasn't set
     * @return a string representation of first found difference in this diff
     * @see #fullDescription()
     */
    @Override
    public String toString() {
        return toString(formatter);
    }

    /**
     * Returns a string representation of first found difference in this diff
     * using the given {@link ComparisonFormatter}
     * @param formatter the {@link ComparisonFormatter} to use
     * @return a string representation of first found difference in this diff
     * @see #fullDescription(ComparisonFormatter)
     */
    public String toString(ComparisonFormatter formatter) {
        if (!hasDifferences()) {
            return "[identical]";
        }
        return getDifferences().iterator().next().getComparison().toString(formatter);
    }

}
