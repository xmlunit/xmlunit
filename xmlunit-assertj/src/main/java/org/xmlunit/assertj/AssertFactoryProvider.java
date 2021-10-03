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
package org.xmlunit.assertj;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.RandomString;

import org.assertj.core.api.Assert;
import org.assertj.core.api.AssertFactory;
import org.w3c.dom.Node;
import org.xmlunit.xpath.XPathEngine;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * In AssertJ before 3.13.0 {@link AssertFactory} class looks like this:
 * <pre>
 * public interface AssertFactory<T, ASSERT> {
 *      ASSERT createAssert(T t);
 * }
 * </pre>
 * <p>
 * So after type erasure it should be like this:
 * <pre>
 * public interface AssertFactory {
 *      Object createAssert(Object t);
 * }
 *
 * In AssertJ 3.13.0 AssertFactory class was change to:
 * <pre>
 * public interface AssertFactory&lt;T, ASSERT extends Assert&lt;?, ?&gt;&gt; {
 *      ASSERT createAssert(T t);
 * }
 * </pre>
 * <p>
 * So after type erasure it should be like this:
 * <pre>
 * public interface AssertFactory {
 *      Assert createAssert(Object t);
 * }
 * </pre>
 * <p>
 * That change brings binary incompatibility so {@link NodeAssertFactory} cannot be used along with latest AssertJ.
 * AssertFactoryProvider checks if there is new version of AssertFactory on class path
 * and if so dynamically creates AssertFactory subclass otherwise return instance of NodeAssertFactory.
 * <p>
 * AssertFactoryProvider uses ByteBuddy internally witch should be provided by latest AssertJ.
 *
 * @see org.assertj.core.api.AssertFactory
 * @see org.assertj.core.api.FactoryBasedNavigableIterableAssert
 * @since XMLUnit 2.6.4
 */
class AssertFactoryProvider {

    @SuppressWarnings("rawtypes")
    private static Class<? extends AssertFactory> assertFactoryClass;

    AssertFactory<Node, SingleNodeAssert> create(XPathEngine engine) {

        if (hasAssertFactoryUpperBoundOnAssertType()) {
            return createProxyInstance(engine);
        }

        return createDefaultInstance(engine);
    }

    private boolean hasAssertFactoryUpperBoundOnAssertType() {

        @SuppressWarnings("rawtypes")
        TypeVariable<Class<AssertFactory>>[] typeParameters = AssertFactory.class.getTypeParameters();
        if (typeParameters.length == 2) {
            Type[] bounds = typeParameters[1].getBounds();
            if (bounds.length == 1) {
                Type assertType = bounds[0];
                if (assertType instanceof ParameterizedType) {
                    ParameterizedType at = (ParameterizedType) assertType;
                    return at.getRawType().equals(Assert.class);
                }
            }
        }

        return false;
    }

    private AssertFactory<Node, SingleNodeAssert> createProxyInstance(XPathEngine engine) {

        try {
            synchronized (AssertFactoryProvider.class) {
                if (assertFactoryClass == null) {
                    assertFactoryClass = new ByteBuddy()
                            .subclass(AssertFactory.class)
                            .name(NodeAssertFactoryDelegate.class.getPackage().getName() + ".XmlUnit$AssertFactory$" + RandomString.make())
                            .method(ElementMatchers.named("createAssert"))
                            .intercept(MethodDelegation.to(new NodeAssertFactoryDelegate(createDefaultInstance(engine))))
                            .make()
                            .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                            .getLoaded();
                }
            }

            @SuppressWarnings("unchecked")
            AssertFactory<Node, SingleNodeAssert> instance = (AssertFactory<Node, SingleNodeAssert>) assertFactoryClass.newInstance();
            return instance;

        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        return createDefaultInstance(engine);
    }

    private NodeAssertFactory createDefaultInstance(XPathEngine engine) {
        return new NodeAssertFactory(engine);
    }

    /**
     * This class should has delegate method with signature matching to type erasure AssertFactory class from AssertJ 3.13.0 or higher
     * which is `Assert createAssert(Object var1)`
     */
    static class NodeAssertFactoryDelegate {

        private final NodeAssertFactory delegate;

        private NodeAssertFactoryDelegate(NodeAssertFactory delegate) {
            this.delegate = delegate;
        }

        @SuppressWarnings("rawtypes")
        Assert delegate(Object obj) {
            return delegate.createAssert((Node) obj);
        }
    }
}
