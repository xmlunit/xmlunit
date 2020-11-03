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
import org.assertj.core.error.ErrorMessageFactory;

/**
 * @since XMLUnit 2.8.1
 */
public class ShouldHaveXPath extends BasicErrorMessageFactory {

    public static ErrorMessageFactory shouldHaveXPath(String nodeName, String xPath) {
        return new ShouldHaveXPath(nodeName, xPath);
    }

    private ShouldHaveXPath(String nodeName, String xPath) {
        super("%nExpecting:%n <%s>%nto have XPath: <%s>",
                unquotedString(nodeName),
                unquotedString(xPath));
    }
}
