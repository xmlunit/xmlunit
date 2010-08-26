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
package net.sf.xmlunit.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public final class Linqy {
    /**
     * Turns the iterable into a list.
     */
    public static <E> List<E> asList(Iterable<E> i) {
        ArrayList<E> a = new ArrayList<E>();
        for (E e : i) {
            a.add(e);
        }
        return a;
    }

    public static <E> Iterable<E> cast(final Iterable i) {
        return new Iterable<E>() {
            public Iterator<E> iterator() {
                return new CastingIterator<E>(i.iterator());
            }
        };
    }

    public static <E> Iterable<E> singleton(final E single) {
        return new Iterable<E>() {
            public Iterator<E> iterator() {
                return new OnceOnlyIterator<E>(single);
            }
        };
    }

    private static class CastingIterator<E> implements Iterator<E> {
        private final Iterator i;
        private CastingIterator(Iterator i) {
            this.i = i;
        }
        public void remove() {
            i.remove();
        }
        public E next() {
            return (E) i.next();
        }
        public boolean hasNext() {
            return i.hasNext();
        }
    }

    private static class OnceOnlyIterator<E> implements Iterator<E> {
        private final E element;
        private boolean iterated = false;
        private OnceOnlyIterator(E element) {
            this.element = element;
        }
        public void remove() {
            throw new UnsupportedOperationException();
        }
        public E next() {
            if (iterated) {
                throw new NoSuchElementException();
            }
            iterated = true;
            return element;
        }
        public boolean hasNext() {
            return !iterated;
        }
    }
}