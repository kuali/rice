/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kim.web.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.kim.bo.impl.RoleImpl;
import org.kuali.rice.kim.bo.types.dto.KimTypeInfo;
import org.kuali.rice.kim.lookup.KimTypeLookupableHelperServiceImpl;
import org.kuali.rice.kim.service.IdentityService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.ResponsibilityService;
import org.kuali.rice.kim.service.UiDocumentService;
import org.kuali.rice.kim.service.support.KimRoleTypeService;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kim.web.struts.form.IdentityManagementDocumentFormBase;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.RiceKeyConstants;
import org.kuali.rice.kns.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kns.web.struts.form.KualiTableRenderFormMetadata;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
abstract public class IdentityManagementDocumentActionBase extends KualiTransactionalDocumentActionBase {

	protected static final String CHANGE_MEMBER_TYPE_CODE_METHOD_TO_CALL = "changeMemberTypeCode";
	protected static final String CHANGE_NAMESPACE_METHOD_TO_CALL = "changeNamespace";

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
		forward.setPath(KimCommonUtils.getPathWithKimContext(path, getActionName()));
		return forward;
	}

	protected abstract String getActionName();
	
    protected IdentityService getIdentityService() {
    	if ( identityService == null ) {
    		identityService = KIMServiceLocator.getIdentityService();
    	}
		return identityService;
	}

    protected ResponsibilityService getResponsibilityService() {
    	if ( responsibilityService == null ) {
    		responsibilityService = KIMServiceLocator.getResponsibilityService();
    	}
		return responsibilityService;
	}

    protected UiDocumentService getUiDocumentService() {
		if ( uiDocumentService == null ) {
			uiDocumentService = KIMServiceLocator.getUiDocumentService();
		}
		return uiDocumentService;
	}

	@Override
    protected String getReturnLocation(HttpServletRequest request, ActionMapping mapping){
    	String returnLocation = super.getReturnLocation(request, mapping);
    	return KimCommonUtils.getPathWithKimContext(returnLocation, getActionName());
    }

	@Override
    protected ActionForward returnToSender(HttpServletRequest request, ActionMapping mapping, KualiDocumentFormBase form) {
        ActionForward dest = null;
        if (form.isReturnToActionList()) {
            String workflowBase = getKualiConfigurationService().getPropertyString(KNSConstants.WORKFLOW_URL_KEY);
            String actionListUrl = workflowBase + "/ActionList.do";

            dest = new ActionForward(actionListUrl, true);
        } else if (StringUtils.isNotBlank(form.getBackLocation())){
        	dest = new ActionForward(form.getBackLocation(), true);
        } else {
        	dest = mapping.findForward(KNSConstants.MAPPING_PORTAL);
            ActionForward newDest = new ActionForward();
            //why is this being done?
            KimCommonUtils.copyProperties(newDest, dest);
            newDest.setPath(getApplicationBaseUrl());
            return newDest;
        }

        setupDocumentExit();
        return dest;
    }
    
	
	@Override
	public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward dest = super.close(mapping, form, request, response);
        if(!canSave(form) || getQuestion(request)!=null){
	        ActionForward newDest = new ActionForward();
	        KimCommonUtils.copyProperties(newDest, dest);
	        newDest.setPath(getApplicationBaseUrl());
	        return newDest;
        }
        return dest;
    }
	
	public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ActionForward forward = super.cancel(mapping, form, request, response);
        Object question = request.getParameter(KNSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        boolean forwardToBasePath = false;
        if(question!=null){
        	Object buttonClicked = request.getParameter(KNSConstants.QUESTION_CLICKED_BUTTON);
        	if ((KNSConstants.DOCUMENT_CANCEL_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)){
        		forwardToBasePath = true;
        	}
        }
        if(forwardToBasePath)
        	return getBasePathForward(request, forward);
        else
        	return forward;
    }

	protected ActionForward getBasePathForward(HttpServletRequest request, ActionForward forward){
		ActionForward newDest = new ActionForward();
        KimCommonUtils.copyProperties(newDest, forward);
        newDest.setPath(getApplicationBaseUrl());
        return newDest;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiTableAction#switchToPage(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward switchToPage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementDocumentFormBase idmForm = (IdentityManagementDocumentFormBase) form;
        
        KualiTableRenderFormMetadata memberTableMetadata = idmForm.getMemberTableMetadata();
        memberTableMetadata.jumpToPage(memberTableMetadata.getSwitchToPageNumber(), idmForm.getMemberRows().size(), idmForm.getRecordsPerPage());
        memberTableMetadata.setColumnToSortIndex(memberTableMetadata.getPreviouslySortedColumnIndex());
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    protected void applyPagingAndSortingFromPreviousPageView(IdentityManagementDocumentFormBase idmForm) {
        KualiTableRenderFormMetadata memberTableMetadata = idmForm.getMemberTableMetadata();

        memberTableMetadata.jumpToPage(memberTableMetadata.getViewedPageNumber(), idmForm.getMemberRows().size(), idmForm.getRecordsPerPage());
    }

    protected boolean validateRole( String roleId, RoleImpl roleImpl, String propertyName, String message){
    	if ( roleImpl == null ) {
        	GlobalVariables.getMessageMap().putError(propertyName, RiceKeyConstants.ERROR_INVALID_ROLE, roleId );
    		return false;
    	}
    	if(KimTypeLookupableHelperServiceImpl.hasDerivedRoleTypeService(roleImpl.getKimRoleType())){
        	GlobalVariables.getMessageMap().putError(propertyName, RiceKeyConstants.ERROR_CANT_ADD_DERIVED_ROLE, message);
        	return false;
        }
    	return true;
    }
 
    public ActionForward changeNamespace(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return refresh(mapping, form, request, response);
    }

    protected KimTypeService getKimTypeService( KimTypeInfo typeInfo ) {
		String serviceName = typeInfo.getKimTypeServiceName();
		if ( StringUtils.isNotBlank(serviceName) ) {
			try {
				KimTypeService service = (KimTypeService)KIMServiceLocator.getService( serviceName );
				if ( service != null && service instanceof KimRoleTypeService ) {
					return (KimRoleTypeService)service;
				} else {
					return (KimRoleTypeService)KIMServiceLocator.getService( "kimNoMembersRoleTypeService" );
				}
			} catch ( Exception ex ) {
//				LOG.error( "Unable to find role type service with name: " + serviceName, ex );
				return (KimRoleTypeService)KIMServiceLocator.getService( "kimNoMembersRoleTypeService" );
			}
		}
		return null;
    }

}
