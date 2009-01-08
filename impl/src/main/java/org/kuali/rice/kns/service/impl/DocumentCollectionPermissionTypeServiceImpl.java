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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocumentCollectionPermissionTypeServiceImpl extends DocumentTypePermissionTypeServiceImpl {

	{
		requiredAttributes.add(KimAttributes.COLLECTION_ITEM_TYPE_CODE);
	}
	
	/**
	 * @see org.kuali.rice.kns.service.impl.DocumentTypePermissionTypeServiceImpl#performPermissionMatches(AttributeSet, List)
	 */
	@Override
	public List<KimPermissionInfo> performPermissionMatches(AttributeSet requestedDetails,
			List<KimPermissionInfo> permissionsList) {
		return permissionsList;
		//TODO: Uncomment this - Commented until all the clients pass in the required attributes
		/*
		List<KimPermissionInfo> matchingPermissions = new ArrayList<KimPermissionInfo>();
		// loop over the permissions, checking the non-document-related ones
		for ( KimPermissionInfo kpi : permissionsList ) {
			boolean addTemplate = 
					KimConstants.PermissionTemplateNames.ADD_ATTACHMENT.equals(kpi.getTemplate().getName()) 
					|| KimConstants.PermissionTemplateNames.ADD_NOTE.equals(kpi.getTemplate().getName());		
			if (!addTemplate && StringUtils.isBlank(requestedDetails.get(KimAttributes.CREATE_BY_SELF_ONLY))) {
				throw new RuntimeException(KimAttributes.CREATE_BY_SELF_ONLY + " should not be blank or null.");
			}
			if ( doesCollectionItemTypecodeMatch(requestedDetails, kpi.getDetails() ) &&
						isCreatedBySelfOnly(requestedDetails, kpi.getDetails() ) ) {
				matchingPermissions.add( kpi );
			}			
		}
		// now, filter the list to just those for the current document
		matchingPermissions = super.performPermissionMatches( requestedDetails, matchingPermissions );
		// TODO: does this need to filter on a priority scheme on the Collection Item Type Code?
		return matchingPermissions;
		*/
	}
	
	protected boolean doesCollectionItemTypecodeMatch(AttributeSet requestedDetails, AttributeSet permissionDetails){
		if(StringUtils.isBlank(permissionDetails.get(KimAttributes.COLLECTION_ITEM_TYPE_CODE))) {
			return true;
		}
		return StringUtils.equals(requestedDetails.get(KimAttributes.COLLECTION_ITEM_TYPE_CODE), 
				permissionDetails.get(KimAttributes.COLLECTION_ITEM_TYPE_CODE));
	}
	
	protected boolean isCreatedBySelfOnly(AttributeSet requestedDetails, AttributeSet permissionDetails){
		if(StringUtils.isBlank(permissionDetails.get(KimAttributes.CREATE_BY_SELF_ONLY))) {
			return true;
		}
		return requestedDetails.get(KimAttributes.CREATE_BY_SELF_ONLY).equals(
				permissionDetails.get(KimAttributes.CREATE_BY_SELF_ONLY));
	}

}