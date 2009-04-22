/*
******************************************************************
Copyright (c) 200, Jeff Martin, Tim Bacon
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

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author TimBacon
 */
public class test_IgnoreTextAndAttributeValuesDifferenceListener
    extends TestCase {
    private DifferenceListener listener;
    public void testDifferenceFound() {
        assertCorrectInterpretation(
                                    DifferenceConstants.ATTR_NAME_NOT_FOUND,
                                    DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
                                    DifferenceConstants.ATTR_SEQUENCE,
                                    DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
                                    DifferenceConstants.ATTR_VALUE,
                                    DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_SIMILAR);
        assertCorrectInterpretation(
                                    DifferenceConstants.ATTR_VALUE_EXPLICITLY_SPECIFIED,
                                    DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_SIMILAR);
        assertCorrectInterpretation(
                                    DifferenceConstants.CDATA_VALUE,
                                    DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
                                    DifferenceConstants.CHILD_NODELIST_LENGTH,
                                    DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
                                    DifferenceConstants.CHILD_NODELIST_SEQUENCE,
                                    DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
                                    DifferenceConstants.COMMENT_VALUE,
                                    DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
                                    DifferenceConstants.DOCTYPE_NAME,
                                    DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
                                    DifferenceConstants.DOCTYPE_PUBLIC_ID,
                                    DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
                                    DifferenceConstants.DOCTYPE_SYSTEM_ID,
                                    DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
                                    DifferenceConstants.ELEMENT_NUM_ATTRIBUTES,
                                    DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
                                    DifferenceConstants.ELEMENT_TAG_NAME,
                                    DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
                                    DifferenceConstants.HAS_CHILD_NODES,
                                    DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
                                    DifferenceConstants.HAS_DOCTYPE_DECLARATION,
                                    DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
                                    DifferenceConstants.NAMESPACE_PREFIX,
                                    DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
                                    DifferenceConstants.NAMESPACE_URI,
                                    DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
                                    DifferenceConstants.NODE_TYPE,
                                    DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
                                    DifferenceConstants.PROCESSING_INSTRUCTION_DATA,
                                    DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
                                    DifferenceConstants.PROCESSING_INSTRUCTION_TARGET,
                                    DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
                                    DifferenceConstants.TEXT_VALUE,
                                    DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_SIMILAR);
    }
    
    private void assertCorrectInterpretation(
                                             Difference difference, int returnValue) {
        assertEquals(difference.toString(),
                     returnValue,
                     listener.differenceFound(difference));
    }
    
    public void testClassInUse() throws Exception {
        String control = "<clouds><cloud name=\"cumulus\" rain=\"maybe\">fluffy</cloud></clouds>";
        String similarTest = "<clouds><cloud name=\"cirrus\" rain=\"no\">wispy</cloud></clouds>";
        
        Diff diff = new Diff(control, similarTest);
        diff.overrideDifferenceListener(listener);
        assertTrue("similar " + diff.toString(), 
                   diff.similar());
        assertTrue("but not identical " + diff.toString(), 
                   !diff.identical());

        DetailedDiff detailedDiff = new DetailedDiff(
                                                     new Diff(control, similarTest));
        assertEquals("2 attribute and 1 text values", 
                     3, detailedDiff.getAllDifferences().size());

        String dissimilarTest = "<clouds><cloud name=\"nimbus\"/></clouds>";
        Diff dissimilarDiff = new Diff(control, dissimilarTest);
        dissimilarDiff.overrideDifferenceListener(listener);
        assertTrue("not similar " + dissimilarDiff.toString(),
                   !dissimilarDiff.similar()); 
            
        DetailedDiff dissimilarDetailedDiff = new DetailedDiff(
                                                               new Diff(control, dissimilarTest));
        dissimilarDetailedDiff.overrideDifferenceListener(listener);
        List differences = dissimilarDetailedDiff.getAllDifferences();
        assertEquals("wrong number of attributes, missing attribute, different attribute value, and missing text node. "
                     + dissimilarDetailedDiff.toString(), 
                     4, differences.size());
        int recoverable = 0;
        for (Iterator iter = differences.iterator(); iter.hasNext(); ) {
            Difference aDifference = (Difference) iter.next();
            if (aDifference.isRecoverable()) {
                recoverable++;
            }
        }
        assertEquals("attribute value difference has been overridden as similar",
                     1, recoverable);
    }

    public void testIssue771839() throws Exception {
        String xmlString1 = "<location>"
            + "<street-address>22 any street</street-address>"
            + "<postcode id='3'>XY0099Z</postcode>"
            + "</location>";
        String xmlString2 = "<location>"
            + "<postcode1 id='1'>EC3M 1EB</postcode1>"
            + "<street-address>20 east cheap</street-address>"
            + "</location>";

        Diff d = new Diff(xmlString1, xmlString2);
        d.overrideDifferenceListener(listener);
        assertFalse(d.similar());
        assertTrue("postcode was matched against postcode1",
                   d.toString().indexOf("Expected element tag name 'postcode'"
                                        + " but was 'postcode1'") > -1);
    }

    public void setUp() {
        listener = 
            new IgnoreTextAndAttributeValuesDifferenceListener();    
    }
    
    /**
     * Constructor for test_IgnoreTextAndAttributeValuesDifferenceListener.
     * @param name
     */
    public test_IgnoreTextAndAttributeValuesDifferenceListener(String name) {
        super(name);
    }
}
