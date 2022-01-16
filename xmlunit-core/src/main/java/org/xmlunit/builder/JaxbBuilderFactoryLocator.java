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

import java.util.Iterator;
import java.util.ServiceLoader;

import org.xmlunit.builder.javax_jaxb.DefaultJaxbBuilderFactory;

/**
 * Helps finding the proper JAXB implementation.
 *
 * @since 2.9.0
 */
class JaxbBuilderFactoryLocator {
    /**
     * Provides the configured JaxbBuilderFactory.
     */
    static JaxbBuilderFactory getFactory() {
        final ServiceLoader<JaxbBuilderFactory> sl = ServiceLoader.load(JaxbBuilderFactory.class);
        final Iterator<JaxbBuilderFactory> factories = sl.iterator();
        return factories.hasNext() ? factories.next() : new DefaultJaxbBuilderFactory();
    }
}
