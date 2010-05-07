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

using System.Collections.Generic;
using System.Text;
using System.Xml;

namespace net.sf.xmlunit.util {
    /// <summary>
    /// Utility algorithms that work on DOM nodes.
    /// </summary>
    public sealed class Nodes {
        private Nodes() { }

        /// <summary>
        /// Extracts a Node's name and namespace URI (if any).
        /// </summary>
        public static XmlQualifiedName GetQName(XmlNode n) {
            return new XmlQualifiedName(n.LocalName, n.NamespaceURI);
        }

        /// <summary>
        /// Tries to merge all direct Text and CDATA children of the given
        /// Node and concatenates their value.
        /// </summary>
        /// <return>an empty string if the Node has no Text or CDATA
        /// children.</return>
        public static string GetMergedNestedText(XmlNode n) {
            StringBuilder sb = new StringBuilder();
            foreach (XmlNode child in n.ChildNodes) {
                if (child is XmlText || child is XmlCDataSection) {
                    string s = child.Value;
                    if (s != null) {
                        sb.Append(s);
                    }
                }
            }
            return sb.ToString();
        }

        /// <summary>
        /// Obtains an element's attributes as dictionary.
        /// </summary>
        public static IDictionary<XmlQualifiedName, string>
            GetAttributes(XmlNode n) {
            IDictionary<XmlQualifiedName, string> map =
                new Dictionary<XmlQualifiedName, string>();
            XmlAttributeCollection coll = n.Attributes;
            if (coll != null) {
                foreach (XmlAttribute a in coll) {
                    map[GetQName(a)] = a.Value;
                }
            }
            return map;
        }
    }
}
