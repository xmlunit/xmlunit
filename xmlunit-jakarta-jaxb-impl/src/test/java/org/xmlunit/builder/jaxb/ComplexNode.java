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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for ComplexNode complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ComplexNode">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SingleNestedComplexNode" type="{https://www.xmlunit.org/test/complexXml}ComplexNode" minOccurs="0"/>
 *         &lt;element name="Id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Date" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="Number" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="Double" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="Decimal" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="NestedComplexNode" type="{https://www.xmlunit.org/test/complexXml}ComplexNode" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ComplexNode", propOrder = {
    "singleNestedComplexNode",
    "id",
    "date",
    "number",
    "_double",
    "decimal",
    "nestedComplexNodes"
})
public class ComplexNode
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "SingleNestedComplexNode")
    protected ComplexNode singleNestedComplexNode;
    @XmlElement(name = "Id")
    protected String id;
    @XmlElement(name = "Date", type = String.class)
    @XmlJavaTypeAdapter(DateTimeToDateAdapter.class)
    @XmlSchemaType(name = "dateTime")
    protected Date date;
    @XmlElement(name = "Number")
    protected Integer number;
    @XmlElement(name = "Double")
    protected Double _double;
    @XmlElement(name = "Decimal")
    protected BigDecimal decimal;
    @XmlElement(name = "NestedComplexNode")
    protected List<ComplexNode> nestedComplexNodes;

    /**
     * Gets the value of the singleNestedComplexNode property.
     *
     * @return
     *     possible object is
     *     {@link ComplexNode }
     *
     */
    public ComplexNode getSingleNestedComplexNode() {
        return singleNestedComplexNode;
    }

    /**
     * Sets the value of the singleNestedComplexNode property.
     *
     * @param value
     *     allowed object is
     *     {@link ComplexNode }
     *
     */
    public void setSingleNestedComplexNode(ComplexNode value) {
        this.singleNestedComplexNode = value;
    }

    /**
     * Gets the value of the id property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the date property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the value of the date property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDate(Date value) {
        this.date = value;
    }

    /**
     * Gets the value of the number property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getNumber() {
        return number;
    }

    /**
     * Sets the value of the number property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setNumber(Integer value) {
        this.number = value;
    }

    /**
     * Gets the value of the double property.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getDouble() {
        return _double;
    }

    /**
     * Sets the value of the double property.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setDouble(Double value) {
        this._double = value;
    }

    /**
     * Gets the value of the decimal property.
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getDecimal() {
        return decimal;
    }

    /**
     * Sets the value of the decimal property.
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setDecimal(BigDecimal value) {
        this.decimal = value;
    }

    /**
     * Gets the value of the nestedComplexNodes property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nestedComplexNodes property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNestedComplexNodes().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ComplexNode }
     *
     *
     */
    public List<ComplexNode> getNestedComplexNodes() {
        if (nestedComplexNodes == null) {
            nestedComplexNodes = new ArrayList<ComplexNode>();
        }
        return this.nestedComplexNodes;
    }

}
