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
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.core.util.RiceKeyConstants;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.api.entity.principal.Principal;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.bo.Role;


import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleMember;
import org.kuali.rice.kim.bo.ui.KimDocumentRolePermission;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleQualifier;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleResponsibility;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegationMember;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegationMemberQualifier;
import org.kuali.rice.kim.document.IdentityManagementRoleDocument;
import org.kuali.rice.kim.impl.responsibility.AddResponsibilityEvent;
import org.kuali.rice.kim.impl.responsibility.ResponsibilityBo;
import org.kuali.rice.kim.lookup.KimTypeLookupableHelperServiceImpl;
import org.kuali.rice.kim.rule.event.ui.AddDelegationMemberEvent;
import org.kuali.rice.kim.rule.event.ui.AddMemberEvent;
import org.kuali.rice.kim.rule.event.ui.AddPermissionEvent;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kim.web.struts.form.IdentityManagementRoleDocumentForm;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KNSServiceLocatorWeb;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kns.web.struts.form.KualiTableRenderFormMetadata;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class IdentityManagementRoleDocumentAction extends IdentityManagementDocumentActionBase {

	public static final String CHANGE_DEL_ROLE_MEMBER_METHOD_TO_CALL = "changeDelegationRoleMember";
	public static final String SWITCH_TO_ROLE_MEMBER_METHOD_TO_CALL = "jumpToRoleMember";
	public static final String REMOVE_AFFECTED_DELEGATES_QUESTION_ID = "RemoveAffectedDelegates";

	protected List<String> methodToCallToUncheckedList = new ArrayList<String>();
	{
		methodToCallToUncheckedList.add(CHANGE_DEL_ROLE_MEMBER_METHOD_TO_CALL);
		methodToCallToUncheckedList.add(CHANGE_MEMBER_TYPE_CODE_METHOD_TO_CALL);
		methodToCallToUncheckedList.add(CHANGE_NAMESPACE_METHOD_TO_CALL);
		methodToCallToUncheckedList.add(SWITCH_TO_ROLE_MEMBER_METHOD_TO_CALL);
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

    public ActionForward sort(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementRoleDocumentForm roleDocumentForm = (IdentityManagementRoleDocumentForm) form;
        this.createDocument(roleDocumentForm);

        // mimic loading the document; consider calling the loadDocument method instead
        Document document = roleDocumentForm.getDocument();
        KualiWorkflowDocument workflowDocument = roleDocumentForm.getDocument().getDocumentHeader().getWorkflowDocument();
        roleDocumentForm.populateHeaderFields(workflowDocument);
        roleDocumentForm.setDocId(document.getDocumentNumber());
        roleDocumentForm.setCanAssignRole(validAssignRole(roleDocumentForm.getRoleDocument()));

        if (KimTypeLookupableHelperServiceImpl.hasDerivedRoleTypeService(roleDocumentForm.getRoleDocument().getKimType())) {
            roleDocumentForm.setCanModifyAssignees(false);
        }
        GlobalVariables.getUserSession().addObject(KimConstants.KimUIConstants.KIM_ROLE_DOCUMENT_SHORT_KEY, roleDocumentForm.getRoleDocument());
        return refresh(mapping, roleDocumentForm, request, response);
    }

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementRoleDocumentForm roleDocumentForm = (IdentityManagementRoleDocumentForm) form;
        if ( roleDocumentForm.getRoleId() == null ) {
            String roleId = request.getParameter(KimConstants.PrimaryKeyConstants.ROLE_ID);
        	roleDocumentForm.setRoleId(roleId);
        }
        
		String kimTypeId = request.getParameter(KimConstants.PrimaryKeyConstants.KIM_TYPE_ID);
		// TODO: move this into the UI service - action should not be making ORM-layer calls
		setKimType(kimTypeId, roleDocumentForm);

		KualiTableRenderFormMetadata memberTableMetadata = roleDocumentForm.getMemberTableMetadata();
		if (roleDocumentForm.getRoleDocument()!=null && roleDocumentForm.getMemberRows() != null) {
			memberTableMetadata.jumpToPage(memberTableMetadata.getViewedPageNumber(), roleDocumentForm.getMemberRows().size(), roleDocumentForm.getRecordsPerPage());
		}

		// KULRICE-4762: active delegates of "inactivated" role members cause validation problems
		ActionForward forward = promptForAffectedDelegates(mapping, form, request, response,
				roleDocumentForm);
		// if we need to prompt the user due to affected delegates, do so:
		if (forward != null) return forward;

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
    	setKimType(roleDocumentForm.getRoleDocument().getRoleTypeId(), roleDocumentForm);
    	getUiDocumentService().setDelegationMembersInDocument( roleDocumentForm.getRoleDocument() );

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
            roleDocumentForm.setKimType(KimApiServiceLocator.getKimTypeInfoService().getKimType(roleDocumentForm.getRoleDocument().getRoleTypeId()));
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

    protected void setKimType(String kimTypeId, IdentityManagementRoleDocumentForm roleDocumentForm){
		if ( StringUtils.isNotBlank(kimTypeId) ) {
            roleDocumentForm.setKimType(KimApiServiceLocator.getKimTypeInfoService().getKimType(kimTypeId));
            if (roleDocumentForm.getRoleDocument() != null) {
            	roleDocumentForm.getRoleDocument().setKimType(roleDocumentForm.getKimType());
            }
		} else if ( roleDocumentForm.getRoleDocument() != null && StringUtils.isNotBlank( roleDocumentForm.getRoleDocument().getRoleTypeId() ) ) {
            roleDocumentForm.setKimType(KimApiServiceLocator.getKimTypeInfoService().getKimType(
            		roleDocumentForm.getRoleDocument().getRoleTypeId()));
        	roleDocumentForm.getRoleDocument().setKimType(roleDocumentForm.getKimType());
		}
    }

    protected void loadRoleIntoDocument( String roleId, IdentityManagementRoleDocumentForm roleDocumentForm){
        KimRoleInfo role = KimApiServiceLocator.getRoleService().getRole(roleId);
        roleDocumentForm.getRoleDocument().setMemberMetaDataTypeToSort(roleDocumentForm.getMemberTableMetadata().getColumnToSortIndex());
        getUiDocumentService().loadRoleDoc(roleDocumentForm.getRoleDocument(), role);
    }

	/***
	 * @see org.kuali.rice.kim.web.struts.action.IdentityManagementDocumentActionBase#getActionName()
	 */
	public String getActionName(){
		return KimConstants.KimUIConstants.KIM_ROLE_DOCUMENT_ACTION;
	}

	protected boolean validAssignRole(IdentityManagementRoleDocument document){
        boolean rulePassed = true;
        if(StringUtils.isNotEmpty(document.getRoleNamespace())){
	        Map<String,String> additionalPermissionDetails = new HashMap<String,String>();
	        additionalPermissionDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE, document.getRoleNamespace());
	        additionalPermissionDetails.put(KimConstants.AttributeConstants.ROLE_NAME, document.getRoleName());
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
        roleDocumentForm.getMember().setMemberId("");
        return refresh(mapping, roleDocumentForm, request, response);
	}

	public ActionForward changeDelegationMemberTypeCode(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		IdentityManagementRoleDocumentForm roleDocumentForm = (IdentityManagementRoleDocumentForm) form;
		KimDocumentRoleMember roleMember = roleDocumentForm.getRoleDocument().getMember(roleDocumentForm.getDelegationMember().getRoleMemberId());
		if(roleMember!=null){
			RoleDocumentDelegationMemberQualifier delegationMemberQualifier;
			for(KimDocumentRoleQualifier roleQualifier: roleMember.getQualifiers()){
				delegationMemberQualifier = roleDocumentForm.getDelegationMember().getQualifier(roleQualifier.getKimAttrDefnId());
				delegationMemberQualifier.setAttrVal(roleQualifier.getAttrVal());
			}
		}
        return refresh(mapping, roleDocumentForm, request, response);
	}

    public ActionForward addResponsibility(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementRoleDocumentForm roleDocumentForm = (IdentityManagementRoleDocumentForm) form;
        KimDocumentRoleResponsibility newResponsibility = roleDocumentForm.getResponsibility();
        if(newResponsibility!=null && StringUtils.isNotBlank(newResponsibility.getResponsibilityId())){
        	Map<String, String> criteria = new HashMap<String, String>();
        	criteria.put(KimConstants.PrimaryKeyConstants.RESPONSIBILITY_ID, newResponsibility.getResponsibilityId());
        	ResponsibilityBo responsibilityImpl = KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(ResponsibilityBo.class, criteria);
        	newResponsibility.setKimResponsibility(responsibilityImpl);
        }

        if (KNSServiceLocatorWeb.getKualiRuleService().applyRules(new AddResponsibilityEvent("",roleDocumentForm.getRoleDocument(), newResponsibility))) {
            if (newResponsibility != null) {
                newResponsibility.setDocumentNumber(roleDocumentForm.getDocument().getDocumentNumber());
            }
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
        if (KNSServiceLocatorWeb.getKualiRuleService().applyRules(new AddPermissionEvent("", roleDocumentForm.getRoleDocument(), newPermission))) {
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

        //See if possible to add with just Group Details filled in (not returned from lookup)
        if (StringUtils.isEmpty(newMember.getMemberId())
        		&& StringUtils.isNotEmpty(newMember.getMemberName())
        		&& StringUtils.isNotEmpty(newMember.getMemberNamespaceCode())
        		&& StringUtils.equals(newMember.getMemberTypeCode(), KimConstants.KimGroupMemberTypes.GROUP_MEMBER_TYPE)) {
        	Group tempGroup = KimApiServiceLocator.getIdentityManagementService().getGroupByName(newMember.getMemberNamespaceCode(), newMember.getMemberName());
        	if (tempGroup != null) {
        		newMember.setMemberId(tempGroup.getId());
        	}
        }

        //See if possible to grab details for Principal
        if (StringUtils.isEmpty(newMember.getMemberId())
        		&& StringUtils.isNotEmpty(newMember.getMemberName())
        		&& StringUtils.equals(newMember.getMemberTypeCode(), KimConstants.KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE)) {
        	Principal principal = KimApiServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName(newMember.getMemberName());
        	if (principal != null) {
        		newMember.setMemberId(principal.getPrincipalId());
        	}
        }
        if(checkKimDocumentRoleMember(newMember) &&
        		KNSServiceLocatorWeb.getKualiRuleService().applyRules(new AddMemberEvent("", roleDocumentForm.getRoleDocument(), newMember))){
        	newMember.setDocumentNumber(roleDocumentForm.getDocument().getDocumentNumber());
        	roleDocumentForm.getRoleDocument().addMember(newMember);
	        roleDocumentForm.setMember(roleDocumentForm.getRoleDocument().getBlankMember());
	        roleDocumentForm.getMemberTableMetadata().jumpToLastPage(roleDocumentForm.getMemberRows().size(), roleDocumentForm.getRecordsPerPage());
        }
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    protected boolean checkKimDocumentRoleMember(KimDocumentRoleMember newMember){
    	boolean memberExists = false;
        String memberName = null;
        String memberNamespace = null;

        if(StringUtils.isBlank(newMember.getMemberTypeCode()) || StringUtils.isBlank(newMember.getMemberId())){
        	GlobalVariables.getMessageMap().putError("document.member.memberId", RiceKeyConstants.ERROR_EMPTY_ENTRY,
        			new String[] {"Member Type Code and Member ID"});
        	return false;
		}

        if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(newMember.getMemberTypeCode())){
        	Principal pi = this.getIdentityService().getPrincipal(newMember.getMemberId());
        	if(pi != null){
        		memberExists = true;
        		memberName = pi.getPrincipalName();
        		memberNamespace = "";
        	}
        }else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(newMember.getMemberTypeCode())){
        	Group gi = KimApiServiceLocator.getGroupService().getGroup(newMember.getMemberId());
        	if(gi != null){
        		memberExists = true;
        		memberName = gi.getName();
        		memberNamespace = gi.getNamespaceCode();
        	}
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(newMember.getMemberTypeCode())){
        	Role ri = KimApiServiceLocator.getRoleService().getRole(newMember.getMemberId());
        	if(!validateRole(newMember.getMemberId(), ri, "document.member.memberId", "Role")){
        	return false;
    		}else{
    			memberExists = true;
           		memberName = ri.getRoleName();
           		memberNamespace = ri.getNamespaceCode();
        }
        }

        if(!memberExists){
        	GlobalVariables.getMessageMap().putError("document.member.memberId", RiceKeyConstants.ERROR_MEMBERID_MEMBERTYPE_MISMATCH,
        			new String[] {newMember.getMemberId()});
        	return false;
		}
        newMember.setMemberName(memberName);
        newMember.setMemberNamespaceCode(memberNamespace);
        return true;
    }

    public ActionForward deleteMember(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementRoleDocumentForm roleDocumentForm = (IdentityManagementRoleDocumentForm) form;
        KimDocumentRoleMember inactivatedRoleMember = roleDocumentForm.getRoleDocument().getMembers().get(getLineToDelete(request));

		// KULRICE-4762: active delegates of "inactivated" role members cause validation problems
		ActionForward forward = promptForAffectedDelegates(mapping, form, request, response,
				roleDocumentForm, /* we haven't actually inactivated them yet, so specify them here */ inactivatedRoleMember);
		// if we need to prompt the user due to affected delegates, do so:
		if (forward != null) return forward;

        Calendar cal = Calendar.getInstance();
        inactivatedRoleMember.setActiveToDate(new Timestamp(cal.getTimeInMillis()));

        roleDocumentForm.getRoleDocument().getMembers().set(getLineToDelete(request), inactivatedRoleMember);
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    public ActionForward deletePermission(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementRoleDocumentForm roleDocumentForm = (IdentityManagementRoleDocumentForm) form;
        roleDocumentForm.getRoleDocument().getPermissions().remove(getLineToDelete(request));
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    protected boolean checkDelegationMember(RoleDocumentDelegationMember newMember){
        if(StringUtils.isBlank(newMember.getMemberTypeCode()) || StringUtils.isBlank(newMember.getMemberId())){
        	GlobalVariables.getMessageMap().putError("document.delegationMember.memberId", RiceKeyConstants.ERROR_EMPTY_ENTRY,
        			new String[] {"Member Type Code and Member ID"});
        	return false;
		}
    	if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(newMember.getMemberTypeCode())){
        	Principal principalInfo = getIdentityManagementService().getPrincipal(newMember.getMemberId());
        	if (principalInfo == null) {
        		GlobalVariables.getMessageMap().putError("document.delegationMember.memberId", RiceKeyConstants.ERROR_MEMBERID_MEMBERTYPE_MISMATCH,
            			new String[] {newMember.getMemberId()});
            	return false;
        	}
        	else {
        		newMember.setMemberName(principalInfo.getPrincipalName());
        	}
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(newMember.getMemberTypeCode())){
        	Group groupInfo = null;
        	groupInfo = getIdentityManagementService().getGroup(newMember.getMemberId());
        	if (groupInfo == null) {
        		GlobalVariables.getMessageMap().putError("document.delegationMember.memberId", RiceKeyConstants.ERROR_MEMBERID_MEMBERTYPE_MISMATCH,
            			new String[] {newMember.getMemberId()});
            	return false;
        	}
        	else {
        		newMember.setMemberName(groupInfo.getName());
                newMember.setMemberNamespaceCode(groupInfo.getNamespaceCode());
        	}
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(newMember.getMemberTypeCode())){
        	KimRoleInfo roleInfo = null;
        	roleInfo = KimApiServiceLocator.getRoleService().getRole(newMember.getMemberId());
        	if (roleInfo == null) {
        		GlobalVariables.getMessageMap().putError("document.delegationMember.memberId", RiceKeyConstants.ERROR_MEMBERID_MEMBERTYPE_MISMATCH,
            			new String[] {newMember.getMemberId()});
            	return false;
        	}
        	else {
        		newMember.setMemberName(roleInfo.getRoleName());
                newMember.setMemberNamespaceCode(roleInfo.getNamespaceCode());
        	}
		}
        return true;
    }

    public ActionForward addDelegationMember(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementRoleDocumentForm roleDocumentForm = (IdentityManagementRoleDocumentForm) form;
        RoleDocumentDelegationMember newDelegationMember = roleDocumentForm.getDelegationMember();

      //See if possible to add with just Group Details filled in (not returned from lookup)
        if (StringUtils.isEmpty(newDelegationMember.getMemberId())
        		&& StringUtils.isNotEmpty(newDelegationMember.getMemberName())
        		&& StringUtils.isNotEmpty(newDelegationMember.getMemberNamespaceCode())
        		&& StringUtils.equals(newDelegationMember.getMemberTypeCode(), KimConstants.KimGroupMemberTypes.GROUP_MEMBER_TYPE)) {
        	Group tempGroup = KimApiServiceLocator.getIdentityManagementService().getGroupByName(newDelegationMember.getMemberNamespaceCode(), newDelegationMember.getMemberName());
        	if (tempGroup != null) {
        		newDelegationMember.setMemberId(tempGroup.getId());
        	}
        }

        //See if possible to grab details for Principal
        if (StringUtils.isEmpty(newDelegationMember.getMemberId())
        		&& StringUtils.isNotEmpty(newDelegationMember.getMemberName())
        		&& StringUtils.equals(newDelegationMember.getMemberTypeCode(), KimConstants.KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE)) {
        	Principal principal = KimApiServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName(newDelegationMember.getMemberName());
        	if (principal != null) {
        		newDelegationMember.setMemberId(principal.getPrincipalId());
        	}
        }

        if (checkDelegationMember(newDelegationMember) && KNSServiceLocatorWeb.getKualiRuleService().applyRules(
        		new AddDelegationMemberEvent("", roleDocumentForm.getRoleDocument(), newDelegationMember))) {
        	newDelegationMember.setDocumentNumber(roleDocumentForm.getDocument().getDocumentNumber());
        	roleDocumentForm.getRoleDocument().addDelegationMember(newDelegationMember);
	        roleDocumentForm.setDelegationMember(roleDocumentForm.getRoleDocument().getBlankDelegationMember());
        }
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    public ActionForward deleteDelegationMember(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementRoleDocumentForm roleDocumentForm = (IdentityManagementRoleDocumentForm) form;
        // Removing, not inactivating -- is this what we really want?
        roleDocumentForm.getRoleDocument().getDelegationMembers().remove(getLineToDelete(request));
        roleDocumentForm.setDelegationMember(roleDocumentForm.getRoleDocument().getBlankDelegationMember());
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiTableRenderAction#switchToPage(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward jumpToRoleMember(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementRoleDocumentForm idmForm = (IdentityManagementRoleDocumentForm) form;
        String delegationRoleMemberId = getDelegationRoleMemberToJumpTo(request);
        KualiTableRenderFormMetadata memberTableMetadata = idmForm.getMemberTableMetadata();
        memberTableMetadata.jumpToPage(idmForm.getPageNumberOfRoleMemberId(delegationRoleMemberId),
        								idmForm.getMemberRows().size(), idmForm.getRecordsPerPage());
        memberTableMetadata.setColumnToSortIndex(memberTableMetadata.getPreviouslySortedColumnIndex());
        idmForm.setAnchor(delegationRoleMemberId);
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    protected String getDelegationRoleMemberToJumpTo(HttpServletRequest request) {
        String delegationRoleMemberIdToJumpTo = "";
        String parameterName = (String) request.getAttribute(KNSConstants.METHOD_TO_CALL_ATTRIBUTE);
        if (StringUtils.isNotBlank(parameterName)) {
            delegationRoleMemberIdToJumpTo = StringUtils.substringBetween(parameterName, ".dmrmi", ".");
        }
        return delegationRoleMemberIdToJumpTo;
    }


	/**
	 * Side-effecting method returns an ActionForward if needed for handling prompting of the user about automatically
	 * "inactivating" active delegates of inactive role members.  If the user has already responded "Yes", delegates are
	 * "inactivated" here, and a null forward is returned.  Otherwise, an appropriate forward is returned.
	 *
	 * @param roleMembersToConsiderInactive additional role members to consider inactive for the purposes of this computation
	 */
	private ActionForward promptForAffectedDelegates(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response,
			IdentityManagementRoleDocumentForm roleDocumentForm, KimDocumentRoleMember ... roleMembersToConsiderInactive)
			throws Exception {
		// KULRICE-4762: Role: Removed an Assignee who has delegations associated with him and now the Role cannot be updated
		// To solve this issue, prompt for confirmation if there are active delegates for the role member being "inactivated",
		// and upon confirmation, "inactivate" the delegates too.
		List<RoleDocumentDelegationMember> activeDelegatesOfInactiveRoleMembers =
			getActiveDelegatesOfInactiveRoleMembers(roleDocumentForm, roleMembersToConsiderInactive);
		ActionForward forward = getAffectedDelegatesQuestionActionForward(activeDelegatesOfInactiveRoleMembers, mapping, form, request,
				response, roleDocumentForm);
		// if the question logic gave us a forward, do it
		if (forward != null) return forward;
		// otherwise, inactivate affected delegates
		if (activeDelegatesOfInactiveRoleMembers.size() > 0) {
			Calendar cal = Calendar.getInstance();
			// deactivate (inactivate?) delegates
			for (RoleDocumentDelegationMember delegateToDeactivate : activeDelegatesOfInactiveRoleMembers) {
				delegateToDeactivate.setActiveToDate(new Timestamp(cal.getTimeInMillis()));
			}
		}
		return null;
	}

	/**
	 * <p>If there are active delegates of an "inactivated" role member, return an ActionForward to prompt the user
	 * letting them know that the delegates will be "inactivated" too if they proceed.
	 * <p>Also, if the user has already responded to the question and the response was (1) "Yes", then return null, signifying
	 * that we can go ahead and take the needed action to "inactivate" the delegates; or (2) "No", then return a basic forward that
	 * will cancel further action.
	 */
	private ActionForward getAffectedDelegatesQuestionActionForward(List<RoleDocumentDelegationMember> activeDelegatesOfInactiveRoleMembers,
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response,
			IdentityManagementRoleDocumentForm roleDocumentForm)
	throws Exception {

		if (activeDelegatesOfInactiveRoleMembers.size() > 0) {
			Object question = getQuestion(request);
			// logic for delegates question
			if (question == null || !REMOVE_AFFECTED_DELEGATES_QUESTION_ID.equals(question)) {
				return performQuestionWithoutInput(mapping, form, request, response, REMOVE_AFFECTED_DELEGATES_QUESTION_ID,
						getKualiConfigurationService().getPropertyString(RiceKeyConstants.QUESTION_ACTIVE_DELEGATES_FOR_INACTIVE_MEMBERS),
						KNSConstants.CONFIRMATION_QUESTION, roleDocumentForm.getMethodToCall(), StringUtils.EMPTY);
			}
			Object buttonClicked = request.getParameter(KNSConstants.QUESTION_CLICKED_BUTTON);
			if ((REMOVE_AFFECTED_DELEGATES_QUESTION_ID.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {
				// the question was answered in the affirmative.
		        // fall through, no special mapping to return
			} else {
				// NO was clicked ... what to do?  Return basic mapping without "inactivating" anything
				return mapping.findForward(RiceConstants.MAPPING_BASIC);
			}
		}

		return null;
	}

	/**
	 * This method returns a list of all active delegates for role members that are inactive
	 *
	 * @param roleDocumentForm form bean
	 * @param roleMembersToConsiderInactive additional role members to consider inactive for the purposes of this computation
	 * @return the active delegates of inactive role members
	 */
	private List<RoleDocumentDelegationMember> getActiveDelegatesOfInactiveRoleMembers(
			IdentityManagementRoleDocumentForm roleDocumentForm, KimDocumentRoleMember ... roleMembersToConsiderInactive) {
		List<KimDocumentRoleMember> roleMembers = roleDocumentForm.getMemberRows();
		List<KimDocumentRoleMember> inactiveRoleMembers = new ArrayList<KimDocumentRoleMember>();
		List<RoleDocumentDelegationMember> activeDelegatesOfInactivatedRoleMembers = new ArrayList<RoleDocumentDelegationMember>();

        inactiveRoleMembers.addAll(Arrays.asList(roleMembersToConsiderInactive));

		if (roleMembers != null) for (KimDocumentRoleMember roleMember : roleMembers) if (roleMember != null) {
			if (!roleMember.isActive()) inactiveRoleMembers.add(roleMember);
		}

		for (KimDocumentRoleMember inactiveRoleMember : inactiveRoleMembers) {
			// check if there are delegates for the member being removed
			List<RoleDocumentDelegationMember> delegationMembers = roleDocumentForm.getRoleDocument().getDelegationMembers();
			if (delegationMembers != null) for (RoleDocumentDelegationMember delegationMember : delegationMembers) {
				if (delegationMember != null && delegationMember.isActive()) {
					// if the roleMember for this delegation is the same as the inactivatedRoleMember
					if (delegationMember.getRoleMemberId().equals(inactiveRoleMember.getRoleMemberId())) {
						activeDelegatesOfInactivatedRoleMembers.add(delegationMember);
					}
				}
			}
		}
		return activeDelegatesOfInactivatedRoleMembers;
	}

	/**
	 * 
	 * This method overrides validateRole() from IdentityManagementDocumentActionBase.
	 * The difference with this method is that it allows derived roles.   
	 * The base implementation returns false if the role is a derived role.
	 * 
	 * @see org.kuali.rice.kim.web.struts.action.IdentityManagementDocumentActionBase#validateRole(java.lang.String, org.kuali.rice.kim.bo.Role, java.lang.String, java.lang.String)
	 */
    protected boolean validateRole( String roleId, Role role, String propertyName, String message){
    	if ( role == null ) {
        	GlobalVariables.getMessageMap().putError(propertyName, RiceKeyConstants.ERROR_INVALID_ROLE, roleId );
    		return false;
    	}
    	return true;
    }
 

}
