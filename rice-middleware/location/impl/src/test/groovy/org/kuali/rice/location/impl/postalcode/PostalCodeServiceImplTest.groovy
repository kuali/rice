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
package org.kuali.rice.location.impl.postalcode

import groovy.mock.interceptor.MockFor
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.kuali.rice.krad.service.BusinessObjectService
import org.kuali.rice.location.api.postalcode.PostalCodeService
import org.kuali.rice.krad.data.DataObjectService
import org.kuali.rice.location.impl.county.CountyBo
import org.kuali.rice.core.api.criteria.GenericQueryResults

class PostalCodeServiceImplTest {

    private final shouldFail = new GroovyTestCase().&shouldFail

    static samplePostalCodes = new HashMap<List<String>, PostalCodeBo>()
    static samplePostalCodesPerCountry = new HashMap<String, List<PostalCodeBo>>()

    PostalCodeServiceImpl postalCodeServiceImpl
    PostalCodeService postalCodeService
    MockFor dataObjectServiceMockFor
    DataObjectService dataObjectService

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
    void setupDataObjectServiceMockContext(){
        dataObjectServiceMockFor = new MockFor(DataObjectService)
    }

    @Before
    void setupServiceUnderTest() {
        postalCodeServiceImpl = new PostalCodeServiceImpl()
        postalCodeService = postalCodeServiceImpl
    }

    void injectDataObjectService(){
        dataObjectService = dataObjectServiceMockFor.proxyDelegateInstance()
        postalCodeServiceImpl.setDataObjectService(dataObjectService)
    }

    @Test
    void test_get_postal_code_null_countryCode() {
        injectDataObjectService()
        shouldFail(IllegalArgumentException) {
            postalCodeService.getPostalCode(null, "48848")
        }
        dataObjectServiceMockFor.verify(dataObjectService)
    }

    @Test
    void test_get_postal_code_null_code() {
        injectDataObjectService()
        shouldFail(IllegalArgumentException) {
            postalCodeService.getPostalCode("US", null)
        }
        dataObjectServiceMockFor.verify(dataObjectService)
    }

    @Test
    void test_get_postal_code_exists() {
        dataObjectServiceMockFor.demand.find(1..1) {
            clazz, id -> samplePostalCodes[["US","48848"]]
        }
        injectDataObjectService()
        Assert.assertEquals(PostalCodeBo.to(samplePostalCodes[["US", "48848"]]), postalCodeService.getPostalCode("US", "48848"))
        dataObjectServiceMockFor.verify(dataObjectService)
    }

    @Test
    void test_get_postal_code_does_not_exist() {
        dataObjectServiceMockFor.demand.find(1..1) {
            clazz, id -> null
        }
        injectDataObjectService()
        Assert.assertNull(postalCodeService.getPostalCode("FOO", "BAR"))
        dataObjectServiceMockFor.verify(dataObjectService)
    }

    @Test
    void test_find_all_postal_codes_in_country_null_countryCode() {
        injectDataObjectService()
        shouldFail(IllegalArgumentException) {
            postalCodeService.findAllPostalCodesInCountry(null)
        }
        dataObjectServiceMockFor.verify(dataObjectService)
    }

    @Test
    void test_find_all_postal_codes_in_country_exists() {
        Collection<PostalCodeBo> postalCodeBo = samplePostalCodesPerCountry["US"]
        List<PostalCodeBo> postalCodeBoList = new ArrayList<PostalCodeBo>()
        postalCodeBoList.addAll(postalCodeBo)
        GenericQueryResults.Builder<PostalCodeBo> builder = GenericQueryResults.Builder.create()

        builder.setResults(postalCodeBoList)
        dataObjectServiceMockFor.demand.findMatching(1..1){
            clazz,queryByCriteria -> builder.build()
        }
        injectDataObjectService()
        def values = postalCodeService.findAllPostalCodesInCountry("US")
        Assert.assertEquals(samplePostalCodesPerCountry["US"].collect { PostalCodeBo.to(it) }, values)

        //is this unmodifiable?
        shouldFail(UnsupportedOperationException) {
            values.add(PostalCodeBo.to(samplePostalCodes[["CA", "604"]]))
        }
        dataObjectServiceMockFor.verify(dataObjectService)
    }

    @Test
    void test_find_all_postal_codes_in_country_does_not_exist() {
        dataObjectServiceMockFor.demand.findMatching(1..1){
            clazz,queryByCriteria -> null
        }
        injectDataObjectService()
        def values = postalCodeService.findAllPostalCodesInCountry("FOO")
        Assert.assertEquals([], values)

        //is this unmodifiable?
        shouldFail(UnsupportedOperationException) {
            values.add(PostalCodeBo.to(samplePostalCodes[["CA", "604"]]))
        }

        dataObjectServiceMockFor.verify(dataObjectService)
    }
}
