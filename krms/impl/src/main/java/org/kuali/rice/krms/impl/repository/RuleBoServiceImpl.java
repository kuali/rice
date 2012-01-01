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
package org.kuali.rice.krms.impl.repository;


import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krms.api.repository.rule.RuleDefinition;
import org.kuali.rice.krms.impl.util.KrmsImplConstants.PropertyNames;

import java.util.*;

public final class RuleBoServiceImpl implements RuleBoService {

	private BusinessObjectService businessObjectService;

	/**
	 * This overridden creates a KRMS Rule in the repository
	 * 
	 * @see org.kuali.rice.krms.impl.repository.RuleBoService#createRule(org.kuali.rice.krms.api.repository.rule.RuleDefinition)
	 */
	@Override
	public RuleDefinition createRule(RuleDefinition rule) {
		if (rule == null){
			throw new IllegalArgumentException("rule is null");
		}
		final String nameKey = rule.getName();
		final String namespaceKey = rule.getNamespace();
		final RuleDefinition existing = getRuleByNameAndNamespace(nameKey, namespaceKey);
		if (existing != null){
			throw new IllegalStateException("the rule to create already exists: " + rule);			
		}	
		RuleBo ruleBo = RuleBo.from(rule);
		businessObjectService.save(ruleBo);
		return RuleBo.to(ruleBo);
	}

	/**
	 * This overridden updates an existing Rule in the Repository
	 * 
	 * @see org.kuali.rice.krms.impl.repository.RuleBoService#updateRule(org.kuali.rice.krms.api.repository.rule.RuleDefinition)
	 */
	@Override
	public void updateRule(RuleDefinition rule) {
		if (rule == null){
			throw new IllegalArgumentException("rule is null");
		}

		// must already exist to be able to update
		final String ruleIdKey = rule.getId();
		final RuleBo existing = businessObjectService.findBySinglePrimaryKey(RuleBo.class, ruleIdKey);
		if (existing == null) {
			throw new IllegalStateException("the rule does not exist: " + rule);
		}
		final RuleDefinition toUpdate;
		if (!existing.getId().equals(rule.getId())){
			// if passed in id does not match existing id, correct it
			final RuleDefinition.Builder builder = RuleDefinition.Builder.create(rule);
			builder.setId(existing.getId());
			toUpdate = builder.build();
		} else {
			toUpdate = rule;
		}
	     
		// copy all updateable fields to bo
		RuleBo boToUpdate = RuleBo.from(toUpdate);

		// delete any old, existing attributes
		Map<String,String> fields = new HashMap<String,String>(1);
		fields.put(PropertyNames.Rule.RULE_ID, toUpdate.getId());
		businessObjectService.deleteMatching(RuleAttributeBo.class, fields);
        
		// update the rule and create new attributes
		businessObjectService.save(boToUpdate);
	}

	/**
	 * This method retrieves a rule from the repository given the rule id.
	 * 
	 * @see org.kuali.rice.krms.impl.repository.RuleBoService#getRuleByRuleId(java.lang.String)
	 */
	@Override
	public RuleDefinition getRuleByRuleId(String ruleId) {
		if (StringUtils.isBlank(ruleId)){
			throw new IllegalArgumentException("rule id is null");
		}
		RuleBo bo = businessObjectService.findBySinglePrimaryKey(RuleBo.class, ruleId);
		return RuleBo.to(bo);
	}

	/**
	 * This method retrieves a rule from the repository given the name of the rule
	 * and namespace.
	 * 
	 * @see org.kuali.rice.krms.impl.repository.RuleBoService#getRuleByRuleId(java.lang.String)
	 */
	@Override
	public RuleDefinition getRuleByNameAndNamespace(String name, String namespace) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("name is blank");
        }
        if (StringUtils.isBlank(namespace)) {
            throw new IllegalArgumentException("namespace is blank");
        }

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", name);
        map.put("namespace", namespace);

        RuleBo myRule = businessObjectService.findByPrimaryKey(RuleBo.class, Collections.unmodifiableMap(map));
		return RuleBo.to(myRule);
	}

//	/**
//	 * This overridden method ...
//	 * 
//	 * @see org.kuali.rice.krms.impl.repository.RuleBoService#createRuleAttribute(org.kuali.rice.krms.api.repository.rule.RuleAttribute)
//	 */
//	@Override
//	public void createRuleAttribute(RuleAttribute attribute) {
//		if (attribute == null){
//			throw new IllegalArgumentException("rule attribute is null");
//		}
//		final String attrIdKey = attribute.getId();
//		final RuleAttribute existing = getRuleAttributeById(attrIdKey);
//		if (existing != null){
//			throw new IllegalStateException("the rule attribute to create already exists: " + attribute);			
//		}
//
//		businessObjectService.save(RuleAttributeBo.from(attribute));		
//	}
//
//	/**
//	 * This overridden method ...
//	 * 
//	 * @see org.kuali.rice.krms.impl.repository.RuleBoService#updateRuleAttribute(org.kuali.rice.krms.api.repository.rule.RuleAttribute)
//	 */
//	@Override
//	public void updateRuleAttribute(RuleAttribute attribute) {
//		if (attribute == null){
//			throw new IllegalArgumentException("rule attribute is null");
//		}
//		final String attrIdKey = attribute.getId();
//		final RuleAttribute existing = getRuleAttributeById(attrIdKey);
//		if (existing == null) {
//			throw new IllegalStateException("the rule attribute does not exist: " + attribute);
//		}
//		final RuleAttribute toUpdate;
//		if (!existing.getId().equals(attribute.getRuleId())){
//			final RuleAttribute.Builder builder = RuleAttribute.Builder.create(attribute);
//			builder.setId(existing.getId());
//			toUpdate = builder.build();
//		} else {
//			toUpdate = attribute;
//		}
//
//		businessObjectService.save(RuleAttributeBo.from(toUpdate));
//	}
//
	/**
	 * This method ...
	 * 
	 * @see org.kuali.rice.krms.impl.repository.RuleBoService#getRuleAttributeById(java.lang.String)
	 */
	public RuleAttributeBo getRuleAttributeById(String attrId) {
		if (StringUtils.isBlank(attrId)){
			return null;			
		}
		RuleAttributeBo bo = businessObjectService.findBySinglePrimaryKey(RuleAttributeBo.class, attrId);
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
			RuleDefinition rule = RuleBo.to(bo);
			rules.add(rule);
		}
		return Collections.unmodifiableList(rules);
	}

}
