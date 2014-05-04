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
package org.kuali.rice.kim.service.impl

import groovy.mock.interceptor.MockFor
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.kuali.rice.core.api.criteria.*
import org.kuali.rice.core.api.exception.RiceIllegalStateException
import org.kuali.rice.kim.api.identity.IdentityService
import org.kuali.rice.kim.api.identity.address.EntityAddress
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliation
import org.kuali.rice.kim.api.identity.citizenship.EntityCitizenship
import org.kuali.rice.kim.api.identity.email.EntityEmail
import org.kuali.rice.kim.api.identity.employment.EntityEmployment
import org.kuali.rice.kim.api.identity.entity.Entity
import org.kuali.rice.kim.api.identity.entity.EntityDefault
import org.kuali.rice.kim.api.identity.entity.EntityDefaultQueryResults
import org.kuali.rice.kim.api.identity.entity.EntityQueryResults
import org.kuali.rice.kim.api.identity.external.EntityExternalIdentifier
import org.kuali.rice.kim.api.identity.name.EntityName
import org.kuali.rice.kim.api.identity.name.EntityNameQueryResults
import org.kuali.rice.kim.api.identity.personal.EntityBioDemographics
import org.kuali.rice.kim.api.identity.personal.EntityEthnicity
import org.kuali.rice.kim.api.identity.phone.EntityPhone
import org.kuali.rice.kim.api.identity.principal.Principal
import org.kuali.rice.kim.api.identity.privacy.EntityPrivacyPreferences
import org.kuali.rice.kim.api.identity.residency.EntityResidency
import org.kuali.rice.kim.api.identity.type.EntityTypeContactInfo
import org.kuali.rice.kim.api.identity.visa.EntityVisa
import org.kuali.rice.kim.impl.identity.IdentityServiceImpl
import org.kuali.rice.kim.impl.identity.address.EntityAddressBo
import org.kuali.rice.kim.impl.identity.address.EntityAddressTypeBo
import org.kuali.rice.kim.impl.identity.affiliation.EntityAffiliationBo
import org.kuali.rice.kim.impl.identity.affiliation.EntityAffiliationTypeBo
import org.kuali.rice.kim.impl.identity.citizenship.EntityCitizenshipBo
import org.kuali.rice.kim.impl.identity.citizenship.EntityCitizenshipStatusBo
import org.kuali.rice.kim.impl.identity.email.EntityEmailBo
import org.kuali.rice.kim.impl.identity.email.EntityEmailTypeBo
import org.kuali.rice.kim.impl.identity.employment.EntityEmploymentBo
import org.kuali.rice.kim.impl.identity.employment.EntityEmploymentStatusBo
import org.kuali.rice.kim.impl.identity.employment.EntityEmploymentTypeBo
import org.kuali.rice.kim.impl.identity.entity.EntityBo
import org.kuali.rice.kim.impl.identity.external.EntityExternalIdentifierBo
import org.kuali.rice.kim.impl.identity.external.EntityExternalIdentifierTypeBo
import org.kuali.rice.kim.impl.identity.name.EntityNameBo
import org.kuali.rice.kim.impl.identity.name.EntityNameTypeBo
import org.kuali.rice.kim.impl.identity.personal.EntityBioDemographicsBo
import org.kuali.rice.kim.impl.identity.personal.EntityEthnicityBo
import org.kuali.rice.kim.impl.identity.phone.EntityPhoneBo
import org.kuali.rice.kim.impl.identity.phone.EntityPhoneTypeBo
import org.kuali.rice.kim.impl.identity.principal.PrincipalBo
import org.kuali.rice.kim.impl.identity.privacy.EntityPrivacyPreferencesBo
import org.kuali.rice.kim.impl.identity.residency.EntityResidencyBo
import org.kuali.rice.kim.impl.identity.type.EntityTypeContactInfoBo
import org.kuali.rice.kim.impl.identity.visa.EntityVisaBo
import org.kuali.rice.krad.data.DataObjectService
import org.kuali.rice.krad.data.PersistenceOption
import org.kuali.rice.krad.service.PersistenceService

import java.text.SimpleDateFormat

class IdentityServiceImplTest {
    private final shouldFail = new GroovyTestCase().&shouldFail

    private MockFor mockCriteriaLookupService;
    private MockFor mockDataObjectService;
    private DataObjectService dos;
    private PersistenceService persistenceService;
    private CriteriaLookupService criteriaLookupService;
    IdentityService identityService;
    IdentityServiceImpl identityServiceImpl;

    Map<String, EntityBo> sampleEntities = new HashMap<String, EntityBo>();
    Map<String, PrincipalBo> samplePrincipals = new HashMap<String, PrincipalBo>();
    Map<String, EntityTypeContactInfoBo> sampleEntityTypeContactInfos = new HashMap<String, EntityTypeContactInfoBo>();
    Map<String, EntityAddressBo> sampleEntityAddresses = new HashMap<String, EntityAddressBo>();
    Map<String, EntityEmailBo> sampleEntityEmails = new HashMap<String, EntityEmailBo>();
    Map<String, EntityPhoneBo> sampleEntityPhones = new HashMap<String, EntityPhoneBo>();
    Map<String, EntityExternalIdentifierBo> sampleEntityExternalIdentifiers = new HashMap<String, EntityExternalIdentifierBo>();
    Map<String, EntityAffiliationBo> sampleEntityAffiliations = new HashMap<String, EntityAffiliationBo>();
    Map<String, EntityPrivacyPreferencesBo> sampleEntityPrivacyPreferences = new HashMap<String, EntityPrivacyPreferencesBo>();
    Map<String, EntityCitizenshipBo> sampleEntityCitizenships = new HashMap<String, EntityCitizenshipBo>();
    Map<String, EntityEthnicityBo> sampleEntityEthnicities = new HashMap<String, EntityEthnicityBo>();
    Map<String, EntityResidencyBo> sampleEntityResidencies = new HashMap<String, EntityResidencyBo>();
    Map<String, EntityVisaBo> sampleEntityVisas = new HashMap<String, EntityVisaBo>();
    Map<String, EntityNameBo> sampleEntityNames = new HashMap<String, EntityNameBo>();
    Map<String, EntityEmploymentBo> sampleEntityEmployments = new HashMap<String, EntityEmploymentBo>();
    Map<String, EntityBioDemographicsBo> sampleEntityBioDemographics = new HashMap<String, EntityBioDemographicsBo>();

    @Before
    void createSampleBOs() {
        EntityPrivacyPreferencesBo firstEntityPrivacyPreferencesBo = new EntityPrivacyPreferencesBo(entityId: "AAA", suppressName: true, suppressEmail: true, suppressAddress: true, suppressPhone: true, suppressPersonal: false);
        String birthDateString = "01/01/2007";
        String deceasedDateString = "01/01/2087";
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date birthDate = formatter.parse(birthDateString);
        Date deceasedDate = formatter.parse(deceasedDateString);
        EntityBioDemographicsBo firstEntityBioDemographicsBo = new EntityBioDemographicsBo(entityId: "AAA", birthDateValue: birthDate, genderCode: "M", deceasedDateValue: deceasedDate, maritalStatusCode: "S", primaryLanguageCode: "EN", secondaryLanguageCode: "FR", birthCountry: "US", birthStateProvinceCode: "IN", birthCity: "Bloomington", geographicOrigin: "None", suppressPersonal: false);
        PrincipalBo firstEntityPrincipal = new PrincipalBo(entityId: "AAA", principalId: "P1", active: true, principalName: "first", versionNumber: 1, password: "first_password");
        List<PrincipalBo> firstPrincipals = new ArrayList<PrincipalBo>();
        firstPrincipals.add(firstEntityPrincipal);
        EntityTypeContactInfoBo firstEntityTypeContactInfoBo = new EntityTypeContactInfoBo(entityId: "AAA", entityTypeCode: "typecodeone", active: true);
        EntityAddressTypeBo firstAddressTypeBo = new EntityAddressTypeBo(code: "addresscodeone");
        EntityAddressBo firstEntityAddressBo = new EntityAddressBo(entityId: "AAA", entityTypeCode: "typecodeone", addressType: firstAddressTypeBo, id: "addressidone", addressTypeCode: "addresscodeone", active: true);
        EntityEmailTypeBo firstEmailTypeBo = new EntityEmailTypeBo(code: "emailcodeone");
        EntityEmailBo firstEntityEmailBo = new EntityEmailBo(entityId: "AAA", entityTypeCode: "typecodeone", emailType: firstEmailTypeBo, id:"emailidone", emailTypeCode: "emailcodeone", active: true);
        EntityPhoneTypeBo firstPhoneType = new EntityPhoneTypeBo(code: "phonecodeone");
        EntityPhoneBo firstEntityPhoneBo = new EntityPhoneBo(entityId: "AAA", entityTypeCode: "typecodeone", phoneType: firstPhoneType, id: "phoneidone", phoneTypeCode: "phonetypecodeone", active: true);
        EntityExternalIdentifierTypeBo firstExternalIdentifierType = new EntityExternalIdentifierTypeBo(code: "exidtypecodeone");
        EntityExternalIdentifierBo firstEntityExternalIdentifierBo = new EntityExternalIdentifierBo(entityId: "AAA", externalIdentifierType: firstExternalIdentifierType, id: "exidone", externalIdentifierTypeCode: "exidtypecodeone");
        EntityAffiliationTypeBo firstAffiliationType = new EntityAffiliationTypeBo(code: "affiliationcodeone");
        EntityAffiliationBo firstEntityAffiliationBo = new EntityAffiliationBo(entityId: "AAA", affiliationType: firstAffiliationType, id: "affiliationidone", affiliationTypeCode: "affiliationcodeone", active: true);
        EntityCitizenshipStatusBo firstEntityCitizenshipStatus = new EntityCitizenshipStatusBo(code: "statuscodeone", name: "statusnameone");
        EntityCitizenshipBo firstEntityCitizenshipBo = new EntityCitizenshipBo(entityId: "AAA", id: "citizenshipidone", active: true, status: firstEntityCitizenshipStatus, statusCode: "statuscodeone");
        EntityEthnicityBo firstEntityEthnicityBo = new EntityEthnicityBo(entityId: "AAA", id: "ethnicityidone");
        EntityResidencyBo firstEntityResidencyBo = new EntityResidencyBo(entityId: "AAA", id: "residencyidone");
        EntityVisaBo firstEntityVisaBo = new EntityVisaBo(entityId: "AAA", id: "visaidone");
        EntityNameTypeBo firstEntityNameType = new EntityNameTypeBo(code: "namecodeone");
        EntityNameBo firstEntityNameBo = new EntityNameBo(entityId: "AAA", id: "nameidone", active: true, firstName: "John", lastName: "Smith", nameType: firstEntityNameType, nameCode: "namecodeone");
        EntityEmploymentTypeBo firstEmploymentType = new EntityEmploymentTypeBo(code: "employmenttypecodeone");
        EntityEmploymentStatusBo firstEmploymentStatus = new EntityEmploymentStatusBo(code: "employmentstatusone");
        EntityEmploymentBo firstEntityEmploymentBo = new EntityEmploymentBo(entityId: "AAA", id: "employmentidone", entityAffiliation: firstEntityAffiliationBo, entityAffiliationId: "affiliationidone", employeeType: firstEmploymentType, employeeTypeCode: "employmenttypecodeone", employeeStatus: firstEmploymentStatus, employeeStatusCode: "employmentstatusone", active: true, employeeId: "emplIdOne");
        List<EntityEmploymentBo> firstEmplymentBos = new ArrayList<EntityEmploymentBo>();
        firstEmplymentBos.add(firstEntityEmploymentBo);
        EntityBo firstEntityBo = new EntityBo(active: true, id: "AAA", privacyPreferences: firstEntityPrivacyPreferencesBo, bioDemographics: firstEntityBioDemographicsBo, principals: firstPrincipals, employmentInformation: firstEmplymentBos);

        EntityPrivacyPreferencesBo secondEntityPrivacyPreferencesBo = new EntityPrivacyPreferencesBo(entityId: "BBB", suppressName: true, suppressEmail: true, suppressAddress: true, suppressPhone: true, suppressPersonal: false);
        EntityBioDemographicsBo secondEntityBioDemographicsBo = new EntityBioDemographicsBo(entityId: "BBB", birthDateValue: birthDate, genderCode: "M", deceasedDateValue: deceasedDate, maritalStatusCode: "S", primaryLanguageCode: "EN", secondaryLanguageCode: "FR", birthCountry: "US", birthStateProvinceCode: "IN", birthCity: "Bloomington", geographicOrigin: "None", suppressPersonal: false);
        PrincipalBo secondEntityPrincipal = new PrincipalBo(entityId: "BBB", principalId: "P2", active: true, principalName: "second", versionNumber: 1, password: "second_password");
        List<PrincipalBo> secondPrincipals = new ArrayList<PrincipalBo>();
        secondPrincipals.add(secondEntityPrincipal);
        EntityTypeContactInfoBo secondEntityTypeContactInfoBo = new EntityTypeContactInfoBo(entityId: "BBB", entityTypeCode: "typecodetwo", active: true);
        EntityAddressTypeBo secondAddressTypeBo = new EntityAddressTypeBo(code: "addresscodetwo");
        EntityAddressBo secondEntityAddressBo = new EntityAddressBo(entityId: "BBB", entityTypeCode: "typecodetwo", addressType: secondAddressTypeBo, id: "addressidtwo", addressTypeCode: "addresscodetwo", active: true);
        EntityEmailTypeBo secondEmailTypeBo = new EntityEmailTypeBo(code: "emailcodetwo");
        EntityEmailBo secondEntityEmailBo = new EntityEmailBo(entityId: "BBB", entityTypeCode: "typecodetwo", emailType: secondEmailTypeBo, id:"emailidtwo", emailTypeCode: "emailcodetwo", active: true);
        EntityPhoneTypeBo secondPhoneType = new EntityPhoneTypeBo(code: "phonecodetwo");
        EntityPhoneBo secondEntityPhoneBo = new EntityPhoneBo(entityId: "BBB", entityTypeCode: "typecodetwo", phoneType: secondPhoneType, id: "phoneidtwo", phoneTypeCode: "phonetypecodetwo", active: true);
        EntityExternalIdentifierTypeBo secondExternalIdentifierType = new EntityExternalIdentifierTypeBo(code: "exidtypecodetwo");
        EntityExternalIdentifierBo secondEntityExternalIdentifierBo = new EntityExternalIdentifierBo(entityId: "BBB", externalIdentifierType: secondExternalIdentifierType, id: "exidtwo", externalIdentifierTypeCode: "exidtypecodetwo");
        EntityAffiliationTypeBo secondAffiliationType = new EntityAffiliationTypeBo(code: "affiliationcodetwo");
        EntityAffiliationBo secondEntityAffiliationBo = new EntityAffiliationBo(entityId: "BBB", affiliationType: secondAffiliationType, id: "affiliationidtwo", affiliationTypeCode: "affiliationcodetwo", active: true);
        EntityCitizenshipStatusBo secondEntityCitizenshipStatus = new EntityCitizenshipStatusBo(code: "statuscodetwo", name: "statusnametwo");
        EntityCitizenshipBo secondEntityCitizenshipBo = new EntityCitizenshipBo(entityId: "BBB", id: "citizenshipidtwo", active: true, status: secondEntityCitizenshipStatus, statusCode: "statuscodetwo");
        EntityEthnicityBo secondEntityEthnicityBo = new EntityEthnicityBo(entityId: "BBB", id: "ethnicityidtwo");
        EntityResidencyBo secondEntityResidencyBo = new EntityResidencyBo(entityId: "BBB", id: "residencyidtwo");
        EntityVisaBo secondEntityVisaBo = new EntityVisaBo(entityId: "BBB", id: "visaidtwo");
        EntityNameTypeBo secondEntityNameType = new EntityNameTypeBo(code: "namecodetwo");
        EntityNameBo secondEntityNameBo = new EntityNameBo(entityId: "BBB", id: "nameidtwo", active: true, firstName: "Bill", lastName: "Wright", nameType: secondEntityNameType, nameCode: "namecodetwo");
        EntityEmploymentTypeBo secondEmploymentType = new EntityEmploymentTypeBo(code: "employmenttypecodetwo");
        EntityEmploymentStatusBo secondEmploymentStatus = new EntityEmploymentStatusBo(code: "employmentstatustwo");
        EntityEmploymentBo secondEntityEmploymentBo = new EntityEmploymentBo(entityId: "BBB", id: "employmentidtwo", entityAffiliation: secondEntityAffiliationBo, entityAffiliationId: "affiliationidtwo", employeeType: secondEmploymentType, employeeTypeCode: "employmenttypecodetwo", employeeStatus: secondEmploymentStatus, employeeStatusCode: "employmentstatustwo", active: true, employeeId: "emplIdTwo");
        List<EntityEmploymentBo> secondEmplymentBos = new ArrayList<EntityEmploymentBo>();
        secondEmplymentBos.add(secondEntityEmploymentBo);
        EntityBo secondEntityBo = new EntityBo(active: true, id: "BBB", privacyPreferences: secondEntityPrivacyPreferencesBo, bioDemographics: secondEntityBioDemographicsBo, principals: secondPrincipals, employmentInformation: secondEmplymentBos);

        for (bo in [firstEntityBo, secondEntityBo]) {
            sampleEntities.put(bo.id, bo)
        }

        for (bo in [firstEntityPrincipal, secondEntityPrincipal]) {
            samplePrincipals.put(bo.principalId, bo);
        }

        for (bo in [firstEntityAddressBo, secondEntityAddressBo]) {
            sampleEntityAddresses.put(bo.entityId, bo);
        }

        for (bo in [firstEntityTypeContactInfoBo, secondEntityTypeContactInfoBo]) {
            sampleEntityTypeContactInfos.put(bo.entityTypeCode, bo);
        }

        for (bo in [firstEntityEmailBo, secondEntityEmailBo]) {
            sampleEntityEmails.put(bo.entityId, bo);
        }

        for (bo in [firstEntityPhoneBo, secondEntityPhoneBo]) {
            sampleEntityPhones.put(bo.entityId, bo);
        }

        for (bo in [firstEntityExternalIdentifierBo, secondEntityExternalIdentifierBo]) {
            sampleEntityExternalIdentifiers.put(bo.entityId, bo);
        }

        for (bo in [firstEntityAffiliationBo, secondEntityAffiliationBo]) {
            sampleEntityAffiliations.put(bo.entityId, bo);
        }

        for (bo in [firstEntityPrivacyPreferencesBo, secondEntityPrivacyPreferencesBo]) {
            sampleEntityPrivacyPreferences.put(bo.entityId, bo);
        }

        for (bo in [firstEntityCitizenshipBo, secondEntityCitizenshipBo]) {
            sampleEntityCitizenships.put(bo.entityId, bo);
        }

        for (bo in [firstEntityEthnicityBo, secondEntityEthnicityBo]) {
            sampleEntityEthnicities.put(bo.entityId, bo);
        }

        for (bo in [firstEntityResidencyBo, secondEntityResidencyBo]) {
            sampleEntityResidencies.put(bo.entityId, bo);
        }

        for (bo in [firstEntityVisaBo, secondEntityVisaBo]) {
            sampleEntityVisas.put(bo.entityId, bo);
        }

        for (bo in [firstEntityNameBo, secondEntityNameBo]) {
            sampleEntityNames.put(bo.entityId, bo);
        }

        for (bo in [firstEntityEmploymentBo, secondEntityEmploymentBo]) {
            sampleEntityEmployments.put(bo.entityId, bo);
        }

        for (bo in [firstEntityBioDemographicsBo, secondEntityBioDemographicsBo]) {
            sampleEntityBioDemographics.put(bo.entityId, bo);
        }
    }

    @Before
    void setupMockContext() {
        mockCriteriaLookupService = new MockFor(CriteriaLookupService.class);
        mockDataObjectService = new MockFor(DataObjectService.class);

    }

    @Before
    void setupServiceUnderTest() {
        identityServiceImpl = new IdentityServiceImpl()
        identityService = identityServiceImpl    //assign Interface type to implementation reference for unit test only
    }

    void injectDataObjectServiceIntoIdentityService() {
        dos = mockDataObjectService.proxyDelegateInstance()
        identityServiceImpl.setDataObjectService(dos)
    }

    void injectCriteriaLookupServiceIntoIdentityService() {
        criteriaLookupService = mockCriteriaLookupService.proxyDelegateInstance();
        identityServiceImpl.setCriteriaLookupService(criteriaLookupService);
    }

    @Test
    void test_createIdentityNullIdentity(){
        injectDataObjectServiceIntoIdentityService()

        shouldFail(IllegalArgumentException.class) {
            identityService.createEntity(null)
        }
        mockDataObjectService.verify(dos)
    }

    @Test
    void test_updateIdentityNullIdentity(){
        injectDataObjectServiceIntoIdentityService();

        shouldFail(IllegalArgumentException.class) {
            identityService.updateEntity(null)
        }
        mockDataObjectService.verify(dos);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEntityEmptyId() {
        Entity entity = identityService.getEntity("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEntityNullId() {
        Entity entity = identityService.getEntity(null);
    }

    @Test
    public void testGetEntity() {
        mockDataObjectService.demand.find(1..sampleEntities.size()) {
            Class clazz, String id -> return sampleEntities.get(id);
        }

        injectDataObjectServiceIntoIdentityService();

        for (EntityBo entityBo in sampleEntities.values()) {
            Assert.assertEquals(EntityBo.to(entityBo), identityService.getEntity(entityBo.id))
        }

        mockDataObjectService.verify(dos)
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEntityByPrincipalIdWithEmptyIdFails() {
        Entity entity = identityService.getEntityByPrincipalId("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEntityByPrincipalIdWithNullIdFails() {
        Entity entity = identityService.getEntityByPrincipalId(null);
    }

    @Test
    public void testGetEntityByPrincipalIdSucceeds() {
        mockDataObjectService.demand.findMatching(1..sampleEntities.size()) {
            Class clazz, QueryByCriteria id -> for (EntityBo entityBo in sampleEntities.values()) {
                EqualPredicate p = (EqualPredicate)id.getPredicate();

                for (PrincipalBo principalBo in entityBo.principals) {
                    if (principalBo.principalId.equals(p.value.value))
                    {
                        Collection<EntityBo> entities = new ArrayList<EntityBo>();
                        entities.add(entityBo);
                        GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
                        builder.setResults(entities);
                        return builder.build();
                    }
                }
            }
        }

        injectDataObjectServiceIntoIdentityService();

        for (EntityBo entityBo in sampleEntities.values()) {
            Assert.assertEquals(EntityBo.to(entityBo), identityService.getEntityByPrincipalId(entityBo.principals[0].principalId));
        }

        mockDataObjectService.verify(dos)
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEntityByPrincipalNameWithEmptyNameFails() {
        Entity entity = identityService.getEntityByPrincipalName("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEntityByPrincipalNameWithNullNameFails() {
        Entity entity = identityService.getEntityByPrincipalName(null);
    }

    @Test
    public void testGetEntityByPrincipalNameSucceeds() {
        mockDataObjectService.demand.findMatching(1..sampleEntities.size()) {
            Class clazz, QueryByCriteria map -> for (EntityBo entityBo in sampleEntities.values()) {
                EqualPredicate p = (EqualPredicate)map.predicate;
                for (PrincipalBo principalBo in entityBo.principals) {
                    if (principalBo.principalName.equals(p.value.value))
                    {
                        GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
                        builder.setResults(Collections.singletonList(entityBo));
                        return builder.build();
                    }
                }
            }
        }

        injectDataObjectServiceIntoIdentityService();

        for (EntityBo entityBo in sampleEntities.values()) {
            Assert.assertEquals(EntityBo.to(entityBo), identityService.getEntityByPrincipalName(entityBo.principals[0].principalName));
        }

        mockDataObjectService.verify(dos)
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPrincipalByPrincipalNameAndPasswordWithEmptyNameFails() {
        Principal principal = identityService.getPrincipalByPrincipalNameAndPassword("", "password");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPrincipalByPrincipalNameAndPasswordWithEmptyPasswordFails() {
        Principal principal = identityService.getPrincipalByPrincipalNameAndPassword("name", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPrincipalByPrincipalNameAndPasswordWithNullNameFails() {
        Principal principal = identityService.getPrincipalByPrincipalNameAndPassword(null, "password");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPrincipalByPrincipalNameAndPasswordWithNullPasswordFails() {
        Principal principal = identityService.getPrincipalByPrincipalNameAndPassword("name", null);
    }

    @Test
    public void testGetPrincipalByPrincipalNameAndPasswordSucceeds() {
        mockDataObjectService.demand.findMatching(1..samplePrincipals.size()) {
            Class clazz, QueryByCriteria map -> for (PrincipalBo principalBo in samplePrincipals.values()) {
                if (principalBo.principalName.equals("second")
                        && principalBo.password.equals("second_password")
                        && principalBo.active)
                {
                    GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
                    builder.setResults(Collections.singletonList(principalBo));
                    return builder.build();
                }
            }
        }

        injectDataObjectServiceIntoIdentityService();

        String id = "P2";
        String name = "second";
        String password = "second_password";
        Assert.assertEquals(PrincipalBo.to(samplePrincipals.get(id)), identityService.getPrincipalByPrincipalNameAndPassword(name, password));

        mockDataObjectService.verify(dos);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPrincipalsByEntityIdWithEmptyEntityIdFails() {
        List<Principal> principals = identityService.getPrincipalsByEntityId("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPrincipalsByEntityIdWithNullEntityIdFails() {
        List<Principal> principals = identityService.getPrincipalsByEntityId(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPrincipalsByEmployeeIdWithEmptyEmployeeIdFails() {
        List<Principal> principals = identityService.getPrincipalsByEmployeeId("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPrincipalsByEmployeeIdWithNullEmployeeIdFails() {
        List<Principal> principals = identityService.getPrincipalsByEmployeeId(null);
    }

    @Test
    public void testGetPrincipalsByEmployeeIdSucceeds() {

        mockDataObjectService.demand.findMatching(1..2) {
            Class clazz, QueryByCriteria map -> for (EntityBo entityBo in sampleEntities.values()) {
                EqualPredicate p = (EqualPredicate)map.getPredicate();
                for (EntityEmploymentBo entityEmploymentBo in entityBo.employmentInformation) {
                    if (p.value.value.equals(entityEmploymentBo.employeeId))
                    {
                        GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
                        builder.setResults(Collections.singletonList(entityEmploymentBo));
                        return builder.build();
                    }
                }

                for (PrincipalBo principalBo in samplePrincipals.values()) {
                    if (p.value.value.toString().equals(principalBo.entityId)
                            && principalBo.active)
                    {
                        GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
                        builder.setResults(Collections.singletonList(principalBo));
                        return builder.build();
                    }
                }
            }
        }

        injectDataObjectServiceIntoIdentityService();

        String employeeId = "emplIdOne";
        String id = "P1";
        String entityId = 'AAA'
        EntityEmployment sampleEntityEmployment = EntityEmploymentBo.to(sampleEntityEmployments.get(entityId));
        Principal samplePrincipal = PrincipalBo.to(samplePrincipals.get(id));

        List<Principal> principals = identityService.getPrincipalsByEmployeeId(employeeId);
        Assert.assertNotNull(principals);
        for (Principal p : principals) {
            Assert.assertEquals(samplePrincipal.getPrincipalId(), p.getPrincipalId());
        }
        mockDataObjectService.verify(dos);
    }

    @Test
    public void testGetPrincipalsByEntityIdSucceeds() {
        mockDataObjectService.demand.findMatching(1..samplePrincipals.size()) {
            Class clazz, QueryByCriteria map -> for (PrincipalBo principalBo in samplePrincipals.values()) {
                if (principalBo.entityId.equals("AAA")
                        && principalBo.active)
                {
                    GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
                    builder.setResults(Collections.singletonList(principalBo));
                    return builder.build();
                }
            }
        }

        injectDataObjectServiceIntoIdentityService();

        String entityId = "AAA";
        String id = "P1";
        Principal samplePrincipal = PrincipalBo.to(samplePrincipals.get(id));
        List<Principal> principals = identityService.getPrincipalsByEntityId(entityId);
        for (Principal p : principals) {
            Assert.assertEquals(samplePrincipal.getEntityId(), p.getEntityId());
        }
        mockDataObjectService.verify(dos);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testAddPrincipalToEntityWithNullPrincipalFails()
    {
        Principal principal = identityService.addPrincipalToEntity(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testAddPrincipalToEntityWithBlankEntityIdFails()
    {
        PrincipalBo principalBo = new PrincipalBo(entityId: "", principalName: "name", principalId: "P1");
        principalBo = identityService.addPrincipalToEntity(PrincipalBo.to(principalBo));
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testAddPrincipalToEntityWithExistingPrincipalFails()
    {
        mockDataObjectService.demand.findMatching(1..samplePrincipals.size()) {
            Class clazz, QueryByCriteria map -> for (PrincipalBo principalBo in samplePrincipals.values()) {
                if (principalBo.principalName.equals("first"))
                {
                    Collection<PrincipalBo> principals = new ArrayList<PrincipalBo>();
                    principals.add(principalBo);
                    GenericQueryResults.Builder b = GenericQueryResults.Builder.create();
                    b.setResults(principals);
                    return b.build();
                }
            }
        }

        injectDataObjectServiceIntoIdentityService();

        PrincipalBo principalBo = new PrincipalBo(entityId: "ABC", principalName: "first", principalId: "P1");
        principalBo = identityService.addPrincipalToEntity(PrincipalBo.to(principalBo));
    }

    @Test
    public void testAddPrincipalToEntitySucceeds()
    {
        PrincipalBo newPrincipalBo = new PrincipalBo(entityId: "ABC", principalName: "new", principalId: "New");

        mockDataObjectService.demand.findMatching(1..samplePrincipals.size()) {
            Class clazz, QueryByCriteria map -> return GenericQueryResults.Builder.create().build();
        }

        mockDataObjectService.demand.save(1..1) {
            PrincipalBo bo, PersistenceOption... options -> return newPrincipalBo;
        }

        injectDataObjectServiceIntoIdentityService();

        Principal newPrincipal = identityService.addPrincipalToEntity(PrincipalBo.to(newPrincipalBo));

        Assert.assertEquals(PrincipalBo.to(newPrincipalBo), newPrincipal);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdatePrincipalWithNullPrincipalFails()
    {
        Principal principal = identityService.updatePrincipal(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testUpdatePrincipalWithBlankEntityIdFails()
    {
        PrincipalBo principalBo = new PrincipalBo(entityId: "", principalName: "name", principalId: "P1");
        principalBo = identityService.updatePrincipal(PrincipalBo.to(principalBo));
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testUpdatePrincipalWithNonExistingPrincipalFails()
    {
        // create a matching scenario where no results are returned
        mockDataObjectService.demand.find(1..samplePrincipals.size()) {
            Class clazz, String id -> return samplePrincipals.get(id)
        }

        injectDataObjectServiceIntoIdentityService();

        PrincipalBo principalBo = new PrincipalBo(entityId: "CCC", principalName: "fifth", principalId: "P5");
        principalBo = identityService.updatePrincipal(PrincipalBo.to(principalBo));
    }

    @Test
    public void testUpdatePrincipalSucceeds()
    {
        PrincipalBo existingPrincipalBo = samplePrincipals.get("P1");

        mockDataObjectService.demand.find(1..samplePrincipals.size()) {
            Class clazz, String id -> return samplePrincipals.get(id)
        }

        mockDataObjectService.demand.save(1..1) {
            PrincipalBo bo, PersistenceOption... options -> return existingPrincipalBo;
        }

        injectDataObjectServiceIntoIdentityService();

        Principal existingPrincipal = identityService.updatePrincipal(PrincipalBo.to(existingPrincipalBo));

        Assert.assertEquals(PrincipalBo.to(existingPrincipalBo), existingPrincipal);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInactivatePrincipalWithEmptyIdFails() {
        Principal principal = identityService.inactivatePrincipal("");
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testInactivatePrincipalWithNonExistentPrincipalFails() {
        mockDataObjectService.demand.find(1..1) {
            Class clazz, String id -> return null;
        }

        injectDataObjectServiceIntoIdentityService();

        Principal principal = identityService.inactivatePrincipal("New");
    }

    @Test
    public void testInactivatePrincipalSucceeds()
    {
        PrincipalBo existingPrincipalBo = samplePrincipals.get("P1");
        PrincipalBo inactivePrincipalBo = new PrincipalBo(entityId: "AAA", principalId: "P1", active: false, principalName: "first", versionNumber: 1, password: "first_password");

        mockDataObjectService.demand.find(1..samplePrincipals.size()) {
            Class clazz, String id -> return samplePrincipals.get(id)
        }

        mockDataObjectService.demand.save(1..1) {
            PrincipalBo bo, PersistenceOption... options -> return inactivePrincipalBo;
        }

        injectDataObjectServiceIntoIdentityService();

        Principal inactivePrincipal = identityService.inactivatePrincipal(existingPrincipalBo.principalId);

        Assert.assertEquals(PrincipalBo.to(inactivePrincipalBo), inactivePrincipal);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testInactivatePrincipalByNameWithNonExistentPrincipalFails() {
        mockDataObjectService.demand.findMatching(1..samplePrincipals.size()) {
            Class clazz, QueryByCriteria map -> return GenericQueryResults.Builder.create().build();
        }

        injectDataObjectServiceIntoIdentityService();

        Principal principal = identityService.inactivatePrincipalByName("new");
    }

    @Test
    public void testInactivatePrincipalByNameSucceeds()
    {
        PrincipalBo existingPrincipalBo = samplePrincipals.get("P1");
        GenericQueryResults.Builder b = GenericQueryResults.Builder.create();
        b.setResults(Collections.singletonList(existingPrincipalBo));
        PrincipalBo inactivePrincipalBo = new PrincipalBo(entityId: "AAA", principalId: "P1", active: false, principalName: "first", versionNumber: 1, password: "first_password");

        mockDataObjectService.demand.findMatching(1..samplePrincipals.size()) {
            Class clazz, QueryByCriteria map -> return b.build();
        }

        mockDataObjectService.demand.save(1) {
            PrincipalBo bo, PersistenceOption... options -> return inactivePrincipalBo;
        }

        injectDataObjectServiceIntoIdentityService();

        Principal inactivePrincipal = identityService.inactivatePrincipalByName(existingPrincipalBo.principalName);

        Assert.assertEquals(PrincipalBo.to(inactivePrincipalBo), inactivePrincipal);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddEntityTypeContactInfoToEntityWithNullFails() {
        EntityTypeContactInfo entityTypeContactInfo = identityService.addEntityTypeContactInfoToEntity(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testAddEntityTypeContactInfoToEntityWithExistingObjectFails() {
        mockDataObjectService.demand.findMatching(1..sampleEntityTypeContactInfos.size()) {
            Class clazz, QueryByCriteria map -> for (EntityTypeContactInfoBo entityTypeContactInfoBo in sampleEntityTypeContactInfos.values()) {
                if (entityTypeContactInfoBo.entityId.equals("AAA")
                        && entityTypeContactInfoBo.entityTypeCode.equals("typecodeone")
                        && entityTypeContactInfoBo.active)
                {
                    GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
                    builder.setResults(Collections.singletonList(entityTypeContactInfoBo));
                    return builder.build();
                }
            }
        }

        injectDataObjectServiceIntoIdentityService();

        EntityTypeContactInfoBo newEntityTypeContactInfoBo = new EntityTypeContactInfoBo(active: true, entityId: "AAA", entityTypeCode: "typecodeone");
        EntityTypeContactInfo entityTypeContactInfo = identityService.addEntityTypeContactInfoToEntity(EntityTypeContactInfoBo.to(newEntityTypeContactInfoBo));
    }

    @Test
    public void testAddEntityTypeContactInfoToEntitySucceeds() {
        EntityTypeContactInfoBo newEntityTypeContactInfoBo = new EntityTypeContactInfoBo(active: true, entityId: "CCC", entityTypeCode: "typecodethree");

        mockDataObjectService.demand.findMatching(1..sampleEntityTypeContactInfos.size()) {
            Class clazz, QueryByCriteria map -> return GenericQueryResults.Builder.create().build();
        }

        mockDataObjectService.demand.save(1..1) {
            EntityTypeContactInfoBo bo, PersistenceOption... options -> return newEntityTypeContactInfoBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityTypeContactInfo entityTypeContactInfo = identityService.addEntityTypeContactInfoToEntity(EntityTypeContactInfoBo.to(newEntityTypeContactInfoBo));

        Assert.assertEquals(EntityTypeContactInfoBo.to(newEntityTypeContactInfoBo), entityTypeContactInfo);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateEntityTypeContactInfoWithNullFails() {
        EntityTypeContactInfo entityTypeContactInfo = identityService.updateEntityTypeContactInfo(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testUpdateEntityTypeContactInfoWithNonExistingObjectFails() {
        mockDataObjectService.demand.findMatching(1..1) {
            Class clazz, QueryByCriteria id -> return GenericQueryResults.Builder.create().build();
        }

        injectDataObjectServiceIntoIdentityService();

        EntityTypeContactInfoBo newEntityTypeContactInfoBo = new EntityTypeContactInfoBo(active: true, entityId: "AAA", entityTypeCode: "typecodeone");
        EntityTypeContactInfo entityTypeContactInfo = identityService.updateEntityTypeContactInfo(EntityTypeContactInfoBo.to(newEntityTypeContactInfoBo));
    }

    @Test
    public void testUpdateEntityTypeContactInfoSucceeds() {
        EntityTypeContactInfoBo existingEntityTypeContactInfoBo = new EntityTypeContactInfoBo(active: true, entityId: "AAA", entityTypeCode: "typecodeone");

        mockDataObjectService.demand.findMatching(1..sampleEntityTypeContactInfos.size()) {
            Class clazz, QueryByCriteria map -> for (EntityTypeContactInfoBo entityTypeContactInfoBo in sampleEntityTypeContactInfos.values()) {
                if (entityTypeContactInfoBo.entityId.equals("AAA")
                        && entityTypeContactInfoBo.entityTypeCode.equals("typecodeone")
                        && entityTypeContactInfoBo.active)
                {
                    GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
                    builder.setResults(Collections.singletonList(entityTypeContactInfoBo));
                    return builder.build();
                }
            }
        }

        mockDataObjectService.demand.save(1..1) {
            EntityTypeContactInfoBo bo, PersistenceOption... options -> return existingEntityTypeContactInfoBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityTypeContactInfo entityTypeContactInfo = identityService.updateEntityTypeContactInfo(EntityTypeContactInfoBo.to(existingEntityTypeContactInfoBo));

        Assert.assertEquals(EntityTypeContactInfoBo.to(existingEntityTypeContactInfoBo), entityTypeContactInfo);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testInactivateEntityTypeContactInfoWithNonExistentObjectFails() {
        mockDataObjectService.demand.findMatching(1..1) {
            Class clazz, QueryByCriteria map -> GenericQueryResults.Builder.create().build();
        }

        injectDataObjectServiceIntoIdentityService();

        EntityTypeContactInfo entityTypeContactInfo = identityService.inactivateEntityTypeContactInfo("new", "new");
    }

    @Test
    public void testInactivateEntityTypeContactInfoSucceeds()
    {
        EntityTypeContactInfoBo existingEntityTypeContactInfoBo = sampleEntityTypeContactInfos.get("typecodeone");
        EntityTypeContactInfoBo inactiveEntityTypeContactInfoBo = new EntityTypeContactInfoBo(entityId: "AAA", entityTypeCode: "typecodeone", active: false);

        mockDataObjectService.demand.findMatching(1..sampleEntityTypeContactInfos.size()) {
            Class clazz, QueryByCriteria map -> for (EntityTypeContactInfoBo entityTypeContactInfoBo in sampleEntityTypeContactInfos.values()) {
                if (entityTypeContactInfoBo.entityId.equals("AAA")
                        && entityTypeContactInfoBo.entityTypeCode.equals("typecodeone")
                        && entityTypeContactInfoBo.active)
                {
                    GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
                    builder.setResults(Collections.singletonList(entityTypeContactInfoBo));
                    return builder.build();
                }
            }
        }

        mockDataObjectService.demand.save(1..1) {
            EntityTypeContactInfoBo bo, PersistenceOption... options -> return inactiveEntityTypeContactInfoBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityTypeContactInfo inactiveEntityTypeContactInfo = identityService.inactivateEntityTypeContactInfo(existingEntityTypeContactInfoBo.entityId, existingEntityTypeContactInfoBo.entityTypeCode);

        Assert.assertEquals(EntityTypeContactInfoBo.to(inactiveEntityTypeContactInfoBo), inactiveEntityTypeContactInfo);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddAddressToEntityWithNullFails() {
        EntityAddress entityAddress = identityService.addAddressToEntity(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testAddAddressToEntityWithExistingAddressFails() {
        mockDataObjectService.demand.findMatching(1..sampleEntityAddresses.size()) {
            Class clazz, QueryByCriteria map -> for (EntityAddressBo entityAddressBo in sampleEntityAddresses.values()) {
                if (entityAddressBo.entityId.equals("AAA")
                        && entityAddressBo.entityTypeCode.equals("typecodeone")
                        && entityAddressBo.addressTypeCode.equals("addresscodeone")
                        && entityAddressBo.active)
                {
                    GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
                    builder.setResults(Collections.singletonList(entityAddressBo));
                    return builder.build();
                }
            }
        }

        injectDataObjectServiceIntoIdentityService();

        EntityAddressTypeBo firstAddressTypeBo = new EntityAddressTypeBo(code: "addresscodeone");
        EntityAddressBo newEntityAddressBo = new EntityAddressBo(entityId: "AAA", entityTypeCode: "typecodeone", addressType: firstAddressTypeBo, addressTypeCode: "addresscodeone");
        EntityAddress entityAddress = identityService.addAddressToEntity(EntityAddressBo.to(newEntityAddressBo));
    }

    @Test
    public void testAddAddressToEntitySucceeds() {
        EntityAddressTypeBo firstAddressTypeBo = new EntityAddressTypeBo(code: "addresscodethree");
        EntityAddressBo newEntityAddressBo = new EntityAddressBo(entityId: "CCC", entityTypeCode: "typecodethree", addressType: firstAddressTypeBo, addressTypeCode: "addresscodethree");

        mockDataObjectService.demand.findMatching(1..sampleEntityAddresses.size()) {
            Class clazz, QueryByCriteria map -> return GenericQueryResults.Builder.create().build();
        }

        mockDataObjectService.demand.save(1..1) {
            EntityAddressBo bo, PersistenceOption... options -> return newEntityAddressBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityAddress entityAddress = identityService.addAddressToEntity(EntityAddressBo.to(newEntityAddressBo));

        Assert.assertEquals(EntityAddressBo.to(newEntityAddressBo), entityAddress);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateAddressWithNullFails() {
        EntityAddress entityAddress = identityService.updateAddress(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testUpdateAddressWithNonExistingAddressFails() {
        mockDataObjectService.demand.findMatching(1..sampleEntityAddresses.size()) {
            Class clazz, Map map -> for (EntityAddressBo entityAddressBo in sampleEntityAddresses.values()) {
                if (entityAddressBo.entityId.equals(map.get("entityId"))
                        && entityAddressBo.entityTypeCode.equals(map.get("entityTypeCode"))
                        && entityAddressBo.addressTypeCode.equals(map.get("addressTypeCode"))
                        && entityAddressBo.active)
                {
                    return entityAddressBo;
                }
            }

                return null;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityAddressTypeBo firstAddressTypeBo = new EntityAddressTypeBo(code: "addresscodethree");
        EntityAddressBo newEntityAddressBo = new EntityAddressBo(entityId: "CCC", entityTypeCode: "typecodethree", addressType: firstAddressTypeBo, addressTypeCode: "addresscodethree");
        EntityAddress entityAddress = identityService.updateAddress(EntityAddressBo.to(newEntityAddressBo));
    }

    @Test
    public void testUpdateAddressSucceeds() {
        EntityAddressTypeBo firstAddressTypeBo = new EntityAddressTypeBo(code: "addresscodeone");
        EntityAddressBo existingEntityAddressBo = new EntityAddressBo(entityId: "AAA", entityTypeCode: "typecodeone", id: "addressidone", addressType: firstAddressTypeBo, addressTypeCode: "addresscodeone");

        mockDataObjectService.demand.findMatching(1..sampleEntityAddresses.size()) {
            Class clazz, QueryByCriteria map -> for (EntityAddressBo entityAddressBo in sampleEntityAddresses.values()) {
                if (entityAddressBo.entityId.equals("AAA")
                        && entityAddressBo.entityTypeCode.equals("typecodeone")
                        && entityAddressBo.addressTypeCode.equals("addresscodeone")
                        && entityAddressBo.active)
                {
                    GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
                    builder.setResults(Collections.singletonList(entityAddressBo));
                    return builder.build();
                }
            }
        }

        mockDataObjectService.demand.save(1..1) {
            EntityAddressBo bo, PersistenceOption... options -> return existingEntityAddressBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityAddress entityAddress = identityService.updateAddress(EntityAddressBo.to(existingEntityAddressBo));

        Assert.assertEquals(EntityAddressBo.to(existingEntityAddressBo), entityAddress);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testInactivateAddressWithNonExistentAddressFails() {
        mockDataObjectService.demand.find(1..1) {
            Class clazz, String id -> return null;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityAddress entityAddress = identityService.inactivateAddress("new");
    }

    @Test
    public void testInactivateAddressSucceeds()
    {
        EntityAddressBo existingEntityAddressBo = sampleEntityAddresses.get("AAA");
        EntityAddressTypeBo firstAddressTypeBo = new EntityAddressTypeBo(code: "addresscodeone");
        EntityAddressBo inactiveEntityAddressBo = new EntityAddressBo(entityId: "AAA", entityTypeCode: "typecodeone", addressType: firstAddressTypeBo, id: "addressidone", addressTypeCode: "addresscodeone", active: false);

        mockDataObjectService.demand.find(1..sampleEntityAddresses.size()) {
            Class clazz, String id -> for (EntityAddressBo entityAddressBo in sampleEntityAddresses.values()) {
                if (entityAddressBo.id.equals(id)) {
                    return entityAddressBo;
                }
            }
        }

        mockDataObjectService.demand.save(1..1) {
            EntityAddressBo bo, PersistenceOption... options -> return inactiveEntityAddressBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityAddress inactiveEntityAddress = identityService.inactivateAddress(existingEntityAddressBo.id);

        Assert.assertEquals(EntityAddressBo.to(inactiveEntityAddressBo), inactiveEntityAddress);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testaddEmailToEntityWithNullFails() {
        EntityEmail entityEmail = identityService.addEmailToEntity(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testAddEmailToEntityWithExistingEmailFails() {
        mockDataObjectService.demand.findMatching(1..sampleEntityEmails.size()) {
            Class clazz, QueryByCriteria map -> for (EntityEmailBo entityEmailBo in sampleEntityEmails.values()) {
                if (entityEmailBo.entityId.equals("AAA")
                        && entityEmailBo.entityTypeCode.equals("typecodeone")
                        && entityEmailBo.emailTypeCode.equals("emailcodeone")
                        && entityEmailBo.active)
                {
                    GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
                    builder.setResults(Collections.singletonList(entityEmailBo));
                    return builder.build();
                }
            }
        }

        injectDataObjectServiceIntoIdentityService();

        EntityEmailTypeBo firstEmailTypeBo = new EntityEmailTypeBo(code: "emailcodeone");
        EntityEmailBo newEntityEmailBo = new EntityEmailBo(entityId: "AAA", entityTypeCode: "typecodeone", emailType: firstEmailTypeBo, emailTypeCode: "emailcodeone", active: true);
        EntityEmail entityEmail = identityService.addEmailToEntity(EntityEmailBo.to(newEntityEmailBo));
    }

    @Test
    public void testAddEmailToEntitySucceeds() {
        EntityEmailTypeBo firstEmailTypeBo = new EntityEmailTypeBo(code: "emailcodethree");
        EntityEmailBo newEntityEmailBo = new EntityEmailBo(entityId: "CCC", entityTypeCode: "typecodethree", emailType: firstEmailTypeBo, emailTypeCode: "emailcodethree", active: true);

        mockDataObjectService.demand.findMatching(1..sampleEntityEmails.size()) {
            Class clazz, QueryByCriteria map -> return GenericQueryResults.Builder.create().build();
        }

        mockDataObjectService.demand.save(1..1) {
            EntityEmailBo bo, PersistenceOption... options -> return newEntityEmailBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityEmail entityEmail = identityService.addEmailToEntity(EntityEmailBo.to(newEntityEmailBo));

        Assert.assertEquals(EntityEmailBo.to(newEntityEmailBo), entityEmail);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateEmailWithNullFails() {
        EntityEmail entityEmail = identityService.updateEmail(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testUpdateEmailWithNonExistingEmailFails() {
        mockDataObjectService.demand.findMatching(1..sampleEntityEmails.size()) {
            Class clazz, Map map -> for (EntityEmailBo entityEmailBo in sampleEntityEmails.values()) {
                if (entityEmailBo.entityId.equals(map.get("entityId"))
                        && entityEmailBo.entityTypeCode.equals(map.get("entityTypeCode"))
                        && entityEmailBo.emailTypeCode.equals(map.get("emailTypeCode"))
                        && entityEmailBo.active)
                {
                    return entityEmailBo;
                }
            }
        }

        injectDataObjectServiceIntoIdentityService();

        EntityEmailTypeBo firstEmailTypeBo = new EntityEmailTypeBo(code: "emailcodethree");
        EntityEmailBo newEntityEmailBo = new EntityEmailBo(entityId: "CCC", entityTypeCode: "typecodethree", emailType: firstEmailTypeBo, emailTypeCode: "emailcodethree", active: true);
        EntityEmail entityEmail = identityService.updateEmail(EntityEmailBo.to(newEntityEmailBo));
    }

    @Test
    public void testUpdateEmailSucceeds() {
        EntityEmailTypeBo firstEmailTypeBo = new EntityEmailTypeBo(code: "emailcodeone");
        EntityEmailBo existingEntityEmailBo = new EntityEmailBo(entityId: "AAA", entityTypeCode: "typecodeone", emailType: firstEmailTypeBo, id:"emailidone", emailTypeCode: "emailcodeone", active: true);

        mockDataObjectService.demand.findMatching(1..sampleEntityEmails.size()) {
            Class clazz, QueryByCriteria map -> for (EntityEmailBo entityEmailBo in sampleEntityEmails.values()) {
                if (entityEmailBo.entityId.equals("AAA")
                        && entityEmailBo.entityTypeCode.equals("typecodeone")
                        && entityEmailBo.emailTypeCode.equals("emailcodeone")
                        && entityEmailBo.active)
                {
                    GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
                    builder.setResults(Collections.singletonList(entityEmailBo));
                    return builder.build();
                }
            }
        }

        mockDataObjectService.demand.save(1..1) {
            EntityEmailBo bo, PersistenceOption... options -> return existingEntityEmailBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityEmail entityEmail = identityService.updateEmail(EntityEmailBo.to(existingEntityEmailBo));

        Assert.assertEquals(EntityEmailBo.to(existingEntityEmailBo), entityEmail);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testInactivateEmailWithNonExistentEmailFails() {
        mockDataObjectService.demand.find(1..1) {
            Class clazz, String id -> return null;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityEmail entityEmail = identityService.inactivateEmail("new");
    }

    @Test
    public void testInactivateEmailSucceeds()
    {
        EntityEmailBo existingEntityEmailBo = sampleEntityEmails.get("AAA");
        EntityEmailTypeBo firstEmailTypeBo = new EntityEmailTypeBo(code: "emailcodeone");
        EntityEmailBo inactiveEntityEmailBo = new EntityEmailBo(entityId: "AAA", entityTypeCode: "typecodeone", emailType: firstEmailTypeBo, id:"emailidone", emailTypeCode: "emailcodeone", active: false);


        mockDataObjectService.demand.find(1..sampleEntityEmails.size()) {
            Class clazz, String id -> for (EntityEmailBo entityEmailBo in sampleEntityEmails.values()) {
                if (entityEmailBo.id.equals(id)) {
                    return entityEmailBo;
                }
            }
        }

        mockDataObjectService.demand.save(1..1) {
            EntityEmailBo bo, PersistenceOption... options -> return inactiveEntityEmailBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityEmail inactiveEntityEmail = identityService.inactivateEmail(existingEntityEmailBo.id);

        Assert.assertEquals(EntityEmailBo.to(inactiveEntityEmailBo), inactiveEntityEmail);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testaddPhoneToEntityWithNullFails() {
        EntityPhone entityPhone = identityService.addPhoneToEntity(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testAddPhoneToEntityWithExistingPhoneFails() {
        mockDataObjectService.demand.findMatching(1..sampleEntityPhones.size()) {
            Class clazz, QueryByCriteria map -> for (EntityPhoneBo entityPhoneBo in sampleEntityPhones.values()) {
                if (entityPhoneBo.entityId.equals("AAA")
                        && entityPhoneBo.entityTypeCode.equals("typecodeone")
                        && entityPhoneBo.phoneTypeCode.equals("phonetypecodeone")
                        && entityPhoneBo.active)
                {
                    GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
                    builder.setResults(Collections.singletonList(entityPhoneBo));
                    return builder.build();
                }
            }
        }

        injectDataObjectServiceIntoIdentityService();

        EntityPhoneTypeBo firstPhoneType = new EntityPhoneTypeBo(code: "phonetypecodeone");
        EntityPhoneBo newEntityPhoneBo = new EntityPhoneBo(entityId: "AAA", entityTypeCode: "typecodeone", phoneType: firstPhoneType, id: "phoneidone", phoneTypeCode: "phonetypecodeone", active: true);
        EntityPhone entityPhone = identityService.addPhoneToEntity(EntityPhoneBo.to(newEntityPhoneBo));
    }

    @Test
    public void testAddPhoneToEntitySucceeds() {
        EntityPhoneTypeBo firstPhoneType = new EntityPhoneTypeBo(code: "phonetypecodethree");
        EntityPhoneBo newEntityPhoneBo = new EntityPhoneBo(entityId: "CCC", entityTypeCode: "typecodethree", phoneType: firstPhoneType, id: "phoneidthree", phoneTypeCode: "phonetypecodethree", active: true);

        mockDataObjectService.demand.findMatching(1..sampleEntityPhones.size()) {
            Class clazz, QueryByCriteria map -> return GenericQueryResults.Builder.create().build();
        }

        mockDataObjectService.demand.save(1..1) {
            EntityPhoneBo bo, PersistenceOption... options -> return newEntityPhoneBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityPhone entityPhone = identityService.addPhoneToEntity(EntityPhoneBo.to(newEntityPhoneBo));

        Assert.assertEquals(EntityPhoneBo.to(newEntityPhoneBo), entityPhone);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdatePhoneWithNullFails() {
        EntityPhone entityPhone = identityService.updatePhone(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testUpdatePhoneWithNonExistingPhoneFails() {
        mockDataObjectService.demand.findMatching(1..sampleEntityPhones.size()) {
            Class clazz, QueryByCriteria map -> return GenericQueryResults.Builder.create().build();
        }

        injectDataObjectServiceIntoIdentityService();

        EntityPhoneTypeBo firstPhoneType = new EntityPhoneTypeBo(code: "phonetypecodethree");
        EntityPhoneBo newEntityPhoneBo = new EntityPhoneBo(entityId: "CCC", entityTypeCode: "typecodethree", phoneType: firstPhoneType, id: "phoneidthree", phoneTypeCode: "phonetypecodethree", active: true);
        EntityPhone entityPhone = identityService.updatePhone(EntityPhoneBo.to(newEntityPhoneBo));
    }

    @Test
    public void testUpdatePhoneSucceeds() {
        EntityPhoneTypeBo firstPhoneType = new EntityPhoneTypeBo(code: "phonetypecodeone");
        EntityPhoneBo existingEntityPhoneBo = new EntityPhoneBo(entityId: "AAA", entityTypeCode: "typecodeone", phoneType: firstPhoneType, id: "phoneidone", phoneTypeCode: "phonetypecodeone", active: true);

        mockDataObjectService.demand.findMatching(1..sampleEntityPhones.size()) {
            Class clazz, QueryByCriteria map -> for (EntityPhoneBo entityPhoneBo in sampleEntityPhones.values()) {
                if (entityPhoneBo.entityId.equals("AAA")
                        && entityPhoneBo.entityTypeCode.equals("typecodeone")
                        && entityPhoneBo.phoneTypeCode.equals("phonetypecodeone")
                        && entityPhoneBo.active)
                {
                    GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
                    builder.setResults(Collections.singletonList(entityPhoneBo));
                    return builder.build();
                }
            }
        }

        mockDataObjectService.demand.save(1..1) {
            EntityPhoneBo bo, PersistenceOption... options -> return existingEntityPhoneBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityPhone entityPhone = identityService.updatePhone(EntityPhoneBo.to(existingEntityPhoneBo));

        Assert.assertEquals(EntityPhoneBo.to(existingEntityPhoneBo), entityPhone);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testInactivatePhoneWithNonExistentPhoneFails() {
        mockDataObjectService.demand.find(1..1) {
            Class clazz, String id -> return null;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityPhone entityPhone = identityService.inactivatePhone("new");
    }

    @Test
    public void testInactivatePhoneSucceeds()
    {
        EntityPhoneBo existingEntityPhoneBo = sampleEntityPhones.get("AAA");
        EntityPhoneTypeBo firstPhoneType = new EntityPhoneTypeBo(code: "phonetypecodeone");
        EntityPhoneBo inactiveEntityPhoneBo = new EntityPhoneBo(entityId: "AAA", entityTypeCode: "typecodeone", phoneType: firstPhoneType, id: "phoneidone", phoneTypeCode: "phonetypecodeone", active: false);

        mockDataObjectService.demand.find(1..sampleEntityPhones.size()) {
            Class clazz, String id -> for (EntityPhoneBo entityPhoneBo in sampleEntityPhones.values()) {
                if (entityPhoneBo.id.equals(id)) {
                    return entityPhoneBo;
                }
            }
        }

        mockDataObjectService.demand.save(1..1) {
            EntityPhoneBo bo, PersistenceOption... options -> return inactiveEntityPhoneBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityPhone inactiveEntityPhone = identityService.inactivatePhone(existingEntityPhoneBo.id);

        Assert.assertEquals(EntityPhoneBo.to(inactiveEntityPhoneBo), inactiveEntityPhone);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testaddExternalIdentifierToEntityWithNullFails() {
        EntityExternalIdentifier entityExternalIdentifier = identityService.addExternalIdentifierToEntity(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testAddExternalIdentifierToEntityWithExistingObjectFails() {
        mockDataObjectService.demand.findMatching(1..sampleEntityExternalIdentifiers.size()) {
            Class clazz, QueryByCriteria map -> for (EntityExternalIdentifierBo entityExternalIdentifierBo in sampleEntityExternalIdentifiers.values()) {
                if (entityExternalIdentifierBo.entityId.equals("AAA")
                        && entityExternalIdentifierBo.externalIdentifierTypeCode.equals("exidtypecodeone"))
                {
                    GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
                    builder.setResults(Collections.singletonList(entityExternalIdentifierBo));
                    return builder.build();
                }
            }
            return GenericQueryResults.Builder.create().build();
        }

        injectDataObjectServiceIntoIdentityService();

        EntityExternalIdentifierTypeBo firstExternalIdentifierType = new EntityExternalIdentifierTypeBo(code: "exidtypecodeone");
        EntityExternalIdentifierBo newEntityExternalIdentifierBo = new EntityExternalIdentifierBo(entityId: "AAA", externalIdentifierType: firstExternalIdentifierType, id: "exidone", externalIdentifierTypeCode: "exidtypecodeone");
        EntityExternalIdentifier entityExternalIdentifier = identityService.addExternalIdentifierToEntity(EntityExternalIdentifierBo.to(newEntityExternalIdentifierBo));
    }

    @Test
    public void testAddExternalIdentifierToEntitySucceeds() {
        EntityExternalIdentifierTypeBo firstExternalIdentifierType = new EntityExternalIdentifierTypeBo(code: "exidtypecodethree");
        EntityExternalIdentifierBo newEntityExternalIdentifierBo = new EntityExternalIdentifierBo(entityId: "CCC", externalIdentifierType: firstExternalIdentifierType, id: "exidthree", externalIdentifierTypeCode: "exidtypecodethree");

        mockDataObjectService.demand.findMatching(1..sampleEntityExternalIdentifiers.size()) {
            Class clazz, QueryByCriteria map -> for (EntityExternalIdentifierBo entityExternalIdentifierBo in sampleEntityExternalIdentifiers.values()) {
                if (entityExternalIdentifierBo.entityId.equals("CCC")
                        && entityExternalIdentifierBo.externalIdentifierTypeCode.equals("exidtypecodethree"))
                {
                    GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
                    builder.setResults(Collections.singletonList(entityExternalIdentifierBo));
                    return builder.build();
                }
            }
            return GenericQueryResults.Builder.create().build();
        }

        mockDataObjectService.demand.save(1..1) {
            EntityExternalIdentifierBo bo, PersistenceOption... options -> return newEntityExternalIdentifierBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityExternalIdentifier entityExternalIdentifier = identityService.addExternalIdentifierToEntity(EntityExternalIdentifierBo.to(newEntityExternalIdentifierBo));

        Assert.assertEquals(EntityExternalIdentifierBo.to(newEntityExternalIdentifierBo), entityExternalIdentifier);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateExternalIdentifierWithNullFails() {
        EntityExternalIdentifier entityExternalIdentifier = identityService.updateExternalIdentifier(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testUpdateExternalIdentifierWithNonExistingObjectFails() {
        mockDataObjectService.demand.findMatching(1..sampleEntityExternalIdentifiers.size()) {
            Class clazz, QueryByCriteria map -> return GenericQueryResults.Builder.create().build();
        }

        injectDataObjectServiceIntoIdentityService();

        EntityExternalIdentifierTypeBo firstExternalIdentifierType = new EntityExternalIdentifierTypeBo(code: "exidtypecodethree");
        EntityExternalIdentifierBo newEntityExternalIdentifierBo = new EntityExternalIdentifierBo(entityId: "CCC", externalIdentifierType: firstExternalIdentifierType, id: "exidthree", externalIdentifierTypeCode: "exidtypecodethree");
        EntityExternalIdentifier entityExternalIdentifier = identityService.updateExternalIdentifier(EntityExternalIdentifierBo.to(newEntityExternalIdentifierBo));
    }

    @Test
    public void testUpdateExternalIdentifierSucceeds() {
        EntityExternalIdentifierTypeBo firstExternalIdentifierType = new EntityExternalIdentifierTypeBo(code: "exidtypecodeone");
        EntityExternalIdentifierBo existingEntityExternalIdentifierBo = new EntityExternalIdentifierBo(entityId: "AAA", externalIdentifierType: firstExternalIdentifierType, id: "exidone", externalIdentifierTypeCode: "exidtypecodeone");

        mockDataObjectService.demand.findMatching(1..sampleEntityExternalIdentifiers.size()) {
            Class clazz, QueryByCriteria map -> for (EntityExternalIdentifierBo entityExternalIdentifierBo in sampleEntityExternalIdentifiers.values()) {
                if (entityExternalIdentifierBo.entityId.equals("AAA")
                        && entityExternalIdentifierBo.externalIdentifierTypeCode.equals("exidtypecodeone"))
                {
                    GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
                    builder.setResults(Collections.singletonList(entityExternalIdentifierBo));
                    return builder.build();
                }
            }
        }

        mockDataObjectService.demand.save(1..1) {
            EntityExternalIdentifierBo bo, PersistenceOption... options -> return existingEntityExternalIdentifierBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityExternalIdentifier entityExternalIdentifier = identityService.updateExternalIdentifier(EntityExternalIdentifierBo.to(existingEntityExternalIdentifierBo));

        Assert.assertEquals(EntityExternalIdentifierBo.to(existingEntityExternalIdentifierBo), entityExternalIdentifier);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testaddAffiliationToEntityWithNullFails() {
        EntityAffiliation entityAffiliation = identityService.addAffiliationToEntity(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testaddAffiliationToEntityWithExistingObjectFails() {
        mockDataObjectService.demand.find(1..sampleEntityAffiliations.size()) {
            Class clazz, String id -> for (EntityAffiliationBo entityAffiliationBo in sampleEntityAffiliations.values()) {
                if (entityAffiliationBo.id.equals(id))
                {
                    return entityAffiliationBo;
                }
            }
        }

        injectDataObjectServiceIntoIdentityService();

        EntityAffiliationTypeBo firstAffiliationType = new EntityAffiliationTypeBo(code: "affiliationcodeone");
        EntityAffiliationBo newEntityAffiliationBo = new EntityAffiliationBo(entityId: "AAA", affiliationType: firstAffiliationType, id: "affiliationidone", affiliationTypeCode: "affiliationcodeone", active: true);
        EntityAffiliation entityAffiliation = identityService.addAffiliationToEntity(EntityAffiliationBo.to(newEntityAffiliationBo));
    }

    @Test
    public void testAddAffiliationToEntitySucceeds() {
        EntityAffiliationTypeBo firstAffiliationType = new EntityAffiliationTypeBo(code: "affiliationcodethree");
        EntityAffiliationBo newEntityAffiliationBo = new EntityAffiliationBo(entityId: "CCC", affiliationType: firstAffiliationType, id: "affiliationidthree", affiliationTypeCode: "affiliationcodethree", active: true);

        mockDataObjectService.demand.find(1..sampleEntityAffiliations.size()) {
            Class clazz, String id -> for (EntityAffiliationBo entityAffiliationBo in sampleEntityAffiliations.values()) {
                if (entityAffiliationBo.id.equals(id))
                {
                    return entityAffiliationBo;
                }
            }
        }

        mockDataObjectService.demand.save(1..1) {
            EntityAffiliationBo bo, PersistenceOption... options -> return newEntityAffiliationBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityAffiliation entityAffiliation = identityService.addAffiliationToEntity(EntityAffiliationBo.to(newEntityAffiliationBo));

        Assert.assertEquals(EntityAffiliationBo.to(newEntityAffiliationBo), entityAffiliation);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateAffiliationWithNullFails() {
        EntityAffiliation entityAffiliation = identityService.updateAffiliation(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testUpdateAffiliationWithNonExistingObjectFails() {
        mockDataObjectService.demand.find(1..sampleEntityAffiliations.size()) {
            Class clazz, String id -> for (EntityAffiliationBo entityAffiliationBo in sampleEntityAffiliations.values()) {
                if (entityAffiliationBo.id.equals(id))
                {
                    return entityAffiliationBo;
                }
            }
        }

        injectDataObjectServiceIntoIdentityService();

        EntityAffiliationTypeBo firstAffiliationType = new EntityAffiliationTypeBo(code: "affiliationcodethree");
        EntityAffiliationBo newEntityAffiliationBo = new EntityAffiliationBo(entityId: "CCC", affiliationType: firstAffiliationType, id: "affiliationidthree", affiliationTypeCode: "affiliationcodethree", active: true);
        EntityAffiliation entityAffiliation = identityService.updateAffiliation(EntityAffiliationBo.to(newEntityAffiliationBo));
    }

    @Test
    public void testUpdateAffiliationSucceeds() {
        EntityAffiliationTypeBo firstAffiliationType = new EntityAffiliationTypeBo(code: "affiliationcodeone");
        EntityAffiliationBo existingEntityAffiliationBo = new EntityAffiliationBo(entityId: "AAA", affiliationType: firstAffiliationType, id: "affiliationidone", affiliationTypeCode: "affiliationcodeone", active: true);

        mockDataObjectService.demand.find(1..sampleEntityAffiliations.size()) {
            Class clazz, String id -> for (EntityAffiliationBo entityAffiliationBo in sampleEntityAffiliations.values()) {
                if (entityAffiliationBo.id.equals(id))
                {
                    return entityAffiliationBo;
                }
            }
        }

        mockDataObjectService.demand.save(1..1) {
            EntityAffiliationBo bo, PersistenceOption... options -> return existingEntityAffiliationBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityAffiliation entityAffiliation = identityService.updateAffiliation(EntityAffiliationBo.to(existingEntityAffiliationBo));

        Assert.assertEquals(EntityAffiliationBo.to(existingEntityAffiliationBo), entityAffiliation);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testInactivateAffiliationWithNonExistentIdFails() {
        mockDataObjectService.demand.find(1..1) {
            Class clazz, String id -> return null;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityAffiliation entityAffiliation = identityService.inactivateAffiliation("new");
    }

    @Test
    public void testInactivateAffiliationSucceeds()
    {

        EntityAffiliationBo existingEntityAffiliationBo = sampleEntityAffiliations.get("AAA");
        EntityAffiliationTypeBo firstAffiliationType = new EntityAffiliationTypeBo(code: "affiliationcodeone");
        EntityAffiliationBo inactiveEntityAffiliationBo = new EntityAffiliationBo(entityId: "AAA", affiliationType: firstAffiliationType, id: "affiliationidone", affiliationTypeCode: "affiliationcodeone", active: false);

        mockDataObjectService.demand.find(1..sampleEntityAffiliations.size()) {
            Class clazz, String id -> for (EntityAffiliationBo entityAffiliationBo in sampleEntityAffiliations.values()) {
                if (entityAffiliationBo.id.equals(id))
                {
                    return entityAffiliationBo;
                }
            }
        }

        mockDataObjectService.demand.save(1..1) {
            EntityAffiliationBo bo, PersistenceOption... options -> return inactiveEntityAffiliationBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityAffiliation inactiveEntityAffiliation = identityService.inactivateAffiliation(existingEntityAffiliationBo.id);

        Assert.assertEquals(EntityAffiliationBo.to(inactiveEntityAffiliationBo), inactiveEntityAffiliation);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindEntitiesWithNullFails() {
        EntityQueryResults entityQueryResults = identityService.findEntities(null);
    }

    @Test
    public void testFindEntitiesSucceeds() {
        GenericQueryResults.Builder<EntityBo> genericQueryResults = new GenericQueryResults.Builder<EntityBo>();
        genericQueryResults.totalRowCount = 0;
        genericQueryResults.moreResultsAvailable = false;
        List<EntityBo> entities = new ArrayList<EntityBo>();
        entities.add(new EntityBo(active: true, id: "AAA"));
        genericQueryResults.results = entities;
        GenericQueryResults<EntityBo> results = genericQueryResults.build();

        mockDataObjectService.demand.findMatching(1..1) {
            Class<EntityBo> queryClass, QueryByCriteria criteria -> return results;
        }

        injectDataObjectServiceIntoIdentityService();

        QueryByCriteria.Builder queryByCriteriaBuilder = new QueryByCriteria.Builder();
        queryByCriteriaBuilder.setStartAtIndex(0);
        queryByCriteriaBuilder.setCountFlag(CountFlag.NONE);
        EqualPredicate equalExpression = new EqualPredicate("entity.entityId", new CriteriaStringValue("AAA"));
        queryByCriteriaBuilder.setPredicates(equalExpression);
        EntityQueryResults entityQueryResults = identityService.findEntities(queryByCriteriaBuilder.build());

        Assert.assertEquals(entityQueryResults.results[0], EntityBo.to(entities[0]));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindEntityDefaultsWithNullFails() {
        EntityDefaultQueryResults entityDefaultQueryResults = identityService.findEntityDefaults(null);
    }

    @Test
    public void testFindEntityDefaultsSucceeds() {
        EntityDefault.Builder entityBuilder = EntityDefault.Builder.create("AAA");
        GenericQueryResults.Builder<EntityBo> genericQueryResults = new GenericQueryResults.Builder<EntityBo>();
        genericQueryResults.totalRowCount = 0;
        genericQueryResults.moreResultsAvailable = false;
        List<EntityBo> entities = new ArrayList<EntityBo>();
        entities.add(new EntityBo(active: true, id: "AAA"));
        genericQueryResults.results = entities;
        GenericQueryResults<EntityBo> results = genericQueryResults.build();

        mockDataObjectService.demand.findMatching(1..1) {
            Class<EntityBo> queryClass, QueryByCriteria criteria -> return results;
        }

        injectDataObjectServiceIntoIdentityService();

        QueryByCriteria.Builder queryByCriteriaBuilder = new QueryByCriteria.Builder();
        queryByCriteriaBuilder.setStartAtIndex(0);
        queryByCriteriaBuilder.setCountFlag(CountFlag.NONE);
        EqualPredicate equalExpression = new EqualPredicate("entity.entityId", new CriteriaStringValue("AAA"));
        queryByCriteriaBuilder.setPredicates(equalExpression);
        EntityDefaultQueryResults entityDefaultQueryResults = identityService.findEntityDefaults(queryByCriteriaBuilder.build());

        // because findEntityDefaults builds the EntityDefault list from the results, we cannot compare entityBuilder.build() in its entirety to the results in their entirety
        Assert.assertEquals(entityDefaultQueryResults.results[0].entityId, entityBuilder.build().entityId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindNamesWithNullFails() {
        EntityNameQueryResults entityNameQueryResults = identityService.findNames(null);
    }

    @Test
    public void testFindNamesSucceeds() {
        EntityName.Builder entityBuilder = EntityName.Builder.create();
        entityBuilder.setFirstName("John");
        entityBuilder.setLastName("Smith");
        GenericQueryResults.Builder<EntityNameBo> genericQueryResults = new GenericQueryResults.Builder<EntityNameBo>();
        genericQueryResults.totalRowCount = 0;
        genericQueryResults.moreResultsAvailable = false;
        List<EntityNameBo> entityNames = new ArrayList<EntityNameBo>();
        entityNames.add(new EntityNameBo(firstName: "John", lastName: "Smith"));
        genericQueryResults.results = entityNames;
        GenericQueryResults<EntityNameBo> results = genericQueryResults.build();

        mockDataObjectService.demand.findMatching(1..1) {
            Class<EntityNameBo> queryClass, QueryByCriteria criteria -> return results;
        }

        injectDataObjectServiceIntoIdentityService();

        QueryByCriteria.Builder queryByCriteriaBuilder = new QueryByCriteria.Builder();
        queryByCriteriaBuilder.setStartAtIndex(0);
        queryByCriteriaBuilder.setCountFlag(CountFlag.NONE);
        EqualPredicate equalExpression = new EqualPredicate("entityName.lastName", new CriteriaStringValue("Smith"));
        queryByCriteriaBuilder.setPredicates(equalExpression);
        EntityNameQueryResults entityNameQueryResults = identityService.findNames(queryByCriteriaBuilder.build());

        Assert.assertEquals(entityNameQueryResults.results[0], entityBuilder.build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateEntityWithNullFails() {
        Entity entity = identityService.createEntity(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testCreateEntityWithExistingObjectFails() {
        mockDataObjectService.demand.find(1..sampleEntities.size()) {
            Class clazz, String id -> return sampleEntities.get(id);
        }

        injectDataObjectServiceIntoIdentityService();

        EntityPrivacyPreferencesBo firstEntityPrivacyPreferencesBo = new EntityPrivacyPreferencesBo(entityId: "AAA", suppressName: true, suppressEmail: true, suppressAddress: true, suppressPhone: true, suppressPersonal: false);
        String birthDateString = "01/01/2007";
        String deceasedDateString = "01/01/2087";
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date birthDate = formatter.parse(birthDateString);
        Date deceasedDate = formatter.parse(deceasedDateString);
        EntityBioDemographicsBo firstEntityBioDemographicsBo = new EntityBioDemographicsBo(entityId: "AAA", birthDateValue: birthDate, genderCode: "M", deceasedDateValue: deceasedDate, maritalStatusCode: "S", primaryLanguageCode: "EN", secondaryLanguageCode: "FR", birthCountry: "US", birthStateProvinceCode: "IN", birthCity: "Bloomington", geographicOrigin: "None", suppressPersonal: false);
        PrincipalBo firstEntityPrincipal = new PrincipalBo(entityId: "AAA", principalId: "P1", active: true, principalName: "first", versionNumber: 1, password: "first_password");
        List<PrincipalBo> firstPrincipals = new ArrayList<PrincipalBo>();
        firstPrincipals.add(firstEntityPrincipal);
        EntityBo newEntityBo = new EntityBo(active: true, id: "AAA", privacyPreferences: firstEntityPrivacyPreferencesBo, bioDemographics: firstEntityBioDemographicsBo, principals: firstPrincipals);
        Entity entity = identityService.createEntity(EntityBo.to(newEntityBo));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateEntityWithNullFails() {
        Entity entity = identityService.updateEntity(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testUpdateEntityWithNonExistingObjectFails() {
        mockDataObjectService.demand.find(1..sampleEntities.size()) {
            Class clazz, String id -> return sampleEntities.get(id);
        }

        injectDataObjectServiceIntoIdentityService();

        EntityBo newEntityBo = new EntityBo(active: true, id: "CCC");
        Entity entity = identityService.updateEntity(EntityBo.to(newEntityBo));
    }

    @Test
    public void testUpdateEntitySucceeds() {
        EntityPrivacyPreferencesBo firstEntityPrivacyPreferencesBo = new EntityPrivacyPreferencesBo(entityId: "AAA", suppressName: true, suppressEmail: true, suppressAddress: true, suppressPhone: true, suppressPersonal: false);
        String birthDateString = "01/01/2007";
        String deceasedDateString = "01/01/2087";
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date birthDate = formatter.parse(birthDateString);
        Date deceasedDate = formatter.parse(deceasedDateString);
        EntityBioDemographicsBo firstEntityBioDemographicsBo = new EntityBioDemographicsBo(entityId: "AAA", birthDateValue: birthDate, genderCode: "M", deceasedDateValue: deceasedDate, maritalStatusCode: "S", primaryLanguageCode: "EN", secondaryLanguageCode: "FR", birthCountry: "US", birthStateProvinceCode: "IN", birthCity: "Bloomington", geographicOrigin: "None", suppressPersonal: false);
        PrincipalBo firstEntityPrincipal = new PrincipalBo(entityId: "AAA", principalId: "P1", active: true, principalName: "first", versionNumber: 1, password: "first_password");
        List<PrincipalBo> firstPrincipals = new ArrayList<PrincipalBo>();
        firstPrincipals.add(firstEntityPrincipal);
        EntityBo existingEntityBo = new EntityBo(active: true, id: "AAA", privacyPreferences: firstEntityPrivacyPreferencesBo, bioDemographics: firstEntityBioDemographicsBo, principals: firstPrincipals);

        mockDataObjectService.demand.find(1..sampleEntities.size()) {
            Class clazz, String id -> return sampleEntities.get(id);
        }

        mockDataObjectService.demand.save(1..1) {
            EntityBo bo, PersistenceOption... options -> return existingEntityBo;
        }

        injectDataObjectServiceIntoIdentityService();

        Entity entity = identityService.updateEntity(EntityBo.to(existingEntityBo));

        Assert.assertEquals(EntityBo.to(existingEntityBo), entity);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testInactivateEntityWithNonExistentEntityFails() {
        mockDataObjectService.demand.find(1..1) {
            Class clazz, String id -> return null;
        }

        injectDataObjectServiceIntoIdentityService();

        Entity entity = identityService.inactivateEntity("new");
    }

    @Test
    public void testInactivateEntitySucceeds()
    {
        EntityBo existingEntityBo = sampleEntities.get("AAA");
        EntityPrivacyPreferencesBo firstEntityPrivacyPreferencesBo = new EntityPrivacyPreferencesBo(entityId: "AAA", suppressName: true, suppressEmail: true, suppressAddress: true, suppressPhone: true, suppressPersonal: false);
        String birthDateString = "01/01/2007";
        String deceasedDateString = "01/01/2087";
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date birthDate = formatter.parse(birthDateString);
        Date deceasedDate = formatter.parse(deceasedDateString);
        EntityBioDemographicsBo firstEntityBioDemographicsBo = new EntityBioDemographicsBo(entityId: "AAA", birthDateValue: birthDate, genderCode: "M", deceasedDateValue: deceasedDate, maritalStatusCode: "S", primaryLanguageCode: "EN", secondaryLanguageCode: "FR", birthCountry: "US", birthStateProvinceCode: "IN", birthCity: "Bloomington", geographicOrigin: "None", suppressPersonal: false);
        PrincipalBo firstEntityPrincipal = new PrincipalBo(entityId: "AAA", principalId: "P1", active: true, principalName: "first", versionNumber: 1, password: "first_password");
        List<PrincipalBo> firstPrincipals = new ArrayList<PrincipalBo>();
        firstPrincipals.add(firstEntityPrincipal);
        EntityBo inactiveEntityBo = new EntityBo(active: false, id: "AAA", privacyPreferences: firstEntityPrivacyPreferencesBo, bioDemographics: firstEntityBioDemographicsBo, principals: firstPrincipals);
        mockDataObjectService.demand.find(1..sampleEntities.size()) {
            Class clazz, String id -> return sampleEntities.get(id);
        }

        mockDataObjectService.demand.save(1..1) {
            EntityBo bo, PersistenceOption... options -> return inactiveEntityBo;
        }

        injectDataObjectServiceIntoIdentityService();

        Entity inactiveEntity = identityService.inactivateEntity(existingEntityBo.id);

        Assert.assertEquals(EntityBo.to(inactiveEntityBo), inactiveEntity);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddPrivacyPreferencesToEntityWithNullFails() {
        EntityPrivacyPreferences entityPrivacyPreferences = identityService.addPrivacyPreferencesToEntity(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testAddPrivacyPreferencesToEntityWithExistingObjectFails() {
        mockDataObjectService.demand.find(1..sampleEntityPrivacyPreferences.size()) {
            Class clazz, String id -> return sampleEntityPrivacyPreferences.get(id);
        }

        injectDataObjectServiceIntoIdentityService();

        EntityPrivacyPreferencesBo newEntityPrivacyPreferencesBo = new EntityPrivacyPreferencesBo(entityId: "AAA", suppressName: true, suppressEmail: true, suppressAddress: true, suppressPhone: true, suppressPersonal: false);
        EntityPrivacyPreferences entityPrivacyPreferences = identityService.addPrivacyPreferencesToEntity(EntityPrivacyPreferencesBo.to(newEntityPrivacyPreferencesBo));
    }

    @Test
    public void testAddPrivacyPreferencesToEntitySucceeds() {
        EntityPrivacyPreferencesBo newEntityPrivacyPreferencesBo = new EntityPrivacyPreferencesBo(entityId: "CCC", suppressName: true, suppressEmail: true, suppressAddress: true, suppressPhone: true, suppressPersonal: false);

        mockDataObjectService.demand.find(1..sampleEntityPrivacyPreferences.size()) {
            Class clazz, String id -> return sampleEntityPrivacyPreferences.get(id);
        }

        mockDataObjectService.demand.save(1..1) {
            EntityPrivacyPreferencesBo bo, PersistenceOption... options -> return newEntityPrivacyPreferencesBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityPrivacyPreferences entityPrivacyPreferences = identityService.addPrivacyPreferencesToEntity(EntityPrivacyPreferencesBo.to(newEntityPrivacyPreferencesBo));

        Assert.assertEquals(EntityPrivacyPreferencesBo.to(newEntityPrivacyPreferencesBo), entityPrivacyPreferences);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdatePrivacyPreferencesWithNullFails() {
        EntityPrivacyPreferences entityPrivacyPreferences = identityService.updatePrivacyPreferences(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testUpdatePrivacyPreferencesWithNonExistingObjectFails() {
        mockDataObjectService.demand.find(1..sampleEntityPrivacyPreferences.size()) {
            Class clazz, String id -> return sampleEntityPrivacyPreferences.get(id);
        }

        injectDataObjectServiceIntoIdentityService();

        EntityPrivacyPreferencesBo newEntityPrivacyPreferencesBo = new EntityPrivacyPreferencesBo(entityId: "CCC", suppressName: true, suppressEmail: true, suppressAddress: true, suppressPhone: true, suppressPersonal: false);
        EntityPrivacyPreferences entityPrivacyPreferences = identityService.updatePrivacyPreferences(EntityPrivacyPreferencesBo.to(newEntityPrivacyPreferencesBo));
    }

    @Test
    public void testUpdatePrivacyPreferencesSucceeds() {
        EntityPrivacyPreferencesBo existingEntityPrivacyPreferencesBo = new EntityPrivacyPreferencesBo(entityId: "AAA", suppressName: true, suppressEmail: true, suppressAddress: true, suppressPhone: true, suppressPersonal: false);

        mockDataObjectService.demand.find(1..sampleEntityPrivacyPreferences.size()) {
            Class clazz, String id -> return sampleEntityPrivacyPreferences.get(id);
        }

        mockDataObjectService.demand.save(1..1) {
            EntityPrivacyPreferencesBo bo, PersistenceOption... options -> return existingEntityPrivacyPreferencesBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityPrivacyPreferences entityPrivacyPreferences = identityService.updatePrivacyPreferences(EntityPrivacyPreferencesBo.to(existingEntityPrivacyPreferencesBo));

        Assert.assertEquals(EntityPrivacyPreferencesBo.to(existingEntityPrivacyPreferencesBo), entityPrivacyPreferences);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddCitizenshipToEntityWithNullFails() {
        EntityCitizenship entityCitizenship = identityService.addCitizenshipToEntity(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testAddCitizenshipToEntityWithExistingObjectFails() {
        mockDataObjectService.demand.findMatching(1..sampleEntityCitizenships.size()) {
            Class clazz, QueryByCriteria map -> for (EntityCitizenshipBo entityCitizenshipBo in sampleEntityCitizenships.values()) {
                if (entityCitizenshipBo.entityId.equals("AAA")
                        && entityCitizenshipBo.statusCode.equals("statuscodeone")
                        && entityCitizenshipBo.active)
                {
                    GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
                    builder.setResults(Collections.singletonList(entityCitizenshipBo));
                    return builder.build();
                }
            }
        }

        injectDataObjectServiceIntoIdentityService();

        EntityCitizenshipStatusBo firstEntityCitizenshipStatus = new EntityCitizenshipStatusBo(code: "statuscodeone", name: "statusnameone");
        EntityCitizenshipBo newEntityCitizenshipBo = new EntityCitizenshipBo(entityId: "AAA", id: "citizenshipidone", active: true, status: firstEntityCitizenshipStatus, statusCode: "statuscodeone");
        EntityCitizenship entityCitizenship = identityService.addCitizenshipToEntity(EntityCitizenshipBo.to(newEntityCitizenshipBo));
    }

    @Test
    public void testAddCitizenshipToEntitySucceeds() {
        EntityCitizenshipStatusBo firstEntityCitizenshipStatus = new EntityCitizenshipStatusBo(code: "statuscodeone", name: "statusnameone");
        EntityCitizenshipBo newEntityCitizenshipBo = new EntityCitizenshipBo(entityId: "CCC", id: "citizenshipidthree", active: true, status: firstEntityCitizenshipStatus, statusCode: "statuscodeone");

        mockDataObjectService.demand.findMatching(1..sampleEntityCitizenships.size()) {
            Class clazz, QueryByCriteria map -> return GenericQueryResults.Builder.create().build();
        }

        mockDataObjectService.demand.save(1..1) {
            EntityCitizenshipBo bo, PersistenceOption... options -> return newEntityCitizenshipBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityCitizenship entityCitizenship = identityService.addCitizenshipToEntity(EntityCitizenshipBo.to(newEntityCitizenshipBo));

        Assert.assertEquals(EntityCitizenshipBo.to(newEntityCitizenshipBo), entityCitizenship);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateCitizenshipWithNullFails() {
        EntityCitizenship entityCitizenship = identityService.updateCitizenship(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testUpdateCitizenshipWithNonExistingObjectFails() {
        mockDataObjectService.demand.findMatching(1..sampleEntityCitizenships.size()) {
            Class clazz, QueryByCriteria id -> for (EntityCitizenshipBo entityCitizenshipBo in sampleEntityCitizenships.values()) {
                if (entityCitizenshipBo.entityId.equals("CCC")
                        && entityCitizenshipBo.statusCode.equals("statuscodeone")
                        && entityCitizenshipBo.active)
                {
                    GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
                    builder.setResults(Collections.singletonList(entityCitizenshipBo));
                    return builder.build();
                }
            }
            return GenericQueryResults.Builder.create().build()
        }

        injectDataObjectServiceIntoIdentityService();

        EntityCitizenshipStatusBo firstEntityCitizenshipStatus = new EntityCitizenshipStatusBo(code: "statuscodeone", name: "statusnameone");
        EntityCitizenshipBo newEntityCitizenshipBo = new EntityCitizenshipBo(entityId: "CCC", id: "citizenshipidthree", active: true, status: firstEntityCitizenshipStatus, statusCode: "statuscodeone");
        EntityCitizenship entityCitizenship = identityService.updateCitizenship(EntityCitizenshipBo.to(newEntityCitizenshipBo));
    }

    @Test
    public void testUpdateCitizenshipSucceeds() {
        EntityCitizenshipStatusBo firstEntityCitizenshipStatus = new EntityCitizenshipStatusBo(code: "statuscodeone", name: "statusnameone");
        EntityCitizenshipBo existingEntityCitizenshipBo = new EntityCitizenshipBo(entityId: "AAA", id: "citizenshipidone", active: true, status: firstEntityCitizenshipStatus, statusCode: "statuscodeone");

        mockDataObjectService.demand.findMatching(1..sampleEntityCitizenships.size()) {
            Class clazz, QueryByCriteria map -> for (EntityCitizenshipBo entityCitizenshipBo in sampleEntityCitizenships.values()) {
                if (entityCitizenshipBo.entityId.equals("AAA")
                        && entityCitizenshipBo.statusCode.equals("statuscodeone")
                        && entityCitizenshipBo.active)
                {
                    GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
                    builder.setResults(Collections.singletonList(entityCitizenshipBo));
                    return builder.build();
                }
            }
        }

        mockDataObjectService.demand.save(1..1) {
            EntityCitizenshipBo bo, PersistenceOption... options -> return existingEntityCitizenshipBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityCitizenship entityCitizenship = identityService.updateCitizenship(EntityCitizenshipBo.to(existingEntityCitizenshipBo));

        Assert.assertEquals(EntityCitizenshipBo.to(existingEntityCitizenshipBo), entityCitizenship);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testInactivateCitizenshipWithNonExistentEntityFails() {
        mockDataObjectService.demand.findMatching(1..1) {
            Class clazz, QueryByCriteria id -> return GenericQueryResults.Builder.create().build();
        }

        injectDataObjectServiceIntoIdentityService();

        EntityCitizenship entityCitizenship = identityService.inactivateCitizenship("new");
    }

    @Test
    public void testInactivateCitizenshipSucceeds()
    {
        EntityCitizenshipBo existingEntityCitizenshipBo = sampleEntityCitizenships.get("AAA");
        EntityCitizenshipStatusBo firstEntityCitizenshipStatus = new EntityCitizenshipStatusBo(code: "statuscodeone", name: "statusnameone");
        EntityCitizenshipBo inactiveEntityCitizenshipBo = new EntityCitizenshipBo(entityId: "AAA", id: "citizenshipidone", active: false, status: firstEntityCitizenshipStatus, statusCode: "statuscodeone");

        mockDataObjectService.demand.findMatching(1..sampleEntityCitizenships.size()) {
            Class clazz, QueryByCriteria map -> for (EntityCitizenshipBo entityCitizenshipBo in sampleEntityCitizenships.values()) {
                //EqualPredicate p = (EqualPredicate)map.getPredicate();
                if (entityCitizenshipBo.id.equals("citizenshipidone")
                        && entityCitizenshipBo.active)
                {
                    GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
                    builder.setResults(Collections.singletonList(entityCitizenshipBo));
                    return builder.build();
                }
            }
        }

        mockDataObjectService.demand.save(1..1) {
            EntityCitizenshipBo bo, PersistenceOption... options -> return inactiveEntityCitizenshipBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityCitizenship inactiveEntityCitizenship = identityService.inactivateCitizenship(existingEntityCitizenshipBo.id);

        Assert.assertEquals(EntityCitizenshipBo.to(inactiveEntityCitizenshipBo), inactiveEntityCitizenship);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddEthnicityToEntityWithNullFails() {
        EntityEthnicity entityEthnicity = identityService.addEthnicityToEntity(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testAddEthnicityToEntityWithExistingObjectFails() {
        mockDataObjectService.demand.find(1..sampleEntityEthnicities.size()) {
            Class clazz, String id -> for (EntityEthnicityBo entityEthnicityBo in sampleEntityEthnicities.values()) {
                if (entityEthnicityBo.id.equals(id))
                {
                    return entityEthnicityBo;
                }
            }
        }

        injectDataObjectServiceIntoIdentityService();

        EntityEthnicityBo newEntityEthnicityBo = new EntityEthnicityBo(entityId: "AAA", id: "ethnicityidone");
        EntityEthnicity entityEthnicity = identityService.addEthnicityToEntity(EntityEthnicityBo.to(newEntityEthnicityBo));
    }

    @Test
    public void testAddEthnicityToEntitySucceeds() {
        EntityEthnicityBo newEntityEthnicityBo = new EntityEthnicityBo(entityId: "CCC", id: "ethnicityidthree");

        mockDataObjectService.demand.find(1..sampleEntityEthnicities.size()) {
            Class clazz, String id -> for (EntityEthnicityBo entityEthnicityBo in sampleEntityEthnicities.values()) {
                if (entityEthnicityBo.id.equals(id))
                {
                    return entityEthnicityBo;
                }
            }
        }

        mockDataObjectService.demand.save(1..1) {
            EntityEthnicityBo bo, PersistenceOption... options -> return newEntityEthnicityBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityEthnicity entityEthnicity = identityService.addEthnicityToEntity(EntityEthnicityBo.to(newEntityEthnicityBo));

        Assert.assertEquals(EntityEthnicityBo.to(newEntityEthnicityBo), entityEthnicity);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateEthnicityWithNullFails() {
        EntityEthnicity entityEthnicity = identityService.updateEthnicity(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testUpdateEthnicityWithNonExistingObjectFails() {
        mockDataObjectService.demand.find(1..sampleEntityEthnicities.size()) {
            Class clazz, String id -> for (EntityEthnicityBo entityEthnicityBo in sampleEntityEthnicities.values()) {
                if (entityEthnicityBo.id.equals(id))
                {
                    return entityEthnicityBo;
                }
            }
        }

        injectDataObjectServiceIntoIdentityService();

        EntityEthnicityBo newEntityEthnicityBo = new EntityEthnicityBo(entityId: "CCC", id: "ethnicityidthree");
        EntityEthnicity entityEthnicity = identityService.updateEthnicity(EntityEthnicityBo.to(newEntityEthnicityBo));
    }

    @Test
    public void testUpdateEthnicitySucceeds() {
        EntityEthnicityBo existingEntityEthnicityBo = new EntityEthnicityBo(entityId: "AAA", id: "ethnicityidone");

        mockDataObjectService.demand.find(1..sampleEntityEthnicities.size()) {
            Class clazz, String id -> for (EntityEthnicityBo entityEthnicityBo in sampleEntityEthnicities.values()) {
                if (entityEthnicityBo.id.equals(id))
                {
                    return entityEthnicityBo;
                }
            }
        }

        mockDataObjectService.demand.save(1..1) {
            EntityEthnicityBo bo, PersistenceOption... options -> return existingEntityEthnicityBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityEthnicity entityEthnicity = identityService.updateEthnicity(EntityEthnicityBo.to(existingEntityEthnicityBo));

        Assert.assertEquals(EntityEthnicityBo.to(existingEntityEthnicityBo), entityEthnicity);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddResidencyToEntityWithNullFails() {
        EntityResidency entityResidency = identityService.addResidencyToEntity(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testAddResidencyToEntityWithExistingObjectFails() {
        mockDataObjectService.demand.find(1..sampleEntityResidencies.size()) {
            Class clazz, String id -> for (EntityResidencyBo entityResidencyBo in sampleEntityResidencies.values()) {
                if (entityResidencyBo.id.equals(id))
                {
                    return entityResidencyBo;
                }
            }
        }

        injectDataObjectServiceIntoIdentityService();

        EntityResidencyBo newEntityResidencyBo = new EntityResidencyBo(entityId: "AAA", id: "residencyidone");
        EntityResidency entityResidency = identityService.addResidencyToEntity(EntityResidencyBo.to(newEntityResidencyBo));
    }

    @Test
    public void testAddResidencyToEntitySucceeds() {
        EntityResidencyBo newEntityResidencyBo = new EntityResidencyBo(entityId: "CCC", id: "residencyidthree");

        mockDataObjectService.demand.find(1..sampleEntityResidencies.size()) {
            Class clazz, String id -> for (EntityResidencyBo entityResidencyBo in sampleEntityResidencies.values()) {
                if (entityResidencyBo.id.equals(id))
                {
                    return entityResidencyBo;
                }
            }
        }

        mockDataObjectService.demand.save(1..1) {
            EntityResidencyBo bo, PersistenceOption... options -> return newEntityResidencyBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityResidency entityResidency = identityService.addResidencyToEntity(EntityResidencyBo.to(newEntityResidencyBo));

        Assert.assertEquals(EntityResidencyBo.to(newEntityResidencyBo), entityResidency);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateResidencyWithNullFails() {
        EntityResidency entityResidency = identityService.updateResidency(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testUpdateResidencyWithNonExistingObjectFails() {
        mockDataObjectService.demand.find(1..sampleEntityResidencies.size()) {
            Class clazz, String id -> for (EntityResidencyBo entityResidencyBo in sampleEntityResidencies.values()) {
                if (entityResidencyBo.id.equals(id))
                {
                    return entityResidencyBo;
                }
            }
        }

        injectDataObjectServiceIntoIdentityService();

        EntityResidencyBo newEntityResidencyBo = new EntityResidencyBo(entityId: "CCC", id: "residencyidthree");
        EntityResidency entityResidency = identityService.updateResidency(EntityResidencyBo.to(newEntityResidencyBo));
    }

    @Test
    public void testUpdateResidencySucceeds() {
        EntityResidencyBo existingEntityResidencyBo = new EntityResidencyBo(entityId: "AAA", id: "residencyidone");

        mockDataObjectService.demand.find(1..sampleEntityResidencies.size()) {
            Class clazz, String id -> for (EntityResidencyBo entityResidencyBo in sampleEntityResidencies.values()) {
                if (entityResidencyBo.id.equals(id))
                {
                    return entityResidencyBo;
                }
            }
        }

        mockDataObjectService.demand.save(1..1) {
            EntityResidencyBo bo, PersistenceOption... options -> return existingEntityResidencyBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityResidency entityResidency = identityService.updateResidency(EntityResidencyBo.to(existingEntityResidencyBo));

        Assert.assertEquals(EntityResidencyBo.to(existingEntityResidencyBo), entityResidency);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddVisaToEntityWithNullFails() {
        EntityVisa entityVisa = identityService.addVisaToEntity(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testAddVisaToEntityWithExistingObjectFails() {
        mockDataObjectService.demand.find(1..sampleEntityVisas.size()) {
            Class clazz, String id -> for (EntityVisaBo entityVisaBo in sampleEntityVisas.values()) {
                if (entityVisaBo.id.equals(id))
                {
                    return entityVisaBo;
                }
            }
        }

        injectDataObjectServiceIntoIdentityService();

        EntityVisaBo newEntityVisaBo = new EntityVisaBo(entityId: "AAA", id: "visaidone");
        EntityVisa entityVisa = identityService.addVisaToEntity(EntityVisaBo.to(newEntityVisaBo));
    }

    @Test
    public void testAddVisaToEntitySucceeds() {
        EntityVisaBo newEntityVisaBo = new EntityVisaBo(entityId: "CCC", id: "visaidthree");

        mockDataObjectService.demand.find(1..sampleEntityVisas.size()) {
            Class clazz, String id -> for (EntityVisaBo entityVisaBo in sampleEntityVisas.values()) {
                if (entityVisaBo.id.equals(id))
                {
                    return entityVisaBo;
                }
            }
        }

        mockDataObjectService.demand.save(1..1) {
            EntityVisaBo bo, PersistenceOption... options -> return newEntityVisaBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityVisa entityVisa = identityService.addVisaToEntity(EntityVisaBo.to(newEntityVisaBo));

        Assert.assertEquals(EntityVisaBo.to(newEntityVisaBo), entityVisa);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateVisaWithNullFails() {
        EntityVisa entityVisa = identityService.updateVisa(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testUpdateVisaWithNonExistingObjectFails() {
        mockDataObjectService.demand.find(1..sampleEntityVisas.size()) {
            Class clazz, String id -> for (EntityVisaBo entityVisaBo in sampleEntityVisas.values()) {
                if (entityVisaBo.id.equals(id))
                {
                    return entityVisaBo;
                }
            }
        }

        injectDataObjectServiceIntoIdentityService();

        EntityVisaBo newEntityVisaBo = new EntityVisaBo(entityId: "CCC", id: "visaidthree");
        EntityVisa entityVisa = identityService.updateVisa(EntityVisaBo.to(newEntityVisaBo));
    }

    @Test
    public void testUpdateVisaSucceeds() {
        EntityVisaBo existingEntityVisaBo = new EntityVisaBo(entityId: "AAA", id: "visaidone");

        mockDataObjectService.demand.find(1..sampleEntityVisas.size()) {
            Class clazz, String id -> for (EntityVisaBo entityVisaBo in sampleEntityVisas.values()) {
                if (entityVisaBo.id.equals(id))
                {
                    return entityVisaBo;
                }
            }
        }

        mockDataObjectService.demand.save(1..1) {
            EntityVisaBo bo, PersistenceOption... options -> return existingEntityVisaBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityVisa entityVisa = identityService.updateVisa(EntityVisaBo.to(existingEntityVisaBo));

        Assert.assertEquals(EntityVisaBo.to(existingEntityVisaBo), entityVisa);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNameToEntityWithNullFails() {
        EntityName entityName = identityService.addNameToEntity(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testAddNameToEntityWithExistingObjectFails() {
        mockDataObjectService.demand.find(1..sampleEntityNames.size()) {
            Class clazz, String id -> for (EntityNameBo entityNameBo in sampleEntityNames.values()) {
                if (entityNameBo.id.equals(id))
                {
                    return entityNameBo;
                }
            }
        }

        injectDataObjectServiceIntoIdentityService();

        EntityNameBo newEntityNameBo = new EntityNameBo(entityId: "AAA", id: "nameidone", active: true, firstName: "John", lastName: "Smith");
        EntityName entityName = identityService.addNameToEntity(EntityNameBo.to(newEntityNameBo));
    }

    @Test
    public void testAddNameToEntitySucceeds() {
        EntityNameTypeBo firstEntityNameType = new EntityNameTypeBo(code: "namecodeone");
        EntityNameBo newEntityNameBo = new EntityNameBo(entityId: "CCC", id: "nameidthree", active: true, firstName: "Willard", lastName: "Jackson", nameType: firstEntityNameType, nameCode: "namecodeone");

        mockDataObjectService.demand.findMatching(1..sampleEntityNames.size()) {
            Class clazz, QueryByCriteria query -> for (EntityNameBo entityNameBo in sampleEntityNames.values()) {
                if (entityNameBo.id.equals("nameidthree"))
                {
                    GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
                    builder.setResults(Collections.singletonList(entityNameBo));
                    return builder.build()
                }
            }
            return GenericQueryResults.Builder.create().build();
        }

        mockDataObjectService.demand.save(1..1) {
            EntityNameBo bo, PersistenceOption... options -> return newEntityNameBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityName entityName = identityService.addNameToEntity(EntityNameBo.to(newEntityNameBo));

        Assert.assertEquals(EntityNameBo.to(newEntityNameBo), entityName);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateNameWithNullFails() {
        EntityName entityName = identityService.updateName(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testUpdateNameWithNonExistingObjectFails() {
        mockDataObjectService.demand.findMatching(1..sampleEntityNames.size()) {
            Class clazz, QueryByCriteria id -> return GenericQueryResults.Builder.create().build();
        }

        injectDataObjectServiceIntoIdentityService();

        EntityNameTypeBo firstEntityNameType = new EntityNameTypeBo(code: "namecodeone");
        EntityNameBo newEntityNameBo = new EntityNameBo(entityId: "CCC", id: "nameidthree", active: true, firstName: "Willard", lastName: "Jackson", nameType: firstEntityNameType, nameCode: "namecodeone");
        EntityName entityName = identityService.updateName(EntityNameBo.to(newEntityNameBo));
    }

    @Test
    public void testUpdateNameSucceeds() {
        EntityNameTypeBo firstEntityNameType = new EntityNameTypeBo(code: "namecodeone");
        EntityNameBo existingEntityNameBo = new EntityNameBo(entityId: "AAA", id: "nameidone", active: true, firstName: "John", lastName: "Smith", nameType: firstEntityNameType, nameCode: "namecodeone");

        mockDataObjectService.demand.findMatching(1..sampleEntityNames.size()) {
            Class clazz, QueryByCriteria map -> for (EntityNameBo entityNameBo in sampleEntityNames.values()) {
                if (entityNameBo.id.equals("nameidone"))
                {
                    GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
                    builder.setResults(Collections.singletonList(entityNameBo));
                    return builder.build();
                }
            }
        }

        mockDataObjectService.demand.save(1..1) {
            EntityNameBo bo, PersistenceOption... options -> return existingEntityNameBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityName entityName = identityService.updateName(EntityNameBo.to(existingEntityNameBo));

        Assert.assertEquals(EntityNameBo.to(existingEntityNameBo), entityName);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testInactivateNameWithNonExistentNameFails() {
        mockDataObjectService.demand.findMatching(1..1) {
            Class clazz, QueryByCriteria id -> return GenericQueryResults.Builder.create().build();
        }

        injectDataObjectServiceIntoIdentityService();

        EntityName entityName = identityService.inactivateName("new");
    }

    @Test
    public void testInactivateNameSucceeds()
    {
        EntityNameBo existingEntityNameBo = sampleEntityNames.get("AAA");
        EntityNameTypeBo firstEntityNameType = new EntityNameTypeBo(code: "namecodeone");
        EntityNameBo inactiveEntityNameBo = new EntityNameBo(entityId: "AAA", id: "nameidone", active: false, firstName: "John", lastName: "Smith", nameType: firstEntityNameType, nameCode: "namecodeone");

        mockDataObjectService.demand.findMatching(1..sampleEntityNames.size()) {
            Class clazz, QueryByCriteria query -> for (EntityNameBo entityNameBo in sampleEntityNames.values()) {
                if (entityNameBo.id.equals("nameidone"))
                {
                    GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
                    builder.setResults(Collections.singletonList(entityNameBo));
                    return builder.build();
                }
            }
        }

        mockDataObjectService.demand.save(1..1) {
            EntityNameBo bo, PersistenceOption... options -> return inactiveEntityNameBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityName inactiveEntityName = identityService.inactivateName(existingEntityNameBo.id);

        Assert.assertEquals(EntityNameBo.to(existingEntityNameBo), inactiveEntityName);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddEmploymentToEntityWithNullFails() {
        EntityEmployment entityEmployment = identityService.addEmploymentToEntity(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testAddEmploymentToEntityWithExistingObjectFails() {
        mockDataObjectService.demand.find(1..sampleEntityEmployments.size()) {
            Class clazz, String id -> for (EntityEmploymentBo entityEmploymentBo in sampleEntityEmployments.values()) {
                if (entityEmploymentBo.id.equals(id))
                {
                    return entityEmploymentBo;
                }
            }
        }

        injectDataObjectServiceIntoIdentityService();

        EntityEmploymentBo newEntityEmploymentBo = new EntityEmploymentBo(entityId: "AAA", id: "employmentidone");
        EntityEmployment entityEmployment = identityService.addEmploymentToEntity(EntityEmploymentBo.to(newEntityEmploymentBo));
    }

    @Test
    public void testAddEmploymentToEntitySucceeds() {
        EntityAffiliationTypeBo firstAffiliationType = new EntityAffiliationTypeBo(code: "affiliationcodeone");
        EntityAffiliationBo firstEntityAffiliationBo = new EntityAffiliationBo(entityId: "CCC", affiliationType: firstAffiliationType, id: "affiliationidone", affiliationTypeCode: "affiliationcodeone", active: true);
        EntityEmploymentTypeBo firstEmploymentType = new EntityEmploymentTypeBo(code: "employmenttypecodeone");
        EntityEmploymentStatusBo firstEmploymentStatus = new EntityEmploymentStatusBo(code: "employmentstatusone");
        EntityEmploymentBo newEntityEmploymentBo = new EntityEmploymentBo(entityId: "CCC", id: "employmentidthree", entityAffiliation: firstEntityAffiliationBo, employeeType: firstEmploymentType, employeeStatus: firstEmploymentStatus);

        mockDataObjectService.demand.findMatching(1..sampleEntityEmployments.size()) {
            Class clazz, QueryByCriteria id -> return GenericQueryResults.Builder.create().build();
        }

        mockDataObjectService.demand.save(1..1) {
            EntityEmploymentBo bo, PersistenceOption... options -> return newEntityEmploymentBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityEmployment entityEmployment = identityService.addEmploymentToEntity(EntityEmploymentBo.to(newEntityEmploymentBo));

        Assert.assertEquals(EntityEmploymentBo.to(newEntityEmploymentBo), entityEmployment);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateEmploymentWithNullFails() {
        EntityEmployment entityEmployment = identityService.updateEmployment(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testUpdateEmploymentWithNonExistingObjectFails() {
        mockDataObjectService.demand.findMatching(1..sampleEntityEmployments.size()) {
            Class clazz, QueryByCriteria query ->
                GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
                return builder.build();
        }

        injectDataObjectServiceIntoIdentityService();

        EntityAffiliationTypeBo firstAffiliationType = new EntityAffiliationTypeBo(code: "affiliationcodeone");
        EntityAffiliationBo firstEntityAffiliationBo = new EntityAffiliationBo(entityId: "CCC", affiliationType: firstAffiliationType, id: "affiliationidone", affiliationTypeCode: "affiliationcodeone", active: true);
        EntityEmploymentTypeBo firstEmploymentType = new EntityEmploymentTypeBo(code: "employmenttypecodeone");
        EntityEmploymentStatusBo firstEmploymentStatus = new EntityEmploymentStatusBo(code: "employmentstatusone");
        EntityEmploymentBo newEntityEmploymentBo = new EntityEmploymentBo(entityId: "CCC", id: "employmentidthree", entityAffiliation: firstEntityAffiliationBo, employeeType: firstEmploymentType, employeeStatus: firstEmploymentStatus);
        EntityEmployment entityEmployment = identityService.updateEmployment(EntityEmploymentBo.to(newEntityEmploymentBo));
    }

    @Test
    public void testUpdateEmploymentSucceeds() {
        EntityAffiliationTypeBo firstAffiliationType = new EntityAffiliationTypeBo(code: "affiliationcodeone");
        EntityAffiliationBo firstEntityAffiliationBo = new EntityAffiliationBo(entityId: "AAA", affiliationType: firstAffiliationType, id: "affiliationidone", affiliationTypeCode: "affiliationcodeone", active: true);
        EntityEmploymentTypeBo firstEmploymentType = new EntityEmploymentTypeBo(code: "employmenttypecodeone");
        EntityEmploymentStatusBo firstEmploymentStatus = new EntityEmploymentStatusBo(code: "employmentstatusone");
        EntityEmploymentBo existingEntityEmploymentBo = new EntityEmploymentBo(entityId: "AAA", id: "employmentidone", entityAffiliation: firstEntityAffiliationBo, entityAffiliationId: "affiliationidone", employeeType: firstEmploymentType, employeeTypeCode: "employmenttypecodeone", employeeStatus: firstEmploymentStatus, employeeStatusCode: "employmentstatusone");

        mockDataObjectService.demand.findMatching(1..sampleEntityEmployments.size()) {
            Class clazz, QueryByCriteria map -> for (EntityEmploymentBo entityEmploymentBo in sampleEntityEmployments.values()) {
                if (entityEmploymentBo.entityId.equals("AAA")
                        && entityEmploymentBo.employeeTypeCode.equals("employmenttypecodeone")
                        && entityEmploymentBo.employeeStatusCode.equals("employmentstatusone")
                        && entityEmploymentBo.entityAffiliationId.equals("affiliationidone"))
                {
                    GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
                    builder.setResults(Collections.singletonList(entityEmploymentBo));
                    return builder.build();
                }
            }
        }

        mockDataObjectService.demand.save(1..1) {
            EntityEmploymentBo bo, PersistenceOption... options -> return existingEntityEmploymentBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityEmployment entityEmployment = identityService.updateEmployment(EntityEmploymentBo.to(existingEntityEmploymentBo));

        Assert.assertEquals(EntityEmploymentBo.to(existingEntityEmploymentBo), entityEmployment);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testInactivateEmploymentWithNonExistentObjectFails() {
        mockDataObjectService.demand.findMatching(1..1) {
            Class clazz, QueryByCriteria query -> return GenericQueryResults.Builder.create().build();
        }

        injectDataObjectServiceIntoIdentityService();

        EntityEmployment entityEmployment = identityService.inactivateEmployment("new");
    }

    @Test
    public void testInactivateEmploymentSucceeds()
    {
        EntityEmploymentBo existingEntityEmploymentBo = sampleEntityEmployments.get("AAA");
        EntityAffiliationTypeBo firstAffiliationType = new EntityAffiliationTypeBo(code: "affiliationcodeone");
        EntityAffiliationBo firstEntityAffiliationBo = new EntityAffiliationBo(entityId: "AAA", affiliationType: firstAffiliationType, id: "affiliationidone", affiliationTypeCode: "affiliationcodeone", active: true);
        EntityEmploymentTypeBo firstEmploymentType = new EntityEmploymentTypeBo(code: "employmenttypecodeone");
        EntityEmploymentStatusBo firstEmploymentStatus = new EntityEmploymentStatusBo(code: "employmentstatusone");
        EntityEmploymentBo inactiveEntityEmploymentBo = new EntityEmploymentBo(entityId: "AAA", id: "employmentidone", entityAffiliation: firstEntityAffiliationBo, entityAffiliationId: "affiliationidone", employeeType: firstEmploymentType, employeeTypeCode: "employmenttypecodeone", employeeStatus: firstEmploymentStatus, employeeStatusCode: "employmentstatusone", active: false);

        mockDataObjectService.demand.findMatching(1..sampleEntityEmployments.size()) {
            Class clazz, QueryByCriteria id -> for (EntityEmploymentBo entityEmploymentBo in sampleEntityEmployments.values()) {
                if (entityEmploymentBo.id.equals(inactiveEntityEmploymentBo.id))
                {
                    GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
                    builder.setResults(Collections.singletonList(entityEmploymentBo));
                    return builder.build();
                }
            }
        }

        mockDataObjectService.demand.save(1..1) {
            EntityEmploymentBo bo, PersistenceOption... options -> return inactiveEntityEmploymentBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityEmployment inactiveEntityEmployment = identityService.inactivateEmployment(existingEntityEmploymentBo.id);

        Assert.assertEquals(EntityEmploymentBo.to(existingEntityEmploymentBo).active, inactiveEntityEmployment.active);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddBioDemographicsToEntityWithNullFails() {
        EntityBioDemographics entityBioDemographics = identityService.addBioDemographicsToEntity(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testAddBioDemographicsToEntityWithExistingObjectFails() {
        mockDataObjectService.demand.find(1..sampleEntityBioDemographics.size()) {
            Class clazz, String entityId -> for (EntityBioDemographicsBo entityBioDemographicsBo in sampleEntityBioDemographics.values()) {
                if (entityBioDemographicsBo.entityId.equals(entityId))
                {
                    return entityBioDemographicsBo;
                }
            }
        }

        injectDataObjectServiceIntoIdentityService();

        String birthDateString = "01/01/2007";
        String deceasedDateString = "01/01/2087";
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date birthDate = formatter.parse(birthDateString);
        Date deceasedDate = formatter.parse(deceasedDateString);
        EntityBioDemographicsBo newEntityBioDemographicsBo = new EntityBioDemographicsBo(entityId: "AAA", birthDateValue: birthDate, genderCode: "M", deceasedDateValue: deceasedDate, maritalStatusCode: "S", primaryLanguageCode: "EN", secondaryLanguageCode: "FR", birthCountry: "US", birthStateProvinceCode: "IN", birthCity: "Bloomington", geographicOrigin: "None", suppressPersonal: false);
        EntityBioDemographics entityBioDemographics = identityService.addBioDemographicsToEntity(EntityBioDemographicsBo.to(newEntityBioDemographicsBo));
    }

    @Test
    public void testAddBioDemographicsToEntitySucceeds() {
        String birthDateString = "01/01/2007";
        String deceasedDateString = "01/01/2087";
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date birthDate = formatter.parse(birthDateString);
        Date deceasedDate = formatter.parse(deceasedDateString);
        EntityBioDemographicsBo newEntityBioDemographicsBo = new EntityBioDemographicsBo(entityId: "CCC", birthDateValue: birthDate, genderCode: "M", deceasedDateValue: deceasedDate, maritalStatusCode: "S", primaryLanguageCode: "EN", secondaryLanguageCode: "FR", birthCountry: "US", birthStateProvinceCode: "IN", birthCity: "Bloomington", geographicOrigin: "None", suppressPersonal: false);

        mockDataObjectService.demand.find(1..sampleEntityBioDemographics.size()) {
            Class clazz, String entityId -> for (EntityBioDemographicsBo entityBioDemographicsBo in sampleEntityBioDemographics.values()) {
                if (entityBioDemographicsBo.entityId.equals(entityId))
                {
                    return entityBioDemographicsBo;
                }
            }
        }

        mockDataObjectService.demand.save(1..1) {
            EntityBioDemographicsBo bo, PersistenceOption... options -> return newEntityBioDemographicsBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityBioDemographics entityBioDemographics = identityService.addBioDemographicsToEntity(EntityBioDemographicsBo.to(newEntityBioDemographicsBo));

        Assert.assertEquals(EntityBioDemographicsBo.to(newEntityBioDemographicsBo), entityBioDemographics);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateBioDemographicsWithNullFails() {
        EntityBioDemographics entityBioDemographics = identityService.updateBioDemographics(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testUpdateBioDemographicsWithNonExistingObjectFails() {
        mockDataObjectService.demand.find(1..sampleEntityBioDemographics.size()) {
            Class clazz, String entityId -> for (EntityBioDemographicsBo entityBioDemographicsBo in sampleEntityBioDemographics.values()) {
                if (entityBioDemographicsBo.entityId.equals(entityId))
                {
                    return entityBioDemographicsBo;
                }
            }
        }

        injectDataObjectServiceIntoIdentityService();

        String birthDateString = "01/01/2007";
        String deceasedDateString = "01/01/2087";
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date birthDate = formatter.parse(birthDateString);
        Date deceasedDate = formatter.parse(deceasedDateString);
        EntityBioDemographicsBo newEntityBioDemographicsBo = new EntityBioDemographicsBo(entityId: "CCC", birthDateValue: birthDate, genderCode: "M", deceasedDateValue: deceasedDate, maritalStatusCode: "S", primaryLanguageCode: "EN", secondaryLanguageCode: "FR", birthCountry: "US", birthStateProvinceCode: "IN", birthCity: "Bloomington", geographicOrigin: "None", suppressPersonal: false);
        EntityBioDemographics entityBioDemographics = identityService.updateBioDemographics(EntityBioDemographicsBo.to(newEntityBioDemographicsBo));
    }

    @Test
    public void testUpdateBioDemographicsSucceeds() {
        String birthDateString = "01/01/2007";
        String deceasedDateString = "01/01/2087";
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date birthDate = formatter.parse(birthDateString);
        Date deceasedDate = formatter.parse(deceasedDateString);
        EntityBioDemographicsBo existingEntityBioDemographicsBo = new EntityBioDemographicsBo(entityId: "AAA", birthDateValue: birthDate, genderCode: "M", deceasedDateValue: deceasedDate, maritalStatusCode: "S", primaryLanguageCode: "EN", secondaryLanguageCode: "FR", birthCountry: "US", birthStateProvinceCode: "IN", birthCity: "Bloomington", geographicOrigin: "None", suppressPersonal: false);

        mockDataObjectService.demand.find(1..sampleEntityBioDemographics.size()) {
            Class clazz, String entityId -> for (EntityBioDemographicsBo entityBioDemographicsBo in sampleEntityBioDemographics.values()) {
                if (entityBioDemographicsBo.entityId.equals(entityId))
                {
                    return entityBioDemographicsBo;
                }
            }
        }

        mockDataObjectService.demand.save(1..1) {
            EntityBioDemographicsBo bo, PersistenceOption... options -> return existingEntityBioDemographicsBo;
        }

        injectDataObjectServiceIntoIdentityService();

        EntityBioDemographics entityBioDemographics = identityService.updateBioDemographics(EntityBioDemographicsBo.to(existingEntityBioDemographicsBo));

        Assert.assertEquals(EntityBioDemographicsBo.to(existingEntityBioDemographicsBo), entityBioDemographics);
    }
}
