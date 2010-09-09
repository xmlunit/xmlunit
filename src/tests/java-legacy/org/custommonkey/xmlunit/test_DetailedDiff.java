/*
******************************************************************
Copyright (c) 2001-2008,2010 Jeff Martin, Tim Bacon
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the following
      disclaimer in the documentation and/or other materials provided
      with the distribution.
    * Neither the name of the xmlunit.sourceforge.net nor the names
      of its contributors may be used to endorse or promote products
      derived from this software without specific prior written
      permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

******************************************************************
*/

package org.custommonkey.xmlunit;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;

import org.custommonkey.xmlunit.examples.MultiLevelElementNameAndTextQualifier;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Test a DetailedDiff. Extend the test case class for Diff so we can rerun those
 * tests with a DetailedDiff and assert that behaviour has not changed.
 */
public class test_DetailedDiff extends test_Diff {
    private String firstForecast, secondForecast;

    public void testAllDifferencesFirstForecastControl() throws Exception {
        Diff multipleDifferences = new Diff(firstForecast, secondForecast);
        DetailedDiff detailedDiff = new DetailedDiff(multipleDifferences);

        List differences = detailedDiff.getAllDifferences();
        assertExpectedDifferencesFirstForecastControl(differences, detailedDiff);
    }

    private void assertExpectedDifferencesFirstForecastControl(List differences,
                                                               DetailedDiff detailedDiff) {
        assertEquals("size: " + detailedDiff, 5, differences.size());
        assertEquals("first: " + detailedDiff,
                     DifferenceConstants.HAS_CHILD_NODES, differences.get(0));
        assertEquals("second: " + detailedDiff,
                     DifferenceConstants.ELEMENT_NUM_ATTRIBUTES, differences.get(1));
        assertEquals("third: " + detailedDiff,
                     DifferenceConstants.ATTR_NAME_NOT_FOUND, differences.get(2));
        assertEquals("fourth: " + detailedDiff,
                     DifferenceConstants.ATTR_VALUE, differences.get(3));
        assertEquals("fifth: " + detailedDiff,
                     DifferenceConstants.CHILD_NODE_NOT_FOUND, differences.get(4));
    }

    public void testAllDifferencesSecondForecastControl() throws Exception {
        Diff multipleDifferences = new Diff(secondForecast, firstForecast);
        DetailedDiff detailedDiff = new DetailedDiff(multipleDifferences);

        List differences = detailedDiff.getAllDifferences();

        assertEquals("size: " + detailedDiff, 5, differences.size());
        assertEquals("first: " + detailedDiff,
                     DifferenceConstants.HAS_CHILD_NODES, differences.get(0));
        assertEquals("second: " + detailedDiff,
                     DifferenceConstants.ELEMENT_NUM_ATTRIBUTES, differences.get(1));
        assertEquals("third: " + detailedDiff,
                     DifferenceConstants.ATTR_VALUE, differences.get(2));
        assertEquals("forth: " + detailedDiff,
                     DifferenceConstants.ATTR_NAME_NOT_FOUND,
                     differences.get(3));
        assertEquals("fifth: " + detailedDiff,
                     DifferenceConstants.CHILD_NODE_NOT_FOUND, differences.get(4));
    }

    public void testPrototypeIsADetailedDiff() throws Exception {
        Diff multipleDifferences = new Diff(firstForecast, secondForecast);
        DetailedDiff detailedDiff = new DetailedDiff(
                                                     new DetailedDiff(multipleDifferences));

        List differences = detailedDiff.getAllDifferences();
        assertExpectedDifferencesFirstForecastControl(differences, detailedDiff);
    }

    public void testLargeFiles() throws Exception {
        int i = 0;
        String expr = null;
        File test, control;
        control = new File(test_Constants.BASEDIR + "/src/tests/resources/controlDetail.xml");
        test = new File(test_Constants.BASEDIR + "/src/tests/resources/testDetail.xml");
        try {
            XMLUnit.setCompareUnmatched(true);
        DetailedDiff differencesWithWhitespace = new DetailedDiff(
                                                                  new Diff(new InputSource(new FileReader(control)), 
                                                                           new InputSource(new FileReader(test))) );

        List l = differencesWithWhitespace.getAllDifferences();
        int unmatchedNodes = 0;
        for (Iterator iter = l.iterator(); iter.hasNext();) {
            Difference d = (Difference) iter.next();
            if (d.getId() == DifferenceConstants.CHILD_NODE_NOT_FOUND_ID) {
                unmatchedNodes++;
            }
        }
        
        assertEquals(1402 + unmatchedNodes,
                     differencesWithWhitespace.getAllDifferences().size()); 

        try {
            XMLUnit.setIgnoreWhitespace(true);
            Diff prototype =
                new Diff(new FileReader(control), new FileReader(test));
            DetailedDiff detailedDiff = new DetailedDiff(prototype);
            List differences = detailedDiff.getAllDifferences();
            unmatchedNodes = 0;
            for (Iterator iter = differences.iterator(); iter.hasNext();) {
                Difference d = (Difference) iter.next();
                if (d.getId() == DifferenceConstants.CHILD_NODE_NOT_FOUND_ID) {
                    unmatchedNodes++;
                }
            }
            assertEquals(40 + unmatchedNodes, differences.size()); 

            SimpleXpathEngine xpathEngine = new SimpleXpathEngine();
            Document controlDoc =
                XMLUnit.buildControlDocument(
                                             new InputSource(new FileReader(control)));
            Document testDoc =
                XMLUnit.buildTestDocument(
                                          new InputSource(new FileReader(test)));

            Difference aDifference;
            String value;
            for (Iterator iter = differences.iterator(); iter.hasNext();) {
                aDifference = (Difference) iter.next();
                if (aDifference.equals(DifferenceConstants.ATTR_VALUE)
                    || aDifference.equals(DifferenceConstants.CDATA_VALUE)
                    || aDifference.equals(DifferenceConstants.COMMENT_VALUE)
                    || aDifference.equals(DifferenceConstants.ELEMENT_TAG_NAME)
                    || aDifference.equals(DifferenceConstants.TEXT_VALUE)) {
                    expr = aDifference.getControlNodeDetail().getXpathLocation();
                    if (expr==null || expr.length()==0) {
                        System.out.println(aDifference);
                    } else {
                        value = xpathEngine.evaluate(expr, controlDoc);
                        assertEquals(i + " control " + aDifference.toString(),
                                     value, aDifference.getControlNodeDetail().getValue());
                    }
        
                    expr = aDifference.getTestNodeDetail().getXpathLocation();
                    if (expr == null || expr.length()==0) {
                        System.out.println(aDifference);
                    } else {
                        value = xpathEngine.evaluate(expr, testDoc);
                        assertEquals(i + " test " + aDifference.toString(),
                                     value, aDifference.getTestNodeDetail().getValue());
                    }
                }
                ++i;
            }
        } catch (Exception e) {
            System.out.println("eek@" + i + ":" + expr);
            throw e;
        } finally {
            XMLUnit.setIgnoreWhitespace(false);
        }
        } finally {
            XMLUnit.clearCompareUnmatched();
        }

    }

    public void testSeeAllDifferencesEvenIfDiffWouldSayHaltComparison() throws Exception {
        String control = "<a><b/><c/></a>";
        String test = "<a><c/></a>";

        Diff d = new Diff(control, test);
        DetailedDiff dd = new DetailedDiff(d);

        List l = dd.getAllDifferences();
        // number of children is different, didn't find <b/>, wrong
        // sequence of nodes
        assertEquals(3, l.size());
    }

    public void testSeeAllDifferencesEvenIfDiffSaysHaltComparison() throws Exception {
        String control = "<a><b/><c/></a>";
        String test = "<a><c/></a>";

        Diff d = new Diff(control, test);
        d.similar();
        DetailedDiff dd = new DetailedDiff(d);

        List l = dd.getAllDifferences();
        // number of children is different, didn't find <b/>, wrong
        // sequence of nodes
        assertEquals(3, l.size());
    }
     
    /**
     * @see http://sourceforge.net/forum/forum.php?thread_id=1691528&forum_id=73274
     */
    public void testHelpForumThread1691528() throws Exception {
        String control = "<table border=\"1\">" 
            + "<tr>"
            + "<th>News</th>"
            + "</tr>"
            + "<tr>"
            + "<td>Newsitem 1</td>"
            + "</tr>"
            + "</table>";
        String test = "<table border=\"1\">" 
            + "<tr>"
            + "<th>News</th>"
            + "</tr>"
            + "<tr>"
            + "<td>Newsitem 2</td>"
            + "<td>Newsitem 1</td>"
            + "</tr>"
            + "</table>";
        
        DetailedDiff diff = new DetailedDiff(new Diff(control, test));
        List changes = diff.getAllDifferences();
        // number of children, text of first child, unexpected second
        // test child
        assertEquals(3, changes.size());
    }

    /**
     * Bug 1860681
     * @see https://sourceforge.net/tracker/index.php?func=detail&amp;aid=1860681&amp;group_id=23187&amp;atid=377768
     */
    public void testXpathOfMissingNode() throws Exception {
        String control = 
            "<books>"
            + "  <book>"
            + "    <title>Kabale und Liebe</title>"
            + "  </book>"
            + "  <book>"
            + "    <title>Schuld und Suehne</title>"
            + "  </book>"
            + "</books>";
        String test =
            "<books>"
            + "  <book>"
            + "    <title>Schuld und Suehne</title>"
            + "  </book>"
            + "</books>";
        XMLUnit.setIgnoreWhitespace(true);
        try {
            Diff diff = new Diff(control, test);
            diff.overrideElementQualifier(new MultiLevelElementNameAndTextQualifier(2));
            DetailedDiff dd = new DetailedDiff(diff); 
            List l = dd.getAllDifferences();
            assertEquals(3, l.size());
            // (0) number of children, (1) node not found, (2) order different
            Difference d = (Difference) l.get(1);
            assertEquals(DifferenceConstants.CHILD_NODE_NOT_FOUND_ID,
                         d.getId());
            assertEquals("/books[1]/book[1]",
                         d.getControlNodeDetail().getXpathLocation());
            assertNull("should be null but is "
                       + d.getTestNodeDetail().getXpathLocation(),
                       d.getTestNodeDetail().getXpathLocation());

            // and reverse
            diff = new Diff(test, control);
            diff.overrideElementQualifier(new MultiLevelElementNameAndTextQualifier(2));
            dd = new DetailedDiff(diff); 
            l = dd.getAllDifferences();
            assertEquals(3, l.size());
            // (0) number of children, (1) order different, (2) node not found
            d = (Difference) l.get(2);
            assertEquals(DifferenceConstants.CHILD_NODE_NOT_FOUND_ID,
                         d.getId());
            assertEquals("/books[1]/book[1]",
                         d.getTestNodeDetail().getXpathLocation());
            assertNull(d.getControlNodeDetail().getXpathLocation());
        } finally {
            XMLUnit.setIgnoreWhitespace(false);
        }
    }

    protected Diff buildDiff(Document control, Document test) {
        return new DetailedDiff(super.buildDiff(control, test));
    }

    protected Diff buildDiff(String control, String test) throws Exception {
        return new DetailedDiff(super.buildDiff(control, test));
    }

    protected Diff buildDiff(Reader control, Reader test) throws Exception {
        return new DetailedDiff(super.buildDiff(control, test));
    }

    protected Diff buildDiff(String control, String test,
                             DifferenceEngineContract engine) throws Exception {
        return new DetailedDiff(super.buildDiff(control, test, engine));
    }

    public test_DetailedDiff(String name) {
        super(name);
        firstForecast = "<weather><today icon=\"clouds\" temp=\"17\">"
            + "<outlook>unsettled</outlook></today></weather>";
        secondForecast = "<weather><today temp=\"20\"/></weather>";
    }

    /**
     * https://sourceforge.net/tracker/?func=detail&aid=2758280&group_id=23187&atid=377768
     */
    public void testCompareUnmatched() throws Exception {
        try {
            XMLUnit.setCompareUnmatched(true);
            String control = "<root><a>1</a>"
                + "<b>1</b>"
                + "<c>1</c>"
                + "<d>1</d>"
                + "<e>1</e></root>";
            String test = "<root><a>1</a>"
                + "<b>1</b>"
                + "<z>1</z>"
                + "<d>1</d>"
                + "<e>1</e></root>";
            DetailedDiff d = (DetailedDiff) buildDiff(control, test);
            List l = d.getAllDifferences();
            assertEquals(1, l.size());
            Difference diff = (Difference) l.get(0);
            assertEquals(DifferenceConstants.ELEMENT_TAG_NAME_ID, diff.getId());
        } finally {
            XMLUnit.clearCompareUnmatched();
        }
    }

    /**
     * https://sourceforge.net/tracker/?func=detail&aid=2758280&group_id=23187&atid=377768
     */
    public void testDontCompareUnmatched() throws Exception {
        String control = "<root><a>1</a>"
            + "<b>1</b>"
            + "<c>1</c>"
            + "<d>1</d>"
            + "<e>1</e></root>";
        String test = "<root><a>1</a>"
            + "<b>1</b>"
            + "<z>1</z>"
            + "<d>1</d>"
            + "<e>1</e></root>";
        try {
            XMLUnit.setCompareUnmatched(false);
            DetailedDiff d = (DetailedDiff) buildDiff(control, test);
            List l = d.getAllDifferences();
            assertEquals(2, l.size());
            Difference diff = (Difference) l.get(0);
            assertEquals(DifferenceConstants.CHILD_NODE_NOT_FOUND_ID,
                         diff.getId());
            assertNotNull(diff.getControlNodeDetail().getNode());
            assertNull(diff.getTestNodeDetail().getNode());
            diff = (Difference) l.get(1);
            assertEquals(DifferenceConstants.CHILD_NODE_NOT_FOUND_ID,
                         diff.getId());
            assertNull(diff.getControlNodeDetail().getNode());
            assertNotNull(diff.getTestNodeDetail().getNode());
        } finally {
            XMLUnit.clearCompareUnmatched();
        }
    }

    /**
     * @see https://sourceforge.net/tracker/index.php?func=detail&amp;aid=3062518&amp;group_id=23187&amp;atid=377768
     */
    public void testIssue3062518() throws Exception {
        String control = "<Fruits>"
            + "<Apple size=\"11\" color=\"green\"/>"
            + "<Apple size=\"15\" color=\"green\"/>"
            + "<Banana size=\"10\"/>"
            + "</Fruits>";
        String test = "<Fruits>"
            + "<Apple size=\"11\" color=\"green\"/>"
            + "<Banana size=\"11\"/>"
            + "</Fruits>";
        try {
            XMLUnit.setCompareUnmatched(false);
            DetailedDiff d = (DetailedDiff) buildDiff(control, test);
            List l = d.getAllDifferences();
            assertEquals(4, l.size());
            // expected 3 children is 2
            Difference diff = (Difference) l.get(0);
            assertEquals(DifferenceConstants.CHILD_NODELIST_LENGTH_ID,
                         diff.getId());
            assertEquals("3", diff.getControlNodeDetail().getValue());
            assertEquals("2", diff.getTestNodeDetail().getValue());
            assertEquals("/Fruits[1]",
                         diff.getControlNodeDetail().getXpathLocation());
            assertEquals("/Fruits[1]",
                         diff.getTestNodeDetail().getXpathLocation());

            // didn't find the second Apple element
            diff = (Difference) l.get(1);
            assertEquals(DifferenceConstants.CHILD_NODE_NOT_FOUND_ID,
                         diff.getId());
            assertEquals("Apple", diff.getControlNodeDetail().getValue());
            assertEquals("null", diff.getTestNodeDetail().getValue());
            assertEquals("/Fruits[1]/Apple[2]",
                         diff.getControlNodeDetail().getXpathLocation());
            assertEquals(null,
                         diff.getTestNodeDetail().getXpathLocation());

            // Banana's size attribute doesn't match
            diff = (Difference) l.get(3);
            assertEquals(DifferenceConstants.ATTR_VALUE_ID,
                         diff.getId());
            assertEquals("10", diff.getControlNodeDetail().getValue());
            assertEquals("11", diff.getTestNodeDetail().getValue());
            assertEquals("/Fruits[1]/Banana[1]/@size",
                         diff.getControlNodeDetail().getXpathLocation());
            assertEquals("/Fruits[1]/Banana[1]/@size",
                         diff.getTestNodeDetail().getXpathLocation());

            // Banana is the third child in control but the second one in test
            diff = (Difference) l.get(2);
            assertEquals("2", diff.getControlNodeDetail().getValue());
            assertEquals("1", diff.getTestNodeDetail().getValue());
            assertEquals("/Fruits[1]/Banana[1]",
                         diff.getControlNodeDetail().getXpathLocation());
            assertEquals("/Fruits[1]/Banana[1]",
                         diff.getTestNodeDetail().getXpathLocation());
        } finally {
            XMLUnit.clearCompareUnmatched();
        }
    }
}
