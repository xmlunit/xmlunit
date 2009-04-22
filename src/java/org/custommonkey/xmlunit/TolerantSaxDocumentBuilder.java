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

package org.custommonkey.xmlunit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Uses Sax events from the <code>ContentHandler</code> and
 * <code>LexicalHandler</code> interfaces to build a DOM document in a tolerant
 * fashion -- it can cope with start tags without end tags, and end tags without
 * start tags for example.
 * Although this subverts the idea of XML being well-formed, it is intended
 * for use with HTML pages so that they can be transformed into DOM
 * trees, without being XHTML to start with.
 * Note that this class currently does not handle entity, DTD or CDATA tags.
 * <br />Examples and more at <a href="http://xmlunit.sourceforge.net"/>xmlunit.sourceforge.net</a>
 * @see HTMLDocumentBuilder#parse
 */
public class TolerantSaxDocumentBuilder
    extends DefaultHandler implements LexicalHandler {
    private final DocumentBuilder documentBuilder;
    private final StringBuffer traceBuffer;
    private Document currentDocument;
    private Element currentElement;

    /**
     * Constructor for specific JAXP parser
     * @param documentBuilder the JAXP parser to use to construct an empty
     *  DOM document that will be built up with SAX calls
     * @throws ParserConfigurationException
     */
    public TolerantSaxDocumentBuilder(DocumentBuilder documentBuilder)
        throws ParserConfigurationException {
        this.documentBuilder = documentBuilder;
        this.traceBuffer = new StringBuffer();
    }

    /**
     * @return the Document built up through the Sax calls
     */
    public Document getDocument() {
        return currentDocument;
    }

    /**
     * @return the trace of Sax calls that were used to build up the Document
     */
    public String getTrace() {
        return traceBuffer.toString();
    }

    /**
     * ContentHandler method
     * @throws SAXException
     */
    public void startDocument() throws SAXException {
        traceBuffer.delete(0, traceBuffer.length());
        trace("startDocument");
        currentDocument = documentBuilder.newDocument();
        currentElement = null;
    }

    /**
     * ContentHandler method
     * @throws SAXException
     */
    public void endDocument() throws SAXException {
        trace("endDocument");
    }

    /**
     * ContentHandler method.
     */
    public void characters(char[] data, int start, int length) {
        if (length >= 0)  {
            String characterData = new String(data, start, length);
            trace("characters:" + characterData);
            if (currentElement == null) {
                warn("Can't append text node to null currentElement");
            } else {
                Text textNode = currentDocument.createTextNode(characterData);
                currentElement.appendChild(textNode);
            }
        } else {
            warn("characters called with negative length");
        }
    }

    /**
     * ContentHandler method
     * @throws SAXException
     */
    public void startElement(String namespaceURI, String localName,
                             String qName, Attributes atts) throws SAXException {
        trace("startElement:" + localName + "~" + qName);
        Element newElement = createElement(namespaceURI, qName, atts);
        appendNode(newElement);
        currentElement = newElement;
    }

    /**
     * ContentHandler method
     * @throws SAXException
     */
    public void endElement(String namespaceURI, String localName,
                           String qName) throws SAXException {
        trace("endElement:" + localName + "~" + qName);
        if (currentElement==null) {
            warn(qName + ": endElement before any startElement");
            return;
        }

        Node parentNode = null;
        boolean atDocumentRoot = false, foundTagToEnd = false;
        Element startElement = currentElement;

        while (!(foundTagToEnd || atDocumentRoot)) {
            parentNode = currentElement.getParentNode();

            if (parentNode.getNodeType()==Node.ELEMENT_NODE) {
                foundTagToEnd = isElementMatching(currentElement, qName);
                currentElement = (Element) parentNode;
            } else if (parentNode.getNodeType()==Node.DOCUMENT_NODE) {
                atDocumentRoot = true;
                if (startElement==currentDocument.getDocumentElement()) {
                    foundTagToEnd = isElementMatching(startElement, qName);
                } else {
                    currentElement = startElement;
                }
            } else {
                throw new IllegalArgumentException("Closing element " + qName
                                                   + ": expecting a parent ELEMENT_NODE but found " + parentNode);
            }
        }
        if (!foundTagToEnd) {
            warn(qName + ": endElement does not match startElement!");
        }
    }

    private boolean isElementMatching(Element anElement, String qname) {
        return anElement.getNodeName()!=null
            && anElement.getNodeName().equals(qname);
    }

    /**
     * Unhandled ContentHandler method
     * @throws SAXException
     */
    public void endPrefixMapping(String prefix) throws SAXException {
        unhandled("endPrefixMapping");
    }

    /**
     * Unhandled ContentHandler method
     * @throws SAXException
     */
    public void ignorableWhitespace (char ch[], int start, int length)
        throws SAXException {
        unhandled("ignorableWhitespace");
    }

    /**
     * ContentHandler method
     * @throws SAXException
     */
    public void processingInstruction(String target, String data)
        throws SAXException {
        trace("processingInstruction");
        ProcessingInstruction instruction =
            currentDocument.createProcessingInstruction(target, data);
        appendNode(instruction);
    }

    /**
     * Unhandled ContentHandler method
     */
    public void setDocumentLocator (Locator locator) {
        unhandled("setDocumentLocator");
    }

    /**
     * Unhandled ContentHandler method
     * @throws SAXException
     */
    public void skippedEntity (String name) throws SAXException {
        unhandled("skippedEntity");
    }

    /**
     * Unhandled ContentHandler method
     * @throws SAXException
     */
    public void startPrefixMapping (String prefix, String uri)
        throws SAXException {
        unhandled("startPrefixMapping");
    }

    /**
     * Unhandled LexicalHandler method.
     * DOM currently doesn't allow DTD to be retrofitted onto a Document.
     * @throws SAXException
     */
    public void startDTD (String name, String publicId,
                          String systemId) throws SAXException {
        unhandled("startDTD");
    }

    /**
     * Unhandled LexicalHandler method
     * @throws SAXException
     */
    public void endDTD ()
        throws SAXException {
        unhandled("endDTD");
    }

    /**
     * Unhandled LexicalHandler method
     * @throws SAXException
     */
    public void startEntity (String name)
        throws SAXException {
        unhandled("startEntity");
    }

    /**
     * Unhandled LexicalHandler method
     * @throws SAXException
     */
    public void endEntity (String name)
        throws SAXException {
        unhandled("endEntity");
    }

    /**
     * Unhandled LexicalHandler method
     * @throws SAXException
     */
    public void startCDATA ()
        throws SAXException {
        unhandled("startCDATA");
    }

    /**
     * Unhandled LexicalHandler method
     * @throws SAXException
     */
    public void endCDATA ()
        throws SAXException {
        unhandled("endCDATA");
    }

    /**
     * LexicalHandler method
     * @throws SAXException
     */
    public void comment(char ch[], int start, int length)
        throws SAXException     {
        String commentText = new String(ch, start, length);
        trace("comment:" + commentText);
        Comment comment = currentDocument.createComment(commentText);
        appendNode(comment);
    }

    /**
     * Log an unhandled ContentHandler or LexicalHandler method
     * @param method
     */
    private void unhandled(String method) {
        trace("Unhandled callback: " + method);
    }

    /**
     * Log a warning about badly formed markup
     * @param msg
     */
    private void warn(String msg) {
        trace("WARNING: " + msg);
    }

    /**
     * Log a handled ContentHandler or LexicalHandler method
     * for tracing / debug purposes
     * @param method
     */
    private void trace(String method) {
        traceBuffer.append(method).append('\n');
    }

    /**
     * Create a DOM Element for insertion into the current document
     * @param namespaceURI
     * @param qName
     * @param attributes
     * @return the created Element
     */
    private Element createElement(String namespaceURI, String qName,
                                  Attributes attributes) {
        Element newElement = currentDocument.createElement(qName);

        if (namespaceURI != null && namespaceURI.length() > 0) {
            newElement.setPrefix(namespaceURI);
        }

        for(int i = 0; attributes != null && i < attributes.getLength(); ++i) {
            newElement.setAttribute(attributes.getQName(i),
                                    attributes.getValue(i));
        }

        return newElement;
    }

    /**
     * Append a node to the current document or the current element in the document
     * @param appendNode
     */
    private void appendNode(Node appendNode) {
        if (currentElement==null) {
            currentDocument.appendChild(appendNode);
        } else {
            currentElement.appendChild(appendNode);
        }
    }
}

