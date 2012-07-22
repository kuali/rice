/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.kim.service.impl;

import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.junit.Test;
import org.kuali.rice.core.api.delegation.DelegationType;
import org.kuali.rice.core.api.exception.RiceIllegalStateException;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kew.api.action.ActionType;
import org.kuali.rice.kim.api.KimApiConstants;
import org.kuali.rice.kim.api.common.delegate.DelegateMember;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.api.role.RoleResponsibilityAction;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.impl.common.delegate.DelegateTypeBo;
import org.kuali.rice.kim.test.KIMTestCase;
import org.kuali.rice.krad.service.KRADServiceLocator;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class RoleServiceImplTest extends KIMTestCase {

	private RoleService roleService;

	public void setUp() throws Exception {
		super.setUp();
		roleService = (RoleService) GlobalResourceLoader.getService(
                new QName(KimApiConstants.Namespaces.KIM_NAMESPACE_2_0, KimApiConstants.ServiceNames.ROLE_SERVICE_SOAP));
	}

	@Test
	public void testPrincipaHasRoleOfDirectAssignment() {
		List <String>roleIds = new ArrayList<String>();
		roleIds.add("r1");
		assertTrue( "p1 has direct role r1", roleService.principalHasRole("p1", roleIds,  Collections
                .<String, String>emptyMap() ));
		//assertFalse( "p4 has no direct/higher level role r1", roleService.principalHasRole("p4", roleIds, null ));
		Map<String, String> qualification = new HashMap<String, String>();
		qualification.put("Attribute 2", "CHEM");
		assertTrue( "p1 has direct role r1 with rp2 attr data", roleService.principalHasRole("p1", roleIds, qualification));
		qualification.clear();
		//requested qualification rolls up to a higher element in some hierarchy 
		// method not implemented yet, not quite clear how this works
		qualification.put("Attribute 3", "PHYS");
		assertTrue( "p1 has direct role r1 with rp2 attr data", roleService.principalHasRole("p1", roleIds, Maps.newHashMap(
                qualification)));
	}

	@Test
	public void testPrincipalHasRoleOfHigherLevel() {
		// "p3" is in "r2" and "r2 contains "r1"
		List <String>roleIds = new ArrayList<String>();
		roleIds.add("r2");
		assertTrue( "p1 has assigned in higher level role r1", roleService.principalHasRole("p1", roleIds,  Collections.<String, String>emptyMap() ));
        assertTrue( "p1 has assigned in higher level role r1", roleService.principalHasRole("p1", roleIds,  Collections.<String, String>emptyMap() ));
	}

    @Test
    public void testDelegateMemberCreateUpdateRemove() {

        Role r2 = roleService.getRole("r2");
        RoleMember rm1 = roleService.assignPrincipalToRole("user2", r2.getNamespaceCode(), r2.getName(),
                new HashMap<String, String>());
        String kimTypeId = "1";

        //Create delegation
        String id = "" + KRADServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_DLGN_MBR_ID_S");
        DelegateTypeBo delegate = new DelegateTypeBo();
        delegate.setDelegationId(id);
        delegate.setDelegationType(DelegationType.PRIMARY);
        delegate.setRoleId(r2.getId());
        delegate.setActive(true);
        delegate.setKimTypeId("" + kimTypeId);
        delegate = KRADServiceLocator.getBusinessObjectService().save(delegate);

        //Create delegate member
        DelegateMember.Builder delegateMemberInfo = DelegateMember.Builder.create();
        delegateMemberInfo.setAttributes(Collections.<String, String>emptyMap());
        delegateMemberInfo.setDelegationId(delegate.getDelegationId());
        delegateMemberInfo.setMemberId("user4");
        delegateMemberInfo.setRoleMemberId(rm1.getId());
        delegateMemberInfo.setType( MemberType.PRINCIPAL );
        DelegateMember inDelegateMember =  delegateMemberInfo.build();
        DelegateMember newDelegateMember = roleService.createDelegateMember(inDelegateMember);
        assertNotNull("delegateMember not created",newDelegateMember);

        //Update delegate member
        Long versionNumber = newDelegateMember.getVersionNumber();
        DateTime dateTimeFrom   = DateTime.now().minusDays(3);
        delegateMemberInfo.setActiveFromDate(dateTimeFrom);
        DateTime dateTimeTo = DateTime.now().plusDays(3);
        delegateMemberInfo.setActiveToDate(dateTimeTo);
        inDelegateMember = delegateMemberInfo.build();
        DelegateMember updateDelegateMember = roleService.createDelegateMember(inDelegateMember);
        assertNotNull("updateDelegateMember not created",newDelegateMember);
        assertEquals("activeFromDate not updated",dateTimeFrom,updateDelegateMember.getActiveFromDate());
        assertEquals("activeToDate not updated",dateTimeTo,updateDelegateMember.getActiveToDate());

        //remove (inactivate) delegate member
        List<DelegateMember>  removeDelegateMembers = new ArrayList<DelegateMember>();
        removeDelegateMembers.add(updateDelegateMember);
        roleService.removeDelegateMembers(removeDelegateMembers);
        DelegateMember removeDelegate = roleService.getDelegationMemberById(updateDelegateMember.getDelegationMemberId()) ;
        assertTrue("removeDelegates did not update activeToDate",removeDelegate.getActiveToDate().equals(updateDelegateMember.getActiveToDate()));
    }


	
	@Test
	public void testPrincipalHasRoleContainsGroupAssigned() {
		// "p2" is in "g1" and "g1" assigned to "r2"
		List <String>roleIds = new ArrayList<String>();
		roleIds.add("r2");
		assertTrue( "p2 is assigned to g1 and g1 assigned to r2", roleService.principalHasRole("p2", roleIds,  Collections.<String, String>emptyMap() ));
	}

    @Test
    public void testAddPrincipalToRoleAndRemove() {
        /*Role r2 = roleService.getRole("r2");
        roleService.assignPrincipalToRole("user4", r2.getNamespaceCode(), r2.getName(),
                new HashMap<String, String>());

        assertTrue("principal should be assigned to role", roleService.principalHasRole("user4", Collections.singletonList(
                r2.getId()), new HashMap<String, String>()));
        
        roleService.removePrincipalFromRole("user4", r2.getNamespaceCode(), r2.getName(), new HashMap<String, String>());

        assertFalse("principal should not be assigned to role", roleService.principalHasRole("user4", Collections.singletonList(
                r2.getId()), new HashMap<String, String>()));*/

        Role r2 = roleService.getRole("r2");
        RoleMember rm1 = roleService.assignPrincipalToRole("user4", r2.getNamespaceCode(), r2.getName(),
                new HashMap<String, String>());

        assertTrue("principal should be assigned to role", roleService.principalHasRole("user4", Collections.singletonList(
                r2.getId()), new HashMap<String, String>()));

        roleService.removePrincipalFromRole("user4", r2.getNamespaceCode(), r2.getName(), new HashMap<String, String>());

        RoleMember rm2 = roleService.assignPrincipalToRole("user4", r2.getNamespaceCode(), r2.getName(),
                new HashMap<String, String>());

        assertFalse(rm1.getId().equals(rm2.getId()));
    }
	
	/**
	 * Tests to ensure that a circular role membership cannot be created via the RoleService.
	 * 
	 * @throws Exception
	 */
	@Test (expected=IllegalArgumentException.class)
	public void testCircularRoleAssignment() {
		Map<String, String> map = new HashMap<String, String>();
		List <String>roleIds = new ArrayList<String>();
		roleIds.add("r1");
		roleService.assignRoleToRole("r5", "AUTH_SVC_TEST2", "RoleThree", map);
	}

    protected RoleResponsibilityAction createRoleResponsibilityAction() {
        Role r = roleService.getRole("r1");
        List<RoleMembership> members = roleService.getRoleMembers(Collections.singletonList("r1"), null);
        RoleMembership rm = members.get(0);

        RoleResponsibilityAction.Builder builder = RoleResponsibilityAction.Builder.create();
        builder.setRoleMemberId(rm.getMemberId());
        builder.setActionTypeCode(ActionType.APPROVE.getCode());

        RoleResponsibilityAction saved = roleService.createRoleResponsibilityAction(builder.build());
        List<RoleResponsibilityAction> rra = roleService.getRoleMemberResponsibilityActions(rm.getMemberId());
        assertEquals(1, rra.size());
        assertEquals(saved, rra.get(0));

        return rra.get(0);
    }

    @Test
    public void testCreateRoleResponsibilityAction() {
        createRoleResponsibilityAction();
    }

    @Test
    public void testUpdateRoleResponsibilityAction() {
        RoleResponsibilityAction rra = createRoleResponsibilityAction();
        RoleResponsibilityAction.Builder builder = RoleResponsibilityAction.Builder.create(rra);
        assertFalse(builder.isForceAction());
        builder.setForceAction(true);
        builder.setActionTypeCode(ActionType.ACKNOWLEDGE.getCode());

        RoleResponsibilityAction updated = roleService.updateRoleResponsibilityAction(builder.build());
        builder.setVersionNumber(updated.getVersionNumber());
        assertEquals(builder.build(), updated);

        // test that the value for rolemember is updated and not cached
        List<RoleResponsibilityAction> rras = roleService.getRoleMemberResponsibilityActions(rra.getRoleMemberId());
        assertEquals(1, rras.size());
        assertEquals(updated, rras.get(0));
    }

    @Test
    public void testDeleteRoleResponsibilityAction() {
        RoleResponsibilityAction rra = createRoleResponsibilityAction();

        roleService.deleteRoleResponsibilityAction(rra.getId());

        List<RoleResponsibilityAction> rras = roleService.getRoleMemberResponsibilityActions(rra.getRoleMemberId());
        assertEquals(0, rras.size());

        try {
            roleService.deleteRoleResponsibilityAction(rra.getId());
            fail("Expected to throw RiceIllegalStateException due to missing RuleResponsibilityAction");
        } catch (RiceIllegalStateException rise) {
            // expected
        }
    }
}
