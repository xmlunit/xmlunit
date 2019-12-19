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
package org.xmlunit.assertj;

import org.hamcrest.TypeSafeMatcher;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.regex.Pattern.DOTALL;
import static org.junit.internal.matchers.ThrowableMessageMatcher.hasMessage;


public class ExpectedException implements TestRule {
    private final org.junit.rules.ExpectedException delegate = org.junit.rules.ExpectedException.none();

    private boolean checkAssertionType = false;

    public static ExpectedException none() {
        return new ExpectedException();
    }

    private ExpectedException() {
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return delegate.apply(base, description);
    }

    public void expectAssertionError(String message) {
        expectAssertionError();
        delegate.expectMessage(format(message));
    }

    public void expectAssertionErrorPattern(String messageRegex) {
        expectAssertionError();
        delegate.expect(hasMessage(new MatchesPattern(messageRegex)));
    }

    private void expectAssertionError() {

        if (!checkAssertionType) {
            delegate.expect(AssertionError.class);
            checkAssertionType = true;
        }
    }

    private class MatchesPattern extends TypeSafeMatcher<String> {
        private String regex;

        MatchesPattern(String regex) {
            this.regex = regex;
        }

        @Override
        protected boolean matchesSafely(String item) {
            return Pattern.compile(regex, DOTALL).matcher(item).matches();
        }

        @Override
        public void describeTo(org.hamcrest.Description description) {
            description.appendText("a string matching the regex '" + regex + "'");
        }
    }
}
