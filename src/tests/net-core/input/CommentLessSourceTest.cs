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

using NUnit.Framework;
using System.IO;
using System.Xml;

namespace net.sf.xmlunit.input {
    [TestFixture]
    public class CommentLessSourceTest {

        [Test]
        public void StripCommentsAtDifferentLevels() {
            StreamSource s =
                new StreamSource(new StringReader("<?xml version='1.0'?>"
                                                  + "<!-- comment 1 -->"
                                                  + "<foo>"
                                                  + "<!-- comment 2 -->"
                                                  + "</foo>"));
            CommentLessSource cls = new CommentLessSource(s);
            XmlDocument d = net.sf.xmlunit.util.Convert.ToDocument(cls);
            Assert.AreEqual(2, d.ChildNodes.Count);
            Assert.IsTrue(d.ChildNodes[0] is XmlDeclaration);
            Assert.IsTrue(d.ChildNodes[1] is XmlElement);
            Assert.AreEqual(0, d.ChildNodes[1].ChildNodes.Count);
        }
    }
}

