package org.kuali.rice.krms.framework;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.krms.api.Action;
import org.kuali.rice.krms.api.Agenda;
import org.kuali.rice.krms.api.Context;
import org.kuali.rice.krms.api.EngineResults;
import org.kuali.rice.krms.api.ExecutionOptions;
import org.kuali.rice.krms.api.Proposition;
import org.kuali.rice.krms.api.Rule;
import org.kuali.rice.krms.api.SelectionCriteria;
import org.kuali.rice.krms.api.Term;
import org.kuali.rice.krms.api.TermResolver;
import org.kuali.rice.krms.api.TermSpecification;
import org.kuali.rice.krms.framework.engine.AgendaTree;
import org.kuali.rice.krms.framework.engine.BasicAgenda;
import org.kuali.rice.krms.framework.engine.BasicContext;
import org.kuali.rice.krms.framework.engine.BasicRule;
import org.kuali.rice.krms.framework.engine.ComparableTermBasedProposition;
import org.kuali.rice.krms.framework.engine.ComparisonOperator;
import org.kuali.rice.krms.framework.engine.ContextProvider;
import org.kuali.rice.krms.framework.engine.ProviderBasedEngine;
import org.kuali.rice.krms.framework.engine.ResultLogger;

public class AgendaTest {
	private static final ResultLogger LOG = ResultLogger.getInstance();

	@Before
	public void setUp() {
		ActionMock.resetActionsFired();
	}

	// totalCostTerm will resolve to the Integer value 5
	private Proposition trueProp = new ComparableTermBasedProposition(ComparisonOperator.GREATER_THAN, totalCostTerm, Integer.valueOf(1));
	private Proposition falseProp = new ComparableTermBasedProposition(ComparisonOperator.GREATER_THAN, totalCostTerm, Integer.valueOf(1000));
	
	@Test
	public void testAllRulesAgenda() {

		Rule rule1 = new BasicRule("r1", trueProp, Collections.<Action>singletonList(new ActionMock("a1")));
		Rule rule2 = new BasicRule("r2", falseProp, Collections.<Action>singletonList(new ActionMock("a2")));
		Rule rule3 = new BasicRule("r3", trueProp, Collections.<Action>singletonList(new ActionMock("a3")));
		
		AgendaTree agendaTree = new AgendaTree(Arrays.asList(rule1, rule2, rule3), null, null, null); 
		Agenda agenda = new BasicAgenda("test", new HashMap<String, String>(), agendaTree);
		
		execute(agenda);

		assertTrue(ActionMock.actionFired("a1"));
		assertFalse(ActionMock.actionFired("a2"));
		assertTrue(ActionMock.actionFired("a3"));
	}
	
	@Test
	public void testIfTrueSubAgenda() {

		Rule rule1 = new BasicRule("r1", trueProp, Collections.<Action>singletonList(new ActionMock("a1")));
		Rule rule2 = new BasicRule("r2", falseProp, Collections.<Action>singletonList(new ActionMock("a2")));
		Rule subRule1 = new BasicRule("r1s1", trueProp, Collections.<Action>singletonList(new ActionMock("a3")));
		
		AgendaTree subAgendaTree1 = new AgendaTree(Arrays.asList(subRule1), null, null, null);
		AgendaTree agendaTree1 = new AgendaTree(Arrays.asList(rule1), subAgendaTree1, null, null); 
		Agenda agenda1 = new BasicAgenda("test", new HashMap<String, String>(), agendaTree1);
		
		execute(agenda1);

		assertTrue(ActionMock.actionFired("a1"));
		assertTrue(ActionMock.actionFired("a3"));
		
		// RESET
		ActionMock.resetActionsFired();
		
		AgendaTree subAgendaTree2 = new AgendaTree(Arrays.asList(subRule1), null, null, null);
		AgendaTree agendaTree2 = new AgendaTree(Arrays.asList(rule2), subAgendaTree2, null, null); 
		Agenda agenda2 = new BasicAgenda("test", new HashMap<String, String>(), agendaTree2);
		
		execute(agenda2);

		assertFalse(ActionMock.actionFired("a2"));
		assertFalse(ActionMock.actionFired("a3"));
	}

	@Test
	public void testIfFalseSubAgenda() {

		Rule rule1 = new BasicRule("r1", trueProp, Collections.<Action>singletonList(new ActionMock("a1")));
		Rule rule2 = new BasicRule("r2", falseProp, Collections.<Action>singletonList(new ActionMock("a2")));
		Rule subRule1 = new BasicRule("r1s1", trueProp, Collections.<Action>singletonList(new ActionMock("a3")));
		
		AgendaTree subAgendaTree1 = new AgendaTree(Arrays.asList(subRule1), null, null, null);
		AgendaTree agendaTree1 = new AgendaTree(Arrays.asList(rule1), null, subAgendaTree1, null); 
		Agenda agenda1 = new BasicAgenda("test", new HashMap<String, String>(), agendaTree1);
		
		execute(agenda1);

		assertTrue(ActionMock.actionFired("a1"));
		assertFalse(ActionMock.actionFired("a3"));
		
		// RESET
		ActionMock.resetActionsFired();
		
		AgendaTree subAgendaTree2 = new AgendaTree(Arrays.asList(subRule1), null, null, null);
		AgendaTree agendaTree2 = new AgendaTree(Arrays.asList(rule2), null, subAgendaTree2, null); 
		Agenda agenda2 = new BasicAgenda("test", new HashMap<String, String>(), agendaTree2);
		
		execute(agenda2);

		assertFalse(ActionMock.actionFired("a2"));
		assertTrue(ActionMock.actionFired("a3"));
	}
	
	@Test
	public void testAfterAgenda() {

		Rule rule1 = new BasicRule("r1", trueProp, Collections.<Action>singletonList(new ActionMock("a1")));
		Rule rule2 = new BasicRule("r2", falseProp, Collections.<Action>singletonList(new ActionMock("a2")));
		Rule subRule1 = new BasicRule("r1s1", trueProp, Collections.<Action>singletonList(new ActionMock("a3")));
		
		AgendaTree subAgendaTree1 = new AgendaTree(Arrays.asList(subRule1), null, null, null);
		AgendaTree agendaTree1 = new AgendaTree(Arrays.asList(rule1), null, null, subAgendaTree1); 
		Agenda agenda1 = new BasicAgenda("test", new HashMap<String, String>(), agendaTree1);
		
		execute(agenda1);

		assertTrue(ActionMock.actionFired("a1"));
		assertTrue(ActionMock.actionFired("a3"));
		
		// RESET
		ActionMock.resetActionsFired();
		
		AgendaTree subAgendaTree2 = new AgendaTree(Arrays.asList(subRule1), null, null, null);
		AgendaTree agendaTree2 = new AgendaTree(Arrays.asList(rule2), null, null, subAgendaTree2); 
		Agenda agenda2 = new BasicAgenda("test", new HashMap<String, String>(), agendaTree2);
		
		execute(agenda2);

		assertFalse(ActionMock.actionFired("a2"));
		assertTrue(ActionMock.actionFired("a3"));
	}	
	
	/**
	 * @param agenda
	 */
	private void execute(Agenda agenda) {
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
	
	private static final TermResolver<Integer> testResolver = new TermResolverMock<Integer>(totalCostTerm.getSpecification(), 10); 
}
