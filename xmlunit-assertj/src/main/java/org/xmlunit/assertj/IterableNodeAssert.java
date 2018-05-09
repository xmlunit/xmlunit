package org.xmlunit.assertj;

import org.assertj.core.api.FactoryBasedNavigableIterableAssert;
import org.w3c.dom.Node;
import org.xmlunit.xpath.XPathEngine;

public class IterableNodeAssert extends FactoryBasedNavigableIterableAssert<IterableNodeAssert, Iterable<Node>, Node, NodeAssert> {

    private final XPathEngine xPathEngine;
    private final Node root;

    public IterableNodeAssert(Iterable<Node> nodes, XPathEngine xPathEngine, Node root) {
        super(nodes, IterableNodeAssert.class, new NodeAssertFactory(xPathEngine));
        this.xPathEngine = xPathEngine;
        this.root = root;
    }

    public IterableNodeAssert exist() {
        return isNotEmpty();
    }

    public void notExist() {
        isEmpty();
    }
}
