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
package org.xmlunit.util;

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

public class LinqyTest {

    @Test
    public void allShouldReturnTrueOnEmptySequence() {
        Assert.assertTrue(Linqy.<Object>all(new ArrayList(), null));
    }

    @Test
    public void anyShouldReturnFalseOnEmptySequence() {
        Assert.assertFalse(Linqy.<Object>any(new ArrayList(), null));
    }

    @Test
    public void anyContract() {
        Assert.assertTrue(Linqy.any(Arrays.asList(false, false, true),
                                    new IdentityPredicate()));
        Assert.assertTrue(Linqy.any(Arrays.asList(true, false), new IdentityPredicate()));
        Assert.assertTrue(Linqy.any(Arrays.asList(true, true), new IdentityPredicate()));
        Assert.assertTrue(Linqy.any(Arrays.asList(true, true, false),
                                    new IdentityPredicate()));
        Assert.assertFalse(Linqy.any(Arrays.asList(false, false), new IdentityPredicate()));
    }

    @Test
    public void allContract() {
        Assert.assertFalse(Linqy.all(Arrays.asList(false, false, true),
                                     new IdentityPredicate()));
        Assert.assertFalse(Linqy.all(Arrays.asList(true, false), new IdentityPredicate()));
        Assert.assertTrue(Linqy.all(Arrays.asList(true, true), new IdentityPredicate()));
        Assert.assertFalse(Linqy.all(Arrays.asList(true, true, false),
                                     new IdentityPredicate()));
        Assert.assertFalse(Linqy.all(Arrays.asList(false, false), new IdentityPredicate()));
    }

    private static class IdentityPredicate implements Predicate<Boolean> {
        @Override
        public boolean test(Boolean b) {
            return b;
        }
    }
}
