package org.kuali.rice.krms.framework;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.krms.api.Action;
import org.kuali.rice.krms.api.Agenda;
import org.kuali.rice.krms.api.Asset;
import org.kuali.rice.krms.api.AssetResolver;
import org.kuali.rice.krms.api.Context;
import org.kuali.rice.krms.api.ContextProvider;
import org.kuali.rice.krms.api.EngineResults;
import org.kuali.rice.krms.api.ExecutionOptions;
import org.kuali.rice.krms.api.Proposition;
import org.kuali.rice.krms.api.Rule;
import org.kuali.rice.krms.api.SelectionCriteria;
import org.kuali.rice.krms.framework.engine.AgendaTree;
import org.kuali.rice.krms.framework.engine.BasicAgenda;
import org.kuali.rice.krms.framework.engine.BasicContext;
import org.kuali.rice.krms.framework.engine.BasicRule;
import org.kuali.rice.krms.framework.engine.CollectionOfComparablesTermBasedProposition;
import org.kuali.rice.krms.framework.engine.CollectionOperator;
import org.kuali.rice.krms.framework.engine.ComparisonOperator;
import org.kuali.rice.krms.framework.engine.ProviderBasedEngine;
import org.kuali.rice.krms.framework.engine.ResultLogger;

public class CollectionOfComparablesTermBasedPropositionTest {
	private static final ResultLogger LOG = ResultLogger.getInstance();
	
	
	@Before
	public void setUp() {
		ActionMock.resetActionsFired();
	}

	@Test
	public void allPropositionTest() {

		// True cases:
		
		// this can be read as "for the collection (100, 1000), all elements are greater than 1 
		assertRuleTrue(Arrays.asList(100f, 1000f), CollectionOperator.ALL, ComparisonOperator.GREATER_THAN, 1f);
		
		assertRuleTrue(Arrays.asList(100f), CollectionOperator.ALL, ComparisonOperator.GREATER_THAN, 1f);
		// This can bend the intuition a bit, but this behavior is correct -- see Wikipedia's entry on the empty set
		assertRuleTrue(Arrays.<Float>asList(), CollectionOperator.ALL, ComparisonOperator.GREATER_THAN, 1f);
		
		// False cases:
		assertRuleFalse(Arrays.asList(100f, 1000f), CollectionOperator.ALL, ComparisonOperator.GREATER_THAN, 10000f);
		assertRuleFalse(Arrays.asList(100f, 1000f), CollectionOperator.ALL, ComparisonOperator.GREATER_THAN, 500f);
		assertRuleFalse(Arrays.asList(1000f, 100f), CollectionOperator.ALL, ComparisonOperator.GREATER_THAN, 500f);
		assertRuleFalse(Arrays.asList(100f), CollectionOperator.ALL, ComparisonOperator.GREATER_THAN, 10000f);

	}
	
	@Test
	public void oneOrMorePropositionTest() {

		// True cases:
		
		// this can be read as "for the collection (100, 1000), one or more elements are greater than 500
		assertRuleTrue(Arrays.asList(100f, 1000f), CollectionOperator.ONE_OR_MORE, ComparisonOperator.GREATER_THAN, 500f);
		assertRuleTrue(Arrays.asList(1000f, 100f), CollectionOperator.ONE_OR_MORE, ComparisonOperator.GREATER_THAN, 500f);
		assertRuleTrue(Arrays.asList(1000f), CollectionOperator.ONE_OR_MORE, ComparisonOperator.GREATER_THAN, 500f);

		// False cases:
		assertRuleFalse(Arrays.asList(1000f, 2000f), CollectionOperator.ONE_OR_MORE, ComparisonOperator.GREATER_THAN, 5000f);
		assertRuleFalse(Arrays.asList(2000f, 1000f), CollectionOperator.ONE_OR_MORE, ComparisonOperator.GREATER_THAN, 5000f);
		assertRuleFalse(Arrays.asList(1000f), CollectionOperator.ONE_OR_MORE, ComparisonOperator.GREATER_THAN, 5000f);
		assertRuleFalse(Arrays.<Float>asList(), CollectionOperator.ONE_OR_MORE, ComparisonOperator.GREATER_THAN, 5000f);

	}
	
	@Test
	public void nonePropositionTest() {
		
		// True cases:
		
		// this can be read as "for the collection (100, 1000), no elements are greater than 5000
		assertRuleTrue(Arrays.asList(100f, 1000f), CollectionOperator.NONE, ComparisonOperator.GREATER_THAN, 5000f);
		assertRuleTrue(Arrays.asList(1000f), CollectionOperator.NONE, ComparisonOperator.GREATER_THAN, 5000f);
		assertRuleTrue(Arrays.<Float>asList(), CollectionOperator.NONE, ComparisonOperator.GREATER_THAN, 5000f);
		
		// False cases:
		assertRuleFalse(Arrays.asList(1000f, 7000f), CollectionOperator.NONE, ComparisonOperator.GREATER_THAN, 5000f);
		assertRuleFalse(Arrays.asList(7000f, 1000f), CollectionOperator.NONE, ComparisonOperator.GREATER_THAN, 5000f);
		assertRuleFalse(Arrays.asList(7000f), CollectionOperator.NONE, ComparisonOperator.GREATER_THAN, 5000f);

	}
	
	/**
	 * @param agenda
	 */
	private void execute(Agenda agenda, AssetResolver ... assetResolvers) {
		Map<String, String> contextQualifiers = new HashMap<String, String>();
		contextQualifiers.put("docTypeName", "Proposal");
		
		List<AssetResolver<?>> testResolvers = Arrays.<AssetResolver<?>>asList(assetResolvers);
		
		Context context = new BasicContext(contextQualifiers, Arrays.asList(agenda), testResolvers);
		ContextProvider contextProvider = new ManualContextProvider(context);
		
		SelectionCriteria selectionCriteria = SelectionCriteria.createCriteria("test", null, contextQualifiers, Collections.EMPTY_MAP);
		
		ProviderBasedEngine engine = new ProviderBasedEngine();
		engine.setContextProvider(contextProvider);
		
		// Set execution options to log execution
		HashMap<String, String> xOptions = new HashMap<String, String>();
		xOptions.put(ExecutionOptions.LOG_EXECUTION.toString(), Boolean.toString(true));
		
		LOG.init();
		EngineResults results = engine.execute(selectionCriteria, new HashMap<Asset, Object>(), xOptions);
	}
	
	private void assertRuleTrue(List<Float> assetValue,
			CollectionOperator collectionOper,
			ComparisonOperator comparisonOper, Float compareValue) {
		assertRule(assetValue, collectionOper, comparisonOper, compareValue, true);
	}
	
	private void assertRuleFalse(List<Float> assetValue,
			CollectionOperator collectionOper,
			ComparisonOperator comparisonOper, Float compareValue) {
		assertRule(assetValue, collectionOper, comparisonOper, compareValue, false);
	}
	
	private void assertRule(List<Float> assetValue,
			CollectionOperator collectionOper,
			ComparisonOperator comparisonOper, Float compareValue, boolean assertTrue) {
		
		boolean actionFired = executeTestAgenda(assetValue, collectionOper, comparisonOper, compareValue).actionFired();
		
		if (assertTrue) { 
			assertTrue(actionFired); 
		} else { 
			assertFalse(actionFired); 
		}
		
		ActionMock.resetActionsFired();
	}

	/**
	 * This method ...
	 * 
	 * @param assetValue
	 * @param collectionOper
	 * @param comparisonOper
	 * @param compareValue
	 * @return
	 */
	private ActionMock executeTestAgenda(List<Float> assetValue,
			CollectionOperator collectionOper,
			ComparisonOperator comparisonOper, Float compareValue) {
		Asset expensesAsset = new Asset("expenses","Collection<Float>");
		AssetResolver<Collection<Float>> expensesResolver = new AssetResolverMock<Collection<Float>>(expensesAsset, assetValue); 
		
		Proposition prop1 = new CollectionOfComparablesTermBasedProposition(collectionOper, comparisonOper, expensesAsset, compareValue);
		ActionMock prop1Action = new ActionMock("prop1Action");
		
		Rule rule = new BasicRule("ALL", prop1, Collections.<Action>singletonList(prop1Action));
		
		AgendaTree agendaTree = new AgendaTree(Arrays.asList(rule), null, null, null); 
		Agenda agenda = new BasicAgenda("test", new HashMap<String, String>(), agendaTree);
		
		execute(agenda, expensesResolver);
		return prop1Action;
	}

}
