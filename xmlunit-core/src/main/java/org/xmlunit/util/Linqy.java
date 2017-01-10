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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A couple of (functional) sequence processing constructs.
 */
public final class Linqy {

    private Linqy() { /* no instances */ }

    /**
     * Turns the iterable into a list.
     */
    public static <E> List<E> asList(Iterable<E> i) {
        if (i instanceof Collection) {
            return new ArrayList<E>((Collection<E>) i);
        }
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
                public E apply(Object o) {
                    return (E) o;
                }
            });
    }

    /**
     * An iterable containing a single element.
     */
    public static <E> Iterable<E> singleton(final E single) {
        return new Iterable<E>() {
            @Override
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
            @Override
            public Iterator<T> iterator() {
                return new MappingIterator<F, T>(from.iterator(), mapper);
            }
        };
    }

    /**
     * Exclude all elements from an iterable that don't match a given
     * predicate.
     */
    public static <T> Iterable<T> filter(final Iterable<T> sequence,
                                         final Predicate<? super T> filter) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new FilteringIterator<T>(sequence.iterator(), filter);
            }
        };
    }

    /**
     * Count the number of elements in a sequence.
     */
    public static int count(Iterable seq) {
        if (seq instanceof Collection) {
            return ((Collection) seq).size();
        }
        int c = 0;
        Iterator it = seq.iterator();
        while (it.hasNext()) {
            c++;
            it.next();
        }
        return c;
    }

    /**
     * Determines whether a given predicate holds true for at least
     * one element.
     *
     * <p>Returns false for an empty sequence.</p>
     */
    public static <T> boolean any(final Iterable<T> sequence,
                                  final Predicate<? super T> predicate) {
        for (T t : sequence) {
            if (predicate.test(t)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines whether a given predicate holds true for all
     * elements.
     *
     * <p>Returns true for an empty sequence.</p>
     */
    public static <T> boolean all(final Iterable<T> sequence,
                                  final Predicate<? super T> predicate) {
        for (T t : sequence) {
            if (!predicate.test(t)) {
                return false;
            }
        }
        return true;
    }

    private static class OnceOnlyIterator<E> implements Iterator<E> {
        private final E element;
        private boolean iterated = false;
        private OnceOnlyIterator(E element) {
            this.element = element;
        }
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
        @Override
        public E next() {
            if (iterated) {
                throw new NoSuchElementException();
            }
            iterated = true;
            return element;
        }
        @Override
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
        @Override
        public void remove() {
            i.remove();
        }
        @Override
        public T next() {
            return mapper.apply(i.next());
        }
        @Override
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
            hasNext(); // allow next() to be called without hasNext()
        }
        @Override
        public void remove() {
            i.remove();
        }
        @Override
        public T next() {
            if (lookAhead == null) {
                throw new NoSuchElementException();
            }
            T next = lookAhead;
            lookAhead = null;
            return next;
        }
        @Override
        public boolean hasNext() {
            while (lookAhead == null && i.hasNext()) {
                T next = i.next();
                if (filter.test(next)) {
                    lookAhead = next;
                }
            }
            return lookAhead != null;
        }
    }

}
