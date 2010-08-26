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
using System.Collections.Generic;
using System.Text;
using System.Xml;
using net.sf.xmlunit.util;

namespace net.sf.xmlunit.diff {
    public class XPathContext {
        private readonly LinkedList<Level> path = new LinkedList<Level>();
        private readonly IDictionary<string, string> uri2Prefix;

        private const string COMMENT = "comment()";
        private const string PI = "processing-instruction()";
        private const string TEXT = "text()";
        private const string OPEN = "[";
        private const string CLOSE = "]";
        private const string SEP = "/";
        private const string ATTR = "@";

        public XPathContext() : this(null) {
        }

        public XPathContext(IDictionary<string, string> uri2Prefix) {
            if (uri2Prefix == null) {
                this.uri2Prefix = new Dictionary<string, string>();
            } else {
                this.uri2Prefix = new Dictionary<string, string>(uri2Prefix);
            }
            path.AddLast(new Level(""));
        }

        public void NavigateToChild(int index) {
            path.AddLast(path.Last.Value.Children[index]);
        }

        public void NavigateToAttribute(XmlQualifiedName attribute) {
            path.AddLast(path.Last.Value.Attributes[attribute]);
        }

        public void NavigateToParent() {
            path.RemoveLast();
        }

        public void AddAttributes<Q>(IEnumerable<Q> attributes)
            where Q : XmlQualifiedName {
            Level current = path.Last.Value;
            foreach (XmlQualifiedName attribute in attributes) {
                current.Attributes[attribute] =
                    new Level(ATTR + GetName(attribute));
            }
        }

        public void SetChildren<N>(IEnumerable<N> children) 
            where N : INodeInfo {
            Level current = path.Last.Value;
            current.Children.Clear();
            AppendChildren(children);
        }

        public void AppendChildren<N>(IEnumerable<N> children) 
            where N : INodeInfo {
            Level current = path.Last.Value;
            int comments, pis, texts;
            comments = pis = texts = 0;
            IDictionary<string, int> elements = new Dictionary<string, int>();

            foreach (Level l in current.Children) {
                string childName = l.Expression;
                if (childName.StartsWith(COMMENT)) {
                    comments++;
                } else if (childName.StartsWith(PI)) {
                    pis++;
                } else if (childName.StartsWith(TEXT)) {
                    texts++;
                } else {
                    childName = childName.Substring(0, childName.IndexOf(OPEN));
                    Add1OrIncrement(childName, elements);
                }
            }

            foreach (INodeInfo child in children) {
                Level l = null;
                switch (child.Type) {
                case XmlNodeType.Comment:
                    l = new Level(COMMENT + OPEN + (++comments) + CLOSE);
                    break;
                case XmlNodeType.ProcessingInstruction:
                    l = new Level(PI + OPEN + (++pis) + CLOSE);
                    break;
                case XmlNodeType.CDATA:
                case XmlNodeType.Text:
                    l = new Level(TEXT + OPEN + (++texts) + CLOSE);
                    break;
                case XmlNodeType.Element:
                    string name = GetName(child.Name);
                    l = new Level(name + OPEN + Add1OrIncrement(name, elements)
                                  + CLOSE);
                    break;
                default:
                    // more or less ignore
                    // FIXME: is this a good thing?
                    l = new Level(string.Empty);
                    break;
                }
                current.Children.Add(l);
            }
        }

        public string XPath {
            get {
                StringBuilder sb = new StringBuilder();
                foreach (Level l in path) {
                    sb.AppendFormat(SEP + "{0}", l.Expression);
                }
                return sb.Replace(SEP + SEP, SEP).ToString();
            }
        }

        private string GetName(XmlQualifiedName name) {
            string ns = name.Namespace;
            string p = null;
            if (ns != null) {
                uri2Prefix.TryGetValue(ns, out p);
            }
            return (p == null ? "" : p + ":") + name.Name;
        }

        /// <summary>
        /// Increments the value name maps to or adds 1 as value if name
        /// isn't present inside the map.
        /// </summary>
        /// <returns>the new mapping for name</returns>
        private static int Add1OrIncrement(string name,
                                           IDictionary<string, int> map) {
            int index = 0;
            map.TryGetValue(name, out index);
            map[name] = ++index;
            return index;
        }

        internal class Level {
            internal readonly string Expression;
            internal readonly IList<Level> Children = new List<Level>();
            internal readonly IDictionary<XmlQualifiedName, Level> Attributes =
                new Dictionary<XmlQualifiedName, Level>();
            internal Level(string expression) {
                this.Expression = expression;
            }
        }

        public interface INodeInfo {
            XmlQualifiedName Name { get; }
            XmlNodeType Type { get; }
        }

        public class DOMNodeInfo : INodeInfo {
            private XmlQualifiedName name;
            private XmlNodeType type;
            public DOMNodeInfo(XmlNode n) {
                name = Nodes.GetQName(n);
                type = n.NodeType;
            }
            public XmlQualifiedName Name { get { return name; } }
            public XmlNodeType Type { get { return type; } }
        }
    }
}