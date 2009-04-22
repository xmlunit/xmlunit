namespace XmlUnit {
    using System.IO;
    using System.Xml;
    
    public class XmlInput {
        private delegate XmlReader XmlInputTranslator(object originalInput, string baseURI);
        private readonly string _baseURI;
        private readonly object _originalInput;
        private readonly XmlInputTranslator _translateInput;
    	private static readonly string CURRENT_FOLDER = ".";
    	
    	private XmlInput(string baseURI, object someXml, XmlInputTranslator translator) {
    		_baseURI = baseURI;
    	     _originalInput = someXml;
            _translateInput = translator;
        }
        
        public XmlInput(string someXml, string baseURI) :
        	this(baseURI, someXml, new XmlInputTranslator(TranslateString)) {
        }
        
        public XmlInput(string someXml) :
        	this(someXml, CURRENT_FOLDER) {
        }
        
        private static XmlReader TranslateString(object originalInput, string baseURI) {
            return new XmlTextReader(baseURI, new StringReader((string) originalInput));
        }
        
        public XmlInput(Stream someXml, string baseURI) :
        	this(baseURI, someXml, new XmlInputTranslator(TranslateStream)) {
        }
        
        public XmlInput(Stream someXml) :
        	this(someXml, CURRENT_FOLDER) {
        }
                
        private static XmlReader TranslateStream(object originalInput, string baseURI) {
            return new XmlTextReader(baseURI, new StreamReader((Stream) originalInput));
        }
        
        public XmlInput(TextReader someXml, string baseURI) :
        	this(baseURI, someXml, new XmlInputTranslator(TranslateReader)) {
        }
        
        public XmlInput(TextReader someXml) :
        	this(someXml, CURRENT_FOLDER) {
        }
                
        private static XmlReader TranslateReader(object originalInput, string baseURI) {
            return new XmlTextReader(baseURI, (TextReader) originalInput);
        }
        
        public XmlInput(XmlReader someXml) :
        	this(null, someXml, new XmlInputTranslator(NullTranslator)) {
        }
                
        private static XmlReader NullTranslator(object originalInput, string baseURI) {
            return (XmlReader) originalInput;
        }
        
        public XmlReader CreateXmlReader() {
            return _translateInput(_originalInput, _baseURI);
        }
                
        public override bool Equals(object other) {
            if (other != null && other is XmlInput) {
                return _originalInput.Equals(((XmlInput)other)._originalInput);
            }
            return false;
        }
        
        public override int GetHashCode() {
            return _originalInput.GetHashCode();
        }
    }
}
