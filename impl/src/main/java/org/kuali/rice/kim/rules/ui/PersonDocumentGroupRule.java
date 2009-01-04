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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.ui.PersonDocumentGroup;
import org.kuali.rice.kim.document.IdentityManagementPersonDocument;
import org.kuali.rice.kim.rule.event.ui.AddGroupEvent;
import org.kuali.rice.kim.rule.ui.AddGroupRule;
import org.kuali.rice.kim.service.KIMServiceLocator;
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
public class PersonDocumentGroupRule extends DocumentRuleBase implements AddGroupRule {
    private static final String NEW_GROUP = "newGroup";

	public boolean processAddGroup(AddGroupEvent addGroupEvent) {
		IdentityManagementPersonDocument document = (IdentityManagementPersonDocument)addGroupEvent.getDocument();
		PersonDocumentGroup newGroup = addGroupEvent.getGroup();
	    boolean rulePassed = true;
	    String errorPath = NEW_GROUP;
        ErrorMap errorMap = GlobalVariables.getErrorMap();
    	List<String> groupIds = KIMServiceLocator.getUiDocumentService().getPopulatableGroupIds();

        if (newGroup == null || StringUtils.isBlank(newGroup.getGroupId())) {
            rulePassed = false;
            errorMap.putError(errorPath+".groupId", RiceKeyConstants.ERROR_EMPTY_ENTRY, new String[] {"Group"});
        	
        } else if (groupIds.isEmpty() || !groupIds.contains(newGroup.getGroupId())) {
            errorMap.putError(errorPath+".groupId", RiceKeyConstants.ERROR_POPULATE_GROUP, new String[] {newGroup.getGroupId()});
            rulePassed = false;
        } else  {
		    for (PersonDocumentGroup group : document.getGroups()) {
		    	if (group.getGroupId().equals(newGroup.getGroupId())) {
		            rulePassed = false;
		            errorMap.putError(errorPath+".groupId", RiceKeyConstants.ERROR_DUPLICATE_ENTRY, new String[] {"Group"});
		    		
		    	}
		    }
        }
		return rulePassed;
	} 

}
