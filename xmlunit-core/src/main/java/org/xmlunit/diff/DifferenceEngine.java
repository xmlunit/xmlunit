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
import javax.xml.transform.Source;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.xmlunit.util.Predicate;

/**
 * XMLUnit's difference engine.
 */
public interface DifferenceEngine {
    /**
     * Registers a listener that is notified of each comparison.
     */
    void addComparisonListener(ComparisonListener l);

    /**
     * Registers a listener that is notified of each comparison with
     * outcome {@link ComparisonResult#EQUAL}.
     */
    void addMatchListener(ComparisonListener l);

    /**
     * Registers a listener that is notified of each comparison with
     * outcome other than {@link ComparisonResult#EQUAL}.
     */
    void addDifferenceListener(ComparisonListener l);

    /**
     * Sets the strategy for selecting nodes to compare.
     */
    void setNodeMatcher(NodeMatcher n);

    /**
     * Sets the optional strategy that decides which attributes to
     * consider and which to ignore during comparison.
     *
     * <p>Only attributes for which the predicate returns true are
     * part of the comparison.  By default all attributes are
     * considered.</p>
     *
     * <p>The "special" namespace, namespace-location and
     * schema-instance-type attributes can not be ignored this way.
     * If you want to suppress comparison of them you'll need to
     * implement {@link DifferenceEvaluator}.</p>
     */
    void setAttributeFilter(Predicate<Attr> attributeFilter);

    /**
     * Sets the optional strategy that decides which nodes to
     * consider and which to ignore during comparison.
     *
     * <p>Only nodes for which the predicate returns true are part of
     * the comparison.  By default nodes that are not document types
     * are considered.</p>
     */
    void setNodeFilter(Predicate<Node> nodeFilter);

    /**
     * Evaluates the severity of a difference.
     */
    void setDifferenceEvaluator(DifferenceEvaluator e);

    /**
     * Determines whether the comparison should stop after given
     * difference has been found.
     */
    void setComparisonController(ComparisonController c);

    /**
     * Establish a namespace context that will be used in {@link
     * Comparison.Detail#getXPath Comparison.Detail#getXPath}.
     *
     * <p>Without a namespace context (or with an empty context) the
     * XPath expressions will only use local names for elements and
     * attributes.</p>
     *
     * @param prefix2Uri maps from prefix to namespace URI.
     */
    void setNamespaceContext(Map<String, String> prefix2Uri);

    /**
     * Compares two pieces of XML and invokes the registered listeners.
     */
    void compare(Source control, Source test);
}
