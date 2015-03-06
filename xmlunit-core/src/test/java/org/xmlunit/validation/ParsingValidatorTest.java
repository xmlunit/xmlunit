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

import static org.junit.Assert.*;
import static org.xmlunit.TestResources.BOOK_DTD;
import static org.xmlunit.TestResources.BOOK_XSD;
import static org.xmlunit.TestResources.TEST_RESOURCE_DIR;

import java.io.File;
import javax.xml.transform.stream.StreamSource;
import org.junit.Test;
import org.xmlunit.TestResources;
import org.xmlunit.XMLUnitException;

public class ParsingValidatorTest {

    @Test public void shouldSuccessfullyValidateSchemaInstance() {
        ParsingValidator v =
            new ParsingValidator(Languages.W3C_XML_SCHEMA_NS_URI);
        v.setSchemaSource(new StreamSource(new File(BOOK_XSD)));
        ValidationResult r = v.validateInstance(new StreamSource(new File(TEST_RESOURCE_DIR
                                                                          + "BookXsdGenerated.xml")));
        assertTrue(r.isValid());
        assertFalse(r.getProblems().iterator().hasNext());
    }

    @Test public void shouldFailOnBrokenSchemaInstance() {
        ParsingValidator v =
            new ParsingValidator(Languages.W3C_XML_SCHEMA_NS_URI);
        v.setSchemaSource(new StreamSource(new File(BOOK_XSD)));
        ValidationResult r = v.validateInstance(new StreamSource(new File(TEST_RESOURCE_DIR
                                                                          + "invalidBook.xml")));
        assertFalse(r.isValid());
        assertTrue(r.getProblems().iterator().hasNext());
    }

    @Test public void shouldSuccessfullyValidateDTDInstance() {
        ParsingValidator v =
            new ParsingValidator(Languages.XML_DTD_NS_URI);
        v.setSchemaSource(new StreamSource(new File(BOOK_DTD)));
        ValidationResult r = v.validateInstance(new StreamSource(new File(TEST_RESOURCE_DIR
                                                                          + "BookWithDoctype.xml")));
        assertTrue(r.isValid());
        assertFalse(r.getProblems().iterator().hasNext());
    }

    @Test public void shouldFailOnBrokenDTDInstance() {
        ParsingValidator v =
            new ParsingValidator(Languages.XML_DTD_NS_URI);
        v.setSchemaSource(new StreamSource(new File(BOOK_DTD)));
        ValidationResult r = v.validateInstance(new StreamSource(new File(TEST_RESOURCE_DIR
                                                                          + "invalidBookWithDoctype.xml")));
        assertFalse(r.isValid());
        assertTrue(r.getProblems().iterator().hasNext());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRejectRelaxNG() {
        new ParsingValidator(Languages.RELAXNG_NS_URI);
    }

    @Test(expected = XMLUnitException.class)
    public void shouldNotAllowSchemaValidation() {
        ParsingValidator v =
            new ParsingValidator(Languages.XML_DTD_NS_URI);
        v.setSchemaSource(new StreamSource(new File(BOOK_DTD)));
        v.validateSchema();
    }


}
