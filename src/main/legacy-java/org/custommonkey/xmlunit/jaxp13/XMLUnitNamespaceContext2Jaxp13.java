/*
******************************************************************
Copyright (c) 2006-2007, Jeff Martin, Tim Bacon
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

package org.custommonkey.xmlunit.jaxp13;

import org.custommonkey.xmlunit.NamespaceContext;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import javax.xml.XMLConstants;

/**
 * Adapts {@link NamespaceContext XMLUnit's NamespaceContext} to
 * {@link javax.xml.namespace.NamespaceContext JAXP 1.3's
 * NamespaceContext}.
 */
public class XMLUnitNamespaceContext2Jaxp13
    implements javax.xml.namespace.NamespaceContext {

    private final Map/*<String, String>*/ nsMap;

    public XMLUnitNamespaceContext2Jaxp13(NamespaceContext ctx) {
        nsMap = turnIntoMap(ctx);
    }
 
    public String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("prefix must not be null");
        }
        String uri = (String) nsMap.get(prefix);
        if (uri == null) {
            uri = XMLConstants.NULL_NS_URI;
        }
        return uri;
    }

    public Iterator getPrefixes(String uri) {
        if (uri == null) {
            throw new IllegalArgumentException("uri must not be null");
        }

        // ensure that the empty string comes out first when asked for
        // the default namespace URI's prefix
        TreeSet/*<String>*/ ts = new TreeSet();
        for (Iterator it = nsMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry/*<String,String>*/ entry = (Map.Entry) it.next();
            if (uri.equals(entry.getValue())) {
                ts.add(entry.getKey());
            }
        }
        return ts.iterator();
    }

    public String getPrefix(String uri) {
        Iterator i = getPrefixes(uri);
        return i.hasNext() ? (String) i.next() : null;
    }

    private static Map turnIntoMap(NamespaceContext ctx) {
        HashMap/*<String, String>*/ m = new HashMap();
        for (Iterator i = ctx.getPrefixes(); i.hasNext(); ) {
            String prefix = (String) i.next();
            String uri = ctx.getNamespaceURI(prefix);
            // according to the Javadocs only the constants defined in
            // XMLConstants are allowed as prefixes for the following
            // two URIs
            if (!XMLConstants.XML_NS_URI.equals(uri)
                && !XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(uri)) {
                m.put(prefix, uri);
            }
        }
        m.put(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
        m.put(XMLConstants.XMLNS_ATTRIBUTE,
              XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
        return m;
    }
}
