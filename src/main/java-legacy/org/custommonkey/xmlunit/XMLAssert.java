/*
******************************************************************
Copyright (c) 2001-2007,2011 Jeff Martin, Tim Bacon
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

import org.custommonkey.xmlunit.exceptions.ConfigurationException;
import org.custommonkey.xmlunit.exceptions.XpathException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;

import junit.framework.Assert;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * Collection of static methods so that XML assertion facilities are available
 * in any class, not just test suites. Thanks to Andrew McCormick and others for
 * suggesting this refactoring.<br/>
 * Available assertion methods are:
 * <ul>
 * <li><strong><code>assertXMLEqual</code></strong><br/>
 *  assert that two pieces of XML markup are <i>similar</i></li>
 * <li><strong><code>assertXMLNotEqual</code></strong><br/>
 *  assert that two pieces of XML markup are <i>different</i></li>
 * <li><strong><code>assertXMLIdentical</code></strong><br/>
 *  assert that two pieces of XML markup are <i>identical</i>. In most cases
 *  this assertion is too strong and <code>assertXMLEqual</code> is sufficient</li>
 * <li><strong><code>assertXpathExists</code></strong><br/> 
 * assert that an XPath expression matches at least one node</li>
 * <li><strong><code>assertXpathNotExists</code></strong><br/> 
 * assert that an XPath expression does not match any nodes</li>
 * <li><strong><code>assertXpathsEqual</code></strong><br/>
 *  assert that the nodes obtained by executing two Xpaths
 *  are <i>similar</i></li>
 * <li><strong><code>assertXpathsNotEqual</code></strong><br/>
 *  assert that the nodes obtained by executing two Xpaths
 *  are <i>different</i></li>
 * <li><strong><code>assertXpathValuesEqual</code></strong><br/>
 *  assert that the flattened String obtained by executing two Xpaths
 *  are <i>similar</i></li>
 * <li><strong><code>assertXpathValuesNotEqual</code></strong><br/>
 *  assert that the flattened String obtained by executing two Xpaths
 *  are <i>different</i></li>
 * <li><strong><code>assertXpathEvaluatesTo</code></strong><br/>
 *  assert that the flattened String obtained by executing an Xpath
 *  is a particular value</li>
 * <li><strong><code>assertXMLValid</code></strong><br/>
 *  assert that a piece of XML markup is valid with respect to a DTD: either
 *  by using the markup's own DTD or a different DTD</li>
 * <li><strong><code>assertNodeTestPasses</code></strong><br/>
 *  assert that a piece of XML markup passes a {@link NodeTest NodeTest}</li>
 * </ul>
 * All underlying similarity and difference testing is done using 
 * {@link Diff Diff} instances which can be instantiated and evaluated
 * independently of this class.
 * @see Diff#similar()
 * @see Diff#identical()
 * <br />Examples and more at <a href="http://xmlunit.sourceforge.net"/>xmlunit.sourceforge.net</a>
 */
public class XMLAssert extends Assert implements XSLTConstants {
    
    protected XMLAssert(){
        super();
    }

    /**
     * Assert that the result of an XML comparison is or is not similar.
     * @param diff the result of an XML comparison
     * @param assertion true if asserting that result is similar
     */
    public static void assertXMLEqual(Diff diff, boolean assertion) {
        assertXMLEqual(null, diff, assertion);
    }
    
    /**
     * Assert that the result of an XML comparison is or is not similar.
     * @param msg additional message to display if assertion fails
     * @param diff the result of an XML comparison
     * @param assertion true if asserting that result is similar
     */
    public static void assertXMLEqual(String msg, Diff diff,
                                      boolean assertion) {       
        if (assertion != diff.similar()) {
            fail(getFailMessage(msg, diff));
        }
    }

    private static String getFailMessage(String msg, Diff diff) {
        StringBuffer sb = new StringBuffer();
        if (msg != null && msg.length() > 0) {
            sb.append(msg).append(", ");
        }
        return sb.append(diff.toString()).toString();
    }

    /**
     * Assert that the result of an XML comparison is or is not identical
     * @param diff the result of an XML comparison
     * @param assertion true if asserting that result is identical
     */
    public static void assertXMLIdentical(Diff diff, boolean assertion) {
        assertXMLIdentical(null, diff, assertion);
    }

    /**
     * Assert that the result of an XML comparison is or is not identical
     * @param msg Message to display if assertion fails
     * @param diff the result of an XML comparison
     * @param assertion true if asserting that result is identical
     */
    public static void assertXMLIdentical(String msg, Diff diff, boolean assertion) {
        if (assertion != diff.identical()) {
            fail(getFailMessage(msg, diff));
        }
    }

    /**
     * Assert that two XML documents are similar
     * @param control XML to be compared against
     * @param test XML to be tested
     * @throws SAXException
     * @throws IOException
     */
    public static void assertXMLEqual(InputSource control, InputSource test)
        throws SAXException, IOException {
        assertXMLEqual(null, control, test);
    }

    /**
     * Assert that two XML documents are similar
     * @param control XML to be compared against
     * @param test XML to be tested
     * @throws SAXException
     * @throws IOException
     */
    public static void assertXMLEqual(String control, String test)
        throws SAXException, IOException {
        assertXMLEqual(null, control, test);
    }

    /**
     * Assert that two XML documents are similar
     * @param control XML to be compared against
     * @param test XML to be tested
     */
    public static void assertXMLEqual(Document control, Document test) {
        assertXMLEqual(null, control, test);
    }

    /**
     * Assert that two XML documents are similar
     * @param control XML to be compared against
     * @param test XML to be tested
     * @throws SAXException
     * @throws IOException
     */
    public static void assertXMLEqual(Reader control, Reader test)
        throws SAXException, IOException {
        assertXMLEqual(null, control, test);
    }

    /**
     * Assert that two XML documents are similar
     * @param err Message to be displayed on assertion failure
     * @param control XML to be compared against
     * @param test XML to be tested
     * @throws SAXException
     * @throws IOException
     */
    public static void assertXMLEqual(String err, InputSource control,
                                      InputSource test)
        throws SAXException, IOException {
        Diff diff = new Diff(control, test);
        assertXMLEqual(err, diff, true);
    }

    /**
     * Assert that two XML documents are similar
     * @param err Message to be displayed on assertion failure
     * @param control XML to be compared against
     * @param test XML to be tested
     * @throws SAXException
     * @throws IOException
     */
    public static void assertXMLEqual(String err, String control, String test)
        throws SAXException, IOException {
        Diff diff = new Diff(control, test);
        assertXMLEqual(err, diff, true);
    }

    /**
     * Assert that two XML documents are similar
     * @param err Message to be displayed on assertion failure
     * @param control XML to be compared against
     * @param test XML to be tested
     */
    public static void assertXMLEqual(String err, Document control,
                                      Document test) {
        Diff diff = new Diff(control, test);
        assertXMLEqual(err, diff, true);
    }

    /**
     * Assert that two XML documents are similar
     * @param err Message to be displayed on assertion failure
     * @param control XML to be compared against
     * @param test XML to be tested
     * @throws SAXException
     * @throws IOException
     */
    public static void assertXMLEqual(String err, Reader control, Reader test)
        throws SAXException, IOException {
        Diff diff = new Diff(control, test);
        assertXMLEqual(err, diff, true);
    }

    /**
     * Assert that two XML documents are NOT similar
     * @param control XML to be compared against
     * @param test XML to be tested
     * @throws SAXException
     * @throws IOException
     */
    public static void assertXMLNotEqual(InputSource control, InputSource test)
        throws SAXException, IOException {
        assertXMLNotEqual(null, control, test);
    }

    /**
     * Assert that two XML documents are NOT similar
     * @param control XML to be compared against
     * @param test XML to be tested
     * @throws SAXException
     * @throws IOException
     */
    public static void assertXMLNotEqual(String control, String test)
        throws SAXException, IOException {
        assertXMLNotEqual(null, control, test);
    }

    /**
     * Assert that two XML documents are NOT similar
     * @param control XML to be compared against
     * @param test XML to be tested
     */
    public static void assertXMLNotEqual(Document control, Document test) {
        assertXMLNotEqual(null, control, test);
    }

    /**
     * Assert that two XML documents are NOT similar
     * @param control XML to be compared against
     * @param test XML to be tested
     * @throws SAXException
     * @throws IOException
     */
    public static void assertXMLNotEqual(Reader control, Reader test)
        throws SAXException, IOException {
        assertXMLNotEqual(null, control, test);
    }

    /**
     * Assert that two XML documents are NOT similar
     * @param err Message to be displayed on assertion failure
     * @param control XML to be compared against
     * @param test XML to be tested
     * @throws SAXException
     * @throws IOException
     */
    public static void assertXMLNotEqual(String err, InputSource control,
                                         InputSource test)
        throws SAXException, IOException {
        Diff diff = new Diff(control, test);
        assertXMLEqual(err, diff, false);
    }

    /**
     * Assert that two XML documents are NOT similar
     * @param err Message to be displayed on assertion failure
     * @param control XML to be compared against
     * @param test XML to be tested
     * @throws SAXException
     * @throws IOException
     */
    public static void assertXMLNotEqual(String err, String control, String test)
        throws SAXException, IOException {
        Diff diff = new Diff(control, test);
        assertXMLEqual(err, diff, false);
    }

    /**
     * Assert that two XML documents are NOT similar
     * @param err Message to be displayed on assertion failure
     * @param control XML to be compared against
     * @param test XML to be tested
     */
    public static void assertXMLNotEqual(String err, Document control,
                                         Document test) {
        Diff diff = new Diff(control, test);
        assertXMLEqual(err, diff, false);
    }

    /**
     * Assert that two XML documents are NOT similar
     * @param err Message to be displayed on assertion failure
     * @param control XML to be compared against
     * @param test XML to be tested
     * @throws SAXException
     * @throws IOException
     */
    public static void assertXMLNotEqual(String err, Reader control, Reader test)
        throws SAXException, IOException {
        Diff diff = new Diff(control, test);
        assertXMLEqual(err, diff, false);
    }

    /**
     * Assert that the node lists of two Xpaths in the same document are equal
     * @param xpathOne
     * @param xpathTwo
     * @param document
     * @see XpathEngine
     */
    public static void assertXpathsEqual(String controlXpath, String testXpath,
                                         Document document)
        throws XpathException {
        assertXpathsEqual(controlXpath, document, testXpath, document);
    }

    /**
     * Assert that the node lists of two Xpaths in the same document are equal
     * @param xpathOne
     * @param xpathTwo
     * @param document
     * @see XpathEngine
     */
    public static void assertXpathsEqual(String controlXpath, String testXpath,
                                         InputSource document)
        throws SAXException, IOException, XpathException {
        assertXpathsEqual(controlXpath, testXpath,
                          XMLUnit.buildControlDocument(document));
    }

    /**
     * Assert that the node lists of two Xpaths in the same XML string are
     * equal
     * @param xpathOne
     * @param xpathTwo
     * @param inXMLString
     * @throws SAXException
     * @throws IOException
     */
    public static void assertXpathsEqual(String controlXpath, String testXpath,
                                         String inXMLString)
        throws SAXException, IOException, XpathException {
        assertXpathsEqual(controlXpath, testXpath,
                          XMLUnit.buildControlDocument(inXMLString));
    }

    /**
     * Assert that the node lists of two Xpaths in two documents are equal
     * @param xpathOne
     * @param xpathTwo
     * @param controlDocument
     * @param testDocument
     * @see XpathEngine
     */
    public static void assertXpathsEqual(String controlXpath,
                                         InputSource controlDocument,
                                         String testXpath,
                                         InputSource testDocument)
        throws SAXException, IOException, XpathException {
        assertXpathsEqual(controlXpath,
                          XMLUnit.buildControlDocument(controlDocument),
                          testXpath,
                          XMLUnit.buildTestDocument(testDocument));
    }

    /**
     * Assert that the node lists of two Xpaths in two XML strings are equal
     * @param xpathOne
     * @param inControlXMLString
     * @param xpathTwo
     * @param inTestXMLString
     * @throws SAXException
     * @throws IOException
     */
    public static void assertXpathsEqual(String controlXpath,
                                         String inControlXMLString,
                                         String testXpath,
                                         String inTestXMLString)
        throws SAXException, IOException,
               XpathException {
        assertXpathsEqual(
                          controlXpath, XMLUnit.buildControlDocument(inControlXMLString),
                          testXpath, XMLUnit.buildTestDocument(inTestXMLString));
    }

    /**
     * Assert that the node lists of two Xpaths in two documents are equal
     * @param xpathOne
     * @param xpathTwo
     * @param controlDocument
     * @param testDocument
     * @see XpathEngine
     */
    public static void assertXpathsEqual(String controlXpath,
                                         Document controlDocument,
                                         String testXpath,
                                         Document testDocument)
        throws XpathException {
        assertXpathEquality(controlXpath, controlDocument, testXpath,
                            testDocument, true);
    }

    /**
     * Assert that the node lists of two Xpaths in the same document are NOT equal
     * @param xpathOne
     * @param xpathTwo
     * @param document
     * @see XpathEngine
     */
    public static void assertXpathsNotEqual(String controlXpath,
                                            String testXpath,
                                            Document document)
        throws XpathException {
        assertXpathsNotEqual(controlXpath, document, testXpath, document);
    }

    /**
     * Assert that the node lists of two Xpaths in the same document are NOT equal
     * @param xpathOne
     * @param xpathTwo
     * @param document
     * @see XpathEngine
     */
    public static void assertXpathsNotEqual(String controlXpath,
                                            String testXpath,
                                            InputSource document)
        throws SAXException, IOException, XpathException {
        assertXpathsNotEqual(controlXpath, testXpath,
                             XMLUnit.buildControlDocument(document));
    }

    /**
     * Assert that the node lists of two Xpaths in the same XML string are NOT
     * equal
     * @param xpathOne
     * @param xpathTwo
     * @param inXMLString
     * @throws SAXException
     * @throws IOException
     */
    public static void assertXpathsNotEqual(String controlXpath,
                                            String testXpath,
                                            String inXMLString)
        throws SAXException, IOException, XpathException {
        assertXpathsNotEqual(controlXpath, testXpath,
                             XMLUnit.buildControlDocument(inXMLString));
    }

    /**
     * Assert that the node lists of two Xpaths in two XML strings are NOT equal
     * @param xpathOne
     * @param inControlXMLString
     * @param xpathTwo
     * @param inTestXMLString
     * @throws SAXException
     * @throws IOException
     */
    public static void assertXpathsNotEqual(String controlXpath,
                                            String inControlXMLString,
                                            String testXpath,
                                            String inTestXMLString)
        throws SAXException, IOException,
               XpathException {
        assertXpathsNotEqual(
                             controlXpath, XMLUnit.buildControlDocument(inControlXMLString),
                             testXpath, XMLUnit.buildTestDocument(inTestXMLString));
    }

    /**
     * Assert that the node lists of two Xpaths in two XML strings are
     * NOT equal
     * @param xpathOne
     * @param controlDocument
     * @param xpathTwo
     * @param testDocument
     * @throws SAXException
     * @throws IOException
     */
    public static void assertXpathsNotEqual(String controlXpath,
                                            InputSource controlDocument,
                                            String testXpath,
                                            InputSource testDocument)
        throws SAXException, IOException, XpathException {
        assertXpathsNotEqual(controlXpath,
                             XMLUnit.buildControlDocument(controlDocument),
                             testXpath,
                             XMLUnit.buildTestDocument(testDocument));
    }

    /**
     * Assert that the node lists of two Xpaths in two documents are NOT equal
     * @param xpathOne
     * @param xpathTwo
     * @param document
     * @see XpathEngine
     */
    public static void assertXpathsNotEqual(String controlXpath,
                                            Document controlDocument,
                                            String testXpath,
                                            Document testDocument)
        throws XpathException {
        assertXpathEquality(controlXpath, controlDocument, testXpath,
                            testDocument, false);
    }

    /**
     * Assert that the node lists of two Xpaths in two documents are
     * equal or not.
     * @param xpathOne
     * @param xpathTwo
     * @param document
     * @param equality whether the values should be equal.
     * @see XpathEngine
     */
    private static void assertXpathEquality(String controlXpath,
                                            Document controlDocument,
                                            String testXpath,
                                            Document testDocument,
                                            boolean equal)
        throws XpathException {
        XpathEngine xpath = XMLUnit.newXpathEngine();
        Diff diff = new Diff(asXpathResultDocument(XMLUnit.newControlParser(),
                                                   xpath.getMatchingNodes(controlXpath,
                                                                          controlDocument)),
                             asXpathResultDocument(XMLUnit.newTestParser(),
                                                   xpath.getMatchingNodes(testXpath,
                                                                          testDocument)));
        assertXMLEqual(diff, equal);
    }

    /**
     * Assert that the evaluation of two Xpaths in the same document are equal
     * @param xpathOne
     * @param xpathTwo
     * @param document
     * @see XpathEngine
     */
    public static void assertXpathValuesEqual(String controlXpath,
                                              String testXpath,
                                              Document document)
        throws XpathException {
        assertXpathValuesEqual(controlXpath, document, testXpath, document);
    }

    /**
     * Assert that the evaluation of two Xpaths in the same XML string are
     *  equal
     * @param xpathOne
     * @param xpathTwo
     * @param document
     * @throws SAXException
     * @throws IOException
     */
    public static void assertXpathValuesEqual(String controlXpath,
                                              String testXpath,
                                              InputSource document)
        throws SAXException, IOException, XpathException {
        assertXpathValuesEqual(controlXpath, testXpath,
                               XMLUnit.buildControlDocument(document));
    }

    /**
     * Assert that the evaluation of two Xpaths in the same XML string are
     *  equal
     * @param xpathOne
     * @param xpathTwo
     * @param inXMLString
     * @throws SAXException
     * @throws IOException
     */
    public static void assertXpathValuesEqual(String controlXpath,
                                              String testXpath,
                                              String inXMLString)
        throws SAXException, IOException,
               XpathException {
        assertXpathValuesEqual(controlXpath, testXpath,
                               XMLUnit.buildControlDocument(inXMLString));
    }

    /**
     * Assert that the evaluation of two Xpaths in two XML strings are equal
     * @param xpathOne
     * @param control
     * @param xpathTwo
     * @param test
     * @throws SAXException
     * @throws IOException
     */
    public static void assertXpathValuesEqual(String controlXpath,
                                              InputSource control,
                                              String testXpath,
                                              InputSource test)
        throws SAXException, IOException,  XpathException {
        assertXpathValuesEqual(controlXpath,
                               XMLUnit.buildControlDocument(control),
                               testXpath,
                               XMLUnit.buildTestDocument(test));
    }

    /**
     * Assert that the evaluation of two Xpaths in two XML strings are equal
     * @param xpathOne
     * @param inControlXMLString
     * @param xpathTwo
     * @param inTestXMLString
     * @throws SAXException
     * @throws IOException
     */
    public static void assertXpathValuesEqual(String controlXpath,
                                              String inControlXMLString,
                                              String testXpath,
                                              String inTestXMLString)
        throws SAXException, IOException,
               XpathException {
        assertXpathValuesEqual(controlXpath,
                               XMLUnit.buildControlDocument(inControlXMLString),
                               testXpath,
                               XMLUnit.buildTestDocument(inTestXMLString));
    }

    /**
     * Assert that the evaluation of two Xpaths in two documents are equal
     * @param xpathOne
     * @param xpathTwo
     * @param document
     * @see XpathEngine
     */
    public static void assertXpathValuesEqual(String controlXpath,
                                              Document controlDocument,
                                              String testXpath,
                                              Document testDocument)
        throws XpathException {
        XpathEngine xpath = XMLUnit.newXpathEngine();
        assertEquals(xpath.evaluate(controlXpath, controlDocument),
                     xpath.evaluate(testXpath, testDocument));
    }

    /**
     * Assert that the evaluation of two Xpaths in the same XML string are
     * NOT equal
     * @param xpathOne
     * @param xpathTwo
     * @param control
     * @throws SAXException
     * @throws IOException
     */
    public static void assertXpathValuesNotEqual(String controlXpath,
                                                 String testXpath,
                                                 InputSource control)
        throws SAXException, IOException, XpathException {
        assertXpathValuesNotEqual(controlXpath, testXpath,
                                  XMLUnit.buildControlDocument(control));
    }

    /**
     * Assert that the evaluation of two Xpaths in the same XML string are
     * NOT equal
     * @param xpathOne
     * @param xpathTwo
     * @param inXMLString
     * @throws SAXException
     * @throws IOException
     */
    public static void assertXpathValuesNotEqual(String controlXpath,
                                                 String testXpath,
                                                 String inXMLString)
        throws SAXException, IOException,
               XpathException {
        assertXpathValuesNotEqual(controlXpath, testXpath,
                                  XMLUnit.buildControlDocument(inXMLString));
    }

    /**
     * Assert that the evaluation of two Xpaths in the same document are
     * NOT equal
     * @param xpathOne
     * @param xpathTwo
     * @param document
     */
    public static void assertXpathValuesNotEqual(String controlXpath,
                                                 String testXpath,
                                                 Document document)
        throws XpathException {
        assertXpathValuesNotEqual(controlXpath, document, testXpath, document);
    }

    /**
     * Assert that the evaluation of two Xpaths in two XML strings are
     * NOT equal
     * @param xpathOne
     * @param control
     * @param xpathTwo
     * @param test
     * @throws SAXException
     * @throws IOException
     */
    public static void assertXpathValuesNotEqual(String controlXpath,
                                                 InputSource control,
                                                 String testXpath,
                                                 InputSource test)
        throws SAXException, IOException, XpathException {
        assertXpathValuesNotEqual(controlXpath,
                                  XMLUnit.buildControlDocument(control),
                                  testXpath,
                                  XMLUnit.buildTestDocument(test));
    }

    /**
     * Assert that the evaluation of two Xpaths in two XML strings are
     * NOT equal
     * @param xpathOne
     * @param inControlXMLString
     * @param xpathTwo
     * @param inTestXMLString
     * @throws SAXException
     * @throws IOException
     */
    public static void assertXpathValuesNotEqual(String controlXpath,
                                                 String inControlXMLString,
                                                 String testXpath,
                                                 String inTestXMLString)
        throws SAXException, IOException,
               XpathException {
        assertXpathValuesNotEqual(controlXpath,
                                  XMLUnit.buildControlDocument(inControlXMLString),
                                  testXpath,
                                  XMLUnit.buildTestDocument(inTestXMLString));
    }

    /**
     * Assert that the evaluation of two Xpaths in two documents are
     * NOT equal
     * @param xpathOne
     * @param xpathTwo
     * @param document
     */
    public static void assertXpathValuesNotEqual(String controlXpath,
                                                 Document controlDocument,
                                                 String testXpath,
                                                 Document testDocument)
        throws XpathException {
        XpathEngine xpath = XMLUnit.newXpathEngine();
        String control = xpath.evaluate(controlXpath, controlDocument);
        String test = xpath.evaluate(testXpath, testDocument);
        if (control!=null) {
            if (control.equals(test)) {
                fail("Expected test value NOT to be equal to control but both were "
                     + test);
            }
        } else if (test != null) {
            fail("control xPath evaluated to empty node set, "
                 + "but test xPath evaluated to " + test);
        }
    }

    /**
     * Assert the value of an Xpath expression in an XML document.
     * @param expectedValue
     * @param xpathExpression
     * @param control
     * @throws SAXException
     * @throws IOException
     * @see XpathEngine which provides the underlying evaluation mechanism
     */
    public static void assertXpathEvaluatesTo(String expectedValue,
                                              String xpathExpression,
                                              InputSource control)
        throws SAXException, IOException,
               XpathException {
        Document document = XMLUnit.buildControlDocument(control);
        assertXpathEvaluatesTo(expectedValue, xpathExpression, document);
    }

    /**
     * Assert the value of an Xpath expression in an XML String
     * @param expectedValue
     * @param xpathExpression
     * @param inXMLString
     * @throws SAXException
     * @throws IOException
     * @see XpathEngine which provides the underlying evaluation mechanism
     */
    public static void assertXpathEvaluatesTo(String expectedValue,
                                              String xpathExpression,
                                              String inXMLString)
        throws SAXException, IOException,
               XpathException {
        Document document = XMLUnit.buildControlDocument(inXMLString);
        assertXpathEvaluatesTo(expectedValue, xpathExpression, document);
    }

    /**
     * Assert the value of an Xpath expression in an DOM Document
     * @param expectedValue
     * @param xpathExpression
     * @param inDocument
     * @see XpathEngine which provides the underlying evaluation mechanism
     */
    public static void assertXpathEvaluatesTo(String expectedValue,
                                              String xpathExpression,
                                              Document inDocument)
        throws XpathException {
        XpathEngine simpleXpathEngine = XMLUnit.newXpathEngine();
        assertEquals(expectedValue,
                     simpleXpathEngine.evaluate(xpathExpression, inDocument));
    }

    /**
     * Assert that a specific XPath exists in some given XML
     * @param inXpathExpression
     * @param control
     * @see XpathEngine which provides the underlying evaluation mechanism
     */
    public static void assertXpathExists(String xPathExpression, 
                                         InputSource control)
        throws IOException, SAXException, XpathException {
        Document inDocument = XMLUnit.buildControlDocument(control);
        assertXpathExists(xPathExpression, inDocument);
    }
    
    /**
     * Assert that a specific XPath exists in some given XML
     * @param inXpathExpression
     * @param inXMLString
     * @see XpathEngine which provides the underlying evaluation mechanism
     */
    public static void assertXpathExists(String xPathExpression, 
                                         String inXMLString)
        throws IOException, SAXException, XpathException {
        Document inDocument = XMLUnit.buildControlDocument(inXMLString);
        assertXpathExists(xPathExpression, inDocument);
    }
    
    /**
     * Assert that a specific XPath exists in some given XML
     * @param inXpathExpression
     * @param inDocument
     * @see XpathEngine which provides the underlying evaluation mechanism
     */
    public static void assertXpathExists(String xPathExpression, 
                                         Document inDocument)
        throws XpathException {
        XpathEngine simpleXpathEngine = XMLUnit.newXpathEngine();
        NodeList nodeList = simpleXpathEngine.getMatchingNodes(
                                                               xPathExpression, inDocument);
        int matches = nodeList.getLength();
        assertTrue("Expecting to find matches for Xpath " + 
                   xPathExpression, matches > 0);
    }

    /**
     * Assert that a specific XPath does NOT exist in some given XML
     * @param inXpathExpression
     * @param control
     * @see XpathEngine which provides the underlying evaluation mechanism
     */
    public static void assertXpathNotExists(String xPathExpression, 
                                            InputSource control)
        throws IOException, SAXException, XpathException {
        Document inDocument = XMLUnit.buildControlDocument(control);
        assertXpathNotExists(xPathExpression, inDocument);
    }
    
    /**
     * Assert that a specific XPath does NOT exist in some given XML
     * @param inXpathExpression
     * @param inXMLString
     * @see XpathEngine which provides the underlying evaluation mechanism
     */
    public static void assertXpathNotExists(String xPathExpression, 
                                            String inXMLString)
        throws IOException, SAXException, XpathException {
        Document inDocument = XMLUnit.buildControlDocument(inXMLString);
        assertXpathNotExists(xPathExpression, inDocument);
    }
    
    /**
     * Assert that a specific XPath does NOT exist in some given XML
     * @param inXpathExpression
     * @param inDocument
     * @see XpathEngine which provides the underlying evaluation mechanism
     */
    public static void assertXpathNotExists(String xPathExpression, 
                                            Document inDocument)
        throws XpathException {
        XpathEngine simpleXpathEngine = XMLUnit.newXpathEngine();
        NodeList nodeList = simpleXpathEngine.getMatchingNodes(
                                                               xPathExpression, inDocument);
        int matches = nodeList.getLength();
        assertEquals("Should be zero matches for Xpath " + 
                     xPathExpression, 0, matches);
    }
    
    /**
     * Assert that an InputSource containing XML contains valid XML:
     * the document must contain a DOCTYPE declaration to be validated
     * @param xml
     * @throws SAXException
     * @throws ConfigurationException if validation could not be turned on
     * @see Validator
     */
    public static void assertXMLValid(InputSource xml)
        throws SAXException, ConfigurationException {
        assertXMLValid(new Validator(xml));
    }

    /**
     * Assert that a String containing XML contains valid XML: the String must
     * contain a DOCTYPE declaration to be validated
     * @param xmlString
     * @throws SAXException
     * @throws ConfigurationException if validation could not be turned on
     * @see Validator
     */
    public static void assertXMLValid(String xmlString)
        throws SAXException, ConfigurationException {
        assertXMLValid(new Validator(xmlString));
    }

    /**
     * Assert that an InputSource containing XML contains valid XML:
     * the document must contain a DOCTYPE to be validated, but the
     * validation will use the systemId to obtain the DTD
     * @param xml
     * @param systemId
     * @throws SAXException
     * @throws ConfigurationException if validation could not be turned on
     * @see Validator
     */
    public static void assertXMLValid(InputSource xml, String systemId)
        throws SAXException, ConfigurationException {
        assertXMLValid(new Validator(xml, systemId));
    }

    /**
     * Assert that a String containing XML contains valid XML: the String must
     * contain a DOCTYPE to be validated, but the validation will use the
     * systemId to obtain the DTD
     * @param xmlString
     * @param systemId
     * @throws SAXException
     * @throws ConfigurationException if validation could not be turned on
     * @see Validator
     */
    public static void assertXMLValid(String xmlString, String systemId)
        throws SAXException, ConfigurationException {
        assertXMLValid(new Validator(xmlString, systemId));
    }

    /**
     * Assert that a piece of XML contains valid XML: the document
     * will be given a DOCTYPE to be validated with the name and
     * systemId specified regardless of whether it already contains a
     * doctype declaration.
     * @param xml
     * @param systemId
     * @param doctype
     * @throws SAXException
     * @throws ConfigurationException if validation could not be turned on
     * @see Validator
     */
    public static void assertXMLValid(InputSource xml, String systemId,
                                      String doctype)
        throws SAXException, ConfigurationException {
        assertXMLValid(new Validator(xml, systemId, doctype));
    }

    /**
     * Assert that a String containing XML contains valid XML: the String will
     * be given a DOCTYPE to be validated with the name and systemId specified
     * regardless of whether it already contains a doctype declaration.
     * @param xmlString
     * @param systemId
     * @param doctype
     * @throws SAXException
     * @throws ConfigurationException if validation could not be turned on
     * @see Validator
     */
    public static void assertXMLValid(String xmlString, String systemId, String doctype)
        throws SAXException, ConfigurationException {
        assertXMLValid(new Validator(new StringReader(xmlString), systemId, doctype));
    }

    /**
     * Assert that a Validator instance returns <code>isValid() == true</code>
     * @param validator
     */
    public static void assertXMLValid(Validator validator) {
        assertEquals(validator.toString(), true, validator.isValid());
    }

    /**
     * Execute a <code>NodeTest<code> for a single node type
     * and assert that it passes
     * @param xml XML to be tested
     * @param tester The test strategy
     * @param nodeType The node type to be tested: constants defined
     *  in {@link Node org.w3c.dom.Node} e.g. <code>Node.ELEMENT_NODE</code>
     * @throws SAXException
     * @throws IOException
     * @see AbstractNodeTester
     * @see CountingNodeTester
     */
    public static void assertNodeTestPasses(InputSource xml, NodeTester tester,
                                            short nodeType)
        throws SAXException, IOException {
        NodeTest test = new NodeTest(xml);
        assertNodeTestPasses(test, tester, new short[] {nodeType}, true);
    }

    /**
     * Execute a <code>NodeTest<code> for a single node type
     * and assert that it passes
     * @param xmlString XML to be tested
     * @param tester The test strategy
     * @param nodeType The node type to be tested: constants defined
     *  in {@link Node org.w3c.dom.Node} e.g. <code>Node.ELEMENT_NODE</code>
     * @throws SAXException
     * @throws IOException
     * @see AbstractNodeTester
     * @see CountingNodeTester
     */
    public static void assertNodeTestPasses(String xmlString, NodeTester tester,
                                            short nodeType)
        throws SAXException, IOException {
        NodeTest test = new NodeTest(xmlString);
        assertNodeTestPasses(test, tester, new short[] {nodeType}, true);
    }

    /**
     * Execute a <code>NodeTest<code> for multiple node types and make an
     * assertion about it whether it is expected to pass
     * @param test a NodeTest instance containing the XML source to be tested
     * @param tester The test strategy
     * @param nodeTypes The node types to be tested: constants defined
     *  in {@link Node org.w3c.dom.Node} e.g. <code>Node.ELEMENT_NODE</code>
     * @param assertion true if the test is expected to pass, false otherwise
     * @see AbstractNodeTester
     * @see CountingNodeTester
     */
    public static void assertNodeTestPasses(NodeTest test, NodeTester tester,
                                            short[] nodeTypes, boolean assertion) {
        try {
            test.performTest(tester, nodeTypes);
            if (!assertion) {
                fail("Expected node test to fail, but it passed!");
            }
        } catch (NodeTestException e) {
            if (assertion) {
                fail("Expected node test to pass, but it failed! "
                     + e.getMessage());
            }
        }
    }

    private static Document asXpathResultDocument(final DocumentBuilder builder,
                                                  final NodeList nodes) {
        final Document d = builder.newDocument();
        final Element root = d.createElement("xpathResult");
        d.appendChild(root);
        final int length = nodes.getLength();
        for (int i = 0; i < length; i++) {
            Node n = d.importNode(nodes.item(i), true);
            if (n instanceof Attr) {
                root.setAttributeNodeNS((Attr) n);
            } else {
                root.appendChild(n);
            }
        }
        return d;
    }
}
