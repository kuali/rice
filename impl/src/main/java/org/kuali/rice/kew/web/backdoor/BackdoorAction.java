/*
 * Copyright 2005-2007 The Kuali Foundation
 *
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
package org.kuali.rice.kew.web.backdoor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kew.web.KewKualiAction;
import org.kuali.rice.kew.web.session.UserSession;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.util.KNSConstants;


/**
 * A Struts Action which permits a user to execute a backdoor login to masquerade
 * as another user.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class BackdoorAction extends KewKualiAction {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BackdoorAction.class);

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        this.initForm(request, form);
        return super.execute(mapping, form, request, response);
    }

    public ActionForward menu(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("start");
        BackdoorForm backdoorForm = (BackdoorForm) form;
        //backdoorForm.setTargetName(Utilities.getKNSParameterValue(KEWConstants.KEW_NAMESPACE, KNSConstants.DetailTypes.BACKDOOR_DETAIL_TYPE, KEWConstants.BACKDOOR_TARGET_FRAME_NAME));
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
    	BackdoorForm backdoorForm=(BackdoorForm)form;
    	//backdoorForm.setTargetName(Utilities.getKNSParameterValue(KEWConstants.KEW_NAMESPACE, KNSConstants.DetailTypes.BACKDOOR_DETAIL_TYPE, KEWConstants.BACKDOOR_TARGET_FRAME_NAME));
    	//LOG.debug(backdoorForm.getGraphic());
    	return mapping.findForward("viewPortal");
    }

    public ActionForward administration(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("administration");
        BackdoorForm backdoorForm = (BackdoorForm) form;
        //backdoorForm.setTargetName(Utilities.getKNSParameterValue(KEWConstants.KEW_NAMESPACE, KNSConstants.DetailTypes.BACKDOOR_DETAIL_TYPE, KEWConstants.BACKDOOR_TARGET_FRAME_NAME));
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
        UserSession uSession = getUserSession(request);
        BackdoorForm backdoorForm = (BackdoorForm) form;
        if (!uSession.establishBackdoorWithPrincipalName(backdoorForm.getBackdoorId())) {
			request.setAttribute("badbackdoor", "Invalid backdoor Id given '" + backdoorForm.getBackdoorId() + "'");
			//return defaultDispatch(mapping, form, request, response);
			return mapping.findForward("portal");
        }
        uSession.getAuthentications().clear();
        setFormGroupPermission(backdoorForm, request);
        //return defaultDispatch(mapping, form, request, response);
        return mapping.findForward("portal");

    }

    private void setFormGroupPermission(BackdoorForm backdoorForm, HttpServletRequest request) {
    	// based on whether or not they have permission to use the fictional "AdministrationAction", kind of a hack for now since I don't have time to
    	// split this single action up and I can't pass the methodToCall to the permission check
    	AttributeSet permissionDetails = new AttributeSet();
    	permissionDetails.put(KimAttributes.NAMESPACE_CODE, KEWConstants.KEW_NAMESPACE);
    	permissionDetails.put(KimAttributes.ACTION_CLASS, "org.kuali.rice.kew.web.backdoor.AdministrationAction");
    	boolean isAdmin = KIMServiceLocator.getIdentityManagementService().isAuthorizedByTemplateName(getUserSession(request).getPrincipalId(), KNSConstants.KNS_NAMESPACE,	KimConstants.PermissionTemplateNames.USE_SCREEN, permissionDetails, new AttributeSet());
        backdoorForm.setIsAdmin(isAdmin);
    }

    public void initForm(HttpServletRequest request, ActionForm form) throws Exception {
    	BackdoorForm backdoorForm = (BackdoorForm) form;

    	// default to true if not defined
    	Boolean showBackdoorLogin = new Boolean(Utilities.getKNSParameterBooleanValue(KEWConstants.KEW_NAMESPACE, KNSConstants.DetailTypes.BACKDOOR_DETAIL_TYPE, KEWConstants.SHOW_BACK_DOOR_LOGIN_IND));

        backdoorForm.setShowBackdoorLogin(showBackdoorLogin);
        setFormGroupPermission(backdoorForm, request);
        if (backdoorForm.getGraphic() != null) {
        	request.getSession().setAttribute("showGraphic", backdoorForm.getGraphic());
        }
    }

    public static UserSession getUserSession(HttpServletRequest request) {
        return UserSession.getAuthenticatedUser();
    }
}
