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
import org.kuali.rice.kim.dto.GroupTypeDTO;
import org.kuali.rice.kim.dto.GroupTypeDefaultAttributeDTO;
import org.kuali.rice.kim.test.KIMTestCase;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

/**
 * Basic test to verify we can access the GroupTypeService through the GRL.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class GroupTypeServiceTest extends KIMTestCase {
    private static final String KIM_TEST_GROUP_TYPE_NAME = "GroupType";
    private static final Long KIM_TEST_GROUP_TYPE_ID = new Long(310);
    private static final String KIM_TEST_GROUP_TYPE_DEFAULT_ATTRIBUTE_NAME_1 = "Test Attribute 1";
    private static final String KIM_TEST_GROUP_TYPE_DEFAULT_ATTRIBUTE_NAME_2 = "Test Attribute 2";
    private static final String URI = "KIM";
    private static final QName SOAP_SERVICE = new QName(URI, "groupTypeSoapService");
    private static final QName JAVA_SERVICE = new QName(URI, "groupTypeService");

    @Test
    public void testGetAllGroupTypeNames_SyncJava() throws Exception {
        testGetAllGroupTypeNames(JAVA_SERVICE);
    }

    @Test
    public void testGetAllGroupTypeNames_SyncSOAP() throws Exception {
        testGetAllGroupTypeNames(SOAP_SERVICE);
    }

    private static void testGetAllGroupTypeNames(QName serviceName) {
        final GroupTypeService groupTypeService = (GroupTypeService) GlobalResourceLoader.getService(serviceName);

        final List<String> names = groupTypeService.getAllGroupTypeNames();
        assertTrue(names.size() == 2);
    }

    @Test
    public void testGetAllGroupTypes_SyncJava() {
        testGetAllGroupTypes(JAVA_SERVICE);
    }

    @Test
    public void testGetAllGroupTypes_SyncSOAP() {
        testGetAllGroupTypes(SOAP_SERVICE);
    }

    private static void testGetAllGroupTypes(QName serviceName) {
        final GroupTypeService groupTypeService = (GroupTypeService) GlobalResourceLoader.getService(serviceName);
        final List<GroupTypeDTO> groupTypes = groupTypeService.getAllGroupTypes();
        assertTrue(groupTypes.size() == 2);
    }

    @Test
    public void testGetGroupType_SyncJava() {
    	testGetGroupType(JAVA_SERVICE);
    }

    @Test
    public void testGetGroupType_SyncSOAP() {
    	testGetGroupType(SOAP_SERVICE);
    }

    private static void testGetGroupType(QName serviceName) {
    	final GroupTypeService groupTypeService = (GroupTypeService) GlobalResourceLoader.getService(serviceName);
        final GroupTypeDTO groupType = groupTypeService.getGroupType(KIM_TEST_GROUP_TYPE_ID);
        assertNotNull(groupType);
        assertEquals(groupType.getName(), KIM_TEST_GROUP_TYPE_NAME);

        final HashMap<String, GroupTypeDefaultAttributeDTO> groupTypeDefaultAttributes = groupType.getGroupTypeDefaultAttributes();
        assertTrue(groupTypeDefaultAttributes.size() == 2);
    }

    @Test
    public void testGetPermissions_SyncJava() {
    	testGetGroupTypeName(JAVA_SERVICE);
    }

    @Test
    public void testGetPermissions_SyncSOAP() {
    	testGetGroupTypeName(SOAP_SERVICE);
    }

    private static void testGetGroupTypeName(QName serviceName) {
    	final GroupTypeService groupTypeService = (GroupTypeService) GlobalResourceLoader.getService(serviceName);
        final String groupTypeName = groupTypeService.getGroupTypeName(KIM_TEST_GROUP_TYPE_ID);
        assertNotNull(groupTypeName);
        assertEquals(groupTypeName, KIM_TEST_GROUP_TYPE_NAME);
    }
}
