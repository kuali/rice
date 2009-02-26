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
package org.kuali.rice.kim.document.rule;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.bo.role.impl.KimRoleImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.bo.types.impl.KimAttributeImpl;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleMember;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleQualifier;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleResponsibility;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleResponsibilityAction;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegationMember;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegationMemberQualifier;
import org.kuali.rice.kim.document.IdentityManagementRoleDocument;
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
import org.kuali.rice.kim.service.support.impl.KimTypeServiceBase;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rules.TransactionalDocumentRuleBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.ErrorMessage;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.RiceKeyConstants;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class IdentityManagementRoleDocumentRule extends TransactionalDocumentRuleBase implements AddPermissionRule, AddResponsibilityRule, AddMemberRule, AddDelegationRule, AddDelegationMemberRule {

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
        GlobalVariables.getErrorMap().addToErrorPath(KNSConstants.DOCUMENT_PROPERTY_NAME);
        valid &= validDuplicateRoleName(roleDoc);
        getDictionaryValidationService().validateDocumentAndUpdatableReferencesRecursively(document, getMaxDictionaryValidationDepth(), true, false);
        valid &= validateRoleQualifier(roleDoc.getMembers(), roleDoc.getKimType());
        valid &= validRoleMemberActiveDates(roleDoc.getMembers());
        valid &= validateDelegationMemberRoleQualifier(roleDoc.getDelegationMembers(), roleDoc.getKimType());
        valid &= validDelegationMemberActiveDates(roleDoc.getDelegationMembers());
        valid &= validRoleResponsibilitiesActions(roleDoc.getResponsibilities());
        valid &= validRoleMembersResponsibilityActions(roleDoc.getMembers());
        GlobalVariables.getErrorMap().removeFromErrorPath(KNSConstants.DOCUMENT_PROPERTY_NAME);

        return valid;
    }
    
    private boolean validDuplicateRoleName(IdentityManagementRoleDocument roleDoc){
    	Map<String, String> criteria = new HashMap<String, String>();
    	criteria.put("roleName", roleDoc.getRoleName());
    	criteria.put("namespaceCode", roleDoc.getRoleNamespace());
    	List<KimRoleImpl> roleImpls = (List<KimRoleImpl>)getBusinessObjectService().findMatching(KimRoleImpl.class, criteria);
    	boolean rulePassed = true;
    	if(roleImpls!=null && roleImpls.size()>0){
    		if(roleImpls.size()==1 && roleImpls.get(0).getRoleId().equals(roleDoc.getRoleId()))
    			rulePassed = true;
    		else{
	    		GlobalVariables.getErrorMap().putError("document.roleName", 
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
    		GlobalVariables.getErrorMap().putError(errorPath, 
   				RiceKeyConstants.ERROR_PRIORITY_NUMBER_RANGE, new String[] {PRIORITY_NUMBER_MIN_VALUE+"", PRIORITY_NUMBER_MAX_VALUE+""});
    		rulePassed = false;
    	}

    	return rulePassed;
    }

    private boolean validateRoleQualifier(List<KimDocumentRoleMember> roleMembers, KimTypeImpl kimType){
		AttributeSet validationErrors = new AttributeSet();

		int memberCounter = 0;
		int attributeCounter = 0;
		AttributeSet errorsTemp;
		AttributeSet attributeSetToValidate;
		KimAttributeImpl attribute;
        KimTypeService kimTypeService = KimCommonUtils.getKimTypeService(kimType);
		for(KimDocumentRoleMember roleMember: roleMembers) {
	        for(KimDocumentRoleQualifier roleQualifier: roleMember.getQualifiers()) {
	        	attributeSetToValidate = new AttributeSet();
	        	attribute = roleQualifier.getKimAttribute();
	        	if(attribute==null){
	        		Map<String, String> criteria = new HashMap<String, String>();
	        		criteria.put("kimAttributeId", roleQualifier.getKimAttrDefnId());
	        		attribute = (KimAttributeImpl)KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(KimAttributeImpl.class, criteria);
	        	}
	        	attributeSetToValidate.put(attribute.getAttributeName(), roleQualifier.getAttrVal());
		        errorsTemp = kimTypeService.validateAttributes(attributeSetToValidate);
		        updateGlobalVariablesErrorKeys(
		        		"document.members["+memberCounter+"].qualifiers["+attributeCounter+"]", 
		        		attributeSetToValidate, errorsTemp);
		        validationErrors.putAll(errorsTemp);
	        	attributeCounter++;
	        }
	        memberCounter++;
	        attributeCounter = 0;
    	}
    	return validationErrors.isEmpty();
    }

    private boolean validateDelegationMemberRoleQualifier(List<RoleDocumentDelegationMember> delegationMembers, KimTypeImpl kimType){
		AttributeSet validationErrors = new AttributeSet();

		int memberCounter = 0;
		int attributeCounter = 0;
		AttributeSet errorsTemp;
		AttributeSet attributeSetToValidate;
		KimAttributeImpl attribute;
        KimTypeService kimTypeService = KimCommonUtils.getKimTypeService(kimType);
		for(RoleDocumentDelegationMember delegationMember: delegationMembers) {
	        for(RoleDocumentDelegationMemberQualifier delegationMemberQualifier: delegationMember.getQualifiers()) {
	        	attributeSetToValidate = new AttributeSet();
	        	attribute = delegationMemberQualifier.getKimAttribute();
	        	if(attribute==null){
	        		Map<String, String> criteria = new HashMap<String, String>();
	        		criteria.put("kimAttributeId", delegationMemberQualifier.getKimAttrDefnId());
	        		attribute = (KimAttributeImpl)getBusinessObjectService().findByPrimaryKey(KimAttributeImpl.class, criteria);
	        	}
	        	attributeSetToValidate.put(attribute.getAttributeName(), delegationMemberQualifier.getAttrVal());
		        errorsTemp = kimTypeService.validateAttributes(attributeSetToValidate);
		        updateGlobalVariablesErrorKeys(
		        		"document.delegationMembers["+memberCounter+"].qualifier("+attributeCounter+")", 
		        		attributeSetToValidate, errorsTemp);
		        validationErrors.putAll(errorsTemp);
	        	attributeCounter++;
	        }
	        memberCounter++;
	        attributeCounter = 0;
    	}
    	return validationErrors.isEmpty();
    }
    
    private void updateGlobalVariablesErrorKeys(String errorPath, AttributeSet attributes, AttributeSet validationErrors){
    	List<ErrorMessage> attributeErrors;
    	String errorKey;
    	for(String attributeName: attributes.keySet()){
    		errorKey = GlobalVariables.getErrorMap().getKeyPath(attributeName, true);
    		attributeErrors = (List<ErrorMessage>)GlobalVariables.getErrorMap().get(errorKey);
    		if(attributeErrors!=null){
    			for(ErrorMessage errorMessage: attributeErrors){
    				GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(errorPath+".attrVal", errorMessage.getErrorKey(), errorMessage.getMessageParameters());
    			}
				GlobalVariables.getErrorMap().remove(errorKey);
    		}
    	}
    }
    
	private boolean validateActiveDate(String errorPath, Timestamp activeFromDate, Timestamp activeToDate) {
		// TODO : do not have detail bus rule yet, so just check this for now.
		boolean valid = true;
		if (activeFromDate != null && activeToDate !=null && activeToDate.before(activeFromDate)) {
	        ErrorMap errorMap = GlobalVariables.getErrorMap();
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
        return new KimDocumentMemberRule().processAddMember(addMemberEvent);    
    }

    public boolean processAddDelegation(AddDelegationEvent addDelegationEvent) {
        return new RoleDocumentDelegationRule().processAddDelegation(addDelegationEvent);    
    }

    public boolean processAddDelegationMember(AddDelegationMemberEvent addDelegationMemberEvent) {
        return new RoleDocumentDelegationMemberRule().processAddDelegationMember(addDelegationMemberEvent);    
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
