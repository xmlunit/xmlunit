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
package org.xmlunit.diff;

import org.w3c.dom.Element;

/**
 * Strategy used by {@link DefaultNodeMatcher} for selecting matching
 * elements.
 */
public interface ElementSelector {
    /**
     * Determine whether the two elements from the control and test
     * XML can be compared.
     * @param controlElement element of the control XML
     * @param testElement element of the test XML
     * @return true if the two elements can be compared
     */
    boolean canBeCompared(Element controlElement, Element testElement);
}
