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

import java.util.Arrays;
import javax.xml.transform.Source;

/**
 * Validates a piece of XML against a schema given in a supported
 * language or the definition of such a schema itself.
 */
public abstract class Validator {
    private Source[] sourceLocations;

    protected Validator() {
    }

    /**
     * Where to find the schema.
     */
    public void setSchemaSources(Source... s) {
        if (s != null) {
            sourceLocations = Arrays.copyOf(s, s.length);
        } else {
            sourceLocations = null;
        }
    }

    /**
     * Where to find the schema.
     */
    public final void setSchemaSource(Source s) {
        setSchemaSources(s == null ? null : new Source[] {s});
    }

    /**
     * Where to find the schema.
     */
    protected Source[] getSchemaSources() {
        return sourceLocations == null ? new Source[0] : sourceLocations;
    }

    /**
     * Validates a schema.
     *
     * @throws UnsupportedOperationException if the language's
     * implementation doesn't support schema validation
     */
    public abstract ValidationResult validateSchema();

    /**
     * Validates an instance against the schema.
     */
    public abstract ValidationResult validateInstance(Source instance);


    /**
     * Factory that obtains a Validator instance based on the schema language.
     *
     * @see Languages
     */
    public static Validator forLanguage(String language) {
        if (!Languages.XML_DTD_NS_URI.equals(language)) {
            return new JAXPValidator(language);
        }
        return new ParsingValidator(Languages.XML_DTD_NS_URI);
    }
}
