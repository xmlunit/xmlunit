// -*- Mode: JDE -*-
/*
******************************************************************
Copyright (c) 2006-2007, Jeff Martin, Tim Bacon
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

import java.io.IOException;
import java.io.Reader;

import org.custommonkey.xmlunit.XpathEngine;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.exceptions.XpathException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import junit.framework.Assert;

/**
 * Example demonstrating how to use the XPath API of XMLUnit in
 * conjunction with regular expressions (as provided by the
 * java.util.regex package of JDK 1.4+).
 */

public class XPathRegexAssert {
    // no instances
    private XPathRegexAssert() {}

    public static void assertXPathMatches(String message, String regex,
                                          String xpath, Document doc)
        throws XpathException {
        XpathEngine engine = XMLUnit.newXpathEngine();
        String value = engine.evaluate(xpath, doc);
        Assert.assertTrue(message, value.matches(regex));
    }

    public static void assertXPathMatches(String message, String regex,
                                          String xpath, String xml)
        throws XpathException, SAXException, IOException {
        Document doc = XMLUnit.buildControlDocument(xml);
        assertXPathMatches(message, regex, xpath, doc);
    }

    public static void assertXPathMatches(String message, String regex,
                                          String xpath, Reader reader)
        throws XpathException, SAXException, IOException {
        Document doc = XMLUnit.buildControlDocument(new InputSource(reader));
        assertXPathMatches(message, regex, xpath, doc);
    }

    public static void assertXPathMatches(String regex,
                                          String xpath, Document doc)
        throws XpathException {
        assertXPathMatches("expected value to match " + regex, regex,
                           xpath, doc);
    }

    public static void assertXPathMatches(String regex,
                                          String xpath, String xml)
        throws XpathException, SAXException, IOException {
        assertXPathMatches("expected value to match " + regex, regex,
                           xpath, xml);
    }

    public static void assertXPathMatches(String regex,
                                          String xpath, Reader reader)
        throws XpathException, SAXException, IOException {
        assertXPathMatches("expected value to match " + regex, regex,
                           xpath, reader);
    }
}
