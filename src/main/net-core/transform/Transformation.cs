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

namespace net.sf.xmlunit.transform {

    /// <summary>
    /// Provides a convenience layer over System.Xml.Xsl.
    /// </summary>
    /// <remarks>
    /// Apart from ArgumentExceptions if you try to pass in null
    /// values only the Transform methods will ever throw exceptions
    /// and these will be XMLUnit's exceptions.
    ///
    /// Each invocation of a Transform method will use a fresh
    /// XslCompiledTransform instance, the Transform methods are
    /// thread-safe.
    /// </remarks>
    public sealed class Transformation {
        private ISource source;
        private ISource styleSheet;
        private XmlResolver xmlResolver = null;
        private readonly XsltSettings settings = new XsltSettings();
        private readonly XsltArgumentList args = new XsltArgumentList();

        public Transformation() {
        }
        /// <param name="s">the source to transform - must not be null.</param>
        public Transformation(ISource s) {
            Source = s;
        }
        /// <summary>
        /// Set the source document to transform - must not be null.
        /// </summary>
        public ISource Source {
            set {
                if (value == null) {
                    throw new ArgumentNullException();
                }
                source = value;
            }
        }
        /// <summary>
        /// Set the stylesheet to use - may be null in which case an
        /// identity transformation will be performed.
        /// </summary>
        /// <param name="s">the stylesheet to use</param>
        public ISource Stylesheet {
            set {
                styleSheet = value;
            }
        }
        /// <summary>
        /// Add a named extension object.
        /// </summary>
        public void AddExtensionObject(string namespaceUri, object extension) {
            if (namespaceUri == null) {
                throw new ArgumentNullException("namespaceUri");
            }
            args.AddExtensionObject(namespaceUri, extension);
        }
        /// <summary>
        /// Clears all extension objects and parameters.
        /// </summary>
        public void Clear() {
            args.Clear();
        }
        /// <summary>
        /// Add a named parameter.
        /// </summary>
        public void AddParameter(string name, string nsUri, object parameter) {
            if (name == null) {
                throw new ArgumentNullException("name");
            }
            if (nsUri == null) {
                throw new ArgumentNullException("nsUri");
            }
            args.AddParam(name, nsUri, parameter);
        }
        /// <summary>
        /// Set the resolver to use for document() and xsl:include/import
        /// </summary>
        /// <remarks>may be null in which case an empty XmlUrlResolver
        /// will be used.</remarks>
        public XmlResolver XmlResolver {
            set {
                xmlResolver = value;
            }
        }
        /// <summary>
        /// Whether the document() function will be allowed.
        /// </summary>
        public bool EnableDocumentFunction {
            set {
                settings.EnableDocumentFunction = value;
            }
        }
        /// <summary>
        /// Whether embedded script blocks will be allowed.
        /// </summary>
        public bool EnableScriptBlocks {
            set {
                settings.EnableScript = value;
            }
        }

        /// <summary>
        /// Perform the transformation.
        /// </summary>
        public void TransformTo(Stream stream) {
            if (stream == null) {
                throw new ArgumentNullException("stream");
            }
            Transform(TransformToStream(stream));
        }

        /// <summary>
        /// Perform the transformation.
        /// </summary>
        public void TransformTo(TextWriter writer) {
            if (writer == null) {
                throw new ArgumentNullException("writer");
            }
            Transform(TransformToTextWriter(writer));
        }

        /// <summary>
        /// Perform the transformation.
        /// </summary>
        public void TransformTo(XmlWriter writer) {
            if (writer == null) {
                throw new ArgumentNullException("writer");
            }
            Transform(TransformToXmlWriter(writer));
        }

        /// <summary>
        /// Perform the transformation.
        /// </summary>
        private void Transform(Transformer transformer) {
            if (source == null) {
                throw new ArgumentNullException("source");
            }
            try {
                XslCompiledTransform t = new XslCompiledTransform();
                if (styleSheet != null) {
                    t.Load(styleSheet.Reader, settings, xmlResolver);
                }
                transformer(t, source.Reader, args);
            } catch (System.Exception ex) {
                throw new XMLUnitException(ex);
            }
        }

        /// <summary>
        /// Convenience method that returns the result of the
        /// transformation as a String.
        /// </summary>
        public string TransformToString() {
            StringWriter sw = new StringWriter();
            TransformTo(sw);
            return sw.ToString();
        }

        /// <summary>
        /// Convenience method that returns the result of the
        /// transformation as a Document.
        /// </summary>
        public XmlDocument TransformToDocument() {
            using (MemoryStream ms = new MemoryStream()) {
                TransformTo(ms);
                ms.Flush();
                ms.Seek(0, SeekOrigin.Begin);

                XmlDocument doc = new XmlDocument();
                doc.Load(ms);
                return doc;
            }
        }

        private delegate void Transformer(XslCompiledTransform t,
                                          XmlReader r,
                                          XsltArgumentList args);

        private static Transformer TransformToStream(Stream stream) {
            return delegate(XslCompiledTransform t, XmlReader r,
                            XsltArgumentList args) {
                t.Transform(r, args, stream);
            };
        }

        private static Transformer TransformToTextWriter(TextWriter tw) {
            return delegate(XslCompiledTransform t, XmlReader r,
                            XsltArgumentList args) {
                t.Transform(r, args, tw);
            };
        }

        private static Transformer TransformToXmlWriter(XmlWriter xw) {
            return delegate(XslCompiledTransform t, XmlReader r,
                            XsltArgumentList args) {
                t.Transform(r, args, xw);
            };
        }

    }

}

