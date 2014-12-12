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

using System;
using System.Linq;
using System.Collections.Generic;

namespace Org.XmlUnit.Util {
    /// <summary>
    /// Sequence processing constructs not present in System.Linq.
    /// </summary>
    public static class Linqy {

        /// <summary>
        /// An enumerable containing a single element.
        /// </summary>
        public static IEnumerable<T> Singleton<T>(T t) {
            yield return t;
        }

        /// <summary>
        /// Like Enumerable.FirstOrDefault but with a configurable default value.
        /// </summary>
        public static T FirstOrDefault<T>(IEnumerable<T> enumerable,
                                          Func<T, bool> predicate,
                                          T defaultValue)
            where T : class {
            return enumerable.FirstOrDefault(predicate) ?? defaultValue;
        }

        /// <summary>
        /// Like Enumerable.FirstOrDefault but with a configurable default value.
        /// </summary>
        public static T FirstOrDefaultValue<T>(IEnumerable<T> enumerable,
                                               Predicate<T> predicate,
                                               T defaultValue)
            where T : struct {
            return enumerable.SkipWhile(t => !predicate(t))
                .Take(1)
                .DefaultIfEmpty(defaultValue)
                .Single();
        }

    }
}