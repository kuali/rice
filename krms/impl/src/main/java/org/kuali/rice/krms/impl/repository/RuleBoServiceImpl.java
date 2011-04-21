/*
 * Copyright 2006-2011 The Kuali Foundation
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
package org.kuali.rice.krms.impl.repository;


import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.krms.api.repository.action.ActionDefinition;
import org.kuali.rice.krms.api.repository.proposition.PropositionDefinition;
import org.kuali.rice.krms.api.repository.rule.RuleAttribute;
import org.kuali.rice.krms.api.repository.rule.RuleDefinition;

import java.util.*;

public final class RuleBoServiceImpl implements RuleBoService {

	private BusinessObjectService businessObjectService;

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.impl.repository.RuleBoService#createRule(org.kuali.rice.krms.api.repository.rule.RuleDefinition)
	 */
	@Override
	public void createRule(RuleDefinition rule) {
		if (rule == null){
			throw new IllegalArgumentException("rule is null");
		}
		final String ruleIdKey = rule.getId();
		final RuleDefinition existing = getRuleByRuleId(ruleIdKey);
		if (existing != null){
			throw new IllegalStateException("the rule to create already exists: " + rule);			
		}	
		businessObjectService.save(from(rule));
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.impl.repository.RuleBoService#updateRule(org.kuali.rice.krms.api.repository.rule.RuleDefinition)
	 */
	@Override
	public void updateRule(RuleDefinition rule) {
		if (rule == null){
			throw new IllegalArgumentException("rule is null");
		}
		final String ruleIdKey = rule.getId();
		final RuleDefinition existing = getRuleByRuleId(ruleIdKey);
		if (existing == null) {
			throw new IllegalStateException("the rule does not exist: " + rule);
		}
		final RuleDefinition toUpdate;
		if (!existing.getId().equals(rule.getId())){
			final RuleDefinition.Builder builder = RuleDefinition.Builder.create(rule);
			builder.setId(existing.getId());
			toUpdate = builder.build();
		} else {
			toUpdate = rule;
		}

		businessObjectService.save(from(toUpdate));

	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.impl.repository.RuleBoService#getRuleByRuleId(java.lang.String)
	 */
	@Override
	public RuleDefinition getRuleByRuleId(String ruleId) {
		if (StringUtils.isBlank(ruleId)){
			return null;			
		}
		RuleBo bo = businessObjectService.findBySinglePrimaryKey(RuleBo.class, ruleId);
		return to(bo);
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.impl.repository.RuleBoService#createRuleAttribute(org.kuali.rice.krms.api.repository.rule.RuleAttribute)
	 */
	@Override
	public void createRuleAttribute(RuleAttribute attribute) {
		if (attribute == null){
			throw new IllegalArgumentException("rule attribute is null");
		}
		final String attrIdKey = attribute.getId();
		final RuleAttribute existing = getRuleAttributeById(attrIdKey);
		if (existing != null){
			throw new IllegalStateException("the rule attribute to create already exists: " + attribute);			
		}

		businessObjectService.save(RuleAttributeBo.from(attribute));		
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.impl.repository.RuleBoService#updateRuleAttribute(org.kuali.rice.krms.api.repository.rule.RuleAttribute)
	 */
	@Override
	public void updateRuleAttribute(RuleAttribute attribute) {
		if (attribute == null){
			throw new IllegalArgumentException("rule attribute is null");
		}
		final String attrIdKey = attribute.getId();
		final RuleAttribute existing = getRuleAttributeById(attrIdKey);
		if (existing == null) {
			throw new IllegalStateException("the rule attribute does not exist: " + attribute);
		}
		final RuleAttribute toUpdate;
		if (!existing.getId().equals(attribute.getRuleId())){
			final RuleAttribute.Builder builder = RuleAttribute.Builder.create(attribute);
			builder.setId(existing.getId());
			toUpdate = builder.build();
		} else {
			toUpdate = attribute;
		}

		businessObjectService.save(RuleAttributeBo.from(toUpdate));
	}

	/**
	 * This method ...
	 * 
	 * @see org.kuali.rice.krms.impl.repository.RuleBoService#getRuleAttributeById(java.lang.String)
	 */
	public RuleAttribute getRuleAttributeById(String attrId) {
		if (StringUtils.isBlank(attrId)){
			return null;			
		}
		RuleAttributeBo bo = businessObjectService.findBySinglePrimaryKey(RuleAttributeBo.class, attrId);
		return RuleAttributeBo.to(bo);
	}

	/**
	 * Converts a mutable bo to it's immutable counterpart
	 * @param bo the mutable business object
	 * @return the immutable object
	 */
	public RuleDefinition to(RuleBo bo) {
		if (bo == null) { return null; }
		RuleDefinition.Builder builder = RuleDefinition.Builder.create(
				bo.getId(), bo.getName(), bo.getNamespace(),
				bo.getTypeId(), bo.getPropId());
		if (bo.getProposition() != null){
			PropositionDefinition.Builder propBuilder = PropositionDefinition.Builder.create(bo.getProposition());
			builder.setProposition(propBuilder);
		}
		List <ActionDefinition.Builder> actionList = new ArrayList<ActionDefinition.Builder>();
		if (bo.getActions() != null){
			for (ActionBo action : bo.getActions()) {
				ActionDefinition.Builder actionBuilder = ActionDefinition.Builder.create(action);
				actionList.add(actionBuilder);
			}
		}
		builder.setActions(actionList);			
		Set<RuleAttribute.Builder> attrBuilders = new HashSet <RuleAttribute.Builder>();
		if (bo.getAttributes() != null){
			for (RuleAttributeBo attrBo : bo.getAttributes() ){
				RuleAttribute.Builder attrBuilder = 
					RuleAttribute.Builder.create(attrBo);
				attrBuilders.add(attrBuilder);
			}
		}
		builder.setAttributes(attrBuilders);
		return builder.build();
	}

	/**
	 * Converts a immutable object to it's mutable bo counterpart
	 * TODO: move to() and from() to impl service
	 * @param im immutable object
	 * @return the mutable bo
	 */
	public RuleBo from(RuleDefinition im) {
		if (im == null) { return null; }

		RuleBo bo = new RuleBo();
		bo.setId( im.getId() );
		bo.setNamespace( im.getNamespace() );
		bo.setName( im.getName() );
		bo.setTypeId( im.getTypeId() );
		bo.setPropId( im.getPropId() );
		bo.setProposition(PropositionBo.from(im.getProposition()));
		List<ActionBo> actionList = new ArrayList<ActionBo>();
		for (ActionDefinition action : im.getActions()){
			actionList.add ( ActionBo.from(action) );
		}
		bo.setActions(actionList);		
		Set<RuleAttributeBo> attributes = new HashSet<RuleAttributeBo>();
		for (RuleAttribute attr : im.getAttributes()){
			attributes.add ( RuleAttributeBo.from(attr) );
		}
		bo.setAttributes(attributes);
		return bo;
	}


	/**
	 * Sets the businessObjectService attribute value.
	 *
	 * @param businessObjectService The businessObjectService to set.
	 */
	public void setBusinessObjectService(final BusinessObjectService businessObjectService) {
		this.businessObjectService = businessObjectService;
	}

	/**
	 * Converts a List<RuleBo> to an Unmodifiable List<Rule>
	 *
	 * @param RuleBos a mutable List<RuleBo> to made completely immutable.
	 * @return An unmodifiable List<Rule>
	 */
	public List<RuleDefinition> convertListOfBosToImmutables(final Collection<RuleBo> ruleBos) {
		ArrayList<RuleDefinition> rules = new ArrayList<RuleDefinition>();
		for (RuleBo bo : ruleBos) {
			RuleDefinition rule = to(bo);
			rules.add(rule);
		}
		return Collections.unmodifiableList(rules);
	}

}
