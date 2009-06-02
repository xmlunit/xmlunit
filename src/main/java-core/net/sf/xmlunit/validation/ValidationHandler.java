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
package net.sf.xmlunit.validation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * ErrorHandler collecting parser exceptions as ValidationProblems
 */
final class ValidationHandler implements ErrorHandler {
    private List<ValidationProblem> problems =
        new LinkedList<ValidationProblem>();
    private boolean valid = true;
    // fatal errors are re-thrown by the parser
    private SAXParseException lastFatalError = null;

    public void error(SAXParseException e) {
        if (e != lastFatalError) {
            valid = false;
            problems.add(ValidationProblem.fromException(e,
                                                         ValidationProblem
                                                         .ProblemType.ERROR)
                         );
        }
    }

    public void fatalError(SAXParseException e) {
        valid = false;
        lastFatalError = e;
        problems.add(ValidationProblem.fromException(e,
                                                     ValidationProblem
                                                     .ProblemType.ERROR));
    }

    public void warning(SAXParseException e) {
        problems.add(ValidationProblem.fromException(e,
                                                     ValidationProblem
                                                     .ProblemType.WARNING));
    }

    ValidationResult getResult() {
        return new ValidationResult(valid,
                                    Collections.unmodifiableList(problems)
                                    );
    }
}
