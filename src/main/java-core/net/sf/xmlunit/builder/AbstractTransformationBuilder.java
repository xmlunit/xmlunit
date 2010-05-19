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
package net.sf.xmlunit.builder;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import net.sf.xmlunit.transform.Transformation;

/**
 * Base class providing the common logic of the XSLT related builders.
 *
 * <p>Not intended to be used outside of this package.</p>
 *
 * <p>I wish there was a way to say <code>implements B</code>.</p>
 */
abstract class
    AbstractTransformationBuilder<B extends ITransformationBuilderBase<B>>
    implements ITransformationBuilderBase<B> {

    private final Transformation helper;

    protected AbstractTransformationBuilder(Source s) {
        helper = new Transformation(s);
    }
    public B withStylesheet(Source s) {
        helper.setStylesheet(s);
        return asB();
    }
    public B withOutputProperty(String name, String value) {
        helper.addOutputProperty(name, value);
        return asB();
    }
    public B withParameter(String name, Object value) {
        helper.addParameter(name, value);
        return asB();
    }
    public B usingFactory(TransformerFactory f) {
        helper.setFactory(f);
        return asB();
    }
    public B withURIResolver(URIResolver r) {
        helper.setURIResolver(r);
        return asB();
    }

    protected Transformation getHelper() {
        return helper;
    }

    @SuppressWarnings("unchecked")
    private B asB() {
        return (B) this;
    }
}
