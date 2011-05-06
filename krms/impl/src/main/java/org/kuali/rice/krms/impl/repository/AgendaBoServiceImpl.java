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
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.SequenceAccessorService;
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaItem;

public final class AgendaBoServiceImpl implements AgendaBoService {
	public final static String WHEN_TRUE = "WHEN TRUE";
	public final static String WHEN_FALSE = "WHEN FALSE";
	public final static String ALWAYS = "ALWAYS";
	
	private BusinessObjectService businessObjectService;
	private SequenceAccessorService sequenceAccessorService;

	/**
	 * This overridden method creates a KRMS Agenda in the repository
	 * 
	 * @see org.kuali.rice.krms.impl.repository.AgendaBoService#createAgenda(org.kuali.rice.krms.api.repository.agenda.AgendaDefinition)
	 */
	@Override
	public void createAgenda(AgendaDefinition agenda) {
		if (agenda == null){
			throw new IllegalArgumentException("agenda is null");
		}
		final String nameKey = agenda.getName();
		final String namespaceKey = agenda.getNamespaceCode();
		final AgendaDefinition existing = getAgendaByNameAndNamespace(nameKey, namespaceKey);
		if (existing != null){
			throw new IllegalStateException("the agenda to create already exists: " + agenda);			
		}	
		businessObjectService.save(AgendaBo.from(agenda));		
	}

	/**
	 * This overridden method updates an existing Agenda in the repository
	 * 
	 * @see org.kuali.rice.krms.impl.repository.AgendaBoService#updateAgenda(org.kuali.rice.krms.api.repository.agenda.AgendaDefinition)
	 */
	@Override
	public void updateAgenda(AgendaDefinition agenda) {
		if (agenda == null){
			throw new IllegalArgumentException("agenda is null");
		}
		final String agendaIdKey = agenda.getId();
		final AgendaDefinition existing = getAgendaByAgendaId(agendaIdKey);
		if (existing == null) {
			throw new IllegalStateException("the agenda does not exist: " + agenda);
		}
		final AgendaDefinition toUpdate;
		if (!existing.getId().equals(agenda.getId())){
			final AgendaDefinition.Builder builder = AgendaDefinition.Builder.create(agenda);
			builder.setId(existing.getId());
			toUpdate = builder.build();
		} else {
			toUpdate = agenda;
		}

		businessObjectService.save(AgendaBo.from(toUpdate));
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.impl.repository.AgendaBoService#getAgendaByAgendaId(java.lang.String)
	 */
	@Override
	public AgendaDefinition getAgendaByAgendaId(String agendaId) {
		if (StringUtils.isBlank(agendaId)){
			throw new IllegalArgumentException("agenda id is null");
		}
		AgendaBo bo = businessObjectService.findBySinglePrimaryKey(AgendaBo.class, agendaId);
		return AgendaBo.to(bo);
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.impl.repository.AgendaBoService#getAgendaByNameAndNamespace(java.lang.String, java.lang.String)
	 */
	@Override
	public AgendaDefinition getAgendaByNameAndNamespace(String name, String namespace) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("name is blank");
        }
        if (StringUtils.isBlank(namespace)) {
            throw new IllegalArgumentException("namespace is blank");
        }

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", name);
        map.put("namespace", namespace);

        AgendaBo myAgenda = businessObjectService.findByPrimaryKey(AgendaBo.class, Collections.unmodifiableMap(map));
		return AgendaBo.to(myAgenda);
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.impl.repository.AgendaBoService#getAgendasByContextId(java.lang.String)
	 */
	@Override
	public Set<AgendaDefinition> getAgendasByContextId(String contextId) {
		if (StringUtils.isBlank(contextId)){
            throw new IllegalArgumentException("context ID is null or blank");			
		}
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("contextId", contextId);
		Set<AgendaBo> bos = (Set<AgendaBo>) businessObjectService.findMatchingOrderBy(AgendaBo.class, map, "sequenceNumber", true);
		return convertListOfBosToImmutables(bos);
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.impl.repository.AgendaBoService#createAgendaItem(org.kuali.rice.krms.api.repository.agenda.AgendaItem)
	 */
	@Override
	public void createAgendaItem(AgendaItem agendaItem) {
		if (agendaItem == null){
			throw new IllegalArgumentException("agendaItem is null");
		}
		if (agendaItem.getId() != null){
			final AgendaDefinition existing = getAgendaByAgendaId(agendaItem.getId());
			if (existing != null){
				throw new IllegalStateException("the agendaItem to create already exists: " + agendaItem);			
			}
		}
		businessObjectService.save(AgendaItemBo.from(agendaItem));		
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.impl.repository.AgendaBoService#updateAgendaItem(org.kuali.rice.krms.api.repository.agenda.AgendaItem)
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
		if (!existing.getId().equals(agendaItem.getId())){
			final AgendaItem.Builder builder = AgendaItem.Builder.create(agendaItem);
			builder.setId(existing.getId());
			toUpdate = builder.build();
		} else {
			toUpdate = agendaItem;
		}

		businessObjectService.save(AgendaItemBo.from(toUpdate));
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.impl.repository.AgendaBoService#addAgendaItem(org.kuali.rice.krms.api.repository.agenda.AgendaItem, java.lang.String, java.lang.String)
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
		if (parentId != null){			
			final AgendaItem.Builder builder = AgendaItem.Builder.create(parent);
			if (position == null){
				builder.setAlwaysId( toCreate.getId() );
			} else if (position.booleanValue() == true){
				builder.setWhenTrueId( toCreate.getId() );
			} else if (position.booleanValue() == false){
				builder.setWhenFalseId( toCreate.getId() );
			}
			final AgendaItem parentToUpdate = builder.build();
			updateAgendaItem( parentToUpdate );
		}
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.impl.repository.AgendaBoService#addAgendaItem(org.kuali.rice.krms.api.repository.agenda.AgendaItem, java.lang.String, java.lang.String)
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

	protected SequenceAccessorService getSequenceAccessorService() {
		if ( sequenceAccessorService == null ) {
			sequenceAccessorService = KNSServiceLocator.getSequenceAccessorService();
		}
		return sequenceAccessorService;
	}

	/**
	 * Converts a List<AgendaBo> to an Unmodifiable List<Agenda>
	 *
	 * @param AgendaBos a mutable List<AgendaBo> to made completely immutable.
	 * @return An unmodifiable List<Agenda>
	 */
	public Set<AgendaDefinition> convertListOfBosToImmutables(final Collection<AgendaBo> agendaBos) {
		Set<AgendaDefinition> agendas = new HashSet<AgendaDefinition>();
		for (AgendaBo bo : agendaBos) {
			AgendaDefinition agenda = AgendaBo.to(bo);
			agendas.add(agenda);
		}
		return Collections.unmodifiableSet(agendas);
	}


}
