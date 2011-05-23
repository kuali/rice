package org.rice.krms.test;

import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.kew.util.PerformanceLogger;
import org.kuali.rice.krms.api.engine.EngineResults;
import org.kuali.rice.krms.api.engine.ExecutionFlag;
import org.kuali.rice.krms.api.engine.ExecutionOptions;
import org.kuali.rice.krms.api.engine.ResultEvent;
import org.kuali.rice.krms.api.engine.SelectionCriteria;
import org.kuali.rice.krms.api.engine.Term;
import org.kuali.rice.krms.api.engine.TermSpecification;
import org.kuali.rice.krms.api.repository.RuleRepositoryService;
import org.kuali.rice.krms.api.repository.action.ActionDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaItem;
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
import org.kuali.rice.krms.framework.engine.ResultLogger;
import org.kuali.rice.krms.framework.engine.result.EngineResultListener;
import org.kuali.rice.krms.framework.engine.result.Log4jResultListener;
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
import org.kuali.rice.krms.impl.repository.KrmsAttributeDefinitionServiceImpl;
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
		super.setup();
		
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

        agendaBoService = new AgendaBoServiceImpl();
        ((AgendaBoServiceImpl)agendaBoService).setBusinessObjectService(getBoService());

        actionBoService = new ActionBoServiceImpl();
        ((ActionBoServiceImpl)actionBoService).setBusinessObjectService(getBoService());
        
        krmsAttributeDefinitionService = new KrmsAttributeDefinitionServiceImpl();
        ((KrmsAttributeDefinitionServiceImpl)krmsAttributeDefinitionService).setBusinessObjectService(getBoService());
        
        
        // wire up services needed for execution

        // Already have termBoService from above, we'll need this for execution too
        
        ruleRepositoryService = new RuleRepositoryServiceImpl();
        ((RuleRepositoryServiceImpl)ruleRepositoryService).setBusinessObjectService(getBoService());

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
	
	@Transactional
	@Test
	public void createAndExecuteTest() {

	    //
	    // create!
	    //
	    
	    // Attribute Defn for context;
	    KrmsAttributeDefinition.Builder contextTypeAttributeDefnBuilder = KrmsAttributeDefinition.Builder.create(null, "Context1Qualifier", "KRMS_TEST");
	    contextTypeAttributeDefnBuilder.setLabel("Context 1 Qualifier");
	    KrmsAttributeDefinition contextTypeAttributeDefinition = krmsAttributeDefinitionService.createAttributeDefinition(contextTypeAttributeDefnBuilder.build());
	    
	    // Attr for context;
	    KrmsTypeAttribute.Builder krmsTypeAttrBuilder = KrmsTypeAttribute.Builder.create(null, null, contextTypeAttributeDefinition.getId(), 1);

		// KrmsType for context
        KrmsTypeDefinition.Builder krmsContextTypeDefnBuilder = KrmsTypeDefinition.Builder.create(null, "KrmsTestContextType", "KRMS_TEST");
        krmsContextTypeDefnBuilder.setAttributes(Collections.singletonList(krmsTypeAttrBuilder));
		KrmsTypeDefinition krmsContextTypeDefinition = krmsContextTypeDefnBuilder.build();
		krmsContextTypeDefinition = krmsTypeRepository.createKrmsType(krmsContextTypeDefinition);

//		KrmsTypeAttribute contextAttributeDefn = krmsContextTypeDefinition.getAttributes().get(0);
		
        // Context
        ContextDefinition.Builder contextBuilder = ContextDefinition.Builder.create("KRMS_TEST", "Context1");
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
        
        // output TermSpec
        TermSpecificationDefinition outputTermSpec = 
            TermSpecificationDefinition.Builder.create(null, contextDefinition.getId(), 
                    "outputTermSpec", "java.lang.String").build();
        outputTermSpec = termBoService.createTermSpecification(outputTermSpec);
        
        // prereq TermSpec
        TermSpecificationDefinition prereqTermSpec = 
            TermSpecificationDefinition.Builder.create(null, contextDefinition.getId(), 
                    "prereqTermSpec", "java.lang.String").build();
        prereqTermSpec = termBoService.createTermSpecification(prereqTermSpec);

        // Term Param
        TermParameterDefinition.Builder termParamBuilder = 
            TermParameterDefinition.Builder.create(null, null, "testParamName", "testParamValue");
        
        // Term
        TermDefinition termDefinition = 
            TermDefinition.Builder.create(null, TermSpecificationDefinition.Builder.create(outputTermSpec), Collections.singleton(termParamBuilder)).build();
        termDefinition = termBoService.createTermDefinition(termDefinition);
		
	    // KrmsType for campus svc
        KrmsTypeDefinition.Builder krmsCampusTypeDefnBuilder = KrmsTypeDefinition.Builder.create(null, "CAMPUS", "KRMS_TEST");
        krmsCampusTypeDefnBuilder.setServiceName("myCampusService");
        KrmsTypeDefinition krmsCampusTypeDefinition = krmsCampusTypeDefnBuilder.build();
        krmsCampusTypeDefinition = krmsTypeRepository.createKrmsType(krmsCampusTypeDefinition);

        // Rule
        RuleDefinition.Builder ruleDefBuilder = 
            RuleDefinition.Builder.create(null, "Rule1", "KRMS_TEST", krmsCampusTypeDefinition.getId(), null);
        RuleDefinition ruleDef = ruleBoService.createRule(ruleDefBuilder.build());

        // Proposition
        PropositionDefinition.Builder propositionDefBuilder =
            PropositionDefinition.Builder.create(PropositionType.SIMPLE.getCode(), ruleDef.getId(), null /* type code is only for custom props */, Collections.<PropositionParameter.Builder>emptyList());
        propositionDefBuilder.setDescription("is campus bloomington");
        
        // PropositionParams
        List<PropositionParameter.Builder> propositionParams = new ArrayList<PropositionParameter.Builder>();
        propositionParams.add( 
                PropositionParameter.Builder.create(null, null, termDefinition.getId(), PropositionParameterType.TERM.getCode(), 1)
        );
        propositionParams.add( 
                PropositionParameter.Builder.create(null, null, "RESULT1", PropositionParameterType.CONSTANT.getCode(), 2)
        );
        propositionParams.add( 
                PropositionParameter.Builder.create(null, null, "=", PropositionParameterType.OPERATOR.getCode(), 3)
        );
        
        // set the parent proposition so the builder will not puke
        for (PropositionParameter.Builder propositionParamBuilder : propositionParams) {
            propositionParamBuilder.setProposition(propositionDefBuilder);
        }

        propositionDefBuilder.setParameters(propositionParams);

        ruleDefBuilder = RuleDefinition.Builder.create(ruleDef);
        ruleDefBuilder.setProposition(propositionDefBuilder);
        ruleDef = ruleDefBuilder.build();
        ruleBoService.updateRule(ruleDef);
        
        // KrmsType for Action
        KrmsTypeDefinition.Builder krmsActionTypeDefnBuilder = KrmsTypeDefinition.Builder.create(null, "KrmsActionResolverType", "KRMS_TEST");
        krmsActionTypeDefnBuilder.setServiceName("testActionTypeService");
        KrmsTypeDefinition krmsActionTypeDefinition = krmsTypeRepository.createKrmsType(krmsActionTypeDefnBuilder.build());

        // Action
        ActionDefinition.Builder actionDefBuilder = ActionDefinition.Builder.create(null, "testAction", "KRMS_TEST", krmsActionTypeDefinition.getId(), ruleDef.getId(), 1);
        ActionDefinition actionDef = actionBoService.createAction(actionDefBuilder.build());

		// KrmsType for TermResolver
		KrmsTypeDefinition.Builder krmsTermResolverTypeDefnBuilder = KrmsTypeDefinition.Builder.create(null, "KrmsTestResolverType", "KRMS_TEST");
		krmsTermResolverTypeDefnBuilder.setServiceName("testResolverTypeService1");
		
		KrmsTypeDefinition krmsTermResolverTypeDefinition = krmsTypeRepository.createKrmsType(krmsTermResolverTypeDefnBuilder.build());

		// TermResolver
		TermResolverDefinition termResolverDef = 
			TermResolverDefinition.Builder.create(null, "KRMS_TEST", "testResolver1", contextDefinition.getId(), krmsTermResolverTypeDefinition.getId(), 
					TermSpecificationDefinition.Builder.create(outputTermSpec), 
					Collections.singleton(TermSpecificationDefinition.Builder.create(prereqTermSpec)), 
					null, 
					Collections.singleton("testParamName")).build();
		termResolverDef = termBoService.createTermResolver(termResolverDef);
		
	    // Attribute Defn for generic type;
        KrmsAttributeDefinition.Builder genericTypeAttributeDefnBuilder = KrmsAttributeDefinition.Builder.create(null, "Event", "KRMS_TEST");
        genericTypeAttributeDefnBuilder.setLabel("event name");
        KrmsAttributeDefinition genericTypeAttributeDefinition = krmsAttributeDefinitionService.createAttributeDefinition(genericTypeAttributeDefnBuilder.build());
        
        // Attr for generic type;
        KrmsTypeAttribute.Builder genericTypeAttrBuilder = KrmsTypeAttribute.Builder.create(null, null, genericTypeAttributeDefinition.getId(), 1);

		// Can use this generic type for KRMS bits that don't actually rely on services on the bus at this point in time
	    KrmsTypeDefinition.Builder krmsGenericTypeDefnBuilder = KrmsTypeDefinition.Builder.create(null, "KrmsTestGenericType", "KRMS_TEST");
	    krmsGenericTypeDefnBuilder.setAttributes(Collections.singletonList(genericTypeAttrBuilder));
	    KrmsTypeDefinition krmsGenericTypeDefinition = krmsTypeRepository.createKrmsType(krmsGenericTypeDefnBuilder.build());

	    AgendaDefinition agendaDef = 
	        AgendaDefinition.Builder.create(null, "testAgenda", "KRMS_TEST", krmsGenericTypeDefinition.getId(), contextDefinition.getId()).build();
	    agendaDef = agendaBoService.createAgenda(agendaDef);
	    
	    AgendaItem.Builder agendaItemBuilder = AgendaItem.Builder.create(null, agendaDef.getId());
	    agendaItemBuilder.setRuleId(ruleDef.getId());
	    
	    AgendaItem agendaItem = agendaBoService.createAgendaItem(agendaItemBuilder.build());
	    
	    AgendaDefinition.Builder agendaDefBuilder = AgendaDefinition.Builder.create(agendaDef);
	    agendaDefBuilder.setAttributes(Collections.singletonMap("Event", "Earthquake"));
	    agendaDefBuilder.setFirstItemId(agendaItem.getId());
	    agendaDef = agendaDefBuilder.build();
	    
	    agendaBoService.updateAgenda(agendaDef);
	    
	    //
	    // execute!
	    //
	    
	    Map<String,String> contextQualifiers = new HashMap<String,String>();
	    contextQualifiers.put("namespaceCode", "KRMS_TEST");
	    contextQualifiers.put("name", "Context1");
	    contextQualifiers.put("Context1Qualifier", "BLAH");
	    Map<String,String> agendaQualifiers = new HashMap<String,String>();
	    Date now = new Date();
	    SelectionCriteria sc = SelectionCriteria.createCriteria("Earthquake", now, contextQualifiers, agendaQualifiers);

	    Map<Term, Object> facts = new HashMap<Term, Object>();
	    TermSpecification campusCodeTermSpec = new TermSpecification("campusCodeTermSpec", "campusCodeType");
	    Term myCampusCode = new Term( campusCodeTermSpec );
	    facts.put(myCampusCode, "BL");
	    
	    TermSpecification resolverPrereqTermSpec = new TermSpecification("prereqTermSpec", "java.lang.String");
        Term resolverPrereqTerm = new Term(resolverPrereqTermSpec);
        facts.put(resolverPrereqTerm, "prereqValue");

	    ExecutionOptions xOptions = new ExecutionOptions();
	    xOptions.setFlag(ExecutionFlag.LOG_EXECUTION, true);

	    // Need to initialize ResultLogger to get results.
	    // TODO: I'm concerned about how this will deal w/ concurrency.
	    ResultLogger resultLogger = ResultLogger.getInstance();
	    resultLogger.addListener(new EngineResultListener());
        resultLogger.addListener(new Log4jResultListener());
	    
        PerformanceLogger perfLog = new PerformanceLogger();
        perfLog.log("starting rule execution");
	    EngineResults eResults = engine.execute(sc, facts, xOptions);
        perfLog.log("finished rule execution", true);

        List<ResultEvent> rEvents = eResults.getAllResults();
	    
	    List<ResultEvent> ruleEvaluationResults = eResults.getResultsOfType(ResultEvent.RuleEvaluated.toString());
	    
	    assertTrue("We should have results for one rule", ruleEvaluationResults.size() == 1);
	    assertTrue("rule should have evaluated to true", ruleEvaluationResults.get(0).getResult() == true);
	    assertTrue("testAction (from type service configured in KRMSTestHarnessSpringBeans.xml) didn't fire", 
	            TestActionTypeService.actionFired("testAction"));
	}
	
}
