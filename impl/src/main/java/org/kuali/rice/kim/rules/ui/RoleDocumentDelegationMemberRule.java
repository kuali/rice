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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegation;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegationMember;
import org.kuali.rice.kim.document.IdentityManagementRoleDocument;
import org.kuali.rice.kim.rule.event.ui.AddDelegationMemberEvent;
import org.kuali.rice.kim.rule.ui.AddDelegationMemberRule;
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

	private static final String ERROR_PATH = "member.memberId";

	public boolean processAddDelegationMember(AddDelegationMemberEvent addDelegationMemberEvent){
		RoleDocumentDelegationMember newMember = addDelegationMemberEvent.getDelegationMember();
		IdentityManagementRoleDocument document = (IdentityManagementRoleDocument)addDelegationMemberEvent.getDocument();
	    boolean rulePassed = true;

        if (newMember == null || StringUtils.isBlank(newMember.getMemberId())){
            GlobalVariables.getErrorMap().putError(ERROR_PATH, RiceKeyConstants.ERROR_EMPTY_ENTRY, new String[] {"Delegation Member"});
            return false;
        }
        for(RoleDocumentDelegation delegation: document.getDelegations()){
    	    for (RoleDocumentDelegationMember member: delegation.getMembers()){
    	    	if (member.getMemberId().equals(newMember.getMemberId())){
    	            rulePassed = false;
    	            GlobalVariables.getErrorMap().putError(ERROR_PATH, RiceKeyConstants.ERROR_DUPLICATE_ENTRY, new String[] {"Delegation Member"});
    	    	}
    	    }
        }
		return rulePassed;
	} 

}