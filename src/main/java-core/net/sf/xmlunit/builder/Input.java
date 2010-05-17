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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import net.sf.xmlunit.exceptions.ConfigurationException;
import net.sf.xmlunit.exceptions.XMLUnitException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class Input {

    public static interface Builder {
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

    public static Builder fromDocument(Document d) {
        return new DOMBuilder(d);
    }

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

    public static Builder fromFile(File f) {
        return new StreamBuilder(f);
    }

    public static Builder fromFile(String name) {
        return new StreamBuilder(new File(name));
    }

    public static Builder fromStream(InputStream s) {
        return new StreamBuilder(s);
    }

    public static Builder fromReader(Reader r) {
        return new StreamBuilder(r);
    }

    public static Builder fromMemory(String s) {
        return fromReader(new StringReader(s));
    }

    public static Builder fromMemory(byte[] b) {
        return fromStream(new ByteArrayInputStream(b));
    }

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

    public static Builder fromURI(URI uri) {
        try {
            return fromURL(uri.toURL());
        } catch (java.net.MalformedURLException ex) {
            throw new IllegalArgumentException("uri " + uri + " is not an URL",
                                               ex);
        }
    }

    public static Builder fromURI(String uri) {
        try {
            return fromURI(new URI(uri));
        } catch (java.net.URISyntaxException ex) {
            throw new IllegalArgumentException("uri " + uri + " is not an URI",
                                               ex);
        }
    }

    public static interface TransformationBuilder extends Builder {
        TransformationBuilder usingFactory(TransformerFactory f);
        TransformationBuilder withOutputProperty(String name, String value);
        TransformationBuilder withParameter(String name, Object value);
        TransformationBuilder withStylesheet(Builder b);
        TransformationBuilder withStylesheet(Source s);
        TransformationBuilder withUriResolver(URIResolver r);
    }

    private static class Transformation implements TransformationBuilder {
        private final Source source;
        private Source styleSheet;
        private TransformerFactory factory;
        private URIResolver uriResolver;
        private final Properties output = new Properties();
        private final Map<String, Object> params = new HashMap<String, Object>();

        private Transformation(Source s) {
            source = s;
        }
        public TransformationBuilder withStylesheet(Source s) {
            styleSheet = s;
            return this;
        }
        public TransformationBuilder withStylesheet(Builder b) {
            return withStylesheet(b.build());
        }
        public TransformationBuilder withOutputProperty(String name,
                                                        String value) {
            output.setProperty(name, value);
            return this;
        }

        public TransformationBuilder withParameter(String name, Object value) {
            params.put(name, value);
            return this;
        }

        public TransformationBuilder usingFactory(TransformerFactory f) {
            factory = f;
            return this;
        }

        public TransformationBuilder withUriResolver(URIResolver r) {
            uriResolver = r;
            return this;
        }

        public Source build() {
            try {
                DOMResult r = new DOMResult();
                TransformerFactory fac = factory;
                if (fac == null) {
                    fac = TransformerFactory.newInstance();
                }
                Transformer t = null;
                if (styleSheet != null) {
                    t = fac.newTransformer(styleSheet);
                } else {
                    t = fac.newTransformer();
                }
                if (uriResolver != null) {
                    t.setURIResolver(uriResolver);
                }
                t.setOutputProperties(output);
                for (Map.Entry<String, Object> ent : params.entrySet()) {
                    t.setParameter(ent.getKey(), ent.getValue());
                }
                t.transform(source, r);
                return new DOMSource(r.getNode());
            } catch (javax.xml.transform.TransformerConfigurationException e) {
                throw new ConfigurationException(e);
            } catch (javax.xml.transform.TransformerException e) {
                throw new XMLUnitException(e);
            }
        }
    }

    public static TransformationBuilder byTransforming(Source s) {
        return new Transformation(s);
    }

    public static TransformationBuilder byTransforming(Builder b) {
        return byTransforming(b.build());
    }
}
