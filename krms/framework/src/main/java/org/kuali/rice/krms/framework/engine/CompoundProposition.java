/**
 * Copyright 2005-2012 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krms.framework.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.kuali.rice.krms.api.engine.ExecutionEnvironment;
import org.kuali.rice.krms.api.engine.ResultEvent;
import org.kuali.rice.krms.api.engine.ExecutionFlag;
import org.kuali.rice.krms.api.repository.LogicalOperator;
import org.kuali.rice.krms.framework.engine.result.BasicResult;

/**
 *
 * An implementation of {@link Proposition} which holds other Propositions and a {@link LogicalOperator}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class CompoundProposition implements Proposition {
	
    private static final ResultLogger LOG = ResultLogger.getInstance();
    
	private final LogicalOperator logicalOperator;
	private final List<Proposition> propositions;

    /**
     * Create a CompoundProposition with the given values
     * @param logicalOperator
     * @param propositions
     */
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
	public PropositionResult evaluate(ExecutionEnvironment environment) {
		
		PropositionResult result = evaluateInner(environment);
		
		// handle compound proposition result logging
		if (LOG.isEnabled(environment)) { 
            LOG.logResult(new BasicResult(ResultEvent.PropositionEvaluated, this, environment, result.getResult()));
        }
		
		return result;
	}

    /**
     * This method handles the evaluation logic
     * 
     * @param environment
     * @return PropositionResult
     * @throws IllegalStateException if the logicalOperator is invalid.
     */
	
    private PropositionResult evaluateInner(ExecutionEnvironment environment) {
    	
    	boolean collatedResult;
    	boolean evaluateAll = environment.getExecutionOptions().getFlag(ExecutionFlag.EVALUATE_ALL_PROPOSITIONS);
    	
        if (logicalOperator == LogicalOperator.AND) {

            collatedResult = true;

			for (Proposition proposition : propositions) {
				
				PropositionResult singleResult = proposition.evaluate(environment);
				logPropositionResult(proposition, singleResult, environment);
								
				if (!singleResult.getResult()) {
					collatedResult = false;
					if(!evaluateAll) break;
				}
			}
			
			return new PropositionResult(collatedResult);
			
		} else if (logicalOperator == LogicalOperator.OR) {
			
		    collatedResult = false;
			
			for (Proposition proposition : propositions) {
				
			    PropositionResult singleResult = proposition.evaluate(environment);
				
				logPropositionResult(proposition, singleResult, environment);
				
				if (!singleResult.getResult()) {
					collatedResult = true;
					if(!evaluateAll) break;
				}
			}
			
			return new PropositionResult(collatedResult);
		}
		throw new IllegalStateException("Invalid logical operator: " + logicalOperator);
    }
    
    
    /*
     * only log if the proposition is not compound
     * and have the compound proposition log its own result
     */
    
    public void logPropositionResult(Proposition proposition, PropositionResult propositionResult, ExecutionEnvironment environment) {
    	    	
    	if(!proposition.isCompound()) {
            LOG.logResult(new BasicResult(propositionResult.getExecutionDetails(), ResultEvent.PropositionEvaluated, proposition, environment, propositionResult.getResult()));		
    	}
    	
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
