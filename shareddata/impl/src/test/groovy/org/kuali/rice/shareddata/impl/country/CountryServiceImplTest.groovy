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





package org.kuali.rice.shareddata.impl.country

import groovy.mock.interceptor.MockFor
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.kuali.rice.kns.service.BusinessObjectService
import org.kuali.rice.kns.util.KNSPropertyConstants
import org.kuali.rice.shareddata.api.country.Country
import org.kuali.rice.shareddata.api.country.CountryService

class CountryServiceImplTest {

  private final shouldFail = new GroovyTestCase().&shouldFail

  static Map<String, CountryBo> sampleCountries = new HashMap<String, CountryBo>()
  static Map<String, CountryBo> sampleCountriesKeyedByAltCode = new HashMap<String, CountryBo>()
  def mockBusinessObjectService

  @BeforeClass
  static void createSampleCountryBOs() {
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
  void setupBoServiceMockContext() {
    mockBusinessObjectService = new MockFor(BusinessObjectService.class)
  }


  @Test
  public void test_getCountry() {
    mockBusinessObjectService.demand.findByPrimaryKey(1..1) {
      Class clazz, Map map -> return sampleCountries.get(map.get(KNSPropertyConstants.POSTAL_COUNTRY_CODE))
    }

    BusinessObjectService bos = mockBusinessObjectService.proxyDelegateInstance()

    CountryService service = new CountryServiceImpl()
    service.setBusinessObjectService(bos)
    Country country = service.getCountry("US")

    Assert.assertEquals(CountryBo.to(sampleCountries.get("US")), country)
    mockBusinessObjectService.verify(bos)
  }

  @Test
  public void testGetByPrimaryIdEmptyCountryCode() {
    shouldFail(IllegalArgumentException.class) {
      new CountryServiceImpl().getCountry("")
    }
  }

  @Test
  public void testGetByPrimaryIdNullCountryCode() {
    shouldFail(IllegalArgumentException.class) {
      new CountryServiceImpl().getCountry(null)
    }
  }

  @Test
  public void testGetByAlternateCode() {
    mockBusinessObjectService.demand.findMatching(1..1) {
      Class clazz, Map map ->
      [sampleCountriesKeyedByAltCode.get(
              map.get(KNSPropertyConstants.ALTERNATE_POSTAL_COUNTRY_CODE))]
    }
    BusinessObjectService bos = mockBusinessObjectService.proxyDelegateInstance()

    CountryService service = new CountryServiceImpl()
    service.setBusinessObjectService(bos)
    Country country = service.getCountryByAlternateCode("USA")

    Assert.assertEquals(CountryBo.to(sampleCountriesKeyedByAltCode.get("USA")), country)
    mockBusinessObjectService.verify(bos)
  }

  @Test
  public void testGetByAlternateCodeWhenNoneFound() {
    mockBusinessObjectService.demand.findMatching(1..1) {Class clazz, Map map -> []}
    BusinessObjectService bos = mockBusinessObjectService.proxyDelegateInstance()

    CountryService service = new CountryServiceImpl()
    service.setBusinessObjectService(bos)
    Country country = service.getCountryByAlternateCode("ZZ")

    Assert.assertNull(country)
    mockBusinessObjectService.verify(bos)

  }

  @Test
  public void testGetByAlternateCodeWhenMultipleFound() {
    mockBusinessObjectService.demand.findMatching(1..1) {
      Class clazz, Map map -> [sampleCountries.get("US"), sampleCountries.get("AU")]
    }
    BusinessObjectService bos = mockBusinessObjectService.proxyDelegateInstance()

    CountryService service = new CountryServiceImpl()
    service.setBusinessObjectService(bos)

    shouldFail(IllegalStateException.class) {
      service.getCountryByAlternateCode("US")
    }

    mockBusinessObjectService.verify(bos)
  }

  @Test
  public void testGetByAlternateCodeWithEmptyCode() {
    shouldFail(IllegalArgumentException.class) {
      new CountryServiceImpl().getCountryByAlternateCode(" ")
    }
  }

  @Test
  public void testGetByAlternateCodeWithNullCode() {
    shouldFail(IllegalArgumentException.class) {
      new CountryServiceImpl().getCountryByAlternateCode(null)
    }
  }

  @Test
  public void findAllCountriesNotRestricted() {
    mockBusinessObjectService.demand.findMatching(1..1) {
      Class clazz, Map map -> [sampleCountries.get("US"), sampleCountries.get("AU")]
    }
    BusinessObjectService bos = mockBusinessObjectService.proxyDelegateInstance()
    CountryService service = new CountryServiceImpl()
    service.setBusinessObjectService(bos)
    service.findAllCountriesNotRestricted()

    mockBusinessObjectService.verify(bos)
  }

  @Test
  public void findAllCountriesNotRestrictedReturnsImmutableList() {
    mockBusinessObjectService.demand.findMatching(1) {
      Class clazz, Map map -> [sampleCountries.get("US"), sampleCountries.get("AU")]
    }
    BusinessObjectService bos = mockBusinessObjectService.proxyDelegateInstance()
    CountryService service = new CountryServiceImpl()
    service.setBusinessObjectService(bos)
    List<Country> countries = service.findAllCountriesNotRestricted()
    shouldFail(UnsupportedOperationException.class) {
      countries.add(null);
    }
    mockBusinessObjectService.verify(bos)

  }

  @Test
  public void testFindAllCountries() {
    mockBusinessObjectService.demand.findMatching(1..1) {
      Class clazz, Map map -> [sampleCountries.get("US"), sampleCountries.get("AU"), sampleCountries.get("ZZ")]
    }
    BusinessObjectService bos = mockBusinessObjectService.proxyDelegateInstance()

    CountryService service = new CountryServiceImpl()
    service.setBusinessObjectService(bos)
    service.findAllCountries()

    mockBusinessObjectService.verify(bos)
  }

  public void testFindAllCountriesReturnsImmutableList() {
    mockBusinessObjectService.demand.findMatching(1..1) {
      Class clazz, Map map -> [sampleCountries.get("US"), sampleCountries.get("AU"), sampleCountries.get("ZZ")]
    }
    BusinessObjectService bos = mockBusinessObjectService.proxyDelegateInstance()

    CountryService service = new CountryServiceImpl()
    service.setBusinessObjectService(bos)
    List<Country> countries = service.findAllCountriesNotRestricted()

    shouldFail(UnsupportedOperationException.class) {
      countries.add(null)
    }
    mockBusinessObjectService.verify(bos)
  }
}
