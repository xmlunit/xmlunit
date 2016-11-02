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
package org.xmlunit.matchers;

import org.junit.Test;
import org.xmlunit.validation.Languages;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.ValidationMatcher.valid;

/**
 * Tests for ValidationMatcher.
 */
public class ValidationMatcherTest {

    @Test
    public void shouldSuccessfullyValidateInstance() {
        assertThat(new StreamSource(new File("../test-resources/BookXsdGenerated.xml")),
                   is(valid(new StreamSource(new File("../test-resources/Book.xsd")))));

    }

    @Test
    public void shouldSuccessfullyValidateInstanceWhenSchemaIsCreatedExternally()
        throws Exception {
        SchemaFactory f = SchemaFactory.newInstance(Languages.W3C_XML_SCHEMA_NS_URI);
        assertThat(new StreamSource(new File("../test-resources/BookXsdGenerated.xml")),
                   is(valid(f.newSchema(new StreamSource(new File("../test-resources/Book.xsd"))))));

    }

    @Test
    public void shouldFailOnBrokenInstance() {
        assertThat(new StreamSource(new File("../test-resources/invalidBook.xml")),
                   is(not(valid(new StreamSource(new File("../test-resources/Book.xsd"))))));
    }

    @Test
    public void shouldFailOnBrokenInstanceWhenSchemaIsCreatedExternally()
        throws Exception {
        SchemaFactory f = SchemaFactory.newInstance(Languages.W3C_XML_SCHEMA_NS_URI);
        assertThat(new StreamSource(new File("../test-resources/invalidBook.xml")),
                   is(not(valid(f.newSchema(new StreamSource(new File("../test-resources/Book.xsd")))))));
    }

    @Test(expected = AssertionError.class)
    public void shouldThrowOnBrokenInstance() {
        assertThat(new StreamSource(new File("../test-resources/invalidBook.xml")),
                   is(valid(new StreamSource(new File("../test-resources/Book.xsd")))));
    }

    @Test
    public void shouldSuccessfullyValidateInstanceWithoutExplicitSchemaSource() {
        assertThat(new StreamSource(new File("../test-resources/BookXsdGenerated.xml")),
                   is(new ValidationMatcher()));

    }

    @Test(expected = AssertionError.class)
    public void shouldThrowOnBrokenInstanceWithoutExplicitSchemaSource() {
        assertThat(new StreamSource(new File("../test-resources/invalidBook.xml")),
                   is(new ValidationMatcher()));

    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenSchemaSourcesContainsNull() {
        valid(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenSchemaSourcesIsNull() {
        new ValidationMatcher((Object[]) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenSchemaIsNull() {
        new ValidationMatcher((Schema) null);
    }

    /**
     * Really only tests there is no NPE.
     * @see "https://github.com/xmlunit/xmlunit/issues/81"
     */
    @Test(expected = AssertionError.class)
    public void canBeCombinedWithFailingMatcher() {
        assertThat("not empty", both(isEmptyString())
                   .and(valid(new StreamSource(new File("../test-resources/Book.xsd")))));
    }

    @Test
    public void canBeCombinedWithPassinggMatcher() {
        assertThat(new StreamSource(new File("../test-resources/BookXsdGenerated.xml")),
                   both(not(nullValue()))
                   .and(valid(new StreamSource(new File("../test-resources/Book.xsd")))));

    }
}
