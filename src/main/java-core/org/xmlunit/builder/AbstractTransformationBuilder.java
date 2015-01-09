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

import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import org.xmlunit.transform.Transformation;

/**
 * Base class providing the common logic of the XSLT related builders.
 *
 * <p>Not intended to be used outside of this package.</p>
 *
 * <p>I wish there was a way to say <code>implements B</code>.</p>
 */
abstract class
    AbstractTransformationBuilder<B extends TransformationBuilderBase<B>>
    implements TransformationBuilderBase<B> {

    private final Transformation helper;

    protected AbstractTransformationBuilder(Source s) {
        helper = new Transformation(s);
    }
    @Override
    public B withStylesheet(Source s) {
        helper.setStylesheet(s);
        return asB();
    }
    @Override
    public B withOutputProperty(String name, String value) {
        helper.addOutputProperty(name, value);
        return asB();
    }
    @Override
    public B withParameter(String name, Object value) {
        helper.addParameter(name, value);
        return asB();
    }
    @Override
    public B usingFactory(TransformerFactory f) {
        helper.setFactory(f);
        return asB();
    }
    @Override
    public B withURIResolver(URIResolver r) {
        helper.setURIResolver(r);
        return asB();
    }

    /**
     * Provides access to a cached {@link Transformation} instance.
     */
    protected Transformation getHelper() {
        return helper;
    }

    @SuppressWarnings("unchecked")
    private B asB() {
        return (B) this;
    }
}
