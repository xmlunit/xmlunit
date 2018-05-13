package org.xmlunit.assertj;

import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;

import static java.lang.String.format;
import static org.xmlunit.assertj.ExpectedException.none;
import static org.xmlunit.assertj.XmlAssert.assertThat;

public class XmlAssertTest {

    @Rule
    public ExpectedException thrown = none();

    @Test
    public void testXPath_shouldReturnNotNull() {
        final MultipleNodeAssert multipleNodeAssert1 = assertThat("<a><b></b><c/></a>").nodesByXPath("/a");
        final MultipleNodeAssert multipleNodeAssert2 = assertThat("<a><b></b><c/></a>").nodesByXPath("/x");

        Assertions.assertThat(multipleNodeAssert1).isNotNull();
        Assertions.assertThat(multipleNodeAssert2).isNotNull();
    }

    @Test
    public void testAssertThat_withNull_shouldFailed() {
        thrown.expectAssertionError(format("%nExpecting actual not to be null"));
        assertThat(null).nodesByXPath("//foo");
    }

    @Test
    public void testNodesByXPath_withNull_shouldFailed() {
        thrown.expectAssertionError(format("%nExpecting not blank but was:<null>"));
        assertThat("<a><b></b><c/></a>").nodesByXPath(null);
    }

    @Test
    public void testNodesByXPath_withWhitespacesOnly_shouldFailed() {
        thrown.expectAssertionError(format("%nExpecting not blank but was:<\" \n \t\">"));
        assertThat("<a><b></b><c/></a>").nodesByXPath(" \n \t");
    }
}