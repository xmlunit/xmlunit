package org.xmlunit.assertj;

import org.assertj.core.api.AbstractAssert;
import org.w3c.dom.Node;
import org.xmlunit.xpath.XPathEngine;

public class NodeAssert extends AbstractAssert<NodeAssert, Node> {

    private final XPathEngine xPathEngine;

    NodeAssert(Node node, XPathEngine xPathEngine) {
        super(node, NodeAssert.class);
        this.xPathEngine = xPathEngine;
    }

    public IterableNodeAssert xPath(String xPath) {
        isNotNull();

        final Iterable<Node> nodes = xPathEngine.selectNodes(xPath, actual);
        return new IterableNodeAssert(nodes, xPathEngine);
    }
}
