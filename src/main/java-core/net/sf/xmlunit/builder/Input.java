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
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URL;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
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

    public static Builder fromURL(URL url) throws Exception {
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
    }

    public static Builder fromURI(URI uri) throws Exception {
        return fromURL(uri.toURL());
    }

    public static Builder fromURI(String uri) throws Exception {
        return fromURI(new URI(uri));
    }

    public static interface TransformationBuilder {
        Builder withStylesheet(Source s);
    }

    private static class TransformationStep1 implements TransformationBuilder {
        private Source sourceDoc;
        private TransformationStep1(Source s) {
            sourceDoc = s;
        }
        public Builder withStylesheet(Source s) {
            return new TransformationStep2(sourceDoc, s);
        }
    }

    private static class TransformationStep2 implements Builder {
        private Source source;
        private Source styleSheet;
        private TransformationStep2(Source source, Source styleSheet) {
            this.source = source;
            this.styleSheet = styleSheet;
        }

        public Source build() {
            try {
                DOMResult r = new DOMResult();
                Transformer t = TransformerFactory.newInstance()
                    .newTransformer(styleSheet);
                t.transform(source, r);
                return new DOMSource(r.getNode());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static TransformationBuilder byTransforming(Source s) {
        return new TransformationStep1(s);
    }
}
