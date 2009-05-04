/*
******************************************************************
Copyright (c) 2001-2008, Jeff Martin, Tim Bacon
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

import org.custommonkey.xmlunit.exceptions.ConfigurationException;
import org.custommonkey.xmlunit.exceptions.XpathException;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Simple class for accessing the Nodes matched by an Xpath expression, or
 * evaluating the String value of an Xpath expression.
 * Uses a <code>copy-of</code> or <code>value-of</code> XSL template (as
 * appropriate) to execute the Xpath.
 * This is not an efficient method for accessing XPaths but it is portable
 * across underlying transform implementations. (Yes I know Jaxen is too, but
 * this approach seemed to be the simplest thing that could possibly work...)
 * <br />Examples and more at <a href="http://xmlunit.sourceforge.net"/>xmlunit.sourceforge.net</a>
 */
public class SimpleXpathEngine implements XpathEngine, XSLTConstants {

    private NamespaceContext ctx = SimpleNamespaceContext.EMPTY_CONTEXT;

    /**
     * What every XSL transform needs
     * @return
     */
    private StringBuffer getXSLTBase() {
        StringBuffer result = new StringBuffer(XML_DECLARATION)
            .append(XMLUnit.getXSLTStart());
        String tmp = result.toString();
        int close = tmp.lastIndexOf('>');
        if (close == -1) {
            close = tmp.length();
        }
        result.insert(close, getNamespaceDeclarations());
        return result;
    }

    /**
     * @param select an xpath syntax <code>select</code> expression
     * @return the <code>copy-of</code> transformation
     */
    private String getCopyTransformation(String select) {
        return getXSLTBase()
            .append("<xsl:preserve-space elements=\"*\"/>")
            .append("<xsl:output method=\"xml\" version=\"1.0\" encoding=\"UTF-8\"/>")
            .append("<xsl:template match=\"/\">")
            .append("<xpathResult>")
            .append("<xsl:apply-templates select=\"").append(select)
            .append("\" mode=\"result\"/>")
            .append("</xpathResult>")
            .append("</xsl:template>")
            .append("<xsl:template match=\"*\" mode=\"result\">")
            .append("  <xsl:copy-of select=\".\"/>")
            .append("</xsl:template>")
            .append("</xsl:stylesheet>")
            .toString();
    }

    /**
     * @param select an xpath syntax <code>select</code> expression
     * @return the <code>value-of</code> transformation
     */
    private String getValueTransformation(String select) {
        return getXSLTBase()
            .append("<xsl:output method=\"text\"/>")
            .append("<xsl:template match=\"/\">")
            .append("  <xsl:value-of select=\"").append(select).append("\"/>")
            .append("</xsl:template>")
            .append("</xsl:stylesheet>")
            .toString();
    }

    /**
     * Perform the actual transformation work required
     * @param xslt
     * @param document
     * @param result
     * @throws XpathException
     * @throws TransformerException
     * @throws ConfigurationException
     */
    private void performTransform(String xslt, Document document,
                                  Result result)
        throws TransformerException, ConfigurationException, XpathException {
        try {
            StreamSource source = new StreamSource(new StringReader(xslt));
            TransformerFactory tf = XMLUnit.newTransformerFactory();
            ErrorListener el = new ErrorListener() {
                    public void error(TransformerException ex)
                        throws TransformerException {
                        // any error in our simple stylesheet must be fatal
                        throw ex;
                    }
                    public void fatalError(TransformerException ex)
                        throws TransformerException {
                        throw ex;
                    }
                    public void warning(TransformerException ex) {
                        // there shouldn't be any warning
                        ex.printStackTrace();
                    }
                };
            tf.setErrorListener(el);
            Transformer transformer = tf.newTransformer(source);
            // Issue 1985229 says Xalan-J 2.7.0 may return null for
            // illegal input
            if (transformer == null) {
                throw new XpathException("failed to obtain an XSLT transformer"
                                         + " for XPath expression.");
            }
            transformer.setErrorListener(el);
            transformer.transform(new DOMSource(document), result);
        } catch (javax.xml.transform.TransformerConfigurationException ex) {
            throw new ConfigurationException(ex);
        }
    }

    /**
     * Testable method to execute the copy-of transform and return the root
     * node of the resulting Document.
     * @param select
     * @param document
     * @throws ConfigurationException
     * @throws TransformerException
     * @return the root node of the Document created by the copy-of transform.
     */
    protected Node getXPathResultNode(String select, Document document)
        throws ConfigurationException, TransformerException, XpathException {
        return getXPathResultAsDocument(select, document).getDocumentElement();
    }

    /**
     * Execute the copy-of transform and return the resulting Document.
     * Used for XMLTestCase comparison
     * @param select
     * @param document
     * @throws ConfigurationException
     * @throws TransformerException
     * @return the Document created by the copy-of transform.
     */
    protected Document getXPathResultAsDocument(String select,
                                                Document document)
        throws ConfigurationException, TransformerException, XpathException {
        DOMResult result = new DOMResult();
        performTransform(getCopyTransformation(select), document, result);
        return (Document) result.getNode();
    }

    /**
     * Execute the specified xpath syntax <code>select</code> expression
     * on the specified document and return the list of nodes (could have
     * length zero) that match
     * @param select
     * @param document
     * @return list of matching nodes
     */
    public NodeList getMatchingNodes(String select, Document document)
        throws ConfigurationException, XpathException {
        try {
            return getXPathResultNode(select, document).getChildNodes();
        } catch (TransformerException ex) {
            throw new XpathException("Failed to apply stylesheet", ex);
        }
    }

    /**
     * Evaluate the result of executing the specified xpath syntax
     * <code>select</code> expression on the specified document
     * @param select
     * @param document
     * @return evaluated result
     */
    public String evaluate(String select, Document document)
        throws ConfigurationException, XpathException {
        try {
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            performTransform(getValueTransformation(select), document, result);
            return writer.toString();
        } catch (TransformerException ex) {
            throw new XpathException("Failed to apply stylesheet", ex);
        }
    }

    public void setNamespaceContext(NamespaceContext ctx) {
        this.ctx = ctx;
    }

    /**
     * returns namespace declarations for all namespaces known to the
     * current context.
     */
    private String getNamespaceDeclarations() {
        StringBuffer nsDecls = new StringBuffer();
        String quoteStyle = "'";
        for (Iterator keys = ctx.getPrefixes(); keys.hasNext(); ) {
            String prefix = (String) keys.next();
            String uri = ctx.getNamespaceURI(prefix);
            if (uri == null) {
                continue;
            }
            // this shouldn't have happened, but better safe than sorry
            if (prefix == null) {
                prefix = "";
            }

            if (uri.indexOf('\'') != -1) {
                quoteStyle = "\"";
            }
            nsDecls.append(' ').append(XMLNS_PREFIX);
            if (prefix.length() > 0) {
                nsDecls.append(':');
            }
            nsDecls.append(prefix).append('=')
                .append(quoteStyle).append(uri).append(quoteStyle)
                .append(' ');
        }
        return nsDecls.toString();
    }
}
