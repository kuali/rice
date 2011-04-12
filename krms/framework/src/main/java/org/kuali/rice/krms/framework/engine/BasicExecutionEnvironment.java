package org.kuali.rice.krms.framework.engine;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.kuali.rice.krms.engine.EngineResults;
import org.kuali.rice.krms.engine.ExecutionEnvironment;
import org.kuali.rice.krms.engine.SelectionCriteria;
import org.kuali.rice.krms.engine.Term;
import org.kuali.rice.krms.engine.TermResolutionEngine;
import org.kuali.rice.krms.engine.TermResolutionException;
import org.kuali.rice.krms.engine.TermResolver;

public final class BasicExecutionEnvironment implements ExecutionEnvironment {

	private final SelectionCriteria selectionCriteria;
	private final Map<Term, Object> facts;
	private final Map<String, String> executionOptions;
	private final EngineResults engineResults;
	private final TermResolutionEngine termResolutionEngine;
	
	public BasicExecutionEnvironment(SelectionCriteria selectionCriteria, Map<Term, Object> facts, Map<String, String> executionOptions) {
		if (selectionCriteria == null) {
			throw new IllegalArgumentException("Selection criteria must not be null.");
		}
		if (facts == null) {
			throw new IllegalArgumentException("Facts must not be null.");
		}
		this.selectionCriteria = selectionCriteria;
		this.facts = new HashMap<Term, Object>(facts.size());
		this.facts.putAll(facts);
		this.executionOptions = new HashMap<String, String>(executionOptions.size());
		this.executionOptions.putAll(executionOptions);
		this.engineResults = new EngineResultsImpl();
		// TODO: inject this (will have to make it non-final)
		this.termResolutionEngine = new TermResolutionEngineImpl();
	}
	
	@Override
	public SelectionCriteria getSelectionCriteria() {
		return this.selectionCriteria;
	}
	
	@Override
	public Map<Term, Object> getFacts() {
		return Collections.unmodifiableMap(facts);
	}
	
	@Override
	public void addTermResolver(TermResolver<?> termResolver) {
		termResolutionEngine.addTermResolver(termResolver);
	}
	
	@Override
	public <T> T resolveTerm(Term term) throws TermResolutionException {
		T value;
		
		// This looks funny, but works around a javac bug: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6302954
		// Specifically, using <T> below works around it.
		value = termResolutionEngine.<T>resolveTerm(term);
		
		publishFact(term, value);
		return value;
	}

	@Override
	public boolean publishFact(Term factName, Object factValue) {
		if (facts.containsKey(factName) && ObjectUtils.equals(facts.get(factName), factValue)) {
			return false;
		}
		facts.put(factName, factValue);
		termResolutionEngine.addTermValue(factName, factValue);
		return true;
	}

	@Override
	public Map<String, String> getExecutionOptions() {
		return executionOptions;
	}
	
	@Override
	public EngineResults getEngineResults() {
		return engineResults;
	}

}
