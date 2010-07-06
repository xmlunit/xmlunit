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

namespace net.sf.xmlunit.diff {

    /// <summary>
    /// The kinds of comparisons XMLUnit performs.
    /// </summary>
    public enum ComparisonType {
        /// <summary>
        /// Do both documents specify the same version in their XML declaration?
        /// </summary>
        XML_VERSION,
        /// <summary>
        /// Do both documents specify the same standalone declaration
        /// in their XML declaration?
        /// </summary>
        XML_STANDALONE,
        /// <summary>
        /// Do both documents specify the same encoding in their XML
        /// declaration?
        /// </summary>
        XML_ENCODING,
        /// <summary>
        /// Do both documents have a DOCTYPE (or neither of each)?
        /// </summary>
        HAS_DOCTYPE_DECLARATION,
        /// <summary>
        /// If the documents both have DOCTYPEs, compare the names.
        /// </summary>
        DOCTYPE_NAME,
        /// <summary>
        /// If the documents both have DOCTYPEs, compare the PUBLIC
        /// identifiers.
        /// </summary>
        DOCTYPE_PUBLIC_ID,
        /// <summary>
        /// If the documents both have DOCTYPEs, compare the SYSTEM
        /// identifiers.
        /// </summary>
        DOCTYPE_SYSTEM_ID,

        /// <summary>
        /// Check whether both documents provide the same values for
        /// xsi:schemaLocation (may even be null).
        /// </summary>
        SCHEMA_LOCATION,
        /// <summary>
        /// Check whether both documents provide the same values for
        /// xsi:noNamspaceSchemaLocation (may even be null).
        /// </summary>
        NO_NAMESPACE_SCHEMA_LOCATION,

        /// <summary>
        /// Compare the node types.
        /// </summary>
        NODE_TYPE,

        /// <summary>
        /// Compare the node's namespace prefixes.
        /// </summary>
        NAMESPACE_PREFIX,
        /// <summary>
        /// Compare the node's namespace URIs.
        /// </summary>
        NAMESPACE_URI,

        /// <summary>
        /// Compare content of text nodes, comments, CDATA sections.
        /// </summary>
        TEXT_VALUE,

        /// <summary>
        /// Compare targets of processing instructions.
        /// </summary>
        PROCESSING_INSTRUCTION_TARGET,
        /// <summary>
        /// Compare data of processing instructions.
        /// </summary>
        PROCESSING_INSTRUCTION_DATA,

        /// <summary>
        /// Compare element names.
        /// </summary>
        ELEMENT_TAG_NAME,
        /// <summary>
        /// Compare explicit/implicit status of attributes.
        /// </summary>
        ATTR_VALUE_EXPLICITLY_SPECIFIED,
        /// <summary>
        /// Compare number of attributes.
        /// </summary>
        ELEMENT_NUM_ATTRIBUTES,
        /// <summary>
        /// Compare attribute's value.
        /// </summary>
        ATTR_VALUE,

        /// <summary>
        /// Compare number of child nodes.
        /// </summary>
        CHILD_NODELIST_LENGTH,
        /// <summary>
        /// Compare order of child nodes.
        /// </summary>
        CHILD_NODELIST_SEQUENCE,

        /// <summary>
        /// Search for a child node matching a specific child node of the
        /// other node.
        /// </summary>
        CHILD_LOOKUP,
        /// <summary>
        /// Search for an atribute with a name matching a specific
        /// attribute of the other node.
        /// </summary>
        ATTR_NAME_LOOKUP,
    }
}