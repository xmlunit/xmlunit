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

/**
 * Holds the common builder methods for XSLT related builders.
 *
 * <p><code>B</code> is the derived builder interface.</p>
 */
public interface
    TransformationBuilderBase<B extends TransformationBuilderBase<B>> {
    /**
     * sets the TraX factory to use.
     */
    B usingFactory(TransformerFactory f);
    /**
     * Adds an output property.
     */
    B withOutputProperty(String name, String value);
    /**
     * Adds a parameter.
     */
    B withParameter(String name, Object value);
    /**
     * Sets the stylesheet to use.
     */
    B withStylesheet(Source s);
    /**
     * Sets the resolver to use for the document() function and
     * xsi:import/include.
     */
    B withURIResolver(URIResolver r);
}
