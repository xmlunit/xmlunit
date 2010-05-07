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

using System.Collections.Generic;
using System.Xml;
using NUnit.Framework;

namespace net.sf.xmlunit.util {
    [TestFixture]
    public class NodesTest {

        private const string FOO = "foo";
        private const string BAR = "bar";
        private const string SOME_URI = "urn:some:uri";

        private XmlDocument doc;

        [SetUp]
        public void CreateDoc() {
            doc = new XmlDocument();
        }

        [Test] public void QNameOfElementWithNoNs() {
            XmlElement e = doc.CreateElement(FOO);
            XmlQualifiedName q = Nodes.GetQName(e);
            Assert.AreEqual(FOO, q.Name);
            Assert.AreEqual(string.Empty, q.Namespace);
            Assert.AreEqual(new XmlQualifiedName(FOO), q);
        }

        [Test] public void QNameOfXmlElementWithNsNoPrefix() {
            XmlElement e = doc.CreateElement(FOO, SOME_URI);
            XmlQualifiedName q = Nodes.GetQName(e);
            Assert.AreEqual(FOO, q.Name);
            Assert.AreEqual(SOME_URI, q.Namespace);
            Assert.AreEqual(new XmlQualifiedName(FOO, SOME_URI), q);
        }

        [Test] public void QNameOfXmlElementWithNsAndPrefix() {
            XmlElement e = doc.CreateElement(BAR, FOO, SOME_URI);
            XmlQualifiedName q = Nodes.GetQName(e);
            Assert.AreEqual(FOO, q.Name);
            Assert.AreEqual(SOME_URI, q.Namespace);
            Assert.AreEqual(new XmlQualifiedName(FOO, SOME_URI), q);
        }

        [Test] public void MergeNoTexts() {
            XmlElement e = doc.CreateElement(FOO);
            Assert.AreEqual(string.Empty, Nodes.GetMergedNestedText(e));
        }

        [Test] public void MergeSingleTextNode() {
            XmlElement e = doc.CreateElement(FOO);
            XmlText t = doc.CreateTextNode(BAR);
            e.AppendChild(t);
            Assert.AreEqual(BAR, Nodes.GetMergedNestedText(e));
        }

        [Test] public void MergeSingleCDATASection() {
            XmlElement e = doc.CreateElement(FOO);
            XmlCDataSection t = doc.CreateCDataSection(BAR);
            e.AppendChild(t);
            Assert.AreEqual(BAR, Nodes.GetMergedNestedText(e));
        }

        [Test] public void MergeIgnoresTextOfChildren() {
            XmlElement e = doc.CreateElement(FOO);
            XmlElement c = doc.CreateElement("child");
            XmlText t = doc.CreateTextNode(BAR);
            e.AppendChild(c);
            c.AppendChild(t);
            Assert.AreEqual(string.Empty, Nodes.GetMergedNestedText(e));
        }

        [Test] public void MergeIgnoresComments() {
            XmlElement e = doc.CreateElement(FOO);
            XmlComment c = doc.CreateComment(BAR);
            e.AppendChild(c);
            Assert.AreEqual(string.Empty, Nodes.GetMergedNestedText(e));
        }

        [Test] public void MergeMultipleChildren() {
            XmlElement e = doc.CreateElement(FOO);
            XmlCDataSection c = doc.CreateCDataSection(BAR);
            e.AppendChild(c);
            e.AppendChild(doc.CreateElement("child"));
            XmlText t = doc.CreateTextNode(BAR);
            e.AppendChild(t);
            Assert.AreEqual(BAR + BAR, Nodes.GetMergedNestedText(e));
        }

        [Test] public void AttributeMapNoAttributes() {
            XmlElement e = doc.CreateElement(FOO);
            IDictionary<XmlQualifiedName, string> m = Nodes.GetAttributes(e);
            Assert.AreEqual(0, m.Count);
        }

        [Test] public void AttributeIDictionaryNoNS() {
            XmlElement e = doc.CreateElement(FOO);
            e.SetAttribute(FOO, BAR);
            IDictionary<XmlQualifiedName, string> m = Nodes.GetAttributes(e);
            Assert.AreEqual(1, m.Count);
            Assert.AreEqual(BAR, m[new XmlQualifiedName(FOO)]);
        }

        [Test] public void AttributeIDictionarywithNS() {
            XmlElement e = doc.CreateElement(FOO);
            e.SetAttribute(FOO, SOME_URI, BAR);
            IDictionary<XmlQualifiedName, string> m = Nodes.GetAttributes(e);
            Assert.AreEqual(1, m.Count);
            Assert.AreEqual(BAR, m[new XmlQualifiedName(FOO, SOME_URI)]);
        }
    }
}
