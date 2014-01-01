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
package org.kuali.rice.location.impl.county

import groovy.mock.interceptor.MockFor
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.kuali.rice.krad.service.BusinessObjectService
import org.kuali.rice.location.api.county.CountyService
import org.kuali.rice.krad.data.DataObjectService
import org.kuali.rice.location.impl.country.CountryBo
import org.kuali.rice.core.api.criteria.GenericQueryResults

class CountyServiceImplTest {
    private final shouldFail = new GroovyTestCase().&shouldFail

    static sampleCounties = new HashMap<List<String>, CountyBo>()
    static sampleCountiesPerCountryState = new HashMap<List<String>, List<CountyBo>>()

    private MockFor dataObjectServiceMock
    private DataObjectService dataObjectService
    CountyService countyService
    CountyServiceImpl countyServiceImpl


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
    void setupDataObjectServiceMockContext() {
        dataObjectServiceMock = new MockFor(DataObjectService)
    }

    @Before
    void setupServiceUnderTest() {
        countyServiceImpl = new CountyServiceImpl()
        countyService = countyServiceImpl
    }

    void injectDataObjectService(){
        dataObjectService = dataObjectServiceMock.proxyDelegateInstance()
        countyServiceImpl.setDataObjectService(dataObjectService)
    }

    @Test
    void test_getCounty_null_countryCode() {
        injectDataObjectService()

        shouldFail(IllegalArgumentException) {
            countyService.getCounty(null, "MI", "48848")
        }
        dataObjectServiceMock.verify(dataObjectService)
    }

    @Test
    void test_getPostalCode_null_stateCode() {
        injectDataObjectService()

        shouldFail(IllegalArgumentException) {
            countyService.getCounty("US", null, "48848")
        }
        dataObjectServiceMock.verify(dataObjectService)
    }

    @Test
    void test_getPostalCode_null_code() {
        injectDataObjectService()

        shouldFail(IllegalArgumentException) {
            countyService.getCounty("US", "MI", null)
        }
        dataObjectServiceMock.verify(dataObjectService)
    }

    @Test
    void test_get_county_exists() {
        dataObjectServiceMock.demand.find(1..1) {clazz, id -> sampleCounties[id.getKeys().get("countryCode"),
                id.getKeys().get("stateCode"),id.getKeys().get("code")] }
        injectDataObjectService()
        Assert.assertEquals(CountyBo.to(sampleCounties[["US", "48848"]]), countyService.getCounty("US", "MI", "shi"))
        dataObjectServiceMock.verify(dataObjectService)
    }

    @Test
    void test_get_county_does_not_exist() {
        dataObjectServiceMock.demand.find(1..1) {clazz, id -> sampleCounties[id.getKeys().get("countryCode"),
                id.getKeys().get("stateCode"),id.getKeys().get("code")] }
        injectDataObjectService()

        Assert.assertNull(countyService.getCounty("FOO", "BAR", "BAZ"))
        dataObjectServiceMock.verify(dataObjectService)
    }

    @Test
    void test_getAllPostalCodesInCountryAndState_null_countryCode() {
        injectDataObjectService()

        shouldFail(IllegalArgumentException) {
            countyService.findAllCountiesInCountryAndState(null, "MI")
        }
        dataObjectServiceMock.verify(dataObjectService)
    }

    @Test
    void test_getAllPostalCodesInCountryAndState_null_stateCode() {
        injectDataObjectService()

        shouldFail(IllegalArgumentException) {
            countyService.findAllCountiesInCountryAndState("US", null)
        }
        dataObjectServiceMock.verify(dataObjectService)
    }

    @Test
    void test_find_all_county_in_country_state_exists() {
        Collection<CountyBo> countyBo = sampleCountiesPerCountryState["US","MI"]
        List<CountyBo> countyBoList = new ArrayList<CountyBo>()
        countyBoList.addAll(countyBo)
        GenericQueryResults.Builder<CountyBo> builder = GenericQueryResults.Builder.create()

        builder.setResults(countyBoList)
        dataObjectServiceMock.demand.findMatching(1..1) {clazz, queryByCriteria -> builder.build()}
        injectDataObjectService()
        def values = countyService.findAllCountiesInCountryAndState("US", "MI")
        Assert.assertEquals(sampleCountiesPerCountryState[["US", "MI"]].collect { CountyBo.to(it) }, values)

        //is this unmodifiable?
        shouldFail(UnsupportedOperationException) {
            values.add(CountyBo.to(sampleCounties[["CA", "MI", "shi"]]))
        }
        dataObjectServiceMock.verify(dataObjectService)
    }

    @Test
    void test_find_all_county_in_country_state_does_not_exist() {
        List<CountyBo> countyBoList = new ArrayList<CountyBo>()
        GenericQueryResults.Builder<CountyBo> builder = GenericQueryResults.Builder.create()

        builder.setResults(countyBoList)

        dataObjectServiceMock.demand.findMatching(1..1) {clazz,queryByCriteria ->builder.build() }
        injectDataObjectService()
        def values = countyService.findAllCountiesInCountryAndState("FOO", "BAR")
        Assert.assertEquals([], values)

        //is this unmodifiable?
        shouldFail(UnsupportedOperationException) {
            values.add(CountyBo.to(sampleCounties[["CA", "MI", "shi"]]))
        }

        dataObjectServiceMock.verify(dataObjectService)
    }
}
