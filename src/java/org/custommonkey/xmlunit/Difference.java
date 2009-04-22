/*
******************************************************************
Copyright (c) 2001-2007, Jeff Martin, Tim Bacon
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


/**
 * Value object that describes a difference between DOM Nodes using one of
 * the DifferenceConstants ID values and a NodeDetail instance.
 * <br />Examples and more at <a href="http://xmlunit.sourceforge.net"/>xmlunit.sourceforge.net</a>
 * @see NodeDetail
 */
public class Difference {
    /** Simple unique identifier */
    private final int id;
    /** Description of the difference */
    private final String description;
    /** TRUE if the difference represents a similarity, FALSE otherwise */
    private boolean recoverable;
    
    private NodeDetail controlNodeDetail = null;
    private NodeDetail testNodeDetail = null;

    /**
     * Constructor for non-similar Difference instances
     * @param id
     * @param description
     */
    protected Difference(int id, String description) {
        this(id, description, false);
    }

    /**
     * Constructor for similar Difference instances
     * @param id
     * @param description
     */
    protected Difference(int id, String description, boolean recoverable) {
        this.id = id;
        this.description = description;
        this.recoverable = recoverable;
    }
    

    /**
     * Copy constructor using prototype Difference and
     * encountered NodeDetails
     */
    protected Difference(Difference prototype, NodeDetail controlNodeDetail,
                         NodeDetail testNodeDetail) {
        this(prototype.getId(), prototype.getDescription(), prototype.isRecoverable());
        this.controlNodeDetail = controlNodeDetail;
        this.testNodeDetail = testNodeDetail;
    }
    
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return TRUE if the difference represents a similarity, FALSE otherwise
     */
    public boolean isRecoverable() {
        return recoverable;
    }
    
    /**
     * Allow the recoverable field value to be overridden.
     * Used when an override DifferenceListener is used in conjunction with
     * a DetailedDiff.
     */
    protected void setRecoverable(boolean overrideValue) {
        recoverable = overrideValue;
    }
    
    /**
     * @return the NodeDetail from the piece of XML used as the control 
     * at the Node where this difference was encountered
     */
    public NodeDetail getControlNodeDetail() {
        return controlNodeDetail;
    }

    /**
     * @return the NodeDetail from the piece of XML used as the test
     * at the Node where this difference was encountered
     */
    public NodeDetail getTestNodeDetail() {
        return testNodeDetail;
    }
    
    /**
     * Now that Differences can be constructed from prototypes
     * we need to be able to compare them to those in DifferenceConstants
     */
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (other instanceof Difference) {
            Difference otherDifference = (Difference) other;
            return id == otherDifference.getId();
        } else {
            return false;
        }
    }

    /**
     * hashcode implementation to go with equals.
     */
    public int hashCode() {
        return id;
    }

    /**
     * @return a basic representation of the object state and identity
     * and if <code>NodeDetail</code> instances are populated append 
     * their details also
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        if (controlNodeDetail == null || testNodeDetail == null) {
            appendBasicRepresentation(buf);
        } else {
            appendDetailedRepresentation(buf);
        }
        return buf.toString();
    }
    
    private void appendBasicRepresentation(StringBuffer buf) {
        buf.append("Difference (#").append(id).
            append(") ").append(description);
    }
    
    private void appendDetailedRepresentation(StringBuffer buf) {
        buf.append("Expected ").append(getDescription())
            .append(" '").append(controlNodeDetail.getValue())
            .append("' but was '").append(testNodeDetail.getValue())
            .append("' - comparing ");
        NodeDescriptor.appendNodeDetail(buf, controlNodeDetail);
        buf.append(" to ");
        NodeDescriptor.appendNodeDetail(buf, testNodeDetail);
    }

}
