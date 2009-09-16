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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleMember;
import org.kuali.rice.kim.document.IdentityManagementRoleDocument;
import org.kuali.rice.kim.document.rule.AttributeValidationHelper;
import org.kuali.rice.kim.rule.event.ui.AddMemberEvent;
import org.kuali.rice.kim.rule.ui.AddMemberRule;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.rules.DocumentRuleBase;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.RiceKeyConstants;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KimDocumentMemberRule extends DocumentRuleBase implements AddMemberRule {

	private static final String ERROR_PATH = "document.member.memberId";

	private AttributeValidationHelper attributeValidationHelper = new AttributeValidationHelper();
	
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
        KimTypeService kimTypeService = KimCommonUtils.getKimTypeService( document.getKimType() );

		boolean attributesUnique;
		AttributeSet errorsAttributesAgainstExisting;
	    int i = 0;
	    AttributeSet newMemberQualifiers;
	    AttributeSet oldMemberQualifiers;
	    for (KimDocumentRoleMember member: document.getMembers()){
	    	newMemberQualifiers = attributeValidationHelper.convertQualifiersToMap(newMember.getQualifiers());
	    	oldMemberQualifiers = attributeValidationHelper.convertQualifiersToMap(member.getQualifiers());
	    	errorsAttributesAgainstExisting = kimTypeService.validateAttributesAgainstExisting(
	    			document.getKimType().getKimTypeId(), newMemberQualifiers, oldMemberQualifiers);
			validationErrors.putAll( 
					attributeValidationHelper.convertErrorsForMappedFields("member.memberId", errorsAttributesAgainstExisting));

	    	attributesUnique = kimTypeService.validateUniqueAttributes(
	    			document.getKimType().getKimTypeId(), newMemberQualifiers, oldMemberQualifiers);
	    	if (!attributesUnique && (member.getMemberId().equals(newMember.getMemberId()) && 
	    			member.getMemberTypeCode().equals(newMember.getMemberTypeCode()))){
	            rulePassed = false;
	            GlobalVariables.getMessageMap().putError("member.memberId", RiceKeyConstants.ERROR_DUPLICATE_ENTRY, new String[] {"Member"});
	            break;
	    	}
	    	i++;
	    }
	    
        if ( kimTypeService != null ) {
    		AttributeSet localErrors = kimTypeService.validateAttributes( document.getKimType().getKimTypeId(), attributeValidationHelper.convertQualifiersToMap( newMember.getQualifiers() ) );
	        validationErrors.putAll( attributeValidationHelper.convertErrors("member" ,attributeValidationHelper.convertQualifiersToAttrIdxMap(newMember.getQualifiers()),localErrors) );
        }
    	if (!validationErrors.isEmpty()) {
    		attributeValidationHelper.moveValidationErrorsToErrorMap(validationErrors);
    		rulePassed = false;
    	}

		return rulePassed;
	} 

	private boolean validAssignRole(KimDocumentRoleMember roleMember, IdentityManagementRoleDocument document){
        boolean rulePassed = true;
		if(StringUtils.isNotEmpty(document.getRoleNamespace())){
			Map<String,String> roleDetails = new HashMap<String,String>();
			roleDetails.put(KimAttributes.NAMESPACE_CODE, document.getRoleNamespace());
			roleDetails.put(KimAttributes.ROLE_NAME, document.getRoleName());
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
