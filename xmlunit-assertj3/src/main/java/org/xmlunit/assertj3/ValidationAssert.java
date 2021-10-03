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
package org.xmlunit.assertj3;

import org.assertj.core.api.AbstractAssert;
import org.xmlunit.builder.Input;
import org.xmlunit.validation.JAXPValidator;
import org.xmlunit.validation.Languages;
import org.xmlunit.validation.ValidationResult;

import javax.xml.transform.Source;
import javax.xml.validation.Schema;

import static org.xmlunit.assertj3.AssertionsAdapter.withAssertInfo;
import static org.xmlunit.assertj3.error.ShouldBeInvalid.shouldBeInvalid;
import static org.xmlunit.assertj3.error.ShouldBeValid.shouldBeValid;

/**
 * Assertion methods for XML validation.
 *
 * <p><b>Simple Example</b></p>
 *
 * <pre>
 * import static org.xmlunit.assertj.XmlAssert.assertThat;
 *
 * final String xml = &quot;&lt;a&gt;&lt;b attr=\&quot;abc\&quot;&gt;&lt;/b&gt;&lt;/a&gt;&quot;;
 *
 * assertThat(xml).isValid();
 * </pre>
 *
 * @since XMLUnit 2.8.1
 */
public class ValidationAssert extends AbstractAssert<ValidationAssert, Source> {

    private final Source[] schemaSources;
    private final Schema schema;

    private ValidationAssert(Source actual, Source[] schemaSources, Schema schema) {
        super(actual, ValidationAssert.class);
        this.schemaSources = schemaSources;
        this.schema = schema;
    }

    static ValidationAssert create(Object xmlSource, XmlAssertConfig config, Object... schemaSources) {

        AssertionsAdapter.assertThat(xmlSource, config.info).isNotNull();

        AssertionsAdapter.assertThat(schemaSources, config.info)
                .isNotNull()
                .doesNotContainNull();

        Source source = Input.from(xmlSource).build();

        Source[] sources = new Source[schemaSources.length];

        for (int i = 0; i < schemaSources.length; i++) {
            sources[i] = Input.from(schemaSources[i]).build();
        }

        return withAssertInfo(new ValidationAssert(source, sources, null), config.info);
    }

    static ValidationAssert create(Object xmlSource, Schema schema, XmlAssertConfig config) {

        AssertionsAdapter.assertThat(xmlSource, config.info).isNotNull();
        AssertionsAdapter.assertThat(schema, config.info).isNotNull();

        Source source = Input.from(xmlSource).build();

        return withAssertInfo(new ValidationAssert(source, null, schema), config.info);
    }

    static ValidationAssert create(Object xmlSource, XmlAssertConfig config) {

        Source source = Input.from(xmlSource).build();

        return withAssertInfo(new ValidationAssert(source, null, null), config.info);
    }

    private ValidationResult validate() {

        JAXPValidator validator = new JAXPValidator(Languages.W3C_XML_SCHEMA_NS_URI);
        if (schema != null) {
            validator.setSchema(schema);
        } else if (schemaSources != null && schemaSources.length > 0) {
            validator.setSchemaSources(schemaSources);
        }
        return validator.validateInstance(actual);
    }

    /**
     * Verifies that actual value is valid against given schema
     *
     * @throws AssertionError if the actual value is not valid against schema
     */
    public ValidationAssert isValid() {
        ValidationResult validationResult = validate();
        if (!validationResult.isValid()) {
            throwAssertionError(shouldBeValid(actual.getSystemId(), validationResult.getProblems()));
        }
        return this;
    }

    /**
     * Verifies that actual value is not valid against given schema
     *
     * @throws AssertionError if the actual value is valid against schema
     */
    public void isInvalid() {
        ValidationResult validateResult = validate();
        if (validateResult.isValid()) {
            throwAssertionError(shouldBeInvalid(actual.getSystemId()));
        }
    }
}
