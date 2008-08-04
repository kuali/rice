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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.user.UserService;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.user.WorkflowUserId;
import edu.iu.uis.eden.web.WorkflowAction;

/**
 * A Struts Action which provides reporting, editing, and creation of users.
 * 
 * @see UserService
 * @see WorkflowUser
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WorkflowUserAction extends WorkflowAction {

    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return createNew(mapping, form, request, response);
    }
    
    /**
     * Creates a new WorkflowUser and populates it in the form.
     */
    public ActionForward createNew(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	WorkflowUserForm userForm = (WorkflowUserForm) form;
    	userForm.setUser(new WebWorkflowUser(KEWServiceLocator.getUserService().getBlankUser()));
    	return mapping.findForward("basic");
    }

    /**
     * Initiates the editing of an existing WorkflowUser.
     */
    public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        WorkflowUserForm userForm = (WorkflowUserForm) form;
        userForm.setUser(new WebWorkflowUser(userForm.getExistingUser()));
        userForm.setShowEdit("yes");
        return mapping.findForward("basic");
    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        WorkflowUserForm userForm = (WorkflowUserForm) form;
        // apply changes to the user and then save it
        WorkflowUser modifiedUser = applyChanges(userForm);
        getUserService().save(modifiedUser);
        userForm.setShowEdit("no");
        saveDocumentActionMessage("user.IUUserService.saved", userForm.getUser().getDisplayName(), request);
        return mapping.findForward("summary");
    }

    public ActionForward report(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        WorkflowUserForm userForm = (WorkflowUserForm) form;
        // the existing user gets set up by establish required state
		userForm.setUser(userForm.getExistingUser());
		// now clear out the existing user so it doesn't show up on the report form
		userForm.setExistingUser(null);
		return mapping.findForward("report");
    }

    /**
     * Applies the changes made to the given WebWorkflowUser on the form to the underlying user object
     * and returns the modified user object.  This could be a newly created user object if
     * this is a new user creation.
     */ 
    private WorkflowUser applyChanges(WorkflowUserForm form) throws Exception {
    	WorkflowUser user = form.getUser();
    	WorkflowUser modifiedUser = null;
    	if (user.getWorkflowId() != null && user.getWorkflowUserId() != null && !user.getWorkflowUserId().isEmpty()) {
    		try {
    			WorkflowUser existingUser = getUserService().getWorkflowUser(user.getWorkflowUserId());
    			if (existingUser != null) {
            		modifiedUser = getUserService().copy(existingUser, true);
        		}
    		} catch (EdenUserNotFoundException e) {
    			// we didn't find an existing user for the given id
    		}
    	} 
    	if (modifiedUser == null) {
    		modifiedUser = KEWServiceLocator.getUserService().getBlankUser();
    	}
    	// copy the data from the form onto the existing user
		modifiedUser.setAuthenticationUserId(user.getAuthenticationUserId());
		modifiedUser.setDisplayName(user.getDisplayName());
		modifiedUser.setEmailAddress(user.getEmailAddress());
		modifiedUser.setEmplId(user.getEmplId());
		modifiedUser.setGivenName(user.getGivenName());
		modifiedUser.setLastName(user.getLastName());
		modifiedUser.setUuId(user.getUuId());
		return modifiedUser;
    }
    
    /**
	 * Do we really want to be deleting users?
	 * 
	 * public ActionForward delete(ActionMapping mapping, ActionForm form,
	 * HttpServletRequest request, HttpServletResponse response) throws
	 * Exception { WorkflowUserForm userForm = (WorkflowUserForm) form;
	 * getUserService().delete(userForm.getUser()); userForm.setShowEdit("no");
	 * saveDocumentActionMessage("user.IUUserService.deleted",
	 * userForm.getUser().getDisplayName(), request); return
	 * mapping.findForward("summary"); }
	 */
    
    public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        WorkflowUserForm userForm = (WorkflowUserForm) form;
        if (userForm.getExistingUser() != null) {
        	userForm.setUser(new WebWorkflowUser(userForm.getExistingUser()));
        } else {
        	userForm.setUser(new WebWorkflowUser(KEWServiceLocator.getUserService().getBlankUser()));
        }
        return mapping.findForward("basic");
    }

    private void saveDocumentActionMessage(String messageKey, String insertValue, HttpServletRequest request) {
        ActionMessages messages = new ActionMessages();
        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(messageKey, insertValue));
        saveMessages(request, messages);
    }

    /**
     * Sets up the existing user on the form.  The existing user is always the user which exists in the database
 	 * with the given workflow id.
     */
    public ActionMessages establishRequiredState(HttpServletRequest request, ActionForm form) throws Exception {
    	request.getSession().getServletContext().setAttribute("UserCaps", KEWServiceLocator.getUserService().getCapabilities());
    	WorkflowUserForm userForm = (WorkflowUserForm) form;
        WorkflowUser workflowUser = null;
        if (!StringUtils.isEmpty(userForm.getWorkflowId())) {
            workflowUser = getUserService().getWorkflowUser(new WorkflowUserId(userForm.getWorkflowId()));
            if (workflowUser == null) {
            	throw new Exception("Could not locate user for the given workflow id: " + userForm.getWorkflowId());
            }
            userForm.setExistingUser(new WebWorkflowUser(workflowUser));
        }
        return null;
    }

    public UserService getUserService() {
        return KEWServiceLocator.getUserService();
    }

}