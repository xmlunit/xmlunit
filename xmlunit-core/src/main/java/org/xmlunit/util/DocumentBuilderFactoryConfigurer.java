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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xmlunit.ConfigurationException;

/**
 * Configures DocumentBuilderFactories.
 *
 * @since XMLUnit 2.6.0
 */
public class DocumentBuilderFactoryConfigurer {

    private final Map<String, Object> attributes, safeAttributes;
    private final Map<String, Boolean> features, safeFeatures;
    private final boolean xIncludeAware, expandEntityRefs;

    private DocumentBuilderFactoryConfigurer(Map<String, Object> attributes, Map<String, Object> safeAttributes,
            Map<String, Boolean> features, Map<String, Boolean> safeFeatures, boolean xIncludeAware,
            boolean expandEntityRefs) {
        this.attributes = attributes;
        this.safeAttributes = safeAttributes;
        this.features = features;
        this.safeFeatures = safeFeatures;
        this.xIncludeAware = xIncludeAware;
        this.expandEntityRefs = expandEntityRefs;
    }

    /**
     * Applies the current configuration.
     *
     * @throws ConfigurationException if any of the attributes or
     * features set is not supported.
     */
    public DocumentBuilderFactory configure(DocumentBuilderFactory factory) {
        for (Map.Entry<String, Object> attr : attributes.entrySet()) {
            try {
                factory.setAttribute(attr.getKey(), attr.getValue());
            } catch (IllegalArgumentException ex) {
                throw new ConfigurationException("Error setting attribute " + attr.getKey(), ex);
            }
        }
        for (Map.Entry<String, Object> attr : safeAttributes.entrySet()) {
            try {
                factory.setAttribute(attr.getKey(), attr.getValue());
            } catch (IllegalArgumentException ex) {
                // swallow
            }
        }
        for (Map.Entry<String, Boolean> feat : features.entrySet()) {
            try {
                factory.setFeature(feat.getKey(), feat.getValue());
            } catch (ParserConfigurationException ex) {
                throw new ConfigurationException("Error setting feature " + feat.getKey(), ex);
            }
        }
        for (Map.Entry<String, Boolean> feat : safeFeatures.entrySet()) {
            try {
                factory.setFeature(feat.getKey(), feat.getValue());
            } catch (ParserConfigurationException ex) {
                // swallow
            }
        }
        factory.setXIncludeAware(xIncludeAware);
        factory.setExpandEntityReferences(expandEntityRefs);
        return factory;
    }

    /**
     * Creates a builder for DocumentBuilderFactoryConfigurers.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * The default instance.
     */
    public static final DocumentBuilderFactoryConfigurer Default = builder()
        .withDTDParsingDisabled()
        .withDTDLoadingDisabled()
        .withXIncludeAware(false)
        .withExpandEntityReferences(false)
        .build();

    /**
     * Builder for a DocumentBuilderFactoryConfigurer.
     *
     * @since XMLUnit 2.6.0
     */
    public static class Builder {
        private final Map<String, Object> attributes = new HashMap<String, Object>();
        private final Map<String, Object> safeAttributes = new HashMap<String, Object>();
        private final Map<String, Boolean> features = new HashMap<String, Boolean>();
        private final Map<String, Boolean> safeFeatures = new HashMap<String, Boolean>();
        private boolean xIncludeAware = false;
        private boolean expandEntityRefs = false;

        /**
         * Builds a DocumentBuilderFactoryConfigurer.
         */
        public DocumentBuilderFactoryConfigurer build() {
            return new DocumentBuilderFactoryConfigurer(Collections.unmodifiableMap(attributes),
                Collections.unmodifiableMap(safeAttributes), Collections.unmodifiableMap(features),
                Collections.unmodifiableMap(safeFeatures), xIncludeAware, expandEntityRefs);
        }

        /**
         * Configures the factory with the given attribute, causes an
         * exception in {@link #configure} if the attribute is not
         * supported.
         */
        public Builder withAttribute(String key, Object value) {
            attributes.put(key, value);
            return this;
        }

        /**
         * Configures the factory with the given attribute if it is
         * supported.
         */
        public Builder withSafeAttribute(String key, Object value) {
            safeAttributes.put(key, value);
            return this;
        }

        /**
         * Configures the factory with the given feature, causes an
         * exception in {@link #configure} if the feature is not
         * supported.
         */
        public Builder withFeature(String key, boolean value) {
            features.put(key, value);
            return this;
        }

        /**
         * Configures the factory with the given feature if it is
         * supported.
         */
        public Builder withSafeFeature(String key, boolean value) {
            safeFeatures.put(key, value);
            return this;
        }

        private static final List<String> DTD_LOAD_DISABLERS = Arrays.asList(
            "http://xerces.apache.org/xerces-j/features.html#external-general-entities",
            "http://xerces.apache.org/xerces2-j/features.html#external-general-entities",
            "http://xml.org/sax/features/external-general-entities",
            "http://apache.org/xml/features/nonvalidating/load-dtd-grammar",
            "http://apache.org/xml/features/nonvalidating/load-external-dtd",
            "http://xerces.apache.org/xerces-j/features.html#external-parameter-entities",
            "http://xerces.apache.org/xerces2-j/features.html#external-parameter-entities",
            "http://xml.org/sax/features/external-parameter-entities"
        );

        /**
         * Configures the factory to not load any external DTDs.
         */
        public Builder withDTDLoadingDisabled() {
            for (String feature : DTD_LOAD_DISABLERS) {
                withSafeFeature(feature, false);
            }
            return this;
        }

        private static final List<String> DTD_PARSE_DISABLERS = Arrays.asList(
            "http://xerces.apache.org/xerces2-j/features.html#disallow-doctype-decl",
            "http://apache.org/xml/features/disallow-doctype-decl"
        );

        /**
         * Configures the factory to not parse any DTDs.
         */
        public Builder withDTDParsingDisabled() {
            for (String feature : DTD_PARSE_DISABLERS) {
                withSafeFeature(feature, false);
            }
            return this;
        }

        /**
         * Configure the factory's XInclude awareness.
         */
        public Builder withXIncludeAware(boolean b) {
            xIncludeAware = b;
            return this;
        }

        /**
         * Configure whether the factory's expands entity references.
         */
        public Builder withExpandEntityReferences(boolean b) {
            expandEntityRefs = b;
            return this;
        }
    }
}
