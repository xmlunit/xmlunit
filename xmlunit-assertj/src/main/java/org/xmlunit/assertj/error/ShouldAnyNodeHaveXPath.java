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
package org.xmlunit.assertj.error;

import org.assertj.core.error.BasicErrorMessageFactory;
import org.assertj.core.error.ErrorMessageFactory;

/**
 * @since XMLUnit 2.6.4
 */
public class ShouldAnyNodeHaveXPath extends BasicErrorMessageFactory {

    /**
     * @param xPath XPath expression
     * @return ErrorMessageFactory when no node has the given XPath
     */
    public static ErrorMessageFactory shouldAnyNodeHaveXPath(String xPath) {
        return new ShouldAnyNodeHaveXPath(xPath);
    }

    private ShouldAnyNodeHaveXPath(String xPath) {
        super("%nExpecting:%nany node in set have XPath: <%s>",
                unquotedString(xPath));
    }
}
