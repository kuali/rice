package org.kuali.rice.kim.service.impl

import java.util.List;

import org.junit.Test;
import groovy.mock.interceptor.MockFor
import org.junit.Before
import org.kuali.rice.kim.api.role.RoleService
import org.kuali.rice.kim.api.permission.PermissionService
import org.kuali.rice.krad.service.BusinessObjectService
import org.kuali.rice.kim.impl.permission.PermissionBo
import org.junit.Assert
import org.junit.BeforeClass
import org.kuali.rice.kim.impl.permission.PermissionDao
import org.kuali.rice.kim.api.common.assignee.Assignee
import org.kuali.rice.kim.api.permission.Permission
import org.kuali.rice.kim.impl.permission.PermissionTemplateBo
import org.kuali.rice.kim.api.role.RoleMembership
import org.kuali.rice.kim.bo.role.dto.PermissionAssigneeInfo
import org.kuali.rice.kim.api.common.delegate.DelegateMember;
import org.kuali.rice.kim.api.common.delegate.DelegateType
import org.kuali.rice.kim.impl.permission.PermissionServiceImpl

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
    private MockFor mockPermissionDao;
    private RoleService roleService;
    private BusinessObjectService boService;
    private PermissionDao permissionDao;
    PermissionService permissionService;
    PermissionServiceImpl permissionServiceImpl;

    static Map<String, PermissionBo> samplePermissions = new HashMap<String, PermissionBo>();

    @BeforeClass
    static void createSampleBOs() {
        PermissionTemplateBo firstPermissionTemplateBo = new PermissionTemplateBo(id: "permissiontemplateidone", name: "permissiontemplateone", namespaceCode: "templatenamespaceone", kimTypeId: "kimtypeidone", versionNumber: 1);
        PermissionBo firstPermissionBo = new PermissionBo(id: "permidone", name: "permissionone", namespaceCode: "namespacecodeone", active: "Y", template: firstPermissionTemplateBo, versionNumber: 1);

        PermissionTemplateBo secondPermissionTemplateBo = new PermissionTemplateBo(id: "permissiontemplateidtwo", name: "permissiontemplatetwo", namespaceCode: "templatenamespacetwo", kimTypeId: "kimtypeidtwo", versionNumber: 1);
        PermissionBo secondPermissionBo = new PermissionBo(id: "permidtwo", name: "permissiontwo", namespaceCode: "namespacecodetwo", active: "Y", template: secondPermissionTemplateBo, versionNumber: 1);

        for (bo in [firstPermissionBo, secondPermissionBo]) {
            samplePermissions.put(bo.id, bo)
        }
    }

    @Before
    void setupMockContext() {
        mockRoleService = new MockFor(RoleService.class);
        mockBoService = new MockFor(BusinessObjectService.class);
        mockPermissionDao = new MockFor(PermissionDao.class);
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
        permissionDao = mockPermissionDao.proxyDelegateInstance();
        permissionServiceImpl.setPermissionDao(permissionDao);
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

        mockPermissionDao.demand.getRoleIdsForPermissions(1) {
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

    @Test(expected = IllegalArgumentException.class)
    void testIsAuthorizedByTemplateNameWithNullFails() {
        permissionService.isAuthorizedByTemplateName(null, null, null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    void testIsAuthorizedByTemplateNameWithBlanksFails() {
        permissionService.isAuthorizedByTemplateName("", "", "", null, null);
    }

    @Test
    void testIsAuthorizedByTemplateNameSucceeds() {
        String authorizedPrincipalId = "principalid";
        String authorizedNamespaceCode = "templatenamespaceone";
        String permissionTemplateName = "permissiontemplate";
        Map<String, String> authorizedPermissionDetails = new HashMap<String, String>();
        Map<String, String> authorizedQualification = new HashMap<String, String>();

        mockBoService.demand.findMatching(1..samplePermissions.size()) {
            Class clazz, Map map -> for (PermissionBo permissionBo in samplePermissions.values()) {
                if (permissionBo.template.namespaceCode.equals(map.get("template.namespaceCode")))
                {
                    Collection<PermissionBo> permissions = new ArrayList<PermissionBo>();
                    permissions.add(permissionBo);
                    return permissions;
                }
            }
        }

        mockPermissionDao.demand.getRoleIdsForPermissions(1) {
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

        Assert.assertEquals(true, permissionService.isAuthorizedByTemplateName(authorizedPrincipalId, authorizedNamespaceCode, permissionTemplateName, authorizedPermissionDetails, authorizedQualification));

        mockBoService.verify(boService)
    }

    @Test(expected = IllegalArgumentException.class)
    void testGetAuthorizedPermissionsWithNullFails() {
        permissionService.getAuthorizedPermissions(null, null, null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    void testGetAuthorizedPermissionsWithBlanksFails() {
        permissionService.getAuthorizedPermissions("", "", "", null, null);
    }

    @Test
    void testGetAuthorizedPermissionsSucceeds() {
        String authorizedPrincipalId = "principalid";
        String authorizedNamespaceCode = "namespacecodeone";
        String authorizedPermissionName = "permissionone";
        Map<String, String> authorizedPermissionDetails = new HashMap<String, String>();
        Map<String, String> authorizedQualification = new HashMap<String, String>();
        List<Permission> expectedPermissions = new ArrayList<Permission>();
        expectedPermissions.add(PermissionBo.to(samplePermissions.get("permidone")));

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

        mockPermissionDao.demand.getRoleIdsForPermissions(1) {
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

		List<Permission> actualPermissions = permissionService.getAuthorizedPermissions(authorizedPrincipalId, authorizedNamespaceCode, authorizedPermissionName, authorizedPermissionDetails, authorizedQualification);

        Assert.assertEquals(expectedPermissions.size(), actualPermissions.size());
        Assert.assertEquals(expectedPermissions[0], actualPermissions[0]);

        mockBoService.verify(boService)
    }

    @Test(expected = IllegalArgumentException.class)
    void testGetAuthorizedPermissionsByTemplateNameWithNullFails() {
        permissionService.getAuthorizedPermissionsByTemplateName(null, null, null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    void testGetAuthorizedPermissionsByTemplateNameWithBlanksFails() {
        permissionService.getAuthorizedPermissionsByTemplateName("", "", "", null, null);
    }

    @Test
    void testGetAuthorizedPermissionsByTemplateNameSucceeds() {
        String authorizedPrincipalId = "principalid";
        String authorizedNamespaceCode = "templatenamespaceone";
        String permissionTemplateName = "permissiontemplate";
        Map<String, String> authorizedPermissionDetails = new HashMap<String, String>();
        Map<String, String> authorizedQualification = new HashMap<String, String>();
        List<Permission> expectedPermissions = new ArrayList<Permission>();
        expectedPermissions.add(PermissionBo.to(samplePermissions.get("permidone")));

        mockBoService.demand.findMatching(1..samplePermissions.size()) {
            Class clazz, Map map -> for (PermissionBo permissionBo in samplePermissions.values()) {
                if (permissionBo.template.namespaceCode.equals(map.get("template.namespaceCode")))
                {
                    Collection<PermissionBo> permissions = new ArrayList<PermissionBo>();
                    permissions.add(permissionBo);
                    return permissions;
                }
            }
        }

        mockPermissionDao.demand.getRoleIdsForPermissions(1) {
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

        List<Permission> actualPermissions = permissionService.getAuthorizedPermissionsByTemplateName(authorizedPrincipalId, authorizedNamespaceCode, permissionTemplateName, authorizedPermissionDetails, authorizedQualification);

        Assert.assertEquals(expectedPermissions.size(), actualPermissions.size());
        Assert.assertEquals(expectedPermissions[0], actualPermissions[0]);

        mockBoService.verify(boService)
    }

    @Test
    void testGetPermissionAssigneesSucceeds() {
		String authorizedPrincipalId = "principalid";
		String authorizedNamespaceCode = "namespacecodeone";
		String authorizedPermissionName = "permissionone";
		Map<String, String> authorizedPermissionDetails = new HashMap<String, String>();
		Map<String, String> authorizedQualification = new HashMap<String, String>();

		Assignee.Builder assigneeBuilder = Assignee.Builder.create("memberid", null, new ArrayList<DelegateType.Builder>());
		List<Assignee> expectedPermissions = new ArrayList<Assignee>();
		expectedPermissions.add(assigneeBuilder.build());
		
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

		mockPermissionDao.demand.getRoleIdsForPermissions(1) {
			Collection<PermissionBo> permissions -> List<String> roleIds = new ArrayList<String>(1);
			roleIds.add("test");
			return roleIds;
		}

		mockRoleService.demand.getRoleMembers(1) {
            List<String> roleIds, Map<String, String> qualification -> List<RoleMembership> memberships = new ArrayList<RoleMembership>(1);
            RoleMembership.Builder builder = RoleMembership.Builder.create("roleidone", "rolememberId", "memberid", "P", null);
            memberships.add(builder.build());
            return memberships;
        }

		injectBusinessObjectServiceIntoPermissionService();
		injectKimPermissionDaoIntoPermissionService();
		injectRoleServiceIntoPermissionService();

		List<Assignee> actualPermissions = permissionService.getPermissionAssignees(authorizedNamespaceCode, authorizedPermissionName, authorizedPermissionDetails, authorizedQualification);

		Assert.assertEquals(expectedPermissions.size(), actualPermissions.size());
		Assert.assertEquals(expectedPermissions[0], actualPermissions[0]);

		mockBoService.verify(boService)
    }

    @Test
    void testGetPermissionAssigneesForTemplateNameSucceeds() {
        String authorizedNamespaceCode = "templatenamespaceone";
        String permissionName = "permission";
        Map<String, String> authorizedPermissionDetails = new HashMap<String, String>();
        Map<String, String> authorizedQualification = new HashMap<String, String>();
        List<PermissionAssigneeInfo> expectedPermissions = new ArrayList<PermissionAssigneeInfo>();
        expectedPermissions.add(new PermissionAssigneeInfo("memberid", "groupId", new ArrayList<DelegateType>()));

        mockBoService.demand.findMatching(1..samplePermissions.size()) {
            Class clazz, Map map -> for (PermissionBo permissionBo in samplePermissions.values()) {
                if (permissionBo.template.namespaceCode.equals(map.get("template.namespaceCode")))
                {
                    Collection<PermissionBo> permissions = new ArrayList<PermissionBo>();
                    permissions.add(permissionBo);
                    return permissions;
                }
            }
        }

        mockPermissionDao.demand.getRoleIdsForPermissions(1) {
            Collection<PermissionBo> permissions -> List<String> roleIds = new ArrayList<String>(1);
            roleIds.add("test");
            return roleIds;
        }

        mockRoleService.demand.getRoleMembers(1) {
            List<String> roleIds, Map<String, String> qualification -> List<RoleMembership> memberships = new ArrayList<RoleMembership>(1);
            RoleMembership.Builder builder = RoleMembership.Builder.create("roleidone", "rolememberId", "memberid", "P", null);
            memberships.add(builder.build());
            return memberships;
        }

        injectBusinessObjectServiceIntoPermissionService();
        injectKimPermissionDaoIntoPermissionService();
        injectRoleServiceIntoPermissionService();

        List<PermissionAssigneeInfo> actualPermissions = permissionService.getPermissionAssigneesForTemplateName(authorizedNamespaceCode, permissionName, authorizedPermissionDetails, authorizedQualification);

        Assert.assertEquals(expectedPermissions.size(), actualPermissions.size());
        Assert.assertEquals(expectedPermissions[0].principalId, actualPermissions[0].principalId);

        mockBoService.verify(boService)
    }

    @Test
    void testGetPermissionsByNameIncludingInactiveSucceeds()
    {
        PermissionTemplateBo firstPermissionTemplateBo = new PermissionTemplateBo(id: "permissiontemplateidone", name: "permissiontemplateone", namespaceCode: "templatenamespaceone", kimTypeId: "kimtypeidone", versionNumber: 1);
        PermissionBo expectedPermissionBo = new PermissionBo(id: "permidone", name: "permissionone", namespaceCode: "namespacecodeone", active: "Y", template: firstPermissionTemplateBo, versionNumber: 1);

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

        injectBusinessObjectServiceIntoPermissionService();

        Permission permission = permissionService.getPermissionsByNameIncludingInactive(expectedPermissionBo.namespaceCode, expectedPermissionBo.name);

        Assert.assertEquals(PermissionBo.to(expectedPermissionBo), permission);

        mockBoService.verify(boService);
    }
}
