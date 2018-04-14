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

import org.xmlunit.diff.Comparison.Detail;
import org.xmlunit.util.TransformerFactoryConfigurer;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.StringWriter;

/**
 * Formatter methods for a {@link Comparison} Object.
 */
public class DefaultComparisonFormatter implements ComparisonFormatter {

    /**
     * Return a short String of the Comparison including the XPath and the shorten value of the effected control and
     * test Node.
     *
     * <p>In general the String will look like "Expected X 'Y' but was 'Z' - comparing A to B" where A and B are the
     * result of invoking {@link #getShortString} on the target and XPath of the control and test details of the
     * comparison. A is the description of the comparison and B and C are the control and test values (passed through
     * {@link #getValue}) respectively.</p>
     *
     * <p>For missing attributes the string has a slightly different format.</p>
     *
     * @param difference the comparison to describe
     * @return a short description of the comparison
     */
    @Override
    public String getDescription(Comparison difference) {
        final ComparisonType type = difference.getType();
        String description = type.getDescription();
        final Detail controlDetails = difference.getControlDetails();
        final Detail testDetails = difference.getTestDetails();
        final String controlTarget =
            getShortString(controlDetails.getTarget(), controlDetails.getXPath(),
                           type);
        final String testTarget =
            getShortString(testDetails.getTarget(), testDetails.getXPath(),
                           type);

        if (type == ComparisonType.ATTR_NAME_LOOKUP ) {
            return String.format("Expected %s '%s' - comparing %s to %s",
                description,
                controlDetails.getXPath(),
                controlTarget, testTarget);
        }
        return String.format("Expected %s '%s' but was '%s' - comparing %s to %s",
            description,
            getValue(controlDetails.getValue(), type), getValue(testDetails.getValue(), type),
            controlTarget, testTarget);
    }

    /**
     * May alter the display of a comparison value for {@link #getShortString} based on the comparison type.
     *
     * <p>This implementation returns {@code value} unless it is a comparison of node types in which case the numeric
     * value (one of the constants defined in the {@link Node} class) is mapped to a more useful String.</p>
     *
     * @param value the value to display
     * @param type the comparison type
     * @return the display value
     *
     * @since XMLUnit 2.4.0
     */
    protected Object getValue(Object value, ComparisonType type) {
        return type == ComparisonType.NODE_TYPE ? nodeType((Short) value) : value;
    }

    /**
     * Return a String representation for {@link #getShortString} that describes the "thing" that has been compared so
     * users know how to locate it.
     *
     * <p>Examples are "&lt;bar ...&gt; at /foo[1]/bar[1]" for a comparison of elements or "&lt;!-- Comment Text --&gt;
     * at /foo[2]/comment()[1]" for a comment.</p>
     *
     * <p>This implementation dispatches to several {@code appendX} methods based on the comparison type or the type of
     * the node.</p>
     *
     * @param node the node to describe
     * @param xpath xpath of the node if applicable
     * @param type the comparison type
     * @return the formatted result
     *
     * @since XMLUnit 2.4.0
     */
    protected String getShortString(Node node, String xpath, ComparisonType type) {
        StringBuilder sb = new StringBuilder();
        if (type == ComparisonType.HAS_DOCTYPE_DECLARATION) {
            Document doc = (Document)  node;
            appendDocumentType(sb, doc.getDoctype());
            appendDocumentElementIndication(sb, doc);
        } else if (node instanceof Document) {
            Document doc = (Document)  node;
            appendDocumentXmlDeclaration(sb, doc);
            appendDocumentElementIndication(sb, doc);
        } else if (node instanceof DocumentType) {
            final DocumentType docType = (DocumentType)  node;
            appendDocumentType(sb, docType);
            appendDocumentElementIndication(sb, docType.getOwnerDocument());
        } else if (node instanceof Attr) {
            appendAttribute(sb, (Attr)  node);
        } else if (node instanceof Element) {
            appendElement(sb, (Element)  node);
        } else if (node instanceof Text) {
            appendText(sb, (Text)  node);
        } else if (node instanceof Comment) {
            appendComment(sb, (Comment)  node);
        } else if (node instanceof ProcessingInstruction) {
            appendProcessingInstruction(sb, (ProcessingInstruction)  node);
        } else if (node == null) {
            sb.append("<NULL>");
        } else {
            sb.append("<!--NodeType ").append(node.getNodeType())
                .append(' ').append(node.getNodeName())
                .append('/').append(node.getNodeValue())
                .append("-->");
        }
        appendXPath(sb, xpath);
        return sb.toString();
    }

    /**
     * Appends the XPath information for {@link #getShortString} if present.
     *
     * @param sb the builder to append to
     * @param xpath the xpath to append, if any
     *
     * @since XMLUnit 2.4.0
     */
    protected void appendXPath(StringBuilder sb, String xpath) {
        if (xpath != null && xpath.length() > 0) {
            sb.append(" at ").append(xpath);
        }
    }

    /**
     * Appends the XML declaration for {@link #getShortString} or {@link #appendFullDocumentHeader} if it contains
     * non-default values.
     *
     * @param sb the builder to append to
     * @return true if the XML declaration has been appended
     *
     * @since XMLUnit 2.4.0
     */
    protected boolean appendDocumentXmlDeclaration(StringBuilder sb, Document doc) {
        if ("1.0".equals(doc.getXmlVersion()) && doc.getXmlEncoding() == null && !doc.getXmlStandalone()) {
            // only default values => ignore
            return false;
        }
        sb.append("<?xml version=\"");
        sb.append(doc.getXmlVersion());
        sb.append("\"");
        if (doc.getXmlEncoding() != null) {
            sb.append(" encoding=\"");
            sb.append(doc.getXmlEncoding());
            sb.append("\"");
        }
        if (doc.getXmlStandalone()) {
            sb.append(" standalone=\"yes\"");
        }
        sb.append("?>");
        return true;
    }

    /**
     * Appends a short indication of the document's root element like "&lt;ElementName...&gt;" for {@link
     * #getShortString}.
     *
     * @param sb the builder to append to
     * @param doc the XML document node
     *
     * @since XMLUnit 2.4.0
     */
    protected void appendDocumentElementIndication(StringBuilder sb, Document doc) {
        sb.append("<");
        sb.append(doc.getDocumentElement().getNodeName());
        sb.append("...>");
    }

    /**
     * Appends the XML DOCTYPE for {@link #getShortString} or {@link #appendFullDocumentHeader} if present.
     *
     * @param sb the builder to append to
     * @param type the document type
     * @return true if the DOCTPYE has been appended
     *
     * @since XMLUnit 2.4.0
     */
    protected boolean appendDocumentType(StringBuilder sb, DocumentType type) {
        if (type == null) {
            return false;
        }
        sb.append("<!DOCTYPE ").append(type.getName());
        boolean hasNoPublicId = true;
        if (type.getPublicId() != null && type.getPublicId().length() > 0) {
            sb.append(" PUBLIC \"").append(type.getPublicId()).append('"');
            hasNoPublicId = false;
        }
        if (type.getSystemId() != null && type.getSystemId().length() > 0) {
            if (hasNoPublicId) {
                sb.append(" SYSTEM");
            }
            sb.append(" \"").append(type.getSystemId()).append("\"");
        }
        sb.append(">");
        return true;
    }

    /**
     * Formats a processing instruction for {@link #getShortString}.
     *
     * @param sb the builder to append to
     * @param instr the processing instruction
     *
     * @since XMLUnit 2.4.0
     */
    protected void appendProcessingInstruction(StringBuilder sb, ProcessingInstruction instr) {
        sb.append("<?")
            .append(instr.getTarget())
            .append(' ').append(instr.getData())
            .append("?>");
    }

    /**
     * Formats a comment for {@link #getShortString}.
     *
     * @param sb the builder to append to
     * @param aNode the comment
     *
     * @since XMLUnit 2.4.0
     */
    protected void appendComment(StringBuilder sb, Comment aNode) {
        sb.append("<!--")
            .append(aNode.getNodeValue())
            .append("-->");
    }

    /**
     * Formats a text or CDATA node for {@link #getShortString}.
     *
     * @param sb the builder to append to
     * @param aNode the text or CDATA node
     *
     * @since XMLUnit 2.4.0
     */
    protected void appendText(StringBuilder sb, Text aNode) {
        sb.append("<")
            .append(aNode.getParentNode().getNodeName())
            .append(" ...>");

        if (aNode instanceof CDATASection) {
            sb.append("<![CDATA[")
                .append(aNode.getNodeValue())
                .append("]]>");
        } else {
            sb.append(aNode.getNodeValue());
        }

        sb.append("</")
            .append(aNode.getParentNode().getNodeName())
            .append(">");
    }

    /**
     * Formats a placeholder for an element for {@link #getShortString}.
     *
     * @param sb the builder to append to
     * @param aNode the element
     *
     * @since XMLUnit 2.4.0
     */
    protected void appendElement(StringBuilder sb, Element aNode) {
        sb.append("<")
            .append(aNode.getNodeName()).append("...")
            .append(">");
    }

    /**
     * Formats a placeholder for an attribute for {@link #getShortString}.
     *
     * @param sb the builder to append to
     * @param aNode the attribute
     *
     * @since XMLUnit 2.4.0
     */
    protected void appendAttribute(StringBuilder sb, Attr aNode) {
        sb.append("<").append(aNode.getOwnerElement().getNodeName());
        sb.append(' ')
            .append(aNode.getNodeName()).append("=\"")
            .append(aNode.getNodeValue()).append("\"...>");
    }

    /**
     * Return the xml node from {@link Detail#getTarget()} as formatted String.
     *
     * <p>Delegates to {@link #getFullFormattedXml} unless the {@code Comparison.Detail}'s {@code target} is null.</p>
     *
     * @param difference The {@link Comparison#getControlDetails()} or {@link Comparison#getTestDetails()}.
     * @param type the implementation can return different details depending on the ComparisonType.
     * @param formatXml set this to true if the Comparison was generated with {@link
     * org.xmlunit.builder.DiffBuilder#ignoreWhitespace()} - this affects the indentation of the generated output.
     *
     * @return the full xml node.
     */
    @Override
    public String getDetails(Comparison.Detail difference, ComparisonType type, boolean formatXml) {
        if (difference.getTarget() == null) {
            return "<NULL>";
        }
        return getFullFormattedXml(difference.getTarget(), type, formatXml);
    }

    /**
     * Formats the node using a format suitable for the node type and comparison.
     *
     * <p>The implementation outputs the document prolog and start element for {@code Document} and {@code DocumentType}
     * nodes and may elect to format the node's parent element rather than just the node depending on the node and
     * comparison type. It delegates to {@link #appendFullDocumentHeader} or {@link #getFormattedNodeXml}.</p>
     *
     * @param node the node to format
     * @param type the comparison type
     * @param formatXml true if the Comparison was generated with {@link
     * org.xmlunit.builder.DiffBuilder#ignoreWhitespace()} - this affects the indentation of the generated output
     * @return the fomatted XML
     *
     * @since XMLUnit 2.4.0
     */
    protected String getFullFormattedXml(final Node node, ComparisonType type, boolean formatXml) {
        StringBuilder sb = new StringBuilder();
        final Node nodeToConvert;
        if (type == ComparisonType.CHILD_NODELIST_SEQUENCE) {
            nodeToConvert = node.getParentNode();
        } else if (node instanceof Document) {
            Document doc = (Document)  node;
            appendFullDocumentHeader(sb, doc);
            return sb.toString();
        } else if (node instanceof DocumentType) {
            Document doc = node.getOwnerDocument();
            appendFullDocumentHeader(sb, doc);
            return sb.toString();
        } else if (node instanceof Attr) {
            nodeToConvert = ((Attr) node).getOwnerElement();
        } else if (node instanceof org.w3c.dom.CharacterData) {
            // in case of a simple text node, show the parent TAGs: "<a>xy</a>" instead "xy".
            nodeToConvert = node.getParentNode();
        } else {
            nodeToConvert = node;
        }
        sb.append(getFormattedNodeXml(nodeToConvert, formatXml));
        return sb.toString().trim();
    }

    /**
     * Appends the XML declaration and DOCTYPE if present as well as the document's root element for {@link
     * #getFullFormattedXml}.
     *
     * @param sb the builder to append to
     * @param doc the document to format
     *
     * @since XMLUnit 2.4.0
     */
    protected void appendFullDocumentHeader(StringBuilder sb, Document doc) {
        if (appendDocumentXmlDeclaration(sb, doc)) {
            sb.append("\n");
        }
        if (appendDocumentType(sb, doc.getDoctype())) {
            sb.append("\n");
        }
        appendOnlyElementStartTagWithAttributes(sb, doc.getDocumentElement());
    }

    private void appendOnlyElementStartTagWithAttributes(StringBuilder sb, final Element element) {
        sb.append("<");
        sb.append(element.getNodeName());
        final NamedNodeMap attributes = element.getAttributes();

        for (int i = 0; i < attributes.getLength(); i++) {
            Node node = attributes.item(i);
            if (node instanceof Attr) {
                Attr attr = (Attr) node;
                sb.append(" ");
                sb.append(attr.toString());
            }
        }
        if (element.hasChildNodes()) {
            sb.append(">\n  ...");
        } else {
            sb.append("/>");
        }
    }

    /**
     * Formats a node with the help of an identity XML transformation.
     *
     * @param nodeToConvert the node to format
     * @param formatXml true if the Comparison was generated with {@link
     * org.xmlunit.builder.DiffBuilder#ignoreWhitespace()} - this affects the indentation of the generated output
     * @return the fomatted XML
     *
     * @since XMLUnit 2.4.0
     */
    protected String getFormattedNodeXml(final Node nodeToConvert, boolean formatXml) {
        String formattedNodeXml;
        try {
            final int numberOfBlanksToIndent = formatXml ? 2 : -1;
            final Transformer transformer = createXmlTransformer(numberOfBlanksToIndent);
            final StringWriter buffer = new StringWriter();
            transformer.transform(new DOMSource(nodeToConvert), new StreamResult(buffer));
            formattedNodeXml = buffer.toString();
        } catch (final Exception e) {
            formattedNodeXml = "ERROR " + e.getMessage();
        }
        return formattedNodeXml;
    }

    /**
     * Create a default Transformer to format a XML-Node to a String.
     * 
     * @param numberOfBlanksToIndent the number of spaces which is used for indent the XML-structure
     * @return the transformer
     *
     * @since XMLUnit 2.4.0
     */
    protected Transformer createXmlTransformer(int numberOfBlanksToIndent) throws TransformerConfigurationException {
        TransformerFactoryConfigurer.Builder b = TransformerFactoryConfigurer.builder()
            .withExternalStylesheetLoadingDisabled()
            .withDTDLoadingDisabled();

        if (numberOfBlanksToIndent >= 0) {
            // not all TransformerFactories support this feature
            b = b.withSafeAttribute("indent-number", numberOfBlanksToIndent);
        }
        final TransformerFactory factory = b.build().configure(TransformerFactory.newInstance());
        final Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        if (numberOfBlanksToIndent >= 0) {
            try {
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String
                    .valueOf(numberOfBlanksToIndent));
            } catch (final IllegalArgumentException ex) {
                // Could not set property '{http://xml.apache.org/xslt}indent-amount' on
                // transformer.getClass().getName()
                // which is fine for us
            }
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        }
        return transformer;
    }

    /**
     * Provides a display text for the constant values of the {@link Node} class that represent node types.
     *
     * @param type the node type
     * @return the display text
     *
     * @since XMLUnit 2.4.0
     */
    protected String nodeType(short type) {
        switch(type) {
        case Node.ELEMENT_NODE:                return "Element";
        case Node.DOCUMENT_TYPE_NODE:          return "Document Type";
        case Node.ENTITY_NODE:                 return "Entity";
        case Node.ENTITY_REFERENCE_NODE:       return "Entity Reference";
        case Node.NOTATION_NODE:               return "Notation";
        case Node.TEXT_NODE:                   return "Text";
        case Node.COMMENT_NODE:                return "Comment";
        case Node.CDATA_SECTION_NODE:          return "CDATA Section";
        case Node.ATTRIBUTE_NODE:              return "Attribute";
        case Node.PROCESSING_INSTRUCTION_NODE: return "Processing Instruction";
        default: break; 
        }
        return Short.toString(type);
    }
}
