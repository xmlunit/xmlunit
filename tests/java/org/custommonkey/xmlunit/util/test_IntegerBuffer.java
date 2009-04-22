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

package org.custommonkey.xmlunit.util;

import junit.framework.TestCase;

/**
 * Tests for IntegerBuffer
 */
public class test_IntegerBuffer extends TestCase {

    public void testToArrayEmpty() {
        assertNotNull((new IntegerBuffer()).toIntArray());
        assertEquals(0, (new IntegerBuffer()).toIntArray().length);
    }

    public void testSingleIntAppend() {
        IntegerBuffer b = new IntegerBuffer();
        b.append(1);
        assertNotNull(b.toIntArray());
        assertEquals(1, b.toIntArray().length);
        assertEquals(1, b.toIntArray()[0]);
    }

    public void testArrayAppend() {
        IntegerBuffer b = new IntegerBuffer();
        b.append(new int[] {1, 2});
        assertNotNull(b.toIntArray());
        assertEquals(2, b.toIntArray().length);
        for (int i = 0; i < 2; i++) {
            assertEquals(i + 1, b.toIntArray()[i]);
        }
    }

    public void testSingleIntAppendWithGrowth() {
        IntegerBuffer b = new IntegerBuffer(1);
        for (int i = 0; i < 2; i++) {
            b.append(i);
        }
        assertNotNull(b.toIntArray());
        assertEquals(2, b.toIntArray().length);
        for (int i = 0; i < 2; i++) {
            assertEquals(i, b.toIntArray()[i]);
        }
    }

    public void testArrayAppendWithGrowth() {
        IntegerBuffer b = new IntegerBuffer(1);
        b.append(new int[] {1, 2});
        assertNotNull(b.toIntArray());
        assertEquals(2, b.toIntArray().length);
        for (int i = 0; i < 2; i++) {
            assertEquals(i + 1, b.toIntArray()[i]);
        }
    }

    public void testSize() {
        IntegerBuffer b = new IntegerBuffer();
        assertEquals(0, b.size());
        b.append(0);
        assertEquals(1, b.size());
        b.append(new int[] {1, 2});
        assertEquals(3, b.size());
    }

    public void testCapacity() {
        IntegerBuffer b = new IntegerBuffer(1);
        assertEquals(1, b.capacity());
        b.append(0);
        assertEquals(1, b.capacity());
        b.append(0);
        assertTrue(b.capacity() > 1);
    }

    public void testIndexOfSimple() {
        IntegerBuffer b = new IntegerBuffer();
        int[] test = new int[] {1, 2, 3};
        assertEquals(-1, b.indexOf(test));
        b.append(test);
        assertEquals(0, b.indexOf(test));
        b.append(test);
        assertEquals(0, b.indexOf(test));
    }

    public void testIndexOfWithOffset() {
        IntegerBuffer b = new IntegerBuffer();
        int[] test = new int[] {1, 2, 3};
        b.append(0);
        assertEquals(-1, b.indexOf(test));
        b.append(test);
        assertEquals(1, b.indexOf(test));
    }

    public void testIndexOfWithRepeatedInts() {
        IntegerBuffer b = new IntegerBuffer();
        int[] test = new int[] {1, 2, 3};
        b.append(1);
        assertEquals(-1, b.indexOf(test));
        b.append(test);
        assertEquals(1, b.indexOf(test));
    }

    public void testIndexOfSupSequenceIsThere() {
        IntegerBuffer b = new IntegerBuffer();
        int[] test = new int[] {1, 2, 3};
        b.append(new int[] {1, 2});
        b.append(4);
        assertEquals(-1, b.indexOf(test));
    }        

    public void testAllBytes() {
        IntegerBuffer buf = new IntegerBuffer();
        for (byte b = Byte.MIN_VALUE; b < Byte.MAX_VALUE; b++) {
            buf.append(b);
        }
        buf.append(Byte.MAX_VALUE);
        int[] is = buf.toIntArray();
        for (int i = Byte.MIN_VALUE; i <= Byte.MAX_VALUE; i++) {
            assertEquals((byte) i, is[i + Math.abs(Byte.MIN_VALUE)]);
        }
    }

    public void testAllChars() {
        IntegerBuffer buf = new IntegerBuffer();
        for (char c = Character.MIN_VALUE; c < Character.MAX_VALUE; c++) {
            buf.append(c);
        }
        buf.append(Character.MAX_VALUE);
        int[] is = buf.toIntArray();
        for (int i = Character.MIN_VALUE; i <= Character.MAX_VALUE; i++) {
            assertEquals((char) i, is[i + Math.abs(Character.MIN_VALUE)]);
        }
    }
}
