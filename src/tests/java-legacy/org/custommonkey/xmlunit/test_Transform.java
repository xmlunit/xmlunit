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

import java.io.File;
import java.io.FileReader;

import org.custommonkey.xmlunit.exceptions.ConfigurationException;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.w3c.dom.Document;

/**
 * Test a Transform
 */
public class test_Transform extends TestCase{
    private static final String FLEABALL = "<fleaball><animal><shaggy>dog</shaggy></animal></fleaball>";

    private static final String DOG = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><dog/>" ;

    private Transform transform;
    private File animal;

    public void testGetResultString() throws Exception {
        transform = new Transform(FLEABALL, animal);
        assertEquals(DOG, stripLineFeeds(transform.getResultString()));
    }

    public void testGetResultDocument() throws Exception {
        transform = new Transform(FLEABALL, animal);
        Diff diff = new Diff(DOG, transform);
        assertEquals(diff.toString(), true, diff.identical());
    }

    public void testIdentityTransform() throws Exception {
        Document control = XMLUnit.buildControlDocument(FLEABALL);
        transform = new Transform(control);
        Document test = transform.getResultDocument();
        Diff diff = new Diff(control, test);
        assertEquals(diff.toString(), true, diff.identical());
    }

    public void testOutputProperty() throws Exception {
        transform = new Transform(FLEABALL, animal);
        transform.setOutputProperty(OutputKeys.METHOD, "html");
        assertNotEquals(DOG, transform.getResultString());
    }

    public void testDOMSourceAndFile() throws Exception {
        transform = new Transform(XMLUnit.buildControlDocument(FLEABALL), animal);
        assertEquals(DOG, stripLineFeeds(transform.getResultString()));
    }

    public void testDOMSourceAndString() throws Exception {
        FileReader reader = new FileReader(animal);
        try {
            char[] animalXSL = new char[1024];
            int length = reader.read(animalXSL);
            transform = new Transform(XMLUnit.buildControlDocument(FLEABALL),
                                      new String(animalXSL, 0, length));
            assertEquals(DOG, stripLineFeeds(transform.getResultString()));
        } finally {
            reader.close();
        }
    }

    /**
     * Raised by Craig Strong 04.04.2002
     */
    public void testXSLIncludeWithoutSystemId() throws Exception {
        if ("1.5".equals(System.getProperty("java.specification.version"))) {
            System.err.println("skipping test since Java 5's XSLT processor"
                               + " is broken.");
            return;
        }
        String input = "<bug><animal>creepycrawly</animal></bug>";
        String xslWithInclude = test_Constants.XML_DECLARATION
            + test_Constants.XSLT_START
            + test_Constants.XSLT_XML_OUTPUT_NOINDENT
            + "<xsl:template match=\"bug\"><xsl:apply-templates select=\"animal\"/></xsl:template>"
            + "<xsl:include href=\"" + test_Constants.BASEDIR + "/src/tests/resources/animal.xsl\"/>"
            + test_Constants.XSLT_END;
        Transform transform = new Transform(input, xslWithInclude);
        transform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        assertEquals("<creepycrawly/>", transform.getResultString());
    }

    /**
     * Issue 1742826
     */
    public void testURIResolverForStylesheet() throws Exception {
        TestResolver tr = new TestResolver();
        try {
            XMLUnit.setURIResolver(tr);
            String s = "<foo/>";
            String xsl = test_Constants.XML_DECLARATION
                + test_Constants.XSLT_START
                + "<xsl:include href=\"urn:bar\"/>"
                + test_Constants.XSLT_END;
            try {
                XMLUnit.getTransformerFactory()
                    .setErrorListener(new ErrorListener() {
                            public void error(TransformerException ex) {
                                log("error", ex);
                            }
                            public void fatalError(TransformerException ex) {
                                log("fatalError", ex);
                            }
                            public void warning(TransformerException ex) {
                                log("warning", ex);
                            }
                            private void log(String m, TransformerException ex) {
                                System.err.println("method " + m
                                                   + " received exception: "
                                                   + ex.getMessage());
                            }
                        });
                Transform transform = new Transform(s, xsl);
                transform.getResultString();
                fail("should fail because of unknown include URI");
            } catch (ConfigurationException tce) {
                // expected exception
            }
            assertTrue("URIResolver has been called", tr.called);
        } finally {
            XMLUnit.setURIResolver(null);
            XMLUnit.getTransformerFactory().setErrorListener(null);
        }
    }

    private void assertNotEquals(Object expected, Object actual) {
        if (expected.equals(actual)) {
            fail("Expected " + expected + " different to actual!");
        }
    }

    public test_Transform(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        animal = new File(test_Constants.BASEDIR + "/src/tests/resources/animal.xsl");
    }

    private static String stripLineFeeds(String s) {
        int index = s.indexOf(test_Constants.LINE_SEPARATOR);
        while (index > -1) {
            s = s.substring(0, index)
                + s.substring(index + test_Constants.LINE_SEPARATOR.length()) ;
            index = s.indexOf(test_Constants.LINE_SEPARATOR);
        }
        return s;
    }

    private static class TestResolver implements URIResolver {
        private boolean called = false;

        public Source resolve(String h, String b) {
            System.err.println("TestResolver called with parameters "
                               + h + " and " + b);
            called = true;
            return null;
        }
    }
}

