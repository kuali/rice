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

import java.util.List;

import javax.xml.namespace.QName;

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kim.dto.PrincipalDTO;
import org.kuali.rice.kim.test.KIMTestCase;

/**
 * Basic test to verify we can access the PrincipalService through the GRL.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class PrincipalServiceTest extends KIMTestCase {
	private static final String EXPECTED_GROUP_NAME = "ParentGroup1";
	private static final Long EXPECTED_PERSON_ID = new Long(161);
	private static final String EXPECTED_PRINCIPAL_NAME = "jdoe";
	private static final Long EXPECTED_ENTITY_ID = new Long(140);
	private static final Long EXPECTED_FIRST_PRINCIPAL_ID = new Long(160);
	private static final String EXPECTED_FIRST_PRINCIPAL_NAME = "jschmoe";

	private PrincipalService principalService;
	private PrincipalService principalSoapService;

	private static final String URI = "KIM";
	private static final QName SOAP_SERVICE = new QName(URI,
			"principalSoapService");
	private static final QName JAVA_SERVICE = new QName(URI, "principalService");

	public void setUp() throws Exception {
		super.setUp();
		principalService = (PrincipalService) GlobalResourceLoader
				.getService(JAVA_SERVICE);
		principalSoapService = (PrincipalService) GlobalResourceLoader
				.getService(SOAP_SERVICE);
	}

	@Test
	public void getAllPrincipalNames_Java() {
		getAllPrincipalNames(principalService);
	}

	@Test
	public void getAllPrincipalNames_Soap() {
		getAllPrincipalNames(principalSoapService);
	}

	/**
	 * This method implements the getAllPrincipalNames unit test
	 *
	 */
	private static void getAllPrincipalNames(
			final PrincipalService principalService) {
		List<String> names = principalService.getAllPrincipalNames();
		assertNotNull("Found principal names", names);
		assertEquals("Wrong number of prinicpal names", 3, names.size());
		assertEquals("Found wrong principal name",
				EXPECTED_FIRST_PRINCIPAL_NAME, names.get(0));
	}

	@Test
	public void getAllPrincipals_Java() {
		getAllPrincipals(principalService);
	}

	@Test
	public void getAllPrincipals_Soap() {
		getAllPrincipals(principalSoapService);
	}

	/**
	 * This method implements the getAllPrincipals unit test
	 *
	 */
	private static void getAllPrincipals(final PrincipalService principalService) {
		List<PrincipalDTO> principals = principalService.getAllPrincipals();
		assertNotNull("Found no principals", principals);
		assertEquals("Wrong number of prinicpal names", 3, principals.size());
		assertEquals("Found wrong principal name",
				EXPECTED_FIRST_PRINCIPAL_NAME, principals.get(0).getName());
		assertEquals("Found wrong principal id", EXPECTED_FIRST_PRINCIPAL_ID,
				principals.get(0).getId());
	}

	@Test
	public void getPrincipal_Java() {
		getPrincipal(principalService);
	}

	@Test
	public void getPrincipal_Soap() {
		getPrincipal(principalSoapService);
	}

	/**
	 * This method implements the getPrincipal unit test
	 *
	 */
	private static void getPrincipal(final PrincipalService principalService) {
		PrincipalDTO principal = principalService
				.getPrincipal(EXPECTED_PRINCIPAL_NAME);
		assertNotNull("Did not find principal", principal);
		assertEquals("Wromg principal name", EXPECTED_PRINCIPAL_NAME, principal
				.getName());
	}

	@Test
	public void getPrincipalNamesForEntity_Java() {
		getPrincipalNamesForEntity(principalService);
	}

	@Test
	public void getPrincipalNamesForEntity_Soap() {
		getPrincipalNamesForEntity(principalSoapService);
	}

	/**
	 * This method implements the getPrincipalsForEntity unit test
	 *
	 */
	private static void getPrincipalNamesForEntity(
			final PrincipalService principalService) {
		List<String> names = principalService
				.getPrincipalNamesForEntity(EXPECTED_ENTITY_ID);
		assertNotNull("Found no principal names for entity", names);
		assertEquals("Found wrong number of principal names", 1, names.size());
		assertEquals("Wrong first principal name for entity",
				EXPECTED_FIRST_PRINCIPAL_NAME, names.get(0));
	}

	@Test
	public void getPrincipalsForEntity_Java() {
		getPrincipalsForEntity(principalService);
	}

	@Test
	public void getPrincipalsForEntity_Soap() {
		getPrincipalsForEntity(principalSoapService);
	}

	/**
	 * This method implements the getPrincipalsForEntity unit test
	 *
	 */
	private static void getPrincipalsForEntity(
			final PrincipalService principalService) {
		List<PrincipalDTO> principals = principalService
				.getPrincipalsForEntity(EXPECTED_ENTITY_ID);
		assertNotNull("Found no entity", principals);
		assertEquals("Found wrong number of principal names",1,
				principals.size());
		assertEquals("Wrong first principal name for entity",
				EXPECTED_FIRST_PRINCIPAL_NAME, principals.get(0).getName());
	}

	@Test
	public void getPrincipalNamesForPerson_Java() {
		getPrincipalNamesForPerson(principalService);
	}

	@Test
	public void getPrincipalNamesForPerson_Soap() {
		getPrincipalNamesForPerson(principalSoapService);
	}

	/**
	 * This method implements the getPrincipalsForPerson unit test
	 *
	 */
	private static void getPrincipalNamesForPerson(
			final PrincipalService principalService) {
		List<String> principals = principalService
				.getPrincipalNamesForPerson(EXPECTED_PERSON_ID);
		assertNotNull("Found no principals for person", principals);
		assertEquals("Wrong number of names found", 1, principals.size());
		assertEquals("Wrong principal name", EXPECTED_PRINCIPAL_NAME,
				principals.get(0));
	}

	@Test
	public void getPrincipalsForPerson_Java() {
		getPrincipalsForPerson(principalService);
	}

	@Test
	public void getPrincipalsForPerson_Soap() {
		getPrincipalsForPerson(principalSoapService);
	}

	/**
	 * This method implements the getPrincipalsForPerson unit test
	 *
	 */
	private static void getPrincipalsForPerson(
			final PrincipalService principalService) {
		List<PrincipalDTO> principals = principalService
				.getPrincipalsForPerson(EXPECTED_PERSON_ID);
		assertNotNull("Found no principals", principals);
		assertEquals("Wrong number of names found", 1, principals.size());
		assertEquals("Wrong principal name", EXPECTED_PRINCIPAL_NAME,
				principals.get(0).getName());
	}

	@Test
	public void isMemberOfGroup_Java() {
		isMemberOfGroup(principalService);
	}

	@Test
	public void isMemberOfGroup_Soap() {
		isMemberOfGroup(principalSoapService);
	}

	/**
	 * This method implements the isMemberOfGroup unit test
	 *
	 */
	private static void isMemberOfGroup(final PrincipalService principalService) {
		final boolean isMember = principalService.isMemberOfGroup(
				EXPECTED_PRINCIPAL_NAME, EXPECTED_GROUP_NAME);
		assertTrue("Is not member of group", isMember);
	}
}
