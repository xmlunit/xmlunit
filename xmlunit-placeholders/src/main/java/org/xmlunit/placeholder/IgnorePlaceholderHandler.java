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
 * Handler for the "ignore" placeholder keyword.
 *
 * <p><b>This class and the whole module are considered experimental
 * and any API may change between releases of XMLUnit.</b></p>
 *
 * <p>Text nodes or attributes containing {@code ${xmlunit.ignore}}
 * will be ignored.</p>
 * @since 2.6.0
 */
public class IgnorePlaceholderHandler implements PlaceholderHandler {
    private static final String PLACEHOLDER_NAME_IGNORE = "ignore";

    @Override
    public String getKeyword() {
        return PLACEHOLDER_NAME_IGNORE;
    }

    @Override
    public ComparisonResult evaluate(String testText, String... param) {
        return ComparisonResult.EQUAL;
    }
}
