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
package org.kuali.rice.kns.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocumentCollectionPermissionTypeServiceImpl extends DocumentTypePermissionTypeServiceImpl {

	{
		inputRequiredAttributes.add(KimAttributes.COLLECTION_ITEM_TYPE_CODE);
	}
	
	/**
	 * @see org.kuali.rice.kns.service.impl.DocumentTypePermissionTypeServiceImpl#performPermissionMatch(org.kuali.rice.kim.bo.types.dto.AttributeSet, org.kuali.rice.kim.bo.role.KimPermission)
	 */
	@Override
	protected boolean performPermissionMatch(AttributeSet requestedDetails, KimPermissionInfo permission) {
		return true;
		//TODO: Uncomment this - Commented until all the clients pass in the required attributes
		/*
		boolean match = super.performPermissionMatch(requestedDetails, permission);
		if ( !match ) {
			return false;
		}
		boolean addTemplate = 
			"Add Attachment".equals(permission.getTemplate().getName()) || "Add Note".equals(permission.getTemplate().getName());		
		if (!addTemplate && StringUtils.isEmpty(requestedDetails.get(KimAttributes.CREATE_BY_SELF_ONLY))) {
			throw new RuntimeException(KimAttributes.CREATE_BY_SELF_ONLY + " should not be blank or null.");
		}
		return match && doesCollectionItemTypecodeMatch(requestedDetails, permission.getDetails()) &&
					isCreatedBySelfOnly(requestedDetails, permission.getDetails());
		*/
	}
	
	protected boolean doesCollectionItemTypecodeMatch(AttributeSet requestedDetails, AttributeSet permissionDetails){
		if(StringUtils.isEmpty(permissionDetails.get(KimAttributes.COLLECTION_ITEM_TYPE_CODE)))
			return true;
		return StringUtils.equals(requestedDetails.get(KimAttributes.COLLECTION_ITEM_TYPE_CODE), 
				permissionDetails.get(KimAttributes.COLLECTION_ITEM_TYPE_CODE));
	}
	
	protected boolean isCreatedBySelfOnly(AttributeSet requestedDetails, AttributeSet permissionDetails){
		if(StringUtils.isEmpty(permissionDetails.get(KimAttributes.CREATE_BY_SELF_ONLY)))
			return true;
		return requestedDetails.get(KimAttributes.CREATE_BY_SELF_ONLY).equals(
				permissionDetails.get(KimAttributes.CREATE_BY_SELF_ONLY));
	}

}