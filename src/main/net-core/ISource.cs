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
using System.Xml;

namespace Org.XmlUnit {
    /// <summary>
    /// Representation of the various ways to provide pieces of XML to
    /// XMLUnit.
    /// </summary>
    public interface ISource {
        /// <summary>
        /// Provides the content.
        /// </summary>
        XmlReader Reader {get;}
        /// <summary>
        /// Some sort of Base-URI of this ISource.
        /// </summary>
        string SystemId {get; set;}
    }
}
