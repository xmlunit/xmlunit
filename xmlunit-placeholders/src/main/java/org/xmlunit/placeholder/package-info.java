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

/**
 * This package contains experimental support for configuring parts of
 * the test engine by using {@code ${xmlunit.KEYWORD}} sequences
 * inside the control document.
 *
 * <p><b>This package and the whole module are considered experimental
 * and any API may change between releases of XMLUnit.</b></p>
 *
 * <p>The placeholder feature allows a placeholder sequence of {@code
 * ${xmlunit.KEYWORD(args...)}} to be used as nested text in elements or as
 * attribute values of the control document and trigger special
 * handling based on the keyword.</p>
 *
 * <p>The "special handling" is controlled by an instance of {@link
 * PlaceholderHandler} per keyword. The class {@link
 * PlaceholderDifferenceEvaluator} loads all implementations via
 * {@code java.util.ServiceLoader} so it is possible to extend the set
 * of handlers via your own modules.</p>
 *
 * <p>The placeholder sequence can take any number of string values as
 * arguments in the form {@code ${xmlunit.KEYWORD(args1,arg2)}} - if
 * no arguments are used the parentheses can be omitted
 * completely. Arguments are not quoted, whitespace inside of the
 * argument list is significant. All separators (by default
 * <code>${</code>, <code>}</code>, {@code (}, {@code )}, and {@code
 * ,}) can be configured explicitly.</p>
 *
 * <p>Keywords currently supported by built-in handlers are:</p>
 *
 * <ul>
 *
 * <li>{@code ${xmlunit.ignore}} which makes XMLUnit ignore the nested
 * text or attribute completely. This is handled by {@link
 * IgnorePlaceholderHandler}.</li>
 *
 * <li>{@code ${xmlunit.isNumber}} makes the comparison pass if the
 * textual content of the element or attributes looks like a
 * number. This is handled by {@link IsNumberPlaceholderHandler}.</li>
 *
 * <li>{@code ${xmlunit.matchesRegex}} makes the comparison pass if
 * the textual content of the element or attribute matches the regular
 * expression specified as the first (and only) argument.  If there is
 * no argument at all, the comparison will fail. This is handled by
 * {@link MatchesRegexPlaceholderHandler}.</li>
 *
 * <li>{@code ${xmlunit.isDateTime}} makes the comparison pass if the
 * textual content of the element or attributes looks like a date or
 * datetime in the current locale or parsed by ISO rules. An optional
 * argument can be used to specify a {@link
 * java.text.SimpleDateFormat} pattern to use when trying to parse the
 * test. This is handled by {@link IsDateTimePlaceholderHandler}.</li>
 *
 * </ul>
 *
 * <p>The default delimiters of <code>${</code> and <code>}</code> can
 * be overwritten using custom regular expressions.</p>
 *
 * <p>The easiest way to activate the placeholder feature is to use
 * one of the methods in {@link PlaceholderSupport} to add it to a
 * {@link org.xmlunit.builder.DiffBuilder} or {@code
 * org.xmlunit.matchers.CompareMatcher} instance. The alternative
 * approach is to create an instance of {@link
 * PlaceholderDifferenceEvaluator} as a {@link
 * org.xmlunit.diff.DifferenceEvaluator} and add it to the builder
 * yourself.</p>
 *
 * @since 2.6.0
 */
package org.xmlunit.placeholder;
