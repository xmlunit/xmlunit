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

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import junit.framework.TestSuite;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * JUnit test for TolerantSaxDocumentBuilder
 */
public class test_TolerantSaxDocumentBuilder extends XMLTestCase {
    private TolerantSaxDocumentBuilder builder;
    private SAXParser parser;

    private static final String SIMPLEST_XML = "<root><node>text</node></root>";

    public void testSimpleDocument() throws Exception {
        String simpleXML = XML_DECLARATION + SIMPLEST_XML;
        Document simpleXMLDocument = XMLUnit.buildControlDocument(
                                                                  simpleXML);
        assertParsedDocumentEqual(simpleXMLDocument, simpleXML);
        assertTrue(builder.getTrace(), builder.getTrace().indexOf("WARNING") == -1);
    }

    private void assertParsedDocumentEqual(Document control, String test)
        throws Exception {
        InputSource parseSource = new InputSource(new StringReader(test));
        parser.setProperty("http://xml.org/sax/properties/lexical-handler",
                           builder);
        parser.parse(parseSource, builder);
        assertXMLEqual(control, builder.getDocument());
    }

    public void testSimpleDocumentWithComments() throws Exception {
        String xmlWithComments = XML_DECLARATION + "<more>" + SIMPLEST_XML
            + "<!--this is a comment -->" + SIMPLEST_XML + "</more>";
        Document documentWithComments = XMLUnit.buildControlDocument(
                                                                     xmlWithComments);
        assertParsedDocumentEqual(documentWithComments, xmlWithComments);
        assertTrue(builder.getTrace(), builder.getTrace().indexOf("WARNING") == -1);
    }

    public void testSimpleDocumentWithProcessingInstruction() throws Exception {
        String xmlWithProcInstruction = XML_DECLARATION + "<more>" + SIMPLEST_XML
            + "<?processing instruction?>" + SIMPLEST_XML + "</more>";
        Document documentWithProcInstruction = XMLUnit.buildControlDocument(
                                                                            xmlWithProcInstruction);
        assertParsedDocumentEqual(documentWithProcInstruction,
                                  xmlWithProcInstruction);
        assertTrue(builder.getTrace(), builder.getTrace().indexOf("WARNING") == -1);
    }

    public void testStartElementWithNoEnd() throws Exception {
        builder.startDocument();
        builder.startElement(null, null, "root", null);

        Document oneElementDocument = XMLUnit.buildControlDocument("<root/>");
        assertXMLEqual(oneElementDocument, builder.getDocument());
        assertTrue(builder.getTrace(), builder.getTrace().indexOf("WARNING") == -1);
    }

    public void testEndElementWithNoStart() throws Exception {
        builder.startDocument();
        builder.startElement(null, null, "root", null);
        builder.endElement(null, null, "node");
        builder.endElement(null, null, "root");

        Document oneElementDocument = XMLUnit.buildControlDocument("<root/>");
        assertXMLEqual(oneElementDocument, builder.getDocument());
        assertTrue(builder.getTrace(), builder.getTrace().indexOf("WARNING") != -1);
    }

    public void testEndElementBeforeStart() throws Exception {
        builder.startDocument();
        builder.endElement(null, null, "root");
        builder.startElement(null, null, "root", null);

        Document oneElementDocument = XMLUnit.buildControlDocument("<root/>");
        assertXMLEqual(oneElementDocument, builder.getDocument());
        assertTrue(builder.getTrace(), builder.getTrace().indexOf("WARNING") != -1);
    }

    public void testTextBeforeStartElement() throws Exception {
        String someText = "how could this happen?!";
        builder.startDocument();
        builder.characters(someText.toCharArray(), 0, someText.length());
        builder.startElement(null, null, "root", null);

        Document oneElementDocument = XMLUnit.buildControlDocument("<root/>");
        assertXMLEqual(oneElementDocument, builder.getDocument());
        assertTrue(builder.getTrace(), builder.getTrace().indexOf("WARNING") != -1);
    }

    public void setUp() throws Exception {
        builder = new TolerantSaxDocumentBuilder(XMLUnit.newTestParser());
        parser = SAXParserFactory.newInstance().newSAXParser();
    }

    public test_TolerantSaxDocumentBuilder(String name) {
        super(name);
    }

    public static TestSuite suite() {
        return new TestSuite(test_TolerantSaxDocumentBuilder.class);
    }

}
