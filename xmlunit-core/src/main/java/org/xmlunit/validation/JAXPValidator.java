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
package org.xmlunit.validation;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xmlunit.XMLUnitException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;

/**
 * Validator using the javax.xml.validation namespace.
 *
 * <p>An implementation detail of {@code
 * javax.xml.validation.Validator} leaks into this class: any {@code
 * xsi:schemaLocation} or {@code xsi:noSchemaLocation} attribute of
 * the instance document will be ignored if any schema source has been
 * set.  This means you must either specify all sources or none of
 * them to successfully validate instances.</p>
 *
 * <p><strong>Security note:</strong> like the rest of the {@code
 * validation} package this class does not restrict external DTD access
 * by default - that has been a conscious decision since XMLUnit 2.6.0
 * because schema validation often needs to load external resources. An
 * instance document with a {@code DOCTYPE} that declares an external
 * entity may therefore cause that entity to be resolved while it is
 * validated. If you validate untrusted input use {@link
 * #setDisableExternalDtdAccess setDisableExternalDtdAccess(true)} to
 * forbid this.</p>
 */
public class JAXPValidator extends Validator {
    private final String language;
    private final SchemaFactory factory;
    private Schema schema;
    private boolean disableExternalDtdAccess;

    /**
     * Creates a validator for the given schema language using the default SchemaFactory.
     * @param language the schema language
     */
    public JAXPValidator(String language) {
        this(language, null);
    }

    /**
     * Creates a validator for the given schema language using a custom SchemaFactory.
     * @param language the schema language
     * @param factory the factory to use
     */
    public JAXPValidator(String language, SchemaFactory factory) {
        this.language = language;
        this.factory = factory;
    }

    /**
     * Sets the schema to use in instance validation directly rather
     * than via {@link #setSchemaSource}.
     * @since XMLUnit 2.3.0
     * @param s the schema as Source
     */
    public final void setSchema(Schema s) {
        schema = s;
    }

    /**
     * Whether external DTD access should be forbidden when parsing the
     * schema and validating instances.
     *
     * <p>The default is {@code false}, leaving external DTD access
     * enabled as it has been since XMLUnit 2.6.0. Setting this to
     * {@code true} sets the {@code accessExternalDTD} property to the
     * empty string on the {@code SchemaFactory} and {@code Validator}
     * used, which closes the XXE vector when validating untrusted
     * instances. The {@code accessExternalSchema} property is left
     * untouched so {@code xs:import} and {@code xsi:schemaLocation}
     * keep working.</p>
     *
     * @since XMLUnit 2.12.1
     * @param disable whether to forbid external DTD access
     */
    public void setDisableExternalDtdAccess(boolean disable) {
        disableExternalDtdAccess = disable;
    }

    private SchemaFactory getFactory() {
        SchemaFactory f = factory == null ? SchemaFactory.newInstance(language) : factory;
        if (disableExternalDtdAccess) {
            restrictExternalDtdAccess(f);
        }
        return f;
    }

    private static void restrictExternalDtdAccess(SchemaFactory f) {
        try {
            f.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        } catch (SAXNotRecognizedException ex) {
            // property not supported, nothing we can do
        } catch (SAXNotSupportedException ex) {
            // property not supported, nothing we can do
        }
    }

    private static void restrictExternalDtdAccess(javax.xml.validation.Validator v) {
        try {
            v.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        } catch (SAXNotRecognizedException ex) {
            // property not supported, nothing we can do
        } catch (SAXNotSupportedException ex) {
            // property not supported, nothing we can do
        }
    }

    @Override public ValidationResult validateSchema() {
        ValidationHandler v = new ValidationHandler();
        SchemaFactory f = getFactory();
        f.setErrorHandler(v);
        try {
            f.newSchema(getSchemaSources());
        } catch (SAXParseException e) {
            v.error((SAXParseException) e);
        } catch (SAXException e) {
            throw new XMLUnitException(e);
        } finally {
            f.setErrorHandler(null);
        }
        return v.getResult();
    }

    @Override public ValidationResult validateInstance(Source s) {
        Schema schema;
        try {
            schema = getSchema();
        } catch (SAXException e) {
            throw new XMLUnitException("The schema is invalid", e);
        }
        ValidationHandler v = new ValidationHandler();
        javax.xml.validation.Validator val = schema.newValidator();
        if (disableExternalDtdAccess) {
            restrictExternalDtdAccess(val);
        }
        val.setErrorHandler(v);
        try {
            val.validate(s);
        } catch (SAXParseException e) {
            v.error((SAXParseException) e);
        } catch (SAXException e) {
            throw new XMLUnitException(e);
        } catch (java.io.IOException e) {
            throw new XMLUnitException(e);
        }
        return v.getResult();
    }

    private Schema getSchema() throws SAXException {
        if (schema != null) {
            return schema;
        }
        Source[] sources = getSchemaSources();
        return sources.length > 0 ? getFactory().newSchema(sources)
            : getFactory().newSchema();
    }
}
