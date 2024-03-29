/*
*****************************************************************
Copyright (c) 2001-2008,2015,2022 Jeff Martin, Tim Bacon
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

package org.custommonkey.xmlunit;

import org.custommonkey.xmlunit.exceptions.ConfigurationException;
import org.custommonkey.xmlunit.exceptions.XMLUnitRuntimeException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.EntityResolver;
import org.xmlunit.util.DocumentBuilderFactoryConfigurer;
import org.xmlunit.util.TransformerFactoryConfigurer;

/**
 * Allows access to project control parameters such as which Parser to use and
 * provides some convenience methods for building Documents from Strings etc.
 */
public final class XMLUnit {
    private static DocumentBuilderFactory controlBuilderFactory;
    private static DocumentBuilderFactory testBuilderFactory;
    private static TransformerFactory transformerFactory;
    private static SAXParserFactory saxParserFactory;
    private static boolean ignoreWhitespace = false;
    private static URIResolver uriResolver = null;
    private static EntityResolver testEntityResolver = null;
    private static EntityResolver controlEntityResolver = null;
    private static NamespaceContext namespaceContext = null;
    private static boolean ignoreDiffBetweenTextAndCDATA = false;
    private static boolean ignoreComments = false;
    private static boolean normalize = false;
    private static boolean normalizeWhitespace = false;
    private static boolean ignoreAttributeOrder = true;
    private static String xsltVersion = "1.0";
    private static String xpathFactoryName = null;
    private static boolean expandEntities = false;
    private static boolean compareUnmatched = true;
    private static boolean enableXXEProtection = false;

    private static final String XSLT_VERSION_START = " version=\"";
    private static final String XSLT_VERSION_END = "\">";

    private static final String STRIP_WHITESPACE_STYLESHEET_START
        = new StringBuffer(XMLConstants.XML_DECLARATION)
        .append(XSLTConstants.XSLT_START_NO_VERSION)
        .append(XSLT_VERSION_START)
        .toString();

    private static final String STRIP_WHITESPACE_STYLESHEET_END
        = new StringBuffer(XSLT_VERSION_END)
        .append(XSLTConstants.XSLT_XML_OUTPUT_NOINDENT)
        .append(XSLTConstants.XSLT_STRIP_WHITESPACE)
        .append(XSLTConstants.XSLT_IDENTITY_TEMPLATE)
        .append(XSLTConstants.XSLT_END)
        .toString();

    private static final String STRIP_COMMENTS_STYLESHEET_START
        = new StringBuffer(XMLConstants.XML_DECLARATION)
        .append(XSLTConstants.XSLT_START_NO_VERSION)
        .append(XSLT_VERSION_START)
        .toString();

    private static final String STRIP_COMMENTS_STYLESHEET_END
        = new StringBuffer(XSLT_VERSION_END)
        .append(XSLTConstants.XSLT_XML_OUTPUT_NOINDENT)
        .append(XSLTConstants.XSLT_STRIP_COMMENTS_TEMPLATE)
        .append(XSLTConstants.XSLT_END)
        .toString();

    /**
     * Private constructor.
     * Makes class non-instantiable
     */
    private XMLUnit() {
        // access via static methods please
    }

    /**
     * Overide the DocumentBuilder to use to parse control documents.
     * This is useful when comparing the output of two different
     * parsers. Note: setting the control parser before any test cases
     * are run will affect the test parser as well.
     * @param className class name of a DocumentBuilderFactory
     */
    public static void setControlParser(String className) {
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory", className);
        controlBuilderFactory = null;
        controlBuilderFactory = getControlDocumentBuilderFactory();
    }
    /**
     * Get the <code>DocumentBuilder</code> instance used to parse the control
     * XML in an XMLTestCase.
     * @return parser for control values
     * @throws ConfigurationException if anything is wrong
     */
    public static DocumentBuilder newControlParser()
        throws ConfigurationException {
        try {
            controlBuilderFactory = getControlDocumentBuilderFactory();
            DocumentBuilder builder =
                controlBuilderFactory.newDocumentBuilder();
            if (controlEntityResolver!=null) {
                builder.setEntityResolver(controlEntityResolver);
            }
            return builder;
        } catch (ParserConfigurationException ex) {
            throw new ConfigurationException(ex);
        }
    }

    /**
     * Sets an EntityResolver to be added to all new test parsers.
     * Setting to null will reset to the default EntityResolver
     * @param resolver EntityResolver
     */
    public static void setTestEntityResolver(EntityResolver resolver) {
        testEntityResolver = resolver;
    }

    /**
     * Sets an EntityResolver to be added to all new control parsers.
     * Setting to null will reset to the default EntityResolver
     * @param resolver EntityResolver
     */
    public static void setControlEntityResolver(EntityResolver resolver) {
        controlEntityResolver = resolver;
    }

    /**
     * Obtains the EntityResolver to be added to all new control parsers.
     * @return EntityResolver to be added to all new control parsers
     */
    public static EntityResolver getControlEntityResolver() {
        return controlEntityResolver;
    }

    /**
     * Obtains the EntityResolver to be added to all new test parsers.
     * @return EntityResolver to be added to all new test parsers
     */
    public static EntityResolver getTestEntityResolver() {
        return testEntityResolver;
    }

    /**
     * Get the <code>DocumentBuilderFactory</code> instance used to instantiate
     * parsers for the control XML in an XMLTestCase.
     * @return factory for control parsers
     */
    public static DocumentBuilderFactory getControlDocumentBuilderFactory() {
        if (controlBuilderFactory == null) {
            controlBuilderFactory = DocumentBuilderFactory.newInstance();
            controlBuilderFactory.setNamespaceAware(true);
            if (enableXXEProtection) {
                DocumentBuilderFactoryConfigurer.Default.configure(controlBuilderFactory);
            }
        }
        return controlBuilderFactory;
    }
    /**
     * Override the <code>DocumentBuilderFactory</code> used to instantiate
     * parsers for the control XML in an XMLTestCase.
     * @param factory factory to use
     */
    public static void setControlDocumentBuilderFactory(DocumentBuilderFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("Cannot set control DocumentBuilderFactory to null!");
        }
        controlBuilderFactory = factory;
    }

    /**
     * Overide the DocumentBuilder to use to parser test documents.
     * This is useful when comparing the output of two different
     * parsers. Note: setting the test parser before any test cases
     * are run will affect the control parser as well.
     * @param className class name of a DocumentBuilderFactory
     */
    public static void setTestParser(String className) {
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory", className);
        testBuilderFactory = null;
        testBuilderFactory = getTestDocumentBuilderFactory();
    }
    /**
     * Get the <code>DocumentBuilder</code> instance used to parse the test XML
     * in an XMLTestCase.
     * @return parser for test values
     * @throws ConfigurationException if anything is wrong
     */
    public static DocumentBuilder newTestParser()
        throws ConfigurationException {
        try {
            testBuilderFactory = getTestDocumentBuilderFactory();
            DocumentBuilder builder = testBuilderFactory.newDocumentBuilder();
            if (testEntityResolver!=null) {
                builder.setEntityResolver(testEntityResolver);
            }
            return builder;
        } catch (ParserConfigurationException ex) {
            throw new ConfigurationException(ex);
        }
    }

    /**
     * Get the <code>DocumentBuilderFactory</code> instance used to instantiate
     * parsers for the test XML in an XMLTestCase.
     * @return factory for test parsers
     */
    public static DocumentBuilderFactory getTestDocumentBuilderFactory() {
        if (testBuilderFactory == null) {
            testBuilderFactory = DocumentBuilderFactory.newInstance();
            testBuilderFactory.setNamespaceAware(true);
            if (enableXXEProtection) {
                DocumentBuilderFactoryConfigurer.Default.configure(testBuilderFactory);
            }
        }
        return testBuilderFactory;
    }
    /**
     * Override the <code>DocumentBuilderFactory</code> used to instantiate
     * parsers for the test XML in an XMLTestCase.
     * @param factory factory to use
     */
    public static void setTestDocumentBuilderFactory(DocumentBuilderFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("Cannot set test DocumentBuilderFactory to null!");
        }
        testBuilderFactory = factory;
    }

    /**
     * Whether to ignore whitespace when comparing node values.
     *
     * <p>This method also invokes
     * <code>setIgnoringElementContentWhitespace()</code> on the
     * underlying control AND test document builder factories.</p>
     *
     * <p>Setting this parameter has no effect on {@link
     * #setNormalizeWhitespace whitespace inside texts}.</p>
     *
     * @param ignore whether to ignore whitespace
     */
    public static void setIgnoreWhitespace(boolean ignore){
        ignoreWhitespace = ignore;
        getControlDocumentBuilderFactory().setIgnoringElementContentWhitespace(ignore);
        getTestDocumentBuilderFactory().setIgnoringElementContentWhitespace(ignore);
    }

    /**
     * Whether to ignore whitespace when comparing node values.
     * @return true if whitespace should be ignored when comparing nodes, false
     * otherwise
     */
    public static boolean getIgnoreWhitespace(){
        return ignoreWhitespace;
    }

    /**
     * Utility method to build a Document using the control DocumentBuilder
     * to parse the specified String.
     * @param fromXML the XML to parse
     * @return Document parsed document
     * @throws SAXException if the parser says so
     * @throws IOException on I/O errors
     */
    public static Document buildControlDocument(String fromXML)
        throws SAXException, IOException {
        return buildDocument(newControlParser(), new StringReader(fromXML));
    }

    /**
     * Utility method to build a Document using the control DocumentBuilder
     * and the specified InputSource
     * @param fromSource the XML to parse
     * @return Document parsed document
     * @throws SAXException if the parser says so
     * @throws IOException on I/O errors
     */
    public static Document buildControlDocument(InputSource fromSource)
        throws IOException, SAXException {
        return buildDocument(newControlParser(), fromSource);
    }

    /**
     * Utility method to build a Document using the test DocumentBuilder
     * to parse the specified String.
     * @param fromXML the XML to parse
     * @return Document parsed document
     * @throws SAXException if the parser says so
     * @throws IOException on I/O errors
     */
    public static Document buildTestDocument(String fromXML)
        throws SAXException, IOException {
        return buildDocument(newTestParser(), new StringReader(fromXML));
    }

    /**
     * Utility method to build a Document using the test DocumentBuilder
     * and the specified InputSource
     * @param fromSource the XML to parse
     * @return Document parsed document
     * @throws SAXException if the parser says so
     * @throws IOException on I/O errors
     */
    public static Document buildTestDocument(InputSource fromSource)
        throws IOException, SAXException {
        return buildDocument(newTestParser(), fromSource);
    }

    /**
     * Utility method to build a Document using a specific DocumentBuilder
     * and reading characters from a specific Reader.
     * @param withBuilder DocumentBuilder to use
     * @param fromReader the XML to parse
     * @return Document parsed document
     * @throws SAXException if the parser says so
     * @throws IOException on I/O errors
     */
    public static Document buildDocument(DocumentBuilder withBuilder,
                                         Reader fromReader) throws SAXException, IOException {
        return buildDocument(withBuilder, new InputSource(fromReader));
    }
    /**
     * Utility method to build a Document using a specific DocumentBuilder
     * and a specific InputSource
     * @param withBuilder DocumentBuilder to use
     * @param fromSource the XML to parse
     * @return Document parsed document
     * @throws SAXException if the parser says so
     * @throws IOException on I/O errors
     */
    public static Document buildDocument(DocumentBuilder withBuilder,
                                         InputSource fromSource) throws IOException, SAXException {
        return withBuilder.parse(fromSource);
    }

    /**
     * Overide the transformer to use for XSLT transformations (and by
     * implication serialization and XPaths).
     * This is useful when comparing transformer implementations.
     * @param className name of a TransformerFactory class
     */
    public static void setTransformerFactory(String className) {
        System.setProperty("javax.xml.transform.TransformerFactory",
                           className);
        transformerFactory = null;
        getTransformerFactory();
    }

    /**
     * Get the transformer to use for XSLT transformations (and by
     * implication serialization and XPaths).
     * @return the current transformer factory in use
     * a new instance of the default transformer factory
     */
    public static TransformerFactory getTransformerFactory() {
        if (transformerFactory == null) {
            transformerFactory = newTransformerFactory();
        }
        return transformerFactory;
    }

    /**
     * Get a fresh transformer to use for XSLT transformations (and by
     * implication serialization and XPaths).
     * @return a new instance of the default transformer factory
     */
    static TransformerFactory newTransformerFactory() {
        TransformerFactory tf = TransformerFactory.newInstance();
        if (uriResolver != null) {
            tf.setURIResolver(uriResolver);
        }
        if (enableXXEProtection) {
            tf = TransformerFactoryConfigurer.Default.configure(tf);
        }
        return tf;
    }

    /**
     * Sets the URIResolver to use during transformations.
     * @param resolver URIResolver
     */
    public static void setURIResolver(URIResolver resolver) {
        if (uriResolver != resolver) {
            uriResolver = resolver;
            transformerFactory = null;
            getTransformerFactory();
        }
    }

    /**
     * Gets the URIResolver used during Transformations.
     * @return URIResolver used during Transformations
     */
    public static URIResolver getURIResolver() {
        return uriResolver;
    }

    /**
     * Override the SAX parser to use in tests.
     * Currently only used by {@link Validator Validator class}
     * @param className name of a SAXParserFactory
     */
    public static void setSAXParserFactory(String className) {
        System.setProperty("javax.xml.parsers.SAXParserFactory", className);
        saxParserFactory = null;
        getSAXParserFactory();
    }

    /**
     * Override the SAX parser to use in tests.
     * Currently only used by {@link Validator Validator class}
     * @param factory factory to use
     */
    public static void setSAXParserFactory(SAXParserFactory factory) {
        saxParserFactory = factory;
    }

    /**
     * Get the SAX parser to use in tests.
     *
     * <p>Unless an instance has been given via {@link
     * #setSAXParserFactory(SAXParserFactory) setSAXParserFactory}
     * explicitly, the returned factory will be namespace aware.</p>
     *
     * @return the SAXParserFactory instance used by the {@link
     * Validator Validator} to perform DTD validation
     */
    public static SAXParserFactory getSAXParserFactory() {
        if (saxParserFactory == null) {
            saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setNamespaceAware(true);
        }
        return saxParserFactory;
    }

    private static String getStripWhitespaceStylesheet() {
        return STRIP_WHITESPACE_STYLESHEET_START + getXSLTVersion()
            + STRIP_WHITESPACE_STYLESHEET_END;
    }

    /**
     * Obtain the transformation that will strip whitespace from a DOM
     * containing empty Text nodes
     * @param forDocument document to apply transformation to
     * @return a <code>Transform</code> to do the whitespace stripping
     */
    public static Transform getStripWhitespaceTransform(Document forDocument) {
        return new Transform(forDocument, getStripWhitespaceStylesheet());
    }

    /**
     * Returns a new Document instance that is identical to the one
     * passed in with element content whitespace removed.
     *
     * <p>Will use {@link #getStripWhitespaceTransform
     * getStripWhitespaceTransform} unless we are operating under the
     * severly broken XSLTC Transformer shipping with JDK 1.5.</p>
     * @param forDoc document to apply transformation to
     * @return document without any element content whitespace
     */
    public static Document getWhitespaceStrippedDocument(Document forDoc) {
        String factory = getTransformerFactory().getClass().getName();
        if (XSLTConstants.JAVA5_XSLTC_FACTORY_NAME.equals(factory)) {
            return stripWhiteSpaceWithoutXSLT(forDoc);
        } else {
            return stripWhiteSpaceUsingXSLT(forDoc);
        }
    }

    private static Document stripWhiteSpaceUsingXSLT(Document forDoc) {
        try {
            Transform whitespaceStripper = getStripWhitespaceTransform(forDoc);
            return whitespaceStripper.getResultDocument();
        } catch (TransformerException e) {
            throw new XMLUnitRuntimeException(e.getMessage(), e.getCause());
        }
    }

    private static Document stripWhiteSpaceWithoutXSLT(Document forDoc) {
        Document copy = (Document) forDoc.cloneNode(true);
        stripEmptyTextNodes(copy);
        return copy;
    }

    private static void stripEmptyTextNodes(Node n) {
        final NodeList nl = n.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node child = nl.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                stripEmptyTextNodes(child);
            } else if (child.getNodeType() == Node.TEXT_NODE) {
                String value = child.getNodeValue();
                if (value == null || value.trim().length() == 0) {
                    n.removeChild(child);
                    --i;
                }
            }
        }
    }

    private static String getStripCommentsStylesheet() {
        return STRIP_COMMENTS_STYLESHEET_START + getXSLTVersion()
            + STRIP_COMMENTS_STYLESHEET_END;
    }

    /**
     * Obtain the transformation that will strip comments from a DOM.
     * @param forDocument document to apply transformation to
     * @return a <code>Transform</code> to do the whitespace stripping
     */
    public static Transform getStripCommentsTransform(Document forDocument) {
        return new Transform(forDocument, getStripCommentsStylesheet());
    }

    /**
     * Place holder for current version info.
     * @return current version
     */
    public static String getVersion() {
        return XMLUnit.class.getPackage().getImplementationVersion();
    }

   /**
     * Compare XML documents provided by two InputSource classes
     * @param control Control document
     * @param test Document to test
     * @return Diff object describing differences in documents
     * @throws SAXException if parsing fails
     * @throws IOException on I/O errors
     */
    public static Diff compareXML(InputSource control, InputSource test)
        throws SAXException, IOException {
        return new Diff(control, test);
    }

    /**
     * Compare XML documents provided by two Reader classes
     * @param control Control document
     * @param test Document to test
     * @return Diff object describing differences in documents
     * @throws SAXException if parsing fails
     * @throws IOException on I/O errors
     */
    public static Diff compareXML(Reader control, Reader test)
        throws SAXException, IOException {
        return new Diff(control, test);
    }

    /**
     * Compare XML documents provided by two Reader classes
     * @param control Control document
     * @param test Document to test
     * @return Diff object describing differences in documents
     * @throws SAXException if parsing fails
     * @throws IOException on I/O errors
     */
    public static Diff compareXML(String control, Reader test)
        throws SAXException, IOException {
        return new Diff(new StringReader(control), test);
    }

    /**
     * Compare XML documents provided by two Reader classes
     * @param control Control document
     * @param test Document to test
     * @return Diff object describing differences in documents
     * @throws SAXException if parsing fails
     * @throws IOException on I/O errors
     */
    public static Diff compareXML(Reader control, String test)
        throws SAXException, IOException {
        return new Diff(control, new StringReader(test));
    }

    /**
     * Compare two XML documents provided as strings
     * @param control Control document
     * @param test Document to test
     * @return Diff object describing differences in documents
     * @throws SAXException if parsing fails
     * @throws IOException on I/O errors
     */
    public static Diff compareXML(String control, String test)
        throws SAXException, IOException {
        return new Diff(control, test);
    }

    /**
     * Compare two XML documents provided as strings
     * @param control Control document
     * @param test Document to test
     * @return Diff object describing differences in documents
     */
    public static Diff compareXML(Document control, Document test) {
        return new Diff(control, test);
    }

    /**
     * Get the NamespaceContext to use in XPath tests.
     * @return NamespaceContext to use in XPath tests
     */
    public static NamespaceContext getXpathNamespaceContext() {
        return namespaceContext;
    }

    /**
     * Set the NamespaceContext to use in XPath tests.
     * @param ctx NamespaceContext to use in XPath tests
     */
    public static void setXpathNamespaceContext(NamespaceContext ctx) {
        namespaceContext = ctx;
    }

    /**
     * Obtains an XpathEngine to use in XPath tests.
     * @return XpathEngine
     */
    public static XpathEngine newXpathEngine() {
        XpathEngine eng = new org.custommonkey.xmlunit.jaxp13.Jaxp13XpathEngine();
        if (namespaceContext != null) {
            eng.setNamespaceContext(namespaceContext);
        }
        return eng;
    }

    /**
     * Whether CDATA sections and Text nodes should be considered the same.
     *
     * <p>The default is false.</p>
     *
     * <p>This also set the DocumentBuilderFactory's {@link
     * javax.xml.parsers.DocumentBuilderFactory#setCoalescing
     * coalescing} flag on the factories for the control and test
     * document.</p>
     *
     * @param b whether CDATA sections and Text nodes should be considered the same
     */
    public static void setIgnoreDiffBetweenTextAndCDATA(boolean b) {
        ignoreDiffBetweenTextAndCDATA = b;
        getControlDocumentBuilderFactory().setCoalescing(b);
        getTestDocumentBuilderFactory().setCoalescing(b);
    }

    /**
     * Whether CDATA sections and Text nodes should be considered the same.
     *
     * @return false by default
     */
    public static boolean getIgnoreDiffBetweenTextAndCDATA() {
        return ignoreDiffBetweenTextAndCDATA;
    }

    /**
     * Whether comments should be ignored.
     *
     * <p>The default value is false</p>
     * @param b whether comments are ignored
     */
    public static void setIgnoreComments(boolean b) {
        ignoreComments = b;
    }

    /**
     * Whether comments should be ignored.
     *
     * <p>The default value is false</p>
     * @return whether comments are ignored
     */
    public static boolean getIgnoreComments() {
        return ignoreComments;
    }

    /**
     * Whether Text nodes should be normalized.
     *
     * <p>The default value is false</p>
     *
     * <p><b>Note:</b> if you are only working with documents read
     * from streams (like files or network connections) or working
     * with strings, there is no reason to change the default since
     * the XML parser is required to normalize the documents.  If you
     * are testing {@link org.w3c.dom.Document Document} instances you've
     * created in code, you may want to alter the default
     * behavior.</p>
     *
     * <p><b>Note2:</b> depending on the XML parser or XSLT
     * transformer you use, setting {@link #setIgnoreWhitespace
     * ignoreWhitespace} or {@link #setIgnoreComments ignoreComments}
     * to true may have already normalized your document and this
     * setting doesn't have any effect anymore.</p>
     * @param b whether Text nodes should be normalizedd
     */
    public static void setNormalize(boolean b) {
        normalize = b;
    }

    /**
     * Whether Text nodes should be normalized.
     *
     * <p>The default value is false</p>
     * @return whether Text nodes should be normalizedd
     */
    public static boolean getNormalize() {
        return normalize;
    }

    /**
     * Whether whitespace characters inside text nodes or attributes
     * should be "normalized".
     *
     * <p>Normalized in this context means that all whitespace is
     * replaced by the space character and adjacent whitespace
     * characters are collapsed to a single space character.  It will
     * also trim the resulting character content on both ends.</p>
     *
     * <p>The default value is false.</p>
     *
     * <p>Setting this parameter has no effect on {@link
     * #setIgnoreWhitespace ignorable whitespace}.</p>
     * @param b whether whitespace characters inside text nodes or attributes should be normalizedd
     */
    public static void setNormalizeWhitespace(boolean b) {
        normalizeWhitespace = b;
    }

    /**
     * Whether whitespace characters inside text nodes or attributes
     * should be "normalized".
     *
     * <p>Normalized in this context means that all whitespace is
     * replaced by the space character and adjacent whitespace
     * characters are collapsed to a single space character.</p>
     *
     * <p>The default value is false.</p>
     * @return whether whitespace characters inside text nodes or attributes should be normalizedd
     */
    public static boolean getNormalizeWhitespace() {
        return normalizeWhitespace;
    }

    /**
     * Whether to ignore the order of attributes on an element.
     *
     * <p>The order of attributes has never been relevant for XML
     * documents, still XMLUnit can consider two pieces of XML
     * not-identical (but similar) if they differ in order of
     * attributes.  Set this option to true to compare the order.</p>
     *
     * <p>The default value is true</p>
     * @param b whether to ignore the order of attributes on an element
     */
    public static void setIgnoreAttributeOrder(boolean b) {
        ignoreAttributeOrder = b;
    }

    /**
     * Whether to ignore the order of attributes on an element.
     *
     * <p>The order of attributes has never been relevant for XML
     * documents, still XMLUnit can consider two pieces of XML
     * not-identical (but similar) if they differ in order of
     * attributes.  Set this option to true to compare the order.</p>
     *
     * <p>The default value is true</p>
     * @return whether to ignore the order of attributes on an element
     */
    public static boolean getIgnoreAttributeOrder() {
        return ignoreAttributeOrder;
    }

    /**
     * Sets the XSLT version to set on stylesheets used internally.
     *
     * <p>Defaults to "1.0".</p>
     *
     * @param s XSLT version to set on stylesheets
     * @throws ConfigurationException if the argument cannot be parsed
     * as a positive number.
     */
    public static void setXSLTVersion(String s) {
        try {
            Number n = NumberFormat.getInstance(Locale.US).parse(s);
            if (n.doubleValue() < 0) {
                throw new ConfigurationException(s + " doesn't reperesent a"
                                                 + " positive number.");
            }
        } catch (ParseException e) {
            throw new ConfigurationException(e);
        }
        xsltVersion = s;
    }

    /**
     * The XSLT version set on stylesheets used internally.
     *
     * <p>Defaults to "1.0".</p>
     * @return XSLT version to set on stylesheets
     */
    public static String getXSLTVersion() {
        return xsltVersion;
    }

    /**
     * Sets the class to use as XPathFactory when using JAXP 1.3.
     * @param className name of an XPathFactory
     */
    public static void setXPathFactory(String className) {
        xpathFactoryName = className;
    }

    /**
     * Gets the class to use as XPathFactory when using JAXP 1.3.
     * @return name of the XPathFactory
     */
    public static String getXPathFactory() {
        return xpathFactoryName;
    }

    /**
     * XSLT stylesheet element using the configured XSLT version.
     */
    static String getXSLTStart() {
        return XSLTConstants.XSLT_START_NO_VERSION
            + XSLT_VERSION_START + getXSLTVersion() + XSLT_VERSION_END;
    }

    /**
     * Whether the parser shall be instructed to expand entity references.
     *
     * <p>Defaults to false.</p>
     *
     * @see javax.xml.parsers.DocumentBuilderFactory#setExpandEntityReferences
     * @param b whether the parser shall be instructed to expand entity references
     */
    public static void setExpandEntityReferences(boolean b) {
        expandEntities = b;
        getControlDocumentBuilderFactory().setExpandEntityReferences(b);
        getTestDocumentBuilderFactory().setExpandEntityReferences(b);
    }

    /**
     * Whether the parser shall be instructed to expand entity references.
     * @return whether the parser shall be instructed to expand entity references
     */
    public static boolean getExpandEntityReferences() {
        return expandEntities;
    }

    /**
     * Whether to compare unmatched control nodes to unmatched test nodes.
     *
     * <p>Defaults to true.</p>
     * @param b whether to compare unmatched control nodes to unmatched test nodes
     */
    public static void setCompareUnmatched(boolean b) {
        compareUnmatched = b;
    }

    /**
     * Whether unmatched control nodes should be compared to unmatched
     * test nodes.
     *
     * @return true by default
     */
    public static boolean getCompareUnmatched() {
        return compareUnmatched;
    }

    /**
     * Whether to enable XXE protection on the factories used by this class.
     *
     * @since XMLUnit 2.6.0
     * @see "https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet"
     * @param b whether to enable XXE protection on the factories used by this class
     */
    public static void setEnableXXEProtection(boolean b) {
        enableXXEProtection = b;
    }

    /**
     * Whether XXE protection is enabled on the factories used by this class.
     *
     * @since XMLUnit 2.6.0
     * @see "https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet"
     * @return whether to enable XXE protection on the factories used by this class
     */
    public static boolean getEnableXXEProtection() {
        return enableXXEProtection;
    }

}
