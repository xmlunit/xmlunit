package org.xmlunit.assertj;

import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;

import static org.xmlunit.assertj.ExpectedException.none;
import static org.xmlunit.assertj.XmlAssert.assertThat;

public class XmlAssertTest {

    @Rule
    public ExpectedException thrown = none();

    @Test
    public void testAssertThat_withNull_shouldFailed() {
        thrown.expectAssertionError("\nExpecting actual not to be null");
        assertThat(null).nodes("//foo");
    }

    @Test
    public void testNodes_withNull_shouldFailed() {
        thrown.expectAssertionError("\nExpecting not blank but was:<null>");
        assertThat("<a><b></b><c/></a>").nodes(null);
    }

    @Test
    public void testNodes_withWhitespacesOnly_shouldFailed() {
        thrown.expectAssertionError("\nExpecting not blank but was:<\"\n \">");
        assertThat("<a><b></b><c/></a>").nodes("\n ");
    }

    @Test
    public void testXPath_shouldReturnNotNull() {
        final IterableNodeAssert iterableNodeAssert1 = assertThat("<a><b></b><c/></a>").nodes("/a");
        final IterableNodeAssert iterableNodeAssert2 = assertThat("<a><b></b><c/></a>").nodes("/x");

        Assertions.assertThat(iterableNodeAssert1).isNotNull();
        Assertions.assertThat(iterableNodeAssert2).isNotNull();
    }
}