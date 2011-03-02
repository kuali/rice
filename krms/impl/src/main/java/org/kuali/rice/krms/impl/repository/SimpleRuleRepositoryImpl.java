package org.kuali.rice.krms.impl.repository;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.kuali.rice.krms.framework.repository.ContextDefinition;
import org.kuali.rice.krms.framework.repository.RuleRepository;

/**
 * This impl has some concurrency issues to consider 
 * @author gilesp
 *
 */
public class SimpleRuleRepositoryImpl implements RuleRepository {
	
	ConcurrentMap<String, Set<ContextDefinition>> contextDefinitions = new ConcurrentHashMap<String, Set<ContextDefinition>>();
	
	@Override
	public ContextDefinition selectContext(Map<String, String> contextQualifiers) {
		Set<ContextDefinition> results = null;
		if (contextQualifiers != null) for (Entry<String,String> entry : contextQualifiers.entrySet()) {
			Set<ContextDefinition> qualifierMatches = contextDefinitions.get(entry.getKey()+"="+entry.getValue());
			if (null == results) {
				results = new HashSet<ContextDefinition>(qualifierMatches);
			} else {
				results.retainAll(qualifierMatches);
			}
		}
		if (results != null) {
			if (results.size() == 1) return results.iterator().next();
			else throw new IllegalArgumentException("ambiguous qualifiers");
		}
		return null;
	}
	
	public void addContextDefinitionMapping(Map<String, String> contextQualifiers, ContextDefinition contextDefinition) {
		if (contextQualifiers != null) for (Entry<String,String> entry : contextQualifiers.entrySet()) {
			String key = entry.getKey()+"="+entry.getValue();
			// TODO: synchronize here
			contextDefinitions.putIfAbsent(key, new HashSet<ContextDefinition>());
			contextDefinitions.get(key).add(contextDefinition);
		}
	}

}
