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

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.custommonkey.xmlunit.ElementNameAndTextQualifier;
import org.custommonkey.xmlunit.ElementNameQualifier;
import org.custommonkey.xmlunit.ElementQualifier;

/**
 * Per popular request an interface implementation that uses element
 * names and the text node containes in the n'th child node to compare
 * elements.
 *
 * <p>This means {@link ElementNameAndTextQualifier
 * ElementNameQualifier} and MultiLevelElementNameQualifier(1) should
 * lead to the same results.</p>
 *
 * <p>Any attribute values are completely ignored.  Only works on
 * elements with exactly one child element at each level.</p>
 *
 * <p>This class mostly exists as an example for custom ElementQualifiers.</p>
 */
public class MultiLevelElementNameAndTextQualifier
    implements ElementQualifier {

    private final int levels;
    private final boolean ignoreEmptyTexts;

    private static final ElementNameQualifier NAME_QUALIFIER =
        new ElementNameQualifier();
    private static final ElementNameAndTextQualifier NAME_AND_TEXT_QUALIFIER =
        new ElementNameAndTextQualifier();

    /**
     * Uses element names and the text nested <code>levels</code>
     * child elements deeper into the element to compare elements.
     *
     * <p>Does not ignore empty text nodes.
     */
    public MultiLevelElementNameAndTextQualifier(int levels) {
        this(levels, false);
    }

    /**
     * Uses element names and the text nested <code>levels</code>
     * child elements deeper into the element to compare elements.
     *
     * @param ignoreEmptyTexts whether whitespace-only textnodes
     * should be ignored.
     */
    public MultiLevelElementNameAndTextQualifier(int levels,
                                                 boolean ignoreEmptyTexts) {
        if (levels < 1) {
            throw new IllegalArgumentException("levels must be equal or"
                                               + " greater than one");
        }
        this.levels = levels;
        this.ignoreEmptyTexts = ignoreEmptyTexts;
    }

    public boolean qualifyForComparison(Element control, Element test) {
        boolean stillSimilar = true;
        Element currentControl = control;
        Element currentTest = test;

        // match on element names only for leading levels
        for (int currentLevel = 0; stillSimilar && currentLevel <= levels - 2;
             currentLevel++) {
            stillSimilar = NAME_QUALIFIER.qualifyForComparison(currentControl,
                                                               currentTest);

            if (stillSimilar) {
                if (currentControl.hasChildNodes()
                    && currentTest.hasChildNodes()) {
                    Node n1 = getFirstEligibleChild(currentControl);
                    Node n2 = getFirstEligibleChild(currentTest);
                    if (n1.getNodeType() == Node.ELEMENT_NODE
                        && n2.getNodeType() == Node.ELEMENT_NODE) {
                        currentControl = (Element) n1;
                        currentTest = (Element) n2;
                    } else {
                        stillSimilar = false;
                    }                        
                } else {
                    stillSimilar = false;
                }
            }
        }

        // finally compare the level containing the text child node
        if (stillSimilar) {
            stillSimilar = NAME_AND_TEXT_QUALIFIER
                .qualifyForComparison(currentControl, currentTest);
        }

        return stillSimilar;
    }

    private Node getFirstEligibleChild(Node parent) {
        Node n1 = parent.getFirstChild();
        if (ignoreEmptyTexts) {
            while (n1.getNodeType() == Node.TEXT_NODE
                   && n1.getNodeValue().trim().length() == 0) {
                Node n2 = n1.getNextSibling();
                if (n2 == null) break;
                n1 = n2;
            }
        }
        return n1;
    }
}
