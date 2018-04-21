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
package org.xmlunit.diff;

import java.util.Locale;

/**
 * The kinds of comparisons XMLUnit performs.
 */
public enum ComparisonType {
    /**
     * Do both documents specify the same version in their XML declaration?
     */
    XML_VERSION,
    /**
     * Do both documents specify the same standalone declaration in
     * their XML declaration?
     */
    XML_STANDALONE,
    /**
     * Do both documents specify the same encoding in their XML declaration?
     */
    XML_ENCODING,
    /**
     * Do both documents have a DOCTYPE (or neither of each)?
     *
     * <p>This difference is most likely masked by a {@link
     * #CHILD_NODELIST_LENGTH} difference as the number of children of
     * the document node is tested before the presence of the document
     * type declaration.</p>
     */
    HAS_DOCTYPE_DECLARATION(true),
    /**
     * If the documents both have DOCTYPEs, compare the names.
     */
    DOCTYPE_NAME(true),
    /**
     * If the documents both have DOCTYPEs, compare the PUBLIC
     * identifiers.
     */
    DOCTYPE_PUBLIC_ID(true),
    /**
     * If the documents both have DOCTYPEs, compare the SYSTEM
     * identifiers.
     */
    DOCTYPE_SYSTEM_ID(true),

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
     * Compare content of text nodes, comments and CDATA sections.
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
    ATTR_VALUE_EXPLICITLY_SPECIFIED("attribute value explicitly specified"),
    /**
     * Compare number of attributes.
     */
    ELEMENT_NUM_ATTRIBUTES("number of attributes"),
    /**
     * Compare attribute's value.
     */
    ATTR_VALUE("attribute value"),
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
    CHILD_LOOKUP("child"),
    /**
     * Search for an attribute with a name matching a specific
     * attribute of the other node.
     */
    ATTR_NAME_LOOKUP("attribute name");

    private final String description;
    private final boolean doctypeComparison;

    private ComparisonType() {
        this(false);
    }

    private ComparisonType(boolean doctypeComparison) {
        this(null, doctypeComparison);
    }

    private ComparisonType(String description) {
        this(description, false);
    }

    private ComparisonType(String description, boolean doctypeComparison) {
        this.description = description;
        this.doctypeComparison = doctypeComparison;
    }

    public String getDescription() {
        if (description == null) {
            return name().toLowerCase(Locale.ENGLISH).replace('_', ' ');
        }
        return description;
    }

    boolean isDoctypeComparison() {
        return doctypeComparison;
    }
}
