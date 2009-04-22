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

import javax.xml.transform.OutputKeys;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * JUnit test for SimpleSerializer
 */
public class test_SimpleSerializer extends TestCase {
    private SimpleSerializer serializer ;

    public void testNode() throws Exception {
        String simpleXML = "<season><spring id=\"1\"><eg>daffodils</eg></spring></season>";
        Document doc = XMLUnit.buildControlDocument(simpleXML);

        serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        assertEquals(false, simpleXML.equals(serializer.serialize(doc)));

        serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        assertEquals(simpleXML, serializer.serialize(doc));

        Element testElem = doc.createElement("eg");
        Text lamb = doc.createTextNode("lamb");
        testElem.appendChild(lamb);

        assertEquals("<eg>lamb</eg>", serializer.serialize(testElem));
    }

    public void setUp() {
        serializer = new SimpleSerializer();
    }

    public test_SimpleSerializer(String name) {
        super(name);
    }

    public static TestSuite suite() {
        return new TestSuite(test_SimpleSerializer.class);
    }

}

