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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;

import org.xmlunit.ConfigurationException;

/**
 * Configures TransformerFactories.
 *
 * @since XMLUnit 2.6.0
 */
public class TransformerFactoryConfigurer {

    private final Map<String, Object> attributes, safeAttributes;
    private final Map<String, Boolean> features, safeFeatures;

    private TransformerFactoryConfigurer(Map<String, Object> attributes, Map<String, Object> safeAttributes,
            Map<String, Boolean> features, Map<String, Boolean> safeFeatures) {
        this.attributes = attributes;
        this.safeAttributes = safeAttributes;
        this.features = features;
        this.safeFeatures = safeFeatures;
    }

    /**
     * Applies the current configuration.
     *
     * @throws ConfigurationException if any of the attributes or
     * features set is not supported.
     */
    public TransformerFactory configure(TransformerFactory factory) {
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
            } catch (TransformerConfigurationException ex) {
                throw new ConfigurationException("Error setting feature " + feat.getKey(), ex);
            }
        }
        for (Map.Entry<String, Boolean> feat : safeFeatures.entrySet()) {
            try {
                factory.setFeature(feat.getKey(), feat.getValue());
            } catch (TransformerConfigurationException ex) {
                // swallow
            }
        }
        return factory;
    }

    /**
     * Creates a builder for TransformerFactoryConfigurers.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * The default instance which disables DTD loading but still
     * allows loading of external stylesheets.
     */
    public static final TransformerFactoryConfigurer Default = builder()
        .withDTDLoadingDisabled()
        .build();

    /**
     * The instance which disables DTD loading as well as loading of
     * external stylesheets.
     */
    public static final TransformerFactoryConfigurer NoExternalAccess = builder()
        .withDTDLoadingDisabled()
        .withExternalStylesheetLoadingDisabled()
        .build();

    /**
     * Builder for a TransformerFactoryConfigurer.
     *
     * @since XMLUnit 2.6.0
     */
    public static class Builder {
        private final Map<String, Object> attributes = new HashMap<String, Object>();
        private final Map<String, Object> safeAttributes = new HashMap<String, Object>();
        private final Map<String, Boolean> features = new HashMap<String, Boolean>();
        private final Map<String, Boolean> safeFeatures = new HashMap<String, Boolean>();

        /**
         * Builds a TransformerFactoryConfigurer.
         */
        public TransformerFactoryConfigurer build() {
            return new TransformerFactoryConfigurer(Collections.unmodifiableMap(attributes),
                Collections.unmodifiableMap(safeAttributes), Collections.unmodifiableMap(features),
                Collections.unmodifiableMap(safeFeatures));
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

        /**
         * Configures the factory to not load any external DTDs.
         */
        public Builder withDTDLoadingDisabled() {
            // XMLConstants.ACCESS_EXTERNAL_DTD is not available in Java 6
            return withSafeAttribute("http://javax.xml.XMLConstants/property/accessExternalDTD", "");
        }

        /**
         * Configures the factory to not parse any DTDs.
         */
        public Builder withExternalStylesheetLoadingDisabled() {
            // XMLConstants.ACCESS_EXTERNAL_STYLESHEET is not available in Java 6
            return withSafeAttribute("http://javax.xml.XMLConstants/property/accessExternalStylesheet", "");
        }
    }
}
