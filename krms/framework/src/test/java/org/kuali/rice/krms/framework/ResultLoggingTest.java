package org.kuali.rice.krms.framework;

import static junit.framework.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.kuali.rice.krms.api.engine.EngineResults;
import org.kuali.rice.krms.api.engine.ExecutionOptions;
import org.kuali.rice.krms.api.engine.ExecutionFlag;
import org.kuali.rice.krms.api.engine.SelectionCriteria;
import org.kuali.rice.krms.api.engine.Term;
import org.kuali.rice.krms.api.engine.TermResolver;
import org.kuali.rice.krms.api.engine.TermSpecification;
import org.kuali.rice.krms.api.repository.LogicalOperator;
import org.kuali.rice.krms.framework.engine.Action;
import org.kuali.rice.krms.framework.engine.Agenda;
import org.kuali.rice.krms.framework.engine.BasicAgenda;
import org.kuali.rice.krms.framework.engine.BasicAgendaTree;
import org.kuali.rice.krms.framework.engine.BasicAgendaTreeEntry;
import org.kuali.rice.krms.framework.engine.BasicContext;
import org.kuali.rice.krms.framework.engine.BasicRule;
import org.kuali.rice.krms.framework.engine.ComparableTermBasedProposition;
import org.kuali.rice.krms.framework.engine.ComparisonOperator;
import org.kuali.rice.krms.framework.engine.CompoundProposition;
import org.kuali.rice.krms.framework.engine.Context;
import org.kuali.rice.krms.framework.engine.ContextProvider;
import org.kuali.rice.krms.framework.engine.Proposition;
import org.kuali.rice.krms.framework.engine.ProviderBasedEngine;
import org.kuali.rice.krms.framework.engine.ResultLogger;
import org.kuali.rice.krms.framework.engine.Rule;

public class ResultLoggingTest {
	private static final ResultLogger LOG = ResultLogger.getInstance();

	@Test
	public void integrationTest() {

		// build a simple rule
		Proposition prop1 = new ComparableTermBasedProposition(ComparisonOperator.GREATER_THAN, totalCostTerm, Integer.valueOf(1));
		Proposition prop2 = new ComparableTermBasedProposition(ComparisonOperator.LESS_THAN, totalCostTerm, Integer.valueOf(1000));
		CompoundProposition compoundProp1 = new CompoundProposition(LogicalOperator.AND, Arrays.asList(prop1, prop2));
		
		Action action1 = new SayHelloAction();
		Rule rule = new BasicRule("InBetween",compoundProp1, Arrays.asList(action1));
		
		BasicAgendaTree agendaTree = new BasicAgendaTree(new BasicAgendaTreeEntry(rule)); 
		Agenda agenda = new BasicAgenda("test", new HashMap<String, String>(), agendaTree);
		
		Map<String, String> contextQualifiers = new HashMap<String, String>();
		contextQualifiers.put("docTypeName", "Proposal");
		
		List<TermResolver<?>> testResolvers = new ArrayList<TermResolver<?>>();
		testResolvers.add(testResolver);
		
		Context context = new BasicContext(Arrays.asList(agenda), testResolvers);
		ContextProvider contextProvider = new ManualContextProvider(context);
		
		SelectionCriteria selectionCriteria = SelectionCriteria.createCriteria("test", null, contextQualifiers, Collections.EMPTY_MAP);
		
		ProviderBasedEngine engine = new ProviderBasedEngine();
		engine.setContextProvider(contextProvider);
		
		// Set execution options to log execution
		ExecutionOptions executionOptions = new ExecutionOptions().setFlag(ExecutionFlag.LOG_EXECUTION, true);
		
		LOG.init();
		EngineResults results = engine.execute(selectionCriteria, new HashMap<Term, Object>(), executionOptions);
		assertNotNull(results);
		
	}
	
	private static final Term totalCostTerm = new Term(new TermSpecification("totalCost","Integer"));
	
	private static final TermResolver<Integer> testResolver = new TermResolver<Integer>(){
		
		@Override
		public int getCost() { return 1; }
		
		@Override
		public TermSpecification getOutput() { return totalCostTerm.getSpecification(); }
		
		@Override
		public Set<TermSpecification> getPrerequisites() { return Collections.emptySet(); }
		
		@Override
		public Set<String> getParameterNames() {
			return Collections.emptySet();
		}
		
		@Override
		public Integer resolve(Map<TermSpecification, Object> resolvedPrereqs, Map<String, String> parameters) {
			return 5;
		}
	};

}
