/**
 * Copyright 2005-2013 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kew.useroptions;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Composite primary key for the {@link UserOptions} class.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UserOptionsId implements Serializable {

    private static final long serialVersionUID = -982957447172014416L;

    private String workflowId;
    private String optionId;

    /**
     * Default constructor
     */
    public UserOptionsId() { }

    /**
     * Constructor to accept a workflow id and option id as parameters and instantiate the class.
     * @param workflowId the workflow id
     * @param optionId the user option id
     */
    public UserOptionsId(String workflowId, String optionId) {
        this.workflowId = workflowId;
        this.optionId = optionId;
    }

    /**
     * Returns the current option id
     * @return the current option id
     */
    public String getOptionId() {
        return optionId;
    }

    /**
     * @see org.kuali.rice.kew.useroptions.UserOptionsId#getOptionId()
     */
    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    /**
     * Returns the current workflow id.
     * @return the current workflow id.
     */
    public String getWorkflowId() {
        return workflowId;
    }

    /**
     * @see org.kuali.rice.kew.useroptions.UserOptionsId#getWorkflowId()
     */
    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    /**
     * Determines if the given object is equal to the current class instance.
     * @param o the object to determine equality with.
     * @return TRUE if the given object is equal to the current class instance, false otherwise.
     */
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UserOptionsId)) {
            return false;
        }
        UserOptionsId pk = (UserOptionsId) o;
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(optionId, pk.optionId);
        builder.append(workflowId, pk.workflowId);
        return builder.isEquals();
    }

    /**
     * Method uses a {@link HashCodeBuilder} to create a composite hashcode.
     * @return the composite hashcode.
     */
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(getOptionId());
        builder.append(getWorkflowId());
        return builder.toHashCode();
    }

}

