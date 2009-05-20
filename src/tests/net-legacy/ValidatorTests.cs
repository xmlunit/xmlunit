namespace XmlUnit.Tests {
    using System;
    using System.IO;
    using System.Xml;
    using System.Xml.Schema;
    using NUnit.Framework;
    using XmlUnit;
    
    [TestFixture]
    public class ValidatorTests {
        public static readonly string VALID_FILE = "..\\..\\..\\src\\tests\\resources\\BookXsdGenerated.xml";
        public static readonly string INVALID_FILE = "..\\..\\..\\src\\tests\\resources\\invalidBook.xml";
                
        [Test][Ignore("seems to fail because of schema location")]
        public void XsdValidFileIsValid() {
            PerformAssertion(VALID_FILE, true);
        } 
                
        private Validator PerformAssertion(string file, bool expected) {
            FileStream input = File.Open(file, FileMode.Open, FileAccess.Read);
            try {
                Validator validator = new Validator(new XmlInput(new StreamReader(input)));
                Assert.AreEqual(expected, validator.IsValid);
                return validator;
            } finally {
                input.Close();
            }
        }
        
        [Ignore("validation seems to return the last error on .Net 2.0, need to double check")]
        [Test] public void XsdInvalidFileIsNotValid() {
            Validator validator = PerformAssertion(INVALID_FILE, false);
            Assert.IsFalse(validator.IsValid);
            Assert.IsTrue(validator.ValidationMessage
                          .IndexOf("http://www.publishing.org") > -1,
                          validator.ValidationMessage);
            Assert.IsTrue(validator.ValidationMessage.IndexOf("Book") > -1,
                          validator.ValidationMessage);
        }
    }
}
