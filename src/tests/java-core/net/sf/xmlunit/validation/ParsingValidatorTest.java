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
import static org.junit.Assert.*;
import org.junit.Test;

public class ParsingValidatorTest {

    @Test public void shouldSuccessfullyValidateSchemaInstance() {
        ParsingValidator v =
            new ParsingValidator(Languages.W3C_XML_SCHEMA_NS_URI);
        v.setSchemaSource(new StreamSource(new File("src/tests/resources/Book.xsd")));
        ValidationResult r = v.validateInstance(new StreamSource(new File("src/tests/resources/BookXsdGenerated.xml")));
        assertTrue(r.isValid());
        assertFalse(r.getProblems().iterator().hasNext());
    }

    @Test public void shouldFailOnBrokenSchemaInstance() {
        ParsingValidator v =
            new ParsingValidator(Languages.W3C_XML_SCHEMA_NS_URI);
        v.setSchemaSource(new StreamSource(new File("src/tests/resources/Book.xsd")));
        ValidationResult r = v.validateInstance(new StreamSource(new File("src/tests/resources/invalidBook.xml")));
        assertFalse(r.isValid());
        assertTrue(r.getProblems().iterator().hasNext());
    }

    @Test public void shouldSuccessfullyValidateDTDInstance() {
        ParsingValidator v =
            new ParsingValidator(Languages.XML_DTD_NS_URI);
        v.setSchemaSource(new StreamSource(new File("src/tests/resources/Book.dtd")));
        ValidationResult r = v.validateInstance(new StreamSource(new File("src/tests/resources/BookWithDoctype.xml")));
        assertTrue(r.isValid());
        assertFalse(r.getProblems().iterator().hasNext());
    }

    @Test public void shouldFailOnBrokenDTDInstance() {
        ParsingValidator v =
            new ParsingValidator(Languages.XML_DTD_NS_URI);
        v.setSchemaSource(new StreamSource(new File("src/tests/resources/Book.dtd")));
        ValidationResult r = v.validateInstance(new StreamSource(new File("src/tests/resources/invalidBookWithDoctype.xml")));
        assertFalse(r.isValid());
        assertTrue(r.getProblems().iterator().hasNext());
    }
}
