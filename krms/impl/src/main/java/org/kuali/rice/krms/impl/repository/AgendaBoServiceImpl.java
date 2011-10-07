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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceIllegalStateException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaItem;
import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinition;
import org.kuali.rice.krms.impl.util.KrmsImplConstants.PropertyNames;

public final class AgendaBoServiceImpl implements AgendaBoService {

    // TODO: deal with active flag

    private BusinessObjectService businessObjectService;
    private KrmsAttributeDefinitionService attributeDefinitionService;
    private SequenceAccessorService sequenceAccessorService;

    /**
     * This overridden method creates a KRMS Agenda in the repository
     */
    @Override
    public AgendaDefinition createAgenda(AgendaDefinition agenda) {
        if (agenda == null){
            throw new IllegalArgumentException("agenda is null");
        }
        final String nameKey = agenda.getName();
        final String contextId = agenda.getContextId();
        final AgendaDefinition existing = getAgendaByNameAndContextId(nameKey, contextId);
        if (existing != null){
            throw new IllegalStateException("the agenda to create already exists: " + agenda);
        }

        AgendaBo agendaBo = from(agenda);
        businessObjectService.save(agendaBo);
        return to(agendaBo);
    }

    /**
     * This overridden method updates an existing Agenda in the repository
     */
    @Override
    public void updateAgenda(AgendaDefinition agenda) {
        if (agenda == null){
            throw new IllegalArgumentException("agenda is null");
        }

        // must already exist to be able to update
        final String agendaIdKey = agenda.getId();
        final AgendaBo existing = businessObjectService.findBySinglePrimaryKey(AgendaBo.class, agendaIdKey);
        if (existing == null) {
            throw new IllegalStateException("the agenda does not exist: " + agenda);
        }
        final AgendaDefinition toUpdate;
        if (existing.getId().equals(agenda.getId())) {
            toUpdate = agenda;
        } else {
            // if passed in id does not match existing id, correct it
            final AgendaDefinition.Builder builder = AgendaDefinition.Builder.create(agenda);
            builder.setId(existing.getId());
            toUpdate = builder.build();
        }

        // copy all updateable fields to bo
        AgendaBo boToUpdate = from(toUpdate);

        // delete any old, existing attributes
        Map<String,String> fields = new HashMap<String,String>(1);
        fields.put(PropertyNames.Agenda.AGENDA_ID, toUpdate.getId());
        businessObjectService.deleteMatching(AgendaAttributeBo.class, fields);

        // update new agenda and create new attributes
        businessObjectService.save(boToUpdate);
    }

    /**
     * This overridden method retrieves an Agenda from the repository
     */
    @Override
    public AgendaDefinition getAgendaByAgendaId(String agendaId) {
        if (StringUtils.isBlank(agendaId)){
            throw new IllegalArgumentException("agenda id is null");
        }
        AgendaBo bo = businessObjectService.findBySinglePrimaryKey(AgendaBo.class, agendaId);
        return to(bo);
    }

    /**
     * This overridden method retrieves an agenda from the repository
     */
    @Override
    public AgendaDefinition getAgendaByNameAndContextId(String name, String contextId) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("name is blank");
        }
        if (StringUtils.isBlank(contextId)) {
            throw new IllegalArgumentException("contextId is blank");
        }

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", name);
        map.put("contextId", contextId);

        AgendaBo myAgenda = businessObjectService.findByPrimaryKey(AgendaBo.class, Collections.unmodifiableMap(map));
        return to(myAgenda);
    }

    /**
     * This overridden method retrieves a set of agendas from the repository
     */
    @Override
    public Set<AgendaDefinition> getAgendasByContextId(String contextId) {
        if (StringUtils.isBlank(contextId)){
            throw new IllegalArgumentException("context ID is null or blank");
        }
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("contextId", contextId);
        Set<AgendaBo> bos = (Set<AgendaBo>) businessObjectService.findMatching(AgendaBo.class, map);
        return convertListOfBosToImmutables(bos);
    }

    /**
     * This overridden method creates a new Agenda in the repository
     */
    @Override
    public AgendaItem createAgendaItem(AgendaItem agendaItem) {
        if (agendaItem == null){
            throw new IllegalArgumentException("agendaItem is null");
        }
        if (agendaItem.getId() != null){
            final AgendaDefinition existing = getAgendaByAgendaId(agendaItem.getId());
            if (existing != null){
                throw new IllegalStateException("the agendaItem to create already exists: " + agendaItem);
            }
        }

        AgendaItemBo bo = AgendaItemBo.from(agendaItem);
        businessObjectService.save(bo);
        return AgendaItemBo.to(bo);
    }

    /**
     * This overridden method updates an existing Agenda in the repository
     */
    @Override
    public void updateAgendaItem(AgendaItem agendaItem) {
        if (agendaItem == null){
            throw new IllegalArgumentException("agendaItem is null");
        }
        final String agendaItemIdKey = agendaItem.getId();
        final AgendaItem existing = getAgendaItemById(agendaItemIdKey);
        if (existing == null) {
            throw new IllegalStateException("the agenda item does not exist: " + agendaItem);
        }
        final AgendaItem toUpdate;
        if (existing.getId().equals(agendaItem.getId())) {
            toUpdate = agendaItem;
        } else {
            final AgendaItem.Builder builder = AgendaItem.Builder.create(agendaItem);
            builder.setId(existing.getId());
            toUpdate = builder.build();
        }

        businessObjectService.save(AgendaItemBo.from(toUpdate));
    }

    /**
     * This overridden method adds a new AgendaItem to the repository
     */
    @Override
    public void addAgendaItem(AgendaItem agendaItem, String parentId, Boolean position) {
        if (agendaItem == null){
            throw new IllegalArgumentException("agendaItem is null");
        }
        AgendaItem parent = null;
        if (parentId != null){
            parent = getAgendaItemById(parentId);
            if (parent == null){
                throw new IllegalStateException("parent agendaItem does not exist in repository. parentId = " + parentId);
            }
        }
        // create new AgendaItem
        final AgendaItem toCreate;
        if (agendaItem.getId() == null) {
            SequenceAccessorService sas = getSequenceAccessorService();
            final AgendaItem.Builder builder = AgendaItem.Builder.create(agendaItem);
            final String newId =sas.getNextAvailableSequenceNumber(
                    "KRMS_AGENDA_ITM_S", AgendaItemBo.class).toString();
            builder.setId(newId);
            toCreate = builder.build();
        } else {
            toCreate = agendaItem;
        }
        createAgendaItem(toCreate);

        // link it to it's parent (for whenTrue/whenFalse, sibling for always
        if (parentId != null) {
            final AgendaItem.Builder builder = AgendaItem.Builder.create(parent);
            if (position == null){
                builder.setAlwaysId( toCreate.getId() );
            } else if (position.booleanValue()){
                builder.setWhenTrueId( toCreate.getId() );
            } else if (!position.booleanValue()){
                builder.setWhenFalseId( toCreate.getId() );
            }
            final AgendaItem parentToUpdate = builder.build();
            updateAgendaItem( parentToUpdate );
        }
    }

    /**
     * This overridden method retrieves an AgendaItem from the repository
     */
    @Override
    public AgendaItem getAgendaItemById(String id) {
        if (StringUtils.isBlank(id)){
            throw new IllegalArgumentException("agenda item id is null");
        }
        AgendaItemBo bo = businessObjectService.findBySinglePrimaryKey(AgendaItemBo.class, id);
        return AgendaItemBo.to(bo);
    }

    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(final BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    protected BusinessObjectService getBusinessObjectService() {
        if ( businessObjectService == null ) {
            businessObjectService = KRADServiceLocator.getBusinessObjectService();
        }
        return businessObjectService;
    }

    /**
     * Sets the sequenceAccessorService attribute value.
     *
     * @param sequenceAccessorService The sequenceAccessorService to set.
     */
    public void setSequenceAccessorService(final SequenceAccessorService sequenceAccessorService) {
        this.sequenceAccessorService = sequenceAccessorService;
    }

    protected SequenceAccessorService getSequenceAccessorService() {
        if ( sequenceAccessorService == null ) {
            sequenceAccessorService = KRADServiceLocator.getSequenceAccessorService();
        }
        return sequenceAccessorService;
    }

    protected KrmsAttributeDefinitionService getAttributeDefinitionService() {
        if (attributeDefinitionService == null) {
            attributeDefinitionService = KrmsRepositoryServiceLocator.getKrmsAttributeDefinitionService();
        }
        return attributeDefinitionService;
    }

    public void setAttributeDefinitionService(KrmsAttributeDefinitionService attributeDefinitionService) {
        this.attributeDefinitionService = attributeDefinitionService;
    }

    /**
     * Converts a Set<AgendaBo> to an Unmodifiable Set<Agenda>
     *
     * @param agendaBos a mutable Set<AgendaBo> to made completely immutable.
     * @return An unmodifiable Set<Agenda>
     */
    public Set<AgendaDefinition> convertListOfBosToImmutables(final Collection<AgendaBo> agendaBos) {
        Set<AgendaDefinition> agendas = new HashSet<AgendaDefinition>();
        if (agendaBos != null){
            for (AgendaBo bo : agendaBos) {
                AgendaDefinition agenda = to(bo);
                agendas.add(agenda);
            }
        }
        return Collections.unmodifiableSet(agendas);
    }

    /**
     * Converts a mutable bo to it's immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    @Override
    public AgendaDefinition to(AgendaBo bo) {
        if (bo == null) { return null; }
        return org.kuali.rice.krms.api.repository.agenda.AgendaDefinition.Builder.create(bo).build();
    }


    /**
     * Converts a immutable object to it's mutable bo counterpart
     * @param im immutable object
     * @return the mutable bo
     */
    @Override
    public AgendaBo from(AgendaDefinition im) {
        if (im == null) { return null; }

        AgendaBo bo = new AgendaBo();
        bo.setId(im.getId());
        bo.setName( im.getName() );
        bo.setTypeId( im.getTypeId() );
        bo.setContextId( im.getContextId() );
        bo.setFirstItemId( im.getFirstItemId() );
        bo.setVersionNumber( im.getVersionNumber() );
        Set<AgendaAttributeBo> attributes = buildAgendaAttributeBo(im);

        bo.setAttributeBos(attributes);

        return bo;
    }

    private Set<AgendaAttributeBo> buildAgendaAttributeBo(AgendaDefinition im) {
        Set<AgendaAttributeBo> attributes = new HashSet<AgendaAttributeBo>();

        ContextBo context = getBusinessObjectService().findBySinglePrimaryKey(ContextBo.class, im.getContextId());

        // build a map from attribute name to definition
        Map<String, KrmsAttributeDefinition> attributeDefinitionMap = new HashMap<String, KrmsAttributeDefinition>();

        List<KrmsAttributeDefinition> attributeDefinitions =
                attributeDefinitionService.findAttributeDefinitionsByType(im.getTypeId());

        for (KrmsAttributeDefinition attributeDefinition : attributeDefinitions) {
            attributeDefinitionMap.put(attributeDefinition.getName(), attributeDefinition);
        }

        // for each entry, build an AgendaAttributeBo and add it to the set
        for (Entry<String,String> entry  : im.getAttributes().entrySet()){
            KrmsAttributeDefinition attrDef = attributeDefinitionMap.get(entry.getKey());

            if (attrDef != null) {
                AgendaAttributeBo attributeBo = new AgendaAttributeBo();
                attributeBo.setAgendaId( im.getId() );
                attributeBo.setAttributeDefinitionId(attrDef.getId());
                attributeBo.setValue(entry.getValue());
                attributeBo.setAttributeDefinition(KrmsAttributeDefinitionBo.from(attrDef));
                attributes.add( attributeBo );
            } else {
                throw new RiceIllegalStateException("there is no attribute definition with the name '" +
                        entry.getKey() + "' that is valid for the agenda type with id = '" + im.getTypeId() +"'");
            }
        }
        return attributes;
    }

}
