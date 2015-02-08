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

package org.xmlunit.diff;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;


public class ComparisonControllersTest {

    @Test
    public void testDefault() {
        Difference equals = new Difference(null, ComparisonResult.EQUAL);
        Difference similar = new Difference(null, ComparisonResult.SIMILAR);
        Difference difference = new Difference(null, ComparisonResult.DIFFERENT);

        assertThat(ComparisonControllers.Default.stopDiffing(equals), is(false));
        assertThat(ComparisonControllers.Default.stopDiffing(similar), is(false));
        assertThat(ComparisonControllers.Default.stopDiffing(difference), is(false));
    }
    
    @Test
    public void testStopWhenDifferent() {
        Difference equals = new Difference(null, ComparisonResult.EQUAL);
        Difference similar = new Difference(null, ComparisonResult.SIMILAR);
        Difference difference = new Difference(null, ComparisonResult.DIFFERENT);

        assertThat(ComparisonControllers.StopWhenDifferent.stopDiffing(equals), is(false));
        assertThat(ComparisonControllers.StopWhenDifferent.stopDiffing(similar), is(false));
        assertThat(ComparisonControllers.StopWhenDifferent.stopDiffing(difference), is(true));
    }
    
    @Test
    public void testStopWhenSimilar() {
        Difference equals = new Difference(null, ComparisonResult.EQUAL);
        Difference similar = new Difference(null, ComparisonResult.SIMILAR);
        Difference difference = new Difference(null, ComparisonResult.DIFFERENT);

        assertThat(ComparisonControllers.StopWhenSimilar.stopDiffing(equals), is(false));
        assertThat(ComparisonControllers.StopWhenSimilar.stopDiffing(similar), is(true));
        assertThat(ComparisonControllers.StopWhenSimilar.stopDiffing(difference), is(true));
    }
}
