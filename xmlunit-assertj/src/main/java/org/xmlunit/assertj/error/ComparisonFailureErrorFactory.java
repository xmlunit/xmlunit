package org.xmlunit.assertj.error;

import org.assertj.core.description.Description;
import org.assertj.core.error.AssertionErrorFactory;
import org.assertj.core.internal.Failures;
import org.assertj.core.presentation.Representation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

abstract class ComparisonFailureErrorFactory implements AssertionErrorFactory {

    private static Constructor<?> comparisonFailureConstructor;

    private static final String EXPECTED_BUT_WAS_MESSAGE = "%nExpecting:%n <%s>%nto be equal to:%n <%s>%nbut was not.";

    abstract String getMessage();
    abstract String getExpected();
    abstract String getActual();

    @Override
    public AssertionError newAssertionError(Description d, Representation representation) {
        AssertionError assertionError = getComparisonFailureInstance();
        if(assertionError != null) {
            return assertionError;
        }

        String message = String.format(EXPECTED_BUT_WAS_MESSAGE, getActual(), getExpected());
        return Failures.instance().failure(message);
    }

    private AssertionError getComparisonFailureInstance() {
        Constructor<?> constructor = getComparisonFailureConstructor();
        if(constructor != null) {
            try {
                Object o = constructor.newInstance(getMessage(), getExpected(), getActual());
                if (o instanceof AssertionError) return (AssertionError) o;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
            }
        }
        return null;
    }
    private Constructor<?> getComparisonFailureConstructor() {
        if(comparisonFailureConstructor == null) {
            try {
                Class<?> targetType = Class.forName("org.junit.ComparisonFailure");
                comparisonFailureConstructor = targetType.getConstructor(String.class, String.class, String.class);
            } catch (ClassNotFoundException | NoSuchMethodException ignored) {
            }
        }

        return comparisonFailureConstructor;
    }
}
