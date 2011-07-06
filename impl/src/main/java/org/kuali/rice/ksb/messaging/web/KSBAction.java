/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.rice.ksb.messaging.web;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.krad.exception.AuthorizationException;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * An abstract super class for all Struts Actions in KEW.  Adds some custom
 * dispatch behavior by extending the Struts DispatchAction.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class KSBAction extends DispatchAction {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KSBAction.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		checkAuthorization(form, "");
		
		try {

			
			ActionMessages messages = null;
			messages = establishRequiredState(request, form);
			if (messages != null && !messages.isEmpty()) {
				// XXX: HACK: FIXME:
				// obviously this implies that we can't return both ActionErrors
				// and ActionMessages... :(
				// probably establishRequiredState should be refactored to have
				// a generic 'should-we-continue'
				// boolean return, so that control flow can be more explicitly
				// specified by the subclass
				if (messages instanceof ActionErrors) {
					saveErrors(request, messages);
				} else {
					saveMessages(request, messages);
				}
				return mapping.findForward("requiredStateError");
			}
			LOG.info(request.getQueryString());
			ActionForward returnForward = null;

			if (request.getParameterMap() != null) {
				for (Iterator iter = request.getParameterMap().entrySet().iterator(); iter.hasNext();) {
					String parameterName = (String) ((Map.Entry) iter.next()).getKey();
					if (parameterName.startsWith("methodToCall.") && parameterName.endsWith(".x")) {
						String methodToCall = parameterName.substring(parameterName.indexOf("methodToCall.") + 13, parameterName.lastIndexOf(".x"));
						if (methodToCall != null && methodToCall.length() > 0) {
							returnForward = this.dispatchMethod(mapping, form, request, response, methodToCall);
						}
					}
				}
			}
			if (returnForward == null) {
				if (request.getParameter("methodToCall") != null && !"".equals(request.getParameter("methodToCall")) && !"execute".equals(request.getParameter("methodToCall"))) {
					LOG.info("dispatch to methodToCall " + request.getParameter("methodToCall") + " called");
					returnForward = super.execute(mapping, form, request, response);
				} else {
					LOG.info("dispatch to default start methodToCall");
					returnForward = start(mapping, form, request, response);
				}
			}

			
			
			messages = establishFinalState(request, form);
			if (messages != null && !messages.isEmpty()) {
				saveMessages(request, messages);
				return mapping.findForward("finalStateError");
			}
			return returnForward;
		} catch (Exception e) {
			LOG.error("Error processing action " + mapping.getPath(), e);
			throw new RuntimeException(e);
		}
	}
	
	protected void checkAuthorization( ActionForm form, String methodToCall) throws AuthorizationException 
    {
    	String principalId = GlobalVariables.getUserSession().getPrincipalId();
    	AttributeSet roleQualifier = new AttributeSet(getRoleQualification(form, methodToCall));
    	AttributeSet permissionDetails = KRADUtils.getNamespaceAndActionClass(this.getClass());
    	
        if (!KimApiServiceLocator.getPermissionService().isAuthorizedByTemplateName(principalId, KRADConstants.KRAD_NAMESPACE,
        		KimConstants.PermissionTemplateNames.USE_SCREEN, permissionDetails, roleQualifier ))
        {
        	throw new AuthorizationException(GlobalVariables.getUserSession().getPrincipalName(), 
            		methodToCall,
            		this.getClass().getSimpleName());
        }
    }
    
    /** 
     * override this method to add data from the form for role qualification in the authorization check
     */
    protected Map<String,String> getRoleQualification(ActionForm form, String methodToCall) {
    	return new HashMap<String,String>();
    }

	public abstract ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception;

	public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return start(mapping, form, request, response);
	}

	public abstract ActionMessages establishRequiredState(HttpServletRequest request, ActionForm form) throws Exception;

	public ActionMessages establishFinalState(HttpServletRequest request, ActionForm form) throws Exception {
		return null;
	}

	public ActionForward noOp(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return mapping.findForward("basic");
	}
	
	protected static KualiModuleService getKualiModuleService() {
        return KRADServiceLocatorWeb.getKualiModuleService();
    }

}
