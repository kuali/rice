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
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.impl.KimPermissionImpl;
import org.kuali.rice.kim.bo.role.impl.KimResponsibilityImpl;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleMember;
import org.kuali.rice.kim.bo.ui.KimDocumentRolePermission;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleResponsibility;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegationMember;
import org.kuali.rice.kim.document.IdentityManagementRoleDocument;
import org.kuali.rice.kim.lookup.KimTypeLookupableHelperServiceImpl;
import org.kuali.rice.kim.rule.event.ui.AddDelegationMemberEvent;
import org.kuali.rice.kim.rule.event.ui.AddMemberEvent;
import org.kuali.rice.kim.rule.event.ui.AddPermissionEvent;
import org.kuali.rice.kim.rule.event.ui.AddResponsibilityEvent;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kim.web.struts.form.IdentityManagementRoleDocumentForm;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.RiceKeyConstants;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class IdentityManagementRoleDocumentAction extends IdentityManagementDocumentActionBase {

	public static final String CHANGE_MEMBER_TYPE_CODE_METHOD_TO_CALL = "changeMemberTypeCode";

    @Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
        String methodToCall = findMethodToCall(form, request);
		ActionForward forward;
        IdentityManagementRoleDocumentForm roleDocumentForm = (IdentityManagementRoleDocumentForm) form;
		String kimTypeId = request.getParameter(KimConstants.PrimaryKeyConstants.KIM_TYPE_ID);
		KimTypeImpl kimType = null;
        if(KNSConstants.DOC_HANDLER_METHOD.equals(methodToCall) && kimTypeId!=null){
	        Map<String, String> criteria = new HashMap<String, String>();
	        criteria.put(KimConstants.PrimaryKeyConstants.KIM_TYPE_ID, kimTypeId);
	        kimType = (KimTypeImpl)KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(KimTypeImpl.class, criteria);
	        if(kimType == null)
	        	throw new IllegalArgumentException("Kim type could not be found for kim type id: "+kimTypeId);
	        roleDocumentForm.setKimType(kimType);
	        roleDocumentForm.getRoleDocument().setKimType(kimType);
	        roleDocumentForm.setDocTypeName(roleDocumentForm.getRoleDocument().getWorkflowDocumentTypeName());
	    }
		if(KNSConstants.PARAM_MAINTENANCE_VIEW_MODE_INQUIRY.equals(methodToCall))
        	forward = mapping.findForward(KNSConstants.PARAM_MAINTENANCE_VIEW_MODE_INQUIRY);
        else {
        	forward = super.execute(mapping, form, request, response);
			if(KNSConstants.DOC_HANDLER_METHOD.equals(methodToCall) && kimTypeId!=null){
		        roleDocumentForm.getRoleDocument().setKimType(kimType);
		        roleDocumentForm.getRoleDocument().setRoleTypeId(kimType.getKimTypeId());
		        roleDocumentForm.getRoleDocument().setRoleTypeName(kimType.getName());
		        roleDocumentForm.getRoleDocument().getRoleId();
		        roleDocumentForm.setMember(roleDocumentForm.getRoleDocument().getBlankMember());
		        roleDocumentForm.setDelegationMember(roleDocumentForm.getRoleDocument().getBlankDelegationMember());
			}
        }
		String commandParam = request.getParameter(KNSConstants.PARAMETER_COMMAND);
        String roleId = request.getParameter(KimConstants.PrimaryKeyConstants.ROLE_ID);
        if (StringUtils.isNotBlank(commandParam) && commandParam.equals(KEWConstants.INITIATE_COMMAND)
				&& StringUtils.isNotBlank(roleId)) {
	        Role role = KIMServiceLocator.getRoleService().getRole(roleId);
	        getUiDocumentService().loadRoleDoc(roleDocumentForm.getRoleDocument(), role);
			roleDocumentForm.setMember(roleDocumentForm.getRoleDocument().getBlankMember());
			roleDocumentForm.setDelegationMember(roleDocumentForm.getRoleDocument().getBlankDelegationMember());
        	if(!KNSConstants.PARAM_MAINTENANCE_VIEW_MODE_INQUIRY.equals(methodToCall))
        		roleDocumentForm.setCanAssignRole(validAssignRole(roleDocumentForm.getRoleDocument()));
        	if(KimTypeLookupableHelperServiceImpl.hasDerivedRoleTypeService(roleDocumentForm.getRoleDocument().getKimType()))
        		roleDocumentForm.setCanModifyAssignees(false);
        }
        if (StringUtils.isNotBlank(commandParam) && commandParam.equals(CHANGE_MEMBER_TYPE_CODE_METHOD_TO_CALL)){
	        roleDocumentForm.getMember().setMemberName("");
		}
		((KualiDocumentFormBase) form).setErrorMapFromPreviousRequest(GlobalVariables.getErrorMap());
		return forward;
    }

	/***
	 * @see org.kuali.rice.kim.web.struts.action.IdentityManagementDocumentActionBase#getActionName()
	 */
	public String getActionName(){
		return KimConstants.KimUIConstants.KIM_ROLE_DOCUMENT_ACTION;
	}

	private boolean validAssignRole(IdentityManagementRoleDocument document){
        boolean rulePassed = true;
        Map<String,String> additionalPermissionDetails = new HashMap<String,String>();
        additionalPermissionDetails.put(KimAttributes.NAMESPACE_CODE, document.getRoleNamespace());
        additionalPermissionDetails.put(KimAttributes.ROLE_NAME, document.getRoleName());
		if (!getDocumentHelperService().getDocumentAuthorizer(document).isAuthorizedByTemplate(
				document,
				KimConstants.NAMESPACE_CODE,
				KimConstants.PermissionTemplateNames.ASSIGN_ROLE,
				GlobalVariables.getUserSession().getPrincipalId(),
				additionalPermissionDetails, null)){
            rulePassed = false;
		}
		return rulePassed;
	}

    public ActionForward addResponsibility(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementRoleDocumentForm roleDocumentForm = (IdentityManagementRoleDocumentForm) form;
        KimDocumentRoleResponsibility newResponsibility = roleDocumentForm.getResponsibility();
        if(newResponsibility!=null && StringUtils.isNotBlank(newResponsibility.getResponsibilityId())){
        	Map<String, String> criteria = new HashMap<String, String>();
        	criteria.put(KimConstants.PrimaryKeyConstants.RESPONSIBILITY_ID, newResponsibility.getResponsibilityId());
        	KimResponsibilityImpl responsibilityImpl = (KimResponsibilityImpl)KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(KimResponsibilityImpl.class, criteria);
        	newResponsibility.setKimResponsibility(responsibilityImpl);
        }

        if (KNSServiceLocator.getKualiRuleService().applyRules(new AddResponsibilityEvent("",roleDocumentForm.getRoleDocument(), newResponsibility))) {
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
        if(newPermission!=null && StringUtils.isNotBlank(newPermission.getPermissionId())){
	    	Map<String, String> criteria = new HashMap<String, String>();
	    	criteria.put(KimConstants.PrimaryKeyConstants.PERMISSION_ID, newPermission.getPermissionId());
	    	KimPermissionImpl kimPermissionImpl = (KimPermissionImpl)KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(KimPermissionImpl.class, criteria);
	    	newPermission.setKimPermission(kimPermissionImpl);
        }
        if (KNSServiceLocator.getKualiRuleService().applyRules(new AddPermissionEvent("", roleDocumentForm.getRoleDocument(), newPermission))) {
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
        if(checkKimDocumentRoleMember(newMember) &&
        		KNSServiceLocator.getKualiRuleService().applyRules(new AddMemberEvent("", roleDocumentForm.getRoleDocument(), newMember))){
        	newMember.setDocumentNumber(roleDocumentForm.getDocument().getDocumentNumber());
        	roleDocumentForm.getRoleDocument().addMember(newMember);
	        roleDocumentForm.setMember(roleDocumentForm.getRoleDocument().getBlankMember());
        }
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    private boolean checkKimDocumentRoleMember(KimDocumentRoleMember newMember){
        if(StringUtils.isBlank(newMember.getMemberTypeCode()) || StringUtils.isBlank(newMember.getMemberId())){
        	GlobalVariables.getErrorMap().putError("document.member.memberId", RiceKeyConstants.ERROR_EMPTY_ENTRY,
        			new String[] {"Member Type Code and Member ID"});
        	return false;
		}
    	BusinessObject member = getUiDocumentService().getMember(newMember.getMemberTypeCode(), newMember.getMemberId());
        if(member==null){
        	GlobalVariables.getErrorMap().putError("document.member.memberId", RiceKeyConstants.ERROR_MEMBERID_MEMBERTYPE_MISMATCH,
        			new String[] {newMember.getMemberId()});
        	return false;
		}
        newMember.setMemberName(getUiDocumentService().getMemberName(newMember.getMemberTypeCode(), member));
        newMember.setMemberNamespaceCode(getUiDocumentService().getMemberNamespaceCode(newMember.getMemberTypeCode(), member));
        return true;
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

    private boolean checkDelegationMember(RoleDocumentDelegationMember newMember){
        if(StringUtils.isBlank(newMember.getMemberTypeCode()) || StringUtils.isBlank(newMember.getMemberId())){
        	GlobalVariables.getErrorMap().putError("document.delegationMember.memberId", RiceKeyConstants.ERROR_EMPTY_ENTRY,
        			new String[] {"Member Type Code and Member ID"});
        	return false;
		}
    	BusinessObject member = getUiDocumentService().getMember(newMember.getMemberTypeCode(), newMember.getMemberId());
        if(member==null){
        	GlobalVariables.getErrorMap().putError("document.delegationMember.memberId", RiceKeyConstants.ERROR_MEMBERID_MEMBERTYPE_MISMATCH,
        			new String[] {newMember.getMemberId()});
        	return false;
		}
        newMember.setMemberName(getUiDocumentService().getMemberName(newMember.getMemberTypeCode(), member));
        newMember.setMemberNamespaceCode(getUiDocumentService().getMemberNamespaceCode(newMember.getMemberTypeCode(), member));
        return true;
    }

    public ActionForward addDelegationMember(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementRoleDocumentForm roleDocumentForm = (IdentityManagementRoleDocumentForm) form;
        RoleDocumentDelegationMember newDelegationMember = roleDocumentForm.getDelegationMember();
        if (checkDelegationMember(newDelegationMember) && KNSServiceLocator.getKualiRuleService().applyRules(
        		new AddDelegationMemberEvent("", roleDocumentForm.getRoleDocument(), newDelegationMember))) {
        	newDelegationMember.setDocumentNumber(roleDocumentForm.getDocument().getDocumentNumber());
        	roleDocumentForm.getRoleDocument().addDelegationMember(newDelegationMember);
	        roleDocumentForm.setDelegationMember(roleDocumentForm.getRoleDocument().getBlankDelegationMember());
        }
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    public ActionForward deleteDelegationMember(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementRoleDocumentForm roleDocumentForm = (IdentityManagementRoleDocumentForm) form;
        roleDocumentForm.getRoleDocument().getDelegationMembers().remove(getLineToDelete(request));
        roleDocumentForm.setDelegationMember(roleDocumentForm.getRoleDocument().getBlankDelegationMember());
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

	@Override
	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

        IdentityManagementRoleDocumentForm roleDocumentForm = (IdentityManagementRoleDocumentForm) form;
        IdentityManagementRoleDocument roleDoc = roleDocumentForm.getRoleDocument();
		return super.save(mapping, form, request, response);
	}

}