/*
******************************************************************
Copyright (c) 2001, Jeff Martin, Tim Bacon
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

import junit.framework.TestCase;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/**
 * Testcase for XpathNodeTracker
 */
public class test_XpathNodeTracker extends TestCase {
    private XpathNodeTracker xpathNodeTracker;
    private static final Node DUMMY_NODE = null;
            
    public void testRootNode() {
        xpathNodeTracker.visitedNode(DUMMY_NODE, "diary");
        assertEquals("root node", "/diary[1]", xpathNodeTracker.toXpathString());
    }
        
    public void testOneLevelOfChildren() {
        xpathNodeTracker.visitedNode(DUMMY_NODE, "diary");
                
        xpathNodeTracker.indent();
        assertEquals("before first child", "/diary[1]", xpathNodeTracker.toXpathString());

        xpathNodeTracker.visitedNode(DUMMY_NODE, "event");
        assertEquals("first child", "/diary[1]/event[1]", xpathNodeTracker.toXpathString());
                
        xpathNodeTracker.visitedNode(DUMMY_NODE, "event");
        assertEquals("2nd child", "/diary[1]/event[2]", xpathNodeTracker.toXpathString());      
                
        xpathNodeTracker.visitedNode(DUMMY_NODE, "event");
        assertEquals("3rd child", "/diary[1]/event[3]", xpathNodeTracker.toXpathString());      

        xpathNodeTracker.visitedNode(DUMMY_NODE, "reminder");
        assertEquals("4th child", "/diary[1]/reminder[1]", xpathNodeTracker.toXpathString());           
                
        xpathNodeTracker.visitedNode(DUMMY_NODE, "event");
        assertEquals("5th child", "/diary[1]/event[4]", xpathNodeTracker.toXpathString());      
    }

    public void testTwoLevelsOfChildren() {
        xpathNodeTracker.visitedNode(DUMMY_NODE, "diary");
                
        xpathNodeTracker.indent();
        xpathNodeTracker.visitedNode(DUMMY_NODE, "event");
                
        xpathNodeTracker.indent();
        xpathNodeTracker.visitedNode(DUMMY_NODE, "details");
        assertEquals("indented", "/diary[1]/event[1]/details[1]", xpathNodeTracker.toXpathString());
                
        xpathNodeTracker.outdent();
        xpathNodeTracker.visitedNode(DUMMY_NODE, "event");
        assertEquals("outdented", "/diary[1]/event[2]", xpathNodeTracker.toXpathString());
                
        xpathNodeTracker.indent();
        xpathNodeTracker.visitedNode(DUMMY_NODE, "details");
        assertEquals("re-indented", "/diary[1]/event[2]/details[1]", xpathNodeTracker.toXpathString());
                
        xpathNodeTracker.outdent();
        assertEquals("re-outdented", "/diary[1]/event[2]", xpathNodeTracker.toXpathString());
                
        xpathNodeTracker.outdent();
        assertEquals("outdented to root node", "/diary[1]", xpathNodeTracker.toXpathString());          
    }

    public void testNodes() throws Exception {
        Document doc = XMLUnit.newControlParser().newDocument();
        Element element = doc.createElementNS("http://example.com/xmlunit", "eg:root");
        xpathNodeTracker.visited(element);
        assertEquals("root element", "/root[1]", xpathNodeTracker.toXpathString());
                
        Attr attr = doc.createAttributeNS("http://example.com/xmlunit", "eg:type");
        attr.setValue("qwerty");
        element.setAttributeNodeNS(attr);
        xpathNodeTracker.visited(attr);
        assertEquals("root element attribute", "/root[1]/@type", xpathNodeTracker.toXpathString());             
                
        xpathNodeTracker.indent();
                
        Comment comment = doc.createComment("testing a comment");
        xpathNodeTracker.visited(comment);
        assertEquals("comment", "/root[1]/comment()[1]", xpathNodeTracker.toXpathString());

        ProcessingInstruction pi = doc.createProcessingInstruction("target","data");
        xpathNodeTracker.visited(pi);
        assertEquals("p-i", "/root[1]/processing-instruction()[1]", xpathNodeTracker.toXpathString());

        Text text = doc.createTextNode("some text");
        xpathNodeTracker.visited(text);
        assertEquals("text", "/root[1]/text()[1]", xpathNodeTracker.toXpathString());

        CDATASection cdata = doc.createCDATASection("some characters");
        xpathNodeTracker.visited(cdata);
        assertEquals("cdata", "/root[1]/text()[2]", xpathNodeTracker.toXpathString());
    }

    public void testRepeatNodesForTestTracker() throws Exception {
        Document doc = XMLUnit.newControlParser().newDocument();
        final Element element = doc.createElement("repeated");
        final Element copy = doc.createElement("repeated");
                
        NodeList nodeList = new NodeList() {   
                public Node item(int index) {
                    switch(index) {
                    case 0:
                        return element;
                    case 1:
                        return copy;
                    default:
                        return null;
                    }
                }
                public int getLength() {
                    return 2;
                }
            };
        xpathNodeTracker.preloadNodeList(nodeList);


        xpathNodeTracker.visited(element);
        assertEquals("root element", "/repeated[1]", xpathNodeTracker.toXpathString());
                
        xpathNodeTracker.visited(element);
        assertEquals("visited root element again", "/repeated[1]", xpathNodeTracker.toXpathString());

        xpathNodeTracker.visited(copy);
        assertEquals("visited copy of root element", "/repeated[2]", xpathNodeTracker.toXpathString());
    }
        
    public void testRepeatNodesForControlTracker() throws Exception {
        Document doc = XMLUnit.newControlParser().newDocument();
        Element element = doc.createElement("repeated");

        xpathNodeTracker.visited(element);
        assertEquals("root element", "/repeated[1]", xpathNodeTracker.toXpathString());
                
        xpathNodeTracker.visited(element);
        assertEquals("visited root element again", "/repeated[2]", xpathNodeTracker.toXpathString());
    }
        
    // bug 1047364
    public void testEmptyIndentOutdentRootNode() {
        xpathNodeTracker.indent();
        xpathNodeTracker.outdent();
        xpathNodeTracker.visitedNode(DUMMY_NODE, "diary");
        assertEquals("root node", "/diary[1]", xpathNodeTracker.toXpathString());
    }
        
    public void setUp() {
        xpathNodeTracker = new XpathNodeTracker();
        xpathNodeTracker.reset();
    }

    /**
     * Constructor for test_XpathNodeTracker.
     * @param name
     */
    public test_XpathNodeTracker(String name) {
        super(name);
    }

}
