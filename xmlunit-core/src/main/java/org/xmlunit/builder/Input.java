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
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import org.xmlunit.XMLUnitException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Fluent API to create Source instances.
 */
public class Input {

    private Input() { /* no instances */ }

    /**
     * Interface for fluent builders of {@link Source}s.
     */
    public interface Builder {
        /**
         * build the actual {@link Source} instance.
         */
        Source build();
    }

    private static class SourceHoldingBuilder implements Builder {
        protected final Source source;
        protected SourceHoldingBuilder(Source source) {
            this.source = source;
        }
        @Override
        public Source build() {
            assert source != null;
            return source;
        }
    }

    /**
     * Build a Source from a DOM Document.
     */
    public static Builder fromDocument(Document d) {
        return new SourceHoldingBuilder(new DOMSource(d));
    }

    /**
     * Build a Source from a DOM Node.
     */
    public static Builder fromNode(Node n) {
        return new SourceHoldingBuilder(new DOMSource(n));
    }

    private static class StreamBuilder extends SourceHoldingBuilder {
        private StreamBuilder(File f) {
            super(new StreamSource(f));
        }
        private StreamBuilder(InputStream s) {
            super(new StreamSource(s));
        }
        private StreamBuilder(Reader r) {
            super(new StreamSource(r));
        }
        private StreamBuilder(final byte[] b) {
            super(new StreamSource() {
                    @Override
                    public InputStream getInputStream() {
                        return new ByteArrayInputStream(b);
                    }
                });
        }
        private StreamBuilder(final String s) {
            super(new StreamSource() {
                    @Override
                    public Reader getReader() {
                        return new StringReader(s);
                    }
                });
        }
        void setSystemId(String id) {
            if (id != null) {
                source.setSystemId(id);
            }
        }
    }

    /**
     * Return the matching Builder for the supported types: {@link Source}, {@link Builder}, {@link Document}, {@link Node},
     * byte[] (XML as byte[]), {@link String} (XML as String), {@link File} (contains XML),
     * {@link URL} (to an XML-Document), {@link URI} (to an XML-Document), {@link InputStream},
     * {@link ReadableByteChannel},
     * Jaxb-{@link Object} (marshal-able with {@link javax.xml.bind.JAXB}.marshal(...))
     */
    public static Builder from(Object object) {
        Builder xml;
        if (object instanceof Source) {
            xml = new SourceHoldingBuilder((Source) object);
        } else if (object instanceof Builder) {
            xml = (Builder) object;
        } else if (object instanceof Document) {
            xml = Input.fromDocument((Document) object);
        } else if (object instanceof Node) {
            xml = Input.fromNode((Node) object);
        } else if (object instanceof byte[]) {
            xml = Input.fromByteArray((byte[]) object);
        } else if (object instanceof String) {
            xml = Input.fromString((String) object);
        } else if (object instanceof File) {
            xml = Input.fromFile((File) object);
        } else if (object instanceof URL) {
            xml = Input.fromURL((URL) object);
        } else if (object instanceof URI) {
            xml = Input.fromURI((URI) object);
        } else if (object instanceof InputStream) {
            xml = Input.fromStream((InputStream) object);
        } else if (object instanceof ReadableByteChannel) {
            xml = Input.fromChannel((ReadableByteChannel) object);
        } else {
            // assume it is a JaxB-Object.
            xml = Input.fromJaxb(object);
        }
        return xml;
    }
    
    /**
     * Build a Source from a Jaxb-Object.
     */
    public static JaxbBuilder fromJaxb(Object jaxbObject) {
        return new JaxbBuilder(jaxbObject);
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
    public static Builder fromString(String s) {
        return new StreamBuilder(s);
    }

    /**
     * Build a Source from an array of bytes.
     */
    public static Builder fromByteArray(byte[] b) {
        return new StreamBuilder(b);
    }

    /**
     * Build a Source from a channel.
     */
    public static Builder fromChannel(ReadableByteChannel c) {
        return fromStream(Channels.newInputStream(c));
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
                    (StreamBuilder) fromByteArray(baos.toByteArray());
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
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("uri " + uri + " is not an URI",
                                               ex);
        }
    }

    /**
     * Builds {@link Source}s by transforming other sources.
     */
    public interface TransformationBuilder
        extends TransformationBuilderBase<TransformationBuilder>, Builder {
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
        @Override
        public TransformationBuilder withStylesheet(Builder b) {
            return withStylesheet(b.build());
        }
        @Override
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
