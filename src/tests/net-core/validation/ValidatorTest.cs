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
using System.Collections.Generic;
using NUnit.Framework;
using net.sf.xmlunit.exceptions;
using net.sf.xmlunit.input;

namespace net.sf.xmlunit.validation {
    [TestFixture]
    public class ValidatorTest {
        [Test]
        public void ShouldSuccessfullyValidateSchema() {
            Validator v = Validator.ForLanguage(Languages.W3C_XML_SCHEMA_NS_URI);
            v.SchemaSource = new StreamSource("../../../src/tests/resources/Book.xsd");
            ValidationResult r = v.ValidateSchema();
            Assert.IsTrue(r.Valid);
            Assert.IsFalse(r.Problems.GetEnumerator().MoveNext());
        }

        [Test]
        public void ShouldSuccessfullyValidateInstance() {
            Validator v = Validator.ForLanguage(Languages.W3C_XML_SCHEMA_NS_URI);
            v.SchemaSource = new StreamSource("../../../src/tests/resources/Book.xsd");
            ValidationResult r = v.ValidateInstance(new StreamSource("../../../src/tests/resources/BookXsdGenerated.xml"));
            IEnumerator<ValidationProblem> problems = r.Problems.GetEnumerator();
            bool haveErrors = problems.MoveNext();

            Assert.IsTrue(r.Valid,
                          "Expected validation to pass, first validation error"
                          + " is "
                          + (haveErrors ? problems.Current.Message : "unknown"));
            Assert.IsFalse(haveErrors);
        }

        [Test]
        public void ShouldFailOnBrokenSchema() {
            Validator v = Validator.ForLanguage(Languages.W3C_XML_SCHEMA_NS_URI);
            v.SchemaSource = new StreamSource("../../../src/tests/resources/broken.xsd");
            ValidationResult r = v.ValidateSchema();
            Assert.IsFalse(r.Valid);
            Assert.IsTrue(r.Problems.GetEnumerator().MoveNext());
        }

        [Test]
        public void ShouldFailOnBrokenInstance() {
            Validator v = Validator.ForLanguage(Languages.W3C_XML_SCHEMA_NS_URI);
            v.SchemaSource = new StreamSource("../../../src/tests/resources/Book.xsd");
            ValidationResult r = v.ValidateInstance(new StreamSource("../../../src/tests/resources/invalidBook.xml"));
            Assert.IsFalse(r.Valid);
            Assert.IsTrue(r.Problems.GetEnumerator().MoveNext());
        }

        [Test]
        public void ShouldThrowWhenValidatingInstanceAndSchemaIsInvalid() {
            Validator v = Validator.ForLanguage(Languages.W3C_XML_SCHEMA_NS_URI);
            v.SchemaSource = new StreamSource("../../../src/tests/resources/broken.xsd");
            Assert.Throws(typeof(XMLUnitException), delegate() {
                    v.ValidateInstance(new StreamSource("../../../src/tests/resources/BookXsdGenerated.xml"));
                });
        }

    }
}
