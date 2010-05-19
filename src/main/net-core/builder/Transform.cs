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

namespace net.sf.xmlunit.builder {

    /// <summary>
    /// Fluent API access to net.sf.xmlunit.transform.Transformation.
    /// </summary>
    public sealed class Transform {

        public interface IBuilder : ITransformationBuilderBase<IBuilder> {
            /// <summary>
            /// Create the result of the transformation.
            /// </summary>
            ITransformationResult Build();
        }
        public interface ITransformationResult {
            /// <summary>
            /// Output the result to a stream.
            /// </summary>
            void To(Stream s);
            /// <summary>
            /// Output the result to a writer.
            /// </summary>
            void To(TextWriter t);
            /// <summary>
            /// Output the result to a writer.
            /// </summary>
            void To(XmlWriter x);
            /// <summary>
            /// Output the result to a string.
            /// </summary>
            string ToString();
            /// <summary>
            /// Output the result to a DOM Document.
            /// </summary>
            XmlDocument ToDocument();
        }

        internal class TransformationBuilder
            : AbstractTransformationBuilder<IBuilder>, IBuilder,
              ITransformationResult {

            internal TransformationBuilder(ISource s) : base(s) {
            }
            public ITransformationResult Build() {
                return this;
            }
            public override string ToString() {
                return Helper.TransformToString();
            }
            public XmlDocument ToDocument() {
                return Helper.TransformToDocument();
            }
            public void To(Stream s) {
                Helper.TransformTo(s);
            }
            public void To(TextWriter w) {
                Helper.TransformTo(w);
            }
            public void To(XmlWriter w) {
                Helper.TransformTo(w);
            }
        }

        /// <summary>
        /// Build a transformation for a source document.
        /// </summary>
        public static IBuilder Source(ISource s) {
            return new TransformationBuilder(s);
        }
    }
}