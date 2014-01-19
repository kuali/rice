/**
 * Copyright 2005-2014 The Kuali Foundation
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

import org.kuali.rice.krad.data.jpa.IdClassBase;

/**
 * Composite primary key for the {@link UserOptions} class.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UserOptionsId extends IdClassBase {

    private static final long serialVersionUID = -982957447172014416L;

    private String workflowId;
    private String optionId;

    public UserOptionsId() {}

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
     * Returns the current workflow id.
     * @return the current workflow id.
     */
    public String getWorkflowId() {
        return workflowId;
    }

}

