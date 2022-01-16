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
package org.xmlunit.builder.jaxb;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the org.xmlunit.test.jaxb package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ComplexNode_QNAME = new QName("https://www.xmlunit.org/test/complexXml", "ComplexNode");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.xmlunit.test.jaxb
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RootNode }
     *
     */
    public RootNode createRootNode() {
        return new RootNode();
    }

    /**
     * Create an instance of {@link ComplexNode }
     *
     */
    public ComplexNode createComplexNode() {
        return new ComplexNode();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ComplexNode }{@code >}
     */
    @XmlElementDecl(namespace = "https://www.xmlunit.org/test/complexXml", name = "ComplexNode")
    public JAXBElement<ComplexNode> createComplexNode(ComplexNode value) {
        return new JAXBElement<ComplexNode>(_ComplexNode_QNAME, ComplexNode.class, null, value);
    }

}
