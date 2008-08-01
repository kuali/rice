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

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.kim.dto.PersonAttributeDTO;
import org.kuali.rice.kim.dto.PersonDTO;
import org.kuali.rice.kim.test.KIMTestCase;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

/**
 * Basic test to verify we can access the PersonService through the GRL.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class PersonServiceTest extends KIMTestCase {
	private static final String EXPECTED_NAMESPACE_NAME = "KIM";
	private static final String EXPECTED_ATTRIBUTE_NAME = "EmailAddress";
	private static final String EXPECTED_ATTRIBUTE_VALUE = "kuali-rice@googlegroups.com";
	private static final String EXPECTED_ATTRIBUTE_NAME2 = "Country";
	private static final String EXPECTED_ATTRIBUTE_VALUE2 = "USA";
	private static final Long EXPECTED_PERSON_ID = new Long(160);
	private static final String EXPECTED_GROUP_NAME = "Group1";
	private static final Map<String, String> EXPECTED_PERSON_ATTRIBUTE = new HashMap<String, String>();
	private static final Map<String, String> EXPECTED_PERSON_ATTRIBUTES = new HashMap<String, String>();
	private PersonService personService;
	private PersonService personSoapService;

	private static final String URI = EXPECTED_NAMESPACE_NAME;
	private static final QName SOAP_SERVICE = new QName(URI,
			"personSoapService");
	private static final QName JAVA_SERVICE = new QName(URI, "personService");

	public void setUp() throws Exception {
		super.setUp();
		personService = (PersonService) GlobalResourceLoader
				.getService(JAVA_SERVICE);
		personSoapService = (PersonService) GlobalResourceLoader
				.getService(SOAP_SERVICE);
		EXPECTED_PERSON_ATTRIBUTE.put(EXPECTED_ATTRIBUTE_NAME,
				EXPECTED_ATTRIBUTE_VALUE);
		EXPECTED_PERSON_ATTRIBUTES.put(EXPECTED_ATTRIBUTE_NAME,
				EXPECTED_ATTRIBUTE_VALUE);
		EXPECTED_PERSON_ATTRIBUTES.put(EXPECTED_ATTRIBUTE_NAME2,
				EXPECTED_ATTRIBUTE_VALUE2);
	}

	@Test
	public void getAllPersonIds_Java() {
		getAllPersonIds(personService);
	}

	@Test
	public void getAllPersonIds_Soap() {
		getAllPersonIds(personSoapService);
	}

	/**
	 * This method ...
	 *
	 */
	private static void getAllPersonIds(final PersonService personService) {
		List<Long> personIds = personService.getAllPersonIds();
		assertNotNull("Found no personsIds", personIds);
		assertEquals("Wrong number of persons found", 2, personIds.size());
		assertEquals("Wrong person found", EXPECTED_PERSON_ID, personIds.get(0));
	}

	@Test
	public void getAllPersons_Java() {
		getAllPersons(personService);
	}

	@Test
	public void getAllPersons_Soap() {
		getAllPersons(personSoapService);
	}

	/**
	 * This method ...
	 *
	 */
	private static void getAllPersons(final PersonService personService) {
		List<PersonDTO> persons = personService.getAllPersons();
		assertNotNull("Found no persons", persons);
		assertEquals("Wrong number of persons found", 2, persons.size());
		assertEquals("Wrong person found", EXPECTED_PERSON_ID, persons.get(0)
				.getId());
	}

	@Test
	public void getAttributeValue_Java() {
		getAttributeValue(personService);
	}

	@Test
	public void getAttributeValue_Soap() {
		getAttributeValue(personSoapService);
	}

	/**
	 * This method ...
	 *
	 */
	private static void getAttributeValue(final PersonService personService) {
		String value = personService.getAttributeValue(EXPECTED_PERSON_ID,
				EXPECTED_ATTRIBUTE_NAME, EXPECTED_NAMESPACE_NAME);
		assertNotNull("Found no attribute value", value);
		assertEquals("Wrong attribute value found", EXPECTED_ATTRIBUTE_VALUE,
				value);
	}

	@Test
	public void getPersonIdsWithAttributes_Java() {
		getPersonIdsWithAttributes(personService);
	}

	@Test
	public void getPersonIdsWithAttributes_Soap() {
		getPersonIdsWithAttributes(personSoapService);
	}

	/**
	 * This method ...
	 *
	 */
	private static void getPersonIdsWithAttributes(
			final PersonService personService) {
		List<Long> personsIds = personService.getPersonIdsWithAttributes(
				EXPECTED_PERSON_ATTRIBUTE, EXPECTED_NAMESPACE_NAME);
		assertNotNull("Found no PersonIds with attributes", personsIds);
		assertEquals("Found wrong number of persons", 2, personsIds.size());
		assertEquals("Wrong personsId (principalId", EXPECTED_PERSON_ID,
				personsIds.get(0));

		personsIds = personService.getPersonIdsWithAttributes(
				EXPECTED_PERSON_ATTRIBUTES, EXPECTED_NAMESPACE_NAME);
		assertNotNull("Found no PersonIds with attributes", personsIds);
		assertEquals("Found wrong number of persons with multiple attributes",
				1, personsIds.size());
		assertEquals("Wrong personsId (principalId", EXPECTED_PERSON_ID,
				personsIds.get(0));
	}

	@Test
	public void getPersonsWithAttributes_Java() {
		getPersonsWithAttributes(personService);
	}

	@Test
	public void getPersonsWithAttributes_Soap() {
		getPersonsWithAttributes(personSoapService);
	}

	/**
	 * This method ...
	 *
	 */
	private static void getPersonsWithAttributes(
			final PersonService personService) {
		List<PersonDTO> persons = personService.getPersonsWithAttributes(
				EXPECTED_PERSON_ATTRIBUTE, EXPECTED_NAMESPACE_NAME);
		assertNotNull("Found no persons with attributes", persons);
		assertEquals("Found wrong number of persons", 2, persons.size());
		assertEquals("Wrong personsId (principalId", EXPECTED_PERSON_ID,
				persons.get(0).getId());

		persons = personService.getPersonsWithAttributes(
				EXPECTED_PERSON_ATTRIBUTES, EXPECTED_NAMESPACE_NAME);
		assertNotNull("Found no persons with attributes", persons);
		assertEquals("Found wrong number of persons with multiple attributes",
				1, persons.size());
		assertEquals("Wrong personsId (principalId", EXPECTED_PERSON_ID,
				persons.get(0).getId());
	}

	@Test
	public void hasAttributes_Java() {
		hasAttributes(personService);
	}

	@Test
	public void hasAttributes_Soap() {
		hasAttributes(personSoapService);
	}

	/**
	 * This method ...
	 *
	 */
	private static void hasAttributes(final PersonService personService) {
		boolean hasAttributes = personService.hasAttributes(EXPECTED_PERSON_ID,
				EXPECTED_PERSON_ATTRIBUTE, EXPECTED_NAMESPACE_NAME);
		assertTrue("Does not have attributes", hasAttributes);

		hasAttributes = personService.hasAttributes(EXPECTED_PERSON_ID,
				EXPECTED_PERSON_ATTRIBUTE, EXPECTED_NAMESPACE_NAME + "x");
		assertFalse("Should not have attributes", hasAttributes);
	}

	@Test
	public void isMemberOfGroup_Java() {
		isMemberOfGroup(personService);
	}

	@Test
	public void isMemberOfGroup_Soap() {
		isMemberOfGroup(personSoapService);
	}

	/**
	 * This method ...
	 *
	 */
	private static void isMemberOfGroup(final PersonService personService) {
		final boolean isMember = personService.isMemberOfGroup(
				EXPECTED_PERSON_ID, EXPECTED_GROUP_NAME);
		assertTrue("Is not a member", isMember);
	}

	@Test
	public void getPersonAttributesForNamespace_Java() {
		getPersonAttributesForNamespace(personService);

	}

	@Test
	public void getPersonAttributesForNamespace_Soap() {
		getPersonAttributesForNamespace(personSoapService);

	}

	/**
	 * This method ...
	 *
	 */
	private static void getPersonAttributesForNamespace(
			final PersonService personService) {
		HashMap<String, PersonAttributeDTO> attributes = personService
				.getPersonAttributesForNamespace(EXPECTED_PERSON_ID,
						EXPECTED_NAMESPACE_NAME);
		assertNotNull("Found no person attributes for namespace", attributes);
		assertEquals("Wrong number of attributes found", 2, attributes.size());
		assertTrue("Wrong attribute found", attributes
				.containsKey(EXPECTED_ATTRIBUTE_NAME));
		assertEquals("Wrong attribute value found", EXPECTED_ATTRIBUTE_VALUE,
				attributes.get(EXPECTED_ATTRIBUTE_NAME).getValue());

		attributes = personService.getPersonAttributesForNamespace(
				EXPECTED_PERSON_ID, EXPECTED_NAMESPACE_NAME + "x");
		assertEquals("Found attributes for non-existent namespace", 0, attributes.size());

		attributes = personService.getPersonAttributesForNamespace(01234445L,
				EXPECTED_NAMESPACE_NAME);
		assertEquals("Found attributes for non-existent person", 0, attributes.size());
	}

	@Test
	public void getPersonAttributesByNamespace_Java() {
		getPersonAttributesByNamespace(personService);
	}

	@Test
	public void getPersonAttributesByNamespace_Soap() {
		getPersonAttributesByNamespace(personSoapService);
	}

	/**
	 * This method ...
	 *
	 */
	private static void getPersonAttributesByNamespace(
			final PersonService personService) {
		HashMap<String, List<PersonAttributeDTO>> attributes = personService
				.getPersonAttributesByNamespace(EXPECTED_PERSON_ID);
		assertNotNull("Found no person attributes by namespace", attributes);
		assertEquals("Wrong number of namespaces found", 1, attributes.size());
		assertNotNull("No namespace list found", attributes
				.containsKey(EXPECTED_NAMESPACE_NAME));
		final List<PersonAttributeDTO> pas = attributes
				.get(EXPECTED_NAMESPACE_NAME);
		assertTrue("Wrong number of person attributes for namespace found", pas
				.size() == 2);
		assertEquals("Wrong person attribute found", EXPECTED_ATTRIBUTE_NAME,
				pas.get(0).getAttributeName());
		assertEquals("Wrong person attribute value found",
				EXPECTED_ATTRIBUTE_VALUE, pas.get(0).getValue());
	}

}
