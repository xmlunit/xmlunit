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
package org.xmlunit.placeholder;

import org.junit.Test;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.DifferenceEvaluators;

import static org.junit.Assert.assertFalse;

public class PlaceholderSupportTest {
    @Test
    public void ignoreWithDefaultDelimiter() throws Exception {
        String control = "<elem1><elem11>${xmlunit.ignore}</elem11></elem1>";
        String test = "<elem1><elem11>abc</elem11></elem1>";
        Diff diff = PlaceholderSupport.withPlaceholderSupport(DiffBuilder.compare(control).withTest(test)).build();

        assertFalse(diff.hasDifferences());
    }

    @Test
    public void ignoreWithCustomDelimters() throws Exception {
        String control = "<elem1><elem11>#[xmlunit.ignore]</elem11></elem1>";
        String test = "<elem1><elem11>abc</elem11></elem1>";
        Diff diff = PlaceholderSupport
            .withPlaceholderSupportUsingDelimiters(DiffBuilder.compare(control), "#\\[", "\\]")
            .withTest(test).build();

        assertFalse(diff.hasDifferences());
    }

    @Test
    public void ignoreChainedWithDefaultDelimters() throws Exception {
        String control = "<elem1><elem11><![CDATA[${xmlunit.ignore}]]></elem11></elem1>";
        String test = "<elem1><elem11>abc</elem11></elem1>";
        Diff diff = PlaceholderSupport
            .withPlaceholderSupportChainedAfter(DiffBuilder.compare(control), DifferenceEvaluators.Default)
            .withTest(test).build();

        assertFalse(diff.toString(), diff.hasDifferences());
    }

    @Test
    public void ignoreChainedWithCustomDelimters() throws Exception {
        String control = "<elem1><elem11><![CDATA[_xmlunit.ignore_]]></elem11></elem1>";
        String test = "<elem1><elem11>abc</elem11></elem1>";
        Diff diff = PlaceholderSupport
            .withPlaceholderSupportUsingDelimitersChainedAfter(DiffBuilder.compare(control),
                "_", "_", DifferenceEvaluators.Default)
            .withTest(test).build();

        assertFalse(diff.toString(), diff.hasDifferences());
    }
}
