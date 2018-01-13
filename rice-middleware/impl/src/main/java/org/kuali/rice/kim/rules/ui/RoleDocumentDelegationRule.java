/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.kim.rules.ui;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegation;
import org.kuali.rice.kim.rule.event.ui.AddDelegationEvent;
import org.kuali.rice.kim.rule.ui.AddDelegationRule;
import org.kuali.rice.kns.rules.DocumentRuleBase;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class RoleDocumentDelegationRule extends DocumentRuleBase implements AddDelegationRule {

	protected static final String ERROR_PATH = "permission.permissionId";
	
	public boolean processAddDelegation(AddDelegationEvent addDelegationEvent) {
		RoleDocumentDelegation newDelegation = addDelegationEvent.getDelegation();
		boolean rulePassed = true;
		if (newDelegation == null || StringUtils.isBlank(newDelegation.getDelegationTypeCode())) {
            rulePassed = false;
            GlobalVariables.getMessageMap().putError(ERROR_PATH, RiceKeyConstants.ERROR_EMPTY_ENTRY, new String[] {"Permission"});
        }
		return rulePassed;
	} 

}
