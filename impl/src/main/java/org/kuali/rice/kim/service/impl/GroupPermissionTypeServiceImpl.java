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
package org.kuali.rice.kim.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;

/**
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class GroupPermissionTypeServiceImpl extends NamespacePermissionTypeServiceImpl {

	{
		inputRequiredAttributes.add(KimAttributes.GROUP_NAME);
	}
	
	/**
	 * Attributes:
	 *	Namespace Code (required, * wildcard allowed to match a set)
	 *	Group Name
	 *	
	 * Requirements:
	 *	- Both namespace code and group name should be passed in the requested details. If not, throw exception.
	 *	- * wildcard allowed to match a set in role member attr data (NamespacePermissionTypeServiceImpl will take care of it)
	 *	- If only namespace code is passed in the permission details, match that.
	 *	- If both namespace code and group name are passed in the permission details, match both. 
	 * 
	 * @see org.kuali.rice.kim.service.support.impl.KimPermissionTypeServiceBase#performPermissionMatch(org.kuali.rice.kim.bo.types.dto.AttributeSet, org.kuali.rice.kim.bo.role.KimPermission)
	 */
	protected boolean performMatch(AttributeSet inputAttributeSet, AttributeSet storedAttributeSet){
		boolean namespaceMatch = super.performMatch(inputAttributeSet, storedAttributeSet);
		return namespaceMatch && 
			doesGroupNameMatch(inputAttributeSet.get(KimAttributes.GROUP_NAME), storedAttributeSet.get(KimAttributes.GROUP_NAME));
	}
	
	protected boolean doesGroupNameMatch(String requestedDetailsGroupName, String permissionDetailsGroupName){
		if(StringUtils.isEmpty(permissionDetailsGroupName))
			return true;
		return requestedDetailsGroupName.equals(permissionDetailsGroupName);		
	}

}