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
import org.apache.commons.collections.CollectionUtils
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
import org.kuali.rice.krms.api.repository.proposition.PropositionDefinition
import org.kuali.rice.krms.api.repository.proposition.PropositionParameter
import org.kuali.rice.krms.api.repository.proposition.PropositionParameterContract
import org.kuali.rice.krms.api.repository.rule.RuleDefinition
import org.kuali.rice.krms.api.repository.term.TermDefinition
import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinition

import javax.xml.namespace.QName

import static org.kuali.rice.krms.impl.repository.RepositoryTestUtils.buildQueryResults;

class RuleBoServiceImplTest {

	private final shouldFail = new GroovyTestCase().&shouldFail

	def mockDataObjectService

	static def NAMESPACE = "KRMS_TEST"
	static def TYPE_ID="1234ABCD"

	static def ATTR_ID_1 = "ACTION_ATTR_001"
	static def ATTR_DEF_ID = "1002"
	static def ACTION_TYPE = "Notification"
	static def ATTR_VALUE = "Spam"
	static def SEQUENCE_1 = new Integer(1)

	private static final String RULE_ID_1 = "RULEID001"
	private static final String RULE_NAME = "Rule1"
	private static final String PROP_ID_1 = "PROP-001"
	private static final String ACTION_ID_1 = "ACTIONID01"

	private static final String PROP_DESCRIPTION = "is Campus Bloomington"
	private static final String PROPOSITION_TYPE_CD_S = "S"
	private static final List<PropositionParameter.Builder> PARM_LIST_1 = createPropositionParametersSet1()

	private static final String AGENDA_ID = "500Agenda"
	private static final String AGENDA_ITEM_ID_1 = "AgendaItem1"
	private static final String SUB_AGENDA_1= "SubAgenda1"

	static def ACTION_NAME_1 = "Say Hello"
	static def ACTION_DESCRIPTION_1 = "Send spam email"

	// test samples
	static def RuleDefinition TEST_RULE_DEF
	static def RuleBo TEST_RULE_BO
	static def ActionDefinition TEST_ACTION_DEF
	static def ActionBo TEST_ACTION_BO
	private static KrmsAttributeDefinitionBo ADB1
	
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
		Map<String,String> myActionAttrs = new HashMap<String,String>()
		myActionAttrs.put(ACTION_TYPE, ATTR_VALUE)
		ActionDefinition.Builder builder = ActionDefinition.Builder.create(ACTION_ID_1, ACTION_NAME_1, NAMESPACE, TYPE_ID, RULE_ID_1, SEQUENCE_1)
		builder.setDescription ACTION_DESCRIPTION_1
		builder.setAttributes myActionAttrs
		builder.build()
		TEST_ACTION_DEF = builder.build()

		// Create ActionAttributeBo
		ActionAttributeBo attributeBo1 = new ActionAttributeBo()
		attributeBo1.setId( ATTR_ID_1 )
		attributeBo1.setValue( ATTR_VALUE )

        // commenting out, this is causing some interference issues with other tests
        // attributeBo1.setAction( ActionBo.from(TEST_ACTION_DEF) )
		attributeBo1.attributeDefinition = ADB1
		List<ActionAttributeBo> attrBos = [attributeBo1]
				
		// Create ActionBo
		TEST_ACTION_BO = new ActionBo()
		TEST_ACTION_BO.setId ACTION_ID_1
		TEST_ACTION_BO.setNamespace NAMESPACE
		TEST_ACTION_BO.setName ACTION_NAME_1
		TEST_ACTION_BO.setTypeId TYPE_ID
		TEST_ACTION_BO.setRule RuleBo.from(RuleDefinition.Builder.create(RULE_ID_1, RULE_NAME, NAMESPACE, TYPE_ID, PROP_ID_1).build())
		TEST_ACTION_BO.setSequenceNumber SEQUENCE_1
		TEST_ACTION_BO.setAttributeBos attrBos

		// Create Proposition Builder
		PropositionDefinition.Builder myPropBuilder = PropositionDefinition.Builder.create(PROP_ID_1, PROPOSITION_TYPE_CD_S, RULE_ID_1, TYPE_ID, PARM_LIST_1)
		myPropBuilder.setDescription(PROP_DESCRIPTION)
		myPropBuilder.setTypeId(TYPE_ID)

		// Create Rule
		RuleDefinition.Builder myBuilder = RuleDefinition.Builder.create(RULE_ID_1, RULE_NAME, NAMESPACE, TYPE_ID, PROP_ID_1)
		myBuilder.setProposition myPropBuilder
		TEST_RULE_DEF = myBuilder.build()
		TEST_RULE_BO = RuleBo.from(TEST_RULE_DEF)

	}

	@Before
	void setupBoServiceMockContext() {
		mockDataObjectService = new MockFor(DataObjectService.class)
	}

	@Test
	public void test_getRuleByRuleId() {
		mockDataObjectService.demand.find(1..1) {clazz, id -> TEST_RULE_BO}
		DataObjectService bos = mockDataObjectService.proxyDelegateInstance()

		RuleBoService service = new RuleBoServiceImpl()
		service.setDataObjectService(bos)
		RuleDefinition myRule = service.getRuleByRuleId(RULE_ID_1)

		Assert.assertEquals(RuleBo.to(TEST_RULE_BO), myRule)
		mockDataObjectService.verify(bos)
	}

	@Test
	public void test_getRuleByRuleId_when_none_found() {
		mockDataObjectService.demand.find(1..1) { clazz, id -> null}
		DataObjectService bos = mockDataObjectService.proxyDelegateInstance()

		RuleBoService service = new RuleBoServiceImpl()
		service.setDataObjectService(bos)
		RuleDefinition myRule = service.getRuleByRuleId("I_DONT_EXIST")

		Assert.assertNull(myRule)
		mockDataObjectService.verify(bos)
	}

	@Test
	public void test_getRuleByRuleId_empty_id() {
		shouldFail(IllegalArgumentException.class) {
			new RuleBoServiceImpl().getRuleByRuleId("")
		}
	}

	@Test
	public void test_getRuleByRuleId_null_action_id() {
		shouldFail(IllegalArgumentException.class) {
			new RuleBoServiceImpl().getRuleByRuleId(null)
		}
	}

	@Test
	public void test_getRuleByNameAndNamespace() {
		mockDataObjectService.demand.findMatching(1..1) { clazz, crit -> buildQueryResults([TEST_RULE_BO]) }
		DataObjectService bos = mockDataObjectService.proxyDelegateInstance()

		RuleBoService service = new RuleBoServiceImpl()
		service.setDataObjectService(bos)
		RuleDefinition myRule = service.getRuleByNameAndNamespace(RULE_ID_1, NAMESPACE)

		Assert.assertEquals(RuleBo.to(TEST_RULE_BO), myRule)
		mockDataObjectService.verify(bos)
	}

	@Test
	public void test_getRuleByNameAndNamespace_when_none_found() {
		mockDataObjectService.demand.findMatching (1..1) { clazz, crit -> buildQueryResults([]) }
		DataObjectService bos = mockDataObjectService.proxyDelegateInstance()

		RuleBoService service = new RuleBoServiceImpl()
		service.setDataObjectService(bos)
		RuleDefinition myRule = service.getRuleByNameAndNamespace("I_DONT_EXIST", NAMESPACE)

		Assert.assertTrue(CollectionUtils.isEmpty(myRule))
		mockDataObjectService.verify(bos)
	}

	@Test
	public void test_getRuleByNameAndNamespace_empty_name() {
		shouldFail(IllegalArgumentException.class) {
			new RuleBoServiceImpl().getRuleByNameAndNamespace("", NAMESPACE)
		}
	}

	@Test
	public void test_getRuleByNameAndNamespace_null_name() {
		shouldFail(IllegalArgumentException.class) {
			new RuleBoServiceImpl().getRuleByNameAndNamespace(null, NAMESPACE)
		}
	}

	@Test
	public void test_getRuleByNameAndNamespace_empty_namespace() {
		shouldFail(IllegalArgumentException.class) {
			new RuleBoServiceImpl().getRuleByNameAndNamespace(RULE_ID_1, "")
		}
	}

	@Test
	public void test_getRuleByNameAndNamespace_null_namespace() {
		shouldFail(IllegalArgumentException.class) {
			new RuleBoServiceImpl().getRuleByNameAndNamespace(RULE_ID_1, null)
		}
	}

	@Test
	public void test_createRule_null_input() {
		def boService = mockDataObjectService.proxyDelegateInstance()
		RuleBoService service = new RuleBoServiceImpl()
		service.setDataObjectService(boService)
		shouldFail(IllegalArgumentException.class) {
			service.createRule(null)
		}
		mockDataObjectService.verify(boService)
	}

	@Test
	void test_createRule_exists() {
		mockDataObjectService.demand.findMatching(1..1) {
			clazz, crit -> buildQueryResults([TEST_RULE_BO])
		}
		DataObjectService bos = mockDataObjectService.proxyDelegateInstance()
		RuleBoService service = new RuleBoServiceImpl()
		service.setDataObjectService(bos)
		shouldFail(IllegalStateException.class) {
			service.createRule(TEST_RULE_DEF)
		}
		mockDataObjectService.verify(bos)
	}

	@Test
	void test_createRule_success() {
		mockDataObjectService.demand.findMatching (1..1) { clazz, crit -> buildQueryResults([])}
		mockDataObjectService.demand.save { bo, opts -> bo }

		DataObjectService bos = mockDataObjectService.proxyDelegateInstance()
		RuleBoService service = new RuleBoServiceImpl()
		service.setDataObjectService(bos)

		service.createRule(TEST_RULE_DEF)
		mockDataObjectService.verify(bos)
	}

	@Test
	public void test_updateRule_null_input() {
		def boService = mockDataObjectService.proxyDelegateInstance()
		RuleBoService service = new RuleBoServiceImpl()
		service.setDataObjectService(boService)
		shouldFail(IllegalArgumentException.class) {
			service.updateRule(null)
		}
		mockDataObjectService.verify(boService)
	}

	@Test
	void test_updateRule_does_not_exist() {
		mockDataObjectService.demand.find(1..1) {
			clazz, id -> null
		}
		DataObjectService bos = mockDataObjectService.proxyDelegateInstance()
		RuleBoService service = new RuleBoServiceImpl()
		service.setDataObjectService(bos)
		shouldFail(IllegalStateException.class) {
			service.updateRule(TEST_RULE_DEF)
		}
		mockDataObjectService.verify(bos)
	}

	@Test
	void test_updateRule_success() {
		mockDataObjectService.demand.find(1..1) { clazz, id -> TEST_RULE_BO }
		mockDataObjectService.demand.save { bo, opts -> bo }
		DataObjectService bos = mockDataObjectService.proxyDelegateInstance()
		RuleBoService service = new RuleBoServiceImpl()
		service.setDataObjectService(bos)
		service.updateRule(TEST_RULE_DEF)
		mockDataObjectService.verify(bos)
	}

	private static createPropositionParametersSet1(){
		List <PropositionParameter.Builder> propParms = new ArrayList <PropositionParameter.Builder> ()
		PropositionParameter.Builder ppBuilder1 = PropositionParameter.Builder.create(new PropositionParameterContract() {
					def String id = "1000"
					def String propId = "2001"
					def String value = "campusCode"
                                        def TermDefinition termValue = null
					def String parameterType = "T"
					def Integer sequenceNumber = new Integer(0)
					def Long versionNumber = new Long(1);
				})
		PropositionParameter.Builder ppBuilder2 = PropositionParameter.Builder.create(new PropositionParameterContract() {
					def String id = "1001"
					def String propId = "2001"
					def String value = "BL"
                                        def TermDefinition termValue = null
					def String parameterType = "C"
					def Integer sequenceNumber = new Integer(1)
					def Long versionNumber = new Long(1);
				})
		PropositionParameter.Builder ppBuilder3 = PropositionParameter.Builder.create(new PropositionParameterContract() {
					def String id = "1003"
					def String propId = "2001"
					def String value = "EQUALS"
                                        def TermDefinition termValue = null
					def String parameterType = "F"
					def Integer sequenceNumber = new Integer(2)
					def Long versionNumber = new Long(1);
				})
		for ( ppb in [ppBuilder1, ppBuilder2, ppBuilder3]){
			propParms.add (ppb)
		}
		return propParms;
	}

}
