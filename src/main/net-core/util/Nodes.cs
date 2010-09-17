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

        /// <summary>
        /// Creates a new Node (of the same type as the original node)
        /// that is similar to the orginal but doesn't contain any
        /// empty text or CDATA nodes and where all textual content
        /// including attribute values or comments are trimmed.
        /// </summary>
        public static XmlNode StripWhitespace(XmlNode original) {
            XmlNode cloned = original.CloneNode(true);
            cloned.Normalize();
            HandleWsRec(cloned, false);
            return cloned;
        }

        /// <summary>
        /// Creates a new Node (of the same type as the original node)
        /// that is similar to the orginal but doesn't contain any
        /// empty text or CDATA nodes and where all textual content
        /// including attribute values or comments are normalized.
        /// </summary>
        /// <remarks>
        /// "normalized" in this context means all whitespace
        /// characters are replaced by space characters and
        /// consecutive whitespace characaters are collapsed.
        /// </remarks>
        public static XmlNode NormalizeWhitespace(XmlNode original) {
            XmlNode cloned = original.CloneNode(true);
            cloned.Normalize();
            HandleWsRec(cloned, true);
            return cloned;
        }

        /// <summary>
        /// Trims textual content of this node, removes empty text and
        /// CDATA children, recurses into its child nodes.
        /// </summary>
        /// <parameter name="normalize">whether to normalize
        /// whitespace as well</parameter>
        private static void HandleWsRec(XmlNode n, bool normalize) {
            if (n is XmlCharacterData || n is XmlProcessingInstruction) {
                string s = n.Value.Trim();
                if (normalize) {
                    s = Normalize(s);
                }
                n.Value = s;
            }
            LinkedList<XmlNode> toRemove = new LinkedList<XmlNode>();
            foreach (XmlNode child in n.ChildNodes) {
                HandleWsRec(child, normalize);
                if (!(n is XmlAttribute)
                    && (child is XmlText || child is XmlCDataSection)
                    && child.Value.Length == 0) {
                    toRemove.AddLast(child);
                }
            }
            foreach (XmlNode child in toRemove) {
                n.RemoveChild(child);
            }
            XmlNamedNodeMap attrs = n.Attributes;
            if (attrs != null) {
                foreach (XmlAttribute a in attrs) {
                    HandleWsRec(a, normalize);
                }
            }
        }

        private const char SPACE = ' ';

        /// <summary>
        /// Normalize a string.
        /// <summary>
        /// <remarks>
        /// "normalized" in this context means all whitespace
        /// characters are replaced by space characters and
        /// consecutive whitespace characaters are collapsed.
        /// </remarks>
        internal static string Normalize(string s) {
            StringBuilder sb = new StringBuilder();
            bool changed = false;
            bool lastCharWasWS = false;
            foreach (char c in s) {
                if (char.IsWhiteSpace(c)) {
                    if (!lastCharWasWS) {
                        sb.Append(SPACE);
                        changed |= (c != SPACE);
                    } else {
                        changed = true;
                    }
                    lastCharWasWS = true;
                } else {
                    sb.Append(c);
                    lastCharWasWS = false;
                }
            }
            return changed ? sb.ToString() : s;
        }
    }
}
