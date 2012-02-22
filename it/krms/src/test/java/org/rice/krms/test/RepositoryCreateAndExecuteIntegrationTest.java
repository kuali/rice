/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.rice.krms.test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.kew.util.PerformanceLogger;
import org.kuali.rice.krms.api.engine.EngineResults;
import org.kuali.rice.krms.api.engine.ExecutionFlag;
import org.kuali.rice.krms.api.engine.ExecutionOptions;
import org.kuali.rice.krms.api.engine.Facts;
import org.kuali.rice.krms.api.engine.ResultEvent;
import org.kuali.rice.krms.api.engine.SelectionCriteria;
import org.kuali.rice.krms.api.repository.RuleRepositoryService;
import org.kuali.rice.krms.api.repository.action.ActionDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaItemDefinition;
import org.kuali.rice.krms.api.repository.context.ContextDefinition;
import org.kuali.rice.krms.api.repository.proposition.PropositionDefinition;
import org.kuali.rice.krms.api.repository.proposition.PropositionParameter;
import org.kuali.rice.krms.api.repository.proposition.PropositionParameterType;
import org.kuali.rice.krms.api.repository.proposition.PropositionType;
import org.kuali.rice.krms.api.repository.rule.RuleDefinition;
import org.kuali.rice.krms.api.repository.term.TermDefinition;
import org.kuali.rice.krms.api.repository.term.TermParameterDefinition;
import org.kuali.rice.krms.api.repository.term.TermResolverDefinition;
import org.kuali.rice.krms.api.repository.term.TermSpecificationDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeAttribute;
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeRepositoryService;
import org.kuali.rice.krms.framework.engine.ProviderBasedEngine;
import org.kuali.rice.krms.impl.provider.repository.CompoundPropositionTypeService;
import org.kuali.rice.krms.impl.provider.repository.RepositoryToEngineTranslator;
import org.kuali.rice.krms.impl.provider.repository.RepositoryToEngineTranslatorImpl;
import org.kuali.rice.krms.impl.provider.repository.RuleRepositoryContextProvider;
import org.kuali.rice.krms.impl.provider.repository.SimplePropositionTypeService;
import org.kuali.rice.krms.impl.repository.ActionBoService;
import org.kuali.rice.krms.impl.repository.ActionBoServiceImpl;
import org.kuali.rice.krms.impl.repository.AgendaBoService;
import org.kuali.rice.krms.impl.repository.AgendaBoServiceImpl;
import org.kuali.rice.krms.impl.repository.ContextAttributeBo;
import org.kuali.rice.krms.impl.repository.ContextBoService;
import org.kuali.rice.krms.impl.repository.ContextBoServiceImpl;
import org.kuali.rice.krms.impl.repository.KrmsAttributeDefinitionService;
import org.kuali.rice.krms.impl.repository.KrmsRepositoryServiceLocator;
import org.kuali.rice.krms.impl.repository.KrmsTypeBoServiceImpl;
import org.kuali.rice.krms.impl.repository.PropositionBoService;
import org.kuali.rice.krms.impl.repository.PropositionBoServiceImpl;
import org.kuali.rice.krms.impl.repository.RuleBoService;
import org.kuali.rice.krms.impl.repository.RuleBoServiceImpl;
import org.kuali.rice.krms.impl.repository.RuleRepositoryServiceImpl;
import org.kuali.rice.krms.impl.repository.TermBoService;
import org.kuali.rice.krms.impl.repository.TermBoServiceImpl;
import org.kuali.rice.krms.impl.type.KrmsTypeResolverImpl;
import org.kuali.rice.test.BaselineTestCase.BaselineMode;
import org.kuali.rice.test.BaselineTestCase.Mode;
import org.springframework.transaction.annotation.Transactional;

@BaselineMode(Mode.CLEAR_DB)
public class RepositoryCreateAndExecuteIntegrationTest extends AbstractBoTest {

    public static final String CAMPUS_CODE_TERM_NAME = "campusCodeTermSpec";
    // Services needed for creation:
	private TermBoService termBoService;
	private ContextBoService contextRepository;
    private KrmsTypeRepositoryService krmsTypeRepository;

    private PropositionBoService propositionBoService;
    private RuleBoService ruleBoService;
    private AgendaBoService agendaBoService;
    private ActionBoService actionBoService;
    private KrmsAttributeDefinitionService krmsAttributeDefinitionService;

    // Services needed for execution:
    // already have TermBoService above, need that for execution too
    private RuleRepositoryService ruleRepositoryService;

    private RuleRepositoryContextProvider contextProvider;
    private ProviderBasedEngine engine;
    private RepositoryToEngineTranslator repositoryToEngineTranslator;

	@Before
	public void setup() {
		// wire up BO services for creation

		termBoService = new TermBoServiceImpl();
		((TermBoServiceImpl)termBoService).setBusinessObjectService(getBoService());

		contextRepository = new ContextBoServiceImpl();
		((ContextBoServiceImpl)contextRepository).setBusinessObjectService(getBoService());

		krmsTypeRepository = new KrmsTypeBoServiceImpl();
		((KrmsTypeBoServiceImpl)krmsTypeRepository).setBusinessObjectService(getBoService());

		propositionBoService = new PropositionBoServiceImpl();
		((PropositionBoServiceImpl)propositionBoService).setBusinessObjectService(getBoService());

        ruleBoService = new RuleBoServiceImpl();
        ((RuleBoServiceImpl)ruleBoService).setBusinessObjectService(getBoService());

        krmsAttributeDefinitionService = KrmsRepositoryServiceLocator.getKrmsAttributeDefinitionService();

        agendaBoService = new AgendaBoServiceImpl();
        ((AgendaBoServiceImpl)agendaBoService).setBusinessObjectService(getBoService());
        ((AgendaBoServiceImpl)agendaBoService).setAttributeDefinitionService(krmsAttributeDefinitionService);

        actionBoService = new ActionBoServiceImpl();
        ((ActionBoServiceImpl)actionBoService).setBusinessObjectService(getBoService());


        // wire up services needed for execution

        // Already have termBoService from above, we'll need this for execution too

        ruleRepositoryService = new RuleRepositoryServiceImpl();
        ((RuleRepositoryServiceImpl)ruleRepositoryService).setBusinessObjectService(getBoService());
        ((RuleRepositoryServiceImpl)ruleRepositoryService).setCriteriaLookupService(KrmsRepositoryServiceLocator.getCriteriaLookupService());


        repositoryToEngineTranslator = new RepositoryToEngineTranslatorImpl();

        KrmsTypeResolverImpl typeResolver = new KrmsTypeResolverImpl();
        typeResolver.setTypeRepositoryService(krmsTypeRepository);

        CompoundPropositionTypeService compoundPropositionTypeService = new CompoundPropositionTypeService();

        // Circular Dependency:
        // CompoundPropositionTypeService -> RepositoryToEngineTranslatorImpl ->
        // KrmsTypeResolverImpl -> CompoundPropositionTypeService
        compoundPropositionTypeService.setTranslator(repositoryToEngineTranslator);

        SimplePropositionTypeService simplePropositionTypeService = new SimplePropositionTypeService();
        simplePropositionTypeService.setTypeResolver(typeResolver);
        simplePropositionTypeService.setTermBoService(termBoService);

        // TODO: custom functions too.
        simplePropositionTypeService.setFunctionRepositoryService(null);

        typeResolver.setDefaultCompoundPropositionTypeService(compoundPropositionTypeService);
        typeResolver.setDefaultSimplePropositionTypeService(simplePropositionTypeService);

        ((RepositoryToEngineTranslatorImpl)repositoryToEngineTranslator).setTermBoService(termBoService);
        ((RepositoryToEngineTranslatorImpl)repositoryToEngineTranslator).setRuleRepositoryService(ruleRepositoryService);
        ((RepositoryToEngineTranslatorImpl)repositoryToEngineTranslator).setTypeRepositoryService(krmsTypeRepository);
        ((RepositoryToEngineTranslatorImpl)repositoryToEngineTranslator).setTypeResolver(typeResolver);

        contextProvider = new RuleRepositoryContextProvider();
        contextProvider.setRuleRepositoryService(ruleRepositoryService);
        contextProvider.setRepositoryToEngineTranslator(repositoryToEngineTranslator);

        engine = new ProviderBasedEngine();
        engine.setContextProvider(contextProvider);

	}

    private ContextDefinition createContextDefinition(String nameSpace) {
        // Attribute Defn for context;
        KrmsAttributeDefinition.Builder contextTypeAttributeDefnBuilder = KrmsAttributeDefinition.Builder.create(null, "Context1Qualifier", nameSpace);
        contextTypeAttributeDefnBuilder.setLabel("Context 1 Qualifier");
        KrmsAttributeDefinition contextTypeAttributeDefinition = krmsAttributeDefinitionService.createAttributeDefinition(contextTypeAttributeDefnBuilder.build());

        // Attr for context;
        KrmsTypeAttribute.Builder krmsTypeAttrBuilder = KrmsTypeAttribute.Builder.create(null, contextTypeAttributeDefinition.getId(), 1);

        // KrmsType for context
        KrmsTypeDefinition.Builder krmsContextTypeDefnBuilder = KrmsTypeDefinition.Builder.create("KrmsTestContextType", nameSpace);
        krmsContextTypeDefnBuilder.setAttributes(Collections.singletonList(krmsTypeAttrBuilder));
        KrmsTypeDefinition krmsContextTypeDefinition = krmsContextTypeDefnBuilder.build();
        krmsContextTypeDefinition = krmsTypeRepository.createKrmsType(krmsContextTypeDefinition);

        // Context
        ContextDefinition.Builder contextBuilder = ContextDefinition.Builder.create(nameSpace, "Context1");
        contextBuilder.setTypeId(krmsContextTypeDefinition.getId());
        ContextDefinition contextDefinition = contextBuilder.build();
        contextDefinition = contextRepository.createContext(contextDefinition);

        // Context Attribute
        // TODO: do this fur eel
        ContextAttributeBo contextAttribute = new ContextAttributeBo();
        contextAttribute.setAttributeDefinitionId(contextTypeAttributeDefinition.getId());
        contextAttribute.setContextId(contextDefinition.getId());
        contextAttribute.setValue("BLAH");
        getBoService().save(contextAttribute);

        return contextDefinition;
    }

    private void createAgendaDefinition(ContextDefinition contextDefinition, String eventName, String nameSpace ) {
        KrmsTypeDefinition krmsGenericTypeDefinition = createKrmsGenericTypeDefinition(nameSpace, "testAgendaTypeService");

        AgendaDefinition agendaDef =
            AgendaDefinition.Builder.create(null, "testAgenda", krmsGenericTypeDefinition.getId(), contextDefinition.getId()).build();
        agendaDef = agendaBoService.createAgenda(agendaDef);

        AgendaItemDefinition.Builder agendaItemBuilder1 = AgendaItemDefinition.Builder.create(null, agendaDef.getId());
        agendaItemBuilder1.setRuleId(createRuleDefinition1(contextDefinition, nameSpace).getId());

        AgendaItemDefinition agendaItem1 = agendaBoService.createAgendaItem(agendaItemBuilder1.build());

        AgendaDefinition.Builder agendaDefBuilder1 = AgendaDefinition.Builder.create(agendaDef);
        agendaDefBuilder1.setAttributes(Collections.singletonMap("Event", eventName));
        agendaDefBuilder1.setFirstItemId(agendaItem1.getId());
        agendaDef = agendaDefBuilder1.build();

        agendaBoService.updateAgenda(agendaDef);
    }

    private KrmsTypeDefinition createKrmsCampusTypeDefinition(String nameSpace) {
	    // KrmsType for campus svc
        KrmsTypeDefinition.Builder krmsCampusTypeDefnBuilder = KrmsTypeDefinition.Builder.create("CAMPUS", nameSpace);
        KrmsTypeDefinition krmsCampusTypeDefinition = krmsTypeRepository.createKrmsType(krmsCampusTypeDefnBuilder.build());
        return krmsCampusTypeDefinition;
    }

    private KrmsTypeDefinition createKrmsActionTypeDefinition(String nameSpace) {
        KrmsTypeDefinition.Builder krmsActionTypeDefnBuilder = KrmsTypeDefinition.Builder.create("KrmsActionResolverType", nameSpace);
        krmsActionTypeDefnBuilder.setServiceName("testActionTypeService");
        KrmsTypeDefinition krmsActionTypeDefinition = krmsTypeRepository.createKrmsType(krmsActionTypeDefnBuilder.build());

        return krmsActionTypeDefinition;
    }

    private KrmsTypeDefinition createKrmsGenericTypeDefinition(String nameSpace) {
        return createKrmsGenericTypeDefinition(nameSpace, null);
    }
    
    

        private KrmsTypeDefinition createKrmsGenericTypeDefinition(String nameSpace, String serviceName) {
	    // Attribute Defn for generic type;
        KrmsAttributeDefinition.Builder genericTypeAttributeDefnBuilder = KrmsAttributeDefinition.Builder.create(null, "Event", nameSpace);
        genericTypeAttributeDefnBuilder.setLabel("event name");
        KrmsAttributeDefinition genericTypeAttributeDefinition1 = krmsAttributeDefinitionService.createAttributeDefinition(genericTypeAttributeDefnBuilder.build());

        // Attr for generic type;
        KrmsTypeAttribute.Builder genericTypeAttrBuilder = KrmsTypeAttribute.Builder.create(null, genericTypeAttributeDefinition1.getId(), 1);

		// Can use this generic type for KRMS bits that don't actually rely on services on the bus at this point in time
	    KrmsTypeDefinition.Builder krmsGenericTypeDefnBuilder = KrmsTypeDefinition.Builder.create("KrmsTestGenericType", nameSpace);
        krmsGenericTypeDefnBuilder.setServiceName(serviceName);
	    krmsGenericTypeDefnBuilder.setAttributes(Collections.singletonList(genericTypeAttrBuilder));
	    KrmsTypeDefinition krmsGenericTypeDefinition = krmsTypeRepository.createKrmsType(krmsGenericTypeDefnBuilder.build());

        return krmsGenericTypeDefinition;
    }

    @Transactional
    @Test
    public void createAndExecuteTest1() {
        String nameSpace = "KRMS_TEST";
        ContextDefinition contextDefinition = createContextDefinition(nameSpace);
        createAgendaDefinition(contextDefinition, "Tsunami", nameSpace);

        Map<String,String> contextQualifiers = new HashMap<String,String>();
        contextQualifiers.put("namespaceCode", nameSpace);
        contextQualifiers.put("name", "Context1");
        contextQualifiers.put("Context1Qualifier", "BLAH");
        DateTime now = new DateTime();

        SelectionCriteria sc1 = SelectionCriteria.createCriteria(now, contextQualifiers,
                Collections.singletonMap(AgendaDefinition.Constants.EVENT, "Tsunami"));

        Facts.Builder factsBuilder1 = Facts.Builder.create();
        factsBuilder1.addFact(CAMPUS_CODE_TERM_NAME, "BL");

        ExecutionOptions xOptions1 = new ExecutionOptions();
        xOptions1.setFlag(ExecutionFlag.LOG_EXECUTION, true);

        PerformanceLogger perfLog = new PerformanceLogger();
        perfLog.log("starting rule execution");
        EngineResults eResults1 = engine.execute(sc1, factsBuilder1.build(), xOptions1);
        perfLog.log("finished rule execution", true);
        List<ResultEvent> rEvents1 = eResults1.getAllResults();

        List<ResultEvent> ruleEvaluationResults1 = eResults1.getResultsOfType(ResultEvent.RULE_EVALUATED.toString());

        assertEquals(1, ruleEvaluationResults1.size());
        assertTrue("rule should have evaluated to true", ruleEvaluationResults1.get(0).getResult());
    }

    private RuleDefinition createRuleDefinition1(ContextDefinition contextDefinition, String nameSpace) {
        // Rule 1
        RuleDefinition.Builder ruleDefBuilder1 =
            RuleDefinition.Builder.create(null, "Rule1", nameSpace, null, null);
        RuleDefinition ruleDef1 = ruleBoService.createRule(ruleDefBuilder1.build());


        ruleDefBuilder1 = RuleDefinition.Builder.create(ruleDef1);
        ruleDefBuilder1.setProposition(createPropositionDefinition1(contextDefinition, ruleDef1));
        ruleDef1 = ruleDefBuilder1.build();
        ruleBoService.updateRule(ruleDef1);

        // Action
        ActionDefinition.Builder actionDefBuilder1 = ActionDefinition.Builder.create(null, "testAction1", nameSpace, createKrmsActionTypeDefinition(nameSpace).getId(), ruleDef1.getId(), 1);
        ActionDefinition actionDef1 = actionBoService.createAction(actionDefBuilder1.build());

        return ruleDef1;
    }

    private PropositionDefinition.Builder createPropositionDefinition1(ContextDefinition contextDefinition, RuleDefinition ruleDef1) {
        // Proposition for rule 1
        PropositionDefinition.Builder propositionDefBuilder1 =
            PropositionDefinition.Builder.create(null, PropositionType.SIMPLE.getCode(), ruleDef1.getId(), null /* type code is only for custom props */, Collections.<PropositionParameter.Builder>emptyList());
        propositionDefBuilder1.setDescription("is campus bloomington");

        // PropositionParams for rule 1
        List<PropositionParameter.Builder> propositionParams1 = new ArrayList<PropositionParameter.Builder>();
        propositionParams1.add(
                PropositionParameter.Builder.create(null, null, createTermDefinition1(contextDefinition).getId(), PropositionParameterType.TERM.getCode(), 1)
        );
        propositionParams1.add(
                PropositionParameter.Builder.create(null, null, "BL", PropositionParameterType.CONSTANT.getCode(), 2)
        );
        propositionParams1.add(
                PropositionParameter.Builder.create(null, null, "=", PropositionParameterType.OPERATOR.getCode(), 3)
        );

        // set the parent proposition so the builder will not puke
        for (PropositionParameter.Builder propositionParamBuilder : propositionParams1) {
            propositionParamBuilder.setProposition(propositionDefBuilder1);
        }

        propositionDefBuilder1.setParameters(propositionParams1);

        return propositionDefBuilder1;
    }

    private TermDefinition createTermDefinition1(ContextDefinition contextDefinition) {
        // campusCode TermSpec
        TermSpecificationDefinition campusCodeTermSpec =
            TermSpecificationDefinition.Builder.create(null, "campusCodeTermSpec", contextDefinition.getId(),
                    "java.lang.String").build();
        campusCodeTermSpec = termBoService.createTermSpecification(campusCodeTermSpec);

        // Term 1
        TermDefinition termDefinition1 =
            TermDefinition.Builder.create(null, TermSpecificationDefinition.Builder.create(campusCodeTermSpec), null).build();
        termDefinition1 = termBoService.createTermDefinition(termDefinition1);

        return termDefinition1;
    }


    @Transactional
    @Test
    public void createAndExecuteTest2() {
        String nameSpace = "KRMS_TEST_2";
        ContextDefinition contextDefinition = createContextDefinition(nameSpace);
        createAgendaDefinition(contextDefinition, "Earthquake", nameSpace);

        Map<String,String> contextQualifiers = new HashMap<String,String>();
        contextQualifiers.put("namespaceCode", nameSpace);
        contextQualifiers.put("name", "Context1");
        contextQualifiers.put("Context1Qualifier", "BLAH");
        Map<String,String> agendaQualifiers = new HashMap<String,String>();
        agendaQualifiers.put(AgendaDefinition.Constants.EVENT, "Earthquake");
        DateTime now = new DateTime();

	    SelectionCriteria sc2 = SelectionCriteria.createCriteria(now, contextQualifiers, agendaQualifiers);

	    Facts.Builder factsBuilder2 = Facts.Builder.create();
        factsBuilder2.addFact(CAMPUS_CODE_TERM_NAME, "BL");
        factsBuilder2.addFact("prereqTermSpec", "prereqValue");

	    ExecutionOptions xOptions2 = new ExecutionOptions();
	    xOptions2.setFlag(ExecutionFlag.LOG_EXECUTION, true);


        PerformanceLogger perfLog = new PerformanceLogger();
        perfLog.log("starting rule execution");
	    EngineResults eResults2 = engine.execute(sc2, factsBuilder2.build(), xOptions2);
        perfLog.log("finished rule execution", true);

        List<ResultEvent> rEvents2 = eResults2.getAllResults();

	    List<ResultEvent> ruleEvaluationResults2 = eResults2.getResultsOfType(ResultEvent.RULE_EVALUATED.toString());

	    assertEquals(1, ruleEvaluationResults2.size());
	    assertTrue("rule should have evaluated to true", ruleEvaluationResults2.get(0).getResult());
	    assertTrue("testAction (from type service configured in KRMSTestHarnessSpringBeans.xml) didn't fire",
	            TestActionTypeService.actionFired("testAction"));
	}

    private RuleDefinition createRuleDefinition2(ContextDefinition contextDefinition, String nameSpace) {
        KrmsTypeDefinition krmsActionTypeDefinition = createKrmsActionTypeDefinition(nameSpace);

        // Rule 2
        RuleDefinition.Builder ruleDefBuilder2 =
            RuleDefinition.Builder.create(null, "Rule2", nameSpace, krmsActionTypeDefinition.getId(), null);
        RuleDefinition ruleDef2 = ruleBoService.createRule(ruleDefBuilder2.build());


        ruleDefBuilder2 = RuleDefinition.Builder.create(ruleDef2);
        ruleDefBuilder2.setProposition(createPropositionDefinition2(contextDefinition, ruleDef2, nameSpace));
        ruleDef2 = ruleDefBuilder2.build();
        ruleBoService.updateRule(ruleDef2);

        // Action
        ActionDefinition.Builder actionDefBuilder2 = ActionDefinition.Builder.create(null, "testAction2", nameSpace, krmsActionTypeDefinition.getId(), ruleDef2.getId(), 1);
        ActionDefinition actionDef2 = actionBoService.createAction(actionDefBuilder2.build());

        return ruleDef2;
    }

    private PropositionDefinition.Builder createPropositionDefinition2(ContextDefinition contextDefinition, RuleDefinition ruleDef2, String nameSpace) {

        // Proposition
        PropositionDefinition.Builder propositionDefBuilder2 =
            PropositionDefinition.Builder.create(null, PropositionType.SIMPLE.getCode(), ruleDef2.getId(), null /* type code is only for custom props */, Collections.<PropositionParameter.Builder>emptyList());
        propositionDefBuilder2.setDescription("is campus bloomington");

        // PropositionParams
        List<PropositionParameter.Builder> propositionParams2 = new ArrayList<PropositionParameter.Builder>();
        propositionParams2.add(
                PropositionParameter.Builder.create(null, null, createTermDefinition2(contextDefinition, nameSpace).getId(), PropositionParameterType.TERM.getCode(), 1)
        );
        propositionParams2.add(
                PropositionParameter.Builder.create(null, null, "RESULT1", PropositionParameterType.CONSTANT.getCode(), 2)
        );
        propositionParams2.add(
                PropositionParameter.Builder.create(null, null, "=", PropositionParameterType.OPERATOR.getCode(), 3)
        );

        // set the parent proposition so the builder will not puke
        for (PropositionParameter.Builder propositionParamBuilder : propositionParams2) {
            propositionParamBuilder.setProposition(propositionDefBuilder2);
        }

        propositionDefBuilder2.setParameters(propositionParams2);

        return propositionDefBuilder2;
    }

    private TermDefinition createTermDefinition2(ContextDefinition contextDefinition, String nameSpace) {
        // output TermSpec
        TermSpecificationDefinition outputTermSpec =
            TermSpecificationDefinition.Builder.create(null, "outputTermSpec", contextDefinition.getId(),
                    "java.lang.String").build();
        outputTermSpec = termBoService.createTermSpecification(outputTermSpec);

        // prereq TermSpec
        TermSpecificationDefinition prereqTermSpec =
            TermSpecificationDefinition.Builder.create(null, "prereqTermSpec", contextDefinition.getId(),
                    "java.lang.String").build();
        prereqTermSpec = termBoService.createTermSpecification(prereqTermSpec);

        // Term Param
        TermParameterDefinition.Builder termParamBuilder2 =
            TermParameterDefinition.Builder.create(null, null, "testParamName", "testParamValue");

        // Term
        TermDefinition termDefinition2 =
            TermDefinition.Builder.create(null, TermSpecificationDefinition.Builder.create(outputTermSpec), Collections.singletonList(termParamBuilder2)).build();
        termDefinition2 = termBoService.createTermDefinition(termDefinition2);

		// KrmsType for TermResolver
		KrmsTypeDefinition.Builder krmsTermResolverTypeDefnBuilder = KrmsTypeDefinition.Builder.create("KrmsTestResolverType", nameSpace);
		krmsTermResolverTypeDefnBuilder.setServiceName("testResolverTypeService1");

		KrmsTypeDefinition krmsTermResolverTypeDefinition = krmsTypeRepository.createKrmsType(krmsTermResolverTypeDefnBuilder.build());

        // TermResolver
		TermResolverDefinition termResolverDef =
			TermResolverDefinition.Builder.create(null, nameSpace, "testResolver1", krmsTermResolverTypeDefinition.getId(),
					TermSpecificationDefinition.Builder.create(outputTermSpec),
					Collections.singleton(TermSpecificationDefinition.Builder.create(prereqTermSpec)),
					null,
					Collections.singleton("testParamName")).build();
		termResolverDef = termBoService.createTermResolver(termResolverDef);

        return termDefinition2;
    }

}
