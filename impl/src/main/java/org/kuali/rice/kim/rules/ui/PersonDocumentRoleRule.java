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
package org.kuali.rice.kim.rules.ui;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.ui.PersonDocumentRole;
import org.kuali.rice.kim.document.IdentityManagementPersonDocument;
import org.kuali.rice.kim.rule.event.ui.AddRoleEvent;
import org.kuali.rice.kim.rule.ui.AddRoleRule;
import org.kuali.rice.kns.rules.DocumentRuleBase;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.RiceKeyConstants;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class PersonDocumentRoleRule extends DocumentRuleBase implements AddRoleRule {
    private static final String ERROR_PATH = "newRole.roleId";

	public boolean processAddRole(AddRoleEvent addRoleEvent) {
		IdentityManagementPersonDocument document = (IdentityManagementPersonDocument)addRoleEvent.getDocument();
		PersonDocumentRole newRole = addRoleEvent.getRole();
	    boolean rulePassed = true;
        ErrorMap errorMap = GlobalVariables.getErrorMap();

        if (StringUtils.isBlank(newRole.getRoleId())) {
            rulePassed = false;
            errorMap.putError(ERROR_PATH, RiceKeyConstants.ERROR_EMPTY_ENTRY, new String[] {"Role"});
        	
        } else {
		    for (PersonDocumentRole role : document.getRoles()) {
		    	if (role.getRoleId().equals(newRole.getRoleId())) {
		            rulePassed = false;
		            errorMap.putError(ERROR_PATH, RiceKeyConstants.ERROR_DUPLICATE_ENTRY, new String[] {"Role"});
		    		
		    	}
		    }
        }
		return rulePassed;
	} 


}
