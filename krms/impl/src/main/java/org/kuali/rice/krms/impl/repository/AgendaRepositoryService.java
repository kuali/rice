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

import org.kuali.rice.krms.api.repository.AgendaDefinition;


public interface AgendaRepositoryService {
	/**
	* Converts a mutable bo to it's immutable counterpart
	* @param bo the mutable business object
	* @return the immutable object
	*/
	public AgendaDefinition to(AgendaDefinitionBo bo);

   /**
	* Converts a immutable object to it's mutable bo counterpart
	* TODO: move to() and from() to impl service
	* @param im immutable object
	* @return the mutable bo
	*/
 	public AgendaDefinitionBo from(AgendaDefinition im);
}
