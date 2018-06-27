package org.xmlunit.assertj;

import org.assertj.core.api.*;
import org.w3c.dom.Node;
import org.xmlunit.builder.Input;
import org.xmlunit.util.Convert;
import org.xmlunit.xpath.JAXPXPathEngine;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import java.util.Map;

import static org.xmlunit.assertj.error.ShouldBeConvertible.shouldBeConvertible;

public class ValueAssert extends AbstractCharSequenceAssert<ValueAssert, String> {

    private ValueAssert(String value) {
        super(value, ValueAssert.class);
    }

    static ValueAssert create(Object xmlSource, Map<String, String> prefix2Uri, DocumentBuilderFactory dbf, String xPath) {
        Assertions.assertThat(xPath).isNotBlank();

        final JAXPXPathEngine engine = new JAXPXPathEngine();
        if (prefix2Uri != null) {
            engine.setNamespaceContext(prefix2Uri);
        }

        Source s = Input.from(xmlSource).build();
        Node root = dbf != null ? Convert.toNode(s, dbf) : Convert.toNode(s);
        String value = engine.evaluate(xPath, root);

        return new ValueAssert(value)
                .describedAs("XPath \"%s\" evaluated to value", xPath);
    }

    public AbstractIntegerAssert<?> asInt() {
        isNotNull();
        int value = 0;
        try {
            value = Integer.parseInt(actual);
        } catch (NumberFormatException e) {
            throwAssertionError(shouldBeConvertible(actual, "int"));
        }

        return Assertions.assertThat(value);
    }

    public AbstractDoubleAssert<?> asDouble() {
        isNotNull();
        double value = 0;
        try {
            value = Double.parseDouble(actual);
        } catch (NumberFormatException e) {
            throwAssertionError(shouldBeConvertible(actual, "double"));
        }

        return Assertions.assertThat(value);
    }

    public AbstractBooleanAssert<?> asBoolean() {
        isNotNull();
        boolean value = false;
        switch (actual.toLowerCase()) {
            case "1":
            case "true":
                value = true; break;
            case "0":
            case "false":
                value = false; break;
            default:
                throwAssertionError(shouldBeConvertible(actual, "boolean"));
        }

        return Assertions.assertThat(value);
    }

    public XmlAssert asXml() {
        return XmlAssert.assertThat(actual);
    }

    public ValueAssert isEqualTo(int expected) {
        asInt().isEqualTo(expected);

        return this;
    }

    public ValueAssert isEqualTo(double expected) {
        asDouble().isEqualTo(expected);

        return this;
    }

    public ValueAssert isEqualTo(boolean expected) {
        asBoolean().isEqualTo(expected);

        return this;
    }
}
