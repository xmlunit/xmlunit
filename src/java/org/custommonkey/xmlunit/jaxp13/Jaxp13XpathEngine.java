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

package org.custommonkey.xmlunit.jaxp13;

import org.custommonkey.xmlunit.NamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.custommonkey.xmlunit.exceptions.ConfigurationException;
import org.custommonkey.xmlunit.exceptions.XpathException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * XPath engine based on javax.xml.xpath.
 */
public class Jaxp13XpathEngine implements XpathEngine {

    private final XPath xpath;

    public Jaxp13XpathEngine() throws ConfigurationException {
        try {
            XPathFactory f = null;
            if (XMLUnit.getXPathFactory() != null) {
                f = (XPathFactory) Class.forName(XMLUnit.getXPathFactory())
                    .newInstance();
            } else {
                f = XPathFactory.newInstance();
            }

            xpath = f.newXPath();
        } catch (Exception ex) {
            throw new ConfigurationException(ex);
        }
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
        throws XpathException {
        try {
            return (NodeList) xpath.evaluate(select, document,
                                             XPathConstants.NODESET);
        } catch (XPathExpressionException ex) {
            throw new XpathException(ex);
        }
    }
    
    /**
     * Evaluate the result of executing the specified xpath syntax
     * <code>select</code> expression on the specified document
     * @param select
     * @param document
     * @return evaluated result
     * @throws TransformerException
     * @throws TransformerConfigurationException
     */
    public String evaluate(String select, Document document)
        throws XpathException {
        try {
            return xpath.evaluate(select, document);
        } catch (XPathExpressionException ex) {
            throw new XpathException(ex);
        }
    }

    public void setNamespaceContext(NamespaceContext ctx) {
        xpath.setNamespaceContext(new XMLUnitNamespaceContext2Jaxp13(ctx));
    }
}
