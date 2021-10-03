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
package org.xmlunit.assertj3;

import org.w3c.dom.Node;
import org.xmlunit.builder.Input;
import org.xmlunit.util.Convert;
import org.xmlunit.util.Nodes;

import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;

class NodeUtils {

    private NodeUtils() {
    }

    static String attributeValue(Node node, String attributeName) {

        Map<QName, String> attributes = Nodes.getAttributes(node);

        for (Map.Entry<QName, String> entry : attributes.entrySet()) {
            final QName qName = entry.getKey();
            if (matchQName(qName, attributeName)) {
                return entry.getValue();
            }
        }

        return null;
    }

    static Node parseSource(Object xmlSource, XmlAssertConfig config) {
        Source s = Input.from(xmlSource).build();
        return config.dbf != null ? Convert.toNode(s, config.dbf) : Convert.toNode(s);
    }

    private static boolean matchQName(QName qName, String name) {

        return qName.toString().equals(name)
                || (qName.getPrefix() + ":" + qName.getLocalPart()).equals(name)
                || qName.getLocalPart().equals(name);
    }
}
