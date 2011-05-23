/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krms.api.repository.context;

import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinitionContract;

import java.util.Set;

/**
 * An interface which defines the contract for context definition objects.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public interface ContextDefinitionContract extends Versioned, Identifiable {

	/**
	 * Returns the namespace of the context definition.  The combination of
	 * namespace and name represent a unique business key for the context
	 * definition.  The namespace should never be null or blank.
	 * 
	 * @return the namespace of the context definition, should never be null or blank
	 */
	String getNamespace();
	
	/**
	 * Returns the name of the context definition.  The combination of name and namespaceCode
	 * represent a unique business key for the context definition.  The name should never be
	 * null or blank.
	 * 
	 * @return the name of the context definition, should never be null or blank
	 */
	String getName();
	
	/**
	 * Returns the type id for the context definition.  If the type id is null, that means
	 * this context definition is of the default type.
	 * 
	 * @return the type id for the context definition, or null if this context definition is of the default type
	 */
	String getTypeId();
	
	
	// TODO: this java doc is confusing
	/**
	 * Returns the set of agendas contained within the context definition. 
	 * This method should never return null but can return null.
	 * 
	 * @return the set of agendas on the context definition
	 */
	Set<? extends AgendaDefinitionContract> getAgendas();
	
}
