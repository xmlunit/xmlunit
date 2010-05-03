/*
  This file is licensed to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package net.sf.xmlunit.diff;

/**
 * The kinds of comparisons XMLUnit performs.
 */
public enum ComparisonType {
    /**
     * Do both documents have a DOCTYPE (or neither of each)?
     */
    HAS_DOCTYPE_DECLARATION,
    /**
     * If the documents both have DOCTYPEs, compare the names.
     */
    DOCTYPE_NAME,
    /**
     * If the documents both have DOCTYPEs, compare the PUBLIC
     * identifiers.
     */
    DOCTYPE_PUBLIC,
    /**
     * If the documents both have DOCTYPEs, compare the SYSTEM
     * identifiers.
     */
    DOCTYPE_SYSTEM,

    /**
     * Check whether both documents provide the same values for
     * xsi:schemaLocation (may even be null).
     */
    SCHEMA_LOCATION,
    /**
     * Check whether both documents provide the same values for
     * xsi:noNamspaceSchemaLocation (may even be null).
     */
    NO_NAMESPACE_SCHEMA_LOCATION,

    /**
     * Compare the node types.
     */
    NODE_TYPE,

    /**
     * Compare the node's namespace prefixes.
     */
    NAMESPACE_PREFIX,
    /**
     * Compare the node's namespace URIs.
     */
    NAMESPACE_URI,

    /**
     * Compare content of CDATA sections.
     */
    CDATA_VALUE,
    /**
     * Compare content of comments.
     */
    COMMENT_VALUE,
    /**
     * Compare content of text nodes.
     */
    TEXT_VALUE,
    /**
     * Compare targets of processing instructions.
     */
    PROCESSING_INSTRUCTION_TARGET,
    /**
     * Compare data of processing instructions.
     */
    PROCESSING_INSTRUCTION_DATA,

    /**
     * Compare element names.
     */
    ELEMENT_TAG_NAME,
    /**
     * Compare explicit/implicit status of attributes.
     */
    ATTR_VALUE_EXPLICITLY_SPECIFIED,
    /**
     * Compare number of attributes.
     */
    ELEMENT_NUM_ATTRIBUTES,
    /**
     * Compare attribute's value.
     */
    ATTR_VALUE,
    /**
     * Compare number of child nodes.
     */
    CHILD_NODELIST_LENGTH,
    /**
     * Compare order of child nodes.
     */
    CHILD_NODELIST_SEQUENCE,

    /**
     * Search for a child node matching a specific child node of the
     * other node.
     */
    CHILD_LOOKUP,
    /**
     * Search for an atribute with a name matching a specific
     * attribute of the other node.
     */
    ATTR_NAME_LOOKUP,
}
