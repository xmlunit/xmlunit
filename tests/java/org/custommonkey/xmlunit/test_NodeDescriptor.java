/*
******************************************************************
Copyright (c) 200, Jeff Martin, Tim Bacon
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

import java.io.File;

import junit.framework.TestCase;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * JUnit test for NodeDescriptor
 */
public class test_NodeDescriptor extends TestCase {
    private StringBuffer stringBuffer;
    private Document aDocument;
    private NodeDetail nodeDetail;
        
    public void testAppendDocumentDetail() throws Exception {
        nodeDetail = new NodeDetail("", aDocument, "/");
        NodeDescriptor.appendNodeDetail(stringBuffer, nodeDetail);
        assertEquals("<" + NodeDescriptor.DOCUMENT_NODE_DESCRIPTION
                     + "<...>> at /", stringBuffer.toString());
    }

    public void testAppendAttributeDetail() throws Exception {
        String attrName = "attrName";
        String attrValue = "attrValue";
        Attr attr = aDocument.createAttribute(attrName);
        attr.setValue(attrValue);
        String tagName = "elemTag";
        Element element = aDocument.createElement(tagName);
        element.setAttributeNode(attr);
        nodeDetail = new NodeDetail("", attr, "/elemTag");
        NodeDescriptor.appendNodeDetail(stringBuffer, nodeDetail);
        assertEquals("<" + tagName + " " 
                     + attrName + "=\"" + attrValue + "\"...> at /elemTag", stringBuffer.toString());
    }

    public void testAppendElementDetail() throws Exception {
        String tagName = "elemTag";
        Element element = aDocument.createElement(tagName);
        nodeDetail = new NodeDetail("", element, "/elemTag");
        NodeDescriptor.appendNodeDetail(stringBuffer, nodeDetail);
        assertEquals("<" + tagName + "...> at /elemTag", stringBuffer.toString());
    }

    public void testAppendTextDetail() throws Exception {
        String textString = "some text";
        Text text = aDocument.createTextNode(textString);
        String tagName = "elemTag";
        Element element = aDocument.createElement(tagName);
        element.appendChild(text);
        nodeDetail = new NodeDetail("", text, "/elemTag/text()");
        NodeDescriptor.appendNodeDetail(stringBuffer, nodeDetail);
        assertEquals("<" + tagName + " ...>" + textString 
                     + "</" + tagName + "> at /elemTag/text()", stringBuffer.toString());
    }

    public void testAppendProcessingInstructionDetail() throws Exception {
        String target = "PItarget";
        String data = "PIdata";
        Node processingInstruction = aDocument.createProcessingInstruction(target, data);
        nodeDetail = new NodeDetail("", processingInstruction, "/processing-instruction()");
        NodeDescriptor.appendNodeDetail(stringBuffer, nodeDetail);
        assertEquals("<?" + target + " " + data + "?> at /processing-instruction()", 
                     stringBuffer.toString());
    }
        
    public void testAppendCommentDetail() throws Exception {
        String comments = "This is a comment";
        Node comment = aDocument.createComment(comments);
        nodeDetail = new NodeDetail("", comment, "/comment()");
        NodeDescriptor.appendNodeDetail(stringBuffer, nodeDetail);
        assertEquals("<!--" + comments + "--> at /comment()", stringBuffer.toString());
    }

    public void testAppendCDataDetail() throws Exception {
        String cData = "<>& etc";
        Node cDataNote = aDocument.createCDATASection(cData);
        nodeDetail = new NodeDetail("", cDataNote, "/text()");
        NodeDescriptor.appendNodeDetail(stringBuffer, nodeDetail);
        assertEquals("<![CDATA[" + cData + "]]> at /text()", stringBuffer.toString());
    }

    public void testAppendDocTypeDetail() throws Exception {
        File dtdA = File.createTempFile(getName() + "A", "dtd");
        dtdA.deleteOnExit();
        String systemOnlyDTD = "<!DOCTYPE blah SYSTEM \"" + dtdA.toURL().toExternalForm() + "\">";
        String someContent = "<blah>ignored</blah>";
        String xmlWithExternalDTD = systemOnlyDTD + someContent;
            
        aDocument = XMLUnit.buildControlDocument(xmlWithExternalDTD);
        Node doctypeA = aDocument.getDoctype();
        nodeDetail = new NodeDetail("", doctypeA, "/");
        NodeDescriptor.appendNodeDetail(stringBuffer, nodeDetail);
        assertEquals(systemOnlyDTD + " at /", stringBuffer.toString());

        stringBuffer = new StringBuffer();
        File dtdB = File.createTempFile(getName() + "B", "dtd");
        dtdB.deleteOnExit();
        String publicDTD = "<!DOCTYPE web-app "
            + "PUBLIC \"-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN\" "
            + "\"" + dtdB.toURL().toExternalForm() + "\">";
        String someOtherContent = "<web-app><!--ignore me--></web-app>";
        String xmlWithPublicDTD = publicDTD + someOtherContent;
            
        Document bDocument = XMLUnit.buildControlDocument(xmlWithPublicDTD);
        Node doctypeB = bDocument.getDoctype();
        nodeDetail = new NodeDetail("", doctypeB, "/");
        NodeDescriptor.appendNodeDetail(stringBuffer, nodeDetail);
        assertEquals(publicDTD + " at /", stringBuffer.toString());
    }

    public void setUp() throws Exception {
        aDocument = XMLUnit.newControlParser().newDocument();
        stringBuffer = new StringBuffer();
    }
                        

    /**
     * Constructor for test_NodeDescriptor.
     * @param name
     */
    public test_NodeDescriptor(String name) {
        super(name);
    }

}
