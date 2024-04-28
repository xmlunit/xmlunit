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

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.xpath.XPathFactoryConfigurationException;
import javax.xml.xpath.XPathFactory;

import org.xmlunit.ConfigurationException;

/**
 * Configures XpathFactories.
 *
 * @since XMLUnit 2.10.0
 */
public class XPathFactoryConfigurer {
    private final Map<String, String> propertys, safePropertys;
    private final Map<String, Boolean> features, safeFeatures;

    private XPathFactoryConfigurer(Map<String, String> propertys, Map<String, String> safePropertys,
            Map<String, Boolean> features, Map<String, Boolean> safeFeatures) {
        this.propertys = propertys;
        this.safePropertys = safePropertys;
        this.features = features;
        this.safeFeatures = safeFeatures;
    }

    /**
     * Applies the current configuration.
     *
     * @param factory the factory to configure
     * @return the configured factory
     * @throws ConfigurationException if any of the propertys or
     * features set is not supported.
     */
    public XPathFactory configure(XPathFactory factory) {
        for (Map.Entry<String, String> prop : propertys.entrySet()) {
            try {
                setProperty(factory, prop.getKey(), prop.getValue());
            } catch (Exception ex) {
                throw new ConfigurationException("Error setting property " + prop.getKey(), ex);
            }
        }
        for (Map.Entry<String, String> prop : safePropertys.entrySet()) {
            try {
                setProperty(factory, prop.getKey(), prop.getValue());
            } catch (Exception ex) {
                // swallow
            }
        }
        for (Map.Entry<String, Boolean> feat : features.entrySet()) {
            try {
                factory.setFeature(feat.getKey(), feat.getValue());
            } catch (XPathFactoryConfigurationException ex) {
                throw new ConfigurationException("Error setting feature " + feat.getKey(), ex);
            }
        }
        for (Map.Entry<String, Boolean> feat : safeFeatures.entrySet()) {
            try {
                factory.setFeature(feat.getKey(), feat.getValue());
            } catch (XPathFactoryConfigurationException ex) {
                // swallow
            }
        }
        return factory;
    }

    /**
     * Creates a builder for XPathFactoryConfigurers.
     * @return a fresh builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * The default instance which disables extension functions.
     *
     * <p>In order to disable extension functions XPathFactory#setProperty needs to exists which requires Java18 or
     * newer. {@link #SecureProcessing} should be available on any JDK.</p></p>
     */
    public static final XPathFactoryConfigurer Default = builder()
        .withExtensionFunctionsDisabled()
        .build();

    /**
     * The instance which enables secure processing thus disables execution of extension functions.
     */
    public static final XPathFactoryConfigurer SecureProcessing = builder()
        .withSecureProcessingEnabled()
        .build();

    /**
     * Builder for a XPathFactoryConfigurer.
     *
     * @since XMLUnit 2.10.0
     */
    public static class Builder {
        private final Map<String, String> propertys = new HashMap<String, String>();
        private final Map<String, String> safePropertys = new HashMap<String, String>();
        private final Map<String, Boolean> features = new HashMap<String, Boolean>();
        private final Map<String, Boolean> safeFeatures = new HashMap<String, Boolean>();

        /**
         * Builds a XPathFactoryConfigurer.
         * @return the configurer
         */
        public XPathFactoryConfigurer build() {
            return new XPathFactoryConfigurer(Collections.unmodifiableMap(propertys),
                Collections.unmodifiableMap(safePropertys), Collections.unmodifiableMap(features),
                Collections.unmodifiableMap(safeFeatures));
        }

        /**
         * Configures the factory with the given property, causes an
         * exception in {@link #configure} if the property is not
         * supported.
         *
         * <p>This method will not do anything if {@link XPathFactory} doesn't support the setPropery method which has
         * been added with Java 18.</p>
         *
         * @param key key of the property to be set
         * @param value value for the property to set
         * @return this
         */
        public Builder withProperty(String key, String value) {
            propertys.put(key, value);
            return this;
        }

        /**
         * Configures the factory with the given property if it is
         * supported.
         *
         * <p>This method will not do anything if {@link XPathFactory} doesn't support the setPropery method which has
         * been added with Java 18.</p>
         *
         * @param key key of the property to be set
         * @param value value for the property to set
         * @return this
         */
        public Builder withSafeProperty(String key, String value) {
            safePropertys.put(key, value);
            return this;
        }

        /**
         * Configures the factory with the given feature, causes an
         * exception in {@link #configure} if the feature is not
         * supported.
         * @param key key of the feature to be set
         * @param value value for the feature to set
         * @return this
         */
        public Builder withFeature(String key, boolean value) {
            features.put(key, value);
            return this;
        }

        /**
         * Configures the factory with the given feature if it is
         * supported.
         * @param key key of the feature to be set
         * @param value value for the feature to set
         * @return this
         */
        public Builder withSafeFeature(String key, boolean value) {
            safeFeatures.put(key, value);
            return this;
        }

        /**
         * Configures the factory to not enable extension functions.
         *
         * <p>This method will not do anything if {@link XPathFactory} doesn't support the setPropery method which has
         * been added with Java 18.</p>
         *
         * @return this
         */
        public Builder withExtensionFunctionsDisabled() {
            return withSafeProperty("jdk.xml.enableExtensionFunctions", "false");
        }

        /**
         * Configures the factory to enable secure processing which disables all external access as well as execution of
         * extension functions.
         * @return this
         */
        public Builder withSecureProcessingEnabled() {
            return withFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        }

    }

    private static final Method setPropertyMethod;

    static {
        Method m = null;
        try {
            m = XPathFactory.class.getMethod("setProperty", String.class, String.class);
        } catch (NoSuchMethodException ex) {
        }
        setPropertyMethod = m;
    }

    private static void setProperty(XPathFactory fac, String name, String value) throws Exception {
        if (setPropertyMethod != null) {
            setPropertyMethod.invoke(fac, name, value);
        }
    }
}
