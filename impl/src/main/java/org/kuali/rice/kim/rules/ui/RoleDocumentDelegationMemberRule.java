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
 * See the License for the specific language governing members and
 * limitations under the License.
 */
package org.kuali.rice.kim.rules.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleMember;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegation;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegationMember;
import org.kuali.rice.kim.document.IdentityManagementRoleDocument;
import org.kuali.rice.kim.document.rule.AttributeValidationHelper;
import org.kuali.rice.kim.rule.event.ui.AddDelegationMemberEvent;
import org.kuali.rice.kim.rule.ui.AddDelegationMemberRule;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kns.rules.DocumentRuleBase;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.RiceKeyConstants;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class RoleDocumentDelegationMemberRule extends DocumentRuleBase implements AddDelegationMemberRule {

	public static final String ERROR_PATH = "document.delegationMember.memberId";

	private AttributeValidationHelper attributeValidationHelper = new AttributeValidationHelper();
	
	public boolean processAddDelegationMember(AddDelegationMemberEvent addDelegationMemberEvent){
		RoleDocumentDelegationMember newMember = addDelegationMemberEvent.getDelegationMember();
		IdentityManagementRoleDocument document = (IdentityManagementRoleDocument)addDelegationMemberEvent.getDocument();
	    boolean rulePassed = true;
        if(newMember == null || StringUtils.isBlank(newMember.getMemberId())){
            GlobalVariables.getErrorMap().putError(ERROR_PATH, RiceKeyConstants.ERROR_EMPTY_ENTRY, new String[] {"Delegation Member"});
            return false;
        }
        if(StringUtils.isBlank(newMember.getRoleMemberId())){
            GlobalVariables.getErrorMap().putError(ERROR_PATH, RiceKeyConstants.ERROR_EMPTY_ENTRY, new String[] {"Role Member"});
            return false;
        }
		List<AttributeSet> attributeSetListToValidate = new ArrayList<AttributeSet>();
		AttributeSet attributeSetToValidate;
		AttributeSet validationErrors = new AttributeSet();
        KimTypeService kimTypeService = KimCommonUtils.getKimTypeService( document.getKimType() );

		for(RoleDocumentDelegationMember roleMember: document.getDelegationMembers()) {
			attributeSetToValidate = attributeValidationHelper.convertQualifiersToMap(roleMember.getQualifiers());
			attributeSetListToValidate.add(attributeSetToValidate);
    	}
		boolean attributesUnique;

	    int i = 0;
	    for (RoleDocumentDelegationMember member: document.getDelegationMembers()){
	    	attributesUnique = kimTypeService.validateUniqueAttributes(
					document.getKimType().getKimTypeId(), 
					attributeValidationHelper.convertQualifiersToMap(newMember.getQualifiers()), 
					attributeValidationHelper.convertQualifiersToMap(member.getQualifiers()));
	    	if (!attributesUnique && (member.getMemberId().equals(newMember.getMemberId()) && 
	    			member.getMemberTypeCode().equals(newMember.getMemberTypeCode()))){
	            rulePassed = false;
	            GlobalVariables.getErrorMap().putError("delegationMember.memberId", RiceKeyConstants.ERROR_DUPLICATE_ENTRY, new String[] {"Delegation Member"});
	            break;
	    	}
	    	i++;
	    }
        
        if ( kimTypeService != null ) {
    		AttributeSet localErrors = kimTypeService.validateAttributes( attributeValidationHelper.convertQualifiersToMap( newMember.getQualifiers() ) );
	        validationErrors.putAll( attributeValidationHelper.convertErrors("delegationMember" ,attributeValidationHelper.convertQualifiersToAttrIdxMap(newMember.getQualifiers()),localErrors) );
        }
    	if (!validationErrors.isEmpty()) {
    		attributeValidationHelper.moveValidationErrorsToErrorMap(validationErrors);
    		rulePassed = false;
    	}
		return rulePassed;
	} 

}