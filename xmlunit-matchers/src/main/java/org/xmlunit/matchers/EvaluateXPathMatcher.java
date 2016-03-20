package org.xmlunit.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.w3c.dom.Node;
import org.xmlunit.builder.Input;
import org.xmlunit.util.Convert;
import org.xmlunit.xpath.JAXPXPathEngine;

import javax.xml.transform.Source;
import java.util.Map;

/**
 * This Hamcrest {@link Matcher} verifies whether the evaluation of the provided XPath expression
 * corresponds to the value matcher specified for the provided input XML object.
 *
 * <p>All types which are supported by {@link Input#from(Object)} can be used as input for the XML object
 * against the matcher is evaluated.</p>
 *
 * <p><b>Simple Example</b></p>
 * <pre>
 * final String xml = &quot;&lt;a&gt;&lt;b attr=\&quot;abc\&quot;&gt;&lt;/b&gt;&lt;/a&gt;&quot;;
 *
 * assertThat(xml, hasXPath("//a/b/@attr", equalTo("abc")));
 * assertThat(xml, hasXPath("count(//a/b/c)", equalTo("0")));
 * </pre>
 *
 * <p><b>Example with namespace mapping</b></p>
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
 *    assertThat(xml,
 *          hasXPath(&quot;//atom:feed/atom:entry/atom:id/text()&quot;, equalTo(&quot;id1&quot;))
 *          .withNamespaceContext(prefix2Uri));
 * </pre>
 *
 * @since XMLUnit 2.1.0
 */
public class EvaluateXPathMatcher extends BaseMatcher<Object> {
    private final String xPath;
    private final Matcher<String> valueMatcher;
    private Map<String, String> prefix2Uri;

    /**
     * Creates a {@link EvaluateXPathMatcher} instance with the associated XPath expression and
     * the value matcher corresponding to the XPath evaluation.
     *
     * @param xPath        xPath expression
     * @param valueMatcher matcher for the value at the specified xpath
     */
    public EvaluateXPathMatcher(String xPath, Matcher<String> valueMatcher) {
        this.xPath = xPath;
        this.valueMatcher = valueMatcher;
    }

    /**
     * Creates a matcher that matches when the examined XML input has a value at the
     * specified <code>xPath</code> that satisfies the specified <code>valueMatcher</code>.
     *
     * <p>For example:</p>
     * <pre>assertThat(xml, hasXPath(&quot;//fruits/fruit/@name&quot;, equalTo(&quot;apple&quot;))</pre>
     *
     * @param xPath the target xpath
     * @param valueMatcher matcher for the value at the specified xpath
     * @return the xpath matcher
     */
    @Factory
    public static EvaluateXPathMatcher hasXPath(String xPath, Matcher<String> valueMatcher) {
        return new EvaluateXPathMatcher(xPath, valueMatcher);
    }

    @Override
    public boolean matches(Object object) {
        String value = xPathEvaluate(object);
        return valueMatcher.matches(value);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("XML with XPath ").appendText(xPath);
        if (valueMatcher != null) {
            description.appendText(" evaluated to ").appendDescriptionOf(valueMatcher);
        }
    }

    @Override
    public void describeMismatch(Object object, Description mismatchDescription) {
        if (valueMatcher != null) {
            String value = xPathEvaluate(object);
            valueMatcher.describeMismatch(value, mismatchDescription);
        }
    }

    /**
     * Utility method used for creating a namespace context mapping to be used in XPath matching.
     *
     * @param prefix2Uri prefix2Uri maps from prefix to namespace URI. It is used to resolve
     *                   XML namespace prefixes in the XPath expression
     */
    public EvaluateXPathMatcher withNamespaceContext(Map<String, String> prefix2Uri) {
        this.prefix2Uri = prefix2Uri;
        return this;
    }

    /**
     * Evaluates the provided XML input to the configured <code>xPath</code> field XPath expression.
     * @param input an XML input
     * @return the result of the XPath evaluation
     */
    private String xPathEvaluate(Object input) {
        JAXPXPathEngine engine = new JAXPXPathEngine();
        if (prefix2Uri != null) {
            engine.setNamespaceContext(prefix2Uri);
        }

        Source s = Input.from(input).build();
        Node n = Convert.toNode(s);
        return engine.evaluate(xPath, n);
    }
}
