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

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.xmlunit.util.Linqy;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Default implemetation of {@link NodeMatcher} that matches control
 * and tests nodes for comparison with the help of {@link
 * NodeTypeMatcher} and {@link ElementSelector}s.
 *
 * <p>There is an important difference between using {@link
 * ElementSelectors#or} to combine multiple {@link ElementSelector}s
 * and using {@link DefaultNodeMatcher}'s constructor with multiple
 * {@link ElementSelector}s:</p>
 *
 * <p>Consider {@link ElementSelector}s {@code e1} and {@code e2} and
 * two control and test nodes each.  Assume {@code e1} would match the
 * first control node to the second test node and vice versa if used
 * alone, while {@code e2} would match the nodes in order (the first
 * control node to the first test and so on).</p>
 *
 * <p>{@link ElementSelectors#or} creates a combined {@link
 * ElementSelector} that is willing to match the first control node to
 * both of the test nodes - and the same for the second control node.
 * Since nodes are compared in order when possible the result will be
 * the same as running {@code e2} alone.</p>
 *
 * <p>{@link DefaultNodeMatcher} with two {@link ElementSelector}s
 * will consult the {@link ElementSelector}s separately and only
 * invoke {@code e2} if there are any nodes not matched by {@code e1}
 * at all.  In this case the result will be the same as running {@code
 * e1} alone.</p>
 */
public class DefaultNodeMatcher implements NodeMatcher {
    private static final short TEXT = Node.TEXT_NODE;
    private static final short CDATA = Node.CDATA_SECTION_NODE;
    private final ElementSelector[] elementSelectors;
    private final NodeTypeMatcher nodeTypeMatcher;

    /**
     * Creates a matcher using {@link ElementSelectors#Default} and
     * {@link DefaultNodeTypeMatcher}.
     */
    public DefaultNodeMatcher() {
        this(ElementSelectors.Default);
    }

    /**
     * Creates a matcher using the given {@link ElementSelector}s and
     * {@link DefaultNodeTypeMatcher}.
     */
    public DefaultNodeMatcher(ElementSelector... es) {
        this(new DefaultNodeTypeMatcher(), es);
    }

    /**
     * Creates a matcher using the given {@link ElementSelector}s and
     * {@link NodeTypeMatcher}.
     *
     * <p>The {@link ElementSelector}s are consulted in order so that
     * the second {@link ElementSelector} only gets to match the nodes
     * that the first one couldn't match to any test nodes ate all and
     * so on.</p>
     */
    public DefaultNodeMatcher(NodeTypeMatcher ntm, ElementSelector... es) {
        nodeTypeMatcher = ntm;
        elementSelectors = es;
    }

    @Override
    public Iterable<Map.Entry<Node, Node>> match(Iterable<Node> controlNodes,
                                                 Iterable<Node> testNodes) {
        Map<Node, Node> matches = new LinkedHashMap<Node, Node>();
        List<Node> controlList = Linqy.asList(controlNodes);
        List<Node> testList = Linqy.asList(testNodes);
        final int testSize = testList.size();
        Set<Integer> unmatchedTestIndexes = new HashSet<Integer>();
        for (int i = 0; i < testSize; i++) {
            unmatchedTestIndexes.add(Integer.valueOf(i));
        }
        final int controlSize = controlList.size();
        Match lastMatch = new Match(null, -1);
        for (int i = 0; i < controlSize; i++) {
            Node control = controlList.get(i);
            Match testMatch = findMatchingNode(control, testList,
                                               lastMatch.index,
                                               unmatchedTestIndexes);
            if (testMatch != null) {
                unmatchedTestIndexes.remove(testMatch.index);
                matches.put(control, testMatch.node);
            }
        }
        return matches.entrySet();
    }

    private Match findMatchingNode(final Node searchFor,
                                   final List<Node> searchIn,
                                   final int indexOfLastMatch,
                                   final Set<Integer> availableIndexes) {
        final int searchSize = searchIn.size();
        Match m = searchIn(searchFor, searchIn,
                           availableIndexes,
                           indexOfLastMatch + 1, searchSize);
        return m != null ? m : searchIn(searchFor, searchIn,
                                        availableIndexes,
                                        0, indexOfLastMatch);
    }

    private Match searchIn(final Node searchFor,
                           final List<Node> searchIn,
                           final Set<Integer> availableIndexes,
                           final int fromInclusive, final int toExclusive) {
        for (ElementSelector e : elementSelectors) {
            Match m = searchIn(searchFor, searchIn, availableIndexes, fromInclusive, toExclusive, e);
            if (m != null) {
                return m;
            }
        }
        return null;
    }


    private Match searchIn(final Node searchFor,
                           final List<Node> searchIn,
                           final Set<Integer> availableIndexes,
                           final int fromInclusive, final int toExclusive,
                           final ElementSelector e) {
        for (int i = fromInclusive; i < toExclusive; i++) {
            if (!availableIndexes.contains(Integer.valueOf(i))) {
                continue;
            }
            if (nodesMatch(searchFor, searchIn.get(i), e)) {
                return new Match(searchIn.get(i), i);
            }
        }
        return null;
    }

    private boolean nodesMatch(final Node n1, final Node n2,
                               final ElementSelector elementSelector) {
        if (n1 instanceof Element && n2 instanceof Element) {
            return elementSelector.canBeCompared((Element) n1, (Element) n2);
        }
        return nodeTypeMatcher.canBeCompared(n1.getNodeType(),
                                             n2.getNodeType());
    }

    private static class Match {
        private final Node node;
        private final int index;
        private Match(Node match, int index) {
            this.node = match;
            this.index = index;
        }
    }

    /**
     * Determines whether two Nodes are eligible for comparison based
     * on their node type.
     */
    public interface NodeTypeMatcher {
        /**
         * Determines whether two Nodes are eligible for comparison
         * based on their node type.
         */
        boolean canBeCompared(short controlType, short testType);
    }

    /**
     * {@link NodeTypeMatcher} that marks pairs of nodes of the same
     * node type as well as pairs of CDATA sections and text nodes as
     * eligible.
     */
    public static class DefaultNodeTypeMatcher implements NodeTypeMatcher {
        @Override
        public boolean canBeCompared(short controlType, short testType) {
            return controlType == testType
                || (controlType == CDATA && testType == TEXT)
                || (controlType == TEXT && testType == CDATA);
        }
    }
}
