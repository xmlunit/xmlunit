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
using System.IO;
using System.Xml;
using System.Xml.Xsl;
using net.sf.xmlunit.exceptions;
using net.sf.xmlunit.input;

namespace net.sf.xmlunit.builder {
    public static class Input {
        public interface IBuilder {
            ISource Build();
        }

        internal class DOMBuilder : IBuilder {
            private readonly ISource source;
            internal DOMBuilder(XmlNode d) {
                source = new DOMSource(d);
            }
            public ISource Build() {
                return source;
            }
        }

        public static IBuilder FromDocument(XmlDocument d) {
            return new DOMBuilder(d);
        }

        public static IBuilder FromNode(XmlNode n) {
            return new DOMBuilder(n);
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
            internal string SystemId {
                set {
                    source.SystemId = value ?? string.Empty;
                }
            }
            public ISource Build() {
                return source;
            }
        }

        public static IBuilder FromFile(string name) {
            return new StreamBuilder(name);
        }

        public static IBuilder FromStream(Stream s) {
            StreamBuilder b = new StreamBuilder(s);
            if (s is FileStream) {
                b.SystemId = new Uri(Path.GetFullPath((s as FileStream).Name))
                    .ToString();
            }
            return b;
        }

        public static IBuilder FromReader(TextReader r) {
            StreamBuilder b = new StreamBuilder(r);
            StreamReader s = r as StreamReader;
            if (s != null && s.BaseStream is FileStream) {
                b.SystemId =
                    new Uri(Path.GetFullPath((s.BaseStream as FileStream).Name))
                    .ToString();
            }
            return b;
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
            ITransformationBuilder WithDocumentFunction();
            ITransformationBuilder WithExtensionObject(string namespaceUri,
                                                       object extension);
            ITransformationBuilder WithParameter(string name,
                                                 string namespaceUri,
                                                 object parameter);
            ITransformationBuilder WithScripting();
            ITransformationBuilder WithStylesheet(ISource s);
            ITransformationBuilder WithStylesheet(IBuilder b);
            ITransformationBuilder WithXmlResolver(XmlResolver r);
            ITransformationBuilder WithoutDocumentFunction();
            ITransformationBuilder WithoutScripting();
        }

        internal class Transformation : ITransformationBuilder {
            private readonly ISource source;
            private ISource styleSheet;
            private XmlResolver xmlResolver = new XmlUrlResolver();
            private readonly XsltSettings settings = new XsltSettings();
            private readonly XsltArgumentList args = new XsltArgumentList();
            internal Transformation(ISource s) {
                source = s;
            }
            public ITransformationBuilder WithStylesheet(ISource s) {
                this.styleSheet = s;
                return this;
            }
            public ITransformationBuilder WithStylesheet(IBuilder b) {
                return WithStylesheet(b.Build());
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

            public ITransformationBuilder WithXmlResolver(XmlResolver r) {
                xmlResolver = r;
                return this;
            }

            public ITransformationBuilder WithScripting() {
                return WithScripting(true);
            }

            public ITransformationBuilder WithoutScripting() {
                return WithScripting(false);
            }

            private ITransformationBuilder WithScripting(bool b) {
                settings.EnableScript = b;
                return this;
            }

            public ITransformationBuilder WithDocumentFunction() {
                return WithDocumentFunction(true);
            }

            public ITransformationBuilder WithoutDocumentFunction() {
                return WithDocumentFunction(false);
            }

            private ITransformationBuilder WithDocumentFunction(bool b) {
                settings.EnableDocumentFunction = b;
                return this;
            }

            public ISource Build() {
                try {
                    XslCompiledTransform t = new XslCompiledTransform();
                    if (styleSheet != null) {
                        t.Load(styleSheet.Reader, settings, xmlResolver);
                    }
                    MemoryStream ms = new MemoryStream();
                    using (ms) {
                        t.Transform(source.Reader,
                                    args,
                                    ms);
                    }
                    return FromMemory(ms.ToArray()).Build();
                } catch (System.Exception ex) {
                    throw new XMLUnitException(ex);
                }
            }
        }

        public static ITransformationBuilder ByTransforming(ISource s) {
            return new Transformation(s);
        }
        public static ITransformationBuilder ByTransforming(IBuilder b) {
            return ByTransforming(b.Build());
        }
    }
}
