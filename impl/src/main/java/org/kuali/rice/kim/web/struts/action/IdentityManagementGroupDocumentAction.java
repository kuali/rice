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
import org.kuali.rice.kim.bo.entity.impl.KimPrincipalImpl;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.bo.group.impl.KimGroupImpl;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.options.MemberTypeValuesFinder;
import org.kuali.rice.kim.bo.role.impl.KimRoleImpl;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.bo.ui.GroupDocumentMember;
import org.kuali.rice.kim.document.IdentityManagementGroupDocument;
import org.kuali.rice.kim.rule.event.ui.AddGroupMemberEvent;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kim.web.struts.form.IdentityManagementGroupDocumentForm;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
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
public class IdentityManagementGroupDocumentAction extends IdentityManagementDocumentActionBase {

	public static final String CHANGE_MEMBER_TYPE_CODE_METHOD_TO_CALL = "changeMemberTypeCode"; 
	
    @Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
        String methodToCall = findMethodToCall(form, request);
		ActionForward forward;
        IdentityManagementGroupDocumentForm groupDocumentForm = (IdentityManagementGroupDocumentForm) form;
		String kimTypeId = request.getParameter(KimConstants.PrimaryKeyConstants.KIM_TYPE_ID);
		KimTypeImpl kimType = null;
        if(KNSConstants.DOC_HANDLER_METHOD.equals(methodToCall) && kimTypeId!=null){
	        Map<String, String> criteria = new HashMap<String, String>();
	        criteria.put(KimConstants.PrimaryKeyConstants.KIM_TYPE_ID, kimTypeId);
	        kimType = (KimTypeImpl)KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(KimTypeImpl.class, criteria);
	        if(kimType == null)
	        	throw new IllegalArgumentException("Kim type could not be found for kim type id: "+kimTypeId);
	        groupDocumentForm.setKimType(kimType);
	        groupDocumentForm.getGroupDocument().setKimType(kimType);
	        groupDocumentForm.setDocTypeName(groupDocumentForm.getGroupDocument().getWorkflowDocumentTypeName());
	    }
		if(KNSConstants.PARAM_MAINTENANCE_VIEW_MODE_INQUIRY.equals(methodToCall)) 
        	forward = mapping.findForward(KNSConstants.PARAM_MAINTENANCE_VIEW_MODE_INQUIRY);
        else {
        	forward = super.execute(mapping, form, request, response);
			if(KNSConstants.DOC_HANDLER_METHOD.equals(methodToCall) && kimTypeId!=null){
		        groupDocumentForm.getGroupDocument().setKimType(kimType);
		        groupDocumentForm.getGroupDocument().setGroupTypeId(kimType.getKimTypeId());
		        groupDocumentForm.getGroupDocument().setGroupTypeName(kimType.getName());
		        groupDocumentForm.getGroupDocument().getGroupId();
		        groupDocumentForm.setMember(groupDocumentForm.getGroupDocument().getBlankMember());
			}
        }
		String commandParam = request.getParameter(KNSConstants.PARAMETER_COMMAND);
        String groupId = request.getParameter(KimConstants.PrimaryKeyConstants.GROUP_ID);
        if (StringUtils.isNotBlank(commandParam) && commandParam.equals(KEWConstants.INITIATE_COMMAND) 
				&& StringUtils.isNotBlank(groupId)) {
	        KimGroup group = KIMServiceLocator.getGroupService().getGroupInfo(groupId);
			KIMServiceLocator.getUiDocumentService().loadGroupDoc(groupDocumentForm.getGroupDocument(), group);
			groupDocumentForm.setMember(groupDocumentForm.getGroupDocument().getBlankMember());
        	if(!KNSConstants.PARAM_MAINTENANCE_VIEW_MODE_INQUIRY.equals(methodToCall))
        		groupDocumentForm.setCanAssignGroup(validAssignGroup(groupDocumentForm.getGroupDocument()));
        } 
        if (StringUtils.isNotBlank(commandParam) && commandParam.equals(CHANGE_MEMBER_TYPE_CODE_METHOD_TO_CALL)){
	        groupDocumentForm.getMember().setMemberName("");
		}
		((KualiDocumentFormBase) form).setErrorMapFromPreviousRequest(GlobalVariables.getErrorMap());
		return forward;
    }
    
	/***
	 * @see org.kuali.rice.kim.web.struts.action.IdentityManagementDocumentActionBase#getActionName()
	 */
	public String getActionName(){
		return KimConstants.KimUIConstants.KIM_GROUP_DOCUMENT_ACTION;
	}

	private boolean validAssignGroup(IdentityManagementGroupDocument document){
        boolean rulePassed = true;
        Map<String,String> additionalPermissionDetails = new HashMap<String,String>();
        additionalPermissionDetails.put(KimAttributes.NAMESPACE_CODE, document.getGroupNamespace());
        additionalPermissionDetails.put(KimAttributes.GROUP_NAME, document.getGroupName());
		if (!getDocumentHelperService().getDocumentAuthorizer(document).isAuthorizedByTemplate(
				document, 
				KimConstants.NAMESPACE_CODE, 
				KimConstants.PermissionTemplateNames.POPULATE_GROUP, 
				GlobalVariables.getUserSession().getPrincipalId(), 
				additionalPermissionDetails, null)){
            rulePassed = false;
		}
		return rulePassed;
	}

    public ActionForward addMember(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementGroupDocumentForm groupDocumentForm = (IdentityManagementGroupDocumentForm) form;
        GroupDocumentMember newMember = groupDocumentForm.getMember();
        if(checkKimDocumentGroupMember(newMember) && 
        		KNSServiceLocator.getKualiRuleService().applyRules(new AddGroupMemberEvent("", groupDocumentForm.getGroupDocument(), newMember))){
        	newMember.setDocumentNumber(groupDocumentForm.getDocument().getDocumentNumber());
        	groupDocumentForm.getGroupDocument().addMember(newMember);
	        groupDocumentForm.setMember(groupDocumentForm.getGroupDocument().getBlankMember());
        }
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    private boolean checkKimDocumentGroupMember(GroupDocumentMember newMember){
        if(StringUtils.isBlank(newMember.getMemberTypeCode()) || StringUtils.isBlank(newMember.getMemberId())){
        	GlobalVariables.getErrorMap().putError("document.member.memberId", RiceKeyConstants.ERROR_EMPTY_ENTRY, 
        			new String[] {"Member Type Code and Member ID"});
        	return false;
		}
    	PersistableBusinessObject object = getMember(newMember.getMemberTypeCode(), newMember.getMemberId());
        if(object==null){
        	GlobalVariables.getErrorMap().putError("document.member.memberId", RiceKeyConstants.ERROR_MEMBERID_MEMBERTYPE_MISMATCH, 
        			new String[] {newMember.getMemberId()});
        	return false;
		}
        newMember.setMemberName(getMemberName(newMember.getMemberTypeCode(), object));
        return true;
    }

    private PersistableBusinessObject getMember(String memberTypeCode, String memberId){
        Class groupMemberTypeClass = null;
        String groupMemberIdName = "";
    	if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode)){
        	groupMemberTypeClass = KimPrincipalImpl.class;
        	groupMemberIdName = "principalId";
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode)){
        	groupMemberTypeClass = KimGroupImpl.class;
        	groupMemberIdName = "groupId";
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode)){
        	groupMemberTypeClass = KimRoleImpl.class;
        	groupMemberIdName = "roleId";
        }
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put(groupMemberIdName, memberId);
        return KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(groupMemberTypeClass, criteria);
    }
    
    public ActionForward deleteMember(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementGroupDocumentForm groupDocumentForm = (IdentityManagementGroupDocumentForm) form;
        groupDocumentForm.getGroupDocument().getMembers().remove(getLineToDelete(request));
        groupDocumentForm.setMember(groupDocumentForm.getGroupDocument().getBlankMember());
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    public String getMemberName(String memberTypeCode, BusinessObject object){
    	String groupMemberName = "";
        if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode)){
        	groupMemberName = ((KimPrincipalImpl)object).getPrincipalName();
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode)){
        	groupMemberName = ((KimGroupImpl)object).getGroupName();
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode)){
        	groupMemberName = ((KimRoleImpl)object).getRoleName();
        }
        return groupMemberName;
    }
    
}