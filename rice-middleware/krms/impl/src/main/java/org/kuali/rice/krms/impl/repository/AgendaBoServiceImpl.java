/**
 * Copyright 2005-2017 The Kuali Foundation
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
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.exception.RiceIllegalStateException;
import org.kuali.rice.core.api.mo.ModelObjectUtils;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.PersistenceOption;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaItemDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinition;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static org.kuali.rice.core.api.criteria.PredicateFactory.in;
import static org.kuali.rice.krms.impl.repository.BusinessObjectServiceMigrationUtils.*;

/**
 * Implementation of the interface for accessing KRMS repository Agenda related
 * business objects.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AgendaBoServiceImpl implements AgendaBoService {

    // TODO: deal with active flag

    // used for converting lists of BOs to model objects
    private static final ModelObjectUtils.Transformer<AgendaItemBo, AgendaItemDefinition> toAgendaItemDefinition =
            new ModelObjectUtils.Transformer<AgendaItemBo, AgendaItemDefinition>() {
                public AgendaItemDefinition transform(AgendaItemBo input) {
                    return AgendaItemBo.to(input);
                };
            };
    // used for converting lists of BOs to model objects
    private static final ModelObjectUtils.Transformer<AgendaBo, AgendaDefinition> toAgendaDefinition =
            new ModelObjectUtils.Transformer<AgendaBo, AgendaDefinition>() {
                public AgendaDefinition transform(AgendaBo input) {
                    return AgendaBo.to(input);
                };
            };

    private DataObjectService dataObjectService;
    private KrmsAttributeDefinitionService attributeDefinitionService;

    /**
     * This overridden method creates a KRMS Agenda in the repository
     */
    @Override
    public AgendaDefinition createAgenda(AgendaDefinition agenda) {
        if (agenda == null) {
            throw new RiceIllegalArgumentException("agenda is null");
        }
        final String nameKey = agenda.getName();
        final String contextId = agenda.getContextId();
        final AgendaDefinition existing = getAgendaByNameAndContextId(nameKey, contextId);
        if (existing != null) {
            throw new IllegalStateException("the agenda to create already exists: " + agenda);
        }

        AgendaBo agendaBo = from(agenda);
        agendaBo = getDataObjectService().save(agendaBo, PersistenceOption.FLUSH);
        return to(agendaBo);
    }

    /**
     * This overridden method updates an existing Agenda in the repository
     */
    @Override
    public AgendaDefinition updateAgenda(AgendaDefinition agendaDefinition) {
        if (agendaDefinition == null) {
            throw new RiceIllegalArgumentException("agenda is null");
        }

        // must already exist to be able to update
        final String agendaDefinitionId = agendaDefinition.getId();

        // All AgendaItemDefinitions for the specified Agenda ID will end up in the "items" property.
        final AgendaBo agendaBoExisting = getDataObjectService().find(AgendaBo.class, agendaDefinitionId);
        if (agendaBoExisting == null) {
            throw new IllegalStateException("the agenda does not exist: " + agendaDefinition);
        }

        final AgendaDefinition agendaDefinitionToUpdate;
        if (agendaBoExisting.getId().equals(agendaDefinition.getId())) {
            agendaDefinitionToUpdate = agendaDefinition;
        } else {
            // if passed in id does not match existing id, correct it
            final AgendaDefinition.Builder builder = AgendaDefinition.Builder.create(agendaDefinition);
            builder.setId(agendaBoExisting.getId());
            agendaDefinitionToUpdate = builder.build();
        }

        // copy all updateable fields to bo
        AgendaBo agendaBoToUpdate = from(agendaDefinitionToUpdate);

        // move over AgendaBo members that don't get populated from AgendaDefinition
        agendaBoToUpdate.setItems(agendaBoExisting.getItems());
        if (StringUtils.isNotBlank(agendaDefinition.getFirstItemId())) {
            agendaBoToUpdate.setFirstItem(getDataObjectService().find(AgendaItemBo.class, agendaDefinition.getFirstItemId()));
        }

        // delete any old, existing attributes
        Map<String, String> fields = new HashMap<String, String>(1);
        fields.put("agenda.id", agendaDefinitionToUpdate.getId());
        // deletes the record(s) in krms_agenda_attr_t specified by agenda_id col
        deleteMatching(getDataObjectService(), AgendaAttributeBo.class, fields);

        // Will get used to determine which "previous" AgendaItems can get deleted
        ArrayList<String> agendaItemsIds = new ArrayList<String>();

        // Will get used for a quick AgendaItem ID to AgendaItemBo lookup
        HashMap<String, AgendaItemBo> mapIdToBo = new HashMap<String, AgendaItemBo>();

        // Get all AgendaItems which have the specified Agenda ID.  Make a "referenced" list as well.
        ArrayList<String> agendaItemIdsReferenced = new ArrayList<String>();
        List<AgendaItemBo> items = agendaBoToUpdate.getItems();
        for (AgendaItemBo agendaItemBo : items) {
            agendaItemsIds.add(agendaItemBo.getId());
            mapIdToBo.put(agendaItemBo.getId(), agendaItemBo);

            if (agendaItemBo.getAlwaysId() != null) {
                agendaItemIdsReferenced.add(agendaItemBo.getAlwaysId());
            }

            if (agendaItemBo.getWhenTrueId() != null) {
                agendaItemIdsReferenced.add(agendaItemBo.getWhenTrueId());
            }

            if (agendaItemBo.getWhenFalseId() != null) {
                agendaItemIdsReferenced.add(agendaItemBo.getWhenFalseId());
            }
        }

        // update new agenda and create new attributes
        AgendaBo agendaBoUpdated = getDataObjectService().save(agendaBoToUpdate, PersistenceOption.FLUSH);

        // Walk the "always", "whenTrue", and "whenFalse" lists.
        ArrayList<String> agendaItemIdsActuallyUsed = new ArrayList<String>();
        if (agendaBoUpdated.getFirstItem() != null) {
            AgendaItemBo agendaItemBoTop = agendaBoUpdated.getFirstItem();

            AgendaItemBo agendaItemBoCurrent = agendaItemBoTop;
            agendaItemIdsActuallyUsed.add(agendaItemBoCurrent.getId());

            // always list
            while (agendaItemBoCurrent != null) {
                if (StringUtils.isNotEmpty(agendaItemBoCurrent.getAlwaysId())) {
                    agendaItemIdsActuallyUsed.add(agendaItemBoCurrent.getAlwaysId());
                }

                agendaItemBoCurrent = agendaItemBoCurrent.getAlways();
            }

            // whenTrue list
            agendaItemBoCurrent = agendaItemBoTop;
            while (agendaItemBoCurrent != null) {
                if (StringUtils.isNotEmpty(agendaItemBoCurrent.getWhenTrueId())) {
                    agendaItemIdsActuallyUsed.add(agendaItemBoCurrent.getWhenTrueId());
                }

                agendaItemBoCurrent = agendaItemBoCurrent.getWhenTrue();
            }

            // whenFalse list
            agendaItemBoCurrent = agendaItemBoTop;
            while (agendaItemBoCurrent != null) {
                if (StringUtils.isNotEmpty(agendaItemBoCurrent.getWhenFalseId())) {
                    agendaItemIdsActuallyUsed.add(agendaItemBoCurrent.getWhenFalseId());
                }

                agendaItemBoCurrent = agendaItemBoCurrent.getWhenFalse();
            }
        }

        // Compare what is used by the updated Agenda to all AgendaItem IDs for this Agenda ID
        for (String sIdActuallyUsed : agendaItemIdsActuallyUsed) {
            if (agendaItemsIds.contains(sIdActuallyUsed)) {
                agendaItemsIds.remove(sIdActuallyUsed);
            }
        }

        // Anything remaining is an orphan. Only delete an AgendaItem which is not referenced,
        // and that will cascade
        for (String sAiboId : agendaItemsIds) {
            boolean bReferenced = agendaItemIdsReferenced.contains(sAiboId);
            if (bReferenced == false) {
                AgendaItemBo aibo = mapIdToBo.get(sAiboId);
                getDataObjectService().delete(aibo);
            }
        }

        return to(agendaBoUpdated);
    }

    @Override
    public void deleteAgenda(String agendaId) {
        if (agendaId == null) {
            throw new RiceIllegalArgumentException("agendaId is null");
        }

        final AgendaBo bo = getDataObjectService().find(AgendaBo.class, agendaId);

        if (bo == null) {
            throw new IllegalStateException("the Agenda to delete does not exist: " + agendaId);
        }

        // delete orphan agenda items, if needed
        AgendaItemBo firstAgendaItem = bo.getFirstItem();

        if (firstAgendaItem != null) {
            getDataObjectService().delete(firstAgendaItem);
            getDataObjectService().flush(AgendaItemBo.class);

            bo.setFirstItem(null);
            bo.setItems(null);
        }

        getDataObjectService().delete(bo);
    }

    /**
     * This overridden method retrieves an Agenda from the repository
     */
    @Override
    public AgendaDefinition getAgendaByAgendaId(String agendaId) {
        if (StringUtils.isBlank(agendaId)) {
            throw new RiceIllegalArgumentException("agenda id is null or blank");
        }
        AgendaBo bo = getDataObjectService().find(AgendaBo.class, agendaId);
        return to(bo);
    }

    /**
     * This overridden method retrieves an agenda from the repository
     */
    @Override
    public AgendaDefinition getAgendaByNameAndContextId(String name, String contextId) {
        if (StringUtils.isBlank(name)) {
            throw new RiceIllegalArgumentException("name is blank");
        }
        if (StringUtils.isBlank(contextId)) {
            throw new RiceIllegalArgumentException("contextId is blank");
        }

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", name);
        map.put("contextId", contextId);

        AgendaBo myAgenda = findSingleMatching(getDataObjectService(), AgendaBo.class, map);
        return to(myAgenda);
    }

    /**
     * This overridden method retrieves a set of agendas from the repository
     */
    @Override
    public List<AgendaDefinition> getAgendasByContextId(String contextId) {
        if (StringUtils.isBlank(contextId)) {
            throw new RiceIllegalArgumentException("context ID is null or blank");
        }
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("contextId", contextId);
        List<AgendaBo> bos = findMatching(getDataObjectService(), AgendaBo.class, map);

        return convertAgendaBosToImmutables(bos);
    }

    /**
     * This overridden method creates a new Agenda in the repository
     */
    @Override
    public AgendaItemDefinition createAgendaItem(AgendaItemDefinition agendaItem) {
        if (agendaItem == null) {
            throw new RiceIllegalArgumentException("agendaItem is null");
        }
        if (agendaItem.getId() != null) {
            final AgendaDefinition existing = getAgendaByAgendaId(agendaItem.getId());
            if (existing != null) {
                throw new IllegalStateException("the agendaItem to create already exists: " + agendaItem);
            }
        }

        AgendaItemBo bo = AgendaItemBo.from(agendaItem);
        if (StringUtils.isNotBlank(agendaItem.getRuleId()) && agendaItem.getRule() == null ) {
            bo.setRule(getDataObjectService().find(RuleBo.class, agendaItem.getRuleId()));
        }

        if (StringUtils.isNotBlank(agendaItem.getAlwaysId()) && agendaItem.getAlways() == null ) {
            bo.setAlways(getDataObjectService().find(AgendaItemBo.class, agendaItem.getAlwaysId()));
        }

        if (StringUtils.isNotBlank(agendaItem.getWhenTrueId()) && agendaItem.getWhenTrue() == null ) {
            bo.setWhenTrue(getDataObjectService().find(AgendaItemBo.class, agendaItem.getWhenTrueId()));
        }

        if (StringUtils.isNotBlank(agendaItem.getWhenFalseId()) && agendaItem.getWhenFalse() == null ) {
            bo.setWhenFalse(getDataObjectService().find(AgendaItemBo.class, agendaItem.getWhenFalseId()));
        }

        bo = getDataObjectService().save(bo, PersistenceOption.FLUSH);
        return AgendaItemBo.to(bo);
    }

    /**
     * This overridden method updates an existing Agenda in the repository
     */
    @Override
    public AgendaItemDefinition updateAgendaItem(AgendaItemDefinition agendaItem) {
        if (agendaItem == null) {
            throw new RiceIllegalArgumentException("agendaItem is null");
        }
        final String agendaItemIdKey = agendaItem.getId();
        final AgendaItemDefinition existing = getAgendaItemById(agendaItemIdKey);
        if (existing == null) {
            throw new IllegalStateException("the agenda item does not exist: " + agendaItem);
        }
        final AgendaItemDefinition toUpdate;
        if (existing.getId().equals(agendaItem.getId())) {
            toUpdate = agendaItem;
        } else {
            final AgendaItemDefinition.Builder builder = AgendaItemDefinition.Builder.create(agendaItem);
            builder.setId(existing.getId());
            toUpdate = builder.build();
        }

        AgendaItemBo aiBo = AgendaItemBo.from(toUpdate);
        final AgendaItemBo updatedData = getDataObjectService().save(aiBo, PersistenceOption.FLUSH);
        return AgendaItemBo.to(updatedData);
    }

    /**
     * This overridden method adds a new AgendaItemDefinition to the repository
     */
    @Override
    public void addAgendaItem(AgendaItemDefinition agendaItem, String parentId, Boolean position) {
        if (agendaItem == null) {
            throw new RiceIllegalArgumentException("agendaItem is null");
        }
        AgendaItemDefinition parent = null;
        if (parentId != null) {
            parent = getAgendaItemById(parentId);
            if (parent == null) {
                throw new IllegalStateException(
                        "parent agendaItem does not exist in repository. parentId = " + parentId);
            }
        }
        // create new AgendaItemDefinition
        final AgendaItemDefinition toCreate;
        if (agendaItem.getId() == null) {
            final AgendaItemDefinition.Builder builder = AgendaItemDefinition.Builder.create(agendaItem);
            builder.setId(AgendaItemBo.agendaItemIdIncrementer.getNewId());
            toCreate = builder.build();
        } else {
            toCreate = agendaItem;
        }
        createAgendaItem(toCreate);

        // link it to it's parent (for whenTrue/whenFalse, sibling for always
        if (parentId != null) {
            final AgendaItemDefinition.Builder builder = AgendaItemDefinition.Builder.create(parent);
            if (position == null) {
                builder.setAlwaysId(toCreate.getId());
            } else if (position.booleanValue()) {
                builder.setWhenTrueId(toCreate.getId());
            } else if (!position.booleanValue()) {
                builder.setWhenFalseId(toCreate.getId());
            }
            final AgendaItemDefinition parentToUpdate = builder.build();
            updateAgendaItem(parentToUpdate);
        }
    }

    /**
     * This overridden method retrieves an AgendaItemDefinition from the repository
     */
    @Override
    public AgendaItemDefinition getAgendaItemById(String id) {
        if (StringUtils.isBlank(id)) {
            throw new RiceIllegalArgumentException("agenda item id is null or blank");
        }

        AgendaItemBo bo = getDataObjectService().find(AgendaItemBo.class, id);

        return AgendaItemBo.to(bo);
    }

    @Override
    public List<AgendaItemDefinition> getAgendaItemsByAgendaId(String agendaId) {
        if (StringUtils.isBlank(agendaId)) {
            throw new RiceIllegalArgumentException("agenda id is null or null");
        }
        List<AgendaItemDefinition> results = null;

        Collection<AgendaItemBo> bos = findMatching(getDataObjectService(), AgendaItemBo.class, Collections.singletonMap(
                "agendaId", agendaId));

        if (CollectionUtils.isEmpty(bos)) {
            results = Collections.emptyList();
        } else {
            results = Collections.unmodifiableList(ModelObjectUtils.transform(bos, toAgendaItemDefinition));
        }

        return results;
    }

    @Override
    public List<AgendaDefinition> getAgendasByType(String typeId) throws RiceIllegalArgumentException {
        if (StringUtils.isBlank(typeId)) {
            throw new RiceIllegalArgumentException("type ID is null or blank");
        }

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("typeId", typeId);
        List<AgendaBo> bos = findMatching(getDataObjectService(), AgendaBo.class, map);

        return convertAgendaBosToImmutables(bos);
    }

    @Override
    public List<AgendaDefinition> getAgendasByTypeAndContext(String typeId, String contextId)
            throws RiceIllegalArgumentException {
        if (StringUtils.isBlank(typeId)) {
            throw new RiceIllegalArgumentException("type ID is null or blank");
        }
        if (StringUtils.isBlank(contextId)) {
            throw new RiceIllegalArgumentException("context ID is null or blank");
        }
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("typeId", typeId);
        map.put("contextId", contextId);
        Collection<AgendaBo> bos = findMatching(getDataObjectService(), AgendaBo.class, map);

        return convertAgendaBosToImmutables(bos);
    }

    @Override
    public List<AgendaItemDefinition> getAgendaItemsByType(String typeId) throws RiceIllegalArgumentException {
        return findAgendaItemsForAgendas(getAgendasByType(typeId));
    }

    @Override
    public List<AgendaItemDefinition> getAgendaItemsByContext(String contextId) throws RiceIllegalArgumentException {
        return findAgendaItemsForAgendas(getAgendasByContextId(contextId));
    }

    @Override
    public List<AgendaItemDefinition> getAgendaItemsByTypeAndContext(String typeId, String contextId)
            throws RiceIllegalArgumentException {
        return findAgendaItemsForAgendas(getAgendasByTypeAndContext(typeId, contextId));
    }

    @Override
    public void deleteAgendaItem(String agendaItemId) throws RiceIllegalArgumentException {
        if (StringUtils.isBlank(agendaItemId)) {
            throw new RiceIllegalArgumentException("agendaItemId must not be blank or null");
        }

        deleteMatching(getDataObjectService(), AgendaItemBo.class, Collections.singletonMap("id", agendaItemId));
    }

    private List<AgendaItemDefinition> findAgendaItemsForAgendas(List<AgendaDefinition> agendaDefinitions) {
        List<AgendaItemDefinition> results = null;

        if (!CollectionUtils.isEmpty(agendaDefinitions)) {
            List<AgendaItemBo> boResults = new ArrayList<AgendaItemBo>(agendaDefinitions.size());

            List<String> agendaIds = new ArrayList<String>(20);
            for (AgendaDefinition agendaDefinition : agendaDefinitions) {
                agendaIds.add(agendaDefinition.getId());

                if (agendaIds.size() == 20) {
                    // fetch batch

                    Predicate predicate = in("agendaId", agendaIds.toArray());
                    QueryByCriteria criteria = QueryByCriteria.Builder.fromPredicates(predicate);
                    QueryResults<AgendaItemBo> batch = getDataObjectService().findMatching(AgendaItemBo.class,
                            criteria);

                    boResults.addAll(batch.getResults());

                    // reset agendaIds
                    agendaIds.clear();
                }
            }

            if (agendaIds.size() > 0) {
                Predicate predicate = in("agendaId", agendaIds.toArray());
                QueryByCriteria criteria = QueryByCriteria.Builder.fromPredicates(predicate);
                QueryResults<AgendaItemBo> batch = getDataObjectService().findMatching(AgendaItemBo.class, criteria);

                boResults.addAll(batch.getResults());
            }

            results = Collections.unmodifiableList(ModelObjectUtils.transform(boResults, toAgendaItemDefinition));
        } else {
            results = Collections.emptyList();
        }

        return results;
    }

    /**
     * Converts a Set<AgendaBo> to an Unmodifiable Set<Agenda>
     *
     * @param agendaBos a mutable Set<AgendaBo> to made completely immutable.
     * @return An unmodifiable Set<Agenda>
     */
    public List<AgendaDefinition> convertAgendaBosToImmutables(final Collection<AgendaBo> agendaBos) {
        if (CollectionUtils.isEmpty(agendaBos)) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(ModelObjectUtils.transform(agendaBos, toAgendaDefinition));
    }

    /**
     * Converts a mutable bo to it's immutable counterpart
     *
     * @param bo the mutable business object
     * @return the immutable object
     */
    @Override
    public AgendaDefinition to(AgendaBo bo) {
        if (bo == null) {
            return null;
        }
        return org.kuali.rice.krms.api.repository.agenda.AgendaDefinition.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to it's mutable bo counterpart
     *
     * @param im immutable object
     * @return the mutable bo
     */
    @Override
    public AgendaBo from(AgendaDefinition im) {
        if (im == null) {
            return null;
        }

        AgendaBo bo = new AgendaBo();
        bo.setId(im.getId());
        bo.setName(im.getName());
        bo.setTypeId(im.getTypeId());
        bo.setContextId(im.getContextId());
        bo.setFirstItemId(im.getFirstItemId());
        bo.setVersionNumber(im.getVersionNumber());
        bo.setActive(im.isActive());
        Set<AgendaAttributeBo> attributes = buildAgendaAttributeBo(im, bo);

        bo.setAttributeBos(attributes);

        return bo;
    }

    private Set<AgendaAttributeBo> buildAgendaAttributeBo(AgendaDefinition im, AgendaBo agendaBo) {
        Set<AgendaAttributeBo> attributes = new HashSet<AgendaAttributeBo>();

        // build a map from attribute name to definition
        Map<String, KrmsAttributeDefinition> attributeDefinitionMap = new HashMap<String, KrmsAttributeDefinition>();

        List<KrmsAttributeDefinition> attributeDefinitions =
                getAttributeDefinitionService().findAttributeDefinitionsByType(im.getTypeId());

        for (KrmsAttributeDefinition attributeDefinition : attributeDefinitions) {
            attributeDefinitionMap.put(attributeDefinition.getName(), attributeDefinition);
        }

        // for each entry, build an AgendaAttributeBo and add it to the set
        for (Entry<String, String> entry : im.getAttributes().entrySet()) {
            KrmsAttributeDefinition attrDef = attributeDefinitionMap.get(entry.getKey());

            if (attrDef != null) {
                AgendaAttributeBo attributeBo = new AgendaAttributeBo();
                attributeBo.setAgenda(agendaBo);
                attributeBo.setValue(entry.getValue());
                attributeBo.setAttributeDefinition(KrmsAttributeDefinitionBo.from(attrDef));
                attributes.add(attributeBo);
            } else {
                throw new RiceIllegalStateException("there is no attribute definition with the name '" +
                        entry.getKey() + "' that is valid for the agenda type with id = '" + im.getTypeId() + "'");
            }
        }
        return attributes;
    }

    /**
     * Gets the {@link DataObjectService}.
     *
     * @return the {@link DataObjectService}
     */
    public DataObjectService getDataObjectService() {
        if (dataObjectService == null) {
            dataObjectService = KRADServiceLocator.getDataObjectService();
        }

        return dataObjectService;
    }

    /**
     * Sets the {@link DataObjectService}.
     *
     * @param dataObjectService the {@link DataObjectService} to set
     */
    public void setDataObjectService(final DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

    /**
     * Gets the {@link KrmsAttributeDefinitionService}.
     *
     * @return the {@link KrmsAttributeDefinitionService}
     */
    public KrmsAttributeDefinitionService getAttributeDefinitionService() {
        if (attributeDefinitionService == null) {
            attributeDefinitionService = KrmsRepositoryServiceLocator.getKrmsAttributeDefinitionService();
        }

        return attributeDefinitionService;
    }

    /**
     * Sets the {@link KrmsAttributeDefinitionService}.
     *
     * @param attributeDefinitionService the {@link KrmsAttributeDefinitionService} to set
     */
    public void setAttributeDefinitionService(KrmsAttributeDefinitionService attributeDefinitionService) {
        this.attributeDefinitionService = attributeDefinitionService;
    }

}