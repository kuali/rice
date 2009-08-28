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
package org.kuali.rice.kim.document.rule;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.impl.RoleImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.bo.types.dto.KimTypeInfo;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleMember;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleResponsibility;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleResponsibilityAction;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegationMember;
import org.kuali.rice.kim.document.IdentityManagementRoleDocument;
import org.kuali.rice.kim.lookup.KimTypeLookupableHelperServiceImpl;
import org.kuali.rice.kim.rule.event.ui.AddDelegationEvent;
import org.kuali.rice.kim.rule.event.ui.AddDelegationMemberEvent;
import org.kuali.rice.kim.rule.event.ui.AddMemberEvent;
import org.kuali.rice.kim.rule.event.ui.AddPermissionEvent;
import org.kuali.rice.kim.rule.event.ui.AddResponsibilityEvent;
import org.kuali.rice.kim.rule.ui.AddDelegationMemberRule;
import org.kuali.rice.kim.rule.ui.AddDelegationRule;
import org.kuali.rice.kim.rule.ui.AddMemberRule;
import org.kuali.rice.kim.rule.ui.AddPermissionRule;
import org.kuali.rice.kim.rule.ui.AddResponsibilityRule;
import org.kuali.rice.kim.rules.ui.KimDocumentMemberRule;
import org.kuali.rice.kim.rules.ui.KimDocumentPermissionRule;
import org.kuali.rice.kim.rules.ui.KimDocumentResponsibilityRule;
import org.kuali.rice.kim.rules.ui.RoleDocumentDelegationMemberRule;
import org.kuali.rice.kim.rules.ui.RoleDocumentDelegationRule;
import org.kuali.rice.kim.service.IdentityService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.ResponsibilityService;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rules.TransactionalDocumentRuleBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.MessageMap;
import org.kuali.rice.kns.util.RiceKeyConstants;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class IdentityManagementRoleDocumentRule extends TransactionalDocumentRuleBase implements AddPermissionRule, AddResponsibilityRule, AddMemberRule, AddDelegationRule, AddDelegationMemberRule {
//	private static final Logger LOG = Logger.getLogger( IdentityManagementRoleDocumentRule.class );
			
    public static final int PRIORITY_NUMBER_MIN_VALUE = 1;
    public static final int PRIORITY_NUMBER_MAX_VALUE = 11;

	private AddResponsibilityRule addResponsibilityRule;
	private AddPermissionRule  addPermissionRule;
	private AddMemberRule  addMemberRule;
	private AddDelegationRule addDelegationRule;
	private AddDelegationMemberRule addDelegationMemberRule;
	private BusinessObjectService businessObjectService;
	private ResponsibilityService responsibilityService;
	private Class<? extends AddResponsibilityRule> addResponsibilityRuleClass = KimDocumentResponsibilityRule.class;
	private Class<? extends AddPermissionRule> addPermissionRuleClass = KimDocumentPermissionRule.class;
	private Class<? extends AddMemberRule> addMemberRuleClass = KimDocumentMemberRule.class;
	private Class<? extends AddDelegationRule> addDelegationRuleClass = RoleDocumentDelegationRule.class;
	private Class<? extends AddDelegationMemberRule> addDelegationMemberRuleClass = RoleDocumentDelegationMemberRule.class;

	private IdentityService identityService;
	
	private AttributeValidationHelper attributeValidationHelper = new AttributeValidationHelper();
	
    public IdentityService getIdentityService() {
        if ( identityService == null) {
            identityService = KIMServiceLocator.getIdentityService();
        }
        return identityService;
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        if (!(document instanceof IdentityManagementRoleDocument))
            return false;

        IdentityManagementRoleDocument roleDoc = (IdentityManagementRoleDocument)document;

        boolean valid = true;
        boolean validateRoleAssigneesAndDelegations = !KimTypeLookupableHelperServiceImpl.hasDerivedRoleTypeService(roleDoc.getKimType());
        GlobalVariables.getMessageMap().addToErrorPath(KNSConstants.DOCUMENT_PROPERTY_NAME);
        valid &= validDuplicateRoleName(roleDoc);
        getDictionaryValidationService().validateDocumentAndUpdatableReferencesRecursively(document, getMaxDictionaryValidationDepth(), true, false);
        if(validateRoleAssigneesAndDelegations){
	        valid &= validAssignRole(roleDoc);
	        valid &= validateRoleQualifier(roleDoc.getMembers(), roleDoc.getKimType());
	        valid &= validRoleMemberActiveDates(roleDoc.getMembers());
	        valid &= validateDelegationMemberRoleQualifier(roleDoc.getMembers(), roleDoc.getDelegationMembers(), roleDoc.getKimType());
	        valid &= validDelegationMemberActiveDates(roleDoc.getDelegationMembers());
	        valid &= validRoleMembersResponsibilityActions(roleDoc.getMembers());
        }
        valid &= validRoleResponsibilitiesActions(roleDoc.getResponsibilities());
        GlobalVariables.getMessageMap().removeFromErrorPath(KNSConstants.DOCUMENT_PROPERTY_NAME);

        return valid;
    }
    
	private boolean validAssignRole(IdentityManagementRoleDocument document){
        boolean rulePassed = true;
        Map<String,String> additionalPermissionDetails = new HashMap<String,String>();
        additionalPermissionDetails.put(KimAttributes.NAMESPACE_CODE, document.getRoleNamespace());
        additionalPermissionDetails.put(KimAttributes.ROLE_NAME, document.getRoleName());
		if((document.getMembers()!=null && document.getMembers().size()>0) ||
				(document.getDelegationMembers()!=null && document.getDelegationMembers().size()>0)){
			if(!getDocumentHelperService().getDocumentAuthorizer(document).isAuthorizedByTemplate(
					document, KimConstants.NAMESPACE_CODE, KimConstants.PermissionTemplateNames.ASSIGN_ROLE, 
					GlobalVariables.getUserSession().getPrincipalId(), additionalPermissionDetails, null)){
	    		GlobalVariables.getMessageMap().putError("document.roleName", 
	    				RiceKeyConstants.ERROR_ASSIGN_ROLE, 
	    				new String[] {document.getRoleNamespace(), document.getRoleName()});
	            rulePassed = false;
			}
		}
		return rulePassed;
	}

    @SuppressWarnings("unchecked")
	private boolean validDuplicateRoleName(IdentityManagementRoleDocument roleDoc){
    	Map<String, String> criteria = new HashMap<String, String>();
    	criteria.put("roleName", roleDoc.getRoleName());
    	criteria.put("namespaceCode", roleDoc.getRoleNamespace());
    	List<RoleImpl> roleImpls = (List<RoleImpl>)getBusinessObjectService().findMatching(RoleImpl.class, criteria);
    	boolean rulePassed = true;
    	if(roleImpls!=null && roleImpls.size()>0){
    		if(roleImpls.size()==1 && roleImpls.get(0).getRoleId().equals(roleDoc.getRoleId()))
    			rulePassed = true;
    		else{
	    		GlobalVariables.getMessageMap().putError("document.roleName", 
	    				RiceKeyConstants.ERROR_DUPLICATE_ENTRY, new String[] {"Role Name"});
	    		rulePassed = false;
    		}
    	}
    	return rulePassed;
    }
    
    private boolean validRoleMemberActiveDates(List<KimDocumentRoleMember> roleMembers) {
    	boolean valid = true;
		int i = 0;
    	for(KimDocumentRoleMember roleMember: roleMembers) {
   			valid &= validateActiveDate("document.members["+i+"].activeToDate", roleMember.getActiveFromDate(), roleMember.getActiveToDate());
    		i++;
    	}
    	return valid;
    }

    private boolean validDelegationMemberActiveDates(List<RoleDocumentDelegationMember> delegationMembers) {
    	boolean valid = true;
		int i = 0;
    	for(RoleDocumentDelegationMember delegationMember: delegationMembers) {
   			valid &= validateActiveDate("document.delegationMembers["+i+"].activeToDate", 
   					delegationMember.getActiveFromDate(), delegationMember.getActiveToDate());
    		i++;
    	}
    	return valid;
    }

    private boolean validRoleResponsibilitiesActions(List<KimDocumentRoleResponsibility> roleResponsibilities){
        int i = 0;
        boolean rulePassed = true;
    	for(KimDocumentRoleResponsibility roleResponsibility: roleResponsibilities){
    		if(!getResponsibilityService().areActionsAtAssignmentLevelById(roleResponsibility.getResponsibilityId()))
    			validateRoleResponsibilityAction("document.responsibilities["+i+"].roleRspActions[0].priorityNumber", roleResponsibility.getRoleRspActions().get(0));
        	i++;
    	}
    	return rulePassed;
    }

    private boolean validRoleMembersResponsibilityActions(List<KimDocumentRoleMember> roleMembers){
        int i = 0;
        int j;
        boolean rulePassed = true;
    	for(KimDocumentRoleMember roleMember: roleMembers){
    		j = 0;
    		if(roleMember.getRoleRspActions()!=null && !roleMember.getRoleRspActions().isEmpty()){
	    		for(KimDocumentRoleResponsibilityAction roleRspAction: roleMember.getRoleRspActions()){
	    			validateRoleResponsibilityAction("document.members["+i+"].roleRspActions["+j+"].priorityNumber", roleRspAction);
		        	j++;
	    		}
    		}
    		i++;
    	}
    	return rulePassed;
    }

    private boolean validateRoleResponsibilityAction(String errorPath, KimDocumentRoleResponsibilityAction roleRspAction){
    	boolean rulePassed = true;
    	/*if(StringUtils.isBlank(roleRspAction.getActionPolicyCode())){
    		GlobalVariables.getErrorMap().putError(errorPath, 
    				RiceKeyConstants.ERROR_EMPTY_ENTRY, new String[] {"Action Policy Code"});
    		rulePassed = false;
    	}
    	if(roleRspAction.getPriorityNumber()==null){
    		GlobalVariables.getErrorMap().putError(errorPath, 
    				RiceKeyConstants.ERROR_EMPTY_ENTRY, new String[] {"Priority Number"});
    		rulePassed = false;
    	}
    	if(StringUtils.isBlank(roleRspAction.getActionTypeCode())){
    		GlobalVariables.getErrorMap().putError(errorPath, 
    				RiceKeyConstants.ERROR_EMPTY_ENTRY, new String[] {"Action Type Code"});
    		rulePassed = false;
    	}*/
    	if(roleRspAction.getPriorityNumber()!=null && 
    			(roleRspAction.getPriorityNumber()<PRIORITY_NUMBER_MIN_VALUE 
    					|| roleRspAction.getPriorityNumber()>PRIORITY_NUMBER_MAX_VALUE)){
    		GlobalVariables.getMessageMap().putError(errorPath, 
   				RiceKeyConstants.ERROR_PRIORITY_NUMBER_RANGE, new String[] {PRIORITY_NUMBER_MIN_VALUE+"", PRIORITY_NUMBER_MAX_VALUE+""});
    		rulePassed = false;
    	}

    	return rulePassed;
    }

    private boolean validateRoleQualifier(List<KimDocumentRoleMember> roleMembers, KimTypeInfo kimType){
		AttributeSet validationErrors = new AttributeSet();

		int memberCounter = 0;
		AttributeSet errorsTemp;
		AttributeSet attributeSetToValidate;
        KimTypeService kimTypeService = KimCommonUtils.getKimTypeService(kimType);
        GlobalVariables.getMessageMap().removeFromErrorPath(KNSConstants.DOCUMENT_PROPERTY_NAME);
		for(KimDocumentRoleMember roleMember: roleMembers) {
			attributeSetToValidate = attributeValidationHelper.convertQualifiersToMap(roleMember.getQualifiers());
			errorsTemp = kimTypeService.validateAttributes(kimType.getKimTypeId(), attributeSetToValidate);
			validationErrors.putAll( 
					attributeValidationHelper.convertErrorsForMappedFields("document.members["+memberCounter+"]", errorsTemp) );
	        memberCounter++;
    	}

		GlobalVariables.getMessageMap().addToErrorPath(KNSConstants.DOCUMENT_PROPERTY_NAME);
		
    	if (validationErrors.isEmpty()) {
    		return true;
    	} else {
    		attributeValidationHelper.moveValidationErrorsToErrorMap(validationErrors);
    		return false;
    	}
    }
    
    private KimDocumentRoleMember getRoleMemberForDelegation(
    		List<KimDocumentRoleMember> roleMembers, RoleDocumentDelegationMember delegationMember){
    	if(roleMembers==null || delegationMember==null || delegationMember.getRoleMemberId()==null) return null;
    	for(KimDocumentRoleMember roleMember: roleMembers){
    		if(delegationMember.getRoleMemberId().equals(roleMember.getRoleMemberId()))
    			return roleMember;
    	}
    	return null;
    }

    private boolean validateDelegationMemberRoleQualifier(List<KimDocumentRoleMember> roleMembers, 
    		List<RoleDocumentDelegationMember> delegationMembers, KimTypeInfo kimType){
		AttributeSet validationErrors = new AttributeSet();
		boolean valid;
		int memberCounter = 0;
		AttributeSet errorsTemp;
		AttributeSet attributeSetToValidate;
        KimTypeService kimTypeService = KimCommonUtils.getKimTypeService(kimType);
        GlobalVariables.getMessageMap().removeFromErrorPath(KNSConstants.DOCUMENT_PROPERTY_NAME);
        KimDocumentRoleMember roleMember;
        String errorPath;
		for(RoleDocumentDelegationMember delegationMember: delegationMembers) {
			errorPath = "delegationMembers["+memberCounter+"]";
			attributeSetToValidate = attributeValidationHelper.convertQualifiersToMap(delegationMember.getQualifiers());
			errorsTemp = kimTypeService.validateAttributes(kimType.getKimTypeId(), attributeSetToValidate);
			validationErrors.putAll(
					attributeValidationHelper.convertErrorsForMappedFields(errorPath, errorsTemp));

			roleMember = getRoleMemberForDelegation(roleMembers, delegationMember);
			if(roleMember==null){
				valid = false;
				GlobalVariables.getMessageMap().putError("document.delegationMembers["+memberCounter+"]", RiceKeyConstants.ERROR_DELEGATE_ROLE_MEMBER_ASSOCIATION, new String[]{});
			} else{
				errorsTemp = kimTypeService.validateUnmodifiableAttributes(
								kimType.getKimTypeId(), 
								attributeValidationHelper.convertQualifiersToMap(roleMember.getQualifiers()), 
								attributeSetToValidate);
				validationErrors.putAll(
						attributeValidationHelper.convertErrorsForMappedFields(errorPath, errorsTemp) );
			}
	        memberCounter++;
    	}
		GlobalVariables.getMessageMap().addToErrorPath(KNSConstants.DOCUMENT_PROPERTY_NAME);
    	if (validationErrors.isEmpty()) {
    		valid = true;
    	} else {
    		attributeValidationHelper.moveValidationErrorsToErrorMap(validationErrors);
    		valid = false;
    	}
    	return valid;
    }
    
	private boolean validateActiveDate(String errorPath, Date activeFromDate, Date activeToDate) {
		// TODO : do not have detail bus rule yet, so just check this for now.
		boolean valid = true;
		if (activeFromDate != null && activeToDate !=null && activeToDate.before(activeFromDate)) {
	        MessageMap errorMap = GlobalVariables.getMessageMap();
            errorMap.putError(errorPath, RiceKeyConstants.ERROR_ACTIVE_TO_DATE_BEFORE_FROM_DATE);
            valid = false;
			
		}
		return valid;
	}
	
	/**
	 * @return the addResponsibilityRule
	 */
	public AddResponsibilityRule getAddResponsibilityRule() {
		if(addResponsibilityRule == null){
			try {
				addResponsibilityRule = addResponsibilityRuleClass.newInstance();
			} catch ( Exception ex ) {
				throw new RuntimeException( "Unable to create AddResponsibilityRule instance using class: " + addResponsibilityRuleClass, ex );
			}
		}
		return addResponsibilityRule;
	}

	/**
	 * @return the addPermissionRule
	 */
	public AddPermissionRule getAddPermissionRule() {
		if(addPermissionRule == null){
			try {
				addPermissionRule = addPermissionRuleClass.newInstance();
			} catch ( Exception ex ) {
				throw new RuntimeException( "Unable to create AddPermissionRule instance using class: " + addPermissionRuleClass, ex );
			}
		}
		return addPermissionRule;
	}
	
	/**
	 * @return the addMemberRule
	 */
	public AddMemberRule getAddMemberRule() {
		if(addMemberRule == null){
			try {
				addMemberRule = addMemberRuleClass.newInstance();
			} catch ( Exception ex ) {
				throw new RuntimeException( "Unable to create AddMemberRule instance using class: " + addMemberRuleClass, ex );
			}
		}
		return addMemberRule;
	}

	/**
	 * @return the addDelegationRule
	 */
	public AddDelegationRule getAddDelegationRule() {
		if(addDelegationRule == null){
			try {
				addDelegationRule = addDelegationRuleClass.newInstance();
			} catch ( Exception ex ) {
				throw new RuntimeException( "Unable to create AddDelegationRule instance using class: " + addDelegationRuleClass, ex );
			}
		}
		return addDelegationRule;
	}

	/**
	 * @return the addDelegationMemberRule
	 */
	public AddDelegationMemberRule getAddDelegationMemberRule() {
		if(addDelegationMemberRule == null){
			try {
				addDelegationMemberRule = addDelegationMemberRuleClass.newInstance();
			} catch ( Exception ex ) {
				throw new RuntimeException( "Unable to create AddDelegationMemberRule instance using class: " + addDelegationMemberRuleClass, ex );
			}
		}
		return addDelegationMemberRule;
	}
	
    public boolean processAddPermission(AddPermissionEvent addPermissionEvent) {
        return new KimDocumentPermissionRule().processAddPermission(addPermissionEvent);    
    }

    public boolean processAddResponsibility(AddResponsibilityEvent addResponsibilityEvent) {
        return new KimDocumentResponsibilityRule().processAddResponsibility(addResponsibilityEvent);    
    }

    public boolean processAddMember(AddMemberEvent addMemberEvent) {
        boolean success = new KimDocumentMemberRule().processAddMember(addMemberEvent);
        success &= validateActiveDate("member.activeFromDate", addMemberEvent.getMember().getActiveFromDate(), addMemberEvent.getMember().getActiveToDate());
        return success;
    }

    public boolean processAddDelegation(AddDelegationEvent addDelegationEvent) {
        return new RoleDocumentDelegationRule().processAddDelegation(addDelegationEvent);    
    }

    public boolean processAddDelegationMember(AddDelegationMemberEvent addDelegationMemberEvent) {
        boolean success = new RoleDocumentDelegationMemberRule().processAddDelegationMember(addDelegationMemberEvent);
        RoleDocumentDelegationMember roleDocumentDelegationMember = addDelegationMemberEvent.getDelegationMember();
        success &= validateActiveDate("delegationMember.activeFromDate", roleDocumentDelegationMember.getActiveFromDate(), roleDocumentDelegationMember.getActiveToDate());
        return success;
    }

	public ResponsibilityService getResponsibilityService() {
		if(responsibilityService == null){
			responsibilityService = KIMServiceLocator.getResponsibilityService();
		}
		return responsibilityService;
	}


	/**
	 * @return the businessObjectService
	 */
	public BusinessObjectService getBusinessObjectService() {
		if(businessObjectService == null){
			businessObjectService = KNSServiceLocator.getBusinessObjectService();
		}
		return businessObjectService;
	}
}
