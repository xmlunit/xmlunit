package org.xmlunit.assertj;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.error.AssertionErrorFactory;
import org.assertj.core.internal.Failures;

import java.util.List;

import static org.assertj.core.util.Lists.newArrayList;

abstract class CustomAbstractAssert<SELF extends CustomAbstractAssert<SELF, ACTUAL>, ACTUAL> extends AbstractAssert<SELF, ACTUAL> {

    private static final String ORG_ASSERTJ = "org.assert";

    CustomAbstractAssert(ACTUAL actual, Class<?> selfType) {
        super(actual, selfType);
    }

    protected void throwAssertionError(AssertionErrorFactory assertionErrorFactory) {
        AssertionError assertionError = assertionErrorFactory.newAssertionError(info.description(), info.representation());
        Failures.instance().removeAssertJRelatedElementsFromStackTraceIfNeeded(assertionError);
        removeCustomAssertRelatedElementsFromStackTraceIfNeeded(assertionError);
        throw assertionError;
    }

    private void removeCustomAssertRelatedElementsFromStackTraceIfNeeded(AssertionError assertionError) {

        if (!Failures.instance().isRemoveAssertJRelatedElementsFromStackTrace()) return;

        List<StackTraceElement> filtered = newArrayList(assertionError.getStackTrace());
        for (StackTraceElement element: assertionError.getStackTrace()) {
            if (isElementOfCustomAssert(element)) {
                filtered.remove(element);
            }
        }
        StackTraceElement[] newStackTrace = filtered.toArray(new StackTraceElement[0]);
        assertionError.setStackTrace(newStackTrace);
    }

    private boolean isElementOfCustomAssert(StackTraceElement stackTraceElement) {

        Class<?> currentAssertClass = getClass();
        while (currentAssertClass != AbstractAssert.class) {
            if (stackTraceElement.getClassName().equals(currentAssertClass.getName())) {
                return true;
            }
            currentAssertClass = currentAssertClass.getSuperclass();
        }
        return false;
    }
}
