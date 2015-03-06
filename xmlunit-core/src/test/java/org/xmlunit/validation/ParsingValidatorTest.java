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
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.xmlunit.TestResources.BOOK_DTD;
import static org.xmlunit.TestResources.BOOK_XSD;
import static org.xmlunit.TestResources.TEST_RESOURCE_DIR;

import java.io.File;
import java.io.IOException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.LocatorImpl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.xmlunit.ConfigurationException;
import org.xmlunit.TestResources;
import org.xmlunit.XMLUnitException;

public class ParsingValidatorTest {

    @Mock
    private SAXParserFactory fac;

    @Mock
    private SAXParser parser;

    @Before
    public void setupMocks() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(fac.newSAXParser()).thenReturn(parser);
    }

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

    @Test
    public void shouldTranslateSAXParseExceptionDuringParseToValidationError() throws Exception {
        doThrow(new SAXParseException("foo", new LocatorImpl()))
            .when(parser).parse(any(InputSource.class), any(DefaultHandler.class));
        ParsingValidator v =
            new ParsingValidator(Languages.XML_DTD_NS_URI);
        v.setSchemaSource(new StreamSource(new File(BOOK_DTD)));
        ValidationResult r = v.validateInstance(new StreamSource(new File(TEST_RESOURCE_DIR
                                                                          + "BookWithDoctype.xml")),
                                                fac);
        assertFalse(r.isValid());
        assertTrue(r.getProblems().iterator().hasNext());
    }

    @Test(expected = XMLUnitException.class)
    public void shouldMapSAXExceptionDuringParse() throws Exception {
        doThrow(new SAXException()).when(parser)
                .parse(any(InputSource.class), any(DefaultHandler.class));
        ParsingValidator v =
            new ParsingValidator(Languages.XML_DTD_NS_URI);
        v.setSchemaSource(new StreamSource(new File(BOOK_DTD)));
        v.validateInstance(new StreamSource(new File(TEST_RESOURCE_DIR
                                                     + "BookWithDoctype.xml")),
                           fac);
    }

    @Test(expected = XMLUnitException.class)
    public void shouldMapSAXException() throws Exception {
        when(fac.newSAXParser()).thenThrow(new SAXException());
        ParsingValidator v =
            new ParsingValidator(Languages.XML_DTD_NS_URI);
        v.validateInstance(new StreamSource(new File(TEST_RESOURCE_DIR
                                                     + "BookWithDoctype.xml")),
                           fac);
    }

    @Test(expected = ConfigurationException.class)
    public void shouldMapParserConfigurationException() throws Exception {
        when(fac.newSAXParser()).thenThrow(new ParserConfigurationException());
        ParsingValidator v =
            new ParsingValidator(Languages.XML_DTD_NS_URI);
        v.validateInstance(new StreamSource(new File(TEST_RESOURCE_DIR
                                                     + "BookWithDoctype.xml")),
                           fac);
    }

    @Test(expected = ConfigurationException.class)
    public void shouldMapSAXNotRecognizedException() throws Exception {
        doThrow(new SAXNotRecognizedException())
            .when(parser).setProperty(anyString(), any());
        ParsingValidator v =
            new ParsingValidator(Languages.W3C_XML_SCHEMA_NS_URI);
        v.validateInstance(new StreamSource(new File(TEST_RESOURCE_DIR
                                                     + "BookWithDoctype.xml")),
                           fac);
    }

    @Test(expected = ConfigurationException.class)
    public void shouldMapSAXNotSupportedException() throws Exception {
        doThrow(new SAXNotSupportedException())
            .when(parser).setProperty(anyString(), any());
        ParsingValidator v =
            new ParsingValidator(Languages.W3C_XML_SCHEMA_NS_URI);
        v.validateInstance(new StreamSource(new File(TEST_RESOURCE_DIR
                                                     + "BookWithDoctype.xml")),
                           fac);
    }

    @Test(expected = XMLUnitException.class)
    public void shouldMapIOException() throws Exception {
        doThrow(new IOException()).when(parser)
                .parse(any(InputSource.class), any(DefaultHandler.class));
        ParsingValidator v =
            new ParsingValidator(Languages.XML_DTD_NS_URI);
        v.validateInstance(new StreamSource(new File(TEST_RESOURCE_DIR
                                                     + "BookWithDoctype.xml")),
                           fac);
    }
}
