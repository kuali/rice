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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.core.util.RiceKeyConstants;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.lookup.KimTypeLookupableHelperServiceImpl;
import org.kuali.rice.kim.service.IdentityService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.KIMServiceLocatorInternal;
import org.kuali.rice.kim.service.ResponsibilityService;
import org.kuali.rice.kim.service.UiDocumentService;
import org.kuali.rice.kim.service.support.KimRoleTypeService;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kim.util.KimCommonUtilsInternal;
import org.kuali.rice.kim.web.struts.form.IdentityManagementDocumentFormBase;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kns.web.struts.form.KualiTableRenderFormMetadata;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
abstract public class IdentityManagementDocumentActionBase extends KualiTransactionalDocumentActionBase {

    private static final Logger LOG = Logger.getLogger( IdentityManagementDocumentActionBase.class );

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
		forward.setPath(KimCommonUtilsInternal.getPathWithKimContext(path, getActionName()));
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
			uiDocumentService = KIMServiceLocatorInternal.getUiDocumentService();
		}
		return uiDocumentService;
	}

	@Override
    protected String getReturnLocation(HttpServletRequest request, ActionMapping mapping){
    	String returnLocation = super.getReturnLocation(request, mapping);
    	return KimCommonUtilsInternal.getPathWithKimContext(returnLocation, getActionName());
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
            KimCommonUtilsInternal.copyProperties(newDest, dest);
            newDest.setPath(getApplicationBaseUrl());
            return newDest;
        }

        setupDocumentExit();
        return dest;
    }    

	protected ActionForward getBasePathForward(HttpServletRequest request, ActionForward forward){
		ActionForward newDest = new ActionForward();
        KimCommonUtilsInternal.copyProperties(newDest, forward);
        newDest.setPath(getApplicationBaseUrl());
        return newDest;
    }

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

    protected boolean validateRole( String roleId, Role role, String propertyName, String message){
    	if ( role == null ) {
        	GlobalVariables.getMessageMap().putError(propertyName, RiceKeyConstants.ERROR_INVALID_ROLE, roleId );
    		return false;
    	}
    	KimType typeInfo = KimApiServiceLocator.getKimTypeInfoService().getKimType(role.getKimTypeId());
    	
    	if(KimTypeLookupableHelperServiceImpl.hasDerivedRoleTypeService(typeInfo)){
        	GlobalVariables.getMessageMap().putError(propertyName, RiceKeyConstants.ERROR_CANT_ADD_DERIVED_ROLE, message);
        	return false;
        }
    	return true;
    }
 
    public ActionForward changeNamespace(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return refresh(mapping, form, request, response);
    }

    protected KimTypeService getKimTypeService( KimType typeInfo ) {
		String serviceName = typeInfo.getServiceName();
		if ( StringUtils.isNotBlank(serviceName) ) {
			try {
				KimTypeService service = (KimTypeService) KIMServiceLocatorInternal.getService(serviceName);
				if ( service != null && service instanceof KimRoleTypeService ) {
					return (KimRoleTypeService)service;
				} else {
					return (KimRoleTypeService) KIMServiceLocatorInternal.getService("kimNoMembersRoleTypeService");
				}
			} catch ( Exception ex ) {
				LOG.error( "Unable to find role type service with name: " + serviceName, ex );
				return (KimRoleTypeService) KIMServiceLocatorInternal.getService("kimNoMembersRoleTypeService");
			}
		}
		return null;
    }

}
