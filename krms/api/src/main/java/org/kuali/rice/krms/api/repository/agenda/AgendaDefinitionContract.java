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
package org.kuali.rice.krms.api.repository.agenda;

import java.util.Map;

import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;

public interface AgendaDefinitionContract extends Identifiable, Inactivatable, Versioned {
	/**
	 * This is the name of the Agenda 
	 *
	 * <p>
	 * name - the name of the Agenda
	 * </p>
	 * @return the name of the Agenda
	 */
	public String getName();

	/**
	 * This is the KrmsType of the Agenda
	 *
	 * @return id for KRMS type related of the Agenda
	 */
	public String getTypeId();
	
	/**
	 * This is the ID of the Context relative to the Agenda. 
	 *
	 * @return id for Context relative to the Agenda
	 */	
	public String getContextId();
	
	/**
	 * This is the ID of the first AgendaItem to be executed in the Agenda. 
	 *
	 * @return id of the first AgendaItem.
	 */	
	public String getFirstItemId();
	
	/**
	 * This method returns a list of attributes associated with the 
	 * Agenda
	 * 
	 * @return a list of AgendaAttribute objects.
	 */
	public Map<String, String> getAttributes();
	
}
