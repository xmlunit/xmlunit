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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * JUnit test for DoctypeInputStream
 */
public class test_DoctypeInputStream extends AbstractDoctypeTests {

    private File testFile;

    public void tearDown() {
        if (testFile != null) {
            testFile.delete();
        }
    }

    private FileInputStream testDocument(String content)
        throws IOException {
        testFile = File.createTempFile("xmlunit_", ".xml");
        FileOutputStream fos = new FileOutputStream(testFile);
        OutputStreamWriter w = new OutputStreamWriter(fos, "ISO-8859-1");
        w.write(content);
        w.close();

        return new FileInputStream(testFile);
    }

    private static String readFully(DoctypeInputStream dis)
        throws IOException {
        StringBuffer buf = new StringBuffer();
        char[] ch = new char[1024];
        int numChars;
        InputStreamReader reader =
            new InputStreamReader(dis, "ISO-8859-1");
        while ((numChars = reader.read(ch))!=-1) {
            buf.append(ch, 0, numChars);
        }
        return buf.toString();
    }

    protected void assertEquals(String expected, String input, String docType,
                                String systemId) throws IOException {
        FileInputStream fis = null;
        try {
            fis = testDocument(input);
            DoctypeInputStream doctypeInputStream =
                new DoctypeInputStream(fis, "ISO-8859-1", docType, systemId);

            assertEquals(expected, readFully(doctypeInputStream));
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }

    public void testGetContent() throws IOException {
        String source = "WooPDeDoO!\nGooRanga!\n plIng! ";
        DoctypeInputStream dis =
            new DoctypeInputStream(new java.io.StringBufferInputStream(source),
                                   null, "nonsense", "words");
        assertEquals(source, dis.getContent(null));
        // can get content indefinitely from this stream
        assertEquals(source, dis.getContent("UTF-8"));
    }

    public test_DoctypeInputStream(String name) {
        super(name);
    }
}

