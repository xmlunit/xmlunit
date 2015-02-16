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

package org.xmlunit.builder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.startsWith;

import org.xmlunit.builder.jaxb.ComplexNode;
import org.xmlunit.builder.jaxb.RootNode;
import org.xmlunit.util.Convert;

import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.StringWriter;


public class JaxbBuilderTest {

    @Test
    public void testMarchal_withJaxbRootObject_shouldReturnSource() throws Exception {
        // prepare test data
        final Object testObject = createRootNode("123");
        
        // run test
        final Source saxSource = new JaxbBuilder(testObject).build();

        // validate result
        final String xmlString = toString(saxSource);
        if (isJdk6()) {
            // JDK 6 uses a generated prefix like "ns2"
            assertThat(xmlString, containsString(":RootNode"));
        } else {
            assertThat(xmlString, startsWith("<test:RootNode"));
        }
        assertThat(xmlString, containsString("<Id>123</Id>"));
    }

    @Test
    public void testMarchal_withCustomMarshaller_shouldReturnSource() throws Exception {
        // prepare test data
        final Object testObject = createRootNode("123");
        Marshaller marshaller = JAXBContext.newInstance(testObject.getClass()).createMarshaller();

        // run test
        final Source saxSource = new JaxbBuilder(testObject).withMarshaller(marshaller ).build();

        // validate result
        final String xmlString = toString(saxSource);
        if (isJdk6()) {
            // JDK 6 uses a generated prefix like "ns2"
            assertThat(xmlString, containsString(":RootNode"));
        } else {
            assertThat(xmlString, startsWith("<test:RootNode"));
        }
        assertThat(xmlString, containsString("<Id>123</Id>"));
    }
    
    @Test
    public void testMarchal_withJaxbObject_shouldReturnSourceInferNameWithoutNamespacePrefix() throws Exception {
        // prepare test data
        final Object testObject = createComplexNode("123");
        
        // run test
        final Source saxSource = new JaxbBuilder(testObject).build();

        // validate result
        final String xmlString = toString(saxSource);
        assertThat(xmlString, startsWith("<complexNode"));
        assertThat(xmlString, containsString("<Id>123</Id>"));
    }


    @Test
    public void testMarchal_withJaxbObjectUseObjectFactory_shouldReturnSourceWithNamespacePrefix() throws Exception {
        // prepare test data
        final Object testObject = createComplexNode("123");

        // run test
        final Source saxSource = new JaxbBuilder(testObject).useObjectFactory().build();

        // validate result
        final String xmlString = toString(saxSource);
        if (isJdk6()) {
            // JDK 6 uses a generated prefix like "ns2"
            assertThat(xmlString, containsString(":ComplexNode"));
        } else {
            assertThat(xmlString, startsWith("<test:ComplexNode"));
        }
        assertThat(xmlString, containsString("<Id>123</Id>"));
    }

    @Test
    public void testMarchal_withJaxbElement_shouldReturnSource() throws Exception {
        // prepare test data
        QName name = new QName("http://www.xmlunit.org/test/complexXml", "ComplexNode", "nsXY");
        final Object testJAXBElement = new JAXBElement<ComplexNode>(name, ComplexNode.class, createComplexNode("123"));

        // run test
        final Source saxSource = new JaxbBuilder(testJAXBElement).build();

        // validate result
        final String xmlString = toString(saxSource);
        if (isJdk6()) {
            // JDK 6 uses the QName prefix "nsXY" or a generated prefix like "ns2"
            assertThat(xmlString, containsString(":ComplexNode"));
        } else {
            assertThat(xmlString, startsWith("<test:ComplexNode"));
        }
        assertThat(xmlString, containsString("<Id>123</Id>"));
    }


    private String toString(Source saxSource) throws Exception {
        final Document document = Convert.toDocument(saxSource);

        DOMSource domSource = new DOMSource(document);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform(domSource, result);

        return writer.toString();
    }


    private RootNode createRootNode(String id) {
        RootNode rootNode = new RootNode();
        rootNode.getIds().add(id);
        return rootNode;
    }


    private ComplexNode createComplexNode(String id) {
        ComplexNode complexNode = new ComplexNode();
        complexNode.setId(id);
        return complexNode;
    }

    private boolean isJdk6() {
        final String javaVersion = System.getProperty("java.version").trim();
        return javaVersion.startsWith("1.6");
    }
}
