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

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Provides an iterable view to a NodeList, the Iterator that can be
 * obtained from this Iterable will be read-only.
 */
public final class IterableNodeList implements Iterable<Node> {
    private final NodeList nl;
    private final int length;

    public IterableNodeList(NodeList nl) {
        this.nl = nl;
        length = nl.getLength();
    }

    public Iterator<Node> iterator() {
        return new NodeListIterator();
    }

    private class NodeListIterator implements Iterator<Node> {
        private int current = 0;
        public void remove() {
            throw new UnsupportedOperationException();
        }
        public Node next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            } 
            return nl.item(current++);
        }
        public boolean hasNext() {
            return current < length;
        }
    }

    /**
     * Turns the NodeList into a list.
     */
    public static List<Node> asList(NodeList l) {
        return Linqy.asList(new IterableNodeList(l));
    }
}
