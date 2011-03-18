package org.kuali.rice.krms.framework.engine;

import org.kuali.rice.krms.api.Asset;
import org.kuali.rice.krms.api.AssetResolutionException;
import org.kuali.rice.krms.api.ExecutionEnvironment;
import org.kuali.rice.krms.api.Proposition;
import org.kuali.rice.krms.api.ResultEvent;
import org.kuali.rice.krms.framework.engine.result.BasicResult;

public class ComparableTermBasedProposition<T> implements Proposition {
	private static final ResultLogger LOG = ResultLogger.getInstance();

	private ComparisonOperator operator;
	private Asset term;
	private T expectedValue;
	
	
	public ComparableTermBasedProposition(ComparisonOperator operator, Asset term, T expectedValue) {
		this.operator = operator;
		this.term = term;
		this.expectedValue = expectedValue;
	}
	
	@Override
	public boolean evaluate(ExecutionEnvironment environment) {
		Comparable<T> termValue;
		try {
			termValue = environment.resolveTerm(term);
		} catch (AssetResolutionException e) {
			// TODO Something better than this
			throw new RuntimeException(e);
		}
		// XXX: Unsafe!
		boolean result = Boolean.valueOf(operator.compare((Comparable<T>)termValue, expectedValue));
		if (LOG.isEnabled(environment)){
			LOG.logResult(new BasicResult(ResultEvent.PropositionEvaluated, this, environment, result));
		}
		return result;
	}

	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(term.toString());
		sb.append(" "+operator.toString());
		sb.append(" "+expectedValue.toString());
		return sb.toString();
	}
}
