package org.kuali.rice.kim.service.impl

import org.junit.Test;
import groovy.mock.interceptor.MockFor;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.junit.Before;
import org.kuali.rice.kim.api.identity.entity.Entity
import org.kuali.rice.kim.api.identity.IdentityService;

class IdentityServiceImplTest {
    private final shouldFail = new GroovyTestCase().&shouldFail

    private MockFor mockBoService
    private BusinessObjectService boService
    IdentityService identityService;
    IdentityServiceImpl identityServiceImpl;

    @Before
    void setupMockContext() {
        mockBoService = new MockFor(BusinessObjectService.class)
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
        injectBusinessObjectServiceIntoIdentityService()

        shouldFail(IllegalArgumentException.class) {
            identityService.updateEntity(null)
        }
        mockBoService.verify(boService)
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEntityEmptyId() {
        Entity entity = identityService.getEntity("");
    }
}
