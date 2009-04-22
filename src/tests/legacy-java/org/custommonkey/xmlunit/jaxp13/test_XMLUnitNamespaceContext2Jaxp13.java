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

package org.custommonkey.xmlunit.jaxp13;

import org.custommonkey.xmlunit.NamespaceContext;
import org.custommonkey.xmlunit.SimpleNamespaceContext;

import java.util.HashMap;
import java.util.Iterator;
import javax.xml.XMLConstants;

import junit.framework.TestCase;

/**
 * JUnit test for XMLUnitNamespaceContext2Jaxp13
 */
public class test_XMLUnitNamespaceContext2Jaxp13 extends TestCase {
    private static final String[] PREFIXES = {"foo", "bar"};
    private static final String[] STANDARD_PREFIXES = {
        XMLConstants.XML_NS_PREFIX, XMLConstants.XMLNS_ATTRIBUTE
    };
    private static final String[] STANDARD_URIS = {
        XMLConstants.XML_NS_URI, XMLConstants.XMLNS_ATTRIBUTE_NS_URI
    };
    private static final String URI = "urn:example";

    public void testBasics() {
        XMLUnitNamespaceContext2Jaxp13 ctx =
            new XMLUnitNamespaceContext2Jaxp13(new SimpleNamespaceContext(setupMap()));
        validate(ctx);
    }

    public void testCannotOverrideStandardPrefixes() {
        HashMap m = setupMap();
        for (int i = 0; i < STANDARD_PREFIXES.length; i++) {
            m.put(STANDARD_PREFIXES[i], URI);
        }
        XMLUnitNamespaceContext2Jaxp13 ctx =
            new XMLUnitNamespaceContext2Jaxp13(new SimpleNamespaceContext(m));
        validate(ctx);
    }

    public void testCannotOverrideStandardURIs() {
        HashMap m = setupMap();
        for (int i = 0; i < STANDARD_PREFIXES.length; i++) {
            m.put(STANDARD_PREFIXES[i] + "1", STANDARD_URIS[i]);
        }
        XMLUnitNamespaceContext2Jaxp13 ctx =
            new XMLUnitNamespaceContext2Jaxp13(new SimpleNamespaceContext(m));
        validate(ctx);
    }

    public void testDefaultNamespaceHandling() {
        HashMap m = setupMap();
        m.put("", URI);
        XMLUnitNamespaceContext2Jaxp13 ctx =
            new XMLUnitNamespaceContext2Jaxp13(new SimpleNamespaceContext(m));
        
        // no matter how many prefixes map to it, DEFAULT_NS must be
        // the first prefix
        assertEquals(XMLConstants.DEFAULT_NS_PREFIX, ctx.getPrefix(URI));

        Iterator it = ctx.getPrefixes(URI);
        assertTrue(it.hasNext());
        assertEquals(XMLConstants.DEFAULT_NS_PREFIX, it.next());
        assertAllPrefixesFound(it);
    }

    private static HashMap setupMap() {
        HashMap map = new HashMap();
        for (int i = 0; i < PREFIXES.length; i++) {
            map.put(PREFIXES[i], URI);
        }
        return map;
    }

    private static void validate(XMLUnitNamespaceContext2Jaxp13 ctx) {
        for (int i = 0; i < PREFIXES.length; i++) {
            assertEquals(URI, ctx.getNamespaceURI(PREFIXES[i]));
        }
        for (int i = 0; i < STANDARD_PREFIXES.length; i++) {
            assertEquals(STANDARD_URIS[i],
                         ctx.getNamespaceURI(STANDARD_PREFIXES[i]));
        }
        assertEquals(XMLConstants.NULL_NS_URI,
                     ctx.getNamespaceURI(PREFIXES[0] + PREFIXES[0]));
        assertEquals(XMLConstants.NULL_NS_URI,
                     ctx.getNamespaceURI(XMLConstants.DEFAULT_NS_PREFIX));
        
        boolean foundThisPrefix = false;
        String prefix = ctx.getPrefix(URI);
        for (int i = 0; !foundThisPrefix && i < PREFIXES.length; i++) {
            if (PREFIXES[i].equals(prefix)) {
                foundThisPrefix = true;
            }
        }
        assertTrue("getPrefix returned a known prefix for " + URI,
                   foundThisPrefix);
        for (int i = 0; i < STANDARD_PREFIXES.length; i++) {
            assertEquals(STANDARD_PREFIXES[i], ctx.getPrefix(STANDARD_URIS[i]));
        }

        assertAllPrefixesFound(ctx.getPrefixes(URI));
        for (int i = 0; i < STANDARD_PREFIXES.length; i++) {
            Iterator it = ctx.getPrefixes(STANDARD_URIS[i]);
            assertTrue("One element for " + STANDARD_URIS[i], it.hasNext());
            assertEquals(STANDARD_PREFIXES[i], it.next());
            assertFalse("Only one element for " + STANDARD_URIS[i],
                        it.hasNext());
        }

        assertNull(ctx.getPrefix(URI + URI));
        assertFalse(ctx.getPrefixes(URI + URI).hasNext());
    }

    private static void assertAllPrefixesFound(Iterator it) {
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
