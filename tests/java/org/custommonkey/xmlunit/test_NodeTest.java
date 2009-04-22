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

import java.io.StringReader;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.w3c.dom.Node;

/**
 * JUnit test for NodeTest
 */
public class test_NodeTest extends TestCase {
    private NodeTest nodeTest;

    private class NodeTypeTester implements NodeTester {
        private short type;
        public NodeTypeTester(short type) {
            this.type = type;
        }
        public void testNode(Node aNode, NodeTest forTest) {
            assertEquals(type, aNode.getNodeType());
        }
        public void noMoreNodes(NodeTest forTest) {
        }
    }

    private class RejectingNodeTester implements NodeTester {
        public boolean completed;
        public void testNode(Node aNode, NodeTest forTest)
            throws NodeTestException {
            throw new NodeTestException("Reject all nodes", aNode);
        }
        public void noMoreNodes(NodeTest forTest) throws NodeTestException {
            completed = true;
            throw new NodeTestException("Rejection");
        }
    }

    public void testFiltering() throws Exception {
        nodeTest = new NodeTest(
                                new StringReader("<message><hello>folks</hello></message>"));
        short nodeType = Node.ELEMENT_NODE;
        nodeTest.performTest(new NodeTypeTester(nodeType), nodeType);

        nodeType = Node.TEXT_NODE;
        nodeTest.performTest(new NodeTypeTester(nodeType), nodeType);

        nodeType = Node.COMMENT_NODE;
        nodeTest.performTest(new NodeTypeTester(nodeType), nodeType);

        short[] nodeTypes = new short[] {Node.TEXT_NODE, Node.COMMENT_NODE};
        nodeTest.performTest(new NodeTypeTester(Node.TEXT_NODE), nodeTypes);
    }

    public void testNodeTesting() throws Exception {
        nodeTest = new NodeTest(
                                new StringReader("<keyboard><qwerty>standard</qwerty></keyboard>"));
        RejectingNodeTester tester = new RejectingNodeTester();

        try {
            nodeTest.performTest(tester, Node.TEXT_NODE);
            fail("Expected NodeTestException");
        } catch (NodeTestException e) {
            assertEquals("not completed", false, tester.completed);
        }

        try {
            nodeTest.performTest(tester, Node.CDATA_SECTION_NODE);
            fail("Expected NodeTestException");
        } catch (NodeTestException e) {
            assertEquals("completed", true, tester.completed);
        }
    }

    public test_NodeTest(String name) {
        super(name);
    }

    public static TestSuite suite() {
        return new TestSuite(test_NodeTest.class);
    }

}

