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

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.w3c.dom.Node;
import org.xmlunit.builder.Input;
import org.xmlunit.util.Convert;
import org.xmlunit.xpath.JAXPXPathEngine;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.xpath.XPathFactory;
import java.util.Map;

/**
 * This Hamcrest {@link Matcher} verifies whether the provided XPath expression corresponds to at least
 * one element in the provided object.
 *
 * <p>All types which are supported by {@link Input#from(Object)}  can be used as input for the object
 * against the matcher is evaluated.</p>
 *
 * <p><b>Simple Example</b></p>
 *
 * <pre>
 * final String xml = &quot;&lt;a&gt;&lt;b attr=\&quot;abc\&quot;&gt;&lt;/b&gt;&lt;/a&gt;&quot;;
 *
 * assertThat(xml, hasXPath("//a/b/@attr"));
 * assertThat(xml, not(hasXPath("//a/b/c")));
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
 *    assertThat(xmlRootElement,
 *          hasXPath(&quot;//atom:feed/atom:entry/atom:id&quot;).withNamespaceContext(prefix2Uri));
 * </pre>
 *
 * @since XMLUnit 2.1.0
 */
public class HasXPathMatcher extends BaseMatcher<Object> {

    private String xPath;
    private DocumentBuilderFactory dbf;
    private XPathFactory xpf;
    private Map<String, String> prefix2Uri;

    /**
     * Creates a {@link HasXPathMatcher} instance with the associated XPath expression.
     *
     * @param xPath xPath expression
     */
    public HasXPathMatcher(String xPath) {
        this.xPath = xPath;
    }

    /**
     * Sets the {@link DocumentBuilderFactory} to use when creating a
     * {@link org.w3c.dom.Document} from the XML input.
     *
     * @param f the DocumentBuilderFactory to use
     * @return this
     * @since XMLUnit 2.6.0
     */
    public HasXPathMatcher withDocumentBuilderFactory(DocumentBuilderFactory f) {
        dbf = f;
        return this;
    }

    /**
     * Sets the {@link XPathFactory} to use.
     *
     * @param f the XPathFactory to use
     * @return this
     * @since XMLUnit 2.6.1
     */
    public HasXPathMatcher withXPathFactory(XPathFactory f) {
        xpf = f;
        return this;
    }

    @Override
    public boolean matches(Object object) {
        JAXPXPathEngine engine = xpf == null ? new JAXPXPathEngine() : new JAXPXPathEngine(xpf);
        if (prefix2Uri != null) {
            engine.setNamespaceContext(prefix2Uri);
        }

        Source s = Input.from(object).build();
        Node n = dbf != null ? Convert.toNode(s, dbf) : Convert.toNode(s);
        Iterable<Node> nodes = engine.selectNodes(xPath, n);

        return nodes.iterator().hasNext();
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("XML with XPath ").appendText(xPath);
    }

    @Override
    public void describeMismatch(Object item, Description mismatchDescription) {
        mismatchDescription.appendText("XPath returned no results.");
    }

    /**
     * Creates a matcher that matches when the examined XML input has at least one node
     * corresponding to the specified <code>xPath</code>.
     *
     * <p>For example:</p>
     * <pre>assertThat(xml, hasXPath("/root/cars[0]/audi"))</pre>
     *
     * @param xPath the target xpath
     * @return the xpath matcher
     */
    @Factory
    public static HasXPathMatcher hasXPath(String xPath) {
        return new HasXPathMatcher(xPath);
    }

    /**
     * Utility method used for creating a namespace context mapping to be used in XPath matching.
     *
     * @param prefix2Uri prefix2Uri maps from prefix to namespace URI. It is used to resolve
     *                   XML namespace prefixes in the XPath expression
     * @return this
     */
    public HasXPathMatcher withNamespaceContext(Map<String, String> prefix2Uri) {
        this.prefix2Uri = prefix2Uri;
        return this;
    }
}
