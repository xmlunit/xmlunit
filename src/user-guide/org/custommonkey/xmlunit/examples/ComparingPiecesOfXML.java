/*
******************************************************************
Copyright (c) 2007, Jeff Martin, Tim Bacon
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

package org.custommonkey.xmlunit.examples;

import java.util.Arrays;
import java.util.List;

import org.custommonkey.xmlunit.*;
import org.w3c.dom.*;

/**
 * Code from "Comparing Pieces of XML" section of User's Guide
 */
public class ComparingPiecesOfXML extends XMLTestCase {

    class MyDifferenceListener implements DifferenceListener {
        private boolean calledFlag = false;
        public boolean called() { return calledFlag; }

        public int differenceFound(Difference difference) {
            calledFlag = true;
            return RETURN_ACCEPT_DIFFERENCE;
        }

        public void skippedComparison(Node control, Node test) {
        }
    }

    private void usingDifferenceEngineDirectly() {
        ComparisonController myComparisonController = null;
        Node controlNode = null;
        Node testNode = null;
        ElementQualifier myElementQualifier = null;

        DifferenceEngine engine = new DifferenceEngine(myComparisonController);
        MyDifferenceListener listener = new MyDifferenceListener();
        engine.compare(controlNode, testNode, listener,
                       myElementQualifier);
        System.err.println("There have been "
                           + (listener.called() ? "" : "no ")
                           + "differences.");
    }

    public class HaltOnNonRecoverable implements ComparisonController {
        public boolean haltComparison(Difference afterDifference) {
            return !afterDifference.isRecoverable();
        }
    }

    public static class IgnoreDoctype implements DifferenceListener {
        private static final int[] IGNORE = new int[] {
            DifferenceConstants.HAS_DOCTYPE_DECLARATION_ID,
            DifferenceConstants.DOCTYPE_NAME_ID,
            DifferenceConstants.DOCTYPE_PUBLIC_ID_ID,
            DifferenceConstants.DOCTYPE_SYSTEM_ID_ID
        };

        static {
            Arrays.sort(IGNORE);
        }

        public int differenceFound(Difference difference) {
            return Arrays.binarySearch(IGNORE, difference.getId()) >= 0
                ? RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL
                : RETURN_ACCEPT_DIFFERENCE;
        }
    
        public void skippedComparison(Node control, Node test) {
        }
    }

    private void comparingTwoPiecesOfXMLUsingDiff() throws Exception {
        Diff d = new Diff("<a><b/><c/></a>", "<a><c/><b/></a>");
        assertFalse(d.identical()); // CHILD_NODELIST_SEQUENCE Difference
        assertTrue(d.similar());
    }

    private void FindingAllDifferencesUsingDetailedDiff() throws Exception {
        Diff d = new Diff("<a><b/><c/></a>", "<a><c/><b/></a>");
        DetailedDiff dd = new DetailedDiff(d);
        dd.overrideElementQualifier(null);
        assertFalse(dd.similar());
        List l = dd.getAllDifferences();
        assertEquals(2, l.size()); // expexted <b/> but was <c/> and vice versa
    }

    private void junit3() throws Exception {
        String CONTROL = null;
        String TEST = null;
        Diff d = new Diff(CONTROL, TEST);
        assertTrue("expected pieces to be similar, " + d.toString(),
                   d.similar());
        assertXMLEqual("expected pieces to be similar", CONTROL, TEST);
    }
}
