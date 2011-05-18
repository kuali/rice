package org.rice.krms.test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.krms.api.engine.Engine;
import org.kuali.rice.krms.api.engine.EngineResults;
import org.kuali.rice.krms.api.engine.ExecutionOptions;
import org.kuali.rice.krms.api.engine.ResultEvent;
import org.kuali.rice.krms.api.engine.SelectionCriteria;
import org.kuali.rice.krms.api.engine.Term;
import org.kuali.rice.krms.api.engine.TermSpecification;
import org.kuali.rice.krms.api.repository.RuleRepositoryService;
import org.kuali.rice.krms.framework.engine.ProviderBasedEngine;
import org.kuali.rice.krms.impl.provider.repository.RepositoryToEngineTranslator;
import org.kuali.rice.krms.impl.provider.repository.RepositoryToEngineTranslatorImpl;
import org.kuali.rice.krms.impl.provider.repository.RuleRepositoryContextProvider;
import org.kuali.rice.krms.impl.repository.RuleRepositoryServiceImpl;
import org.kuali.rice.krms.impl.repository.TermBoService;
import org.kuali.rice.krms.impl.repository.TermBoServiceImpl;
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

	@Before
	public void setup() {
		super.setup();
		
		// insert repository items
		
		// wire up BO services
		ruleRepositoryService = new RuleRepositoryServiceImpl();
		((RuleRepositoryServiceImpl)ruleRepositoryService).setBusinessObjectService(getBoService());

		termBoService = new TermBoServiceImpl();
		((TermBoServiceImpl)termBoService).setBusinessObjectService(getBoService());
		
		repositoryToEngineTranslator = new RepositoryToEngineTranslatorImpl();
		((RepositoryToEngineTranslatorImpl)repositoryToEngineTranslator).setTermBoService(termBoService);
		
		contextProvider = new RuleRepositoryContextProvider();
		contextProvider.setRuleRepositoryService(ruleRepositoryService);
		contextProvider.setRepositoryToEngineTranslator(repositoryToEngineTranslator);
		
		engine = new ProviderBasedEngine();
		engine.setContextProvider(contextProvider);
	}
	
	@Test
	public void executeSimpleContextTest() {
		
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
		
		ExecutionOptions xOptions = new ExecutionOptions();
		EngineResults eResults = engine.execute(sc, facts, xOptions);
		List<ResultEvent> rEvents = eResults.getAllResults();
	}
	
}
