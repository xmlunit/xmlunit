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

using System.Collections;
using System.Collections.Generic;

namespace net.sf.xmlunit.util {
    /// <summary>
    /// A couple of (functional) sequence processing constructs.
    /// </summary>
    public sealed class Linqy {

        /// <summary>
        /// Turns an enumerable into its type-safe cousin.
        /// </summary>
        public static IEnumerable<T> Cast<T>(IEnumerable i) {
            foreach (T t in i) {
                yield return t;
            }
        }

        /// <summary>
        /// An enumerable containing a single element.
        /// </summary>
        public static IEnumerable<T> Singleton<T>(T t) {
            yield return t;
        }

        /// <summary>
        /// Create a new enumerable by applying a mapper function to
        /// each element of a given sequence.
        /// </summary>
        public static IEnumerable<T> Map<F, T>(IEnumerable<F> from,
                                               Mapper<F, T> mapper) {
            foreach (F f in from) {
                yield return mapper(f);
            }
        }

        /// <summary>
        /// A function mapping from one type to another.
        /// </summary>
        public delegate T Mapper<F, T>(F from);

        /// <summary>
        /// Exclude all elements from an enumerable that don't match a
        /// given predicate.
        /// </summary>
        public static IEnumerable<T> Filter<T>(IEnumerable<T> sequence,
                                               Predicate<T> filter) {
            foreach (T t in sequence) {
                if (filter(t)) {
                    yield return t;
                }
            }
        }

        /// <summary>
        /// Count the number of elements in a sequence.
        /// </summary>
        public static int Count(IEnumerable e) {
            int c = 0;
            foreach (object o in e) {
                c++;
            }
            return c;
        }
    }
}