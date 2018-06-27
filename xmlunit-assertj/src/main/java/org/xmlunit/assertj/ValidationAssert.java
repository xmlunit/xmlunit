package org.xmlunit.assertj;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.xmlunit.builder.Input;
import org.xmlunit.validation.JAXPValidator;
import org.xmlunit.validation.Languages;
import org.xmlunit.validation.ValidationResult;

import javax.xml.transform.Source;
import javax.xml.validation.Schema;

import static org.xmlunit.assertj.error.ShouldBeInvalid.shouldBeInvalid;
import static org.xmlunit.assertj.error.ShouldBeValid.shouldBeValid;

public class ValidationAssert extends AbstractAssert<ValidationAssert, Source> {

    private final Source[] schemaSources;
    private final Schema schema;

    private ValidationAssert(Source actual, Source[] schemaSources, Schema schema) {
        super(actual, ValidationAssert.class);
        this.schemaSources = schemaSources;
        this.schema = schema;
    }

    static ValidationAssert create(Object xmlSource, Object... schemaSources) {

        Assertions.assertThat(xmlSource).isNotNull();

        Assertions.assertThat(schemaSources)
                .isNotNull()
                .isNotEmpty()
                .doesNotContainNull();

        Source source = Input.from(xmlSource).build();

        Source[] sources = new Source[schemaSources.length];

        for (int i = 0; i < schemaSources.length; i++) {
            sources[i] = Input.from(schemaSources[i]).build();
        }

        return new ValidationAssert(source, sources, null);
    }

    static ValidationAssert create(Object xmlSource, Schema schema) {

        Assertions.assertThat(xmlSource).isNotNull();
        Assertions.assertThat(schema).isNotNull();

        Source source = Input.from(xmlSource).build();

        return new ValidationAssert(source, null, schema);
    }

    static ValidationAssert create(Object xmlSource) {

        Source source = Input.from(xmlSource).build();

        return new ValidationAssert(source, null, null);
    }

    private ValidationResult validate() {

        JAXPValidator validator = new JAXPValidator(Languages.W3C_XML_SCHEMA_NS_URI);
        if (schema != null) {
            validator.setSchema(schema);
        } else {
            validator.setSchemaSources(schemaSources);
        }
        return validator.validateInstance(actual);
    }

    public ValidationAssert isValid() {
        ValidationResult validationResult = validate();
        if (!validationResult.isValid()) {
            throwAssertionError(shouldBeValid(actual.getSystemId(), validationResult.getProblems()));
        }
        return this;
    }

    public void isInvalid() {
        ValidationResult validateResult = validate();
        if (validateResult.isValid()) {
            throwAssertionError(shouldBeInvalid(actual.getSystemId()));
        }
    }
}
