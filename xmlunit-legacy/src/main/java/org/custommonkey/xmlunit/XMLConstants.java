/*
******************************************************************
Copyright (c) 2001-2007,2015-2016 Jeff Martin, Tim Bacon
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
    * Neither the name of the XMLUnit nor the names
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
    String XML_DECLARATION =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";

    /**
     * xmlns attribute prefix
     */
    String XMLNS_PREFIX = "xmlns";

    /**
     * "&lt;"
     */
    String OPEN_START_NODE = "<";

    /**
     * "&lt;/"
     */
    String OPEN_END_NODE = "</";

    /**
     * "&gt;"
     */
    String CLOSE_NODE = ">";

    /**
     * "![CDATA["
     */
    String START_CDATA = "![CDATA[";
    /**
     * "]]"
     */
    String END_CDATA = "]]";

    /**
     * "!--"
     */
    String START_COMMENT = "!--";
    /**
     * "--""
     */
    String END_COMMENT = "--";

    /**
     * "?"
     */
    String START_PROCESSING_INSTRUCTION = "?";
    /**
     * "?"
     */
    String END_PROCESSING_INSTRUCTION = "?";

    /**
     * "!DOCTYPE"
     */
    String START_DOCTYPE = "!DOCTYPE ";
    
    /**
     * "/"
     */
    String XPATH_SEPARATOR = "/";

    /**
     * "["
     */
    String XPATH_NODE_INDEX_START = "[";

    /**
     * "]"
     */
    String XPATH_NODE_INDEX_END = "]";
    
    /**
     * "comment()"
     */
    String XPATH_COMMENT_IDENTIFIER = "comment()";
    
    /**
     * "processing-instruction()"
     */
    String XPATH_PROCESSING_INSTRUCTION_IDENTIFIER = "processing-instruction()";
    
    /**
     * "text()"
     */
    String XPATH_CHARACTER_NODE_IDENTIFIER = "text()";

    /**
     * "@"
     */
    String XPATH_ATTRIBUTE_IDENTIFIER = "@";

    /**
     * http://www.w3.org/2001/XMLSchema
     */
    String W3C_XML_SCHEMA_NS_URI
        = "http://www.w3.org/2001/XMLSchema";

    /**
     * http://www.w3.org/2001/XMLSchema-instance
     */
    String W3C_XML_SCHEMA_INSTANCE_NS_URI
        = "http://www.w3.org/2001/XMLSchema-instance";

    /**
     * "schemaLocation"
     */
    String W3C_XML_SCHEMA_INSTANCE_SCHEMA_LOCATION_ATTR
        = "schemaLocation";

    /**
     * "noNamespaceSchemaLocation"
     */
    String W3C_XML_SCHEMA_INSTANCE_NO_NAMESPACE_SCHEMA_LOCATION_ATTR
        = "noNamespaceSchemaLocation";

    /**
     * ""
     */
    String NULL_NS_URI = "";
}
