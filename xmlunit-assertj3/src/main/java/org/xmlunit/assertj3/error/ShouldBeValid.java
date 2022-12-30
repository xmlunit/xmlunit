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
import org.xmlunit.validation.ValidationProblem;

import static java.lang.String.format;

/**
 * @since XMLUnit 2.8.1
 */
public class ShouldBeValid extends BasicErrorMessageFactory {

    /**
     * @param systemId optional systemId
     * @param problems validation problems detected
     * @return ErrorMessageFactory when document is not valid
     */
    public static ShouldBeValid shouldBeValid(String systemId, Iterable<ValidationProblem> problems) {
        String systemId1 = systemId != null ? systemId : "instance";

        StringBuilder builder = new StringBuilder();
        int index = 1;

        for (ValidationProblem problem : problems) {
            builder.append(index++).append(".");

            if (problem.getLine() != ValidationProblem.UNKNOWN) {
                builder.append(" line=").append(problem.getLine()).append(';');
            }
            if (problem.getColumn() != ValidationProblem.UNKNOWN) {
                builder.append(" column=").append(problem.getColumn()).append(';');
            }

            builder.append(" type=").append(problem.getType()).append(';');
            builder.append(" message=").append(problem.getMessage());
            builder.append("%n");
        }

        String problemsStr = format(builder.toString());

        return new ShouldBeValid(systemId1, problemsStr);
    }


    private ShouldBeValid(String systemId, String problems) {
        super("%nExpecting:%n <%s>%nto be valid but found following problems:%n%s",
                unquotedString(systemId),
                unquotedString(problems));
    }
}
