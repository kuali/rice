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
import org.junit.Before
import org.junit.Test
import org.kuali.rice.kns.service.BusinessObjectService

class PostalCodeServiceImplTest {

    private final shouldFail = new GroovyTestCase().&shouldFail

    private def MockFor mockBoService
    private PostalCodeServiceImpl pcservice;

    @Before
    void setupBoServiceMockContext() {
        mockBoService = new MockFor(BusinessObjectService)
        pcservice = new PostalCodeServiceImpl()
    }

    @Test
    void test_getPostalCode_null_countryCode() {
        def boService = mockBoService.proxyDelegateInstance()
        pcservice.setBusinessObjectService(boService);

        shouldFail(IllegalArgumentException.class) {
            pcservice.getPostalCode(null, "48848")
        }
        mockBoService.verify(boService)
    }

    @Test
    void test_getPostalCode_null_code() {
        def boService = mockBoService.proxyDelegateInstance()
        pcservice.setBusinessObjectService(boService);

        shouldFail(IllegalArgumentException.class) {
            pcservice.getPostalCode("US", null)
        }
        mockBoService.verify(boService)
    }

    @Test
    void test_findAllPostalCodesInCountry_null_countryCode() {
        def boService = mockBoService.proxyDelegateInstance()
        pcservice.setBusinessObjectService(boService);

        shouldFail(IllegalArgumentException.class) {
            pcservice.findAllPostalCodesInCountry(null)
        }
        mockBoService.verify(boService)
    }
}
