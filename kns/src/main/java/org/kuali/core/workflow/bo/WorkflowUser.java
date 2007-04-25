/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.core.workflow.bo;

import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.BaseWorkflowUser;

/**
 * This class ensures that a lower case authenticationUserId is returned.
 * 
 * 
 */
public class WorkflowUser extends BaseWorkflowUser {
    /**
     * This method returns a lower case version of the authenticationUserId
     * 
     * @see edu.iu.uis.eden.user.BaseWorkflowUser#getAuthenticationUserId()
     */
    public AuthenticationUserId getAuthenticationUserId() {
        if (super.getAuthenticationUserId() != null) {
            super.setAuthenticationUserId(new AuthenticationUserId(super.getAuthenticationUserId().getAuthenticationId().toLowerCase()));
        }
        return super.getAuthenticationUserId();
    }
}
