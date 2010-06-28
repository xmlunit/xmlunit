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

import javax.xml.transform.Source;
import net.sf.xmlunit.util.Convert;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;

/**
 * Difference engine based on DOM.
 */
public final class DOMDifferenceEngine extends AbstractDifferenceEngine {

    public void compare(Source control, Source test) {
        if (control == null) {
            throw new IllegalArgumentException("control must not be null");
        }
        if (test == null) {
            throw new IllegalArgumentException("test must not be null");
        }
        compareNodes(Convert.toNode(control), Convert.toNode(test));
    }

    /**
     * Recursively compares two XML nodes.
     *
     * <p>Performs comparisons common to all node types, the performs
     * the node type specific comparisons and finally recures into
     * the node's child lists.</p>
     *
     * <p>Stops as soon as any comparison returns
     * ComparisonResult.CRITICAL.</p>
     *
     * <p>package private to support tests.</p>
     */
    ComparisonResult compareNodes(Node control, Node test) {
        ComparisonResult lastResult =
            compare(new Comparison(ComparisonType.NODE_TYPE, control,
                                   null, control.getNodeType(),
                                   test, null, test.getNodeType()));
        if (lastResult == ComparisonResult.CRITICAL) {
            return lastResult;
        }
        lastResult =
            compare(new Comparison(ComparisonType.NAMESPACE_URI, control,
                                   null, control.getNamespaceURI(),
                                   test, null, test.getNamespaceURI()));
        if (lastResult == ComparisonResult.CRITICAL) {
            return lastResult;
        }
        lastResult =
            compare(new Comparison(ComparisonType.NAMESPACE_PREFIX, control,
                                   null, control.getPrefix(),
                                   test, null, test.getPrefix()));
        if (lastResult == ComparisonResult.CRITICAL) {
            return lastResult;
        }
        NodeList controlChildren = control.getChildNodes();
        NodeList testChildren = test.getChildNodes();
        lastResult =
            compare(new Comparison(ComparisonType.CHILD_NODELIST_LENGTH,
                                   control, null, controlChildren.getLength(),
                                   test, null, testChildren.getLength()));
        if (lastResult == ComparisonResult.CRITICAL) {
            return lastResult;
        }
        lastResult = nodeTypeSpecificComparison(control, test);
        if (lastResult == ComparisonResult.CRITICAL) {
            return lastResult;
        }
        return compareNodeLists(controlChildren, testChildren);
    }

    /**
     * Dispatches to the node type specific comparison if one is
     * defined for the given combination of nodes.
     *
     * <p>package private to support tests.</p>
     */
    ComparisonResult nodeTypeSpecificComparison(Node control, Node test) {
        switch (control.getNodeType()) {
        case Node.CDATA_SECTION_NODE:
        case Node.COMMENT_NODE:
        case Node.TEXT_NODE:
            if (test instanceof CharacterData) {
                return compareCharacterData((CharacterData) control,
                                            (CharacterData) test);
            }
            break;
        case Node.DOCUMENT_NODE:
            if (test instanceof Document) {
                return compareDocuments((Document) control,
                                        (Document) test);
            }
            break;
        case Node.ELEMENT_NODE:
            if (test instanceof Element) {
                return compareElements((Element) control,
                                       (Element) test);
            }
            break;
        case Node.PROCESSING_INSTRUCTION_NODE:
            if (test instanceof ProcessingInstruction) {
                return
                    compareProcessingInstructions((ProcessingInstruction) control,
                                                  (ProcessingInstruction) test);
            }
            break;
        }
        return ComparisonResult.EQUAL;
    }

    /**
     * Compares textual content.
     */
    private ComparisonResult compareCharacterData(CharacterData control,
                                                  CharacterData test) {
        return compare(new Comparison(ComparisonType.TEXT_VALUE, control,
                                      null, control.getData(),
                                      test, null, test.getData()));
    }

    ComparisonResult compareDocuments(Document control,
                                      Document test) {
        DocumentType controlDt = control.getDoctype();
        DocumentType testDt = test.getDoctype();
        ComparisonResult r = 
            compare(new Comparison(ComparisonType.HAS_DOCTYPE_DECLARATION,
                                   control, null,
                                   Boolean.valueOf(controlDt != null),
                                   test, null,
                                   Boolean.valueOf(testDt != null)));
        if (r == ComparisonResult.CRITICAL) {
            return r;
        }
        if (controlDt != null && testDt != null) {
            r = compareDocTypes(controlDt, testDt);
        }
        if (r == ComparisonResult.CRITICAL) {
            return r;
        }
        r = compare(new Comparison(ComparisonType.XML_VERSION,
                                   control, null, control.getXmlVersion(),
                                   test, null, test.getXmlVersion()));
        if (r == ComparisonResult.CRITICAL) {
            return r;
        }
        r = compare(new Comparison(ComparisonType.XML_STANDALONE,
                                   control, null, control.getXmlStandalone(),
                                   test, null, test.getXmlStandalone()));
        if (r == ComparisonResult.CRITICAL) {
            return r;
        }
        return compare(new Comparison(ComparisonType.XML_ENCODING,
                                      control, null, control.getXmlEncoding(),
                                      test, null, test.getXmlEncoding()));
    }

    ComparisonResult compareDocTypes(DocumentType control,
                                     DocumentType test) {
        ComparisonResult r = 
            compare(new Comparison(ComparisonType.DOCTYPE_NAME,
                                   control, null, control.getName(),
                                   test, null, test.getName()));
        if (r == ComparisonResult.CRITICAL) {
            return r;
        }
        r = compare(new Comparison(ComparisonType.DOCTYPE_PUBLIC_ID,
                                   control, null, control.getPublicId(),
                                   test, null, test.getPublicId()));
        if (r == ComparisonResult.CRITICAL) {
            return r;
        }
        return compare(new Comparison(ComparisonType.DOCTYPE_SYSTEM_ID,
                                      control, null, control.getSystemId(),
                                      test, null, test.getSystemId()));
    }

    ComparisonResult compareElements(Element control,
                                     Element test) {
        return ComparisonResult.EQUAL;
    }

    ComparisonResult
        compareProcessingInstructions(ProcessingInstruction control,
                                      ProcessingInstruction test) {
        ComparisonResult r = 
            compare(new Comparison(ComparisonType.PROCESSING_INSTRUCTION_TARGET,
                                   control, null, control.getTarget(),
                                   test, null, test.getTarget()));
        if (r == ComparisonResult.CRITICAL) {
            return r;
        }
        return compare(new Comparison(ComparisonType.PROCESSING_INSTRUCTION_DATA,
                                      control, null, control.getData(),
                                      test, null, test.getData()));
    }

    ComparisonResult compareNodeLists(NodeList control, NodeList test) {
        return ComparisonResult.EQUAL;
    }
}
