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

using net.sf.xmlunit.util;

namespace net.sf.xmlunit.input {

    /// <summary>
    /// A source that is obtained from a different source by removing
    /// all empty text nodes and normalizing the non-empty ones.
    /// </summary>
    /// <remarks>
    /// "normalized" in this context means all whitespace characters
    /// are replaced by space characters and consecutive whitespace
    /// characaters are collapsed.
    /// </remarks>
    public class WhitespaceNormalizedSource : DOMSource {
        public WhitespaceNormalizedSource(ISource originalSource) :
            base(Nodes.NormalizeWhitespace(Convert.ToDocument(originalSource)))
            {
            SystemId = originalSource.SystemId;
        }
    }
}
