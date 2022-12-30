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

import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;
import javax.xml.xpath.XPathFactory;

import static org.xmlunit.assertj3.error.ShouldNotHaveThrown.shouldNotHaveThrown;


/**
 * Entry point for fluent interface for writing assertions based on AssertJ library.
 *
 * <p>All types which are supported by {@link Input#from(Object)}
 * can be used as input for {@link XmlAssert#assertThat(Object)}</p>
 *
 * <p><b>Simple Example</b></p>
 *
 * <pre>
 *    import static org.xmlunit.assertj.XmlAssert.assertThat;
 *
 *    final String xml = &quot;&lt;a&gt;&lt;b attr=\&quot;abc\&quot;&gt;&lt;/b&gt;&lt;/a&gt;&quot;;
 *
 *    assertThat(xml).nodesByXPath("//a/b/@attr").exist();
 *    assertThat(xml).hasXPath("//a/b/@attr");
 *    assertThat(xml).doesNotHaveXPath("//a/b/c");
 * </pre>
 *
 * <p><b>Example with namespace mapping</b></p>
 *
 * <pre>
 *    String xml = &quot;&lt;?xml version=\&quot;1.0\&quot; encoding=\&quot;UTF-8\&quot;?&gt;&quot; +
 *          &quot;&lt;feed xmlns=\&quot;http://www.w3.org/2005/Atom\&quot;&gt;&quot; +
 *          &quot;   &lt;title&gt;title&lt;/title&gt;&quot; +
 *          &quot;   &lt;entry&gt;&quot; +
 *          &quot;       &lt;title&gt;title1&lt;/title&gt;&quot; +
 *          &quot;       &lt;id&gt;id1&lt;/id&gt;&quot; +
 *          &quot;   &lt;/entry&gt;&quot; +
 *          &quot;&lt;/feed&gt;&quot;;
 *
 *    HashMap&lt;String, String&gt; prefix2Uri = new HashMap&lt;String, String&gt;();
 *    prefix2Uri.put(&quot;atom&quot;, &quot;http://www.w3.org/2005/Atom&quot;);
 *    assertThat(xml)
 *          .withNamespaceContext(prefix2Uri)
 *          .hasXPath(&quot;//atom:feed/atom:entry/atom:id&quot;));
 * </pre>
 *
 * <p><b>Testing XPath expression value</b></p>
 *
 * <pre>
 *    String xml = &quot;&lt;a&gt;&lt;b attr=\&quot;abc\&quot;&gt;&lt;/b&gt;&lt;/a&gt;&quot;;
 *
 *    assertThat(xml).valueByXPath("//a/b/@attr").isEqualTo("abc");
 *    assertThat(xml).valueByXPath("count(//a/b)").isEqualTo(1);
 * </pre>
 *
 * <p><b>Example with XML validation</b></p>
 *
 * <pre>
 *    String xml = &quot;&lt;a&gt;&lt;b attr=\&quot;abc\&quot;&gt;&lt;/b&gt;&lt;/a&gt;&quot;;
 *    StreamSource xsd = new StreamSource(new File("schema.xsd"));
 *
 *    assertThat(xml).isValid();
 *    assertThat(xml).isValidAgainst(xsd);
 * </pre>
 *
 * <p><b>Example with XMLs comparision</b></p>
 *
 * <pre>
 *    final String control = &quot;&lt;a&gt;&lt;b attr=\&quot;abc\&quot;&gt;&lt;/b&gt;&lt;/a&gt;&quot;;
 *    final String test = &quot;&lt;a&gt;&lt;b attr=\&quot;xyz\&quot;&gt;&lt;/b&gt;&lt;/a&gt;&quot;;
 *
 *    assertThat(test).and(control).areIdentical();
 *    assertThat(test).and(control).areNotIdentical();
 *    assertThat(test).and(control).areSimilar();
 *    assertThat(test).and(control).areNotSimilar();
 *
 *    assertThat(test).and(control)
 *          .normalizeWhitespace()
 *          .ignoreComments()
 *          .withNodeMatcher(new DefaultNodeMatcher(new MyElementSelector()))
 *          .withDifferenceEvaluator(DifferenceEvaluators.chain(
 *               DifferenceEvaluators.Default, new MyDifferenceEvaluator()));
 *          .areIdentical();
 * </pre>
 *
 * @since XMLUnit 2.8.1
 */
public class XmlAssert extends AbstractAssert<XmlAssert, Object> {

    private XmlAssertConfig config;

    private XmlAssert(Object o) {
        super(o, XmlAssert.class);
        this.config = new XmlAssertConfig(getWritableAssertionInfo());
    }

    /**
     * Factory method for {@link XmlAssert}
     *
     * @param o object with type supported by {@link Input#from(Object)}
     * @return a fresh XmlAssert instance
     */
    public static XmlAssert assertThat(Object o) {
        return new XmlAssert(o);
    }

    /**
     * Sets the {@link DocumentBuilderFactory} to use when creating a
     * {@link org.w3c.dom.Document} from the XML input.
     *
     * @throws AssertionError if the actual value is {@code null}.
     * @param dbf factory to use
     * @return this
     */
    public XmlAssert withDocumentBuilderFactory(DocumentBuilderFactory dbf) {
        isNotNull();
        this.config.dbf = dbf;
        return this;
    }

    /**
     * Sets the {@link XPathFactory} to use for XPath related assertions.
     *
     * @throws AssertionError if the actual value is {@code null}.
     * @param xpf factory to use
     * @return this
     */
    public XmlAssert withXPathFactory(XPathFactory xpf) {
        isNotNull();
        this.config.xpf = xpf;
        return this;
    }

    /**
     * Utility method used for creating a namespace context mapping to be used in XPath matching.
     *
     * @param prefix2Uri prefix2Uri maps from prefix to namespace URI. It is used to resolve
     *                   XML namespace prefixes in the XPath expression
     * @throws AssertionError if the actual value is {@code null}.
     * @return this
     */
    public XmlAssert withNamespaceContext(Map<String, String> prefix2Uri) {
        isNotNull();
        this.config.prefix2Uri = prefix2Uri;
        return this;
    }

    /**
     * Create {@link MultipleNodeAssert} from nodes selecting by given <b>xPath</b>.
     *
     * @throws AssertionError if the xPath is blank.
     * @throws AssertionError if the actual value is {@code null}.
     * @throws AssertionError if the actual value provide invalid XML.
     * @param xPath XPath expression
     * @return assert capturing the selected nodes
     */
    public MultipleNodeAssert nodesByXPath(String xPath) {
        isNotNull();
        try {
            return MultipleNodeAssert.create(actual, xPath, config);
        } catch (Exception e) {
            throwAssertionError(shouldNotHaveThrown(e));
        }
        return null; //fix compile issue
    }

    /**
     * Equivalent for <pre>{@link #nodesByXPath(String) nodesByXPath(xPath)}.{@link MultipleNodeAssert#exist() exist()}</pre>
     * @param xPath XPath expression
     * @return assert capturing the selected nodes
     */
    public MultipleNodeAssert hasXPath(String xPath) {
        return nodesByXPath(xPath).exist();
    }

    /**
     * Equivalent for <pre>{@link #nodesByXPath(String) nodesByXPath(xPath)}.{@link MultipleNodeAssert#doNotExist() doNotExist()}</pre>
     * @param xPath XPath expression
     */
    public void doesNotHaveXPath(String xPath) {
        nodesByXPath(xPath).doNotExist();
    }

    /**
     * Create {@link ValueAssert} from value of given <b>xPath</b> expression.
     *
     * @throws AssertionError if the xPath is blank.
     * @throws AssertionError if the actual value is {@code null}.
     * @throws AssertionError if the actual value provide invalid XML.
     * @param xPath XPath expression
     * @return {@code Assert} for the node's value
     */
    public ValueAssert valueByXPath(String xPath) {
        isNotNull();
        try {
            return ValueAssert.create(actual, xPath, config);
        } catch (Exception e) {
            throwAssertionError(shouldNotHaveThrown(e));
        }
        return null; //fix compile issue
    }

    /**
     * Create {@link CompareAssert} for given <b>control</b> XML source and actual XML source.
     *
     * @throws AssertionError if the actual value is {@code null}
     * @throws AssertionError if the control value is {@code null}
     * @param control actual XML to compare object under test against
     * @return assert for comparison
     */
    public CompareAssert and(Object control) {
        isNotNull();
        try {
            return CompareAssert.create(actual, control, config);
        } catch (Exception e) {
            throwAssertionError(shouldNotHaveThrown(e));
        }
        return null; //fix compile issue
    }

    /**
     * Check if actual value is valid against W3C XML Schema
     *
     * @throws AssertionError if the actual value is {@code null}.
     * @throws AssertionError if the actual value is invalid
     * @return this
     */
    public XmlAssert isValid() {
        isNotNull();
        ValidationAssert.create(actual, config).isValid();
        return this;
    }

    /**
     * Check if actual value is not valid against W3C XML Schema
     *
     * @throws AssertionError if the actual value is {@code null}.
     * @throws AssertionError if the actual value is valid
     * @return this
     */
    public XmlAssert isInvalid() {
        isNotNull();
        ValidationAssert.create(actual, config).isInvalid();
        return this;
    }

    /**
     * Check if actual value is valid against given schema
     *
     * @throws AssertionError if the actual value is {@code null}.
     * @throws AssertionError if the actual value is invalid
     * @param schema schema to validate against
     * @return this
     */
    public XmlAssert isValidAgainst(Schema schema) {
        isNotNull();
        ValidationAssert.create(actual, schema, config).isValid();
        return this;
    }

    /**
     * Check if actual value is not valid against given schema
     *
     * @throws AssertionError if the actual value is {@code null}.
     * @throws AssertionError if the actual value is valid
     * @param schema schema to validate against
     * @return this
     */
    public XmlAssert isNotValidAgainst(Schema schema) {
        isNotNull();
        ValidationAssert.create(actual, schema, config).isInvalid();
        return this;
    }

    /**
     * Check if actual value is valid against schema provided by given sources
     *
     * @throws AssertionError if the actual value is {@code null}.
     * @throws AssertionError if the actual value is invalid
     * @param schemaSources schema documents to validate against
     * @return this
     */
    public XmlAssert isValidAgainst(Object... schemaSources) {
        isNotNull();
        ValidationAssert.create(actual, config, schemaSources).isValid();
        return this;
    }

    /**
     * Check if actual value is not valid against schema provided by given sources
     *
     * @throws AssertionError if the actual value is {@code null}.
     * @throws AssertionError if the actual value is valid
     * @param schemaSources schema documents to validate against
     * @return this
     */
    public XmlAssert isNotValidAgainst(Object... schemaSources) {
        isNotNull();
        ValidationAssert.create(actual, config, schemaSources).isInvalid();
        return this;
    }
}
