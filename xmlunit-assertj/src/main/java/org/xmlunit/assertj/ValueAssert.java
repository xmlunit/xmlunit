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

import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractCharSequenceAssert;
import org.assertj.core.api.AbstractDoubleAssert;
import org.assertj.core.api.AbstractIntegerAssert;
import org.assertj.core.util.CheckReturnValue;
import org.w3c.dom.Node;
import org.xmlunit.builder.Input;
import org.xmlunit.util.Convert;
import org.xmlunit.xpath.JAXPXPathEngine;

import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.xpath.XPathFactory;

import static org.xmlunit.assertj.error.ShouldBeConvertible.shouldBeConvertible;

/**
 * Assertion methods for {@link String} result of XPath evaluation.
 *
 * <p><b>Simple Example</b></p>
 *
 * <pre>
 * import static org.xmlunit.assertj.XmlAssert.assertThat;
 *
 * final String xml = &quot;&lt;a&gt;&lt;b attr=\&quot;abc\&quot;&gt;&lt;/b&gt;&lt;/a&gt;&quot;;
 *
 * assertThat(xml).valueByXPath("count(//a/b)").isEqualTo(3);
 * </pre>
 *
 * @since XMLUnit 2.6.1
 */
public class ValueAssert extends AbstractCharSequenceAssert<ValueAssert, String> {

    private ValueAssert(String value) {
        super(value, ValueAssert.class);
    }

    static ValueAssert create(Object xmlSource, Map<String, String> prefix2Uri, DocumentBuilderFactory dbf,
                              XPathFactory xpf, String xPath) {
        AssertionsAdapter.assertThat(xPath).isNotBlank();

        final JAXPXPathEngine engine = xpf == null ? new JAXPXPathEngine() : new JAXPXPathEngine(xpf);
        if (prefix2Uri != null) {
            engine.setNamespaceContext(prefix2Uri);
        }

        Source s = Input.from(xmlSource).build();
        Node root = dbf != null ? Convert.toNode(s, dbf) : Convert.toNode(s);
        String value = engine.evaluate(xPath, root);

        return new ValueAssert(value)
                .describedAs("XPath \"%s\" evaluated to value", xPath);
    }

    /**
     * Returns an {@code Assert} object that allows performing assertions on integer value of the {@link String} under test.
     *
     * @throws AssertionError if the actual value is {@code null}.
     * @throws AssertionError if the actual value does not contain a parsable integer
     */
    public AbstractIntegerAssert<?> asInt() {
        isNotNull();
        int value = 0;
        try {
            value = Integer.parseInt(actual);
        } catch (NumberFormatException e) {
            throwAssertionError(shouldBeConvertible(actual, "int"));
        }

        return AssertionsAdapter.assertThat(value);
    }

    /**
     * Returns an {@code Assert} object that allows performing assertions on integer value of the {@link String} under test.
     *
     * @throws AssertionError if the actual value is {@code null}.
     * @throws AssertionError if the actual value does not contain a parsable double
     */
    public AbstractDoubleAssert<?> asDouble() {
        isNotNull();
        double value = 0;
        try {
            value = Double.parseDouble(actual);
        } catch (NumberFormatException e) {
            throwAssertionError(shouldBeConvertible(actual, "double"));
        }

        return AssertionsAdapter.assertThat(value);
    }

    /**
     * Returns an {@code Assert} object that allows performing assertions on boolean value of the {@link String} under test.
     * <p>
     * If actual value after lowercasing is one of the following "true", "false", then it can be parsed to boolean.
     *
     * @throws AssertionError if the actual value is {@code null}.
     * @throws AssertionError if the actual value does not contain a parsable boolean
     */
    public AbstractBooleanAssert<?> asBoolean() {
        isNotNull();
        boolean value = false;
        switch (actual.toLowerCase()) {
            case "true":
                value = true;
                break;
            case "false":
                value = false;
                break;
            default:
                throwAssertionError(shouldBeConvertible(actual, "boolean"));
        }

        return AssertionsAdapter.assertThat(value);
    }

    /**
     * Returns an {@code XmlAssert} object that allows performing assertions on XML value of the {@link String} under test.
     *
     * @throws AssertionError if the actual value is {@code null}.
     */
    public XmlAssert asXml() {
        isNotNull();
        return asXml(null);
    }

    /**
     * Returns an {@code XmlAssert} object that allows performing assertions on XML value of the {@link String} under test
     * wrapping around tag with name given in <b>wrapNodeName</b>.
     * If wrapNodeName is null or empty then wrapping is not applied.
     * <p>
     * Pseudocode:
     * <pre>
     *
     *  // given
     *  wrapNodeName = "ul";
     *  actual = "%lt;li&gt;a&lt;/li&gt;&lt;li&gt;&lt;/li&gt;";
     *
     *  // then
     *  xml = "&lt;ul&gt;&lt;li&gt;a&lt;/li&gt;&lt;li&gt;&lt;/li&gt;&lt;/ul&gt;";
     *  return XmlAssert.assertThat(xml);
     * </pre>
     *
     * @throws AssertionError if the actual value is {@code null}.
     * @since XMLUnit 2.6.4
     */
    public XmlAssert asXml(String wrapNodeName) {
        isNotNull();
        final String xml;
        if (wrapNodeName == null || wrapNodeName.isEmpty()) {
            xml = actual;
        } else {
            xml = String.format("<%s>%s</%s>", wrapNodeName, actual, wrapNodeName);
        }

        return XmlAssert.assertThat(xml);
    }

    /**
     * Try convert the {@link String} under test to int using {@link #asInt()} and compare with given value.
     */
    public ValueAssert isEqualTo(int expected) {
        asInt().isEqualTo(expected);

        return this;
    }

    /**
     * Try convert the {@link String} under test to double using {@link #asDouble()} and compare with given value.
     */
    public ValueAssert isEqualTo(double expected) {
        asDouble().isEqualTo(expected);

        return this;
    }

    /**
     * Try convert the {@link String} under test to boolean using {@link #asBoolean()} and compare with given value.
     */
    public ValueAssert isEqualTo(boolean expected) {
        asBoolean().isEqualTo(expected);

        return this;
    }

    /** {@inheritDoc} */
    @Override
    @CheckReturnValue
    // deal with binary incompatible change in AssertJ 3.15.0, see https://github.com/xmlunit/xmlunit/issues/181
    public ValueAssert describedAs(final String description, final Object... args) {
        getWritableAssertionInfo().description(description, args);
        return myself;
    }
}
