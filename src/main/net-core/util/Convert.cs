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
using System.Xml;
using net.sf.xmlunit.input;

namespace net.sf.xmlunit.util {
    /// <summary>
    /// Conversion methods.
    /// </summary>
    public sealed class Convert {
        private Convert() { }

        /// <summary>
        /// Creates a DOM Document from an ISource.
        /// </summary>
        public static XmlDocument ToDocument(ISource s) {
            DOMSource ds = s as DOMSource;
            if (ds != null) {
                XmlDocument doc = ds.Node as XmlDocument;
                if (doc != null) {
                    return doc;
                }
            }
            XmlDocument d = new XmlDocument();
            d.Load(s.Reader);
            return d;
        }

        /// <summary>
        /// Creates a DOM Node from an ISource.
        /// </summary>
        /// <remarks>
        /// Unless the source is a DOMSource this will return the same
        /// result as ToDocument.
        /// </remarks>
        public static XmlNode ToNode(ISource s) {
            DOMSource ds = s as DOMSource;
            return ds != null ? ds.Node : ToDocument(s);
        }

        /// <summary>
        /// Creates a namespace resolver from a Map prefix =&gt;
        /// Namespace URI.
        /// </summary>
        public static XmlNamespaceManager
            ToNamespaceContext(IDictionary<string, string> prefix2URI) {
            XmlNamespaceManager man = new XmlNamespaceManager(new NameTable());
            foreach (KeyValuePair<string, string> kv in prefix2URI) {
                man.AddNamespace(kv.Key, kv.Value);
            }
            return man;
        }
    }
}
