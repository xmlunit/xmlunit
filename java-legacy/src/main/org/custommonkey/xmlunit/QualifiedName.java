/*
*****************************************************************
Copyright (c) 2014 Jeff Martin, Tim Bacon
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

/**
 * Since javax.xml.namespace.QName is not present prior to Java5, this
 * is XMLUnit's own abstraction.
 */
public final class QualifiedName {
    private final String namespaceUri;
    private final String localName;

    public QualifiedName(String localName) {
        this(XMLConstants.NULL_NS_URI, localName);
    }

    public QualifiedName(String namespaceUri, String localName) {
        if (localName == null) {
            throw new IllegalArgumentException("localName must not be null");
        }
        this.namespaceUri =
            namespaceUri == null ? XMLConstants.NULL_NS_URI : namespaceUri;
        this.localName = localName;
    }

    public String getNamespaceURI() {
        return namespaceUri;
    }

    public String getLocalName() {
        return localName;
    }

    public int hashCode() {
        return 7 * namespaceUri.hashCode() + localName.hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof QualifiedName)) {
            return false;
        }
        QualifiedName other = (QualifiedName) o;
        return namespaceUri.equals(other.namespaceUri)
            && localName.equals(other.localName);
    }

    /**
     * Parses strings of the form "{NS-URI}LOCAL-NAME" or "prefix:localName" as QualifiedNames.
     *
     * <p>When using the prefix-version the prefix must be defined
     * inside the current NamespaceContext.</p>
     *
     * @see XMLUnit#setXpathNamespaceContext
     */
    public static QualifiedName valueOf(String value) {
        return valueOf(value, XMLUnit.getXpathNamespaceContext());
    }

    /**
     * Represents the QualifiedName as {NS-URI}LOCAL-NAME.
     *
     * <p>If the NS-URI is equal to NULL_NS_URI only the local name is returned.</p>
     */
    public String toString() {
        return XMLConstants.NULL_NS_URI.equals(namespaceUri) ?
            localName : "{" + namespaceUri + "}" + localName;
    }

    /**
     * Parses strings of the form "{NS-URI}LOCAL-NAME" or "prefix:localName" as QualifiedNames.
     *
     * <p>When using the prefix-version the prefix must be defined
     * inside the NamespaceContext given as argument.</p>
     */
    public static QualifiedName valueOf(String value, NamespaceContext ctx) {
        if (value == null) {
            throw new IllegalArgumentException("value must not be null");
        }
        int colon = value.indexOf(':');
        int closingBrace = value.indexOf('}');
        boolean qnameToStringStyle = value.startsWith("{") && closingBrace > 0;
        if (!qnameToStringStyle && colon < 0) {
            return new QualifiedName(value); // null namespace
        }
        return qnameToStringStyle ? parseQNameToString(value, closingBrace)
            : parsePrefixFormat(value, colon, ctx);
    }

    private static QualifiedName parseQNameToString(String value, int closingBrace) {
        if (closingBrace + 1 == value.length()) {
            throw new IllegalArgumentException("localName must not be empty in "
                                               + value);
        }
        return new QualifiedName(value.substring(1, closingBrace),
                                 value.substring(closingBrace + 1));
    }

    private static QualifiedName parsePrefixFormat(String value, int colon,
                                                   NamespaceContext ctx) {
        if (colon + 1 == value.length()) {
            throw new IllegalArgumentException("localName must not be empty in "
                                               + value);
        }
        if (ctx == null) {
            throw new IllegalArgumentException("Cannot parse " + value
                                               + " without a NamespaceContext");
        }
        String prefix = value.substring(0, colon);
        String nsUri = ctx.getNamespaceURI(prefix);
        if (nsUri == null) {
            throw new IllegalArgumentException(prefix + " is unknown to "
                                               + "NamespaceContext");
        }
        return new QualifiedName(nsUri, value.substring(colon + 1));
    }
}
