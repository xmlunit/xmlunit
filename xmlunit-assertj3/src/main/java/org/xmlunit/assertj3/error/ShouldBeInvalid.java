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
package org.xmlunit.assertj3.error;

import org.assertj.core.error.BasicErrorMessageFactory;

/**
 * @since XMLUnit 2.8.1
 */
public class ShouldBeInvalid extends BasicErrorMessageFactory {

    /**
     * @param systemId optional systemId
     * @return ErrorMessageFactory when document is valid
     */
    public static ShouldBeInvalid shouldBeInvalid(String systemId) {

        return new ShouldBeInvalid(systemId != null ? systemId : "instance");
    }

    private ShouldBeInvalid(String systemId) {
        super("%nExpecting:%n <%s>%nto be invalid", unquotedString(systemId));
    }
}
