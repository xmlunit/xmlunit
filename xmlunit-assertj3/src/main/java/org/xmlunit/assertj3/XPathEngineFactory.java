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
package org.xmlunit.assertj3;

import org.xmlunit.xpath.JAXPXPathEngine;
import org.xmlunit.xpath.XPathEngine;

/**
 * @since XMLUnit 2.8.3
 */
class XPathEngineFactory {

    private XPathEngineFactory() {
    }

    static XPathEngine create(XmlAssertConfig config) {
        final JAXPXPathEngine engine = config.xpf == null ? new JAXPXPathEngine() : new JAXPXPathEngine(config.xpf);
        if (config.prefix2Uri != null) {
            engine.setNamespaceContext(config.prefix2Uri);
        }

        return engine;
    }
}
