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

import static org.xmlunit.diff.ComparisonResult.DIFFERENT;
import static org.xmlunit.diff.ComparisonResult.EQUAL;

/**
 * Handler for the {@code isNumber} placeholder keyword.
 * @since 2.6.3
 */
public class IsNumberPlaceholderHandler implements PlaceholderHandler {
    private static final String PLACEHOLDER_NAME = "isNumber";

    private static final String NUMBER_PATTERN = "^[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?$";

    @Override
    public String getKeyword() {
        return PLACEHOLDER_NAME;
    }

    @Override
    public ComparisonResult evaluate(String testText, String... param) {
        return testText != null && testText.trim().matches(NUMBER_PATTERN) ? EQUAL : DIFFERENT;
    }
}
