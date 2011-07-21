package org.kuali.rice.kim.service.impl

import org.junit.Test;
import groovy.mock.interceptor.MockFor;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.kuali.rice.kim.api.identity.entity.Entity
import org.kuali.rice.kim.api.identity.IdentityService;
import org.junit.Assert
import org.kuali.rice.kim.impl.identity.entity.EntityBo
import org.kuali.rice.kim.impl.identity.privacy.EntityPrivacyPreferencesBo
import org.kuali.rice.kim.impl.identity.personal.EntityBioDemographicsBo
import java.text.SimpleDateFormat
import org.kuali.rice.krad.service.PersistenceService
import org.kuali.rice.kim.impl.identity.principal.PrincipalBo
import org.kuali.rice.kim.api.identity.principal.Principal
import org.kuali.rice.core.api.exception.RiceIllegalStateException

class IdentityServiceImplTest {
    private final shouldFail = new GroovyTestCase().&shouldFail

    private MockFor mockBoService;
    private MockFor mockPersistenceService;
    private BusinessObjectService boService;
    private PersistenceService persistenceService;
    IdentityService identityService;
    IdentityServiceImpl identityServiceImpl;

    static Map<String, EntityBo> sampleEntities = new HashMap<String, EntityBo>();
    static Map<String, EntityBo> samplePrincipals = new HashMap<String, PrincipalBo>();

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
        List<PrincipalBo> firstPrincipals = new ArrayList<PrincipalBo>();
        firstPrincipals.add(firstEntityPrincipal);
        EntityBo firstEntityBo = new EntityBo(active: true, id: "AAA", privacyPreferences: firstEntityPrivacyPreferencesBo, bioDemographics: firstEntityBioDemographicsBo, principals: firstPrincipals);

        EntityPrivacyPreferencesBo secondEntityPrivacyPreferencesBo = new EntityPrivacyPreferencesBo(entityId: "BBB", suppressName: true, suppressEmail: true, suppressAddress: true, suppressPhone: true, suppressPersonal: false);
        EntityBioDemographicsBo secondEntityBioDemographicsBo = new EntityBioDemographicsBo(entityId: "BBB", birthDateValue: birthDate, genderCode: "M", deceasedDateValue: deceasedDate, maritalStatusCode: "S", primaryLanguageCode: "EN", secondaryLanguageCode: "FR", countryOfBirthCode: "US", birthStateCode: "IN", cityOfBirth: "Bloomington", geographicOrigin: "None", suppressPersonal: false);
        PrincipalBo secondEntityPrincipal = new PrincipalBo(entityId: "BBB", principalId: "P2", active: true, principalName: "second", versionNumber: 1, password: "second_password");
        List<PrincipalBo> secondPrincipals = new ArrayList<PrincipalBo>();
        secondPrincipals.add(secondEntityPrincipal);
        EntityBo secondEntityBo = new EntityBo(active: true, id: "BBB", privacyPreferences: secondEntityPrivacyPreferencesBo, bioDemographics: secondEntityBioDemographicsBo, principals: secondPrincipals);

        for (bo in [firstEntityBo, secondEntityBo]) {
            sampleEntities.put(bo.id, bo)
        }

        for (bo in [firstEntityPrincipal, secondEntityPrincipal]) {
            samplePrincipals.put(bo.principalId, bo);
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
}
