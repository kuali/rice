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



package org.kuali.rice.shareddata.impl.state

import groovy.mock.interceptor.MockFor
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.kuali.rice.kns.service.BusinessObjectService

class StateServiceImplTest {

    private final shouldFail = new GroovyTestCase().&shouldFail

    static Map<String, StateBo> sampleStates = new HashMap<String, StateBo>()
    private def MockFor mockBoService
    private StateServiceImpl sservice;

    @BeforeClass
    static void createSampleStateBOs() {
        def michiganBo = new StateBo(active: true, countryCode: "US", code: "MI",
                name: "Michigan")
        def illinoisBo = new StateBo(active: true, countryCode: "US", code: "IL",
                name: "Illinois")
        def nowhereBo = new StateBo(active: true, countryCode: "ZZ", code: "ZZZ",
                name: "nowhere")
        [michiganBo, illinoisBo, nowhereBo].each {
            sampleStates.put(it.code, it)
        }
    }

    @Before
    void setupBoServiceMockContext() {
        mockBoService = new MockFor(BusinessObjectService)
        sservice = new StateServiceImpl()
    }

    @Test
    void test_getState_null_countryCode() {
        def boService = mockBoService.proxyDelegateInstance()
        sservice.setBusinessObjectService(boService);

        shouldFail(IllegalArgumentException.class) {
            sservice.getState(null, "MI")
        }
        mockBoService.verify(boService)
    }

    @Test
    void test_getState_null_code() {
        def boService = mockBoService.proxyDelegateInstance()
        sservice.setBusinessObjectService(boService);

        shouldFail(IllegalArgumentException.class) {
            sservice.getState("US", null)
        }
        mockBoService.verify(boService)
    }

    @Test
    void test_findAllStatesInCountry_null_countryCode() {
        def boService = mockBoService.proxyDelegateInstance()
        sservice.setBusinessObjectService(boService);

        shouldFail(IllegalArgumentException.class) {
            sservice.findAllStatesInCountry(null)
        }
        mockBoService.verify(boService)
    }
}
