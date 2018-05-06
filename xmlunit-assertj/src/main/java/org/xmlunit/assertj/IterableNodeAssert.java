package org.xmlunit.assertj;

import org.assertj.core.api.FactoryBasedNavigableIterableAssert;
import org.w3c.dom.Node;
import org.xmlunit.xpath.XPathEngine;

class IterableNodeAssert extends FactoryBasedNavigableIterableAssert<IterableNodeAssert, Iterable<Node>, Node, NodeAssert> {

    private final XPathEngine xPathEngine;

    public IterableNodeAssert(Iterable<Node> nodes, XPathEngine xPathEngine) {
        super(nodes, IterableNodeAssert.class, new NodeAssertFactory(xPathEngine));
        this.xPathEngine = xPathEngine;
    }
}
