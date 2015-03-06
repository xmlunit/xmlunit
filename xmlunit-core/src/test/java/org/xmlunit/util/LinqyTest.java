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
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;

public class LinqyTest {

    @Test
    public void castContract() {
        ArrayList al = new ArrayList();
        al.add((Object) "");
        Iterable<String> s = Linqy.<String>cast(al);
        Assert.assertTrue(s.iterator().next() instanceof String);
    }

    @Test
    public void canRemoveFromMapIterator() {
        ArrayList al = new ArrayList();
        al.add("foo");
        Iterator i = Linqy.map(al, new IdentityMapper()).iterator();
        i.next();
        i.remove();
        Assert.assertEquals(0, al.size());
    }

    @Test
    public void canRemoveFromFilterIterator() {
        ArrayList al = new ArrayList();
        al.add("foo");
        Iterator i = Linqy.filter(al, new IsNotNullPredicate()).iterator();
        i.next();
        i.remove();
        Assert.assertEquals(0, al.size());
    }

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

    private static class IdentityMapper implements Linqy.Mapper<Object, Object> {
        @Override
        public Object apply(Object s) {
            return s;
        }
    }

    private class IsNotNullPredicate implements Predicate<Object> {
        @Override
        public boolean test(Object toTest) {
            return toTest != null;
        }
    }
}
