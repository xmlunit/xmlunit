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
package org.xmlunit.xpath;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import javax.xml.transform.Source;
import javax.xml.xpath.XPathFactory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.xmlunit.ConfigurationException;
import org.xmlunit.XMLUnitException;
import org.xmlunit.builder.Input;

public class JAXPXPathEngineTest extends AbstractXPathEngineTest {
    @Mock
    private XPathFactory fac;

    @Before
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Override protected XPathEngine getEngine() {
        return new JAXPXPathEngine();
    }

    @Test(expected=ConfigurationException.class)
    public void shouldTranslateExceptionInConstructor() throws Exception {
        when(fac.newXPath()).thenThrow(new NullPointerException());
        new JAXPXPathEngine(fac);
    }

    @Test
    public void evaluateDoesNotResolveExternalEntities() throws Exception {
        Source s = sourceWithExternalEntity();
        try {
            assertFalse(getEngine().evaluate("/foo", s).contains(SECRET));
        } catch (XMLUnitException expected) {
            // DOCTYPE rejected by the hardened parser
        }
    }

    @Test
    public void selectNodesDoesNotResolveExternalEntities() throws Exception {
        Source s = sourceWithExternalEntity();
        try {
            for (org.w3c.dom.Node n : getEngine().selectNodes("/foo", s)) {
                assertFalse(n.getTextContent().contains(SECRET));
            }
        } catch (XMLUnitException expected) {
            // DOCTYPE rejected by the hardened parser
        }
    }

    private static final String SECRET = "TOP-SECRET-XXE-MARKER";

    private static Source sourceWithExternalEntity() throws Exception {
        File secret = File.createTempFile("xmlunit-xxe", ".txt");
        secret.deleteOnExit();
        Writer w = new FileWriter(secret);
        try {
            w.write(SECRET);
        } finally {
            w.close();
        }
        String xml = "<?xml version=\"1.0\"?>\n"
            + "<!DOCTYPE foo [ <!ENTITY xxe SYSTEM \""
            + secret.toURI().toASCIIString() + "\"> ]>\n"
            + "<foo>&xxe;</foo>";
        return Input.fromString(xml).build();
    }
}
