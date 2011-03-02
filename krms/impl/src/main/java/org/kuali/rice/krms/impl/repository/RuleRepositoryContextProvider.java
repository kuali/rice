package org.kuali.rice.krms.impl.repository;

import java.util.Map;

import org.kuali.rice.krms.api.Asset;
import org.kuali.rice.krms.api.Context;
import org.kuali.rice.krms.api.ContextProvider;
import org.kuali.rice.krms.api.SelectionCriteria;
import org.kuali.rice.krms.framework.repository.ContextDefinition;
import org.kuali.rice.krms.framework.repository.RuleRepository;

public class RuleRepositoryContextProvider implements ContextProvider {

	private RuleRepository ruleRepository;
	
	@Override
	public Context loadContext(SelectionCriteria selectionCriteria, Map<Asset, Object> facts, Map<String, String> executionOptions) {
		ContextDefinition contextDefinition = ruleRepository.selectContext(selectionCriteria.getContextQualifiers());
		return loadContextFromDefinition(contextDefinition);
	}
	
	protected Context loadContextFromDefinition(ContextDefinition contextDefinition) {
		// TODO...translate repository data model to execution model
		return null;
	}
	
	public void setRuleRepository(RuleRepository ruleRepository) {
		this.ruleRepository = ruleRepository;
	}

}
