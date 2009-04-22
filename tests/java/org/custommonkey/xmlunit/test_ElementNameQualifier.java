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
 * JUnit testcase for ElementNameEqualifier
 */
public class test_ElementNameQualifier extends TestCase {
    private Document document;
    private ElementNameQualifier elementNameQualifier;
    private static final String NAME_A = "nameA";
    private static final String NAME_B = "nameB";
        
    public void testElementsNoNamespace() throws Exception {
        Element control = document.createElement(NAME_A);
        Element test = document.createElement(NAME_A);
        assertTrue("nameA comparable to nameA", elementNameQualifier.qualifyForComparison(control, test));
                
        test = document.createElement(NAME_B);
        assertFalse("nameA not comparable to nameB", elementNameQualifier.qualifyForComparison(control, test));
    }
        
    public void testElementsWithNamespace() throws Exception {
        String anURI = "gopher://example.com";
        String qnameQualifierA = "qnq:";
                
        Element control = document.createElementNS(anURI, qnameQualifierA + NAME_A);
        Element test = document.createElementNS(anURI, qnameQualifierA + NAME_A);
        assertTrue("qualified nameA comparable to nameA", elementNameQualifier.qualifyForComparison(control, test));

        test = document.createElementNS(anURI, qnameQualifierA + NAME_B);
        assertFalse("qualified nameA not comparable to nameB", elementNameQualifier.qualifyForComparison(control, test));
                
        String qnameQualifierB = "pgp:";
        test = document.createElementNS(anURI, qnameQualifierB + NAME_A);
        assertTrue("qualified nameA comparable to requalified nameA", elementNameQualifier.qualifyForComparison(control, test));

        test = document.createElementNS(anURI, qnameQualifierB + NAME_B);
        assertFalse("qualified nameA not comparable to requalifiednameB", 
                    elementNameQualifier.qualifyForComparison(control, test));

        String anotherURI = "ftp://example.com";
        test = document.createElementNS(anotherURI, qnameQualifierA + NAME_A);
        assertFalse("qualified nameA not comparable to anotherURI nameA", 
                    elementNameQualifier.qualifyForComparison(control, test));
                
        test = document.createElementNS(anotherURI, qnameQualifierB + NAME_A);
        assertFalse("qualified nameA comparable to requalified-anotherURI nameA",
                    elementNameQualifier.qualifyForComparison(control, test));

        test = document.createElementNS(anotherURI, qnameQualifierB + NAME_B);
        assertFalse("qualified nameA not comparable to requalified-anotherURI nameB",
                    elementNameQualifier.qualifyForComparison(control, test));
    }
                
                
    public void setUp() throws Exception {
        document = XMLUnit.newControlParser().newDocument();
        elementNameQualifier = new ElementNameQualifier();
    }

    public static TestSuite suite() {
        return new TestSuite(test_ElementNameQualifier.class);
    }
        
    public test_ElementNameQualifier(String name) {
        super(name);
    }

}
