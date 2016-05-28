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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.net.URI;
import java.net.URL;
import java.nio.channels.FileChannel;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;

import org.w3c.dom.Document;

import org.xmlunit.NullNode;
import org.xmlunit.TestResources;
import org.xmlunit.XMLUnitException;
import org.xmlunit.builder.jaxb.ComplexNode;
import org.xmlunit.util.Convert;
import org.xmlunit.xpath.JAXPXPathEngine;

import org.junit.Test;

import static org.hamcrest.core.Is.*;
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

    @Test public void shouldParseAnExistingFileFromChannel() throws Exception {
        FileChannel fc = null;
        FileInputStream is = null;
        try {
            is = new FileInputStream(TestResources.ANIMAL_FILE);
            fc = is.getChannel();
            allIsWellFor(Input.fromChannel(fc).build());
        } finally {
            if (fc != null) {
                fc.close();
            }
            if (is != null) {
                is.close();
            }
        }
    }

    @Test public void shouldParseString() throws Exception {
        allIsWellFor(Input.fromString(new String(readTestFile(), "UTF-8"))
                     .build());
    }

    @Test public void shouldParseBytes() throws Exception {
        allIsWellFor(Input.fromByteArray(readTestFile()).build());
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
        Source input = Input.fromString("<animal>furry</animal>").build();
        Source s = Input.byTransforming(input)
            .withStylesheet(Input.fromFile(TestResources.ANIMAL_XSL)
                            .build())
            .build();
        allIsWellFor(s, "furry");
    }

    @Test public void shouldParseATransformationFromBuilder() throws Exception {
        Input.Builder input = Input.fromString("<animal>furry</animal>");
        Source s = Input.byTransforming(input)
            .withStylesheet(Input.fromFile(TestResources.ANIMAL_XSL))
            .build();
        allIsWellFor(s, "furry");
    }

    @Test public void shouldParseJaxbObject() throws Exception {
        allIsWellFor(Input.fromJaxb(new ComplexNode()).build(), "complexNode");
    }

    @Test public void shouldParseUnknownToSource() throws Exception {
        // from Source
        allIsWellFor(Input.from(Input.fromByteArray(readTestFile()).build()).build());
        // from Builder
        allIsWellFor(Input.from(Input.fromByteArray(readTestFile())).build());
        // from Document
        allIsWellFor(Input.from(parse(Input.fromFile(TestResources.ANIMAL_FILE).build())).build());
        // from File
        allIsWellFor(Input.from(new File(TestResources.ANIMAL_FILE)).build());
        // from String
        allIsWellFor(Input.from(new String(readTestFile(), "UTF-8")).build());
        // from byte[]
        allIsWellFor(Input.from(readTestFile()).build());
        // from URI
        allIsWellFor(Input.from(new URI("file:" + TestResources.ANIMAL_FILE)).build());
        // from URL
        allIsWellFor(Input.from(new URL("file:" + TestResources.ANIMAL_FILE)).build());
        // from Jaxb-Object
        allIsWellFor(Input.from(new ComplexNode()).build(), "complexNode");
        // from InputStream
        FileInputStream is = null;
        try {
            is = new FileInputStream(TestResources.ANIMAL_FILE);
            allIsWellFor(Input.from(is).build());
        } finally {
            if (is != null) {
                is.close();
                is = null;
            }
        }
        assertNotNull(Input.from(new NullNode()).build());
        FileChannel fc = null;
        try {
            is = new FileInputStream(TestResources.ANIMAL_FILE);
            fc = is.getChannel();
            allIsWellFor(Input.from(fc).build());
        } finally {
            if (fc != null) {
                fc.close();
            }
            if (is != null) {
                is.close();
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRejectNonURLsStringVersion() {
        Input.fromURI("foo bar");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRejectNonURLsURIVersion() throws Exception {
        Input.fromURI(new URI("urn:xmlunit:foo"));
    }

    @Test(expected = XMLUnitException.class)
    public void shouldTranslateIOException() throws Exception {
        // openStream throws an IOException
        Input.fromURL(new URL("mailto:info@example.org"));
    }

    @Test
    public void canCreateInputFromNode() {
        assertNotNull(Input.fromNode(new NullNode()).build());
    }

    /**
     * @see "https://github.com/xmlunit/xmlunit/issues/84"
     */
    @Test
    public void testStringSourceCanBeUsedMoreThanOnce() {
        Source xml = Input.fromString("<a><b>bvalue</b><c>cvalue</c></a>").build();

        JAXPXPathEngine xpath = new JAXPXPathEngine();

        assertEquals(xpath.evaluate("//a/b", xml), "bvalue");
        assertEquals(xpath.evaluate("//a/c", xml), "cvalue");
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
        String url = new File(fileName).toURI().toString();
        if (url.startsWith("file:/") && !url.startsWith("file:///")
            && ("1.5".equals(System.getProperty("java.specification.version"))
                || transformerIsApacheXalan())
            ) {
            // Java5's StreamSource as well as the one used by apache
            // Xalan create a triple slash URLs,
            // Java6's sticks with only one - toURI uses only one
            // slash in either version
            url = "file:///" + url.substring(6);
        }
        return url;
    }

    private static boolean transformerIsApacheXalan() {
        try {
            TransformerFactory fac = TransformerFactory.newInstance();
            return fac.getClass().getName()
                .equals("org.apache.xalan.processor.TransformerFactoryImpl");
        } catch (Exception ex) {
            return false;
        }
    }
}
