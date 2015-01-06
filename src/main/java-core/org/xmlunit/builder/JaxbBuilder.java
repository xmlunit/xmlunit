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

package org.xmlunit.builder;

import org.xmlunit.builder.Input.Builder;

import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.util.JAXBSource;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import java.beans.Introspector;

/**
 * {@link Builder} for Jaxb-Object and creating a {@link JAXBSource}.
 * <p>
 * If no custom {@link Marshaller} is set by {@link #withMarshaller(Marshaller)}, then the same logic as in {@link JAXB}
 * is used the create a default {@link Marshaller}.
 */
public class JaxbBuilder implements Builder {

    private final Object object;
    private Marshaller marshaller;

    protected JaxbBuilder(final Object object) {
        this.object = object;
    }

    public JaxbBuilder withMarshaller(final Marshaller marshaller) {
        this.marshaller = marshaller;
        return this;
    }

    @Override
    public Source build() {
        try {
            if (marshaller == null) {
                createDefaultMarshaller();
            }

            final Object jaxbObject = getPreparedJaxbObject();
            final JAXBSource jaxbSource = new JAXBSource(marshaller, jaxbObject);
            // the fake InputSource cannot be used (the Convert.java will create a working one if it is null)
            jaxbSource.setInputSource(null);
            return jaxbSource;
        } catch (final JAXBException e) {
            throw new DataBindingException(e);
        }
    }

    private Object getPreparedJaxbObject() {
        final Object jaxbObject;
        if (object instanceof JAXBElement) {
            jaxbObject = object;
        } else {
            final Class<?> clazz = object.getClass();
            final XmlRootElement r = clazz.getAnnotation(XmlRootElement.class);
            if (r == null) {
                // we need to infer the name
                jaxbObject = createJAXBElement(object, clazz);
            } else {
                jaxbObject = object;
            }
        }
        return jaxbObject;
    }

    private void createDefaultMarshaller() throws JAXBException, PropertyException {
        JAXBContext context;
        if (object instanceof JAXBElement) {
            context = JAXBContext.newInstance(((JAXBElement<?>) object).getDeclaredType());
        } else {
            final Class<?> clazz = object.getClass();
            context = JAXBContext.newInstance(clazz);
        }
        marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    }

    @SuppressWarnings("unchecked")
    private static JAXBElement createJAXBElement(final Object object, final Class<?> clazz) {
        return new JAXBElement(new QName(inferName(clazz)), clazz, object);
    }

    private static String inferName(final Class clazz) {
        return Introspector.decapitalize(clazz.getSimpleName());
    }
}
