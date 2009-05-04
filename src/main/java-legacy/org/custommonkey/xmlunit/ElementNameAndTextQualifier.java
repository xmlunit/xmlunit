/*
******************************************************************
Copyright (c) 2001, Jeff Martin, Tim Bacon
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

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * More complex interface implementation that tests two elements for tag name
 * and text content comparability. 
 * <br />Examples and more at 
 * <a href="http://xmlunit.sourceforge.net"/>xmlunit.sourceforge.net</a>
 * @see DifferenceEngine#compareNodeList(NodeList, NodeList, int, DifferenceListener, ElementQualifier)
 * @see Diff#overrideElementQualifier(ElementQualifier)
 */
public class ElementNameAndTextQualifier extends ElementNameQualifier {
    /**
     * Determine whether two elements qualify for further Difference comparison.
     * @param control
     * @param test
     * @return true if the two elements qualify for further comparison based on
     * both the superclass qualification (namespace URI and non- namespaced tag
     * name), and the qualification of the text nodes contained within the
     * elements; false otherwise
     */
    public boolean qualifyForComparison(Element control, Element test) {
        if (super.qualifyForComparison(control, test)) {
            return similar(extractText(control), extractText(test));
        }
        return false; 
    }
        
    /**
     * Determine whether the text nodes contain similar values
     * @param control
     * @param test
     * @return true if text nodes are similar, false otherwise
     */
    protected boolean similar(Text control, Text test) {                
        if (control == null) {
            return test == null;
        } else if (test == null) {
            return false;
        }
        return control.getNodeValue().equals(test.getNodeValue());
    }

    /**
     * Extract the normalized text from within an element
     * @param fromElement
     * @return extracted Text node (could be null)
     */ 
    protected Text extractText(Element fromElement) {
        fromElement.normalize();
        NodeList fromNodeList = fromElement.getChildNodes(); 
        Node currentNode;
        for (int i=0; i < fromNodeList.getLength(); ++i) {
            currentNode = fromNodeList.item(i);
            if (currentNode.getNodeType() == Node.TEXT_NODE) {
                return (Text) currentNode;
            }
        }
        return null;
    }                   

}
