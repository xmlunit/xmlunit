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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * JUnit testcase for ElementNameAndTextQualifier
 * @see test_Diff#testRepeatedElementNamesWithTextQualification()
 */
public class test_ElementNameAndTextQualifier extends TestCase {
    private static final String TAG_NAME = "tagYoureIt";
    private static final String TEXT_A = "textA";
    private static final String TEXT_B = "textB";
    private Document document;
    private ElementNameAndTextQualifier elementNameAndTextQualifier;
        
    public void testSingleTextValue() throws Exception {
        Element control = document.createElement(TAG_NAME);
        control.appendChild(document.createTextNode(TEXT_A));

        Element test = document.createElement(TAG_NAME);
                
        assertFalse("control text not comparable to empty text", 
                    elementNameAndTextQualifier.qualifyForComparison(control, test));
                
        test.appendChild(document.createTextNode(TEXT_A));              
        assertTrue("control textA comparable to test textA",
                   elementNameAndTextQualifier.qualifyForComparison(control, test));
                                        
        test = document.createElement(TAG_NAME);

        test.appendChild(document.createTextNode(TEXT_B));
        assertFalse("control textA not comparable to test textB",
                    elementNameAndTextQualifier.qualifyForComparison(control, test));
    }
        
    public void testMultipleTextValues() throws Exception {
        Element control = document.createElement(TAG_NAME);
        control.appendChild(document.createTextNode(TEXT_A));
        control.appendChild(document.createTextNode(TEXT_B));

        Element test = document.createElement(TAG_NAME);
        test.appendChild(document.createTextNode(TEXT_A + TEXT_B));
        assertTrue("denormalised control text comparable to normalised test text",
                   elementNameAndTextQualifier.qualifyForComparison(control, test));
    }
        
    public void setUp() throws Exception {
        document = XMLUnit.newControlParser().newDocument();
        elementNameAndTextQualifier = new ElementNameAndTextQualifier();
    }

    /**
     * Constructor for test_ElementNameAndTextQualifier.
     */
    public test_ElementNameAndTextQualifier() {
        super();
    }

    /**
     * Constructor for test_ElementNameAndTextQualifier.
     * @param name
     */
    public test_ElementNameAndTextQualifier(String name) {
        super(name);
    }
        
    public static TestSuite suite() {
        return new TestSuite(test_ElementNameAndTextQualifier.class);
    }
}
