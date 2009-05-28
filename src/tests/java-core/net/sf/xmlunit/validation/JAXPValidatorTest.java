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

import java.io.File;
import javax.xml.transform.stream.StreamSource;
import net.sf.xmlunit.exceptions.XMLUnitException;
import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;
import org.junit.Test;

public class JAXPValidatorTest {

    @Test public void shouldSuccessfullyValidateSchema() {
        JAXPValidator v = new JAXPValidator(Languages.W3C_XML_SCHEMA_NS_URI);
        v.setSchemaSource(new StreamSource(new File("src/tests/resources/Book.xsd")));
        ValidationResult r = v.validateSchema();
        assertTrue(r.isValid());
        assertFalse(r.getProblems().iterator().hasNext());
    }

    @Test public void shouldSuccessfullyValidateInstance() {
        JAXPValidator v = new JAXPValidator(Languages.W3C_XML_SCHEMA_NS_URI);
        v.setSchemaSource(new StreamSource(new File("src/tests/resources/Book.xsd")));
        ValidationResult r = v.validateInstance(new StreamSource(new File("src/tests/resources/BookXsdGenerated.xml")));
        assertTrue(r.isValid());
        assertFalse(r.getProblems().iterator().hasNext());
    }

    @Test public void shouldFailOnBrokenSchema() {
        JAXPValidator v = new JAXPValidator(Languages.W3C_XML_SCHEMA_NS_URI);
        v.setSchemaSource(new StreamSource(new File("src/tests/resources/broken.xsd")));
        ValidationResult r = v.validateSchema();
        assertFalse(r.isValid());
        assertTrue(r.getProblems().iterator().hasNext());
    }

    @Test public void shouldFailOnBrokenInstance() {
        JAXPValidator v = new JAXPValidator(Languages.W3C_XML_SCHEMA_NS_URI);
        v.setSchemaSource(new StreamSource(new File("src/tests/resources/Book.xsd")));
        ValidationResult r = v.validateInstance(new StreamSource(new File("src/tests/resources/invalidBook.xml")));
        assertFalse(r.isValid());
        assertTrue(r.getProblems().iterator().hasNext());
    }

    @Test public void shouldThrowWhenValidatingInstanceAndSchemaIsInvalid() {
        JAXPValidator v = new JAXPValidator(Languages.W3C_XML_SCHEMA_NS_URI);
        v.setSchemaSource(new StreamSource(new File("src/tests/resources/broken.xsd")));
        try {
            v.validateInstance(new StreamSource(new File("src/tests/resources/BookXsdGenerated.xml")));
            fail("should have thrown an exception");
        } catch (Exception e) {
            assertThat(e, is(XMLUnitException.class));
        }
    }

    @Test public void shouldThrowWhenValidatingInstanceAndSchemaIsNotThere() {
        JAXPValidator v = new JAXPValidator(Languages.W3C_XML_SCHEMA_NS_URI);
        v.setSchemaSource(new StreamSource(new File("src/tests/resources/foo.xsd")));
        try {
            v.validateInstance(new StreamSource(new File("src/tests/resources/BookXsdGenerated.xml")));
            fail("should have thrown an exception");
        } catch (Exception e) {
            assertThat(e, is(XMLUnitException.class));
        }
    }
}
