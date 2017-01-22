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

    public NormalizedSource() {
    }

    /**
     * Creates a new source that is created by "normalizing" the given
     * source.
     *
     * <p>See the class-level JavaDocs for details.</p>
     *
     * @param originalSource the original source
     */
    public NormalizedSource(Source originalSource) {
        if (originalSource != null) {
            Document doc = Convert.toDocument(originalSource);
            doc.normalizeDocument();
            super.setNode(doc);
            setSystemId(originalSource.getSystemId());
        }
    }

    /**
     * Creates a new source that is created by "normalizing" the given
     * document.
     *
     * <p>See the class-level JavaDocs for details.</p>
     *
     * @param doc the original source
     */
    public NormalizedSource(Document doc) {
        this(doc, null);
    }

    /**
     * Creates a new source that is created by "normalizing" the given
     * document.
     *
     * <p>See the class-level JavaDocs for details.</p>
     *
     * @param doc the original source
     * @param systemId the system id to use for the new source
     */
    public NormalizedSource(Document doc, String systemId) {
        if (doc != null) {
            doc.normalizeDocument();
        }
        super.setNode(doc);
        setSystemId(systemId);
    }

    /**
     * Creates a new source that is created by "normalizing" the given
     * node.
     *
     * <p>See the class-level JavaDocs for details.</p>
     *
     * @param n the original node
     */
    public NormalizedSource(Node n) {
        this(n, null);
    }

    /**
     * Creates a new source that is created by "normalizing" the given
     * node.
     *
     * <p>See the class-level JavaDocs for details.</p>
     *
     * @param n the original node
     * @param systemId the system id to use for the new source
     */
    public NormalizedSource(Node n, String systemId) {
        setNormalizedNode(n);
        setSystemId(systemId);
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
