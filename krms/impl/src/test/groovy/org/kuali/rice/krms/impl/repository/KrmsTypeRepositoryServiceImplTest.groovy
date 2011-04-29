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



package org.kuali.rice.krms.impl.repository

import groovy.mock.interceptor.MockFor
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test
import org.kuali.rice.kns.service.BusinessObjectService
import org.kuali.rice.kns.util.KNSPropertyConstants
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeRepositoryService;

class KrmsTypeRepositoryServiceImplTest {

	private final shouldFail = new GroovyTestCase().&shouldFail

	static Map<String, KrmsTypeBo> sampleTypes = new HashMap<String, KrmsTypeBo>()
	static Map<String, KrmsTypeBo> sampleTypesKeyedByName = new HashMap<String, KrmsTypeBo>()
	def mockBusinessObjectService

	@BeforeClass
	static void createSampleTypeBOs() {
		KrmsTypeBo defaultBo = new KrmsTypeBo(active: true, id: "1", name: "DEFAULT", namespace: "KRMS_TEST", serviceName: "KrmsTypeRepositoryServiceImpl")
		KrmsTypeBo studentBo = new KrmsTypeBo(active: true, id: "2", name: "Student", namespace: "KRMS_TEST", serviceName: "KrmsTypeRepositoryServiceImpl")
		KrmsTypeBo ifopalBo = new KrmsTypeBo(active: true, id: "3", name: "IFOPAL", namespace: "KC_TEST", serviceName: null)
		for (bo in [defaultBo, studentBo, ifopalBo]) {
			sampleTypes.put(bo.id, bo)
			sampleTypesKeyedByName.put(bo.name, bo)
		}
	}

	@Before
	void setupBoServiceMockContext() {
		mockBusinessObjectService = new MockFor(BusinessObjectService.class)
	}


	@Test
	public void test_getType() {
		mockBusinessObjectService.demand.findBySinglePrimaryKey(1..1) {
			clazz, id -> sampleTypes.get("1")
		}

		BusinessObjectService bos = mockBusinessObjectService.proxyDelegateInstance()

		KrmsTypeRepositoryService service = new KrmsTypeRepositoryServiceImpl()
		service.setBusinessObjectService(bos)
		KrmsTypeDefinition myType = service.getTypeById("1")

		Assert.assertEquals(KrmsTypeBo.to(sampleTypes.get("1")), myType)
		mockBusinessObjectService.verify(bos)
	}

	@Test
	public void testGetByIdWhenNoneFound() {
		mockBusinessObjectService.demand.findBySinglePrimaryKey(1..1) {Class clazz, String id -> null}
		BusinessObjectService bos = mockBusinessObjectService.proxyDelegateInstance()

		KrmsTypeRepositoryService service = new KrmsTypeRepositoryServiceImpl()
		service.setBusinessObjectService(bos)
		KrmsTypeDefinition myType = service.getTypeById("I_DONT_EXIST")

		Assert.assertNull(myType)
		mockBusinessObjectService.verify(bos)
	}

	@Test
	public void testGetByPrimaryIdEmptyTypeId() {
		shouldFail(IllegalArgumentException.class) {
			new KrmsTypeRepositoryServiceImpl().getTypeById("")
		}
	}

	@Test
	public void testGetByPrimaryIdNullTypeId() {
		shouldFail(IllegalArgumentException.class) {
			new KrmsTypeRepositoryServiceImpl().getTypeById(null)
		}
	}

	@Test
	public void testGetByNameAndNamespace_null_type_id() {
		KrmsTypeRepositoryService service = new KrmsTypeRepositoryServiceImpl()
		shouldFail(IllegalArgumentException.class) {
			KrmsTypeDefinition myType = service.getTypeByNameAndNamespace(null,"KRMS_TEST")
		}
	}

	@Test
	public void testGetByNameAndNamespace_null_namespace() {
		KrmsTypeRepositoryService service = new KrmsTypeRepositoryServiceImpl()
		shouldFail(IllegalArgumentException.class) {
			KrmsTypeDefinition myType = service.getTypeByNameAndNamespace("Student",null)
		}
	}

	@Test
	public void testGetByNameAndNamespace() {
		mockBusinessObjectService.demand.findByPrimaryKey(1..1) {
			Class clazz, Map map -> sampleTypesKeyedByName.get("Student")
		}
		BusinessObjectService bos = mockBusinessObjectService.proxyDelegateInstance()

		KrmsTypeRepositoryService service = new KrmsTypeRepositoryServiceImpl()
		service.setBusinessObjectService(bos)
		KrmsTypeDefinition myType = service.getTypeByNameAndNamespace("Student","KRMS_TEST")

		Assert.assertEquals(KrmsTypeBo.to(sampleTypesKeyedByName.get("Student")), myType)
		mockBusinessObjectService.verify(bos)
	}

	@Test
	public void test_findAllTypesByNamespace_null_namespace() {
		KrmsTypeRepositoryService service = new KrmsTypeRepositoryServiceImpl()
		shouldFail(IllegalArgumentException.class) {
			KrmsTypeDefinition myType = service.findAllTypesByNamespace(null)
		}
	}

  @Test
  public void test_findAllTypesByNamespace() {
     mockBusinessObjectService.demand.findMatching(1..1) {
      Class clazz, Map map -> [sampleTypes.get("1"), sampleTypes.get("2")]
    }
    BusinessObjectService bos = mockBusinessObjectService.proxyDelegateInstance()
    KrmsTypeRepositoryService service = new KrmsTypeRepositoryServiceImpl()
    service.setBusinessObjectService(bos)
    Collection<KrmsTypeDefinition> myTypes = service.findAllTypesByNamespace("KRMS_TEST")

	Assert.assertEquals( myTypes.size(), new Integer(2))
	Assert.assertEquals(KrmsTypeBo.to(sampleTypes.get("1")), myTypes[0])
	Assert.assertEquals(KrmsTypeBo.to(sampleTypes.get("2")), myTypes[1])
    mockBusinessObjectService.verify(bos)
  }

  @Test
  public void test_findAllTypes() {
    mockBusinessObjectService.demand.findMatching(1..1) {
      Class clazz, Map map -> [sampleTypes.get("1"), sampleTypes.get("2"), sampleTypes.get("3")]
    }
    BusinessObjectService bos = mockBusinessObjectService.proxyDelegateInstance()

    KrmsTypeRepositoryService service = new KrmsTypeRepositoryServiceImpl()
    service.setBusinessObjectService(bos)
    Collection<KrmsTypeDefinition> myTypes = service.findAllTypes()
	Assert.assertEquals( myTypes.size(), new Integer(3))
    mockBusinessObjectService.verify(bos)
  }
  
  @Test
  public void test_createKrmsType_null_input() {
	  def boService = mockBusinessObjectService.proxyDelegateInstance()
	  KrmsTypeRepositoryService service = new KrmsTypeRepositoryServiceImpl()
	  service.setBusinessObjectService(boService)
	  shouldFail(IllegalArgumentException.class) {
		  service.createKrmsType(null)
	  }
	  mockBusinessObjectService.verify(boService)
  }

}
