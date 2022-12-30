/*
******************************************************************
Copyright (c) 2001-2010,2013,2015-2016,2018,2022 Jeff Martin, Tim Bacon
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the following
      disclaimer in the documentation and/or other materials provided
      with the distribution.
    * Neither the name of the XMLUnit nor the names
      of its contributors may be used to endorse or promote products
      derived from this software without specific prior written
      permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

******************************************************************
*/

package org.custommonkey.xmlunit;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.transform.Source;

import org.xmlunit.builder.Input;
import org.xmlunit.diff.ByNameAndTextRecSelector;
import org.xmlunit.diff.Comparison;
import org.xmlunit.diff.ComparisonListener;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.ComparisonType;
import org.xmlunit.diff.DOMDifferenceEngine;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.DifferenceEvaluator;
import org.xmlunit.diff.DifferenceEvaluators;
import org.xmlunit.diff.ElementSelector;
import org.xmlunit.diff.ElementSelectors;
import org.xmlunit.diff.NodeFilters;
import org.xmlunit.diff.NodeMatcher;
import org.xmlunit.input.CommentLessSource;
import org.xmlunit.input.WhitespaceNormalizedSource;
import org.xmlunit.input.WhitespaceStrippedSource;
import org.xmlunit.util.IterableNodeList;
import org.xmlunit.util.Linqy;
import org.xmlunit.util.Predicate;
import org.custommonkey.xmlunit.examples.RecursiveElementNameAndTextQualifier;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Class that has responsibility for comparing Nodes and notifying a
 * DifferenceListener of any differences or dissimilarities that are found.
 * Knows how to compare namespaces and nested child nodes, but currently
 * only compares nodes of type ELEMENT_NODE, CDATA_SECTION_NODE,
 * COMMENT_NODE, DOCUMENT_TYPE_NODE, PROCESSING_INSTRUCTION_NODE and TEXT_NODE.
 * Nodes of other types (eg ENTITY_NODE) will be skipped.
 * @see DifferenceListener#differenceFound(Difference)
 */
public class NewDifferenceEngine
    implements DifferenceConstants, DifferenceEngineContract {

    private static final Integer ZERO = Integer.valueOf(0);
    private static final Map<Class<? extends ElementQualifier>, ElementSelector>
        KNOWN_SELECTORS;
    static {
        Map<Class<? extends ElementQualifier>, ElementSelector> m =
            new HashMap<Class<? extends ElementQualifier>, ElementSelector>();
        m.put(ElementNameAndTextQualifier.class,
              ElementSelectors.byNameAndText);
        m.put(ElementNameQualifier.class, ElementSelectors.byName);
        m.put(RecursiveElementNameAndTextQualifier.class,
              new ByNameAndTextRecSelector());
        KNOWN_SELECTORS = Collections.unmodifiableMap(m);
    }

    private final ComparisonController controller;
    private MatchTracker matchTracker;

    /**
     * Simple constructor that uses no MatchTracker at all.
     * @param controller the instance used to determine whether a Difference
     * detected by this class should halt further comparison or not
     * @see ComparisonController#haltComparison(Difference)
     */
    public NewDifferenceEngine(ComparisonController controller) {
        this(controller, null);
    }

    /**
     * Simple constructor
     * @param controller the instance used to determine whether a Difference
     * detected by this class should halt further comparison or not
     * @param matchTracker the instance that is notified on each
     * successful match.  May be null.
     * @see ComparisonController#haltComparison(Difference)
     * @see MatchTracker#matchFound(Difference)
     */
    public NewDifferenceEngine(ComparisonController controller,
                               MatchTracker matchTracker) {
        this.controller = controller;
        this.matchTracker = matchTracker;
    }

    /**
     * @param matchTracker the instance that is notified on each
     * successful match.  May be null.
     */
    @Override
    public void setMatchTracker(MatchTracker matchTracker) {
        this.matchTracker = matchTracker;
    }

    /**
     * Entry point for Node comparison testing.
     * @param control Control XML to compare
     * @param test Test XML to compare
     * @param listener Notified of any {@link Difference differences} detected
     * during node comparison testing
     * @param elementQualifier Used to determine which elements qualify for
     * comparison e.g. when a node has repeated child elements that may occur
     * in any sequence and that sequence is not considered important.
     */
    @Override
    public void compare(Node control, Node test, DifferenceListener listener,
                        ElementQualifier elementQualifier) {
        DOMDifferenceEngine engine = new DOMDifferenceEngine();
        engine.setNodeFilter(NodeFilters.AcceptAll);

        final IsBetweenDocumentNodeAndRootElement checkPrelude =
            new IsBetweenDocumentNodeAndRootElement();
        engine.addComparisonListener(checkPrelude);

        if (matchTracker != null) {
            engine
                .addMatchListener(new MatchTracker2ComparisonListener(matchTracker));
        }

        org.xmlunit.diff.ComparisonController mappedController =
            new ComparisonController2ComparisonController(controller);
        engine.setComparisonController(mappedController);
        if (listener != null) {
            final DifferenceEvaluator evaluator =
                DifferenceEvaluators.chain(DifferenceEvaluators.Default,
                                           DifferenceEvaluators.ignorePrologDifferencesExceptDoctype(),
                                           new IgnoreDoctypeNotPresentDifferences(),
                                           new DifferenceListener2DifferenceEvaluator(listener));
            engine.setDifferenceEvaluator(evaluator);
        }

        NodeMatcher m = new DefaultNodeMatcher();
        if (elementQualifier != null) {
            Class<?> c = elementQualifier.getClass();
            if (KNOWN_SELECTORS.containsKey(c)) {
                m = new DefaultNodeMatcher(KNOWN_SELECTORS.get(c));
            } else {
                m = new DefaultNodeMatcher(new ElementQualifier2ElementSelector(elementQualifier));
            }
        }
        if (!XMLUnit.getCompareUnmatched()) {
            engine.setNodeMatcher(m);
        } else {
            engine.setNodeMatcher(new CompareUnmatchedNodeMatcher(m));
        }

        Input.Builder ctrlBuilder = Input.fromNode(control);
        Input.Builder tstBuilder = Input.fromNode(test);

        Source ctrlSource = ctrlBuilder.build();
        Source tstSource = tstBuilder.build();
        if (XMLUnit.getIgnoreComments()) {
            ctrlSource = new CommentLessSource(ctrlSource);
            tstSource = new CommentLessSource(tstSource);
        }
        if (XMLUnit.getNormalizeWhitespace()) {
            ctrlSource = new WhitespaceNormalizedSource(ctrlSource);
            tstSource = new WhitespaceNormalizedSource(tstSource);
        } else if (XMLUnit.getIgnoreWhitespace()) {
            ctrlSource = new WhitespaceStrippedSource(ctrlSource);
            tstSource = new WhitespaceStrippedSource(tstSource);
        }

        engine.compare(ctrlSource, tstSource);
    }

    private static Iterable<Difference> toDifference(org.xmlunit.diff.Difference d) {
        return toDifference(d.getComparison());
    }

    /**
     * Maps a Comparison to Differences.
     * @param comp comparison to map
     * @return Differences
     */
    public static Iterable<Difference> toDifference(Comparison comp) {
        List<Difference> diffs = new LinkedList<Difference>();
        Difference proto = null;
        switch (comp.getType()) {
        case ATTR_VALUE_EXPLICITLY_SPECIFIED:
            proto = ATTR_VALUE_EXPLICITLY_SPECIFIED;
            break;
        case HAS_DOCTYPE_DECLARATION:
            proto = HAS_DOCTYPE_DECLARATION;
            break;
        case DOCTYPE_NAME:
            proto = DOCTYPE_NAME;
            break;
        case DOCTYPE_PUBLIC_ID:
            proto = DOCTYPE_PUBLIC_ID;
            break;
        case DOCTYPE_SYSTEM_ID:
            proto = DOCTYPE_SYSTEM_ID;
            break;
        case SCHEMA_LOCATION:
            proto = SCHEMA_LOCATION;
            break;
        case NO_NAMESPACE_SCHEMA_LOCATION:
            proto = NO_NAMESPACE_SCHEMA_LOCATION;
            break;
        case NODE_TYPE:
            proto = NODE_TYPE;
            break;
        case NAMESPACE_PREFIX:
            proto = NAMESPACE_PREFIX;
            break;
        case NAMESPACE_URI:
            proto = NAMESPACE_URI;
            break;
        case TEXT_VALUE:
            if (comp.getControlDetails().getTarget() instanceof CDATASection) {
                proto = CDATA_VALUE;
            } else if (comp.getControlDetails().getTarget()
                       instanceof Comment) {
                proto = COMMENT_VALUE;
            } else {
                proto = TEXT_VALUE;
            }
            break;
        case PROCESSING_INSTRUCTION_TARGET:
            proto = PROCESSING_INSTRUCTION_TARGET;
            break;
        case PROCESSING_INSTRUCTION_DATA:
            proto = PROCESSING_INSTRUCTION_DATA;
            break;
        case ELEMENT_TAG_NAME:
            proto = ELEMENT_TAG_NAME;
            break;
        case ELEMENT_NUM_ATTRIBUTES:
            proto = ELEMENT_NUM_ATTRIBUTES;
            break;
        case ATTR_VALUE:
            proto = ATTR_VALUE;
            break;
        case CHILD_NODELIST_LENGTH:
            Comparison.Detail cd = comp.getControlDetails();
            Comparison.Detail td = comp.getTestDetails();
            if (ZERO.equals(cd.getValue())
                || ZERO.equals(td.getValue())) {
                diffs.add(new Difference(HAS_CHILD_NODES,
                                         new NodeDetail(String
                                                        .valueOf(!ZERO
                                                                 .equals(cd
                                                                         .getValue())),
                                                        cd.getTarget(),
                                                        cd.getXPath()),
                                         new NodeDetail(String
                                                        .valueOf(!ZERO
                                                                 .equals(td
                                                                         .getValue())),
                                                        td.getTarget(),
                                                        td.getXPath())));
            }
            proto = CHILD_NODELIST_LENGTH;
            break;
        case CHILD_NODELIST_SEQUENCE:
            proto = CHILD_NODELIST_SEQUENCE;
            break;
        case CHILD_LOOKUP:
            proto = CHILD_NODE_NOT_FOUND;
            break;
        case ATTR_NAME_LOOKUP:
            proto = ATTR_NAME_NOT_FOUND;
            break;
        default:
            /* comparison doesn't match one of legacy's built-in differences */
            break;
        }
        if (proto != null) {
            diffs.add(new Difference(proto, toNodeDetail(comp.getControlDetails()),
                                     toNodeDetail(comp.getTestDetails())));
        }
        return diffs;
    }

    /**
     * Maps node details.
     * @param detail XMLUnit 2.x detail
     * @return XMLUnit 1.x detail
     */
    public static NodeDetail toNodeDetail(Comparison.Detail detail) {
        String value = String.valueOf(detail.getValue());
        if (detail.getValue() instanceof Node) {
            value = ((Node) detail.getValue()).getNodeName();
        }
        return new NodeDetail(value, detail.getTarget(),
                              detail.getXPath());
    }

    /**
     * Adapts XMLUnit 1.x MatchTracker to XMLUnit 2.x ComparisonListener.
     */
    public static class MatchTracker2ComparisonListener
        implements ComparisonListener {
        private final MatchTracker mt;

        /**
         * @param m MatchTracker to adapt
         */
        public MatchTracker2ComparisonListener(MatchTracker m) {
            mt = m;
        }

        @Override
        public void comparisonPerformed(Comparison comparison,
                                        ComparisonResult outcome) {
            for (Difference diff : toDifference(comparison)) {
                mt.matchFound(diff);
            }
        }
    }

    /**
     * Adapts XMLUnit 1.x ComparisonController to XMLUnit 2.x ComparisonController.
     */
    public static class ComparisonController2ComparisonController
        implements org.xmlunit.diff.ComparisonController {
        private final ComparisonController cc;
        /**
         * @param c ComparisonController to adapt.
         */
        public ComparisonController2ComparisonController(ComparisonController c) {
            cc = c;
        }

        @Override
        public boolean stopDiffing(org.xmlunit.diff.Difference difference) {
            for (Difference diff : toDifference(difference)) {
                if (cc.haltComparison(diff)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Adapts XMLUnit 1.x ComparisonQualifider to XMLUnit 2.x ElementSelector.
     */
    public static class ElementQualifier2ElementSelector
        implements ElementSelector {
        private final ElementQualifier eq;

        /**
         * @param eq ElementQualifier to adapt
         */
        public ElementQualifier2ElementSelector(ElementQualifier eq) {
            this.eq = eq;
        }

        @Override
        public boolean canBeCompared(Element controlElement,
                                     Element testElement) {
            return eq.qualifyForComparison(controlElement, testElement);
        }

    }

    private static final class IgnoreDoctypeNotPresentDifferences implements DifferenceEvaluator {
        public ComparisonResult evaluate(Comparison comparison,
                                         ComparisonResult outcome) {
            if (outcome == ComparisonResult.EQUAL) {
                return outcome;
            }
            if (comparison.getType() == ComparisonType.CHILD_LOOKUP
                && (isDoctype(comparison.getControlDetails())
                    || isDoctype(comparison.getTestDetails()))) {
                return ComparisonResult.EQUAL;
            }
            if (comparison.getType() == ComparisonType.CHILD_NODELIST_LENGTH
                && isDocumentWithDocTypeDifference(comparison)
                && Math.abs((Integer) comparison.getControlDetails().getValue()
                            - (Integer) comparison.getTestDetails().getValue())
                   == 1) {
                return ComparisonResult.EQUAL;
            }
            if (comparison.getType() == ComparisonType.CHILD_NODELIST_SEQUENCE
                && isDocumentWithDocTypeDifference(comparison)) {
                return ComparisonResult.EQUAL;
            }
            return outcome;
        }

        private boolean isDoctype(Comparison.Detail detail) {
            return detail != null && detail.getTarget() instanceof DocumentType;
        }

        private boolean isDocumentWithDocTypeDifference(Comparison comparison) {
            if (!(comparison.getControlDetails().getTarget() instanceof Document)) {
                return false;
            }
            return hasDoctypeChild(comparison.getControlDetails().getTarget())
                || hasDoctypeChild(comparison.getTestDetails().getTarget());
        }

        private boolean hasDoctypeChild(Node n) {
            return Linqy.any(new IterableNodeList(n.getChildNodes()),
                             new Predicate<Node>() {
                                 public boolean test(Node n) {
                                     return n instanceof DocumentType;
                                 }
                             });
        }
    }

    /**
     * Adapts XMLUnit 1.x DifferenceListener to XMLUnit 2.x DifferenceEvaluator.
     */
    public static class DifferenceListener2DifferenceEvaluator
        implements DifferenceEvaluator {
        private final DifferenceListener dl;

        /**
         * @param dl DifferenceListener to adapt
         */
        public DifferenceListener2DifferenceEvaluator(DifferenceListener dl) {
            this.dl = dl;
        }

        @Override
        public ComparisonResult evaluate(Comparison comparison,
                                         ComparisonResult outcome) {
            if (outcome == ComparisonResult.EQUAL) {
                return outcome;
            }
            ComparisonResult max = outcome;
            for (Difference diff : toDifference(comparison)) {
                ComparisonResult curr = null;
                switch (dl.differenceFound(diff)) {
                case DifferenceListener
                    .RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL:
                    curr = ComparisonResult.EQUAL;
                    break;
                case DifferenceListener
                    .RETURN_IGNORE_DIFFERENCE_NODES_SIMILAR:
                    curr = ComparisonResult.SIMILAR;
                    break;
                case DifferenceListener
                    .RETURN_UPGRADE_DIFFERENCE_NODES_DIFFERENT:
                    curr = ComparisonResult.DIFFERENT;
                    break;
                default:
                    // unknown result, ignore it
                    break;
                }
                if (curr != null && curr.compareTo(max) > 0) {
                    max = curr;
                }
            }
            return max;
        }
    }

    /**
     * Tests whether the DifferenceEngine is currently processing
     * comparisons of "things" between the document node and the
     * document's root element (comments or PIs, mostly) since these
     * must be ignored for backwards compatibility reasons.
     *
     * <p>Relies on the following assumptions:
     * <ul>

     *   <li>the last comparison DOMDifferenceEngine performs on the
     *     document node is an XML_ENCODING comparison.</li>
     *   <li>the first comparison DOMDifferenceEngine performs on matching
     *     root elements is a NODE_TYPE comparison.  The control Node
     *     is an Element Node.</li>
     *   <li>the first comparison DOMDifferenceEngine performs if the
     *     root elements don't match is a CHILD_LOOKUP comparison.
     *     The control Node is an Element Node.</li>
     * </ul>
     * </p>
     */
    private static class IsBetweenDocumentNodeAndRootElement
        implements ComparisonListener {

        private boolean haveSeenXmlEncoding = false;
        private boolean haveSeenElementNodeComparison = false;

        public void comparisonPerformed(Comparison comparison,
                                        ComparisonResult outcome) {
            if (comparison.getType() == ComparisonType.XML_ENCODING) {
                haveSeenXmlEncoding = true;
            } else if (comparison.getControlDetails().getTarget()
                          instanceof Element
                       &&
                       (comparison.getType() == ComparisonType.NODE_TYPE
                        || comparison.getType() == ComparisonType.CHILD_LOOKUP)
                       ) {
                haveSeenElementNodeComparison = true;
            }
        }

        private boolean shouldSkip() {
            return haveSeenXmlEncoding && !haveSeenElementNodeComparison;
        }
    }

    private static class CompareUnmatchedNodeMatcher
        implements NodeMatcher {
        private final NodeMatcher nestedMatcher;
        private CompareUnmatchedNodeMatcher(NodeMatcher nested) {
            nestedMatcher = nested;
        }

        public Iterable<Map.Entry<Node, Node>>
            match(Iterable<Node> controlNodes,
                  Iterable<Node> testNodes) {
            final Map<Node, Node> map = new HashMap<Node, Node>();
            for (Map.Entry<Node, Node> e
                     : nestedMatcher.match(controlNodes, testNodes)) {
                map.put(e.getKey(), e.getValue());
            }

            final LinkedList<Map.Entry<Node, Node>> result =
                new LinkedList<Map.Entry<Node, Node>>();

            for (Node n : controlNodes) {
                if (map.containsKey(n)) {
                    result.add(new Entry(n, map.get(n)));
                } else {
                    Iterable<Node> unmatchedTestElements =
                        Linqy.filter(testNodes, new Predicate<Node>() {
                                @Override
                                public boolean test(Node t) {
                                    return !map.containsValue(t);
                                }
                            });
                    Iterator<Node> it = unmatchedTestElements.iterator();
                    if (it.hasNext()) {
                        Node t = it.next();
                        map.put(n, t);
                        result.add(new Entry(n, t));
                    }
                }
            }
            return result;
        }

        private static class Entry implements Map.Entry<Node, Node> {
            private final Node key;
            private final Node value;
            private Entry(Node k, Node v) {
                key = k;
                value = v;
            }
            public Node getKey() { return key; }
            public Node getValue() { return value; }
            public Node setValue(Node v) {
                throw new UnsupportedOperationException();
            }
        }
    }

}
