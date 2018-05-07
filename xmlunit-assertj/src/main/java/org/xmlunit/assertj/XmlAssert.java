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

public class XmlAssert extends AbstractAssert<XmlAssert, Object> {

    private DocumentBuilderFactory dbf;
    private Map<String, String> prefix2Uri;

    private XmlAssert(Object o) {
        super(o, XmlAssert.class);
    }

    public static XmlAssert assertThat(Object o) {
        return new XmlAssert(o);
    }

    public IterableNodeAssert nodes(String xPath) {
        isNotNull();
        Assertions.assertThat(xPath).isNotBlank();

        XPathEngine xPathEngine = createXPathEngine();

        Source s = Input.from(actual).build();
        Node root = dbf != null ? Convert.toNode(s, dbf) : Convert.toNode(s);
        Iterable<Node> nodes = xPathEngine.selectNodes(xPath, root);

        return new IterableNodeAssert(nodes, xPathEngine, root);
    }

    public XmlAssert withDocumentBuildFactory(DocumentBuilderFactory dbf) {
        isNotNull();
        this.dbf = dbf;
        return this;
    }

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
