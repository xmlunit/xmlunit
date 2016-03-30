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

    private Object getValue(Object value, ComparisonType type) {
        return type == ComparisonType.NODE_TYPE ? nodeType((Short) value) : value;
    }

    private String getShortString(Node node, String xpath, ComparisonType type) {
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
        if (xpath != null && xpath.length() > 0) {
            sb.append(" at ").append(xpath);
        }
        return sb.toString();
    }

    private static boolean appendDocumentXmlDeclaration(StringBuilder sb, Document doc) {
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

    /** a short indication of the documents root element like "&lt;ElementName...&gt;".*/
    private static void appendDocumentElementIndication(StringBuilder sb, Document doc) {
        sb.append("<");
        sb.append(doc.getDocumentElement().getNodeName());
        sb.append("...>");
    }

    private static boolean appendDocumentType(StringBuilder sb, DocumentType type) {
        if (type == null) {
            return false;
        }
        sb.append("<!DOCTYPE ").append(type.getName());
        boolean hasNoPublicId = true;
        if (type.getPublicId()!=null && type.getPublicId().length() > 0) {
            sb.append(" PUBLIC \"").append(type.getPublicId()).append('"');
            hasNoPublicId = false;
        }
        if (type.getSystemId()!=null && type.getSystemId().length() > 0) {
            if (hasNoPublicId) {
                sb.append(" SYSTEM");
            }
            sb.append(" \"").append(type.getSystemId()).append("\"");
        }
        sb.append(">");
        return true;
    }

    private static void appendProcessingInstruction(StringBuilder sb, ProcessingInstruction instr) {
        sb.append("<?")
            .append(instr.getTarget())
            .append(' ').append(instr.getData())
            .append("?>");
    }

    private static void appendComment(StringBuilder sb, Comment aNode) {
        sb.append("<!--")
            .append(aNode.getNodeValue())
            .append("-->");
    }

    private static void appendText(StringBuilder sb, Text aNode) {
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

    private static void appendElement(StringBuilder sb, Element aNode) {
        sb.append("<")
        .append(aNode.getNodeName()).append("...")
        .append(">");
    }

    private static void appendAttribute(StringBuilder sb, Attr aNode) {
        sb.append("<").append(aNode.getOwnerElement().getNodeName());
        sb.append(' ')
            .append(aNode.getNodeName()).append("=\"")
            .append(aNode.getNodeValue()).append("\"...>");
    }

    @Override
    public String getDetails(Comparison.Detail difference, ComparisonType type, boolean formatXml) {
        if (difference.getTarget() == null) {
            return "<NULL>";
        }
        return getFullFormattedXml(difference.getTarget(), type, formatXml);
    }

    private String getFullFormattedXml(final Node node, ComparisonType type, boolean formatXml) {
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

    private void appendFullDocumentHeader(StringBuilder sb, Document doc) {
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

    private static String getFormattedNodeXml(final Node nodeToConvert, boolean formatXml) {
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
     * create a default Transformer to format a XML-Node to a String.
     * 
     * @param numberOfBlanksToIndent the number of spaces which is used for indent the XML-structure
     */
    private static Transformer createXmlTransformer(int numberOfBlanksToIndent) throws TransformerConfigurationException {
        final TransformerFactory factory = TransformerFactory.newInstance();
        // as not all TransformerFactories support this feature -> catch the IllegalArgumentException
        if (numberOfBlanksToIndent >= 0) {
            try {
                factory.setAttribute("indent-number", numberOfBlanksToIndent);
            } catch (final IllegalArgumentException ex) {
                // Could not set property 'indent-number' on factory.getClass().getName()
                // which is fine for us
            }
        }
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

    private static String nodeType(short type) {
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
