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
 * <p><b>This class and the whole module are considered experimental
 * and any API may change between releases of XMLUnit.</b></p>
 *
 * @since 2.6.0
 */
public class PlaceholderSupport {
    private PlaceholderSupport() { /* no instances */ }

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
        return withPlaceholderSupportUsingDelimiters(configurer, placeholderOpeningDelimiterRegex,
            placeholderClosingDelimiterRegex, null, null, null);
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
     * @param placeholderArgsOpeningDelimiterRegex regular expression for
     * the opening delimiter of the placeholder's argument list, defaults to {@link
     * PlaceholderDifferenceEvaluator#PLACEHOLDER_DEFAULT_ARGS_OPENING_DELIMITER_REGEX}
     * if the parameter is null or blank
     * @param placeholderArgsClosingDelimiterRegex regular expression for
     * the closing delimiter of the placeholder's argument list, defaults to {@link
     * PlaceholderDifferenceEvaluator#PLACEHOLDER_DEFAULT_ARGS_CLOSING_DELIMITER_REGEX}
     * if the parameter is null or blank
     * @param placeholderArgsSeparatorRegex regular expression for the
     * delimiter between arguments inside of the placeholder's
     * argument list, defaults to {@link
     * PlaceholderDifferenceEvaluator#PLACEHOLDER_DEFAULT_ARGS_SEPARATOR_REGEX}
     * if the parameter is null or blank
     * @return the configurer with placeholder support added in
     * @since 2.7.0
     */
    public static <D extends DifferenceEngineConfigurer<D>>
        D withPlaceholderSupportUsingDelimiters(final D configurer,
            final String placeholderOpeningDelimiterRegex,
            final String placeholderClosingDelimiterRegex,
            final String placeholderArgsOpeningDelimiterRegex,
            final String placeholderArgsClosingDelimiterRegex,
            final String placeholderArgsSeparatorRegex) {
        return configurer.withDifferenceEvaluator(new PlaceholderDifferenceEvaluator(placeholderOpeningDelimiterRegex,
            placeholderClosingDelimiterRegex, placeholderArgsOpeningDelimiterRegex,
            placeholderArgsClosingDelimiterRegex, placeholderArgsSeparatorRegex));
    }

    /**
     * Adds placeholder support to a {@link DifferenceEngineConfigurer} considering an additional {@link DifferenceEvaluator}.
     *
     * @param configurer the configurer to add support to
     * @param evaluator the additional evaluator - placeholder support is
     * {@link DifferenceEvaluators#chain chain}ed after the given
     * evaluator
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
     * @param evaluator the additional evaluator - placeholder support is
     * {@link DifferenceEvaluators#chain chain}ed after the given
     * evaluator
     */
    public static <D extends DifferenceEngineConfigurer<D>>
        D withPlaceholderSupportUsingDelimitersChainedAfter(D configurer, String placeholderOpeningDelimiterRegex,
            String placeholderClosingDelimiterRegex, DifferenceEvaluator evaluator) {
        return withPlaceholderSupportUsingDelimitersChainedAfter(configurer, placeholderOpeningDelimiterRegex,
            placeholderClosingDelimiterRegex, null, null, null, evaluator);
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
     * @param evaluator the additional evaluator - placeholder support is
     * {@link DifferenceEvaluators#chain chain}ed after the given
     * evaluator
     * @param placeholderArgsOpeningDelimiterRegex regular expression for
     * the opening delimiter of the placeholder's argument list, defaults to {@link
     * PlaceholderDifferenceEvaluator#PLACEHOLDER_DEFAULT_ARGS_OPENING_DELIMITER_REGEX}
     * if the parameter is null or blank
     * @param placeholderArgsClosingDelimiterRegex regular expression for
     * the closing delimiter of the placeholder's argument list, defaults to {@link
     * PlaceholderDifferenceEvaluator#PLACEHOLDER_DEFAULT_ARGS_CLOSING_DELIMITER_REGEX}
     * if the parameter is null or blank
     * @param placeholderArgsSeparatorRegex regular expression for the
     * delimiter between arguments inside of the placeholder's
     * argument list, defaults to {@link
     * PlaceholderDifferenceEvaluator#PLACEHOLDER_DEFAULT_ARGS_SEPARATOR_REGEX}
     * if the parameter is null or blank
     */
    public static <D extends DifferenceEngineConfigurer<D>>
        D withPlaceholderSupportUsingDelimitersChainedAfter(D configurer,
            final String placeholderOpeningDelimiterRegex,
            final String placeholderClosingDelimiterRegex,
            final String placeholderArgsOpeningDelimiterRegex,
            final String placeholderArgsClosingDelimiterRegex,
            final String placeholderArgsSeparatorRegex,
            final DifferenceEvaluator evaluator) {
        return configurer.withDifferenceEvaluator(DifferenceEvaluators.chain(
          evaluator, new PlaceholderDifferenceEvaluator(placeholderOpeningDelimiterRegex,
              placeholderClosingDelimiterRegex, placeholderArgsOpeningDelimiterRegex,
              placeholderArgsClosingDelimiterRegex, placeholderArgsSeparatorRegex)));
    }
}
