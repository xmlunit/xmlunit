/*
******************************************************************
Copyright (c) 2001-2007,2015,2022 Jeff Martin, Tim Bacon
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
        * Neither the name of the XMLUnit nor the names
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

import org.xmlunit.diff.ElementSelectors;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Simple interface implementation that tests two elements for name
 * comparability. This class provides the default behaviour within a
 * DifferenceEngine (for backwards compatibility)
 * @see DifferenceEngine#compareNodeList(java.util.List, java.util.List, int, DifferenceListener, ElementQualifier)
 * @see Diff#overrideElementQualifier(ElementQualifier)
 */
public class ElementNameQualifier implements ElementQualifier {
    /**
     * Determine whether two elements qualify for further Difference comparison.
     * @return true if the two elements qualify for further comparison based on
     *  their  similar namespace URI and non-namespaced tag name,
     *  false otherwise
     */
    @Override
    public boolean qualifyForComparison(Element control, Element test) {
        return ElementSelectors.byName.canBeCompared(control, test);
    }

    /**
     * Determine whether two nodes are defined by the same namespace URI
     * @param control control node
     * @param test test node
     * @return true if the two nodes are both defined by the same namespace URI
     *  (including the default - empty - namespace), false otherwise
     */
    protected boolean equalsNamespace(Node control, Node test) {
        String controlNS = control.getNamespaceURI();
        String testNS = test.getNamespaceURI();
        if (controlNS == null) {
            return testNS == null;
        }
        return controlNS.equals(testNS);
    }

    /**
     * Strip any namespace information off a node name
     * @param node node
     * @return the localName if the node is namespaced, or the name otherwise
     */
    protected String getNonNamespacedNodeName(Node node) {
        String name = node.getLocalName();
        if (name == null) {
            return node.getNodeName();
        }
        return name;
    }

}
