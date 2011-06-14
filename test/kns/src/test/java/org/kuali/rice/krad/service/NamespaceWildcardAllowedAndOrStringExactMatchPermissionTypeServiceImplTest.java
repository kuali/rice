/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.rice.krad.service;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.krad.service.impl.NamespaceWildcardAllowedAndOrStringExactMatchPermissionTypeServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class NamespaceWildcardAllowedAndOrStringExactMatchPermissionTypeServiceImplTest {

    NamespaceWildcardAllowedAndOrStringExactMatchPermissionTypeServiceImpl permissionService;
    
    @Before
    public void setUp() throws Exception {
        permissionService = 
            new NamespaceWildcardAllowedAndOrStringExactMatchPermissionTypeServiceImpl();
        
        permissionService.setExactMatchStringAttributeName("actionClass");
        permissionService.setNamespaceRequiredOnStoredAttributeSet(false);
        permissionService.setCheckRequiredAttributes(true);
    }

    @Test
    public void testIngesterPermissionExampleLikeRice() {
        AttributeSet requestedDetails = getUseIngesterRequestedDetails();
        
        List<KimPermissionInfo> permissionsList = new ArrayList<KimPermissionInfo>();

        permissionsList.add(createPermission("Use Screen", "KR-SYS", "namespaceCode=KR*"));
        KimPermissionInfo exactMatch = createPermission("Use Screen", "KR-WKFLW", "actionClass=org.kuali.rice.kew.batch.web.IngesterAction", "namespaceCode=KR-WKFLW");
        permissionsList.add(exactMatch);
       
        List<KimPermissionInfo> returnedPermissions = permissionService.getMatchingPermissions(requestedDetails, permissionsList);
        assertTrue(returnedPermissions.size() == 1);
        assertTrue(returnedPermissions.get(0).equals(exactMatch));
    }
    
    /**
     * This is my best guess for the data described in KULRICE-3770
     */
    @Test
    public void testIngesterPermissionExampleLikeKFS() {
        AttributeSet requestedDetails = getUseIngesterRequestedDetails();
        
        List<KimPermissionInfo> permissionsList = new ArrayList<KimPermissionInfo>();

        permissionsList.add(createPermission("Use Screen", "KR-SYS", "namespaceCode=KR*"));
        KimPermissionInfo exactMatch = createPermission("Use Screen", "KR-WKFLW", "actionClass=org.kuali.rice.kew.batch.web.IngesterAction");
        permissionsList.add(exactMatch);
       
        List<KimPermissionInfo> returnedPermissions = permissionService.getMatchingPermissions(requestedDetails, permissionsList);
        assertTrue(returnedPermissions.size() == 1);
        assertTrue(returnedPermissions.get(0).equals(exactMatch));
    }
    
    /**
     * This method recreates the requested details that would be encountered when accessing 
     * the xml ingester screen.
     */
    private AttributeSet getUseIngesterRequestedDetails() {
        AttributeSet requestedDetails = new AttributeSet();
        requestedDetails.put("actionClass", "org.kuali.rice.kew.batch.web.IngesterAction");
        requestedDetails.put("namespaceCode", "KR-WKFLW");
        return requestedDetails;
    }

    /**
     * @return a KimPermissionInfo object for the given name, namespace, and varargs "=" delimited attributes
     */
    private KimPermissionInfo createPermission(String name, String namespace, String ... attrs) {
        KimPermissionInfo perm = new KimPermissionInfo();;

        perm.setName(name);
        perm.setNamespaceCode(namespace);

        AttributeSet permissionDetails = new AttributeSet();
        
        for (String attr : attrs) {
            String [] splitAttr = attr.split("=", 2);
            permissionDetails.put(splitAttr[0], splitAttr[1]);
        }
        
        perm.setDetails(permissionDetails);
        return perm;
    }

}
