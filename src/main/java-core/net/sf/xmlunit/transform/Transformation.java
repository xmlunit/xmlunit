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
package net.sf.xmlunit.transform;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamResult;
import net.sf.xmlunit.exceptions.ConfigurationException;
import net.sf.xmlunit.exceptions.XMLUnitException;
import org.w3c.dom.Document;

/**
 * Provides a convenience layer over TraX.
 *
 * <p>Apart from IllegalArgumentExceptions if you try to pass in null
 * values only the transform methods will ever throw exceptions and
 * these will be XMLUnit's runtime exceptions.</p>
 *
 * <p>Each invocation of a transform method will use a fresh
 * Transformer instance, the transform methods are thread-safe.</p>
 */
public final class Transformation {
    private Source source;
    private Source styleSheet;
    private TransformerFactory factory;
    private URIResolver uriResolver;
    private ErrorListener errorListener;
    private final Properties output = new Properties();
    private final Map<String, Object> params = new HashMap<String, Object>();

    public Transformation() {
    }
    /**
     * @param s the source to transform - must not be null.
     */
    public Transformation(Source s) {
        setSource(s);
    }
    /**
     * Set the source document to transform.
     * @param s the source to transform - must not be null.
     */
    public void setSource(Source s) {
        if (s == null) {
            throw new IllegalArgumentException("source must not be null");
        }
        source = s;
    }
    /**
     * Set the stylesheet to use.
     * @param s the stylesheet to use - may be null in which case an
     * identity transformation will be performed.
     */
    public void setStylesheet(Source s) {
        styleSheet = s;
    }
    /**
     * Add a named output property.
     *
     * @param name name of the property - must not be null
     * @param value value of the property - must not be null
     */
    public void addOutputProperty(String name, String value) {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        if (value == null) {
            throw new IllegalArgumentException("value must not be null");
        }
        output.setProperty(name, value);
    }
    /**
     * Clear all output properties.
     */
    public void clearOutputProperties() {
        output.clear();
    }
    /**
     * Add a named parameter.
     *
     * @param name name of the parameter - must not be null
     * @param value value of the parameter - may be null
     */
    public void addParameter(String name, Object value) {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        params.put(name, value);
    }
    /**
     * Clear all output parameters.
     */
    public void clearParameters() {
        params.clear();
    }
    /**
     * Set the TraX factory to use.
     *
     * @param f the factory to use - may be null in which case the
     * default factory will be used.
     */
    public void setFactory(TransformerFactory f) {
        factory = f;
    }
    /**
     * Set the resolver to use for document() and xsl:include/import
     *
     * <p>The resolver will <b>not</b> be attached to the factory.</p>
     *
     * @param r the resolver - may be null in which case no explicit
     * resolver will be used
     */
    public void setURIResolver(URIResolver r) {
        uriResolver = r;
    }
    /**
     * Set the error listener for the transformation.
     *
     * <p>The listener will <b>not</b> be attached to the factory.</p>
     *
     * @param l the listener - may be null in which case no listener
     * will be used
     */
    public void setErrorListener(ErrorListener l) {
        errorListener = l;
    }
    /**
     * Perform the transformation.
     *
     * @param r where to send the transformation result - must not be null
     * @exception IllegalArgumentException if source or result are null
     * @exception ConfigurationException if the TraX system isn't
     * configured properly
     * @exception XMLUnitException if the transformation throws an
     * exception
     */
    public void transformTo(Result r) {
        if (source == null) {
            throw new IllegalArgumentException("source must not be null");
        }
        if (r == null) {
            throw new IllegalArgumentException("result must not be null");
        }
        try {
            TransformerFactory fac = factory;
            if (fac == null) {
                fac = TransformerFactory.newInstance();
            }
            Transformer t = null;
            if (styleSheet != null) {
                t = fac.newTransformer(styleSheet);
            } else {
                t = fac.newTransformer();
            }
            if (uriResolver != null) {
                t.setURIResolver(uriResolver);
            }
            if (errorListener != null) {
                t.setErrorListener(errorListener);
            }
            t.setOutputProperties(output);
            for (Map.Entry<String, Object> ent : params.entrySet()) {
                t.setParameter(ent.getKey(), ent.getValue());
            }
            t.transform(source, r);
        } catch (javax.xml.transform.TransformerConfigurationException e) {
            throw new ConfigurationException(e);
        } catch (javax.xml.transform.TransformerException e) {
            throw new XMLUnitException(e);
        }
    }
    /**
     * Convenience method that returns the result of the
     * transformation as a String.
     *
     * @exception IllegalArgumentException if source is null
     * @exception ConfigurationException if the TraX system isn't
     * configured properly
     * @exception XMLUnitException if the transformation throws an
     * exception
     */
    public String transformToString() {
        StringWriter sw = new StringWriter();
        transformTo(new StreamResult(sw));
        return sw.toString();
    }
    /**
     * Convenience method that returns the result of the
     * transformation as a Document.
     *
     * @exception IllegalArgumentException if source is null
     * @exception ConfigurationException if the TraX system isn't
     * configured properly
     * @exception XMLUnitException if the transformation throws an
     * exception
     */
    public Document transformToDocument() {
        DOMResult r = new DOMResult();
        transformTo(r);
        return (Document) r.getNode();
    }
}