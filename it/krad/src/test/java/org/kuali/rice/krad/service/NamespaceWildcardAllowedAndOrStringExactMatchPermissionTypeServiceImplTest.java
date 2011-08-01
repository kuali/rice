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
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeDataBo;
import org.kuali.rice.kim.impl.permission.PermissionAttributeBo;
import org.kuali.rice.kim.impl.permission.PermissionBo;
import org.kuali.rice.krad.service.impl.NamespaceWildcardAllowedAndOrStringExactMatchPermissionTypeServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class NamespaceWildcardAllowedAndOrStringExactMatchPermissionTypeServiceImplTest {

    NamespaceWildcardAllowedAndOrStringExactMatchPermissionTypeServiceImpl permissionService;
    
    @Before
    public void setUp() throws Exception {
        permissionService = 
            new NamespaceWildcardAllowedAndOrStringExactMatchPermissionTypeServiceImpl() {
                @Override protected boolean isCheckRequiredAttributes() {
                    return true;
                }
            };
        
        permissionService.setExactMatchStringAttributeName("actionClass");
        permissionService.setNamespaceRequiredOnStoredMap(false);

    }

    @Test
    public void testIngesterPermissionExampleLikeRice() {
        Map<String, String> requestedDetails = getUseIngesterRequestedDetails();
        
        List<PermissionBo> permissionsList = new ArrayList<PermissionBo>();

        permissionsList.add(createPermission("Use Screen", "KR-SYS", "namespaceCode=KR*"));
        PermissionBo exactMatch = createPermission("Use Screen", "KR-WKFLW", "actionClass=org.kuali.rice.kew.batch.web.IngesterAction", "namespaceCode=KR-WKFLW");
        permissionsList.add(exactMatch);

        List<Permission> immutablePermissionList = new ArrayList<Permission>();
        for (PermissionBo bo : permissionsList) { immutablePermissionList.add(PermissionBo.to(bo));}
       
        List<Permission> returnedPermissions = permissionService.getMatchingPermissions(requestedDetails, immutablePermissionList);
        assertTrue(returnedPermissions.size() == 1);
        assertTrue(returnedPermissions.get(0).equals(exactMatch));
    }
    
    /**
     * This is my best guess for the data described in KULRICE-3770
     */
    @Test
    public void testIngesterPermissionExampleLikeKFS() {
        Map<String, String> requestedDetails = getUseIngesterRequestedDetails();
        
        List<PermissionBo> permissionsList = new ArrayList<PermissionBo>();

        permissionsList.add(createPermission("Use Screen", "KR-SYS", "namespaceCode=KR*"));
        PermissionBo exactMatch = createPermission("Use Screen", "KR-WKFLW", "actionClass=org.kuali.rice.kew.batch.web.IngesterAction");
        permissionsList.add(exactMatch);

        List<Permission> immutablePermissionList = new ArrayList<Permission>();
        for (PermissionBo bo : permissionsList) { immutablePermissionList.add(PermissionBo.to(bo));}
       
        List<Permission> returnedPermissions = permissionService.getMatchingPermissions(requestedDetails, immutablePermissionList);
        assertTrue(returnedPermissions.size() == 1);
        assertTrue(returnedPermissions.get(0).equals(exactMatch));
    }
    
    /**
     * This method recreates the requested details that would be encountered when accessing 
     * the xml ingester screen.
     */
    private Map<String, String> getUseIngesterRequestedDetails() {
        Map<String, String> requestedDetails = new HashMap<String, String>();
        requestedDetails.put("actionClass", "org.kuali.rice.kew.batch.web.IngesterAction");
        requestedDetails.put("namespaceCode", "KR-WKFLW");
        return requestedDetails;
    }

    /**
     * @return a KimPermissionInfo object for the given name, namespace, and varargs "=" delimited attributes
     */
    private PermissionBo createPermission(String name, String namespace, String ... attrs) {
        PermissionBo perm = new PermissionBo();

        perm.setName(name);
        perm.setNamespaceCode(namespace);

        Map<String,String> permissionDetails = new HashMap<String,String>();
        
        for (String attr : attrs) {
            String [] splitAttr = attr.split("=", 2);
            permissionDetails.put(splitAttr[0], splitAttr[1]);
        }

        List<PermissionAttributeBo> attrBos = KimAttributeDataBo
                .createFrom(PermissionAttributeBo.class, perm.getAttributes(),
                        perm.getTemplate().getKimTypeId());

        perm.setAttributeDetails(attrBos);
        return perm;
    }

}
