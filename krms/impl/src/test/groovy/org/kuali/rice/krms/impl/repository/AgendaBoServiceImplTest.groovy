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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import groovy.mock.interceptor.MockFor
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test
import org.kuali.rice.kns.bo.PersistableBusinessObject
import org.kuali.rice.kns.service.BusinessObjectService
import org.kuali.rice.kns.util.KNSPropertyConstants
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinition

class AgendaBoServiceImplTest {

	private final shouldFail = new GroovyTestCase().&shouldFail
	def mockBusinessObjectService

	private static final String NAMESPACE = "KRMS_TEST"
	private static final String AGENDA_ID_1 = "AGENDAID001"
	private static final String AGENDA_NAME = "Agenda1"
	private static final String TYPE_ID = "1234XYZ"
	private static final String CONTEXT_ID_1 = "CONTEXT-001"
	private static final String AGENDA_ITEM_ID_1 = "ITEM01"

	private static final String ATTR_NAME_1 = "Department"
	private static final String ATTR_VALUE_1 = "Biology"
	private static final String ATTR_DEF_ID_1 = "1001"
	private static final String ATTR_NAME_2 = "Fund"
	private static final String ATTR_VALUE_2 = "19900A"
	private static final String ATTR_DEF_ID_2 = "1002"
	
	private static AgendaDefinition TEST_AGENDA_DEF;
	private static AgendaBo TEST_AGENDA_BO;
	
	private static AgendaDefinition TEST_AGENDA_ITEM_DEF;
	private static AgendaBo TEST_AGENDA_ITEM_BO;
	
	private static KrmsAttributeDefinitionBo ADB1;
	private static KrmsAttributeDefinitionBo ADB2;
	
	@BeforeClass
	static void createSamples() {
		Map<String,String> myAttrs = new HashMap<String,String>()
		myAttrs.put(ATTR_NAME_1, ATTR_VALUE_1)
		myAttrs.put(ATTR_NAME_2, ATTR_VALUE_2)
		AgendaDefinition.Builder builder = AgendaDefinition.Builder.create(AGENDA_ID_1, AGENDA_NAME, NAMESPACE, TYPE_ID, CONTEXT_ID_1)
		builder.setFirstItemId(AGENDA_ITEM_ID_1)
		builder.setAttributes(myAttrs);
		TEST_AGENDA_DEF = builder.build()

		// create Agenda bo
		TEST_AGENDA_BO = new AgendaBo();
		TEST_AGENDA_BO.setId( AGENDA_ID_1 );
		TEST_AGENDA_BO.setNamespace( NAMESPACE );
		TEST_AGENDA_BO.setName( AGENDA_NAME );
		TEST_AGENDA_BO.setTypeId( TYPE_ID );
		TEST_AGENDA_BO.setContextId( CONTEXT_ID_1 );
		TEST_AGENDA_BO.setFirstItemId( AGENDA_ITEM_ID_1 );

		// krmsAttributeDefinitionBos
		ADB1 = new KrmsAttributeDefinitionBo();
		ADB1.id = ATTR_DEF_ID_1
		ADB1.name = ATTR_NAME_1
		ADB1.namespace = NAMESPACE
		ADB2 = new KrmsAttributeDefinitionBo();
		ADB2.id = ATTR_DEF_ID_2
		ADB2.name = ATTR_NAME_2
		ADB2.namespace = NAMESPACE

		// build the set of agenda attribute BOs
		AgendaAttributeBo attributeBo1 = new AgendaAttributeBo();
		attributeBo1.setAgendaId( AGENDA_ID_1 );
		attributeBo1.setAttributeDefinitionId( ATTR_DEF_ID_1 );
		attributeBo1.setValue( ATTR_VALUE_1 );
		attributeBo1.attributeDefinition = ADB1;
		AgendaAttributeBo attributeBo2 = new AgendaAttributeBo();
		attributeBo2.setAgendaId( AGENDA_ID_1 );
		attributeBo2.setAttributeDefinitionId( ATTR_DEF_ID_2 );
		attributeBo2.setValue( ATTR_VALUE_2 );
		attributeBo2.attributeDefinition = ADB2;
		Set<AgendaAttributeBo> attributes = [attributeBo1, attributeBo2]
        
		TEST_AGENDA_BO.setAttributeBos(attributes);
	}

	@Before
	void setupBoServiceMockContext() {
		mockBusinessObjectService = new MockFor(BusinessObjectService.class)
	}

	@Test
	public void test_getAgendaByAgendaId() {
		mockBusinessObjectService.demand.findBySinglePrimaryKey(1..1) {clazz, id -> TEST_AGENDA_BO}
		BusinessObjectService bos = mockBusinessObjectService.proxyDelegateInstance()

		AgendaBoService service = new AgendaBoServiceImpl()
		service.setBusinessObjectService(bos)
		AgendaDefinition myAgenda = service.getAgendaByAgendaId(AGENDA_ID_1)

		Assert.assertEquals(service.to(TEST_AGENDA_BO), myAgenda)
		mockBusinessObjectService.verify(bos)
	}

	@Test
	public void test_getAgendaByAgendaId_when_none_found() {
		mockBusinessObjectService.demand.findBySinglePrimaryKey(1..1) {Class clazz, String id -> null}
		BusinessObjectService bos = mockBusinessObjectService.proxyDelegateInstance()

		AgendaBoService service = new AgendaBoServiceImpl()
		service.setBusinessObjectService(bos)
		AgendaDefinition myAgenda = service.getAgendaByAgendaId("I_DONT_EXIST")

		Assert.assertNull(myAgenda)
		mockBusinessObjectService.verify(bos)
	}

	@Test
	public void test_getAgendaByAgendaId_empty_id() {
		shouldFail(IllegalArgumentException.class) {
			new AgendaBoServiceImpl().getAgendaByAgendaId("")
		}
	}

	@Test
	public void test_getAgendaByAgendaId_null_action_id() {
		shouldFail(IllegalArgumentException.class) {
			new AgendaBoServiceImpl().getAgendaByAgendaId(null)
		}
	}
	
	@Test
	public void test_getAgendaByNameAndNamespace() {
		mockBusinessObjectService.demand.findByPrimaryKey(1..1) {clazz, map -> TEST_AGENDA_BO}
		BusinessObjectService bos = mockBusinessObjectService.proxyDelegateInstance()

		AgendaBoService service = new AgendaBoServiceImpl()
		service.setBusinessObjectService(bos)
		AgendaDefinition myAgenda = service.getAgendaByNameAndNamespace(AGENDA_ID_1, NAMESPACE)

		Assert.assertEquals(service.to(TEST_AGENDA_BO), myAgenda)
		mockBusinessObjectService.verify(bos)
	}

	@Test
	public void test_getAgendaByNameAndNamespace_when_none_found() {
		mockBusinessObjectService.demand.findByPrimaryKey(1..1) {Class clazz, Map map -> null}
		BusinessObjectService bos = mockBusinessObjectService.proxyDelegateInstance()

		AgendaBoService service = new AgendaBoServiceImpl()
		service.setBusinessObjectService(bos)
		AgendaDefinition myAgenda = service.getAgendaByNameAndNamespace("I_DONT_EXIST", NAMESPACE)

		Assert.assertNull(myAgenda)
		mockBusinessObjectService.verify(bos)
	}

	@Test
	public void test_getAgendaByNameAndNamespace_empty_name() {
		shouldFail(IllegalArgumentException.class) {
			new AgendaBoServiceImpl().getAgendaByNameAndNamespace("", NAMESPACE)
		}
	}

	@Test
	public void test_getAgendaByNameAndNamespace_null_name() {
		shouldFail(IllegalArgumentException.class) {
			new AgendaBoServiceImpl().getAgendaByNameAndNamespace(null, NAMESPACE)
		}
	}

	@Test
	public void test_getAgendaByNameAndNamespace_empty_namespace() {
		shouldFail(IllegalArgumentException.class) {
			new AgendaBoServiceImpl().getAgendaByNameAndNamespace(AGENDA_ID_1, "")
		}
	}

	@Test
	public void test_getAgendaByNameAndNamespace_null_namespace() {
		shouldFail(IllegalArgumentException.class) {
			new AgendaBoServiceImpl().getAgendaByNameAndNamespace(AGENDA_ID_1, null)
		}
	}

	@Test
	public void test_getAgendasByContextId() {
		Set<AgendaBo> resultSet = new HashSet<AgendaBo>();
		resultSet.add(TEST_AGENDA_BO);
		mockBusinessObjectService.demand.findMatching(1..1) {Class clazz, Map map -> resultSet}
		BusinessObjectService bos = mockBusinessObjectService.proxyDelegateInstance()

		AgendaBoService service = new AgendaBoServiceImpl()
		service.setBusinessObjectService(bos)
		Set<AgendaDefinition> myAgendas = service.getAgendasByContextId(CONTEXT_ID_1)

		Assert.assertEquals(service.to(TEST_AGENDA_BO), myAgendas.iterator().next())
		mockBusinessObjectService.verify(bos)
	}

	@Test
	public void test_getAgendasByContextId_when_none_found() {
		mockBusinessObjectService.demand.findMatching(1..1) {Class clazz, Map map -> null}
		BusinessObjectService bos = mockBusinessObjectService.proxyDelegateInstance()

		AgendaBoService service = new AgendaBoServiceImpl()
		service.setBusinessObjectService(bos)
		Set<AgendaDefinition> myAgendas = service.getAgendasByContextId("I_DONT_EXIST")

		Assert.assertEquals(myAgendas.size(), 0)
		mockBusinessObjectService.verify(bos)
	}

	@Test
	public void test_getAgendasByContextId_empty_id() {
		shouldFail(IllegalArgumentException.class) {
			new AgendaBoServiceImpl().getAgendasByContextId("")
		}
	}

	@Test
	public void test_getAgendasByContextId_null_rule_id() {
		shouldFail(IllegalArgumentException.class) {
			new AgendaBoServiceImpl().getAgendasByContextId(null)
		}
	}


  @Test
  public void test_createAgenda_null_input() {
	  def boService = mockBusinessObjectService.proxyDelegateInstance()
	  AgendaBoService service = new AgendaBoServiceImpl()
	  service.setBusinessObjectService(boService)
	  shouldFail(IllegalArgumentException.class) {
		  service.createAgenda(null)
	  }
	  mockBusinessObjectService.verify(boService)
  }

  @Test
  void test_createAgenda_exists() {
		mockBusinessObjectService.demand.findByPrimaryKey(1..1) {
			Class clazz, Map map -> TEST_AGENDA_BO
		}
		BusinessObjectService bos = mockBusinessObjectService.proxyDelegateInstance()
		AgendaBoService service = new AgendaBoServiceImpl()
		service.setBusinessObjectService(bos)
		shouldFail(IllegalStateException.class) {
			service.createAgenda(TEST_AGENDA_DEF)
		}
		mockBusinessObjectService.verify(bos)
  }

  @Test
  void test_createAgenda_success() {
		mockBusinessObjectService.demand.findByPrimaryKey(1..1) {Class clazz, Map map -> null}
		mockBusinessObjectService.demand.findMatching(1..1) { Class clazz, Map map -> [ADB1] }
		mockBusinessObjectService.demand.findMatching(1..1) { Class clazz, Map map -> [ADB2] }
		mockBusinessObjectService.demand.save { PersistableBusinessObject bo -> }
		BusinessObjectService bos = mockBusinessObjectService.proxyDelegateInstance()
		AgendaBoService service = new AgendaBoServiceImpl()
		service.setBusinessObjectService(bos)

		KrmsAttributeDefinitionService kads = new KrmsAttributeDefinitionServiceImpl();
		kads.setBusinessObjectService(bos)
		KrmsRepositoryServiceLocator.setKrmsAttributeDefinitionService(kads)
				
		service.createAgenda(TEST_AGENDA_DEF)
		mockBusinessObjectService.verify(bos)
  }

  @Test
  public void test_updateAgenda_null_input() {
	  def boService = mockBusinessObjectService.proxyDelegateInstance()
	  AgendaBoService service = new AgendaBoServiceImpl()
	  service.setBusinessObjectService(boService)
	  shouldFail(IllegalArgumentException.class) {
		  service.updateAgenda(null)
	  }
	  mockBusinessObjectService.verify(boService)
  }

  @Test
  void test_updateAgenda_does_not_exist() {
		mockBusinessObjectService.demand.findBySinglePrimaryKey(1..1) {
			Class clazz, String id -> null
		}
		BusinessObjectService bos = mockBusinessObjectService.proxyDelegateInstance()
		AgendaBoService service = new AgendaBoServiceImpl()
		service.setBusinessObjectService(bos)
		shouldFail(IllegalStateException.class) {
			service.updateAgenda(TEST_AGENDA_DEF)
		}
		mockBusinessObjectService.verify(bos)
  }

  @Test
  void test_updateAgenda_success() {
		mockBusinessObjectService.demand.findBySinglePrimaryKey(1..1) {Class clazz, String id -> TEST_AGENDA_BO}
		mockBusinessObjectService.demand.findMatching(1..1) { Class clazz, Map map -> [ADB1] }
		mockBusinessObjectService.demand.findMatching(1..1) { Class clazz, Map map -> [ADB2] }
		mockBusinessObjectService.demand.save { PersistableBusinessObject bo -> }

		BusinessObjectService bos = mockBusinessObjectService.proxyDelegateInstance()
		AgendaBoService service = new AgendaBoServiceImpl()
		service.setBusinessObjectService(bos)

		KrmsAttributeDefinitionService kads = new KrmsAttributeDefinitionServiceImpl();
		kads.setBusinessObjectService(bos)
		KrmsRepositoryServiceLocator.setKrmsAttributeDefinitionService(kads)
				
		service.updateAgenda(TEST_AGENDA_DEF)
		mockBusinessObjectService.verify(bos)
  }

}
