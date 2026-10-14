/*
******************************************************************
Copyright (c) 2008,2011,2014-2015 Jeff Martin, Tim Bacon
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

package org.custommonkey.xmlunit.jaxp13;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.test_Constants;
import org.custommonkey.xmlunit.exceptions.XMLUnitRuntimeException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class test_Validator extends TestCase {

    public void testGoodSchemaIsValid() throws Exception {
        Validator v = new Validator();
        v.addSchemaSource(new StreamSource(new File(test_Constants.TEST_RESOURCE_DIR
                                                    + "Book.xsd")));
        assertTrue(v.isSchemaValid());
    }

    public void testGoodSchemaIsValidWithCustomPrefix() throws Exception {
        Validator v = new Validator();
        v.addSchemaSource(new StreamSource(new File(test_Constants.TEST_RESOURCE_DIR
                                                    + "Book.xsd2")));
        assertTrue(v.isSchemaValid());
    }

    public void testGoodSchemaHasNoErrors() throws Exception {
        Validator v = new Validator();
        v.addSchemaSource(new StreamSource(new File(test_Constants.TEST_RESOURCE_DIR
                                                    + "Book.xsd")));
        assertEquals(0, v.getSchemaErrors().size());
    }

    public void testBrokenSchemaIsInvalid() throws Exception {
        Validator v = new Validator();
        v.addSchemaSource(new StreamSource(new File(test_Constants.TEST_RESOURCE_DIR
                                                    + "broken.xsd")));
        assertFalse(v.isSchemaValid());
    }

    public void testBrokenSchemaHasErrors() throws Exception {
        Validator v = new Validator();
        v.addSchemaSource(new StreamSource(new File(test_Constants.TEST_RESOURCE_DIR
                                                    + "broken.xsd")));
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
        v.addSchemaSource(new StreamSource(new File(test_Constants.TEST_RESOURCE_DIR
                                                    + "Book.xsd")));
        StreamSource s =
            new StreamSource(new File(test_Constants.TEST_RESOURCE_DIR
                                      + "BookXsdGenerated.xml"));
        assertTrue(v.isInstanceValid(s));
    }

    public void testGoodInstanceIsValidNoSchemaSource() throws Exception {
        Validator v = new Validator();
        StreamSource s =
            new StreamSource(new File(test_Constants.TEST_RESOURCE_DIR
                                      + "BookXsdGeneratedWithFixedSchemaLocation.xml"));
        assertTrue(v.isInstanceValid(s));
    }

    public void testBadInstanceIsInvalid() throws Exception {
        Validator v = new Validator();
        v.addSchemaSource(new StreamSource(new File(test_Constants.TEST_RESOURCE_DIR
                                                    + "Book.xsd")));
        StreamSource s =
            new StreamSource(new File(test_Constants.TEST_RESOURCE_DIR
                                      + "invalidBook.xml"));
        assertFalse(v.isInstanceValid(s));
    }

    public void testBadInstanceHasErrors() throws Exception {
        Validator v = new Validator();
        v.addSchemaSource(new StreamSource(new File(test_Constants.TEST_RESOURCE_DIR
                                                    + "Book.xsd")));
        StreamSource s =
            new StreamSource(new File(test_Constants.TEST_RESOURCE_DIR
                                      + "invalidBook.xml"));
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
        v.addSchemaSource(new StreamSource(new File(test_Constants.TEST_RESOURCE_DIR
                                                    + "broken.xsd")));
        StreamSource s =
            new StreamSource(new File(test_Constants.TEST_RESOURCE_DIR
                                      + "BookXsdGenerated.xml"));
        try {
            v.isInstanceValid(s);
            fail("expected exception because schema is invalid");
        } catch (XMLUnitRuntimeException e) {
            assertTrue(e.getCause() instanceof SAXException);
        }
    }

    public void testInstanceValidationOfMissingFile() {
        Validator v = new Validator();
        v.addSchemaSource(new StreamSource(new File(test_Constants.TEST_RESOURCE_DIR
                                                    + "Book.xsd")));
        StreamSource s =
            new StreamSource(new File(test_Constants.TEST_RESOURCE_DIR
                                      + "not there.xml"));
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
     * @see <a href="http://weblogs.java.net/blog/kohsuke/archive/2006/02/validate_xml_us.html"/>
     */
    public void XtestGoodRelaxNGSchemaIsValid() throws Exception {
        Validator v = new Validator(javax.xml.XMLConstants.RELAXNG_NS_URI);
        v.addSchemaSource(new StreamSource(new File(test_Constants.TEST_RESOURCE_DIR
                                                    + "Book.rng")));
        assertTrue(v.isSchemaValid());
    }

    /**
     * fails unless you manage to setup JAXP 1.3 and RELAX NG support
     * @see #XtestGoodRelaxNGSchemaIsValid()
     */
    public void XtestGoodInstanceIsValidRNG() throws Exception {
        Validator v = new Validator(javax.xml.XMLConstants.RELAXNG_NS_URI);
        v.addSchemaSource(new StreamSource(new File(test_Constants.TEST_RESOURCE_DIR
                                                    + "Book.rng")));
        StreamSource s =
            new StreamSource(new File(test_Constants.TEST_RESOURCE_DIR
                                      + "BookXsdGeneratedNoSchema.xml"));
        assertTrue(v.isInstanceValid(s));
    }

    /**
     * fails unless you manage to setup JAXP 1.3 and RELAX NG support
     * @see #XtestGoodRelaxNGSchemaIsValid()
     */
    public void XtestBadInstanceIsInvalidRNG() throws Exception {
        Validator v = new Validator(javax.xml.XMLConstants.RELAXNG_NS_URI);
        v.addSchemaSource(new StreamSource(new File(test_Constants.TEST_RESOURCE_DIR
                                                    + "Book.rng")));
        StreamSource s =
            new StreamSource(new File(test_Constants.TEST_RESOURCE_DIR
                                      + "invalidBook.xml"));
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
        v.addSchemaSource(new StreamSource(new File(test_Constants.TEST_RESOURCE_DIR
                                                    + "Book.rngc")));
        assertTrue(v.isSchemaValid());
        StreamSource s =
            new StreamSource(new File(test_Constants.TEST_RESOURCE_DIR
                                      + "BookXsdGeneratedNoSchema.xml"));
        assertTrue(v.isInstanceValid(s));
    }

    public void testXXEProtectionAppliesToInstanceValidation() throws Exception {
        File secret = File.createTempFile("xmlunit-xxe-secret", ".txt");
        secret.deleteOnExit();
        FileWriter w = new FileWriter(secret);
        try {
            w.write("INJECTED");
        } finally {
            w.close();
        }
        File xsd = File.createTempFile("xmlunit-xxe", ".xsd");
        xsd.deleteOnExit();
        w = new FileWriter(xsd);
        try {
            w.write("<?xml version='1.0'?>"
                    + "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
                    + "<xs:element name='root'><xs:simpleType>"
                    + "<xs:restriction base='xs:string'>"
                    + "<xs:enumeration value='INJECTED'/>"
                    + "</xs:restriction></xs:simpleType></xs:element></xs:schema>");
        } finally {
            w.close();
        }
        String instance = "<?xml version='1.0'?>"
            + "<!DOCTYPE root [ <!ENTITY xxe SYSTEM '" + secret.toURI() + "'> ]>"
            + "<root>&xxe;</root>";
        XMLUnit.setEnableXXEProtection(true);
        try {
            Validator v = new Validator();
            v.addSchemaSource(new StreamSource(xsd));
            // had the external entity been resolved the instance would
            // have matched the schema's enumeration and validation would
            // have succeeded
            assertFalse(v.isInstanceValid(new StreamSource(new StringReader(instance))));
        } finally {
            XMLUnit.setEnableXXEProtection(false);
        }
    }

}
