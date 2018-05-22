package org.xmlunit.assertj;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.xmlunit.builder.Input;
import org.xmlunit.validation.JAXPValidator;
import org.xmlunit.validation.Languages;
import org.xmlunit.validation.ValidationResult;

import javax.xml.transform.Source;
import javax.xml.validation.Schema;

public class ValidationAssert extends AbstractAssert<ValidationAssert, Object> {

    private final Source[] schemaSource;
    private final Schema schema;
    private ValidationResult result;

    private ValidationAssert(Object actual, Source[] schemaSource, Schema schema) {
        super(actual, ValidationAssert.class);
        this.schemaSource = schemaSource;
        this.schema = schema;
    }

    static ValidationAssert create(Object xmlSource, Object... schemaSource) {

        Assertions.assertThat(schemaSource)
                .isNotNull()
                .isNotEmpty()
                .doesNotContainNull();

        Source[] sources = new Source[schemaSource.length];

        for (int i = 0; i < schemaSource.length; i++) {
            sources[i] = Input.from(schemaSource[i]).build();
        }

        return new ValidationAssert(xmlSource, sources, null);
    }

    static ValidationAssert create(Object xmlSource, Schema schema) {
        Assertions.assertThat(schema).isNotNull();

        return new ValidationAssert(xmlSource, null, schema);
    }

    private void validate() {
        if (result == null) {
            Source source = Input.from(actual).build();
            JAXPValidator validator = new JAXPValidator(Languages.W3C_XML_SCHEMA_NS_URI);
            if (schema != null) {
                validator.setSchema(schema);
            } else {
                validator.setSchemaSources(schemaSource);
            }
            this.result = validator.validateInstance(source);
        }
    }

    public ValidationAssert isValid() {
        validate();
        if (!result.isValid()) {
            failWithMessage("dupa");
        }
        return this;
    }

    public void isNotValid() {
        validate();
        if (result.isValid()) {
            failWithMessage("dupa");
        }
    }
}
