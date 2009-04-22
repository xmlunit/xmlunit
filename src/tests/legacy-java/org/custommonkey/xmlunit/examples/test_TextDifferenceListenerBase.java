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

import junit.framework.TestCase;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceListener;
import org.w3c.dom.Node;

public class test_TextDifferenceListenerBase extends TestCase {
    private static final String C_ATTR = "controlAttr";
    private static final String T_ATTR = "testAttr";
    private static final String C_CDATA = "controlCdata";
    private static final String T_CDATA = "testCdata";
    private static final String C_CMMT = "controlComment";
    private static final String T_CMMT = "testComment";
    private static final String C_TEXT = "controlText";
    private static final String T_TEXT = "testText";

    public void testTextDifferenceDelegations() throws Exception {
        final int[] invocations = new int[4];

        String control = getDoc(C_ATTR, C_CDATA, C_CMMT, C_TEXT);
        String test = getDoc(T_ATTR, T_CDATA, T_CMMT, T_TEXT);

        TextDifferenceListenerBase b = new TextDifferenceListenerBase(null) {
                protected int attributeDifference(Difference d) {
                    assertEquals(C_ATTR, d.getControlNodeDetail().getValue());
                    assertEquals(T_ATTR, d.getTestNodeDetail().getValue());
                    invocations[0]++;
                    return 1;
                }

                protected int cdataDifference(Difference d) {
                    assertEquals(C_CDATA, d.getControlNodeDetail().getValue());
                    assertEquals(T_CDATA, d.getTestNodeDetail().getValue());
                    invocations[1]++;
                    return 1;
                }

                protected int commentDifference(Difference d) {
                    assertEquals(C_CMMT, d.getControlNodeDetail().getValue());
                    assertEquals(T_CMMT, d.getTestNodeDetail().getValue());
                    invocations[2]++;
                    return 1;
                }

                protected int textDifference(Difference d) {
                    assertEquals(C_TEXT, d.getControlNodeDetail().getValue());
                    assertEquals(T_TEXT, d.getTestNodeDetail().getValue());
                    invocations[3]++;
                    return 1;
                }
            };

        Diff d = new Diff(control, test);
        d.overrideDifferenceListener(b);

        assertTrue(d.identical());

        for (int i = 0; i < invocations.length; i++) {
            assertEquals(1, invocations[i]);
        }
    }

    public void testTextualDifference() throws Exception {
        final int[] invocations = new int[1];

        String control = getDoc(C_ATTR, C_CDATA, C_CMMT, C_TEXT);
        String test = getDoc(T_ATTR, T_CDATA, T_CMMT, T_TEXT);

        TextDifferenceListenerBase b = new TextDifferenceListenerBase(null) {
                protected int textualDifference(Difference d) {
                    invocations[0]++;
                    return 1;
                }
            };

        Diff d = new Diff(control, test);
        d.overrideDifferenceListener(b);

        assertTrue(d.identical());
        assertEquals(4, invocations[0]);
    }

    public void testFullDelegation() throws Exception {
        final int[] invocations = new int[1];

        String control = getDoc(C_ATTR, C_CDATA, C_CMMT, C_TEXT);
        String test = getDoc(T_ATTR, T_CDATA, T_CMMT, T_TEXT);

        TextDifferenceListenerBase b =
            new TextDifferenceListenerBase(new DifferenceListener() {
                    public int differenceFound(Difference d) {
                        invocations[0]++;
                        return 1;
                    }
                    public void skippedComparison(Node c, Node t) {
                        fail("skippedComparison shouldn't get invoked");
                    }
                }) {};

        Diff d = new Diff(control, test);
        d.overrideDifferenceListener(b);

        assertTrue(d.identical());
        assertEquals(4, invocations[0]);
    }

    private static String getDoc(String attr, String cdata, String comment,
                                 String text) {
        return "<root><first attr=\"" + attr + "\"/><!--" + comment + "-->"
            + "<second><![CDATA[" + cdata + "]]></second><third>" + text
            + "</third></root>";
    }
}
