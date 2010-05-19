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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import net.sf.xmlunit.exceptions.XMLUnitException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Fluent API to create Source instances.
 */
public class Input {

    public static interface Builder {
        /**
         * build the actual Source instance.
         */
        Source build();
    }

    private static class DOMBuilder implements Builder {
        private final Source source;
        private DOMBuilder(Node d) {
            source = new DOMSource(d);
        }
        public Source build() {
            assert source != null;
            return source;
        }
    }

    /**
     * Build a Source from a DOM Document.
     */
    public static Builder fromDocument(Document d) {
        return new DOMBuilder(d);
    }

    /**
     * Build a Source from a DOM Node.
     */
    public static Builder fromNode(Node n) {
        return new DOMBuilder(n);
    }

    private static class StreamBuilder implements Builder {
        private final Source source;
        private StreamBuilder(File f) {
            source = new StreamSource(f);
        }
        private StreamBuilder(InputStream s) {
            source = new StreamSource(s);
        }
        private StreamBuilder(Reader r) {
            source = new StreamSource(r);
        }
        void setSystemId(String id) {
            if (id != null) {
                source.setSystemId(id);
            }
        }
        public Source build() {
            assert source != null;
            return source;
        }
    }

    /**
     * Build a Source from a file.
     */
    public static Builder fromFile(File f) {
        return new StreamBuilder(f);
    }

    /**
     * Build a Source from a named file.
     */
    public static Builder fromFile(String name) {
        return new StreamBuilder(new File(name));
    }

    /**
     * Build a Source from a stream.
     */
    public static Builder fromStream(InputStream s) {
        return new StreamBuilder(s);
    }

    /**
     * Build a Source from a reader.
     */
    public static Builder fromReader(Reader r) {
        return new StreamBuilder(r);
    }

    /**
     * Build a Source from a string.
     */
    public static Builder fromMemory(String s) {
        return fromReader(new StringReader(s));
    }

    /**
     * Build a Source from an array of bytes.
     */
    public static Builder fromMemory(byte[] b) {
        return fromStream(new ByteArrayInputStream(b));
    }

    /**
     * Build a Source from an URL.
     */
    public static Builder fromURL(URL url) {
        try {
            InputStream in = null;
            try {
                in = url.openStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int read = -1;
                byte[] buf = new byte[4096];
                while ((read = in.read(buf)) >= 0) {
                    if (read > 0) {
                        baos.write(buf, 0, read);
                    }
                }
                StreamBuilder b =
                    (StreamBuilder) fromMemory(baos.toByteArray());
                try {
                    b.setSystemId(url.toURI().toString());
                } catch (URISyntaxException use) {
                    // impossible - shouldn't have been an URL in the
                    // first place
                    b.setSystemId(url.toString());
                }
                return b;
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        } catch (IOException ex) {
            throw new XMLUnitException(ex);
        }
    }

    /**
     * Build a Source from an URI.
     * @param uri must represent a valid URL
     */
    public static Builder fromURI(URI uri) {
        try {
            return fromURL(uri.toURL());
        } catch (java.net.MalformedURLException ex) {
            throw new IllegalArgumentException("uri " + uri + " is not an URL",
                                               ex);
        }
    }

    /**
     * Build a Source from an URI.
     * @param uri must represent a valid URL
     */
    public static Builder fromURI(String uri) {
        try {
            return fromURI(new URI(uri));
        } catch (java.net.URISyntaxException ex) {
            throw new IllegalArgumentException("uri " + uri + " is not an URI",
                                               ex);
        }
    }

    public static interface TransformationBuilder
        extends ITransformationBuilderBase<TransformationBuilder>, Builder {
        /**
         * Sets the stylesheet to use.
         */
        TransformationBuilder withStylesheet(Builder b);
    }

    private static class Transformation
        extends AbstractTransformationBuilder<TransformationBuilder>
        implements TransformationBuilder {

        private Transformation(Source s) {
            super(s);
        }
        public TransformationBuilder withStylesheet(Builder b) {
            return withStylesheet(b.build());
        }
        public Source build() {
            return new DOMSource(getHelper().transformToDocument());
        }
    }

    /**
     * Build a Source by XSLT transforming a different Source.
     */
    public static TransformationBuilder byTransforming(Source s) {
        return new Transformation(s);
    }

    /**
     * Build a Source by XSLT transforming a different Source.
     */
    public static TransformationBuilder byTransforming(Builder b) {
        return byTransforming(b.build());
    }
}
