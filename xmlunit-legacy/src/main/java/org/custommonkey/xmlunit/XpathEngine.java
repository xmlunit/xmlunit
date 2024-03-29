/*
******************************************************************
Copyright (c) 2006-2007,2015,2022 Jeff Martin, Tim Bacon
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

import org.custommonkey.xmlunit.exceptions.XpathException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * Abstraction of an engine evaluating XPath expressions.
 */
public interface XpathEngine {

    /**
     * Execute the specified xpath syntax <code>select</code> expression
     * on the specified document and return the list of nodes (could have
     * length zero) that match
     * @param select the XPath expression
     * @param document the XML source to apply the expression to
     * @return matching nodes
     * @throws XpathException if the underlying implementation does
     */
    NodeList getMatchingNodes(String select, Document document)
        throws XpathException;

    /**
     * Evaluate the result of executing the specified xpath syntax
     * <code>select</code> expression on the specified document
     * @param select the XPath expression
     * @param document the XML source to apply the expression to
     * @return evaluated result
     * @throws XpathException if the underlying implementation does
     */
    String evaluate(String select, Document document)
        throws XpathException;

    /**
     * Establish a namespace context.
     * @param ctx the NamespaceContext
     */
    void setNamespaceContext(NamespaceContext ctx);
}
