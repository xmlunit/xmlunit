/*
******************************************************************
Copyright (c) 2001-2010, Jeff Martin, Tim Bacon
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
    * Neither the name of the xmlunit.sourceforge.net nor the names
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

import javax.xml.transform.Source;

import net.sf.xmlunit.builder.Input;
import net.sf.xmlunit.diff.Comparison;
import net.sf.xmlunit.diff.ComparisonListener;
import net.sf.xmlunit.diff.ComparisonResult;
import net.sf.xmlunit.diff.ComparisonType;
import net.sf.xmlunit.diff.DOMDifferenceEngine;
import net.sf.xmlunit.diff.DifferenceEvaluator;
import net.sf.xmlunit.diff.DifferenceEvaluators;
import net.sf.xmlunit.diff.ElementSelector;
import net.sf.xmlunit.input.CommentLessSource;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Class that has responsibility for comparing Nodes and notifying a
 * DifferenceListener of any differences or dissimilarities that are found.
 * Knows how to compare namespaces and nested child nodes, but currently
 * only compares nodes of type ELEMENT_NODE, CDATA_SECTION_NODE,
 * COMMENT_NODE, DOCUMENT_TYPE_NODE, PROCESSING_INSTRUCTION_NODE and TEXT_NODE.
 * Nodes of other types (eg ENTITY_NODE) will be skipped.
 * <br />Examples and more at <a href="http://xmlunit.sourceforge.net"/>xmlunit.
 * sourceforge.net</a>
 * @see DifferenceListener#differenceFound(Difference)
 */
public class NewDifferenceEngine
    implements DifferenceConstants, DifferenceEngineContract {

    private static final Integer ZERO = Integer.valueOf(0);

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
    public void compare(Node control, Node test, DifferenceListener listener, 
                        ElementQualifier elementQualifier) {
        DOMDifferenceEngine engine = new DOMDifferenceEngine();
        if (matchTracker != null) {
            engine
                .addMatchListener(new MatchTracker2ComparisonListener(matchTracker));
        }

        DifferenceEvaluator controllerAsEvaluator =
            new ComparisonController2DifferenceEvaluator(controller);
        if (listener != null) {
            DifferenceEvaluator l = 
                new DifferenceListener2DifferenceEvaluator(listener);
            engine
                .setDifferenceEvaluator(DifferenceEvaluators.first(l,
                                                                   controllerAsEvaluator));
        } else  {
            engine
                .setDifferenceEvaluator(controllerAsEvaluator);
        }
        if (elementQualifier != null) {
            engine
                .setElementSelector(new ElementQualifier2ElementSelector(elementQualifier));
        }

        Input.Builder ctrlBuilder = Input.fromNode(control);
        Input.Builder tstBuilder = Input.fromNode(test);

        Source ctrlSource = ctrlBuilder.build();
        Source tstSource = tstBuilder.build();
        if (XMLUnit.getIgnoreComments()) {
            ctrlSource = new CommentLessSource(ctrlSource);
            tstSource = new CommentLessSource(tstSource);
        }

        engine.compare(ctrlSource, tstSource);
    }

    public static Difference toDifference(Comparison comp) {
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
                return new Difference(HAS_CHILD_NODES,
                                      new NodeDetail(String
                                                     .valueOf(!ZERO
                                                              .equals(cd
                                                                      .getValue())),
                                                     (Node) cd.getTarget(),
                                                     cd.getXPath()),
                                      new NodeDetail(String
                                                     .valueOf(!ZERO
                                                              .equals(td
                                                                      .getValue())),
                                                     (Node) td.getTarget(),
                                                     td.getXPath()));
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
            return new Difference(proto, toNodeDetail(comp.getControlDetails()),
                                  toNodeDetail(comp.getTestDetails()));
        }
        return null;
    }

    public static NodeDetail toNodeDetail(Comparison.Detail detail) {
        return new NodeDetail(String.valueOf(detail.getValue()),
                              (Node) detail.getTarget(),
                              detail.getXPath());
    }

    public static class MatchTracker2ComparisonListener
        implements ComparisonListener {
        private final MatchTracker mt;

        public MatchTracker2ComparisonListener(MatchTracker m) {
            mt = m;
        }

        public void comparisonPerformed(Comparison comparison,
                                        ComparisonResult outcome) {
            Difference diff = toDifference(comparison);
            if (diff != null) {
                mt.matchFound(diff);
            }
        }
    }

    public static class DifferenceListener2ComparisonListener
        implements ComparisonListener {
        private final DifferenceListener dl;

        public DifferenceListener2ComparisonListener(DifferenceListener dl) {
            this.dl = dl;
        }

        public void comparisonPerformed(Comparison comparison,
                                        ComparisonResult outcome) {
            Difference diff = toDifference(comparison);
            if (diff != null) {
                dl.differenceFound(diff);
            }
        }
    }

    private static final Short TEXT_TYPE = Short.valueOf(Node.TEXT_NODE);
    private static final Short CDATA_TYPE =
        Short.valueOf(Node.CDATA_SECTION_NODE);

    private static boolean swallowComparison(Comparison comparison,
                                             ComparisonResult outcome) {
        if (outcome == ComparisonResult.EQUAL) {
            return true;
        }
        if (XMLUnit.getIgnoreDiffBetweenTextAndCDATA()
            && comparison.getType() == ComparisonType.NODE_TYPE) {
            return (
                    TEXT_TYPE.equals(comparison.getControlDetails().getValue())
                    ||
                    CDATA_TYPE.equals(comparison.getControlDetails().getValue())
                    )
                && (
                    TEXT_TYPE.equals(comparison.getTestDetails().getValue())
                    ||
                    CDATA_TYPE.equals(comparison.getTestDetails().getValue())
                    );
        }
        return false;
    }

    public static class ComparisonController2DifferenceEvaluator
        implements DifferenceEvaluator {
        private final ComparisonController cc;
        public ComparisonController2DifferenceEvaluator(ComparisonController c) {
            cc = c;
        }

        public ComparisonResult evaluate(Comparison comparison,
                                         ComparisonResult outcome) {
            if (!swallowComparison(comparison, outcome)) {
                Difference diff = toDifference(comparison);
                if (diff != null && cc.haltComparison(diff)) {
                    return ComparisonResult.CRITICAL;
                }
                return outcome;
            }
            return ComparisonResult.EQUAL;
        }
    }

    public static class ElementQualifier2ElementSelector
        implements ElementSelector {
        private final ElementQualifier eq;

        public ElementQualifier2ElementSelector(ElementQualifier eq) {
            this.eq = eq;
        }

        public boolean canBeCompared(Element controlElement,
                                     Element testElement) {
            return eq.qualifyForComparison(controlElement, testElement);
        }

    }

    public static class DifferenceListener2DifferenceEvaluator
        implements DifferenceEvaluator {
        private final DifferenceListener dl;

        public DifferenceListener2DifferenceEvaluator(DifferenceListener dl) {
            this.dl = dl;
        }

        public ComparisonResult evaluate(Comparison comparison,
                                         ComparisonResult outcome) {
            if (!swallowComparison(comparison, outcome)) {
                Difference diff = toDifference(comparison);
                if (diff != null) {
                    switch (dl.differenceFound(diff)) {
                    case DifferenceListener
                        .RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL:
                        return ComparisonResult.EQUAL;
                    case DifferenceListener
                        .RETURN_IGNORE_DIFFERENCE_NODES_SIMILAR:
                        return ComparisonResult.SIMILAR;
                    case DifferenceListener
                        .RETURN_UPGRADE_DIFFERENCE_NODES_DIFFERENT:
                        return ComparisonResult.DIFFERENT;
                    }
                }
                return outcome;
            }
            return ComparisonResult.EQUAL;
        }
    }
}
