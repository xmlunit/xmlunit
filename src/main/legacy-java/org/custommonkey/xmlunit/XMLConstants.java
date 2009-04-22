/*
******************************************************************
Copyright (c) 2001-2007 Jeff Martin, Tim Bacon
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
 * A convenient place to hang constants relating to general XML usage
 */
public interface XMLConstants {

    /**
     * &lt;?xml&gt; declaration
     */
    public static final String XML_DECLARATION =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";

    /**
     * xmlns attribute prefix
     */
    public static final String XMLNS_PREFIX = "xmlns";

    /**
     * "&lt;"
     */
    public static final String OPEN_START_NODE = "<";

    /**
     * "&lt;/"
     */
    public static final String OPEN_END_NODE = "</";

    /**
     * "&gt;"
     */
    public static final String CLOSE_NODE = ">";

    /**
     * "![CDATA["
     */
    public static final String START_CDATA = "![CDATA[";
    /**
     * "]]"
     */
    public static final String END_CDATA = "]]";

    /**
     * "!--"
     */
    public static final String START_COMMENT = "!--";
    /**
     * "--""
     */
    public static final String END_COMMENT = "--";

    /**
     * "?"
     */
    public static final String START_PROCESSING_INSTRUCTION = "?";
    /**
     * "?"
     */
    public static final String END_PROCESSING_INSTRUCTION = "?";

    /**
     * "!DOCTYPE"
     */
    public static final String START_DOCTYPE = "!DOCTYPE ";
    
    /**
     * "/"
     */
    public static final String XPATH_SEPARATOR = "/";

    /**
     * "["
     */
    public static final String XPATH_NODE_INDEX_START = "[";

    /**
     * "]"
     */
    public static final String XPATH_NODE_INDEX_END = "]";
    
    /**
     * "comment()"
     */
    public static final String XPATH_COMMENT_IDENTIFIER = "comment()";
    
    /**
     * "processing-instruction()"
     */
    public static final String XPATH_PROCESSING_INSTRUCTION_IDENTIFIER = "processing-instruction()";
    
    /**
     * "text()"
     */
    public static final String XPATH_CHARACTER_NODE_IDENTIFIER = "text()";

    /**
     * "&at;"
     */
    public static final String XPATH_ATTRIBUTE_IDENTIFIER = "@";

    /**
     * http://www.w3.org/2001/XMLSchema
     */
    public static final String W3C_XML_SCHEMA_NS_URI
        = "http://www.w3.org/2001/XMLSchema";

    /**
     * http://www.w3.org/2001/XMLSchema-instance
     */
    public static final String W3C_XML_SCHEMA_INSTANCE_NS_URI
        = "http://www.w3.org/2001/XMLSchema-instance";

    /**
     * "schemaLocation"
     */
    public static final String W3C_XML_SCHEMA_INSTANCE_SCHEMA_LOCATION_ATTR
        = "schemaLocation";

    /**
     * "noNamespaceSchemaLocation"
     */
    String W3C_XML_SCHEMA_INSTANCE_NO_NAMESPACE_SCHEMA_LOCATION_ATTR
        = "noNamespaceSchemaLocation";
}
