/**
 * Copyright 2005-2017 The Kuali Foundation
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
import org.kuali.rice.kim.rule.ui.AddPersonDelegationMemberRule;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.BusinessRule;
import org.kuali.rice.krad.rules.rule.event.DocumentEventBase;

/**
 * This is a description of what this class does - shyu don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class AddPersonDelegationMemberEvent extends DocumentEventBase {
	private RoleDocumentDelegationMember delegationMember;

	public AddPersonDelegationMemberEvent(String errorPathPrefix, IdentityManagementPersonDocument document) {
        super("adding Delegation Member document " + getDocumentId(document), errorPathPrefix, document);
    }

    public AddPersonDelegationMemberEvent(String errorPathPrefix, Document document, RoleDocumentDelegationMember member) {
        this(errorPathPrefix, (IdentityManagementPersonDocument) document);
        this.delegationMember = KradDataServiceLocator.getDataObjectService().copyInstance(member);
    }

    @Override
    public Class<? extends BusinessRule> getRuleInterfaceClass() {
        return AddPersonDelegationMemberRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AddPersonDelegationMemberRule) rule).processAddPersonDelegationMember(this);
    }

	/**
	 * @return the delegationMember
	 */
	public RoleDocumentDelegationMember getDelegationMember() {
		return this.delegationMember;
	}

	/**
	 * @param delegationMember the delegationMember to set
	 */
	public void setDelegationMember(RoleDocumentDelegationMember delegationMember) {
		this.delegationMember = delegationMember;
	}


}
