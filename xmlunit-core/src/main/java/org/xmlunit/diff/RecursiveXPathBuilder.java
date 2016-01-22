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
import java.util.Map;
import javax.xml.namespace.QName;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.xmlunit.util.IterableNodeList;
import org.xmlunit.util.Linqy;
import org.xmlunit.util.Mapper;
import org.xmlunit.util.Nodes;

/**
 * Finds the XPathContext of a Node by recursively building up the XPathContext.
 */
public class RecursiveXPathBuilder implements Mapper<Node, XPathContext> {

    private Map<String, String> prefix2uri;

    /**
     * Establish a namespace context that will be used in for the
     * XPath.
     *
     * <p>Without a namespace context (or with an empty context) the
     * XPath expressions will only use local names for elements and
     * attributes.</p>
     *
     * @param prefix2uri maps from prefix to namespace URI.
     */
    public void setNamespaceContext(Map<String, String> prefix2uri) {
        this.prefix2uri = prefix2uri == null
            ? Collections.<String, String> emptyMap()
            : Collections.unmodifiableMap(prefix2uri);
    }

    @Override
    public XPathContext apply(Node n) {
        if (n instanceof Attr) {
            return getXPathForAttribute((Attr) n);
        } else {
            return getXPathForNonAttribute(n);
        }
    }

    private XPathContext getXPathForNonAttribute(Node n) {
        Node parent = n.getParentNode();
        if (parent == null || parent instanceof Document) {
            return new XPathContext(prefix2uri, n);
        }
        XPathContext parentContext = getXPathForNonAttribute(parent);
        IterableNodeList nl = new IterableNodeList(parent.getChildNodes());
        parentContext.setChildren(Linqy.map(nl, ElementSelectors.TO_NODE_INFO));
        ChildNodeXPathContextProvider cn = new ChildNodeXPathContextProvider(parentContext,
                                                                             nl);
        return cn.apply(n);
    }

    private XPathContext getXPathForAttribute(Attr a) {
        XPathContext elementContext = getXPathForNonAttribute(a.getOwnerElement());
        QName q = Nodes.getQName(a);
        elementContext.addAttribute(q);
        elementContext.navigateToAttribute(q);
        return elementContext;
    }
}
