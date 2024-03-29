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

import java.util.Map;
import org.w3c.dom.Node;

/**
 * Strategy that matches control and tests nodes for comparison.
 */
public interface NodeMatcher {

    /**
     * Matches control and test nodes against each other, returns the
     * matching pairs.
     *
     * <p>Nodes passed in as attributes but not returned as member of
     * any pair will cause {@link ComparisonType#CHILD_LOOKUP}
     * differences}.</p>
     *
     * @param controlNodes the control nodes
     * @param testNodes the test nodes
     * @return a Map.Entry containing the pair for each matched pair of nodes
     */
    Iterable<Map.Entry<Node, Node>> match(Iterable<Node> controlNodes,
                                          Iterable<Node> testNodes);
}
