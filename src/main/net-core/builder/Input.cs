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
using System.IO;
using System.Xml;
using System.Xml.Xsl;
using net.sf.xmlunit.input;

namespace net.sf.xmlunit.builder {
    public static class Input {
        public interface IBuilder {
            ISource Build();
        }

        internal class DOMBuilder : IBuilder {
            private readonly ISource source;
            internal DOMBuilder(XmlDocument d) {
                source = new DOMSource(d);
            }
            public ISource Build() {
                return source;
            }
        }

        public static IBuilder FromDocument(XmlDocument d) {
            return new DOMBuilder(d);
        }

        internal class StreamBuilder : IBuilder {
            private readonly ISource source;
            internal StreamBuilder(string s) {
                source = new StreamSource(s);
            }
            internal StreamBuilder(Stream s) {
                source = new StreamSource(s);
            }
            internal StreamBuilder(TextReader r) {
                source = new StreamSource(r);
            }
            public ISource Build() {
                return source;
            }
        }

        public static IBuilder FromFile(string name) {
            return new StreamBuilder(name);
        }

        public static IBuilder FromStream(Stream s) {
            return new StreamBuilder(s);
        }

        public static IBuilder FromReader(TextReader r) {
            return new StreamBuilder(r);
        }

        public static IBuilder FromMemory(string s) {
            return FromReader(new StringReader(s));
        }

        public static IBuilder FromMemory(byte[] b) {
            return FromStream(new MemoryStream(b));
        }

        public static IBuilder FromURI(string uri) {
            return new StreamBuilder(uri);
        }

        public static IBuilder FromURI(System.Uri uri) {
            return new StreamBuilder(uri.AbsoluteUri);
        }

        public interface ITransformationBuilder : IBuilder {
            ITransformationBuilder WithStylesheet(ISource s);
            ITransformationBuilder WithExtensionObject(string namespaceUri,
                                                       object extension);
            ITransformationBuilder WithParameter(string name,
                                                 string namespaceUri,
                                                 object parameter);
        }

        internal class Transformation : ITransformationBuilder {
            private readonly ISource source;
            private ISource styleSheet;
            private readonly XsltArgumentList args = new XsltArgumentList();
            internal Transformation(ISource s) {
                source = s;
            }
            public ITransformationBuilder WithStylesheet(ISource s) {
                this.styleSheet = s;
                return this;
            }

            public ITransformationBuilder WithExtensionObject(string namespaceUri,
                                                              object extension) {
                args.AddExtensionObject(namespaceUri, extension);
                return this;
            }

            public ITransformationBuilder WithParameter(string name,
                                                        string namespaceUri,
                                                        object parameter) {
                args.AddParam(name, namespaceUri, parameter);
                return this;
            }

            public ISource Build() {
                try {
                XslCompiledTransform t = new XslCompiledTransform();
                if (styleSheet != null) {
                    t.Load(styleSheet.Reader);
                }
                MemoryStream ms = new MemoryStream();
                using (ms) {
                    t.Transform(source.Reader,
                                args,
                                ms);
                }
                return FromMemory(ms.ToArray()).Build();
                } catch (Exception ex) {
                    throw new XMLUnitException(ex);
                }
            }
        }

        public static ITransformationBuilder ByTransforming(ISource s) {
            return new Transformation(s);
        }
    }
}
