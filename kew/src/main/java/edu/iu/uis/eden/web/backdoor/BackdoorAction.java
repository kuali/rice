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
package edu.iu.uis.eden.web.backdoor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.web.WorkflowAction;
import edu.iu.uis.eden.web.session.UserSession;
import edu.iu.uis.eden.workgroup.GroupNameId;
import edu.iu.uis.eden.workgroup.Workgroup;
import edu.iu.uis.eden.workgroup.WorkgroupService;

/**
 * A Struts Action which permits a user to execute a backdoor login to masquerade
 * as another user.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class BackdoorAction extends WorkflowAction {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BackdoorAction.class);

    public ActionForward menu(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("start");
        BackdoorForm backdoorForm = (BackdoorForm) form;
        backdoorForm.setTargetName(Utilities.getApplicationConstant("Config.Backdoor.TargetFrameName"));
        return mapping.findForward("viewBackdoor");
    }

    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("start");
        return portal(mapping, form, request, response);
    }

    public ActionForward portal(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception{
    	LOG.debug("portal started");
    	BackdoorForm backdoorForm=(BackdoorForm)form;
    	backdoorForm.setTargetName(Utilities.getApplicationConstant("Config.Backdoor.TargetFrameName"));
    	//LOG.debug(backdoorForm.getGraphic());
    	return mapping.findForward("viewPortal");
    }

    public ActionForward administration(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("administration");
        BackdoorForm backdoorForm = (BackdoorForm) form;
        backdoorForm.setTargetName(Utilities.getApplicationConstant("Config.Backdoor.TargetFrameName"));
        return mapping.findForward("administration");
    }

    public ActionForward logout(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("logout");
        UserSession uSession = getUserSession(request);
        uSession.clearBackdoor();
        setFormGroupPermission((BackdoorForm)form, request);
        //request.setAttribute("reloadPage","true");
        return mapping.findForward("viewPortal");

    }

    public ActionForward login(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("login");
        /*
        UserSession uSession = getUserSession(request);
        BackdoorForm backdoorForm = (BackdoorForm) form;
        try{
            uSession.setBackdoorId(backdoorForm.getBackdoorId());
            backdoorForm.setBackdoorId(uSession.getNetworkId());
            //setFormGroupPermission(backdoorForm, request);
            //set up preferences as backdoor person
            //uSession.setPreferences(SpringServiceLocator.getPreferencesService().getPreferences(uSession.getWorkflowUser()));
            //request.setAttribute("reloadPage","true");

        }catch(EdenUserNotFoundException ex){
        	uSession.setBackdoorId(null);
        	backdoorForm.setBackdoorId(null);
        	request.setAttribute("BackdoorException","No such user found. Login failure!");
        }
        setFormGroupPermission(backdoorForm, request);
        uSession.setPreferences(SpringServiceLocator.getPreferencesService().getPreferences(uSession.getWorkflowUser()));
        return mapping.findForward("viewPortal");
        */
        UserSession uSession = getUserSession(request);
        BackdoorForm backdoorForm = (BackdoorForm) form;
        if (!uSession.setBackdoorId(backdoorForm.getBackdoorId())) {
			request.setAttribute("badbackdoor", "Invalid backdoor Id given '" + backdoorForm.getBackdoorId() + "'");
        	return mapping.findForward("viewBackdoor");
        }
        uSession.getAuthentications().clear();
        backdoorForm.setBackdoorId(uSession.getNetworkId());
        setFormGroupPermission(backdoorForm, request);
        //set up preferences as backdoor person
        uSession.setGroups(KEWServiceLocator.getWorkgroupService().getUsersGroupNames(uSession.getWorkflowUser()));
        uSession.setPreferences(KEWServiceLocator.getPreferencesService().getPreferences(uSession.getWorkflowUser()));
        KEWServiceLocator.getWebAuthenticationService().establishInitialUserSession(uSession, request);
        //request.setAttribute("reloadPage","true");
        return mapping.findForward("viewBackdoor");

    }

    private void setFormGroupPermission(BackdoorForm backdoorForm, HttpServletRequest request) {
        Workgroup workflowAdminGroup = getWorkgroupService().getWorkgroup(new GroupNameId(Utilities.getApplicationConstant(EdenConstants.WORKFLOW_ADMIN_WORKGROUP_NAME_KEY)));
        backdoorForm.setIsWorkflowAdmin(new Boolean(workflowAdminGroup.hasMember(getUserSession(request).getWorkflowUser())));
        	}

    public ActionMessages establishRequiredState(HttpServletRequest request, ActionForm form) throws Exception {
    	BackdoorForm backdoorForm = (BackdoorForm) form;
    	String showBackdoorLoginValue = Utilities.getApplicationConstant(EdenConstants.SHOW_BACK_DOOR_LOGIN_KEY);
    	// default to true if not defined
    	Boolean showBackdoorLogin = Boolean.TRUE;
    	if (!StringUtils.isEmpty(showBackdoorLoginValue)) {
    		showBackdoorLogin = new Boolean(showBackdoorLoginValue);
    	}
        backdoorForm.setShowBackdoorLogin(showBackdoorLogin);
        setFormGroupPermission(backdoorForm, request);
        if (backdoorForm.getGraphic() != null) {
        	request.getSession().setAttribute("showGraphic", backdoorForm.getGraphic());
        }
        return null;
    }

    private WorkgroupService getWorkgroupService() {
        return (WorkgroupService) KEWServiceLocator.getService(KEWServiceLocator.WORKGROUP_SRV);
    }
}