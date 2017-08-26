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
package org.xmlunit.input;

import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;
import javax.xml.transform.stream.StreamSource;
import org.xmlunit.util.Convert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class CommentLessSourceTest {

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[] { null },
                             new Object[] { "1.0" },
                             new Object[] { "2.0" });
    }

    private final String xsltVersion;

    public CommentLessSourceTest(String xsltVersion) {
        this.xsltVersion = xsltVersion;
    }

    @Test
    public void stripCommentsAtDifferentLevels() {
        CommentLessSource cls = getSource("<?xml version='1.0'?>"
                                          + "<!-- comment 1 -->"
                                          + "<foo>"
                                          + "<!-- comment 2 -->"
                                          + "</foo>");
        Document d = Convert.toDocument(cls);
        assertEquals(1, d.getChildNodes().getLength());
        assertTrue(d.getChildNodes().item(0) instanceof Element);
        assertEquals(0, d.getChildNodes().item(0).getChildNodes().getLength());
    }

    @Test(expected = IllegalArgumentException.class)
    public void cantWrapNullSource() {
        new CommentLessSource(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cantUseNullVersion() {
        new CommentLessSource(new StreamSource(new StringReader("foo")), null);
    }

    private CommentLessSource getSource(String s) {
        StreamSource src = s == null ? null : new StreamSource(new StringReader(s));
        return xsltVersion == null ? new CommentLessSource(src)
            : new CommentLessSource(src, xsltVersion);
    }

}
