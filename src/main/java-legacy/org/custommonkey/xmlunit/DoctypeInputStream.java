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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * Adapts the marked-up content in a source InputStream to specify that it
 * conforms to a different DTD.
 * Combines InputStream semantics with the ability to specify a target doctype
 * for a byte stream containing XML markup.
 * Used by Validator class to wrap an InputStrea, when performing validation of a
 * document against a DTD.
 * <br />Examples and more at <a href="http://xmlunit.sourceforge.net"/>xmlunit.sourceforge.net</a>
 */
public class DoctypeInputStream extends InputStream {

    private final ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
    private final InputStream wrappedStream;
    private final DoctypeSupport support;

    /**
     * Create an InputStream whose XML content is provided by the
     * originalSource with the exception of the DOCTYPE which is
     * provided by the doctypeName and systemID.
     * @param originalSource
     * @param doctypeName
     * @param systemID
     */
    public DoctypeInputStream(InputStream originalSource, String encoding,
                              String doctypeName, String systemID) {
        wrappedStream = originalSource instanceof BufferedInputStream
            ? originalSource : new BufferedInputStream(originalSource);
        support =
            new DoctypeSupport(doctypeName, systemID,
                               new DoctypeSupport.Readable() {
                                   public int read() throws IOException {
                                       return wrappedStream.read();
                                   }
                               },
                               false, encoding);
    }

    /**
     * @return the content of the original source, without amendments or
     *  substitutions. Safe to call multiple times.
     * @throws IOException if thrown while reading from the original source
     */
    protected String getContent(String encoding) throws IOException {
        if (baos.size() == 0) {
            byte[] buffer = new byte[8192];
            int bytesRead = -1;
            while ((bytesRead = wrappedStream.read(buffer)) > -1) {
                baos.write(buffer, 0, bytesRead);
            }
        }
        return encoding == null ? baos.toString() : baos.toString(encoding);
    }

    /**
     * Read DOCTYPE-replaced content from the wrapped InputStream
     */
    public int read() throws IOException {
        return support.read();
    }

    public void close() throws IOException {
        wrappedStream.close();
    }
}
