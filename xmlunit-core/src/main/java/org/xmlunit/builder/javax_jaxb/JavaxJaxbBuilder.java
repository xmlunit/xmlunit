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

package org.xmlunit.builder.javax_jaxb;

import org.xmlunit.XMLUnitException;
import org.xmlunit.builder.JaxbBuilder;

import javax.xml.bind.DataBindingException;
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
import java.lang.reflect.Method;

/**
 * {@link Builder} for Jaxb-Object and creating a {@link JAXBSource}.
 *
 * <p>If no custom {@link Marshaller} is set by {@link JaxbBuilder#withMarshaller}, then the same logic as in {@link
 * JAXB} is used the create a default {@link Marshaller}.</p>
 *
 * @since 2.9.0
 */
public class JavaxJaxbBuilder extends JaxbBuilder {

    /**
     * Creates a builder based on the given object.
     */
    protected JavaxJaxbBuilder(final Object object) {
        super(object);
    }

    @Override
    public Source build() {
        try {
            final Object baseMarshaller = getMarshaller();
            Marshaller marshaller;

            if (baseMarshaller != null) {
                if (baseMarshaller instanceof Marshaller) {
                    marshaller = (Marshaller) baseMarshaller;
                } else {
                    throw new XMLUnitException("provided Marshaller must be a " + Marshaller.class.getName());
                }
            } else {
                marshaller = createDefaultMarshaller();
            }

            final Object jaxbObject = getPreparedJaxbObject();
            final JAXBSource jaxbSource = new JAXBSource(marshaller, jaxbObject);
            // the fake InputSource cannot be used (the Convert.java
            // will create a working one if it is null)
            jaxbSource.setInputSource(null);
            return jaxbSource;
        } catch (final JAXBException e) {
            throw new DataBindingException(e);
        }
    }

    private Object getPreparedJaxbObject() {
        final Object object = getObject();
        final Object jaxbObject;
        if (object instanceof JAXBElement) {
            jaxbObject = object;
        } else {
            final Class<?> clazz = object.getClass();
            final XmlRootElement r = clazz.getAnnotation(XmlRootElement.class);
            if (r == null) {
                if (getUseObjectFactory()) {
                    jaxbObject = createJAXBElement(object);
                } else {
                    jaxbObject = createInferredJAXBElement(object);
                }
            } else {
                jaxbObject = object;
            }
        }
        return jaxbObject;
    }

    private Marshaller createDefaultMarshaller() throws JAXBException, PropertyException {
        final Object object = getObject();
        JAXBContext context;
        if (object instanceof JAXBElement) {
            context = JAXBContext.newInstance(((JAXBElement<?>) object).getDeclaredType());
        } else {
            final Class<?> clazz = object.getClass();
            context = JAXBContext.newInstance(clazz);
        }
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        return marshaller;
    }

    @SuppressWarnings("unchecked")
    private static <T> JAXBElement<T> createInferredJAXBElement(final T object) {
        final Class<T> clazz = (Class<T>) object.getClass();
        // we need to infer the name
        return new JAXBElement<T>(new QName(inferName(clazz)), clazz, object);
    }

    private static <T> JAXBElement<T> createJAXBElement(final T jaxbObj) {
        final JAXBElement<T> jaxbElementFromObjectFactory = createJaxbElementFromObjectFactory(jaxbObj);
        if (jaxbElementFromObjectFactory == null) {
            return createInferredJAXBElement(jaxbObj);
        } else {
            return jaxbElementFromObjectFactory;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> JAXBElement<T> createJaxbElementFromObjectFactory(final T obj) {
        try {
            final Class<?> objFactClass = getObjectFactoryClass(obj);
            final Object objFact = objFactClass.newInstance();
            final Method[] methods = objFactClass.getMethods();

            Object jaxbObj = null;
            for (final Method method : methods) {
                final Class<?>[] params = method.getParameterTypes();
                if (params.length == 1 && params[0] == obj.getClass()
                    && method.getReturnType().isAssignableFrom(JAXBElement.class)) {
                    jaxbObj = method.invoke(objFact, obj);
                    break;
                }
            }
            return (JAXBElement<T>) jaxbObj;
        } catch (final Exception e) {
            // ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException
            return null;
        }
    }

    private static <T> Class<?> getObjectFactoryClass(final T obj) throws ClassNotFoundException {
        final String objFactClassName = obj.getClass().getPackage().getName() + ".ObjectFactory";
        return Thread.currentThread().getContextClassLoader().loadClass(objFactClassName);
    }

    private static String inferName(final Class clazz) {
        return Introspector.decapitalize(clazz.getSimpleName());
    }
}
