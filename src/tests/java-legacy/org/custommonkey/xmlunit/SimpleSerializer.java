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
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.w3c.dom.Node;

/**
 * Simple serialization class that uses a NodeInputStream (and hence
 * a DOM-to-Stream identity transformation) to perform the work.
 * This is not an efficient process for serialization, but it is portable
 * across underlying transform implementations which always comes in handy...
 * Only used so far for testing hence it's position in the test side of the
 *  source tree.
 */
public class SimpleSerializer {
    private final int bufferSize;
    private final Properties outputProperties;

    /**
     * Construct a new instance with default buffer size
     */
    public SimpleSerializer() {
        this(1024);
    }

    /**
     * Construct a new instance with specific buffer size (handy for large
     * documents)
     */
    public SimpleSerializer(int bufferSize) {
        this.bufferSize = bufferSize;
        outputProperties = new Properties();
    }

    /**
     * Set an output property for the serialied form
     * @param name
     * @param value
     * @see javax.xml.transform.OutputKeys
     */
    public void setOutputProperty(String name, String value) {
        outputProperties.setProperty(name, value);
    }

    /**
     * Serialize a Node to a String with default String encoding
     * @param domNode
     * @return full String representation of Node
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    public String serialize(Node domNode)
        throws IOException, UnsupportedEncodingException {
        return serialize(domNode, null);
    }

    /**
     * Serialize a Node to a String with specific String encoding
     * @param domNode
     * @return full String representation of Node
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    public String serialize(Node domNode, String encoding)
        throws IOException, UnsupportedEncodingException {
        NodeInputStream nodeStream = new NodeInputStream(domNode,
                                                         outputProperties);

        InputStreamReader reader;
        if (encoding == null) {
            // use default encoding
            reader = new InputStreamReader(nodeStream);
        } else {
            reader = new InputStreamReader(nodeStream, encoding);
        }

        StringBuffer buf = new StringBuffer();
        char[] chars = new char[bufferSize];
        int len;
        while ((len = reader.read(chars))!=-1) {
            buf.append(chars, 0, len);
        }
        reader.close();

        return buf.toString();
    }
}

