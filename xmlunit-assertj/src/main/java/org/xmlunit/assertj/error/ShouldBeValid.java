package org.xmlunit.assertj.error;

import org.assertj.core.error.BasicErrorMessageFactory;
import org.xmlunit.validation.ValidationProblem;

import static java.lang.String.format;

public class ShouldBeValid extends BasicErrorMessageFactory {

    public static ShouldBeValid shouldBeValid(String systemId, Iterable<ValidationProblem> problems) {
        String systemId1 = systemId != null ? systemId : "instance";

        StringBuilder builder = new StringBuilder();
        int index = 1;

        for (ValidationProblem problem : problems) {
            builder.append(index++).append(".");

            if (problem.getLine() != ValidationProblem.UNKNOWN) {
                builder.append(" line=").append(problem.getLine()).append(';');
            }
            if (problem.getColumn() != ValidationProblem.UNKNOWN) {
                builder.append(" column=").append(problem.getColumn()).append(';');
            }

            builder.append(" type=").append(problem.getType()).append(';');
            builder.append(" message=").append(problem.getMessage());
            builder.append("%n");
        }

        String problemsStr = format(builder.toString());

        return new ShouldBeValid(systemId1, problemsStr);
    }


    private ShouldBeValid(String systemId, String problems) {
        super("%nExpecting:%n <%s>%nto be valid but found following problems:%n%s",
                unquotedString(systemId),
                unquotedString(problems));
    }
}
