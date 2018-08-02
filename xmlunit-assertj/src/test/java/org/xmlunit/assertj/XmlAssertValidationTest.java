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
package org.xmlunit.assertj;

import org.junit.Rule;
import org.junit.Test;
import org.xmlunit.validation.Languages;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;

import static org.xmlunit.assertj.ExpectedException.none;
import static org.xmlunit.assertj.XmlAssert.assertThat;

public class XmlAssertValidationTest {

    @Rule
    public ExpectedException thrown = none();

    @Test
    public void testIsValidAgainst_shouldPass() {
        StreamSource xml = new StreamSource(new File("../test-resources/BookXsdGenerated.xml"));
        StreamSource xsd = new StreamSource(new File("../test-resources/Book.xsd"));

        assertThat(xml).isValidAgainst(xsd);
    }

    @Test
    public void testIsValidAgainst_withExternallyCreatedSchemaInstance_shouldPass() throws Exception {
        StreamSource xml = new StreamSource(new File("../test-resources/BookXsdGenerated.xml"));
        StreamSource xsd = new StreamSource(new File("../test-resources/Book.xsd"));

        SchemaFactory factory = SchemaFactory.newInstance(Languages.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(xsd);

        assertThat(xml).isValidAgainst(schema);
    }

    @Test
    public void testIsNotValidAgainst_withBrokenXml_shouldPass() {
        final StreamSource xml = new StreamSource(new File("../test-resources/invalidBook.xml"));
        final StreamSource xsd = new StreamSource(new File("../test-resources/Book.xsd"));

        assertThat(xml).isNotValidAgainst(xsd);
    }

    @Test
    public void testIsNotValidAgainst_withBrokenXml_andExternallyCreatedSchemaInstance_shouldPass() throws Exception {
        StreamSource xml = new StreamSource(new File("../test-resources/invalidBook.xml"));
        StreamSource xsd = new StreamSource(new File("../test-resources/Book.xsd"));

        SchemaFactory factory = SchemaFactory.newInstance(Languages.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(xsd);

        assertThat(xml).isNotValidAgainst(schema);
    }

    @Test
    public void testIsValidAgainst_withBrokenXml_shouldFailed() {

        thrown.expectAssertionErrorPattern("^\\nExpecting:\\n <.*\\.\\.\\/test-resources\\/invalidBook.xml>\\nto be valid but found following problems:\\n.*");
        thrown.expectAssertionError("1. line=9; column=8; type=ERROR;" +
                " message=cvc-complex-type.2.4.b: The content of element 'Book' is not complete." +
                " One of '{\"https://www.xmlunit.org/publishing\":Publisher}' is expected.");

        StreamSource xml = new StreamSource(new File("../test-resources/invalidBook.xml"));
        StreamSource xsd = new StreamSource(new File("../test-resources/Book.xsd"));

        assertThat(xml).isValidAgainst(xsd);
    }

    @Test
    public void testIsValidAgainst_withEmptySourcesArray_shouldPass() {

        StreamSource xml = new StreamSource(new File("../test-resources/BookXsdGenerated.xml"));

        assertThat(xml).isValidAgainst();
        assertThat(xml).isValidAgainst(new Object[0]);
    }

    @Test
    public void testIsValidAgainst_withBrokenXmlAndEmptySourcesArray_shouldFailed() {

        thrown.expectAssertionError("1. line=9; column=8; type=ERROR;" +
                " message=cvc-complex-type.2.4.b: The content of element 'Book' is not complete." +
                " One of '{\"https://www.xmlunit.org/publishing\":Publisher}' is expected.");

        StreamSource xml = new StreamSource(new File("../test-resources/invalidBook.xml"));

        assertThat(xml).isValidAgainst();
    }

    @Test
    public void testIsValid_shouldPass() {

        StreamSource xml = new StreamSource(new File("../test-resources/BookXsdGenerated.xml"));

        assertThat(xml).isValid();
    }

    @Test
    public void testIsValid_withBrokenXml_shouldPass() {

        thrown.expectAssertionError("1. line=9; column=8; type=ERROR;" +
                " message=cvc-complex-type.2.4.b: The content of element 'Book' is not complete." +
                " One of '{\"https://www.xmlunit.org/publishing\":Publisher}' is expected.");

        StreamSource xml = new StreamSource(new File("../test-resources/invalidBook.xml"));

        assertThat(xml).isValid();
    }

    @Test
    public void testIsInvalid_withBrokenXml_shouldPass() {

        StreamSource xml = new StreamSource(new File("../test-resources/invalidBook.xml"));

        assertThat(xml).isInvalid();
    }

    @Test
    public void testIsInvalid_shouldField() {

        thrown.expectAssertionErrorPattern("^\\nExpecting:\\n <.*\\.\\.\\/test-resources\\/BookXsdGenerated.xml>\\nto be invalid");

        StreamSource xml = new StreamSource(new File("../test-resources/BookXsdGenerated.xml"));

        assertThat(xml).isInvalid();
    }

    @Test
    public void testIsValidAgainst_withNullSchemaSources_shouldFailed() {

        thrown.expectAssertionError("actual not to be null");

        StreamSource xml = new StreamSource(new File("../test-resources/BookXsdGenerated.xml"));

        assertThat(xml).isValidAgainst((Object[]) null);
    }

    @Test
    public void testIsValidAgainst_withNullSchema_shouldFailed() {

        thrown.expectAssertionError("actual not to be null");

        StreamSource xml = new StreamSource(new File("../test-resources/BookXsdGenerated.xml"));

        assertThat(xml).isValidAgainst((Schema) null);
    }
}
