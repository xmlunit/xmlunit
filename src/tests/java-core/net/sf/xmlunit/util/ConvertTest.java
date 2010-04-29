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
package net.sf.xmlunit.util;

import java.io.File;
import java.io.FileInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import net.sf.xmlunit.Resources;
import org.hamcrest.core.IsNull;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;

public class ConvertTest {

    private static void convertAndAssert(Source s) throws Exception {
        DocumentBuilder b =
            DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document d = b.parse(Convert.toInputSource(s));
        assertThat(d, IsNull.notNullValue());
        assertThat(d.getDocumentElement().getTagName(), is("animal"));
    }

    @Test public void streamSourceToInputSource() throws Exception {
        convertAndAssert(new StreamSource(new File(Resources.ANIMAL_FILE)));
    }

    @Test public void domSourceToInputSource() throws Exception {
        DocumentBuilder b =
            DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document d = b.parse(new File(Resources.ANIMAL_FILE));
        convertAndAssert(new DOMSource(d));
    }

    @Test public void saxSourceToInputSource() throws Exception {
        InputSource s = new InputSource(new FileInputStream(Resources.ANIMAL_FILE));
        convertAndAssert(new SAXSource(s));
    }

}
