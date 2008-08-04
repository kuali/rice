/*
 * Copyright 2008 The Kuali Foundation
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
import java.util.Map;

import javax.xml.namespace.QName;

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kim.test.KIMTestCase;

/**
 * This is a description of what this class does - lindholm don't forget to fill
 * this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class AuthorizationServiceTest extends KIMTestCase {
	private static final String EXPECTED_NAMESPACE_NAME = "KRA";
	private static final String URI = "KIM";

	private AuthorizationService authorizationService;
	private AuthorizationService authorizationSoapService;

	private static final Long EXPECTED_ENTITY_ID = new Long(140);
	private static final Long BAD_ENTITY_ID = new Long(-1);
	private static final Long EXPECTED_PERSON_ID = new Long(160);
	private static final Long BAD_PERSON_ID = new Long(-1);
	private static final String EXPECTED_PERMISSION_NAME = "canSave";
	private static final Map<String, String> EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTES = new HashMap<String, String>();
	private static final Map<String, String> EXPECTED_ENTITY_QUALIFIED_ROLE_ATTRIBUTES = new HashMap<String, String>();
	private static final String EXPECTED_ROLE_NAME = "Dean";
	private static final String EXPECTED_PRINCIPAL_NAME = "jschmoe";
	private static final String EXPECTED_GROUP_NAME = "Group1";

	private static final QName SOAP_SERVICE = new QName(URI,
			"kimAuthorizationSoapService");
	private static final QName JAVA_SERVICE = new QName(URI,
			"kimAuthorizationService");

	public void setUp() throws Exception {
		super.setUp();
		authorizationService = (AuthorizationService) GlobalResourceLoader
				.getService(JAVA_SERVICE);
		authorizationSoapService = (AuthorizationService) GlobalResourceLoader
				.getService(SOAP_SERVICE);
		EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTES.put("Department", "Finance");
		EXPECTED_ENTITY_QUALIFIED_ROLE_ATTRIBUTES.put("QualifiedRole", "Some role");
	}

	@Test
	public void entityHasQualifiedRole_Java() {
		entityHasQualifiedRole(authorizationService);
	}

	@Test
	public void entityHasQualifiedRole_Soap() {
		entityHasQualifiedRole(authorizationSoapService);
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.AuthorizationService#entityHasQualifiedRole(java.lang.Long,
	 *      java.lang.String, java.util.Map)
	 */
	private void entityHasQualifiedRole(
			final AuthorizationService authorizationService) {
		boolean hasRole = authorizationService.entityHasQualifiedRole(
				EXPECTED_ENTITY_ID, EXPECTED_ROLE_NAME, EXPECTED_ENTITY_QUALIFIED_ROLE_ATTRIBUTES);
		assertTrue("Does not have role", hasRole);

		hasRole = authorizationService.entityHasQualifiedRole(
				EXPECTED_ENTITY_ID, EXPECTED_ROLE_NAME + "x", EXPECTED_ENTITY_QUALIFIED_ROLE_ATTRIBUTES);
		assertFalse("Should not have role", hasRole);

		hasRole = authorizationService.entityHasQualifiedRole(
				EXPECTED_ENTITY_ID, EXPECTED_ROLE_NAME, EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTES);
		assertFalse("Should not have role", hasRole);

		hasRole = authorizationService.entityHasQualifiedRole(
				BAD_ENTITY_ID, EXPECTED_ROLE_NAME, EXPECTED_ENTITY_QUALIFIED_ROLE_ATTRIBUTES);
		assertFalse("Should not have role", hasRole);
		
		hasRole = authorizationService.entityHasQualifiedRole(
				EXPECTED_ENTITY_ID, EXPECTED_ROLE_NAME + "x", EXPECTED_ENTITY_QUALIFIED_ROLE_ATTRIBUTES);
		assertFalse("Should not have role", hasRole);
	}

	@Test
	public void entityHasRole_Java() {
		entityHasRole(authorizationService);
	}

	@Test
	public void entityHasRole_Soap() {
		entityHasRole(authorizationSoapService);
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.AuthorizationService#entityHasRole(java.lang.Long,
	 *      java.lang.String)
	 */
	private void entityHasRole(final AuthorizationService authorizationService) {
		boolean hasRole = authorizationService.entityHasRole(EXPECTED_ENTITY_ID,
				EXPECTED_ROLE_NAME);
		assertTrue("Entity doesn't have role", hasRole);

		hasRole = authorizationService.entityHasRole(BAD_ENTITY_ID,
				EXPECTED_ROLE_NAME);
		assertFalse("Entity shouldn't have role", hasRole);

		hasRole = authorizationService.entityHasRole(EXPECTED_ENTITY_ID,
				EXPECTED_ROLE_NAME+"x");
		assertFalse("Entity shouldn't have role", hasRole);
	}

	@Test
	public void groupHasQualifiedRole_Java() {
		groupHasQualifiedRole(authorizationService);
	}

	@Test
	public void groupHasQualifiedRole_Soap() {
		groupHasQualifiedRole(authorizationSoapService);
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.AuthorizationService#entityHasQualifiedRole(java.lang.Long,
	 *      java.lang.String, java.util.Map)
	 */
	private void groupHasQualifiedRole(
			final AuthorizationService authorizationService) {
		boolean hasRole = authorizationService.groupHasQualifiedRole(
				EXPECTED_GROUP_NAME, EXPECTED_ROLE_NAME, EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTES);
		assertTrue("Does not have role", hasRole);

		hasRole = authorizationService.groupHasQualifiedRole(
				EXPECTED_GROUP_NAME, EXPECTED_ROLE_NAME, EXPECTED_ENTITY_QUALIFIED_ROLE_ATTRIBUTES);
		assertFalse("Should not have role", hasRole);

		hasRole = authorizationService.groupHasQualifiedRole(
				EXPECTED_GROUP_NAME + "x", EXPECTED_ROLE_NAME, EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTES);
		assertFalse("Should not have role", hasRole);

		hasRole = authorizationService.groupHasQualifiedRole(
				EXPECTED_GROUP_NAME, EXPECTED_ROLE_NAME + "x", EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTES);
		assertFalse("Should not have role", hasRole);

	}

	@Test
	public void groupHasRole_Java() {
		groupHasRole(authorizationService);
	}

	@Test
	public void groupHasRole_Soap() {
		groupHasRole(authorizationSoapService);
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.AuthorizationService#groupHasRole(java.lang.String,
	 *      java.lang.String)
	 */
	private void groupHasRole(final AuthorizationService authorizationService) {
		boolean hasRole = authorizationService.groupHasRole(EXPECTED_GROUP_NAME,
				EXPECTED_ROLE_NAME);
		assertTrue("Group doesn't have role", hasRole);

		hasRole = authorizationService.groupHasRole(EXPECTED_GROUP_NAME+"x",
				EXPECTED_ROLE_NAME);
		assertFalse("Group shouldn't have role", hasRole);

		hasRole = authorizationService.groupHasRole(EXPECTED_GROUP_NAME,
				EXPECTED_ROLE_NAME+"x");
		assertFalse("Group shouldn't have role", hasRole);
	}

	@Test
	public void isEntityAuthorized_Java() {
		isEntityAuthorized(authorizationService);
	}

	@Test
	public void isEntityAuthorized_Soap() {
		isEntityAuthorized(authorizationSoapService);
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.AuthorizationService#isEntityAuthorized(java.lang.Long,
	 *      java.lang.String, java.lang.String)
	 */
	private void isEntityAuthorized(
			final AuthorizationService authorizationService) {
		boolean isAuthorized = authorizationService.isEntityAuthorized(
				EXPECTED_ENTITY_ID, EXPECTED_PERMISSION_NAME, EXPECTED_NAMESPACE_NAME);
		assertTrue("Entity is not authorized", isAuthorized);

		isAuthorized = authorizationService.isEntityAuthorized(
				BAD_ENTITY_ID, EXPECTED_PERMISSION_NAME, EXPECTED_NAMESPACE_NAME);
		assertFalse("Entity should not be authorized", isAuthorized);

		isAuthorized = authorizationService.isEntityAuthorized(
				EXPECTED_ENTITY_ID, EXPECTED_PERMISSION_NAME+"x", EXPECTED_NAMESPACE_NAME);
		assertFalse("Entity should not be authorized", isAuthorized);

		isAuthorized = authorizationService.isEntityAuthorized(
				EXPECTED_ENTITY_ID, EXPECTED_PERMISSION_NAME, EXPECTED_NAMESPACE_NAME+"x");
		assertFalse("Entity should not be authorized", isAuthorized);
	}

	@Test
	public void isEntityAuthorizedForQualifiedPermission_Java() {
		isEntityAuthorizedForQualifiedPermission(authorizationService);
	}

	@Test
	public void isEntityAuthorizedForQualifiedPermission_Soap() {
		isEntityAuthorizedForQualifiedPermission(authorizationSoapService);
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.AuthorizationService#isEntityAuthorizedForQualifiedPermission(java.lang.Long,
	 *      java.lang.String, java.util.Map, java.lang.String)
	 */
	private void isEntityAuthorizedForQualifiedPermission(
			final AuthorizationService authorizationService) {
		boolean isAuthorized = authorizationService
				.isEntityAuthorizedForQualifiedPermission(EXPECTED_ENTITY_ID,
						EXPECTED_PERMISSION_NAME, EXPECTED_ENTITY_QUALIFIED_ROLE_ATTRIBUTES, EXPECTED_NAMESPACE_NAME);
		assertTrue("Is not authorized", isAuthorized);

		isAuthorized = authorizationService
		.isEntityAuthorizedForQualifiedPermission(BAD_ENTITY_ID,
				EXPECTED_PERMISSION_NAME, EXPECTED_ENTITY_QUALIFIED_ROLE_ATTRIBUTES, EXPECTED_NAMESPACE_NAME);
		assertFalse("Should not be authorized", isAuthorized);


		isAuthorized = authorizationService
		.isEntityAuthorizedForQualifiedPermission(EXPECTED_ENTITY_ID,
				EXPECTED_PERMISSION_NAME, EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTES, EXPECTED_NAMESPACE_NAME);
		assertFalse("Should not be authorized", isAuthorized);

		isAuthorized = authorizationService
		.isEntityAuthorizedForQualifiedPermission(EXPECTED_ENTITY_ID,
				EXPECTED_PERMISSION_NAME + "x", EXPECTED_ENTITY_QUALIFIED_ROLE_ATTRIBUTES, EXPECTED_NAMESPACE_NAME);
		assertFalse("Should not be authorized", isAuthorized);

		isAuthorized = authorizationService
		.isEntityAuthorizedForQualifiedPermission(EXPECTED_ENTITY_ID,
				EXPECTED_PERMISSION_NAME, EXPECTED_ENTITY_QUALIFIED_ROLE_ATTRIBUTES, EXPECTED_NAMESPACE_NAME + "x");
		assertFalse("Should not be authorized", isAuthorized);

	}

	@Test
	public void isPersonAuthorized_Java() {
		isPersonAuthorized(authorizationService);
	}

	@Test
	public void isPersonAuthorized_Soap() {
		isPersonAuthorized(authorizationSoapService);
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.AuthorizationService#isPersonAuthorized(java.lang.Long,
	 *      java.lang.String, java.lang.String)
	 */
	private void isPersonAuthorized(
			final AuthorizationService authorizationService) {
		boolean isAuthorized = authorizationService.isPersonAuthorized(
				EXPECTED_PERSON_ID, EXPECTED_PERMISSION_NAME, EXPECTED_NAMESPACE_NAME);
		assertTrue("Person is not authorized", isAuthorized);

		isAuthorized = authorizationService.isPersonAuthorized(
				EXPECTED_PERSON_ID+1234, EXPECTED_PERMISSION_NAME, EXPECTED_NAMESPACE_NAME);
		assertFalse("Person should not be authorized", isAuthorized);

		isAuthorized = authorizationService.isPersonAuthorized(
				EXPECTED_PERSON_ID, EXPECTED_PERMISSION_NAME+"x", EXPECTED_NAMESPACE_NAME);
		assertFalse("Person should not be authorized", isAuthorized);

		isAuthorized = authorizationService.isPersonAuthorized(
				EXPECTED_PERSON_ID, EXPECTED_PERMISSION_NAME, EXPECTED_NAMESPACE_NAME+"x");
		assertFalse("Person should not be authorized", isAuthorized);
	}

	@Test
	public void isPersonAuthorizedForQualifiedPermission_Java() {
		isPersonAuthorizedForQualifiedPermission(authorizationService);
	}

	@Test
	public void isPersonAuthorizedForQualifiedPermission_Soap() {
		isPersonAuthorizedForQualifiedPermission(authorizationSoapService);
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.AuthorizationService#isPersonAuthorizedForQualifiedPermission(java.lang.Long,
	 *      java.lang.String, java.util.Map, java.lang.String)
	 */
	private void isPersonAuthorizedForQualifiedPermission(
			final AuthorizationService authorizationService) {
		boolean isAuthorized = authorizationService
				.isPersonAuthorizedForQualifiedPermission(EXPECTED_PERSON_ID,
						EXPECTED_PERMISSION_NAME, EXPECTED_ENTITY_QUALIFIED_ROLE_ATTRIBUTES, EXPECTED_NAMESPACE_NAME);
		assertTrue("Is not authorized", isAuthorized);

		isAuthorized = authorizationService
		.isPersonAuthorizedForQualifiedPermission(BAD_PERSON_ID,
				EXPECTED_PERMISSION_NAME, EXPECTED_ENTITY_QUALIFIED_ROLE_ATTRIBUTES, EXPECTED_NAMESPACE_NAME);
		assertFalse("Should not be authorized", isAuthorized);

		isAuthorized = authorizationService
		.isPersonAuthorizedForQualifiedPermission(EXPECTED_PERSON_ID,
				EXPECTED_PERMISSION_NAME, EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTES, EXPECTED_NAMESPACE_NAME);
		assertFalse("Should not be authorized", isAuthorized);

		isAuthorized = authorizationService
		.isPersonAuthorizedForQualifiedPermission(EXPECTED_PERSON_ID,
				EXPECTED_PERMISSION_NAME + "x", EXPECTED_ENTITY_QUALIFIED_ROLE_ATTRIBUTES, EXPECTED_NAMESPACE_NAME);
		assertFalse("Should not be authorized", isAuthorized);

		isAuthorized = authorizationService
		.isPersonAuthorizedForQualifiedPermission(EXPECTED_PERSON_ID,
				EXPECTED_PERMISSION_NAME, EXPECTED_ENTITY_QUALIFIED_ROLE_ATTRIBUTES, EXPECTED_NAMESPACE_NAME + "x");
		assertFalse("Should not be authorized", isAuthorized);
	}

	@Test
	public void isPrincipalAuthorized_Java() {
		isPrincipalAuthorized(authorizationService);
	}

	@Test
	public void isPrincipalAuthorized_Soap() {
		isPrincipalAuthorized(authorizationSoapService);
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.AuthorizationService#isPrincipalAuthorized(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	private void isPrincipalAuthorized(
			final AuthorizationService authorizationService) {
		boolean isAuthorized = authorizationService.isPrincipalAuthorized(
				EXPECTED_PRINCIPAL_NAME, EXPECTED_PERMISSION_NAME, EXPECTED_NAMESPACE_NAME);
		assertTrue("Principal doesn't have permission", isAuthorized);

		isAuthorized = authorizationService.isPrincipalAuthorized(
				EXPECTED_PRINCIPAL_NAME+"x", EXPECTED_PERMISSION_NAME, EXPECTED_NAMESPACE_NAME);
		assertFalse("Principal has permission", isAuthorized);

		isAuthorized = authorizationService
		.isPrincipalAuthorizedForQualifiedPermission(EXPECTED_PRINCIPAL_NAME,
				EXPECTED_PERMISSION_NAME, EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTES, EXPECTED_NAMESPACE_NAME);
		assertFalse("Should not have permission", isAuthorized);

		isAuthorized = authorizationService.isPrincipalAuthorized(
				EXPECTED_PRINCIPAL_NAME, EXPECTED_PERMISSION_NAME+"x", EXPECTED_NAMESPACE_NAME);
		assertFalse("Principal has permission", isAuthorized);

		isAuthorized = authorizationService.isPrincipalAuthorized(
				EXPECTED_PRINCIPAL_NAME, EXPECTED_PERMISSION_NAME, EXPECTED_NAMESPACE_NAME+"x");
		assertFalse("Principal has permission", isAuthorized);

	}

	@Test
	public void isPrincipalAuthorizedForQualifiedPermission_Java() {
		isPrincipalAuthorizedForQualifiedPermission(authorizationService);
	}

	@Test
	public void isPrincipalAuthorizedForQualifiedPermission_Soap() {
		isPrincipalAuthorizedForQualifiedPermission(authorizationSoapService);
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.AuthorizationService#isPrincipalAuthorizedForQualifiedPermission(java.lang.String,
	 *      java.lang.String, java.util.Map, java.lang.String)
	 */
	private void isPrincipalAuthorizedForQualifiedPermission(
			final AuthorizationService authorizationService) {
		boolean isAuthorized = authorizationService
				.isPrincipalAuthorizedForQualifiedPermission(EXPECTED_PRINCIPAL_NAME,
						EXPECTED_PERMISSION_NAME, EXPECTED_ENTITY_QUALIFIED_ROLE_ATTRIBUTES, EXPECTED_NAMESPACE_NAME);
		assertTrue("Does not have permission", isAuthorized);
		
		isAuthorized = authorizationService
		.isPrincipalAuthorizedForQualifiedPermission(EXPECTED_PRINCIPAL_NAME,
				EXPECTED_PERMISSION_NAME, EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTES, EXPECTED_NAMESPACE_NAME);
		assertFalse("Should not have permission", isAuthorized);

		isAuthorized = authorizationService
		.isPrincipalAuthorizedForQualifiedPermission(EXPECTED_PRINCIPAL_NAME + "x",
				EXPECTED_PERMISSION_NAME, EXPECTED_ENTITY_QUALIFIED_ROLE_ATTRIBUTES, EXPECTED_NAMESPACE_NAME);
		assertFalse("Should not have permission", isAuthorized);
		
		isAuthorized = authorizationService
		.isPrincipalAuthorizedForQualifiedPermission(EXPECTED_PRINCIPAL_NAME,
				EXPECTED_PERMISSION_NAME + "x", EXPECTED_ENTITY_QUALIFIED_ROLE_ATTRIBUTES, EXPECTED_NAMESPACE_NAME);
		assertFalse("Should not have permission", isAuthorized);
		
		isAuthorized = authorizationService
		.isPrincipalAuthorizedForQualifiedPermission(EXPECTED_PRINCIPAL_NAME,
				EXPECTED_PERMISSION_NAME, EXPECTED_ENTITY_QUALIFIED_ROLE_ATTRIBUTES, EXPECTED_NAMESPACE_NAME + "x");
		assertFalse("Should not have permission", isAuthorized);
	}

	@Test
	public void personHasQualifiedRole_Java() {
		personHasQualifiedRole(authorizationService);
	}

	@Test
	public void personHasQualifiedRole_Soap() {
		personHasQualifiedRole(authorizationSoapService);
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.AuthorizationService#personHasQualifiedRole(java.lang.Long,
	 *      java.lang.String, java.util.Map)
	 */
	private void personHasQualifiedRole(
			final AuthorizationService authorizationService) {
		boolean hasRole = authorizationService.personHasQualifiedRole(
				EXPECTED_PERSON_ID, EXPECTED_ROLE_NAME, EXPECTED_ENTITY_QUALIFIED_ROLE_ATTRIBUTES);
		assertTrue("Does not have role", hasRole);
		
		hasRole = authorizationService.personHasQualifiedRole(
				EXPECTED_PERSON_ID, EXPECTED_ROLE_NAME, EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTES);
		assertFalse("Should not have role", hasRole);
		
		hasRole = authorizationService.personHasQualifiedRole(
				BAD_PERSON_ID, EXPECTED_ROLE_NAME, EXPECTED_ENTITY_QUALIFIED_ROLE_ATTRIBUTES);
		assertFalse("Should not have role", hasRole);
		
		hasRole = authorizationService.personHasQualifiedRole(
				EXPECTED_PERSON_ID, EXPECTED_ROLE_NAME + "x", EXPECTED_ENTITY_QUALIFIED_ROLE_ATTRIBUTES);
		assertFalse("Should not have role", hasRole);
		
		
	}

	@Test
	public void personHasRole_Java() {
		personHasRole(authorizationService);
	}

	@Test
	public void personHasRole_Soap() {
		personHasRole(authorizationSoapService);
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.AuthorizationService#personHasRole(java.lang.Long,
	 *      java.lang.String)
	 */
	private void personHasRole(final AuthorizationService authorizationService) {
		boolean hasRole = authorizationService.personHasRole(EXPECTED_PERSON_ID,
				EXPECTED_ROLE_NAME);
		assertTrue("Does not have role", hasRole);

		hasRole = authorizationService.personHasRole(EXPECTED_PERSON_ID+1234,
				EXPECTED_ROLE_NAME);
		assertFalse("Should not have role", hasRole);

		hasRole = authorizationService.personHasRole(EXPECTED_PERSON_ID,
				EXPECTED_ROLE_NAME+"x");
		assertFalse("Should not have role", hasRole);
	}

	@Test
	public void principalHasQualifiedRole_Java() {
		principalHasQualifiedRole(authorizationService);
	}

	@Test
	public void principalHasQualifiedRole_Soap() {
		principalHasQualifiedRole(authorizationSoapService);
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.AuthorizationService#principalHasQualifiedRole(java.lang.String,
	 *      java.lang.String, java.util.Map)
	 */
	private void principalHasQualifiedRole(
			final AuthorizationService authorizationService) {
		boolean hasRole = authorizationService.principalHasQualifiedRole(
				EXPECTED_PRINCIPAL_NAME, EXPECTED_ROLE_NAME, EXPECTED_ENTITY_QUALIFIED_ROLE_ATTRIBUTES);
		assertTrue("Does not have role", hasRole);
		
		hasRole = authorizationService.principalHasQualifiedRole(
				EXPECTED_PRINCIPAL_NAME, EXPECTED_ROLE_NAME, EXPECTED_GROUP_QUALIFIED_ROLE_ATTRIBUTES);
		assertFalse("Should not have role", hasRole);
		
		hasRole = authorizationService.principalHasQualifiedRole(
				EXPECTED_PRINCIPAL_NAME + "x", EXPECTED_ROLE_NAME, EXPECTED_ENTITY_QUALIFIED_ROLE_ATTRIBUTES);
		assertFalse("Should not have role", hasRole);

		hasRole = authorizationService.principalHasQualifiedRole(
				EXPECTED_PRINCIPAL_NAME, EXPECTED_ROLE_NAME + "x", EXPECTED_ENTITY_QUALIFIED_ROLE_ATTRIBUTES);
		assertFalse("Should not have role", hasRole);	
	}

	@Test
	public void principalHasRole_Java() {
		principalHasRole(authorizationService);
	}

	@Test
	public void principalHasRole_Soap() {
		principalHasRole(authorizationSoapService);
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.AuthorizationService#principalHasRole(java.lang.String,
	 *      java.lang.String)
	 */
	private void principalHasRole(
			final AuthorizationService authorizationService) {
		boolean hasRole = authorizationService.principalHasRole(
				EXPECTED_PRINCIPAL_NAME, EXPECTED_ROLE_NAME);
		assertTrue("Principal doesn't have role", hasRole);

		hasRole = authorizationService.principalHasRole(
				EXPECTED_PRINCIPAL_NAME+"x", EXPECTED_ROLE_NAME);
		assertFalse("Principal shouldn't have role", hasRole);

		hasRole = authorizationService.principalHasRole(
				EXPECTED_PRINCIPAL_NAME, EXPECTED_ROLE_NAME+"x");
		assertFalse("Principal shouldn't have role", hasRole);


	}

}
