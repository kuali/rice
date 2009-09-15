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
package org.kuali.rice.kim.rule.event.ui;

import org.kuali.rice.kim.bo.ui.RoleDocumentDelegationMember;
import org.kuali.rice.kim.document.IdentityManagementPersonDocument;
import org.kuali.rice.kim.rule.ui.AddPersonDocumentDelegationMemberQualifierRule;
import org.kuali.rice.kim.rule.ui.AddPersonDocumentRoleQualifierRule;
import org.kuali.rice.kns.rule.BusinessRule;
import org.kuali.rice.kns.rule.event.KualiDocumentEventBase;

/**
 * This is a description of what this class does - wliang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class AddPersonDocumentDelegationMemberQualifierEvent extends KualiDocumentEventBase {

	private IdentityManagementPersonDocument document;
	private RoleDocumentDelegationMember delegationMember;
		
	public AddPersonDocumentDelegationMemberQualifierEvent(String errorPathPrefix, IdentityManagementPersonDocument document, 
			RoleDocumentDelegationMember delegationMember) {
        super("adding delegationMember qualifiers to person document " + getDocumentId(document), errorPathPrefix, document);
        this.document = document; 
        this.delegationMember = delegationMember;
    }

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
	 */
	public Class getRuleInterfaceClass() {
		return AddPersonDocumentDelegationMemberQualifierRule.class;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.rice.kns.rule.BusinessRule)
	 */
	public boolean invokeRuleMethod(BusinessRule rule) {
		return ((AddPersonDocumentDelegationMemberQualifierRule) rule).processAddPersonDocumentDelegationMemberQualifier(delegationMember, document);
	}

}
