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
import org.kuali.rice.kim.dto.EntityAttributeDTO;
import org.kuali.rice.kim.dto.EntityDTO;
import org.kuali.rice.kim.test.KIMTestCase;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

/**
 * Basic test to verify we can access the EntityService through the GRL.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class EntityServiceTest extends KIMTestCase {
    private static final String EXPECTED_PHONE_ATTRIBUTE_VALUE = "555-5555";
    private static final String EXPECTED_GROUP_EMAIL_ATTRIBUTE_VALUE = "kuali-rice@googlegroups.com";
    private EntityService entityService;
    private EntityService entitySoapService;

    private static final Long EXPECTED_ENTITY_ID = new Long(141);
    private static final Long EXPECTED_FIRST_ENTITY_ID = new Long(140);
    private static final String EXPECTED_PHONE_ATTRIBUTE_NAME = "GroupPhoneNumber";
    private static final String EXPECTED_GROUP_EMAIL_ATTRIBUTE_NAME = "GroupPhoneNumber";
    private static final String EXPECTED_NAMESPACE_NAME = "KRA";
    private static final Map<String, String> EXPECTED_ENTITY_ATTRIBUTE = new HashMap<String, String>();
    private static final Map<String, String> EXPECTED_ENTITY_ATTRIBUTES = new HashMap<String, String>();
    private static final Map<String, String> EXPECTED_BAD_ENTITY_ATTRIBUTES = new HashMap<String, String>();
    private static final String EXPECTED_GROUP_NAME = "Group1";

    private static final String URI = "KIM";
    private static final QName SOAP_SERVICE = new QName(URI, "entitySoapService");
    private static final QName JAVA_SERVICE = new QName(URI, "entityService");

    public void setUp() throws Exception {
        super.setUp();
        entityService = (EntityService) GlobalResourceLoader.getService(JAVA_SERVICE);
        entitySoapService = (EntityService) GlobalResourceLoader.getService(SOAP_SERVICE);
        EXPECTED_ENTITY_ATTRIBUTE.put(EXPECTED_PHONE_ATTRIBUTE_NAME, EXPECTED_PHONE_ATTRIBUTE_VALUE);
        EXPECTED_ENTITY_ATTRIBUTES.put(EXPECTED_PHONE_ATTRIBUTE_NAME, EXPECTED_PHONE_ATTRIBUTE_VALUE);
        EXPECTED_ENTITY_ATTRIBUTES.put(EXPECTED_GROUP_EMAIL_ATTRIBUTE_NAME, EXPECTED_PHONE_ATTRIBUTE_VALUE);
    }

    /**
     * This method ...
     */
    @Test
    public void getAllEntityIds_Java() {
        getAllEntityIds(entityService);
    }
    @Test
    public void getAllEntityIds_Soap() {
        getAllEntityIds(entitySoapService);
    }

	/**
	 * This method ...
	 *
	 */
	private void getAllEntityIds(final EntityService entityService) {
		List<Long> ids = entityService.getAllEntityIds();
        assertNotNull("Found no entityIds", ids);
        assertEquals("Wrong number of entity ids returned", 3, ids.size());
        assertEquals("Wrong entityId", EXPECTED_FIRST_ENTITY_ID, ids.get(0));
	}

    /**
     * This method ...
     */
    @Test
    public void getAllEntitys_Java() {
    	getAllEntitys(entityService);
    }
    @Test
    public void getAllEntitys_Soap() {
    	getAllEntitys(entitySoapService);
    }

	/**
	 * This method ...
	 *
	 */
	private void getAllEntitys(final EntityService entityService) {
		List<EntityDTO> entitys = entityService.getAllEntitys();
        assertNotNull("Found no entitys", entitys);
        assertEquals("Wrong number of entitys returned", 3, entitys.size());
        assertEquals("Wrong entity", EXPECTED_FIRST_ENTITY_ID, entitys.get(0).getId());
	}

    /**
     * This method ...
     */
    @Test
    public void getAttributeValue_Java() {
        getAttributeValue(entityService);
    }
    @Test
    public void getAttributeValue_Soap() {
        getAttributeValue(entitySoapService);
    }

	/**
	 * This method ...
	 *
	 */
	private void getAttributeValue(final EntityService entityService) {
		String value = entityService.getAttributeValue(EXPECTED_ENTITY_ID, EXPECTED_PHONE_ATTRIBUTE_NAME, EXPECTED_NAMESPACE_NAME);
        assertNotNull("Found no attribute value", value);
        assertEquals("Wrong attribute value", EXPECTED_PHONE_ATTRIBUTE_VALUE, value);
	}

    /**
     * This method ...
     */
    @Test
    public void getEntityAttributesByNamespace_Java() {
        getEntityAttributesByNamespace(entityService);
    }
    @Test
    public void getEntityAttributesByNamespace_Soap() {
        getEntityAttributesByNamespace(entitySoapService);
    }

	/**
	 * This method ...
	 *
	 */
	private void getEntityAttributesByNamespace(final EntityService entityService) {
		HashMap<String, List<EntityAttributeDTO>> namespaces = entityService.getEntityAttributesByNamespace(EXPECTED_ENTITY_ID);
        assertNotNull("Found no entity attributes by namespace", namespaces);
        assertEquals("Found more than one namespace", 2, namespaces.size());
        assertNotNull("No attributes found for namespace", namespaces.get(EXPECTED_NAMESPACE_NAME));
        List<EntityAttributeDTO> dtos = namespaces.get(EXPECTED_NAMESPACE_NAME);
        assertNotNull("Found no attributes for namespace", dtos);
        assertEquals("Wrong number of attributes for namespace found", 1, dtos.size());
        assertEquals(dtos.get(0).getAttributeName(), EXPECTED_PHONE_ATTRIBUTE_NAME);
	}

    /**
     * This method ...
     */
    @Test
    public void getEntityAttributesForNamespace_Java() {
        getEntityAttributesForNamespace(entityService);
    }
    @Test
    public void getEntityAttributesForNamespace_Soap() {
        getEntityAttributesForNamespace(entitySoapService);
    }

	/**
	 * This method ...
	 *
	 */
	private void getEntityAttributesForNamespace(final EntityService entityService) {
		HashMap<String, EntityAttributeDTO> attributes = entityService.getEntityAttributesForNamespace(EXPECTED_ENTITY_ID, EXPECTED_NAMESPACE_NAME);
        assertNotNull("Found no attributes in namespace", attributes);
        assertTrue("Wrong number of attributes found", attributes.size() == 1);
        assertNotNull("Attribute not found", attributes.get(EXPECTED_PHONE_ATTRIBUTE_NAME));
        assertTrue("Wrong attribute ID", attributes.get(EXPECTED_PHONE_ATTRIBUTE_NAME).getId() == 151);
        assertEquals("Wrong attribute entity ID", EXPECTED_ENTITY_ID, attributes.get(EXPECTED_PHONE_ATTRIBUTE_NAME).getEntityId());
	}

    /**
     * This method ...
     */
	@Test
	public void getEntityIdsWithAttributes_Java() {
		getEntityIdsWithAttributes(entityService);
	}
	@Test
	public void getEntityIdsWithAttributes_Soap() {
		getEntityIdsWithAttributes(entitySoapService);
	}

	/**
	 * This method ...
	 *
	 */
	private void getEntityIdsWithAttributes(final EntityService entityService) {
		List<Long> attributes = entityService.getEntityIdsWithAttributes(EXPECTED_ENTITY_ATTRIBUTE, EXPECTED_NAMESPACE_NAME);
        assertNotNull("Found no entityIds with attributes", attributes);
        assertEquals("Wrong number of attributes for namespace found",1,  attributes.size());
        assertEquals("Wrong entity id for attribute.found", EXPECTED_ENTITY_ID , attributes.get(0));
	}

    /**
     * This method ...
     */
    @Test
    public void getEntitysWithAttributes_Java() {
        getEntitysWithAttribute(entityService);
    }
    @Test
    public void getEntitysWithAttributes_Soap() {
        getEntitysWithAttribute(entitySoapService);
    }

	/**
	 * This method ...
	 *
	 */
	private void getEntitysWithAttribute(final EntityService entityService) {
		List<EntityDTO> attributes = entityService.getEntitysWithAttributes(EXPECTED_ENTITY_ATTRIBUTE, EXPECTED_NAMESPACE_NAME);
        assertNotNull("Found no entitys with attributes", attributes);
        assertEquals("Wrong number of entitys with attribute found", 1, attributes.size());
        assertEquals("Wrong entity id for attribute.found", EXPECTED_ENTITY_ID, attributes.get(0).getId());

        attributes = entityService.getEntitysWithAttributes(EXPECTED_ENTITY_ATTRIBUTES, EXPECTED_NAMESPACE_NAME);
        assertNotNull("Found no entitys with attributes", attributes);
        assertEquals("Wrong number of entitys with attributes found", 1, attributes.size());
        assertEquals("Wrong entity id for attribute.found", EXPECTED_ENTITY_ID, attributes.get(0).getId());
	}

    /**
     * This method ...
     */
    @Test
    public void hasAttributes_Java() {
    	hasAttributes(entityService);
    }
    @Test
    public void hasAttributes_Soap() {
    	hasAttributes(entitySoapService);
    }

	/**
	 * This method ...
	 *
	 */
	private void hasAttributes(final EntityService entityService) {
		boolean hasAttribute = entityService.hasAttributes(EXPECTED_ENTITY_ID, EXPECTED_ENTITY_ATTRIBUTE, EXPECTED_NAMESPACE_NAME);
        assertTrue("Did not have attribute", hasAttribute);

        final boolean hasAttributes = entityService.hasAttributes(EXPECTED_ENTITY_ID, EXPECTED_ENTITY_ATTRIBUTES, EXPECTED_NAMESPACE_NAME);
        assertTrue("Did not have attributes", hasAttributes);

        final boolean hasNotAttribute = entityService.hasAttributes(EXPECTED_ENTITY_ID, EXPECTED_ENTITY_ATTRIBUTE, "Global");
        assertTrue("Should not have attribute", hasNotAttribute);
	}

    /**
     * This method ...
     */
    @Test
    public void isMemberOfGroup_Java() {
        isMemberOfGroup(entityService);
    }
    @Test
    public void isMemberOfGroup_Soap() {
        isMemberOfGroup(entitySoapService);
    }

	/**
	 * This method ...
	 *
	 */
	private void isMemberOfGroup(final EntityService entityService) {
		boolean isMember = entityService.isMemberOfGroup(EXPECTED_ENTITY_ID, EXPECTED_GROUP_NAME);
        assertTrue("Is member of group", isMember);
	}
}
