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

public final class ActionRepositoryServiceImpl implements ActionRepositoryService {

    private BusinessObjectService businessObjectService;

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.impl.repository.ActionRepositoryService#createAction(org.kuali.rice.krms.api.repository.action.ActionDefinition)
	 */
	@Override
	public void createAction(ActionDefinition action) {
		if (action == null){
	        throw new IllegalArgumentException("action is null");
		}
		final String actionNameKey = action.getName();
		final String actionNamespaceKey = action.getNamespace();
		final ActionDefinition existing = getActionByNameAndNamespace(actionNameKey, actionNamespaceKey);
		if (existing != null){
            throw new IllegalStateException("the action to create already exists: " + action);			
		}	
		businessObjectService.save(ActionBo.from(action));
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.impl.repository.ActionRepositoryService#updateAction(org.kuali.rice.krms.api.repository.action.ActionDefinition)
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
	 * @see org.kuali.rice.krms.impl.repository.ActionRepositoryService#getActionsByRuleId(java.lang.String)
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
	 * @see org.kuali.rice.krms.impl.repository.ActionRepositoryService#getActionsByRuleId(java.lang.String)
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
	 * @see org.kuali.rice.krms.impl.repository.ActionRepositoryService#getActionByRuleIdAndSequenceNumber(java.lang.String, java.lang.Integer)
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
	 * @see org.kuali.rice.krms.impl.repository.ActionRepositoryService#createActionAttribute(org.kuali.rice.krms.api.repository.action.ActionAttribute)
	 */
	@Override
	public void createActionAttribute(ActionAttribute attribute) {
		if (attribute == null){
	        throw new IllegalArgumentException("action attribute is null");
		}
		final String attrIdKey = attribute.getId();
		final ActionAttribute existing = getActionAttributeById(attrIdKey);
		if (existing != null){
            throw new IllegalStateException("the action attribute to create already exists: " + attribute);			
		}
		
		businessObjectService.save(ActionAttributeBo.from(attribute));		
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.impl.repository.ActionRepositoryService#updateActionAttribute(org.kuali.rice.krms.api.repository.action.ActionAttribute)
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
	 * @see org.kuali.rice.krms.impl.repository.ActionRepositoryService#getActionsByRuleId(java.lang.String)
	 */
	public ActionAttribute getActionAttributeById(String attrId) {
		if (StringUtils.isBlank(attrId)){
            return null;			
		}
		ActionAttributeBo bo = businessObjectService.findBySinglePrimaryKey(ActionAttributeBo.class, attrId);
		return ActionAttributeBo.to(bo);
	}

//	/**
//	 * Converts a mutable bo to it's immutable counterpart
//	 * @param bo the mutable business object
//	 * @return the immutable object
//	 */
//	public ActionDefinition to(ActionBo bo) {
//		if (bo == null) { return null; }
//		ActionDefinition.Builder builder = ActionDefinition.Builder.create( bo.getId(), bo.getName(), bo.getNamespace(),
//				bo.getTypeId(), bo.getRuleId(), bo.getSequenceNumber());
//		builder.setDescription(bo.getDescription());
//		
//		Set<ActionAttribute.Builder> attrBuilders = new HashSet<ActionAttribute.Builder>();
//		if (bo.getAttributes() != null){
//			for (ActionAttributeBo attrBo : bo.getAttributes() ){
//				ActionAttribute.Builder attrBuilder = ActionAttribute.Builder.create(attrBo);
//				attrBuilders.add(attrBuilder);
//			}
//		}
//		builder.setAttributes(attrBuilders);
//		return builder.build();
//	}
//
//   /**
//	* Converts a immutable object to it's mutable bo counterpart
//	* TODO: move to() and from() to impl service
//	* @param im immutable object
//	* @return the mutable bo
//	*/
//   public ActionBo from(ActionDefinition im) {
//	   if (im == null) { return null; }
//
//	   ActionBo bo = new ActionBo();
//	   bo.setId( im.getId() );
//	   bo.setNamespace( im.getNamespace() );
//	   bo.setName( im.getName() );
//	   bo.setTypeId( im.getTypeId() );
//	   bo.setDescription( im.getDescription() );
//	   bo.setRuleId( im.getRuleId() );
//	   bo.setSequenceNumber( im.getSequenceNumber() );
//	   Set<ActionAttributeBo> attributes = new HashSet<ActionAttributeBo>();
//	   for (ActionAttribute attr : im.getAttributes()){
//		   attributes.add ( ActionAttributeBo.from(attr) );
//	   }
//	   bo.setAttributes(attributes);
//	   return bo;
//   }
 

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
