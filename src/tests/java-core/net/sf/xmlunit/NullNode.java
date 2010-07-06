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
package net.sf.xmlunit;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

public class NullNode implements Node {
    public Node appendChild(Node n) {
        return n;
    }
    public Node cloneNode(boolean deep) {
        return this;
    }
    public short compareDocumentPosition(Node other) {
        return 0;
    }
    public NamedNodeMap getAttributes() {
        return null;
    }
    public String getBaseURI() {
        return null;
    }
    public NodeList getChildNodes() {
        return new NodeList() {
            public int getLength() {
                return 0;
            }
            public Node item(int idx) {
                throw new IndexOutOfBoundsException();
            }
        };
    }
    public Object getFeature(String f, String v) {
        return null;
    }
    public Node getFirstChild() {
        return null;
    }
    public Node getLastChild() {
        return null;
    }
    public String getLocalName() {
        return null;
    }
    public String getNamespaceURI() {
        return null;
    }
    public Node getNextSibling() {
        return null;
    }
    public String getNodeName() {
        return null;
    }
    public short getNodeType() {
        return 0;
    }
    public String getNodeValue() {
        return null;
    }
    public Document getOwnerDocument() {
        return null;
    }
    public Node getParentNode() {
        return null;
    }
    public String getPrefix() {
        return null;
    }
    public Node getPreviousSibling() {
        return null;
    }
    public String getTextContent() {
        return null;
    }
    public Object getUserData(String key) {
        return null;
    }
    public boolean hasAttributes() {
        return false;
    }
    public boolean hasChildNodes() {
        return false;
    }
    public Node insertBefore(Node n, Node r) {
        return n;
    }
    public boolean isDefaultNamespace(String u) {
        return false;
    }
    public boolean isEqualNode(Node n) {
        return isSameNode(n);
    }
    public boolean isSameNode(Node n) {
        return this == n;
    }
    public boolean isSupported(String f, String v) {
        return false;
    }
    public String lookupNamespaceURI(String s) {
        return null;
    }
    public String lookupPrefix(String s) {
        return null;
    }
    public void normalize() { }
    public Node removeChild(Node n) {
        return n;
    }
    public Node replaceChild(Node n, Node o) {
        return o;
    }
    public void setNodeValue(String s) { }
    public void setPrefix(String s) { }
    public void setTextContent(String s) { }
    public Object setUserData(String k, Object d, UserDataHandler h) {
        return null;
    }
}
