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

namespace net.sf.xmlunit.input {
    public class StreamSource : AbstractSource {
        public StreamSource(TextReader rdr)
            : base(XmlReader.Create(rdr)) {
        }

        public StreamSource(Stream stream)
            : base(XmlReader.Create(stream)) {
        }

        public StreamSource(string uri)
            : base(XmlReader.Create(uri)) {
        }
    }
}
