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

/**
 * Performs replacement of one String by another String
 *  within one or more Strings.
 * This was once required but a code refactoring made it redundant and I don't have
 *  the heart to kill it off...!
 */
public class Replacement {
    private final char[] ofChars;
    private final char[] byChars;

    public Replacement(String ofString, String byString) {
        this(ofString.toCharArray(),
             byString == null ? null : byString.toCharArray());
    }

    public Replacement(char[] ofChars, char[] byChars) {
        this.ofChars = ofChars;
        this.byChars = byChars;
    }

    public final String replace(String inString) {
        StringBuffer buf =  replaceAndAppend(inString.toCharArray(),
                                             new StringBuffer(inString.length()));

        return buf.toString();
    }

    public final char[] replace(char[] inChars) {
        StringBuffer buf = replaceAndAppend(inChars,
                                            new StringBuffer(inChars.length));

        char[] replacement = new char[buf.length()];
        buf.getChars(0, buf.length(), replacement, 0);

        return replacement;
    }

    public final StringBuffer replaceAndAppend(char[] inChars,
                                               StringBuffer toBuffer) {
        int ofPos = 0;
        int falseStartPos = -1;
        for (int i=0; i < inChars.length; ++i) {
            if (inChars[i] == ofChars[ofPos]) {
                if (falseStartPos == -1) {
                    falseStartPos = i;
                }
                ++ofPos;
            } else {
                ofPos = 0;
                if (falseStartPos != -1) {
                    for (; falseStartPos < i; ++falseStartPos) {
                        toBuffer.append(inChars[falseStartPos]);
                    }
                    falseStartPos = -1;
                }
                toBuffer.append(inChars[i]);
            }

            if (ofPos == ofChars.length) {
                if (hasReplaceBy()) {
                    toBuffer.append(byChars);
                }
                ofPos = 0;
                falseStartPos = -1;
            }
        }

        return toBuffer;
    }

    public boolean hasReplaceBy() {
        return byChars != null && byChars.length > 0;
    }
}
