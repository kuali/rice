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

import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.core.util.RiceKeyConstants;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.bo.impl.GroupImpl;
import org.kuali.rice.kim.bo.ui.GroupDocumentMember;
import org.kuali.rice.kim.bo.ui.GroupDocumentQualifier;
import org.kuali.rice.kim.document.IdentityManagementGroupDocument;
import org.kuali.rice.kim.rule.event.ui.AddGroupMemberEvent;
import org.kuali.rice.kim.rule.ui.AddGroupMemberRule;
import org.kuali.rice.kim.rules.ui.GroupDocumentMemberRule;
import org.kuali.rice.kim.service.IdentityService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.KIMServiceLocatorWeb;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rules.TransactionalDocumentRuleBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.MessageMap;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class IdentityManagementGroupDocumentRule extends TransactionalDocumentRuleBase implements AddGroupMemberRule {

	protected AddGroupMemberRule addGroupMemberRule;
	protected AttributeValidationHelper attributeValidationHelper = new AttributeValidationHelper();
	
	protected BusinessObjectService businessObjectService;
	protected Class<? extends GroupDocumentMemberRule> addGroupMemberRuleClass = GroupDocumentMemberRule.class;

	protected IdentityService identityService; 
	
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
        GlobalVariables.getMessageMap().addToErrorPath(KNSConstants.DOCUMENT_PROPERTY_NAME);
        valid &= validAssignGroup(groupDoc);
        valid &= validDuplicateGroupName(groupDoc);
        getDictionaryValidationService().validateDocumentAndUpdatableReferencesRecursively(document, getMaxDictionaryValidationDepth(), true, false);
        valid &= validateGroupQualifier(groupDoc.getQualifiers(), groupDoc.getKimType());
        valid &= validGroupMemberActiveDates(groupDoc.getMembers());
        GlobalVariables.getMessageMap().removeFromErrorPath(KNSConstants.DOCUMENT_PROPERTY_NAME);

        return valid;
    }
    
	protected boolean validAssignGroup(IdentityManagementGroupDocument document){
        boolean rulePassed = true;
        Map<String,String> additionalPermissionDetails = new HashMap<String,String>();
        additionalPermissionDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE, document.getGroupNamespace());
        additionalPermissionDetails.put(KimConstants.AttributeConstants.GROUP_NAME, document.getGroupName());
		if(document.getMembers()!=null && document.getMembers().size()>0){
			if(!getDocumentHelperService().getDocumentAuthorizer(document).isAuthorizedByTemplate(
					document, KimConstants.NAMESPACE_CODE, KimConstants.PermissionTemplateNames.POPULATE_GROUP, 
					GlobalVariables.getUserSession().getPrincipalId(), additionalPermissionDetails, null)){
	    		GlobalVariables.getMessageMap().putError("document.groupName", 
	    				RiceKeyConstants.ERROR_ASSIGN_GROUP, 
	    				new String[] {document.getGroupNamespace(), document.getGroupName()});
	            rulePassed = false;
			}
		}
		return rulePassed;
	}

    @SuppressWarnings("unchecked")
	protected boolean validDuplicateGroupName(IdentityManagementGroupDocument groupDoc){
    	Map<String, String> criteria = new HashMap<String, String>();
    	criteria.put("groupName", groupDoc.getGroupName());
    	criteria.put("namespaceCode", groupDoc.getGroupNamespace());
    	List<GroupImpl> groupImpls = (List<GroupImpl>)getBusinessObjectService().findMatching(GroupImpl.class, criteria);
    	boolean rulePassed = true;
    	if(groupImpls!=null && groupImpls.size()>0){
    		if(groupImpls.size()==1 && groupImpls.get(0).getGroupId().equals(groupDoc.getGroupId()))
    			rulePassed = true;
    		else{
	    		GlobalVariables.getMessageMap().putError("document.groupName", 
	    				RiceKeyConstants.ERROR_DUPLICATE_ENTRY, new String[] {"Group Name"});
	    		rulePassed = false;
    		}
    	}
    	return rulePassed;
    }
    
    protected boolean validGroupMemberActiveDates(List<GroupDocumentMember> groupMembers) {
    	boolean valid = true;
		int i = 0;
    	for(GroupDocumentMember groupMember: groupMembers) {
   			valid &= validateActiveDate("document.members["+i+"].activeToDate", groupMember.getActiveFromDate(), groupMember.getActiveToDate());
    		i++;
    	}
    	return valid;
    }

    protected boolean validateGroupQualifier(List<GroupDocumentQualifier> groupQualifiers, KimType kimType){
		AttributeSet validationErrors = new AttributeSet();

		AttributeSet errorsTemp;
		AttributeSet attributeSetToValidate;
        KimTypeService kimTypeService = KIMServiceLocatorWeb.getKimTypeService(kimType);
        GlobalVariables.getMessageMap().removeFromErrorPath(KNSConstants.DOCUMENT_PROPERTY_NAME);
		attributeSetToValidate = attributeValidationHelper.convertQualifiersToMap(groupQualifiers);
		errorsTemp = kimTypeService.validateAttributes(kimType.getId(), attributeSetToValidate);
		validationErrors.putAll( attributeValidationHelper.convertErrors("",attributeValidationHelper.convertQualifiersToAttrIdxMap(groupQualifiers),errorsTemp) );
		GlobalVariables.getMessageMap().addToErrorPath(KNSConstants.DOCUMENT_PROPERTY_NAME);
		
    	if (validationErrors.isEmpty()) {
    		return true;
    	} 
    	attributeValidationHelper.moveValidationErrorsToErrorMap(validationErrors);
    	return false;
    }
    
	protected boolean validateActiveDate(String errorPath, Timestamp activeFromDate, Timestamp activeToDate) {
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
