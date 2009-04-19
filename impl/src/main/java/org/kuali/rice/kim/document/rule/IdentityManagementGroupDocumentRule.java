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

import org.kuali.rice.kim.bo.impl.GroupImpl;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.bo.types.impl.KimAttributeImpl;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.bo.ui.GroupDocumentMember;
import org.kuali.rice.kim.bo.ui.GroupDocumentQualifier;
import org.kuali.rice.kim.document.IdentityManagementGroupDocument;
import org.kuali.rice.kim.rule.event.ui.AddGroupMemberEvent;
import org.kuali.rice.kim.rule.ui.AddGroupMemberRule;
import org.kuali.rice.kim.rules.ui.GroupDocumentMemberRule;
import org.kuali.rice.kim.service.IdentityService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kim.util.KimConstants;
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
public class IdentityManagementGroupDocumentRule extends TransactionalDocumentRuleBase implements AddGroupMemberRule {

	private AddGroupMemberRule addGroupMemberRule;
	private BusinessObjectService businessObjectService;
	private Class<? extends GroupDocumentMemberRule> addGroupMemberRuleClass = GroupDocumentMemberRule.class;

	private IdentityService identityService; 
	
    public IdentityService getIdentityService() {
        if ( identityService == null) {
            identityService = KIMServiceLocator.getIdentityService();
        }
        return identityService;
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        if (!(document instanceof IdentityManagementGroupDocument))
            return false;

        IdentityManagementGroupDocument groupDoc = (IdentityManagementGroupDocument)document;

        boolean valid = true;
        GlobalVariables.getErrorMap().addToErrorPath(KNSConstants.DOCUMENT_PROPERTY_NAME);
        valid &= validAssignGroup(groupDoc);
        valid &= validDuplicateGroupName(groupDoc);
        getDictionaryValidationService().validateDocumentAndUpdatableReferencesRecursively(document, getMaxDictionaryValidationDepth(), true, false);
        valid &= validateGroupQualifier(groupDoc.getQualifiers(), groupDoc.getKimType());
        valid &= validGroupMemberActiveDates(groupDoc.getMembers());
        GlobalVariables.getErrorMap().removeFromErrorPath(KNSConstants.DOCUMENT_PROPERTY_NAME);

        return valid;
    }
    
	private boolean validAssignGroup(IdentityManagementGroupDocument document){
        boolean rulePassed = true;
        Map<String,String> additionalPermissionDetails = new HashMap<String,String>();
        additionalPermissionDetails.put(KimAttributes.NAMESPACE_CODE, document.getGroupNamespace());
        additionalPermissionDetails.put(KimAttributes.GROUP_NAME, document.getGroupName());
		if(document.getMembers()!=null && document.getMembers().size()>0){
			if(!getDocumentHelperService().getDocumentAuthorizer(document).isAuthorizedByTemplate(
					document, KimConstants.NAMESPACE_CODE, KimConstants.PermissionTemplateNames.POPULATE_GROUP, 
					GlobalVariables.getUserSession().getPrincipalId(), additionalPermissionDetails, null)){
	    		GlobalVariables.getErrorMap().putError("document.groupName", 
	    				RiceKeyConstants.ERROR_ASSIGN_GROUP, 
	    				new String[] {document.getGroupNamespace(), document.getGroupName()});
	            rulePassed = false;
			}
		}
		return rulePassed;
	}

    private boolean validDuplicateGroupName(IdentityManagementGroupDocument groupDoc){
    	Map<String, String> criteria = new HashMap<String, String>();
    	criteria.put("groupName", groupDoc.getGroupName());
    	criteria.put("namespaceCode", groupDoc.getGroupNamespace());
    	List<GroupImpl> groupImpls = (List<GroupImpl>)getBusinessObjectService().findMatching(GroupImpl.class, criteria);
    	boolean rulePassed = true;
    	if(groupImpls!=null && groupImpls.size()>0){
    		if(groupImpls.size()==1 && groupImpls.get(0).getGroupId().equals(groupDoc.getGroupId()))
    			rulePassed = true;
    		else{
	    		GlobalVariables.getErrorMap().putError("document.groupName", 
	    				RiceKeyConstants.ERROR_DUPLICATE_ENTRY, new String[] {"Group Name"});
	    		rulePassed = false;
    		}
    	}
    	return rulePassed;
    }
    
    private boolean validGroupMemberActiveDates(List<GroupDocumentMember> groupMembers) {
    	boolean valid = true;
		int i = 0;
    	for(GroupDocumentMember groupMember: groupMembers) {
   			valid &= validateActiveDate("document.members["+i+"].activeToDate", groupMember.getActiveFromDate(), groupMember.getActiveToDate());
    		i++;
    	}
    	return valid;
    }

    private boolean validateGroupQualifier(List<GroupDocumentQualifier> groupQualifiers, KimTypeImpl kimType){
		AttributeSet validationErrors = new AttributeSet();

		int attributeCounter = 0;
		AttributeSet errorsTemp;
		AttributeSet attributeSetToValidate;
		KimAttributeImpl attribute;
        KimTypeService kimTypeService = KimCommonUtils.getKimTypeService(kimType);
        for(GroupDocumentQualifier groupQualifier: groupQualifiers) {
        	attributeSetToValidate = new AttributeSet();
        	attribute = groupQualifier.getKimAttribute();
        	if(attribute==null){
        		Map<String, String> criteria = new HashMap<String, String>();
        		criteria.put("kimAttributeId", groupQualifier.getKimAttrDefnId());
        		attribute = (KimAttributeImpl)KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(KimAttributeImpl.class, criteria);
        	}
        	attributeSetToValidate.put(attribute.getAttributeName(), groupQualifier.getAttrVal());
	        errorsTemp = kimTypeService.validateAttributes(attributeSetToValidate);
	        updateGlobalVariablesErrorKeys(
	        		"document.qualifiers["+attributeCounter+"]", 
	        		attributeSetToValidate, errorsTemp);
	        validationErrors.putAll(errorsTemp);
        	attributeCounter++;
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
	 * @return the addGroupMemberRule
	 */
	public AddGroupMemberRule getAddGroupMemberRule() {
		if(addGroupMemberRule == null){
			try {
				addGroupMemberRule = addGroupMemberRuleClass.newInstance();
			} catch ( Exception ex ) {
				throw new RuntimeException( "Unable to create AddMemberRule instance using class: " + addGroupMemberRuleClass, ex );
			}
		}
		return addGroupMemberRule;
	}

    public boolean processAddGroupMember(AddGroupMemberEvent addGroupMemberEvent) {
        return new GroupDocumentMemberRule().processAddGroupMember(addGroupMemberEvent);    
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