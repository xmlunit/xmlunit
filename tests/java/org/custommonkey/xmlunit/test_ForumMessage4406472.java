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

package org.custommonkey.xmlunit;

import java.util.Arrays;
import junit.framework.TestCase;
import org.w3c.dom.Node;

/**
 * @see http://sf.net/forum/message.php?msg_id=4406472
 */
public class test_ForumMessage4406472 extends TestCase {

    private static final String doc1 = 
        "<pub:Book xmlns:pub=\"http://www.publishing.org\" date=\"2007-01-01\">"
        + "     <pub:Title>String</pub:Title>"
        + "     <pub:Author>String</pub:Author>"
        + "     <pub:ISBN>String</pub:ISBN>"
        + "     <pub:Publisher>String</pub:Publisher>"
        + "     <pub:Price>34.50</pub:Price>"
        + "</pub:Book>";

    private static final String doc2 = 
        "<p:Book xmlns:p=\"http://www.publishing.org\" date=\"1900-01-01\">"
        + "     <p:Title>Bla</p:Title>"
        + "     <p:Author>Bla</p:Author>"
        + "     <p:ISBN>Bla</p:ISBN>"
        + "     <p:Publisher>Bla</p:Publisher>"
        + "     <p:Price>0.00</p:Price>"
        + "</p:Book>";

    private class OriginalDifferenceListener implements DifferenceListener { 
        private int[] IGNORE = new int[] { 
            DifferenceConstants.ATTR_VALUE_ID, 
            DifferenceConstants.ATTR_VALUE_EXPLICITLY_SPECIFIED_ID, 
            DifferenceConstants.TEXT_VALUE_ID, 
            DifferenceConstants.NAMESPACE_PREFIX_ID, 
            DifferenceConstants.NAMESPACE_URI_ID 
        }; 
 
        public int differenceFound(Difference difference) { 
            Arrays.sort(IGNORE); 
            return Arrays.binarySearch(IGNORE, difference.getId()) >= 0 
                ? RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL 
                : RETURN_ACCEPT_DIFFERENCE; 
        } 
 
        public void skippedComparison(Node control, Node test) { 
        } 
    } 

    private class ModifiedDifferenceListener implements DifferenceListener { 
        private int[] IGNORE = new int[] { 
            DifferenceConstants.ATTR_VALUE_ID, 
            DifferenceConstants.ATTR_VALUE_EXPLICITLY_SPECIFIED_ID, 
            DifferenceConstants.TEXT_VALUE_ID, 
            DifferenceConstants.NAMESPACE_PREFIX_ID, 
        };

        private ModifiedDifferenceListener() {
            Arrays.sort(IGNORE);
        }
 
        public int differenceFound(Difference difference) { 
            return Arrays.binarySearch(IGNORE, difference.getId()) >= 0 
                ? RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL 
                : difference.isRecoverable()
                    ? RETURN_IGNORE_DIFFERENCE_NODES_SIMILAR
                    : RETURN_ACCEPT_DIFFERENCE;
        } 
 
        public void skippedComparison(Node control, Node test) { 
        } 
    }

    public void testOriginal() throws Exception {
        Diff d = new Diff(doc1, doc2);
        d.overrideDifferenceListener(new OriginalDifferenceListener());
        assertTrue(d.toString(), d.similar());
    }

    public void testModified() throws Exception {
        Diff d = new Diff(doc1, doc2);
        d.overrideDifferenceListener(new ModifiedDifferenceListener());
        assertTrue(d.toString(), d.similar());
    }
}
