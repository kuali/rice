package org.kuali.rice.krms.framework.repository;

import java.util.Map;

public interface RuleRepository {

	public ContextDefinition selectContext(Map<String, String> contextQualifiers);
	
}
