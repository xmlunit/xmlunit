namespace XmlUnit {
    using System.IO;
    using System.Text;
    using System.Xml.XPath;
    
    public class XPath {
        private readonly string _xPathExpression;
        
        public XPath(string anXPathExpression) {
            _xPathExpression = anXPathExpression;
        }
        
        public bool XPathExists(string forSomeXml) {
            return XPathExists(new XmlInput(forSomeXml));
        }
        
        public bool XPathExists(XmlInput forInput) {
            XPathNodeIterator iterator = GetNodeIterator(forInput);
            return (iterator.Count > 0);
        }
                
        private XPathNodeIterator GetNodeIterator(XmlInput forXmlInput) {
            XPathNavigator xpathNavigator = GetNavigator(forXmlInput);
            return xpathNavigator.Select(_xPathExpression);            
        }
                
        private XPathNavigator GetNavigator(XmlInput forXmlInput) {            
            XPathDocument xpathDocument = 
                new XPathDocument(forXmlInput.CreateXmlReader());
            return xpathDocument.CreateNavigator();
        }
                
        public string EvaluateXPath(string forSomeXml) {
            return EvaluateXPath(new XmlInput(forSomeXml));
        }
        
        public string EvaluateXPath(XmlInput forXmlInput) {
            XPathNavigator xpathNavigator = GetNavigator(forXmlInput);
            XPathExpression xPathExpression = xpathNavigator.Compile(_xPathExpression);
            if (xPathExpression.ReturnType == XPathResultType.NodeSet) {
                return EvaluateXPath(xpathNavigator);
            } else {
                return xpathNavigator.Evaluate(xPathExpression).ToString();
            }
        }
        
        private string EvaluateXPath(XPathNavigator forXPathNavigator) {
            XPathNodeIterator iterator = forXPathNavigator.Select(_xPathExpression);
            
            StringBuilder stringBuilder = new StringBuilder();
            XPathNavigator xpathNavigator;
            
            while (iterator.MoveNext()) {
                xpathNavigator = iterator.Current;
                stringBuilder.Insert(stringBuilder.Length, xpathNavigator.Value);
            }
            return stringBuilder.ToString();
        }
    }
}
