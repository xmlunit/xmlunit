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
package net.sf.xmlunit.matcher;

import org.junit.Test;

import javax.xml.transform.stream.StreamSource;
import java.io.File;

import static net.sf.xmlunit.matcher.ValidationMatcher.valid;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

/**
 * Tests for ValidationMatcher.
 */
public class ValidationMatcherTest {

    @Test
    public void shouldSuccessfullyValidateInstance() {
        assertThat(new StreamSource(new File("src/tests/resources/BookXsdGenerated.xml")),
                is(valid(new StreamSource(new File("src/tests/resources/Book.xsd")))));

    }

    @Test
    public void shouldFailOnBrokenInstance() {
        assertThat(new StreamSource(new File("src/tests/resources/invalidBook.xml")),
                is(not(valid(new StreamSource(new File("src/tests/resources/Book.xsd"))))));
    }
}
