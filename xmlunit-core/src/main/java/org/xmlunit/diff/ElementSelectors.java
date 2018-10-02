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

import static org.xmlunit.util.Linqy.all;
import static org.xmlunit.util.Linqy.any;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.xml.namespace.QName;
import org.xmlunit.util.IsNullPredicate;
import org.xmlunit.util.Linqy;
import org.xmlunit.util.Mapper;
import org.xmlunit.util.Nodes;
import org.xmlunit.util.Predicate;
import org.xmlunit.xpath.JAXPXPathEngine;
import org.xmlunit.xpath.XPathEngine;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Common ElementSelector implementations.
 */
public final class ElementSelectors {

    /**
     * Always returns true, i.e. each element can be compared to each
     * other element.
     *
     * <p>Generally this means elements will be compared in document
     * order.</p>
     */
    public static final ElementSelector Default = new ElementSelector() {
            @Override
            public boolean canBeCompared(Element controlElement,
                                         Element testElement) {
                return true;
            }
        };

    /**
     * Elements with the same local name (and namespace URI - if any)
     * can be compared.
     */
    public static final ElementSelector byName = new ElementSelector() {
            @Override
            public boolean canBeCompared(Element controlElement,
                                         Element testElement) {
                return controlElement != null
                    && testElement != null
                    && bothNullOrEqual(Nodes.getQName(controlElement),
                                       Nodes.getQName(testElement));
            }
        };

    /**
     * Elements with the same local name (and namespace URI - if any)
     * and nested text (if any) can be compared.
     */
    public static final ElementSelector byNameAndText = new ElementSelector() {
            @Override
            public boolean canBeCompared(Element controlElement,
                                         Element testElement) {
                return byName.canBeCompared(controlElement, testElement)
                    && bothNullOrEqual(Nodes.getMergedNestedText(controlElement),
                                       Nodes.getMergedNestedText(testElement));
            }
        };

    /**
     * Elements with the same local name (and namespace URI - if any)
     * and attribute values for all attributes can be compared.
     */
    public static final ElementSelector byNameAndAllAttributes =
        new ElementSelector() {
            @Override
            public boolean canBeCompared(Element controlElement,
                                         Element testElement) {
                if (!byName.canBeCompared(controlElement, testElement)) {
                    return false;
                }
                Map<QName, String> cAttrs = Nodes.getAttributes(controlElement);
                Map<QName, String> tAttrs = Nodes.getAttributes(testElement);
                if (cAttrs.size() != tAttrs.size()) {
                    return false;
                }
                return mapsEqualForKeys(cAttrs, tAttrs, cAttrs.keySet());
            }
        };
    /**
     * String Constants.
     */
    private static final String SELECTORS_MUST_NOT_BE_NULL = "selectors must not be null";
    private static final String ATTRIBUTES_MUST_NOT_CONTAIN_NULL_VALUES = "attributes must not contain null values";
    private static final String ATTRIBUTES_MUST_NOT_BE_NULL = "attributes must not be null";


    /**
     * Maps Nodes to their NodeInfo equivalent.
     */
    static final Mapper<Node, XPathContext.NodeInfo> TO_NODE_INFO =
        new Mapper<Node, XPathContext.NodeInfo>() {
            @Override
            public XPathContext.NodeInfo apply(Node n) {
                return new XPathContext.DOMNodeInfo(n);
            }
        };

    private ElementSelectors() { }

    /**
     * Elements with the same local name (and namespace URI - if any)
     * and attribute values for the given attribute names can be
     * compared.
     *
     * <p>Attributes are only searched for in the null namespace.</p>
     */
    public static ElementSelector byNameAndAttributes(String... attribs) {
        if (attribs == null) {
            throw new IllegalArgumentException(ATTRIBUTES_MUST_NOT_BE_NULL);
        }
        if (any(Arrays.asList(attribs), new IsNullPredicate())) {
            throw new IllegalArgumentException(ATTRIBUTES_MUST_NOT_CONTAIN_NULL_VALUES);
        }
        QName[] qs = new QName[attribs.length];
        for (int i = 0; i < attribs.length; i++) {
            qs[i] = new QName(attribs[i]);
        }
        return byNameAndAttributes(qs);
    }

    /**
     * Elements with the same local name (and namespace URI - if any)
     * and attribute values for the given attribute names can be
     * compared.
     *
     * <p>Namespace URIs of attributes are those of the attributes on
     * the control element or the null namespace if they don't
     * exist.</p>
     */
    public static ElementSelector
        byNameAndAttributesControlNS(final String... attribs) {

        if (attribs == null) {
            throw new IllegalArgumentException(ATTRIBUTES_MUST_NOT_BE_NULL);
        }
        final Collection<String> qs = Arrays.asList(attribs);
        if (any(qs, new IsNullPredicate())) {
            throw new IllegalArgumentException(ATTRIBUTES_MUST_NOT_CONTAIN_NULL_VALUES);
        }
        final HashSet<String> as = new HashSet<String>(qs);
        return new ElementSelector() {
            @Override
            public boolean canBeCompared(Element controlElement,
                                         Element testElement) {
                if (!byName.canBeCompared(controlElement, testElement)) {
                    return false;
                }
                Map<QName, String> cAttrs = Nodes.getAttributes(controlElement);
                Map<String, QName> qNameByLocalName =
                    new HashMap<String, QName>();
                for (QName q : cAttrs.keySet()) {
                    String local = q.getLocalPart();
                    if (as.contains(local)) {
                        qNameByLocalName.put(local, q);
                    }
                }
                for (String a : as) {
                    QName q = qNameByLocalName.get(a);
                    if (q == null) {
                        qNameByLocalName.put(a, new QName(a));
                    }
                }
                return mapsEqualForKeys(cAttrs,
                                        Nodes.getAttributes(testElement),
                                        qNameByLocalName.values());
            }
        };
    }

    /**
     * Elements with the same local name (and namespace URI - if any)
     * and attribute values for the given attribute names can be
     * compared.
     */
    public static ElementSelector byNameAndAttributes(final QName... attribs) {
        if (attribs == null) {
            throw new IllegalArgumentException(ATTRIBUTES_MUST_NOT_BE_NULL);
        }
        final Collection<QName> qs = Arrays.asList(attribs);
        if (any(qs, new IsNullPredicate())) {
            throw new IllegalArgumentException(ATTRIBUTES_MUST_NOT_CONTAIN_NULL_VALUES);
        }
        return new ElementSelector() {
            @Override
            public boolean canBeCompared(Element controlElement,
                                         Element testElement) {
                if (!byName.canBeCompared(controlElement, testElement)) {
                    return false;
                }
                return mapsEqualForKeys(Nodes.getAttributes(controlElement),
                                        Nodes.getAttributes(testElement),
                                        qs);
            }
        };
    }

    /**
     * Negates another ElementSelector.
     */
    public static ElementSelector not(final ElementSelector es) {
        if (es == null) {
            throw new IllegalArgumentException("es must not be null");
        }
        return new ElementSelector() {
            @Override
            public boolean canBeCompared(Element controlElement,
                                         Element testElement) {
                return !es.canBeCompared(controlElement, testElement);
            }
        };
    }

    /**
     * Accepts two elements if at least one of the given ElementSelectors does.
     *
     * <p>There is an important difference between using {@link
     * ElementSelectors#or} to combine multiple {@link
     * ElementSelector}s and using {@link DefaultNodeMatcher}'s
     * constructor with multiple {@link ElementSelector}s:</p>
     *
     * <p>Consider {@link ElementSelector}s {@code e1} and {@code e2}
     * and two control and test nodes each.  Assume {@code e1} would
     * match the first control node to the second test node and vice
     * versa if used alone, while {@code e2} would match the nodes in
     * order (the first control node to the first test and so on).</p>
     *
     * <p>{@link ElementSelectors#or} creates a combined {@link
     * ElementSelector} that is willing to match the first control
     * node to both of the test nodes - and the same for the second
     * control node.  Since nodes are compared in order when possible
     * the result will be the same as running {@code e2} alone.</p>
     *
     * <p>{@link DefaultNodeMatcher} with two {@link ElementSelector}s
     * will consult the {@link ElementSelector}s separately and only
     * invoke {@code e2} if there are any nodes not matched by {@code
     * e1} at all.  In this case the result will be the same as
     * running {@code e1} alone.</p>
     */
    public static ElementSelector or(final ElementSelector... selectors) {
        if (selectors == null) {
            throw new IllegalArgumentException(SELECTORS_MUST_NOT_BE_NULL);
        }
        final Collection<ElementSelector> s = Arrays.asList(selectors);
        if (any(s, new IsNullPredicate())) {
            throw new IllegalArgumentException("selectors must not contain null values");
        }
        return new ElementSelector() {
            @Override
            public boolean canBeCompared(Element controlElement,
                                         Element testElement) {
                return any(s, new CanBeComparedPredicate(controlElement, testElement));
            }
        };
    }

    /**
     * Accepts two elements if all of the given ElementSelectors do.
     */
    public static ElementSelector and(final ElementSelector... selectors) {
        if (selectors == null) {
            throw new IllegalArgumentException(SELECTORS_MUST_NOT_BE_NULL);
        }
        final Collection<ElementSelector> s = Arrays.asList(selectors);
        if (any(s, new IsNullPredicate())) {
            throw new IllegalArgumentException("selectors must not contain null values");
        }
        return new ElementSelector() {
            @Override
            public boolean canBeCompared(Element controlElement,
                                         Element testElement) {
                return all(s,
                           new CanBeComparedPredicate(controlElement, testElement));
            }
        };
    }

    /**
     * Accepts two elements if exactly on of the given ElementSelectors does.
     */
    public static ElementSelector xor(final ElementSelector es1,
                                      final ElementSelector es2) {
        if (es1 == null || es2 == null) {
            throw new IllegalArgumentException(SELECTORS_MUST_NOT_BE_NULL);
        }
        return new ElementSelector() {
            @Override
            public boolean canBeCompared(Element controlElement,
                                         Element testElement) {
                return es1.canBeCompared(controlElement, testElement)
                    ^ es2.canBeCompared(controlElement, testElement);
            }
        };
    }

    /**
     * Applies the wrapped ElementSelector's logic if and only if the
     * control element matches the given predicate.
     */
    public static ElementSelector conditionalSelector(final Predicate<? super Element> predicate,
                                                      final ElementSelector es) {

        if (predicate == null) {
            throw new IllegalArgumentException("predicate must not be null");
        }
        if (es == null) {
            throw new IllegalArgumentException("es must not be null");
        }
        return new ElementSelector() {
            @Override
            public boolean canBeCompared(Element controlElement,
                                         Element testElement) {
                return predicate.test(controlElement)
                    && es.canBeCompared(controlElement, testElement);
            }
        };
    }

    /**
     * Applies the wrapped ElementSelector's logic if and only if the
     * control element has the given (local) name.
     */
    public static ElementSelector selectorForElementNamed(final String expectedName,
                                                          final ElementSelector es) {
        if (expectedName == null) {
            throw new IllegalArgumentException("expectedName must not be null");
        }

        return conditionalSelector(elementNamePredicate(expectedName), es);
    }

    /**
     * Applies the wrapped ElementSelector's logic if and only if the
     * control element has the given name.
     */
    public static ElementSelector selectorForElementNamed(final QName expectedName,
                                                          final ElementSelector es) {
        if (expectedName == null) {
            throw new IllegalArgumentException("expectedName must not be null");
        }

        return conditionalSelector(elementNamePredicate(expectedName), es);
    }

    /**
     * Selects two elements as matching if the child elements selected
     * via XPath match using the given childSelector.
     *
     * <p>The xpath expression should yield elements.  Two elements
     * match if a DefaultNodeMatcher applied to the selected children
     * finds matching pairs for all children.</p>
     *
     * @param xpath XPath expression applied in the context of the
     * elements to chose from that selects the children to compare.
     * @param childSelector ElementSelector to apply to the selected children.
     */
    public static ElementSelector byXPath(String xpath, ElementSelector childSelector) {
        return byXPath(xpath, null, childSelector);
    }

    /**
     * Selects two elements as matching if the child elements selected
     * via XPath match using the given childSelector.
     *
     * <p>The xpath expression should yield elements.  Two elements
     * match if a DefaultNodeMatcher applied to the selected children
     * finds matching pairs for all children.</p>
     *
     * @param xpath XPath expression applied in the context of the
     * elements to chose from that selects the children to compare.
     * @param prefix2Uri maps from prefix to namespace URI.
     * @param childSelector ElementSelector to apply to the selected children.
     */
    public static ElementSelector byXPath(final String xpath,
                                          Map<String, String> prefix2Uri,
                                          ElementSelector childSelector) {
        final XPathEngine engine = new JAXPXPathEngine();
        if (prefix2Uri != null) {
            engine.setNamespaceContext(prefix2Uri);
        }
        final NodeMatcher nm = new DefaultNodeMatcher(childSelector);
        return new ElementSelector() {
            @Override
            public boolean canBeCompared(Element controlElement,
                                         Element testElement) {
                Iterable<Node> controlChildren = engine.selectNodes(xpath, controlElement);
                int expected = Linqy.count(controlChildren);
                int matched =
                    Linqy.count(nm.match(controlChildren,
                                         engine.selectNodes(xpath, testElement)));
                return expected == matched;
            }
        };
    }

    /**
     * {@code then}-part of conditional {@link ElementSelectors} built
     * via {@link ConditionalSelectorBuilder}.
     */
    public interface ConditionalSelectorBuilderThen {
        /**
         * Specifies the ElementSelector to use when the condition holds true.
         */
        ConditionalSelectorBuilder thenUse(ElementSelector es);
    }

    /**
     * Allows to build complex {@link ElementSelector}s by combining simpler blocks.
     *
     * <p>All {@code when*}s are consulted in order and if one returns
     * {@code true} then the associated {@code ElementSelector} is
     * used.  If all of the, return {@code false}, the default set up
     * with {@code elseUse} if any is used.</p>
     */
    public interface ConditionalSelectorBuilder {
        /**
         * Sets up a conditional ElementSelector.
         */
        ConditionalSelectorBuilderThen when(Predicate<? super Element> predicate);
        /**
         * Sets up a conditional ElementSelector.
         */
        ConditionalSelectorBuilderThen whenElementIsNamed(String expectedName);
        /**
         * Sets up a conditional ElementSelector.
         */
        ConditionalSelectorBuilderThen whenElementIsNamed(QName expectedName);
        /**
         * Assigns a default ElementSelector that is used if all
         * {@code when}s have returned false.
         */
        ConditionalSelectorBuilder elseUse(ElementSelector es);
        /**
         * Builds a conditional ElementSelector.
         */
        ElementSelector build();
    }

    /**
     * Allows to build complex {@link ElementSelector}s by combining simpler blocks.
     *
     * <p>All pairs created by the {@code when*}/{@code thenUse} pairs
     * are evaluated in order until one returns true, finally the
     * {@code default}, if any, is consulted.</p>
     */
    public static ConditionalSelectorBuilder conditionalBuilder() {
        return new DefaultConditionalSelectorBuilder();
    }

    private static boolean bothNullOrEqual(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }

    private static boolean mapsEqualForKeys(Map<QName, String> control,
                                            Map<QName, String> test,
                                            Iterable<QName> keys) {
        for (QName q : keys) {
            if (!bothNullOrEqual(control.get(q), test.get(q))) {
                return false;
            }
        }
        return true;
    }

    static Predicate<Element> elementNamePredicate(final String expectedName) {
        return new Predicate<Element>() {
            @Override
            public boolean test(Element e) {
                if (e == null) {
                    return false;
                }
                String name = e.getLocalName();
                if (name == null) {
                    name = e.getNodeName();
                }
                return expectedName.equals(name);
            }
        };
    }

    static Predicate<Element> elementNamePredicate(final QName expectedName) {
        return new Predicate<Element>() {
            @Override
            public boolean test(Element e) {
                return e != null && expectedName.equals(Nodes.getQName(e));
            }
        };
    }

    private static class CanBeComparedPredicate implements Predicate<ElementSelector> {
        private final Element e1;
        private final Element e2;

        private CanBeComparedPredicate(Element e1, Element e2) {
            this.e1 = e1;
            this.e2 = e2;
        }

        @Override
        public boolean test(ElementSelector es) {
            return es.canBeCompared(e1, e2);
        }
    }

}
