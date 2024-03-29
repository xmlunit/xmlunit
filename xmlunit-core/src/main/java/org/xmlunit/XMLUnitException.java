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
package org.xmlunit;

/**
 * Base class of any Exception thrown within XMLUnit.
 */
public class XMLUnitException extends RuntimeException {
    private static final long serialVersionUID = 673558045568231955L;

    /**
     * Initializes the exception.
     *
     * @param message the detail message
     * @param cause the root cause of the exception
     */
    public XMLUnitException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Initializes an exception without cause.
     *
     * @param message the detail message
     */
    public XMLUnitException(String message) {
        this(message, null);
    }

    /**
     * Initializes an exception using the wrapped exception's message.
     *
     * @param cause the root cause of the exception
     */
    public XMLUnitException(Throwable cause) {
        this(cause != null ? cause.getMessage() : null, cause);
    }
}
