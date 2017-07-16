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

import org.xmlunit.diff.Comparison.Detail;

/**
 * Formatter methods for a {@link Comparison} Object.
 */
public interface ComparisonFormatter {

    /**
     * Return a short String of the Comparison including the XPath and the shorten value of the effected control and
     * test Node.
     * <p>
     * This is used for {@link Diff#toString()}.
     * @param difference the comparison to describe
     * @return a short description of the comparison
     */
    String getDescription(Comparison difference);

    /**
     * Return the xml node from {@link Detail#getTarget()} as formatted String.
     * <p>
     * This can be used to produce a nice compare-View in your IDE (e.g. with org.junit.ComparisonFailure).
     *
     * @param details The {@link Comparison#getControlDetails()} or {@link Comparison#getTestDetails()}.
     * @param type the implementation can return different details depending on the ComparisonType.
     * @param formatXml set this to true if the Comparison was generated with {@link org.xmlunit.builder.DiffBuilder#ignoreWhitespace()}.
     * @return the full xml node.
     */
    String getDetails(Detail details, ComparisonType type, boolean formatXml);

}
