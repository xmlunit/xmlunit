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
package net.sf.xmlunit.input;

import java.io.StringReader;
import javax.xml.transform.stream.StreamSource;
import net.sf.xmlunit.util.Convert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.junit.Assert.*;

public class CommentLessSourceTest {

    @Test public void stripCommentsAtDifferentLevels() {
        StreamSource s =
            new StreamSource(new StringReader("<?xml version='1.0'?>"
                                              + "<!-- comment 1 -->"
                                              + "<foo>"
                                              + "<!-- comment 2 -->"
                                              + "</foo>"));
        CommentLessSource cls = new CommentLessSource(s);
        Document d = Convert.toDocument(cls);
        assertEquals(1, d.getChildNodes().getLength());
        assertTrue(d.getChildNodes().item(0) instanceof Element);
        assertEquals(0, d.getChildNodes().item(0).getChildNodes().getLength());
    }

}
