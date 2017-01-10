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
import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.NoSuchElementException;
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

    @Test(expected=NoSuchElementException.class)
    public void cantReadPastFirstElementForSingleton() {
        Iterator<String> i = Linqy.singleton("foo").iterator();
        i.next();
        i.next();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void cantRemoveFromSingletonIterator() {
        Iterator<String> i = Linqy.singleton("foo").iterator();
        i.remove();
    }

    @Test(expected=NoSuchElementException.class)
    public void cantReadPastLastFilterElement() {
        Iterator<String> i = Linqy.filter(Arrays.asList("foo"), new IsNullPredicate())
            .iterator();
        i.next();
    }

    @Test
    public void countUsesCollectionSizeWhenAvailable() {
        final boolean[] calls = new boolean[2]; // [0] has iterator been called? [1] has size been called?
        int count = Linqy.count(new AbstractCollection() {
            @Override
            public Iterator iterator() {
                calls[0] = true;
                return Arrays.asList(new Object()).iterator();
            }
            @Override
            public int size() {
                calls[1] = true;
                return 1;
            }
        });
        Assert.assertEquals(1, count);
        Assert.assertFalse("iterator has been called", calls[0]);
        Assert.assertTrue("size has not been called", calls[1]);
    }

    @Test
    public void countDoesntNeedCollection() {
        final boolean[] called = new boolean[1];
        int count = Linqy.count(new Iterable() {
            @Override
            public Iterator iterator() {
                called[0] = true;
                return Arrays.asList(new Object()).iterator();
            }
        });
        Assert.assertEquals(1, count);
        Assert.assertTrue("iterator has not been called", called[0]);
    }

    private static class IdentityPredicate implements Predicate<Boolean> {
        @Override
        public boolean test(Boolean b) {
            return b;
        }
    }

    private static class IdentityMapper implements Mapper<Object, Object> {
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
