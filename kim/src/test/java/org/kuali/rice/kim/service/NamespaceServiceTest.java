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
import org.kuali.rice.kim.dto.NamespaceDTO;
import org.kuali.rice.kim.dto.NamespaceDefaultAttributeDTO;
import org.kuali.rice.kim.dto.PermissionDTO;
import org.kuali.rice.kim.test.KIMTestCase;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.kuali.rice.test.data.PerTestUnitTestData;
import org.kuali.rice.test.data.UnitTestData;
import org.kuali.rice.test.data.UnitTestSql;
import org.kuali.rice.test.data.PerSuiteUnitTestData;

/**
 * Basic test to verify we can access the NamespaceService through the GRL.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */

public class NamespaceServiceTest extends KIMTestCase {
    private static final String KIM_TEST_NAMESPACE_NAME = "KIM Test Namespace";
    private static final String KIM_TEST_NAMESPACE_DEFAULT_ATTRIBUTE_NAME = "KIM Test Namespace Default Attribute";
    private static final String KIM_TEST_PERMISSION_NAME = "KIM Test Permission";
    private static final String URI = "KIM";
    private static final QName SOAP_SERVICE = new QName(URI, "namespaceSoapService");
    private static final QName JAVA_SERVICE = new QName(URI, "namespaceService");

    @Test
    public void testGetAllNamespaceNames_SyncJava() throws Exception {
        testGetAllNamespaceNames(JAVA_SERVICE);
    }

    @Test
    public void testGetAllNamespaceNames_SyncSOAP() throws Exception {
        testGetAllNamespaceNames(SOAP_SERVICE);
    }

    private static void testGetAllNamespaceNames(QName serviceName) {
        final NamespaceService namespaceService = (NamespaceService) GlobalResourceLoader.getService(serviceName);

        final List<String> names = namespaceService.getAllNamespaceNames();
        assertTrue(names.size() == 1);
        assertEquals(names.get(0), KIM_TEST_NAMESPACE_NAME);
    }

    @Test
    public void testGetAllNamespaces_SyncJava() {
        testGetAllNamespaces(JAVA_SERVICE);
    }

    @Test
    public void testGetAllNamespaces_SyncSOAP() {
        testGetAllNamespaces(SOAP_SERVICE);
    }

    private static void testGetAllNamespaces(QName serviceName) {
        final NamespaceService namespaceService = (NamespaceService) GlobalResourceLoader.getService(serviceName);
        final List<NamespaceDTO> namespaces = namespaceService.getAllNamespaces();
        assertTrue(namespaces.size() == 1);

        final NamespaceDTO namespace = namespaces.get(0);
        assertEquals(namespace.getName(), KIM_TEST_NAMESPACE_NAME);

        final HashMap<String, NamespaceDefaultAttributeDTO> namespaceDefaultAttributes = namespace.getNamespaceAttributes();
        assertTrue(namespaceDefaultAttributes.size() == 1);

        final NamespaceDefaultAttributeDTO namespaceDefaultAttribute = namespaceDefaultAttributes.get(KIM_TEST_NAMESPACE_DEFAULT_ATTRIBUTE_NAME);
        assertNotNull(namespaceDefaultAttribute);
        assertEquals(namespaceDefaultAttribute.getAttributeName(), KIM_TEST_NAMESPACE_DEFAULT_ATTRIBUTE_NAME);

        final HashMap<String, PermissionDTO> namespacePermissions = namespace.getNamespacePermissions();
        assertTrue(namespacePermissions.size() == 1);

        final PermissionDTO permission = namespacePermissions.get(KIM_TEST_PERMISSION_NAME);
        assertNotNull(permission);
        assertEquals(permission.getName(), KIM_TEST_PERMISSION_NAME);
//        assertEquals(permission.getNamespaceDto().getName(), KIM_TEST_NAMESPACE_NAME);
    }

    @Test
    public void testGetPermissionNames_SyncJava() {
        testGetPermissionNames(JAVA_SERVICE);
    }

    @Test
    public void testGetPermissionNames_SyncSOAP() {
        testGetPermissionNames(SOAP_SERVICE);
    }

    private static void testGetPermissionNames(QName serviceName) {
        final NamespaceService namespaceService = (NamespaceService) GlobalResourceLoader.getService(serviceName);
        final List<String> names = namespaceService.getPermissionNames(KIM_TEST_NAMESPACE_NAME);
        assertTrue(names.size() == 1);
        assertEquals(names.get(0), KIM_TEST_PERMISSION_NAME);
    }

    @Test
    public void testGetPermissions_SyncJava() {
        testGetPermissions(JAVA_SERVICE);
    }

    @Test
    public void testGetPermissions_SyncSOAP() {
        testGetPermissions(SOAP_SERVICE);
    }

    private static void testGetPermissions(QName serviceName) {
        final NamespaceService namespaceService = (NamespaceService) GlobalResourceLoader.getService(serviceName);
        final List<PermissionDTO> permissions = namespaceService.getPermissions(KIM_TEST_NAMESPACE_NAME);
        assertTrue(permissions.size() == 1);

        final PermissionDTO permission = permissions.get(0);
        assertEquals(permission.getName(), KIM_TEST_PERMISSION_NAME);
//        assertEquals(permission.getNamespaceDto().getName(), KIM_TEST_NAMESPACE_NAME);
    }
}
