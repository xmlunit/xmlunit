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
package org.xmlunit.matchers;

import static org.xmlunit.util.Linqy.any;
import static org.xmlunit.util.Linqy.asList;
import static org.xmlunit.util.Linqy.map;

import org.xmlunit.builder.Input;
import org.xmlunit.util.IsNullPredicate;
import org.xmlunit.util.Linqy.Mapper;
import org.xmlunit.validation.JAXPValidator;
import org.xmlunit.validation.Languages;
import org.xmlunit.validation.ValidationProblem;
import org.xmlunit.validation.ValidationResult;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;

import javax.xml.transform.Source;
import java.util.Arrays;

/**
 * Hamcrest Matcher for XML Validation.
 */
public class ValidationMatcher extends BaseMatcher {

    private final Source schemaSource[];
    private Source instance;
    private ValidationResult result;

    public ValidationMatcher(Object... schemaSource) {
        if (schemaSource == null) {
            throw new IllegalArgumentException("schemaSource must not be null");
        }
        Iterable<Object> schemaSourceList = Arrays.asList(schemaSource);
        if (any(schemaSourceList, new IsNullPredicate())) {
            throw new IllegalArgumentException("schemaSource must not contain null values");
        }
        this.schemaSource = asList(map(schemaSourceList,
                                       new Mapper<Object, Source>() {
                                           @Override
                                           public Source apply(Object source) {
                                               return Input.from(source).build();
                                           }
                                       })
                                   ).toArray(new Source[schemaSource.length]);
    }

    @Override
    public boolean matches(Object instance) {
        this.instance = Input.from(instance).build();
        JAXPValidator v = new JAXPValidator(Languages.W3C_XML_SCHEMA_NS_URI);
        if (schemaSource.length > 0) {
            v.setSchemaSources(schemaSource);
        }
        this.result = v.validateInstance(this.instance);
        return this.result.isValid();
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(" that ")
            .appendValue(instance.getSystemId());
        if (schemaSource.length > 0) {
            description.appendText(" validates against ");
        } else {
            description.appendText(" validates");
        }
        for (Source schema : Arrays.asList(schemaSource)) {
            description.appendValue(schema.getSystemId());
        }
    }

    @Override
    public void describeMismatch(final Object item, final Description mismatchDescription) {
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
    public static ValidationMatcher valid(final Object schemaSource) {
        return new ValidationMatcher(schemaSource);
    }


}
