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
using System.Text;
using System.Xml;
using NUnit.Framework;

namespace net.sf.xmlunit.builder {

    [TestFixture]
    public class InputTest {

        private const string TEST_FILE = "../../../src/tests/resources/test1.xml";

        private static XmlDocument Parse(ISource s) {
            XmlDocument d = new XmlDocument();
            d.Load(s.Reader);
            return d;
        }

        [Test] public void ShouldParseADocument() {
            XmlDocument d = Parse(Input.FromFile(TEST_FILE).Build());
            AllIsWellFor(Input.FromDocument(d).Build());
        }

        [Test] public void ShouldParseAnExistingFileByName() {
            AllIsWellFor(Input.FromFile(TEST_FILE).Build());
        }

        [Test] public void ShouldParseAnExistingFileFromStream() {
            using (FileStream fs = new FileStream(TEST_FILE, FileMode.Open,
                                                  FileAccess.Read)) {
                AllIsWellFor(Input.FromStream(fs).Build());
            }
        }

        [Test] public void ShouldParseAnExistingFileFromReader() {
            using (StreamReader r = new StreamReader(TEST_FILE)) {
                AllIsWellFor(Input.FromReader(r).Build());
            }
        }

        [Test] public void ShouldParseString() {
            AllIsWellFor(Input.FromMemory(Encoding.UTF8.GetString(ReadTestFile()))
                         .Build());
        }

        [Test] public void ShouldParseBytes() {
            AllIsWellFor(Input.FromMemory(ReadTestFile()).Build());
        }

        [Ignore("looks as if file-URIs didn't work, revisit")]
        [Test] public void ShouldParseFileFromURIString() {
            AllIsWellFor(Input.FromURI("file:" + TEST_FILE).Build());
        }

        [Ignore("looks as if file-URIs didn't work, revisit")]
        [Test] public void ShouldParseFileFromURI() {
            AllIsWellFor(Input.FromURI(new Uri("file:" + TEST_FILE)).Build());
        }

        [Test] public void ShouldParseATransformationFromSource() {
            ISource input = Input.FromMemory("<animal>furry</animal>").Build();
            ISource s = Input.ByTransforming(input)
                .WithStylesheet(Input.FromFile("../../../src/tests/resources/animal.xsl")
                                .Build())
                .Build();
            Assert.That(s, Is.Not.Null);
            XmlDocument d = Parse(s);
            Assert.That(d, Is.Not.Null);
            Assert.That(d.DocumentElement.Name, Is.EqualTo("furry"));
        }

        [Test] public void ShouldParseATransformationFromBuilder() {
            Input.IBuilder input = Input.FromMemory("<animal>furry</animal>");
            ISource s = Input.ByTransforming(input)
                .WithStylesheet(Input.FromFile("../../../src/tests/resources/animal.xsl"))
                .Build();
            Assert.That(s, Is.Not.Null);
            XmlDocument d = Parse(s);
            Assert.That(d, Is.Not.Null);
            Assert.That(d.DocumentElement.Name, Is.EqualTo("furry"));
        }

        private static void AllIsWellFor(ISource s) {
            Assert.That(s, Is.Not.Null);
            XmlDocument d = Parse(s);
            Assert.That(d, Is.Not.Null);
            Assert.That(d.DocumentElement.Name, Is.EqualTo("animal"));
        }

        private static byte[] ReadTestFile() {
            using (FileStream fs = new FileStream(TEST_FILE, FileMode.Open,
                                                  FileAccess.Read))
            using (MemoryStream ms = new MemoryStream()) {
                byte[] buffer = new byte[1024];
                int read = -1;
                while ((read = fs.Read(buffer, 0, buffer.Length)) > 0) {
                    ms.Write(buffer, 0, read);
                }
                return ms.ToArray();
            }
        }
    }
}