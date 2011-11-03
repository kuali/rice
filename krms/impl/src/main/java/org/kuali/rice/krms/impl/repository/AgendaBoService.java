/**
 * Copyright 2005-2011 The Kuali Foundation
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

import java.util.Set;

import org.kuali.rice.krms.api.repository.agenda.AgendaDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaItem;

/**
 * This is the interface for accessing KRMS repository Agenda related
 * business objects. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface AgendaBoService {

    /**
     * This will create a {@link AgendaDefinition} exactly like the parameter passed in.
     *
     * @param agenda  The Agenda to create
     * @throws IllegalArgumentException if the Agenda is null
     * @throws IllegalStateException if the Agenda already exists in the system
     */
	public AgendaDefinition createAgenda(AgendaDefinition agenda);
	
    /**
     * This will update an existing {@link AgendaDefinition}.
     *
     * @param agenda  The Agenda to update
     * @throws IllegalArgumentException if the Agenda is null
     * @throws IllegalStateException if the Agenda does not exists in the system
     */	
	public void updateAgenda(AgendaDefinition agenda);
	
    /**
     * Retrieves an Agenda from the repository based on the given agenda id.
     *
     * @param agendaId the id of the Agenda to retrieve
     * @return an {@link AgendaDefinition} identified by the given agendaId.  
     * A null reference is returned if an invalid or non-existent id is supplied.
     */
	public AgendaDefinition getAgendaByAgendaId(String agendaId);
	
    /**
     * Retrieves an Agenda from the repository based on the provided agenda name
     * and context id.
     *
     * @param name the name of the Agenda to retrieve.
     * @param contextId the id of the context that the agenda belongs to.
     * @return an {@link AgendaDefinition} identified by the given name and namespace.  
     * A null reference is returned if an invalid or non-existent name and
     * namespace combination is supplied.
     */
	public AgendaDefinition getAgendaByNameAndContextId(String name, String contextId);
	
    /**
     * Retrieves a set of Agendas associated with a context.
     *
     * @param contextId the id of the context
     * @return a set of {@link AgendaDefinition} associated with the given context.  
     * A null reference is returned if an invalid or contextId is supplied.
     */
	public Set<AgendaDefinition> getAgendasByContextId(String contextId);
	
    /**
     * This will create an {@link AgendaItem} in the repository exactly like
     * the parameter passed in.
     *
     * @param agendaItem  The AgendaItem to create
     * @throws IllegalArgumentException if the AgendaItem is null
     * @throws IllegalStateException if the AgendaItem already exists in the system
     */
	public AgendaItem createAgendaItem(AgendaItem agendaItem);
	
    /**
     * This will update an existing {@link AgendaItem}.
     *
     * @param agendaItem  The AgendaItem to update
     * @throws IllegalArgumentException if the AgendaItem is null
     * @throws IllegalStateException if the AgendaItem does not exists in the system
     */	
	public void updateAgendaItem(AgendaItem agendaItem);
	
    /**
     * This will create an {@link AgendaItem} in the repository exactly like
     * the parameter passed in.  The AgendaItem will be linked to an existing
     * AgendaItem in the relationship provided. Linking the AgendaItems effectively
     * builds a tree of AgendaItems that may be traversed by the engine.
     *
     * @param agendaItem  The AgendaItem to create
     * @param parentId  The id of the existing AgendaItem to be linked with the
     *  newly created AgendaItem 
     * @param position. A boolean used to specify the relationship between the
     *  linked AgendaItems.
     *  <p> If the position parameter is true, the new AgendaItem is linked as the next 
     *  AgendaItem to be evaluated if the parent AgendaItem evaluates to TRUE.
     *  <p> If the position parameter is false, the new AgendaItem is linked as the next 
     *  AgendaItem to be evaluated if the parent AgendaItem evaluates to FALSE.
     *  <p> If the position parameter is null,  the new AgendaItem is linked as the next 
     *  AgendaItem to be evaluated after any true or false branches of the tree have
     *  been traversed.
     * @throws IllegalArgumentException if the AgendaItem is null
     * @throws IllegalStateException if the parent AgendaItem does not already exists in the system
     */
	public void addAgendaItem(AgendaItem agendaItem, String parentId, Boolean position);
	
    /**
     * Retrieves an AgendaItem from the repository based on the given agenda id.
     *
     * @param id the id of the AgendaItem to retrieve
     * @return an {@link AgendaItem} identified by the given id.  
     * A null reference is returned if an invalid or non-existent id is supplied.
     */
	public AgendaItem getAgendaItemById(String id);

	/**
	* Converts a mutable bo to it's immutable counterpart
	* @param bo the mutable business object
	* @return the immutable object
	*/
	public AgendaDefinition to(AgendaBo bo);

   /**
	* Converts a immutable object to it's mutable bo counterpart
	* @param im immutable object
	* @return the mutable bo
	*/
	public AgendaBo from(AgendaDefinition im);
}
