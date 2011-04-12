package org.kuali.rice.krms.framework.engine;

import org.kuali.rice.krms.api.engine.ExecutionEnvironment;
import org.kuali.rice.krms.api.engine.ResultEvent;
import org.kuali.rice.krms.api.engine.Term;
import org.kuali.rice.krms.api.engine.TermResolutionException;
import org.kuali.rice.krms.framework.engine.result.BasicResult;

public class ComparableTermBasedProposition<T> implements Proposition {
	private static final ResultLogger LOG = ResultLogger.getInstance();

	private final ComparisonOperator operator;
	private final Term term;
	private final T expectedValue;
	
	
	public ComparableTermBasedProposition(ComparisonOperator operator, Term term, T expectedValue) {
		this.operator = operator;
		this.term = term;
		this.expectedValue = expectedValue;
	}
	
	@Override
	public boolean evaluate(ExecutionEnvironment environment) {
		Comparable<T> termValue;
		try {
			termValue = environment.resolveTerm(term);
		} catch (TermResolutionException e) {
			// TODO Something better than this
			throw new RuntimeException(e);
		}
		
		boolean result = compare(termValue);
		
		if (LOG.isEnabled(environment)){
			LOG.logResult(new BasicResult(ResultEvent.PropositionEvaluated, this, environment, result));
		}
		return result;
	}

	/**
	 * This method does the actual comparison of the term value w/ the expected value
	 * 
	 * @param termValue
	 * @return the boolean result of the comparison
	 */
	protected boolean compare(Comparable<T> termValue) {
		boolean result = Boolean.valueOf(operator.compare(termValue, getExpectedValue()));
		return result;
	}
	
	/**
	 * @return the expectedValue
	 */
	protected T getExpectedValue() {
		return this.expectedValue;
	}

	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(term.toString());
		sb.append(" "+operator.toString());
		sb.append(" "+expectedValue.toString());
		return sb.toString();
	}
}
