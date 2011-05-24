package org.kuali.rice.krms.framework.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kuali.rice.krms.api.engine.ExecutionEnvironment;
import org.kuali.rice.krms.api.engine.ResultEvent;
import org.kuali.rice.krms.api.repository.LogicalOperator;
import org.kuali.rice.krms.framework.engine.result.BasicResult;

public final class CompoundProposition implements Proposition {
    private static final ResultLogger LOG = ResultLogger.getInstance();
    
	private final LogicalOperator logicalOperator;
	private final List<Proposition> propositions;
	
	public CompoundProposition(LogicalOperator logicalOperator, List<Proposition> propositions) {
		if (propositions == null || propositions.isEmpty()) {
			throw new IllegalArgumentException("Propositions must be non-null and non-empty.");
		}
		if (logicalOperator == null) {
			throw new IllegalArgumentException("Logical operator must be non-null.");
		}
		this.logicalOperator = logicalOperator;
		this.propositions = new ArrayList<Proposition>(propositions);
	}
	
	@Override
	public boolean evaluate(ExecutionEnvironment environment) {
		boolean result = evaluateInner(environment);
		
		// handle compound proposition result logging
		if (LOG.isEnabled(environment)){
            LOG.logResult(new BasicResult(ResultEvent.PropositionEvaluated, this, environment, result));
        }
		
		return result;
	}

    /**
     * This method handles the evaluation logic
     * 
     * @param environment
     * @return
     */
    private boolean evaluateInner(ExecutionEnvironment environment) {
        if (logicalOperator == LogicalOperator.AND) {
			for (Proposition proposition : propositions) {
				boolean result = proposition.evaluate(environment);
				if (!result) {
					return false;
				}
			}
			return true;
		} else if (logicalOperator == LogicalOperator.OR) {
			for (Proposition proposition : propositions) {
				if (proposition.evaluate(environment)) {
					return true;
				}
			}
			return false;
		}
		throw new IllegalStateException("Invalid logical operator: " + logicalOperator);
    }
	

    @Override
    public List<Proposition> getChildren() {
        return Collections.unmodifiableList(propositions);
    }
    
    @Override
    public boolean isCompound() {
        return true;
    }

}
