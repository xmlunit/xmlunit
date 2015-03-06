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
package org.xmlunit.validation;

import java.util.Iterator;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;

import org.junit.Assert;
import org.junit.Test;

public class ValidationHandlerTest {

    private ValidationHandler handler = new ValidationHandler();

    @Test
    public void errorIsAProblem() {
        handler.error(new SAXParseException("foo", new LocatorImpl()));
        ValidationResult r = handler.getResult();
        Assert.assertFalse(r.isValid());
        Assert.assertTrue(r.getProblems().iterator().hasNext());
        ValidationProblem p = r.getProblems().iterator().next();
        Assert.assertEquals(ValidationProblem.ProblemType.ERROR, p.getType());
    }

    @Test
    public void fatalErrorIsAProblem() {
        handler.fatalError(new SAXParseException("foo", new LocatorImpl()));
        ValidationResult r = handler.getResult();
        Assert.assertFalse(r.isValid());
        Assert.assertTrue(r.getProblems().iterator().hasNext());
        ValidationProblem p = r.getProblems().iterator().next();
        Assert.assertEquals(ValidationProblem.ProblemType.ERROR, p.getType());
    }

    @Test
    public void worningIsAProblemButDoesntInvalidateResult() {
        handler.warning(new SAXParseException("foo", new LocatorImpl()));
        ValidationResult r = handler.getResult();
        Assert.assertTrue(r.isValid());
        Assert.assertTrue(r.getProblems().iterator().hasNext());
        ValidationProblem p = r.getProblems().iterator().next();
        Assert.assertEquals(ValidationProblem.ProblemType.WARNING, p.getType());
    }

    @Test
    public void shouldIgnoreErrorThatHasBeenReportedAsFatalAlready() {
        SAXParseException ex = new SAXParseException("foo", new LocatorImpl());
        handler.fatalError(ex);
        handler.error(ex);
        ValidationResult r = handler.getResult();
        Assert.assertFalse(r.isValid());
        Iterator problems = r.getProblems().iterator();
        problems.next();
        Assert.assertFalse(problems.hasNext());
    }

    @Test
    public void shouldRecordErrorThatIsDifferentFromPreviousFatalAlready() {
        SAXParseException ex = new SAXParseException("foo", new LocatorImpl());
        handler.fatalError(ex);
        handler.error(new SAXParseException("foo", new LocatorImpl()));
        ValidationResult r = handler.getResult();
        Assert.assertFalse(r.isValid());
        Iterator problems = r.getProblems().iterator();
        problems.next();
        Assert.assertTrue(problems.hasNext());
    }
}
