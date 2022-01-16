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
 * This package contains the JAXB builder using the {@code javax.xml.bind} package.
 *
 * <p>This package is used by default when {@link Input#fromJaxb} is used and the {@code xmlunit-jakarta-jaxb-impl}
 * module is not present. If you are running on Java7 or 8 JAXB is part of the Java classlibrary, with later versions
 * you need the JAXB specification and implementation of <a href="https://javaee.github.io/jaxb-v2/">JavaEE</a> or <a
 * href="https://jakarta.ee/specifications/xml-binding/">JakartaEE</a>. This package is only compatible with the 2.x
 * version of Jakarta XML Binding, for version 3.x and later you must use the {@code xmlunit-jakarta-jaxb-impl} module
 * and the {@code org.xmlunit.builder.jakarta_jaxb} package.</p>
 *
 * @since 2.9.0
 */
package org.xmlunit.builder.javax_jaxb;

import org.xmlunit.builder.Input;
