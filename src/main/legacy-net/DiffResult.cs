namespace XmlUnit {
	using System; 
	using System.Text; 
	
    public class DiffResult {
        private bool _identical = true;
        private bool _equal = true;
        private Difference _difference;
    	private StringBuilder _stringBuilder;
    	
    	public DiffResult() {
    		_stringBuilder = new StringBuilder();
    	}
        
        public bool Identical {
            get {
                return _identical;
            }
        }
        
        public bool Equal {
            get {
                return _equal;
            }
        }
        
        public Difference Difference {
            get {
                return _difference;
            }
        }
     
        public string StringValue {
        	get {
	        	if (_stringBuilder.Length == 0) {
	        		if (Identical) {
	        			_stringBuilder.Append("Identical");        			
	        		} else {
	        			_stringBuilder.Append("Equal");
	        		}
	        	}
	        	return _stringBuilder.ToString();
        	}
        }
        
        public void DifferenceFound(XmlDiff inDiff, Difference difference) {
            _identical = false;
            if (difference.MajorDifference) {
                _equal = false;
            }       
            _difference = difference;
        	if (_stringBuilder.Length == 0) {
        		_stringBuilder.Append(inDiff.OptionalDescription);
        	}
        	_stringBuilder.Append(Environment.NewLine).Append(difference);
        }        
    }
	
}
