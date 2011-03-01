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







package org.kuali.rice.shareddata.impl.postalcode

import groovy.mock.interceptor.MockFor
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.kuali.rice.kns.service.BusinessObjectService

class PostalCodeServiceImplTest {

    private final shouldFail = new GroovyTestCase().&shouldFail

    static samplePostalCodes = new HashMap<List<String>, PostalCodeBo>()
    static samplePostalCodesPerCountry = new HashMap<String, List<PostalCodeBo>>()

    private def MockFor mockBoService
    private PostalCodeServiceImpl pcservice;

    @BeforeClass
    static void createSamplePostalCodeBOs() {
        def laingburgBo = new PostalCodeBo(active: true, countryCode: "US", stateCode: "MI", code: "48848",
                cityName: "Laingburg")
        def bevHillsBo = new PostalCodeBo(active: true, countryCode: "US", stateCode: "CA", code: "90210",
                cityName: "Bev Hills")
        def blubberBay = new PostalCodeBo(active: true, countryCode: "CA", stateCode: "BC", code: "604",
                cityName: "Blubber Bay")
        [laingburgBo, bevHillsBo, blubberBay].each {
            samplePostalCodes[[it.code, it.countryCode].asImmutable()] = it
        }
        samplePostalCodesPerCountry["US"] = [laingburgBo, bevHillsBo]
        samplePostalCodesPerCountry["CA"] = [blubberBay]
    }

    @Before
    void setupBoServiceMockContext() {
        mockBoService = new MockFor(BusinessObjectService)
        pcservice = new PostalCodeServiceImpl()
    }

    @Test
    void test_get_postal_code_null_countryCode() {
        def boService = mockBoService.proxyDelegateInstance()
        pcservice.setBusinessObjectService(boService);

        shouldFail(IllegalArgumentException.class) {
            pcservice.getPostalCode(null, "48848")
        }
        mockBoService.verify(boService)
    }

    @Test
    void test_get_postal_code_null_code() {
        def boService = mockBoService.proxyDelegateInstance()
        pcservice.setBusinessObjectService(boService);

        shouldFail(IllegalArgumentException.class) {
            pcservice.getPostalCode("US", null)
        }
        mockBoService.verify(boService)
    }

    @Test
    void test_get_postal_code_exists() {
        mockBoService.demand.findByPrimaryKey (1..1) { clazz, map -> samplePostalCodes[map["countryCode"], [map["code"]]] }
        def boService = mockBoService.proxyDelegateInstance()
        pcservice.setBusinessObjectService(boService);
        Assert.assertEquals (PostalCodeBo.to(samplePostalCodes[["US", "48848"]]), pcservice.getPostalCode("US", "48848"))
        mockBoService.verify(boService)
    }

    @Test
    void test_get_postal_code_does_not_exist() {
        mockBoService.demand.findByPrimaryKey (1..1) { clazz, map -> samplePostalCodes[map["countryCode"], [map["code"]]] }
        def boService = mockBoService.proxyDelegateInstance()
        pcservice.setBusinessObjectService(boService);
        Assert.assertNull (pcservice.getPostalCode("FOO", "BAR"))
        mockBoService.verify(boService)
    }

    @Test
    void test_find_all_postal_codes_in_country_null_countryCode() {
        def boService = mockBoService.proxyDelegateInstance()
        pcservice.setBusinessObjectService(boService);

        shouldFail(IllegalArgumentException.class) {
            pcservice.findAllPostalCodesInCountry(null)
        }
        mockBoService.verify(boService)
    }

    @Test
    void test_find_all_postal_codes_in_country_exists() {
        mockBoService.demand.findMatching (1..1) { clazz, map -> samplePostalCodesPerCountry[map["countryCode"]] }
        def boService = mockBoService.proxyDelegateInstance()
        pcservice.setBusinessObjectService(boService);
        def values = pcservice.findAllPostalCodesInCountry("US")
        Assert.assertEquals (samplePostalCodesPerCountry["US"].collect { PostalCodeBo.to(it) }, values)

        //is this unmodifiable?
        shouldFail(UnsupportedOperationException.class) {
            values.add(PostalCodeBo.to(samplePostalCodes[["CA", "604"]]))
        }
        mockBoService.verify(boService)
    }

    @Test
    void test_find_all_postal_codes_in_country_does_not_exist() {
        mockBoService.demand.findMatching (1..1) { clazz, map -> samplePostalCodesPerCountry[map["countryCode"]] }
        def boService = mockBoService.proxyDelegateInstance()
        pcservice.setBusinessObjectService(boService);
        def values = pcservice.findAllPostalCodesInCountry("FOO")
        Assert.assertEquals ([], values)

        //is this unmodifiable?
        shouldFail(UnsupportedOperationException.class) {
            values.add(PostalCodeBo.to(samplePostalCodes[["CA", "604"]]))
        }

        mockBoService.verify(boService)
    }
}
