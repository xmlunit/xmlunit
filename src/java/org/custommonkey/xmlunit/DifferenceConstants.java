/*
******************************************************************
Copyright (c) 2001-2007, Jeff Martin, Tim Bacon
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
 * Constants for describing differences between DOM Nodes.
 * <br />Examples and more at <a href="http://xmlunit.sourceforge.net"/>xmlunit.sourceforge.net</a>
 */
public interface DifferenceConstants {
    /** Comparing an implied attribute value against an explicit value */
    int ATTR_VALUE_EXPLICITLY_SPECIFIED_ID = 1;
    /** Comparing 2 elements and one has an attribute the other does not */
    int ATTR_NAME_NOT_FOUND_ID = 2;
    /** Comparing 2 attributes with the same name but different values */
    int ATTR_VALUE_ID = 3;
    /** Comparing 2 attribute lists with the same attributes in different sequence */
    int ATTR_SEQUENCE_ID = 4;
    /** Comparing 2 CDATA sections with different values */
    int CDATA_VALUE_ID = 5;
    /** Comparing 2 comments with different values */
    int COMMENT_VALUE_ID = 6;
    /** Comparing 2 document types with different names */
    int DOCTYPE_NAME_ID = 7;
    /** Comparing 2 document types with different public identifiers */
    int DOCTYPE_PUBLIC_ID_ID = 8;
    /** Comparing 2 document types with different system identifiers */
    int DOCTYPE_SYSTEM_ID_ID = 9;
    /** Comparing 2 elements with different tag names */
    int ELEMENT_TAG_NAME_ID = 10;
    /** Comparing 2 elements with different number of attributes */
    int ELEMENT_NUM_ATTRIBUTES_ID = 11;
    /** Comparing 2 processing instructions with different targets */
    int PROCESSING_INSTRUCTION_TARGET_ID = 12;
    /** Comparing 2 processing instructions with different instructions */
    int PROCESSING_INSTRUCTION_DATA_ID = 13;
    /** Comparing 2 different text values */
    int TEXT_VALUE_ID = 14;
    /** Comparing 2 nodes with different namespace prefixes */
    int NAMESPACE_PREFIX_ID = 15;
    /** Comparing 2 nodes with different namespace URIs */
    int NAMESPACE_URI_ID = 16;
    /** Comparing 2 nodes with different node types */
    int NODE_TYPE_ID = 17;
    /** Comparing 2 nodes but only one has any children*/
    int HAS_CHILD_NODES_ID = 18;
    /** Comparing 2 nodes with different numbers of children */
    int CHILD_NODELIST_LENGTH_ID = 19;
    /** Comparing 2 nodes with children whose nodes are in different sequence*/
    int CHILD_NODELIST_SEQUENCE_ID = 20;
    /** Comparing 2 Documents only one of which has a doctype */
    int HAS_DOCTYPE_DECLARATION_ID = 21;
    /** Comparing 2 nodes and one holds more childnodes than can be
     * matched against child nodes of the other. */
    int CHILD_NODE_NOT_FOUND_ID = 22;
    /** Comparing 2 nodes with different xsi:schemaLocation
     * attributes, potentially only one of the two provides such an
     * attribute at all.
     */
    int SCHEMA_LOCATION_ID = 23;
    /** Comparing 2 nodes with different xsi:noNamespaceSchemaLocation
     * attributes, potentially only one of the two provides such an
     * attribute at all.
     */
    int NO_NAMESPACE_SCHEMA_LOCATION_ID = 24;
        
    /** Comparing an implied attribute value against an explicit value */
    public static final Difference ATTR_VALUE_EXPLICITLY_SPECIFIED =
        new Difference(ATTR_VALUE_EXPLICITLY_SPECIFIED_ID, 
                       "attribute value explicitly specified", true);

    /** Comparing 2 elements and one has an attribute the other does not */
    public static final Difference ATTR_NAME_NOT_FOUND =
        new Difference(ATTR_NAME_NOT_FOUND_ID, "attribute name");

    /** Comparing 2 attributes with the same name but different values */
    public static final Difference ATTR_VALUE =
        new Difference(ATTR_VALUE_ID, "attribute value");

    /** Comparing 2 attribute lists with the same attributes in different sequence */
    public static final Difference ATTR_SEQUENCE =
        new Difference(ATTR_SEQUENCE_ID, "sequence of attributes", true);

    /** Comparing 2 CDATA sections with different values */
    public static final Difference CDATA_VALUE =
        new Difference(CDATA_VALUE_ID, "CDATA section value");

    /** Comparing 2 comments with different values */
    public static final Difference COMMENT_VALUE =
        new Difference(COMMENT_VALUE_ID, "comment value");

    /** Comparing 2 document types with different names */
    public static final Difference DOCTYPE_NAME =
        new Difference(DOCTYPE_NAME_ID, "doctype name");

    /** Comparing 2 document types with different public identifiers */
    public static final Difference DOCTYPE_PUBLIC_ID =
        new Difference(DOCTYPE_PUBLIC_ID_ID, "doctype public identifier");

    /** Comparing 2 document types with different system identifiers */
    public static final Difference DOCTYPE_SYSTEM_ID =
        new Difference(DOCTYPE_SYSTEM_ID_ID, "doctype system identifier", true);

    /** Comparing 2 elements with different tag names */
    public static final Difference ELEMENT_TAG_NAME =
        new Difference(ELEMENT_TAG_NAME_ID, "element tag name");

    /** Comparing 2 elements with different number of attributes */
    public static final Difference ELEMENT_NUM_ATTRIBUTES =
        new Difference(ELEMENT_NUM_ATTRIBUTES_ID, "number of element attributes");

    /** Comparing 2 processing instructions with different targets */
    public static final Difference PROCESSING_INSTRUCTION_TARGET =
        new Difference(PROCESSING_INSTRUCTION_TARGET_ID, 
                       "processing instruction target");

    /** Comparing 2 processing instructions with different instructions */
    public static final Difference PROCESSING_INSTRUCTION_DATA =
        new Difference(PROCESSING_INSTRUCTION_DATA_ID, 
                       "processing instruction data");

    /** Comparing 2 different text values */
    public static final Difference TEXT_VALUE =
        new Difference(TEXT_VALUE_ID, "text value");

    /** Comparing 2 nodes with different namespace prefixes */
    public static final Difference NAMESPACE_PREFIX =
        new Difference(NAMESPACE_PREFIX_ID, "namespace prefix", true);

    /** Comparing 2 nodes with different namespace URIs */
    public static final Difference NAMESPACE_URI =
        new Difference(NAMESPACE_URI_ID, "namespace URI");

    /** Comparing 2 nodes with different node types */
    public static final Difference NODE_TYPE =
        new Difference(NODE_TYPE_ID, "node type");

    /** Comparing 2 nodes but only one has any children*/
    public static final Difference HAS_CHILD_NODES =
        new Difference(HAS_CHILD_NODES_ID, "presence of child nodes to be");

    /** Comparing 2 nodes with different numbers of children */
    public static final Difference CHILD_NODELIST_LENGTH =
        new Difference(CHILD_NODELIST_LENGTH_ID, "number of child nodes");

    /** Comparing 2 nodes with children whose nodes are in different sequence*/
    public static final Difference CHILD_NODELIST_SEQUENCE =
        new Difference(CHILD_NODELIST_SEQUENCE_ID, 
                       "sequence of child nodes", true);
    
    /** Comparing 2 Documents only one of which has a doctype */
    public static final Difference HAS_DOCTYPE_DECLARATION = 
        new Difference(HAS_DOCTYPE_DECLARATION_ID, 
                       "presence of doctype declaration", true);

    /** Comparing 2 nodes and one holds more childnodes than can be
     * matched against child nodes of the other. */
    public static final Difference CHILD_NODE_NOT_FOUND = 
        new Difference(CHILD_NODE_NOT_FOUND_ID, "presence of child node");

    /** Comparing 2 nodes with different xsi:schemaLocation
     * attributes, potentially only one of the two provides such an
     * attribute at all.
     */
    public static final Difference SCHEMA_LOCATION = 
        new Difference(SCHEMA_LOCATION_ID, "xsi:schemaLocation attribute",
                       true);
    /** Comparing 2 nodes with different xsi:noNamespaceSchemaLocation
     * attributes, potentially only one of the two provides such an
     * attribute at all.
     */
    public static final Difference NO_NAMESPACE_SCHEMA_LOCATION = 
        new Difference(NO_NAMESPACE_SCHEMA_LOCATION_ID,
                       "xsi:noNamespaceSchemaLocation attribute",
                       true);
}
