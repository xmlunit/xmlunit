/*
******************************************************************
Copyright (c) 2001-2007,2015-2016,2022 Jeff Martin, Tim Bacon
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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Enumeration;

import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;

import org.w3c.dom.Document;

import org.xml.sax.ContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Build a DOM document from HTML content converting from 'plain' HTML into
 * 'XHTML' along the way with the help of a TolerantSaxDocumentBuilder and
 * the Swing html parser classes.
 * This allows XML assertions to be made against badly formed HTML.
 * @see TolerantSaxDocumentBuilder
 */
public class HTMLDocumentBuilder {
    /**
     * The document builder.
     */
    protected final TolerantSaxDocumentBuilder tolerantSaxDocumentBuilder;
    /**
     * The adapter between Swing and SAX.
     */
    protected final SwingEvent2SaxAdapter swingEvent2SaxAdapter;
    private final StringBuilder traceBuilder;

    /**
     * Constructor
     * @param tolerantSaxDocumentBuilder the instance that will receive SAX
     *  calls generated as the HTML is parsed and build up a DOM Document
     */
    public HTMLDocumentBuilder(TolerantSaxDocumentBuilder tolerantSaxDocumentBuilder) {
        this.tolerantSaxDocumentBuilder = tolerantSaxDocumentBuilder;
        this.swingEvent2SaxAdapter = new SwingEvent2SaxAdapter();
        this.traceBuilder = new StringBuilder();
    }

    /**
     * Parses the document.
     * <p>Not thread-safe!</p>
     * @param reader reader to read the document from
     * @return a DOM document parsed from the Reader via an SwingEvent2SaxAdapter
     * and TolerantSaxBuilder.
     * @throws SAXException if the parser feels like it
     * @throws IOException on I/O errors
     * @see TolerantSaxDocumentBuilder
     */
    public Document parse(Reader reader) throws SAXException, IOException {
        traceBuilder.delete(0, traceBuilder.length());
        swingEvent2SaxAdapter.parse(reader, tolerantSaxDocumentBuilder);
        traceBuilder.append(tolerantSaxDocumentBuilder.getTrace());
        return tolerantSaxDocumentBuilder.getDocument();
    }

    /**
     * Parses the document.
     * <p>Not thread-safe!</p>
     * @param htmlString string to read the document from
     * @throws SAXException if the parser feels like it
     * @throws IOException on I/O errors
     * @return a DOM document parsed from the String via an SwingEvent2SaxAdapter
     * and TolerantSaxBuilder.
     * @see TolerantSaxDocumentBuilder
     */
    public Document parse(String htmlString) throws SAXException, IOException {
        return parse(new StringReader(htmlString));
    }

    /**
     * @return the trace of events and / or warnings encountered during parsing
     */
    public String getTrace() {
        return traceBuilder.toString();
    }

    /**
     * Append to the log built up during parsing
     * @param msg what to append
     */
    private void trace(String msg) {
        traceBuilder.append(msg).append('\n');
    }

    /**
     * Adapts Swing HTML callback messages to Sax equivalents, passing them
     * to a Sax-aware ContentHandler.
     */
    public class SwingEvent2SaxAdapter extends HTMLEditorKit.ParserCallback {
        private static final boolean IGNORE_HTML_CHAR_SET = true;
        private final AttributesImpl attributes;
        private final ParserDelegator delegator;
        private boolean lastTagWasSimpleTag;
        private ContentHandler saxContentHandler;
        private SAXException firstUnhandledException;

        /**
         * Default constructor
         */
        public SwingEvent2SaxAdapter() {
            this.attributes = new AttributesImpl();
            this.delegator = new ParserDelegator();
        }

        /**
         * Perform Swing-HTML-parse-event-to-Sax-event conversion
         * @param reader reader to read the document from
         * @param saxContentHandler content handler receiving SAX events while parsing the document
         * @throws SAXException if the parser feels like it
         * @throws IOException on I/O errors
         */
        public void parse(Reader reader, ContentHandler saxContentHandler)
            throws SAXException, IOException {
            this.saxContentHandler = saxContentHandler;
            preParse();
            delegator.parse(reader, this, IGNORE_HTML_CHAR_SET);
            postParse();
        }

        /**
         * Equivalent to Sax <code>startDocument</code>
         * @throws SAXException
         */
        private void preParse() throws SAXException {
            firstUnhandledException = null;
            saxContentHandler.startDocument();
        }

        /**
         * Equivalent to Sax <code>endDocument</code>
         * @throws SAXException if any SAXExceptions have occurred during
         *  parsing
         */
        private void postParse() throws SAXException {
            try {
                saxContentHandler.endDocument();
            } catch (SAXException e) {
                handleSAXException(e);
            }
            if (firstUnhandledException != null) {
                throw firstUnhandledException;
            }
        }

        /**
         * Swing-HTML-parser template method, no ContentHandler equivalent
         */
        @Override
        public void flush() throws BadLocationException {
            throw new UnsupportedOperationException();
        }

        /**
         * Equivalent to Sax <code>characters</code>
         */
        @Override
        public void handleText(char[] data, int pos) {
            try {
                int startPos;
                if (lastTagWasSimpleTag) {
                    startPos = getStartIgnoringClosingSimpleTag(data);
                } else {
                    startPos = 0;
                }
                if (startPos < data.length) {
                    saxContentHandler.characters(data, startPos,
                                                 data.length - startPos);
                }
            } catch (SAXException e) {
                handleSAXException(e);
            }
        }

        /**
         * Adjusts the start offset into the character array for the fact that
         * the Swing HTML parser doesn't handle simple tags with explicit
         * closing angle brackets e.g. &lt;hr/&gt;
         * @param data
         * @return offset of actual character data into the array
         */
        private int getStartIgnoringClosingSimpleTag(char[] data) {
            if (data[0] == '>') {
                return 1;
            }
            return 0;
        }

        /**
         * Equivalent to Sax LexicalHandler <code>comment</code> method.
         * If the supplied ContentHandler is also an LexicalHandler then the
         * cast will be made and the sax event passed on.
         */
        @Override
        public void handleComment(char[] data, int pos) {
            if (saxContentHandler instanceof LexicalHandler) {
                try {
                    ((LexicalHandler)saxContentHandler).comment(data,
                                                                0, data.length);
                } catch (SAXException e) {
                    handleSAXException(e);
                }
            } else {
                trace("Unhandled comment " + new String(data));
            }
        }

        /**
         * Equivalent to Sax <code>startElement</code>
         */
        @Override
        public void handleStartTag(javax.swing.text.html.HTML.Tag tag,
                                   MutableAttributeSet attributeSet, int pos) {
            try {
                saxContentHandler.startElement("", "", tag.toString(),
                                               convertToSaxAttributes(attributeSet));
            } catch (SAXException e) {
                handleSAXException(e);
            }
            lastTagWasSimpleTag = false;
        }

        /**
         * Equivalent to Sax <code>endElement</code>
         */
        @Override
        public void handleEndTag(javax.swing.text.html.HTML.Tag tag, int pos) {
            try {
                saxContentHandler.endElement("", "", tag.toString());
            } catch (SAXException e) {
                handleSAXException(e);
            }
        }

        /**
         * Equivalent to Sax <code>startElement</code> plus
         * <code>endElement</code>
         */
        @Override
        public void handleSimpleTag(javax.swing.text.html.HTML.Tag tag,
                                    MutableAttributeSet attributeSet, int pos) {
            handleStartTag(tag, attributeSet, pos);
            handleEndTag(tag, pos);
            lastTagWasSimpleTag = true;
        }

        /**
         * Swing-HTML-parser template method, no ContentHandler equivalent.
         * These errors are generally recoverable, so they are logged.
         */
        @Override
        public void handleError(String errorMsg, int pos){
            trace("HTML ERROR: " + errorMsg);
        }

        /**
         * Simple conversion method.
         * @param attributeSet
         * @return Sax CDATA Attributes from the Swing MutableAttributeSet
         */
        private Attributes convertToSaxAttributes(
                                                  MutableAttributeSet attributeSet) {
            Object attrName, attrValue;

            attributes.clear();
            for(Enumeration en = attributeSet.getAttributeNames();
                en.hasMoreElements(); ) {
                attrName = en.nextElement();
                attrValue = attributeSet.getAttribute(attrName);
                attributes.addAttribute("", "", attrName.toString(),
                                        "CDATA", attrValue.toString());
            }

            return attributes;
        }

        /**
         * Log an error from the ContentHandler for raising post-parse
         */
        private void handleSAXException(SAXException e) {
            trace("SAX Error: " + e.getMessage());
            if (firstUnhandledException==null) {
                firstUnhandledException = e;
            }
        }
    }

}
