/*
******************************************************************
Copyright (c) 2008,2015,2022 Jeff Martin, Tim Bacon
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
    * Neither the name of the XMLUnit nor the names
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

import java.util.Locale;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceListener;

/**
 * Ignores case for all textual content.
 */
public class CaseInsensitiveDifferenceListener
    extends TextDifferenceListenerBase {

    /**
     * Creates a new instance
     * @param delegateTo the DifferenceListener to delegate to.
     */
    public CaseInsensitiveDifferenceListener(DifferenceListener delegateTo) {
        super(delegateTo);
    }

    @Override
    protected int textualDifference(Difference d) {
        String control = d.getControlNodeDetail().getValue();
        if (control != null) {
            control = control.toLowerCase(Locale.US);
            if (d.getTestNodeDetail().getValue() != null
                && control.equals(d.getTestNodeDetail().getValue()
                                  .toLowerCase(Locale.US))) {
                return
                    DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
            }
        }
        // some string is null, delegate
        return super.textualDifference(d);
    }
}
