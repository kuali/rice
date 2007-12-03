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
package edu.iu.uis.eden.user.web;

import org.apache.struts.action.ActionForm;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.EmplId;
import edu.iu.uis.eden.user.UuId;
import edu.iu.uis.eden.user.WorkflowUserId;
import edu.iu.uis.eden.util.Utilities;

/**
 * A Struts ActionForm for the {@link WorkflowUserAction}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WorkflowUserForm extends ActionForm {

	private WebWorkflowUser existingUser;
	private WebWorkflowUser user;
    private String methodToCall = "";
	private String showEdit = "no";
	
    public WorkflowUserForm() {
        user = new WebWorkflowUser(KEWServiceLocator.getUserService().getBlankUser());
    }

    public String getInstructionForCreateNew() {
        return Utilities.getApplicationConstant(EdenConstants.USER_CREATE_NEW_INSTRUCTION_KEY);
    }
    
	public WebWorkflowUser getUser() {
		return user;
	}

	public void setUser(WebWorkflowUser user) {
		this.user = user;
	}

	public String getExistingWorkflowId() {
		return existingUser.getWorkflowUserId().getWorkflowId();
	}
	
	public String getWorkflowId() {
		return user.getWorkflowUserId().getWorkflowId();
	}

	public void setWorkflowId(String workflowId) {
		user.setWorkflowUserId(new WorkflowUserId(workflowId));
	}

    public String getShowEdit() {
        return showEdit;
    }

    public void setShowEdit(String showEdit) {
        this.showEdit = showEdit;
    }

    public WebWorkflowUser getExistingUser() {
        return existingUser;
    }

    public void setExistingUser(WebWorkflowUser existingUser) {
        this.existingUser = existingUser;
    }
    
    public String getMethodToCall() {
        return methodToCall;
    }

    public void setMethodToCall(String methodToCall) {
        this.methodToCall = methodToCall;
    }

    public String getExistingAuthenticationId() {
        return existingUser.getAuthenticationUserId().getAuthenticationId();
    }
    
    public String getAuthenticationId() {
        return user.getAuthenticationUserId().getAuthenticationId();
    }

    public void setAuthenticationId(String authenticationId) {
        user.setAuthenticationUserId(new AuthenticationUserId(authenticationId));
    }

    public String getExistingEmplId() {
        return existingUser.getEmplId().getEmplId();
    }
    
    public String getEmplId() {
        return user.getEmplId().getEmplId();
    }

    public void setEmplId(String emplId) {
        user.setEmplId(new EmplId(emplId));
    }

    public String getExistingUuId() {
        return existingUser.getUuId().getUuId();
    }
    
    public String getUuId() {
        return user.getUuId().getUuId();
    }

    public void setUuId(String uuId) {
        user.setUuId(new UuId(uuId));
    }
    

}
