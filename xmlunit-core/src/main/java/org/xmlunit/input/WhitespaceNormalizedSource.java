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
package org.xmlunit.input;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import org.xmlunit.util.Convert;
import org.xmlunit.util.Nodes;

/**
 * A source that is obtained from a different source by removing all
 * empty text nodes and normalizing the non-empty ones.
 *
 * <p>"normalized" in this context means all whitespace characters
 * are replaced by space characters and consecutive whitespace
 * characters are collapsed.</p>
 */
public class WhitespaceNormalizedSource extends DOMSource {

    /**
     * Creates a new source that consists of the given source with all
     * whitespace characters replaced by spaces and consecutive
     * whitespace characters collapsed.
     *
     * @param originalSource the original source
     */
    public WhitespaceNormalizedSource(Source originalSource) {
        super(Nodes.normalizeWhitespace(Convert.toDocument(originalSource)));
        setSystemId(originalSource.getSystemId());
    }

    /**
     * Creates a new source that consists of the given source with all
     * whitespace characters replaced by spaces and consecutive
     * whitespace characters collapsed.
     *
     * @param originalSource the original source
     * @param dbf the DocumentBuilderFactory to use when creating a
     * DOM document from originalSource
     */
    public WhitespaceNormalizedSource(Source originalSource,
                                      DocumentBuilderFactory dbf) {
        super(Nodes.normalizeWhitespace(Convert.toDocument(originalSource, dbf)));
        setSystemId(originalSource.getSystemId());
    }
}
