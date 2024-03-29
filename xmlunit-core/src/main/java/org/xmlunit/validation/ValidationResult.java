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
package org.xmlunit.validation;

/**
 * The result of a validation.
 */
public class ValidationResult {
    private final boolean valid;
    private final Iterable<ValidationProblem> problems;

    /**
     * Creates a result based on a valid flag and an Iterable of
     * iteration problems.
     * @param valid whether validation has been successful
     * @param problems the problems detected
     */
    public ValidationResult(boolean valid, Iterable<ValidationProblem> problems) {
        this.valid = valid;
        this.problems = problems;
    }

    /**
     * Has the validation been successful?
     *
     * <p>A successful validation results in no validation problems of
     * type ERROR.</p>
     * @return whether validation has been successful
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Retrieves the problems that have been found.
     * @return problems detected
     */
    public Iterable<ValidationProblem> getProblems() {
        return problems;
    }
}
