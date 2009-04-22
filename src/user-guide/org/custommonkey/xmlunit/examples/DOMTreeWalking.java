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

import junit.framework.TestCase;

import org.custommonkey.xmlunit.*;
import org.w3c.dom.*;

/**
 * Code from "DOM Tree Walking" section of User's Guide
 */
public class DOMTreeWalking extends TestCase {

    private String myXML = null;
    static final String ATTRIBUTE_NAME = null;

    private void AccessingAttributesInANodeTest() throws Exception {
        NodeTest nt = new NodeTest(myXML);
        NodeTester tester = new MyNodeTester();
        nt.performTest(tester, Node.ELEMENT_NODE);
    }

    private void AccessingAttributesInANodeTestAbstractNodeTesterVersion() throws Exception {
        NodeTest nt = new NodeTest(myXML);
        NodeTester tester = new AbstractNodeTester() {
                public void testElement(Element element) throws NodeTestException {
                    Attr attributeToTest = element.getAttributeNode(ATTRIBUTE_NAME);
                }
            };
        nt.performTest(tester, Node.ELEMENT_NODE);
    }

    static class MyNodeTester implements NodeTester {
        public void testNode(Node aNode, NodeTest test) {
            Element anElement = (Element) aNode;
            Attr attributeToTest = anElement.getAttributeNode(ATTRIBUTE_NAME);
        }

        public void noMoreNodes(NodeTest test) {}
    }
}
