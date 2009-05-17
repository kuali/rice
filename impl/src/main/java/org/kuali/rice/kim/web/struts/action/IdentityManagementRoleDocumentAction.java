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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.impl.RoleImpl;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.bo.role.impl.KimResponsibilityImpl;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleMember;
import org.kuali.rice.kim.bo.ui.KimDocumentRolePermission;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleQualifier;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleResponsibility;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegationMember;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegationMemberQualifier;
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
import org.kuali.rice.kns.util.RiceKeyConstants;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kns.web.struts.form.KualiTableRenderFormMetadata;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class IdentityManagementRoleDocumentAction extends IdentityManagementDocumentActionBase {

	private List<String> methodToCallToUncheckedList = new ArrayList<String>();
	{
		methodToCallToUncheckedList.add(CHANGE_DEL_ROLE_MEMBER_METHOD_TO_CALL);
		methodToCallToUncheckedList.add(CHANGE_MEMBER_TYPE_CODE_METHOD_TO_CALL);
	}
	/**
	 * This constructs a ...
	 * 
	 */
	public IdentityManagementRoleDocumentAction() {
		super();
		for(String methodToCallToUncheck: methodToCallToUncheckedList)
			addMethodToCallToUncheckedList(methodToCallToUncheck);
	}

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		ActionForward forward = null;
        IdentityManagementRoleDocumentForm roleDocumentForm = (IdentityManagementRoleDocumentForm) form;
        if(methodToCallToUncheckedList.contains(roleDocumentForm.getRoleCommand()))
        	forward = dispatchMethod(mapping, form, request, response, roleDocumentForm.getRoleCommand());
        if ( roleDocumentForm.getRoleId() == null ) {
            String roleId = request.getParameter(KimConstants.PrimaryKeyConstants.ROLE_ID);
        	roleDocumentForm.setRoleId(roleId);
        }
		String kimTypeId = request.getParameter(KimConstants.PrimaryKeyConstants.KIM_TYPE_ID);
		// TODO: move this into the UI service - action should not be making ORM-layer calls
		setKimType(kimTypeId, roleDocumentForm);

		KualiTableRenderFormMetadata memberTableMetadata = roleDocumentForm.getMemberTableMetadata();
		if (roleDocumentForm.getMemberRows() != null) {
		    memberTableMetadata.jumpToPage(memberTableMetadata.getViewedPageNumber(), roleDocumentForm.getMemberRows().size(), roleDocumentForm.getRecordsPerPage());
		}
        forward = super.execute(mapping, roleDocumentForm, request, response);

		roleDocumentForm.setCanAssignRole(validAssignRole(roleDocumentForm.getRoleDocument()));
		if(KimTypeLookupableHelperServiceImpl.hasDerivedRoleTypeService(roleDocumentForm.getRoleDocument().getKimType())) {
			roleDocumentForm.setCanModifyAssignees(false);
		}
		GlobalVariables.getUserSession().addObject(KimConstants.KimUIConstants.KIM_ROLE_DOCUMENT_SHORT_KEY, roleDocumentForm.getRoleDocument());
		return forward;
    }
    
    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#loadDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase form)
    		throws WorkflowException {
    	super.loadDocument(form);

    	IdentityManagementRoleDocumentForm roleDocumentForm = (IdentityManagementRoleDocumentForm) form;
    	if(roleDocumentForm.getRoleDocument().getKimType()==null)
    		roleDocumentForm.getRoleDocument().setKimType(roleDocumentForm.getKimType());
        roleDocumentForm.setMember(roleDocumentForm.getRoleDocument().getBlankMember());
        roleDocumentForm.setDelegationMember(roleDocumentForm.getRoleDocument().getBlankDelegationMember());

		KualiTableRenderFormMetadata memberTableMetadata = roleDocumentForm.getMemberTableMetadata();
		if (roleDocumentForm.getMemberRows() != null) {
		    memberTableMetadata.jumpToFirstPage(roleDocumentForm.getMemberRows().size(), roleDocumentForm.getRecordsPerPage());
		}
    }
    
    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase form)
    		throws WorkflowException {
    	super.createDocument(form);
    	IdentityManagementRoleDocumentForm roleDocumentForm = (IdentityManagementRoleDocumentForm) form;

    	if ( roleDocumentForm.getRoleId() == null ) {
    		roleDocumentForm.getRoleDocument().setKimType(roleDocumentForm.getKimType());
    		roleDocumentForm.getRoleDocument().initializeDocumentForNewRole();
    		roleDocumentForm.setRoleId( roleDocumentForm.getRoleDocument().getRoleId() );
            Map<String, String> criteria = new HashMap<String, String>();
            criteria.put(KimConstants.PrimaryKeyConstants.KIM_TYPE_ID, roleDocumentForm.getRoleDocument().getRoleTypeId() );
            roleDocumentForm.setKimType((KimTypeImpl)KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(KimTypeImpl.class, criteria));
    	} else {
    		loadRoleIntoDocument( roleDocumentForm.getRoleId(), roleDocumentForm );
    	}
        
    	roleDocumentForm.setMember(roleDocumentForm.getRoleDocument().getBlankMember());
        roleDocumentForm.setDelegationMember(roleDocumentForm.getRoleDocument().getBlankDelegationMember());

		KualiTableRenderFormMetadata memberTableMetadata = roleDocumentForm.getMemberTableMetadata();
		if (roleDocumentForm.getMemberRows() != null) {
		    memberTableMetadata.jumpToFirstPage(roleDocumentForm.getMemberRows().size(), roleDocumentForm.getRecordsPerPage());
		}
    }

    private void setKimType(String kimTypeId, IdentityManagementRoleDocumentForm roleDocumentForm){
		if ( StringUtils.isNotBlank(kimTypeId) ) {
	        Map<String, String> criteria = new HashMap<String, String>();
	        criteria.put(KimConstants.PrimaryKeyConstants.KIM_TYPE_ID, kimTypeId);
	        roleDocumentForm.setKimType((KimTypeImpl)KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(KimTypeImpl.class, criteria));
		} else if ( StringUtils.isNotBlank( roleDocumentForm.getRoleDocument().getRoleTypeId() ) ) {
	        Map<String, String> criteria = new HashMap<String, String>();
	        criteria.put(KimConstants.PrimaryKeyConstants.KIM_TYPE_ID, kimTypeId);
	        roleDocumentForm.setKimType((KimTypeImpl)KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(KimTypeImpl.class, criteria));
		}
    }
    
    private void loadRoleIntoDocument( String roleId, IdentityManagementRoleDocumentForm roleDocumentForm){
        KimRoleInfo role = KIMServiceLocator.getRoleService().getRole(roleId);
        getUiDocumentService().loadRoleDoc(roleDocumentForm.getRoleDocument(), role);
    }
    
	/***
	 * @see org.kuali.rice.kim.web.struts.action.IdentityManagementDocumentActionBase#getActionName()
	 */
	public String getActionName(){
		return KimConstants.KimUIConstants.KIM_ROLE_DOCUMENT_ACTION;
	}

	private boolean validAssignRole(IdentityManagementRoleDocument document){
        boolean rulePassed = true;
        if(StringUtils.isNotEmpty(document.getRoleNamespace())){
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
        }
		return rulePassed;
	}
	
	public ActionForward changeMemberTypeCode(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		IdentityManagementRoleDocumentForm roleDocumentForm = (IdentityManagementRoleDocumentForm) form;
        roleDocumentForm.getMember().setMemberName("");
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
	}

	public ActionForward changeDelegationRoleMember(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		IdentityManagementRoleDocumentForm roleDocumentForm = (IdentityManagementRoleDocumentForm) form;
		KimDocumentRoleMember roleMember = roleDocumentForm.getRoleDocument().getMember(roleDocumentForm.getDelegationMember().getRoleMemberId());
		if(roleMember!=null){
			RoleDocumentDelegationMemberQualifier delegationMemberQualifier;
			for(KimDocumentRoleQualifier roleQualifier: roleMember.getQualifiers()){
				delegationMemberQualifier = roleDocumentForm.getDelegationMember().getQualifier(roleQualifier.getKimAttrDefnId());
				delegationMemberQualifier.setAttrVal(roleQualifier.getAttrVal());
			}
		}
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
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
	        roleDocumentForm.getRoleDocument().updateMembers(newResponsibility);
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
	        roleDocumentForm.getMemberTableMetadata().jumpToLastPage(roleDocumentForm.getMemberRows().size(), roleDocumentForm.getRecordsPerPage());
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
        if(StringUtils.equals(newMember.getMemberTypeCode(), KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE) && !validateRole((RoleImpl)member, "document.member.memberId", "Role")){
        	return false;
        }

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

}