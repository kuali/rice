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
public class NamespacePermissionTypeServiceImpl extends
		KimPermissionTypeServiceBase {

	{
		requiredAttributes.add(KimAttributes.NAMESPACE_CODE);
	}
	
	/**
	 * Check for entries that match the namespace.  Only return the one which is the most specific. 
	 * 
	 * I.e., matches best. KR-NS will have priority over KR-*
	 * 
	 * @see org.kuali.rice.kim.service.support.impl.KimPermissionTypeServiceBase#performPermissionMatches(org.kuali.rice.kim.bo.types.dto.AttributeSet, java.util.List)
	 */
	@Override
	protected List<KimPermissionInfo> performPermissionMatches(AttributeSet requestedDetails, List<KimPermissionInfo> permissionsList) {
        KimPermissionInfo exactMatchPerm = null;
        KimPermissionInfo partialMatchPerm = null;
        String requestedNamespaceCode = requestedDetails.get(KimAttributes.NAMESPACE_CODE);
        for ( KimPermissionInfo kpi : permissionsList ) {
            String permissionNamespaceCode = kpi.getDetails().get(KimAttributes.NAMESPACE_CODE);
            if ( StringUtils.equals(requestedNamespaceCode, permissionNamespaceCode ) ) {
                exactMatchPerm = kpi;
                // if an exact match, there's no need to search any further
                break;
            } else if ( requestedNamespaceCode != null
                    && permissionNamespaceCode != null
                    && requestedNamespaceCode.matches(permissionNamespaceCode.replaceAll("\\*", ".*") ) ) {
                partialMatchPerm = kpi;
                // don't break, since there still could be an exact match
            }
        }
        // return the exact match if set
        List<KimPermissionInfo> matchingPermissions = new ArrayList<KimPermissionInfo>();
        if ( exactMatchPerm != null ) {
            matchingPermissions.add( exactMatchPerm );
        } else if ( partialMatchPerm != null ) {
            matchingPermissions.add( partialMatchPerm );
        }
        
        return matchingPermissions;
	}
}
