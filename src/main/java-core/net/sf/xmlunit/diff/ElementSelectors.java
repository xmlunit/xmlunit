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
package net.sf.xmlunit.diff;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.xml.namespace.QName;
import net.sf.xmlunit.util.Nodes;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Common ElementSelector implementations.
 */
public final class ElementSelectors {
    private ElementSelectors() { }

    /**
     * Always returns true, i.e. each element can be compared to each
     * other element.
     *
     * <p>Generally this means elements will be compared in document
     * order.</p>
     */
    public static final ElementSelector Default = new ElementSelector() {
            public boolean canBeCompared(Element controlElement,
                                         Element testElement) {
                return true;
            }
        };

    /**
     * Elements with the same local name (and namespace URI - if any)
     * can be compared.
     */
    public static final ElementSelector byName = new ElementSelector() {
            public boolean canBeCompared(Element controlElement,
                                         Element testElement) {
                return controlElement != null
                    && testElement != null
                    && bothNullOrEqual(Nodes.getQName(controlElement),
                                       Nodes.getQName(testElement));
            }
        };

    /**
     * Elements with the same local name (and namespace URI - if any)
     * and nested text (if any) can be compared.
     */
    public static final ElementSelector byNameAndText = new ElementSelector() {
            public boolean canBeCompared(Element controlElement,
                                         Element testElement) {
                return byName.canBeCompared(controlElement, testElement)
                    && bothNullOrEqual(Nodes.getMergedNestedText(controlElement),
                                       Nodes.getMergedNestedText(testElement));
            }
        };

    /**
     * Elements with the same local name (and namespace URI - if any)
     * and attribute values for the given attribute names can be
     * compared.
     *
     * <p>Attributes are only searched for in the null namespace.</p>
     */
    public static ElementSelector byNameAndAttributes(String... attribs) {
        if (attribs == null) {
            throw new IllegalArgumentException("attributes must not be null");
        }
        QName[] qs = new QName[attribs.length];
        for (int i = 0; i < attribs.length; i++) {
            qs[i] = new QName(attribs[i]);
        }
        return byNameAndAttributes(qs);
    }

    /**
     * Elements with the same local name (and namespace URI - if any)
     * and attribute values for the given attribute names can be
     * compared.
     *
     * <p>Namespace URIs of attributes are those of the attributes on
     * the control element or the null namespace if the don't
     * exist.</p>
     */
    public static ElementSelector
        byNameAndAttributesControlNS(final String... attribs) {

        if (attribs == null) {
            throw new IllegalArgumentException("attributes must not be null");
        }
        final HashSet<String> as = new HashSet(Arrays.asList(attribs));
        return new ElementSelector() {
            public boolean canBeCompared(Element controlElement,
                                         Element testElement) {
                if (!byName.canBeCompared(controlElement, testElement)) {
                    return false;
                }
                Map<QName, String> cAttrs = Nodes.getAttributes(controlElement);
                Map<String, QName> qNameByLocalName =
                    new HashMap<String, QName>();
                for (QName q : cAttrs.keySet()) {
                    String local = q.getLocalPart();
                    if (as.contains(local)) {
                        qNameByLocalName.put(local, q);
                    }
                }
                for (String a : as) {
                    QName q = qNameByLocalName.get(a);
                    if (q == null) {
                        qNameByLocalName.put(a, new QName(a));
                    }
                }
                return mapsEqualForKeys(cAttrs,
                                        Nodes.getAttributes(testElement),
                                        qNameByLocalName.values());
            }
        };
    }

    /**
     * Elements with the same local name (and namespace URI - if any)
     * and attribute values for the given attribute names can be
     * compared.
     */
    public static ElementSelector byNameAndAttributes(final QName... attribs) {
        if (attribs == null) {
            throw new IllegalArgumentException("attributes must not be null");
        }
        final Collection<QName> qs = Arrays.asList(attribs);
        return new ElementSelector() {
            public boolean canBeCompared(Element controlElement,
                                         Element testElement) {
                if (!byName.canBeCompared(controlElement, testElement)) {
                    return false;
                }
                return mapsEqualForKeys(Nodes.getAttributes(controlElement),
                                        Nodes.getAttributes(testElement),
                                        qs);
            }
        };
    }

    /**
     * Elements with the same local name (and namespace URI - if any)
     * and attribute values for all attributes can be compared.
     */
    public static final ElementSelector byNameAndAllAttributes =
        new ElementSelector() {
            public boolean canBeCompared(Element controlElement,
                                         Element testElement) {
                if (!byName.canBeCompared(controlElement, testElement)) {
                    return false;
                }
                Map<QName, String> cAttrs = Nodes.getAttributes(controlElement);
                Map<QName, String> tAttrs = Nodes.getAttributes(testElement);
                if (cAttrs.size() != tAttrs.size()) {
                    return false;
                }
                return mapsEqualForKeys(cAttrs, tAttrs, cAttrs.keySet());
            }
        };

    /**
     * Elements with the same local name (and namespace URI - if any)
     * and child elements and nested text at each level (if any) can
     * be compared.
     */
    public static final ElementSelector
        byNameAndTextRec = new ElementSelector() {
                public boolean canBeCompared(Element controlElement,
                                             Element testElement) {
                    if (!byNameAndText.canBeCompared(controlElement,
                                                     testElement)) {
                        return false;
                    }
                    NodeList controlChildren = controlElement.getChildNodes();
                    NodeList testChildren = testElement.getChildNodes();
                    final int controlLen = controlChildren.getLength();
                    final int testLen = testChildren.getLength();
                    int controlIndex, testIndex;
                    for (controlIndex = testIndex = 0;
                         controlIndex < controlLen && testIndex < testLen;
                         ) {
                        // find next non-text child nodes
                        Node c = controlChildren.item(controlIndex);
                        while (isText(c) && ++controlIndex < controlLen) {
                            c = controlChildren.item(controlIndex);
                        }
                        if (isText(c)) {
                            break;
                        }
                        Node t = testChildren.item(testIndex);
                        while (isText(t) && ++testIndex < testLen) {
                            t = testChildren.item(testIndex);
                        }
                        if (isText(t)) {
                            break;
                        }

                        // different types of children make elements
                        // non-comparable
                        if (c.getNodeType() != t.getNodeType()) {
                            return false;
                        }
                        // recurse for child elements
                        if (c instanceof Element
                            && !byNameAndTextRec.canBeCompared((Element) c,
                                                               (Element) t)) {
                            return false;
                        }

                        controlIndex++;
                        testIndex++;
                    }

                    // child lists exhausted?
                    if (controlIndex < controlLen) {
                        Node n = controlChildren.item(controlIndex);
                        while (isText(n) && ++controlIndex < controlLen) {
                            n = controlChildren.item(controlIndex);
                        }
                        // some non-Text children remained
                        if (controlIndex < controlLen) {
                            return false;
                        }
                    }
                    if (testIndex < testLen) {
                        Node n = testChildren.item(testIndex);
                        while (isText(n) && ++testIndex < testLen) {
                            n = testChildren.item(testIndex);
                        }
                        // some non-Text children remained
                        if (testIndex < testLen) {
                            return false;
                        }
                    }
                    return true;
                }
            };

    private static boolean bothNullOrEqual(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }

    private static boolean mapsEqualForKeys(Map<QName, String> control,
                                            Map<QName, String> test,
                                            Iterable<QName> keys) {
        for (QName q : keys) {
            if (!bothNullOrEqual(control.get(q), test.get(q))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isText(Node n) {
        return n instanceof Text || n instanceof CDATASection;
    }
}