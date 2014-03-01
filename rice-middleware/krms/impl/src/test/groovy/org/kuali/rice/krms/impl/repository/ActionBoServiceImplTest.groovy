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
import org.kuali.rice.core.api.CoreConstants
import org.kuali.rice.core.api.config.property.ConfigContext
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader
import org.kuali.rice.core.api.resourceloader.ResourceLoader
import org.kuali.rice.core.impl.config.property.JAXBConfigImpl
import org.kuali.rice.krad.data.DataObjectService
import org.kuali.rice.krms.api.repository.action.ActionDefinition
import org.kuali.rice.krms.api.repository.rule.RuleDefinition
import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinition

import javax.xml.namespace.QName

import static org.kuali.rice.krms.impl.repository.RepositoryTestUtils.buildQueryResults;

class ActionBoServiceImplTest {

	private final shouldFail = new GroovyTestCase().&shouldFail

	static def NAMESPACE = "KRMS_TEST"
	static def TYPE_ID="1234ABCD"		
	
	static def ATTR_ID_1 = "ACTION_ATTR_001"
	static def ATTR_DEF_ID = "1002"
	static def ACTION_TYPE = "Notification"
	static def ATTR_VALUE = "Spam"
	static def SEQUENCE_1 = new Integer(1)
	
	static def RULE_ID_1 = "RULEID001"	
	static def ACTION_ID_1 = "ACTIONID01"
	static def ACTION_NAME_1 = "Say Hello"
	static def ACTION_DESCRIPTION_1 = "Send spam email"
	
	// test samples
	static def ActionDefinition TEST_ACTION_DEF
	static def ActionBo TEST_ACTION_BO
	private static KrmsAttributeDefinitionBo ADB1
	
	// test services
	def mockDataObjectService
	
	@BeforeClass
	static void createSamples() {
        def config = new JAXBConfigImpl();
        config.putProperty(CoreConstants.Config.APPLICATION_ID, "APPID");
        ConfigContext.init(config);

        GlobalResourceLoader.stop()
        GlobalResourceLoader.addResourceLoader([
                getName: { -> new QName("krmsAttributeDefinitionService") },
                getService: { [
                        getKrmsAttributeBo: { a, b -> KrmsAttributeDefinitionBo.from(KrmsAttributeDefinition.Builder.create("123", a, b).build()) }
                ] as KrmsAttributeDefinitionService },
                stop: {}
        ] as ResourceLoader);

		//  create krmsAttributeDefinitionBo
		ADB1 = new KrmsAttributeDefinitionBo();
		ADB1.id = ATTR_DEF_ID
		ADB1.name = ACTION_TYPE
		ADB1.namespace = NAMESPACE
		
		// create ActionDefinition		
		Map<String,String> myAttrs = new HashMap<String,String>()
		myAttrs.put(ACTION_TYPE, ATTR_VALUE)
		ActionDefinition.Builder builder = ActionDefinition.Builder.create(ACTION_ID_1, ACTION_NAME_1, NAMESPACE, TYPE_ID, RULE_ID_1, SEQUENCE_1)
		builder.setDescription ACTION_DESCRIPTION_1
		builder.setAttributes myAttrs
		builder.build()
		TEST_ACTION_DEF = builder.build()

		// Create ActionAttributeBo
		ActionAttributeBo attributeBo1 = new ActionAttributeBo()
		attributeBo1.setId( ATTR_ID_1 )
		attributeBo1.setValue( ATTR_VALUE )

        // this causes trouble with other tests due to nested service call to findMatching, disabling:
        // attributeBo1.setAction( ActionBo.from(builder.build()) )

		attributeBo1.attributeDefinition = ADB1
		List<ActionAttributeBo> attrBos = [attributeBo1]
		
		
		// Create ActionBo
		TEST_ACTION_BO = new ActionBo()
		TEST_ACTION_BO.setId ACTION_ID_1
		TEST_ACTION_BO.setNamespace NAMESPACE
		TEST_ACTION_BO.setName ACTION_NAME_1
		TEST_ACTION_BO.setTypeId TYPE_ID
		TEST_ACTION_BO.setRule RuleBo.from(RuleDefinition.Builder.create(RULE_ID_1, "rule"+RULE_ID_1, "testNamespace", "bogusTypeId", "bogusPropId").build());
		TEST_ACTION_BO.setSequenceNumber SEQUENCE_1
		TEST_ACTION_BO.setAttributeBos attrBos
	}

	@Before
	void setupBoServiceMockContext() {
		mockDataObjectService = new MockFor(DataObjectService.class)
	}

	@Test
	public void test_getActionByActionId() {
		mockDataObjectService.demand.find(1..1) {clazz, id -> TEST_ACTION_BO}
		DataObjectService bos = mockDataObjectService.proxyDelegateInstance()

		ActionBoService service = new ActionBoServiceImpl()
		service.setDataObjectService(bos)
		ActionDefinition myAction = service.getActionByActionId(ACTION_ID_1)

		Assert.assertEquals(ActionBo.to(TEST_ACTION_BO), myAction)
		mockDataObjectService.verify(bos)
	}

	@Test
	public void test_getActionByActionId_when_none_found() {
		mockDataObjectService.demand.find(1..1) {Class clazz, String id -> null}
		DataObjectService bos = mockDataObjectService.proxyDelegateInstance()

		ActionBoService service = new ActionBoServiceImpl()
		service.setDataObjectService(bos)
		ActionDefinition myAction = service.getActionByActionId("I_DONT_EXIST")

		Assert.assertNull(myAction)
		mockDataObjectService.verify(bos)
	}

	@Test
	public void test_getActionByActionId_empty_id() {
		shouldFail(IllegalArgumentException.class) {
			new ActionBoServiceImpl().getActionByActionId("")
		}
	}

	@Test
	public void test_getActionByActionId_null_action_id() {
		shouldFail(IllegalArgumentException.class) {
			new ActionBoServiceImpl().getActionByActionId(null)
		}
	}
	
	@Test
	public void test_getActionByNameAndNamespace() {
		mockDataObjectService.demand.findMatching(1..1) {clazz, map -> buildQueryResults([TEST_ACTION_BO]) }
		DataObjectService bos = mockDataObjectService.proxyDelegateInstance()

		ActionBoService service = new ActionBoServiceImpl()
		service.setDataObjectService(bos)
		ActionDefinition myAction = service.getActionByNameAndNamespace(ACTION_ID_1, NAMESPACE)

		Assert.assertEquals(ActionBo.to(TEST_ACTION_BO), myAction)
		mockDataObjectService.verify(bos)
	}

	@Test
	public void test_getActionByNameAndNamespace_when_none_found() {
		mockDataObjectService.demand.findMatching(1..1) { clazz, crit -> buildQueryResults([])}
		DataObjectService bos = mockDataObjectService.proxyDelegateInstance()

		ActionBoService service = new ActionBoServiceImpl()
		service.setDataObjectService(bos)
		ActionDefinition myAction = service.getActionByNameAndNamespace("I_DONT_EXIST", NAMESPACE)

		Assert.assertNull(myAction)
		mockDataObjectService.verify(bos)
	}

	@Test
	public void test_getActionByNameAndNamespace_empty_name() {
		shouldFail(IllegalArgumentException.class) {
			new ActionBoServiceImpl().getActionByNameAndNamespace("", NAMESPACE)
		}
	}

	@Test
	public void test_getActionByNameAndNamespace_null_name() {
		shouldFail(IllegalArgumentException.class) {
			new ActionBoServiceImpl().getActionByNameAndNamespace(null, NAMESPACE)
		}
	}

	@Test
	public void test_getActionByNameAndNamespace_empty_namespace() {
		shouldFail(IllegalArgumentException.class) {
			new ActionBoServiceImpl().getActionByNameAndNamespace(ACTION_ID_1, "")
		}
	}

	@Test
	public void test_getActionByNameAndNamespace_null_namespace() {
		shouldFail(IllegalArgumentException.class) {
			new ActionBoServiceImpl().getActionByNameAndNamespace(ACTION_ID_1, null)
		}
	}

	@Test
	public void test_getActionsByRuleId() {
		mockDataObjectService.demand.findMatching(1..1) { clazz, crit -> buildQueryResults([TEST_ACTION_BO]) }

		DataObjectService bos = mockDataObjectService.proxyDelegateInstance()

		ActionBoService service = new ActionBoServiceImpl()
		service.setDataObjectService(bos)
		List<ActionDefinition> myActions = service.getActionsByRuleId(RULE_ID_1)

		Assert.assertEquals(ActionBo.to(TEST_ACTION_BO), myActions[0])
		mockDataObjectService.verify(bos)
	}

	@Test
	public void test_getActionsByRuleId_when_none_found() {
		mockDataObjectService.demand.findMatching(1..1) { clazz, crit -> buildQueryResults([])}
		DataObjectService dos = mockDataObjectService.proxyDelegateInstance()

		ActionBoService service = new ActionBoServiceImpl()
		service.setDataObjectService(dos)
		List<ActionDefinition> myActions = service.getActionsByRuleId("I_DONT_EXIST")

		Assert.assertEquals(myActions.size(), 0)
		mockDataObjectService.verify(dos)
	}

	@Test
	public void test_getActionsByRuleId_empty_id() {
		shouldFail(IllegalArgumentException.class) {
			new ActionBoServiceImpl().getActionsByRuleId("")
		}
	}

	@Test
	public void test_getActionsByRuleId_null_rule_id() {
		shouldFail(IllegalArgumentException.class) {
			new ActionBoServiceImpl().getActionsByRuleId(null)
		}
	}

	@Test
	public void test_getActionByRuleIdAndSequenceNumber() {
		mockDataObjectService.demand.find(1..1) {clazz, map -> TEST_ACTION_BO}
		DataObjectService bos = mockDataObjectService.proxyDelegateInstance()

		ActionBoService service = new ActionBoServiceImpl()
		service.setDataObjectService(bos)
		ActionDefinition myAction = service.getActionByRuleIdAndSequenceNumber(RULE_ID_1, SEQUENCE_1)

		Assert.assertEquals(ActionBo.to(TEST_ACTION_BO), myAction)
		mockDataObjectService.verify(bos)
	}

	@Test
	public void test_getActionByRuleIdAndSequenceNumber_when_none_found() {
		mockDataObjectService.demand.find(1..1) { clazz, crit -> null }
		DataObjectService dos = mockDataObjectService.proxyDelegateInstance()

		ActionBoService service = new ActionBoServiceImpl()
		service.setDataObjectService(dos)
		ActionDefinition myAction = service.getActionByRuleIdAndSequenceNumber("I_DONT_EXIST", SEQUENCE_1)

		Assert.assertNull(myAction)
		mockDataObjectService.verify(dos)
	}

	@Test
	public void test_getActionByRuleIdAndSequenceNumber_empty_id() {
		shouldFail(IllegalArgumentException.class) {
			new ActionBoServiceImpl().getActionByRuleIdAndSequenceNumber("", SEQUENCE_1)
		}
	}

	@Test
	public void test_getActionByRuleIdAndSequenceNumber_null_rule_id() {
		shouldFail(IllegalArgumentException.class) {
			new ActionBoServiceImpl().getActionByRuleIdAndSequenceNumber(null, SEQUENCE_1)
		}
	}
	
	@Test
	public void test_getActionByRuleIdAndSequenceNumber_null_rule_sequence() {
		shouldFail(IllegalArgumentException.class) {
			new ActionBoServiceImpl().getActionByRuleIdAndSequenceNumber(RULE_ID_1, null)
		}
	}

  @Test
  public void test_createAction_null_input() {
	  def dataObjectService = mockDataObjectService.proxyDelegateInstance()
	  ActionBoService service = new ActionBoServiceImpl()
	  service.setDataObjectService(dataObjectService)
	  shouldFail(IllegalArgumentException.class) {
		  service.createAction(null)
	  }
	  mockDataObjectService.verify(dataObjectService)
  }

  @Test
  void test_createAction_exists() {
		mockDataObjectService.demand.findMatching(1..1) {
			clazz, map -> buildQueryResults([TEST_ACTION_BO])
		}
		DataObjectService dataObjectService = mockDataObjectService.proxyDelegateInstance()
		ActionBoService service = new ActionBoServiceImpl()
		service.setDataObjectService(dataObjectService)
		shouldFail(IllegalStateException.class) {
			service.createAction(TEST_ACTION_DEF)
		}
		mockDataObjectService.verify(dataObjectService)
  }

  @Test
  void test_createAction_success() {
		mockDataObjectService.demand.findMatching(1..1) { clazz, crit -> buildQueryResults([])}
		mockDataObjectService.demand.findMatching(1..1) { clazz, crit -> buildQueryResults([ADB1]) }
		mockDataObjectService.demand.save { bo, po ->
            ((ActionBo)bo).setId("1");
            return bo;
        }
		
		DataObjectService dataObjectService = mockDataObjectService.proxyDelegateInstance()
		ActionBoService service = new ActionBoServiceImpl()
		service.setDataObjectService(dataObjectService)
		
		KrmsAttributeDefinitionService kads = new KrmsAttributeDefinitionServiceImpl();
		kads.setDataObjectService(dataObjectService)
		KrmsRepositoryServiceLocator.setKrmsAttributeDefinitionService(kads)
		
		service.createAction(TEST_ACTION_DEF)
		mockDataObjectService.verify(dataObjectService)
  }

  @Test
  public void test_updateAction_null_input() {
	  def dataObjectService = mockDataObjectService.proxyDelegateInstance()
	  ActionBoService service = new ActionBoServiceImpl()
	  service.setDataObjectService(dataObjectService)
	  shouldFail(IllegalArgumentException.class) {
		  service.updateAction(null)
	  }
	  mockDataObjectService.verify(dataObjectService)
  }

  @Test
  void test_updateAction_does_not_exist() {
		mockDataObjectService.demand.find(1..1) {
			Class clazz, String id -> null
		}
		DataObjectService dataObjectService = mockDataObjectService.proxyDelegateInstance()
		ActionBoService service = new ActionBoServiceImpl()
		service.setDataObjectService(dataObjectService)
		shouldFail(IllegalStateException.class) {
			service.updateAction(TEST_ACTION_DEF)
		}
		mockDataObjectService.verify(dataObjectService)
  }

  @Test
  void test_updateAction_success() {
		mockDataObjectService.demand.find(1..1) { clazz, id -> TEST_ACTION_BO}
		mockDataObjectService.demand.findMatching(1..1) { clazz, map -> buildQueryResults([ADB1]) }
		mockDataObjectService.demand.deleteMatching(1) { clazz, map -> }
		mockDataObjectService.demand.save { bo, persistanceOptions -> }

		DataObjectService dataObjectService = mockDataObjectService.proxyDelegateInstance()
		ActionBoService service = new ActionBoServiceImpl()
		service.setDataObjectService(dataObjectService)

		KrmsAttributeDefinitionService kads = new KrmsAttributeDefinitionServiceImpl();
		kads.setDataObjectService(dataObjectService)
		KrmsRepositoryServiceLocator.setKrmsAttributeDefinitionService(kads)
		
		service.updateAction(TEST_ACTION_DEF)
		mockDataObjectService.verify(dataObjectService)
  }
}
