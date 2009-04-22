namespace XmlUnit.Tests {
    using NUnit.Framework;
    using System;
    using System.Xml;
    using XmlUnit;
    
    [TestFixture]
    public class DiffResultTests {
        private DiffResult _result;
    	private XmlDiff _diff;
        private Difference _majorDifference, _minorDifference;
        
        [SetUp] public void CreateDiffResult() {
            _result = new DiffResult();
        	_diff = new XmlDiff("<a/>", "<b/>");
            _majorDifference = new Difference(DifferenceType.ELEMENT_TAG_NAME_ID, XmlNodeType.Element, XmlNodeType.Element);
            _minorDifference = new Difference(DifferenceType.ATTR_SEQUENCE_ID, XmlNodeType.Comment, XmlNodeType.Comment);
        }
        
        [Test] public void NewDiffResultIsEqualAndIdentical() {
            Assert.AreEqual(true, _result.Identical);
            Assert.AreEqual(true, _result.Equal);
        	Assert.AreEqual("Identical", _result.StringValue);
        }
        
        [Test] public void NotEqualOrIdenticalAfterMajorDifferenceFound() {
            _result.DifferenceFound(_diff, _majorDifference);
            Assert.AreEqual(false, _result.Identical);
            Assert.AreEqual(false, _result.Equal);
        	Assert.AreEqual(_diff.OptionalDescription
        	                       + Environment.NewLine
        	                       + _majorDifference.ToString(), _result.StringValue);
        }
        
        [Test] public void NotIdenticalButEqualAfterMinorDifferenceFound() {
            _result.DifferenceFound(_diff, _minorDifference);
            Assert.AreEqual(false, _result.Identical);
            Assert.AreEqual(true, _result.Equal);
        	Assert.AreEqual(_diff.OptionalDescription
        	                       + Environment.NewLine
        	                       + _minorDifference.ToString(), _result.StringValue);
        }
    }
}
