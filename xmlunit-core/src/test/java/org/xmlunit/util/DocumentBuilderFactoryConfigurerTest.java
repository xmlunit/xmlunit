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

import java.io.StringReader;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.InputSource;
import org.junit.Test;

import static org.junit.Assert.fail;

public class DocumentBuilderFactoryConfigurerTest {

    private static final String DOCTYPE_DOC =
        "<!DOCTYPE foo><foo/>";

    private static boolean parses(DocumentBuilderFactory factory) throws Exception {
        factory.setNamespaceAware(true);
        try {
            factory.newDocumentBuilder()
                .parse(new InputSource(new StringReader(DOCTYPE_DOC)));
            return true;
        } catch (org.xml.sax.SAXParseException ex) {
            return false;
        }
    }

    @Test
    public void withDTDParsingDisabledRejectsDoctype() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactoryConfigurer.builder()
            .withDTDParsingDisabled()
            .build()
            .configure(DocumentBuilderFactory.newInstance());
        if (parses(factory)) {
            fail("expected DOCTYPE declaration to be rejected");
        }
    }

    @Test
    public void defaultRejectsDoctype() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactoryConfigurer.Default
            .configure(DocumentBuilderFactory.newInstance());
        if (parses(factory)) {
            fail("expected DOCTYPE declaration to be rejected by Default");
        }
    }

    @Test
    public void defaultWithDtdParsingAcceptsDoctype() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactoryConfigurer.DefaultWithDTDParsing
            .configure(DocumentBuilderFactory.newInstance());
        if (!parses(factory)) {
            fail("expected DOCTYPE declaration to be accepted by DefaultWithDTDParsing");
        }
    }
}
