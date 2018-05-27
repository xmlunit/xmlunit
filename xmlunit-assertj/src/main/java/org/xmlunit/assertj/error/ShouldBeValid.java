package org.xmlunit.assertj.error;

import org.assertj.core.error.BasicErrorMessageFactory;
import org.xmlunit.validation.ValidationProblem;

public class ShouldBeValid extends BasicErrorMessageFactory {

    public static ShouldBeValid shouldBeValid(String systemId, Iterable<ValidationProblem> problems) {
        StringBuilder builder = new StringBuilder();
        int index = 1;

        for (ValidationProblem problem : problems) {
            builder.append(index++).append(".");

            if (problem.getLine() != ValidationProblem.UNKNOWN) {
                builder.append(" line=").append(problem.getLine());
            }
            if (problem.getColumn() != ValidationProblem.UNKNOWN) {
                builder.append(" column=").append(problem.getColumn());
            }

            builder.append(" type=").append(problem.getType());
            builder.append(" message=").append(problem.getMessage());
            builder.append("%n");
        }

        return new ShouldBeValid(systemId != null ? systemId : "instance", builder.toString());
    }


    private ShouldBeValid(String systemId, String problems) {
        super("%nExpecting:%n <%s>%nto be valid but found following problems:%n%s",
                unquotedString(systemId),
                unquotedString(problems));
    }
}
