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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.role.KimRole;
import org.kuali.rice.kim.bo.role.impl.KimPermissionImpl;
import org.kuali.rice.kim.bo.role.impl.KimResponsibilityImpl;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleMember;
import org.kuali.rice.kim.bo.ui.KimDocumentRolePermission;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleResponsibility;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegation;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegationMember;
import org.kuali.rice.kim.document.IdentityManagementRoleDocument;
import org.kuali.rice.kim.rule.event.ui.AddDelegationEvent;
import org.kuali.rice.kim.rule.event.ui.AddDelegationMemberEvent;
import org.kuali.rice.kim.rule.event.ui.AddMemberEvent;
import org.kuali.rice.kim.rule.event.ui.AddPermissionEvent;
import org.kuali.rice.kim.rule.event.ui.AddResponsibilityEvent;
import org.kuali.rice.kim.service.IdentityService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.ResponsibilityService;
import org.kuali.rice.kim.service.UiDocumentService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kim.web.struts.form.IdentityManagementRoleDocumentForm;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.web.struts.action.KualiTransactionalDocumentActionBase;

/**
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class IdentityManagementRoleDocumentAction extends KualiTransactionalDocumentActionBase {


	protected IdentityService identityService;
	protected ResponsibilityService responsibilityService;
	protected UiDocumentService uiDocumentService;
	public static final String CHANGE_MEMBER_TYPE_CODE_METHOD_TO_CALL = "changeMemberTypeCode"; 
	
    @Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
        String methodToCall = findMethodToCall(form, request);
		ActionForward forward;
		if(KNSConstants.PARAM_MAINTENANCE_VIEW_MODE_INQUIRY.equals(methodToCall)) 
        	forward = mapping.findForward(KNSConstants.PARAM_MAINTENANCE_VIEW_MODE_INQUIRY);
        else
        	forward = super.execute(mapping, form, request, response);
        IdentityManagementRoleDocumentForm roleDocumentForm = (IdentityManagementRoleDocumentForm) form;
        IdentityManagementRoleDocument roleDoc = (IdentityManagementRoleDocument)roleDocumentForm.getDocument();
        String commandParam = request.getParameter(KNSConstants.PARAMETER_COMMAND);
        String kimTypeId = request.getParameter(KimConstants.PrimaryKeyConstants.KIM_TYPE_ID);
        if(KNSConstants.DOC_HANDLER_METHOD.equals(methodToCall) && kimTypeId!=null){
	        Map<String, String> criteria = new HashMap<String, String>();
	        criteria.put(KimConstants.PrimaryKeyConstants.KIM_TYPE_ID, kimTypeId);
	        KimTypeImpl kimType = (KimTypeImpl)KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(KimTypeImpl.class, criteria);
	        roleDocumentForm.getRoleDocument().setKimType(kimType);
	        roleDocumentForm.getRoleDocument().setRoleTypeId(kimType.getKimTypeId());
	        roleDocumentForm.getRoleDocument().setRoleTypeName(kimType.getName());
	        roleDocumentForm.setMember(roleDocumentForm.getRoleDocument().getBlankMember());
        }
        String roleId = request.getParameter(KimConstants.PrimaryKeyConstants.ROLE_ID);
        if (StringUtils.isNotBlank(commandParam) && commandParam.equals(KEWConstants.INITIATE_COMMAND) 
				&& StringUtils.isNotBlank(roleId)) {
	        KimRole role = KIMServiceLocator.getRoleService().getRole(roleId);
			KIMServiceLocator.getUiDocumentService().loadRoleDoc(roleDoc, role);
			roleDocumentForm.setMember(roleDocumentForm.getRoleDocument().getBlankMember());
		} 
        if (StringUtils.isNotBlank(commandParam) && commandParam.equals(CHANGE_MEMBER_TYPE_CODE_METHOD_TO_CALL)){
	        roleDocumentForm.getMember().setMemberName("");
		}
		return forward;
    }
    
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
		// EBO does not have base path for lookup in rice
		// rice  has 'kr.url' as '/${env}/kr' while kfs is full base path
		// the returnlocalurl may have 'http' so, it should start from the beginning
		// this is kind of hack
		if (path.indexOf(request.getScheme()) != 0 && path.indexOf("lookup.do") > 0) {
			if (request.getServerPort() == 443) {
				path = request.getScheme() + "://" + request.getServerName()+path;
			} else {
				path = request.getScheme() + "://" + request.getServerName()+ ":" + request.getServerPort()+path;
			}
		}
		path = path.replace("identityManagementRoleDocument.do", "kim/identityManagementRoleDocument.do");
		forward.setPath(path);
		return forward;
	}

    public ActionForward addResponsibility(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementRoleDocumentForm roleDocumentForm = (IdentityManagementRoleDocumentForm) form;
        KimDocumentRoleResponsibility newResponsibility = roleDocumentForm.getResponsibility();
        if (KNSServiceLocator.getKualiRuleService().applyRules(new AddResponsibilityEvent("",roleDocumentForm.getRoleDocument(), newResponsibility))) {
        	Map<String, String> criteria = new HashMap<String, String>();
        	criteria.put(KimConstants.PrimaryKeyConstants.RESPONSIBILITY_ID, newResponsibility.getResponsibilityId());
        	KimResponsibilityImpl responsibilityImpl = (KimResponsibilityImpl)KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(KimResponsibilityImpl.class, criteria);
        	newResponsibility.setKimResponsibility(responsibilityImpl);
        	newResponsibility.setDocumentNumber(roleDocumentForm.getDocument().getDocumentNumber());
	        roleDocumentForm.getRoleDocument().addResponsibility(newResponsibility);
	        roleDocumentForm.setResponsibility(new KimDocumentRoleResponsibility());
	        roleDocumentForm.getRoleDocument().updateMembers(newResponsibility, roleDocumentForm);
        }
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    public ActionForward deleteResponsibility(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementRoleDocumentForm roleDocumentForm = (IdentityManagementRoleDocumentForm) form;
        roleDocumentForm.getRoleDocument().getResponsibilities().remove(getLineToDelete(request));
        roleDocumentForm.getRoleDocument().updateMembers(roleDocumentForm);
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    public ActionForward addPermission(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementRoleDocumentForm roleDocumentForm = (IdentityManagementRoleDocumentForm) form;
        KimDocumentRolePermission newPermission = roleDocumentForm.getPermission();
        if (KNSServiceLocator.getKualiRuleService().applyRules(new AddPermissionEvent("", roleDocumentForm.getRoleDocument(), newPermission))) {
        	Map<String, String> criteria = new HashMap<String, String>();
        	criteria.put(KimConstants.PrimaryKeyConstants.PERMISSION_ID, newPermission.getPermissionId());
        	KimPermissionImpl permissionImpl = (KimPermissionImpl)KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(KimPermissionImpl.class, criteria);
        	newPermission.setKimPermission(permissionImpl);
        	newPermission.setDocumentNumber(roleDocumentForm.getDocument().getDocumentNumber());
        	newPermission.setRoleId(roleDocumentForm.getRoleDocument().getRoleId());
	        roleDocumentForm.getRoleDocument().getPermissions().add(newPermission);
	        roleDocumentForm.setPermission(new KimDocumentRolePermission());
        }
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    public ActionForward addMember(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementRoleDocumentForm roleDocumentForm = (IdentityManagementRoleDocumentForm) form;
        KimDocumentRoleMember newMember = roleDocumentForm.getMember();
        if (KNSServiceLocator.getKualiRuleService().applyRules(new AddMemberEvent("", roleDocumentForm.getRoleDocument(), newMember))) {
        	newMember.setDocumentNumber(roleDocumentForm.getDocument().getDocumentNumber());
        	roleDocumentForm.getRoleDocument().addMember(newMember);
	        roleDocumentForm.setMember(roleDocumentForm.getRoleDocument().getBlankMember());
        }
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    public ActionForward deleteMember(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementRoleDocumentForm roleDocumentForm = (IdentityManagementRoleDocumentForm) form;
        roleDocumentForm.getRoleDocument().getMembers().remove(getLineToDelete(request));
        roleDocumentForm.setMember(roleDocumentForm.getRoleDocument().getBlankMember());
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    public ActionForward deletePermission(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementRoleDocumentForm roleDocumentForm = (IdentityManagementRoleDocumentForm) form;
        roleDocumentForm.getRoleDocument().getPermissions().remove(getLineToDelete(request));
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    public ActionForward addDelegation(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementRoleDocumentForm roleDocumentForm = (IdentityManagementRoleDocumentForm) form;
        RoleDocumentDelegation newDelegation = roleDocumentForm.getDelegation();
        if (KNSServiceLocator.getKualiRuleService().applyRules(new AddDelegationEvent("",roleDocumentForm.getRoleDocument(), newDelegation))) {
        	newDelegation.setDocumentNumber(roleDocumentForm.getDocument().getDocumentNumber());
	        roleDocumentForm.getRoleDocument().addDelegation(newDelegation);
	        roleDocumentForm.setDelegation(new RoleDocumentDelegation());
        }
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    public ActionForward deleteDelegation(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementRoleDocumentForm roleDocumentForm = (IdentityManagementRoleDocumentForm) form;
        roleDocumentForm.getRoleDocument().getDelegations().remove(getLineToDelete(request));
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    public ActionForward addDelegationMember(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementRoleDocumentForm roleDocumentForm = (IdentityManagementRoleDocumentForm) form;
        RoleDocumentDelegationMember newDelegationMember = roleDocumentForm.getDelegation().getMember();
        if (KNSServiceLocator.getKualiRuleService().applyRules(
        		new AddDelegationMemberEvent("", roleDocumentForm.getRoleDocument(), newDelegationMember))) {
        	newDelegationMember.setDocumentNumber(roleDocumentForm.getDocument().getDocumentNumber());
        	RoleDocumentDelegation delegation = roleDocumentForm.getRoleDocument().getDelegations().get(getSelectedLine(request));
        	delegation.getMembers().add(newDelegationMember);
	        roleDocumentForm.getDelegation().setMember(new RoleDocumentDelegationMember());
        }
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    public ActionForward deleteDelegationMember(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementRoleDocumentForm roleDocumentForm = (IdentityManagementRoleDocumentForm) form;
        roleDocumentForm.getRoleDocument().getMembers().remove(getLineToDelete(request));
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

	@Override
	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

        IdentityManagementRoleDocumentForm roleDocumentForm = (IdentityManagementRoleDocumentForm) form;
        IdentityManagementRoleDocument roleDoc = roleDocumentForm.getRoleDocument();
		return super.save(mapping, form, request, response);
	}

	private String getKimTypeServiceName (KimTypeImpl kimType) {
    	String serviceName = kimType.getKimTypeServiceName();
    	if (StringUtils.isBlank(serviceName)) {
    		serviceName = KimConstants.DEFAULT_KIM_TYPE_SERVICE;
    	}
    	return serviceName;

	}

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

}