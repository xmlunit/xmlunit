/*
******************************************************************
Copyright (c) 2001-2008, Jeff Martin, Tim Bacon
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

package org.custommonkey.xmlunit;

import java.util.List;
import java.util.ArrayList;

/**
 * Compares and describes all the differences between two XML documents.
 * The document comparison does not stop once the first unrecoverable difference
 * is found, unlike the Diff class.
 * Note that because the differences are described relative to some control XML
 * the list of all differences when <i>A</i> is compared to <i>B</i> will not
 * necessarily be the same as when <i>B</i> is compared to <i>A</i>.
 * <br />Examples and more at <a href="http://xmlunit.sourceforge.net"/>xmlunit.sourceforge.net</a>
 */
public class DetailedDiff extends Diff {
    private final List allDifferences;

    /**
     * Create a new instance based on a prototypical Diff instance
     * @param prototype the Diff instance for which more detailed difference
     *  information is required
     */
    public DetailedDiff(Diff prototype) {
        super(prototype);
        allDifferences = new ArrayList();
    }

    /**
     * DifferenceListener implementation.
     * Add the difference to the list of all differences
     * @param expected
     * @param actual
     * @param control
     * @param test
     * @param comparingWhat
     * @return the value supplied by the superclass implementation
     */
    public int differenceFound(Difference difference) {
        final int returnValue = super.differenceFound(difference);
        switch (returnValue) {
        case RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL:
            return returnValue;
        case RETURN_ACCEPT_DIFFERENCE:
            break;
        case RETURN_IGNORE_DIFFERENCE_NODES_SIMILAR:
            difference.setRecoverable(true);
            break;
        case RETURN_UPGRADE_DIFFERENCE_NODES_DIFFERENT:
            difference.setRecoverable(false);
            break;
        default:
            throw new IllegalArgumentException(returnValue
                                               + " is not a defined "
                                               + " DifferenceListener"
                                               + ".RETURN_... value");
        }
        allDifferences.add(difference);
        return returnValue;
    }

    /**
     * ComparisonController implementation.
     * @param afterDifference
     * @return false always as this class wants to see all differences
     */
    public boolean haltComparison(Difference afterDifference) {
        return false;
    }

    /**
     * Obtain all the differences found by this instance
     * @return a list of {@link Difference differences}
     */
    public List getAllDifferences() {
        compare();
        return allDifferences;
    }
}
