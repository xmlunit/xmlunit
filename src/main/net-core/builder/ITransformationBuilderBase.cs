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

namespace net.sf.xmlunit.builder {

    /// <summary>
    /// Holds the common builder methods for XSLT related builders.
    /// </summary>
    /// <remarks>
    /// B is the derived builder interface.
    /// </remarks>
    public interface ITransformationBuilderBase<B>
        where B : ITransformationBuilderBase<B> {
        /// <summary>
        /// Enables the document() function
        /// </summary>
        B WithDocumentFunction();
        /// <summary>
        /// Adds an extension object.
        /// </summary>
        B WithExtensionObject(string namespaceUri, object extension);
        /// <summary>
        /// Adds a parameter.
        /// </summary>
        B WithParameter(string name, string namespaceUri, object parameter);
        /// <summary>
        /// Enables Script Blocks.
        /// </summary>
        B WithScripting();
        /// <summary>
        /// Sets the stylesheet to use.
        /// </summary>
        B WithStylesheet(ISource s);
        /// <summary>
        /// Sets the resolver to use for the document() function and
        /// xsi:import/include.
        /// </summary>
        B WithXmlResolver(XmlResolver r);
        /// <summary>
        /// Disables the document() function
        /// </summary>
        B WithoutDocumentFunction();
        /// <summary>
        /// Disables Script Blocks.
        /// </summary>
        B WithoutScripting();
    }
}