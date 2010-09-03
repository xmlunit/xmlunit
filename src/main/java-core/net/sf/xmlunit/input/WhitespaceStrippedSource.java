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
package net.sf.xmlunit.input;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import net.sf.xmlunit.util.Convert;
import net.sf.xmlunit.util.Nodes;

/**
 * A source that is obtained from a different source by removing all
 * empty text nodes and trimming the non-empty ones.
 */
public class WhitespaceStrippedSource extends DOMSource {

    public WhitespaceStrippedSource(Source originalSource) {
        super(Nodes.stripWhitespace(Convert.toDocument(originalSource)));
        setSystemId(originalSource.getSystemId());
    }
}
