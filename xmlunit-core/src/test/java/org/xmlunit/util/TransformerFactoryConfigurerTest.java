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
package org.xmlunit.util;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;
import org.xmlunit.TestResources;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TransformerFactoryConfigurerTest {

    private static final String INPUT = "<a/>";

    private static final String EXTENSION_FUNCTION_STYLESHEET =
        "<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'"
        + " xmlns:rt='http://xml.apache.org/xalan/java/java.lang.System'>"
        + "<xsl:template match='/'><out><xsl:value-of select='rt:getProperty(\"user.name\")'/></out>"
        + "</xsl:template></xsl:stylesheet>";

    @Test
    public void defaultStopsExtensionFunctions() throws Exception {
        TransformerFactory fac = TransformerFactoryConfigurer.Default
            .configure(TransformerFactory.newInstance());
        try {
            Transformer t = fac.newTransformer(
                new StreamSource(new StringReader(EXTENSION_FUNCTION_STYLESHEET)));
            t.transform(new StreamSource(new StringReader(INPUT)),
                        new StreamResult(new StringWriter()));
            fail("expected the extension function to be rejected");
        } catch (TransformerException ex) {
            // expected - extension functions are not allowed
        }
    }

    @Test
    public void defaultStillAllowsExternalStylesheets() throws Exception {
        String animalXslUri = new File(TestResources.ANIMAL_XSL).getAbsoluteFile().toURI().toString();
        String importing =
            "<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>"
            + "<xsl:import href='" + animalXslUri + "'/>"
            + "<xsl:template match='/'><xsl:apply-templates select='animal'/></xsl:template>"
            + "</xsl:stylesheet>";
        TransformerFactory fac = TransformerFactoryConfigurer.Default
            .configure(TransformerFactory.newInstance());
        Transformer t = fac.newTransformer(new StreamSource(new StringReader(importing)));
        StringWriter out = new StringWriter();
        t.transform(new StreamSource(new StringReader("<animal>dog</animal>")),
                    new StreamResult(out));
        assertTrue("expected the imported stylesheet to be applied, was: " + out,
                   out.toString().contains("<dog/>"));
    }
}
