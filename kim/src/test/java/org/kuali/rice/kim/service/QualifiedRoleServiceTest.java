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
import java.util.Map;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kuali.rice.kim.dto.EntityDTO;
import org.kuali.rice.kim.dto.GroupDTO;
import org.kuali.rice.kim.dto.PersonDTO;
import org.kuali.rice.kim.dto.PrincipalDTO;
import org.kuali.rice.kim.dto.RoleDTO;
import org.kuali.rice.kim.test.KIMTestCase;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

/**
 * Basic test to verify we can access the QualifiedRoleService through the GRL.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class QualifiedRoleServiceTest extends KIMTestCase {
    private static final String EXPECTED_GROUP_NAME = "Group1";
	private static final String EXPECTED_ROLE_NAME = "Dean";
    private static final String EXPECTED_FIRST_PRINCIPAL_NAME = "jschmoe";
    private static final HashMap<String, String> EXPECTED_QUALIFIED_PRINCIPAL_ROLE_ATTRIBUTE = new HashMap<String, String>();
    private static final HashMap<String, String> EXPECTED_QUALIFIED_PRINCIPAL_ROLE_ATTRIBUTES = new HashMap<String, String>();
    private static final HashMap<String, String> EXPECTED_QUALIFIED_GROUP_ROLE_ATTRIBUTE = new HashMap<String, String>();
    private static final HashMap<String, String> EXPECTED_QUALIFIED_GROUP_ROLE_ATTRIBUTES = new HashMap<String, String>();
    private static final HashMap<String, String> EXPECTED_QUALIFIED_ROLE_ATTRIBUTES = new HashMap<String, String>();
    private static final HashMap<String, String> EXPECTED_QUALIFIED_ENTITY_ATTRIBUTES = new HashMap<String, String>();
    private static final Long EXPECTED_PERSON_ID = new Long(160);
    private static final Long EXPECTED_ENTITY_ID = new Long(140);
    private QualifiedRoleService qualifiedRoleService;
    private QualifiedRoleService qualifiedRoleSoapService;

    private static final String URI = "KIM";
    private static final QName SOAP_SERVICE = new QName(URI, "qualifiedRoleSoapService");
    private static final QName JAVA_SERVICE = new QName(URI, "qualifiedRoleService");

    public void setUp() throws Exception {
        super.setUp();
        qualifiedRoleService = (QualifiedRoleService) GlobalResourceLoader.getService(JAVA_SERVICE);
        qualifiedRoleSoapService = (QualifiedRoleService) GlobalResourceLoader.getService(SOAP_SERVICE);
        EXPECTED_QUALIFIED_PRINCIPAL_ROLE_ATTRIBUTE.put("QualifiedRole2", "Some role2");
        EXPECTED_QUALIFIED_PRINCIPAL_ROLE_ATTRIBUTES.put("QualifiedRole", "Some role");
        EXPECTED_QUALIFIED_PRINCIPAL_ROLE_ATTRIBUTES.put("QualifiedRole2", "Some role2");

        EXPECTED_QUALIFIED_ROLE_ATTRIBUTES.put("Account Number", "12345");
        EXPECTED_QUALIFIED_GROUP_ROLE_ATTRIBUTE.put("College", "Arts and Science");
        EXPECTED_QUALIFIED_GROUP_ROLE_ATTRIBUTES.put("Department", "Finance");
        EXPECTED_QUALIFIED_GROUP_ROLE_ATTRIBUTES.put("College", "Arts and Science");
        EXPECTED_QUALIFIED_ENTITY_ATTRIBUTES.put("EmailAddress", "kuali-rice@googlegroups.com");
    }

    @Test
    public void getGroupNames() {
        List<String> groups = qualifiedRoleService.getGroupNames(EXPECTED_ROLE_NAME, EXPECTED_QUALIFIED_GROUP_ROLE_ATTRIBUTE);
        assertNotNull("Found no groups", groups);
        assertEquals("Wrong number of groups found", 2, groups.size());
        assertEquals("Wrong group name found", EXPECTED_GROUP_NAME, groups.get(0));

        groups = qualifiedRoleService.getGroupNames(EXPECTED_ROLE_NAME, EXPECTED_QUALIFIED_GROUP_ROLE_ATTRIBUTES);
        assertNotNull("Found no groups", groups);
        assertTrue("Wrong number of groups found", groups.size() == 1);
        assertEquals("Wrong group name found", EXPECTED_GROUP_NAME, groups.get(0));
    }

    @Test
    public void getGroups() {
        List<GroupDTO> groups = qualifiedRoleService.getGroups(EXPECTED_ROLE_NAME, EXPECTED_QUALIFIED_GROUP_ROLE_ATTRIBUTE);
        assertNotNull("Found no groups", groups);
        assertEquals("Wrong number of groups found", 2, groups.size());
        assertEquals("Wrong group found", EXPECTED_GROUP_NAME, groups.get(0).getName());
        groups = qualifiedRoleService.getGroups(EXPECTED_ROLE_NAME, EXPECTED_QUALIFIED_GROUP_ROLE_ATTRIBUTE);
        assertNotNull("Found no groups", groups);
        assertEquals("Wrong number of groups found",1, groups.size());
        assertEquals("Wrong group found", EXPECTED_GROUP_NAME, groups.get(0).getName());

    }

    @Test
    public void getPersonIds() {
        List<Long> personsIds = qualifiedRoleService.getPersonIds(EXPECTED_ROLE_NAME, EXPECTED_QUALIFIED_PRINCIPAL_ROLE_ATTRIBUTE);
        assertNotNull("Found no personsIds", personsIds);
        assertEquals("Did not find right number of matching persons", 2, personsIds.size());
        assertEquals("Wrong first person found", EXPECTED_PERSON_ID, personsIds.get(0));

        personsIds = qualifiedRoleService.getPersonIds(EXPECTED_ROLE_NAME, EXPECTED_QUALIFIED_PRINCIPAL_ROLE_ATTRIBUTES);
        assertNotNull("Found no personsIds", personsIds);
        assertEquals("Did not find right number of matching persons", 1, personsIds.size());
        assertEquals("Wrong first person found", EXPECTED_PERSON_ID, personsIds.get(0));
}

    @Test
    public void getPersons() {
        List<PersonDTO> persons = qualifiedRoleService.getPersons(EXPECTED_ROLE_NAME, EXPECTED_QUALIFIED_PRINCIPAL_ROLE_ATTRIBUTE);
        assertNotNull("Found no persons", persons);
        assertEquals("Did not find right number of matching persons", 2, persons.size());
        assertEquals("Wrong first person found", EXPECTED_PERSON_ID, persons.get(0).getId());

        persons = qualifiedRoleService.getPersons(EXPECTED_ROLE_NAME, EXPECTED_QUALIFIED_PRINCIPAL_ROLE_ATTRIBUTES);
        assertNotNull("Found no persons", persons);
        assertEquals("Did not find right number of matching persons", 1, persons.size());
        assertEquals("Wrong first person found", EXPECTED_PERSON_ID, persons.get(0).getId());
    }

    @Test
    public void getPrincipalNames() {
        List<String> principalNames = qualifiedRoleService.getPrincipalNames(EXPECTED_ROLE_NAME, EXPECTED_QUALIFIED_PRINCIPAL_ROLE_ATTRIBUTE);
        assertNotNull("Found no principal names", principalNames);
        assertEquals("Did not find right number of matching principal", 2, principalNames.size());
        assertEquals("Wrong first principal found", EXPECTED_FIRST_PRINCIPAL_NAME, principalNames.get(0));
    }

    @Test
    public void getPrincipals() {
        List<PrincipalDTO> principals = qualifiedRoleService.getPrincipals(EXPECTED_ROLE_NAME, EXPECTED_QUALIFIED_PRINCIPAL_ROLE_ATTRIBUTE);
        assertNotNull("Found no principals", principals);
        assertEquals("Did not find right number of matching principal", 2, principals.size());
        assertEquals("Wrong first principal found", EXPECTED_FIRST_PRINCIPAL_NAME, principals.get(0).getName());
    }

    @Test
    public void  getRoleNames() {
        List<String> roleNames = qualifiedRoleService.getRoleNames(EXPECTED_QUALIFIED_ROLE_ATTRIBUTES);
        assertNotNull("Found no role names", roleNames);
        assertEquals("Wrong number of roles found", 1, roleNames.size());
        assertEquals("Wrong first role name", EXPECTED_ROLE_NAME, roleNames.get(0));
    }

    @Test
    public void getRoles() {
        List<RoleDTO> roles = qualifiedRoleService.getRoles(EXPECTED_QUALIFIED_ROLE_ATTRIBUTES);
        assertNotNull("Found no roles", roles);
        assertEquals("Wrong number of roles found", 1, roles.size());
        assertEquals("Wrong first role name", EXPECTED_ROLE_NAME, roles.get(0).getName());
    }

    @Test
    public void getEntityIds() {
        List<Long> entityIds = qualifiedRoleService.getEntityIds(EXPECTED_ROLE_NAME, EXPECTED_QUALIFIED_ENTITY_ATTRIBUTES);
        assertNotNull("Found no entityIds", entityIds);
        assertEquals("Wrong number of entity IDs found", 1, entityIds.size());
        assertEquals("Wrong entity ID", EXPECTED_ENTITY_ID, entityIds.get(0));
    }

    @Test
    public void getEntitys() {
        List<EntityDTO> entitys = qualifiedRoleService.getEntitys(EXPECTED_ROLE_NAME, EXPECTED_QUALIFIED_ENTITY_ATTRIBUTES);
        assertNotNull("Found no entitys", entitys);
        assertEquals("Wrong number of entity IDs found", 1, entitys.size());
        assertEquals("Wrong entity ID", EXPECTED_ENTITY_ID, entitys.get(0).getId());
    }
}
