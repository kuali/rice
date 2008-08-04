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
package org.kuali.rice.kim.service;

import java.util.HashMap;
import java.util.List;

import javax.xml.namespace.QName;

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kim.dto.EntityDTO;
import org.kuali.rice.kim.dto.GroupDTO;
import org.kuali.rice.kim.dto.GroupQualifiedRoleAttributeDTO;
import org.kuali.rice.kim.dto.GroupQualifiedRoleDTO;
import org.kuali.rice.kim.dto.PermissionDTO;
import org.kuali.rice.kim.dto.PersonDTO;
import org.kuali.rice.kim.dto.PrincipalDTO;
import org.kuali.rice.kim.dto.PrincipalQualifiedRoleAttributeDTO;
import org.kuali.rice.kim.dto.PrincipalQualifiedRoleDTO;
import org.kuali.rice.kim.dto.RoleDTO;
import org.kuali.rice.kim.test.KIMTestCase;

/**
 * Basic test to verify we can access the QualifiedRoleService through the GRL.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RoleServiceTest extends KIMTestCase {
	private static final String EXPECTED_GROUP_NAME = "Group1";
	private static final Long EXPECTED_GROUP_ROLE_ID = new Long(300);
	private static final String EXPECTED_PRINCIPAL_QUALIFIED_ROLE_ATTRIBUTE_VALUE = "Some role2";
	private static final String EXPECTED_PRINCIPAL_QUALIFIED_ROLE_ATTRIBUTE_NAME = "QualifiedRole2";
	private static final String EXPECTED_PRINCIPAL_QUALIFIED_ROLE_ATTRIBUTE2_VALUE = "Some role";
	private static final String EXPECTED_PRINCIPAL_QUALIFIED_ROLE_ATTRIBUTE2_NAME = "QualifiedRole";
	private static final String EXPECTED_PRINCIPAL_NAME = "jschmoe";
	private static final String EXPECTED_PERMISSION = "canSave";
	private RoleService roleService;
	private RoleService roleSoapService;

	private static final String URI = "KIM";
	private static final QName SOAP_SERVICE = new QName(URI, "roleSoapService");
	private static final QName JAVA_SERVICE = new QName(URI, "roleService");

	private static final Long EXPECTED_ROLE_ID = new Long(190);
	private static final Long EXPECTED_GROUP_ID = new Long(300);

	private static final Long EXPECTED_ENTITY_ID_FOR_ROLE = new Long(140);
	private static final Long EXPECTED_PERSON_ID_FOR_ROLE = new Long(160);
	private static final Long EXPECTED_QUALIFIED_PRINCIPAL_ID = new Long(160);
	private static final String EXPECTED_QUALIFIED_PRINCIPAL_NAME = "jschmoe";
	private static final Long EXPECTED_QUALIFIED_GROUP_ID = new Long(300);
	private static final String EXPECTED_QUALIFIED_GROUP_NAME = "Group1";
	private static final String EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTE_VALUE = "Finance";
	private static final String EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTE_NAME = "Department";
	private static final String EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTE2_VALUE = "Arts and Science";
	private static final String EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTE2_NAME = "College";
	private static final String EXPECTED_ROLE_NAME = "Dean";

	private static final HashMap<String, String> EXPECTED_PRINCIPAL_QUALIFIED_ROLE_ATTRIBUTES = new HashMap<String, String>();
	private static final HashMap<String, String> EXPECTED_PRINCIPAL_QUALIFIED_ROLE_ATTRIBUTES2 = new HashMap<String, String>();
	private static final HashMap<String, String> EXPECTED_PRINCIPAL_QUALIFIED_ROLE_ATTRIBUTES3 = new HashMap<String, String>();
	private static final HashMap<String, String> EXPECTED_PRINCIPAL_QUALIFIED_ROLE_ATTRIBUTES4 = new HashMap<String, String>();


	private static final HashMap<String, String> EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTES = new HashMap<String, String>();
	private static final HashMap<String, String> EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTES2 = new HashMap<String, String>();
	private static final HashMap<String, String> EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTES3 = new HashMap<String, String>();
	private static final HashMap<String, String> EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTES4 = new HashMap<String, String>();

	public void setUp() throws Exception {
		super.setUp();
		roleService = (RoleService) GlobalResourceLoader
				.getService(JAVA_SERVICE);
		roleSoapService = (RoleService) GlobalResourceLoader
				.getService(SOAP_SERVICE);

		EXPECTED_PRINCIPAL_QUALIFIED_ROLE_ATTRIBUTES.put(
				EXPECTED_PRINCIPAL_QUALIFIED_ROLE_ATTRIBUTE_NAME,
				EXPECTED_PRINCIPAL_QUALIFIED_ROLE_ATTRIBUTE_VALUE);
		EXPECTED_PRINCIPAL_QUALIFIED_ROLE_ATTRIBUTES2.put(
				EXPECTED_PRINCIPAL_QUALIFIED_ROLE_ATTRIBUTE_NAME,
				EXPECTED_PRINCIPAL_QUALIFIED_ROLE_ATTRIBUTE_VALUE);
		EXPECTED_PRINCIPAL_QUALIFIED_ROLE_ATTRIBUTES2.put(
				EXPECTED_PRINCIPAL_QUALIFIED_ROLE_ATTRIBUTE2_NAME,
				EXPECTED_PRINCIPAL_QUALIFIED_ROLE_ATTRIBUTE2_VALUE);
		EXPECTED_PRINCIPAL_QUALIFIED_ROLE_ATTRIBUTES3.put(
				EXPECTED_PRINCIPAL_QUALIFIED_ROLE_ATTRIBUTE_NAME + "x",
				EXPECTED_PRINCIPAL_QUALIFIED_ROLE_ATTRIBUTE_VALUE);
		EXPECTED_PRINCIPAL_QUALIFIED_ROLE_ATTRIBUTES4.put(
				EXPECTED_PRINCIPAL_QUALIFIED_ROLE_ATTRIBUTE_NAME,
				EXPECTED_PRINCIPAL_QUALIFIED_ROLE_ATTRIBUTE_VALUE + "x");

		EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTES.put(EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTE_NAME, EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTE_VALUE);
		EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTES2.put(EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTE_NAME, EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTE_VALUE);
		EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTES2.put(EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTE2_NAME, EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTE2_VALUE);
		EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTES3.put(EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTE_NAME + "x", EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTE_VALUE);
		EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTES4.put(EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTE_NAME, EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTE_VALUE + "x");

		}

	@Test
	public void getAllRoleNames_Java() {
		getAllRoleNames(roleService);
	}

	@Test
	public void getAllRoleNames_Soap() {
		getAllRoleNames(roleSoapService);
	}

	/**
	 * This method ...
	 *
	 */
	private static void getAllRoleNames(final RoleService roleService) {
		List<String> roles = roleService.getAllRoleNames();
		assertNotNull("Found no role names", roles);
		assertEquals("Wrong number of role names found", 2, roles.size());
		assertEquals("wrong role name", EXPECTED_ROLE_NAME, roles.get(0));
	}

	@Test
	public void getAllRoles_Java() {
		getAllRoles(roleService);
	}

	@Test
	public void getAllRoles_Soap() {
		getAllRoles(roleSoapService);
	}

	/**
	 * This method ...
	 *
	 */
	private static void getAllRoles(final RoleService roleService) {
		List<RoleDTO> roles = roleService.getAllRoles();
		assertNotNull("Found no roles", roles);
		assertEquals("Wrong number of roles found", 2, roles.size());

		final RoleDTO role = roles.get(0);
		assertNotNull("No role provided", role);
		assertEquals("wrong role name", EXPECTED_ROLE_NAME, role.getName());
	}

	@Test
	public void getRoleById_Java() {
		getRoleById(roleService);
	}

	@Test
	public void getRoleById_Soap() {
		getRoleById(roleSoapService);
	}

	/**
	 * This method ...
	 *
	 */
	private static void getRoleById(final RoleService roleService) {
		RoleDTO role = roleService.getRole(EXPECTED_ROLE_ID);
		assertNotNull("Found no role", role);
		assertEquals("Wrong role name", EXPECTED_ROLE_NAME, role.getName());
	}

	@Test
	public void getRoleByName_Java() {
		getRoleByName(roleService);
	}

	@Test
	public void getRoleByName_Soap() {
		getRoleByName(roleSoapService);
	}

	/**
	 * This method ...
	 *
	 */
	private static void getRoleByName(final RoleService roleService) {
		RoleDTO role = roleService.getRole(EXPECTED_ROLE_NAME);
		assertNotNull("Found no role", role);
		assertEquals("Wrong role name", EXPECTED_ROLE_NAME, role.getName());
	}

	@Test
	public void getGroupNamesWithRole_Java() {
		getGroupNamesWithRole(roleService);
	}

	@Test
	public void getGroupNamesWithRole_Soap() {
		getGroupNamesWithRole(roleSoapService);
	}

	/**
	 * This method ...
	 *
	 */
	private static void getGroupNamesWithRole(final RoleService roleService) {
		List<String> groupNames = roleService
				.getGroupNamesWithRole(EXPECTED_ROLE_NAME);
		assertNotNull("Found no group names", groupNames);
		assertEquals("Wrong number of group names found", 2, groupNames.size());
		assertEquals(EXPECTED_GROUP_NAME, groupNames.get(0));
	}

	@Test
	public void getGroupQualifiedRoles_Java() {
		getGroupQualifiedRoles(roleService);
	}

	@Test
	public void getGroupQualifiedRoles_Soap() {
		getGroupQualifiedRoles(roleSoapService);
	}

	/**
	 * This method ...
	 *
	 */
	private static void getGroupQualifiedRoles(final RoleService roleService) {
		List<GroupQualifiedRoleDTO> gqr = roleService
				.getGroupQualifiedRoles(EXPECTED_ROLE_NAME);
		assertNotNull("Found no group qualified roles", gqr);
		assertEquals("Wrong number of group qualified roles found", 2, gqr
				.size());

		GroupQualifiedRoleDTO group = gqr.get(0);
		assertEquals("Wrong role found", EXPECTED_ROLE_ID, group
				.getRoleId());
		assertEquals("Wrong group found", EXPECTED_GROUP_ID, group
				.getGroupId());

		final HashMap<String, GroupQualifiedRoleAttributeDTO> attrs = group.getQualifiedRoleAttributeDtos();

		assertNotNull("No qualified roles attributes found", attrs);

		assertEquals("Wrong number of qualified role attributes found", 2,
				attrs.size());

		GroupQualifiedRoleAttributeDTO attr = attrs
				.get(EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTE_NAME);
		assertNotNull("Did not find qualified attribute", attr);

		assertEquals("Wrong qualified attribute name found",
				EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTE_NAME, attr.getAttributeName());
		assertEquals("Wrong qualifed attribute value found",
				EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTE_VALUE, attr
						.getAttributeValue());
		assertEquals("Wrong role ID from qualified attribute",
				EXPECTED_ROLE_ID, attr.getRoleId());
		assertEquals("Wrong role name from qualified attribute",
				EXPECTED_ROLE_NAME, attr.getRoleDto().getName());
		assertEquals("Wrong principal ID from qualified attribute",
				EXPECTED_QUALIFIED_GROUP_ID, attr.getGroupId());

		gqr = roleService.getGroupQualifiedRoles(EXPECTED_ROLE_NAME + "x");
		assertEquals("Should not have found any qualified attributes for role",
				0, gqr.size());

	}

	@Test
	public void getGroupQualifiedRolesByAttribute_Java() {
		getGroupQualifiedRolesByAttribute(roleService);
	}

	@Test
	public void getGroupQualifiedRolesByAttribute_Soap() {
		getGroupQualifiedRolesByAttribute(roleSoapService);
	}

	/**
	 * This method ...
	 *
	 */
	private static void getGroupQualifiedRolesByAttribute(
			final RoleService roleService) {
		List<GroupQualifiedRoleDTO> gqr = roleService.getGroupQualifiedRoles(
				EXPECTED_ROLE_NAME, EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTES);
		assertNotNull("Found no attribute qualified roles", gqr);
		assertEquals("Wrong number of group qualified roles", 1, gqr.size());
		checkQualifiedGroupRole(gqr);

		gqr = roleService.getGroupQualifiedRoles(
				EXPECTED_ROLE_NAME, EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTES2);
		assertNotNull("Found no attribute qualified roles", gqr);
		assertEquals("Wrong number of group qualified roles", 1, gqr.size());
		checkQualifiedGroupRole(gqr);

		gqr = roleService.getGroupQualifiedRoles(
				EXPECTED_ROLE_NAME + "x", EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTES);
		assertNotNull("Should not have found groups", gqr);
		assertEquals("Should not have found groups", 0, gqr.size());

		gqr = roleService.getGroupQualifiedRoles(
				EXPECTED_ROLE_NAME, EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTES3);
		assertNotNull("Should not have found groups", gqr);
		assertEquals("Should not have found groups", 0, gqr.size());

		gqr = roleService.getGroupQualifiedRoles(
				EXPECTED_ROLE_NAME, EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTES4);
		assertNotNull("Should not have found groups", gqr);
		assertEquals("Should not have found groups", 0, gqr.size());
	}

	/**
	 * This method ...
	 *
	 * @param gqr
	 */
	private static void checkQualifiedGroupRole(List<GroupQualifiedRoleDTO> gqr) {
		final GroupQualifiedRoleDTO role = gqr.get(0);
		assertNotNull("No qualifed role attributes found", role);

		assertEquals("Wrong qualified group role found", EXPECTED_GROUP_ID,
				role.getGroupId());
		assertEquals("Wrong qualified role found", EXPECTED_ROLE_ID, role
				.getRoleId());

		final GroupDTO groupDto = role.getGroupDto();
		assertNotNull("No group dto", groupDto);
		assertEquals("Wrong qualified group ID", EXPECTED_GROUP_ID, groupDto.getId());
		assertEquals("Wrong qualified group name", EXPECTED_QUALIFIED_GROUP_NAME, groupDto.getName());

		final RoleDTO roleDto = role.getRoleDto();
		assertNotNull("No role dto", roleDto);
		assertEquals("Wrong role ID", EXPECTED_ROLE_ID, roleDto.getId());
		assertEquals("Wrong role name", EXPECTED_ROLE_NAME, roleDto.getName());

		final HashMap<String, GroupQualifiedRoleAttributeDTO> attrs = role.getQualifiedRoleAttributeDtos();
		assertNotNull("No qualified role attributes found", attrs);

		final GroupQualifiedRoleAttributeDTO attr = attrs.get(EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTE_NAME);
		assertNotNull("No qualified role attribute found", attr);
		assertEquals("Wrong qualified attribute name", EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTE_NAME, attr.getAttributeName());
		assertEquals("Wrong qualified attribute value", EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTE_VALUE, attr.getAttributeValue());
	}

	@Test
	public void getGroupsWithRole_Java() {
		getGroupsWithRole(roleService);
	}

	@Test
	public void getGroupsWithRole_Soap() {
		getGroupsWithRole(roleSoapService);
	}

	/**
	 * This method ...
	 *
	 */
	private static void getGroupsWithRole(final RoleService roleService) {
		List<GroupDTO> groups = roleService
				.getGroupsWithRole(EXPECTED_ROLE_NAME);
		assertNotNull("Found no groups with roles", groups);
		assertEquals("Wrong number of groups found", 2, groups.size());
		final GroupDTO g = groups.get(0);
		assertEquals("Wrong ID", EXPECTED_GROUP_ROLE_ID, g.getId());
		assertEquals("Wrong group name", EXPECTED_GROUP_NAME, g.getName());

		groups = roleService
		.getGroupsWithRole(EXPECTED_ROLE_NAME + "x");
		assertNotNull("Found no groups with roles", groups);
		assertEquals("Found groups for bad role", 0, groups.size());
	}

	@Test
	public void getPermissionNamesForRole_Java() {
		getPermissionNamesForRole(roleService);
	}

	@Test
	public void getPermissionNamesForRole_Soap() {
		getPermissionNamesForRole(roleSoapService);
	}

	/**
	 * This method ...
	 *
	 */
	private static void getPermissionNamesForRole(final RoleService roleService) {
		List<String> permissions = roleService
				.getPermissionNamesForRole(EXPECTED_ROLE_NAME);
		assertNotNull("Found no permission names for role", permissions);
		assertEquals("Wrong number of permissions for role found", 2,
				permissions.size());
		assertEquals("Wrong permission id", EXPECTED_PERMISSION, permissions
				.get(0));
	}

	@Test
	public void getPermissionsForRole_Java() {
		getPermissionsForRole(roleService);
	}

	@Test
	public void getPermissionsForRole_Soap() {
		getPermissionsForRole(roleSoapService);
	}

	/**
	 * This method ...
	 *
	 */
	private static void getPermissionsForRole(final RoleService roleService) {
		List<PermissionDTO> permissions = roleService
				.getPermissionsForRole(EXPECTED_ROLE_NAME);
		assertNotNull("Found no permissions", permissions);
		assertEquals("Wrong number of permissions found", 2, permissions.size());
		assertEquals("Wrong permission name", EXPECTED_PERMISSION, permissions
				.get(0).getName());

		permissions = roleService
		.getPermissionsForRole(EXPECTED_ROLE_NAME + "x");
		assertNotNull("Found no permissions", permissions);
		assertEquals("Found permissions for bad role", 0, permissions.size());
	}

	@Test
	public void getPersonIdsWithRole_Java() {
		getPersonIdsWithRole(roleService);
	}

	@Test
	public void getPersonIdsWithRole_Soap() {
		getPersonIdsWithRole(roleSoapService);
	}

	/**
	 * This method ...
	 *
	 */
	private static void getPersonIdsWithRole(final RoleService roleService) {
		List<Long> persons = roleService
				.getPersonIdsWithRole(EXPECTED_ROLE_NAME);
		assertNotNull("Found no personsIds with role", persons);
		assertEquals("Wrong number of persons found", 2, persons.size());
		assertEquals("Wrong person found", persons.get(0),
				EXPECTED_PERSON_ID_FOR_ROLE);
	}

	@Test
	public void getPersonsWithRole_Java() {
		getPersonsWithRole(roleService);
	}

	@Test
	public void getPersonsWithRole_Soap() {
		getPersonsWithRole(roleSoapService);
	}

	/**
	 * This method ...
	 *
	 */
	private static void getPersonsWithRole(final RoleService roleService) {
		List<PersonDTO> persons = roleService
				.getPersonsWithRole(EXPECTED_ROLE_NAME);
		assertNotNull("Found no persons with role", persons);
		assertEquals("Did not find one person", 2, persons.size());
		assertEquals("Wrong person found", EXPECTED_PERSON_ID_FOR_ROLE, persons
				.get(0).getId());

		persons = roleService
		.getPersonsWithRole(EXPECTED_ROLE_NAME + "x");
		assertNotNull("Found no persons with bad role", persons);
		assertEquals("Found person for bad role", 0, persons.size());
	}

	@Test
	public void getPrincipalNamesWithRole_Java() {
		getPrincipalNamesWithRole(roleService);
	}

	@Test
	public void getPrincipalNamesWithRole_Soap() {
		getPrincipalNamesWithRole(roleSoapService);
	}

	/**
	 * This method ...
	 *
	 */
	private static void getPrincipalNamesWithRole(final RoleService roleService) {
		List<String> principals = roleService
				.getPrincipalNamesWithRole(EXPECTED_ROLE_NAME);
		assertNotNull("Found no principal names", principals);
		assertEquals("Wrong number of principal names found", 2, principals
				.size());
		assertEquals("Wrong principal name", EXPECTED_PRINCIPAL_NAME,
				principals.get(0));
	}

	@Test
	public void getPrincipalsWithRole_Java() {
		getPrincipalsWithRole(roleService);
	}

	@Test
	public void getPrincipalsWithRole_Soap() {
		getPrincipalsWithRole(roleSoapService);
	}

	/**
	 * This method ...
	 *
	 */
	private static void getPrincipalsWithRole(final RoleService roleService) {
		List<PrincipalDTO> principals = roleService
				.getPrincipalsWithRole(EXPECTED_ROLE_NAME);
		assertNotNull("Found no principals with role", principals);
		assertEquals("Wrong number of principals with role found", 2,
				principals.size());
		assertEquals("Wrong principal name", EXPECTED_PRINCIPAL_NAME,
				principals.get(0).getName());

		principals = roleService
		.getPrincipalsWithRole(EXPECTED_ROLE_NAME + "x");
		assertNotNull("Found no principals with bad role", principals);
		assertEquals("Wrong number of principals with bad role found", 0,
				principals.size());
	}

	@Test
	public void getPrincipalQualifiedRoles_Java() {
		getPrincipalQualifiedRoles(roleService);
	}

	@Test
	public void getPrincipalQualifiedRoles_Soap() {
		getPrincipalQualifiedRoles(roleSoapService);
	}

	/**
	 * This method ...
	 *
	 */
	private static void getPrincipalQualifiedRoles(final RoleService roleService) {
		List<PrincipalQualifiedRoleDTO> principalQualified = roleService
				.getPrincipalQualifiedRoles(EXPECTED_ROLE_NAME);
		assertNotNull("Found no principal qualified roles", principalQualified);
		assertEquals("Wrong number of principal qualified roles", 2,
				principalQualified.size());

		final PrincipalQualifiedRoleDTO dto = principalQualified.get(0);
		assertNotNull("Not PrincipalQualifiedRoleDTO", dto);
		assertEquals("Wrong qualified principal id found",
				EXPECTED_QUALIFIED_PRINCIPAL_ID, dto.getPrincipalId());
		assertEquals("Wrong qualified principal found",
				EXPECTED_QUALIFIED_PRINCIPAL_ID, dto.getPrincipalDto().getId());
		assertNotNull("No qualified roles attributes found", dto
				.getQualifiedRoleAttributeDtos());

		final HashMap<String, PrincipalQualifiedRoleAttributeDTO> attrs = dto
				.getQualifiedRoleAttributeDtos();
		assertNotNull("No qualified role attributes found", attrs);
		assertEquals("Wrong number of qualified role attributes found", 2,
				attrs.size());

		PrincipalQualifiedRoleAttributeDTO attr = attrs.get(EXPECTED_PRINCIPAL_QUALIFIED_ROLE_ATTRIBUTE_NAME);
		assertNotNull("Did not find qualified attribute", attr);
		assertEquals("Wrong qualified attribute name found", EXPECTED_PRINCIPAL_QUALIFIED_ROLE_ATTRIBUTE_NAME, attr.getAttributeName());
		assertEquals("Wrong qualifed attribute value found", EXPECTED_PRINCIPAL_QUALIFIED_ROLE_ATTRIBUTE_VALUE, attr.getAttributeValue());
		assertEquals("Wrong role ID from qualified attribute", EXPECTED_ROLE_ID, attr.getRoleId());
		assertEquals("Wrong role name from qualified attribute", EXPECTED_ROLE_NAME, attr.getRoleDto().getName());
		assertEquals("Wrong principal ID from qualified attribute", EXPECTED_QUALIFIED_PRINCIPAL_ID, attr.getPrincipalId());
		assertEquals("Wrong principal name from qualifeid attribute", EXPECTED_QUALIFIED_PRINCIPAL_NAME, attr.getPrincipalDto().getName());

		principalQualified = roleService.getPrincipalQualifiedRoles(EXPECTED_ROLE_NAME + "x");
		assertEquals("Should not have found any qualified attributes for role", 0, principalQualified.size());

	}

	@Test
	public void getPrincipalQualifiedRolesByAttribute_Java() {
		getPrincipalQualifiedRolesByAttribute(roleService);
	}

	@Test
	public void getPrincipalQualifiedRolesByAttribute_Soap() {
		getPrincipalQualifiedRolesByAttribute(roleSoapService);
	}

	/**
	 * This method ...
	 *
	 */
	private static void getPrincipalQualifiedRolesByAttribute(
			final RoleService roleService) {
		List<PrincipalQualifiedRoleDTO> principalQualified = roleService
				.getPrincipalQualifiedRoles(EXPECTED_ROLE_NAME,
						EXPECTED_PRINCIPAL_QUALIFIED_ROLE_ATTRIBUTES);
		assertNotNull("Found no principal qualified roles by attribute",
				principalQualified);
		assertEquals("Wrong number of principals found", 2, principalQualified
				.size());
		checkPrincipalQualifiedRolesByAttribute(principalQualified);

		principalQualified = roleService
		.getPrincipalQualifiedRoles(EXPECTED_ROLE_NAME,
				EXPECTED_PRINCIPAL_QUALIFIED_ROLE_ATTRIBUTES2);
		assertNotNull("Found no principal qualified roles by attribute",
				principalQualified);
		assertEquals("Wrong number of principals found", 1, principalQualified
				.size());
		checkPrincipalQualifiedRolesByAttribute(principalQualified);


		principalQualified = roleService
		.getPrincipalQualifiedRoles(EXPECTED_ROLE_NAME + "x",
				EXPECTED_PRINCIPAL_QUALIFIED_ROLE_ATTRIBUTES);
		assertNotNull("Did not return list", principalQualified);
		assertEquals("Found principals for bad role name", 0, principalQualified.size());

		principalQualified = roleService
		.getPrincipalQualifiedRoles(EXPECTED_ROLE_NAME + "x",
				EXPECTED_PRINCIPAL_QUALIFIED_ROLE_ATTRIBUTES3);
		assertNotNull("Did not return list", principalQualified);
		assertEquals("Found principals for invalid qualifed attribute name", 0, principalQualified.size());

		principalQualified = roleService
		.getPrincipalQualifiedRoles(EXPECTED_ROLE_NAME + "x",
				EXPECTED_PRINCIPAL_QUALIFIED_ROLE_ATTRIBUTES4);
		assertNotNull("Did not return list", principalQualified);
		assertEquals("Found principals for invalid qualifed attribute value", 0, principalQualified.size());

	}

	/**
	 * This method test a Qualified Principal object for correctness
	 *
	 * @param principalQualified
	 */
	private static void checkPrincipalQualifiedRolesByAttribute(
			List<PrincipalQualifiedRoleDTO> principalQualified) {

		final PrincipalQualifiedRoleDTO principal = principalQualified.get(0);
		assertNotNull("No principal dto", principal);
		assertEquals("Wrong principal found", EXPECTED_QUALIFIED_PRINCIPAL_ID,
				principal.getPrincipalId());
		assertNotNull("No principalDTO", principal.getPrincipalDto());
		assertEquals("Wrong principalDTO ID found", EXPECTED_QUALIFIED_PRINCIPAL_ID,
				principal.getPrincipalDto().getId());
		assertEquals("Wrong role", EXPECTED_ROLE_ID, principal.getRoleId());
		assertEquals("Wrong role", EXPECTED_ROLE_ID, principal.getRoleDto()
				.getId());
		assertNotNull("No role dto", principal.getRoleDto());
	}

	@Test
	public void getEntityIdsWithRole_Java() {
		getEntityIdsWithRole(roleService);
	}

	@Test
	public void getEntityIdsWithRole_Soap() {
		getEntityIdsWithRole(roleSoapService);
	}

	/**
	 * This method ...
	 *
	 */
	private static void getEntityIdsWithRole(final RoleService roleService) {
		List<Long> entitys = roleService
				.getEntityIdsWithRole(EXPECTED_ROLE_NAME);
		assertNotNull("Found no entityIds with role", entitys);
		assertEquals("Wrong entityId", EXPECTED_ENTITY_ID_FOR_ROLE, entitys
				.get(0));

		entitys = roleService
		.getEntityIdsWithRole(EXPECTED_ROLE_NAME + "x");
		assertNotNull("Found no entityIds with role", entitys);
		assertEquals("Found entity for bad role", 0, entitys.size());
	}

	@Test
	public void getEntitysWithRole_Java() {
		getEntitysWithRole(roleService);
	}

	@Test
	public void getEntitysWithRole_Soap() {
		getEntitysWithRole(roleSoapService);
	}

	/**
	 * This method ...
	 *
	 */
	private static void getEntitysWithRole(final RoleService roleService) {
		List<EntityDTO> entitys = roleService
				.getEntitysWithRole(EXPECTED_ROLE_NAME);
		assertNotNull("Found no entitys with role", entitys);
		assertEquals("Found more than one entity for role", 2, entitys.size());
		assertEquals("Wrong entityId", EXPECTED_ENTITY_ID_FOR_ROLE, entitys
				.get(0).getId());
	}
}
