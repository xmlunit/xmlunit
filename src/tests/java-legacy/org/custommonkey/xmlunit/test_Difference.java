/*
******************************************************************
Copyright (c) 2001-2009, Jeff Martin, Tim Bacon
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

import java.util.List;
import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author TimBacon
 */
public class test_Difference extends TestCase {
    private final Difference ORIGINAL = 
        DifferenceConstants.ATTR_NAME_NOT_FOUND;

    public void testCopyConstructor() {
        Difference copy = new Difference(ORIGINAL, null, null);
        assertEquals("id", ORIGINAL.getId(), copy.getId());
        assertEquals("description", 
                     ORIGINAL.getDescription(), copy.getDescription());
        assertEquals("recoverable", 
                     ORIGINAL.isRecoverable(), copy.isRecoverable());
        
        assertEquals("precondition", false, ORIGINAL.isRecoverable());
        copy.setRecoverable(true);
        assertEquals("recoverable again", 
                     !ORIGINAL.isRecoverable(), copy.isRecoverable());
    }
    
    public void testEquals() {
        assertTrue("not equal to null", !ORIGINAL.equals(null));
        assertTrue("not equal to other class", !ORIGINAL.equals("aString"));
        assertEquals("equal to self", ORIGINAL, ORIGINAL);
        
        Difference copy = new Difference(ORIGINAL, null, null);
        assertEquals("equal to copy", ORIGINAL, copy);        
    }
    
    public void testToString() throws Exception {
        String originalAsString = "Difference (#" + ORIGINAL.getId()
            + ") " + ORIGINAL.getDescription();
        assertEquals("Original", originalAsString, ORIGINAL.toString());
        
        Document document = XMLUnit.newControlParser().newDocument();
        
        Node controlNode = document.createComment("control");
        NodeDetail controlNodeDetail = new NodeDetail(controlNode.getNodeValue(),
                                                      controlNode, "/testToString/comment()");
                
        Node testNode = document.createComment("test");
        NodeDetail testNodeDetail = new NodeDetail(testNode.getNodeValue(),
                                                   testNode, "/testToString/comment()");
                
        Difference difference = new Difference(DifferenceConstants.COMMENT_VALUE, 
                                               controlNodeDetail, testNodeDetail);
        StringBuffer buf = new StringBuffer("Expected ")
            .append(DifferenceConstants.COMMENT_VALUE.getDescription())
            .append(" 'control' but was 'test' - comparing ");
        NodeDescriptor.appendNodeDetail(buf, controlNodeDetail);
        buf.append(" to ");
        NodeDescriptor.appendNodeDetail(buf, testNodeDetail);
        assertEquals("detail", buf.toString(), difference.toString());
    }
    
    // bug 2386807
    public void testXpathOfMissingAttribute() throws Exception {
        Diff d = new Diff("<foo><bar a=\"x\" y=\"z\"/></foo>",
                          "<foo><bar a=\"x\"/></foo>");
        DetailedDiff dd = new DetailedDiff(d);
        List diffs = dd.getAllDifferences();
        assertEquals(2, diffs.size());
        Difference d1 = (Difference) diffs.get(0);
        assertEquals(DifferenceConstants.ELEMENT_NUM_ATTRIBUTES_ID,
                     d1.getId());
        assertEquals("/foo[1]/bar[1]",
                     d1.getControlNodeDetail().getXpathLocation());
        assertEquals("/foo[1]/bar[1]",
                     d1.getTestNodeDetail().getXpathLocation());
        Difference d2 = (Difference) diffs.get(1);
        assertEquals(DifferenceConstants.ATTR_NAME_NOT_FOUND_ID,
                     d2.getId());
        assertEquals("/foo[1]/bar[1]/@y",
                     d2.getControlNodeDetail().getXpathLocation());
        assertEquals("/foo[1]/bar[1]",
                     d2.getTestNodeDetail().getXpathLocation());
    }

    /**
     * Constructor for test_Difference.
     * @param name
     */
    public test_Difference(String name) {
        super(name);
    }

}
