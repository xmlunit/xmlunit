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
using net.sf.xmlunit.transform;

namespace net.sf.xmlunit.builder {

    /// <summary>
    /// Base class providing the common logic of the XSLT related builders.
    /// </summary>
    /// <remarks>
    /// Not intended to be used outside of this package.
    /// I wish there was a way to say <code>: B</code>.
    /// </remarks>
    internal abstract class AbstractTransformationBuilder<B>
        : ITransformationBuilderBase<B>
        where B : class, ITransformationBuilderBase<B> {

        private readonly Transformation t;

        protected AbstractTransformationBuilder(ISource s) {
            t = new Transformation(s);
        }
        public B WithStylesheet(ISource s) {
            t.Stylesheet = s;
            return AsB;
        }
        public B WithExtensionObject(string namespaceUri, object extension) {
            t.AddExtensionObject(namespaceUri, extension);
            return AsB;
        }
        public B WithParameter(string name, string namespaceUri,
                               object parameter) {
            t.AddParameter(name, namespaceUri, parameter);
            return AsB;
        }
        public B WithXmlResolver(XmlResolver r) {
            t.XmlResolver = r;
            return AsB;
        }
        public B WithScripting() {
            return WithScripting(true);
        }
        public B WithoutScripting() {
            return WithScripting(false);
        }
        private B WithScripting(bool b) {
            t.EnableScriptBlocks = b;
            return AsB;
        }
        public B WithDocumentFunction() {
            return WithDocumentFunction(true);
        }
        public B WithoutDocumentFunction() {
            return WithDocumentFunction(false);
        }
        private B WithDocumentFunction(bool b) {
            t.EnableDocumentFunction = b;
            return AsB;
        }

        protected Transformation Helper {
            get {
                return t;
            }
        }

        private B AsB {
            get {
                return this as B;
            }
        }
    }
}