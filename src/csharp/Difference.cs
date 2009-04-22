namespace XmlUnit {
    using System.Xml;    
    
    public class Difference {
        private readonly DifferenceType _id;
        private readonly bool _majorDifference;
        private XmlNodeType _controlNodeType;
        private XmlNodeType _testNodeType;
        
        public Difference(DifferenceType id) {
            _id = id;
            _majorDifference = Differences.isMajorDifference(id);
        }
        
        public Difference(DifferenceType id, XmlNodeType controlNodeType, XmlNodeType testNodeType) 
        : this(id) {
            _controlNodeType = controlNodeType;
            _testNodeType = testNodeType;
        }
        
        public DifferenceType Id {
            get {
                return _id;
            }
        }
        
        public bool MajorDifference {
            get {
                return _majorDifference;
            }
        }
        
        public XmlNodeType ControlNodeType {
            get {
                return _controlNodeType;
            }
        }
        
        public XmlNodeType TestNodeType {
            get {
                return _testNodeType;
            }
        }
        
        public override string ToString() {
            string asString = base.ToString() + " type: " + (int) _id 
                + ", control Node: " + _controlNodeType.ToString()
                + ", test Node: " + _testNodeType.ToString();            
            return asString;
        }
    }
}
