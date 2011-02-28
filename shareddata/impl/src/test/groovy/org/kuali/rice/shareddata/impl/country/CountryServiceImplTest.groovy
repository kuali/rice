package org.kuali.rice.shareddata.impl.country

import org.junit.Test
import org.kuali.rice.shareddata.api.country.Country
import org.junit.Assert
import org.junit.BeforeClass
import groovy.mock.interceptor.MockFor
import org.kuali.rice.kns.service.KualiModuleService
import org.kuali.rice.shareddata.api.country.CountryService
import org.kuali.rice.kns.service.ModuleService
import org.kuali.rice.kns.util.KNSPropertyConstants
import org.junit.Before


class CountryServiceImplTest {

  private final shouldFail = new GroovyTestCase().&shouldFail

  static Map<String, CountryBo> sampleCountries = new HashMap<String, CountryBo>()
  static Map<String, CountryBo> sampleCountriesKeyedByAltCode = new HashMap<String, CountryBo>()
  def mockKualiModuleService
  def mockModuleService


  @BeforeClass
  static void createSampleCountryBOs() {
    CountryBo unitedStatesBo = new CountryBo(active: true, alternatePostalCountryCode: "USA", postalCountryCode: "US",
            postalCountryName: "United States", postalCountryRestricted: false)
    CountryBo australiaBo = new CountryBo(active: true, alternatePostalCountryCode: "AU", postalCountryCode: "AUS",
            postalCountryName: "Australia", postalCountryRestricted: false)
    CountryBo nowhereBo = new CountryBo(active: true, alternatePostalCountryCode: "ZZ", postalCountryCode: "ZZZ",
            postalCountryName: "Australia", postalCountryRestricted: true)
    for (bo in [unitedStatesBo, australiaBo, nowhereBo]) {
      sampleCountries.put(bo.postalCountryCode, bo)
      sampleCountriesKeyedByAltCode.put(bo.alternatePostalCountryCode, bo)
    }
  }

  @Before
  void setupBoServiceMockContext() {
    mockKualiModuleService = new MockFor(KualiModuleService.class)
    mockModuleService = new MockFor(ModuleService.class)
  }


  @Test
  public void testGetByPrimaryId() {
    mockModuleService.demand.getExternalizableBusinessObject(1..1) {
      Class clazz, Map map -> return sampleCountries.get(map.get(KNSPropertyConstants.POSTAL_COUNTRY_CODE))
    }
    ModuleService ms = mockModuleService.proxyDelegateInstance()

    mockKualiModuleService.demand.getResponsibleModuleService(1..1) {Class clazz -> ms}
    KualiModuleService kms = mockKualiModuleService.proxyDelegateInstance()

    CountryService service = new CountryServiceImpl()
    service.setKualiModuleService(kms)
    Country country = service.getByPrimaryId("US")

    Assert.assertEquals(CountryBo.to(sampleCountries.get("US")), country)
    mockModuleService.verify(ms)
    mockKualiModuleService.verify(kms)
  }

  @Test
  public void testGetByPrimaryIdEmptyCountryCode() {
    Country c = new CountryServiceImpl().getByPrimaryId("")
    Assert.assertNull(c)
  }

  @Test
  public void testGetByPrimaryIdNullCountryCode() {
    Country c = new CountryServiceImpl().getByPrimaryId(null)
    Assert.assertNull(c)
  }

  @Test
  public void testGetByAlternatePostalCountryCode() {
    mockModuleService.demand.getExternalizableBusinessObjectsList(1..1) {
      Class clazz, Map map ->
      [sampleCountriesKeyedByAltCode.get(
              map.get(KNSPropertyConstants.ALTERNATE_POSTAL_COUNTRY_CODE))]
    }
    ModuleService ms = mockModuleService.proxyDelegateInstance()

    mockKualiModuleService.demand.getResponsibleModuleService(1..1) {Class clazz -> ms}
    KualiModuleService kms = mockKualiModuleService.proxyDelegateInstance()

    CountryService service = new CountryServiceImpl()
    service.setKualiModuleService(kms)
    Country country = service.getByAlternatePostalCountryCode("USA")

    Assert.assertEquals(CountryBo.to(sampleCountriesKeyedByAltCode.get("USA")), country)
    mockModuleService.verify(ms)
    mockKualiModuleService.verify(kms)
  }

  @Test
  public void testGetByAlternatePostalCountryCodeWhenNoneFound() {
    mockModuleService.demand.getExternalizableBusinessObjectsList(1..1) {Class clazz, Map map -> []}
    ModuleService ms = mockModuleService.proxyDelegateInstance()

    mockKualiModuleService.demand.getResponsibleModuleService(1..1) {Class clazz -> ms}
    KualiModuleService kms = mockKualiModuleService.proxyDelegateInstance()

    CountryService service = new CountryServiceImpl()
    service.setKualiModuleService(kms)
    Country country = service.getByAlternatePostalCountryCode("ZZ")

    Assert.assertNull(country)
    mockModuleService.verify(ms)
    mockKualiModuleService.verify(kms)

  }

  public void testGetByAlternatePostalCountryCodeWhenMultipleFound() {
    mockModuleService.demand.getExternalizableBusinessObjectsList(1..1) {
      Class clazz, Map map -> [sampleCountries.get("US"), sampleCountries.get("AU")]
    }
    ModuleService ms = mockModuleService.proxyDelegateInstance()

    mockKualiModuleService.demand.getResponsibleModuleService(1..1) {Class clazz -> ms}
    KualiModuleService kms = mockKualiModuleService.proxyDelegateInstance()

    CountryService service = new CountryServiceImpl()
    service.setKualiModuleService(mockKualiModuleService.proxyDelegateInstance())

    shouldFail(IllegalStateException.class) {
      service.getByAlternatePostalCountryCode("US")
    }

    mockModuleService.verify(ms)
    mockKualiModuleService.verify(kms)
  }

  @Test
  public void testGetByAlternatePostalCountryCodeWithEmptyCode() {
    Country country = new CountryServiceImpl().getByAlternatePostalCountryCode(" ")
    Assert.assertNull(country)
  }

  @Test
  public void testGetByAlternatePostalCountryCodeWithNullCode() {
    Country country = new CountryServiceImpl().getByAlternatePostalCountryCode(null)
    Assert.assertNull(country)
  }

  @Test
  public void findAllCountriesNotRestricted() {
    mockModuleService.demand.getExternalizableBusinessObjectsList(1..1) {
      Class clazz, Map map -> [sampleCountries.get("US"), sampleCountries.get("AU")]
    }
    ModuleService ms = mockModuleService.proxyDelegateInstance()

    mockKualiModuleService.demand.getResponsibleModuleService(1..1) {Class clazz -> ms}
    KualiModuleService kms = mockKualiModuleService.proxyDelegateInstance()

    CountryService service = new CountryServiceImpl()
    service.setKualiModuleService(kms)
    service.findAllCountriesNotRestricted()

    mockModuleService.verify(ms)
    mockKualiModuleService.verify(kms)
  }

  @Test
  public void testFindAllCountries() {
    mockModuleService.demand.getExternalizableBusinessObjectsList(1..1) {
      Class clazz, Map map -> [sampleCountries.get("US"), sampleCountries.get("AU"), sampleCountries.get("ZZ")]
    }
    ModuleService ms = mockModuleService.proxyDelegateInstance()

    mockKualiModuleService.demand.getResponsibleModuleService(1..1) { Class clazz -> ms}
    KualiModuleService kms = mockKualiModuleService.proxyDelegateInstance()

    CountryService service = new CountryServiceImpl()
    service.setKualiModuleService(kms)
    service.findAllCountriesNotRestricted()

    mockModuleService.verify(ms)
    mockKualiModuleService.verify(kms)
  }
}
