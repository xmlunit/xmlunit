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

package org.custommonkey.xmlunit;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import net.sf.xmlunit.builder.Input;
import net.sf.xmlunit.exceptions.XMLUnitException;
import net.sf.xmlunit.exceptions.XMLUnitException;
import net.sf.xmlunit.transform.Transformation;
import org.custommonkey.xmlunit.exceptions.XMLUnitRuntimeException;
import org.custommonkey.xmlunit.exceptions.ConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Handy wrapper for an XSLT transformation performed using JAXP/Trax.
 * Note that transformation is not actually performed until a call to
 * <code>getResultXXX</code> method, and Templates are not used.
 * <br />Examples and more at <a href="http://xmlunit.sourceforge.net"/>xmlunit.sourceforge.net</a>
 */
public class Transform {
    private static final File PWD = new File(".");

    private final Transformation transformation;
    // only here in order to support the getParameter method
    private final Map<String, Object> parameters = new HashMap<String, Object>();

    /**
     * Create a transformation using String input XML and String stylesheet
     * @param input
     * @param stylesheet
     */
    public Transform(String input, String stylesheet) {
        this(input == null ? null : Input.fromMemory(input),
             stylesheet == null ? null : Input.fromMemory(stylesheet));
    }

    /**
     * Create a transformation using String input XML and stylesheet in a File
     * @param input
     * @param stylesheet
     */
    public Transform(String input, File stylesheet) {
        this(input == null ? null : Input.fromMemory(input),
             stylesheet == null ? null : Input.fromFile(stylesheet));
    }

    /**
     * Create a transformation using InputSource input XML and
     * InputSource stylesheet
     * @param input
     * @param stylesheet
     */
    public Transform(InputSource input, InputSource stylesheet) {
        this(new SAXSource(input), new SAXSource(stylesheet));
    }

    /**
     * Create a transformation using InputSource input XML and
     * stylesheet in a File
     * @param input
     * @param stylesheet
     */
    public Transform(InputSource input, File stylesheet) {
        this(new SAXSource(input),
             stylesheet == null ? null : Input.fromFile(stylesheet).build());
    }

    /**
     * Create a transformation that allows us to serialize a DOM Node
     * @param source
     */
    public Transform(Node sourceNode) {
        this(sourceNode, (String) null);
    }

    /**
     * Create a transformation from an input Node and stylesheet in a String
     * @param sourceNode
     * @param stylesheet
     */
    public Transform(Node sourceNode, String stylesheet) {
        this(sourceNode == null ? null : Input.fromNode(sourceNode),
             stylesheet == null ? null : Input.fromMemory(stylesheet));
    }

    /**
     * Create a transformation from an input Node and stylesheet in a File
     * @param sourceNode
     * @param stylesheet
     */
    public Transform(Node sourceNode, File stylesheet) {
        this(sourceNode == null ? null : Input.fromNode(sourceNode),
             stylesheet == null ? null : Input.fromFile(stylesheet));
    }

    private Transform(Input.Builder input, Input.Builder stylesheet) {
        this(input == null ? null : input.build(),
             stylesheet == null ? null : stylesheet.build());
    }

    /**
     * Create a transformation using Source input XML and Source stylesheet
     * @param inputReader
     * @param stylesheetReader
     */
    public Transform(Source inputSource, Source stylesheetSource) {
        transformation = new Transformation(inputSource);
        transformation.setStylesheet(stylesheetSource);
        transformation.setFactory(XMLUnit.getTransformerFactory());

        provideSystemIdIfRequired(inputSource);
        provideSystemIdIfRequired(stylesheetSource);
    }

    /**
     * Ensure that the source has a systemId
     * @param source
     */
    private void provideSystemIdIfRequired(Source source) {
        if (source!=null && (source.getSystemId() == null
                             || source.getSystemId().length() == 0)) {
            source.setSystemId(getDefaultSystemId());
        }
    }

    /**
     * @return the current working directory as an URL-form string
     */
    private String getDefaultSystemId() {
        try {
            return PWD.toURL().toExternalForm();
        } catch (MalformedURLException e) {
            throw new XMLUnitRuntimeException("Unable to determine current "
                                              + "working directory!", e);
        }
    }
    /**
     * Perform the actual transformation
     * @param result
     * @throws TransformerException
     */
    protected void transformTo(final Result result) throws TransformerException {
        withExceptionHandling(new Trans<Object>() {
                public Object transform() {
                    transformation.transformTo(result);
                    return null;
                }
            });
    }

    /**
     * Perform the XSLT transformation specified in the constructor
     * @return the result as a String
     * @throws TransformerException
     */
    public String getResultString() throws TransformerException {
        return withExceptionHandling(new Trans<String>() {
                public String transform() {
                    return transformation.transformToString();
                }
            });
    }

    /**
     * Perform the XSLT transformation specified in the constructor
     * @return the result as a DOM Document
     * @throws TransformerException
     */
    public Document getResultDocument() throws TransformerException {
        return withExceptionHandling(new Trans<Document>() {
                public Document transform() {
                    return transformation.transformToDocument();
                }
            });
    }

    /**
     * Override an output property specified in the transformation stylesheet
     * @param name
     * @param value
     */
    public void setOutputProperty(String name, String value) {
        transformation.addOutputProperty(name, value);
    }

    /**
     * Override output properties specified in the transformation stylesheet
     * @param outputProperties
     * @see Transformer#setOutputProperties(java.util.Properties)
     */
    public void setOutputProperties(Properties outputProperties) {
        for (Enumeration e = outputProperties.propertyNames();
             e.hasMoreElements(); ) {
            Object key = e.nextElement();
            if (key != null) {
                String name = key.toString();
                String value = outputProperties.getProperty(name);
                if (value != null) {
                    setOutputProperty(name, value);
                }
            }
        }
    }

    /**
     * Add a parameter for the transformation
     * @param name
     * @param value
     * @see Transformer#setParameter(java.lang.String, java.lang.Object)
     */
    public void setParameter(String name, Object value) {
        parameters.put(name, value);
        transformation.addParameter(name, value);
    }

    /**
     * See a parameter used for the transformation
     * @param name
     * @return the parameter value
     * @see Transformer#getParameter(java.lang.String)
     */
    public Object getParameter(String name) {
        return parameters.get(name);
    }

    /**
     * Clear parameters used for the transformation 
     * @see Transformer#clearParameters()
     */
    public void clearParameters() {
        parameters.clear();
        transformation.clearParameters();
    }

    /**
     * Set the URIResolver for the transformation
     * @see Transformer#setURIResolver(javax.xml.transform.URIResolver)
     */
    public void setURIResolver(URIResolver uriResolver) {
        transformation.setURIResolver(uriResolver);
    }

    /**
     * Set the ErrorListener for the transformation
     * @see Transformer#setErrorListener(javax.xml.transform.ErrorListener)
     */
    public void setErrorListener(ErrorListener errorListener) {
        transformation.setErrorListener(errorListener);
    }

    private static <R> R withExceptionHandling(Trans<R> trans)
        throws TransformerException {
        try {
            return trans.transform();
        } catch (net.sf.xmlunit.exceptions.ConfigurationException ex) {
            throw new ConfigurationException(ex.getMessage(), ex.getCause());
        } catch (XMLUnitException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof TransformerException) {
                throw (TransformerException) cause;
            }
            throw new XMLUnitRuntimeException(ex.getMessage(), cause);
        }
    }

    private interface Trans<R> {
        R transform();
    }
}
