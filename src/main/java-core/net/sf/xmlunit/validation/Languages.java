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
package net.sf.xmlunit.validation;

import javax.xml.XMLConstants;

/**
 * Constants for the languages supported by XMLUnit's schema
 * validation.  More languages may be supported depending on your JAXP
 * environment.
 */
public final class Languages {
    private Languages() {}

    /**
     * W3C XML Schema. 
     *
     * @see javax.xml.XMLConstants#W3C_XML_SCHEMA_NS_URI
     */
    public static final String W3C_XML_SCHEMA_NS_URI =
        XMLConstants.W3C_XML_SCHEMA_NS_URI;

    /**
     * DTD
     *
     * @see javax.xml.XMLConstants#XML_DTD_NS_URI
     */
    public static final String XML_DTD_NS_URI = XMLConstants.XML_DTD_NS_URI;

    /**
     * RELAX NG
     *
     * <p>This is most likely only supported if you use additional
     * libraries and configure JAXP to use them.</p>
     *
     * @see javax.xml.XMLConstants#RELAXNG_NS_URI
     */
    public static final String RELAXNG_NS_URI = XMLConstants.RELAXNG_NS_URI;
}
