/**
 * Copyright 2005-2018 The Kuali Foundation
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
import org.kuali.rice.core.api.exception.RiceIllegalStateException;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.PersistenceOption;
import org.kuali.rice.krms.api.repository.action.ActionDefinition;
import org.kuali.rice.krms.api.repository.rule.RuleDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import static org.kuali.rice.krms.impl.repository.BusinessObjectServiceMigrationUtils.findSingleMatching;

public class RuleBoServiceImpl implements RuleBoService {

	private DataObjectService dataObjectService;
    private KrmsAttributeDefinitionService attributeDefinitionService;

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
        ruleBo = dataObjectService.save(ruleBo, PersistenceOption.FLUSH);

        return RuleBo.to(ruleBo);
    }

    /**
     * This overridden updates an existing Rule in the Repository
     *
     * @see org.kuali.rice.krms.impl.repository.RuleBoService#updateRule(org.kuali.rice.krms.api.repository.rule.RuleDefinition)
     */
    @Override
    public RuleDefinition updateRule(RuleDefinition rule) {
        if (rule == null){
            throw new IllegalArgumentException("rule is null");
        }

        // must already exist to be able to update
        final String ruleIdKey = rule.getId();
        final RuleBo existing = dataObjectService.find(RuleBo.class, ruleIdKey);

        if (existing == null) {
            throw new IllegalStateException("the rule does not exist: " + rule);
        }

        final RuleDefinition toUpdate;
        String existingPropositionId = null;

        if (existing.getProposition() != null){
            existingPropositionId = existing.getProposition().getId();
        }

        if (!existing.getId().equals(rule.getId())){
            // if passed in id does not match existing id, correct it
            final RuleDefinition.Builder builder = RuleDefinition.Builder.create(rule);
            builder.setId(existing.getId());
            toUpdate = builder.build();
        } else {
            toUpdate = rule;
        }

        RuleBo boToUpdate = RuleBo.from(toUpdate);
        reconcileActionAttributes(boToUpdate.getActions(), existing.getActions());

        // update the rule and create new attributes
        RuleBo updatedData = dataObjectService.save(boToUpdate, PersistenceOption.FLUSH);

        //delete the orphan proposition
        if (updatedData.getProposition() != null && StringUtils.isNotBlank(existingPropositionId)){
           if (!(updatedData.getProposition().getId().equals(existingPropositionId))) {
              dataObjectService.delete(existing.getProposition());
           }
        }

        return RuleBo.to(updatedData);
    }

    /**
     * Transfer any ActionAttributeBos that still apply from the existing actions, while updating their values.
     *
     * <p>This method is side effecting, it replaces elements in the passed in toUpdateActionBos collection. </p>
     *
     * @param toUpdateActionBos the new ActionBos which will (later) be persisted
     * @param existingActionBos the ActionBos which have been fetched from the database
     */
    private void reconcileActionAttributes(List<ActionBo> toUpdateActionBos, List<ActionBo> existingActionBos) {
        for (ActionBo toUpdateAction : toUpdateActionBos) {

            ActionBo matchingExistingAction = findMatchingExistingAction(toUpdateAction, existingActionBos);

            if (matchingExistingAction == null) { continue; }

            ListIterator<ActionAttributeBo> toUpdateAttributesIter = toUpdateAction.getAttributeBos().listIterator();

            while (toUpdateAttributesIter.hasNext()) {
                ActionAttributeBo toUpdateAttribute = toUpdateAttributesIter.next();

                ActionAttributeBo matchingExistingAttribute =
                        findMatchingExistingAttribute(toUpdateAttribute, matchingExistingAction.getAttributeBos());

                if (matchingExistingAttribute == null) { continue; }

                // set the new value into the existing attribute, then replace the new attribute with the existing one
                matchingExistingAttribute.setValue(toUpdateAttribute.getValue());
                toUpdateAttributesIter.set(matchingExistingAttribute);
            }
        }
    }

    /**
     * Returns the action in existingActionBos that has the same ID as existingAction, or null if none matches.
     *
     * @param toUpdateAction
     * @param existingActionBos
     * @return the matching action, or null if none match.
     */
    private ActionBo findMatchingExistingAction(ActionBo toUpdateAction, List<ActionBo> existingActionBos) {
        for (ActionBo existingAction : existingActionBos) {
            if (existingAction.getId().equals(toUpdateAction.getId())) {
                return existingAction;
            }
        }

        return null;
    }

    /**
     * Returns the attribute in existingAttributeBos that has the same attributeDefinitionId as toUpdateAttribute, or
     * null if none matches.
     *
     * @param toUpdateAttribute
     * @param existingAttributeBos
     * @return the matching attribute, or null if none match.
     */
    private ActionAttributeBo findMatchingExistingAttribute(ActionAttributeBo toUpdateAttribute,
            List<ActionAttributeBo> existingAttributeBos) {
        for (ActionAttributeBo existingAttribute : existingAttributeBos) {
            if (existingAttribute.getAttributeDefinitionId().equals(toUpdateAttribute.getAttributeDefinitionId())) {
                return existingAttribute;
            }
        }

        return null;
    }

    @Override
    public void deleteRule(String ruleId) {
        if (ruleId == null) {
            throw new IllegalArgumentException("ruleId is null");
        }

        final RuleDefinition existing = getRuleByRuleId(ruleId);

        if (existing == null) {
            throw new IllegalStateException("the Rule to delete does not exists: " + ruleId);
        }

        dataObjectService.delete(from(existing));
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

        RuleBo bo = dataObjectService.find(RuleBo.class, ruleId);

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
            throw new IllegalArgumentException("name is null or blank");
        }
        if (StringUtils.isBlank(namespace)) {
            throw new IllegalArgumentException("namespace is null or blank");
        }

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", name);
        map.put("namespace", namespace);

        RuleBo myRule = findSingleMatching(dataObjectService, RuleBo.class, Collections.unmodifiableMap(map));

        return RuleBo.to(myRule);
    }

    /**
     * Gets a rule attribute by its ID
     *
     * @param attrId the rule attribute's ID
     * @return the rule attribute
     */
    public RuleAttributeBo getRuleAttributeById(String attrId) {
        if (StringUtils.isBlank(attrId)){
            return null;
        }

        RuleAttributeBo bo = dataObjectService.find(RuleAttributeBo.class, attrId);

        return bo;
    }

    /**
     * Converts a immutable {@link RuleDefinition} to its mutable {@link RuleBo} counterpart.
     * @param rule the immutable object.
     * @return a {@link RuleBo} the mutable RuleBo.
     *
     */
    public RuleBo from(RuleDefinition rule) {
        if (rule == null) { return null; }

        RuleBo ruleBo = new RuleBo();
        ruleBo.setName(rule.getName());
        ruleBo.setDescription(rule.getDescription());
        ruleBo.setNamespace(rule.getNamespace());
        ruleBo.setTypeId(rule.getTypeId());
        ruleBo.setProposition(PropositionBo.from(rule.getProposition()));
        ruleBo.setId(rule.getId());
        ruleBo.setActive(rule.isActive());
        ruleBo.setVersionNumber(rule.getVersionNumber());
        ruleBo.setActions(buildActionBoList(rule));
        ruleBo.setAttributeBos(buildAttributeBoList(rule));

        return ruleBo;
    }

    private Set<RuleAttributeBo> buildAttributeBo(RuleDefinition im) {
        Set<RuleAttributeBo> attributes = new HashSet<RuleAttributeBo>();

        // build a map from attribute name to definition
        Map<String, KrmsAttributeDefinition> attributeDefinitionMap = new HashMap<String, KrmsAttributeDefinition>();

        List<KrmsAttributeDefinition> attributeDefinitions = getAttributeDefinitionService().findAttributeDefinitionsByType(im.getTypeId());

        for (KrmsAttributeDefinition attributeDefinition : attributeDefinitions) {
            attributeDefinitionMap.put(attributeDefinition.getName(), attributeDefinition);
        }

        // for each entry, build a RuleAttributeBo and add it to the set
        if (im.getAttributes() != null) {
            for (Map.Entry<String,String> entry  : im.getAttributes().entrySet()) {
                KrmsAttributeDefinition attrDef = attributeDefinitionMap.get(entry.getKey());

                if (attrDef != null) {
                    RuleAttributeBo attributeBo = new RuleAttributeBo();
                    attributeBo.setRule( RuleBo.from(im) );
                    attributeBo.setValue(entry.getValue());
                    attributeBo.setAttributeDefinition(KrmsAttributeDefinitionBo.from(attrDef));
                    attributes.add( attributeBo );
                } else {
                    throw new RiceIllegalStateException("there is no attribute definition with the name '" +
                            entry.getKey() + "' that is valid for the rule type with id = '" + im.getTypeId() +"'");
                }
            }
        }

        return attributes;
    }

    private List<RuleAttributeBo> buildAttributeBoList(RuleDefinition im) {
        List<RuleAttributeBo> attributes = new LinkedList<RuleAttributeBo>();

        // build a map from attribute name to definition
        Map<String, KrmsAttributeDefinition> attributeDefinitionMap = new HashMap<String, KrmsAttributeDefinition>();

        List<KrmsAttributeDefinition> attributeDefinitions = getAttributeDefinitionService().findAttributeDefinitionsByType(im.getTypeId());

        for (KrmsAttributeDefinition attributeDefinition : attributeDefinitions) {
            attributeDefinitionMap.put(attributeDefinition.getName(), attributeDefinition);
        }

        // for each entry, build a RuleAttributeBo and add it to the set
        if (im.getAttributes() != null) {
            for (Map.Entry<String,String> entry  : im.getAttributes().entrySet()) {
                KrmsAttributeDefinition attrDef = attributeDefinitionMap.get(entry.getKey());

                if (attrDef != null) {
                    RuleAttributeBo attributeBo = new RuleAttributeBo();
                    attributeBo.setRule(RuleBo.from(im));
                    attributeBo.setValue(entry.getValue());
                    attributeBo.setAttributeDefinition(KrmsAttributeDefinitionBo.from(attrDef));
                    attributes.add( attributeBo );
                } else {
                    throw new RiceIllegalStateException("there is no attribute definition with the name '" +
                            entry.getKey() + "' that is valid for the rule type with id = '" + im.getTypeId() +"'");
                }
            }
        }
        return attributes;
    }

    private List<ActionBo> buildActionBoList(RuleDefinition im) {
        List<ActionBo> actions = new LinkedList<ActionBo>();

        for (ActionDefinition actionDefinition : im.getActions()) {
            actions.add(ActionBo.from(actionDefinition));
        }

        return actions;
    }

    /**
     * Sets the dataObjectService attribute value.
     *
     * @param dataObjectService The dataObjectService to set.
     */
    public void setDataObjectService(final DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

    /**
     * Converts a List<RuleBo> to an Unmodifiable List<Rule>
     *
     * @param ruleBos a mutable List<RuleBo> to made completely immutable.
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


    protected KrmsAttributeDefinitionService getAttributeDefinitionService() {
        if (attributeDefinitionService == null) {
            attributeDefinitionService = KrmsRepositoryServiceLocator.getKrmsAttributeDefinitionService();
        }

        return attributeDefinitionService;
    }
}
