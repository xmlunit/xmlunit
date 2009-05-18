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
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import net.sf.xmlunit.exceptions.ConfigurationException;
import net.sf.xmlunit.exceptions.XMLUnitException;
import org.w3c.dom.Document;

public class Input {

    public static interface Builder {
        Source build();
    }

    private static class DOMBuilder implements Builder {
        private final Source source;
        private DOMBuilder(Document d) {
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
                return fromMemory(baos.toByteArray());
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
        TransformationBuilder withStylesheet(Source s);
        TransformationBuilder withParameter(String name, Object value);
        TransformationBuilder withOutputProperty(String name, String value);
    }

    private static class Transformation implements TransformationBuilder {
        private final Source source;
        private Source styleSheet;
        private final Properties output = new Properties();
        private final Map<String, Object> params = new HashMap<String, Object>();

        private Transformation(Source s) {
            source = s;
        }
        public TransformationBuilder withStylesheet(Source s) {
            styleSheet = s;
            return this;
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

        public Source build() {
            try {
                DOMResult r = new DOMResult();
                TransformerFactory fac = TransformerFactory.newInstance();
                Transformer t = null;
                if (styleSheet != null) {
                    t = fac.newTransformer(styleSheet);
                } else {
                    t = fac.newTransformer();
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
}
