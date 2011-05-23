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


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.krms.api.repository.action.ActionAttribute;
import org.kuali.rice.krms.api.repository.action.ActionDefinition;

public final class ActionBoServiceImpl implements ActionBoService {

    private BusinessObjectService businessObjectService;

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.impl.repository.ActionBoService#createAction(org.kuali.rice.krms.api.repository.action.ActionDefinition)
	 */
	@Override
	public ActionDefinition createAction(ActionDefinition action) {
		if (action == null){
	        throw new IllegalArgumentException("action is null");
		}
		final String actionNameKey = action.getName();
		final String actionNamespaceKey = action.getNamespace();
		final ActionDefinition existing = getActionByNameAndNamespace(actionNameKey, actionNamespaceKey);
		if (existing != null){
            throw new IllegalStateException("the action to create already exists: " + action);			
		}	
		
		ActionBo bo = ActionBo.from(action);
		businessObjectService.save(bo);
		
		return ActionBo.to(bo);
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.impl.repository.ActionBoService#updateAction(org.kuali.rice.krms.api.repository.action.ActionDefinition)
	 */
	@Override
	public void updateAction(ActionDefinition action) {
		if (action == null){
	        throw new IllegalArgumentException("action is null");
		}
		final String actionIdKey = action.getId();
		final ActionDefinition existing = getActionByActionId(actionIdKey);
        if (existing == null) {
            throw new IllegalStateException("the action does not exist: " + action);
        }
        final ActionDefinition toUpdate;
        if (!existing.getId().equals(action.getId())){
        	final ActionDefinition.Builder builder = ActionDefinition.Builder.create(action);
        	builder.setId(existing.getId());
        	toUpdate = builder.build();
        } else {
        	toUpdate = action;
        }
        
        businessObjectService.save(ActionBo.from(toUpdate));
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.impl.repository.ActionBoService#getActionsByRuleId(java.lang.String)
	 */
	@Override
	public ActionDefinition getActionByActionId(String actionId) {
		if (StringUtils.isBlank(actionId)){
            throw new IllegalArgumentException("action ID is null or blank");			
		}
		ActionBo bo = businessObjectService.findBySinglePrimaryKey(ActionBo.class, actionId);
		return ActionBo.to(bo);
	}

	@Override
	public ActionDefinition getActionByNameAndNamespace(String name, String namespace) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("name is blank");
        }
        if (StringUtils.isBlank(namespace)) {
            throw new IllegalArgumentException("namespace is blank");
        }

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", name);
        map.put("namespace", namespace);

        ActionBo myAction = businessObjectService.findByPrimaryKey(ActionBo.class, Collections.unmodifiableMap(map));
        return ActionBo.to(myAction);
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.impl.repository.ActionBoService#getActionsByRuleId(java.lang.String)
	 */
	@Override
	public List<ActionDefinition> getActionsByRuleId(String ruleId) {
		if (StringUtils.isBlank(ruleId)){
            throw new IllegalArgumentException("ruleId is null or blank");			
		}
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("ruleId", ruleId);
		List<ActionBo> bos = (List<ActionBo>) businessObjectService.findMatchingOrderBy(ActionBo.class, map, "sequenceNumber", true);
		return convertListOfBosToImmutables(bos);
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.impl.repository.ActionBoService#getActionByRuleIdAndSequenceNumber(java.lang.String, java.lang.Integer)
	 */
	@Override
	public ActionDefinition getActionByRuleIdAndSequenceNumber(String ruleId, Integer sequenceNumber) {
		if (StringUtils.isBlank(ruleId)) {
            throw new IllegalArgumentException("ruleId is null or blank");
		}
		if (sequenceNumber == null) {
            throw new IllegalArgumentException("sequenceNumber is null");
		}
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("ruleId", ruleId);
        map.put("sequenceNumber", sequenceNumber);
		ActionBo bo = businessObjectService.findByPrimaryKey(ActionBo.class, map);
		return ActionBo.to(bo);
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.impl.repository.ActionBoService#createActionAttribute(org.kuali.rice.krms.api.repository.action.ActionAttribute)
	 */
	@Override
	public ActionAttribute createActionAttribute(ActionAttribute attribute) {
		if (attribute == null){
	        throw new IllegalArgumentException("action attribute is null");
		}
		final String attrIdKey = attribute.getId();
		final ActionAttribute existing = getActionAttributeById(attrIdKey);
		if (existing != null){
            throw new IllegalStateException("the action attribute to create already exists: " + attribute);			
		}		
		
		ActionAttributeBo bo = ActionAttributeBo.from(attribute);
		businessObjectService.save(bo);
		
		return ActionAttributeBo.to(bo);
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.impl.repository.ActionBoService#updateActionAttribute(org.kuali.rice.krms.api.repository.action.ActionAttribute)
	 */
	@Override
	public void updateActionAttribute(ActionAttribute attribute) {
		if (attribute == null){
	        throw new IllegalArgumentException("action attribute is null");
		}
		final String attrIdKey = attribute.getId();
		final ActionAttribute existing = getActionAttributeById(attrIdKey);
        if (existing == null) {
            throw new IllegalStateException("the action attribute does not exist: " + attribute);
        }
        final ActionAttribute toUpdate;
        if (!existing.getId().equals(attribute.getActionId())){
        	final ActionAttribute.Builder builder = ActionAttribute.Builder.create(attribute);
        	builder.setId(existing.getId());
        	toUpdate = builder.build();
        } else {
        	toUpdate = attribute;
        }
        
        businessObjectService.save(ActionAttributeBo.from(toUpdate));		
	}
	
	
	/**
	 * This method ...
	 * 
	 * @see org.kuali.rice.krms.impl.repository.ActionBoService#getActionsByRuleId(java.lang.String)
	 */
	public ActionAttribute getActionAttributeById(String attrId) {
		if (StringUtils.isBlank(attrId)){
            return null;			
		}
		ActionAttributeBo bo = businessObjectService.findBySinglePrimaryKey(ActionAttributeBo.class, attrId);
		return ActionAttributeBo.to(bo);
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
     * Converts a List<ActionBo> to an Unmodifiable List<Action>
     *
     * @param ActionBos a mutable List<ActionBo> to made completely immutable.
     * @return An unmodifiable List<Action>
     */
    List<ActionDefinition> convertListOfBosToImmutables(final Collection<ActionBo> actionBos) {
    	if (actionBos == null) return Collections.emptyList();
        ArrayList<ActionDefinition> actions = new ArrayList<ActionDefinition>();
        for (ActionBo bo : actionBos) {
            ActionDefinition action = ActionBo.to(bo);
            actions.add(action);
        }
        return Collections.unmodifiableList(actions);
    }


}
