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
package org.kuali.rice.kns.workflow.bo;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.CascadeType;
import javax.persistence.Table;
import javax.persistence.Entity;

import org.kuali.rice.kew.user.AuthenticationUserId;
import org.kuali.rice.kew.user.BaseWorkflowUser;


/**
 * This class ensures that a lower case authenticationUserId is returned.
 * 
 * 
 */
@Entity
@Table(name="FS_UNIVERSAL_USR_T")
public class WorkflowUser extends BaseWorkflowUser {
    /**
     * This method returns a lower case version of the authenticationUserId
     * 
     * @see org.kuali.rice.kew.user.BaseWorkflowUser#getAuthenticationUserId()
     */
    public AuthenticationUserId getAuthenticationUserId() {
        if (super.getAuthenticationUserId() != null) {
            super.setAuthenticationUserId(new AuthenticationUserId(super.getAuthenticationUserId().getAuthenticationId().toLowerCase()));
        }
        return super.getAuthenticationUserId();
    }
}

