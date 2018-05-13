package org.xmlunit.assertj;

import org.assertj.core.api.FactoryBasedNavigableIterableAssert;
import org.w3c.dom.Node;

public class MultipleNodeAssert extends FactoryBasedNavigableIterableAssert<MultipleNodeAssert, Iterable<Node>, Node, SingleNodeAssert> {

    interface SingleNodeAssertConsumer {
        void accept(SingleNodeAssert t);
    }

    public MultipleNodeAssert(Iterable<Node> nodes) {
        super(nodes, MultipleNodeAssert.class, new NodeAssertFactory());
    }

    public MultipleNodeAssert exist() {
        return isNotEmpty();
    }

    public void notExist() {
        isEmpty();
    }

    public MultipleNodeAssert haveAttribute(final String attributeName) {

        allSatisfy(new SingleNodeAssertConsumer() {
            @Override
            public void accept(SingleNodeAssert singleNodeAssert) {
                singleNodeAssert.hasAttribute(attributeName);
            }
        });

        return this;
    }

    public MultipleNodeAssert haveAttribute(final String attributeName, final String attributeValue) {

        allSatisfy(new SingleNodeAssertConsumer() {
            @Override
            public void accept(SingleNodeAssert singleNodeAssert) {
                singleNodeAssert.hasAttribute(attributeName, attributeValue);
            }
        });

        return this;
    }

    private void allSatisfy(SingleNodeAssertConsumer consumer) {
        int index = 0;
        for (Node node : actual) {
            final SingleNodeAssert singleNodeAssert = toAssert(node, navigationDescription("check node at index " + index));
            consumer.accept(singleNodeAssert);
            index++;
        }
    }
}
