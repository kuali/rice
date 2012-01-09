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
package org.kuali.rice.krms.api.repository.agenda;

import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.krms.api.repository.rule.RuleDefinitionContract;

public interface AgendaItemContract extends Identifiable, Versioned {

	/**
	 * This is the agenda ID for the Agenda associated with this Agenda Item
	 *
	 * <p>
	 * It is the agenda ID for the Agenda object associated with this Agenda Item.
	 * </p>
	 * @return ID for AgendaItem
	 */
	public String getAgendaId();

    /**
     * This is ID of the Rule associated with this AgendaItem.
     * <p>
     * Each AgendaItem has either a Rule or a SubAgenda associated with it, but not both.
     * <p>
     * @return ID of the Rule associated with the AgendaItem
     */
	public String getRuleId();

    /**
     * This is ID of the SubAgenda associated with this AgendaItem.
     * <p>
     * Each AgendaItem has either a Rule or a SubAgenda associated with it, but not both.
     * <p>
     * @return ID of the SubAgenda associated with the AgendaItem
     */
	public String getSubAgendaId();

    /**
     * This is ID of the next AgendaItem to be executed if the Rule associated
     * AgendaItem evaluates to true.
     * @return ID of the next AgendaItem
     */	
	public String getWhenTrueId();
	
    /**
     * This is ID of the next AgendaItem to be executed if the Rule associated 
     * AgendaItem evaluates to false.
     * @return ID of the next AgendaItem
     */	
	public String getWhenFalseId();
	
    /**
     * This is ID of the next AgendaItem to be executed after following any
     * defined true or false actions.
     * @return ID of the next AgendaItem
     */	
	public String getAlwaysId();

	/**
	 * 
	 * This method returns the Rule associated with this AgendaItem.
	 * 
	 * @return an immutable representation of the Rule
	 */
	public RuleDefinitionContract getRule();

	/**
	 * 
	 * This method returns the SubAgenda associated with this AgendaItem.
	 * 
	 * @return an immutable representation of the SubAgenda
	 */
	public AgendaDefinitionContract getSubAgenda();
	
    /**
     * This method returns the next AgendaItem to be executed if the 
     * Rule associated with this AgendaItem evaluates to true.
     * @return an immutable representation of the next AgendaItem
     */	
	public AgendaItemContract getWhenTrue();
	
    /**
     * This method returns the next AgendaItem to be executed if the 
     * Rule associated with this AgendaItem evaluates to false.
     * @return an immutable representation of the next AgendaItem
     */	
	public AgendaItemContract getWhenFalse();

	/**
     * This is ID of the next AgendaItem to be executed after following any
     * defined true or false actions.
     * @return an immutable representation of the next AgendaItem
     */	
	public AgendaItemContract getAlways();

}
