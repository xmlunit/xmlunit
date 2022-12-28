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
 * This package contains the JAXB builder using the {@code jakarta.xml.bind} package.
 *
 * <p>This package is used by default when {@link org.xmlunit.builder.Input#fromJaxb} is used and the {@code
 * xmlunit-jakarta-jaxb-impl} module is present. This package requires at least the 3.x version of the <a
 * href="https://jakarta.ee/specifications/xml-binding/">JakartaEE</a> version of Jakarta XML Binding. If you want to
 * use the {@code javax.xml.bind} version of JAXB instead, the {@code xmlunit-jakarta-jaxb-impl} module must not be
 * present and you use the {@code org.xmlunit.builder.javax_jaxb} package instead.</p>
 *
 * @since 2.9.0
 */
package org.xmlunit.builder.jakarta_jaxb;
