package org.kuali.rice.kim.service.impl

import org.junit.Test;
import groovy.mock.interceptor.MockFor;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.kuali.rice.kim.api.identity.entity.Entity
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.type.EntityTypeContactInfo;
import org.junit.Assert
import org.kuali.rice.kim.impl.identity.entity.EntityBo
import org.kuali.rice.kim.impl.identity.privacy.EntityPrivacyPreferencesBo
import org.kuali.rice.kim.impl.identity.personal.EntityBioDemographicsBo
import java.text.SimpleDateFormat
import org.kuali.rice.krad.service.PersistenceService
import org.kuali.rice.kim.impl.identity.principal.PrincipalBo
import org.kuali.rice.kim.api.identity.principal.Principal
import org.kuali.rice.core.api.exception.RiceIllegalStateException
import org.kuali.rice.kim.impl.identity.type.EntityTypeContactInfoBo
import org.kuali.rice.kim.impl.identity.address.EntityAddressBo
import org.apache.cxf.wsdl.http.AddressType
import org.kuali.rice.kim.impl.identity.address.EntityAddressTypeBo
import org.kuali.rice.kim.api.identity.address.EntityAddress
import org.kuali.rice.kim.api.identity.email.EntityEmail
import org.kuali.rice.kim.impl.identity.email.EntityEmailBo
import org.kuali.rice.kim.impl.identity.email.EntityEmailTypeBo
import org.kuali.rice.kim.api.identity.phone.EntityPhone
import org.kuali.rice.kim.impl.identity.phone.EntityPhoneBo
import org.kuali.rice.kim.impl.identity.phone.EntityPhoneTypeBo
import org.junit.Ignore

class IdentityServiceImplTest {
    private final shouldFail = new GroovyTestCase().&shouldFail

    private MockFor mockBoService;
    private MockFor mockPersistenceService;
    private BusinessObjectService boService;
    private PersistenceService persistenceService;
    IdentityService identityService;
    IdentityServiceImpl identityServiceImpl;

    static Map<String, EntityBo> sampleEntities = new HashMap<String, EntityBo>();
    static Map<String, PrincipalBo> samplePrincipals = new HashMap<String, PrincipalBo>();
    static Map<String, EntityTypeContactInfoBo> sampleEntityTypeContactInfos = new HashMap<String, EntityTypeContactInfoBo>();
    static Map<String, EntityAddressBo> sampleEntityAddresses = new HashMap<String, EntityAddressBo>();
    static Map<String, EntityEmailBo> sampleEntityEmails = new HashMap<String, EntityEmailBo>();
    static Map<String, EntityPhoneBo> sampleEntityPhones = new HashMap<String, EntityPhoneBo>();

    @BeforeClass
    static void createSampleBOs() {
        EntityPrivacyPreferencesBo firstEntityPrivacyPreferencesBo = new EntityPrivacyPreferencesBo(entityId: "AAA", suppressName: true, suppressEmail: true, suppressAddress: true, suppressPhone: true, suppressPersonal: false);
        String birthDateString = "01/01/2007";
        String deceasedDateString = "01/01/2087";
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date birthDate = formatter.parse(birthDateString);
        Date deceasedDate = formatter.parse(deceasedDateString);
        EntityBioDemographicsBo firstEntityBioDemographicsBo = new EntityBioDemographicsBo(entityId: "AAA", birthDateValue: birthDate, genderCode: "M", deceasedDateValue: deceasedDate, maritalStatusCode: "S", primaryLanguageCode: "EN", secondaryLanguageCode: "FR", countryOfBirthCode: "US", birthStateCode: "IN", cityOfBirth: "Bloomington", geographicOrigin: "None", suppressPersonal: false);
        PrincipalBo firstEntityPrincipal = new PrincipalBo(entityId: "AAA", principalId: "P1", active: true, principalName: "first", versionNumber: 1, password: "first_password");
        EntityTypeContactInfoBo firstEntityTypeContactInfoBo = new EntityTypeContactInfoBo(entityId: "AAA", entityTypeCode: "typecodeone", active: true);
        EntityAddressTypeBo firstAddressTypeBo = new EntityAddressTypeBo(code: "addresscodeone");
        EntityAddressBo firstEntityAddressBo = new EntityAddressBo(entityId: "AAA", entityTypeCode: "typecodeone", addressType: firstAddressTypeBo, id: "addressidone", addressTypeCode: "addresscodeone", active: true);
        EntityEmailTypeBo firstEmailTypeBo = new EntityEmailTypeBo(code: "emailcodeone");
        EntityEmailBo firstEntityEmailBo = new EntityEmailBo(entityId: "AAA", entityTypeCode: "typecodeone", emailType: firstEmailTypeBo, id:"emailidone", emailTypeCode: "emailcodeone", active: true);
        EntityPhoneTypeBo firstPhoneType = new EntityPhoneTypeBo(code: "phonecodeone");
        EntityPhoneBo firstEntityPhoneBo = new EntityPhoneBo(entityId: "AAA", entityTypeCode: "typecodeone", phoneType: firstPhoneType, id: "phoneidone", phoneTypeCode: "phonetypecodeone", active: true);
        List<PrincipalBo> firstPrincipals = new ArrayList<PrincipalBo>();
        firstPrincipals.add(firstEntityPrincipal);
        EntityBo firstEntityBo = new EntityBo(active: true, id: "AAA", privacyPreferences: firstEntityPrivacyPreferencesBo, bioDemographics: firstEntityBioDemographicsBo, principals: firstPrincipals);

        EntityPrivacyPreferencesBo secondEntityPrivacyPreferencesBo = new EntityPrivacyPreferencesBo(entityId: "BBB", suppressName: true, suppressEmail: true, suppressAddress: true, suppressPhone: true, suppressPersonal: false);
        EntityBioDemographicsBo secondEntityBioDemographicsBo = new EntityBioDemographicsBo(entityId: "BBB", birthDateValue: birthDate, genderCode: "M", deceasedDateValue: deceasedDate, maritalStatusCode: "S", primaryLanguageCode: "EN", secondaryLanguageCode: "FR", countryOfBirthCode: "US", birthStateCode: "IN", cityOfBirth: "Bloomington", geographicOrigin: "None", suppressPersonal: false);
        PrincipalBo secondEntityPrincipal = new PrincipalBo(entityId: "BBB", principalId: "P2", active: true, principalName: "second", versionNumber: 1, password: "second_password");
        EntityTypeContactInfoBo secondEntityTypeContactInfoBo = new EntityTypeContactInfoBo(entityId: "BBB", entityTypeCode: "typecodetwo", active: true);
        EntityAddressTypeBo secondAddressTypeBo = new EntityAddressTypeBo(code: "addresscodetwo");
        EntityAddressBo secondEntityAddressBo = new EntityAddressBo(entityId: "BBB", entityTypeCode: "typecodetwo", addressType: secondAddressTypeBo, id: "addressidtwo", addressTypeCode: "addresscodetwo", active: true);
        EntityEmailTypeBo secondEmailTypeBo = new EntityEmailTypeBo(code: "emailcodetwo");
        EntityEmailBo secondEntityEmailBo = new EntityEmailBo(entityId: "BBB", entityTypeCode: "typecodetwo", emailType: secondEmailTypeBo, id:"emailidtwo", emailTypeCode: "emailcodetwo", active: true);
        EntityPhoneTypeBo secondPhoneType = new EntityPhoneTypeBo(code: "phonecodetwo");
        EntityPhoneBo secondEntityPhoneBo = new EntityPhoneBo(entityId: "BBB", entityTypeCode: "typecodetwo", phoneType: secondPhoneType, id: "phoneidtwo", phoneTypeCode: "phonetypecodetwo", active: true);
        List<PrincipalBo> secondPrincipals = new ArrayList<PrincipalBo>();
        secondPrincipals.add(secondEntityPrincipal);
        EntityBo secondEntityBo = new EntityBo(active: true, id: "BBB", privacyPreferences: secondEntityPrivacyPreferencesBo, bioDemographics: secondEntityBioDemographicsBo, principals: secondPrincipals);

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
    }

    @Before
    void setupMockContext() {
        mockBoService = new MockFor(BusinessObjectService.class);
        mockPersistenceService = new MockFor(PersistenceService.class);
    }

    @Before
    void setupServiceUnderTest() {
        identityServiceImpl = new IdentityServiceImpl()
        identityService = identityServiceImpl    //assign Interface type to implementation reference for unit test only
    }

    void injectBusinessObjectServiceIntoIdentityService() {
        boService = mockBoService.proxyDelegateInstance()
        identityServiceImpl.setBusinessObjectService(boService)
    }

    void injectPersistenceServiceIntoIdentityService() {
        persistenceService = mockPersistenceService.proxyDelegateInstance();
        identityServiceImpl.setPersistenceService(persistenceService);
    }

    @Test
    void test_createIdentityNullIdentity(){
        injectBusinessObjectServiceIntoIdentityService()

        shouldFail(IllegalArgumentException.class) {
            identityService.createEntity(null)
        }
        mockBoService.verify(boService)
    }

    @Test
    void test_updateIdentityNullIdentity(){
        injectBusinessObjectServiceIntoIdentityService();

        shouldFail(IllegalArgumentException.class) {
            identityService.updateEntity(null)
        }
        mockBoService.verify(boService);
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
        mockBoService.demand.findByPrimaryKey(1..sampleEntities.size()) {
            Class clazz, Map map -> return sampleEntities.get(map.get("id"))
        }

        mockPersistenceService.demand.retrieveNonKeyFields(1..sampleEntities.size()) {
            Object object -> return null
        }

        injectBusinessObjectServiceIntoIdentityService();
        injectPersistenceServiceIntoIdentityService();

        for (EntityBo entityBo in sampleEntities.values()) {
            Assert.assertEquals(EntityBo.to(entityBo), identityService.getEntity(entityBo.id))
        }

        mockBoService.verify(boService)
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
        mockBoService.demand.findMatching(1..sampleEntities.size()) {
            Class clazz, Map map -> for (EntityBo entityBo in sampleEntities.values()) {
                for (PrincipalBo principalBo in entityBo.principals) {
                    if (principalBo.principalId.equals(map.get("principals.principalId")))
                    {
                        Collection<EntityBo> entities = new ArrayList<EntityBo>();
                        entities.add(entityBo);
                        return entities;
                    }
                }
            }
        }

        injectBusinessObjectServiceIntoIdentityService();

        for (EntityBo entityBo in sampleEntities.values()) {
            Assert.assertEquals(EntityBo.to(entityBo), identityService.getEntityByPrincipalId(entityBo.principals[0].principalId));
        }

        mockBoService.verify(boService)
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
        mockBoService.demand.findMatching(1..sampleEntities.size()) {
            Class clazz, Map map -> for (EntityBo entityBo in sampleEntities.values()) {
                for (PrincipalBo principalBo in entityBo.principals) {
                    if (principalBo.principalName.equals(map.get("principals.principalName")))
                    {
                        Collection<EntityBo> entities = new ArrayList<EntityBo>();
                        entities.add(entityBo);
                        return entities;
                    }
                }
            }
        }

        injectBusinessObjectServiceIntoIdentityService();

        for (EntityBo entityBo in sampleEntities.values()) {
            Assert.assertEquals(EntityBo.to(entityBo), identityService.getEntityByPrincipalName(entityBo.principals[0].principalName));
        }

        mockBoService.verify(boService)
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
        mockBoService.demand.findMatching(1..samplePrincipals.size()) {
            Class clazz, Map map -> for (PrincipalBo principalBo in samplePrincipals.values()) {
                if (principalBo.principalName.equals(map.get("principalName"))
                    && principalBo.password.equals(map.get("password"))
                    && principalBo.active)
                {
                    Collection<PrincipalBo> principals = new ArrayList<PrincipalBo>();
                    principals.add(principalBo);
                    return principals;
                }
            }
        }

        injectBusinessObjectServiceIntoIdentityService();

        String id = "P2";
        String name = "second";
        String password = "second_password";
        Assert.assertEquals(PrincipalBo.to(samplePrincipals.get(id)), identityService.getPrincipalByPrincipalNameAndPassword(name, password));

        mockBoService.verify(boService);
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
        mockBoService.demand.findMatching(1..samplePrincipals.size()) {
            Class clazz, Map map -> for (PrincipalBo principalBo in samplePrincipals.values()) {
                if (principalBo.principalName.equals(map.get("principalName")))
                {
                    Collection<PrincipalBo> principals = new ArrayList<PrincipalBo>();
                    principals.add(principalBo);
                    return principals;
                }
            }
        }

        injectBusinessObjectServiceIntoIdentityService();

        PrincipalBo principalBo = new PrincipalBo(entityId: "ABC", principalName: "first", principalId: "P1");
        principalBo = identityService.addPrincipalToEntity(PrincipalBo.to(principalBo));
    }

    @Test
    public void testAddPrincipalToEntitySucceeds()
    {
        PrincipalBo newPrincipalBo = new PrincipalBo(entityId: "ABC", principalName: "new", principalId: "New");

        mockBoService.demand.findMatching(1..samplePrincipals.size()) {
            Class clazz, Map map -> for (PrincipalBo principalBo in samplePrincipals.values()) {
                if (principalBo.principalName.equals(map.get("principalName")))
                {
                    Collection<PrincipalBo> principals = new ArrayList<PrincipalBo>();
                    principals.add(principalBo);
                    return principals;
                }
            }

            return new ArrayList<PrincipalBo>();
        }

        mockBoService.demand.save(1..1) {
            PrincipalBo bo -> return newPrincipalBo;
        }

        injectBusinessObjectServiceIntoIdentityService();

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
        mockBoService.demand.findMatching(1..samplePrincipals.size()) {
            Class clazz, Map map -> return new ArrayList<PrincipalBo>();
        }

        injectBusinessObjectServiceIntoIdentityService();

        PrincipalBo principalBo = new PrincipalBo(entityId: "CCC", principalName: "fifth", principalId: "P5");
        principalBo = identityService.updatePrincipal(PrincipalBo.to(principalBo));
    }

    @Test
    public void testUpdatePrincipalSucceeds()
    {
        PrincipalBo existingPrincipalBo = samplePrincipals.get("P1");

        mockBoService.demand.findMatching(1..samplePrincipals.size()) {
            Class clazz, Map map -> for (PrincipalBo principalBo in samplePrincipals.values()) {
                if (principalBo.principalName.equals(map.get("principalName")))
                {
                    Collection<PrincipalBo> principals = new ArrayList<PrincipalBo>();
                    principals.add(principalBo);
                    return principals;
                }
            }

            return new ArrayList<PrincipalBo>();
        }

        mockBoService.demand.save(1..1) {
            PrincipalBo bo -> return existingPrincipalBo;
        }

        injectBusinessObjectServiceIntoIdentityService();

        Principal existingPrincipal = identityService.updatePrincipal(PrincipalBo.to(existingPrincipalBo));

        Assert.assertEquals(PrincipalBo.to(existingPrincipalBo), existingPrincipal);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInactivatePrincipalWithEmptyIdFails() {
        Principal principal = identityService.inactivatePrincipal("");
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testInactivatePrincipalWithNonExistentPrincipalFails() {
        mockBoService.demand.findByPrimaryKey(1..1) {
            Class clazz, Map map -> return null;
        }

        injectBusinessObjectServiceIntoIdentityService();

        Principal principal = identityService.inactivatePrincipal("New");
    }

    @Test
    public void testInactivatePrincipalSucceeds()
    {
        PrincipalBo existingPrincipalBo = samplePrincipals.get("P1");
        PrincipalBo inactivePrincipalBo = new PrincipalBo(entityId: "AAA", principalId: "P1", active: false, principalName: "first", versionNumber: 1, password: "first_password");

        mockBoService.demand.findByPrimaryKey(1..samplePrincipals.size()) {
            Class clazz, Map map -> return samplePrincipals.get(map.get("principalId"))
        }

        mockBoService.demand.save(1..1) {
            PrincipalBo bo -> return inactivePrincipalBo;
        }

        injectBusinessObjectServiceIntoIdentityService();

        Principal inactivePrincipal = identityService.inactivatePrincipal(existingPrincipalBo.principalId);

        Assert.assertEquals(PrincipalBo.to(inactivePrincipalBo), inactivePrincipal);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testInactivatePrincipalByNameWithNonExistentPrincipalFails() {
        mockBoService.demand.findMatching(1..samplePrincipals.size()) {
            Class clazz, Map map -> return new ArrayList<PrincipalBo>();
        }

        injectBusinessObjectServiceIntoIdentityService();

        Principal principal = identityService.inactivatePrincipalByName("new");
    }

    @Test
    public void testInactivatePrincipalByNameSucceeds()
    {
        PrincipalBo existingPrincipalBo = samplePrincipals.get("P1");
        PrincipalBo inactivePrincipalBo = new PrincipalBo(entityId: "AAA", principalId: "P1", active: false, principalName: "first", versionNumber: 1, password: "first_password");

        mockBoService.demand.findMatching(1..samplePrincipals.size()) {
            Class clazz, Map map -> for (PrincipalBo principalBo in samplePrincipals.values()) {
                if (principalBo.principalName.equals(map.get("principalName")))
                {
                    Collection<PrincipalBo> principals = new ArrayList<PrincipalBo>();
                    principals.add(principalBo);
                    return principals;
                }
            }
        }

        mockBoService.demand.save(1..1) {
            PrincipalBo bo -> return inactivePrincipalBo;
        }

        injectBusinessObjectServiceIntoIdentityService();

        Principal inactivePrincipal = identityService.inactivatePrincipalByName(existingPrincipalBo.principalName);

        Assert.assertEquals(PrincipalBo.to(inactivePrincipalBo), inactivePrincipal);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddEntityTypeContactInfoToEntityWithNullFails() {
        EntityTypeContactInfo entityTypeContactInfo = identityService.addEntityTypeContactInfoToEntity(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testAddEntityTypeContactInfoToEntityWithExistingObjectFails() {
        mockBoService.demand.findByPrimaryKey(1..sampleEntityTypeContactInfos.size()) {
            Class clazz, Map map -> for (EntityTypeContactInfoBo entityTypeContactInfoBo in sampleEntityTypeContactInfos.values()) {
                if (entityTypeContactInfoBo.entityId.equals(map.get("entityId"))
                    && entityTypeContactInfoBo.entityTypeCode.equals(map.get("entityTypeCode"))
                    && entityTypeContactInfoBo.active)
                {
                    return entityTypeContactInfoBo;
                }
            }
        }

        injectBusinessObjectServiceIntoIdentityService();

        EntityTypeContactInfoBo newEntityTypeContactInfoBo = new EntityTypeContactInfoBo(active: true, entityId: "AAA", entityTypeCode: "typecodeone");
        EntityTypeContactInfo entityTypeContactInfo = identityService.addEntityTypeContactInfoToEntity(EntityTypeContactInfoBo.to(newEntityTypeContactInfoBo));
    }

    @Test
    public void testAddEntityTypeContactInfoToEntitySucceeds() {
        EntityTypeContactInfoBo newEntityTypeContactInfoBo = new EntityTypeContactInfoBo(active: true, entityId: "CCC", entityTypeCode: "typecodethree");

        mockBoService.demand.findByPrimaryKey(1..sampleEntityTypeContactInfos.size()) {
            Class clazz, Map map -> for (EntityTypeContactInfoBo entityTypeContactInfoBo in sampleEntityTypeContactInfos.values()) {
                if (entityTypeContactInfoBo.entityId.equals(map.get("entityId"))
                    && entityTypeContactInfoBo.entityTypeCode.equals(map.get("entityTypeCode"))
                    && entityTypeContactInfoBo.active)
                {
                    return entityTypeContactInfoBo;
                }
            }
        }

        mockBoService.demand.save(1..1) {
            EntityTypeContactInfoBo bo -> return newEntityTypeContactInfoBo;
        }

        injectBusinessObjectServiceIntoIdentityService();

        EntityTypeContactInfo entityTypeContactInfo = identityService.addEntityTypeContactInfoToEntity(EntityTypeContactInfoBo.to(newEntityTypeContactInfoBo));

        Assert.assertEquals(EntityTypeContactInfoBo.to(newEntityTypeContactInfoBo), entityTypeContactInfo);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateEntityTypeContactInfoWithNullFails() {
        EntityTypeContactInfo entityTypeContactInfo = identityService.updateEntityTypeContactInfo(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testUpdateEntityTypeContactInfoWithNonExistingObjectFails() {
        mockBoService.demand.findByPrimaryKey(1..1) {
            Class clazz, Map map -> return null;
        }

        injectBusinessObjectServiceIntoIdentityService();

        EntityTypeContactInfoBo newEntityTypeContactInfoBo = new EntityTypeContactInfoBo(active: true, entityId: "AAA", entityTypeCode: "typecodeone");
        EntityTypeContactInfo entityTypeContactInfo = identityService.updateEntityTypeContactInfo(EntityTypeContactInfoBo.to(newEntityTypeContactInfoBo));
    }

    @Test
    public void testUpdateEntityTypeContactInfoSucceeds() {
        EntityTypeContactInfoBo existingEntityTypeContactInfoBo = new EntityTypeContactInfoBo(active: true, entityId: "AAA", entityTypeCode: "typecodeone");

        mockBoService.demand.findByPrimaryKey(1..sampleEntityTypeContactInfos.size()) {
            Class clazz, Map map -> for (EntityTypeContactInfoBo entityTypeContactInfoBo in sampleEntityTypeContactInfos.values()) {
                if (entityTypeContactInfoBo.entityId.equals(map.get("entityId"))
                    && entityTypeContactInfoBo.entityTypeCode.equals(map.get("entityTypeCode"))
                    && entityTypeContactInfoBo.active)
                {
                    return entityTypeContactInfoBo;
                }
            }
        }

        mockBoService.demand.save(1..1) {
            EntityTypeContactInfoBo bo -> return existingEntityTypeContactInfoBo;
        }

        injectBusinessObjectServiceIntoIdentityService();

        EntityTypeContactInfo entityTypeContactInfo = identityService.updateEntityTypeContactInfo(EntityTypeContactInfoBo.to(existingEntityTypeContactInfoBo));

        Assert.assertEquals(EntityTypeContactInfoBo.to(existingEntityTypeContactInfoBo), entityTypeContactInfo);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testInactivateEntityTypeContactInfoWithNonExistentObjectFails() {
        mockBoService.demand.findByPrimaryKey(1..1) {
            Class clazz, Map map -> return null;
        }

        injectBusinessObjectServiceIntoIdentityService();

        EntityTypeContactInfo entityTypeContactInfo = identityService.inactivateEntityTypeContactInfo("new", "new");
    }

    @Test
    public void testInactivateEntityTypeContactInfoSucceeds()
    {
        EntityTypeContactInfoBo existingEntityTypeContactInfoBo = sampleEntityTypeContactInfos.get("typecodeone");
        EntityTypeContactInfoBo inactiveEntityTypeContactInfoBo = new EntityTypeContactInfoBo(entityId: "AAA", entityTypeCode: "typecodeone", active: false);

        mockBoService.demand.findByPrimaryKey(1..sampleEntityTypeContactInfos.size()) {
            Class clazz, Map map -> for (EntityTypeContactInfoBo entityTypeContactInfoBo in sampleEntityTypeContactInfos.values()) {
                if (entityTypeContactInfoBo.entityId.equals(map.get("entityId"))
                    && entityTypeContactInfoBo.entityTypeCode.equals(map.get("entityTypeCode"))
                    && entityTypeContactInfoBo.active)
                {
                    return entityTypeContactInfoBo;
                }
            }
        }

        mockBoService.demand.save(1..1) {
            EntityTypeContactInfoBo bo -> return inactiveEntityTypeContactInfoBo;
        }

        injectBusinessObjectServiceIntoIdentityService();

        EntityTypeContactInfo inactiveEntityTypeContactInfo = identityService.inactivateEntityTypeContactInfo(existingEntityTypeContactInfoBo.entityId, existingEntityTypeContactInfoBo.entityTypeCode);

        Assert.assertEquals(EntityTypeContactInfoBo.to(inactiveEntityTypeContactInfoBo), inactiveEntityTypeContactInfo);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddAddressToEntityWithNullFails() {
        EntityAddress entityAddress = identityService.addAddressToEntity(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testAddAddressToEntityWithExistingAddressFails() {
        mockBoService.demand.findByPrimaryKey(1..sampleEntityAddresses.size()) {
            Class clazz, Map map -> for (EntityAddressBo entityAddressBo in sampleEntityAddresses.values()) {
                if (entityAddressBo.entityId.equals(map.get("entityId"))
                    && entityAddressBo.entityTypeCode.equals(map.get("entityTypeCode"))
                    && entityAddressBo.addressTypeCode.equals(map.get("addressTypeCode"))
                    && entityAddressBo.active)
                {
                    return entityAddressBo;
                }
            }
        }

        injectBusinessObjectServiceIntoIdentityService();

        EntityAddressTypeBo firstAddressTypeBo = new EntityAddressTypeBo(code: "addresscodeone");
        EntityAddressBo newEntityAddressBo = new EntityAddressBo(entityId: "AAA", entityTypeCode: "typecodeone", addressType: firstAddressTypeBo, addressTypeCode: "addresscodeone");
        EntityAddress entityAddress = identityService.addAddressToEntity(EntityAddressBo.to(newEntityAddressBo));
    }

    @Test
    public void testAddAddressToEntitySucceeds() {
        EntityAddressTypeBo firstAddressTypeBo = new EntityAddressTypeBo(code: "addresscodethree");
        EntityAddressBo newEntityAddressBo = new EntityAddressBo(entityId: "CCC", entityTypeCode: "typecodethree", addressType: firstAddressTypeBo, addressTypeCode: "addresscodethree");

        mockBoService.demand.findByPrimaryKey(1..sampleEntityAddresses.size()) {
            Class clazz, Map map -> for (EntityAddressBo entityAddressBo in sampleEntityAddresses.values()) {
                if (entityAddressBo.entityId.equals(map.get("entityId"))
                    && entityAddressBo.entityTypeCode.equals(map.get("entityTypeCode"))
                    && entityAddressBo.addressTypeCode.equals(map.get("addressTypeCode"))
                    && entityAddressBo.active)
                {
                    return entityAddressBo;
                }
            }
        }

        mockBoService.demand.save(1..1) {
            EntityAddressBo bo -> return newEntityAddressBo;
        }

        injectBusinessObjectServiceIntoIdentityService();

        EntityAddress entityAddress = identityService.addAddressToEntity(EntityAddressBo.to(newEntityAddressBo));

        Assert.assertEquals(EntityAddressBo.to(newEntityAddressBo), entityAddress);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateAddressWithNullFails() {
        EntityAddress entityAddress = identityService.updateAddress(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testUpdateAddressWithNonExistingAddressFails() {
        mockBoService.demand.findByPrimaryKey(1..sampleEntityAddresses.size()) {
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

        injectBusinessObjectServiceIntoIdentityService();

        EntityAddressTypeBo firstAddressTypeBo = new EntityAddressTypeBo(code: "addresscodethree");
        EntityAddressBo newEntityAddressBo = new EntityAddressBo(entityId: "CCC", entityTypeCode: "typecodethree", addressType: firstAddressTypeBo, addressTypeCode: "addresscodethree");
        EntityAddress entityAddress = identityService.updateAddress(EntityAddressBo.to(newEntityAddressBo));
    }

    @Test
    public void testUpdateAddressSucceeds() {
        EntityAddressTypeBo firstAddressTypeBo = new EntityAddressTypeBo(code: "addresscodeone");
        EntityAddressBo existingEntityAddressBo = new EntityAddressBo(entityId: "AAA", entityTypeCode: "typecodeone", id: "addressidone", addressType: firstAddressTypeBo, addressTypeCode: "addresscodeone");

        mockBoService.demand.findByPrimaryKey(1..sampleEntityAddresses.size()) {
            Class clazz, Map map -> for (EntityAddressBo entityAddressBo in sampleEntityAddresses.values()) {
                if (entityAddressBo.entityId.equals(map.get("entityId"))
                    && entityAddressBo.entityTypeCode.equals(map.get("entityTypeCode"))
                    && entityAddressBo.addressTypeCode.equals(map.get("addressTypeCode"))
                    && entityAddressBo.active)
                {
                    return entityAddressBo;
                }
            }
        }

        mockBoService.demand.save(1..1) {
            EntityAddressBo bo -> return existingEntityAddressBo;
        }

        injectBusinessObjectServiceIntoIdentityService();

        EntityAddress entityAddress = identityService.updateAddress(EntityAddressBo.to(existingEntityAddressBo));

        Assert.assertEquals(EntityAddressBo.to(existingEntityAddressBo), entityAddress);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testInactivateAddressWithNonExistentAddressFails() {
        mockBoService.demand.findByPrimaryKey(1..1) {
            Class clazz, Map map -> return null;
        }

        injectBusinessObjectServiceIntoIdentityService();

        EntityAddress entityAddress = identityService.inactivateAddress("new");
    }

    @Test
    public void testInactivateAddressSucceeds()
    {
        EntityAddressBo existingEntityAddressBo = sampleEntityAddresses.get("AAA");
        EntityAddressTypeBo firstAddressTypeBo = new EntityAddressTypeBo(code: "addresscodeone");
        EntityAddressBo inactiveEntityAddressBo = new EntityAddressBo(entityId: "AAA", entityTypeCode: "typecodeone", addressType: firstAddressTypeBo, id: "addressidone", addressTypeCode: "addresscodeone", active: false);

        mockBoService.demand.findByPrimaryKey(1..sampleEntityAddresses.size()) {
            Class clazz, Map map -> for (EntityAddressBo entityAddressBo in sampleEntityAddresses.values()) {
                if (entityAddressBo.id.equals(map.get("id"))) {
                    return entityAddressBo;
                }
            }
        }

        mockBoService.demand.save(1..1) {
            EntityAddressBo bo -> return inactiveEntityAddressBo;
        }

        injectBusinessObjectServiceIntoIdentityService();

        EntityAddress inactiveEntityAddress = identityService.inactivateAddress(existingEntityAddressBo.id);

        Assert.assertEquals(EntityAddressBo.to(inactiveEntityAddressBo), inactiveEntityAddress);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testaddEmailToEntityWithNullFails() {
        EntityEmail entityEmail = identityService.addEmailToEntity(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testAddEmailToEntityWithExistingEmailFails() {
        mockBoService.demand.findByPrimaryKey(1..sampleEntityEmails.size()) {
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

        injectBusinessObjectServiceIntoIdentityService();

        EntityEmailTypeBo firstEmailTypeBo = new EntityEmailTypeBo(code: "emailcodeone");
        EntityEmailBo newEntityEmailBo = new EntityEmailBo(entityId: "AAA", entityTypeCode: "typecodeone", emailType: firstEmailTypeBo, emailTypeCode: "emailcodeone", active: true);
        EntityEmail entityEmail = identityService.addEmailToEntity(EntityEmailBo.to(newEntityEmailBo));
    }

    @Test
    public void testAddEmailToEntitySucceeds() {
        EntityEmailTypeBo firstEmailTypeBo = new EntityEmailTypeBo(code: "emailcodethree");
        EntityEmailBo newEntityEmailBo = new EntityEmailBo(entityId: "CCC", entityTypeCode: "typecodethree", emailType: firstEmailTypeBo, emailTypeCode: "emailcodethree", active: true);

        mockBoService.demand.findByPrimaryKey(1..sampleEntityEmails.size()) {
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

        mockBoService.demand.save(1..1) {
            EntityEmailBo bo -> return newEntityEmailBo;
        }

        injectBusinessObjectServiceIntoIdentityService();

        EntityEmail entityEmail = identityService.addEmailToEntity(EntityEmailBo.to(newEntityEmailBo));

        Assert.assertEquals(EntityEmailBo.to(newEntityEmailBo), entityEmail);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateEmailWithNullFails() {
        EntityEmail entityEmail = identityService.updateEmail(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testUpdateEmailWithNonExistingEmailFails() {
        mockBoService.demand.findByPrimaryKey(1..sampleEntityEmails.size()) {
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

        injectBusinessObjectServiceIntoIdentityService();

        EntityEmailTypeBo firstEmailTypeBo = new EntityEmailTypeBo(code: "emailcodethree");
        EntityEmailBo newEntityEmailBo = new EntityEmailBo(entityId: "CCC", entityTypeCode: "typecodethree", emailType: firstEmailTypeBo, emailTypeCode: "emailcodethree", active: true);
        EntityEmail entityEmail = identityService.updateEmail(EntityEmailBo.to(newEntityEmailBo));
    }

    @Test
    public void testUpdateEmailSucceeds() {
        EntityEmailTypeBo firstEmailTypeBo = new EntityEmailTypeBo(code: "emailcodeone");
        EntityEmailBo existingEntityEmailBo = new EntityEmailBo(entityId: "AAA", entityTypeCode: "typecodeone", emailType: firstEmailTypeBo, id:"emailidone", emailTypeCode: "emailcodeone", active: true);

        mockBoService.demand.findByPrimaryKey(1..sampleEntityEmails.size()) {
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

        mockBoService.demand.save(1..1) {
            EntityEmailBo bo -> return existingEntityEmailBo;
        }

        injectBusinessObjectServiceIntoIdentityService();

        EntityEmail entityEmail = identityService.updateEmail(EntityEmailBo.to(existingEntityEmailBo));

        Assert.assertEquals(EntityEmailBo.to(existingEntityEmailBo), entityEmail);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testInactivateEmailWithNonExistentEmailFails() {
        mockBoService.demand.findByPrimaryKey(1..1) {
            Class clazz, Map map -> return null;
        }

        injectBusinessObjectServiceIntoIdentityService();

        EntityEmail entityEmail = identityService.inactivateEmail("new");
    }

    @Test
    public void testInactivateEmailSucceeds()
    {
        EntityEmailBo existingEntityEmailBo = sampleEntityEmails.get("AAA");
        EntityEmailTypeBo firstEmailTypeBo = new EntityEmailTypeBo(code: "emailcodeone");
        EntityEmailBo inactiveEntityEmailBo = new EntityEmailBo(entityId: "AAA", entityTypeCode: "typecodeone", emailType: firstEmailTypeBo, id:"emailidone", emailTypeCode: "emailcodeone", active: false);


        mockBoService.demand.findByPrimaryKey(1..sampleEntityEmails.size()) {
            Class clazz, Map map -> for (EntityEmailBo entityEmailBo in sampleEntityEmails.values()) {
                if (entityEmailBo.id.equals(map.get("id"))) {
                    return entityEmailBo;
                }
            }
        }

        mockBoService.demand.save(1..1) {
            EntityEmailBo bo -> return inactiveEntityEmailBo;
        }

        injectBusinessObjectServiceIntoIdentityService();

        EntityEmail inactiveEntityEmail = identityService.inactivateEmail(existingEntityEmailBo.id);

        Assert.assertEquals(EntityEmailBo.to(inactiveEntityEmailBo), inactiveEntityEmail);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testaddPhoneToEntityWithNullFails() {
        EntityPhone entityPhone = identityService.addPhoneToEntity(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testAddPhoneToEntityWithExistingPhoneFails() {
        mockBoService.demand.findByPrimaryKey(1..sampleEntityPhones.size()) {
            Class clazz, Map map -> for (EntityPhoneBo entityPhoneBo in sampleEntityPhones.values()) {
                if (entityPhoneBo.entityId.equals(map.get("entityId"))
                    && entityPhoneBo.entityTypeCode.equals(map.get("entityTypeCode"))
                    && entityPhoneBo.phoneTypeCode.equals(map.get("phoneTypeCode"))
                    && entityPhoneBo.active)
                {
                    return entityPhoneBo;
                }
            }
        }

        injectBusinessObjectServiceIntoIdentityService();

        EntityPhoneTypeBo firstPhoneType = new EntityPhoneTypeBo(code: "phonetypecodeone");
        EntityPhoneBo newEntityPhoneBo = new EntityPhoneBo(entityId: "AAA", entityTypeCode: "typecodeone", phoneType: firstPhoneType, id: "phoneidone", phoneTypeCode: "phonetypecodeone", active: true);
        EntityPhone entityPhone = identityService.addPhoneToEntity(EntityPhoneBo.to(newEntityPhoneBo));
    }

    @Test
    public void testAddPhoneToEntitySucceeds() {
        EntityPhoneTypeBo firstPhoneType = new EntityPhoneTypeBo(code: "phonetypecodethree");
        EntityPhoneBo newEntityPhoneBo = new EntityPhoneBo(entityId: "CCC", entityTypeCode: "typecodethree", phoneType: firstPhoneType, id: "phoneidthree", phoneTypeCode: "phonetypecodethree", active: true);

        mockBoService.demand.findByPrimaryKey(1..sampleEntityPhones.size()) {
            Class clazz, Map map -> for (EntityPhoneBo entityPhoneBo in sampleEntityPhones.values()) {
                if (entityPhoneBo.entityId.equals(map.get("entityId"))
                    && entityPhoneBo.entityTypeCode.equals(map.get("entityTypeCode"))
                    && entityPhoneBo.phoneTypeCode.equals(map.get("phoneTypeCode"))
                    && entityPhoneBo.active)
                {
                    return entityPhoneBo;
                }
            }
        }

        mockBoService.demand.save(1..1) {
            EntityPhoneBo bo -> return newEntityPhoneBo;
        }

        injectBusinessObjectServiceIntoIdentityService();

        EntityPhone entityPhone = identityService.addPhoneToEntity(EntityPhoneBo.to(newEntityPhoneBo));

        Assert.assertEquals(EntityPhoneBo.to(newEntityPhoneBo), entityPhone);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdatePhoneWithNullFails() {
        EntityPhone entityPhone = identityService.updatePhone(null);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testUpdatePhoneWithNonExistingPhoneFails() {
        mockBoService.demand.findByPrimaryKey(1..sampleEntityPhones.size()) {
            Class clazz, Map map -> for (EntityPhoneBo entityPhoneBo in sampleEntityPhones.values()) {
                if (entityPhoneBo.entityId.equals(map.get("entityId"))
                    && entityPhoneBo.entityTypeCode.equals(map.get("entityTypeCode"))
                    && entityPhoneBo.phoneTypeCode.equals(map.get("phoneTypeCode"))
                    && entityPhoneBo.active)
                {
                    return entityPhoneBo;
                }
            }
        }

        injectBusinessObjectServiceIntoIdentityService();

        EntityPhoneTypeBo firstPhoneType = new EntityPhoneTypeBo(code: "phonetypecodethree");
        EntityPhoneBo newEntityPhoneBo = new EntityPhoneBo(entityId: "CCC", entityTypeCode: "typecodethree", phoneType: firstPhoneType, id: "phoneidthree", phoneTypeCode: "phonetypecodethree", active: true);
        EntityPhone entityPhone = identityService.updatePhone(EntityPhoneBo.to(newEntityPhoneBo));
    }

    @Test
    public void testUpdatePhoneSucceeds() {
        EntityPhoneTypeBo firstPhoneType = new EntityPhoneTypeBo(code: "phonetypecodeone");
        EntityPhoneBo existingEntityPhoneBo = new EntityPhoneBo(entityId: "AAA", entityTypeCode: "typecodeone", phoneType: firstPhoneType, id: "phoneidone", phoneTypeCode: "phonetypecodeone", active: true);

        mockBoService.demand.findByPrimaryKey(1..sampleEntityPhones.size()) {
            Class clazz, Map map -> for (EntityPhoneBo entityPhoneBo in sampleEntityPhones.values()) {
                if (entityPhoneBo.entityId.equals(map.get("entityId"))
                    && entityPhoneBo.entityTypeCode.equals(map.get("entityTypeCode"))
                    && entityPhoneBo.phoneTypeCode.equals(map.get("phoneTypeCode"))
                    && entityPhoneBo.active)
                {
                    return entityPhoneBo;
                }
            }
        }

        mockBoService.demand.save(1..1) {
            EntityPhoneBo bo -> return existingEntityPhoneBo;
        }

        injectBusinessObjectServiceIntoIdentityService();

        EntityPhone entityPhone = identityService.updatePhone(EntityPhoneBo.to(existingEntityPhoneBo));

        Assert.assertEquals(EntityPhoneBo.to(existingEntityPhoneBo), entityPhone);
    }

    @Test(expected = RiceIllegalStateException.class)
    public void testInactivatePhoneWithNonExistentPhoneFails() {
        mockBoService.demand.findByPrimaryKey(1..1) {
            Class clazz, Map map -> return null;
        }

        injectBusinessObjectServiceIntoIdentityService();

        EntityPhone entityPhone = identityService.inactivatePhone("new");
    }

    @Test
    public void testInactivatePhoneSucceeds()
    {
        EntityPhoneBo existingEntityPhoneBo = sampleEntityPhones.get("AAA");
        EntityPhoneTypeBo firstPhoneType = new EntityPhoneTypeBo(code: "phonetypecodeone");
        EntityPhoneBo inactiveEntityPhoneBo = new EntityPhoneBo(entityId: "AAA", entityTypeCode: "typecodeone", phoneType: firstPhoneType, id: "phoneidone", phoneTypeCode: "phonetypecodeone", active: false);

        mockBoService.demand.findByPrimaryKey(1..sampleEntityPhones.size()) {
            Class clazz, Map map -> for (EntityPhoneBo entityPhoneBo in sampleEntityPhones.values()) {
                if (entityPhoneBo.id.equals(map.get("id"))) {
                    return entityPhoneBo;
                }
            }
        }

        mockBoService.demand.save(1..1) {
            EntityPhoneBo bo -> return inactiveEntityPhoneBo;
        }

        injectBusinessObjectServiceIntoIdentityService();

        EntityPhone inactiveEntityPhone = identityService.inactivatePhone(existingEntityPhoneBo.id);

        Assert.assertEquals(EntityPhoneBo.to(inactiveEntityPhoneBo), inactiveEntityPhone);
    }
}
