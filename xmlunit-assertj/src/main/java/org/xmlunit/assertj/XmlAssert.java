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

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.xmlunit.builder.Input;
import org.xmlunit.xpath.JAXPXPathEngine;
import org.xmlunit.xpath.XPathEngine;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;
import java.util.Map;

import static org.xmlunit.assertj.error.ShouldNotHaveThrown.shouldNotHaveThrown;


/**
 * Entry point for fluent interface for writing assertions based on AssertJ library.
 *
 * <p>All types which are supported by {@link Input#from(Object)}
 * can be used as input for {@link XmlAssert#assertThat(Object)}</p>
 *
 * <p><b>Simple Example</b></p>
 *
 * <pre>
 * import static org.xmlunit.assertj.XmlAssert.assertThat;
 *
 * final String xml = &quot;&lt;a&gt;&lt;b attr=\&quot;abc\&quot;&gt;&lt;/b&gt;&lt;/a&gt;&quot;;
 *
 * assertThat(xml).nodesByXPath("//a/b/@attr").exist();
 * assertThat(xml).hasXPath("//a/b/@attr");
 * assertThat(xml).doesNotHaveXPath("//a/b/c");
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
 * @since XMLUnit 2.6.1
 */
public class XmlAssert extends AbstractAssert<XmlAssert, Object> {

    private DocumentBuilderFactory dbf;
    private Map<String, String> prefix2Uri;

    private XmlAssert(Object o) {
        super(o, XmlAssert.class);
    }

    /**
     * Factory method for {@link XmlAssert}
     *
     * @param o object with type supported by {@link Input#from(Object)}
     */
    public static XmlAssert assertThat(Object o) {
        return new XmlAssert(o);
    }

    /**
     * Sets the {@link DocumentBuilderFactory} to use when creating a
     * {@link org.w3c.dom.Document} from the XML input.
     *
     * @throws AssertionError if the actual value is {@code null}.
     */
    public XmlAssert withDocumentBuildFactory(DocumentBuilderFactory dbf) {
        isNotNull();
        this.dbf = dbf;
        return this;
    }

    /**
     * Utility method used for creating a namespace context mapping to be used in XPath matching.
     *
     * @param prefix2Uri prefix2Uri maps from prefix to namespace URI. It is used to resolve
     *                   XML namespace prefixes in the XPath expression
     * @throws AssertionError if the actual value is {@code null}.
     */
    public XmlAssert withNamespaceContext(Map<String, String> prefix2Uri) {
        isNotNull();
        this.prefix2Uri = prefix2Uri;
        return this;
    }

    /**
     * Create {@link MultipleNodeAssert} from nodes selecting by given <b>xPath</b>.
     *
     * @throws AssertionError if the actual value is {@code null}.
     * @throws AssertionError if the actual value provide invalid XML.
     */
    public MultipleNodeAssert nodesByXPath(String xPath) {
        isNotNull();

        try {
            return MultipleNodeAssert.create(actual, prefix2Uri, dbf, xPath);

        } catch (Exception e) {

            throwAssertionError(shouldNotHaveThrown(e));
        }

        return null;
    }

    /**
     * Equivalent for <pre>{@link #nodesByXPath(String) nodesByXPath(xPath)}.{@link MultipleNodeAssert#exist() exist()}</pre>
     */
    public MultipleNodeAssert hasXPath(String xPath) {
        return nodesByXPath(xPath).exist();
    }

    /**
     * Equivalent for <pre>{@link #nodesByXPath(String) nodesByXPath(xPath)}.{@link MultipleNodeAssert#doNotExist() doNotExist()}</pre>
     */
    public void doesNotHaveXPath(String xPath) {
        nodesByXPath(xPath).doNotExist();
    }

    public ValidationAssert isValid() {
        isNotNull();
        return ValidationAssert.create(actual).isValid();
    }

    public void isInvalid() {
        isNotNull();
        ValidationAssert.create(actual).isInvalid();
    }

    public ValidationAssert isValidAgainst(Schema schema) {
        isNotNull();
        return ValidationAssert.create(actual, schema).isValid();
    }

    public void isNotValidAgainst(Schema schema) {
        isNotNull();
        ValidationAssert.create(actual, schema).isInvalid();
    }

    public ValidationAssert isValidAgainst(Object... schemaSources) {
        isNotNull();
        return ValidationAssert.create(actual, schemaSources).isValid();
    }

    public void isNotValidAgainst(Object... schemaSources) {
        isNotNull();
        ValidationAssert.create(actual, schemaSources).isInvalid();
    }
}
