package org.kuali.rice.krms.framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.kuali.rice.krms.api.engine.EngineResults;
import org.kuali.rice.krms.api.engine.ExecutionOptions;
import org.kuali.rice.krms.api.engine.LogicalOperator;
import org.kuali.rice.krms.api.engine.Proposition;
import org.kuali.rice.krms.api.engine.Rule;
import org.kuali.rice.krms.api.engine.SelectionCriteria;
import org.kuali.rice.krms.api.engine.Term;
import org.kuali.rice.krms.api.engine.TermResolver;
import org.kuali.rice.krms.api.engine.TermSpecification;
import org.kuali.rice.krms.framework.engine.Agenda;
import org.kuali.rice.krms.framework.engine.AgendaTree;
import org.kuali.rice.krms.framework.engine.BasicAgenda;
import org.kuali.rice.krms.framework.engine.BasicContext;
import org.kuali.rice.krms.framework.engine.BasicRule;
import org.kuali.rice.krms.framework.engine.ComparableTermBasedProposition;
import org.kuali.rice.krms.framework.engine.ComparisonOperator;
import org.kuali.rice.krms.framework.engine.CompoundProposition;
import org.kuali.rice.krms.framework.engine.Context;
import org.kuali.rice.krms.framework.engine.ContextProvider;
import org.kuali.rice.krms.framework.engine.ProviderBasedEngine;
import org.kuali.rice.krms.framework.engine.ResultLogger;

public class KRMSTest {
	private static final ResultLogger LOG = ResultLogger.getInstance();

	@Test
	public void compoundPropositionTest() {

		Proposition prop1 = new ComparableTermBasedProposition(ComparisonOperator.GREATER_THAN, totalCostTerm, Integer.valueOf(1));
		Proposition prop2 = new ComparableTermBasedProposition(ComparisonOperator.LESS_THAN, totalCostTerm, Integer.valueOf(1000));
		Proposition prop3 = new ComparableTermBasedProposition(ComparisonOperator.GREATER_THAN, totalCostTerm, Integer.valueOf(1000));
		CompoundProposition compoundProp1 = new CompoundProposition(LogicalOperator.AND, Arrays.asList(prop1, prop2, prop3));
		
		Rule rule = new BasicRule("InBetween",compoundProp1, null);
		
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

	private static final TermResolver<Integer> testResolver = new TermResolverMock<Integer>(totalCostTerm.getSpecification(), 1);
}
