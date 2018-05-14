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
 * @since XMLUnit 2.6.1
 */
public class ShouldNotHaveAttribute extends BasicErrorMessageFactory {

    public static ErrorMessageFactory shouldNotHaveAttribute(String nodeName, String attributeName) {
        return new ShouldNotHaveAttribute(nodeName, attributeName);
    }

    public static ErrorMessageFactory shouldNotHaveAttributeWithValue(String nodeName, String attributeName, String attributeValue) {
        return new ShouldNotHaveAttribute(nodeName, attributeName, attributeValue);
    }

    private ShouldNotHaveAttribute(String nodeName, String attributeName) {
        super("%nExpecting:%n <%s>%nnot to have attribute:%n <%s>",
                unquotedString(nodeName),
                unquotedString(attributeName));
    }

    private ShouldNotHaveAttribute(String nodeName, String attributeName, String attributeValue) {
        super("%nExpecting:%n <%s>%nnot to have attribute:%n <%s>%nwith value:%n <%s>",
                unquotedString(nodeName),
                unquotedString(attributeName),
                unquotedString(attributeValue));
    }
}
