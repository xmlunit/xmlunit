/*
******************************************************************
Copyright (c) 2007-2008, Jeff Martin, Tim Bacon
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

import java.util.HashMap;
import junit.framework.TestCase;
import org.custommonkey.xmlunit.exceptions.ConfigurationException;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class AbstractXpathEngineTests extends TestCase {

    protected static final String[] testAttrNames = {"attrOne", "attrTwo"};

    protected static final String testString =
        "<test><nodeWithoutAttributes>intellectual property rights"
        + " </nodeWithoutAttributes>"
        + "<nodeWithoutAttributes>make us all poorer </nodeWithoutAttributes>"
        + "<nodeWithAttributes " + testAttrNames[0] + "=\"open source \" "
        + testAttrNames[1]
        + "=\"is the answer \">free your code from its chains"
        + "</nodeWithAttributes></test>";
    protected Document testDocument;

    protected abstract XpathEngine newXpathEngine();

    public void testGetMatchingNodesNoMatches() throws Exception {
        NodeList nodeList = newXpathEngine().getMatchingNodes("toast",
                                                              testDocument);
        assertEquals(0, nodeList.getLength());
    }

    public void testGetMatchingNodesMatchRootElement() throws Exception {
        NodeList nodeList = newXpathEngine().getMatchingNodes("test",
                                                              testDocument);
        assertEquals(1, nodeList.getLength());
        assertEquals(Node.ELEMENT_NODE, nodeList.item(0).getNodeType());
    }

    public void testGetMatchingNodesMatchElement() throws Exception {
        NodeList nodeList = newXpathEngine()
            .getMatchingNodes("test/nodeWithoutAttributes", testDocument);
        assertEquals(2, nodeList.getLength());
        assertEquals(Node.ELEMENT_NODE, nodeList.item(0).getNodeType());
    }

    public void testGetMatchingNodesMatchText() throws Exception {
        NodeList nodeList = newXpathEngine().getMatchingNodes("test//text()",
                                                              testDocument);
        assertEquals(3, nodeList.getLength());
        assertEquals(Node.TEXT_NODE, nodeList.item(0).getNodeType());
    }

    public void testGetMatchingNodesCheckSubNodes() throws Exception {
        NodeList nodeList = newXpathEngine()
            .getMatchingNodes("test/nodeWithAttributes", testDocument);
        assertEquals(1, nodeList.getLength());
        Node aNode;

        aNode = nodeList.item(0);
        assertEquals(Node.ELEMENT_NODE, aNode.getNodeType());
        assertEquals(true, aNode.hasAttributes());
        assertEquals(true, aNode.hasChildNodes());

        NodeList children = aNode.getChildNodes();
        int length = children.getLength();
        assertEquals(1, length);
        for (int i=0; i < length; ++i) {
            assertEquals(Node.TEXT_NODE, children.item(i).getNodeType());
        }

        NamedNodeMap attributes = aNode.getAttributes();
        int numAttrs = attributes.getLength();
        assertEquals(testAttrNames.length, numAttrs);
        for (int i=0; i < testAttrNames.length; ++i) {
            Node attrNode = attributes.getNamedItem(testAttrNames[i]);
            assertNotNull(attrNode);
            assertEquals(Node.ATTRIBUTE_NODE, attrNode.getNodeType());
        }
    }

    public void testEvaluate() throws Exception {
        String result = newXpathEngine().evaluate("count(test//node())",
                                                  testDocument);
        assertEquals("3 elements and 3 text nodes", "6", result);
    }

    public void testXpathPrefixChange() throws Exception {
        String testDoc = "<t:test xmlns:t=\"urn:foo\"><t:bar/></t:test>";
        Document d = XMLUnit.buildControlDocument(testDoc);
        HashMap m = new HashMap();
        m.put("foo", "urn:foo");
        NamespaceContext ctx = new SimpleNamespaceContext(m);
        XpathEngine engine = newXpathEngine();
        engine.setNamespaceContext(ctx);

        NodeList l = engine.getMatchingNodes("//foo:bar", d);
        assertEquals(1, l.getLength());
        assertEquals(Node.ELEMENT_NODE, l.item(0).getNodeType());

        String s = engine.evaluate("count(foo:test//node())", d);
        assertEquals("1", s);
    }

    // http://sourceforge.net/forum/forum.php?thread_id=1832061&forum_id=73274
    public void testXpathExistsWithNsAndLocalNameSelector() throws Exception {
        String testDoc =
            "<MtcEnv Version=\"1.0\" xmlns=\"http://www.Mtc.com/schemas\" xmlns:bms=\"http://www.cieca.com/BMS\"> "
            + "<EnvContext> "
            + "<NameValuePair> "
            + "<Name>Timestamp</Name> "
            + "<Value>2007-07-26T11:59:00</Value> "
            + "</NameValuePair> "
            + "</EnvContext> "
            + "<EnvBodyList> "
            + "<EnvBody> "
            + "<Metadata> "
            + "<Identifier>CIECABMSAssignmentAddRq</Identifier> "
            + "</Metadata> "
            + "<Content> "
            + "<bms:CIECA> "
            + "<bms:AssignmentAddRq> "
            + "<bms:RqUID>3744f84b-ac18-5303-0082-764bdeb20df9</bms:RqUID> "
            + "</bms:AssignmentAddRq> "
            + "</bms:CIECA> "
            + "</Content> "
            + "</EnvBody> "
            + "</EnvBodyList> "
            + "</MtcEnv>";
        Document d = XMLUnit.buildControlDocument(testDoc);

        XpathEngine engine = newXpathEngine();
        NodeList l =
            engine.getMatchingNodes("//*[local-name()='RqUID'][namespace-uri()='http://www.cieca.com/BMS']", d);
        assertEquals(1, l.getLength());
    }

    public void setUp() throws Exception {
        testDocument = XMLUnit.buildControlDocument(testString);
    }

    public AbstractXpathEngineTests(String name) {
        super(name);
    }


    public void testEvaluateInvalidXPath() throws Exception {
    	String xpath = "count(test//*[@attrOne='open source])";
    	try {
            String result = newXpathEngine().evaluate(xpath, testDocument);
            fail("expected Exception to be thrown but wasn't");
    	} catch (XpathException ex) {
            // expected
    	} catch (ConfigurationException ex) {
            // acceptable in the JAXP 1.2 case
        }
    }
}
