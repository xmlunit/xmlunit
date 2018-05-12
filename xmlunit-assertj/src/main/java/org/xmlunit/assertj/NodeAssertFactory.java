package org.xmlunit.assertj;

import org.assertj.core.api.AssertFactory;
import org.w3c.dom.Node;
import org.xmlunit.xpath.XPathEngine;

class NodeAssertFactory implements AssertFactory<Node, SingleNodeAssert> {

    private final XPathEngine xPathEngine;

    NodeAssertFactory(XPathEngine xPathEngine) {
        this.xPathEngine = xPathEngine;
    }

    @Override
    public SingleNodeAssert createAssert(Node node) {
        return new SingleNodeAssert(node, xPathEngine);
    }
}
