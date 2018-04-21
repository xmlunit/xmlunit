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

import org.w3c.dom.Node;
import org.xmlunit.util.Predicate;

/**
 * Common NodeFilter implementations.
 */
public final class NodeFilters {

    /**
     * Suppresses document-type and XML declaration nodes.
     *
     * <p>This is the default used by {@link AbstractDifferenceEngine}
     * and thus {@link DOMDifferenceEngine}.</p>
     */
    public static final Predicate<Node> Default = new Predicate<Node>() {
            @Override
            public boolean test(Node n) {
                return n.getNodeType() != Node.DOCUMENT_TYPE_NODE;
            }
        };

    /**
     * Accepts all nodes.
     *
     * @since XMLUnit 2.6.0
     */
    public static final Predicate<Node> AcceptAll = new Predicate<Node>() {
            @Override
            public boolean test(Node n) {
                return true;
            }
        };

    private NodeFilters() { }
}
