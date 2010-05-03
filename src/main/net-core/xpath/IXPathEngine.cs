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

namespace net.sf.xmlunit.xpath {

    /// <summary>
    /// Interface for XMLUnit's XPath abstraction.
    /// </summary>
    public interface IXPathEngine {
        /// <summary>
        /// Returns a potentially empty collection of Nodes matching an
        /// XPath expression.
        /// </summary>
        IEnumerable<XmlNode> SelectNodes(string xPath, ISource s);
        /// <summary>
        /// Evaluates an XPath expression and stringifies the result.
        /// </summary>
        string Evaluate(string xPath, ISource s);
        /// <summary>
        /// Establish a namespace context - maps from prefix to namespace URI.
        /// </summary>
        IDictionary<string, string> NamespaceContext { set; }
    }
}