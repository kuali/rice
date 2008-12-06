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
package org.kuali.rice.kew.identity.service.impl;

import org.kuali.rice.kew.dto.GroupIdDTO;
import org.kuali.rice.kew.identity.service.IdentityHelperService;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.service.KIMServiceLocator;

/**
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class IdentityHelperServiceImpl implements IdentityHelperService {
		
	public KimGroup getGroup(GroupIdDTO groupId) {
		if (groupId.getGroupId() != null) {
			return KIMServiceLocator.getIdentityManagementService().getGroup(groupId.getGroupId());
		} else {
			return KIMServiceLocator.getIdentityManagementService().getGroupByName(groupId.getNamespace(), groupId.getGroupName());
		}
	}
	
}
