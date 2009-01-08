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
import org.kuali.rice.kim.service.impl.NamespacePermissionTypeServiceImpl;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NamespaceWildcardAllowedAndOrStringExactMatchPermissionTypeServiceImpl
		extends NamespacePermissionTypeServiceImpl {
	protected String exactMatchStringAttributeName;
	protected boolean namespaceRequiredOnStoredAttributeSet;

	@Override
	protected List<KimPermissionInfo> performPermissionMatches(AttributeSet requestedDetails, List<KimPermissionInfo> permissionsList) {
	    List<KimPermissionInfo> matchingPermissions = new ArrayList<KimPermissionInfo>();
        List<KimPermissionInfo> matchingBlankPermissions = new ArrayList<KimPermissionInfo>();
	    String requestedAttributeValue = requestedDetails.get(exactMatchStringAttributeName);
	    for ( KimPermissionInfo kpi : permissionsList ) {
	        String permissionAttributeValue = kpi.getDetails().get(exactMatchStringAttributeName);
	        if ( StringUtils.equals(requestedAttributeValue, permissionAttributeValue) ) {
	            matchingPermissions.add(kpi);
	        } else if ( StringUtils.isBlank(permissionAttributeValue) ) {
	            matchingBlankPermissions.add(kpi);
	        }
	    }
	    // if the exact match worked, use those when checking the namespace
	    // otherwise, use those with a blank additional property value
	    if ( !matchingPermissions.isEmpty() ) {
            List<KimPermissionInfo> matchingWithNamespace = super.performPermissionMatches(requestedDetails, matchingPermissions);
	        if ( !namespaceRequiredOnStoredAttributeSet ) {
	            // if the namespace is not required and the namespace match would have excluded
	            // the results, return the original set of matches
	            if ( matchingWithNamespace.isEmpty() ) {
	                return matchingPermissions;
	            }
	        }
            return matchingWithNamespace;
	    } else if ( !matchingBlankPermissions.isEmpty() ) {
            List<KimPermissionInfo> matchingWithNamespace = super.performPermissionMatches(requestedDetails, matchingBlankPermissions);
            if ( !namespaceRequiredOnStoredAttributeSet ) {
                // if the namespace is not required and the namespace match would have excluded
                // the results, return the original set of matches
                if ( matchingWithNamespace.isEmpty() ) {
                    return matchingBlankPermissions;
                }
            }
            return matchingWithNamespace;
	    }
	    return matchingPermissions; // will be empty if drops to here
	}
	
	public void setExactMatchStringAttributeName(
			String exactMatchStringAttributeName) {
		this.exactMatchStringAttributeName = exactMatchStringAttributeName;
		requiredAttributes.add(exactMatchStringAttributeName);
	}

	public void setNamespaceRequiredOnStoredAttributeSet(
			boolean namespaceRequiredOnStoredAttributeSet) {
		this.namespaceRequiredOnStoredAttributeSet = namespaceRequiredOnStoredAttributeSet;
	}
}