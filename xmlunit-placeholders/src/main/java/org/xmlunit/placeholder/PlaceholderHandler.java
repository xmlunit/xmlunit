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
package org.xmlunit.placeholder;

import org.xmlunit.diff.ComparisonResult;

/**
 * Interface implemented by classes that are responsible for a
 * placeholder keyword.
 *
 * <p><b>This class and the whole module are considered experimental
 * and any API may change between releases of XMLUnit.</b></p>
 *
 * <p>Implementations are expected to be thread-safe, the {@link
 * #evaluate} method may be invoked by multiple threads in
 * parallel.</p>
 * @since 2.7.0
 */
public interface PlaceholderHandler {
    /**
     * The placeholder keyword this handler is responsible for.
     */
    String getKeyword();

    /**
     * Evaluate the test value when control contained the placeholder
     * handled by this class.
     *
     * @param testText the textual content of the element or attribute
     * this placeholder has been added to.
     * @param placeholderParameters any arguments provided to the
     * placeholder.
     */
    ComparisonResult evaluate(String testText, String... placeholderParameters);
}
