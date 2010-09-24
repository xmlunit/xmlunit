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

/**
 * A couple of (functional) sequence processing constructs.
 */
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

    /**
     * Turns an iterable into its type-safe cousin.
     */
    public static <E> Iterable<E> cast(final Iterable i) {
        return map(i, new Mapper<Object, E>() {
                public E map(Object o) {
                    return (E) o;
                }
            });
    }

    /**
     * An iterable containing a single element.
     */
    public static <E> Iterable<E> singleton(final E single) {
        return new Iterable<E>() {
            public Iterator<E> iterator() {
                return new OnceOnlyIterator<E>(single);
            }
        };
    }

    /**
     * Create a new iterable by applying a mapper function to each
     * element of a given sequence.
     */
    public static <F, T> Iterable<T> map(final Iterable<F> from,
                                         final Mapper<? super F, T> mapper) {
        return new Iterable<T>() {
            public Iterator<T> iterator() {
                return new MappingIterator<F, T>(from.iterator(), mapper);
            }
        };
    }

    /**
     * A function mapping from one type to another.
     */
    public interface Mapper<F, T> {
        T map(F from);
    }

    /**
     * Exclude all elements from an iterable that don't match a given
     * predicate.
     */
    public static <T> Iterable<T> filter(final Iterable<T> sequence,
                                         final Predicate<? super T> filter) {
        return new Iterable<T>() {
            public Iterator<T> iterator() {
                return new FilteringIterator<T>(sequence.iterator(), filter);
            }
        };
    }

    /**
     * A function that tests an object for a property.
     */
    public interface Predicate<T> {
        boolean matches(T toTest);
    }

    /**
     * Count the number of elements in a sequence.
     */
    public static int count(Iterable seq) {
        int c = 0;
        Iterator it = seq.iterator();
        while (it.hasNext()) {
            c++;
            it.next();
        }
        return c;
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

    private static class MappingIterator<F, T> implements Iterator<T> {
        private final Iterator<F> i;
        private final Mapper<? super F, T> mapper;
        private MappingIterator(Iterator<F> i, Mapper<? super F, T> mapper) {
            this.i = i;
            this.mapper = mapper;
        }
        public void remove() {
            i.remove();
        }
        public T next() {
            return mapper.map(i.next());
        }
        public boolean hasNext() {
            return i.hasNext();
        }
    }

    private static class FilteringIterator<T> implements Iterator<T> {
        private final Iterator<T> i;
        private final Predicate<? super T> filter;
        private T lookAhead = null;
        private FilteringIterator(Iterator<T> i, Predicate<? super T> filter) {
            this.i = i;
            this.filter = filter;
        }
        public void remove() {
            i.remove();
        }
        public T next() {
            if (lookAhead == null) {
                throw new NoSuchElementException();
            }
            T next = lookAhead;
            lookAhead = null;
            return next;
        }
        public boolean hasNext() {
            while (lookAhead == null && i.hasNext()) {
                T next = i.next();
                if (filter.matches(next)) {
                    lookAhead = next;
                }
            }
            return lookAhead != null;
        }
    }

}