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
package org.kuali.rice.kim.service.support.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kim.bo.role.KimPermission;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.KimPermissionTypeService;

/**
 * This is a description of what this class does - bhargavp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimPermissionTypeServiceBase extends KimTypeServiceBase implements KimPermissionTypeService {

	/**
	 * @see org.kuali.rice.kim.service.support.KimPermissionTypeService#doPermissionDetailsMatch(org.kuali.rice.kim.bo.types.dto.AttributeSet, java.util.List)
	 */
	public <E extends KimPermission> boolean doPermissionDetailsMatch(AttributeSet requestedDetails, List<E> permissionsList) {
		return performPermissionMatches(translateInputAttributeSet(requestedDetails), permissionsList);
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.support.KimPermissionTypeService#doesPermissionDetailMatch(org.kuali.rice.kim.bo.types.dto.AttributeSet, KimPermission)
	 */
	public boolean doesPermissionDetailMatch(AttributeSet requestedDetails, KimPermission permission) {
		return performPermissionMatch(translateInputAttributeSet(requestedDetails), permission);
	}

	protected <E extends KimPermission> boolean performPermissionMatches(AttributeSet requestedDetails, List<E> permissionsList) {
		List<AttributeSet> permissionDetailList = new ArrayList<AttributeSet>();
		for (KimPermission permission : permissionsList) {
			permissionDetailList.add(permission.getDetails());
		}
		return performMatches(requestedDetails, permissionDetailList);
	}

	protected boolean performPermissionMatch(AttributeSet requestedDetails, KimPermission permission) {
		return performMatch(requestedDetails, permission.getDetails());
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.support.KimPermissionTypeService#getAllImpliedDetails(org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	public List<AttributeSet> getAllImpliedDetails(AttributeSet requestedDetails) {
		// TODO bhargavp - THIS METHOD NEEDS JAVADOCS
		return null;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.support.KimPermissionTypeService#getAllImplyingDetails(org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	public List<AttributeSet> getAllImplyingDetails(
			AttributeSet requestedDetails) {
		// TODO bhargavp - THIS METHOD NEEDS JAVADOCS
		return null;
	}

}
