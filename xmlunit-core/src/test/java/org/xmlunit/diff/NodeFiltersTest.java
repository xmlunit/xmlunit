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

import org.junit.Test;
import org.w3c.dom.Node;
import org.xmlunit.util.Predicate;

import static org.junit.Assert.*;

public class NodeFiltersTest {

    private static class TestFilter implements Predicate<Node> {
        private final boolean doReturn;
        private boolean called;

        TestFilter(boolean ret) {
            doReturn = ret;
        }

        @Override
        public boolean test(Node n) {
            called = true;
            return doReturn;
        }
    }

    @Test
    public void emptySatisfiesAllReturnsTrue() {
        assertTrue(NodeFilters.satifiesAll().test(null));
    }

    @Test
    public void emptySatisfiesAnyReturnsFalse() {
        assertFalse(NodeFilters.satifiesAny().test(null));
    }

    @Test
    public void satisfiesAllWorksAsPromised() {
        TestFilter n1 = new TestFilter(true);
        TestFilter n2 = new TestFilter(false);
        TestFilter n3 = new TestFilter(true);
        assertTrue(NodeFilters.satifiesAll(n1).test(null));
        assertFalse(NodeFilters.satifiesAll(n1, n2, n3).test(null));
        assertFalse(n3.called);
        assertTrue(NodeFilters.satifiesAll(n1, n3).test(null));
    }

    @Test
    public void satisfiesAnyWorksAsPromised() {
        TestFilter n1 = new TestFilter(false);
        TestFilter n2 = new TestFilter(true);
        TestFilter n3 = new TestFilter(false);
        assertFalse(NodeFilters.satifiesAny(n1).test(null));
        assertTrue(NodeFilters.satifiesAny(n1, n2, n3).test(null));
        assertFalse(n3.called);
        assertFalse(NodeFilters.satifiesAny(n1, n3).test(null));
    }

}
