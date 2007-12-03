/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.clientapp.vo;

import java.io.Serializable;

import edu.iu.uis.eden.user.WorkflowUser;

/**
 * Transport object representing a {@link WorkflowUser}.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 * @workflow.webservice-object
 */
public class UserVO implements Serializable {
    
    static final long serialVersionUID = -5651830932718276022L;
    
    private String networkId;
    private String uuId;
    private String emplId;
    private String workflowId;
    private String displayName;
    private String lastName;
    private String firstName;
    private String emailAddress;
    private boolean userPreferencePopDocHandler;
    
    public UserVO() {}

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmplId() {
        return emplId;
    }
    public void setEmplId(String emplId) {
        this.emplId = emplId;
    }
    public String getNetworkId() {
        return networkId;
    }
    public void setNetworkId(String netId) {
        this.networkId = netId;
    }
    public String getUuId() {
        return uuId;
    }
    public void setUuId(String uuId) {
        this.uuId = uuId;
    }
    public String getWorkflowId() {
        return workflowId;
    }
    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }
    public boolean isUserPreferencePopDocHandler() {
        return userPreferencePopDocHandler;
    }
    public void setUserPreferencePopDocHandler(boolean userPreferencePopDocHandler) {
        this.userPreferencePopDocHandler = userPreferencePopDocHandler;
    }
}