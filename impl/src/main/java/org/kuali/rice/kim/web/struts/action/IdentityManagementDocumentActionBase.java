/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kim.web.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.kim.service.IdentityService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.ResponsibilityService;
import org.kuali.rice.kim.service.UiDocumentService;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.web.struts.action.KualiTransactionalDocumentActionBase;

/**
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
abstract public class IdentityManagementDocumentActionBase extends KualiTransactionalDocumentActionBase {


	protected IdentityService identityService;
	protected ResponsibilityService responsibilityService;
	protected UiDocumentService uiDocumentService;

    /**
     * 
     * This overridden method is to add 'kim/" to the return path
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#performLookup(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
	@Override
	public ActionForward performLookup(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ActionForward forward =  super.performLookup(mapping, form, request, response);
		String path = forward.getPath();
		//Making the hack look cleaner!
		path = path.replace(getActionName(), "kim/"+getActionName());
		forward.setPath(path);
		return forward;
	}

	protected abstract String getActionName();
	
    public IdentityService getIdentityService() {
    	if ( identityService == null ) {
    		identityService = KIMServiceLocator.getIdentityService();
    	}
		return identityService;
	}

    public ResponsibilityService getResponsibilityService() {
    	if ( responsibilityService == null ) {
    		responsibilityService = KIMServiceLocator.getResponsibilityService();
    	}
		return responsibilityService;
	}

	public UiDocumentService getUiDocumentService() {
		if ( uiDocumentService == null ) {
			uiDocumentService = KIMServiceLocator.getUiDocumentService();
		}
		return uiDocumentService;
	}

	@Override
    protected String getReturnLocation(HttpServletRequest request, ActionMapping mapping){
    	return getBasePath(request);
    }

	@Override
	public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward dest = super.close(mapping, form, request, response);
        if(!canSave(form) || getQuestion(request)!=null){
	        ActionForward newDest = new ActionForward();
	        KimCommonUtils.copyProperties(newDest, dest);
	        newDest.setPath(getReturnLocation(request, mapping));
	        return newDest;
        }
        return dest;
    }

}