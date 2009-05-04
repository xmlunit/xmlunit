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

import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * JUnit test for DoctypeReader
 */
public class test_DoctypeReader extends AbstractDoctypeTests {
    private DoctypeReader doctypeReader;
    private StringReader sourceReader;
    private static final String NEWLINE = System.getProperty("line.separator");

    public void testGetContent() throws IOException {
        String source = "WooPDeDoO!" + NEWLINE + "GooRanga!"
            + NEWLINE + " plIng! ";
        sourceReader = new StringReader(source);
        doctypeReader = new DoctypeReader(sourceReader,
                                          "nonsense", "words");
        assertEquals(source, doctypeReader.getContent());
        // can get content indefinitely from this reader
        assertEquals(source, doctypeReader.getContent());
    }

    private void initDummyDoctypeReader() {
        sourceReader = new StringReader("yabba");
        doctypeReader = new DoctypeReader(sourceReader,
                                          "yabba", "don\'t");
    }

    public void testReplaceDoctypeInternalDTD() {
        initDummyDoctypeReader();
        StringBuffer buf = new StringBuffer(test_Constants.CHUCK_JONES_RIP_DTD_DECL);
        assertEquals("<!DOCTYPE ni SYSTEM \"shrubbery\">",
                     doctypeReader.replaceDoctype(buf, "ni", "shrubbery"));
    }

    public void testReplaceDoctypeExternalDTD() {
        initDummyDoctypeReader();
        StringBuffer buf = new StringBuffer(
                                            "<! DOCTYPE PUBLIC \"yak\" SYSTEM \"llama\">");
        assertEquals("<! DOCTYPE ni SYSTEM \"shrubbery\">",
                     doctypeReader.replaceDoctype(buf, "ni", "shrubbery"));
    }

    public void testReplaceDoctypeNoDTD() {
        initDummyDoctypeReader();
        StringBuffer buf = new StringBuffer(NO_DTD);
        assertEquals("<!DOCTYPE ni SYSTEM \"shrubbery\">" + NO_DTD,
                     doctypeReader.replaceDoctype(buf, "ni", "shrubbery"));
    }

    public void testReplaceDoctypeNoDTDButXMLDecl() {
        initDummyDoctypeReader();
        StringBuffer buf = new StringBuffer(test_Constants.XML_DECLARATION
                                            + NO_DTD);
        assertEquals(test_Constants.XML_DECLARATION +
                     "<!DOCTYPE ni SYSTEM \"shrubbery\">" + NO_DTD,
                     doctypeReader.replaceDoctype(buf, "ni", "shrubbery"));
    }

    private static String readFully(DoctypeReader reader)
        throws IOException {
        StringBuffer buf = new StringBuffer();
        char[] ch = new char[1024];
        int numChars;
        while ((numChars = reader.read(ch))!=-1) {
            buf.append(ch, 0, numChars);
        }
        return buf.toString();
    }

    protected void assertEquals(String expected, String input, String docType,
                                String systemId) throws IOException {
        DoctypeReader doctypeReader =
            new DoctypeReader(new StringReader(expected), docType,
                                   systemId);
        assertEquals(expected, readFully(doctypeReader));
    }

    public test_DoctypeReader(String name) {
        super(name);
    }
}

