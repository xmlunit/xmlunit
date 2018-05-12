package org.xmlunit.assertj;

import org.assertj.core.api.FactoryBasedNavigableIterableAssert;
import org.w3c.dom.Node;
import org.xmlunit.xpath.XPathEngine;

public class MultipleNodeAssert extends FactoryBasedNavigableIterableAssert<MultipleNodeAssert, Iterable<Node>, Node, SingleNodeAssert> {

    private final XPathEngine xPathEngine;
    private final Node root;

    public MultipleNodeAssert(Iterable<Node> nodes, XPathEngine xPathEngine, Node root) {
        super(nodes, MultipleNodeAssert.class, new NodeAssertFactory(xPathEngine));
        this.xPathEngine = xPathEngine;
        this.root = root;
    }

    public MultipleNodeAssert exist() {
        return isNotEmpty();
    }

    public void notExist() {
        isEmpty();
    }
}
