package org.xmlunit.matchers;

import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.EvaluateXPathMatcher.hasXPath;
import static org.xmlunit.matchers.TypeMatcher.asBigDecimal;
import static org.xmlunit.matchers.TypeMatcher.asDouble;
import static org.xmlunit.matchers.TypeMatcher.asInt;

public class TypeMatcherTest {

    @Test
    public void testXPathCountAsType() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<fruits>" +
                "<fruit name=\"apple\"/>" +
                "<fruit name=\"orange\"/>" +
                "<fruit name=\"banana\"/>" +
                "</fruits>";

        assertThat(xml, hasXPath("count(//fruits/fruit)", asDouble(equalTo(3.0))));
        assertThat(xml, hasXPath("count(//fruits/fruit)", asBigDecimal(greaterThan(BigDecimal.ONE))));
        assertThat(xml, hasXPath("count(//fruits/fruit)", asInt(lessThan(4))));
    }
}
