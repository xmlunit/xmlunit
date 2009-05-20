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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.net.URI;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import org.w3c.dom.Document;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsNull.*;
import static org.junit.Assert.*;
import org.junit.Test;

public class InputTest {

    private static final String TEST_FILE = "src/tests/resources/test1.xml";

    private static Document parse(Source s) throws Exception {
        DocumentBuilder b =
            DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return b.parse(SAXSource.sourceToInputSource(s));
    }

    @Test public void shouldParseADocument() throws Exception {
        Document d = parse(Input.fromFile(TEST_FILE).build());
        // it looks as if SAXSource.sourceToInputSource cannot deal
        // with a DOMSource, so we cannot use the parse method
        Source s = Input.fromDocument(d).build();
        assertThat(s, is(DOMSource.class));
        Object o = ((DOMSource) s).getNode();
        assertThat(o, is(Document.class));
        Document d2 = (Document) o;
        assertThat(d2, notNullValue());
        assertThat(d2.getDocumentElement().getTagName(), is("animal"));
    }

    @Test public void shouldParseAnExistingFileByName() throws Exception {
        allIsWellFor(Input.fromFile(TEST_FILE).build());
    }

    @Test public void shouldParseAnExistingFileByFile() throws Exception {
        allIsWellFor(Input.fromFile(new File(TEST_FILE)).build());
    }

    @Test public void shouldParseAnExistingFileFromStream() throws Exception {
        FileInputStream is = null;
        try {
            is = new FileInputStream(TEST_FILE);
            allIsWellFor(Input.fromStream(is).build());
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    @Test public void shouldParseAnExistingFileFromReader() throws Exception {
        FileReader r = null;
        try {
            r = new FileReader(TEST_FILE);
            allIsWellFor(Input.fromReader(r).build());
        } finally {
            if (r != null) {
                r.close();
            }
        }
    }

    @Test public void shouldParseString() throws Exception {
        allIsWellFor(Input.fromMemory(new String(readTestFile(), "UTF-8"))
                     .build());
    }

    @Test public void shouldParseBytes() throws Exception {
        allIsWellFor(Input.fromMemory(readTestFile()).build());
    }

    @Test public void shouldParseFileFromURIString() throws Exception {
        allIsWellFor(Input.fromURI("file:" + TEST_FILE).build());
    }

    @Test public void shouldParseFileFromURI() throws Exception {
        allIsWellFor(Input.fromURI(new URI("file:" + TEST_FILE)).build());
    }

    @Test public void shouldParseFileFromURL() throws Exception {
        allIsWellFor(Input.fromURL(new URL("file:" + TEST_FILE)).build());
    }

    @Test public void shouldParseATransformation() throws Exception {
        Source input = Input.fromMemory("<animal>furry</animal>").build();
        Source s = Input.byTransforming(input)
            .withStylesheet(Input.fromFile("src/tests/resources/animal.xsl")
                            .build())
            .build();
        // again, transformed is a DOMSource, cannot use parse()
        assertThat(s, is(DOMSource.class));
        Object o = ((DOMSource) s).getNode();
        assertThat(o, is(Document.class));
        Document d2 = (Document) o;
        assertThat(d2, notNullValue());
        assertThat(d2.getDocumentElement().getTagName(), is("furry"));
    }

    private static void allIsWellFor(Source s) throws Exception {
        assertThat(s, notNullValue());
        Document d = parse(s);
        assertThat(d, notNullValue());
        assertThat(d.getDocumentElement().getTagName(), is("animal"));
    }

    private static byte[] readTestFile() throws Exception {
        FileInputStream is = new FileInputStream(TEST_FILE);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int read = -1;
        while ((read = is.read(buffer)) >= 0) {
            if (read > 0) {
                bos.write(buffer, 0, read);
            }
        }
        is.close();
        return bos.toByteArray();
    }
}