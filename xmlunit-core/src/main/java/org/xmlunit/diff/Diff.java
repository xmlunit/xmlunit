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

import org.xmlunit.builder.DiffBuilder;

import javax.xml.transform.Source;

import java.util.Iterator;
import java.util.List;


/**
 * The Diff-Object is the result of two comparisons.
 * @see DiffBuilder
 */
public class Diff {

    private final List<Difference> differences;

    private final Source controlSource;

    private final Source testSource;

    private static final ComparisonFormatter DEFAULT_FORMATTER =
        new DefaultComparisonFormatter();

    public Diff(Source controlSource, Source testSource, List<Difference> differences) {
        super();
        this.controlSource = controlSource;
        this.testSource = testSource;
        this.differences = differences;
    }

    /**
     * @return true if there was at least one difference.
     */
    public boolean hasDifferences() {
        return !(differences.isEmpty());
    }

    /**
     * @return all found differences.
     */
    public Iterator<Difference> getDifferences() {
        return differences.iterator();
    }


    public Source getControlSource() {
        return controlSource;
    }


    public Source getTestSource() {
        return testSource;
    }

    @Override
    public String toString() {
        return toString(DEFAULT_FORMATTER);
    }

    public String toString(ComparisonFormatter formatter) {
        if (differences.isEmpty()) {
            return "[identical]";
        }
        return formatter.getDescription(getDifferences().next().getComparison());
    }

}
