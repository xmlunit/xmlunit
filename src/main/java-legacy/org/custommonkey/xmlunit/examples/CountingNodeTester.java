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

import org.custommonkey.xmlunit.NodeTest;
import org.custommonkey.xmlunit.NodeTestException;
import org.custommonkey.xmlunit.NodeTester;
import org.w3c.dom.Node;

/**
 * Counts the number of nodes in a document to allow assertions to be made
 *  using a NodeTest.
 * <br />Examples and more at <a href="http://xmlunit.sourceforge.net"/>xmlunit.sourceforge.net</a>
 * @see NodeTest
 */
public class CountingNodeTester implements NodeTester {
    private final int expectedNumNodes;
    private int actualNumNodes;

    public CountingNodeTester(int expectedNumNodes) {
        this.expectedNumNodes = expectedNumNodes;
    }

    /**
     * A single Node is always valid
     * @param aNode
     * @param forTest
     */
    public void testNode(Node aNode, NodeTest forTest) {
        actualNumNodes++;
    }

    /**
     * Called by NodeTest when all nodes have been iterated over: time to see
     * if all the nodes that were expected were found.
     * Note that this method also invokes {@link #resetCounter resetCounter}
     * so that the instance can be reused.
     * @exception true if expected num nodes == actual num nodes,
     * false otherwise
     */
    public void noMoreNodes(NodeTest forTest) throws NodeTestException {
        int testedNodes = actualNumNodes;
        resetCounter();
        if (testedNodes != expectedNumNodes) {
            throw new NodeTestException("Counted " + testedNodes
                                        + " node(s) but expected " + expectedNumNodes);
        }
    }

    /**
     * Reset the counter so that an instance can be reused for another
     * <code>NodeTest</code>
     */
    public void resetCounter() {
        actualNumNodes = 0;
    }
}
