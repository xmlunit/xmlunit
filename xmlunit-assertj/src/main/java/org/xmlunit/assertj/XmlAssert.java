package org.xmlunit.assertj;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.w3c.dom.Node;
import org.xmlunit.builder.Input;
import org.xmlunit.util.Convert;
import org.xmlunit.xpath.JAXPXPathEngine;
import org.xmlunit.xpath.XPathEngine;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
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
 */
public class XmlAssert extends AbstractAssert<XmlAssert, Object> {

    private DocumentBuilderFactory dbf;
    private Map<String, String> prefix2Uri;

    private XmlAssert(Object o) {
        super(o, XmlAssert.class);
    }

    /**
     * Factory method for {@link XmlAssert}
     * @param o object with type supported by {@link Input#from(Object)}
     */
    public static XmlAssert assertThat(Object o) {
        return new XmlAssert(o);
    }

    /**
     * Create {@link MultipleNodeAssert} from nodes selecting by given <b>xPath</b>.
     *
     * @throws AssertionError if the actual value is {@code null}.
     * @throws AssertionError if the actual value provide invalid XML.
     */
    public MultipleNodeAssert nodesByXPath(String xPath) {
        isNotNull();

        Assertions.assertThat(xPath).isNotBlank();

        try {
            XPathEngine xPathEngine = createXPathEngine();

            Source s = Input.from(actual).build();
            Node root = dbf != null ? Convert.toNode(s, dbf) : Convert.toNode(s);
            Iterable<Node> nodes = xPathEngine.selectNodes(xPath, root);

            return new MultipleNodeAssert(nodes);

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
     *
     * @throws AssertionError if the actual value is {@code null}.
     */
    public XmlAssert withNamespaceContext(Map<String, String> prefix2Uri) {
        isNotNull();
        this.prefix2Uri = prefix2Uri;
        return this;
    }

    private XPathEngine createXPathEngine() {

        final JAXPXPathEngine engine = new JAXPXPathEngine();
        if (prefix2Uri != null) {
            engine.setNamespaceContext(prefix2Uri);
        }

        return engine;
    }
}
