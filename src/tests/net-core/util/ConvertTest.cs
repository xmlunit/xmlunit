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
using NUnit.Framework;
using net.sf.xmlunit.input;

namespace net.sf.xmlunit.util {
    [TestFixture]
    public class ConvertTest {
        private static void ConvertToDocumentAndAssert(ISource s) {
            DocumentAsserts(Convert.ToDocument(s));
        }

        private static void DocumentAsserts(XmlDocument d) {
            Assert.IsNotNull(d);
            Assert.AreEqual("animal", d.DocumentElement.Name);
        }

        [Test]
        public void StreamSourceToDocument() {
            ConvertToDocumentAndAssert(new StreamSource(TestResources.ANIMAL_FILE));
        }

        [Test]
        public void DomSourceToDocument() {
            XmlDocument d = new XmlDocument();
            d.Load(TestResources.ANIMAL_FILE);
            ConvertToDocumentAndAssert(new DOMSource(d));
            Assert.AreSame(d, Convert.ToDocument(new DOMSource(d)));
        }

        private static void ConvertToNodeAndAssert(ISource s) {
            XmlNode n = Convert.ToNode(s);
            DocumentAsserts(n is XmlDocument
                            ? n as XmlDocument : n.OwnerDocument);
        }

        [Test]
        public void StreamSourceToNode() {
            ConvertToNodeAndAssert(new StreamSource(TestResources.ANIMAL_FILE));
        }

        [Test]
        public void DomSourceToNode() {
            XmlDocument d = new XmlDocument();
            d.Load(TestResources.ANIMAL_FILE);
            ConvertToNodeAndAssert(new DOMSource(d));
            Assert.AreSame(d, Convert.ToNode(new DOMSource(d)));
        }
    }
}