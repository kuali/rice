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
package org.kuali.rice.krms.api.repository;

import org.kuali.rice.core.api.mo.Versioned;

/**
 * An interface which defines the contract for context definition objects.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public interface ContextDefinitionContract extends Versioned {

	/**
	 * Returns the id of the context definition.  This id should be unique amongst
	 * the set of all context definitions.
	 * 
	 * @return the id of the context definition
	 */
	String getContextDefinitionId();
	
	/**
	 * Returns the namespace code of the context definition.  The combination of
	 * namespaceCode and name represent a unique business key for the context
	 * definition.
	 * 
	 * @return the namespace code of the context definition
	 */
	String getNamespaceCode();
	
	/**
	 * Returns the name of the context definition.  The combination of name and namespaceCode
	 * represent a unique business key for the context definition.
	 * 
	 * @return the name of the context definition
	 */
	String getName();
	
	/**
	 * Returns the type id for the context definition.
	 * 
	 * @return the type id for the context definition
	 */
	String getTypeId();
	
}
