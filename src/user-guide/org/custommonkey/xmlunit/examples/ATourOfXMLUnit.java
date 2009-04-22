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

package org.custommonkey.xmlunit.examples;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import org.custommonkey.xmlunit.*;

/**
 * All code snippets from the "A Tour of XMLUnit" section of the the
 * User Guide.
 */
public class ATourOfXMLUnit extends XMLTestCase {
    public ATourOfXMLUnit(String name) {
        super(name);
    }

    // never invoked
    private void configure() {
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
            "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
        System.setProperty("javax.xml.parsers.SAXParserFactory",
            "org.apache.xerces.jaxp.SAXParserFactoryImpl");
        System.setProperty("javax.xml.transform.TransformerFactory",
            "org.apache.xalan.processor.TransformerFactoryImpl");
        XMLUnit
            .setControlParser("org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
        XMLUnit
            .setTestParser("org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
        XMLUnit
            .setSAXParserFactory("org.apache.xerces.jaxp.SAXParserFactoryImpl");
        XMLUnit
            .setTransformerFactory("org.apache.xalan.processor.TransformerFactoryImpl");
    }

    public void testForEquality() throws Exception {
        String myControlXML = "<msg><uuid>0x00435A8C</uuid></msg>";
        String myTestXML = "<msg><localId>2376</localId></msg>";
        assertXMLEqual("Comparing test xml to control xml",
                       myControlXML, myTestXML);
    }

    public void testXMLIdentical()throws Exception {
        String myControlXML =
            "<struct><int>3</int><boolean>false</boolean></struct>";
        String myTestXML =
            "<struct><boolean>false</boolean><int>3</int></struct>";
        Diff myDiff = new Diff(myControlXML, myTestXML);
        assertTrue("XML similar " + myDiff.toString(),
                   myDiff.similar());
        assertTrue("XML identical " + myDiff.toString(),
                   myDiff.identical());
    }

    public void testAllDifferences() throws Exception {
        String myControlXML = "<news><item id=\"1\">War</item>"
            + "<item id=\"2\">Plague</item>"
            + "<item id=\"3\">Famine</item></news>";
        String myTestXML = "<news><item id=\"1\">Peace</item>"
            + "<item id=\"2\">Health</item>"
            + "<item id=\"3\">Plenty</item></news>";
        DetailedDiff myDiff = new DetailedDiff(new Diff(myControlXML,
                                                        myTestXML));
        List allDifferences = myDiff.getAllDifferences();
        assertEquals(myDiff.toString(), 2, allDifferences.size());
    }

    public void testCompareToSkeletonXML() throws Exception {
        String myControlXML = "<location><street-address>22 any street</street-address><postcode>XY00 99Z</postcode></location>";
        String myTestXML = "<location><street-address>20 east cheap</street-address><postcode>EC3M 1EB</postcode></location>";
        DifferenceListener myDifferenceListener = new IgnoreTextAndAttributeValuesDifferenceListener();
        Diff myDiff = new Diff(myControlXML, myTestXML);
        myDiff.overrideDifferenceListener(myDifferenceListener);
        assertTrue("test XML matches control skeleton XML",
                   myDiff.similar());
    }

    public void testRepeatedChildElements() throws Exception {
        String myControlXML = "<suite>"
            + "<test status=\"pass\">FirstTestCase</test>"
            + "<test status=\"pass\">SecondTestCase</test></suite>";
        String myTestXML = "<suite>"
            + "<test status=\"pass\">SecondTestCase</test>"
            + "<test status=\"pass\">FirstTestCase</test></suite>";
        assertXMLNotEqual("Repeated child elements in different sequence order are not equal by default",
                          myControlXML, myTestXML);
        Diff myDiff = new Diff(myControlXML, myTestXML);
        myDiff.overrideElementQualifier(new ElementNameAndTextQualifier());
        assertXMLEqual("But they are equal when an ElementQualifier controls which test element is compared with each control element",
                       myDiff, true);
    }

    public void testXSLTransformation() throws Exception {
        String myInputXML = "...";
        File myStylesheetFile = new File("...");
        Transform myTransform = new Transform(myInputXML, myStylesheetFile);
        String myExpectedOutputXML = "...";
        Diff myDiff = new Diff(myExpectedOutputXML, myTransform);
        assertTrue("XSL transformation worked as expected", myDiff.similar());
    }

    public void testAnotherXSLTransformation() throws Exception {
        File myInputXMLFile = new File("...");
        File myStylesheetFile = new File("...");
        Transform myTransform = new Transform(
                                              new StreamSource(myInputXMLFile),
                                              new StreamSource(myStylesheetFile));
        Document myExpectedOutputXML =
            XMLUnit.buildDocument(XMLUnit.getControlParser(),
                                  new FileReader("..."));
        Diff myDiff = new Diff(myExpectedOutputXML,
                               myTransform.getResultDocument());
        assertTrue("XSL transformation worked as expected", myDiff.similar());
    }

    public void testValidation() throws Exception {
        XMLUnit.getTestDocumentBuilderFactory().setValidating(true);
        // As the document is parsed it is validated against its referenced DTD
        Document myTestDocument = XMLUnit.buildTestDocument("...");
        String mySystemId = "...";
        String myDTDUrl = new File("...").toURL().toExternalForm();
        Validator myValidator = new Validator(myTestDocument, mySystemId,
                                              myDTDUrl);
        assertTrue("test document validates against unreferenced DTD",
                   myValidator.isValid());
    }

    public void testXPaths() throws Exception {
        String mySolarSystemXML = "<solar-system>"
            + "<planet name='Earth' position='3' supportsLife='yes'/>"
            + "<planet name='Venus' position='4'/></solar-system>";
        assertXpathExists("//planet[@name='Earth']", mySolarSystemXML);
        assertXpathNotExists("//star[@name='alpha centauri']",
                             mySolarSystemXML);
        assertXpathsEqual("//planet[@name='Earth']",
                          "//planet[@position='3']", mySolarSystemXML);
        assertXpathsNotEqual("//planet[@name='Venus']",
                             "//planet[@supportsLife='yes']",
                             mySolarSystemXML);
    }

    public void testXPathValues() throws Exception {
        String myJavaFlavours = "<java-flavours>"
            + "<jvm current='some platforms'>1.1.x</jvm>"
            + "<jvm current='no'>1.2.x</jvm>"
            + "<jvm current='yes'>1.3.x</jvm>"
            + "<jvm current='yes' latest='yes'>1.4.x</jvm></javaflavours>";
        assertXpathEvaluatesTo("2", "count(//jvm[@current='yes'])",
                               myJavaFlavours);
        assertXpathValuesEqual("//jvm[4]/@latest", "//jvm[4]/@current",
                               myJavaFlavours);
        assertXpathValuesNotEqual("//jvm[2]/@current",
                                  "//jvm[3]/@current", myJavaFlavours);
    }

    public void testXpathsInHTML() throws Exception {
        String someBadlyFormedHTML = "<html><title>Ugh</title>"
            + "<body><h1>Heading<ul>"
            + "<li id='1'>Item One<li id='2'>Item Two";
        TolerantSaxDocumentBuilder tolerantSaxDocumentBuilder =
            new TolerantSaxDocumentBuilder(XMLUnit.getTestParser());
        HTMLDocumentBuilder htmlDocumentBuilder =
            new HTMLDocumentBuilder(tolerantSaxDocumentBuilder);
        Document wellFormedDocument =
            htmlDocumentBuilder.parse(someBadlyFormedHTML);
        assertXpathEvaluatesTo("Item One", "/html/body//li[@id='1']",
                               wellFormedDocument);
    }

    public void testCountingNodeTester() throws Exception {
        String testXML = "<fibonacci><val>1</val><val>2</val><val>3</val>"
            + "<val>5</val><val>9</val></fibonacci>";
        CountingNodeTester countingNodeTester = new CountingNodeTester(4);
        assertNodeTestPasses(testXML, countingNodeTester, Node.TEXT_NODE);
    }

    public void testCustomNodeTester() throws Exception {
        String testXML = "<fibonacci><val>1</val><val>2</val><val>3</val>"
            + "<val>5</val><val>9</val></fibonacci>";
        NodeTest nodeTest = new NodeTest(testXML);
        assertNodeTestPasses(nodeTest, new FibonacciNodeTester(),
                             new short[] {Node.TEXT_NODE,
                                          Node.ELEMENT_NODE},
                             true);
    }

    private class FibonacciNodeTester extends AbstractNodeTester {
        private int nextVal = 1, lastVal = 1, priorVal = 0;

        public void testText(Text text) throws NodeTestException {
            int val = Integer.parseInt(text.getData());
            if (nextVal != val) {
                throw new NodeTestException("Incorrect value", text);
            }
            nextVal = val + lastVal;
            priorVal = lastVal;
            lastVal = val;
        }

        public void testElement(Element element) throws NodeTestException {
            String name = element.getLocalName();
            if ("fibonacci".equals(name) || "val".equals(name)) {
                return;
            }
            throw new NodeTestException("Unexpected element", element);
        }

        public void noMoreNodes(NodeTest nodeTest) throws NodeTestException {
        }
    }
}

