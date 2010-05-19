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

    internal abstract class AbstractTransformationBuilder<T>
        : ITransformationBuilderBase<T>
          where T : class, ITransformationBuilderBase<T> {
        private readonly Transformation t;

        protected AbstractTransformationBuilder(ISource s) {
            t = new Transformation(s);
        }
        public T WithStylesheet(ISource s) {
            t.Stylesheet = s;
            return this as T;
        }
        public T WithExtensionObject(string namespaceUri, object extension) {
            t.AddExtensionObject(namespaceUri, extension);
            return this as T;
        }
        public T WithParameter(string name, string namespaceUri,
                               object parameter) {
            t.AddParameter(name, namespaceUri, parameter);
            return this as T;
        }
        public T WithXmlResolver(XmlResolver r) {
            t.XmlResolver = r;
            return this as T;
        }
        public T WithScripting() {
            return WithScripting(true);
        }
        public T WithoutScripting() {
            return WithScripting(false);
        }
        private T WithScripting(bool b) {
            t.EnableScriptBlocks = b;
            return this as T;
        }
        public T WithDocumentFunction() {
            return WithDocumentFunction(true);
        }
        public T WithoutDocumentFunction() {
            return WithDocumentFunction(false);
        }
        private T WithDocumentFunction(bool b) {
            t.EnableDocumentFunction = b;
            return this as T;
        }

        protected Transformation Helper {
            get {
                return t;
            }
        }
    }
}