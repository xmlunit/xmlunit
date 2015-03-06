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

import java.util.Arrays;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;

public class IterableNodeListTest {

    @Mock
    private NodeList list;

    @Mock
    private Node node1;

    @Mock
    private Node node2;

    private IterableNodeList il;

    @Before
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
        when(list.getLength()).thenReturn(2);
        when(list.item(0)).thenReturn(node1);
        when(list.item(1)).thenReturn(node2);
        il = new IterableNodeList(list);
    }

    @Test(expected=UnsupportedOperationException.class)
    public void cantRemoveFromIterator() {
        il.iterator().remove();
    }

    @Test
    public void iteratesOverNodes() {
        int i = 0;
        for (Node n : il) {
            switch (i) {
            case 0:
                Assert.assertSame(node1, n);
                break;
            case 1:
                Assert.assertSame(node2, n);
                break;
            default:
                Assert.fail("there shouldn't be more than 2 nodes");
                break;
            }
            i++;
        }
        Assert.assertEquals(2, i);
    }

    @Test
    public void asListCreatesExpectedList() {
        Assert.assertArrayEquals(new Node[] { node1, node2 },
                                 IterableNodeList.asList(list).toArray(new Node[2]));
    }
}
