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

        public interface ITransformationBuilder {
            IBuilder WithStylesheet(ISource s);
        }

        internal class TransformationStep1 : ITransformationBuilder {
            private ISource sourceDoc;
            internal TransformationStep1(ISource s) {
                sourceDoc = s;
            }
            public IBuilder WithStylesheet(ISource s) {
                return new TransformationStep2(sourceDoc, s);
            }
        }

        internal class TransformationStep2 : IBuilder {
            private ISource source;
            private ISource styleSheet;
            internal TransformationStep2(ISource source, ISource styleSheet) {
                this.source = source;
                this.styleSheet = styleSheet;
            }

            public ISource Build() {
                XslCompiledTransform t = new XslCompiledTransform();
                t.Load(styleSheet.Reader);
                MemoryStream ms = new MemoryStream();
                using (ms) {
                    t.Transform(source.Reader,
                                new XsltArgumentList(),
                                ms);
                }
                return FromMemory(ms.ToArray()).Build();
            }
        }

        public static ITransformationBuilder ByTransforming(ISource s) {
            return new TransformationStep1(s);
        }
    }
}
