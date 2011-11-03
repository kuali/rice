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
package org.kuali.rice.krms.api.repository.action;

import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;

import java.util.Map;

public interface ActionDefinitionContract extends Identifiable, Versioned {

	/**
	 * This is the name of the Action 
	 *
	 * <p>
	 * name - the name of the Action
	 * </p>
	 * @return the name of the Action
	 */
	public String getName();

	/**
	 * This is the namespace of the Action 
	 *
	 * <p>
	 * The namespace of the Action
	 * </p>
	 * @return the namespace of the Action
	 */
	public String getNamespace();

    /**
     * This is the description for what the parameter is used for.  This can be null or a blank string.
     * @return description
     */
	public String getDescription();

	/**
	 * This is the KrmsType of the Action
	 *
	 * @return id for KRMS type related of the Action
	 */
	public String getTypeId();
	
	/**
	 * This method returns the id of the rule associated with the action
	 * 
	 * @return id for the Rule associated with the action.
	 */
	public String getRuleId();
	
	/**
	 * This method returns the id of the rule associated with the action
	 * 
	 * @return id for the Rule associated with the action.
	 */
	public Integer getSequenceNumber();
	
	/**
	 * This method returns a set of attributes associated with the 
	 * Action.  The attributes are represented as name/value pairs.
	 * 
	 * @return a set of ActionAttribute objects.
	 */
	public Map<String, String> getAttributes();
	

}
