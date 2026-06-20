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
 * Validation of XML documents and schemas.
 *
 * <p><strong>Security note:</strong> the validators in this package do
 * not restrict access to external DTDs or entities by default. This has
 * been a conscious decision since XMLUnit 2.6.0 because schema
 * validation often needs to load external resources, and the package is
 * meant to be used on trusted input. An instance document with a {@code
 * DOCTYPE} that declares an external entity may therefore cause that
 * entity to be resolved while it is validated, which is an XXE/SSRF
 * vector when the input is untrusted. If you validate untrusted input
 * use {@link org.xmlunit.validation.JAXPValidator#setDisableExternalDtdAccess
 * JAXPValidator.setDisableExternalDtdAccess(true)} or {@link
 * org.xmlunit.validation.ParsingValidator#setDisableExternalEntities
 * ParsingValidator.setDisableExternalEntities(true)} to forbid this.</p>
 */
package org.xmlunit.validation;
