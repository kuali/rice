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
package org.kuali.rice.krms.api.repository;

import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinitionContract;

/**
 * Base interface intended for extension by other AttributeContract interfaces 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface BaseAttributeContract extends Identifiable {

	/**
	 * This is the id of the definition of the attribute. 
	 *
	 * <p>
	 * It identifies the attribute definition
	 * </p>
	 * @return the attribute definition id.
	 */
	public String getAttributeDefinitionId();

	/**
	 * This is the value of the attribute
	 * 
	 * @return the value of the attribute
	 */
	public String getValue();

	/**
	 * This is the definition of the attribute
	 */
	public KrmsAttributeDefinitionContract getAttributeDefinition();

}
