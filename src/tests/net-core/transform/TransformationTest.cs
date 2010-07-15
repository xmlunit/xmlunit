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
using net.sf.xmlunit.builder;
using NUnit.Framework;

namespace net.sf.xmlunit.transform {
    [TestFixture]
    public class TransformationTest {
        private Transformation t;

        [SetUp] public void CreateTransformation() {
            t = new Transformation(Input.FromFile(TestResources.DOG_FILE)
                                   .Build());
            t.Stylesheet = Input.FromFile(TestResources.ANIMAL_XSL).Build();
        }

        [Test] public void TransformAnimalToString() {
            Assert.AreEqual("<?xml version=\"1.0\" encoding=\"utf-16\"?><dog />",
                            t.TransformToString().Replace("\n", string.Empty));
        }

        [Test] public void TransformAnimalToDocument() {
            XmlDocument doc = t.TransformToDocument();
            Assert.AreEqual("dog", doc.DocumentElement.Name);
        }
    }
}
