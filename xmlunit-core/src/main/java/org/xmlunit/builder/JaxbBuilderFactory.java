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

package org.xmlunit.builder;

import javax.xml.transform.Source;

/**
 * Interface that creates a JAXB Builder.
 *
 * @since 2.9.0
 */
public interface JaxbBuilderFactory {
    /**
     * Create a concrete instance of a JaxbBuilder.
     *
     * @param object the object to translate into a {@link Source}.
     * @return concrete instance of a JaxbBuilder
     */
    JaxbBuilder create(Object object);
}
