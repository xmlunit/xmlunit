namespace XmlUnit.Tests {
    using System;
    using System.IO;
    using System.Xml;
    using System.Xml.Schema;
    using NUnit.Framework;
    using XmlUnit;
    
    [TestFixture]
    public class ValidatorTests {
        public static readonly string VALID_FILE = ".\\..\\tests\\etc\\BookXsdGenerated.xml";
        public static readonly string INVALID_FILE = ".\\..\\tests\\etc\\invalidBook.xml";
                
        [Test] public void XsdValidFileIsValid() {
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
