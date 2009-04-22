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

import junit.framework.Assert;
import junit.framework.TestCase;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/**
 * JUnit test for AbstractNodeTester
 */
public class test_AbstractNodeTester extends TestCase {

    public void testExactlyOncePerMethod() throws Exception {
        String testXml = "<!DOCTYPE foo ["
            + "<!ELEMENT foo (#PCDATA)>"
            + "<!ATTLIST foo  attr CDATA #IMPLIED>"
            + "<!ENTITY my \"hello\">"
            + "<!NOTATION notation PUBLIC \"pub\">"
            + "]>"
            + "<foo attr=\"value\">"
            + "<!--comment-->"
            + "<?target processing-instruction?>"
            + "bar"
            + "&my;"
            + "<![CDATA[baz]]>"
            + "</foo>";
        NodeTest nt = new NodeTest(testXml);
        ExactlyOncePerMethod tester = new ExactlyOncePerMethod();
        nt.performTest(tester, new short[] {
                           Node.ATTRIBUTE_NODE,
                           Node.CDATA_SECTION_NODE,
                           Node.COMMENT_NODE,
                           Node.DOCUMENT_FRAGMENT_NODE,
                           Node.DOCUMENT_NODE,
                           Node.DOCUMENT_TYPE_NODE,
                           Node.ELEMENT_NODE,
                           Node.ENTITY_NODE,
                           Node.ENTITY_REFERENCE_NODE,
                           Node.NOTATION_NODE,
                           Node.PROCESSING_INSTRUCTION_NODE,
                           Node.TEXT_NODE,
                       });
        tester.verify();
    }

    private class ExactlyOncePerMethod extends AbstractNodeTester {

        private boolean cdataCalled;
        private boolean commentCalled;
        private boolean elementCalled;
        private boolean piCalled;
        private boolean textCalled;
        private boolean noMoreNodesCalled;

        public void testCDATASection(CDATASection cdata) {
            Assert.assertFalse("testCDATASection called", cdataCalled);
            cdataCalled = true;
            Assert.assertEquals("baz", cdata.getNodeValue());
        }

        public void testComment(Comment comment) {
            Assert.assertFalse("testComment called", commentCalled);
            commentCalled = true;
            Assert.assertEquals("comment", comment.getNodeValue());
        }

        public void testElement(Element element) {
            Assert.assertFalse("testElement called", elementCalled);
            elementCalled = true;
            Assert.assertEquals("foo", element.getNodeName());
            Assert.assertEquals("value", element.getAttribute("attr"));
        }

        public void testProcessingInstruction(ProcessingInstruction instr) {
            Assert.assertFalse("testProcessingInstruction called", piCalled);
            piCalled = true;
            Assert.assertEquals("target", instr.getTarget());
            Assert.assertEquals("processing-instruction", instr.getData());
        }

        public void testText(Text text) {
            Assert.assertFalse("testText called", textCalled);
            textCalled = true;
            Assert.assertEquals("barhello", text.getNodeValue());
        }

        public void noMoreNodes(NodeTest t) {
            Assert.assertFalse("noMoreNodes called", noMoreNodesCalled);
            noMoreNodesCalled = true;
        }

        void verify() {
            Assert.assertTrue("testCDATASection not called", cdataCalled);
            Assert.assertTrue("testComment not called", commentCalled);
            Assert.assertTrue("testElement not called", elementCalled);
            Assert.assertTrue("testProcessingInstruction not called",
                              piCalled);
            Assert.assertTrue("testText not called", textCalled);
            Assert.assertTrue("noMoreNodes not called", noMoreNodesCalled);
        }
    }
}
