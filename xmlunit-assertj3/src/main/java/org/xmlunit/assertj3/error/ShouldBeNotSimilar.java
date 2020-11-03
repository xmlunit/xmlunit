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
public class ShouldBeNotSimilar extends BasicErrorMessageFactory {

    public static ShouldBeNotSimilar shouldBeNotIdentical(String controlSystemId, String testSystemId) {

        return new ShouldBeNotSimilar(controlSystemId, testSystemId, "identical");
    }

    public static ShouldBeNotSimilar shouldBeNotSimilar(String controlSystemId, String testSystemId) {

        return new ShouldBeNotSimilar(controlSystemId, testSystemId, "similar");
    }

    private ShouldBeNotSimilar(String controlSystemId, String testSystemId, String type) {
        super("%nExpecting:%n <%s> and <%s> to be not %s",
                unquotedString(controlSystemId != null ? controlSystemId : "control instance"),
                unquotedString(testSystemId != null ? testSystemId : "test instance"),
                unquotedString(type));
    }
}
