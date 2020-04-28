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
package org.xmlunit.assertj.util;

import org.junit.rules.ExternalResource;

import java.util.Locale;

public class SetEnglishLocaleRule extends ExternalResource {

    private Locale locale;    

    @Override
    protected void before() {
        locale = Locale.getDefault();
        maybeSetDefault(Locale.ENGLISH);
    }

    @Override
    protected void after() {
        maybeSetDefault(locale);
    }

    private void maybeSetDefault(final Locale l) {
        if (l != null && !l.equals(Locale.getDefault())) {
            Locale.setDefault(l);
        }
    }
}
                                                      
