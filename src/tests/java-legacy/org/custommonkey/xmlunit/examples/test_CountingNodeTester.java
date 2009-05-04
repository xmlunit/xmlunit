/*
******************************************************************
Copyright (c) 2001-2007, Jeff Martin, Tim Bacon
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

import java.io.StringReader;

import junit.framework.TestCase;

import org.custommonkey.xmlunit.NodeTest;
import org.custommonkey.xmlunit.NodeTestException;
import org.w3c.dom.Node;

/**
 * JUnit test for CountingNodeTester
 */
public class test_CountingNodeTester extends TestCase {
    private NodeTest test;
    private CountingNodeTester tester;

    public void testPositivePath() throws Exception {
        test = new NodeTest(new StringReader("<a><b>c</b></a>"));
        tester = new CountingNodeTester(2);
        test.performTest(tester, Node.ELEMENT_NODE);

        tester = new CountingNodeTester(1);
        test.performTest(tester, Node.TEXT_NODE);

        tester = new CountingNodeTester(3);
        test.performTest(tester,
                         new short[] {Node.TEXT_NODE, Node.ELEMENT_NODE});

        tester = new CountingNodeTester(0);
        test.performTest(tester, Node.COMMENT_NODE);
    }

    public void testNegativePath() throws Exception {
        test = new NodeTest(new StringReader("<a><b>c</b></a>"));
        try {
            tester = new CountingNodeTester(2);
            test.performTest(tester, Node.TEXT_NODE);
            fail("Expected NodeTestException");
        } catch (NodeTestException e) {
            // failure, as expected
        }

        try {
            tester = new CountingNodeTester(1);
            test.performTest(tester, Node.ELEMENT_NODE);
            fail("Expected NodeTestException");
        } catch (NodeTestException e) {
            // failure, as expected
        }

        try {
            tester = new CountingNodeTester(2);
            test.performTest(tester,
                             new short[] {Node.TEXT_NODE, Node.ELEMENT_NODE});
            fail("Expected NodeTestException");
        } catch (NodeTestException e) {
            // failure, as expected
        }

        try {
            tester = new CountingNodeTester(1);
            test.performTest(tester, Node.COMMENT_NODE);
            fail("Expected NodeTestException");
        } catch (NodeTestException e) {
            // failure, as expected
        }

        try {
            tester = new CountingNodeTester(0);
            test.performTest(tester, Node.TEXT_NODE);
            fail("Expected NodeTestException");
        } catch (NodeTestException e) {
            // failure, as expected
        }
    }

    public test_CountingNodeTester(String name) {
        super(name);
    }

}

