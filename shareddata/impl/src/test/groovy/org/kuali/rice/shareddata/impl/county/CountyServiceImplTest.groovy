/*
 * Copyright 2006-2011 The Kuali Foundation
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





package org.kuali.rice.shareddata.impl.county

import groovy.mock.interceptor.MockFor
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.kuali.rice.kns.service.BusinessObjectService

class CountyServiceImplTest {
    private final shouldFail = new GroovyTestCase().&shouldFail

    static sampleCounties = new HashMap<List<String>, CountyBo>()
    static sampleCountiesPerCountryState = new HashMap<List<String>, List<CountyBo>>()

    private def MockFor mockBoService
    private CountyServiceImpl cservice;

    @BeforeClass
    static void createSamplePostalCodeBOs() {
        def shiBo = new CountyBo(active: true, countryCode: "US", stateCode: "MI", code: "shi",
                name: "Shiawassee ")
        def laBo = new CountyBo(active: true, countryCode: "US", stateCode: "CA", code: "la",
                name: "Los Angeles")
        //no clue if CA has counties :-)
        def tiBo = new CountyBo(active: true, countryCode: "CA", stateCode: "BC", code: "ti",
                name: "Texada Island")
        [shiBo, laBo, tiBo].each {
            sampleCounties[[it.code, it.countryCode, it.stateCode].asImmutable()] = it
        }
        sampleCountiesPerCountryState[["US", "MI"].asImmutable()] = [shiBo]
        sampleCountiesPerCountryState[["US", "CA"].asImmutable()] = [laBo]
        sampleCountiesPerCountryState[["CA", "BC"].asImmutable()] = [tiBo]
    }

    @Before
    void setupBoServiceMockContext() {
        mockBoService = new MockFor(BusinessObjectService)
        cservice = new CountyServiceImpl()
    }

    @Test
    void test_getCounty_null_countryCode() {
        def boService = mockBoService.proxyDelegateInstance()
        cservice.setBusinessObjectService(boService);

        shouldFail(IllegalArgumentException.class) {
            cservice.getCounty(null, "MI", "48848")
        }
        mockBoService.verify(boService)
    }

    @Test
    void test_getPostalCode_null_stateCode() {
        def boService = mockBoService.proxyDelegateInstance()
        cservice.setBusinessObjectService(boService);

        shouldFail(IllegalArgumentException.class) {
            cservice.getCounty("US", null, "48848")
        }
        mockBoService.verify(boService)
    }

    @Test
    void test_getPostalCode_null_code() {
        def boService = mockBoService.proxyDelegateInstance()
        cservice.setBusinessObjectService(boService);

        shouldFail(IllegalArgumentException.class) {
            cservice.getCounty("US", "MI", null)
        }
        mockBoService.verify(boService)
    }

    @Test
    void test_get_county_exists() {
        mockBoService.demand.findByPrimaryKey (1..1) { clazz, map -> sampleCounties[map["countryCode"], map["stateCode"], [map["code"]]] }
        def boService = mockBoService.proxyDelegateInstance()
        cservice.setBusinessObjectService(boService);
        Assert.assertEquals (CountyBo.to(sampleCounties[["US", "48848"]]), cservice.getCounty("US", "MI", "shi"))
        mockBoService.verify(boService)
    }

    @Test
    void test_get_county_does_not_exist() {
        mockBoService.demand.findByPrimaryKey (1..1) { clazz, map -> sampleCounties[map["countryCode"],  map["stateCode"], [map["code"]]] }
        def boService = mockBoService.proxyDelegateInstance()
        cservice.setBusinessObjectService(boService);
        Assert.assertNull (cservice.getCounty("FOO", "BAR", "BAZ"))
        mockBoService.verify(boService)
    }

    @Test
    void test_getAllPostalCodesInCountryAndState_null_countryCode() {
        def boService = mockBoService.proxyDelegateInstance()
        cservice.setBusinessObjectService(boService);

        shouldFail(IllegalArgumentException.class) {
            cservice.findAllCountiesInCountryAndState(null, "MI")
        }
        mockBoService.verify(boService)
    }

    @Test
    void test_getAllPostalCodesInCountryAndState_null_stateCode() {
        def boService = mockBoService.proxyDelegateInstance()
        cservice.setBusinessObjectService(boService);

        shouldFail(IllegalArgumentException.class) {
            cservice.findAllCountiesInCountryAndState("US", null)
        }
        mockBoService.verify(boService)
    }

    @Test
    void test_find_all_county_in_country_state_exists() {
        mockBoService.demand.findMatching (1..1) { clazz, map -> sampleCountiesPerCountryState[map["countryCode"], map["stateCode"]] }
        def boService = mockBoService.proxyDelegateInstance()
        cservice.setBusinessObjectService(boService);
        def values = cservice.findAllCountiesInCountryAndState("US", "MI")
        Assert.assertEquals (sampleCountiesPerCountryState[["US", "MI"]].collect { CountyBo.to(it) }, values)

        //is this unmodifiable?
        shouldFail(UnsupportedOperationException.class) {
            values.add(CountyBo.to(sampleCounties[["CA", "MI", "shi"]]))
        }
        mockBoService.verify(boService)
    }

    @Test
    void test_find_all_county_in_country_state_does_not_exist() {
        mockBoService.demand.findMatching (1..1) { clazz, map -> sampleCountiesPerCountryState[map["countryCode"], map["stateCode"]] }
        def boService = mockBoService.proxyDelegateInstance()
        cservice.setBusinessObjectService(boService);
        def values = cservice.findAllCountiesInCountryAndState("FOO", "BAR")
        Assert.assertEquals ([], values)

        //is this unmodifiable?
        shouldFail(UnsupportedOperationException.class) {
            values.add(CountyBo.to(sampleCounties[["CA", "MI", "shi"]]))
        }

        mockBoService.verify(boService)
    }
}
