package org.xmlunit.matchers;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.IsXmlEqualMatcher.xmlEqualTo;

public class IsXmlEqualMatcherTest {

    @Test
    public void shouldMatchEqualXml() throws Exception {
        assertThat("<element>text</element>", xmlEqualTo("<element>text</element>"));
    }

    @Test
    public void shouldNotMatchDistinctXml() throws Exception {
        assertThat("<element>text</element>", not(xmlEqualTo("<element/>")));
    }
}
