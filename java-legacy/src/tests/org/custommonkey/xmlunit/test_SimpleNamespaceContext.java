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

import java.util.HashMap;
import java.util.Iterator;
import junit.framework.TestCase;

/**
 * JUnit test for SimpleNamespaceContext
 */
public class test_SimpleNamespaceContext extends TestCase {

    private static final String[] PREFIXES = {"foo", "bar"};
    private static final String URI = "urn:example";

    public void testEmptyContextIsEmpty() {
        assertFalse(SimpleNamespaceContext.EMPTY_CONTEXT.getPrefixes()
                    .hasNext());
    }

    public void testSimpleMap() {
        validate(new SimpleNamespaceContext(setupMap()));
    }

    public void testCopyOfMap() {
        HashMap map = setupMap();
        SimpleNamespaceContext ctx = new SimpleNamespaceContext(map);
        // change a mapping
        map.put(PREFIXES[0], URI + PREFIXES[0]);
        // add a new one
        map.put(PREFIXES[0] + PREFIXES[0], URI);
        validate(ctx);
    }

    private static HashMap setupMap() {
        HashMap map = new HashMap();
        for (int i = 0; i < PREFIXES.length; i++) {
            map.put(PREFIXES[i], URI);
        }
        return map;
    }

    private static void validate(NamespaceContext ctx) {
        for (int i = 0; i < PREFIXES.length; i++) {
            assertEquals(URI, ctx.getNamespaceURI(PREFIXES[i]));
        }
        Iterator it = ctx.getPrefixes();
        boolean[] found = new boolean[PREFIXES.length];
        int count = 0;
        while (it.hasNext()) {
            count++;
            String p = (String) it.next();
            boolean foundThisPrefix = false;
            for (int i = 0; !foundThisPrefix && i < PREFIXES.length; i++) {
                if (PREFIXES[i].equals(p)) {
                    found[i] = foundThisPrefix = true;
                }
            }
            if (!foundThisPrefix) {
                fail("Prefix " + p + " should not be in this context");
            }
        }
        assertEquals(PREFIXES.length, count);
        for (int i = 0; i < PREFIXES.length; i++) {
            assertTrue("Context contained " + PREFIXES[i], found[i]);
        }
    }
}
