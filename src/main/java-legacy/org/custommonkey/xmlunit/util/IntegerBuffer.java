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

package org.custommonkey.xmlunit.util;

/**
 * Simplistic dynamically growing buffer of integers used by DoctypeSupport.
 *
 * <p>No attempt has been made to make this class thread-safe at all.
 * The append methods and indexOf are not too efficient either, but
 * work for what we need.</p>
 */
public class IntegerBuffer {

    // should be big enough for the DoctypeSupport use case
    private static final int INITIAL_SIZE = 512;

    private int[] buffer;
    private int currentSize;

    /**
     * Creates a new buffer.
     */
    public IntegerBuffer() {
        this(INITIAL_SIZE);
    }

    /**
     * Creates a new buffer with the given initial capacity.
     */
    public IntegerBuffer(int capacity) {
        buffer = new int[capacity];
    }

    /**
     * Returns the current size.
     */
    public int size() {
        return currentSize;
    }

    /**
     * Returns the current capacity (the size the buffer can use
     * before it needs to grow).
     */
    public int capacity() {
        return buffer.length;
    }

    /**
     * Appends a single int.
     */
    public void append(int i) {
        // could simply delegate to the other append methods, but this
        // (avoiding arraycopy) is more efficient.
        while (currentSize >= buffer.length) {
            grow();
        }
        buffer[currentSize++] = i;
    }

    /**
     * Appends an array of ints.
     */
    public void append(int[] i) {
        // could simply delegate to the other append methods, but this
        // (avoiding repeated comparisions) is more efficient.
        while (currentSize + i.length > buffer.length) {
            grow();
        }
        System.arraycopy(i, 0, buffer, currentSize, i.length);
        currentSize += i.length;
    }

    /**
     * Returns an arry view of this buffer's content.
     */
    public int[] toIntArray() {
        int[] i = new int[currentSize];
        System.arraycopy(buffer, 0, i, 0, currentSize);
        return i;
    }

    /**
     * finds sequence in current buffer.
     *
     * @return index of sequence or -1 if not found
     */
    public int indexOf(int[] sequence) {
        int index = -1;
        for (int i = 0; index == -1 && i <= currentSize - sequence.length;
             i++) {
            if (buffer[i] == sequence[0]) {
                boolean matches = true;
                for (int j = 1; matches && j < sequence.length; j++) {
                    if (buffer[i + j] != sequence[j]) {
                        matches = false;
                    }
                }
                if (matches) {
                    index = i;
                }
            }
        }
        return index;
    }

    private void grow() {
        int[] i = new int[buffer.length * 2 + 1];
        System.arraycopy(buffer, 0, i, 0, buffer.length);
        buffer = i;
    }
}
