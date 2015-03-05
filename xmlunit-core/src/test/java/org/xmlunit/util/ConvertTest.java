/*
  This file is licensed to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package org.xmlunit.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlunit.ConfigurationException;
import org.xmlunit.TestResources;
import org.xmlunit.XMLUnitException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class ConvertTest {

    @Mock
    private TransformerFactory tFac;

    @Mock
    private Transformer transformer;

    @Mock
    private DocumentBuilderFactory dFac;

    @Mock
    private DocumentBuilder builder;

    @Before
    public void setupMocks() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(tFac.newTransformer()).thenReturn(transformer);
        when(dFac.newDocumentBuilder()).thenReturn(builder);
    }
    
    private static void convertToInputSourceAndAssert(Source s)
        throws Exception {
        DocumentBuilder b =
            DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document d = b.parse(Convert.toInputSource(s));
        documentAsserts(d);
    }

    private static void documentAsserts(Document d) {
        assertThat(d, IsNull.notNullValue());
        assertThat(d.getDocumentElement().getTagName(), is("animal"));
    }

    @Test public void streamSourceToInputSource() throws Exception {
        convertToInputSourceAndAssert(new StreamSource(new File(TestResources.ANIMAL_FILE)));
    }

    @Test public void domSourceToInputSource() throws Exception {
        Document d = animalDocument();
        convertToInputSourceAndAssert(new DOMSource(d));
    }

    @Test public void saxSourceToInputSource() throws Exception {
        InputSource s = new InputSource(new FileInputStream(TestResources.ANIMAL_FILE));
        convertToInputSourceAndAssert(new SAXSource(s));
    }

    private static void convertToDocumentAndAssert(Source s) {
        documentAsserts(Convert.toDocument(s));
    }

    @Test public void streamSourceToDocument() throws Exception {
        convertToDocumentAndAssert(new StreamSource(new File(TestResources.ANIMAL_FILE)));
    }

    @Test public void domSourceToDocument() throws Exception {
        Document d = animalDocument();
        convertToDocumentAndAssert(new DOMSource(d));
        assertSame(d, Convert.toDocument(new DOMSource(d)));
    }

    @Test public void saxSourceToDocument() throws Exception {
        InputSource s = new InputSource(new FileInputStream(TestResources.ANIMAL_FILE));
        convertToDocumentAndAssert(new SAXSource(s));
    }

    @Test public void domElementToDocument() throws Exception {
        Document d = animalDocument();
        convertToDocumentAndAssert(new DOMSource(d.getDocumentElement()));
        assertNotSame(d,
                      Convert.toDocument(new DOMSource(d.getDocumentElement())));
    }

    private static void convertToNodeAndAssert(Source s) {
        Node n = Convert.toNode(s);
        Document d = n instanceof Document ? (Document) n : n.getOwnerDocument();
        documentAsserts(d);
    }

    @Test public void streamSourceToNode() throws Exception {
        convertToNodeAndAssert(new StreamSource(new File(TestResources.ANIMAL_FILE)));
    }

    @Test public void domSourceToNode() throws Exception {
        Document d = animalDocument();
        convertToNodeAndAssert(new DOMSource(d));
        assertSame(d, Convert.toNode(new DOMSource(d)));
    }

    private static void convertToNodeWithDocBuilderFactoryAndAssert(Source s) {
        Node n = Convert.toNode(s, DocumentBuilderFactory.newInstance());
        Document d = n instanceof Document ? (Document) n : n.getOwnerDocument();
        documentAsserts(d);
    }

    @Test public void streamSourceToNodeWithDocBuilderFactory() throws Exception {
        convertToNodeAndAssert(new StreamSource(new File(TestResources.ANIMAL_FILE)));
    }

    @Test public void domSourceToNodeWithDocBuilderFactory() throws Exception {
        Document d = animalDocument();
        convertToNodeWithDocBuilderFactoryAndAssert(new DOMSource(d));
        assertSame(d, Convert.toNode(new DOMSource(d)));
    }

    @Test public void saxSourceToNode() throws Exception {
        InputSource s = new InputSource(new FileInputStream(TestResources.ANIMAL_FILE));
        convertToNodeWithDocBuilderFactoryAndAssert(new SAXSource(s));
    }

    @Test public void domElementToNode() throws Exception {
        Document d = animalDocument();
        convertToNodeAndAssert(new DOMSource(d));
        assertSame(d.getDocumentElement(),
                   Convert.toNode(new DOMSource(d.getDocumentElement())));
    }

    @Test(expected=ConfigurationException.class)
    public void shouldMapTransformerConfigurationException() throws Exception {
        when(tFac.newTransformer())
            .thenThrow(new TransformerConfigurationException());
        Convert.toInputSource(new DOMSource(animalDocument()), tFac);
    }

    @Test
    public void shouldMapTransformerException() throws Exception {
        doThrow(new TransformerException("foo"))
            .when(transformer)
            .transform(any(Source.class), any(Result.class));
        try {
            Convert.toInputSource(new DOMSource(animalDocument()), tFac);
            fail("should have thrown XMLUnitException");
        } catch (XMLUnitException ex) {
            // assert this is not a XMLUnitException subclass
            assertEquals(XMLUnitException.class, ex.getClass());
        }
    }

    @Test(expected=ConfigurationException.class)
    public void shouldMapParserConfigurationException() throws Exception {
        when(dFac.newDocumentBuilder())
            .thenThrow(new ParserConfigurationException());
        Convert.toDocument(new StreamSource(new File(TestResources.ANIMAL_FILE)),
                           dFac);
    }
    
    @Test
    public void shouldMapSAXException() throws Exception {
        doThrow(new SAXException())
            .when(builder)
            .parse(any(InputSource.class));

        try {
            Convert.toDocument(new StreamSource(new File(TestResources.ANIMAL_FILE)),
                               dFac);
        } catch (XMLUnitException ex) {
            // assert this is not a XMLUnitException subclass
            assertEquals(XMLUnitException.class, ex.getClass());
        }
    }

    @Test
    public void shouldMapIOException() throws Exception {
        doThrow(new IOException())
            .when(builder)
            .parse(any(InputSource.class));

        try {
            Convert.toDocument(new StreamSource(new File(TestResources.ANIMAL_FILE)),
                               dFac);
        } catch (XMLUnitException ex) {
            // assert this is not a XMLUnitException subclass
            assertEquals(XMLUnitException.class, ex.getClass());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void namespaceContextWontReturnNamespaceForNullPrefix() {
        NamespaceContext ctx = Convert.toNamespaceContext(new HashMap<String, String>());
        ctx.getNamespaceURI(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void namespaceContextWontReturnPrefixForNullURI() {
        NamespaceContext ctx = Convert.toNamespaceContext(new HashMap<String, String>());
        ctx.getPrefix(null);
    }
    
    @Test
    public void namespaceContextReturnsNsUri() {
        NamespaceContext ctx = Convert.toNamespaceContext(new HashMap<String, String>());
        assertEquals(XMLConstants.XML_NS_URI,
                     ctx.getNamespaceURI(XMLConstants.XML_NS_PREFIX));
    }

    @Test
    public void namespaceContextReturnsNsPrefix() {
        NamespaceContext ctx = Convert.toNamespaceContext(new HashMap<String, String>());
        assertEquals(XMLConstants.XML_NS_PREFIX,
                     ctx.getPrefix(XMLConstants.XML_NS_URI));
    }
    
    @Test
    public void namespaceContextReturnsXmlAttributeNsUri() {
        NamespaceContext ctx = Convert.toNamespaceContext(new HashMap<String, String>());
        assertEquals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI,
                     ctx.getNamespaceURI(XMLConstants.XMLNS_ATTRIBUTE));
    }

    @Test
    public void namespaceContextReturnsXmlAttributeNsPrefix() {
        NamespaceContext ctx = Convert.toNamespaceContext(new HashMap<String, String>());
        assertEquals(XMLConstants.XMLNS_ATTRIBUTE,
                     ctx.getPrefix(XMLConstants.XMLNS_ATTRIBUTE_NS_URI));
    }

    @Test
    public void namespaceContextReturnsExpectedNsUri() {
        Map<String, String> m = new HashMap<String, String>();
        m.put("foo", "bar");
        NamespaceContext ctx = Convert.toNamespaceContext(m);
        assertEquals("bar", ctx.getNamespaceURI("foo"));
    }

    @Test
    public void namespaceContextReturnsExpectedPrefix() {
        Map<String, String> m = new HashMap<String, String>();
        m.put("foo", "bar");
        NamespaceContext ctx = Convert.toNamespaceContext(m);
        assertEquals("foo", ctx.getPrefix("bar"));
    }

    @Test
    public void namespaceContextReturnsFirstPrefix() {
        Map<String, String> m = new LinkedHashMap<String, String>();
        m.put("foo", "bar");
        m.put("baz", "bar");
        NamespaceContext ctx = Convert.toNamespaceContext(m);
        assertEquals("foo", ctx.getPrefix("bar"));
    }

    @Test
    public void namespaceContextReturnsAllPrefixes() {
        Map<String, String> m = new LinkedHashMap<String, String>();
        m.put("foo", "bar");
        m.put("baz", "bar");
        NamespaceContext ctx = Convert.toNamespaceContext(m);
        assertArrayEquals(new String[] { "foo", "baz" },
                          toArray(ctx.getPrefixes("bar")));
    }

    private static Document animalDocument() throws Exception {
        DocumentBuilder b =
            DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return b.parse(new File(TestResources.ANIMAL_FILE));
    }

    private static String[] toArray(Iterator<String> i) {
        ArrayList<String> al = new ArrayList<String>();
        while (i.hasNext()) {
            al.add(i.next());
        }
        return al.toArray(new String[al.size()]);
    }
}
