XMLUnit version 1.0
===================

To run this software you will need:
- Junit (http://www.junit.org/)
- a JAXP compliant XML SAX and DOM parser (e.g. Apache Xerces)
- a JAXP/Trax compliant XSLT engine (e.g. Apache Xalan)
in your classpath

If you want to build the source code you will also need Ant (http://jakarta.apache.org/ant)

This build of the source code was prepared using JDK1.4.1, Junit3.8.1, and Ant1.5: it is 
not binary compatible with JDK1.1.x or Junit3.7.x. If you want to use these older libraries 
then you will need to rebuild the source code first.

Enjoy!
http://xmlunit.sourceforge.net/

Changes in this version:
- DifferenceListener interface refactored: single method now provides the NodeDetail 
  of nodes that differ when a comparison is performed
- NEW NodeDetail class added to supply details of compared nodes including XPath location!
- NEW ElementQualifier interface added so that documents containing elements with 
  repeated names can be compared using attribute or text values to determine which of the 
  candidate elements are actually comparable (fixes various feature requests / posted bugs)
- NEW ElementNameQualifier, ElementNameAndTextQualifier and ElementNameAndAttributeQualifier
  classes added to provide the default (backwards compatible) and extended 
  implementions of the ElementQualifier interface
- NEW ComparisonController interface now used to control the operation of a 
  DifferenceEngine instance (extracted from DifferenceListener)
- Incorporated DifferenceConstants patch submitted by ludovicc 
- Added support for namespaced attributes, previously missing
- Build file now incorporates JUnitReport
- Deprecated assertNotXpathsEqual() in favour of assertXpathsNotEqual() in XMLTestCase
- Moved assertion methods from XMLTestCase to new XMLAssert class
- PDF overview document added to distribution and content updated
- Updated Javadocs on website
tim.bacon@thoughtworks.com
November/December 2002, April 2003

Changes in version 0.8:
- Changes to compiled jar in distribution required for compatibility with JUnit 3.8
- Fixes for a defect in the DetailedDiff class that caused a 
  ClassCastException, raised by Ryan MacLachlan
- Small API changes for usability (e.g. allow use of Source constructor arguments
  for Diff and Transform)
tim.bacon@thoughtworks.com
September 2002

Changes in version 0.7:
- DifferenceListener interface extended to allow more user control over how to
  evaluate differences between control and test XML
- NEW IgnoreTextAndAttributeValuesDifferenceListener class added to allow
  difference evaluation solely on the basis of tag and attribute names (ignoring
  differences between text and attribute values completely)
- Fixes for a defect in the DetailedDiff class that caused a 
  NullPointerException, raised by eBernhard and bob
- Additional methods assertXpathExists and assertNotXpathExists added to 
  XMLTestCase class
- Additional methods added to Transform class to complete wrapping of 
  javax.xml.Transformer class, requested by tCantegrel
tim.bacon@thoughtworks.com
August 2002
  
Changes in version 0.6
- NEW DetailedDiff class to extract all the differences between two pieces of XML
- NEW HTMLDocumentBuilder and TolerantSaxDocumentBuilder classes added to enable
  XML assertions to be made against badly formed HTML documents (uses the Swing
  html parser)
- toString() method to return identity and description added to Difference class
- assertXMLEqual and assertXMLNotEqual with (Document, Document) arguments added
  to XMLTestCase
- XMLTestCase now implements the XSLTConstants (and by implication the
  XMLConstants interface)
- Fixes for defects raised by danny zapata (handling namespaces), aakture (handling
  redundant whitespace in XMLUnit.getIgnoreWhitespace), and Craeg strong
  (handling nested XSL include/import in Transform)
Thanks for the feedack guys!
timBacon@primeEight.co.uk
June 2002

Changes in version 0.5:
- NEW assertion methods in XMLTestCase: assertXMLEqual, assertXMLNotEqual,
  assertXpathsEqual, assertXpathsNotEqual,
  assertXpathValuesEqual, assertXpathValuesNotEqual, assertXpathEvaluatesTo,
  assertXMLValid, assertXMLIdentical, assertNodeTestPasses
- NEW DifferenceEngine and Difference classes used by the Diff class to
  facilitate comparison across multiple namespaces and for more node types
  (DocumentType, Comment, CDATASection, and ProcessingInstruction
  as well as Element, Attribute and Text)
- Diff messages now more descriptive
- NEW SimpleXpathEngine class for testing Xpath expressions
- NEW SimpleSerializer class
- TransformerFactory setter and getter, and utility buildDocument() methods
  added to XMLUnit class
- 'global' but non-static methods in XMLTestCase deprecated (XMLUnit static
  methods should be used instead)
- Xalan and Xerces specified as default JAXP implementations in the 'test'
  target of the ant build file (to get around limitations in the Crimson parser)
timBacon@primeEight.co.uk
April 2002

Features added in version 0.4:
- NEW Validator class for validation against DTDs
- NEW NodeTest class, NodeTester interface, AbstractNodeTester and
  CountingNodeTester classes for validating individual Nodes in a DOM tree
  (NB: requires support for DOM Traversal in your DOM implementation)
- cleaner separation of source and test code through directory structure
- better documentation
- replacement of all deprecated JUnit assert() with assertEquals() calls
timBacon@primeEight.co.uk
March 2002

Features added in version 0.3:
- NEW Transform class added to support for JAXP/Trax compliant XSLT engines
- Diff class extended to support Transform class
- more testcases added
- removed external dependency on JDOM
timBacon@primeEight.co.uk
October 2001

Original source:
- Diff class to describe differences between DOM documents
- XMLUnit class to act as a Diff factory
- XMLTestCase extension to JUnit TestCase
jeff@customMonkey.org
April 2001

