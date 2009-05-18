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
using System;

namespace net.sf.xmlunit.exceptions {

    /// <summary>
    /// Base class of any Exception thrown within XMLUnit.
    /// </summary>
    public class XMLUnitException : Exception {
        /// <summary>
        /// Inititializes the exception.
        /// </summary>
        /// <param name="message">the detail message</param>
        /// <param name="cause">the root cause of the exception</param>
        public XMLUnitException(string message, Exception cause) :
            base(message, cause) {
        }

        /// <summary>
        /// Inititializes an exception without cause.
        /// </summary>
        /// <param name="message">the detail message</param>
        public XMLUnitException(string message) :
            base(message, null) {
        }

        ///<summary>
        /// Inititializes an exception using the wrapped exception's message.
        ///</summary>
        /// <param name="cause">the root cause of the exception</param>
        public XMLUnitException(Exception cause) :
            base(cause != null ? cause.Message : null, cause) {
        }
    }
}
