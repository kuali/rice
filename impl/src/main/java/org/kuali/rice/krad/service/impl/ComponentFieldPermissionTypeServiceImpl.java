/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.krad.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.mo.common.Attributes;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.service.support.impl.KimPermissionTypeServiceBase;
import org.kuali.rice.kim.util.KimConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComponentFieldPermissionTypeServiceImpl extends KimPermissionTypeServiceBase {

	{
//		requiredAttributes.add(KimAttributes.COMPONENT_NAME);
//		requiredAttributes.add(KimAttributes.PROPERTY_NAME);
	}
	
	/**
	 * Compare the component and property names between the request and matching permissions.
	 * Make entries with a matching property name take precedence over those with blank property 
	 * names on the stored permissions.  Only match entries with blank property names if
	 * no entries match on the exact property name. 
	 * 
	 * @see org.kuali.rice.kim.service.support.impl.KimPermissionTypeServiceBase#performPermissionMatches(org.kuali.rice.core.util.AttributeSet, java.util.List)
	 */
	@Override
	protected List<KimPermissionInfo> performPermissionMatches(Attributes requestedDetails,
			List<KimPermissionInfo> permissionsList) {
		List<KimPermissionInfo> propertyMatches = new ArrayList<KimPermissionInfo>();
		List<KimPermissionInfo> prefixPropertyMatches = new ArrayList<KimPermissionInfo>();
		List<KimPermissionInfo> blankPropertyMatches = new ArrayList<KimPermissionInfo>();
		String propertyName = requestedDetails.get(KimConstants.AttributeConstants.PROPERTY_NAME);
		String componentName = requestedDetails.get(KimConstants.AttributeConstants.COMPONENT_NAME);
		for ( KimPermissionInfo kpi : permissionsList ) {
			if ( StringUtils.equals( componentName, kpi.getDetails().get( KimConstants.AttributeConstants.COMPONENT_NAME ) ) ) {
				String permPropertyName = kpi.getDetails().get(KimConstants.AttributeConstants.PROPERTY_NAME);
				if ( StringUtils.isBlank( permPropertyName ) ) {
					blankPropertyMatches.add( kpi );
				} else if ( StringUtils.equals( propertyName, permPropertyName ) ) {
					propertyMatches.add( kpi );
				} else if ( doesPropertyNameMatch(propertyName, permPropertyName) ) {
					prefixPropertyMatches.add( kpi );
				}
			}
		}
		if ( !propertyMatches.isEmpty() ) {
			return propertyMatches;
		} else if ( !prefixPropertyMatches.isEmpty() ) {
			return prefixPropertyMatches;
		} else {
			return blankPropertyMatches;
		}
	}

}
