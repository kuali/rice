package org.kuali.rice.krms.framework.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kuali.rice.krms.api.engine.ExecutionEnvironment;
import org.kuali.rice.krms.api.repository.LogicalOperator;

public final class CompoundProposition implements Proposition {

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
