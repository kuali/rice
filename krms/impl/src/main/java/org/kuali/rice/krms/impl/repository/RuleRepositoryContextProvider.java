package org.kuali.rice.krms.impl.repository;

import java.util.Map;

import org.kuali.rice.krms.api.Asset;
import org.kuali.rice.krms.api.Context;
import org.kuali.rice.krms.api.SelectionCriteria;
import org.kuali.rice.krms.api.repository.ContextDefinition;
import org.kuali.rice.krms.api.repository.RuleRepositoryService;
import org.kuali.rice.krms.framework.engine.ContextProvider;

public class RuleRepositoryContextProvider implements ContextProvider {

	private RuleRepositoryService ruleRepositoryService;
	
	@Override
	public Context loadContext(SelectionCriteria selectionCriteria, Map<Asset, Object> facts, Map<String, String> executionOptions) {
		ContextDefinition contextDefinition = ruleRepositoryService.selectContext(selectionCriteria.getContextQualifiers());
		return loadContextFromDefinition(contextDefinition);
	}
	
	protected Context loadContextFromDefinition(ContextDefinition contextDefinition) {
		// TODO...translate repository data model to execution model
		return null;
	}
	
	public void setRuleRepositoryService(RuleRepositoryService ruleRepositoryService) {
		this.ruleRepositoryService = ruleRepositoryService;
	}

}
