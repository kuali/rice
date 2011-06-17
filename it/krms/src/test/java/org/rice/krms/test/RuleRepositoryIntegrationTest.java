package org.rice.krms.test;

import static junit.framework.Assert.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.krms.api.engine.Engine;
import org.kuali.rice.krms.api.engine.EngineResults;
import org.kuali.rice.krms.api.engine.ExecutionFlag;
import org.kuali.rice.krms.api.engine.ExecutionOptions;
import org.kuali.rice.krms.api.engine.ResultEvent;
import org.kuali.rice.krms.api.engine.SelectionCriteria;
import org.kuali.rice.krms.api.engine.Term;
import org.kuali.rice.krms.api.engine.TermSpecification;
import org.kuali.rice.krms.api.repository.RuleRepositoryService;
import org.kuali.rice.krms.api.repository.type.KrmsTypeRepositoryService;
import org.kuali.rice.krms.framework.engine.ProviderBasedEngine;
import org.kuali.rice.krms.framework.engine.ResultLogger;
import org.kuali.rice.krms.framework.engine.result.EngineResultListener;
import org.kuali.rice.krms.framework.engine.result.Log4jResultListener;
import org.kuali.rice.krms.impl.provider.repository.RepositoryToEngineTranslator;
import org.kuali.rice.krms.impl.provider.repository.RepositoryToEngineTranslatorImpl;
import org.kuali.rice.krms.impl.provider.repository.RuleRepositoryContextProvider;
import org.kuali.rice.krms.impl.provider.repository.SimplePropositionTypeService;
import org.kuali.rice.krms.impl.repository.KrmsTypeBoServiceImpl;
import org.kuali.rice.krms.impl.repository.RuleRepositoryServiceImpl;
import org.kuali.rice.krms.impl.repository.TermBoService;
import org.kuali.rice.krms.impl.repository.TermBoServiceImpl;
import org.kuali.rice.krms.impl.type.KrmsTypeResolverImpl;
import org.kuali.rice.test.BaselineTestCase.BaselineMode;
import org.kuali.rice.test.BaselineTestCase.Mode;
import org.kuali.rice.test.data.UnitTestData;
import org.kuali.rice.test.data.PerSuiteUnitTestData;

@BaselineMode(Mode.ROLLBACK)
@PerSuiteUnitTestData({
        @UnitTestData(filename = "classpath:org/kuali/rice/test/RuleRepositoryTestData.sql")
})
public class RuleRepositoryIntegrationTest extends AbstractBoTest {
	
	private RuleRepositoryService ruleRepositoryService;

	private RuleRepositoryContextProvider contextProvider;
	private ProviderBasedEngine engine;
	private RepositoryToEngineTranslator repositoryToEngineTranslator;
	private TermBoService termBoService;
    private KrmsTypeRepositoryService krmsTypeRepository;

    private ResultLogger resultLogger = ResultLogger.getInstance();
    
	@Before
	public void setup() {
		super.setup();
		
		// insert repository items
		
		// wire up BO services
		ruleRepositoryService = new RuleRepositoryServiceImpl();
		((RuleRepositoryServiceImpl)ruleRepositoryService).setBusinessObjectService(getBoService());

		termBoService = new TermBoServiceImpl();
		((TermBoServiceImpl)termBoService).setBusinessObjectService(getBoService());
		
		krmsTypeRepository = new KrmsTypeBoServiceImpl();
		((KrmsTypeBoServiceImpl)krmsTypeRepository).setBusinessObjectService(getBoService());

        KrmsTypeResolverImpl typeResolver = new KrmsTypeResolverImpl();
        typeResolver.setTypeRepositoryService(krmsTypeRepository);

        SimplePropositionTypeService simplePropositionTypeService = new SimplePropositionTypeService();
        simplePropositionTypeService.setTypeResolver(typeResolver);
        simplePropositionTypeService.setTermBoService(termBoService);
        typeResolver.setDefaultSimplePropositionTypeService(simplePropositionTypeService);
        
		repositoryToEngineTranslator = new RepositoryToEngineTranslatorImpl();
		((RepositoryToEngineTranslatorImpl)repositoryToEngineTranslator).setTermBoService(termBoService);
        ((RepositoryToEngineTranslatorImpl)repositoryToEngineTranslator).setRuleRepositoryService(ruleRepositoryService);
        ((RepositoryToEngineTranslatorImpl)repositoryToEngineTranslator).setTypeRepositoryService(krmsTypeRepository);
        ((RepositoryToEngineTranslatorImpl)repositoryToEngineTranslator).setTypeResolver(typeResolver);
	
		contextProvider = new RuleRepositoryContextProvider();
		contextProvider.setRuleRepositoryService(ruleRepositoryService);
		contextProvider.setRepositoryToEngineTranslator(repositoryToEngineTranslator);
		
		engine = new ProviderBasedEngine();
		engine.setContextProvider(contextProvider);

	    resultLogger.addListener(new EngineResultListener());
        resultLogger.addListener(new Log4jResultListener());	
	}
	
	@Test
	public void executeSimpleContextTest() {
		
		Map<String,String> contextQualifiers = new HashMap<String,String>();
		contextQualifiers.put("namespaceCode", "KRMS_TEST");
		contextQualifiers.put("name", "Context1");
		contextQualifiers.put("Context1Qualifier", "BLAH");
		Map<String,String> agendaQualifiers = new HashMap<String,String>();
		Date now = new Date();
		SelectionCriteria sc = SelectionCriteria.createCriteria("EARTHQUAKE", now, contextQualifiers, agendaQualifiers);
		
		Map<Term, Object> facts = new HashMap<Term, Object>();
		TermSpecification campusCodeTermSpec = new TermSpecification("campusCodeTermSpec", "java.lang.String");
		Term myCampusCode = new Term( campusCodeTermSpec );
		facts.put(myCampusCode, "BL");
		
		ExecutionOptions xOptions = new ExecutionOptions();
		xOptions.setFlag(ExecutionFlag.LOG_EXECUTION, true);
		EngineResults eResults = engine.execute(sc, facts, xOptions);
		List<ResultEvent> rEvents = eResults.getAllResults();
		
	    List<ResultEvent> ruleEvaluationResults = eResults.getResultsOfType(ResultEvent.RuleEvaluated.toString());
	    
	    assertTrue("We should have results for one rule", ruleEvaluationResults.size() == 1);
	    assertTrue("rule should have evaluated to true", ruleEvaluationResults.get(0).getResult() == true);

	}
	
}
