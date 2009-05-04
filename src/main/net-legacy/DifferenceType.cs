namespace XmlUnit {
    public enum DifferenceType : int {        
    	/** Comparing an implied attribute value against an explicit value */
    	ATTR_VALUE_EXPLICITLY_SPECIFIED_ID = 1,
    	
    	/** Comparing 2 elements and one has an attribute the other does not */
    	ATTR_NAME_NOT_FOUND_ID = 2,
    	
    	/** Comparing 2 attributes with the same name but different values */
    	ATTR_VALUE_ID = 3,
    	
    	/** Comparing 2 attribute lists with the same attributes in different sequence */
    	ATTR_SEQUENCE_ID = 4,
    	
    	/** Comparing 2 CDATA sections with different values */
    	CDATA_VALUE_ID = 5,
    	
    	/** Comparing 2 comments with different values */
    	COMMENT_VALUE_ID = 6,
    	
    	/** Comparing 2 document types with different names */
    	DOCTYPE_NAME_ID = 7,
    	
    	/** Comparing 2 document types with different public identifiers */
    	DOCTYPE_PUBLIC_ID_ID = 8,
    	
    	/** Comparing 2 document types with different system identifiers */
    	DOCTYPE_SYSTEM_ID_ID = 9,
    	
    	/** Comparing 2 elements with different tag names */
    	ELEMENT_TAG_NAME_ID = 10,
    	
    	/** Comparing 2 elements with different number of attributes */
    	ELEMENT_NUM_ATTRIBUTES_ID = 11,
    	
    	/** Comparing 2 processing instructions with different targets */
    	PROCESSING_INSTRUCTION_TARGET_ID = 12,
    	
    	/** Comparing 2 processing instructions with different instructions */
    	PROCESSING_INSTRUCTION_DATA_ID = 13,
    	
    	/** Comparing 2 different text values */
    	TEXT_VALUE_ID = 14,
    	
    	/** Comparing 2 nodes with different namespace prefixes */
    	NAMESPACE_PREFIX_ID = 15,
    	
    	/** Comparing 2 nodes with different namespace URIs */
    	NAMESPACE_URI_ID = 16,
    	
    	/** Comparing 2 nodes with different node types */
    	NODE_TYPE_ID = 17,
    	
    	/** Comparing 2 nodes but only one has any children*/
    	HAS_CHILD_NODES_ID = 18,
    	
    	/** Comparing 2 nodes with different numbers of children */
    	CHILD_NODELIST_LENGTH_ID = 19,
    	
    	/** Comparing 2 nodes with children whose nodes are in different sequence*/
    	CHILD_NODELIST_SEQUENCE_ID = 20,
    	
    	/** Comparing 2 Documents only one of which has a doctype */
    	HAS_DOCTYPE_DECLARATION_ID = 21,
	
    	/** Comparing 2 Documents only one of which has an XML Prefix Declaration */
    	HAS_XML_DECLARATION_PREFIX_ID = 22,
    } ;
}
