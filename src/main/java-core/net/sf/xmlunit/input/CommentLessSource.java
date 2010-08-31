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

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import net.sf.xmlunit.transform.Transformation;

/**
 * A source that is obtained from a different source by stripping all
 * comments.
 */
public final class CommentLessSource extends DOMSource {

    public CommentLessSource(Source originalSource) {
        super();
        if (originalSource == null) {
            throw new IllegalArgumentException("source must not be null");
        }
        Transformation t = new Transformation(originalSource);
        t.setStylesheet(getStylesheet());
        setNode(t.transformToDocument());
    }

    private static final String STYLE =
        "<stylesheet xmlns=\"http://www.w3.org/1999/XSL/Transform\">"
        + "<template match=\"node()[not(self::comment())]|@*\"><copy>"
        + "<apply-templates select=\"node()[not(self::comment())]|@*\"/>"
        + "</copy></template>"
        + "</stylesheet>";

    private static Source getStylesheet() {
        return new StreamSource(new java.io.StringReader(STYLE));
    }
}
