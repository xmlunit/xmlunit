package org.xmlunit.assertj;

import org.assertj.core.api.AssertFactory;
import org.w3c.dom.Node;

class NodeAssertFactory implements AssertFactory<Node, SingleNodeAssert> {

    @Override
    public SingleNodeAssert createAssert(Node node) {
        return new SingleNodeAssert(node);
    }
}
