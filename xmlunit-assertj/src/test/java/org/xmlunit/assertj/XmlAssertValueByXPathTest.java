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
package org.xmlunit.assertj;

import org.junit.Rule;
import org.junit.Test;

import static java.lang.String.format;
import static org.xmlunit.assertj.ExpectedException.none;
import static org.xmlunit.assertj.XmlAssert.assertThat;

public class XmlAssertValueByXPathTest {

    @Rule
    public ExpectedException thrown = none();

    @Test
    public void testValueByXPath_withNull_shouldFailed() {

        thrown.expectAssertionError(format("%nExpecting not blank but was:<null>"));

        assertThat("<a><b></b><c/></a>").valueByXPath(null);
    }

    @Test
    public void testValueByXPath_withWhitespacesOnly_shouldFailed() {

        thrown.expectAssertionError(format("%nExpecting not blank but was:<\" \n \t\">"));

        assertThat("<a><b></b><c/></a>").valueByXPath(" \n \t");
    }

    @Test
    public void testValueByXpath_withInvalidXml_shouldFailed() {

        thrown.expectAssertionErrorPattern(".*Expecting code not to raise a throwable but caught.*Content is not allowed in prolog.*");

        assertThat("not empty").valueByXPath("count(//atom:feed/atom:entry)");
    }
}
