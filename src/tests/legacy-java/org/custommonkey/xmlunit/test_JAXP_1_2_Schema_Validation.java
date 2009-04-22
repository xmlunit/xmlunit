/*
******************************************************************
Copyright (c) 200, Jeff Martin, Tim Bacon
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
import java.io.FileInputStream;
import java.io.FileReader;

import org.xml.sax.InputSource;

import junit.framework.TestCase;

public class test_JAXP_1_2_Schema_Validation extends TestCase {

    private Validator validator;

    public void testUsingStringURI() throws Exception {
        File xsdFile = new File(test_Constants.BASEDIR + "/tests/etc/Book.xsd");
        assertTrue("xsdFile " + xsdFile.getAbsolutePath() + " exists", 
                   xsdFile.exists());

        File xmlFile = new File(test_Constants.BASEDIR 
                                + "/tests/etc/BookXsdGeneratedNoSchema.xml");
        assertTrue("xmlFile " + xmlFile.getAbsolutePath() + " exists", 
                   xmlFile.exists());

        validator = new Validator(new FileReader(xmlFile));

        validator.useXMLSchema(true);
        validator.setJAXP12SchemaSource(xsdFile.getAbsolutePath());

        validator.assertIsValid();
    }

    public void testUsingInputStream() throws Exception {
        File xsdFile = new File(test_Constants.BASEDIR + "/tests/etc/Book.xsd");
        assertTrue("xsdFile " + xsdFile.getAbsolutePath() + " exists", 
                   xsdFile.exists());

        File xmlFile = new File(test_Constants.BASEDIR 
                                + "/tests/etc/BookXsdGeneratedNoSchema.xml");
        assertTrue("xmlFile " + xmlFile.getAbsolutePath() + " exists", 
                   xmlFile.exists());

        validator = new Validator(new FileReader(xmlFile));

        validator.useXMLSchema(true);
        validator.setJAXP12SchemaSource(new FileInputStream(xsdFile));

        validator.assertIsValid();
    }

    public void testUsingInputSource() throws Exception {
        File xsdFile = new File(test_Constants.BASEDIR + "/tests/etc/Book.xsd");
        assertTrue("xsdFile " + xsdFile.getAbsolutePath() + " exists", 
                   xsdFile.exists());

        File xmlFile = new File(test_Constants.BASEDIR
                                + "/tests/etc/BookXsdGeneratedNoSchema.xml");
        assertTrue("xmlFile " + xmlFile.getAbsolutePath() + " exists", 
                   xmlFile.exists());

        validator = new Validator(new FileReader(xmlFile));

        validator.useXMLSchema(true);
        validator
            .setJAXP12SchemaSource(new InputSource(new FileReader(xsdFile)));

        validator.assertIsValid();
    }

    public void testUsingAFile() throws Exception {
        File xsdFile = new File(test_Constants.BASEDIR + "/tests/etc/Book.xsd");
        assertTrue("xsdFile " + xsdFile.getAbsolutePath() + " exists", 
                   xsdFile.exists());

        File xmlFile = new File(test_Constants.BASEDIR
                                + "/tests/etc/BookXsdGeneratedNoSchema.xml");
        assertTrue("xmlFile " + xmlFile.getAbsolutePath() + " exists", 
                   xmlFile.exists());

        validator = new Validator(new FileReader(xmlFile));

        validator.useXMLSchema(true);
        validator.setJAXP12SchemaSource(xsdFile);

        validator.assertIsValid();
    }
    
    public void testUsingObjectArrayContainingStringURI() throws Exception {
        File xsdFile = new File(test_Constants.BASEDIR + "/tests/etc/Book.xsd");
        assertTrue("xsdFile " + xsdFile.getAbsolutePath() + " exists", 
                   xsdFile.exists());

        File xmlFile = new File(test_Constants.BASEDIR 
                                + "/tests/etc/BookXsdGeneratedNoSchema.xml");
        assertTrue("xmlFile " + xmlFile.getAbsolutePath() + " exists", 
                   xmlFile.exists());

        validator = new Validator(new FileReader(xmlFile));

        validator.useXMLSchema(true);
        validator.setJAXP12SchemaSource(new Object[] {
                                            xsdFile.getAbsolutePath()
                                        });

        validator.assertIsValid();
    }
    
    public void testUsingNonExistentFile() throws Exception {
        File xsdFile = new File(test_Constants.BASEDIR + "/tests/etc/BookDoesNotExist.xsd");
        assertFalse("xsdFile " + xsdFile.getAbsolutePath() + " exists", 
                    xsdFile.exists());

        File xmlFile = new File(test_Constants.BASEDIR
                                + "/tests/etc/BookXsdGeneratedNoSchema.xml");
        assertTrue("xmlFile " + xmlFile.getAbsolutePath() + " exists", 
                   xmlFile.exists());

        validator = new Validator(new FileReader(xmlFile));

        validator.useXMLSchema(true);
        validator.setJAXP12SchemaSource(xsdFile);

        assertFalse("Isn't valid since no schema can be found", validator.isValid());
    }
    
    public void testUsingInvalidXML() throws Exception {
        File xsdFile = new File(test_Constants.BASEDIR + "/tests/etc/Book.xsd");
        assertTrue("xsdFile " + xsdFile.getAbsolutePath() + " exists", 
                   xsdFile.exists());

        File xmlFile = new File(test_Constants.BASEDIR
                                + "/tests/etc/InvalidBookXsdGeneratedNoSchema.xml");
        assertTrue("xmlFile " + xmlFile.getAbsolutePath() + " exists", 
                   xmlFile.exists());

        validator = new Validator(new FileReader(xmlFile));

        validator.useXMLSchema(true);
        validator.setJAXP12SchemaSource(xsdFile);

        assertFalse("Isn't valid since no schema can be found", validator.isValid());
    }
}
