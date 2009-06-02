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
package net.sf.xmlunit.validation;

import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import net.sf.xmlunit.exceptions.XMLUnitException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Validator using the javax.xml.validation namespace.
 */
public class JAXPValidator extends Validator {
    private final String language;
    private final SchemaFactory factory;

    public JAXPValidator(String language) {
        this(language, null);
    }

    public JAXPValidator(String language, SchemaFactory factory) {
        this.language = language;
        this.factory = factory;
    }

    private SchemaFactory getFactory() {
        return factory == null ? SchemaFactory.newInstance(language) : factory;
    }

    @Override public ValidationResult validateSchema() {
        ValidationHandler v = new ValidationHandler();
        SchemaFactory f = getFactory();
        f.setErrorHandler(v);
        try {
            f.newSchema(getSchemaSources());
        } catch (SAXException e) {
            if (e instanceof SAXParseException) {
                v.error((SAXParseException) e);
            } else {
                throw new XMLUnitException(e);
            }
        } finally {
            f.setErrorHandler(null);
        }
        return v.getResult();
    }

    @Override public ValidationResult validateInstance(Source s) {
        Schema schema;
        try {
            schema = getFactory().newSchema(getSchemaSources());
        } catch (SAXException e) {
            throw new XMLUnitException("The schema is invalid", e);
        }
        ValidationHandler v = new ValidationHandler();
        javax.xml.validation.Validator val = schema.newValidator();
        val.setErrorHandler(v);
        try {
            val.validate(s);
        } catch (SAXException e) {
            if (e instanceof SAXParseException) {
                v.error((SAXParseException) e);
            } else {
                throw new XMLUnitException(e);
            }
        } catch (java.io.IOException e) {
            throw new XMLUnitException(e);
        }
        return v.getResult();
    }

}
