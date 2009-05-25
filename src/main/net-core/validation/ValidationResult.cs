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

using System.Collections.Generic;

namespace net.sf.xmlunit.validation {

    /// <summary>
    /// The result of a validation.
    /// </summary>
public class ValidationResult {
    private readonly bool valid;
    private readonly IEnumerable<ValidationProblem> problems;

    public ValidationResult(bool valid, IEnumerable<ValidationProblem> problems) {
        this.valid = valid;
        this.problems = problems;
    }

    /// <summary>
    /// Has the validation been successful?
    /// </summary>
    public bool Valid {
        get {
        return valid;
        }
    }

    /// <summary>
    /// Retrieves the problems that have been found.
    /// </summary>
    public IEnumerable<ValidationProblem> Problems {
        get {
        return problems;
        }
    }
}
}

