/*
******************************************************************
Copyright (c) 2001, Jeff Martin, Tim Bacon
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
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/**
 * Helper class.
 * Abstract interface implementation that performs Node-type checks and
 * delegates testNode() processing to subclass.
 * <br />Examples and more at <a href="http://xmlunit.sourceforge.net"/>xmlunit.sourceforge.net</a>
 * @see NodeTest
 */
public abstract class AbstractNodeTester implements NodeTester {
    /**
     * Validate a single Node by delegating to node type specific methods.
     * @see #testAttribute(Attr)
     * @see #testCDATASection(CDATASection)
     * @see #testComment(Comment)
     * @see #testDocumentType(DocumentType)
     * @see #testElement(Element)
     * @see #testEntity(Entity)
     * @see #testEntityReference(EntityReference)
     * @see #testNotation(Notation)
     * @see #testProcessingInstruction(ProcessingInstruction)
     * @see #testText(Text)
     */
    public void testNode(Node aNode, NodeTest forTest) throws NodeTestException {
        switch (aNode.getNodeType()) {
        case Node.ATTRIBUTE_NODE:
            // should not happen as attributes are not exposed by DOM traversal
            testAttribute((Attr)aNode);
            break;
        case Node.CDATA_SECTION_NODE:
            testCDATASection((CDATASection)aNode);
            break;
        case Node.COMMENT_NODE:
            testComment((Comment)aNode);
            break;
        case Node.DOCUMENT_TYPE_NODE:
            testDocumentType((DocumentType)aNode);
            break;
        case Node.ELEMENT_NODE:
            testElement((Element)aNode);
            break;
        case Node.ENTITY_NODE:
            testEntity((Entity)aNode);
            break;
        case Node.ENTITY_REFERENCE_NODE:
            testEntityReference((EntityReference)aNode);
            break;
        case Node.NOTATION_NODE:
            testNotation((Notation)aNode);
            break;
        case Node.PROCESSING_INSTRUCTION_NODE:
            testProcessingInstruction(
                                      (ProcessingInstruction) aNode);
            break;
        case Node.TEXT_NODE:
            testText((Text)aNode);
            break;
        default:
            throw new NodeTestException("No delegate method for Node type",
                                        aNode);
        }
    }

    /**
     * Template delegator for testNode() method. OVERRIDE to add custom logic
     * @param attribute
     * @exception NodeTestException always: override if required in subclass
     */
    public void testAttribute(Attr attribute) throws NodeTestException {
        unhandled(attribute);
    }
    /**
     * Template delegator for testNode() method. OVERRIDE to add custom logic
     * @param cdata
     * @exception NodeTestException always: override if required in subclass
     */
    public void testCDATASection(CDATASection cdata) throws NodeTestException {
        unhandled(cdata);
    }
    /**
     * Template delegator for testNode() method. OVERRIDE to add custom logic
     * @param comment
     * @exception NodeTestException always: override if required in subclass
     */
    public void testComment(Comment comment) throws NodeTestException {
        unhandled(comment);
    }
    /**
     * Template delegator for testNode() method. OVERRIDE to add custom logic
     * @param doctype
     * @exception NodeTestException always: override if required in subclass
     */
    public void testDocumentType(DocumentType doctype) throws NodeTestException {
        unhandled(doctype);
    }
    /**
     * Template delegator for testNode() method. OVERRIDE to add custom logic
     * @param element
     * @exception NodeTestException always: override if required in subclass
     */
    public void testElement(Element element) throws NodeTestException {
        unhandled(element);
    }
    /**
     * Template delegator for testNode() method. OVERRIDE to add custom logic
     * @param entity
     * @exception NodeTestException always: override if required in subclass
     */
    public void testEntity(Entity entity) throws NodeTestException {
        unhandled(entity);
    }
    /**
     * Template delegator for testNode() method. OVERRIDE to add custom logic
     * @param reference
     * @exception NodeTestException always: override if required in subclass
     */
    public void testEntityReference(EntityReference reference) throws NodeTestException {
        unhandled(reference);
    }
    /**
     * Template delegator for testNode() method. OVERRIDE to add custom logic
     * @param notation
     * @exception NodeTestException always: override if required in subclass
     */
    public void testNotation(Notation notation) throws NodeTestException {
        unhandled(notation);
    }
    /**
     * Template delegator for testNode() method. OVERRIDE to add custom logic
     * @param instr
     * @exception NodeTestException always: override if required in subclass
     */
    public void testProcessingInstruction(ProcessingInstruction instr) throws NodeTestException  {
        unhandled(instr);
    }
    /**
     * Template delegator for testNode() method. OVERRIDE to add custom logic
     * @param text
     * @exception NodeTestException always: override if required in subclass
     */
    public void testText(Text text) throws NodeTestException {
        unhandled(text);
    }
    
    private void unhandled(Node aNode) throws NodeTestException {
        throw new NodeTestException("Test fails by default in AbstractNodeTester", aNode);
    }

    /**
     * Validate that the Nodes validated one-by-one in the <code>isValid</code>
     * method were all the Nodes expected. By default do nothing: 
     * can override to add custom logic
     * @exception NodeTestException if mode Nodes were expected
     */
    public void noMoreNodes(NodeTest forTest) throws NodeTestException {
        //by default do nothing
    }
}

