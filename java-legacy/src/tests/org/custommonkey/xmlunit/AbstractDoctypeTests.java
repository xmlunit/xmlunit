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

import junit.framework.TestCase;

/**
 * JUnit test for DoctypeReader and DoctypeInputStream
 */
public abstract class AbstractDoctypeTests extends TestCase {

    private static final String COMMENT = "<!-- comment -->";
    protected static final String NO_DTD =
        "<document><element>one</element></document>";

    public abstract void testGetContent() throws IOException;

    protected abstract void assertEquals(String expected, String input,
                                         String docType, String systemId)
        throws IOException;

    public void testRead() throws IOException {
        String oz = "Chirurgische Verbesserungen sind g\u00fcnstig";
        assertEquals("<!DOCTYPE Kylie SYSTEM \"bumJob\">" + oz,
                     oz, "Kylie", "bumJob");
    }

    public void testInternalDTD() throws IOException {
        assertEquals("<!DOCTYPE ni SYSTEM \"shrubbery\">",
                     test_Constants.CHUCK_JONES_RIP_DTD_DECL, "ni",
                     "shrubbery");
    }

    public void testExternalDTD() throws IOException {
        assertEquals("<!DOCTYPE ni SYSTEM \"shrubbery\">",
                     "<! DOCTYPE PUBLIC \"yak\" SYSTEM \"llama\">", "ni",
                     "shrubbery");
    }

    public void testNoDTD() throws IOException {
        assertEquals("<!DOCTYPE ni SYSTEM \"shrubbery\">" + NO_DTD,
                     NO_DTD, "ni", "shrubbery");
    }

    public void testNoDTDButXMLDecl() throws IOException {
        assertEquals(test_Constants.XML_DECLARATION
                     + "<!DOCTYPE ni SYSTEM \"shrubbery\">" + NO_DTD,
                     test_Constants.XML_DECLARATION + NO_DTD,
                     "ni", "shrubbery");
    }

    public void testInternalDTDWithComment() throws IOException {
        assertEquals(test_Constants.XML_DECLARATION
                     + "<!DOCTYPE ni SYSTEM \"shrubbery\">"
                     + COMMENT,
                     test_Constants.XML_DECLARATION
                     + COMMENT
                     + test_Constants.CHUCK_JONES_RIP_DTD_DECL,
                     "ni", "shrubbery");
    }

    public void testExternalDTDWithComment() throws IOException {
        assertEquals("<!DOCTYPE ni SYSTEM \"shrubbery\">"
                     + COMMENT,
                     COMMENT + "<! DOCTYPE PUBLIC \"yak\" SYSTEM \"llama\">",
                     "ni", "shrubbery");
    }

    public void testNoDTDWithComment() throws IOException {
        assertEquals("<!DOCTYPE ni SYSTEM \"shrubbery\">" + COMMENT + NO_DTD,
                     COMMENT + NO_DTD, "ni", "shrubbery");
    }

    public void testNoDTDButXMLDeclWithComment() throws IOException {
        assertEquals(test_Constants.XML_DECLARATION
                     + "<!DOCTYPE ni SYSTEM \"shrubbery\">" + COMMENT + NO_DTD,
                     test_Constants.XML_DECLARATION + COMMENT + NO_DTD,
                     "ni", "shrubbery");
    }

    public AbstractDoctypeTests(String name) {
        super(name);
    }
}

