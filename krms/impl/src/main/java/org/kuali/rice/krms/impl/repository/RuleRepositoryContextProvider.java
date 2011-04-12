package org.kuali.rice.krms.impl.repository;

import java.util.Map;

import org.kuali.rice.krms.api.engine.SelectionCriteria;
import org.kuali.rice.krms.api.engine.Term;
import org.kuali.rice.krms.api.repository.ContextDefinition;
import org.kuali.rice.krms.api.repository.RuleRepositoryService;
import org.kuali.rice.krms.framework.engine.Context;
import org.kuali.rice.krms.framework.engine.ContextProvider;

public class RuleRepositoryContextProvider implements ContextProvider {

	private RuleRepositoryService ruleRepositoryService;
	
	@Override
	public Context loadContext(SelectionCriteria selectionCriteria, Map<Term, Object> facts, Map<String, String> executionOptions) {
//		ContextDefinition contextDefinition = ruleRepositoryService.selectContext(selectionCriteria.getContextQualifiers());
//		return loadContextFromDefinition(contextDefinition);
		return null; // for now
	}
	
	protected Context loadContextFromDefinition(ContextDefinition contextDefinition) {
		// TODO...translate repository data model to execution model
		return null;
	}
	
	public void setRuleRepositoryService(RuleRepositoryService ruleRepositoryService) {
		this.ruleRepositoryService = ruleRepositoryService;
	}

}
