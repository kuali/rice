/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.test.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.CodedAttribute;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliation;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliationType;
import org.kuali.rice.kim.api.identity.citizenship.EntityCitizenship;
import org.kuali.rice.kim.api.identity.employment.EntityEmployment;
import org.kuali.rice.kim.api.identity.entity.Entity;
import org.kuali.rice.kim.api.identity.external.EntityExternalIdentifier;
import org.kuali.rice.kim.api.identity.name.EntityName;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.identity.residency.EntityResidency;
import org.kuali.rice.kim.api.identity.type.EntityTypeContactInfoContract;
import org.kuali.rice.kim.api.identity.visa.EntityVisa;
import org.kuali.rice.kim.impl.identity.employment.EntityEmploymentBo;
import org.kuali.rice.kim.impl.identity.entity.EntityBo;
import org.kuali.rice.kim.service.KIMServiceLocatorInternal;
import org.kuali.rice.kim.test.KIMTestCase;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.test.BaselineTestCase;

import static org.junit.Assert.*;

/**
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.NONE)
public class IdentityServiceImplTest extends KIMTestCase {

	private IdentityService identityService;

	@Override
    public void setUp() throws Exception {
		super.setUp();
		identityService = (IdentityService) KIMServiceLocatorInternal.getBean("kimIdentityDelegateService");
	}

	@Test
	public void testGetPrincipal() {
		Principal principal = identityService.getPrincipal("KULUSER");
		assertNotNull("principal must not be null", principal);
		assertEquals("Principal name did not match expected result","kuluser", principal.getPrincipalName());
	}

    @Test
    public void testGetPrincipalsByEntityId() {
        List<Principal> principals = identityService.getPrincipalsByEntityId("1136");
        assertNotNull("principal must not be null", principals);
        for (Principal principal: principals) {
            assertEquals("Principal name did not match expected result","kuluser", principal.getPrincipalName());
        }
    }

    @Test
    public void testGetPrincipalsByEntityIdInactive() {
        List<Principal> principals = identityService.getPrincipalsByEntityId("1139");
        assertNotNull("principal must not be null", principals);
        for (Principal principal: principals) {
            assertEquals("Principal name did not match expected result","inactiveusernm", principal.getPrincipalName());
        }
    }

    @Test
    public void testGetPrincipalsByEmployeeId() {
        List<Principal> principals = identityService.getPrincipalsByEmployeeId("0000001138");
        assertNotNull("principal must not be null", principals);
        for (Principal principal: principals) {
            assertEquals("Principal name did not match expected result","activeusernm", principal.getPrincipalName());
            assertEquals("Entity Id did not match expected result","1138", principal.getEntityId());
        }
    }

    @Test
    public void testGetPrincipalsByEmployeeIdInactive() {
        List<Principal> principals = identityService.getPrincipalsByEmployeeId("0000001140");
        assertNotNull("principal must not be null", principals);
        for (Principal principal: principals) {
            assertEquals("Principal name did not match expected result","inactiveempid", principal.getPrincipalName());
            assertEquals("Entity Id did not match expected result","1140", principal.getEntityId());
        }
    }

	@Test
	public void testGetPrincipalByPrincipalName() {
		Principal principal = identityService.getPrincipalByPrincipalName("kuluser");
		assertNotNull("principal must not be null", principal);
		assertEquals("Principal ID did not match expected result","KULUSER", principal.getPrincipalId());
	}

	@Test
	public void testGetContainedAttributes() {
		Principal principal = identityService.getPrincipal("p1");

		Entity entity = identityService.getEntity(principal.getEntityId());
		assertNotNull( "Entity Must not be null", entity );
		EntityTypeContactInfoContract eet = entity.getEntityTypeContactInfoByTypeCode( "PERSON" );
		assertNotNull( "PERSON EntityEntityType Must not be null", eet );
		assertEquals( "there should be 1 email address", 1, eet.getEmailAddresses().size() );
		assertEquals( "email address does not match", "p1@kuali.org", eet.getDefaultEmailAddress().getEmailAddressUnmasked() );
	}

    @Test
    public void testCreateEntity() {

        // This test will verify that all entityIds and other foreign keys are set when createEntity is called.
        // EntityEthnicity, EntityPrivacyPreferences, EntityBioDemographics, and EntityTypeContactInfo cannot be
        // created without a known entity ID so they were not included in this test.

        Entity.Builder entity = Entity.Builder.create();
        entity.setActive(true);

        populateEntityForCreate(entity);

        Entity createdEntity = identityService.createEntity(entity.build());
        Entity entityFromDb = identityService.getEntity(createdEntity.getId());

        assertNotNull("createdEntity must not be null", entityFromDb);

        List<Principal> createdPrincipals = entityFromDb.getPrincipals();
        assertNotNull("createdPrincipals must not be null", createdPrincipals);
        assertEquals("There must be one principal", 1, createdPrincipals.size());
        for (Principal principal : createdPrincipals) {
            assertNotNull("principal entityId must not be null", principal.getEntityId());
            assertNotNull("principal principalId must not be null", principal.getPrincipalId());
        }

        List<EntityName> createdNames = entityFromDb.getNames();
        assertNotNull("createdNames must not be null", createdNames);
        assertEquals("There must be one name", 1, createdNames.size());
        for (EntityName name : createdNames) {
            assertNotNull("name entityId must not be null", name.getEntityId());
        }

        List<EntityExternalIdentifier> entityExternalIdentifiers = entityFromDb.getExternalIdentifiers();
        assertNotNull("entityExternalIdentifiers must not be null", entityExternalIdentifiers);
        assertEquals("There must be one entityExternalIdentifier", 1, entityExternalIdentifiers.size());
        for (EntityExternalIdentifier entityExternalIdentifier : entityExternalIdentifiers) {
            assertNotNull("entityExternalIdentifier entityId must not be null", entityExternalIdentifier.getEntityId());
        }

        List<EntityAffiliation> createdEntityAffiliations = entityFromDb.getAffiliations();
        assertNotNull("createdEntityAffiliations must not be null", createdEntityAffiliations);
        assertEquals("There must be two createdEntityAffiliations", 2, createdEntityAffiliations.size());
        for (EntityAffiliation entityAffiliation : createdEntityAffiliations) {
            assertNotNull("entityAffiliation entityId must not be null", entityAffiliation.getEntityId());
        }

        List<EntityEmployment> createdEntityEmployments = entityFromDb.getEmploymentInformation();
        assertNotNull("createdEntityEmployments must not be null", createdEntityEmployments);
        assertEquals("There must be one createdEntityEmployment", 1, createdEntityEmployments.size());
        for (EntityEmployment entityEmployment : createdEntityEmployments) {
            assertNotNull("entityEmployment entityId must not be null", entityEmployment.getEntityId());
            EntityEmploymentBo entityEmploymentBo = EntityEmploymentBo.from(entityEmployment);
            assertNotNull("entityEmploymentBo entityAffiliationId must not be null",
                    entityEmploymentBo.getEntityAffiliationId());
            EntityAffiliation entityAffiliation = entityEmployment.getEntityAffiliation();
            assertNotNull("entityAffiliation Id must not be null", entityAffiliation.getId());
            assertNotNull("entityAffiliation entityId must not be null", entityAffiliation.getEntityId());
        }

        List<EntityCitizenship> createdEntityCitizenships = entityFromDb.getCitizenships();
        assertNotNull("entityCitizenships must not be null", createdEntityCitizenships);
        assertEquals("There must be 1 entityCitizenship", 1, createdEntityCitizenships.size());
        for (EntityCitizenship entityCitizenship : createdEntityCitizenships) {
            assertNotNull("entityCitizenship entityId must not be null", entityCitizenship.getEntityId());
        }

        List<EntityResidency> createdEntityResidencies = entityFromDb.getResidencies();
        assertNotNull("createdEntityResidencies must not be null", createdEntityResidencies);
        assertEquals("There must be 1 createdEntityResidency", 1, createdEntityResidencies.size());
        for (EntityResidency entityResidency : createdEntityResidencies) {
            assertNotNull("entityResidency entityId must not be null", entityResidency.getEntityId());
        }

        List<EntityVisa> createdEntityVisas = entityFromDb.getVisas();
        assertNotNull("createdEntityVisas must not be null", createdEntityVisas);
        assertEquals("There must be 1 createdEntityVisa", 1, createdEntityVisas.size());
        for (EntityVisa entityVisa : createdEntityVisas) {
            assertNotNull("entityVisa entityId must not be null", entityVisa.getEntityId());
        }

        // Adding the following lines so more complicated joins will be created.  This is to
        // test the changes made for KULRICE-14269.

        Map<String, Object> criteriaToAnd = new HashMap<String, Object>(3);
        criteriaToAnd.put("employmentInformation.employeeId", Arrays.asList("1234test", "xxxxtest", "zzzztest"));
        criteriaToAnd.put("employmentInformation.primaryDepartmentCode", Arrays.asList("BL-CHEM", "BL-ART", "BL-MY"));
        criteriaToAnd.put("active", Boolean.TRUE);
        QueryByCriteria finalCriteria = QueryByCriteria.Builder.andAttributes(criteriaToAnd).build();
        List<EntityBo> results = KRADServiceLocator.getDataObjectService().findMatching(EntityBo.class,
                finalCriteria).getResults();
        assertTrue(results.size() == 1);

        //  The following SQL cares about data from two different nested collections.  (emp info and affiliations)
        Map<String, Object> criteriaToAnd2 = new HashMap<String, Object>(3);
        criteriaToAnd2.put("employmentInformation.employeeId", Arrays.asList("1234test", "xxxxtest", "zzzztest"));
        criteriaToAnd2.put("affiliations.campusCode", Arrays.asList("BL", "MX", "UT"));
        criteriaToAnd2.put("active", Boolean.TRUE);
        QueryByCriteria finalCriteria2 = QueryByCriteria.Builder.andAttributes(criteriaToAnd2).build();
        List<EntityBo> results2 = KRADServiceLocator.getDataObjectService().findMatching(EntityBo.class,
                finalCriteria2).getResults();
        assertTrue(results2.size() == 1);
    }

    @Test
    public void testUpdateEntity() {
        Principal principal = identityService.getPrincipal("p1");
        Entity entity = identityService.getEntity(principal.getEntityId());
        assertNotNull("Entity Must not be null", entity);

        assertEquals("Entity should have 1 name", 1, entity.getNames().size());
        Entity.Builder builder = Entity.Builder.create(entity);

        // add a Name
        List<EntityName.Builder> names = builder.getNames();
        names.add(getNewEntityName(entity.getId()));

        // add student affiliation
        EntityAffiliation.Builder affiliationStdnt = EntityAffiliation.Builder.create();
        affiliationStdnt.setActive(true);
        affiliationStdnt.setCampusCode("MX");
        affiliationStdnt.setAffiliationType(EntityAffiliationType.Builder.create("STDNT"));
        builder.getAffiliations().add(affiliationStdnt);

        // add employment
        EntityEmployment.Builder entityEmploymentBuilder = EntityEmployment.Builder.create();
        EntityAffiliation.Builder affiliationStaff = EntityAffiliation.Builder.create();
        affiliationStaff.setActive(true);
        affiliationStaff.setCampusCode("GR");
        affiliationStaff.setAffiliationType(EntityAffiliationType.Builder.create("STAFF"));
        entityEmploymentBuilder.setEntityAffiliation(affiliationStaff);
        entityEmploymentBuilder.setActive(true);
        entityEmploymentBuilder.setEmployeeId("1234test");
        entityEmploymentBuilder.setPrimary(false);
        entityEmploymentBuilder.setBaseSalaryAmount(new KualiDecimal(8000));
        entityEmploymentBuilder.setPrimaryDepartmentCode("BL-CHEM");
        builder.getEmploymentInformation().add(entityEmploymentBuilder);

        entity = identityService.updateEntity(builder.build());
        entity = EntityBo.to( KradDataServiceLocator.getDataObjectService().find(EntityBo.class, entity.getId()) );

        assertNotNull("Entity Must not be null", entity);
        assertEquals("Entity should have 2 names", 2, entity.getNames().size());

        List<EntityAffiliation> createdEntityAffiliations = entity.getAffiliations();
        assertNotNull("createdEntityAffiliations must not be null", createdEntityAffiliations);
        assertEquals("There must be two createdEntityAffiliations", 2, createdEntityAffiliations.size());
        for (EntityAffiliation entityAffiliation : createdEntityAffiliations) {
            assertNotNull("entityAffiliation entityId must not be null", entityAffiliation.getEntityId());
        }

        List<EntityEmployment> createdEntityEmployments = entity.getEmploymentInformation();
        List<EntityEmployment> entityEmploymentToRemove = new ArrayList<EntityEmployment>();
        assertNotNull("createdEntityEmployments must not be null", createdEntityEmployments);
        assertEquals("There must be two createdEntityEmployments", 2, createdEntityEmployments.size());
        for (EntityEmployment entityEmployment : createdEntityEmployments) {
            assertNotNull("entityEmployment entityId must not be null", entityEmployment.getEntityId());
            if (entityEmployment.getEmployeeId().equalsIgnoreCase("1234test")) {
                EntityEmploymentBo entityEmploymentBo = EntityEmploymentBo.from(entityEmployment);
                assertNotNull("entityEmploymentBo entityAffiliationId must not be null",
                        entityEmploymentBo.getEntityAffiliationId());
                EntityAffiliation entityAffiliation = entityEmployment.getEntityAffiliation();
                assertNotNull("entityAffiliation Id must not be null", entityAffiliation.getId());
                assertNotNull("entityAffiliation entityId must not be null", entityAffiliation.getEntityId());
            }
        }

        // remove the old name - make sure collection items are removed.
        builder = Entity.Builder.create(entity);
        builder.setNames(Collections.singletonList(getNewEntityName(entity.getId())));

        entity = identityService.updateEntity(builder.build());
        assertNotNull("Entity Must not be null", entity);
        assertEquals("Entity should have 1 names", 1, entity.getNames().size());
    }

    private EntityName.Builder getNewEntityName(String entityId) {

        EntityName.Builder builder = EntityName.Builder.create();
        builder.setActive(true);
        builder.setDefaultValue(false);
        builder.setEntityId(entityId);
        builder.setFirstName("Bob");
        builder.setLastName("Bobbers");
        builder.setNamePrefix("Mr");

        CodedAttribute.Builder nameType = CodedAttribute.Builder.create(identityService.getNameType(
                KimConstants.NameTypes.PRIMARY));
        builder.setNameType(nameType);
        return builder;
    }

    private void populateEntityForCreate(Entity.Builder entityBuilder) {
        // set up principals
        Principal.Builder principalBuilder = Principal.Builder.create("tinMan");
        principalBuilder.setActive(true);
        List<Principal.Builder> principals = new ArrayList<Principal.Builder>();
        principals.add(principalBuilder);
        entityBuilder.setPrincipals(principals);

        // set up names
        EntityName.Builder nameBuilder = EntityName.Builder.create();
        nameBuilder.setFirstName("Nick");
        nameBuilder.setLastName("Chopper");
        nameBuilder.setDefaultValue(true);
        List<EntityName.Builder> entityNames = new ArrayList<EntityName.Builder>();
        entityNames.add(nameBuilder);
        entityBuilder.setNames(entityNames);

        // set up student affiliation
        EntityAffiliation.Builder affiliationStdnt = EntityAffiliation.Builder.create();
        affiliationStdnt.setActive(true);
        affiliationStdnt.setCampusCode("MX");
        affiliationStdnt.setAffiliationType(EntityAffiliationType.Builder.create("STDNT"));
        List<EntityAffiliation.Builder> entityAffiliations = new ArrayList<EntityAffiliation.Builder>();
        entityAffiliations.add(affiliationStdnt);
        entityBuilder.setAffiliations(entityAffiliations);

        // set up employment
        EntityEmployment.Builder entityEmploymentBuilder = EntityEmployment.Builder.create();
        EntityAffiliation.Builder affiliationStaff = EntityAffiliation.Builder.create();
        affiliationStaff.setActive(true);
        affiliationStaff.setCampusCode("GR");
        affiliationStaff.setAffiliationType(EntityAffiliationType.Builder.create("STAFF"));
        entityEmploymentBuilder.setEntityAffiliation(affiliationStaff);
        entityEmploymentBuilder.setActive(true);
        entityEmploymentBuilder.setEmployeeId("1234test");
        entityEmploymentBuilder.setPrimary(true);
        entityEmploymentBuilder.setBaseSalaryAmount(new KualiDecimal(8000));
        entityEmploymentBuilder.setPrimaryDepartmentCode("BL-CHEM");
        List<EntityEmployment.Builder> entityEmployments = new ArrayList<EntityEmployment.Builder>();
        entityEmployments.add(entityEmploymentBuilder);
        entityBuilder.setEmploymentInformation(entityEmployments);

        // set up external identifier
        EntityExternalIdentifier.Builder externalIdentifierBuilder = EntityExternalIdentifier.Builder.create();
        externalIdentifierBuilder.setExternalIdentifierTypeCode("SSN");
        externalIdentifierBuilder.setExternalId("444-44-4444");
        List<EntityExternalIdentifier.Builder> externalIdentifiers = new ArrayList<EntityExternalIdentifier.Builder>();
        externalIdentifiers.add(externalIdentifierBuilder);
        entityBuilder.setExternalIdentifiers(externalIdentifiers);

        // set up citizenship
        EntityCitizenship.Builder citizenshipBuilder = EntityCitizenship.Builder.create();
        citizenshipBuilder.setCountryCode("OZ");
        citizenshipBuilder.setActive(true);
        List<EntityCitizenship.Builder> entityCitizenships = new ArrayList<EntityCitizenship.Builder>();
        entityCitizenships.add(citizenshipBuilder);
        entityBuilder.setCitizenships(entityCitizenships);

        // set up residency
        EntityResidency.Builder residencyBuilder = EntityResidency.Builder.create();
        residencyBuilder.setDeterminationMethod("XXX");
        residencyBuilder.setInState("YYY");
        List<EntityResidency.Builder> entityResidencies = new ArrayList<EntityResidency.Builder>();
        entityResidencies.add(residencyBuilder);
        entityBuilder.setResidencies(entityResidencies);

        // set up visa
        EntityVisa.Builder visaBuilder = EntityVisa.Builder.create();
        visaBuilder.setVisaId("1234");
        List<EntityVisa.Builder> entityVisas = new ArrayList<EntityVisa.Builder>();
        entityVisas.add(visaBuilder);
        entityBuilder.setVisas(entityVisas);
    }
}
