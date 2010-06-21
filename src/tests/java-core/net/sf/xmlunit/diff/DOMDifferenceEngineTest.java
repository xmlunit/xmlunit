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
package net.sf.xmlunit.diff;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import static org.junit.Assert.*;

public class DOMDifferenceEngineTest {

    private static class ResultGrabber implements DifferenceEvaluator {
        private ComparisonResult outcome = ComparisonResult.CRITICAL;
        public ComparisonResult evaluate(Comparison comparison,
                                         ComparisonResult outcome) {
            this.outcome = outcome;
            return outcome;
        }
    }

    @Test public void compareTwoNulls() {
        ResultGrabber g = new ResultGrabber();
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        d.setDifferenceEvaluator(g);
        assertEquals(ComparisonResult.EQUAL,
                     d.compare(new Comparison(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                              null, null, null,
                                              null, null, null)));
        assertEquals(ComparisonResult.EQUAL, g.outcome);
    }

    @Test public void compareControlNullTestNonNull() {
        ResultGrabber g = new ResultGrabber();
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        d.setDifferenceEvaluator(g);
        assertEquals(ComparisonResult.DIFFERENT,
                     d.compare(new Comparison(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                              null, null, null,
                                              null, null, "")));
        assertEquals(ComparisonResult.DIFFERENT, g.outcome);
    }

    @Test public void compareControlNonNullTestNull() {
        ResultGrabber g = new ResultGrabber();
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        d.setDifferenceEvaluator(g);
        assertEquals(ComparisonResult.DIFFERENT,
                     d.compare(new Comparison(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                              null, null, "",
                                              null, null, null)));
        assertEquals(ComparisonResult.DIFFERENT, g.outcome);
    }

    @Test public void compareTwoDifferentNonNulls() {
        ResultGrabber g = new ResultGrabber();
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        d.setDifferenceEvaluator(g);
        assertEquals(ComparisonResult.DIFFERENT,
                     d.compare(new Comparison(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                              null, null, new Short("1"),
                                              null, null, new Short("2"))));
        assertEquals(ComparisonResult.DIFFERENT, g.outcome);
    }

    @Test public void compareTwoEqualNonNulls() {
        ResultGrabber g = new ResultGrabber();
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        d.setDifferenceEvaluator(g);
        assertEquals(ComparisonResult.EQUAL,
                     d.compare(new Comparison(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                              null, null, new Short("2"),
                                              null, null, new Short("2"))));
        assertEquals(ComparisonResult.EQUAL, g.outcome);
    }

    @Test public void compareNotifiesListener() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        ComparisonListenerSupportTest.Listener l =
            new ComparisonListenerSupportTest.Listener(ComparisonResult.EQUAL);
        d.addComparisonListener(l);
        assertEquals(ComparisonResult.EQUAL,
                     d.compare(new Comparison(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                              null, null, new Short("2"),
                                              null, null, new Short("2"))));
        assertEquals(1, l.getInvocations());
    }

    @Test public void compareUsesResultOfEvaluator() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        ComparisonListenerSupportTest.Listener l =
            new ComparisonListenerSupportTest.Listener(ComparisonResult.SIMILAR);
        d.addComparisonListener(l);
        d.setDifferenceEvaluator(new DifferenceEvaluator() {
                public ComparisonResult evaluate(Comparison comparison,
                                                 ComparisonResult outcome) {
                    return ComparisonResult.SIMILAR;
                }
            });
        assertEquals(ComparisonResult.SIMILAR,
                     d.compare(new Comparison(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                              null, null, new Short("2"),
                                              null, null, new Short("2"))));
        assertEquals(1, l.getInvocations());
    }

    private static class DiffExpecter implements ComparisonListener {
        private int invoked = 0;
        private final int expectedInvocations;
        private final ComparisonType type;
        private DiffExpecter(ComparisonType type) {
            this(type, 1);
        }
        private DiffExpecter(ComparisonType type, int expected) {
            this.type = type;
            this.expectedInvocations = expected;
        }
        public void comparisonPerformed(Comparison comparison,
                                        ComparisonResult outcome) {
            assertTrue(invoked < expectedInvocations);
            invoked++;
            assertEquals(type, comparison.getType());
            assertEquals(ComparisonResult.CRITICAL, outcome);
        }
    }

    private Document doc;

    @Before public void createDoc() throws Exception {
        doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .newDocument();
    }

    @Test public void compareNodesOfDifferentType() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.NODE_TYPE);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(doc.createElement("x"),
                                    doc.createComment("x")));
        assertEquals(1, ex.invoked);
    }

    @Test public void compareNodesWithoutNS() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.NODE_TYPE, 0);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);
        assertEquals(ComparisonResult.EQUAL,
                     d.compareNodes(doc.createElement("x"),
                                    doc.createElement("x")));
        assertEquals(0, ex.invoked);
    }

    @Test public void compareNodesDifferentNS() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.NAMESPACE_URI);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(doc.createElementNS("x", "y"),
                                    doc.createElementNS("z", "y")));
        assertEquals(1, ex.invoked);
    }

    @Test public void compareNodesDifferentPrefix() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.NAMESPACE_PREFIX);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(new DifferenceEvaluator() {
                public ComparisonResult evaluate(Comparison comparison,
                                                 ComparisonResult outcome) {
                    if (comparison.getType()
                        == ComparisonType.NAMESPACE_PREFIX) {
                        assertEquals(ComparisonResult.DIFFERENT, outcome);
                        return ComparisonResult.CRITICAL;
                    }
                    assertEquals(ComparisonResult.EQUAL, outcome);
                    return ComparisonResult.EQUAL;
                }
            });
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(doc.createElementNS("x", "x:y"),
                                    doc.createElementNS("x", "z:y")));
        assertEquals(1, ex.invoked);
    }

    @Test public void compareNodesDifferentNumberOfChildren() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex =
            new DiffExpecter(ComparisonType.CHILD_NODELIST_LENGTH, 2);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);
        Element e1 = doc.createElement("x");
        Element e2 = doc.createElement("x");
        assertEquals(ComparisonResult.EQUAL, d.compareNodes(e1, e2));
        e1.appendChild(doc.createElement("x"));
        assertEquals(ComparisonResult.CRITICAL, d.compareNodes(e1, e2));
        assertEquals(1, ex.invoked);
        e2.appendChild(doc.createElement("x"));
        assertEquals(ComparisonResult.EQUAL, d.compareNodes(e1, e2));
        e2.appendChild(doc.createElement("x"));
        assertEquals(ComparisonResult.CRITICAL, d.compareNodes(e1, e2));
        assertEquals(2, ex.invoked);
    }

}
