/*
******************************************************************
Copyright (c) 2007, Jeff Martin, Tim Bacon
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

import org.custommonkey.xmlunit.exceptions.XMLUnitRuntimeException;
import org.custommonkey.xmlunit.util.IntegerBuffer;

/**
 * Contains some common code for DoctypeReader and DoctypeInputStream.
 *
 * <p>When used with DoctypeInputStream it assumes that the whole
 * DOCTYPE declaration consists of US-ASCII characters.</p>
 */
final class DoctypeSupport {

    static interface Readable {
        int read() throws IOException;
    }

    final static String DOCTYPE_OPEN_DECL = "<!";
    final static String DOCTYPE_CLOSE_DECL = ">";
    final static String DOCTYPE = "DOCTYPE ";
    final static String SYSTEM = " SYSTEM \"";
    private final static int[] DOCTYPE_INTS = {
        'D', 'O', 'C', 'T', 'Y', 'P', 'E', ' '
    };

    private boolean hasSplit;
    private final Readable original;
    private Readable decl;
    private Readable beforeDoctype;
    private Readable afterDoctype;

    /**
     * Encapsulates a DOCTYPE declaration for the given name and system id.
     */
    DoctypeSupport(String name, String systemId, Readable original,
                   boolean characters, String encoding) {
        this.original = original;

        StringBuffer sb = new StringBuffer(DOCTYPE_OPEN_DECL);
        sb.append(DOCTYPE).append(name).append(SYSTEM)
            .append(systemId).append('\"').append(DOCTYPE_CLOSE_DECL);
        String s = sb.toString();
        IntegerBuffer buf =
            new IntegerBuffer(s.length() * (characters ? 1 : 2));

        if (characters) {
            char[] c = s.toCharArray();
            for (int i = 0; i < c.length; i++) {
                buf.append(c[i]);
            }
        } else {
            try {
                byte[] b = encoding == null
                    ? s.getBytes() : s.getBytes(encoding);
                for (int i = 0; i < b.length; i++) {
                    buf.append(b[i] & 0xFF);
                }
            } catch (java.io.UnsupportedEncodingException use) {
                throw new XMLUnitRuntimeException("Unsupported encoding", use);
            }
        }

        decl = new IntBufferReadable(buf);
    }

    /**
     * Reads the next character from the declaration.
     * @return -1 if the end of the declaration has been reached.
     */
    int read() throws IOException {
        int nextInt = -1;
        if (!hasSplit) {
            split();
        }
        if (beforeDoctype != null) {
            nextInt = beforeDoctype.read();
            if (nextInt == -1) {
                beforeDoctype = null;
            }
        }
        if (nextInt == -1 && decl != null) {
            nextInt = decl.read();
            if (nextInt == -1) {
                decl = null;
            }
        }
        if (nextInt == -1 && afterDoctype != null) {
            nextInt = afterDoctype.read();
            if (nextInt == -1) {
                afterDoctype = null;
            }
        }
        if (nextInt == -1) {
            nextInt = original.read();
        }
        return nextInt;
    }

    /**
     * Reads enough of the original Readable to know where to place
     * the declaration.  Fills beforeDecl and afterDecl from the data
     * read ahead.  Swallows the original DOCTYPE if there is one.
     */
    private void split() throws IOException {
        hasSplit = true;
        IntegerBuffer before = new IntegerBuffer();
        IntegerBuffer after = new IntegerBuffer();

        int current;
        boolean ready = false;
        boolean stillNeedToSeeDoctype = true;
        while (!ready && (current = original.read()) != -1) {
            if (Character.isWhitespace((char) current)) {
                before.append(current);
            } else if (current == '<') {
                // could be XML declaration, comment, PI, DOCTYPE
                // or the first element
                int[] elementOrDeclOr = readUntilCloseCharIsReached();
                if (elementOrDeclOr.length > 0) {
                    if (elementOrDeclOr[0] == '?') {
                        // XML declaration or PI
                        before.append('<');
                        before.append(elementOrDeclOr);
                    } else if (elementOrDeclOr[0] != '!') {
                        // first element
                        after.append('<');
                        after.append(elementOrDeclOr);
                        stillNeedToSeeDoctype = false;
                        ready = true;
                    } else {
                        // comment or doctype
                        IntegerBuffer b =
                            new IntegerBuffer(elementOrDeclOr.length);
                        b.append(elementOrDeclOr);
                        if (b.indexOf(DOCTYPE_INTS) == -1) {
                            after.append('<');
                            after.append(elementOrDeclOr);
                        } else {
                            // swallow old declaration
                            stillNeedToSeeDoctype = false;
                        }
                        ready = true;
                    }
                } else {
                    after.append('<');
                    stillNeedToSeeDoctype = false;
                    ready = true;
                }
            } else {
                after.append(current);
                stillNeedToSeeDoctype = false;
                ready = true;
            }
        }

        // need to eliminate original DOCTYPE if it exists
        while (stillNeedToSeeDoctype && (current = original.read()) != -1) {
            if (Character.isWhitespace((char) current)) {
                after.append(current);
            } else if (current == '<') {
                int[] elementOrDeclOr = readUntilCloseCharIsReached();
                if (elementOrDeclOr.length > 0) {
                    if (elementOrDeclOr[0] == '?') {
                        // PI
                        after.append('<');
                        after.append(elementOrDeclOr);
                    } else if (elementOrDeclOr[0] != '!') {
                        // first element
                        after.append('<');
                        after.append(elementOrDeclOr);
                        stillNeedToSeeDoctype = false;
                    } else {
                        // comment or doctype
                        IntegerBuffer b =
                            new IntegerBuffer(elementOrDeclOr.length);
                        b.append(elementOrDeclOr);
                        if (b.indexOf(DOCTYPE_INTS) == -1) {
                            after.append('<');
                            after.append(elementOrDeclOr);
                        } else {
                            // swallow old declaration
                            stillNeedToSeeDoctype = false;
                        }
                    }
                } else {
                    after.append('<');
                    stillNeedToSeeDoctype = false;
                }
            } else {
                after.append(current);
                stillNeedToSeeDoctype = false;
            }
        }

        beforeDoctype = before.size() > 0
            ? new IntBufferReadable(before) : null;
        afterDoctype = after.size() > 0
            ? new IntBufferReadable(after) : null;
    }

    private int[] readUntilCloseCharIsReached() throws IOException {
        IntegerBuffer i = new IntegerBuffer();
        int intRead = -1;
        int openCount = 1;
        while (openCount > 0 && (intRead = original.read()) != -1) {
            i.append(intRead);
            if (intRead == '<') {
                openCount++;
            }
            if (intRead == '>') {
                openCount--;
            }
        }
        return i.toIntArray();
    }
    
    private static class IntBufferReadable implements Readable {
        private final int[] buf;
        private int off;
        IntBufferReadable(IntegerBuffer b) {
            buf = b.toIntArray();
        }
        public int read() {
            return off >= buf.length ? -1 : buf[off++];
        }
    }

}
