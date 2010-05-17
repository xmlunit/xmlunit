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
import org.w3c.dom.Document;
import net.sf.xmlunit.TestResources;
import net.sf.xmlunit.util.Convert;
import org.junit.Test;

import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsInstanceOf.*;
import static org.hamcrest.core.IsNull.*;
import static org.junit.Assert.*;

public class InputTest {

    private static Document parse(Source s) throws Exception {
        DocumentBuilder b =
            DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return b.parse(Convert.toInputSource(s));
    }

    @Test public void shouldParseADocument() throws Exception {
        Document d = parse(Input.fromFile(TestResources.ANIMAL_FILE).build());
        Source s = Input.fromDocument(d).build();
        allIsWellFor(s);
    }

    @Test public void shouldParseAnExistingFileByName() throws Exception {
        Source s = Input.fromFile(TestResources.ANIMAL_FILE).build();
        allIsWellFor(s);
        assertEquals(toFileUri(TestResources.ANIMAL_FILE), s.getSystemId());
    }

    @Test public void shouldParseAnExistingFileByFile() throws Exception {
        Source s = Input.fromFile(new File(TestResources.ANIMAL_FILE)).build();
        allIsWellFor(s);
        assertEquals(toFileUri(TestResources.ANIMAL_FILE), s.getSystemId());
    }

    @Test public void shouldParseAnExistingFileFromStream() throws Exception {
        FileInputStream is = null;
        try {
            is = new FileInputStream(TestResources.ANIMAL_FILE);
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
            r = new FileReader(TestResources.ANIMAL_FILE);
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
        allIsWellFor(Input.fromURI("file:" + TestResources.ANIMAL_FILE).build());
    }

    @Test public void shouldParseFileFromURI() throws Exception {
        allIsWellFor(Input.fromURI(new URI("file:" + TestResources.ANIMAL_FILE))
                     .build());
    }

    @Test public void shouldParseFileFromURL() throws Exception {
        allIsWellFor(Input.fromURL(new URL("file:" + TestResources.ANIMAL_FILE))
                     .build());
    }

    @Test public void shouldParseATransformationFromSource() throws Exception {
        Source input = Input.fromMemory("<animal>furry</animal>").build();
        Source s = Input.byTransforming(input)
            .withStylesheet(Input.fromFile("src/tests/resources/animal.xsl")
                            .build())
            .build();
        allIsWellFor(s, "furry");
    }

    @Test public void shouldParseATransformationFromBuilder() throws Exception {
        Input.Builder input = Input.fromMemory("<animal>furry</animal>");
        Source s = Input.byTransforming(input)
            .withStylesheet(Input.fromFile("src/tests/resources/animal.xsl"))
            .build();
        allIsWellFor(s, "furry");
    }

    private static void allIsWellFor(Source s) throws Exception {
        allIsWellFor(s, "animal");
    }

    private static void allIsWellFor(Source s, String rootElementName)
        throws Exception {
        assertThat(s, notNullValue());
        Document d = parse(s);
        assertThat(d, notNullValue());
        assertThat(d.getDocumentElement().getTagName(), is(rootElementName));
    }

    private static byte[] readTestFile() throws Exception {
        FileInputStream is = new FileInputStream(TestResources.ANIMAL_FILE);
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

    private static String toFileUri(String fileName) {
        return new File(fileName).toURI().toString();
    }
}