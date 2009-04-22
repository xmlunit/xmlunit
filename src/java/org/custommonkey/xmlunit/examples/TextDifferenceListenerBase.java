/*
******************************************************************
Copyright (c) 2008, Jeff Martin, Tim Bacon
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the following
      disclaimer in the documentation and/or other materials provided
      with the distribution.
    * Neither the name of the xmlunit.sourceforge.net nor the names
      of its contributors may be used to endorse or promote products
      derived from this software without specific prior written
      permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

******************************************************************
*/
package org.custommonkey.xmlunit.examples;

import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceConstants;
import org.custommonkey.xmlunit.DifferenceListener;
import org.w3c.dom.Node;

/**
 * Base class that delegates all differences to another DifferenceListener.
 *
 * <p>Subclasses get a chance to hook into special methods that will
 * be invoked for differences in textual values of attributes, CDATA
 * sections, Text or comment nodes.</p>
 */
public abstract class TextDifferenceListenerBase
    implements DifferenceListener {

    private final DifferenceListener delegateTo;

    protected TextDifferenceListenerBase(DifferenceListener delegateTo) {
        this.delegateTo = delegateTo;
    }

    /**
     * Delegates to the nested DifferenceListener unless the
     * Difference is of type {@link DifferenceConstants#ATTR_VALUE_ID
     * ATTR_VALUE_ID}, {@link DifferenceConstants#CDATA_VALUE_ID
     * CDATA_VALUE_ID}, {@link DifferenceConstants#COMMENT_VALUE_ID
     * COMMENT_VALUE_ID} or {@link DifferenceConstants#TEXT_VALUE_ID
     * TEXT_VALUE_ID} - for those special differences {@link
     * #attributeDifference attributeDifference}, {@link
     * #cdataDifference cdataDifference}, {@link #commentDifference
     * commentDifference} or {@link #textDifference textDifference}
     * are invoked respectively.
     */
    public int differenceFound(Difference difference) {
        switch (difference.getId()) {
        case DifferenceConstants.ATTR_VALUE_ID:
            return attributeDifference(difference);
        case DifferenceConstants.CDATA_VALUE_ID:
            return cdataDifference(difference);
        case DifferenceConstants.COMMENT_VALUE_ID:
            return commentDifference(difference);
        case DifferenceConstants.TEXT_VALUE_ID:
            return textDifference(difference);
        }
        return delegateTo.differenceFound(difference);
    }

    /**
     * Delegates to {@link #textualDifference textualDifference}.
     */
    protected int attributeDifference(Difference d) {
        return textualDifference(d);
    }

    /**
     * Delegates to {@link #textualDifference textualDifference}.
     */
    protected int cdataDifference(Difference d) {
        return textualDifference(d);
    }

    /**
     * Delegates to {@link #textualDifference textualDifference}.
     */
    protected int commentDifference(Difference d) {
        return textualDifference(d);
    }

    /**
     * Delegates to {@link #textualDifference textualDifference}.
     */
    protected int textDifference(Difference d) {
        return textualDifference(d);
    }

    /**
     * Delegates to the nested DifferenceListener.
     */
    protected int textualDifference(Difference d) {
        return delegateTo.differenceFound(d);
    }

    public void skippedComparison(Node control, Node test) {
        delegateTo.skippedComparison(control, test);
    }

}
