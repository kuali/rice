package org.kuali.rice.krms.framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.kuali.rice.krms.engine.Action;
import org.kuali.rice.krms.engine.Agenda;
import org.kuali.rice.krms.engine.Context;
import org.kuali.rice.krms.engine.EngineResults;
import org.kuali.rice.krms.engine.ExecutionOptions;
import org.kuali.rice.krms.engine.LogicalOperator;
import org.kuali.rice.krms.engine.Proposition;
import org.kuali.rice.krms.engine.Rule;
import org.kuali.rice.krms.engine.SelectionCriteria;
import org.kuali.rice.krms.engine.Term;
import org.kuali.rice.krms.engine.TermResolver;
import org.kuali.rice.krms.engine.TermSpecification;
import org.kuali.rice.krms.framework.engine.AgendaTree;
import org.kuali.rice.krms.framework.engine.BasicAgenda;
import org.kuali.rice.krms.framework.engine.BasicContext;
import org.kuali.rice.krms.framework.engine.BasicRule;
import org.kuali.rice.krms.framework.engine.ComparableTermBasedProposition;
import org.kuali.rice.krms.framework.engine.ComparisonOperator;
import org.kuali.rice.krms.framework.engine.CompoundProposition;
import org.kuali.rice.krms.framework.engine.ContextProvider;
import org.kuali.rice.krms.framework.engine.ProviderBasedEngine;
import org.kuali.rice.krms.framework.engine.ResultLogger;

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
		
		AgendaTree agendaTree = new AgendaTree(Arrays.asList(rule), null, null, null); 
		Agenda agenda = new BasicAgenda("test", new HashMap<String, String>(), agendaTree);
		
		Map<String, String> contextQualifiers = new HashMap<String, String>();
		contextQualifiers.put("docTypeName", "Proposal");
		
		List<TermResolver<?>> testResolvers = new ArrayList<TermResolver<?>>();
		testResolvers.add(testResolver);
		
		Context context = new BasicContext(contextQualifiers, Arrays.asList(agenda), testResolvers);
		ContextProvider contextProvider = new ManualContextProvider(context);
		
		SelectionCriteria selectionCriteria = SelectionCriteria.createCriteria("test", null, contextQualifiers, Collections.EMPTY_MAP);
		
		ProviderBasedEngine engine = new ProviderBasedEngine();
		engine.setContextProvider(contextProvider);
		
		// Set execution options to log execution
		HashMap<String, String> xOptions = new HashMap<String, String>();
		xOptions.put(ExecutionOptions.LOG_EXECUTION.toString(), Boolean.toString(true));
		
		LOG.init();
		EngineResults results = engine.execute(selectionCriteria, new HashMap<Term, Object>(), xOptions);
		
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
