package org.kuali.rice.krms.impl.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;

import org.kuali.rice.krms.api.repository.AgendaTreeDefinition;
import org.kuali.rice.krms.api.repository.ContextDefinition;
import org.kuali.rice.krms.api.repository.ContextSelectionCriteria;
import org.kuali.rice.krms.api.repository.RuleDefinition;
import org.kuali.rice.krms.api.repository.RuleRepositoryService;

/**
 * This impl has some concurrency issues to consider 
 * @author gilesp
 *
 */
public class RuleRepositoryServiceImpl implements RuleRepositoryService {
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.api.repository.RuleRepositoryService#selectContext(org.kuali.rice.krms.api.repository.ContextSelectionCriteria)
	 */
	@Override
	public ContextDefinition selectContext(
			ContextSelectionCriteria contextSelectionCriteria) {
		// TODO
		throw new UnsupportedOperationException("TODO - implement me!!!");
	}

	@Override
	public AgendaTreeDefinition getAgendaTree(String agendaId) {
		// TODO
		throw new UnsupportedOperationException("TODO - implement me!!!");
	}
	
	@Override
	public List<AgendaTreeDefinition> getAgendaTrees(List<String> agendaIds) {
		// TODO
		throw new UnsupportedOperationException("TODO - implement me!!!");
	}
	
	@Override
	public RuleDefinition getRule(String ruleId) {
		// TODO
		throw new UnsupportedOperationException("TODO - implement me!!!");
	}
	
	@Override
	public List<RuleDefinition> getRules(List<String> ruleIds) {
		// TODO
		throw new UnsupportedOperationException("TODO - implement me!!!");
	}

}
