namespace XmlUnit {
    using NUnit.Framework;
    using System.IO;
    
    public class XmlAssertion : Assert {
        public static void AssertXmlEquals(TextReader controlTextReader, TextReader testTextReader) {
            AssertXmlEquals(new XmlDiff(controlTextReader, testTextReader));
        }

        public static void AssertXmlEquals(string controlText, string testText) {
           AssertXmlEquals(new XmlDiff(controlText, testText));
        }

        public static void AssertXmlEquals(XmlInput controlInput, XmlInput testInput) {
            AssertXmlEquals(new XmlDiff(controlInput, testInput));
        }        
        
        public static void AssertXmlIdentical(TextReader controlTextReader, TextReader testTextReader) {
            AssertXmlIdentical(new XmlDiff(controlTextReader, testTextReader));
        }        

        public static void AssertXmlIdentical(string controlText, string testText) {
            AssertXmlIdentical(new XmlDiff(controlText, testText));
        }        
        
        public static void AssertXmlIdentical(XmlInput controlInput, XmlInput testInput) {
            AssertXmlIdentical(new XmlDiff(controlInput, testInput));
        }        
        
        public static void AssertXmlEquals(XmlDiff xmlDiff) {
            AssertXmlEquals(xmlDiff, true);
        }
        
        public static void AssertXmlNotEquals(XmlDiff xmlDiff) {
            AssertXmlEquals(xmlDiff, false);
        }

        private static void AssertXmlEquals(XmlDiff xmlDiff, bool equalOrNot) {
            DiffResult diffResult = xmlDiff.Compare();
            if (equalOrNot) {
              NUnit.Framework.Assert.IsTrue(diffResult.Equal, diffResult.StringValue);
            } else {
              NUnit.Framework.Assert.IsFalse(diffResult.Equal, diffResult.StringValue);
            }
        }
        
        public static void AssertXmlIdentical(XmlDiff xmlDiff) {
            AssertXmlIdentical(xmlDiff, true);
        }
        
        public static void AssertXmlNotIdentical(XmlDiff xmlDiff) {
            AssertXmlIdentical(xmlDiff, false);
        }
        
        private static void AssertXmlIdentical(XmlDiff xmlDiff, bool identicalOrNot) {
            DiffResult diffResult = xmlDiff.Compare();
            if (identicalOrNot) {
              NUnit.Framework.Assert.IsTrue(diffResult.Identical, xmlDiff.OptionalDescription);
            } else {
              NUnit.Framework.Assert.IsFalse(diffResult.Identical, xmlDiff.OptionalDescription);
            }
        }
        
        public static void AssertXmlValid(string someXml) {
            AssertXmlValid(new XmlInput(someXml));
        }
        
        public static void AssertXmlValid(string someXml, string baseURI) {
            AssertXmlValid(new XmlInput(someXml, baseURI));
        }
        
        public static void AssertXmlValid(TextReader reader) {
            AssertXmlValid(new XmlInput(reader));
        }
        
        public static void AssertXmlValid(TextReader reader, string baseURI) {
            AssertXmlValid(new XmlInput(reader, baseURI));
        }
        
        public static void AssertXmlValid(XmlInput xmlInput) {
            Validator validator = new Validator(xmlInput);
            AssertXmlValid(validator);
        }
        
        public static void AssertXmlValid(Validator validator) {
          NUnit.Framework.Assert.IsTrue(validator.IsValid, validator.ValidationMessage);
        }
        
        public static void AssertXPathExists(string anXPathExpression, string inXml) {
            AssertXPathExists(anXPathExpression, new XmlInput(inXml));
        }
        
        public static void AssertXPathExists(string anXPathExpression, TextReader inXml) {
            AssertXPathExists(anXPathExpression, new XmlInput(inXml));
        }
        
        public static void AssertXPathExists(string anXPathExpression, XmlInput inXml) {
            XPath xpath = new XPath(anXPathExpression);
            NUnit.Framework.Assert.AreEqual(true, xpath.XPathExists(inXml));
        }
        
        public static void AssertXPathEvaluatesTo(string anXPathExpression, string inXml, 
                                                  string expectedValue) {
            AssertXPathEvaluatesTo(anXPathExpression, new XmlInput(inXml), expectedValue);
        }
        
        public static void AssertXPathEvaluatesTo(string anXPathExpression, TextReader inXml, 
                                                  string expectedValue) {
            AssertXPathEvaluatesTo(anXPathExpression, new XmlInput(inXml), expectedValue);
        }
                                                  
        public static void AssertXPathEvaluatesTo(string anXPathExpression, XmlInput inXml, 
                                                  string expectedValue) {
            XPath xpath = new XPath(anXPathExpression);
            NUnit.Framework.Assert.AreEqual(expectedValue, xpath.EvaluateXPath(inXml));
        }
        
        public static void AssertXslTransformResults(string xslTransform, string xmlToTransform, string expectedResult) {
        	AssertXslTransformResults(new XmlInput(xslTransform), new XmlInput(xmlToTransform), new XmlInput(expectedResult));
        }
        
        public static void AssertXslTransformResults(XmlInput xslTransform, XmlInput xmlToTransform, XmlInput expectedResult) {
        	Xslt xslt = new Xslt(xslTransform);
        	XmlOutput output = xslt.Transform(xmlToTransform);
        	AssertXmlEquals(expectedResult, output.AsXml());
        }

    }
}
