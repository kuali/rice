package org.kuali.rice.krms.impl.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krms.api.repository.RuleRepositoryService;
import org.kuali.rice.krms.api.repository.agenda.AgendaTreeDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaTreeRuleEntry;
import org.kuali.rice.krms.api.repository.agenda.AgendaTreeSubAgendaEntry;
import org.kuali.rice.krms.api.repository.context.ContextDefinition;
import org.kuali.rice.krms.api.repository.context.ContextSelectionCriteria;
import org.kuali.rice.krms.api.repository.rule.RuleDefinition;

/**
 *
 */
public class RuleRepositoryServiceImpl extends RepositoryServiceBase implements RuleRepositoryService {
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.api.repository.RuleRepositoryService#selectContext(org.kuali.rice.krms.api.repository.context.ContextSelectionCriteria)
	 */
    @Override
    public ContextDefinition selectContext(
    		ContextSelectionCriteria contextSelectionCriteria) {
    	if (contextSelectionCriteria == null){
    		throw new IllegalArgumentException("selection criteria is null");
    	}
    	if (StringUtils.isBlank(contextSelectionCriteria.getNamespaceCode())){
    		throw new IllegalArgumentException("selection criteria namespace code is null or blank");
    	}
    	Map<String, String> attributesById = convertAttributeKeys(
    			contextSelectionCriteria.getContextQualifiers(),
    			contextSelectionCriteria.getNamespaceCode());
    	Map<String, String> contextQualifiers = new HashMap<String,String>();
    	
    	// TODO: use new criteria API so we can match multiple qualifiers at once.
    	
    	contextQualifiers.put("namespace", contextSelectionCriteria.getNamespaceCode());
    	contextQualifiers.put("name", contextSelectionCriteria.getName());
    	for(Entry<String,String> attributeEntry : attributesById.entrySet()) {
			contextQualifiers.put("attributes.attributeDefinitionId", attributeEntry.getKey());
			contextQualifiers.put("attributes.value", attributeEntry.getValue());
		}    	
    	
    	List<ContextBo> resultBos = (List<ContextBo>) getBusinessObjectService().findMatching(ContextBo.class, contextQualifiers);

    	//assuming 1 ?
    	ContextDefinition result = null;
    	if (resultBos != null) {
    		if (resultBos.size() == 1) {
    			ContextBo bo = resultBos.iterator().next();
    			return ContextBo.to(bo);
    		}
    		else throw new IllegalArgumentException("ambiguous qualifiers");
    	}
    	return result;
    }

	@Override
	public AgendaTreeDefinition getAgendaTree(String agendaId) {
		if (StringUtils.isBlank(agendaId)){
    		throw new IllegalArgumentException("agenda id is null or blank");
    	}
		// Get agenda items from db, then build up agenda tree structure
		AgendaBo agendaBo = getBusinessObjectService().findBySinglePrimaryKey(AgendaBo.class, agendaId);
		String agendaItemId = agendaBo.getFirstItemId();
		
		// walk thru the agenda items, building an agenda tree definition Builder along the way
		AgendaTreeDefinition.Builder myBuilder = AgendaTreeDefinition.Builder.create();
		myBuilder.setAgendaId( agendaId );
		myBuilder = walkAgendaItemTree(agendaItemId, myBuilder);
		
		// build the agenda tree and return it
		return myBuilder.build();
	}
	
	@Override
	public List<AgendaTreeDefinition> getAgendaTrees(List<String> agendaIds) {
		List<AgendaTreeDefinition> agendaTrees = new ArrayList<AgendaTreeDefinition>();
		for (String agendaId : agendaIds){
			agendaTrees.add( getAgendaTree(agendaId) );
		}
        return Collections.unmodifiableList(agendaTrees);		
	}
	
	@Override
	public RuleDefinition getRule(String ruleId) {
		if (StringUtils.isBlank(ruleId)){
			return null;			
		}
		RuleBo bo = getBusinessObjectService().findBySinglePrimaryKey(RuleBo.class, ruleId);
		return RuleBo.to(bo);
	}
	
	@Override
	public List<RuleDefinition> getRules(List<String> ruleIds) {
		Map<String,String> fieldValues = new HashMap<String,String>();
		for (String ruleId : ruleIds){
			fieldValues.put("id", ruleId);
		}
		Collection<RuleBo> bos = getBusinessObjectService().findMatching(RuleBo.class, fieldValues);
		ArrayList<RuleDefinition> rules = new ArrayList<RuleDefinition>();
        for (RuleBo bo : bos) {
            RuleDefinition rule = RuleBo.to(bo);
            rules.add(rule);
        }
        return Collections.unmodifiableList(rules);
	}

	/**
	 * Recursive method to create AgendaTreeDefinition builder
	 * 	
	 *  
	 */
	private AgendaTreeDefinition.Builder walkAgendaItemTree(String agendaItemId, AgendaTreeDefinition.Builder builder){
		//TODO: prevent circular, endless looping
		if (StringUtils.isBlank(agendaItemId)){
			return null;
		}
		// Get AgendaItem Business Object from database
		// NOTE: if we read agendaItem one at a time from db.   Could consider linking in OJB and getting all at once
		AgendaItemBo agendaItemBo = getBusinessObjectService().findBySinglePrimaryKey(AgendaItemBo.class, agendaItemId);
		
		// If Rule  
		// TODO: validate that only either rule or subagenda, not both
		if (!StringUtils.isBlank( agendaItemBo.getRuleId() )){
			// setup new rule entry builder
			AgendaTreeRuleEntry.Builder ruleEntryBuilder = AgendaTreeRuleEntry.Builder
					.create(agendaItemBo.getId(), agendaItemBo.getRuleId());
			ruleEntryBuilder.setRuleId( agendaItemBo.getRuleId() );
			ruleEntryBuilder.setAgendaItemId( agendaItemBo.getId() );
			if (agendaItemBo.getWhenTrueId() != null){
				// Go follow the true branch, creating AgendaTreeDefinintion Builder for the
				// true branch level
				AgendaTreeDefinition.Builder myBuilder = AgendaTreeDefinition.Builder.create();
				myBuilder.setAgendaId( agendaItemBo.getAgendaId() );
				ruleEntryBuilder.setIfTrue( walkAgendaItemTree(agendaItemBo.getWhenTrueId(),myBuilder));
			}
			if (agendaItemBo.getWhenFalseId() != null){
				// Go follow the false branch, creating AgendaTreeDefinintion Builder 
				AgendaTreeDefinition.Builder myBuilder = AgendaTreeDefinition.Builder.create();
				myBuilder.setAgendaId( agendaItemBo.getAgendaId() );
				ruleEntryBuilder.setIfFalse( walkAgendaItemTree(agendaItemBo.getWhenFalseId(), myBuilder));
			}
			// Build the Rule Entry and add it to the AgendaTreeDefinition builder
			builder.addRuleEntry( ruleEntryBuilder.build() );
		}
		// if SubAgenda and a sub agenda tree entry
		if (!StringUtils.isBlank(agendaItemBo.getSubAgendaId())) {
			AgendaTreeSubAgendaEntry.Builder subAgendaEntryBuilder = 
				AgendaTreeSubAgendaEntry.Builder.create(agendaItemBo.getId(), agendaItemBo.getSubAgendaId());
			builder.addSubAgendaEntry( subAgendaEntryBuilder.build() );
			}

		// if this agenda item has an "After Id", follow that branch
		if (!StringUtils.isBlank( agendaItemBo.getAlwaysId() )){
			builder = walkAgendaItemTree( agendaItemBo.getAlwaysId(), builder);
			
		}
		return builder;
	}
}
