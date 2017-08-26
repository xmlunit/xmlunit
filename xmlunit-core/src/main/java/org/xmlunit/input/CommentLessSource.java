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

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.xmlunit.transform.Transformation;

/**
 * A source that is obtained from a different source by stripping all
 * comments.
 *
 * <p>As of XMLUnit 2.5.0 it is possible to select the XSLT version to
 * use for the stylesheet. The default now is 2.0, it used to be 1.0
 * and you may need to change the value if your transformer doesn't
 * support XSLT 2.0.</p>
 */
public final class CommentLessSource extends DOMSource {

    private static final String DEFAULT_VERSION = "2.0";

    private static final String STYLE_TEMPLATE =
            "<stylesheet version=\"%1$s\" xmlns=\"http://www.w3.org/1999/XSL/Transform\">"
            + "<template match=\"node()[not(self::comment())]|@*\"><copy>"
            + "<apply-templates select=\"node()[not(self::comment())]|@*\"/>"
            + "</copy></template>"
            + "</stylesheet>";

    /**
     * Stylesheet used to strip all comments from an XML document.
     */
    public static final String STYLE = getStylesheetContent(DEFAULT_VERSION);

    /**
     * Creates a new source that consists of the given source with all
     * comments removed using an XSLT stylesheet of version 2.0.
     *
     * @param originalSource the original source
     */
    public CommentLessSource(Source originalSource) {
        this(originalSource, DEFAULT_VERSION);
    }

    /**
     * Creates a new source that consists of the given source with all
     * comments removed.
     *
     * @param originalSource the original source
     * @param xsltVersion use this version for the stylesheet
     * @since XMLUnit 2.5.0
     */
    public CommentLessSource(Source originalSource, String xsltVersion) {
        super();
        if (originalSource == null) {
            throw new IllegalArgumentException("source must not be null");
        }
        if (xsltVersion == null) {
            throw new IllegalArgumentException("xsltVersion must not be null");
        }
        Transformation t = new Transformation(originalSource);
        t.setStylesheet(getStylesheet(xsltVersion));
        setNode(t.transformToDocument());
    }

    private static Source getStylesheet(String xsltVersion) {
        return new StreamSource(new java.io.StringReader(getStylesheetContentCached(xsltVersion)));
    }

    private static String getStylesheetContentCached(String xsltVersion) {
        return DEFAULT_VERSION.equals(xsltVersion) ? STYLE : getStylesheetContent(xsltVersion);
    }

    private static String getStylesheetContent(String xsltVersion) {
        return String.format(STYLE_TEMPLATE, xsltVersion);
    }
}
