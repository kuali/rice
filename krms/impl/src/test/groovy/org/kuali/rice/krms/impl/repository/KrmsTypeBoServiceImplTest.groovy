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
import org.kuali.rice.kns.bo.PersistableBusinessObject
import org.kuali.rice.kns.service.BusinessObjectService
import org.kuali.rice.kns.util.KNSPropertyConstants
import org.kuali.rice.krms.api.repository.type.KrmsTypeAttribute
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeRepositoryService;

class KrmsTypeBoServiceImplTest {

	private final shouldFail = new GroovyTestCase().&shouldFail

	static Map<String, KrmsTypeBo> sampleTypes = new HashMap<String, KrmsTypeBo>()
	static Map<String, KrmsTypeBo> sampleTypesKeyedByName = new HashMap<String, KrmsTypeBo>()
	def mockBusinessObjectService

	// create chart attribute Builder
	private static final String NAMESPACE = "KRMS_TEST"
	private static final String TYPE_ID="1234ABCD"
	private static final String NAME="Chart_Org"
	private static final String SERVICE_NAME="chartOrgService"
		
	private static final String ATTR_ID_1="CHART_ATTR"
	private static final String CHART_ATTR_DEF_ID = "1000"
	private static final Integer SEQUENCE_NUMBER_1 = new Integer(1)
	
	private static final String ATTR_ID_2="ORG_ATTR"
	private static final String ORG_ATTR_DEF_ID = "1002"
	private static final Integer SEQUENCE_NUMBER_2 = new Integer(2)
	
	// create sample KrmsType builder and build
	private static KrmsTypeAttribute.Builder chartAttrBuilder = KrmsTypeAttribute.Builder.create(ATTR_ID_1, TYPE_ID, CHART_ATTR_DEF_ID, SEQUENCE_NUMBER_1)
	private static KrmsTypeAttribute.Builder orgAttrBuilder = KrmsTypeAttribute.Builder.create(ATTR_ID_2, TYPE_ID, ORG_ATTR_DEF_ID, SEQUENCE_NUMBER_2)
	private static List<KrmsTypeAttribute.Builder> attrs = Arrays.asList(chartAttrBuilder, orgAttrBuilder)
	private static KrmsTypeDefinition TEST_KRMS_TYPE_DEF = KrmsTypeDefinition.Builder.create(TYPE_ID, NAME, NAMESPACE)
		.serviceName(SERVICE_NAME)
		.attributes(attrs)
		.build()
	private static KrmsTypeBo TEST_KRMS_TYPE_BO = KrmsTypeBo.from(TEST_KRMS_TYPE_DEF)
		
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
		mockBusinessObjectService = new MockFor(BusinessObjectService.class)
	}


	@Test
	public void test_getType() {
		mockBusinessObjectService.demand.findBySinglePrimaryKey(1..1) {
			clazz, id -> sampleTypes.get("1")
		}

		BusinessObjectService bos = mockBusinessObjectService.proxyDelegateInstance()

		KrmsTypeRepositoryService service = new KrmsTypeBoServiceImpl()
		service.setBusinessObjectService(bos)
		KrmsTypeDefinition myType = service.getTypeById("1")

		Assert.assertEquals(KrmsTypeBo.to(sampleTypes.get("1")), myType)
		mockBusinessObjectService.verify(bos)
	}

	@Test
	public void testGetByIdWhenNoneFound() {
		mockBusinessObjectService.demand.findBySinglePrimaryKey(1..1) {Class clazz, String id -> null}
		BusinessObjectService bos = mockBusinessObjectService.proxyDelegateInstance()

		KrmsTypeRepositoryService service = new KrmsTypeBoServiceImpl()
		service.setBusinessObjectService(bos)
		KrmsTypeDefinition myType = service.getTypeById("I_DONT_EXIST")

		Assert.assertNull(myType)
		mockBusinessObjectService.verify(bos)
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

	@Test
	public void testGetByNameAndNamespace_null_type_id() {
		KrmsTypeRepositoryService service = new KrmsTypeBoServiceImpl()
		shouldFail(IllegalArgumentException.class) {
			KrmsTypeDefinition myType = service.getTypeByNameAndNamespace(null,"KRMS_TEST")
		}
	}

	@Test
	public void testGetByNameAndNamespace_null_namespace() {
		KrmsTypeRepositoryService service = new KrmsTypeBoServiceImpl()
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

		KrmsTypeRepositoryService service = new KrmsTypeBoServiceImpl()
		service.setBusinessObjectService(bos)
		KrmsTypeDefinition myType = service.getTypeByNameAndNamespace("Student","KRMS_TEST")

		Assert.assertEquals(KrmsTypeBo.to(sampleTypesKeyedByName.get("Student")), myType)
		mockBusinessObjectService.verify(bos)
	}

	@Test
	public void test_findAllTypesByNamespace_null_namespace() {
		KrmsTypeRepositoryService service = new KrmsTypeBoServiceImpl()
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
    KrmsTypeRepositoryService service = new KrmsTypeBoServiceImpl()
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

    KrmsTypeRepositoryService service = new KrmsTypeBoServiceImpl()
    service.setBusinessObjectService(bos)
    Collection<KrmsTypeDefinition> myTypes = service.findAllTypes()
	Assert.assertEquals( myTypes.size(), new Integer(3))
    mockBusinessObjectService.verify(bos)
  }
  
  @Test
  public void test_createKrmsType_null_input() {
	  def boService = mockBusinessObjectService.proxyDelegateInstance()
	  KrmsTypeRepositoryService service = new KrmsTypeBoServiceImpl()
	  service.setBusinessObjectService(boService)
	  shouldFail(IllegalArgumentException.class) {
		  service.createKrmsType(null)
	  }
	  mockBusinessObjectService.verify(boService)
  }

  @Test
  void test_createKrmsType_exists() {
		mockBusinessObjectService.demand.findByPrimaryKey(1..1) {
			Class clazz, Map map -> TEST_KRMS_TYPE_BO
		}
		BusinessObjectService bos = mockBusinessObjectService.proxyDelegateInstance()
		KrmsTypeRepositoryService service = new KrmsTypeBoServiceImpl()
		service.setBusinessObjectService(bos)
		shouldFail(IllegalStateException.class) {
			service.createKrmsType(TEST_KRMS_TYPE_DEF)
		}
		mockBusinessObjectService.verify(bos)
  }

  @Test
  void test_createKrmsType_success() {
		mockBusinessObjectService.demand.findByPrimaryKey(1..1) {Class clazz, Map map -> null}
		mockBusinessObjectService.demand.save { PersistableBusinessObject bo -> }
		
		BusinessObjectService bos = mockBusinessObjectService.proxyDelegateInstance()
		KrmsTypeRepositoryService service = new KrmsTypeBoServiceImpl()
		service.setBusinessObjectService(bos)
		
		service.createKrmsType(TEST_KRMS_TYPE_DEF)
		mockBusinessObjectService.verify(bos)
  }

  @Test
  public void test_updateKrmsType_null_input() {
	  def boService = mockBusinessObjectService.proxyDelegateInstance()
	  KrmsTypeRepositoryService service = new KrmsTypeBoServiceImpl()
	  service.setBusinessObjectService(boService)
	  shouldFail(IllegalArgumentException.class) {
		  service.updateKrmsType(null)
	  }
	  mockBusinessObjectService.verify(boService)
  }

  @Test
  void test_updateKrmsType_does_not_exist() {
		mockBusinessObjectService.demand.findBySinglePrimaryKey(1..1) {
			Class clazz, String id -> null
		}
		BusinessObjectService bos = mockBusinessObjectService.proxyDelegateInstance()
		KrmsTypeRepositoryService service = new KrmsTypeBoServiceImpl()
		service.setBusinessObjectService(bos)
		shouldFail(IllegalStateException.class) {
			service.updateKrmsType(TEST_KRMS_TYPE_DEF)
		}
		mockBusinessObjectService.verify(bos)
  }

  @Test
  void test_updateKrmsType_success() {
		mockBusinessObjectService.demand.findBySinglePrimaryKey(1..1) {Class clazz, String id -> TEST_KRMS_TYPE_BO}
		mockBusinessObjectService.demand.save { PersistableBusinessObject bo -> }
		BusinessObjectService bos = mockBusinessObjectService.proxyDelegateInstance()
		KrmsTypeRepositoryService service = new KrmsTypeBoServiceImpl()
		service.setBusinessObjectService(bos)
		service.updateKrmsType(TEST_KRMS_TYPE_DEF)
		mockBusinessObjectService.verify(bos)
  }

}
