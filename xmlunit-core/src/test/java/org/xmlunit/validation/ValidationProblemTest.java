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

import org.junit.Test;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ValidationProblemTest {

    @Test
    public void trivialGetterTests() {
        ValidationProblem p =
            new ValidationProblem("foo", 1, 2, ValidationProblem.ProblemType.ERROR);
        assertEquals("foo", p.getMessage());
        assertEquals(1, p.getLine());
        assertEquals(2, p.getColumn());
        assertEquals(ValidationProblem.ProblemType.ERROR, p.getType());
    }

    @Test
    public void trivialToStringTest() {
        ValidationProblem p =
            new ValidationProblem("foo", 1, 2, ValidationProblem.ProblemType.ERROR);
        String s = p.toString();
        assertThat(s, containsString("line=1"));
        assertThat(s, containsString("column=2"));
        assertThat(s, containsString("type=ERROR"));
        assertThat(s, containsString("message='foo'"));
    }

    @Test
    public void shouldSubstituteUNKNOWNForUnknownLocations() {
        ValidationProblem p = ValidationProblem
            .fromException(new SAXParseException("foo", new LocatorImpl()),
                           ValidationProblem.ProblemType.ERROR);
        assertEquals(ValidationProblem.UNKNOWN, p.getLine());
        assertEquals(ValidationProblem.UNKNOWN, p.getColumn());
    }
}
