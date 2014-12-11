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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Implementation of NamespaceContext that's backed by a map.
 */
public class SimpleNamespaceContext implements NamespaceContext {
    /* prefix -> NS URI */
    private final Map/*<String, String>*/ prefixMap;

    /**
     * An empty context containing no prefixes at all.
     */
    public static final SimpleNamespaceContext EMPTY_CONTEXT =
        new SimpleNamespaceContext(Collections.EMPTY_MAP);

    /**
     * Creates a NamespaceContext backed by the given map.
     *
     * <p>Copies the map, changes made to the given map after calling
     * the constructor are not reflected into the
     * NamespaceContext.</p>
     *
     * @param prefixMap maps prefix to Namespace URI
     */
    public SimpleNamespaceContext(Map prefixMap) {
        this.prefixMap = new HashMap(prefixMap);
    }

    public String getNamespaceURI(String prefix) {
        return (String) prefixMap.get(prefix);
    }

    public Iterator getPrefixes() {
        return prefixMap.keySet().iterator();
    }
}
