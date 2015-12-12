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
package org.xmlunit.diff;

import java.util.Map;
import java.util.LinkedHashMap;
import javax.xml.namespace.QName;
import org.xmlunit.util.Predicate;
import org.w3c.dom.Element;

class DefaultConditionalSelectorBuilder
    implements ElementSelectors.ConditionalSelectorBuilder,
               ElementSelectors.ConditionalSelectorBuilderThen {

    private ElementSelector defaultSelector;
    private final Map<Predicate<? super Element>, ElementSelector> conditionalSelectors =
        new LinkedHashMap<Predicate<? super Element>, ElementSelector>();
    private Predicate<? super Element> pendingCondition;

    @Override
    public ElementSelectors.ConditionalSelectorBuilder thenUse(ElementSelector es) {
        if (pendingCondition == null) {
            throw new IllegalStateException("missing condition");
        }
        conditionalSelectors.put(pendingCondition, es);
        pendingCondition = null;
        return this;
    }
    @Override
    public ElementSelectors.ConditionalSelectorBuilderThen when(Predicate<? super Element> predicate) {
        if (pendingCondition != null) {
            throw new IllegalStateException("unbalanced conditions");
        }
        pendingCondition = predicate;
        return this;
    }
    @Override
    public ElementSelectors.ConditionalSelectorBuilder elseUse(ElementSelector es) {
        if (defaultSelector != null) {
            throw new IllegalStateException("can't have more than one default selector");
        }
        defaultSelector = es;
        return this;
    }
    @Override
    public ElementSelectors.ConditionalSelectorBuilderThen whenElementIsNamed(String expectedName) {
        return when(ElementSelectors.elementNamePredicate(expectedName));
    }
    @Override
    public ElementSelectors.ConditionalSelectorBuilderThen whenElementIsNamed(QName expectedName) {
        return when(ElementSelectors.elementNamePredicate(expectedName));
    }
    @Override
    public ElementSelector build() {
        if (pendingCondition != null) {
            throw new IllegalStateException("unbalanced conditions");
        }
        return new ConditionalSelector(conditionalSelectors, defaultSelector);
    }

    private static class ConditionalSelector implements ElementSelector {
        private final Map<Predicate<? super Element>, ElementSelector> conditionalSelectors;
        private final ElementSelector defaultSelector;

        private ConditionalSelector(Map<Predicate<? super Element>, ElementSelector> conditionalSelectors,
                                    ElementSelector defaultSelector) {
            this.conditionalSelectors = new LinkedHashMap<Predicate<? super Element>, ElementSelector>(conditionalSelectors);
            this.defaultSelector = defaultSelector;
        }

        @Override
        public boolean canBeCompared(Element controlElement,
                                     Element testElement) {
            for (Map.Entry<Predicate<? super Element>, ElementSelector> e : conditionalSelectors.entrySet()) {
                if (e.getKey().test(controlElement)) {
                    return e.getValue().canBeCompared(controlElement, testElement);
                }
            }
            if (defaultSelector != null) {
                return defaultSelector.canBeCompared(controlElement, testElement);
            }
            return false;
        }
    }
}

