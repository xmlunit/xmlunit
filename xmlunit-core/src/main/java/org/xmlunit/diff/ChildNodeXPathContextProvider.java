/*
  This file is licensed to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package org.xmlunit.diff;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import org.w3c.dom.Node;
import org.xmlunit.util.Mapper;

/**
 * Maps {@link Node} to {@link XPathContext} by assuming all nodes
 * passed in are child nodes of the same parent node who's
 * XPathContext is provided as argument to the constructor.
 */
class ChildNodeXPathContextProvider implements Mapper<Node, XPathContext> {
    private final XPathContext xpathContext;
    private final Map<Node, Integer> childIndex;

    /**
     * Creates an instance of ChildNodeXPathContextProvider.
     *
     * @param parentContext context of the parent of all Nodes ever
     * expected to be passed in as arguments to {@link
     * #apply}.  This XPathContext must be "positioned
     * at" the parent element and already know about all its children.
     * @param children all child nodes of the parent in the same order
     * they are known to the XPathContext.
     */
    ChildNodeXPathContextProvider(XPathContext parentContext, Iterable<Node> children) {
        this.xpathContext = parentContext.clone();
        Map<Node, Integer> index = new HashMap<Node, Integer>();
        int i = 0;
        for (Node n : children) {
            index.put(n, i++);
        }
        childIndex = Collections.unmodifiableMap(index);
    }

    @Override
    public XPathContext apply(Node n) {
        XPathContext ctx = xpathContext.clone();
        Integer idx = childIndex.get(n);
        if (idx == null) {
            throw new NoSuchElementException(n + " is not a known child node");
        }
        ctx.navigateToChild(idx.intValue());
        return ctx;
    }
}
