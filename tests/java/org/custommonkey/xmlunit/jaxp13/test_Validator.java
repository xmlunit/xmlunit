/*
******************************************************************
Copyright (c) 2008, Jeff Martin, Tim Bacon
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

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import org.custommonkey.xmlunit.test_Constants;
import org.custommonkey.xmlunit.exceptions.XMLUnitRuntimeException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class test_Validator extends TestCase {

    public void testGoodSchemaIsValid() throws Exception {
        Validator v = new Validator();
        v.addSchemaSource(new StreamSource(new File(test_Constants.BASEDIR 
                                                    + "/tests/etc/Book.xsd")));
        assertTrue(v.isSchemaValid());
    }

    public void testGoodSchemaHasNoErrors() throws Exception {
        Validator v = new Validator();
        v.addSchemaSource(new StreamSource(new File(test_Constants.BASEDIR 
                                                    + "/tests/etc/Book.xsd")));
        assertEquals(0, v.getSchemaErrors().size());
    }

    public void testBrokenSchemaIsInvalid() throws Exception {
        Validator v = new Validator();
        v.addSchemaSource(new StreamSource(new File(test_Constants.BASEDIR 
                                                    + "/tests/etc/broken.xsd")));
        assertFalse(v.isSchemaValid());
    }

    public void testBrokenSchemaHasErrors() throws Exception {
        Validator v = new Validator();
        v.addSchemaSource(new StreamSource(new File(test_Constants.BASEDIR 
                                                    + "/tests/etc/broken.xsd")));
        List l = v.getSchemaErrors();
        for (Iterator i = l.iterator(); i.hasNext(); ) {
            Object ex = i.next();
            assertTrue(ex instanceof SAXParseException);
            /*
            System.err.println(ex);
            */
        }
        assertTrue(l.size() > 0);
    }

    public void testGoodInstanceIsValid() throws Exception {
        Validator v = new Validator();
        v.addSchemaSource(new StreamSource(new File(test_Constants.BASEDIR 
                                                    + "/tests/etc/Book.xsd")));
        StreamSource s =
            new StreamSource(new File(test_Constants.BASEDIR
                                      + "/tests/etc/BookXsdGenerated.xml"));
        assertTrue(v.isInstanceValid(s));
    }

    public void testBadInstanceIsInvalid() throws Exception {
        Validator v = new Validator();
        v.addSchemaSource(new StreamSource(new File(test_Constants.BASEDIR 
                                                    + "/tests/etc/Book.xsd")));
        StreamSource s =
            new StreamSource(new File(test_Constants.BASEDIR
                                      + "/tests/etc/invalidBook.xml"));
        assertFalse(v.isInstanceValid(s));
    }

    public void testBadInstanceHasErrors() throws Exception {
        Validator v = new Validator();
        v.addSchemaSource(new StreamSource(new File(test_Constants.BASEDIR 
                                                    + "/tests/etc/Book.xsd")));
        StreamSource s =
            new StreamSource(new File(test_Constants.BASEDIR
                                      + "/tests/etc/invalidBook.xml"));
        List l = v.getInstanceErrors(s);
        for (Iterator i = l.iterator(); i.hasNext(); ) {
            Object ex = i.next();
            assertTrue(ex instanceof SAXParseException);
            /*
            System.err.println(ex);
            */
        }
        assertTrue(l.size() > 0);
    }

    public void testInstanceValidationOfBrokenSchema() {
        Validator v = new Validator();
        v.addSchemaSource(new StreamSource(new File(test_Constants.BASEDIR 
                                                    + "/tests/etc/broken.xsd")));
        StreamSource s =
            new StreamSource(new File(test_Constants.BASEDIR
                                      + "/tests/etc/BookXsdGenerated.xml"));
        try {
            v.isInstanceValid(s);
            fail("expected exception because schema is invalid");
        } catch (XMLUnitRuntimeException e) {
            assertTrue(e.getCause() instanceof SAXException);
        }
    }

    public void testInstanceValidationOfMissingFile() {
        Validator v = new Validator();
        v.addSchemaSource(new StreamSource(new File(test_Constants.BASEDIR 
                                                    + "/tests/etc/Book.xsd")));
        StreamSource s =
            new StreamSource(new File(test_Constants.BASEDIR
                                      + "/tests/etc/not there.xml"));
        try {
            v.isInstanceValid(s);
            fail("expected exception because instance doesn't exist");
        } catch (XMLUnitRuntimeException e) {
            assertTrue(e.getCause() instanceof IOException);
        }
    }

    /**
     * fails unless you manage to setup JAXP 1.3 and RELAX NG support
     *
     * <p>The setup that worked for Stefan when he wrote this test:
     * JDK 1.5.0_09, isorelax-jaxp-bridge-1.0, together with msv.jar,
     * isorelax.jar, relaxngDatatype.jar and xsdlib.jar from msv's
     * latest nightly build (2008-02-13, actually).  The same jars do
     * not work with Java6.</p>
     * 
     * @see http://weblogs.java.net/blog/kohsuke/archive/2006/02/validate_xml_us.html
     */
    public void XtestGoodRelaxNGSchemaIsValid() throws Exception {
        Validator v = new Validator(javax.xml.XMLConstants.RELAXNG_NS_URI);
        v.addSchemaSource(new StreamSource(new File(test_Constants.BASEDIR 
                                                    + "/tests/etc/Book.rng")));
        assertTrue(v.isSchemaValid());
    }

    /**
     * fails unless you manage to setup JAXP 1.3 and RELAX NG support
     * @see #XtestGoodRelaxNGSchemaIsValid()
     */
    public void XtestGoodInstanceIsValidRNG() throws Exception {
        Validator v = new Validator(javax.xml.XMLConstants.RELAXNG_NS_URI);
        v.addSchemaSource(new StreamSource(new File(test_Constants.BASEDIR 
                                                    + "/tests/etc/Book.rng")));
        StreamSource s =
            new StreamSource(new File(test_Constants.BASEDIR
                                      + "/tests/etc/BookXsdGeneratedNoSchema.xml"));
        assertTrue(v.isInstanceValid(s));
    }

    /**
     * fails unless you manage to setup JAXP 1.3 and RELAX NG support
     * @see #XtestGoodRelaxNGSchemaIsValid()
     */
    public void XtestBadInstanceIsInvalidRNG() throws Exception {
        Validator v = new Validator(javax.xml.XMLConstants.RELAXNG_NS_URI);
        v.addSchemaSource(new StreamSource(new File(test_Constants.BASEDIR 
                                                    + "/tests/etc/Book.rng")));
        StreamSource s =
            new StreamSource(new File(test_Constants.BASEDIR
                                      + "/tests/etc/invalidBook.xml"));
        List l = v.getInstanceErrors(s);
        for (Iterator i = l.iterator(); i.hasNext(); ) {
            Object ex = i.next();
            assertTrue(ex instanceof SAXParseException);
            /*
            System.err.println(ex);
            */
        }
        assertTrue(l.size() > 0);
    }

    /**
     * fails even using the setup in XtestGoodRelaxNGSchemaIsValid()
     * since a SAXParser is trying to read the compact syntax
     * definition and chokes on it not being XML.
     * @see #XtestGoodRelaxNGSchemaIsValid()
     */
    public void XtestGoodRelaxNGCompactSyntaxIsValid() throws Exception {
        Validator v = new Validator(javax.xml.XMLConstants.RELAXNG_NS_URI);
        v.addSchemaSource(new StreamSource(new File(test_Constants.BASEDIR 
                                                    + "/tests/etc/Book.rngc")));
        assertTrue(v.isSchemaValid());
        StreamSource s =
            new StreamSource(new File(test_Constants.BASEDIR
                                      + "/tests/etc/BookXsdGeneratedNoSchema.xml"));
        assertTrue(v.isInstanceValid(s));
    }

}
