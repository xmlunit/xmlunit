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
using System.Xml.Schema;

namespace net.sf.xmlunit.validation {

    /// <summary>
    /// A validation "problem" which may be an error or a warning.
    /// </summary>
    public class ValidationProblem {
        public const int UNKNOWN = -1;

        private readonly int line, column;
        private readonly XmlSeverityType type;
        private readonly string message;

        public ValidationProblem(string message, int line, int column,
                                 XmlSeverityType type) {
            this.message = message;
            this.line = line;
            this.column = column;
            this.type = type;
        }

        /// <summary>
        /// The line where the problem occured or UNKNOWN.
        /// </summary>
        public int Line {
            get {
                return line;
            }
        }

        /// <summary>
        /// The column where the problem occured or UNKNOWN.
        /// </summary>
        public int Column {
            get {
                return column;
            }
        }

        /// <summary>
        /// Whether this is an error or a warning.
        /// </summary>
        public XmlSeverityType Type {
            get {
                return type;
            }
        }

        /// <summary>
        /// The problem's message.
        /// </summary>
        public string Message {
            get {
                return message;
            }
        }

        internal static ValidationProblem FromEvent(ValidationEventArgs e) {
            XmlSchemaException ex = e.Exception;
            return new ValidationProblem(e.Message,
                                         ex == null ? UNKNOWN : ex.LineNumber,
                                         ex == null ? UNKNOWN : ex.LinePosition,
                                         e.Severity);
        }
    }
}
