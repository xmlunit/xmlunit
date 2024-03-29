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
 * Exception thrown when anything inside JAXP throws a
 * *ConfigurationException.
 */
public class ConfigurationException extends XMLUnitException {
    private static final long serialVersionUID = 3976394040594872937L;

    /**
     * Creates a new exception from a cause.
     * @param cause the root cause
     */
    public ConfigurationException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new exception from a cause and a custom message.
     * @param message a custom message
     * @param cause the root cause
     * @since XMLUnit 2.6.0
     */
    public ConfigurationException(String message, Throwable cause) {
        super(cause);
    }
}
