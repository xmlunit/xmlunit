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

namespace net.sf.xmlunit {

    public sealed class TestResources {
        private static readonly string PREFIX = "../../../";

        public static readonly string TESTS_DIR =
            PREFIX + "src/tests/resources/";

        public static readonly string ANIMAL_FILE = TESTS_DIR + "test1.xml";
        public static readonly string BLAME_FILE =
            TESTS_DIR + "test.blame.html";

        public static readonly string ANIMAL_XSL = TESTS_DIR + "animal.xsl";
        public static readonly string DOG_FILE = TESTS_DIR + "testAnimal.xml";

        public static readonly string BOOK_DTD = TESTS_DIR + "Book.dtd";
        public static readonly string TEST_DTD = TESTS_DIR + "test.dtd";

        private TestResources() { }
    }
}
