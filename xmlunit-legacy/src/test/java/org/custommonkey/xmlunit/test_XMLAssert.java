/*
******************************************************************
Copyright (c) 2015 Jeff Martin, Tim Bacon
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
    * Neither the name of the XMLUnit nor the names
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
import java.util.HashMap;
import java.util.Map;
import org.xml.sax.InputSource;
import org.w3c.dom.Node;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.custommonkey.xmlunit.examples.CountingNodeTester;

public class test_XMLAssert extends TestCase {
    public void testAssertXMLEqualStringDiffBooleanWithMatch() throws Exception {
        Diff d = XMLUnit.compareXML("<foo/>", "<foo/>");
        XMLAssert.assertXMLEqual("msg", d, true);
        try {
            XMLAssert.assertXMLEqual("msg", d, false);
        } catch (AssertionFailedError f) {
            // expected
            return;
        }
        fail("should have thrown an exception");
    }

    public void testAssertXMLEqualStringDiffBooleanWithDiff() throws Exception {
        Diff d = XMLUnit.compareXML("<foo/>", "<bar/>");
        XMLAssert.assertXMLEqual("msg", d, false);
        try {
            XMLAssert.assertXMLEqual("msg", d, true);
        } catch (AssertionFailedError f) {
            // expected
            return;
        }
        fail("should have thrown an exception");
    }

    public void testAssertXMIdenticalDiffBooleanWithMatch() throws Exception {
        Diff d = XMLUnit.compareXML("<foo/>", "<foo/>");
        XMLAssert.assertXMLIdentical(d, true);
        try {
            XMLAssert.assertXMLIdentical(d, false);
        } catch (AssertionFailedError f) {
            // expected
            return;
        }
        fail("should have thrown an exception");
    }

    public void testAssertXMIdenticalDiffBooleanWithDiff() throws Exception {
        Diff d = XMLUnit.compareXML("<foo/>", "<bar/>");
        XMLAssert.assertXMLIdentical(d, false);
        try {
            XMLAssert.assertXMLIdentical(d, true);
        } catch (AssertionFailedError f) {
            // expected
            return;
        }
        fail("should have thrown an exception");
    }

    public void testAssertXpathsNotEqual() throws Exception {
        String xpathXML = "<foo><bar/></foo>";
        XMLAssert.assertXpathsNotEqual("/foo",
                                       new InputSource(new StringReader(xpathXML)),
                                       "/foo/bar",
                                       new InputSource(new StringReader(xpathXML)));
    }

    public void testAssertXpathValuesNotEqual() throws Exception {
        String controlXML = "<foo>bar</foo>";
        try {
            XMLAssert.assertXpathValuesNotEqual("/foo", controlXML,
                                                "/foo", controlXML);
        } catch (AssertionFailedError f) {
            // expected
            return;
        }
        fail("should have thrown an exception");
    }

    public void testAssertXpathValuesNotEqualNoMatch() throws Exception {
        String controlXML = "<foo>bar</foo>";
        try {
            XMLAssert.assertXpathValuesNotEqual("/baz", controlXML,
                                                "/baz", controlXML);
        } catch (AssertionFailedError f) {
            // expected
            return;
        }
        fail("should have thrown an exception");
    }

    public void testXpathEvaluatesToQualifiedName() throws Exception {
        String faultDocument = "<env:Envelope "
            + "xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'>"
            + "<env:Body><env:Fault><faultcode>env:Server</faultcode>"
            + "<faultstring>marche pas</faultstring><detail/></env:Fault>"
            + "</env:Body></env:Envelope>";
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("env11", "http://schemas.xmlsoap.org/soap/envelope/");
        XMLUnit.setXpathNamespaceContext(new SimpleNamespaceContext(namespaces));
        XMLAssert.assertXpathEvaluatesTo(QualifiedName.valueOf("env11:Server"),
                                         "//env11:Envelope/env11:Body/"
                                         + "env11:Fault/faultcode",
                                         new InputSource(new StringReader(faultDocument)));
    }

    public void testAssertNodeTestPassesWithMatch() throws Exception {
        NodeTest test = new NodeTest(new StringReader(test_XMLTestCase.xpathValuesTestXML));
        NodeTester tester = new CountingNodeTester(1);
        XMLAssert.assertNodeTestPasses(test, tester, new short[] { Node.TEXT_NODE }, true);
        try {
            XMLAssert.assertNodeTestPasses(test, tester, new short[] { Node.TEXT_NODE },
                                           false);
        } catch (AssertionFailedError e) {
            // expected
            return;
        }
        fail("should have thrown an exception");
    }

    public void testAssertNodeTestPassesWithDiff() throws Exception {
        NodeTest test = new NodeTest(new StringReader(test_XMLTestCase.xpathValuesTestXML));
        NodeTester tester = new CountingNodeTester(1);
        XMLAssert.assertNodeTestPasses(test, tester, new short[] { Node.ELEMENT_NODE },
                                       false);
        try {
            XMLAssert.assertNodeTestPasses(test, tester, new short[] { Node.ELEMENT_NODE },
                                           true);
        } catch (AssertionFailedError e) {
            // expected
            return;
        }
        fail("should have thrown an exception");
    }
}
