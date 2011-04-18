/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krms.impl.provider.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.rice.krms.api.engine.TermResolver;
import org.kuali.rice.krms.api.repository.ActionDefinition;
import org.kuali.rice.krms.api.repository.AgendaDefinition;
import org.kuali.rice.krms.api.repository.AgendaTreeDefinition;
import org.kuali.rice.krms.api.repository.AgendaTreeEntryDefinition;
import org.kuali.rice.krms.api.repository.AgendaTreeRuleEntry;
import org.kuali.rice.krms.api.repository.AgendaTreeSubAgendaEntry;
import org.kuali.rice.krms.api.repository.ContextDefinition;
import org.kuali.rice.krms.api.repository.PropositionDefinition;
import org.kuali.rice.krms.api.repository.RepositoryDataException;
import org.kuali.rice.krms.api.repository.RuleDefinition;
import org.kuali.rice.krms.api.repository.RuleRepositoryService;
import org.kuali.rice.krms.api.repository.TermResolverDefinition;
import org.kuali.rice.krms.api.type.KrmsTypeDefinition;
import org.kuali.rice.krms.api.type.KrmsTypeRepositoryService;
import org.kuali.rice.krms.framework.engine.Action;
import org.kuali.rice.krms.framework.engine.Agenda;
import org.kuali.rice.krms.framework.engine.AgendaTree;
import org.kuali.rice.krms.framework.engine.AgendaTreeEntry;
import org.kuali.rice.krms.framework.engine.BasicAgenda;
import org.kuali.rice.krms.framework.engine.BasicAgendaTree;
import org.kuali.rice.krms.framework.engine.BasicAgendaTreeEntry;
import org.kuali.rice.krms.framework.engine.BasicContext;
import org.kuali.rice.krms.framework.engine.BasicRule;
import org.kuali.rice.krms.framework.engine.Context;
import org.kuali.rice.krms.framework.engine.Proposition;
import org.kuali.rice.krms.framework.engine.Rule;
import org.kuali.rice.krms.framework.engine.SubAgenda;
import org.kuali.rice.krms.framework.type.TermResolverTypeService;
import org.kuali.rice.krms.impl.repository.TermBoService;
import org.kuali.rice.krms.impl.type.KrmsTypeResolver;
import org.springframework.util.CollectionUtils;

/**
 * TODO... 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class RepositoryToEngineTranslatorImpl implements RepositoryToEngineTranslator {

	private RuleRepositoryService ruleRepositoryService;
	private KrmsTypeRepositoryService typeRepositoryService;
	private KrmsTypeResolver typeResolver;
	private TermBoService termBoService;
	
	@Override
	public Context translateContextDefinition(ContextDefinition contextDefinition) {
		if (contextDefinition == null) {
			return null;
		}
		List<Agenda> agendas = new ArrayList<Agenda>();
		for (AgendaDefinition agendaDefinition : contextDefinition.getAgendas()) {
			Agenda agenda = translateAgendaDefinition(agendaDefinition);
			agendas.add(agenda);
		}
		
		List<TermResolverDefinition> termResolverDefs = 
			termBoService.getTermResolversByContextId(contextDefinition.getContextDefinitionId());
		
		List<TermResolver<?>> termResolvers = new ArrayList<TermResolver<?>>();

		if (!CollectionUtils.isEmpty(termResolverDefs)) for (TermResolverDefinition termResolverDef : termResolverDefs) {
			if (termResolverDef != null) {
				TermResolver<?> termResolver = translateTermResolver(termResolverDef);
				if (termResolver != null) termResolvers.add(termResolver);
			}
		}
		
		return new BasicContext(agendas, termResolvers); 
	}

	/**
	 * This method translates a {@link TermResolverDefinition} into a {@link TermResolver}
	 * 
	 * @param termResolverDef
	 * @return
	 */
	private TermResolver<?> translateTermResolver(TermResolverDefinition termResolverDef) {
		if (termResolverDef == null) throw new IllegalArgumentException("termResolverDef must not be null");
		KrmsTypeDefinition krmsTypeDef = typeRepositoryService.getTypeById(termResolverDef.getTypeId());
		if (krmsTypeDef == null) throw new IllegalStateException(TermResolverDefinition.class.getSimpleName() + 
				" has an invalid KRMS type: typeId=" + termResolverDef.getTypeId());
		TermResolverTypeService termResolverTypeService = 
			typeResolver.getTermResolverTypeService(termResolverDef, krmsTypeDef);
		
		TermResolver<?> termResolver = termResolverTypeService.loadTermResolver(termResolverDef);
		return termResolver;
	}
	
	@Override
	public Agenda translateAgendaDefinition(AgendaDefinition agendaDefinition) {
		// TODO: Handle event name.  Is it one of attributes?
		return new BasicAgenda("", agendaDefinition.getAttributes(), new LazyAgendaTree(agendaDefinition, this));
	}
		
	@Override
	public AgendaTree translateAgendaDefinitionToAgendaTree(AgendaDefinition agendaDefinition) {
		AgendaTreeDefinition agendaTreeDefinition = ruleRepositoryService.getAgendaTree(agendaDefinition.getAgendaId());
		return translateAgendaTreeDefinition(agendaTreeDefinition);
	}
	
	@Override
	public AgendaTree translateAgendaTreeDefinition(AgendaTreeDefinition agendaTreeDefinition) {
	
		List<String> ruleIds = new ArrayList<String>();
		List<String> subAgendaIds = new ArrayList<String>();
		for (AgendaTreeEntryDefinition entryDefinition : agendaTreeDefinition.getEntries()) {
			if (entryDefinition instanceof AgendaTreeRuleEntry) {
				ruleIds.add(((AgendaTreeRuleEntry)entryDefinition).getRuleId());
			} else if (entryDefinition instanceof AgendaTreeSubAgendaEntry) {
				subAgendaIds.add(((AgendaTreeSubAgendaEntry)entryDefinition).getSubAgendaId());
			} else {
				throw new IllegalStateException("Encountered invalid agenda tree entry definition class, did not understand type: " + entryDefinition);
			}
		}
		
		Map<String, Rule> rules = loadRules(ruleIds);
		Map<String, SubAgenda> subAgendas = loadSubAgendas(subAgendaIds);
		
		List<AgendaTreeEntry> entries = new ArrayList<AgendaTreeEntry>();
	
		for (AgendaTreeEntryDefinition entryDefinition : agendaTreeDefinition.getEntries()) {
			if (entryDefinition instanceof AgendaTreeRuleEntry) {
				AgendaTreeRuleEntry ruleEntry = (AgendaTreeRuleEntry)entryDefinition;
				AgendaTree ifTrue = null;
				AgendaTree ifFalse = null;
				if (ruleEntry.getIfTrue() != null) {
					ifTrue = translateAgendaTreeDefinition(ruleEntry.getIfTrue());
				}
				if (ruleEntry.getIfTrue() != null) {
					ifFalse = translateAgendaTreeDefinition(ruleEntry.getIfFalse());
				}
				Rule rule = rules.get(ruleEntry.getRuleId());
				if (rule == null) {
					throw new IllegalStateException("Failed to locate rule with id: " + ruleEntry.getRuleId());
				}
				BasicAgendaTreeEntry agendaTreeEntry = new BasicAgendaTreeEntry(rule, ifTrue, ifFalse);
				entries.add(agendaTreeEntry);
			} else if (entryDefinition instanceof AgendaTreeSubAgendaEntry) {
				AgendaTreeSubAgendaEntry subAgendaEntry = (AgendaTreeSubAgendaEntry)entryDefinition;
				SubAgenda subAgenda = subAgendas.get(subAgendaEntry.getSubAgendaId());
				if (subAgenda == null) {
					throw new IllegalStateException("Failed to locate sub agenda with id: " + subAgendaEntry.getSubAgendaId());
				}
				BasicAgendaTreeEntry agendaTreeEntry = new BasicAgendaTreeEntry(subAgenda, null, null);
				entries.add(agendaTreeEntry);
			} else {
				throw new IllegalStateException("Encountered invalid agenda tree entry class, did not understand type: " + entryDefinition);
			}
		}
		return new BasicAgendaTree(entries);
	}
	
	protected Map<String, Rule> loadRules(List<String> ruleIds) {
		List<RuleDefinition> ruleDefinitions = ruleRepositoryService.getRules(ruleIds);
		validateRuleDefinitions(ruleIds, ruleDefinitions);
		Map<String, Rule> rules = new HashMap<String, Rule>();
		for (RuleDefinition ruleDefinition : ruleDefinitions) {
			rules.put(ruleDefinition.getRuleId(), translateRuleDefinition(ruleDefinition));
		}
		return rules;
	}
	
	/**
	 * Ensures that there is a rule definition for every rule id in the original list.
	 */
	private void validateRuleDefinitions(List<String> ruleIds, List<RuleDefinition> ruleDefinitions) {
		if (ruleIds.size() != ruleDefinitions.size()) {
			Map<String, RuleDefinition> indexedRuleDefinitions = indexRuleDefinitions(ruleDefinitions);
			for (String ruleId : ruleIds) {
				if (!indexedRuleDefinitions.containsKey(ruleId)) {
					throw new RepositoryDataException("Failed to locate a rule with id '" + ruleId + "' in the repository.");
				}
			}
		}
	}
	
	private Map<String, RuleDefinition> indexRuleDefinitions(List<RuleDefinition> ruleDefinitions) {
		Map<String, RuleDefinition> ruleDefinitionMap = new HashMap<String, RuleDefinition>();
		for (RuleDefinition ruleDefinition : ruleDefinitions) {
			ruleDefinitionMap.put(ruleDefinition.getRuleId(), ruleDefinition);
		}
		return ruleDefinitionMap;
	}
	
	protected Map<String, SubAgenda> loadSubAgendas(List<String> subAgendaIds) {
		List<AgendaTreeDefinition> subAgendaDefinitions = ruleRepositoryService.getAgendaTrees(subAgendaIds);
		validateSubAgendaDefinitions(subAgendaIds, subAgendaDefinitions);
		Map<String, SubAgenda> subAgendas = new HashMap<String, SubAgenda>();
		for (AgendaTreeDefinition subAgendaDefinition : subAgendaDefinitions) {
			subAgendas.put(subAgendaDefinition.getAgendaId(), translateAgendaTreeDefinitionToSubAgenda(subAgendaDefinition));
		}
		return subAgendas;
	}
	
	/**
	 * Ensures that there is a rule definition for every rule id in the original list.
	 */
	private void validateSubAgendaDefinitions(List<String> subAgendaIds, List<AgendaTreeDefinition> subAgendaDefinitions) {
		if (subAgendaIds.size() != subAgendaDefinitions.size()) {
			Map<String, AgendaTreeDefinition> indexedSubAgendaDefinitions = indexSubAgendaDefinitions(subAgendaDefinitions);
			for (String subAgendaId : subAgendaIds) {
				if (!indexedSubAgendaDefinitions.containsKey(subAgendaId)) {
					throw new RepositoryDataException("Failed to locate an agenda with id '" + subAgendaId + "' in the repository.");
				}
			}
		}
	}
	
	private Map<String, AgendaTreeDefinition> indexSubAgendaDefinitions(List<AgendaTreeDefinition> subAgendaDefinitions) {
		Map<String, AgendaTreeDefinition> subAgendaDefinitionMap = new HashMap<String, AgendaTreeDefinition>();
		for (AgendaTreeDefinition subAgendaDefinition : subAgendaDefinitions) {
			subAgendaDefinitionMap.put(subAgendaDefinition.getAgendaId(), subAgendaDefinition);
		}
		return subAgendaDefinitionMap;
	}
	
	@Override
	public Rule translateRuleDefinition(RuleDefinition ruleDefinition) {
		Proposition condition = translatePropositionDefinition(ruleDefinition.getProposition());
		List<Action> actions = new ArrayList<Action>();
		if (ruleDefinition.getActions() != null) {
			for (ActionDefinition actionDefinition : ruleDefinition.getActions()) {
				actions.add(translateActionDefinition(actionDefinition));
			}
		}
		return new BasicRule(condition, actions);
	}
	
	@Override
	public Proposition translatePropositionDefinition(PropositionDefinition propositionDefinition) {
		KrmsTypeDefinition typeDefinition = null;
		if (propositionDefinition.getTypeId() != null) {
			typeDefinition = typeRepositoryService.getTypeById(propositionDefinition.getTypeId());
			if (typeDefinition == null) {
				throw new RepositoryDataException("Failed to locate a type definition for proposition typeId: " + propositionDefinition.getTypeId());
			}
		}
		return new LazyProposition(propositionDefinition, typeDefinition, typeResolver);
	}
	
	@Override
	public Action translateActionDefinition(ActionDefinition actionDefinition) {
		if (actionDefinition.getTypeId() == null) {
			throw new RepositoryDataException("Given ActionDefinition does not have a typeId, actionId was: " + actionDefinition.getActionId());
		}
		KrmsTypeDefinition typeDefinition = typeRepositoryService.getTypeById(actionDefinition.getTypeId());
		if (typeDefinition == null) {
			throw new RepositoryDataException("Failed to locate a type definition for agenda typeId: " + actionDefinition.getTypeId());
		}
		return new LazyAction(actionDefinition, typeDefinition, typeResolver);
	}
	
	@Override
	public SubAgenda translateAgendaTreeDefinitionToSubAgenda(AgendaTreeDefinition subAgendaDefinition) {
		return new SubAgenda(translateAgendaTreeDefinition(subAgendaDefinition));
	}
	
	/**
	 * @param ruleRepositoryService the ruleRepositoryService to set
	 */
	public void setRuleRepositoryService(
			RuleRepositoryService ruleRepositoryService) {
		this.ruleRepositoryService = ruleRepositoryService;
	}
	
	/**
	 * @param termBoService the termBoService to set
	 */
	public void setTermBoService(TermBoService termBoService) {
		this.termBoService = termBoService;
	}
	
	/**
	 * @param typeRepositoryService the typeRepositoryService to set
	 */
	public void setTypeRepositoryService(
			KrmsTypeRepositoryService typeRepositoryService) {
		this.typeRepositoryService = typeRepositoryService;
	}
	
	/**
	 * @param typeResolver the typeResolver to set
	 */
	public void setTypeResolver(KrmsTypeResolver typeResolver) {
		this.typeResolver = typeResolver;
	}

}
