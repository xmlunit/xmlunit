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

import org.w3c.dom.Attr;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

/**
 * Class for describing Nodes
 */
public class NodeDescriptor implements XMLConstants {
    protected static final String DOCUMENT_NODE_DESCRIPTION = "Document Node ";
        
    /**
     * Convert a Node into a simple String representation 
     * and append to StringBuffer
     * @param buf
     * @param aNode
     */
    public static void appendNodeDetail(StringBuffer buf, NodeDetail nodeDetail) {
        appendNodeDetail(buf, nodeDetail.getNode(), true);
        buf.append(" at ").append(nodeDetail.getXpathLocation());
    }
    
    private static void appendNodeDetail(StringBuffer buf, Node aNode,  
                                         boolean notRecursing) {
        if (aNode==null) {
            return;
        }
        if (notRecursing) {
            buf.append(XMLConstants.OPEN_START_NODE);
        }
        switch (aNode.getNodeType()) {
        case Node.ATTRIBUTE_NODE:
            appendAttributeDetail(buf, aNode);
            break;
        case Node.ELEMENT_NODE:
            appendElementDetail(buf, aNode, notRecursing);
            break;
        case Node.TEXT_NODE:
            appendTextDetail(buf, aNode);
            break;
        case Node.CDATA_SECTION_NODE:
            appendCdataSectionDetail(buf, aNode);
            break;
        case Node.COMMENT_NODE:
            appendCommentDetail(buf, aNode);
            break;
        case Node.PROCESSING_INSTRUCTION_NODE:
            appendProcessingInstructionDetail(buf, aNode);
            break;
        case Node.DOCUMENT_TYPE_NODE:
            appendDocumentTypeDetail(buf, aNode);
            break;
        case Node.DOCUMENT_NODE:
            appendDocumentDetail(buf);
            break;
        default:
            buf.append("!--NodeType ").append(aNode.getNodeType())
                .append(' ').append(aNode.getNodeName())
                .append('/').append(aNode.getNodeValue())
                .append("--");

        }
        if (notRecursing) {
            buf.append(XMLConstants.CLOSE_NODE);
        }
    }

    protected static void appendDocumentDetail(StringBuffer buf) {
        buf.append(DOCUMENT_NODE_DESCRIPTION)
            .append(XMLConstants.OPEN_START_NODE)
            .append("...")
            .append(XMLConstants.CLOSE_NODE);
    }

    protected static void appendDocumentTypeDetail(StringBuffer buf, Node aNode) {
        DocumentType type = (DocumentType) aNode;
        buf.append(XMLConstants.START_DOCTYPE).append(type.getName());
        boolean hasNoPublicId = true;
        if (type.getPublicId()!=null
            && type.getPublicId().length() > 0) {
            buf.append(" PUBLIC \"").append(type.getPublicId())
                .append('"');
            hasNoPublicId = false;
        }
        if (type.getSystemId()!=null
            && type.getSystemId().length() > 0) {
            if (hasNoPublicId) {
                buf.append(" SYSTEM");
            }
            buf.append(" \"").append(type.getSystemId())
                .append('"');
        }
    }

    protected static void appendProcessingInstructionDetail(
                                                            StringBuffer buf, Node aNode) {
        ProcessingInstruction instr = (ProcessingInstruction) aNode;
        buf.append(XMLConstants.START_PROCESSING_INSTRUCTION)
            .append(instr.getTarget())
            .append(' ').append(instr.getData())
            .append(XMLConstants.END_PROCESSING_INSTRUCTION);
    }

    protected static void appendCommentDetail(StringBuffer buf, Node aNode) {
        buf.append(XMLConstants.START_COMMENT)
            .append(aNode.getNodeValue())
            .append(XMLConstants.END_COMMENT);
    }

    protected static void appendCdataSectionDetail(StringBuffer buf, Node aNode) {
        buf.append(XMLConstants.START_CDATA)
            .append(aNode.getNodeValue())
            .append(XMLConstants.END_CDATA);
    }

    protected static void appendTextDetail(StringBuffer buf, Node aNode) {
        appendNodeDetail(buf, aNode.getParentNode(), false);
        buf.append(" ...").append(XMLConstants.CLOSE_NODE)
            .append(aNode.getNodeValue())
            .append(XMLConstants.OPEN_END_NODE);
        appendNodeDetail(buf, aNode.getParentNode(), false);
    }

    protected static void appendElementDetail(StringBuffer buf, Node aNode,
                                              boolean notRecursing) {
        buf.append(aNode.getNodeName());
        if (notRecursing) {
            buf.append("...");
        }
    }

    protected static void appendAttributeDetail(StringBuffer buf, Node aNode) {         
        appendNodeDetail(buf,
                         ((Attr)aNode).getOwnerElement(), false);
        buf.append(' ')
            .append(aNode.getNodeName()).append("=\"")
            .append(aNode.getNodeValue()).append("\"...");
    }
}
