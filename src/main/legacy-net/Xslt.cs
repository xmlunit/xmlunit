namespace XmlUnit {
    using System.IO;
    using System.Security.Policy;
    using System.Xml;
    using System.Xml.XPath;
    using System.Xml.Xsl;
    
    public class Xslt {
        private readonly XmlInput _xsltInput;
    	private readonly XmlResolver _xsltResolver;
    	private readonly Evidence _evidence;
    	
        public Xslt(XmlInput xsltInput, XmlResolver xsltResolver, Evidence evidence) {
            _xsltInput = xsltInput;
        	_xsltResolver = xsltResolver;
        	_evidence = evidence;
        }
        
        public Xslt(XmlInput xsltInput) 
        	: this(xsltInput, null, null) {
        }
        
        public Xslt(string xslt, string baseURI) 
            : this(new XmlInput(xslt, baseURI)) {
        }
        
        public Xslt(string xslt) 
            : this(new XmlInput(xslt)) {
        }
        
        public XmlOutput Transform(string someXml) {
        	return Transform(new XmlInput(someXml)); 
        }
        
        public XmlOutput Transform(XmlInput someXml) {
        	return Transform(someXml, null);
        }
        
        public XmlOutput Transform(XmlInput someXml, XsltArgumentList xsltArgs) {
        	return Transform(someXml.CreateXmlReader(), null, xsltArgs);
        }
        
        public XmlOutput Transform(XmlReader xmlTransformed, XmlResolver resolverForXmlTransformed, XsltArgumentList xsltArgs) {
            XslTransform transform = new XslTransform();
	        XmlReader xsltReader = _xsltInput.CreateXmlReader();
	            
            transform.Load(xsltReader, _xsltResolver, _evidence);
            
            XmlSpace space = XmlSpace.Default;
            XPathDocument document = new XPathDocument(xmlTransformed, space);
            XPathNavigator navigator = document.CreateNavigator();
            
            return new XmlOutput(transform, xsltArgs, navigator, resolverForXmlTransformed, 
                                 new XmlReader[] {xmlTransformed, xsltReader});
        }
    }
}
