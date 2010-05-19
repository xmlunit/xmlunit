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
package net.sf.xmlunit.transform;

import javax.xml.transform.OutputKeys;
import net.sf.xmlunit.TestResources;
import net.sf.xmlunit.builder.Input;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import static org.hamcrest.core.IsNot.*;
import static org.junit.Assert.*;

public class TransformationTest {
    private Transformation t;

    @Before public void createTransformation() {
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

}
