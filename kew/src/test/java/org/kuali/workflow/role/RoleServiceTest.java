/*
 * Copyright 2005-2007 The Kuali Foundation.
 *
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
package org.kuali.workflow.role;

import java.sql.Timestamp;

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.workflow.identity.IdentityType;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.routetemplate.RuleAttribute;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RoleServiceTest extends KEWTestCase {

	private RoleService roleService;

	@Override
	protected void setUpTransaction() throws Exception {
		super.setUpTransaction();
		roleService = KEWServiceLocator.getRoleService();
	}

	@Ignore
	@Test
	public void testSaveRole() throws Exception {
		Role role = new Role();
		role.setDescription("My Description");
		role.setName("RoleServiceTestRole");
		assertNull(role.getRoleId());
		roleService.save(role);
		assertNotNull(role.getRoleId());

		// refetch the Role
		Role roleRefetched = roleService.findRoleByName(role.getName());
		assertEquals("Role IDs should be the same.", role.getRoleId(), roleRefetched.getRoleId());
		roleRefetched = roleService.findRoleById(role.getRoleId());
		assertEquals("Role names should be the same.", role.getName(), roleRefetched.getName());

		// add some attributes
		RuleAttribute ruleAttribute = KEWServiceLocator.getRuleAttributeService().findByName("TestRuleAttribute");
		assertNotNull("TestRuleAttribute should exist", ruleAttribute);
		RoleAttribute roleAttribute = new RoleAttribute();
		roleAttribute.setRole(roleRefetched);
		roleAttribute.setRuleAttribute(ruleAttribute);
		roleRefetched.getAttributes().add(roleAttribute);
		roleService.save(roleRefetched);

		assertEquals("Role IDs should be the same.", roleRefetched.getRoleId(), roleAttribute.getRoleId());
		assertEquals("Rule Attribute IDs should be the same.", ruleAttribute.getRuleAttributeId(), roleAttribute.getRuleAttributeId());
		assertNotNull("Role Attribute should have an ID.", roleAttribute.getRoleAttributeId());

		// refetch the Role
		roleRefetched = roleService.findRoleByName(role.getName());
		assertEquals("Should have 1 attribute", 1, roleRefetched.getAttributes().size());

		// save a Role without a description, should be legal
		role = new Role();
		role.setName("RoleServiceTestRole2");
		roleService.save(role);
		role = roleService.findRoleByName("RoleServiceTestRole2");
		assertNull(role.getDescription());
		role.setDescription("");
		roleService.save(role);
		role = roleService.findRoleByName("RoleServiceTestRole2");
		// the database doesn't store an empty string but rather NULL (could this be Oracle-specific?)
		assertNull(role.getDescription());
	}

	@Ignore
	@Test
	public void testRoleNameUnique() throws Exception {
		Role role = new Role();
		role.setDescription("desc1");
		role.setName("RoleServiceTestRole");
		assertNull(role.getRoleId());
		roleService.save(role);

		// try saving a role with the same name, we should encounter an error
		Role role2 = new Role();
		role2.setDescription("desc2");
		role2.setName("RoleServiceTestRole");
		try {
			roleService.save(role2);
			fail("An error should have been thrown when attempting to save a Role with a duplicate name.");
		} catch (Exception e) {}

		// while we're at it, check that we can't enter an empty role name
		role2.setName(null);
		try {
			roleService.save(role2);
			fail("An error should have been thrown when attempting to save a Role with a null name.");
		} catch (Exception e) {}

		role2.setName("");
		try {
			roleService.save(role2);
			fail("An error should have been thrown when attempting to save a Role with an empty name.");
		} catch (Exception e) {}
	}

	@Ignore
	@Test
	public void testSaveQualifiedRole() throws Exception {
		// create and save the Role
		Role role = new Role();
		role.setName("RoleServiceTestRole-testSaveQualifiedRole");
		RoleAttribute roleAttribute = new RoleAttribute();
		roleAttribute.setRole(role);
		roleAttribute.setRuleAttribute(KEWServiceLocator.getRuleAttributeService().findByName("TestRuleAttribute"));
		role.getAttributes().add(roleAttribute);
		roleService.save(role);

		// create qualified role
		QualifiedRole qRole = new QualifiedRole();
		Timestamp now = new Timestamp(System.currentTimeMillis());
		qRole.setActivationDate(now);
		qRole.setActive(true);
		qRole.setCurrent(true);
		qRole.setDescription("My First Qualified Role");
		qRole.setRole(role);
		qRole.setVersionNumber(new Integer(0));

		// add members
		QualifiedRoleMember member1 = new QualifiedRoleMember();
		member1.setMemberId("ewestfal");
		member1.setMemberType(IdentityType.USER);
		member1.setQualifiedRole(qRole);
		qRole.getMembers().add(member1);

		QualifiedRoleMember member2 = new QualifiedRoleMember();
		member2.setMemberId("MyWorkgroup");
		member2.setMemberType(IdentityType.GROUP);
		member2.setQualifiedRole(qRole);
		qRole.getMembers().add(member2);

		// add extension 1
		QualifiedRoleExtension extension1 = new QualifiedRoleExtension();
		extension1.setQualifiedRole(qRole);
		extension1.setRoleAttribute(roleAttribute);

		QualifiedRoleExtensionValue extension1Value1 = new QualifiedRoleExtensionValue();
		extension1Value1.setExtension(extension1);
		extension1Value1.setKey("chart");
		extension1Value1.setValue("BL");

		QualifiedRoleExtensionValue extension1Value2 = new QualifiedRoleExtensionValue();
		extension1Value2.setExtension(extension1);
		extension1Value2.setKey("org");
		extension1Value2.setValue("PSY");

		extension1.getExtensionValues().add(extension1Value1);
		extension1.getExtensionValues().add(extension1Value2);

		qRole.getExtensions().add(extension1);

		// add extension 2
		QualifiedRoleExtension extension2 = new QualifiedRoleExtension();
		extension2.setQualifiedRole(qRole);
		extension2.setRoleAttribute(roleAttribute);

		QualifiedRoleExtensionValue extension2Value1 = new QualifiedRoleExtensionValue();
		extension2Value1.setExtension(extension2);
		extension2Value1.setKey("totalAmount");
		extension2Value1.setValue("1234");
		extension2.getExtensionValues().add(extension2Value1);

		qRole.getExtensions().add(extension2);

		// save the qualified role
		roleService.save(qRole);

		// reload it and make sure all of the data was sucessfully saved
		QualifiedRole qRole2 = roleService.findQualifiedRoleById(qRole.getQualifiedRoleId());
		assertNotNull(qRole2);
		assertNotNull(qRole2.getQualifiedRoleId());
		assertTrue(qRole2.isActive());
		assertTrue(qRole2.isCurrent());
		assertNotNull(qRole2.getRole());
		assertEquals(role.getName(), qRole2.getRole().getName());

		// check the members
		assertEquals(2, qRole2.getMembers().size());
		member1 = qRole2.getMembers().get(0);
		member2 = qRole2.getMembers().get(1);
		assertEquals("ewestfal", member1.getMemberId());
		assertEquals(IdentityType.USER, member1.getMemberType());
		assertEquals(member1.getQualifiedRoleMemberId(), member1.getResponsibilityId());
		assertEquals("MyWorkgroup", member2.getMemberId());
		assertEquals(IdentityType.GROUP, member2.getMemberType());
		assertEquals(member2.getQualifiedRoleMemberId(), member2.getResponsibilityId());

		// remove a member
		qRole2.getMembers().remove(1);
		roleService.save(qRole2);

		// now check that it got removed
		qRole2 = roleService.findQualifiedRoleById(qRole2.getQualifiedRoleId());
		assertEquals(1, qRole2.getMembers().size());
		assertEquals("ewestfal", qRole2.getMembers().get(0).getMemberId());

		// check the extensions
		assertEquals(2, qRole2.getExtensions().size());
		extension1 = qRole2.getExtensions().get(0);
		extension2 = qRole2.getExtensions().get(1);

		assertEquals("TestRuleAttribute", extension1.getRoleAttribute().getRuleAttribute().getName());
		assertEquals(2, extension1.getExtensionValues().size());
		extension1Value1 = extension1.getExtensionValues().get(0);
		extension1Value2 = extension1.getExtensionValues().get(1);
		assertEquals("chart", extension1Value1.getKey());
		assertEquals("BL", extension1Value1.getValue());
		assertEquals("org", extension1Value2.getKey());
		assertEquals("PSY", extension1Value2.getValue());

		// remove a value and an extension
		extension1.getExtensionValues().remove(extension1Value1);
		qRole2.getExtensions().remove(extension2);
		roleService.save(qRole2);

		// verify that there is only 1 extension left with 1 value
		qRole2 = roleService.findQualifiedRoleById(qRole2.getQualifiedRoleId());
		assertEquals(1, qRole2.getExtensions().size());
		extension1 = qRole2.getExtensions().get(0);
		assertEquals(1, extension1.getExtensionValues().size());
		assertEquals("org", extension1.getExtensionValues().get(0).getKey());
	}

}
