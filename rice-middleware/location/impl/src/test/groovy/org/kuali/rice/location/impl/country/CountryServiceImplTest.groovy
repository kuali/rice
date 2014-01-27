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
package org.kuali.rice.location.impl.country

import groovy.mock.interceptor.MockFor
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.kuali.rice.core.api.criteria.GenericQueryResults
import org.kuali.rice.coreservice.framework.parameter.ParameterService
import org.kuali.rice.krad.data.DataObjectService
import org.kuali.rice.location.api.country.Country
import org.kuali.rice.location.api.country.CountryService

class CountryServiceImplTest {

    private final shouldFail = new GroovyTestCase().&shouldFail

    static Map<String, CountryBo> sampleCountries = new HashMap<String, CountryBo>()
    static Map<String, CountryBo> sampleCountriesKeyedByAltCode = new HashMap<String, CountryBo>()

    MockFor dataObjectServiceMockFor
    DataObjectService dataObjectService
    MockFor parameterServiceMockFor
    ParameterService parameterService
    CountryServiceImpl countryServiceImpl
    CountryService countryService

    @BeforeClass
    static void createSampleCountryBOs() {
        //Doing setup in a static context since bring up and down a server is an expensive operation
        CountryBo unitedStatesBo = new CountryBo(active: true, alternateCode: "USA", code: "US",
                name: "United States", restricted: false)
        CountryBo australiaBo = new CountryBo(active: true, alternateCode: "AU", code: "AUS",
                name: "Australia", restricted: false)
        CountryBo nowhereBo = new CountryBo(active: true, alternateCode: "ZZ", code: "ZZZ",
                name: "Australia", restricted: true)
        for (bo in [unitedStatesBo, australiaBo, nowhereBo]) {
            sampleCountries.put(bo.code, bo)
            sampleCountriesKeyedByAltCode.put(bo.alternateCode, bo)
        }
    }

    @Before
    void setupMockContext() {
        parameterServiceMockFor = new MockFor(ParameterService.class)
        dataObjectServiceMockFor = new MockFor(DataObjectService.class)
    }

    @Before
    void setupServiceUnderTest() {
        countryServiceImpl = new CountryServiceImpl()
        countryService = countryServiceImpl    //assign Interface type to implementation reference for unit test only
    }

    void injectParameterService() {
        parameterService = parameterServiceMockFor.proxyDelegateInstance()
        countryServiceImpl.setParameterService(parameterService)
    }

    void injectDataObjectService(){
        dataObjectService = dataObjectServiceMockFor.proxyDelegateInstance()
        countryServiceImpl.setDataObjectService(dataObjectService)
    }

    @Test
    public void test_getCountry() {
        dataObjectServiceMockFor.demand.find(1) { Class clazz, Object id -> return sampleCountries.get("US")}
        injectDataObjectService()

        Country country = countryService.getCountry("US")
        Assert.assertEquals(CountryBo.to(sampleCountries.get("US")), country)
        dataObjectServiceMockFor.verify(dataObjectService)
    }

    @Test
    public void testGetByPrimaryIdEmptyCountryCode() {
        shouldFail(IllegalArgumentException) {
            countryService.getCountry("")
        }
    }

    @Test
    public void testGetByPrimaryIdNullCountryCode() {
        shouldFail(IllegalArgumentException) {
            countryService.getCountry(null)
        }
    }

    @Test
    public void testGetByAlternateCode() {
        CountryBo countryBo = sampleCountriesKeyedByAltCode.get("USA")
        List<CountryBo> countryBoList = new ArrayList<CountryBo>()
        countryBoList.add(countryBo)
        GenericQueryResults.Builder<CountryBo> builder = GenericQueryResults.Builder.create()

         builder.setResults(countryBoList)
         dataObjectServiceMockFor.demand.findMatching(1){
            clazz,queryByCriteria -> builder.build()
        }
        injectDataObjectService()

        Country country = countryService.getCountryByAlternateCode("USA")
        Assert.assertEquals(CountryBo.to(sampleCountriesKeyedByAltCode.get("USA")), country)
        dataObjectServiceMockFor.verify(dataObjectService)
    }

    @Test
    public void testGetByAlternateCodeWhenNoneFound() {
        dataObjectServiceMockFor.demand.findMatching(1) {clazz, queryByCriteria -> GenericQueryResults.Builder.create().build()}
        injectDataObjectService()

        Country country = countryService.getCountryByAlternateCode("ZZ")
        Assert.assertNull(country)
        dataObjectServiceMockFor.verify(dataObjectService)

    }

    @Test
    public void testGetByAlternateCodeWhenMultipleFound() {
        CountryBo countryBo = sampleCountriesKeyedByAltCode.get("USA")
        List<CountryBo> countryBoList = new ArrayList<CountryBo>()
        countryBoList.add(countryBo)
        countryBo = sampleCountriesKeyedByAltCode.get("AU")
        countryBoList.add(countryBo)
        GenericQueryResults.Builder<CountryBo> builder = GenericQueryResults.Builder.create()

        builder.setResults(countryBoList)
        dataObjectServiceMockFor.demand.findMatching(1){
            clazz, queryByCriteria -> builder.build()
        }
        injectDataObjectService()
        shouldFail(IllegalStateException) {
            countryService.getCountryByAlternateCode("US")
        }
        dataObjectServiceMockFor.verify(dataObjectService)
    }

    @Test
    public void testGetByAlternateCodeWithEmptyCode() {
        shouldFail(IllegalArgumentException) {
            countryService.getCountryByAlternateCode(" ")
        }
    }

    @Test
    public void testGetByAlternateCodeWithNullCode() {
        shouldFail(IllegalArgumentException) {
            countryService.getCountryByAlternateCode(null)
        }
    }

    @Test
    public void findAllCountriesNotRestricted() {
        CountryBo countryBo = sampleCountries.get("USA")
        List<CountryBo> countryBoList = new ArrayList<CountryBo>()
        countryBoList.add(countryBo)
        countryBo = sampleCountries.get("AU")
        GenericQueryResults.Builder<CountryBo> builder = GenericQueryResults.Builder.create()
        dataObjectServiceMockFor.demand.findMatching(1){
            clazz, queryByCriteria -> builder.build()
        }


        injectDataObjectService()
        countryService.findAllCountriesNotRestricted()
        dataObjectServiceMockFor.verify(dataObjectService)
    }

    @Test
    public void findAllCountriesNotRestrictedReturnsImmutableList() {
        CountryBo countryBo = sampleCountries.get("USA")
        List<CountryBo> countryBoList = new ArrayList<CountryBo>()
        countryBoList.add(countryBo)
        countryBo = sampleCountries.get("AU")
        GenericQueryResults.Builder<CountryBo> builder = GenericQueryResults.Builder.create()


        dataObjectServiceMockFor.demand.findMatching(1){
            clazz,queryByCriteria -> builder.build()
        }


        injectDataObjectService()

        List<Country> countries = countryService.findAllCountriesNotRestricted()
        shouldFail(UnsupportedOperationException) {
            countries.add(null);
        }
        dataObjectServiceMockFor.verify(dataObjectService)

    }

    @Test
    public void testFindAllCountries() {
        CountryBo countryBo = sampleCountries.get("USA")
        List<CountryBo> countryBoList = new ArrayList<CountryBo>()
        countryBoList.add(countryBo)
        countryBo = sampleCountries.get("AU")
        countryBoList.add(countryBo)
        countryBo = sampleCountries.get("ZZ")
        GenericQueryResults.Builder<CountryBo> builder = GenericQueryResults.Builder.create()


        dataObjectServiceMockFor.demand.findMatching(1){
            clazz,queryByCriteria -> builder.build()
        }


        injectDataObjectService()
        countryService.findAllCountries()
        dataObjectServiceMockFor.verify(dataObjectService)
    }

    @Test
    public void testFindAllCountriesReturnsImmutableList() {
        CountryBo countryBo = sampleCountries.get("USA")
        List<CountryBo> countryBoList = new ArrayList<CountryBo>()
        countryBoList.add(countryBo)
        countryBo = sampleCountries.get("AU")
        countryBoList.add(countryBo)
        countryBo = sampleCountries.get("ZZ")
        GenericQueryResults.Builder<CountryBo> builder = GenericQueryResults.Builder.create()


        dataObjectServiceMockFor.demand.findMatching(1){
            clazz,queryByCriteria -> builder.build()
        }


        injectDataObjectService()
        List<Country> countries = countryService.findAllCountriesNotRestricted()
        shouldFail(UnsupportedOperationException) {
            countries.add(null)
        }
        dataObjectServiceMockFor.verify(dataObjectService)
    }

    @Test
    public void testGetDefaultCountry() {
        dataObjectServiceMockFor.demand.find(1) { Class clazz, Object id -> return sampleCountries.get("US")}


        injectDataObjectService()
        parameterServiceMockFor.demand.getParameterValueAsString(1) {
            String namespaceCode, String componentCode, String parameterName -> "US"
        }
        injectParameterService()

        Country country = countryService.getDefaultCountry()
        assert country != null
        assert country.code == "US"

        dataObjectServiceMockFor.verify(dataObjectService)
        parameterServiceMockFor.verify(parameterService)
    }

    /**
     * If the default country code configured in the parameter service doesn't map to a valid country, then the method
     * should just return null
     */
    @Test
    public void testGetDefaultCountry_invalidDefaultCountryCode() {
        dataObjectServiceMockFor.demand.find(1){Class clazz, String id -> return sampleCountries.get("XX")}

        injectDataObjectService()
        parameterServiceMockFor.demand.getParameterValueAsString(1) {
            String namespaceCode, String componentCode, String parameterName -> "BLAH!!!"
        }
        injectParameterService()

        Country country = countryService.getDefaultCountry()
        assert country == null

        dataObjectServiceMockFor.verify(dataObjectService)
        parameterServiceMockFor.verify(parameterService)
    }

    @Test
    public void testGetDefaultCountry_nullDefaultCountryCode() {
        injectDataObjectService()
        parameterServiceMockFor.demand.getParameterValueAsString(1) {
            String namespaceCode, String componentCode, String parameterName -> null
        }
        injectParameterService()

        Country country = countryService.getDefaultCountry()
        assert country == null

        dataObjectServiceMockFor.verify(dataObjectService)
        parameterServiceMockFor.verify(parameterService)
    }

    @Test
    public void testGetDefaultCountry_emptyDefaultCountryCode() {
        injectDataObjectService()
        parameterServiceMockFor.demand.getParameterValueAsString(1) {
            String namespaceCode, String componentCode, String parameterName -> ""
        }
        injectParameterService()

        Country country = countryService.getDefaultCountry()
        assert country == null

        dataObjectServiceMockFor.verify(dataObjectService)
        parameterServiceMockFor.verify(parameterService)
    }

}
