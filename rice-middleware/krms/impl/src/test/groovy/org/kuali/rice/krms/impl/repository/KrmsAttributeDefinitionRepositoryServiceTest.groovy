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
package org.kuali.rice.krms.impl.repository

import groovy.mock.interceptor.MockFor
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.kuali.rice.krad.data.DataObjectService
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition

import static org.kuali.rice.krms.impl.repository.RepositoryTestUtils.buildQueryResults;

class KrmsAttributeDefinitionRepositoryServiceTest {

  private final shouldFail = new GroovyTestCase().&shouldFail

  static Map<String, KrmsTypeBo> sampleTypes = new HashMap<String, KrmsTypeBo>()
  static Map<String, KrmsTypeBo> sampleTypesKeyedByName = new HashMap<String, KrmsTypeBo>()
  def mockDataObjectService

  @BeforeClass
  static void createSampleTypeBOs() {
    KrmsTypeBo defaultBo = new KrmsTypeBo(active: true, id: "1", name: "DEFAULT", namespace: "KRMS_TEST", serviceName: "KrmsTypeBoServiceImpl")
    KrmsTypeBo studentBo = new KrmsTypeBo(active: true, id: "2", name: "Student", namespace: "KRMS_TEST", serviceName: "KrmsTypeBoServiceImpl")
    KrmsTypeBo ifopalBo = new KrmsTypeBo(active: true, id: "3", name: "IFOPAL", namespace: "KC_TEST", serviceName: null)
    for (bo in [defaultBo, studentBo, ifopalBo]) {
      sampleTypes.put(bo.id, bo)
      sampleTypesKeyedByName.put(bo.name, bo)
    }
  }

  @Before
  void setupBoServiceMockContext() {
      mockDataObjectService = new MockFor(DataObjectService.class)
  }


  @Test
  public void test_getType() {
    mockDataObjectService.demand.find(1..1) {
      Class clazz, id -> return sampleTypes.get("1")
    }

    DataObjectService dos = mockDataObjectService.proxyDelegateInstance()

    KrmsTypeBoServiceImpl service = new KrmsTypeBoServiceImpl()
    service.setDataObjectService(dos)
    KrmsTypeDefinition myType = service.getTypeById("1")

    Assert.assertEquals(KrmsTypeBo.to(sampleTypes.get("1")), myType)
    mockDataObjectService.verify(dos)
  }

  @Test
  public void testGetByPrimaryIdEmptyTypeId() {
      shouldFail(IllegalArgumentException.class) {
        new KrmsTypeBoServiceImpl().getTypeById("")
      }
  }

  @Test
  public void testGetByPrimaryIdNullTypeId() {
      shouldFail(IllegalArgumentException.class) {
        new KrmsTypeBoServiceImpl().getTypeById(null)
      }
  }

//  @Ignore
//  @Test
//  public void testGetByNameAndNamespace() {
//    mockDataObjectService.demand.findMatching(1..2) {
//      Class clazz, Map map ->
//      [sampleTypesKeyedByName.get(
//              map.get(KRADPropertyConstants.ALTERNATE_POSTAL_COUNTRY_CODE))]
//    }
//    DataObjectService dataObjectService = mockDataObjectService.proxyDelegateInstance()
//
//    KrmsTypeRepositoryService service = new KrmsTypeBoServiceImpl()
//    service.setDataObjectService(dataObjectService)
//    KrmsType myType = service.getTypeByNameAndNamespace("Student","KRMS_TEST")
//
//    Assert.assertEquals(KrmsTypeBo.to(sampleTypesKeyedByName.get("Student")), myType)
//    mockDataObjectService.verify(dataObjectService)
//  }

//  @Test
//  public void testGetByIdWhenNoneFound() {
//    mockDataObjectService.demand.findMatching(1..1) {Class clazz, Map map -> []}
//    DataObjectService dataObjectService = mockDataObjectService.proxyDelegateInstance()
//
//    CountryService service = new CountryServiceImpl()
//    service.setDataObjectService(dataObjectService)
//    Country country = service.getCountryByAlternateCode("ZZ")
//
//    Assert.assertNull(country)
//    mockDataObjectService.verify(dataObjectService)
//
//  }
//
//  @Test
//  public void testGetByAlternatePostalCountryCodeWhenMultipleFound() {
//    mockDataObjectService.demand.findMatching(1..1) {
//      Class clazz, Map map -> [sampleCountries.get("US"), sampleCountries.get("AU")]
//    }
//    DataObjectService dataObjectService = mockDataObjectService.proxyDelegateInstance()
//
//    CountryService service = new CountryServiceImpl()
//    service.setDataObjectService(dataObjectService)
//
//    shouldFail(IllegalStateException.class) {
//      service.getCountryByAlternateCode("US")
//    }
//
//    mockDataObjectService.verify(dataObjectService)
//  }
//
//  @Test
//  public void testGetByAlternatePostalCountryCodeWithEmptyCode() {
//      shouldFail(IllegalArgumentException.class) {
//        new CountryServiceImpl().getCountryByAlternateCode(" ")
//      }
//  }
//
//  @Test
//  public void testGetByAlternatePostalCountryCodeWithNullCode() {
//      shouldFail(IllegalArgumentException.class) {
//        new CountryServiceImpl().getCountryByAlternateCode(null)
//      }
//  }

  @Test
  public void findAllTypesByNamespace() {
     mockDataObjectService.demand.findMatching(1..1) {
      clazz, map -> buildQueryResults([sampleTypes.get("1"), sampleTypes.get("2")])
    }
    DataObjectService dataObjectService = mockDataObjectService.proxyDelegateInstance()
    KrmsTypeBoServiceImpl service = new KrmsTypeBoServiceImpl()
    service.setDataObjectService(dataObjectService)
    service.findAllTypesByNamespace("KRMS_TEST")

    mockDataObjectService.verify(dataObjectService)
  }

  @Test
  public void testFindAllTypes() {
    mockDataObjectService.demand.findMatching(1..1) {
      Class clazz, crit -> buildQueryResults([sampleTypes.get("1"), sampleTypes.get("2"), sampleTypes.get("3")])
    }
    DataObjectService dos = mockDataObjectService.proxyDelegateInstance()

    KrmsTypeBoServiceImpl service = new KrmsTypeBoServiceImpl()
    service.setDataObjectService(dos)
    service.findAllTypes()

    mockDataObjectService.verify(dos)
  }
}
