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
package org.xmlunit.transform;

import java.util.Properties;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.w3c.dom.Document;
import org.xmlunit.ConfigurationException;
import org.xmlunit.TestResources;
import org.xmlunit.XMLUnitException;
import org.xmlunit.builder.Input;

import static org.hamcrest.core.IsNot.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TransformationTest {
    private Transformation t;

    @Mock
    private TransformerFactory fac;

    @Mock
    private Transformer transformer;

    @Before
    public void setupMocks() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(fac.newTransformer(any(Source.class))).thenReturn(transformer);
        when(fac.newTransformer()).thenReturn(transformer);
    }

    @Before
    public void createTransformation() {
        t = new Transformation(Input.fromFile(TestResources.DOG_FILE)
                               .build());
        t.setStylesheet(Input.fromFile(TestResources.ANIMAL_XSL).build());
    }

    @Test public void transformAnimalToString() {
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><dog/>",
                     t.transformToString());
    }

    @Test public void transformAnimalToDocument() {
        Document doc = t.transformToDocument();
        assertEquals("dog", doc.getDocumentElement().getTagName());
    }

    @Test public void transformAnimalToHtml() {
        t.addOutputProperty(OutputKeys.METHOD, "html");
        assertThat(t.transformToString(),
                   not("<?xml version=\"1.0\" encoding=\"UTF-8\"?><dog/>"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldRejectNullSourceInSetSource() {
        Transformation t = new Transformation();
        t.setSource(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldRejectNullOutputPropertyName() {
        Transformation t = new Transformation();
        t.addOutputProperty(null, "foo");
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldRejectNullOutputPropertyValue() {
        Transformation t = new Transformation();
        t.addOutputProperty("foo", null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldRejectNullParameterName() {
        Transformation t = new Transformation();
        t.addParameter(null, "foo");
    }

    @Test(expected=ConfigurationException.class)
    public void shouldTransformTransformerConfigurationException() throws Exception {
        when(fac.newTransformer(any(Source.class)))
            .thenThrow(new TransformerConfigurationException());
        t.setFactory(fac);
        t.transformToString();
    }

    @Test
    public void shouldTransformTransformerException() throws Exception {
        doThrow(new TransformerException("foo"))
            .when(transformer)
            .transform(any(Source.class), any(Result.class));
        t.setFactory(fac);
        try {
            t.transformToString();
            fail("should have thrown XMLUnitException");
        } catch (XMLUnitException ex) {
            // assert this is not a XMLUnitException subclass
            assertEquals(XMLUnitException.class, ex.getClass());
        }
    }

    @Test
    public void shouldCallNoArgNewTransformerWithoutStylesheet() throws Exception {
        Transformation t = new Transformation(Input.fromFile(TestResources.DOG_FILE)
                                              .build());
        t.setFactory(fac);
        t.transformToString();

        verify(fac).newTransformer();
    }

    @Test
    public void passesThroughOutputProperties() {
        t.setFactory(fac);
        t.addOutputProperty(OutputKeys.METHOD, "html");
        t.transformToString();

        Properties p = new Properties();
        p.setProperty(OutputKeys.METHOD, "html");
        verify(transformer).setOutputProperties(p);
    }

    @Test
    public void clearsOutputProperties() {
        t.setFactory(fac);
        t.addOutputProperty(OutputKeys.METHOD, "html");
        t.clearOutputProperties();
        t.transformToString();

        Properties p = new Properties();
        p.setProperty(OutputKeys.METHOD, "html");
        verify(transformer, never()).setOutputProperties(p);
        verify(transformer).setOutputProperties(new Properties());
    }

    @Test
    public void passesThroughParameters() {
        t.setFactory(fac);
        t.addParameter(OutputKeys.METHOD, "html");
        t.transformToString();

        verify(transformer).setParameter(OutputKeys.METHOD, "html");
    }

    @Test
    public void clearsParameters() {
        t.setFactory(fac);
        t.addParameter(OutputKeys.METHOD, "html");
        t.clearParameters();
        t.transformToString();

        verify(transformer, never()).setParameter(OutputKeys.METHOD, "html");
    }

    @Test
    public void passesThroughURIResolver() {
        URIResolver u = mock(URIResolver.class);
        t.setFactory(fac);
        t.setURIResolver(u);
        t.transformToString();

        verify(transformer).setURIResolver(u);
    }

    @Test
    public void passesThroughErrorListener() {
        ErrorListener e = mock(ErrorListener.class);
        t.setFactory(fac);
        t.setErrorListener(e);
        t.transformToString();

        verify(transformer).setErrorListener(e);
    }

    @Test(expected=IllegalStateException.class)
    public void shouldRejectNullSourceInTransform() {
        Transformation t = new Transformation();
        t.transformToString();
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldRejectNullResult() {
        t.transformTo(null);
    }
}
