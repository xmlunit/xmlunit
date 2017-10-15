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

import org.xmlunit.builder.DifferenceEngineConfigurer;
import org.xmlunit.diff.DifferenceEvaluator;
import org.xmlunit.diff.DifferenceEvaluators;

/**
 * Adds support for the placeholder feature to a {@link
 * DifferenceEngineConfigurer} - like {@link
 * org.xmlunit.builder.DiffBuilder} or {@code
 * org.xmlunit.matchers.CompareMatcher}.
 *
 * @since 2.5.1
 */
public class PlaceholderSupport {
    /**
     * Adds placeholder support to a {@link DifferenceEngineConfigurer}.
     * @param configurer the configurer to add support to
     * @return the configurer with placeholder support added in
     */
    public static <D extends DifferenceEngineConfigurer<D>>
        D withPlaceholderSupport(D configurer) {
        return withPlaceholderSupportUsingDelimiters(configurer, null, null);
    }

    /**
     * Adds placeholder support to a {@link DifferenceEngineConfigurer}.
     * @param configurer the configurer to add support to
     * @param placeholderOpeningDelimiterRegex regular expression for
     * the opening delimiter of placeholder, defaults to {@link
     * PlaceholderDifferenceEvaluator#PLACEHOLDER_DEFAULT_OPENING_DELIMITER_REGEX}
     * if the parameter is null or blank
     * @param placeholderClosingDelimiterRegex regular expression for
     * the closing delimiter of placeholder, defaults to {@link
     * PlaceholderDifferenceEvaluator#PLACEHOLDER_DEFAULT_CLOSING_DELIMITER_REGEX}
     * if the parameter is null or blank
     * @return the configurer with placeholder support added in
     */
    public static <D extends DifferenceEngineConfigurer<D>>
        D withPlaceholderSupportUsingDelimiters(D configurer, String placeholderOpeningDelimiterRegex,
            String placeholderClosingDelimiterRegex) {
        return configurer.withDifferenceEvaluator(new PlaceholderDifferenceEvaluator(placeholderOpeningDelimiterRegex,
            placeholderClosingDelimiterRegex));
    }

    /**
     * Adds placeholder support to a {@link DifferenceEngineConfigurer} considering an additional {@link DifferenceEvaluator}.
     *
     * @param configurer the configurer to add support to
     * @param evaluator the additional evaluator - payload support is chained after the given evaluator
     */
    public static <D extends DifferenceEngineConfigurer<D>>
        D withPlaceholderSupportChainedAfter(D configurer, DifferenceEvaluator evaluator) {
        return withPlaceholderSupportUsingDelimitersChainedAfter(configurer, null, null, evaluator);
    }

    /**
     * Adds placeholder support to a {@link DifferenceEngineConfigurer} considering an additional {@link DifferenceEvaluator}.
     *
     * @param configurer the configurer to add support to
     * @param placeholderOpeningDelimiterRegex regular expression for
     * the opening delimiter of placeholder, defaults to {@link
     * PlaceholderDifferenceEvaluator#PLACEHOLDER_DEFAULT_OPENING_DELIMITER_REGEX}
     * if the parameter is null or blank
     * @param placeholderClosingDelimiterRegex regular expression for
     * the closing delimiter of placeholder, defaults to {@link
     * PlaceholderDifferenceEvaluator#PLACEHOLDER_DEFAULT_CLOSING_DELIMITER_REGEX}
     * if the parameter is null or blank
     * @param evaluator the additional evaluator - payload support is chained after the given evaluator
     */
    public static <D extends DifferenceEngineConfigurer<D>>
        D withPlaceholderSupportUsingDelimitersChainedAfter(D configurer, String placeholderOpeningDelimiterRegex,
            String placeholderClosingDelimiterRegex, DifferenceEvaluator evaluator) {
        return configurer.withDifferenceEvaluator(DifferenceEvaluators.chain(
          evaluator, new PlaceholderDifferenceEvaluator(placeholderOpeningDelimiterRegex,
              placeholderClosingDelimiterRegex)));
    }
}
