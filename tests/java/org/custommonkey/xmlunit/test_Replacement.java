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


import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * JUnit test for Replacement
 */
public class test_Replacement extends TestCase {
    private Replacement replacement;

    public void testCharReplacement() {
        char[] ch = {'h','o','a','g'};
        replacement = new Replacement(new char[] {'o','a'}, new char[] {'u'});
        assertEquals("hug", new String(replacement.replace(ch)));

        replacement = new Replacement(new char[] {'g'}, new char[] {'r', 's', 'e'});
        assertEquals("hoarse", new String(replacement.replace(ch)));
        assertEquals("hasReplaceBy", true, replacement.hasReplaceBy());
    }

    public void testSimpleString() {
        replacement = new Replacement("x", "y");
        // 1st char
        assertEquals("yen", replacement.replace("xen"));
        // last char
        assertEquals("boy", replacement.replace("box"));
        // not 1st or last
        assertEquals("aye", replacement.replace("axe"));
        // no replacement
        assertEquals("bag", replacement.replace("bag"));
        // multiple replacements
        assertEquals("yoyo", replacement.replace("xoxo"));
        // multiple concurrent replacements
        assertEquals("yyjykyy", replacement.replace("xxjxkxx"));

        assertEquals("hasReplaceBy", true, replacement.hasReplaceBy());
    }

    public void testComplexString() {
        replacement = new Replacement(" a whole bunch of words",
                                      "some other words altogether");
        assertEquals("Here aresome other words altogether...",
                     replacement.replace("Here are a whole bunch of words..."));
        
        replacement = new Replacement("pp", "p");
        assertEquals("hapy", replacement.replace("happy"));
        assertEquals("happy", replacement.replace("happppy"));
        assertEquals("tap", replacement.replace("tapp"));
        assertEquals("paw", replacement.replace("ppaw"));
    }

    public void testNullReplacebyString() {
        replacement = new Replacement(" ", null);
        assertEquals("hasReplaceBy", false, replacement.hasReplaceBy());
        assertEquals("wedon'twantnowhitespace",
                     replacement.replace("we don't want no whitespace"));
    }

    public void testNullReplacebyChars() {
        replacement = new Replacement(new char[] {'a'}, null);
        assertEquals("hasReplaceBy", false, replacement.hasReplaceBy());
        assertEquals("ntidisestblishmentrinism",
                     replacement.replace("antidisestablishmentarianism"));
    }

    public void testEmptyReplaceby() {
        replacement = new Replacement(new char[] {'w'}, new char[0]);
        assertEquals("hasReplaceBy", false, replacement.hasReplaceBy());
        assertEquals("ibbleobble",
                     replacement.replace("wibblewobble"));
    }

    public test_Replacement(String name) {
        super(name);
    }

    public static TestSuite suite() {
        return new TestSuite(test_Replacement.class);
    }

}

