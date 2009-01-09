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
package org.kuali.rice.kew.actionrequest;

import org.kuali.rice.kew.user.Recipient;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.service.KIMServiceLocator;

/**
 * Represents an ActionRequest recipient who is a KimGroup
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimGroupRecipient implements Recipient {

	private static final long serialVersionUID = 1L;
	private KimGroup group;
	
	public KimGroupRecipient(String groupId) {
		this(KIMServiceLocator.getIdentityManagementService().getGroup(groupId));
	}
		
	public KimGroupRecipient(KimGroup group) {
		if (group == null) {
			throw new IllegalArgumentException("Attempted to create a KimGroupRecipient with a null KimGroup!");
		}
		this.group = group;
	}
	
	public String getDisplayName() {
		return getGroup().getGroupName();
	}
	
	public KimGroup getGroup() {
		return this.group;
	}
	
	public String getGroupId() {
		return getGroup().getGroupId();
	}
	

}
