/**
 * Copyright 2005-2014 The Kuali Foundation
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.kuali.rice.core.api.delegation.DelegationType;
import org.kuali.rice.core.api.exception.RiceIllegalStateException;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kew.api.action.ActionType;
import org.kuali.rice.kim.api.KimApiConstants;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.common.delegate.DelegateMember;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.api.role.RoleResponsibilityAction;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.impl.common.delegate.DelegateMemberAttributeDataBo;
import org.kuali.rice.kim.impl.common.delegate.DelegateMemberBo;
import org.kuali.rice.kim.impl.common.delegate.DelegateTypeBo;
import org.kuali.rice.kim.impl.role.RoleMemberAttributeDataBo;
import org.kuali.rice.kim.impl.role.RoleMemberBo;
import org.kuali.rice.kim.test.KIMTestCase;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.data.PersistenceOption;

import com.google.common.collect.Maps;

public class RoleServiceImplTest extends KIMTestCase {
    private RoleService roleService;
    static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    static final String ROLE_MEMBER_ID1 = "45123";
    static final String ROLE_ID = "100";
    static final String MEMBER_ID = "1";
    static final MemberType MEMBER_TYPE_R = MemberType.ROLE;
    static final String ACTIVE_FROM_STRING = "2011-01-01 12:00:00";
    static final DateTime ACTIVE_FROM = new DateTime(FORMATTER.parseDateTime(ACTIVE_FROM_STRING));
    static final String ACTIVE_TO_STRING1 = "2013-01-01 12:00:00";
    static final String ACTIVE_TO_STRING2 = "2014-01-01 12:00:00";
    static final DateTime ACTIVE_TO1 = new DateTime(FORMATTER.parseDateTime(ACTIVE_TO_STRING1));
    static final DateTime ACTIVE_TO2 = new DateTime(FORMATTER.parseDateTime(ACTIVE_TO_STRING2));

    @Override
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
        String id = getNextSequenceStringValue("KRIM_DLGN_MBR_ID_S");
        DelegateTypeBo delegate = new DelegateTypeBo();
        delegate.setDelegationId(id);
        delegate.setDelegationType(DelegationType.PRIMARY);
        delegate.setRoleId(r2.getId());
        delegate.setActive(true);
        delegate.setKimTypeId("" + kimTypeId);
        delegate = KradDataServiceLocator.getDataObjectService().save(delegate, PersistenceOption.FLUSH);

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
        delegateMemberInfo.setDelegationMemberId(newDelegateMember.getDelegationMemberId());
        DateTime dateTimeFrom   = DateTime.now().minusDays(3);
        delegateMemberInfo.setActiveFromDate(dateTimeFrom);
        DateTime dateTimeTo = DateTime.now().plusDays(3);
        delegateMemberInfo.setActiveToDate(dateTimeTo);
        inDelegateMember = delegateMemberInfo.build();
        roleService.updateDelegateMember(inDelegateMember);
        DelegateMember updatedDelegateMember = DelegateMember.Builder.create( KradDataServiceLocator.getDataObjectService().find(DelegateMemberBo.class, inDelegateMember.getDelegationMemberId()) ).build();

        assertEquals("Delegate member was updated",newDelegateMember.getDelegationMemberId(),updatedDelegateMember.getDelegationMemberId());
        assertNotNull("updateDelegateMember not created",updatedDelegateMember);
        assertEquals("activeFromDate not updated",dateTimeFrom,updatedDelegateMember.getActiveFromDate());
        assertEquals("activeToDate not updated",dateTimeTo,updatedDelegateMember.getActiveToDate());

        //remove (inactivate) delegate member
        roleService.removeDelegateMembers(Collections.singletonList(updatedDelegateMember));
        DelegateMemberBo removedDelegateMember = KradDataServiceLocator.getDataObjectService().find(DelegateMemberBo.class, updatedDelegateMember.getDelegationMemberId());
        //assertEquals("removeDelegateMembers did not remove the existing member",updatedDelegateMember.getDelegationMemberId(), removedDelegateMember.getDelegationMemberId() );
        assertNotNull("after removal, versionNumber should not be null", removedDelegateMember.getVersionNumber());
        assertEquals("removeDelegateMembers did not update the existing member", new Long(updatedDelegateMember.getVersionNumber() + 1), removedDelegateMember.getVersionNumber() );
        assertNotNull("after removal, active to date should not be null", removedDelegateMember.getActiveToDate());
        assertTrue("removeDelegateMembers did not update activeToDate",removedDelegateMember.getActiveToDate().isBeforeNow());
    }

    @Test
    public void testRoleMemberCreateUpdate() {

        Role roleId = roleService.getRole(ROLE_ID);
        List<String> roleIds = new ArrayList<String>();
        roleIds.add(roleId.getId());

        Map<String,String> attributes = new HashMap<String,String>();
        attributes.put("parameterName", "parameterNameBefore");
        attributes.put("namespaceCode", "namespaceCodeBefore");
        attributes.put("componentName", "componentNameBefore");

        RoleMember roleMember =  roleService.createRoleMember(RoleMember.Builder.create(ROLE_ID, ROLE_MEMBER_ID1, MEMBER_ID, MEMBER_TYPE_R, ACTIVE_FROM, ACTIVE_TO1, attributes, "", "").build());
        RoleMemberBo rmBo = getRoleMemberBo(roleMember.getId());

        RoleMember.Builder updatedRoleMember = RoleMember.Builder.create(roleMember);
        updatedRoleMember.setActiveToDate(ACTIVE_TO2);
        Map<String,String> newAttributes = new HashMap<String,String>();
        newAttributes.put("parameterName", "parameterNameAfter");
        newAttributes.put("namespaceCode", "namespaceCodeAfter");
        newAttributes.put("componentName", "componentNameAfter");
        updatedRoleMember.setAttributes(newAttributes);

        roleService.updateRoleMember(updatedRoleMember.build());
        RoleMemberBo updatedRmBo = getRoleMemberBo(roleMember.getId());

        assertEquals(3,rmBo.getAttributeDetails().size());
        assertEquals(3,updatedRmBo.getAttributeDetails().size());

        for (RoleMemberAttributeDataBo newRoleMemberAttrDataBo :  updatedRmBo.getAttributeDetails()) {
            for (RoleMemberAttributeDataBo oldRoleMemberAttrDataBo :  rmBo.getAttributeDetails()) {
                if (newRoleMemberAttrDataBo.getKimTypeId().equals(oldRoleMemberAttrDataBo.getKimTypeId()) &&
                    newRoleMemberAttrDataBo.getKimAttributeId().equals(oldRoleMemberAttrDataBo.getKimAttributeId())) {
                        assertEquals("updated role member version number incorrect", new Long(2), newRoleMemberAttrDataBo.getVersionNumber());
                }
            }
        }
    }

    @Test
    public void testRoleMemberCreateUpdateNoAttrChange() {

        Role roleId = roleService.getRole(ROLE_ID);
        List<String> roleIds = new ArrayList<String>();
        roleIds.add(roleId.getId());

        Map<String,String> attributes = new HashMap<String,String>();
        attributes.put("parameterName", "parameterNameBefore");
        attributes.put("namespaceCode", "namespaceCodeBefore");
        attributes.put("componentName", "componentNameBefore");

        RoleMember roleMember =  roleService.createRoleMember(RoleMember.Builder.create(ROLE_ID, ROLE_MEMBER_ID1, MEMBER_ID, MEMBER_TYPE_R, ACTIVE_FROM, ACTIVE_TO1, attributes, "", "").build());
        RoleMemberBo rmBo = getRoleMemberBo(roleMember.getId());

        RoleMember.Builder updatedRoleMember = RoleMember.Builder.create(roleMember);
        updatedRoleMember.setActiveToDate(ACTIVE_TO2);
        updatedRoleMember.setAttributes(rmBo.getAttributes());

        roleService.updateRoleMember(updatedRoleMember.build());
        RoleMemberBo updatedRmBo = getRoleMemberBo(roleMember.getId());

        assertEquals(3,rmBo.getAttributeDetails().size());
        assertEquals(3,updatedRmBo.getAttributeDetails().size());

        for (RoleMemberAttributeDataBo newRoleMemberAttrDataBo :  updatedRmBo.getAttributeDetails()) {
            for (RoleMemberAttributeDataBo oldRoleMemberAttrDataBo :  rmBo.getAttributeDetails()) {
                if (newRoleMemberAttrDataBo.getKimTypeId().equals(oldRoleMemberAttrDataBo.getKimTypeId()) &&
                        newRoleMemberAttrDataBo.getKimAttributeId().equals(oldRoleMemberAttrDataBo.getKimAttributeId())) {
                    assertEquals(oldRoleMemberAttrDataBo.getAttributeValue(), newRoleMemberAttrDataBo.getAttributeValue());
                    assertEquals("updated role member version number incorrect (since no update - should not have been changed)", new Long(1), newRoleMemberAttrDataBo.getVersionNumber());
                }
            }
        }
    }

    @Test
    public void testRoleMemberCreateUpdateRemoveOneAttr() {

        Role roleId = roleService.getRole(ROLE_ID);
        List<String> roleIds = new ArrayList<String>();
        roleIds.add(roleId.getId());

        Map<String,String> attributes = new HashMap<String,String>();
        attributes.put("parameterName", "parameterNameBefore");
        attributes.put("namespaceCode", "namespaceCodeBefore");
        attributes.put("componentName", "componentNameBefore");

        RoleMember roleMember =  roleService.createRoleMember(RoleMember.Builder.create(ROLE_ID, ROLE_MEMBER_ID1, MEMBER_ID, MEMBER_TYPE_R, ACTIVE_FROM, ACTIVE_TO1, attributes, "", "").build());
        RoleMemberBo rmBo = getRoleMemberBo(roleMember.getId());
        assertEquals(3,rmBo.getAttributeDetails().size());

        RoleMember.Builder updatedRoleMember = RoleMember.Builder.create(roleMember);
        updatedRoleMember.setActiveToDate(ACTIVE_TO2);
        Map<String,String> newAttributes = new HashMap<String,String>();
        newAttributes.put("parameterName", "parameterNameAfter");
        newAttributes.put("namespaceCode", "namespaceCodeAfter");
        updatedRoleMember.setAttributes(newAttributes);

        roleService.updateRoleMember(updatedRoleMember.build());
        RoleMemberBo updatedRmBo = getRoleMemberBo(roleMember.getId());

        assertEquals(2, updatedRmBo.getAttributeDetails().size());

        for (RoleMemberAttributeDataBo newRoleMemberAttrDataBo :  updatedRmBo.getAttributeDetails()) {
            for (RoleMemberAttributeDataBo oldRoleMemberAttrDataBo :  rmBo.getAttributeDetails()) {
                if (newRoleMemberAttrDataBo.getKimTypeId().equals(oldRoleMemberAttrDataBo.getKimTypeId()) &&
                        newRoleMemberAttrDataBo.getKimAttributeId().equals(oldRoleMemberAttrDataBo.getKimAttributeId())) {
                    assertEquals(new Long(2), newRoleMemberAttrDataBo.getVersionNumber());
                }
            }
        }
    }

    @Test
    public void testRoleMemberCreateUpdateAddOneAttr() {

        Role roleId = roleService.getRole(ROLE_ID);
        List<String> roleIds = new ArrayList<String>();
        roleIds.add(roleId.getId());

        Map<String,String> attributes = new HashMap<String,String>();
        attributes.put("parameterName", "parameterNameBefore");
        attributes.put("namespaceCode", "namespaceCodeBefore");

        RoleMember roleMember =  roleService.createRoleMember(RoleMember.Builder.create(ROLE_ID, ROLE_MEMBER_ID1, MEMBER_ID, MEMBER_TYPE_R, ACTIVE_FROM, ACTIVE_TO1, attributes, "", "").build());
        RoleMemberBo rmBo = getRoleMemberBo(roleMember.getId());
        assertEquals("Original role member number of attributes is incorrect", 2,rmBo.getAttributeDetails().size());

        RoleMember.Builder updatedRoleMember = RoleMember.Builder.create(roleMember);
        updatedRoleMember.setActiveToDate(ACTIVE_TO2);
        Map<String,String> newAttributes = new HashMap<String,String>();
        newAttributes.put("parameterName", "parameterNameAfter");
        newAttributes.put("namespaceCode", "namespaceCodeAfter");
        newAttributes.put("componentName", "componentNameAfter");

        updatedRoleMember.setAttributes(newAttributes);

        roleService.updateRoleMember(updatedRoleMember.build());
        RoleMemberBo updatedRmBo = getRoleMemberBo(roleMember.getId());

        //assertEquals("Original role member number of attributes is incorrect: " + rmBo, 2,rmBo.getAttributeDetails().size());
        assertEquals("updated role member number of attributes is incorrect", 3,updatedRmBo.getAttributeDetails().size());

        System.err.println( updatedRmBo );

        for (RoleMemberAttributeDataBo newRoleMemberAttrDataBo : updatedRmBo.getAttributeDetails() ) {
            assertEquals( newRoleMemberAttrDataBo.getKimAttribute().getAttributeName() + " value is incorrect", newRoleMemberAttrDataBo.getKimAttribute().getAttributeName() + "After", newRoleMemberAttrDataBo.getAttributeValue() );
            if (newRoleMemberAttrDataBo.getKimAttribute().getAttributeName().equals("componentName")) {
                assertEquals("componentName (new attribute) versionNumber incorrect", new Long(1), newRoleMemberAttrDataBo.getVersionNumber());
            } else {
                assertEquals(newRoleMemberAttrDataBo.getKimAttribute().getAttributeName() + " (updated attribute) versionNumber incorrect", new Long(2), newRoleMemberAttrDataBo.getVersionNumber());
            }
        }
    }


    @Test
    public void testDelegateMemberCreateUpdateRemoveWithAttr() {

        Role r2 = roleService.getRole(ROLE_ID);
        RoleMember rm1 = roleService.assignPrincipalToRole("user2", r2.getNamespaceCode(), r2.getName(),
                new HashMap<String, String>());
        String kimTypeId = "1";

        //Create delegation
        String id = getNextSequenceStringValue("KRIM_DLGN_MBR_ID_S");
        DelegateTypeBo delegate = new DelegateTypeBo();
        delegate.setDelegationId(id);
        delegate.setDelegationType(DelegationType.PRIMARY);
        delegate.setRoleId(r2.getId());
        delegate.setActive(true);
        delegate.setKimTypeId(kimTypeId);
        delegate = KradDataServiceLocator.getDataObjectService().save(delegate, PersistenceOption.FLUSH);

        //Create delegate member
        DelegateMember.Builder delegateMemberInfo = DelegateMember.Builder.create();
        delegateMemberInfo.setDelegationId(delegate.getDelegationId());
        delegateMemberInfo.setMemberId("user4");
        delegateMemberInfo.setRoleMemberId(rm1.getId());
        delegateMemberInfo.setType( MemberType.PRINCIPAL );
        Map<String,String> attributes = new HashMap<String,String>();
        attributes.put("parameterName", "parameterNameBefore");
        attributes.put("namespaceCode", "namespaceCodeBefore");
        attributes.put("componentName", "componentNameBefore");
        delegateMemberInfo.setAttributes(attributes);
        DelegateMember inDelegateMember =  delegateMemberInfo.build();
        DelegateMember newDelegateMember = roleService.createDelegateMember(inDelegateMember);
        assertNotNull("delegateMember not created",newDelegateMember);

//        DelegateMemberBo originalDelegateMemberBo = getDelegateMemberBo(newDelegateMember.getDelegationMemberId());

        //Update delegate member
        DateTime threeDaysAgo   = DateTime.now().minusDays(3);
        DateTime threeDaysFromNow = DateTime.now().plusDays(3);
        delegateMemberInfo.setActiveFromDate(threeDaysAgo);
        delegateMemberInfo.setActiveToDate(threeDaysFromNow);
        delegateMemberInfo.setDelegationMemberId(newDelegateMember.getDelegationMemberId());
        Map<String,String> newAttributes = new HashMap<String,String>();
        newAttributes.put("parameterName", "parameterNameAfter");
        newAttributes.put("namespaceCode", "namespaceCodeAfter");
        newAttributes.put("componentName", "componentNameAfter");
        delegateMemberInfo.setAttributes(newAttributes);
        newDelegateMember = delegateMemberInfo.build();
        DelegateMember updateDelegateMember = roleService.updateDelegateMember(newDelegateMember);

        assertNotNull("updateDelegateMember not updated", updateDelegateMember);
        assertEquals("activeFromDate not updated",threeDaysAgo,updateDelegateMember.getActiveFromDate());
        assertEquals("activeToDate not updated",threeDaysFromNow,updateDelegateMember.getActiveToDate());

        DelegateMemberBo updatedDelegateMemberBo = getDelegateMemberBo(updateDelegateMember.getDelegationMemberId());

        for (DelegateMemberAttributeDataBo newRoleMemberAttrDataBo :  updatedDelegateMemberBo.getAttributeDetails()) {
            for (DelegateMemberAttributeDataBo oldRoleMemberAttrDataBo :  updatedDelegateMemberBo.getAttributeDetails()) {
                if (newRoleMemberAttrDataBo.getKimTypeId().equals(oldRoleMemberAttrDataBo.getKimTypeId()) &&
                        newRoleMemberAttrDataBo.getKimAttributeId().equals(oldRoleMemberAttrDataBo.getKimAttributeId())) {
                    assertEquals("version number on new role member incorrect", new Long(2), newRoleMemberAttrDataBo.getVersionNumber());
                }
            }
        }

        //remove (inactivate) delegate member
        List<DelegateMember>  removeDelegateMembers = new ArrayList<DelegateMember>();
        removeDelegateMembers.add(updateDelegateMember);
        roleService.removeDelegateMembers(removeDelegateMembers);
        DelegateMember removedDelegateMember = roleService.getDelegationMemberById(updateDelegateMember.getDelegationMemberId()) ;
        assertTrue("removeDelegateMembers did not remove the existing member",removedDelegateMember.getDelegationMemberId().equals(updateDelegateMember.getDelegationMemberId()));
        assertNotNull("after removal, versionNumber should not be null", removedDelegateMember.getVersionNumber());
        assertTrue("removeDelegateMembers did not remove the existing member",removedDelegateMember.getVersionNumber().equals(updateDelegateMember.getVersionNumber() + 1));
        assertNotNull("after removal, active to date should not be null", removedDelegateMember.getActiveToDate());
        assertTrue("removeDelegateMembers did not update activeToDate",removedDelegateMember.getActiveToDate().isBeforeNow());
    }

    protected RoleMemberBo getRoleMemberBo(String roleMemberId) {
        if (StringUtils.isBlank(roleMemberId)) {
            return null;
        }

        return KradDataServiceLocator.getDataObjectService().find(RoleMemberBo.class, roleMemberId);
    }

    protected DelegateMemberBo getDelegateMemberBo(String delegationMemberId) {
        if (StringUtils.isBlank(delegationMemberId)) {
            return null;
        }

        return KradDataServiceLocator.getDataObjectService().find(DelegateMemberBo.class, delegationMemberId);
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

    @Test
    public void testAddMultiplePrincipalsToRole() {
        Role r2 = roleService.getRole("r2");
        // Test with empty conditions param
        Map<String, String> conditions = new HashMap<String, String>();

        int originalNumberOfPrincipals = roleService.getRoleMemberPrincipalIds(r2.getNamespaceCode(), r2.getName(),
                conditions).size();

        RoleMember rm1 = roleService.assignPrincipalToRole("user3", r2.getNamespaceCode(), r2.getName(), conditions);
        RoleMember rm2 = roleService.assignPrincipalToRole("user4", r2.getNamespaceCode(), r2.getName(), conditions);

        assertTrue("principal should be assigned to role", roleService.principalHasRole("user3",
                Collections.singletonList(r2.getId()), conditions));
        assertTrue("principal should be assigned to role", roleService.principalHasRole("user4",
                Collections.singletonList(r2.getId()), conditions));

        int numberOfPrincipals = roleService.getRoleMemberPrincipalIds(r2.getNamespaceCode(), r2.getName(), conditions)
                .size();

        assertEquals("Should have been two Principals added to role", numberOfPrincipals - 2,
                originalNumberOfPrincipals);

        r2 = roleService.getRole("r2");
        roleService.removePrincipalFromRole("user3", r2.getNamespaceCode(), r2.getName(), conditions);
        roleService.removePrincipalFromRole("user4", r2.getNamespaceCode(), r2.getName(), conditions);

        r2 = roleService.getRole("r2");
        assertFalse("principal should have been removed from role", roleService.principalHasRole("user3",
                Collections.singletonList(r2.getId()), conditions));
        assertFalse("principal should have been removed from role", roleService.principalHasRole("user4",
                Collections.singletonList(r2.getId()), conditions));
        numberOfPrincipals = roleService.getRoleMemberPrincipalIds(r2.getNamespaceCode(), r2.getName(),
                new HashMap<String, String>()).size();
        assertEquals("Should have had the two added Principals removed", numberOfPrincipals,
                originalNumberOfPrincipals);
    }

    @Test
    public void testAddMultipleQualifiedPrincipalsToRole() {
        Role rCampus = roleService.getRole("r-campus");
        // Test with qualifying conditions
        Map<String, String> conditions = Collections.singletonMap(KimConstants.AttributeConstants.CAMPUS_CODE, "BL");
        int originalNumberOfPrincipals = roleService.getRoleMemberPrincipalIds(rCampus.getNamespaceCode(),
                rCampus.getName(), Collections.singletonMap(KimConstants.AttributeConstants.CAMPUS_CODE, "BL")).size();

        roleService.assignPrincipalToRole("user3", rCampus.getNamespaceCode(), rCampus.getName(),
                conditions);
        roleService.assignPrincipalToRole("user4", rCampus.getNamespaceCode(), rCampus.getName(),
                conditions);

        assertTrue("principal should be assigned to role", roleService.principalHasRole("user3",
                Collections.singletonList(rCampus.getId()), conditions));
        assertTrue("principal should be assigned to role", roleService.principalHasRole("user4",
                Collections.singletonList(rCampus.getId()), conditions));

        int numberOfPrincipals = roleService.getRoleMemberPrincipalIds(rCampus.getNamespaceCode(), rCampus.getName(),
                Collections.singletonMap(KimConstants.AttributeConstants.CAMPUS_CODE, "BL")).size();

        assertEquals("Should have been two Principals added to role", numberOfPrincipals,
                originalNumberOfPrincipals + 2);

        rCampus = roleService.getRole("r-campus");
        roleService.removePrincipalFromRole("user3", rCampus.getNamespaceCode(), rCampus.getName(), conditions);
        roleService.removePrincipalFromRole("user4", rCampus.getNamespaceCode(), rCampus.getName(), conditions);

        rCampus = roleService.getRole("r-campus");
        assertFalse("principal should have been removed from role", roleService.principalHasRole("user3",
                Collections.singletonList(rCampus.getId()), conditions));
        assertFalse("principal should have been removed from role", roleService.principalHasRole("user3",
                Collections.singletonList(rCampus.getId()), conditions));

        numberOfPrincipals = roleService.getRoleMemberPrincipalIds(rCampus.getNamespaceCode(), rCampus.getName(),
                Collections.singletonMap(KimConstants.AttributeConstants.CAMPUS_CODE, "BL")).size();
        assertEquals("Should have had the two added Principals removed", numberOfPrincipals,
                originalNumberOfPrincipals);
    }

    @Test
    public void testAddQualifiedPrincipalToRoleDoesNotReuseWrongRoleMember() {
        Role rCampus = roleService.getRole("r-campus");
        // Test with qualifying conditions
        Map<String, String> campusBLqualifier = Collections.singletonMap(KimConstants.AttributeConstants.CAMPUS_CODE, "BL");
        Map<String, String> campusKOqualifier = Collections.singletonMap(KimConstants.AttributeConstants.CAMPUS_CODE, "KO");

        List<RoleMembership> roleMembers = roleService.getRoleMembers(Collections.singletonList(rCampus.getId()), campusBLqualifier);
        // clean the role out
        for ( RoleMembership rm : roleMembers ) {
            roleService.removePrincipalFromRole(rm.getMemberId(), rCampus.getNamespaceCode(), rCampus.getName(), campusBLqualifier);
        }
        roleMembers = roleService.getRoleMembers(Collections.singletonList(rCampus.getId()), campusBLqualifier);

        // make sure they're gone
        assertEquals("Pre-check failed - should not be any members with" + campusBLqualifier + ".  Members: " + roleMembers, 0, roleMembers.size());

        roleMembers = roleService.getRoleMembers(Collections.singletonList(rCampus.getId()), campusKOqualifier);
        // clean the role out
        for ( RoleMembership rm : roleMembers ) {
            roleService.removePrincipalFromRole(rm.getMemberId(), rCampus.getNamespaceCode(), rCampus.getName(), campusKOqualifier);
        }
        roleMembers = roleService.getRoleMembers(Collections.singletonList(rCampus.getId()), campusKOqualifier);

        // make sure they're gone
        assertEquals("Pre-check failed - should not be any members with" + campusKOqualifier + ".  Members: " + roleMembers, 0, roleMembers.size());

        RoleMember rm1 = roleService.assignPrincipalToRole("user3", rCampus.getNamespaceCode(), rCampus.getName(),
                campusBLqualifier);
        assertTrue("user3 should be assigned to role", roleService.principalHasRole("user3",
                Collections.singletonList(rCampus.getId()), campusBLqualifier));
        assertNotNull( "Role member ID should have been assigned", rm1.getId() );
        assertNotNull( "role member missing campus code qualifier", rm1.getAttributes().get(KimConstants.AttributeConstants.CAMPUS_CODE) );
        assertEquals( "campus code on role member incorrect", "BL", rm1.getAttributes().get(KimConstants.AttributeConstants.CAMPUS_CODE) );

        // attempt to add the user again, but with campus code KO
        RoleMember rm2 = roleService.assignPrincipalToRole("user3", rCampus.getNamespaceCode(), rCampus.getName(),
                campusKOqualifier);
        assertNotNull( "role member missing campus code qualifier", rm2.getAttributes().get(KimConstants.AttributeConstants.CAMPUS_CODE) );
        assertEquals( "campus code on role member incorrect", "KO", rm2.getAttributes().get(KimConstants.AttributeConstants.CAMPUS_CODE) );
        assertTrue("user3 should be assigned to role for campus code KO", roleService.principalHasRole("user3",
                Collections.singletonList(rCampus.getId()), campusKOqualifier));
        assertNotNull( "Role member ID should have been assigned", rm1.getId() );
        assertFalse( "Role member ID SHOULD NOT be the same as previous assignment since qualifiers are different",
                rm1.getId().equals(rm2.getId()) );

        roleMembers = roleService.getRoleMembers(Collections.singletonList(rCampus.getId()), campusBLqualifier);
        assertEquals("Should only be one principal in role with " + campusBLqualifier + ".  Members: " + roleMembers, 1, roleMembers.size());
        roleMembers = roleService.getRoleMembers(Collections.singletonList(rCampus.getId()), campusKOqualifier);
        assertEquals("Should only be one principal in role with " + campusKOqualifier + ".  Members: " + roleMembers, 1, roleMembers.size());

        roleService.removePrincipalFromRole("user3", rCampus.getNamespaceCode(), rCampus.getName(), campusBLqualifier);

        assertFalse("principal should have been removed from role", roleService.principalHasRole("user3",
                Collections.singletonList(rCampus.getId()), campusBLqualifier));
    }

    @Test
    public void testAddQualifiedPrincipalToRoleTwice() {
        Role rCampus = roleService.getRole("r-campus");
        // Test with qualifying conditions
        Map<String, String> campusBLqualifier = Collections.singletonMap(KimConstants.AttributeConstants.CAMPUS_CODE, "BL");

        List<RoleMembership> roleMembers = roleService.getRoleMembers(Collections.singletonList(rCampus.getId()), campusBLqualifier);
        // clean the role out
        for ( RoleMembership rm : roleMembers ) {
            roleService.removePrincipalFromRole(rm.getMemberId(), rCampus.getNamespaceCode(), rCampus.getName(), campusBLqualifier);
        }
        roleMembers = roleService.getRoleMembers(Collections.singletonList(rCampus.getId()), campusBLqualifier);

        // make sure they're gone
        assertEquals("Pre-check failed - should not be any members with campus code BL.  Members: " + roleMembers, 0, roleMembers.size());

        RoleMember rm1 = roleService.assignPrincipalToRole("user3", rCampus.getNamespaceCode(), rCampus.getName(),
                campusBLqualifier);
        assertTrue("user3 should be assigned to role", roleService.principalHasRole("user3",
                Collections.singletonList(rCampus.getId()), campusBLqualifier));
        assertNotNull( "Role member ID should have been assigned", rm1.getId() );
        // attempt to add the user again
        RoleMember rm2 = roleService.assignPrincipalToRole("user3", rCampus.getNamespaceCode(), rCampus.getName(),
                campusBLqualifier);
        assertTrue("user3 should be still assigned to role", roleService.principalHasRole("user3",
                Collections.singletonList(rCampus.getId()), campusBLqualifier));
        assertNotNull( "Role member ID should have been assigned", rm1.getId() );
        assertEquals( "Role member ID be the same as previous assignment since user and qualifiers are the same",
                rm1.getId(),
                rm2.getId() );

        roleMembers = roleService.getRoleMembers(Collections.singletonList(rCampus.getId()), campusBLqualifier);

        assertEquals("Should only be one principal in role with campus code BL.  Members: " + roleMembers, 1, roleMembers.size());

        roleService.removePrincipalFromRole("user3", rCampus.getNamespaceCode(), rCampus.getName(), campusBLqualifier);

        assertFalse("principal should have been removed from role", roleService.principalHasRole("user3",
                Collections.singletonList(rCampus.getId()), campusBLqualifier));
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
        List<RoleMembership> members = roleService.getRoleMembers(Collections.singletonList("r1"), null);
        RoleMembership rm = members.get(0);

        RoleResponsibilityAction.Builder builder = RoleResponsibilityAction.Builder.create();
        builder.setRoleMemberId(rm.getMemberId());
        builder.setActionTypeCode(ActionType.APPROVE.getCode());

        RoleResponsibilityAction saved = roleService.createRoleResponsibilityAction(builder.build());
        List<RoleResponsibilityAction> rra = roleService.getRoleMemberResponsibilityActions(rm.getMemberId());
        assertEquals("incorrect number of RoleResponsibilityAction returned", 1, rra.size());
        assertEquals("saved RoleResponsibilityAction does not match expected", saved, rra.get(0));

        return rra.get(0);
    }

    @Test
    public void testGetRoleMembers() {
        List<RoleMembership> members = roleService.getRoleMembers(Collections.singletonList("r1"), null);
        assertNotNull( "returned member list should not be null", members);
        assertEquals("Wrong numbers of members in the role", 2, members.size());
    }

    @Test
    public void testGetRoleMembersWithExactMatchRoleTypeEmptyQualifier() {
        Role rCampus = roleService.getRole("r-campus");
        assertNotNull( "Campus-based role missing from test data", rCampus );
        assertEquals( "Campus role type incorrect", "kt-campus", rCampus.getKimTypeId());
        List<RoleMembership> members = roleService.getRoleMembers(Collections.singletonList("r-campus"), Collections.<String,String>emptyMap());
        assertNotNull( "returned member list should not be null", members);
        assertEquals("Wrong numbers of members in the role: " + members, 2, members.size());
    }

    @Test
    public void testGetRoleMembersWithExactMatchRoleType() {
        Role rCampus = roleService.getRole("r-campus");
        assertNotNull( "Campus-based role missing from test data", rCampus );
        assertEquals( "Campus role type incorrect", "kt-campus", rCampus.getKimTypeId());
        List<RoleMembership> members = roleService.getRoleMembers(Collections.singletonList("r-campus"), Collections.singletonMap(KimConstants.AttributeConstants.CAMPUS_CODE, "BL"));
        assertNotNull( "returned member list should not be null", members);
        assertEquals("Wrong numbers of members returned from role: " + members, 1, members.size());
    }

    @Test
    public void testRemoveRoleFromRoleWithExactQualification() {
        Role rCampus = roleService.getRole("r-campus-2");
        assertNotNull( "Campus-based role missing from test data", rCampus );
        assertEquals( "Campus role type incorrect", "kt-campus", rCampus.getKimTypeId());

        List<RoleMembership> firstLevelRoleMembers = roleService.getFirstLevelRoleMembers(Collections.singletonList("r-campus-2"));
        assertEquals("wrong number of role members: " + firstLevelRoleMembers, 2, firstLevelRoleMembers.size());

        // Find the role member for BL and run some sanity checks on the data
        RoleMembership blRoleMember = null;
        RoleMembership nonBlRoleMember = null;
        for ( RoleMembership rm : firstLevelRoleMembers ) {
            if ( StringUtils.equals( rm.getQualifier().get(KimConstants.AttributeConstants.CAMPUS_CODE), "BL" ) ) {
                blRoleMember = rm;
            } else {
                nonBlRoleMember = rm;
            }
        }
        assertNotNull( "Both role members have qualifer BL, the test can not function", nonBlRoleMember);
        assertNotNull( "Neither role member has qualifer BL, the test can not function", blRoleMember);
        assertEquals( "The BL role member needs to be a role", MemberType.ROLE, blRoleMember.getType() );
        Role blMemberRole = roleService.getRole( blRoleMember.getMemberId() );
        assertNotNull( "role specified on BL role member does not exist", blMemberRole );

        roleService.removeRoleFromRole(blRoleMember.getMemberId(), rCampus.getNamespaceCode(), rCampus.getName(), Collections.singletonMap(KimConstants.AttributeConstants.CAMPUS_CODE, "BL"));

        firstLevelRoleMembers = roleService.getFirstLevelRoleMembers(Collections.singletonList("r-campus-2"));
        assertEquals("wrong number of role members after removal: " + firstLevelRoleMembers, 1, firstLevelRoleMembers.size());

        assertEquals("Wrong role member remains", "r1", firstLevelRoleMembers.get(0).getMemberId() );
    }

    @Test
    public void testGetRoleQualifersForPrincipalByNamespaceAndRolenameWithoutQualifier() {
        Role rCampus = roleService.getRoleByNamespaceCodeAndName("AUTH_SVC_TEST2", "Campus Reviewer");
        assertNotNull( "Campus-based role missing from test data", rCampus );
        assertEquals( "Campus role type incorrect", "kt-campus", rCampus.getKimTypeId());

        List<Map<String, String>> qualifiers = roleService.getRoleQualifersForPrincipalByNamespaceAndRolename("p9", "AUTH_SVC_TEST2", "Campus Reviewer", Collections.<String,String>emptyMap() );
        assertNotNull( "Returned qualifier list should not be null", qualifiers );
        assertEquals( "Qualifier list should have one entry", 1, qualifiers.size() );
        assertTrue( "campus code qualifier missing", qualifiers.get(0).containsKey(KimConstants.AttributeConstants.CAMPUS_CODE) );
        assertEquals( "campus code qualifier incorrect", "BL", qualifiers.get(0).get(KimConstants.AttributeConstants.CAMPUS_CODE) );
    }

    @Test
    public void testGetRoleQualifersForPrincipalByNamespaceAndRolenameWithQualifier() {
        Role rCampus = roleService.getRoleByNamespaceCodeAndName("AUTH_SVC_TEST2", "Campus Reviewer");
        assertNotNull( "Campus-based role missing from test data", rCampus );
        assertEquals( "Campus role type incorrect", "kt-campus", rCampus.getKimTypeId());

        List<Map<String, String>> qualifiers = roleService.getRoleQualifersForPrincipalByNamespaceAndRolename("p9", "AUTH_SVC_TEST2", "Campus Reviewer", Collections.singletonMap(KimConstants.AttributeConstants.CAMPUS_CODE, "BL") );
        assertNotNull( "Returned qualifier list should not be null", qualifiers );
        assertEquals( "Qualifier list should have one entry", 1, qualifiers.size() );
        assertTrue( "campus code qualifier missing", qualifiers.get(0).containsKey(KimConstants.AttributeConstants.CAMPUS_CODE) );
        assertEquals( "campus code qualifier incorrect", "BL", qualifiers.get(0).get(KimConstants.AttributeConstants.CAMPUS_CODE) );
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
        assertEquals("incorrect number of RoleResponsibilityAction returned", 1, rras.size());
        assertEquals("updated RoleResponsibilityAction does not match expected", updated, rras.get(0));
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
