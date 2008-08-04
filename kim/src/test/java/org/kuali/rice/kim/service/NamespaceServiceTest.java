/*
 * Copyright 2007 The Kuali Foundation Licensed under the Educational Community License, Version 1.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.opensource.org/licenses/ecl1.php Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package org.kuali.rice.kim.service;

import java.util.HashMap;
import java.util.List;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kim.dto.NamespaceDTO;
import org.kuali.rice.kim.dto.NamespaceDefaultAttributeDTO;
import org.kuali.rice.kim.dto.PermissionDTO;
import org.kuali.rice.kim.test.KIMTestCase;

/**
 * Basic test to verify we can access the NamespaceService through the GRL.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */

public class NamespaceServiceTest extends KIMTestCase {
    private static final int EXPECTED_NUMBER_OF_NAMESPACES = 4;
	private static final String KIM_TEST_NAMESPACE_NAME = "KIM";
    private static final String KIM_TEST_NAMESPACE_DEFAULT_ATTRIBUTE_NAME = "FirstName";
    private static final String KIM_TEST_PERMISSION_NAME = "canSave";
    private static final String URI = "KIM";
    private static final QName SOAP_SERVICE = new QName(URI, "namespaceSoapService");
    private static final QName JAVA_SERVICE = new QName(URI, "namespaceService");

    NamespaceService namespaceService;
    NamespaceService namespaceSoapService ;

    public void setUp() throws Exception {
        super.setUp();
        namespaceService = (NamespaceService) GlobalResourceLoader.getService(JAVA_SERVICE);
        namespaceSoapService = (NamespaceService) GlobalResourceLoader.getService(SOAP_SERVICE);
    }

    @Test
    public void testGetAllNamespaceNames_SyncJava() throws Exception {
        testGetAllNamespaceNames(namespaceService);
    }

    @Test
    public void testGetAllNamespaceNames_SyncSOAP() throws Exception {
        testGetAllNamespaceNames(namespaceSoapService);
    }

    private static void testGetAllNamespaceNames(final NamespaceService namespaceService) {
        final List<String> names = namespaceService.getAllNamespaceNames();
        assertEquals("Wrong number of namespaces found", EXPECTED_NUMBER_OF_NAMESPACES, names.size());
        assertEquals("Wrong first namespace name found", KIM_TEST_NAMESPACE_NAME, names.get(0));
    }

    @Test
    public void testGetAllNamespaces_SyncJava() {
        testGetAllNamespaces(namespaceService);
    }

    @Test
    public void testGetAllNamespaces_SyncSOAP() {
        testGetAllNamespaces(namespaceSoapService);
    }

    private static void testGetAllNamespaces(final NamespaceService namespaceService) {
        final List<NamespaceDTO> namespaces = namespaceService.getAllNamespaces();
        assertEquals("Wrong number of namespaces found", EXPECTED_NUMBER_OF_NAMESPACES, namespaces.size());

        final NamespaceDTO namespace = namespaces.get(0);
        assertEquals("Wrong first namespace name found", KIM_TEST_NAMESPACE_NAME, namespaces.get(0).getName());

        final HashMap<String, NamespaceDefaultAttributeDTO> namespaceDefaultAttributes = namespace.getNamespaceAttributes();
        assertEquals("Wrong number of default attributes", 1, namespaceDefaultAttributes.size());

        final NamespaceDefaultAttributeDTO namespaceDefaultAttribute = namespaceDefaultAttributes.get(KIM_TEST_NAMESPACE_DEFAULT_ATTRIBUTE_NAME);
        assertNotNull(namespaceDefaultAttribute);
        assertEquals(KIM_TEST_NAMESPACE_DEFAULT_ATTRIBUTE_NAME, namespaceDefaultAttribute.getAttributeName());

        final HashMap<String, PermissionDTO> namespacePermissions = namespace.getNamespacePermissions();
        assertEquals("Wrong number of namespace permissions" , 1, namespacePermissions.size());

        final PermissionDTO permission = namespacePermissions.get(KIM_TEST_PERMISSION_NAME);
        assertNotNull(permission);
        assertEquals("Wrong permission name", KIM_TEST_PERMISSION_NAME, permission.getName());
    }

    @Test
    public void testGetPermissionNames_SyncJava() {
        testGetPermissionNames(namespaceService);
    }

    @Test
    public void testGetPermissionNames_SyncSOAP() {
        testGetPermissionNames(namespaceSoapService);
    }

    private static void testGetPermissionNames(final NamespaceService namespaceService) {
        final List<String> names = namespaceService.getPermissionNames(KIM_TEST_NAMESPACE_NAME);
        assertEquals("Wrong number of permission names", 1, names.size());
        assertEquals("Wrong permission name", KIM_TEST_PERMISSION_NAME, names.get(0));
    }

    @Test
    public void testGetPermissions_SyncJava() {
        testGetPermissions(namespaceService);
    }

    @Test
    public void testGetPermissions_SyncSOAP() {
        testGetPermissions(namespaceSoapService);
    }

    private static void testGetPermissions(final NamespaceService namespaceService) {
        final List<PermissionDTO> permissions = namespaceService.getPermissions(KIM_TEST_NAMESPACE_NAME);
        assertEquals("Wrong number of permissions", 1, permissions.size());

        final PermissionDTO permission = permissions.get(0);
        assertEquals("Wrong permission name", KIM_TEST_PERMISSION_NAME, permission.getName());
    }
}
