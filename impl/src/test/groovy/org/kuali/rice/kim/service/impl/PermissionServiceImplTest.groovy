package org.kuali.rice.kim.service.impl

import org.junit.Test;
import groovy.mock.interceptor.MockFor
import org.junit.Before
import org.kuali.rice.kim.api.role.RoleService
import org.kuali.rice.kim.service.PermissionService
import org.kuali.rice.krad.service.BusinessObjectService
import org.kuali.rice.kim.impl.permission.PermissionBo
import org.junit.Assert
import org.junit.BeforeClass
import org.kuali.rice.kim.dao.KimPermissionDao

/*
 * Copyright 2007-2009 The Kuali Foundation
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
class PermissionServiceImplTest {
    private MockFor mockRoleService;
    private MockFor mockBoService;
    private MockFor mockKimPermissionDao;
    private RoleService roleService;
    private BusinessObjectService boService;
    private KimPermissionDao kimPermissionDao;
    PermissionService permissionService;
    PermissionServiceImpl permissionServiceImpl;

    static Map<String, PermissionBo> samplePermissions = new HashMap<String, PermissionBo>();

    @BeforeClass
    static void createSampleBOs() {
        PermissionBo firstPermissionBo = new PermissionBo(id: "permidone", name: "permissionone", namespaceCode: "namespacecodeone", active: "Y");
        PermissionBo secondPermissionBo = new PermissionBo(id: "permidtwo", name: "permissiontwo", namespaceCode: "namespacecodetwo", active: "Y");

        for (bo in [firstPermissionBo, secondPermissionBo]) {
            samplePermissions.put(bo.id, bo)
        }
    }

    @Before
    void setupMockContext() {
        mockRoleService = new MockFor(RoleService.class);
        mockBoService = new MockFor(BusinessObjectService.class);
        mockKimPermissionDao = new MockFor(KimPermissionDao.class);
    }

    @Before
    void setupServiceUnderTest() {
        permissionServiceImpl = new PermissionServiceImpl()
        permissionService = permissionServiceImpl    //assign Interface type to implementation reference for unit test only
    }

    void injectRoleServiceIntoPermissionService() {
        roleService = mockRoleService.proxyDelegateInstance();
        permissionServiceImpl.setRoleService(roleService);
    }

    void injectBusinessObjectServiceIntoPermissionService() {
        boService = mockBoService.proxyDelegateInstance();
        permissionServiceImpl.setBusinessObjectService(boService);
    }

    void injectKimPermissionDaoIntoPermissionService() {
        kimPermissionDao = mockKimPermissionDao.proxyDelegateInstance();
        permissionServiceImpl.setPermissionDao(kimPermissionDao);
    }

    @Test(expected = IllegalArgumentException.class)
    void testIsAuthorizedWithNullFails() {
        permissionService.isAuthorized(null, null, null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    void testIsAuthorizedWithBlanksFails() {
        permissionService.isAuthorized("", "", "", null, null);
    }

    @Test
    void testIsAuthorizedSucceeds() {
        String authorizedPrincipalId = "principalid";
        String authorizedNamespaceCode = "namespacecodeone";
        String authorizedPermissionName = "permissionone";
        Map<String, String> authorizedPermissionDetails = new HashMap<String, String>();
        Map<String, String> authorizedQualification = new HashMap<String, String>();

        mockBoService.demand.findMatching(1..samplePermissions.size()) {
            Class clazz, Map map -> for (PermissionBo permissionBo in samplePermissions.values()) {
                if (permissionBo.namespaceCode.equals(map.get("namespaceCode")))
                {
                    Collection<PermissionBo> permissions = new ArrayList<PermissionBo>();
                    permissions.add(permissionBo);
                    return permissions;
                }
            }
        }

        mockKimPermissionDao.demand.getRoleIdsForPermissions(1) {
            Collection<PermissionBo> permissions -> List<String> roleIds = new ArrayList<String>(1);
            roleIds.add("test");
            return roleIds;
        }

        mockRoleService.demand.principalHasRole(1) {
            String principalId, List<String> roleIds, Map<String, String> qualification -> return true;
        }

        injectBusinessObjectServiceIntoPermissionService();
        injectKimPermissionDaoIntoPermissionService();
        injectRoleServiceIntoPermissionService();

        Assert.assertEquals(true, permissionService.isAuthorized(authorizedPrincipalId, authorizedNamespaceCode, authorizedPermissionName, authorizedPermissionDetails, authorizedQualification));

        mockBoService.verify(boService)
    }
}
