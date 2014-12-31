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

import java.util.HashMap;
import java.util.Map;
import junit.framework.Assert;
import junit.framework.TestCase;

public class test_QualifiedName extends TestCase {

    public void testParseBareLocalName() {
        Assert.assertEquals(new QualifiedName("foo"),
                            QualifiedName.valueOf("foo"));
    }

    public void testParseQNameToStringStyle() {
        Assert.assertEquals(new QualifiedName("foo", "bar"),
                            QualifiedName.valueOf("{foo}bar"));
    }

    public void testParsePrefixStyle() {
        Map m = new HashMap();
        m.put("pre", "foo");
        NamespaceContext ctx = new SimpleNamespaceContext(m);
        Assert.assertEquals(new QualifiedName("foo", "bar"),
                            QualifiedName.valueOf("pre:bar", ctx));
    }

    public void testParsePrefixStyleImplicitContext() {
        Map m = new HashMap();
        m.put("pre", "foo");
        XMLUnit.setXpathNamespaceContext(new SimpleNamespaceContext(m));
        try {
            Assert.assertEquals(new QualifiedName("foo", "bar"),
                                QualifiedName.valueOf("pre:bar"));
        } finally {
            XMLUnit.setXpathNamespaceContext(null);
        }
    }

    public void testValueMustNotBeNull() {
        shouldThrowWithMessage(null, "null");
    }

    public void testLocalPartMustNotBeEmptyQNameStyle() {
        shouldThrowWithMessage("{foo}", "must not be empty");
    }

    public void testLocalPartMustNotBeEmptyPrefixStyle() {
        shouldThrowWithMessage("foo:", "must not be empty");
    }

    public void testPrefixStyleRequiresNamespaceContext() {
        shouldThrowWithMessage("foo:bar", "without a NamespaceContext");
    }

    public void testPrefixMustBeKnownToContext() {
        Map m = new HashMap();
        XMLUnit.setXpathNamespaceContext(new SimpleNamespaceContext(m));
        try {
            shouldThrowWithMessage("foo:bar", "foo is unknown");
        } finally {
            XMLUnit.setXpathNamespaceContext(null);
        }
    }

    private void shouldThrowWithMessage(String valueToParse,
                                        String messagePart) {
        try {
            QualifiedName.valueOf(valueToParse);
            Assert.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            String msg = ex.getMessage();
            Assert.assertTrue("exception message should contain '"
                              + messagePart + "' but was " + msg,
                              msg.indexOf(messagePart) >= 0);
        }
    }
}
