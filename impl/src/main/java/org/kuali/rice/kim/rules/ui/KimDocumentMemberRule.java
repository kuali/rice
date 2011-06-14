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
 * See the License for the specific language governing members and
 * limitations under the License.
 */
package org.kuali.rice.kim.rules.ui;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.core.util.RiceKeyConstants;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleMember;
import org.kuali.rice.kim.document.IdentityManagementRoleDocument;
import org.kuali.rice.kim.document.rule.AttributeValidationHelper;
import org.kuali.rice.kim.rule.event.ui.AddMemberEvent;
import org.kuali.rice.kim.rule.ui.AddMemberRule;
import org.kuali.rice.kim.service.KIMServiceLocatorWeb;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.krad.rules.DocumentRuleBase;
import org.kuali.rice.krad.util.GlobalVariables;

import java.util.HashMap;
import java.util.Map;


/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KimDocumentMemberRule extends DocumentRuleBase implements AddMemberRule {

	private static final String ERROR_PATH = "member.memberId";

	protected AttributeValidationHelper attributeValidationHelper = new AttributeValidationHelper();
	
	public boolean processAddMember(AddMemberEvent addMemberEvent){
		KimDocumentRoleMember newMember = addMemberEvent.getMember();
		IdentityManagementRoleDocument document = (IdentityManagementRoleDocument)addMemberEvent.getDocument();
	    boolean rulePassed = true;

        if (newMember == null || StringUtils.isBlank(newMember.getMemberId())){
            GlobalVariables.getMessageMap().putError(ERROR_PATH, RiceKeyConstants.ERROR_EMPTY_ENTRY, new String[] {"Member"});
            return false;
        }
    	if(!validAssignRole(newMember, document))
    		return false;
		AttributeSet validationErrors = new AttributeSet();
        KimTypeService kimTypeService = KIMServiceLocatorWeb.getKimTypeService(document.getKimType());
        
        Long newMemberFromTime = newMember.getActiveFromDate() == null ? 0L : newMember.getActiveFromDate().getTime();
        Long newMemberToTime = newMember.getActiveToDate() == null ? Long.MAX_VALUE : newMember.getActiveToDate().getTime();
        
        boolean attributesUnique;
		AttributeSet errorsAttributesAgainstExisting;
	    int i = 0;
	    AttributeSet newMemberQualifiers;
	    AttributeSet oldMemberQualifiers;
	    for (KimDocumentRoleMember member: document.getMembers()){
	    	Long memberFromTime = member.getActiveFromDate() == null ? 0L : member.getActiveFromDate().getTime();
            Long memberToTime = member.getActiveToDate() == null ? Long.MAX_VALUE : member.getActiveToDate().getTime();
	    	newMemberQualifiers = attributeValidationHelper.convertQualifiersToMap(newMember.getQualifiers());
	    	oldMemberQualifiers = attributeValidationHelper.convertQualifiersToMap(member.getQualifiers());
	    	errorsAttributesAgainstExisting = kimTypeService.validateAttributesAgainstExisting(
	    			document.getKimType().getId(), newMemberQualifiers, oldMemberQualifiers);
			validationErrors.putAll( 
					attributeValidationHelper.convertErrorsForMappedFields(ERROR_PATH, errorsAttributesAgainstExisting));

	    	attributesUnique = kimTypeService.validateUniqueAttributes(
	    			document.getKimType().getId(), newMemberQualifiers, oldMemberQualifiers);
	    	if (!attributesUnique && (member.getMemberId().equals(newMember.getMemberId()) && 
	    			member.getMemberTypeCode().equals(newMember.getMemberTypeCode()))
	    			&& ((newMemberFromTime >= memberFromTime && newMemberFromTime < memberToTime) 
        					|| (newMemberToTime >= memberFromTime && newMemberToTime <= memberToTime))
	    	){
	            rulePassed = false;
	            GlobalVariables.getMessageMap().putError(ERROR_PATH, RiceKeyConstants.ERROR_DUPLICATE_ENTRY, new String[] {"Member"});
	            break;
	    	}
	    	i++;
	    }
	    
        if ( kimTypeService != null && !newMember.isRole()) {
    		AttributeSet localErrors = kimTypeService.validateAttributes( document.getKimType().getId(), attributeValidationHelper.convertQualifiersToMap( newMember.getQualifiers() ) );
	        validationErrors.putAll( attributeValidationHelper.convertErrors("member" ,attributeValidationHelper.convertQualifiersToAttrIdxMap(newMember.getQualifiers()),localErrors) );
        }
    	if (!validationErrors.isEmpty()) {
    		attributeValidationHelper.moveValidationErrorsToErrorMap(validationErrors);
    		rulePassed = false;
    	}

		return rulePassed;
	} 

	protected boolean validAssignRole(KimDocumentRoleMember roleMember, IdentityManagementRoleDocument document){
        boolean rulePassed = true;
		if(StringUtils.isNotEmpty(document.getRoleNamespace())){
			Map<String,String> roleDetails = new HashMap<String,String>();
			roleDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE, document.getRoleNamespace());
			roleDetails.put(KimConstants.AttributeConstants.ROLE_NAME, document.getRoleName());
			if (!getDocumentHelperService().getDocumentAuthorizer(document).isAuthorizedByTemplate(
					document, 
					KimConstants.NAMESPACE_CODE, 
					KimConstants.PermissionTemplateNames.ASSIGN_ROLE, 
					GlobalVariables.getUserSession().getPerson().getPrincipalId(), 
					roleDetails, null)){
	            GlobalVariables.getMessageMap().putError(ERROR_PATH, RiceKeyConstants.ERROR_ASSIGN_ROLE, 
	            		new String[] {document.getRoleNamespace(), document.getRoleName()});
	            rulePassed = false;
			}
		}
		return rulePassed;
	}

}
