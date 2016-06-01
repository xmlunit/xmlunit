/*
  This file is licensed to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package org.xmlunit.matchers;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertThat;
import static org.xmlunit.TestResources.TEST_RESOURCE_DIR;
import static org.xmlunit.matchers.CompareMatcher.isIdenticalTo;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

import org.xmlunit.XMLUnitException;
import org.xmlunit.builder.Input;
import org.xmlunit.builder.Input.Builder;
import org.xmlunit.diff.Comparison;
import org.xmlunit.diff.Comparison.Detail;
import org.xmlunit.diff.ComparisonFormatter;
import org.xmlunit.diff.ComparisonListener;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.ComparisonType;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.DifferenceEvaluator;
import org.xmlunit.diff.ElementSelectors;
import org.xmlunit.util.Predicate;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.ComparisonFailure;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CompareMatcherTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /** set this to true for manual review of the Error Messages, how the really looks in your IDE. */
    private final boolean letExceptionTestFail = false;

    @Test
    public void testIsIdenticalTo_withAssertionErrorForAttributes_throwsReadableMessage() {
        // Expected Exception
        expect(AssertionError.class);
        expectMessage("Expected attribute value 'xy' but was 'xyz'");
        expectMessage("at /Element[1]/@attr2");
        expectMessage("attr2=\"xyz\"");

        // run test:
        assertThat("<Element attr2=\"xyz\" attr1=\"12\"/>", isIdenticalTo("<Element attr1=\"12\" attr2=\"xy\"/>"));
    }

    @Test
    public void testIsIdenticalTo_withAssertionErrorForElementOrder_throwsReadableMessage() {
        // Expected Exception
        expect(AssertionError.class);
        expectMessage("Expected child nodelist sequence '0' but was '1'");
        expectMessage("comparing <b...> at /a[1]/b[1] to <b...> at /a[1]/b[1]");

        // run test:
        assertThat("<a><c/><b/></a>", isIdenticalTo("<a><b/><c/></a>")
            .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testIsSimilarTo_withAssertionErrorForElementOrder_throwsReadableMessage() {
        // run test:
        assertThat("<a><c/><b/></a>", isSimilarTo("<a><b/><c/></a>")
            .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testIsIdenticalTo_withAssertionErrorForWhitespaces_throwsReadableMessage() {
        // Expected Exception
        expect(AssertionError.class);
        expectMessage("Expected child nodelist length '1' but was '3'");
        expectMessage("<a>" + getLineSeparator()  + "  <b/>" + getLineSeparator()  + "</a>");
        expectMessage("<a><b/></a>");

        // run test:
        assertThat("<a>\n  <b/>\n</a>", isIdenticalTo("<a><b/></a>"));
    }

    @Test
    public void testIsIdenticalTo_withComparisonFailureForWhitespaces_throwsReadableMessage() {
        // Expected Exception
        expect(ComparisonFailure.class);
        expectMessage("Expected child nodelist length '1' but was '3'");
        expectMessage("expected:<<a>[<b/>]</a>> but was:<<a>[" + getLineSeparator()  + "  <b/>" + getLineSeparator()  + "]</a>>");

        // run test:
        assertThat("<a>\n  <b/>\n</a>", isIdenticalTo("<a><b/></a>").throwComparisonFailure());
    }

    @Test
    public void testIsIdenticalTo_withIgnoreWhitespaces_shouldSucceed() {

        // run test:
        assertThat("<a>\n  <b/>\n</a>", isIdenticalTo("<a><b/></a>").ignoreWhitespace());
    }

    @Test
    public void testIsIdenticalTo_withIgnoreComments_shouldSucceed() {

        // run test:
        assertThat("<a><!-- test --></a>", isIdenticalTo("<a></a>").ignoreComments());
    }

    @Test
    public void testIsIdenticalTo_withNormalizeWhitespace_shouldSucceed() {

        // run test:
        assertThat("<a>\n  <b>\n  Test\n  Node\n  </b>\n</a>", isIdenticalTo("<a><b>Test Node</b></a>")
            .normalizeWhitespace());
    }

    @Test
    public void testIsIdenticalTo_withNormalizeWhitespace_shouldFail() {

        expect(AssertionError.class);
        expectMessage("Expected text value 'TestNode' but was 'Test Node'");

        // run test:
        assertThat("<a>\n  <b>\n  Test\n  Node\n  </b>\n</a>", isIdenticalTo("<a><b>TestNode</b></a>")
            .normalizeWhitespace());
    }

    @Test
    public void testIsSimilarTo_withFileInput() {
        expect(AssertionError.class);
        expectMessage("In Source");
        expectMessage("test2.xml");

        // run test:
        assertThat(new File(TEST_RESOURCE_DIR, "test1.xml"),
                   isSimilarTo(new File(TEST_RESOURCE_DIR, "test2.xml")));
    }

    @Test
    public void testIsSimilarTo_withDifferenceEvaluator_shouldSucceed() {
        // prepare testData
        final String control = "<a><b attr=\"abc\"></b></a>";
        final String test = "<a><b attr=\"xyz\"></b></a>";

        // run test
        assertThat(test, isSimilarTo(control).withDifferenceEvaluator(new IgnoreAttributeDifferenceEvaluator("attr")));

    }

    @Test
    public void testIsSimilarTo_withComparisonFormatter_shouldFailWithCustomMessage() {
        // prepare testData
        expect(AssertionError.class);
        expectMessage("DESCRIPTION");
        expectMessage("DETAIL-abc");
        expectMessage("DETAIL-xyz");

        final String control = "<a><b attr=\"abc\"></b></a>";
        final String test = "<a><b attr=\"xyz\"></b></a>";

        // run test
        assertThat(test, isSimilarTo(control).withComparisonFormatter(new DummyComparisonFormatter()));

    }

    @Test
    public void testIsSimilarTo_withComparisonFormatterAndThrowComparisonFailure_shouldFailWithCustomMessage() {
        // prepare testData
        expect(ComparisonFailure.class);
        expectMessage("DESCRIPTION");
        expectMessage("DETAIL-[abc]");
        expectMessage("DETAIL-[xyz]");

        final String control = "<a><b attr=\"abc\"></b></a>";
        final String test = "<a><b attr=\"xyz\"></b></a>";

        // run test
        assertThat(test, isSimilarTo(control).withComparisonFormatter(new DummyComparisonFormatter()).throwComparisonFailure());

    }

    @Test
    public void testIsSimilarTo_withComparisonListener_shouldCollectChanges() {
        CounterComparisonListener comparisonListener = new CounterComparisonListener();
        String controlXml = "<a><b>Test Value</b><c>ABC</c></a>";
        String testXml = "<a><b><![CDATA[Test Value]]></b><c>XYZ</c></a>";

        // run test:
        try {
            assertThat(testXml, isSimilarTo(controlXml).withComparisonListeners(comparisonListener));
        } catch (AssertionError e) {
            assertThat(e.getMessage(), containsString("Expected text value 'ABC' but was 'XYZ'"));
        }
        
        // validate result
        assertThat(comparisonListener.differents, is(1));
        assertThat(comparisonListener.similars, is(1));
        assertThat(comparisonListener.equals, is(Matchers.greaterThan(10)));
    }

    @Test
    public void testIsSimilarTo_withDifferenceListener_shouldCollectChanges() {
        CounterComparisonListener comparisonListener = new CounterComparisonListener();
        String controlXml = "<a><b>Test Value</b><c>ABC</c></a>";
        String testXml = "<a><b><![CDATA[Test Value]]></b><c>XYZ</c></a>";

        // run test:
        try {
            assertThat(testXml, isSimilarTo(controlXml).withDifferenceListeners(comparisonListener));
            Assert.fail("Should throw AssertionError");
        } catch (AssertionError e) {
            assertThat(e.getMessage(), containsString("Expected text value 'ABC' but was 'XYZ'"));
        }
        
        // validate result
        assertThat(comparisonListener.differents, is(1));
        assertThat(comparisonListener.similars, is(1));
        assertThat(comparisonListener.equals, is(0));
    }

    @Test
    public void testCompareMatcherWrapper_shouldWriteFailedTestInput() {
        
        final String control = "<a><b attr=\"abc\"></b></a>";
        final String test = "<a><b attr=\"xyz\"></b></a>";

        // run test
        final String fileName = "testCompareMatcherWrapper.xml";
        try {
            assertThat(test, TestCompareMatcherWrapper.isSimilarTo(control).withTestFileName(fileName));
            Assert.fail("Should throw AssertionError");
        } catch (AssertionError e) {
            assertThat(e.getMessage(), containsString("Expected attribute value 'abc' but was 'xyz'"));
        }
        
        // validate that the written File contains the right data:
        assertThat(new File(getTestResultFolder(), fileName), isSimilarTo(test));
    }

    @Test
    public void testDiff_withAttributeDifferences() {
        final String control = "<a><b attr1=\"abc\" attr2=\"def\"></b></a>";
        final String test = "<a><b attr1=\"xyz\" attr2=\"def\"></b></a>";

        try {
            assertThat(test, isSimilarTo(control));
            Assert.fail("Should throw AssertionError");
        } catch (AssertionError e) {
            assertThat(e.getMessage(), containsString("Expected attribute value 'abc' but was 'xyz'"));
        }

        assertThat(test, isSimilarTo(control)
                   .withAttributeFilter(new Predicate<Attr>() {
                           @Override
                           public boolean test(Attr a) {
                               return !"attr1".equals(a.getName());
                           }
                       }));
    }

    @Test
    public void testDiff_withExtraNodes() {
        String control = "<a><b></b><c/></a>";
        String test = "<a><b></b><c/><d/></a>";

        try {
            assertThat(test, isSimilarTo(control));
        } catch (AssertionError e) {
            assertThat(e.getMessage(), containsString("Expected child nodelist length '2' but was '3'"));
        }

        assertThat(test,
                   isSimilarTo(control)
                   .withNodeFilter(new Predicate<Node>() {
                           @Override
                           public boolean test(Node n) {
                               return !"d".equals(n.getNodeName());
                           }
                       }));
    }

    /**
     * Really only tests there is no NPE.
     * @see "https://github.com/xmlunit/xmlunit/issues/81"
     */
    @Test(expected = AssertionError.class)
    public void canBeCombinedWithFailingMatcher() {
        assertThat("not empty", both(isEmptyString()).and(isIdenticalTo("")));
    }

    @Test
    public void canBeCombinedWithPassingMatcher() {
        assertThat("<a><c/><b/></a>", both(not(isEmptyString()))
                   .and(isSimilarTo("<a><b/><c/></a>")
                        .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText))));
    }

    @Test
    public void usesDocumentBuilderFactory() throws Exception {
        DocumentBuilderFactory dFac = Mockito.mock(DocumentBuilderFactory.class);
        DocumentBuilder b = Mockito.mock(DocumentBuilder.class);
        Mockito.when(dFac.newDocumentBuilder()).thenReturn(b);
        Mockito.doThrow(new IOException())
            .when(b).parse(Mockito.any(InputSource.class));

        String control = "<a><b></b><c/></a>";
        String test = "<a><b></b><c/><d/></a>";

        try {
            assertThat("<a><b></b><c/></a>",
                       not(isSimilarTo("<a><b></b><c/><d/></a>")
                           .withDocumentBuilderFactory(dFac)));
            Assert.fail("Expected exception");
        } catch (XMLUnitException ex) {
            Mockito.verify(b).parse(Mockito.any(InputSource.class));
        }
    }

    public void expect(Class<? extends Throwable> type) {
        if (letExceptionTestFail) return;
        thrown.expect(type);
    }

    public void expectMessage(String substring) {
        if (letExceptionTestFail) return;
        thrown.expectMessage(substring);
    }

    private String getLineSeparator() {
        return System.getProperty("line.separator");
    }

    private final class DummyComparisonFormatter implements ComparisonFormatter {

        @Override
        public String getDetails(Detail details, ComparisonType type, boolean formatXml) {
            return "DETAIL-" + details.getValue();
        }

        @Override
        public String getDescription(Comparison difference) {
            return "DESCRIPTION";
        }
    }

    private final class IgnoreAttributeDifferenceEvaluator implements DifferenceEvaluator {

        private final String attributeName;

        public IgnoreAttributeDifferenceEvaluator(String attributeName) {
            this.attributeName = attributeName;
        }

        @Override
        public ComparisonResult evaluate(Comparison comparison, ComparisonResult outcome) {
            final Node controlNode = comparison.getControlDetails().getTarget();
            if (controlNode instanceof Attr) {
                Attr attr = (Attr) controlNode;
                if (attr.getName().equals(attributeName)) {
                    return ComparisonResult.EQUAL;
                }
            }
            return outcome;
        }
    }

    private final class CounterComparisonListener implements ComparisonListener {

        private int equals;
        private int similars;
        private int differents;

        @Override
        public void comparisonPerformed(Comparison comparison, ComparisonResult outcome) {
            switch (outcome) {
            case EQUAL:
                equals++;
                break;
            case SIMILAR:
                similars++;
                break;
            case DIFFERENT:
                differents++;
                break;

            default:
                break;
            }
        }

    }

    /**
     * Example Wrapper for {@link CompareMatcher}.
     * <p>
     * This example will write the Test-Input into the Files System.<br>
     * This could be useful for manual reviews or as template for a control-File.
     * 
     */
    private static class TestCompareMatcherWrapper extends BaseMatcher<Object> {
        private final CompareMatcher compareMatcher;
        private String fileName;
        protected TestCompareMatcherWrapper(CompareMatcher compareMatcher) {
            this.compareMatcher = compareMatcher;
        }
        public TestCompareMatcherWrapper withTestFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public static TestCompareMatcherWrapper isSimilarTo(final Object control) {
            return new TestCompareMatcherWrapper(CompareMatcher.isSimilarTo(control));
        }

        @Override
        public boolean matches(Object testItem) {
            if (fileName == null) {
                return compareMatcher.matches(testItem);
            }
            // do something with your Test-Source
            final Builder builder = Input.from(testItem);
            // e.g.: write the testItem into the FilesSystem. So it can be used as template for a new control-File.
            final File testFile = writeIntoTestResultFolder(builder.build());
            return compareMatcher.matches(testFile);
        }

        private File writeIntoTestResultFolder(final Source source) throws TransformerFactoryConfigurationError {
            FileOutputStream fop = null;
            File file = new File(getTestResultFolder(), this.fileName);
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                fop = new FileOutputStream(file);
                marshal(source, fop);
                fop.flush();
                fop.close();
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                if (fop != null) {
                    try {
                        fop.close();
                    } catch (IOException e) {
                        // ignore exception during close
                    }
                }
            }
            return file;
        }

        private void marshal(final Source source, FileOutputStream fop)
                throws TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {
            StreamResult r = new StreamResult(fop);
            TransformerFactory fac = TransformerFactory.newInstance();
            Transformer t = fac.newTransformer();
            t.transform(source, r);
        }

        @Override
        public void describeTo(Description description) {
            compareMatcher.describeTo(description);
        }

        @Override
        public void describeMismatch(Object item, Description description) {
            compareMatcher.describeMismatch(item, description);
        }
        
    }

    private static File getTestResultFolder() {
        final File folder = new File(//
            String.format("./target/testResults/%s", CompareMatcherTest.class.getSimpleName()));
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
    }
}
