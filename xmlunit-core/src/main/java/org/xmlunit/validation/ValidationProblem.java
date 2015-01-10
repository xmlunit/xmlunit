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

import org.xml.sax.SAXParseException;

/**
 * A validation "problem" which may be an error or a warning.
 */
public class ValidationProblem {
    /**
     * The type of validation problem encountered.
     */
    public static enum ProblemType {ERROR, WARNING};

    /**
     * Constant used for unknown location information.
     */
    public static final int UNKNOWN = -1;

    private final int line, column;
    private final ProblemType type;
    private final String message;

    /**
     * Creates a ValidationProblem for the given message and location
     * of the given type.
     */
    public ValidationProblem(String message, int line, int column,
                             ProblemType type) {
        this.message = message;
        this.line = line;
        this.column = column;
        this.type = type;
    }

    /**
     * The line where the problem occured or {@link #UNKNOWN UNKNOWN}.
     */
    public int getLine() {
        return line;
    }

    /**
     * The column where the problem occured or {@link #UNKNOWN UNKNOWN}.
     */
    public int getColumn() {
        return column;
    }

    /**
     * Whether this is an error or a warning.
     */
    public ProblemType getType() {
        return type;
    }

    /**
     * The problem's message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ValidationProblem { ");
        sb.append("line=").append(line);
        sb.append(", column=").append(column);
        sb.append(", type=").append(type);
        sb.append(", message='").append(message).append('\'');
        sb.append(" }");
        return sb.toString();
    }

    static ValidationProblem fromException(SAXParseException e,
                                           ProblemType type) {
        return new ValidationProblem(e.getMessage(),
                                     e.getLineNumber() > 0
                                     ? e.getLineNumber() : UNKNOWN,
                                     e.getColumnNumber() > 0
                                     ? e.getColumnNumber() : UNKNOWN,
                                     type);
    }

}
