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
import org.kuali.rice.kns.util.KNSConstants;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocumentTypeAndExistingRecordsOnlyPermissionTypeServiceImpl extends
                DocumentTypePermissionTypeServiceImpl {

    protected List<String> inputRequiredAttributes = new ArrayList<String>();
//    protected List<String> storedRequiredAttributes = new ArrayList<String>();

    {
        inputRequiredAttributes.add(KimAttributes.DOCUMENT_TYPE_NAME);
		inputRequiredAttributes.add(KNSConstants.MAINTENANCE_ACTN);
		
//        storedRequiredAttributes.add(KimAttributes.DOCUMENT_TYPE_NAME);
//		storedRequiredAttributes.add(KimAttributes.EXISTING_RECORDS_ONLY);
	}
    
    
    /**
     * @see org.kuali.rice.kns.service.impl.DocumentTypePermissionTypeServiceImpl#performPermissionMatches(org.kuali.rice.kim.bo.types.dto.AttributeSet, java.util.List)
     */
    @Override
    public List<KimPermissionInfo> performPermissionMatches(AttributeSet requestedDetails,
    		List<KimPermissionInfo> permissionsList) {
	    List<KimPermissionInfo> matchingPermissions = new ArrayList<KimPermissionInfo>();
	    String maintenanceAction = requestedDetails.get(KNSConstants.MAINTENANCE_ACTN);
	    // filter the permissions based on the maintenance action requested before checking
	    // the document hierarchy
	    for ( KimPermissionInfo kpi : permissionsList ) {
	    	if ( Boolean.parseBoolean(kpi.getDetails().get(KimAttributes.EXISTING_RECORDS_ONLY) ) ) {
	    		if ( StringUtils.equals( maintenanceAction, KNSConstants.MAINTENANCE_ACTN ) ) {
		            matchingPermissions.add(kpi);
	    		}
	    	} else {
	            matchingPermissions.add(kpi);
	        }
	    }
	    return super.performPermissionMatches( requestedDetails, matchingPermissions );
    }
    
}
