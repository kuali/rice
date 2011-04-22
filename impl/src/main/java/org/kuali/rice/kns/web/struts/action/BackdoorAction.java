/*
 * Copyright 2006-2011 The Kuali Foundation
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
package org.kuali.rice.kns.web.struts.action;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.core.framework.services.CoreFrameworkServiceLocator;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.web.struts.form.BackdoorForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * A Struts Action which permits a user to execute a backdoor login to masquerade
 * as another user.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class BackdoorAction extends KualiAction {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BackdoorAction.class);

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        this.initForm(request, form);
        return super.execute(mapping, form, request, response);
    }

    public ActionForward menu(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("menu");
        return mapping.findForward("basic");
    }

    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	return portal(mapping, form, request, response);
    }
    
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("start");
        return portal(mapping, form, request, response);
    }

    public ActionForward portal(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception{
    	LOG.debug("portal started");
    	return mapping.findForward("viewPortal");
    }

    public ActionForward administration(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("administration");
        return mapping.findForward("administration");
    }

    public ActionForward logout(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("logout");
        
        String forward = "viewPortal";
        UserSession uSession = getUserSession(request);
        
        if (uSession.isBackdoorInUse()) {
            uSession.clearBackdoorUser();
            setFormGroupPermission((BackdoorForm)form, request);
            //request.setAttribute("reloadPage","true");
            
            org.kuali.rice.kns.UserSession KnsUserSession;
            KnsUserSession = GlobalVariables.getUserSession();
            KnsUserSession.clearBackdoorUser();
        }
        else {
            forward = "logout";
        }
        
        return mapping.findForward(forward);
    }

    public ActionForward login(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("login");
        UserSession uSession = getUserSession(request);
        BackdoorForm backdoorForm = (BackdoorForm) form;

        //if backdoor Id is empty or equal to currently logged in user, clear backdoor id
        if (uSession.isBackdoorInUse() &&
                (StringUtils.isEmpty(backdoorForm.getBackdoorId())
                || uSession.getLoggedInUserPrincipalName().equals(backdoorForm.getBackdoorId()))) {
            return logout(mapping, form, request, response);
        }
        
        try {
        	uSession.setBackdoorUser(backdoorForm.getBackdoorId());
        } catch (RiceRuntimeException e) {
        	LOG.warn("invalid backdoor id " + backdoorForm.getBackdoorId(), e);
            request.setAttribute("badbackdoor", "Invalid backdoor Id given '" + backdoorForm.getBackdoorId() + "'");
            return mapping.findForward("portal");
        }

        setFormGroupPermission(backdoorForm, request);
        
        return mapping.findForward("portal");
    }

    private void setFormGroupPermission(BackdoorForm backdoorForm, HttpServletRequest request) {
    	// based on whether or not they have permission to use the fictional "AdministrationAction", kind of a hack for now since I don't have time to
    	// split this single action up and I can't pass the methodToCall to the permission check
    	AttributeSet permissionDetails = new AttributeSet();
    	permissionDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE, KEWConstants.KEW_NAMESPACE);
    	permissionDetails.put(KimConstants.AttributeConstants.ACTION_CLASS, "org.kuali.rice.kew.web.backdoor.AdministrationAction");
    	boolean isAdmin = KIMServiceLocator.getIdentityManagementService().isAuthorizedByTemplateName(getUserSession(request).getPrincipalId(), KNSConstants.KNS_NAMESPACE,	KimConstants.PermissionTemplateNames.USE_SCREEN, permissionDetails, new AttributeSet());
        backdoorForm.setIsAdmin(isAdmin);
    }

    public void initForm(HttpServletRequest request, ActionForm form) throws Exception {
    	BackdoorForm backdoorForm = (BackdoorForm) form;

    	Boolean showBackdoorLogin = CoreFrameworkServiceLocator.getParameterService().getParameterValueAsBoolean(KEWConstants.KEW_NAMESPACE, KNSConstants.DetailTypes.BACKDOOR_DETAIL_TYPE, KEWConstants.SHOW_BACK_DOOR_LOGIN_IND);
        backdoorForm.setShowBackdoorLogin(showBackdoorLogin);
        setFormGroupPermission(backdoorForm, request);
        if (backdoorForm.getGraphic() != null) {
        	request.getSession().setAttribute("showGraphic", backdoorForm.getGraphic());
        }
    }

    public static UserSession getUserSession(HttpServletRequest request) {
        return GlobalVariables.getUserSession();
    }
}
