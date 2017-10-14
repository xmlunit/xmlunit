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
 * <p>You configure the difference engine to use {@link
 * PlaceholderDifferenceEvaluator} and it will act on the supported
 * keywords.</p>
 *
 * <p>Currently supported are:</p>
 *
 * <ul>
 * <li>{@code ${xmlunit.ignore}} which makes XMLUnit ignore the element containing it completely.</li>
 * </ul>
 *
 * <p><b>This package and the whole module are considered experimental
 * and any API may change between releases of XMLUnit.</b></p>
 * @since 2.5.1
 */
package org.xmlunit.placeholder;
