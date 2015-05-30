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

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.xmlunit.util.Convert;

/**
 * Performs XML normalization on a given Source, Document or Node.
 *
 * <p>For Nodes this means adjacent text nodes are merged to single
 * nodes and empty Text nodes removed (recursively).  For Documents
 * and Sources additional normalization steps may be taken depending
 * on your DOMConfiguration.  See the linked JavaDocs for details.</p>
 *
 * <p>When reading documents a parser usually puts the document into
 * normalized form anway.  You will only need to perform XML
 * normalization on DOM trees you have created programmatically.</p>
 *
 * @see "http://docs.oracle.com/javase/6/docs/api/org/w3c/dom/Document.html#normalizeDocument%28%29"
 * @see "http://docs.oracle.com/javase/6/docs/api/org/w3c/dom/Node.html#normalize%28%29"
 */
public class NormalizedSource extends DOMSource {

    public NormalizedSource(Source originalSource) {
        if (originalSource != null) {
            Document doc = Convert.toDocument(originalSource);
            doc.normalizeDocument();
            super.setNode(doc);
            setSystemId(originalSource.getSystemId());
        }
    }

    public NormalizedSource(Document doc) {
        if (doc != null) {
            doc.normalizeDocument();
        }
        super.setNode(doc);
    }

    public NormalizedSource(Node n) {
        setNormalizedNode(n);
    }

    @Override
    public void setNode(Node n) {
        setNormalizedNode(n);
    }

    private void setNormalizedNode(Node n) {
        if (n != null) {
            n.normalize();
        }
        super.setNode(n);
    }
}
