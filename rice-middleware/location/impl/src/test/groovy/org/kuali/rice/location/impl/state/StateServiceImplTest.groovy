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
package org.kuali.rice.location.impl.state

import groovy.mock.interceptor.MockFor
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException
import org.kuali.rice.core.api.exception.RiceIllegalStateException
import org.kuali.rice.krad.service.BusinessObjectService
import org.kuali.rice.location.api.country.Country
import org.kuali.rice.location.api.country.CountryService
import org.kuali.rice.location.api.state.StateService
import org.kuali.rice.krad.data.DataObjectService
import org.kuali.rice.location.impl.county.CountyBo
import org.kuali.rice.core.api.criteria.GenericQueryResults

class StateServiceImplTest {

    private final shouldFail = new GroovyTestCase().&shouldFail

    static sampleStates = new HashMap<List<String, String>, StateBo>()
    static sampleStatesPerCountry = new HashMap<String, List<StateBo>>()
    
    private MockFor mockCountryService;
    private MockFor dataObjectServiceMockFor
    private DataObjectService dataObjectService
    
    static Country uSCountry
    
    StateServiceImpl stateServiceImpl;
    StateService stateService
    CountryService countryService
    

    @BeforeClass
    static void createSampleStateBOs() {
        def michiganBo = new StateBo(active: true, countryCode: "US", code: "MI",
                name: "Michigan")
        def illinoisBo = new StateBo(active: true, countryCode: "US", code: "IL",
                name: "Illinois")
        def britishColumbiaBo = new StateBo(active: true, countryCode: "CA", code: "BC",
                name: "British Columbia")
        [michiganBo, illinoisBo, britishColumbiaBo].each {
            sampleStates[[it.code, it.countryCode].asImmutable()] = it
        }
        sampleStatesPerCountry["US"] = [michiganBo, illinoisBo]
        sampleStatesPerCountry["CA"] = [britishColumbiaBo]
        
        uSCountry = Country.Builder.create("US", "USA", "United States", false, true).build()
    }

    @Before
    void setupDataObjectServiceMockContext(){
        dataObjectServiceMockFor = new MockFor(DataObjectService)
    }
    
    @Before
    void setupMockContext() {
        mockCountryService = new MockFor(CountryService.class);
    }
    
    @Before
    void setupServiceUnderTest() {
        stateServiceImpl = new StateServiceImpl()
        stateService = stateServiceImpl
    }
    
    void injectCountryServiceIntoStateService() {
        countryService = mockCountryService.proxyDelegateInstance();
        stateServiceImpl.setCountryService(countryService);
    }

    void injectDataObjectService(){
        dataObjectService = dataObjectServiceMockFor.proxyDelegateInstance()
        stateServiceImpl.setDataObjectService(dataObjectService)
    }

    @Test
    void test_get_state_null_countryCode() {
        injectDataObjectService()

        shouldFail(IllegalArgumentException) {
            stateService.getState(null, "MI")
        }
        dataObjectServiceMockFor.verify(dataObjectService)
    }

    @Test
    void test_get_state_null_code() {
        injectDataObjectService()

        shouldFail(IllegalArgumentException) {
            stateService.getState("US", null)
        }
        dataObjectServiceMockFor.verify(dataObjectService)
    }

    @Test
    void test_get_state_exists() {
        dataObjectServiceMockFor.demand.find(1..1) {
            clazz, id -> sampleStates[["US","MI"]]
        }
        injectDataObjectService()
        Assert.assertEquals(StateBo.to(sampleStates[["US", "MI"]]), stateService.getState("US", "MI"))
        dataObjectServiceMockFor.verify(dataObjectService)
    }

    @Test
    void test_get_state_does_not_exist() {
        dataObjectServiceMockFor.demand.find(1..1){
            clazz, id-> null
        }
        injectDataObjectService()
        Assert.assertNull(stateService.getState("FOO", "BAR"))
        dataObjectServiceMockFor.verify(dataObjectService)
    }

    @Test
    void test_find_all_states_in_country_null_countryCode() {
        injectDataObjectService()

        shouldFail(IllegalArgumentException) {
            stateService.findAllStatesInCountry(null)
        }
        dataObjectServiceMockFor.verify(dataObjectService)
    }

    @Test
    void test_find_all_states_in_country_exists() {
//        mockBoService.demand.findMatching(1..1) { clazz, map -> sampleStatesPerCountry[map["countryCode"]] }
//        injectBusinessObjectServiceIntoStateService()

        Collection<StateBo> stateBo = sampleStatesPerCountry["US"]
        List<StateBo> stateBoList = new ArrayList<StateBo>()
        stateBoList.addAll(stateBo)
        GenericQueryResults.Builder<StateBo> builder = GenericQueryResults.Builder.create()

        builder.setResults(stateBoList)
        dataObjectServiceMockFor.demand.findMatching(1..1){
            clazz,queryByCriteria -> builder.build()
        }
        injectDataObjectService()
        def values = stateService.findAllStatesInCountry("US")
        Assert.assertEquals(sampleStatesPerCountry["US"].collect { StateBo.to(it) }, values)

        //is this unmodifiable?
        shouldFail(UnsupportedOperationException) {
            values.add(StateBo.to(sampleStates[["CA", "BC"]]))
        }
        dataObjectServiceMockFor.verify(dataObjectService)
    }
    
    @Test
    void test_find_all_states_in_country_does_not_exist() {
        dataObjectServiceMockFor.demand.findMatching(1..1){
            clazz,queryByCriteria -> null
        }
        injectDataObjectService()
        def values = stateService.findAllStatesInCountry("FOO")
        Assert.assertEquals([], values)

        //is this unmodifiable?
        shouldFail(UnsupportedOperationException) {
            values.add(StateBo.to(sampleStates[["CA", "BC"]]))
        }

        dataObjectServiceMockFor.verify(dataObjectService)
    }
    
    @Test
    void test_find_all_states_in_country_by_alt_code_exists() {
        Collection<StateBo> stateBo = sampleStatesPerCountry["US"]
        List<StateBo> stateBoList = new ArrayList<StateBo>()
        stateBoList.addAll(stateBo)
        GenericQueryResults.Builder<StateBo> builder = GenericQueryResults.Builder.create()

        builder.setResults(stateBoList)

        dataObjectServiceMockFor.demand.findMatching(1..1){
            clazz,queryByCriteria -> builder.build()
        }
        injectDataObjectService()


        mockCountryService.demand.getCountryByAlternateCode(1) {uSCountry}
        injectCountryServiceIntoStateService();


        def values = stateService.findAllStatesInCountryByAltCode("USA")
        Assert.assertEquals(sampleStatesPerCountry["US"].collect { StateBo.to(it) }, values)

        dataObjectServiceMockFor.verify(dataObjectService)
        mockCountryService.verify(countryService)
    }
	
	@Test
	void test_find_all_states_in_country_by_alt_code_does_not_exist() {
        injectDataObjectService()
        
        mockCountryService.demand.getCountryByAlternateCode(1) {null}     
        injectCountryServiceIntoStateService();
        
        shouldFail(RiceIllegalStateException) {
            def values = stateService.findAllStatesInCountryByAltCode("FOO")
        }

        dataObjectServiceMockFor.verify(dataObjectService)
        mockCountryService.verify(countryService)    }
  
	@Test
	public void test_find_all_states_in_country_by_alt_code_pass_null() {
        injectDataObjectService()
        injectCountryServiceIntoStateService();
        
        shouldFail(RiceIllegalArgumentException) {
            def values = stateService.findAllStatesInCountryByAltCode(null)
        }

        dataObjectServiceMockFor.verify(dataObjectService)
        mockCountryService.verify(countryService)	}
}
