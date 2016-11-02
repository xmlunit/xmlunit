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

import static org.hamcrest.core.IsInstanceOf.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.xmlunit.TestResources.TEST_RESOURCE_DIR;

import java.io.File;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.xmlunit.TestResources;
import org.xmlunit.XMLUnitException;

public class JAXPValidatorTest {
    private static final File BOOK_XSD = new File(TestResources.BOOK_XSD);

    @Mock
    private SchemaFactory fac;

    @Mock
    private Schema schema;

    @Mock
    private javax.xml.validation.Validator validator;

    @Before
    public void setupMocks() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(fac.newSchema(any(Source[].class))).thenReturn(schema);
        when(fac.newSchema()).thenReturn(schema);
        when(schema.newValidator()).thenReturn(validator);
    }

    @Test public void shouldSuccessfullyValidateSchema() {
        JAXPValidator v = new JAXPValidator(Languages.W3C_XML_SCHEMA_NS_URI);
        v.setSchemaSource(new StreamSource(BOOK_XSD));
        ValidationResult r = v.validateSchema();
        assertTrue(r.isValid());
        assertFalse(r.getProblems().iterator().hasNext());
    }

    @Test public void shouldSuccessfullyValidateInstance() {
        JAXPValidator v = new JAXPValidator(Languages.W3C_XML_SCHEMA_NS_URI);
        v.setSchemaSource(new StreamSource(BOOK_XSD));
        ValidationResult r = v.validateInstance(new StreamSource(new File(TEST_RESOURCE_DIR
                                                                          + "BookXsdGenerated.xml")));
        assertTrue(r.isValid());
        assertFalse(r.getProblems().iterator().hasNext());
    }

    @Test public void shouldSuccessfullyValidateInstanceWhenSchemaIsCreatedExternally()
        throws Exception {
        JAXPValidator v = new JAXPValidator(Languages.W3C_XML_SCHEMA_NS_URI);
        SchemaFactory f = SchemaFactory.newInstance(Languages.W3C_XML_SCHEMA_NS_URI);
        v.setSchema(f.newSchema(new StreamSource(BOOK_XSD)));
        ValidationResult r = v.validateInstance(new StreamSource(new File(TEST_RESOURCE_DIR
                                                                          + "BookXsdGenerated.xml")));
        assertTrue(r.isValid());
        assertFalse(r.getProblems().iterator().hasNext());
    }

    @Test public void shouldSuccessfullyValidateInstanceWithoutSchemaSource() {
        JAXPValidator v = new JAXPValidator(Languages.W3C_XML_SCHEMA_NS_URI);
        ValidationResult r = v.validateInstance(new StreamSource(new File(TEST_RESOURCE_DIR
                                                                          + "BookXsdGeneratedWithFixedSchemaLocation.xml")));
        assertTrue(r.isValid());
        assertFalse(r.getProblems().iterator().hasNext());
    }

    @Test public void shouldFailOnBrokenSchema() {
        JAXPValidator v = new JAXPValidator(Languages.W3C_XML_SCHEMA_NS_URI);
        v.setSchemaSource(new StreamSource(new File(TEST_RESOURCE_DIR + "broken.xsd")));
        ValidationResult r = v.validateSchema();
        assertFalse(r.isValid());
        assertTrue(r.getProblems().iterator().hasNext());
    }

    @Test public void shouldFailOnBrokenInstance() {
        JAXPValidator v = new JAXPValidator(Languages.W3C_XML_SCHEMA_NS_URI);
        v.setSchemaSource(new StreamSource(BOOK_XSD));
        ValidationResult r = v.validateInstance(new StreamSource(new File(TEST_RESOURCE_DIR
                                                                          + "invalidBook.xml")));
        assertFalse(r.isValid());
        assertTrue(r.getProblems().iterator().hasNext());
    }

    @Test public void shouldFailOnBrokenInstanceWhenSchemaIsCreatedExternally()
        throws Exception {
        JAXPValidator v = new JAXPValidator(Languages.W3C_XML_SCHEMA_NS_URI);
        SchemaFactory f = SchemaFactory.newInstance(Languages.W3C_XML_SCHEMA_NS_URI);
        v.setSchema(f.newSchema(new StreamSource(BOOK_XSD)));
        ValidationResult r = v.validateInstance(new StreamSource(new File(TEST_RESOURCE_DIR
                                                                          + "invalidBook.xml")));
        assertFalse(r.isValid());
        assertTrue(r.getProblems().iterator().hasNext());
    }

    @Test public void shouldThrowWhenValidatingInstanceAndSchemaIsInvalid() {
        JAXPValidator v = new JAXPValidator(Languages.W3C_XML_SCHEMA_NS_URI);
        v.setSchemaSource(new StreamSource(new File(TEST_RESOURCE_DIR + "broken.xsd")));
        try {
            v.validateInstance(new StreamSource(new File(TEST_RESOURCE_DIR
                                                         + "BookXsdGenerated.xml")));
            fail("should have thrown an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(XMLUnitException.class));
        }
    }

    @Test public void shouldThrowWhenValidatingInstanceAndSchemaIsNotThere() {
        JAXPValidator v = new JAXPValidator(Languages.W3C_XML_SCHEMA_NS_URI);
        v.setSchemaSource(new StreamSource(new File(TEST_RESOURCE_DIR + "foo.xsd")));
        try {
            v.validateInstance(new StreamSource(new File(TEST_RESOURCE_DIR
                                                         + "BookXsdGenerated.xml")));
            fail("should have thrown an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(XMLUnitException.class));
        }
    }

    @Test
    public void validateSchemaTranslatesSAXParseExceptionIntoValidationError() throws Exception {
        when(fac.newSchema(any(Source[].class)))
            .thenThrow(new SAXParseException("foo", new LocatorImpl()));
        JAXPValidator v = new JAXPValidator(Languages.W3C_XML_SCHEMA_NS_URI, fac);
        v.setSchemaSource(new StreamSource(BOOK_XSD));
        ValidationResult r = v.validateSchema();
        assertFalse(r.isValid());
        assertTrue(r.getProblems().iterator().hasNext());
    }

    @Test(expected=XMLUnitException.class)
    public void validateSchemaTranslatesSAXException() throws Exception {
        when(fac.newSchema(any(Source[].class))).thenThrow(new SAXException());
        JAXPValidator v = new JAXPValidator(Languages.W3C_XML_SCHEMA_NS_URI, fac);
        v.setSchemaSource(new StreamSource(BOOK_XSD));
        v.validateSchema();
    }

    @Test
    public void validateInstanceTranslatesSAXParseExceptionIntoValidationError() throws Exception {
        doThrow(new SAXParseException("foo", new LocatorImpl()))
            .when(validator).validate(any(Source.class));
        JAXPValidator v = new JAXPValidator(Languages.W3C_XML_SCHEMA_NS_URI, fac);
        v.setSchemaSource(new StreamSource(BOOK_XSD));
        ValidationResult r = v.validateInstance(new StreamSource(new File(TEST_RESOURCE_DIR
                                                                          + "BookXsdGenerated.xml")));
        assertFalse(r.isValid());
        assertTrue(r.getProblems().iterator().hasNext());
    }

    @Test(expected=XMLUnitException.class)
    public void validateInstanceTranslatesSAXException() throws Exception {
        doThrow(new SAXException()).when(validator).validate(any(Source.class));
        JAXPValidator v = new JAXPValidator(Languages.W3C_XML_SCHEMA_NS_URI, fac);
        v.setSchemaSource(new StreamSource(BOOK_XSD));
        v.validateInstance(new StreamSource(new File(TEST_RESOURCE_DIR
                                                     + "BookXsdGenerated.xml")));
    }
}
