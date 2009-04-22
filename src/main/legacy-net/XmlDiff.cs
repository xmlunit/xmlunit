namespace XmlUnit {
    using System;
    using System.Collections;
    using System.IO;
    using System.Xml;
    using System.Xml.Schema;
    
    public class XmlDiff {
        private const string XMLNS_PREFIX = "xmlns";

        private readonly XmlInput controlInput;
        private readonly XmlInput testInput;
        private readonly DiffConfiguration _diffConfiguration;
        private DiffResult _diffResult;
                
        public XmlDiff(XmlInput control, XmlInput test, 
                       DiffConfiguration diffConfiguration) {
            _diffConfiguration =  diffConfiguration;
            controlInput = control;
            testInput = test;
        }
        
        public XmlDiff(XmlInput control, XmlInput test)
            : this(control, test, new DiffConfiguration()) {
        }

        public XmlDiff(TextReader control, TextReader test)
            : this(new XmlInput(control), new XmlInput(test)) {
        }
        
        public XmlDiff(string control, string test) 
            : this(new XmlInput(control), new XmlInput(test)) {
        }
        
        private XmlReader CreateXmlReader(XmlInput forInput) {
            XmlReader xmlReader = forInput.CreateXmlReader();
        	
            if (xmlReader is XmlTextReader) {
                ((XmlTextReader) xmlReader ).WhitespaceHandling = _diffConfiguration.WhitespaceHandling;
            }
            
            if (_diffConfiguration.UseValidatingParser) {
                XmlValidatingReader validatingReader = new XmlValidatingReader(xmlReader);
                return validatingReader;
            }
            
            return xmlReader;
        }
        
        public DiffResult Compare() {
            if (_diffResult == null) {
                _diffResult = new DiffResult();
                using (XmlReader controlReader = CreateXmlReader(controlInput))
                using (XmlReader testReader = CreateXmlReader(testInput)) {
                    if (!controlInput.Equals(testInput)) {
                        Compare(_diffResult, controlReader, testReader);
                    }
                }
            }
            return _diffResult;
        }
        
        private void Compare(DiffResult result, XmlReader controlReader,
                             XmlReader testReader) {
            try {
                ReaderWithState control = new ReaderWithState(controlReader);
                ReaderWithState test = new ReaderWithState(testReader);
                do {
                    control.Read();
                    test.Read();
                    Compare(result, control, test);
                } while (control.HasRead && test.HasRead) ;
            } catch (FlowControlException e) {       
                Console.Out.WriteLine(e.Message);
            }
        }        
        
        private void Compare(DiffResult result, ReaderWithState control,
                             ReaderWithState test) {
            if (control.HasRead) {
                if (test.HasRead) {
                    CompareNodes(result, control, test);
                    CheckEmptyOrAtEndElement(result, control, test);
                } else {
                    DifferenceFound(DifferenceType.CHILD_NODELIST_LENGTH_ID, result);
                } 
            }
        }
                
        private void CompareNodes(DiffResult result, ReaderWithState control,
                                  ReaderWithState test) {
            XmlNodeType controlNodeType = control.Reader.NodeType;
            XmlNodeType testNodeType = test.Reader.NodeType;
            if (!controlNodeType.Equals(testNodeType)) {
                CheckNodeTypes(controlNodeType, testNodeType, result,
                               control, test);
            } else if (controlNodeType == XmlNodeType.Element) {
                CompareElements(result, control, test);
            } else if (controlNodeType == XmlNodeType.Text) {
                CompareText(result, control, test);
            }
        }
        
        private void CheckNodeTypes(XmlNodeType controlNodeType,
                                    XmlNodeType testNodeType,
                                    DiffResult result,
                                    ReaderWithState control,
                                    ReaderWithState test) {
            ReaderWithState readerToAdvance = null;
            if (controlNodeType.Equals(XmlNodeType.XmlDeclaration)) {
                readerToAdvance = control;
            } else if (testNodeType.Equals(XmlNodeType.XmlDeclaration)) {        			
                readerToAdvance = test;
            }
        	
            if (readerToAdvance != null) {
            	DifferenceFound(DifferenceType.HAS_XML_DECLARATION_PREFIX_ID, 
            	                controlNodeType, testNodeType, result);
                readerToAdvance.Read();
                CompareNodes(result, control, test);
            } else {
            	DifferenceFound(DifferenceType.NODE_TYPE_ID, controlNodeType, 
             	                testNodeType, result);
            }       
        }
        
        private void CompareElements(DiffResult result, ReaderWithState control,
                                     ReaderWithState test) {
            string controlTagName = control.Reader.Name;
            string testTagName = test.Reader.Name;
            if (!String.Equals(controlTagName, testTagName)) {
                DifferenceFound(DifferenceType.ELEMENT_TAG_NAME_ID, result);
            } else {
                XmlAttribute[] controlAttributes =
                    GetNonSpecialAttributes(control);
                XmlAttribute[] testAttributes = GetNonSpecialAttributes(test);
                if (controlAttributes.Length != testAttributes.Length) {
                    DifferenceFound(DifferenceType.ELEMENT_NUM_ATTRIBUTES_ID, result);
                }
                CompareAttributes(result, controlAttributes, testAttributes);
            }
        }
        
        private void CompareAttributes(DiffResult result,
                                       XmlAttribute[] controlAttributes,
                                       XmlAttribute[] testAttributes) {
            ArrayList unmatchedTestAttributes = new ArrayList();
            unmatchedTestAttributes.AddRange(testAttributes);
            for (int i=0; i < controlAttributes.Length; ++i) {
                
                bool controlIsInNs = IsNamespaced(controlAttributes[i]);
                string controlAttrName =
                    GetUnNamespacedNodeName(controlAttributes[i]);
                XmlAttribute testAttr = null;
                if (!controlIsInNs) {
                    testAttr = FindAttributeByName(testAttributes,
                                                   controlAttrName);
                } else {
                    testAttr = FindAttributeByNameAndNs(testAttributes,
                                                        controlAttrName,
                                                        controlAttributes[i]
                                                        .NamespaceURI);
                }

                if (testAttr != null) {
                    unmatchedTestAttributes.Remove(testAttr);
                    if (!_diffConfiguration.IgnoreAttributeOrder
                        && testAttr != testAttributes[i]) {
                        DifferenceFound(DifferenceType.ATTR_SEQUENCE_ID,
                                        result);
                    }

                    if (controlAttributes[i].Value != testAttr.Value) {
                        DifferenceFound(DifferenceType.ATTR_VALUE_ID, result);
                    }

                } else {
                    DifferenceFound(DifferenceType.ATTR_NAME_NOT_FOUND_ID,
                                    result);
                }
            }
            foreach (XmlAttribute a in unmatchedTestAttributes) {
                DifferenceFound(DifferenceType.ATTR_NAME_NOT_FOUND_ID, result);
            }
        }
        
      private void CompareText(DiffResult result, ReaderWithState control,
                               ReaderWithState test) {
            string controlText = control.Reader.Value;
            string testText = test.Reader.Value;
            if (!String.Equals(controlText, testText)) {
                DifferenceFound(DifferenceType.TEXT_VALUE_ID, result);
            }
        }
        
        private void DifferenceFound(DifferenceType differenceType, DiffResult result) {
            DifferenceFound(new Difference(differenceType), result);
        }
        
        private void DifferenceFound(Difference difference, DiffResult result) {
            result.DifferenceFound(this, difference);
            if (!ContinueComparison(difference)) {
                throw new FlowControlException(difference);
            }
        }
        
        private void DifferenceFound(DifferenceType differenceType, 
                                     XmlNodeType controlNodeType,
                                     XmlNodeType testNodeType, 
                                     DiffResult result) {
            DifferenceFound(new Difference(differenceType, controlNodeType, testNodeType),
                            result);
        }
        
        private bool ContinueComparison(Difference afterDifference) {
            return !afterDifference.MajorDifference;
        }
        
        private void CheckEmptyOrAtEndElement(DiffResult result, 
                                              ReaderWithState control,
                                              ReaderWithState test) {
            if (control.LastElementWasEmpty) {
                if (!test.LastElementWasEmpty) {
                    CheckEndElement(test, result);
                }
            } else {
                if (test.LastElementWasEmpty) {
                    CheckEndElement(control, result);
                }
            }
        }
        
        private XmlAttribute[] GetNonSpecialAttributes(ReaderWithState r) {
            ArrayList l = new ArrayList();
            int length = r.Reader.AttributeCount;
            if (length > 0) {
                XmlDocument doc = new XmlDocument();
                r.Reader.MoveToFirstAttribute();
                for (int i = 0; i < length; i++) {
                    XmlAttribute a = doc.CreateAttribute(r.Reader.Name,
                                                         r.Reader.NamespaceURI);
                    if (!IsXMLNSAttribute(a)) {
                        a.Value = r.Reader.Value;
                        l.Add(a);
                    }
                    r.Reader.MoveToNextAttribute();
                }
            }
            return (XmlAttribute[]) l.ToArray(typeof(XmlAttribute));
        }

        private bool IsXMLNSAttribute(XmlAttribute attribute) {
            return XMLNS_PREFIX == attribute.Prefix ||
                XMLNS_PREFIX == attribute.Name;
        }

        private XmlAttribute FindAttributeByName(XmlAttribute[] attrs,
                                                 string name) {
            foreach (XmlAttribute a in attrs) {
                if (GetUnNamespacedNodeName(a) == name) {
                    return a;
                }
            }
            return null;
        }

        private XmlAttribute FindAttributeByNameAndNs(XmlAttribute[] attrs,
                                                      string name,
                                                      string nsUri) {
            foreach (XmlAttribute a in attrs) {
                if (GetUnNamespacedNodeName(a) == name
                    && a.NamespaceURI == nsUri) {
                    return a;
                }
            }
            return null;
        }

        private string GetUnNamespacedNodeName(XmlNode aNode) {
            return GetUnNamespacedNodeName(aNode, IsNamespaced(aNode));
        }
    
        private string GetUnNamespacedNodeName(XmlNode aNode,
                                               bool isNamespacedNode) {
            if (isNamespacedNode) {
                return aNode.LocalName;
            }
            return aNode.Name;
        }

        private bool IsNamespaced(XmlNode aNode) {
            string ns = aNode.NamespaceURI;
            return ns != null && ns.Length > 0;
        }

        private void CheckEndElement(ReaderWithState reader, DiffResult result) {            
            bool readResult = reader.Read();
            if (!readResult
                || reader.Reader.NodeType != XmlNodeType.EndElement) {
                DifferenceFound(DifferenceType.CHILD_NODELIST_LENGTH_ID, result);
            }
        }
        
        public string OptionalDescription {
            get {
                return _diffConfiguration.Description;
            }
        }

        private class FlowControlException : ApplicationException {
            public FlowControlException(Difference cause) : base(cause.ToString()) {
            }
        }
        
        private class ReaderWithState {
            internal ReaderWithState(XmlReader reader) {
                Reader = reader;
                HasRead = false;
                LastElementWasEmpty = false;
            }

            internal readonly XmlReader Reader;
            internal bool HasRead;
            internal bool LastElementWasEmpty;

            internal bool Read() {
                HasRead = Reader.Read();
                if (HasRead) {
                    switch (Reader.NodeType) {
                    case XmlNodeType.Element:
                        LastElementWasEmpty = Reader.IsEmptyElement;
                        break;
                    case XmlNodeType.EndElement:
                        LastElementWasEmpty = false;
                        break;
                    default:
                        // don't care
                        break;
                    }
                }
                return HasRead;
            }

            internal string State {
                get {
                    return string.Format("Name {0}, NodeType {1}, IsEmpty {2},"
                                         + " HasRead {3}, LastWasEmpty {4}",
                                         Reader.Name,
                                         Reader.NodeType,
                                         Reader.IsEmptyElement,
                                         HasRead, LastElementWasEmpty);
                }
            }
        }
    }
}
