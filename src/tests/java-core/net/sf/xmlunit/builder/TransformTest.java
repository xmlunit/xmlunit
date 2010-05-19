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

import javax.xml.transform.OutputKeys;
import net.sf.xmlunit.TestResources;
import net.sf.xmlunit.builder.Input;
import org.junit.Test;
import org.w3c.dom.Document;

import static org.hamcrest.core.IsNot.*;
import static org.junit.Assert.*;

public class TransformTest {

    @Test public void transformAnimalToString() {
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><dog/>",
                     Transform
                     .source(Input.fromFile(TestResources.DOG_FILE)
                             .build())
                     .withStylesheet(Input.fromFile(TestResources.ANIMAL_XSL)
                                     .build())
                     .build()
                     .toString());
    }

    @Test public void transformAnimalToDocument() {
        Document doc = Transform
            .source(Input.fromFile(TestResources.DOG_FILE).build())
            .withStylesheet(Input.fromFile(TestResources.ANIMAL_XSL)
                            .build())
            .build()
            .toDocument();
        assertEquals("dog", doc.getDocumentElement().getTagName());
    }

    @Test public void transformAnimalToHtml() {
        assertThat(Transform
                   .source(Input.fromFile(TestResources.DOG_FILE).build())
                   .withStylesheet(Input.fromFile(TestResources.ANIMAL_XSL)
                                   .build())
                   .withOutputProperty(OutputKeys.METHOD, "html")
                   .build()
                   .toString(),
                   not("<?xml version=\"1.0\" encoding=\"UTF-8\"?><dog/>"));
    }

}

