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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimPermissionTypeServiceBase;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ComponentFieldPermissionTypeServiceImpl extends KimPermissionTypeServiceBase {

	{
		requiredAttributes.add(KimAttributes.COMPONENT_NAME);
		requiredAttributes.add(KimAttributes.PROPERTY_NAME);
	}
	
	/**
	 * Compare the component and property names between the request and matching permissions.
	 * Make entries with a matching property name take precedence over those with blank property 
	 * names on the stored permissions.  Only match entries with blank property names if
	 * no entries match on the exact property name. 
	 * 
	 * @see org.kuali.rice.kim.service.support.impl.KimPermissionTypeServiceBase#performPermissionMatches(org.kuali.rice.kim.bo.types.dto.AttributeSet, java.util.List)
	 */
	@Override
	protected List<KimPermissionInfo> performPermissionMatches(AttributeSet requestedDetails,
			List<KimPermissionInfo> permissionsList) {
		List<KimPermissionInfo> propertyMatches = new ArrayList<KimPermissionInfo>();
		List<KimPermissionInfo> blankPropertyMatches = new ArrayList<KimPermissionInfo>();
		String propertyName = requestedDetails.get(KimAttributes.PROPERTY_NAME);
		String componentName = requestedDetails.get(KimAttributes.COMPONENT_NAME);
		for ( KimPermissionInfo kpi : permissionsList ) {
			if ( StringUtils.equals( componentName, kpi.getDetails().get( KimAttributes.COMPONENT_NAME ) ) ) {
				String permPropertyName = kpi.getDetails().get(KimAttributes.PROPERTY_NAME);
				if ( StringUtils.isBlank( permPropertyName ) ) {
					blankPropertyMatches.add( kpi );
				} else if ( StringUtils.equals( propertyName, permPropertyName ) ) {
					propertyMatches.add( kpi );
				}
			}
		}
		if ( !propertyMatches.isEmpty() ) {
			return propertyMatches;
		} else {
			return blankPropertyMatches;
		}
	}

}
