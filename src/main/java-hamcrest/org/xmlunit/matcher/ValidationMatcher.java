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
package org.xmlunit.matcher;

import org.xmlunit.validation.JAXPValidator;
import org.xmlunit.validation.Languages;
import org.xmlunit.validation.ValidationProblem;
import org.xmlunit.validation.ValidationResult;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeMatcher;

import javax.xml.transform.Source;
import java.util.Arrays;

/**
 * Hamcrest Matcher for XML Validation.
 */
public class ValidationMatcher extends TypeSafeMatcher<Source> {

    private final Source schemaSource[];
    private Source instance;
    private ValidationResult result;

    public ValidationMatcher(Source... schemaSource) {
        this.schemaSource = schemaSource;
    }

    @Override
    public boolean matchesSafely(Source instance) {
        this.instance = instance;
        JAXPValidator v = new JAXPValidator(Languages.W3C_XML_SCHEMA_NS_URI);
        if (schemaSource.length <= 1) {
            v.setSchemaSource(schemaSource[0]);
        } else {
            v.setSchemaSources(schemaSource);
        }
        this.result = v.validateInstance(instance);
        return this.result.isValid();
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(" that ")
                .appendValue(instance.getSystemId())
                .appendText(" validates against ");
        for (Source schema : Arrays.asList(schemaSource)) {
            description.appendValue(schema.getSystemId());
        }
    }

    @Override
    protected void describeMismatchSafely(final Source instance, final Description mismatchDescription) {
        if (this.result != null && this.result.getProblems() != null) {
            mismatchDescription.appendText(" got validation errors: ");
            for (ValidationProblem problem : this.result.getProblems()) {
                mismatchDescription.appendText(problem.toString());
            }
        } else {
            mismatchDescription.appendText(" got unexpected error!");
        }
    }

    @Factory
    public static ValidationMatcher valid(final Source schemaSource) {
        return new ValidationMatcher(schemaSource);
    }


}
