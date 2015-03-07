/*
******************************************************************
Copyright (c) 2001,2015 Jeff Martin, Tim Bacon
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

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.io.StringReader;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import junit.framework.TestSuite;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * JUnit test for HTMLDocumentBuilder
 */
public class test_HTMLDocumentBuilder extends XMLTestCase {
    @Mock
    private ContentHandler handler;

    private static final String xHtml =
        "<html><head><title>test</title></head>" +
        "<body><h1>hello</h1><p>world</p><hr/><div><img src=\"foo.bar\"/>" +
        "<ul><li>one</li><li>two</li></ul></div></body></html>";
    private Document xHtmlDocument;
    private HTMLDocumentBuilder parser;
    private TolerantSaxDocumentBuilder builder;

    public void testParseGoodHtml() throws Exception {
        assertParsedDocumentEqual(xHtmlDocument, xHtml);
        assertEquals(parser.getTrace(),-1, parser.getTrace().indexOf("WARNING"));
    }

    public void testParseOldHtml() throws Exception {
        String oldHTML=
            "<html><head><title>test</title></head>" +
            "<body><h1>hello</h1><p>world<hr><div><img src=\"foo.bar\">" +
            "<ul><li>one<li>two</ul></div></body></html>";
        assertParsedDocumentEqual(xHtmlDocument, oldHTML);
        assertEquals(parser.getTrace(),-1, parser.getTrace().indexOf("WARNING"));
    }

    public void testParsePoorHtml() throws Exception {
        String poorHTML=
            "<html><head><title>test</title></head>" +
            "<body><h1>hello</h1><p>world<hr><div><img src=\"foo.bar\">" +
            "<ul><li>one<li>two";
        assertParsedDocumentEqual(xHtmlDocument, poorHTML);
        assertEquals(parser.getTrace(),-1, parser.getTrace().indexOf("WARNING"));
    }

    public void testCharactersAfterSimpleElement() throws Exception {
        HTMLDocumentBuilder.SwingEvent2SaxAdapter adapter =
            new MyHTMLDocumentBuilder().create();
        adapter.parse(new StringReader("<foo/>"), handler);
        adapter.handleSimpleTag(javax.swing.text.html.HTML.Tag.BR,
                                new javax.swing.text.SimpleAttributeSet(),
                                0);
        char[] foo = ">foo".toCharArray();
        adapter.handleText(foo, 0);
        verify(handler).characters(foo, 1, 3);
    }

    public void testHandlingOfSAXExceptions() throws Exception {
        SAXException e = new SAXException();
        doThrow(e).when(handler).endDocument();
        HTMLDocumentBuilder.SwingEvent2SaxAdapter adapter =
            new MyHTMLDocumentBuilder().create();
        try {
            adapter.parse(new StringReader("<foo/>"), handler);
        } catch (SAXException ex) {
            assertSame(e, ex);
        }
    }

    public void testParseWithComment() throws Exception {
        String h = "<!-- comment -->" + xHtml;
        assertParsedDocumentEqual(XMLUnit.buildControlDocument(h), h);
    }

    private void assertParsedDocumentEqual(Document control, String test)
        throws Exception {
        assertXMLEqual(control, parser.parse(test));
    }

    public test_HTMLDocumentBuilder(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        xHtmlDocument = XMLUnit.buildControlDocument(xHtml);
        builder = new TolerantSaxDocumentBuilder(XMLUnit.newTestParser());
        parser = new HTMLDocumentBuilder(builder);
        MockitoAnnotations.initMocks(this);
    }

    public static TestSuite suite() {
        return new TestSuite(test_HTMLDocumentBuilder.class);
    }

    private class MyHTMLDocumentBuilder extends HTMLDocumentBuilder {
        private MyHTMLDocumentBuilder() {
            super(builder);
        }
        public SwingEvent2SaxAdapter create() {
            return new SwingEvent2SaxAdapter();
        }
    }
}

