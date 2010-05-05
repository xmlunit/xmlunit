/*
******************************************************************
Copyright (c) 2008, Jeff Martin, Tim Bacon
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

import net.sf.xmlunit.diff.ElementSelectors;
import org.w3c.dom.Element;
import org.custommonkey.xmlunit.ElementQualifier;

/**
 * Compares all Element and Text nodes in two pieces of XML. Allows elements of
 * complex, deeply nested types that are returned in different orders but have
 * the same content to be recognized as comparable.
 *
 * @author Frank Callahan 
 */
public class RecursiveElementNameAndTextQualifier implements ElementQualifier {

    /**
     * Uses element names and the text nested an arbitrary level of
     * child elements deeper into the element to compare
     * elements. Checks all nodes, not just first child element.
     * 
     * <p> Does not ignore empty text nodes.
     */
    public RecursiveElementNameAndTextQualifier() {
    }

    /**
     * Returns result of recursive comparison of all the nodes of a
     * control and test element.
     */
    public boolean qualifyForComparison(Element currentControl,
                                        Element currentTest) {
        return ElementSelectors.byNameAndTextRec.canBeCompared(currentControl,
                                                               currentTest);
    }
}
