//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.10-b140310.1920 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.12.29 at 04:09:58 PM CET 
//


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
