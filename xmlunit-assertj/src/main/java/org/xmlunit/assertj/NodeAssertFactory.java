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
package org.xmlunit.assertj;

import org.assertj.core.api.AssertFactory;
import org.w3c.dom.Node;
import org.xmlunit.xpath.XPathEngine;

/**
 * @since XMLUnit 2.6.1
 */
class NodeAssertFactory implements AssertFactory<Node, SingleNodeAssert> {

    private XPathEngine engine;

    public NodeAssertFactory(XPathEngine engine) {
        this.engine = engine;
    }

    @Override
    public SingleNodeAssert createAssert(Node node) {
        return new SingleNodeAssert(node, engine);
    }
}
